package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.network.manager.FactionStandingsSyncManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Every player's standing with each of the three alliances, kept in the overworld's saved data so a
 * world carries its politics with it.
 * <p>
 * The server owns the numbers and every change is sent to the player it happened to, which is why the
 * ledger screen needs no packet of its own: it draws what the last sync left on the client. The client
 * copy is the local player's three totals and nothing else - a client has no business knowing anyone
 * else's standing.
 */
public final class FactionStandings extends SavedData {
    private static final String SAVE_DATA_NAME = "faction_standings";
    private static final Map<UUID, int[]> STANDINGS = new HashMap<>();
    private static final int[] CLIENT = new int[Alliance.values().length];
    /** A standing is a score, not a currency, so it is only bounded to keep it out of integer trouble. */
    private static final int CEILING = 100_000;

    public static final Codec<FactionStandings> CODEC = Entry.CODEC.listOf()
            .xmap(FactionStandings::fromEntries, FactionStandings::toEntries);

    public static final SavedDataType<FactionStandings> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), FactionStandings::new, CODEC);

    private FactionStandings() {
        super();
    }

    // ---- reading ----

    /** The local player's standing with one alliance, as last synced from the server. */
    public static int clientStanding(Alliance alliance) {
        return CLIENT[alliance.ordinal()];
    }

    public static void acceptClient(int[] totals) {
        for (Alliance alliance : Alliance.values()) {
            CLIENT[alliance.ordinal()] = alliance.ordinal() < totals.length ? totals[alliance.ordinal()] : 0;
        }
    }

    public static int standing(Player player, Alliance alliance) {
        if (player.level().isClientSide()) return clientStanding(alliance);
        return row(player)[alliance.ordinal()];
    }

    public static FactionRank rank(Player player, Alliance alliance) {
        return FactionRank.of(standing(player, alliance));
    }

    public static boolean isHostile(Player player, Alliance alliance) {
        return standing(player, alliance) < 0;
    }

    // ---- changing ----

    /** A standing is held between the floor a country turns on you at and the ceiling. */
    public static int clamp(int value) {
        return Math.max(FactionRank.FLOOR, Math.min(value, CEILING));
    }

    /**
     * Puts a standing exactly where an operator asks for it, which is what the set command is for: a
     * world being built or a scene being staged needs a rank now, not after an afternoon of tokens.
     * The total is held to the same bounds every other change is, and sent to the player it happened to.
     */
    public static void set(ServerPlayer player, Alliance alliance, int value) {
        int[] row = row(player);
        int after = clamp(value);
        if (row[alliance.ordinal()] == after) return;
        row[alliance.ordinal()] = after;
        instance(player.level()).setDirty();
        sync(player);
    }

    /**
     * Moves one alliance's standing and tells the player.
     * <p>
     * A loss is felt in full. It used to be held at the bottom of the rank the player already held, to
     * keep a kill from undoing a rank - but the rank is read off the total, so at exactly a threshold that
     * rule froze the number: six brigands killed cost the West nothing at all, and the only way to fall out
     * of a country's favour was to hand its rivals tokens. Losing standing now simply works.
     */
    public static void adjust(ServerPlayer player, Alliance alliance, int delta) {
        if (delta == 0) return;
        int[] row = row(player);
        int before = row[alliance.ordinal()];
        int after = clamp(before + delta);
        if (after == before) return;
        row[alliance.ordinal()] = after;
        instance(player.level()).setDirty();
        sync(player);
    }

    /**
     * Hands every one of an alliance's tokens in the player's pack to that alliance's hall. The
     * alliance gains a point a token's worth and each rival loses half that again, so swearing to one
     * side is felt by the other two.
     *
     * @return how many tokens were taken, or 0 when the pack held none
     */
    public static int handIn(ServerPlayer player, Alliance alliance) {
        int taken = takeTokens(player, alliance);
        if (taken == 0) return 0;
        adjust(player, alliance, taken * FactionRank.TOKEN_VALUE);
        for (Alliance rival : alliance.rivals()) {
            adjust(player, rival, -taken * FactionRank.KILL_VALUE);
        }
        return taken;
    }

    /** Removes every token of one alliance from the pack and answers with how many there were. */
    private static int takeTokens(ServerPlayer player, Alliance alliance) {
        int taken = 0;
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (!stack.is(alliance.token())) continue;
            taken += stack.getCount();
            inventory.setItem(slot, net.minecraft.world.item.ItemStack.EMPTY);
        }
        return taken;
    }

    // ---- pluming ----

    public static void sync(ServerPlayer player) {
        int[] row = row(player);
        PacketDistributor.sendToPlayer(player,
                new FactionStandingsSyncManager(row[0], row[1], row[2]));
    }

    private static int[] row(Player player) {
        return STANDINGS.computeIfAbsent(player.getUUID(), ignored -> new int[Alliance.values().length]);
    }

    private static FactionStandings instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return new FactionStandings();
    }

    // ---- saving ----

    private static FactionStandings fromEntries(List<Entry> entries) {
        STANDINGS.clear();
        for (Entry entry : entries) {
            STANDINGS.put(entry.player(), new int[]{entry.west(), entry.east(), entry.harad()});
        }
        return new FactionStandings();
    }

    private static List<Entry> toEntries(FactionStandings standings) {
        List<Entry> entries = new ArrayList<>(STANDINGS.size());
        for (Map.Entry<UUID, int[]> row : STANDINGS.entrySet()) {
            int[] totals = row.getValue();
            entries.add(new Entry(row.getKey(), totals[0], totals[1], totals[2]));
        }
        return entries;
    }

    public record Entry(UUID player, int west, int east, int harad) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
                Codec.INT.optionalFieldOf("west", 0).forGetter(Entry::west),
                Codec.INT.optionalFieldOf("east", 0).forGetter(Entry::east),
                Codec.INT.optionalFieldOf("harad", 0).forGetter(Entry::harad)
        ).apply(instance, Entry::new));
    }

    @Nullable
    public static FactionStandings peek(Level level) {
        return level instanceof ServerLevel ? instance(level) : null;
    }
}
