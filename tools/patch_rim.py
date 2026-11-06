"""One-shot: floor to the footprint edge, channel-only water guard, rim sweep."""
CR = "\r\n"
A = "src/main/java/com/greatorator/tolkienmobs/world/registration/ArdaMap.java"
C = "src/main/java/com/greatorator/tolkienmobs/faction/CityCommands.java"
D = "docs/rivendell-state.md"
L = "CHANGELOG.md"


def load(p):
    return open(p, "rb").read().decode("utf-8")


def cr(t):
    return t.replace("\n", CR)


def rep(s, old, new):
    assert s.count(old) == 1, (s.count(old), old[:90])
    return s.replace(old, new)


# ---------------- ArdaMap: the floor is the footprint ----------------
s = load(A)
s = rep(s, cr("""    private static final int VALLEY_HALF_X = 272;
    private static final int VALLEY_HALF_Z = 251;
"""), cr("""    private static final int VALLEY_HALF_X = 277;
    private static final int VALLEY_HALF_Z = 256;
"""))
s = rep(s, cr("""     * than the footprint on every side - 544 by 502 inside a 554 by 512 city - so the mountain's foot
     * begins inside the build and the build embeds itself in it. There is no apron of flat ground in
     * front of the walls for the join to be dressed across, because there is no join: the lowest band of
     * the city is laid into the rock, and the stone that was there shows wherever the build has nothing
     * of its own.
"""), cr("""     * is the footprint itself - 554 by 512 - so the rock's foot begins at the first block outside the
     * walls rather than four blocks inside them. A strip of crown left standing inside the build is a wall
     * of its own at every face the build leaves open, and that strip is what read as a four-block wall at
     * the river's mouth and again at the spring. There is no apron of flat ground in front of the walls
     * for the join to be dressed across, because there is no join: the lowest band of the city is laid on
     * the floor, and what the ground does outside the walls is the drape's to shape.
"""))
open(A, "wb").write(s.encode("utf-8"))

# ---------------- CityCommands ----------------
s = load(C)
s = rep(s, cr("""    private static final int DRAPE_REACH = 32;
"""), cr("""    private static final int DRAPE_REACH = 32;
    /**
     * How far in from the footprint's edge a column the build leaves empty is still the drape's: the strip
     * of crown the valley used to keep inside the walls, and the only room the pass has to take a wall
     * down on a world made before the floor reached the build's own edge.
     */
    private static final int DRAPE_RIM = 6;
"""))

s = rep(s, cr("""        boolean[] water = new boolean[wide * deep];
"""), cr("""        int[] watered = new int[wide * deep];
        Arrays.fill(watered, Integer.MIN_VALUE);
"""))
s = rep(s, cr("""                if (name.contains("water")) water[at] = true;
"""), cr("""                if (name.contains("water")) watered[at] = Math.max(watered[at], y);
"""))

s = rep(s, cr("""        // A wall with a doorway in it is still a wall:
"""), cr("""        boolean[] empty = new boolean[wide * deep];
        for (int i = 0; i < empty.length; i++) {
            empty[i] = outline[i] == Integer.MIN_VALUE;
        }
        // A wall with a doorway in it is still a wall:
"""))

s = rep(s, cr("""        // Every chunk the ring touches is loaded first:
"""), cr("""        // A column is a watercourse only where the water is what stands on top of it - a channel the river
        // can run in. Water buried in the build is not an outlet: the spring's aqueduct is under a roof at
        // 239, and reading it as one left a hole in the draping in front of the wall it runs in.
        boolean[] channel = new boolean[wide * deep];
        for (int i = 0; i < channel.length; i++) {
            channel[i] = outline[i] != Integer.MIN_VALUE && watered[i] == outline[i];
        }
        // Every chunk the ring touches is loaded first:
"""))

s = rep(s, cr("""                if (water[at]) {
"""), cr("""                if (channel[at]) {
"""))

s = rep(s, cr("""     * Water is left exactly as it is - the build's own carrying of it and the channel it runs in. Filling
     * up to a wall beside a river is what closes a river's mouth, and the mouth is the one thing the
     * valley was drawn round.
"""), cr("""     * A column whose top is water is left exactly as it is - the channel the river runs in. Filling up to
     * a wall beside a river is what closes a river's mouth, and the mouth is the one thing the valley was
     * drawn round.
"""))

s = rep(s, cr("""        return new Drape(cut, filled, open);
"""), cr("""        // The footprint's own rim: a column the build leaves empty this close to its outside edge is cut to
        // the columns beside it, so the ground inside the walls and the ground outside them are one surface.
        // Nothing is filled here - the city's own courtyards are inside the box - and nothing is cut where
        // the ground already stands at or under the wall.
        for (int x = box.lowX(); x <= box.highX(); x++) {
            for (int z = box.lowZ(); z <= box.highZ(); z++) {
                int gx = x - box.lowX();
                int gz = z - box.lowZ();
                int at = gx + gz * wide;
                if (!empty[at]) continue;
                if (Math.min(Math.min(gx, wide - 1 - gx), Math.min(gz, deep - 1 - gz)) > DRAPE_RIM) continue;
                int wall = outline[at];
                if (wall == Integer.MIN_VALUE) continue;
                int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
                if (top <= wall) continue;
                BlockState skin = level.getBlockState(new BlockPos(x, top, z));
                for (int y = top; y > wall; y--) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                }
                if (skin.isSolid()) {
                    level.setBlock(new BlockPos(x, wall, z), skin, 2);
                }
                cut++;
            }
        }
        return new Drape(cut, filled, open);
"""))
open(C, "wb").write(s.encode("utf-8"))

# ---------------- docs ----------------
s = load(D)
s = rep(s, cr("""- The rim **stands at the crown from the build's own edge outward.** The floor's rectangle is four blocks
  inside the build's outside edge (`VALLEY_HALF_X` 272 of 277, `VALLEY_HALF_Z` 251 of 256), so the rock
  meets the walls at 318 and holds that height out to `VALLEY_RIM = 96` blocks past them - a square, not a
  ring, with `RIDGE_SWING` read at nothing - then falls to the vale over `VALLEY_FALL = 200`. It used to
  climb from the floor over ninety blocks and hold the crown only out to 175, which is the bowl this was
  drawn to replace.
"""), cr("""- The rim **stands at the crown from the first block outside the walls.** The floor is the build's own
  footprint (`VALLEY_HALF_X` 277, `VALLEY_HALF_Z` 256), so the rock begins where the build ends, holds the
  crown out to `VALLEY_RIM = 96` blocks past it - a square, not a ring, with `RIDGE_SWING` read at nothing -
  and then falls to the vale over `VALLEY_FALL = 200`. It used to hold the crown only out to 175, and then
  to stop the floor four blocks inside the walls, which left a strip of crown standing inside the build
  that read as a wall of its own at every face the build leaves open - the river's mouth and the spring.
"""))
open(D, "wb").write(s.encode("utf-8"))

# ---------------- changelog ----------------
s = load(L)
i = s.index("### Fixed" + CR) + len("### Fixed" + CR)
j = i
while j < len(s) and s[j] in "\r\n":
    j += 1
bullet = ("- Reach the valley's floor to the build's own outside edge: it stopped four blocks short, which left a"+CR+
          "  strip of crown standing inside the footprint at 318 and read as a four-block wall at the river's"+CR+
          "  mouth and again at the spring. The drape also takes that strip down on a world made before this,"+CR+
          "  and treats only water that is a column's own top as a watercourse - water buried in the build,"+CR+
          "  like the spring's aqueduct at 239, was leaving a hole in the draping in front of the wall."+CR)
assert s[j:j+2] == "- "
open(L, "wb").write((s[:j] + bullet + s[j:]).encode("utf-8"))
print("patched: floor to the footprint edge, rim sweep, channel-only water")
