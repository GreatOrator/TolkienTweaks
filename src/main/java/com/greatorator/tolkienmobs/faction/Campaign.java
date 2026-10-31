package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.network.manager.CampaignSyncManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
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
import java.util.Optional;
import java.util.UUID;

/**
 * How far a player has got on each of the three fronts, which is the one thing the mod was missing to
 * make its quests a campaign rather than a handful of good errands.
 * <p>
 * A front is a numbered line of pages. The page for a stage is not random - it is written out below,
 * because the line's business is the campaign's and not a farmer's fence - and the herald standing in
 * that alliance's hall is the only body who hands one over. The stage moves on only when the ledger
 * actually pays the page off: {@link #settle} is handed the quest the ledger just settled and compares
 * it against the page this front handed out, so a page dropped from the book, or a command clearing it,
 * leaves the front exactly where it was.
 * <p>
 * The stage is synced to its owner because the mark over the herald's head is drawn on the client, and
 * that mark has to stop when a front has no more pages to give.
 */
public final class Campaign extends SavedData {
    private static final String SAVE_DATA_NAME = "campaign";
    /** How many pages a front has. Page 0 is the first the herald holds; the rest are written one at a time. */
    public static final int PAGES = 1;
    /** The opening page: soldiers of one rival army, enough to be noticed and not yet a war. */
    private static final int OPENING_GOAL = 6;
    private static final int OPENING_STANDING = 60;

    private static final Map<UUID, Front[]> PROGRESS = new HashMap<>();
    private static final int[] CLIENT = new int[Alliance.values().length];

    /** One player's place in one front: how far it has got, and the page it is holding out. */
    public record Front(int stage, @Nullable Quest handed) {
        static final Codec<Front> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("stage", 0).forGetter(Front::stage),
                Quest.CODEC.optionalFieldOf("handed").xmap(o -> o.orElse(null), Optional::ofNullable)
                        .forGetter(Front::handed)
        ).apply(instance, Front::new));
    }

    public static final Codec<Campaign> CODEC = Entry.CODEC.listOf()
            .xmap(Campaign::fromEntries, Campaign::toEntries);

    public static final SavedDataType<Campaign> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Campaign::new, CODEC);

    private Campaign() {
        super();
    }

    // ---- reading ----

    /** The stage the local player has reached on one front, as last synced from the server. */
    public static int clientStage(Alliance alliance) {
        return CLIENT[alliance.ordinal()];
    }

    public static void acceptClient(int[] stages) {
        for (Alliance alliance : Alliance.values()) {
            CLIENT[alliance.ordinal()] = alliance.ordinal() < stages.length ? stages[alliance.ordinal()] : 0;
        }
    }

    public static int stage(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        return row(player)[alliance.ordinal()].stage();
    }

    /**
     * The page the herald of an alliance is holding for this player, or null once the front is written
     * out. The giver is left blank here and filled in where it is handed over, so the page itself is the
     * same for every player.
     */
    @Nullable
    public static Quest page(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        int stage = row(player)[alliance.ordinal()].stage();
        if (stage >= PAGES) return null;
        List<Alliance> rivals = alliance.rivals();
        Alliance target = rivals.get(stage % rivals.size());
        return new Quest(Quest.Kind.FACTION, target.id(), OPENING_GOAL, 0, alliance.id(),
                OPENING_STANDING, List.of(), "", BlockPos.ZERO, Quest.NOBODY);
    }

    // ---- handing it over and settling it ----

    /** Notes the page that was really handed over, so settling it can be told apart from any other. */
    public static void noteHanded(ServerPlayer player, Alliance alliance, Quest page) {
        Front[] row = row(player);
        row[alliance.ordinal()] = new Front(row[alliance.ordinal()].stage(), page);
        store(player).setDirty();
    }

    /**
     * Moves a front on when the page it handed out is paid. The ledger calls this from the one place a
     * quest is settled, which is what keeps a page dropped, abandoned or cleared from counting as done.
     */
    public static void settle(ServerPlayer player, Quest quest) {
        if (!(player.level() instanceof ServerLevel)) return;
        ensureLoaded(player.level());
        Front[] row = PROGRESS.get(player.getUUID());
        if (row == null) return;
        for (Alliance alliance : Alliance.values()) {
            Front front = row[alliance.ordinal()];
            if (front.handed() == null || !front.handed().equals(quest)) continue;
            row[alliance.ordinal()] = new Front(front.stage() + 1, null);
            store(player).setDirty();
            sync(player);
            return;
        }
    }

    // ---- plumbing ----

    public static void sync(ServerPlayer player) {
        ensureLoaded(player.level());
        Front[] row = row(player);
        PacketDistributor.sendToPlayer(player,
                new CampaignSyncManager(row[0].stage(), row[1].stage(), row[2].stage()));
    }

    private static Front[] row(Player player) {
        return PROGRESS.computeIfAbsent(player.getUUID(), ignored -> blank());
    }

    private static Front[] blank() {
        return new Front[]{new Front(0, null), new Front(0, null), new Front(0, null)};
    }

    private static Campaign store(ServerPlayer player) {
        return instance(player.level());
    }

    /** Reads the world's file into the map, once, before the map is read or written. */
    private static void ensureLoaded(Level level) {
        instance(level);
    }

    private static Campaign instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return new Campaign();
    }

    private static Campaign fromEntries(List<Entry> entries) {
        PROGRESS.clear();
        for (Entry entry : entries) {
            PROGRESS.put(entry.player(), new Front[]{entry.west(), entry.east(), entry.harad()});
        }
        return new Campaign();
    }

    private static List<Entry> toEntries(Campaign campaign) {
        List<Entry> entries = new ArrayList<>(PROGRESS.size());
        for (Map.Entry<UUID, Front[]> row : PROGRESS.entrySet()) {
            Front[] fronts = row.getValue();
            entries.add(new Entry(row.getKey(), fronts[0], fronts[1], fronts[2]));
        }
        return entries;
    }

    public record Entry(UUID player, Front west, Front east, Front harad) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
                Front.CODEC.optionalFieldOf("west", new Front(0, null)).forGetter(Entry::west),
                Front.CODEC.optionalFieldOf("east", new Front(0, null)).forGetter(Entry::east),
                Front.CODEC.optionalFieldOf("harad", new Front(0, null)).forGetter(Entry::harad)
        ).apply(instance, Entry::new));
    }
}
