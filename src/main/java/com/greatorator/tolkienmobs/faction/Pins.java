package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ground that was walked before it was named: the block a location stands on, chosen in a world whose
 * hills have already been made rather than computed from the drawing.
 * <p>
 * Everything else in this package places ground by asking the map: a country's middle, a cell's
 * middle, then the nearest column inside that search that will take a footprint. That is an answer to
 * "somewhere in this part of the country", and it is the right answer for a world nobody has looked
 * at yet. It is not an answer to "here": a scout who has flown the country, found the gap between the
 * crags and the dry bench above the water, and written the numbers down has already done the work the
 * survey was approximating, and the survey should not be asked to guess it back.
 * <p>
 * So a pin is a location written down twice. Once in the <em>world</em>, by {@code /tmpin}: somebody
 * standing on the spot says this is where the camp goes, and the placement paths obey it for that
 * world from then on. Once in {@link #SOURCE}, for every world: a pin scouted once and worth keeping
 * goes in the table in this file, so the same camp stands in the same place in a world nobody has
 * walked. The command prints that line, so pinning is a copy rather than a transcription.
 * <p>
 * The table is read before the drawing is, by both of the things that place ground - {@link Places}
 * for a name, {@link Sites} for a building - so a pinned location is exact, and the survey is left to
 * the laying out of the footprint, which still has to sit on ground the world actually made.
 * <p>
 * A pin is also what lets a name stand where the drawing paints nothing: the valley is the case that
 * matters, since no column of the map is sampled as IMLADRIS while the city is only planned. Pinned by
 * hand, it is ground like any other, and the pages that need it stop waiting on a drawing that has not
 * caught up.
 * <p>
 * The y is the surface the scout stood on. It is not used to seat a building - the laying measures the
 * ground under the footprint and seats on that - so a y a block or two out is not an error, only a
 * note of where the scout was standing.
 */
public final class Pins extends SavedData {
    private static final String SAVE_DATA_NAME = "pins";

    /**
     * The pins that hold for every world, keyed by whatever the code asks with: a place's id as the
     * catalogue writes it ({@code "rivendell"}, {@code "thorenhad"}), or a structure's template id as
     * a page names it ({@code "tolkienmobs:key_structures/barachans_camp"}).
     * <p>
     * Empty is the normal state and a working one: a location with no pin is surveyed from the drawing
     * exactly as it was before any of this existed, so the campaign can be walked with an empty table.
     * Lines added here are printed by {@code /tmpin}, which is the intended way to get them.
     * <p>
     * A pin that stands on the country was re-read against the drawing on 24 November 2026, when black moved to y 0,
     * white to 318 and water to 25: its y is the ground of its own column, one over it where a body or a name stands
     * there, so a {@code /tmtp} arrives on the surface rather than inside it. That took the Shire's camp pins up by a
     * stone's throw, forty-two at Barachen's fire, and the High Pass's camp down by forty-four. A pin inside a build
     * - the village's own marks, the halls laid on their pins - keeps its offset from that build instead, and a pin
     * underground keeps the y it was cut at.
     */
    private static final Map<String, BlockPos> SOURCE = new LinkedHashMap<>(Map.ofEntries(
            // Rivendell has no pin while its site is re-scouted: the old one, 5200, 75, -3370, was the block the
            // cut's low corner went on, and both it and the stone inside the city were removed on 24 November 2026.
            // The new pin goes on the block the replaced cut's lowest band stands on, and the city occupies that y
            // to y + 243. It is not in this table until the site is walked.
            // The Shire's village, raised at the pond the owner marked on 26 November 2026: the cut is planted at its low
            // corner, 866, 70, -2917, and this is the same gate in the cut that -463, 44, -124 was in the village walked
            // in Progress Check 3 - the corner plus 237 east, 11 up and 388 south, which is the block the cut leaves
            // from. A pin is where the name stands rather than where the cut starts, so the corner is not the gate and
            // the corner is not the pin: /tmtp should put a traveller inside the village, not on its wall. A world that
            // already holds the old village keeps its own saved pin, so nothing moves under it.
            Map.entry("hobbiton", new BlockPos(1103, 81, -2529)),
            // Bree, walked in the same world: the town's own arrival point at the head of the hall's front line, one
            // over the ground the hall's markers were set in. It was the middle of the cut's footprint - 1272, 122, 80 -
            // which is a fifth of the way across a 496 by 672 town from its west gate.
            Map.entry("bree", new BlockPos(1059, 77, -31)),
            // Bucklebury, the first village east of the crossing, pinned for a build that does not exist yet: the east
            // bank 150 east and 60 south of the Brandywine bridge at 1230, -3490, ground 90, dry from the bridge's own
            // east foot and dry the whole 1,076 blocks to Bree. The town was lifted four blocks on 27 November 2026, so
            // its own floor at this column is 95 rather than 91 - the pin rises with it, since it stands on the floor.
            // It has no cut, no template and no catalogue entry, so the
            // key is inert until a build asks for it - the ground is written down so it cannot be lost, and /tmtp
            // finds it by that name as a set point in the meantime; the catalogue does not carry it, so /tmpin
            // place refuses it. The reference sheet's own label at pixel 795, 740 is this place, not Hobbiton.
            Map.entry("bucklebury", new BlockPos(1380, 95, -3432)),
            // Michel Delving, pinned for the same reason: the flat shelf 906 blocks due west of Hobbiton, ground 103,
            // with the land climbing away west and north and a shallow vale to the south-east. Four blocks of relief
            // across a hundred and eight across two hundred, so a town plan has room; the whole line from Hobbiton is
            // dry and steps up by at most one block a column, so a road west would grade without a cut. No cut yet.
            Map.entry("micheldelving", new BlockPos(-40, 104, -2740)),
            // Hobbiton's mustering hall, on the ground it was raised on and has stood on since: pinned at -529, 101,
            // -74 by /tmpin hall add in the owner's Progress Check 1 world, and read back out of that world's own pins
            // once the hall was up and called perfect. The key comes from TownHalls rather than being written here, so
            // the table and the command cannot disagree about how a town's hall is named. The hall is laid at the pin's
            // own column, so it came down with the village's seven: 54 to 47. It moves with the village on 26 November
            // 2026 by the same offsets it had in the cut - 171 east, 14 up and 438 south of the corner - so it stands in
            // the new footprint at 1037, 84, -2479 and not two thousand blocks from it, on the ground its old village left.
            Map.entry(TownHalls.key("hobbiton"), new BlockPos(1037, 84, -2479)),
            // Bree's mustering hall, marked in the owner's Progress Check 3 world: two blocks of ammolite at 1031 and
            // 1032, 122, -32, set into the grass on the town's west side and pointed at the middle two blocks of the
            // hall's own front - the 16-block side the job board is on. The hall is laid turned half round from the
            // pin's corner (TownHalls.TURNED), which puts its board on the box's low-z face, so the line the markers
            // stand on is that face: the box runs 1024..1039 by -32..-17, its west wall on the cut's own west edge, and
            // its floor course goes into the block the markers stand in, level with the ground round it. The pin is the
            // block a builder stands in, which is the course above the floor.
            Map.entry(TownHalls.key("bree"), new BlockPos(1024, 77, -32)),
            // The Carrock, pinned where the owner stood it: the cut's own low north-west corner at 8307, 187, -4165, the
            // block /tmcut place carrock is given so the box lands where the owner already had it. The rock is cut rather
            // than templated, so Carrock.autospawn reads this pin on the first start of a world and lays the cut from it:
            // the pin is the corner the pieces are planted on, and /tmpin is still how it is moved.
            Map.entry(Carrock.NAME, new BlockPos(8307, 156, -4165)),
            // The goblin cave page 27 is fought in, settled along the road at the door the scout walked: the
            // mountain rises south of it, so the cut runs into the hill there. The shelter camp beyond the way in
            // is pinned beside it, so the fires have a camp outside them in every world rather than only in the
            // one they were scouted in - a pin under a template's own name is what Sites raises, and the two of
            // these are the pair the chapter walks up to.
            Map.entry(GoblinCave.PIN, new BlockPos(5628, 146, -3621)),
            Map.entry(Sites.SHELTER_CAMP, new BlockPos(5595, 143, -3621)),
            // The dwarven keep: its own north-west bottom corner, which is the plan the pass was cut to and the box
            // GundabadKeep lays on the first start of a world. Pinned because the hold is a hundred and twenty-two by
            // a hundred and eighty-three and there is exactly one box in the massif it fits - the one the chamber's
            // marked floor was measured into - so a page that names Gundabad is pointed at the hold that stands
            // rather than at a second one surveyed out of the country's middle.
            Map.entry(Sites.GUNDABAD, ArdaMap.keepPlan()),
            // The Key-stone's vault, which is not a ruin at the source of the Bruinen: it is the cave east of the gate,
            // and the stone is kept in it. Pinned to the room's own dais - the mark Gimli stands on, read through the
            // cave's own instance so a pin and a body cannot drift apart - because the name was never pinned and a name
            // without a pin is surveyed out of the middle of the country the map paints, which left page 2-5 pointed at
            // a hill a chapter's walk from anywhere the chapter had been.
            Map.entry("keystone", GundabadCave.post()),
            // Chapter one, walked in the scouting world: the names on the ground their pages are spoken on, and
            // the camps and the four crags on the hillsides they were found on. The y is where the scout stood;
            // the laying measures the ground under a footprint and seats it, so the block is a target and not a
            // measurement. Keys go through the Sites constants, so a checklist line and a placement cannot drift.
            Map.entry("thorenhad", new BlockPos(4691, 119, -3273)),
            Map.entry("barachen", new BlockPos(4812, 120, -2885)),
            // Barachen's own camp, raised by the world rather than by a page: the corner of the doorway the scout
            // marked at 4820, 76, -2885, that block being the middle of the template's south face, so the corner
            // is eight west of it and the build is seated level with the ground the road arrives on.
            Map.entry(Sites.BARACHENS_CAMP, new BlockPos(4812, 120, -2885)),
            Map.entry("ford", new BlockPos(5061, 118, -3136)),
            // Elladan's camp, read back out of the scouted save rather than written down from the drawing: the world
            // raised it at 4640, 92, -3313, six east and one north of the pin that asked for it, with its floor four
            // blocks above the ground outside its door. Its seat drops it by that four, so the pin is written where
            // the camp will stand - the corner, dropped - and not where it happened to be standing when it was read.
            Map.entry(Sites.ELLADAN_CAMP, new BlockPos(4640, 117, -3313)),
            Map.entry(Sites.WOOD_RUIN, new BlockPos(4782, 121, -3058)),
            Map.entry(Sites.TROLL_CAVE, new BlockPos(4460, 112, -2477)),
            Map.entry(Sites.TROLL_CAVE_2, new BlockPos(4568, 116, -2511)),
            Map.entry(Sites.TROLL_CAVE_3, new BlockPos(4508, 115, -2429)),
            // Matuna's lair, the wood-troll's own crag: the hill the scout marked in the wood east of the valley, at
            // 4633, 94, -3081. Pinned like the stone's three, so the lair stands whether or not the page that names him
            // has been taken, and /tmpin and /tmtp have a name for it at last. Without a pin the crag had none of that:
            // a command asking for it was told nothing was called that, and the page's own survey fell back on the
            // middle of the Trollshaws.
            Map.entry(Sites.TROLL_CAVE_4, new BlockPos(4633, 122, -3081)),
            // Where the ruin's animal lies and where Barachen's keeper stands, marked after both builds had already
            // been raised: a building is dressed every visit now, so these are read rather than only laid once.
            Map.entry(Sites.WOOD_RUIN + ".body", new BlockPos(4787, 121, -3052)),
            Map.entry(Sites.BARACHENS_CAMP + ".keeper", new BlockPos(4822, 120, -2875)),
            // The road half of each doorway pair, recorded where a route can read it: the second marker of a pair
            // is the last block of the road that reaches that door, and the ruin's and Barachen's were both walked.
            Map.entry(Sites.WOOD_RUIN + ".end", new BlockPos(4787, 121, -3058)),
            Map.entry(Sites.BARACHENS_CAMP + ".end", new BlockPos(4820, 120, -2886)),
            // Elladan's camp's own two: the door's block and the ground outside it, and the spot inside the camp a
            // scout stood on, both walked on the camp as it stands and both written four lower - the drop its seat
            // gives it - so the man at the fire stands on the floor that will be under him rather than the one under
            // him now.
            Map.entry(Sites.ELLADAN_CAMP + ".end", new BlockPos(4648, 117, -3314)),
            Map.entry(Sites.ELLADAN_CAMP + ".keeper", new BlockPos(4645, 117, -3308)),
            // Where the road out of the valley's crossing ends and the next one begins: the marker at 4972 is both,
            // so the road south to Thorenhad starts here rather than at the water.
            // The ford's marker as the redux world reads it: one block west of where it was first written down, and
            // two lower, which is the ground beside the road's last block rather than on it.
            Map.entry("ford.road", new BlockPos(4971, 118, -3075)),
            // The four crags' road ends, read off the ammolite markers set down in the redux world once the crags were
            // standing. Each sits on its crag's footprint edge, twenty-seven or twenty-eight blocks out from the
            // middle of it, which is the ground a road arriving at a crag has to meet. Recorded so that a road to any
            // of the stone's three, or to Matuna's lair, has an end to grade to instead of the survey's guess. The
            // chapter's other road ends were written down earlier: the ruin's, Barachen's fire's, Thorenhad's door
            // and the valley's two banks.
            Map.entry(Sites.TROLL_CAVE + ".end", new BlockPos(4515, 115, -2446)),
            Map.entry(Sites.TROLL_CAVE_2 + ".end", new BlockPos(4624, 117, -2479)),
            Map.entry(Sites.TROLL_CAVE_3 + ".end", new BlockPos(4562, 116, -2398)),
            Map.entry(Sites.TROLL_CAVE_4 + ".end", new BlockPos(4689, 122, -3050)),
            // Chapter two's camp, walked at the High Pass's east mouth: 8215, -3674 is the corner the 28 by 22 camp
            // is laid from, so its south wall stands on the doorway the scout marked at 8229, -3652 and its floor runs
            // twelve blocks north of it, where he stood inside it at 8233, -3664. The .end is that doorway's road
            // block, the ground outside the wall the camp's own track leaves from.
            Map.entry(Sites.GLOIN_CAMP, new BlockPos(8215, 181, -3674)),
            Map.entry(Sites.GLOIN_CAMP + ".end", new BlockPos(8229, 180, -3652)),
            // The camp's own name, written at the middle of the footprint the pin above lays: a page that names
            // Glóin's camp asks for ground by the name, and the middle is where the fire and the man at it stand.
            Map.entry("gloin", new BlockPos(8229, 180, -3663)),
            // The departure's own courtyard, read off eleven marker blocks laid on the staging plaza at
            // 5117, -3409 on 10 November 2026 - one block per body, the mithril one Elrond's. The blocks were
            // a survey and are not wanted again: they stand in the world they were laid in and in no other, so
            // the ground they gave is written here and every world stands the Company on it. Elrond's own is
            // also the gathering mark - where the note's scene begins and where a rehearsal is held - and the
            // walk out's first waypoint is derived from the ring rather than pinned.
            Map.entry(Departure.GATHER_PIN, new BlockPos(5117, 77, -3407)),
            Map.entry(Departure.bodyPin("elrond"), new BlockPos(5117, 77, -3407)),
            Map.entry(Departure.bodyPin("gandalf"), new BlockPos(5117, 77, -3412)),
            Map.entry(Departure.bodyPin("aragorn"), new BlockPos(5115, 77, -3413)),
            Map.entry(Departure.bodyPin("gimli"), new BlockPos(5113, 77, -3411)),
            Map.entry(Departure.bodyPin("legolas"), new BlockPos(5121, 77, -3411)),
            Map.entry(Departure.bodyPin("boromir"), new BlockPos(5119, 77, -3413)),
            Map.entry(Departure.bodyPin("bilbo"), new BlockPos(5115, 77, -3407)),
            Map.entry(Departure.bodyPin("frodo"), new BlockPos(5116, 77, -3410)),
            Map.entry(Departure.bodyPin("samwise"), new BlockPos(5118, 77, -3410)),
            Map.entry(Departure.bodyPin("pippin"), new BlockPos(5114, 77, -3410)),
            Map.entry(Departure.bodyPin("merry"), new BlockPos(5120, 77, -3410)),
            // The walk out's route, read off the ten ammolite marks laid on the road north of the courtyard
            // on 10 November 2026, the nearest to the plaza first: the Company lines up on the first and
            // walks the rest, which carry them out of sight round the shoulder of the hill. The y is the
            // surface a mark was laid on; the ground is read again on every step, so this is a line of
            // places rather than a set of step heights.
            Map.entry(Departure.walkPin(1), new BlockPos(5117, 77, -3420)),
            Map.entry(Departure.walkPin(2), new BlockPos(5116, 80, -3425)),
            Map.entry(Departure.walkPin(3), new BlockPos(5113, 81, -3428)),
            Map.entry(Departure.walkPin(4), new BlockPos(5110, 82, -3431)),
            Map.entry(Departure.walkPin(5), new BlockPos(5108, 83, -3434)),
            Map.entry(Departure.walkPin(6), new BlockPos(5103, 85, -3438)),
            Map.entry(Departure.walkPin(7), new BlockPos(5101, 91, -3446)),
            Map.entry(Departure.walkPin(8), new BlockPos(5100, 94, -3451)),
            Map.entry(Departure.walkPin(9), new BlockPos(5096, 95, -3455)),
            Map.entry(Departure.walkPin(10), new BlockPos(5095, 95, -3464))
            // Chapter one is all of it. What is left is chapter two's four and the last act's two.
    ));

    /**
     * The pins this world has been told about, by key. A world pin wins over a source pin: the ground
     * of the world in front of the scout is the ground that matters there, and a pack update that moves
     * a source pin should not drag a world's own choice out from under it.
     */
    private static final Map<String, BlockPos> PINNED = new LinkedHashMap<>();
    /** What {@link #instance} answers with when there is no server to hold a file. */
    private static final Pins NO_SERVER = new Pins(false);

    /** One location as a world's file holds it: the key the code asks with, and the block. */
    public record Pin(String key, BlockPos at) {
        static final Codec<Pin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("key").forGetter(Pin::key),
                BlockPos.CODEC.fieldOf("at").forGetter(Pin::at)
        ).apply(instance, Pin::new));
    }

    public static final Codec<Pins> CODEC = Pin.CODEC.listOf().xmap(Pins::from, Pins::toList);

    public static final SavedDataType<Pins> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Pins::new, CODEC);

    /** A world with no pins file starts with nothing pinned by hand, and forgets the last world's. */
    private Pins() {
        this(true);
    }

    /** A stand-in for a level with no server behind it, which neither reads nor clears anything. */
    private Pins(boolean fresh) {
        super();
        if (fresh) PINNED.clear();
    }

    /**
     * The block a location was pinned to, or null when it has not been scouted yet - which is the
     * answer for every location on a fresh checkout, and leaves the drawing to speak.
     * <p>
     * The world is read first and the file second, so a spot chosen in this world is not overruled by
     * one written into the build.
     */
    @Nullable
    public static BlockPos at(@Nullable Level level, @Nullable String key) {
        if (key == null) return null;
        if (level != null) {
            instance(level);
            BlockPos here = PINNED.get(key);
            if (here != null) return here;
        }
        return SOURCE.get(key);
    }

    /** Pins a location in this world, replacing one of the same key, and answers where it went. */
    public static BlockPos set(Level level, String key, BlockPos at) {
        Pins store = instance(level);
        PINNED.put(key, at);
        store.setDirty();
        return at;
    }

    /** Forgets this world's pin for a key, and answers whether there was one to forget. */
    public static boolean remove(Level level, String key) {
        Pins store = instance(level);
        if (PINNED.remove(key) == null) return false;
        store.setDirty();
        return true;
    }

    /** Every pin this world has been told about, in the order they were given. */
    public static List<Pin> pinned(Level level) {
        instance(level);
        List<Pin> pins = new ArrayList<>(PINNED.size());
        for (Map.Entry<String, BlockPos> row : PINNED.entrySet()) {
            pins.add(new Pin(row.getKey(), row.getValue()));
        }
        return pins;
    }

    /** How many pins hold for every world, which is what a pack ships with. */
    public static int sourced() {
        return SOURCE.size();
    }

    /** The keys a pack already pins, for a command that lists what is set without being asked. */
    public static List<String> sourceKeys() {
        return List.copyOf(SOURCE.keySet());
    }

    /** How a key stands, as a checklist reads it. */
    public enum State {
        /** Pinned in this world, by {@code /tmpin} or by a scout standing on the ground. */
        WORLD,
        /** Pinned in the build, so every world has it whether anybody walked one or not. */
        SOURCE,
        /** Not pinned, so the drawing decides where it goes. */
        NONE
    }

    /**
     * One piece of ground the pages ask for: the key a pin would be written under, and what it is.
     */
    public record Need(String key, String what) {
    }

    /**
     * The ground the campaign's pages ask for, in the order a chapter reaches it: every location either
     * chapter names, as a name to be announced or a building to be raised.
     * <p>
     * It is a checklist rather than a placement. Nothing here places anything, and a key without a pin
     * is surveyed from the drawing exactly as it always was, so this list cannot break a world that
     * ignores it - what it does is say how much of the map has actually been walked, which is otherwise
     * only knowable by reading two chapters of pages and remembering which ones needed ground.
     * <p>
     * The five quarries that are stood up ahead of the player rather than at a place - the Giant,
     * Ulmug's goblins, Dugorim, the barrow-wight and the Rider - are not here, because they are not
     * ground anybody chooses.
     */
    private static final List<Need> NEEDED = List.of(
            new Need("rivendell", "Rivendell - Elrond, Glorfindel and Legolas"),
            new Need("thorenhad", "Thorenhad - Elladan's camp"),
            new Need("barachen", "Barachen's fire"),
            new Need("ford", "the ford the beryl is carried to"),
            new Need("keystone", "the Key-stone's vault in the cave beside Gundabad"),
            new Need("carndum", "Carn Dum"),
            new Need(Sites.ELLADAN_CAMP, "Elladan's camp at Thorenhad"),
            new Need(Sites.WOOD_RUIN, "the wood ruin the black horse lies in"),
            new Need(Sites.TROLL_CAVE, "Bandrith's cave"),
            new Need(Sites.TROLL_CAVE_2, "Hoklond's cave"),
            new Need(Sites.TROLL_CAVE_3, "Sholdroth's cave"),
            new Need(Sites.TROLL_CAVE_4, "Matuna's lair"),
            new Need(Sites.GLOIN_CAMP, "Gloin's camp at the pass mouth"),
            new Need(GoblinCave.PIN, "the goblin fires of the Pinnath Fenui"),
            new Need(Sites.GUNDABAD, "Gundabad"),
            new Need(Sites.BOSS_CASTLE, "the Witch-king's castle"),
            new Need(Sites.BOSS_ARENA, "the arena beneath it"));

    /** The ground the pages ask for, in the order they reach it. */
    public static List<Need> needed() {
        return NEEDED;
    }

    /**
     * How a key stands: pinned in this world, pinned for every world, or left to the drawing. The same
     * order {@link #at} reads, so a checklist and a placement cannot disagree about what is pinned.
     */
    public static State state(@Nullable Level level, String key) {
        if (level != null) {
            instance(level);
            if (PINNED.containsKey(key)) return State.WORLD;
        }
        return SOURCE.containsKey(key) ? State.SOURCE : State.NONE;
    }

    // ---- plumbing ----

    private static Pins instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return NO_SERVER;
    }

    private static List<Pin> toList(Pins store) {
        List<Pin> pins = new ArrayList<>(PINNED.size());
        for (Map.Entry<String, BlockPos> row : PINNED.entrySet()) {
            pins.add(new Pin(row.getKey(), row.getValue()));
        }
        return pins;
    }

    private static Pins from(List<Pin> pins) {
        PINNED.clear();
        for (Pin pin : pins) {
            PINNED.put(pin.key(), pin.at());
        }
        // Read without clearing: the map was just filled from the file.
        return new Pins(false);
    }
}
