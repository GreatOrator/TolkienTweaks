package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.network.manager.CourageSyncManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Every player's courage, kept in the overworld's saved data beside their standings and their book.
 * <p>
 * The server owns the number and every change is sent to the player it happened to, so the ledger and
 * the HUD both read the same synced cache and neither needs a packet of its own. The client copy is
 * the local player's total and nothing else.
 * <p>
 * Courage is earned in <em>deeds</em>, and a deed is always something the player did rather than
 * something that happened to them:
 * <ul>
 *     <li>work handed in - a quest seen through, however small;</li>
 *     <li>fear withstood - carrying terror or an aura without being broken by it, which counts once a
 *     minute at most so that the deed is the endurance and not the ticking;</li>
 *     <li>one of the Nine felled, and their master with them.</li>
 * </ul>
 * The last of those is why the ladder is worth climbing slowly: a player who walks into Mordor early
 * is not asked to have courage already, only to come back with some.
 */
public final class CourageLedger extends SavedData {
    private static final String SAVE_DATA_NAME = "courage_ledger";
    /** How long between two deeds for withstanding the same fear, in ticks. */
    private static final long FEAR_DEED_COOLDOWN = 1200L;

    private static final Map<UUID, Integer> COURAGE = new HashMap<>();
    private static final Map<UUID, Long> NEXT_FEAR_DEED = new HashMap<>();

    private static int client = 0;

    public static final Codec<CourageLedger> CODEC = Entry.CODEC.listOf()
            .xmap(CourageLedger::fromEntries, CourageLedger::toEntries);

    public static final SavedDataType<CourageLedger> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), CourageLedger::new, CODEC);

    private CourageLedger() {
        super();
    }

    // ---- what a deed is worth ----

    /** The deeds a player can be paid courage for. */
    public enum Deed {
        /** A quest handed in, whatever kind it was. */
        WORK_DONE(1),
        /** Terror or an aura carried while still standing. */
        FEAR_WITHSTOOD(1),
        /** One of the Nine put down. */
        NAZGUL_FELLED(5),
        /** The Witch-king himself. */
        WITCHKING_FELLED(20);

        private final int points;

        Deed(int points) {
            this.points = points;
        }

        public int points() {
            return points;
        }
    }

    // ---- reading ----

    /** The local player's courage, as last synced from the server. */
    public static int clientCourage() {
        return client;
    }

    public static void acceptClient(int deeds) {
        client = Math.max(0, deeds);
    }

    /** The local player's rung, for the ledger and the HUD alike. */
    public static Courage clientStage() {
        return Courage.of(client);
    }

    public static int courage(Player player) {
        if (player.level().isClientSide()) return clientCourage();
        ensureLoaded(player.level());
        return row(player);
    }

    /** A mob has no courage to speak of: only a player climbs this ladder. */
    public static int courage(LivingEntity entity) {
        return entity instanceof Player player ? courage(player) : 0;
    }

    public static Courage stage(Player player) {
        return Courage.of(courage(player));
    }

    // ---- earning ----

    public static void award(ServerPlayer player, Deed deed) {
        add(player, deed.points());
    }

    /**
     * Adds to a player's courage and tells them when it carries them up a rung. Crossing a rung is the
     * only moment courage is worth announcing; the deeds between are meant to be quiet, or the climb
     * would be a stream of congratulation.
     */
    public static void add(ServerPlayer player, int points) {
        if (points <= 0) return;
        CourageLedger store = instance(player.level());
        int before = row(player);
        int after = Math.min(Courage.CEILING, before + points);
        if (after == before) return;
        COURAGE.put(player.getUUID(), after);
        store.setDirty();
        Courage was = Courage.of(before);
        Courage now = Courage.of(after);
        if (now != was) announce(player, now);
        sync(player);
    }

    /**
     * A fear carried to the end, which is the one deed a player earns by standing still and taking it.
     * It counts at most once a minute, so a long terror is one deed rather than six.
     */
    public static void fearWithstood(ServerPlayer player) {
        long now = player.level().getGameTime();
        if (now < NEXT_FEAR_DEED.getOrDefault(player.getUUID(), 0L)) return;
        NEXT_FEAR_DEED.put(player.getUUID(), now + FEAR_DEED_COOLDOWN);
        award(player, Deed.FEAR_WITHSTOOD);
    }

    /** Puts a courage total exactly where an operator asks for it, for trying the rungs by hand. */
    public static void set(ServerPlayer player, int deeds) {
        CourageLedger store = instance(player.level());
        int before = row(player);
        int after = Math.max(0, Math.min(deeds, Courage.CEILING));
        COURAGE.put(player.getUUID(), after);
        if (after != before) store.setDirty();
        sync(player);
    }

    private static void announce(ServerPlayer player, Courage stage) {
        player.sendSystemMessage(Component.translatable("courage.tolkienmobs.risen", stage.displayName())
                .withStyle(ChatFormatting.GOLD));
        player.sendOverlayMessage(Component.translatable("courage.tolkienmobs.risen.overlay", stage.displayName())
                .withStyle(ChatFormatting.GOLD));
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS, 0.6F, 0.9F);
    }

    // ---- pluming ----

    public static void sync(ServerPlayer player) {
        // The file is read before the client is told anything: a player who has just logged in would
        // otherwise be sent the empty row that was built for them.
        ensureLoaded(player.level());
        PacketDistributor.sendToPlayer(player, new CourageSyncManager(row(player)));
    }

    private static int row(Player player) {
        return COURAGE.getOrDefault(player.getUUID(), 0);
    }

    private static void ensureLoaded(Level level) {
        instance(level);
    }

    private static CourageLedger instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return new CourageLedger();
    }

    // ---- saving ----

    private static CourageLedger fromEntries(List<Entry> entries) {
        COURAGE.clear();
        NEXT_FEAR_DEED.clear();
        for (Entry entry : entries) {
            COURAGE.put(entry.player(), entry.courage());
            NEXT_FEAR_DEED.put(entry.player(), entry.nextFearDeed());
        }
        return new CourageLedger();
    }

    private static List<Entry> toEntries(CourageLedger ledger) {
        List<Entry> entries = new ArrayList<>(COURAGE.size());
        for (Map.Entry<UUID, Integer> row : COURAGE.entrySet()) {
            entries.add(new Entry(row.getKey(), row.getValue(),
                    NEXT_FEAR_DEED.getOrDefault(row.getKey(), 0L)));
        }
        return entries;
    }

    public record Entry(UUID player, int courage, long nextFearDeed) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
                Codec.INT.optionalFieldOf("courage", 0).forGetter(Entry::courage),
                Codec.LONG.optionalFieldOf("next_fear_deed", 0L).forGetter(Entry::nextFearDeed)
        ).apply(instance, Entry::new));
    }
}
