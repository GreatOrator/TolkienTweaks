package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.network.manager.CampaignSyncManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
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
     * other, and then the front's own errand - a beast of its own country, or a scroll off the enemy's
     * road - which is the page that stops the three lines reading as one line repainted. The list is the
     * line - a stage past its end is a front with nothing left to hand over - so
     * writing one more page here is all the campaign needs to go on, and a front has as many pages as
     * this list is long.
     */
    private static final Map<Alliance, List<Page>> PAGE_LINES = Map.of(
            Alliance.WEST, List.of(
                    new Page(Quest.Kind.FACTION, "", 6, 60,
                            "campaign.tolkienmobs.page.muster", "campaign.tolkienmobs.page.muster.place"),
                    new Page(Quest.Kind.FACTION, "", 12, 120,
                            "campaign.tolkienmobs.page.southroad", "campaign.tolkienmobs.page.southroad.place"),
                    new Page(Quest.Kind.HUNT, "tolkienmobs:entityttmbarrow", 2, 150,
                            "campaign.tolkienmobs.page.keeping", "campaign.tolkienmobs.page.keeping.place")),
            Alliance.EAST, List.of(
                    new Page(Quest.Kind.FACTION, "", 6, 60,
                            "campaign.tolkienmobs.page.marches", "campaign.tolkienmobs.page.marches.place"),
                    new Page(Quest.Kind.FACTION, "", 12, 120,
                            "campaign.tolkienmobs.page.levy", "campaign.tolkienmobs.page.levy.place"),
                    new Page(Quest.Kind.FETCH, "tolkienmobs:item_scroll", 1, 0,
                            "campaign.tolkienmobs.page.steward", "campaign.tolkienmobs.page.steward.place")),
            Alliance.HARAD, List.of(
                    new Page(Quest.Kind.FACTION, "", 6, 60,
                            "campaign.tolkienmobs.page.sun", "campaign.tolkienmobs.page.sun.place"),
                    new Page(Quest.Kind.FACTION, "", 12, 120,
                            "campaign.tolkienmobs.page.ford", "campaign.tolkienmobs.page.ford.place"),
                    new Page(Quest.Kind.HUNT, "tolkienmobs:entityttmmumakil", 1, 150,
                            "campaign.tolkienmobs.page.mumak", "campaign.tolkienmobs.page.mumak.place"))
    );

    /**
     * One page of a front's line: what it asks of the player, what the country pays for it, and the two
     * things that say what the step <em>is</em> - its name, and the place it happens in.
     * <p>
     * Both are translation keys rather than words, so a page is read in its reader's language, and both
     * are blank on a page that has no name of its own: the book then falls back to the objective alone
     * rather than printing an empty heading.
     */
    private record Page(Quest.Kind kind, String target, int goal, int standing, String title, String place) {
    }

    /** How many pages a front has. */
    public static int pages(Alliance alliance) {
        return PAGE_LINES.get(alliance).size();
    }

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
    /**
     * The name of the campaign's own page, and the place the book says it is met at. He belongs to no
     * front, so his name cannot sit in a front's line and is written here instead.
     */
    private static final String RIDER_TITLE = "campaign.tolkienmobs.page.rider";
    private static final String RIDER_PLACE = "campaign.tolkienmobs.page.rider.place";

    private static final Map<UUID, Front[]> PROGRESS = new HashMap<>();
    /** What {@link #instance} answers with when there is no server to hold a file. */
    private static final Campaign NO_SERVER = new Campaign(false);
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

    /**
     * The campaign's Rider, tracked per player while he is out there. The map is a shortcut rather
     * than a record: a reloaded world forgets it, and {@link #livingRider} falls back to the tag he
     * carries before it concludes there is nobody to find.
     */
    private static final Map<UUID, UUID> RIDER_ENTITY = new HashMap<>();
    /** The tag every campaign Rider wears, and the tag that ties one to the player he was sent for. */
    private static final String RIDER_TAG = "tolkienmobs:campaign_rider";
    /**
     * Where a player stood when the Rider's page came into their hands. He is owed to the road, not
     * to the room the page was handed over in, so this is where the ride out has to begin.
     */
    private static final Map<UUID, BlockPos> RIDER_CALLED = new HashMap<>();
    /** How far from that door, and how far ahead, he is stood up. */
    private static final int RIDER_CALL_DISTANCE = 24;
    private static final int RIDER_WAY_AHEAD = 24;
    private static final int RIDER_WAY_SPREAD = 6;
    /** How wide a search for a Rider who is already out there, and how often to look. */
    private static final double RIDER_SEARCH = 128.0D;
    private static final int RIDER_CHECK_INTERVAL = 20;

    public static final Codec<Campaign> CODEC = Entry.CODEC.listOf()
            .xmap(Campaign::fromEntries, Campaign::toEntries);

    public static final SavedDataType<Campaign> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Campaign::new, CODEC);

    /**
     * The instance a world with no campaign file starts from. The clearing is the point: these maps are
     * static, so a player who leaves one world and starts another in the same session would otherwise
     * find the last world's stages still standing in them, with no file to overwrite them. When a world
     * does have a file, {@link #fromEntries} clears the same map before it fills it.
     */
    private Campaign() {
        this(true);
    }

    /** A stand-in for a level with no server behind it, which neither reads nor clears anything. */
    private Campaign(boolean fresh) {
        super();
        if (fresh) PROGRESS.clear();
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
            if (CLIENT[alliance.ordinal()] < pages(alliance)) return false;
        }
        return true;
    }

    /**
     * Whether this herald still has something to say to the player, which is what his mark means: a front
     * with pages left, or the campaign's own page once every front is settled and the Rider is not yet
     * behind the player.
     */
    public static boolean clientHasWork(Alliance alliance) {
        if (CLIENT[alliance.ordinal()] < pages(alliance)) return true;
        return clientSealed() && clientRider == 0;
    }

    /**
     * The name of the step a front is on, as the book draws it, or an empty component when that step has
     * no name of its own. Read off the synced stage rather than off the page itself: a stage only moves
     * when the ledger settles a page, so while one is carried the stage is that page's index.
     */
    public static Component clientTitle(Alliance alliance) {
        List<Page> line = PAGE_LINES.get(alliance);
        int stage = CLIENT[alliance.ordinal()];
        return stage < line.size() ? named(line.get(stage).title()) : Component.empty();
    }

    /** Where the front's current step happens, or an empty component when the page does not say. */
    public static Component clientPlace(Alliance alliance) {
        List<Page> line = PAGE_LINES.get(alliance);
        int stage = CLIENT[alliance.ordinal()];
        return stage < line.size() ? named(line.get(stage).place()) : Component.empty();
    }

    /** The campaign's own page, which is the Rider's: what it is called and where he is met. */
    public static Component clientRiderTitle() {
        return named(RIDER_TITLE);
    }

    public static Component clientRiderPlace() {
        return named(RIDER_PLACE);
    }

    private static Component named(String key) {
        return key.isEmpty() ? Component.empty() : Component.translatable(key);
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
        List<Page> line = PAGE_LINES.get(alliance);
        if (stage < line.size()) {
            Page page = line.get(stage);
            // The soldier pages name a different rival each, so the line alternates whose soldiers it
            // wants rather than asking for the same ones twice running.
            String target = page.target();
            if (page.kind() == Quest.Kind.FACTION) {
                List<Alliance> rivals = alliance.rivals();
                target = rivals.get(stage % rivals.size()).id();
            }
            return new Quest(page.kind(), target, page.goal(), 0, alliance.id(), page.standing(), List.of(),
                    "", BlockPos.ZERO, Quest.NOBODY, true);
        }
        return sealed(row) && row[RIDER_SLOT].stage() == 0 ? riderPage() : null;
    }

    /**
     * The Rider, as a page: a hunt of two, priced by all three countries together. Asked of the registry
     * rather than written out, so a page cannot name a mob the world does not have - a hunt whose target
     * is in no registry can never be finished, and the campaign would sit on it for good.
     * <p>
     * Two, and not one, because the page's whole business is the kill that changes nothing. Felling him
     * once paid the page, so the meeting the act turns on - him standing there afterwards, unharmed and
     * uninterested - happened only as a sentence in a changelog. The campaign stands him up again while
     * the page is still carried (see {@link #tick}), so the second meeting is the one the player has to
     * come to terms with, and the second fall is what settles the page.
     */
    private static Quest riderPage() {
        return new Quest(Quest.Kind.HUNT, riderId(), 2, 0, "", RIDER_STANDING, List.of(),
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
        // The Rider is not stood up in the room the page is handed over in: the page is taken at a
        // bench, and a wraith conjured beside it reads as a trick. Where the player stood is noted,
        // and {@link #tick} stands him up once they are out under open sky and a ride away from the
        // door, which is a meeting rather than a magic trick.
        if (isCampaignPage(player, page)) {
            RIDER_CALLED.put(player.getUUID(), player.blockPosition());
        }
    }

    /**
     * Stands the campaign's Rider up in front of the player, out on the ground, and marks him as the
     * campaign's so his death can be told apart from any other Nazgûl the player meets. He is placed
     * at riding distance rather than beside the player, because the meeting the page promises is one
     * the player has to ride out to.
     */
    private static void standRiderUp(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        RandomSource random = level.getRandom();
        Direction facing = player.getDirection();
        int reach = RIDER_WAY_AHEAD + random.nextInt(RIDER_WAY_SPREAD);
        int x = player.blockPosition().getX() + facing.getStepX() * reach;
        int z = player.blockPosition().getZ() + facing.getStepZ() * reach;
        // A chunk that is not loaded has no ground to stand on yet: the next look will find one.
        if (!level.hasChunkAt(new BlockPos(x, player.blockPosition().getY(), z))) return;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        Entity entity = TolkienEntities.ENTITY_TTM_NAZGUL.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob rider)) return;
        rider.snapTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        rider.setPersistenceRequired();
        rider.setCustomName(Component.literal("The Rider"));
        rider.setCustomNameVisible(true);
        rider.addTag(RIDER_TAG);
        rider.addTag("tolkienmobs:rider_" + player.getUUID());

        if (level.addFreshEntity(rider)) {
            RIDER_ENTITY.put(player.getUUID(), rider.getUUID());
        }
    }

    /**
     * Keeps the campaign's Rider in the world while his page is carried and unfinished.
     * <p>
     * Two things are settled here that a one-off spawn at the hand-over could not settle. The first is
     * <em>where</em>: while the player is indoors, or still within sight of the bench the page was
     * taken at, nothing happens - and once they are out under open sky and a ride away from that door,
     * he is waiting a little ahead of them. The second is <em>whether he still is</em>: a world reloaded
     * forgets which body was his, a player who walks to another country leaves him behind, and either
     * way the page must not turn into a hunt with nothing left to hunt. What keeps a second Rider from
     * being stood up after the first is killed is the page itself - the kill is counted while the page
     * is still carried, so a finished page means the meeting has already happened.
     */
    public static void tick(ServerPlayer player) {
        if (player.tickCount % RIDER_CHECK_INTERVAL != 0) return;
        Quest page = carriedCampaignPage(player);
        if (page == null || page.complete()) {
            RIDER_CALLED.remove(player.getUUID());
            return;
        }
        if (livingRider(player) != null) return;
        BlockPos called = RIDER_CALLED.get(player.getUUID());
        if (called == null) {
            // Owed with no place remembered - a reloaded world - so the road starts under their feet.
            RIDER_CALLED.put(player.getUUID(), player.blockPosition());
            return;
        }
        if (!player.level().canSeeSky(player.blockPosition())) return;
        int dx = player.blockPosition().getX() - called.getX();
        int dz = player.blockPosition().getZ() - called.getZ();
        if (dx * dx + dz * dz < RIDER_CALL_DISTANCE * RIDER_CALL_DISTANCE) return;
        standRiderUp(player);
        if (RIDER_ENTITY.containsKey(player.getUUID())) {
            // The first meeting is what the page promised. Every one after it is him answering the only
            // thing the player has to say to him, which is that they killed him.
            player.sendSystemMessage(Component.translatable(page.progress() > 0
                            ? "quest.tolkienmobs.rider.again"
                            : "quest.tolkienmobs.rider.coming")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    /** The campaign's own page if the player is carrying it and has not finished it yet. */
    @Nullable
    private static Quest carriedCampaignPage(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (isCampaignPage(player, quest)) return quest;
        }
        return null;
    }

    /**
     * The Rider this player is owed, if he is already out there. The tracked id is tried first, and a
     * Rider found by tag is written back into it - which is how a reloaded world, or a player who
     * walked to another country, is kept from being handed a second one.
     */
    @Nullable
    private static Entity livingRider(ServerPlayer player) {
        UUID known = RIDER_ENTITY.get(player.getUUID());
        if (known != null) {
            Entity rider = player.level() instanceof ServerLevel level ? level.getEntity(known) : null;
            if (rider != null && !rider.isRemoved()) return rider;
            RIDER_ENTITY.remove(player.getUUID());
        }
        for (Entity rider : player.level().getEntities(player,
                player.getBoundingBox().inflate(RIDER_SEARCH), found -> isCampaignRider(player, found))) {
            RIDER_ENTITY.put(player.getUUID(), rider.getUUID());
            return rider;
        }
        return null;
    }

    /**
     * Checks whether a killed entity is the campaign's Rider for a given player. The Rider is
     * identified by his tags, which include the player's UUID.
     */
    public static boolean isCampaignRider(ServerPlayer player, Entity victim) {
        return victim.entityTags().contains(RIDER_TAG)
                && victim.entityTags().contains("tolkienmobs:rider_" + player.getUUID());
    }

    /**
     * Called when the campaign's Rider dies. The Rider is removed from the tracking map, but the
     * quest is not settled here - that happens through the normal quest ledger when the player
     * hands in the completed quest. The Rider can be respawned fresh for the next stage.
     */
    public static void onRiderDeath(ServerPlayer player) {
        RIDER_ENTITY.remove(player.getUUID());
    }

    /**
     * Whether a page is the campaign's own rather than one of a front's. Read off what the page asks for
     * rather than off what was noted as handed over, because this is asked while a page is still being
     * handed out - before the slot knows about it - and again of every copy already in a book.
     */
    public static boolean isCampaignPage(ServerPlayer player, Quest quest) {
        return quest.story() && quest.kind() == Quest.Kind.HUNT && quest.target().equals(riderId());
    }

    /**
     * Whether the player is already carrying a page of this front, whoever handed it over. Two halls of
     * one alliance - an old one and a new one, or a hall beside a giver stood up by a command - would
     * otherwise each write a page for the same step, and only the last of them could ever settle it.
     */
    public static boolean holdsFrontPage(ServerPlayer player, Alliance alliance) {
        for (Quest quest : QuestLedger.active(player)) {
            if (quest.story() && !isCampaignPage(player, quest) && alliance.id().equals(quest.employer())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether the page paid off is the page a slot handed out. Compared by its terms rather than by the
     * record itself, because the copy the player carries has moved on by every blow they landed: the
     * noted page sits at no progress from the moment it is handed over, so equality would never recognise
     * a finished one, and the front would hand the same page out again for ever.
     */
    private static boolean sameErrand(@Nullable Quest handed, Quest paid) {
        return handed != null && handed.kind() == paid.kind()
                && handed.target().equals(paid.target())
                && handed.employer().equals(paid.employer())
                && handed.goal() == paid.goal();
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
            if (!sameErrand(front.handed(), quest)) continue;
            row[alliance.ordinal()] = new Front(front.stage() + 1, null);
            store(player).setDirty();
            sync(player);
            return false;
        }
        Front rider = row[RIDER_SLOT];
        if (!sameErrand(rider.handed(), quest)) return false;
        row[RIDER_SLOT] = new Front(1, null);
        store(player).setDirty();
        sync(player);
        return true;
    }

    /**
     * Which slot of a player's row a page belongs to: the campaign's own, or the herald's front. The
     * Rider is picked out by what he asks for rather than by the kind of work, because a front can end
     * on a hunt of its own country's beast - and a hunt written into the campaign's slot would end the
     * war on the first wight a player felled, long before any front was settled.
     */
    private static int slotOf(Quest page, Alliance alliance) {
        return page.kind() == Quest.Kind.HUNT && page.target().equals(riderId())
                ? RIDER_SLOT
                : alliance.ordinal();
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
            if (row[alliance.ordinal()].stage() < pages(alliance)) return false;
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
        return NO_SERVER;
    }

    private static Campaign fromEntries(List<Entry> entries) {
        PROGRESS.clear();
        for (Entry entry : entries) {
            PROGRESS.put(entry.player(), new Front[]{entry.west(), entry.east(), entry.harad(),
                    entry.rider()});
        }
        // Read without clearing: the map was just filled from the file.
        return new Campaign(false);
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
