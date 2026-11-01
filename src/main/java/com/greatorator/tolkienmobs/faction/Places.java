package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.ArdaSurvey;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The world's named ground: what a place is called, where it has been set, and who has already been
 * told.
 * <p>
 * Three things are kept apart on purpose, because they are owned by different people. The
 * <em>catalogue</em> is the campaign's - a fixed list of names with the one line each of them is
 * introduced by, and the country that colours it - and it lives in this file rather than in a world,
 * because the Hobbiton of one save is the Hobbiton of the next. The <em>placements</em> are the
 * world's: a place has no coordinates until somebody standing where it belongs says so, since the
 * generator puts hills where it likes and no two worlds put a barrow in the same gap. The
 * <em>sightings</em> are the player's: each of them is told about a place once, ever, which is the
 * whole of what announcing a place means.
 * <p>
 * The banner follows the campaign's own older notes on how a region should announce itself, which
 * were written as command blocks: a title over a line, both of them bold and italic so the words read
 * as a nameplate for the country rather than as a chat message, a second to arrive and five to be
 * read and a second to leave, and a score set afterwards so nobody who has been through is told
 * again. The colour of the name is whose country it is: the three alliances wear their own ink, and
 * ground that belongs to nobody is written plainly.
 * <p>
 * A place is a circle around a point rather than a border, because a border needs a shape and a world
 * that has not been built yet cannot say what shape its country is. Where two circles overlap the
 * smaller one wins, so a village named inside a road named inside a country is read as the village.
 */
public final class Places extends SavedData {
    private static final String SAVE_DATA_NAME = "places";

    /**
     * The banner's timing, as the command blocks had it: one second in, five held, one out. Long
     * enough for a four-word line, short enough that it never covers the view of somebody arriving
     * somewhere dangerous.
     */
    private static final int FADE_IN = 20;
    private static final int STAY = 100;
    private static final int FADE_OUT = 20;

    /** How wide a place is unless it says otherwise, which is the radius the notes used. */
    public static final int DEFAULT_RADIUS = 32;
    /**
     * How much ground a place needs before it is named: a circle has to be stood in, not built on, so
     * it asks the map for far less than a building does and is found far more often.
     */
    private static final int PLACE_GROUND = 5;
    /** How often a player's feet are looked at. A circle thirty blocks across cannot be crossed in one. */
    private static final int CHECK_INTERVAL = 20;
    /**
     * One name the world can be told about: the id its translation keys hang off, the country whose
     * ink it is written in - or none, for ground that belongs to nobody, which is written plainly
     * rather than being given to the nearest power - and whether the stone has anything to say here.
     * <p>
     * That last one is a mark rather than a list kept elsewhere, so ground is marked by the entry
     * that describes it: writing a place's sheet is what makes the beryl react to it, and a place
     * without a sheet is ground the stone has no opinion about.
     * <p>
     * The line under the name is not stored here because it is not a fact about the place: it is the
     * second translation key, {@code place.tolkienmobs.<id>.line}, so a name and what it is to the
     * people who live there are written together and read in the reader's own language.
     */
    public record Name(String id, @Nullable Alliance alliance, boolean beryl) {
        /** The key the place's name is written under. */
        public String key() {
            return "place.tolkienmobs." + id;
        }

        /** The key the stone's answer for this place is written under. */
        public String berylKey() {
            return key() + ".beryl";
        }

        /** The key the one line introducing it is written under. */
        public String lineKey() {
            return key() + ".line";
        }

        /** The ink its name is written in, which is its country's or nobody's. */
        public ChatFormatting ink() {
            return alliance == null ? ChatFormatting.WHITE : alliance.chat();
        }
    }

    /**
     * The names this world can have, in the order an operator reads them. The West's country, as the
     * campaign's first act walks it: the road the whole line starts on, the mounds its third page
     * ends at, and the places a player passes between them. The other two fronts are added here when
     * their lines are written, which is the same shape of work as a page.
     */
    private static final List<Name> CATALOGUE = List.of(
            new Name("greenway", Alliance.WEST, true),
            new Name("barrows", Alliance.WEST, true),
            new Name("hobbiton", Alliance.WEST, false),
            new Name("bree", Alliance.WEST, false),
            new Name("weathertop", Alliance.WEST, true),
            new Name("ford", Alliance.WEST, true),
            new Name("tharbad", Alliance.WEST, true),
            new Name("oldforest", null, false)
    );

    /**
     * One place as this world has it: which of the catalogue it is, the dimension it stands in, the
     * point its circle is drawn around, and how wide that circle is.
     */
    public record Place(String id, ResourceKey<Level> dimension, BlockPos centre, int radius) {
        static final Codec<Place> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(Place::id),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Place::dimension),
                BlockPos.CODEC.fieldOf("centre").forGetter(Place::centre),
                Codec.INT.optionalFieldOf("radius", DEFAULT_RADIUS).forGetter(Place::radius)
        ).apply(instance, Place::new));
    }

    /** Where each place of the catalogue has been set in this world, by id. */
    private static final Map<String, Place> PLACED = new LinkedHashMap<>();
    /** What each player has already been shown, by their id. */
    private static final Map<UUID, Set<String>> SEEN = new HashMap<>();
    /** What {@link #instance} answers with when there is no server to hold a file. */
    private static final Places NO_SERVER = new Places(false);

    public static final Codec<Places> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Place.CODEC.listOf().optionalFieldOf("places", List.of()).forGetter(Places::placeList),
            Seen.CODEC.listOf().optionalFieldOf("seen", List.of()).forGetter(Places::seenList)
    ).apply(instance, Places::from));

    public static final SavedDataType<Places> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Places::new, CODEC);

    /**
     * A world with no places file starts from nothing named. These maps are static, so a player who
     * leaves one world and opens another in the same session would otherwise carry the first world's
     * towns and sightings into the second - the same trap the campaign's own file had to clear.
     */
    private Places() {
        this(true);
    }

    /** A stand-in for a level with no server behind it, which neither reads nor clears anything. */
    private Places(boolean fresh) {
        super();
        if (fresh) {
            PLACED.clear();
            SEEN.clear();
        }
    }

    // ---- the catalogue ----

    /** Every name this world can have, in the order they are listed. */
    public static List<Name> catalogue() {
        return CATALOGUE;
    }

    /** The name with this id, or null when the catalogue has no such place. */
    @Nullable
    public static Name known(String id) {
        for (Name name : CATALOGUE) {
            if (name.id().equals(id)) return name;
        }
        return null;
    }

    // ---- placing ----

    /** Sets a place at a point, replacing it if this world already had one of that name. */
    public static Place set(ServerLevel level, String id, BlockPos centre, int radius) {
        Places store = instance(level);
        Place place = new Place(id, level.dimension(), centre, Math.max(1, radius));
        PLACED.put(id, place);
        store.setDirty();
        return place;
    }

    /** Takes a place out of this world, and answers whether there was one to take out. */
    public static boolean remove(Level level, String id) {
        Places store = instance(level);
        if (PLACED.remove(id) == null) return false;
        store.setDirty();
        return true;
    }

    /** Where this world has put a place, or null when it has not been set here yet. */
    @Nullable
    public static Place place(Level level, String id) {
        instance(level);
        return PLACED.get(id);
    }

    /**
     * A place this world has named, naming it first if it never was.
     * <p>
     * A placement has always been an operator's to make - somebody stands where a place ought to be and
     * says so - and that is the right way to put Weathertop where Weathertop belongs. What it is not is
     * a thing the campaign can wait on: a walk page is handed by a herald, and a page whose ground this
     * world has never named is a page nobody can finish, so a front that needs one stops at it for good.
     * The catalogue's names are ground the map already draws, so the map is asked where one of them would
     * stand - the same pick a structure gets, made in the country the drawing puts it - and the answer is
     * written down as a placement. The banner then fires on arrival exactly as it does at a hand-placed
     * one, and a world where somebody has already placed it is left alone.
     *
     * @return the place, or null when the catalogue has no such name or the map draws no ground for it
     */
    @Nullable
    public static Place ensure(ServerPlayer player, String id, int radius) {
        Place existing = place(player.level(), id);
        if (existing != null) return existing;
        Name name = known(id);
        if (name == null || !(player.level() instanceof ServerLevel level)) return null;
        // A name is written where the drawing puts it, and the drawing's pick inside a cell is fixed
        // by the cell and the name, so a place needs no distance of its own: two players asking for the
        // same name are sent to the same ground wherever they were standing when they asked.
        ArdaSurvey.Ground ground = ArdaSurvey.find(level, player.blockPosition(),
                ArdaMap.Country::land, PLACE_GROUND, PLACE_GROUND, id.hashCode(), 0, 0);
        if (ground == null) return null;
        return set(level, id, ground.centre(), radius);
    }

    /** Every place this world has set, in the catalogue's order. */
    public static List<Place> placed(Level level) {
        instance(level);
        return List.copyOf(PLACED.values());
    }

    /**
     * The place a player is standing in, or null when they are between places. Where two overlap the
     * smaller wins, so the village is read rather than the country it sits in.
     */
    @Nullable
    public static Place at(ServerPlayer player) {
        instance(player.level());
        ResourceKey<Level> here = player.level().dimension();
        Place best = null;
        for (Place place : PLACED.values()) {
            if (!place.dimension().equals(here)) continue;
            double reach = place.radius();
            if (place.centre().distToCenterSqr(player.position()) > reach * reach) continue;
            if (best == null || place.radius() < best.radius()) best = place;
        }
        return best;
    }

    // ---- being told ----

    /**
     * Looks at where a player's feet are, and introduces them to the place they have just come into
     * if they have never been introduced to it. Called from the server's player tick, so it costs a
     * distance check a second per player and nothing at all when nothing is named.
     */
    public static void tick(ServerPlayer player) {
        if (player.tickCount % CHECK_INTERVAL != 0) return;
        // A page that asks to be walked somewhere is settled by the ground under the player's
        // feet, so it is looked at on the clock that already measures where they are standing.
        // Built ground is built on the same clock: close enough to see it is close enough for the
        // chunk to be loaded, so nothing is raised into ground nobody is looking at.
        Sites.tick(player);
        QuestLedger.checkArrival(player);
        Place here = at(player);
        if (here == null) return;
        if (!markSeen(player, here.id())) return;
        announce(player, here);
    }

    /** Whether a player has already been shown a place. */
    public static boolean seen(ServerPlayer player, String id) {
        instance(player.level());
        return SEEN.getOrDefault(player.getUUID(), Set.of()).contains(id);
    }

    /**
     * Notes that a player has been shown a place, and answers whether this was the first time. A
     * sighting is written down the same tick the banner is sent, so a player who logs out inside a
     * town is not told about it again next time they log in.
     */
    private static boolean markSeen(ServerPlayer player, String id) {
        Places store = instance(player.level());
        Set<String> seen = SEEN.computeIfAbsent(player.getUUID(), ignored -> new HashSet<>());
        if (!seen.add(id)) return false;
        store.setDirty();
        return true;
    }

    /** Forgets every place a player has been shown, so a test can walk the same road twice. */
    public static int forget(ServerPlayer player) {
        Places store = instance(player.level());
        Set<String> seen = SEEN.remove(player.getUUID());
        if (seen == null || seen.isEmpty()) return 0;
        store.setDirty();
        return seen.size();
    }

    /**
     * Introduces a place: the name over the line, both bold and italic, the name in the ink of
     * whoever holds the ground and the line in the plain white of a subtitle, and the command blocks'
     * own timing in front of both.
     */
    private static void announce(ServerPlayer player, Place place) {
        Name name = known(place.id());
        // A place set by a pack under an id this build has no words for is a placement looking for
        // words rather than a banner worth showing.
        if (name == null) return;
        Component title = Component.translatable(name.key())
                .withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC, name.ink());
        Component line = Component.translatable(name.lineKey())
                .withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC);
        player.connection.send(new ClientboundSetTitlesAnimationPacket(FADE_IN, STAY, FADE_OUT));
        player.connection.send(new ClientboundSetSubtitleTextPacket(line));
        player.connection.send(new ClientboundSetTitleTextPacket(title));
    }

    /**
     * Whether the player is carrying the beryl, which is what asks the stone its question. Read by
     * the campaign too, because the page the stone is lent on is only finished by a hand holding
     * it: the ground answers the stone, and there is nothing to answer without one.
     */
    public static boolean carriesBeryl(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).is(TolkienItems.ITEM_BERYL.get())) return true;
        }
        return false;
    }

    /**
     * What the stone has to say about the ground a player is standing on, or null where that ground
     * has no answer written - most of the world does not, and a stone that spoke everywhere would be a
     * map rather than a stone. Read when the beryl is held up, which is the only way to ask it: a line
     * that arrives by itself is the ground talking, not the stone.
     */
    @Nullable
    public static MutableComponent readingAt(ServerPlayer player) {
        Place here = at(player);
        if (here == null) return null;
        Name name = known(here.id());
        if (name == null || !name.beryl()) return null;
        return Component.translatable(name.berylKey()).withStyle(ChatFormatting.ITALIC);
    }

    // ---- plumbing ----

    private static Places instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return NO_SERVER;
    }

    private List<Place> placeList() {
        return List.copyOf(PLACED.values());
    }

    private List<Seen> seenList() {
        List<Seen> entries = new ArrayList<>(SEEN.size());
        for (Map.Entry<UUID, Set<String>> row : SEEN.entrySet()) {
            entries.add(new Seen(row.getKey(), List.copyOf(row.getValue())));
        }
        return entries;
    }

    private static Places from(List<Place> places, List<Seen> seen) {
        PLACED.clear();
        SEEN.clear();
        for (Place place : places) {
            PLACED.put(place.id(), place);
        }
        for (Seen entry : seen) {
            SEEN.put(entry.player(), new HashSet<>(entry.places()));
        }
        // Read without clearing: the maps were just filled from the file.
        return new Places(false);
    }

    /** One player's sightings, as a file holds them. */
    private record Seen(UUID player, List<String> places) {
        static final Codec<Seen> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Seen::player),
                Codec.STRING.listOf().optionalFieldOf("places", List.of()).forGetter(Seen::places)
        ).apply(instance, Seen::new));
    }
}
