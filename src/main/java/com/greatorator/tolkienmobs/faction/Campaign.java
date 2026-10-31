package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.network.manager.CampaignSyncManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
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
 * The Rider is the one page that is not a front's. He belongs to nobody and everybody fears him, so all
 * three countries ask for him together, and only once all three lines are settled: one step for the
 * campaign as a whole, kept in a fourth slot beside the three fronts. That is also what gives the thing
 * an ending - a campaign whose every page belongs to a front has no way to be over.
 * <p>
 * The stage is synced to its owner because the mark over the herald's head is drawn on the client, and
 * that mark has to stop when nothing is left to ask.
 */
public final class Campaign extends SavedData {
    private static final String SAVE_DATA_NAME = "campaign";
    /**
     * What each page of a front asks for, in order: soldiers of one rival army, then soldiers of the
     * other. The list is the line - a stage past its end is a front with nothing left to hand over - so
     * writing one more page here is all the campaign needs to go on, and a front has as many pages as
     * this list is long.
     */
    private static final List<Page> PAGE_LINE = List.of(
            new Page(Quest.Kind.FACTION, 6, 60),
            new Page(Quest.Kind.FACTION, 12, 120));

    /** One page of a front's line: what it asks of the player, and what the country pays for it. */
    private record Page(Quest.Kind kind, int goal, int standing) {
    }

    /** How many pages a front has. */
    public static final int PAGES = PAGE_LINE.size();

    /**
     * The slot the campaign's own page lives in, one past the three fronts. It is a stage like any
     * other - 0 until the Rider is paid off - so it needs no machinery of its own.
     */
    private static final int RIDER_SLOT = Alliance.values().length;
    /**
     * What the Rider is worth. All three countries pay it, each in full: they were all afraid of him
     * and all wanted him gone, and none of them sent the player alone.
     */
    private static final int RIDER_STANDING = 200;

    private static final Map<UUID, Front[]> PROGRESS = new HashMap<>();
    private static final int[] CLIENT = new int[Alliance.values().length];
    private static int clientRider;

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

    /** The local player's stage on the campaign's own page: 0 while the Rider is still out there. */
    public static int clientRider() {
        return clientRider;
    }

    /** Whether all three fronts are settled on the client's last-synced copy. */
    public static boolean clientSealed() {
        for (Alliance alliance : Alliance.values()) {
            if (CLIENT[alliance.ordinal()] < PAGES) return false;
        }
        return true;
    }

    /**
     * Whether this herald still has something to say to the player, which is what his mark means: a front
     * with pages left, or the campaign's own page once every front is settled and the Rider is not yet
     * behind the player.
     */
    public static boolean clientHasWork(Alliance alliance) {
        if (CLIENT[alliance.ordinal()] < PAGES) return true;
        return clientSealed() && clientRider == 0;
    }

    public static void acceptClient(int[] stages) {
        for (Alliance alliance : Alliance.values()) {
            CLIENT[alliance.ordinal()] = alliance.ordinal() < stages.length ? stages[alliance.ordinal()] : 0;
        }
        clientRider = stages.length > RIDER_SLOT ? stages[RIDER_SLOT] : 0;
    }

    public static int stage(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        return row(player)[alliance.ordinal()].stage();
    }

    /** Whether every front has been carried to the end of its line. */
    public static boolean sealed(ServerPlayer player) {
        ensureLoaded(player.level());
        return sealed(row(player));
    }

    /** Whether the campaign's own page is behind the player, which is the campaign finished. */
    public static boolean done(ServerPlayer player) {
        ensureLoaded(player.level());
        return row(player)[RIDER_SLOT].stage() > 0;
    }

    /**
     * The page this alliance's herald is holding for this player, or null once there is nothing left to
     * ask. A herald whose own front is settled still has something to give while the other two fight, and
     * once all three are settled any of them hands over the campaign's last page - whoever the player
     * reaches first, since it is the same quarry.
     * <p>
     * The giver is left blank here and filled in where it is handed over, so the page itself is the same
     * for every player.
     */
    @Nullable
    public static Quest page(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        Front[] row = row(player);
        int stage = row[alliance.ordinal()].stage();
        if (stage < PAGES) {
            Page page = PAGE_LINE.get(stage);
            // The soldier pages name a different rival each, so the line alternates whose soldiers it
            // wants rather than asking for the same ones twice running.
            List<Alliance> rivals = alliance.rivals();
            String target = rivals.get(stage % rivals.size()).id();
            return new Quest(page.kind(), target, page.goal(), 0, alliance.id(), page.standing(), List.of(),
                    "", BlockPos.ZERO, Quest.NOBODY, true);
        }
        return sealed(row) && row[RIDER_SLOT].stage() == 0 ? riderPage() : null;
    }

    /**
     * The Rider, as a page: a hunt of one, priced by all three countries together. Asked of the registry
     * rather than written out, so a page cannot name a mob the world does not have - a hunt whose target
     * is in no registry can never be finished, and the campaign would sit on it for good.
     */
    private static Quest riderPage() {
        return new Quest(Quest.Kind.HUNT, riderId(), 1, 0, "", RIDER_STANDING, List.of(),
                "", BlockPos.ZERO, Quest.NOBODY, true);
    }

    private static String riderId() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(TolkienEntities.ENTITY_TTM_NAZGUL.get()).toString();
    }

    // ---- handing it over and settling it ----

    /** Notes the page that was really handed over, so settling it can be told apart from any other. */
    public static void noteHanded(ServerPlayer player, Alliance alliance, Quest page) {
        Front[] row = row(player);
        int slot = slotOf(page, alliance);
        row[slot] = new Front(row[slot].stage(), page);
        store(player).setDirty();
    }

    /**
     * Whether a page is the campaign's own rather than one of a front's. Read off its kind and target
     * rather than by equality, because the copy the player carries has advanced past the copy noted here
     * as soon as a blow lands.
     */
    public static boolean isCampaignPage(ServerPlayer player, Quest quest) {
        ensureLoaded(player.level());
        Front rider = row(player)[RIDER_SLOT].handed() == null ? null : row(player)[RIDER_SLOT];
        return rider != null && quest.kind() == rider.handed().kind()
                && quest.target().equals(rider.handed().target());
    }

    /**
     * Moves a front on when the page it handed out is paid, and answers whether the page settled was the
     * campaign's own rather than a front's - the ledger pays that one differently, because it has no
     * single employer. The ledger calls this from the one place a quest is settled, which is what keeps a
     * page dropped, abandoned or cleared from counting as done.
     */
    public static boolean settle(ServerPlayer player, Quest quest) {
        if (!(player.level() instanceof ServerLevel)) return false;
        ensureLoaded(player.level());
        Front[] row = PROGRESS.get(player.getUUID());
        if (row == null) return false;
        for (Alliance alliance : Alliance.values()) {
            Front front = row[alliance.ordinal()];
            if (front.handed() == null || !front.handed().equals(quest)) continue;
            row[alliance.ordinal()] = new Front(front.stage() + 1, null);
            store(player).setDirty();
            sync(player);
            return false;
        }
        Front rider = row[RIDER_SLOT];
        if (rider.handed() == null || !rider.handed().equals(quest)) return false;
        row[RIDER_SLOT] = new Front(1, null);
        store(player).setDirty();
        sync(player);
        return true;
    }

    /** Which slot of a player's row a page belongs to: the campaign's own, or the herald's front. */
    private static int slotOf(Quest page, Alliance alliance) {
        return page.kind() == Quest.Kind.HUNT ? RIDER_SLOT : alliance.ordinal();
    }

    // ---- plumbing ----

    public static void sync(ServerPlayer player) {
        ensureLoaded(player.level());
        Front[] row = row(player);
        PacketDistributor.sendToPlayer(player,
                new CampaignSyncManager(row[0].stage(), row[1].stage(), row[2].stage(),
                        row[RIDER_SLOT].stage()));
    }

    private static boolean sealed(Front[] row) {
        for (Alliance alliance : Alliance.values()) {
            if (row[alliance.ordinal()].stage() < PAGES) return false;
        }
        return true;
    }

    private static Front[] row(Player player) {
        return PROGRESS.computeIfAbsent(player.getUUID(), ignored -> blank());
    }

    private static Front[] blank() {
        return new Front[]{new Front(0, null), new Front(0, null), new Front(0, null),
                new Front(0, null)};
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
            PROGRESS.put(entry.player(), new Front[]{entry.west(), entry.east(), entry.harad(),
                    entry.rider()});
        }
        return new Campaign();
    }

    private static List<Entry> toEntries(Campaign campaign) {
        List<Entry> entries = new ArrayList<>(PROGRESS.size());
        for (Map.Entry<UUID, Front[]> row : PROGRESS.entrySet()) {
            Front[] fronts = row.getValue();
            entries.add(new Entry(row.getKey(), fronts[0], fronts[1], fronts[2], fronts[RIDER_SLOT]));
        }
        return entries;
    }

    public record Entry(UUID player, Front west, Front east, Front harad, Front rider) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
                Front.CODEC.optionalFieldOf("west", new Front(0, null)).forGetter(Entry::west),
                Front.CODEC.optionalFieldOf("east", new Front(0, null)).forGetter(Entry::east),
                Front.CODEC.optionalFieldOf("harad", new Front(0, null)).forGetter(Entry::harad),
                Front.CODEC.optionalFieldOf("rider", new Front(0, null)).forGetter(Entry::rider)
        ).apply(instance, Entry::new));
    }
}
