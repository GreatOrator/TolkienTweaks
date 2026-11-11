#!/usr/bin/env python3
"""Cuts a goblin cave against a pinned door, and draws it before any of it is carved.

The door is a pin: somebody stood at it and `/tmpin place goblins` wrote their feet down. This reads
that pin out of the world, reads the ground around it block by block out of the same save, and then
plans the cave the way the generator will cut it - a mouth at the door, a ramp down into the hill, a
landing, a squared hall and two caverns off it - against the hillside that is actually there.

    python cave_section.py --world "run/saves/Serious Test" --pin goblins

Three things are drawn, each of them a slice through the plan:

    down the hill      a vertical section along the way in, s = 0: the sky, the ground, the mouth,
                       the ramp, the landing and the hall, so the grade and the headroom read.
    across the hall    the same section turned, through the middle of the hall and both caverns.
    plan               the cave from above at the hall's own height.

`#` is rock, `.` is cave air, `M` the doorway, `o` the ground's own surface, and a blank is sky.

Two kinds of room, on purpose. The hall is squared: it is the worked part, cut and floored by whoever
holds the pass. The chambers are caverns - a wall that swells and pinches round the compass, a ceiling
that domes over, an alcove or two off each, and then rock left standing in it: rubble on the floor,
stalactites hanging and stalagmites standing wherever the cell hash puts them - because a goblin hall is
a room somebody made and the caves behind it are not. Both are the same plan in `GoblinCave`, down to
the sines and the hash.

The one rule the plan keeps is that the cave never opens where nobody cut a mouth: every cell is
dropped if it comes within ROOF of the ground above it. The number of cells that rule took is
reported, since a cavern that had to be flattened to a crawlway is a cavern in the wrong place.

Nothing is carved, placed or written: this is the drawing the carve is argued from.
"""
import argparse
import math
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import grab_build as gb
import world_height as wh

ARDA = "tolkienmobs:arda"
ROOF = 3            # blocks of rock kept between a cave and the sky above it
DROP = 40           # how far the way in falls, from the door's own ground
RAMP = DROP * 2     # one block down in two, so the way in runs twice as far as it falls
HALL_BACK = RAMP + 6          # where the hall starts, in blocks past the door
HALL_FRONT = HALL_BACK + 18   # and where it ends
HALL_SIDE = 8       # half its width
HALL_HEIGHT = 13    # the hall's own headroom
LANDING_HEIGHT = 5  # the landing's
LANDING_SIDE = 3    # half the landing's width
PASSAGE_NEAR = HALL_SIDE + 1     # the way out of the hall to each flank
PASSAGE_FAR = HALL_SIDE + 13
PASSAGE_HEIGHT = 7
FLANK_PASS_BACK = HALL_BACK + 10   # where that passage leaves the hall
FLANK_PASS_FRONT = HALL_BACK + 22  # and where it arrives
TUNNEL_BACK = 134   # where a flank tunnel leaves its own chamber
TUNNEL_FRONT = 184  # and where it is well into the back one
TUNNEL_INSIDE = 22  # and how far it stays off the hall's own line
CHAMBER_TALL = 20   # the tallest any lobe of a chamber gets
FAR = 220           # the plan's own far wall
FAR_SIDE = 60       # and how wide it reaches

# A chamber is several overlapping lobes - (a, s, reach a, reach s, height) - rather than one ellipse, so
# its outline has shoulders and recesses. FLANK is one side of the hall and is mirrored; BACK is the
# chamber behind the hall's far wall and is the larger of the three.
FLANK = (
    (112, 28, 20, 18, 16),
    (136, 36, 12, 11, 12),
    (96, 40, 11, 10, 11),
    (118, 46, 8, 8, 10),
)
BACK = (
    (172, 0, 26, 30, 20),
    (196, -18, 15, 16, 15),
    (200, 20, 14, 15, 14),
    (150, 13, 13, 13, 13),
)
# The way from each flank chamber into the back one: a string of small balls whose centres wander, so it
# is a tunnel rather than a bored corridor. Mirrored with the flank.
TUNNEL = (
    (140, -38, 4, 4, 6),
    (146, -36, 4, 4, 5),
    (151, -39, 3, 4, 5),
    (156, -35, 4, 5, 6),
    (161, -37, 3, 4, 5),
    (166, -33, 4, 4, 6),
    (171, -35, 3, 4, 5),
    (176, -28, 5, 5, 6),
)
OPEN = 4            # the way in's own blocks, cut through the surface
OUTSIDE = 1         # blocks cut in front of the door, so it is not walled in
MOUTH_SIDE = 2      # half the mouth's width, and the way in's
MOUTH_HEIGHT = 7    # and how tall both are
SPUR_TOP = 16       # of 256: how often a column of cavern keeps a stalactite
SPUR_LOW = 14       # and how often a stalagmite
SPUR_LONG = 5       # the longest of either, in blocks
COARSE = 0.16       # how far a wall wanders, as a share of its reach
FINE = 0.06         # and how much of that is per two blocks
DIRECTIONS = {"north": (0, -1), "south": (0, 1), "west": (-1, 0), "east": (1, 0)}
MASK = 0xFFFFFFFF


def noise(a, s, y):
    """A deterministic 0..255 from a cell, the same number `GoblinCave`'s hash gives."""
    h = ((a * 0x27D4EB2D) & MASK) ^ ((s * 0x165667B1) & MASK) ^ ((y * 0x9E3779B1) & MASK)
    h ^= h >> 15
    h = (h * 0x85EBCA6B) & MASK
    h ^= h >> 13
    h = (h * 0xC2B2AE35) & MASK
    h ^= h >> 16
    return h & 0xFF


def pin_at(world, key):
    """The block a pin holds, out of the world's own data files."""
    for path in sorted(world.rglob("*.dat")):
        if "data" not in path.parts:
            continue
        try:
            root = gb.read_nbt(path.read_bytes())
        except Exception:
            continue
        for found, at in pairs(root):
            if found == key:
                return at, path
    return None, None


def pairs(node):
    """Every {key, at} pair anywhere in a tag."""
    out = []
    if isinstance(node, dict):
        if "key" in node and "at" in node:
            try:
                out.append((str(node["key"]), tuple(int(v) for v in node["at"])))
            except (TypeError, ValueError):
                pass
        else:
            for child in node.values():
                out.extend(pairs(child))
    elif isinstance(node, list):
        for child in node:
            out.extend(pairs(child))
    return out


def ground(world, centre, radius):
    """The top solid block of every column in a square, keyed by (x, z)."""
    cx, cz = centre
    columns = [(cx + dx, cz + dz) for dx in range(-radius, radius + 1)
               for dz in range(-radius, radius + 1)]
    top, _ = wh.tops(world, ARDA, columns)
    return {column: top[column][0] for column in columns if column in top}


def height(patch, x, z):
    return patch.get((x, z))


class Plan:
    """The cave as a set of cells, in door-relative coordinates: `a` into the hill, `s` across."""

    def __init__(self, door, door_floor, direction, patch):
        self.door = (door[0], door[2])
        self.door_floor = door_floor
        self.floor = door_floor - DROP
        self.patch = patch
        self.dx, self.dz = DIRECTIONS[direction]
        self.sx, self.sz = -self.dz, self.dx
        self.cells = {}
        self._spurs = {}
        self.dropped = 0
        self.cut()

    def at(self, a, s):
        return (self.door[0] + self.dx * a + self.sx * s, self.door[1] + self.dz * a + self.sz * s)

    def ramp_floor(self, a):
        """The way in falls from the door's own ground to the floor, one block down in two."""
        return self.floor + DROP - (a - 1) * DROP // (RAMP - 1)

    def void(self, a, s, y):
        """Whether the plan puts cave air at one cell, before the hill is consulted."""
        sideways = abs(s)
        if (0 <= a <= 2 and sideways <= MOUTH_SIDE
                and self.door_floor <= y <= self.door_floor + MOUTH_HEIGHT - 1):
            return True
        if 1 <= a <= RAMP and sideways <= MOUTH_SIDE:
            low = self.ramp_floor(a)
            if low <= y <= low + MOUTH_HEIGHT - 1:
                return True
        if (RAMP - 2 <= a <= HALL_BACK and sideways <= LANDING_SIDE
                and self.floor <= y <= self.floor + LANDING_HEIGHT - 1):
            return True
        if (HALL_BACK <= a <= HALL_FRONT and sideways <= HALL_SIDE
                and self.floor <= y <= self.floor + HALL_HEIGHT - 1):
            return True
        # The way out of the hall to each flank chamber.
        if (FLANK_PASS_BACK <= a <= FLANK_PASS_FRONT and PASSAGE_NEAR <= sideways <= PASSAGE_FAR
                and self.floor <= y <= self.floor + PASSAGE_HEIGHT - 1):
            return True
        return self.cavern(a, s, y)

    def cavern(self, a, s, y):
        """A chamber is several overlapping lobes, so no room is one ellipse with a dome on it."""
        if not (self.floor <= y < self.floor + CHAMBER_TALL):
            return False
        # Rubble on the floor, so a cavern's bottom is not a poured slab.
        if y == self.floor and noise(a >> 1, s >> 1, 505) < 40:
            return False
        for centre_a, centre_s, ra, rs, height in FLANK:
            if self.blob(a, s, y, centre_a, centre_s, ra, rs, height):
                return True
            if self.blob(a, s, y, centre_a, -centre_s, ra, rs, height):
                return True
        for centre_a, centre_s, ra, rs, height in BACK:
            if self.blob(a, s, y, centre_a, centre_s, ra, rs, height):
                return True
        # The flank tunnels are only in one stretch of the plan, so the rest of it never looks at them.
        if TUNNEL_BACK <= a <= TUNNEL_FRONT and abs(s) >= TUNNEL_INSIDE:
            for centre_a, centre_s, ra, rs, height in TUNNEL:
                if self.blob(a, s, y, centre_a, centre_s, ra, rs, height):
                    return True
                if self.blob(a, s, y, centre_a, -centre_s, ra, rs, height):
                    return True
        return False

    def blob(self, a, s, y, centre_a, centre_s, reach_a, reach_s, height):
        """One lobe: radii pinched near its ceiling, swelling by three sines, worn off true by the hash."""
        if not (self.floor <= y < self.floor + height):
            return False
        # Nothing this far outside a lobe can be inside it however far its wall wanders.
        if abs(a - centre_a) > reach_a * 1.8 + 1 or abs(s - centre_s) > reach_s * 1.8 + 1:
            return False
        share = (y - self.floor) / float(height - 1)
        shrink = 1.0 if share <= 0.4 else 1.0 - 0.5 * ((share - 0.4) / 0.6)
        da, ds = a - centre_a, s - centre_s
        angle = math.atan2(ds, da)
        shrink *= (1.0
                   + 0.26 * math.sin(2.0 * angle + 2.1)
                   + 0.17 * math.sin(3.0 * angle + 0.6)
                   + 0.09 * math.sin(5.0 * angle - 1.3))
        ra, rs = reach_a * shrink, reach_s * shrink
        if ra <= 0 or rs <= 0:
            return False
        q = (da * da) / (ra * ra) + (ds * ds) / (rs * rs)
        # The wall wanders: one wobble over four blocks and a finer one over two.
        wander = ((noise(a >> 2, s >> 2, y >> 2) - 128) / 128.0) * COARSE \
            + ((noise(a >> 1, s >> 1, y >> 1) - 128) / 128.0) * FINE
        return q <= 1.0 + wander

    def spur(self, a, s):
        """What one column keeps: how far a stalactite hangs, how far a stalagmite stands."""
        key = (a, s)
        if key not in self._spurs:
            hang = 1 + noise(a >> 1, s >> 1, 202) % SPUR_LONG \
                if noise(a >> 1, s >> 1, 101) < SPUR_TOP else 0
            stand = 1 + noise(a >> 1, s >> 1, 404) % SPUR_LONG \
                if noise(a >> 1, s >> 1, 303) < SPUR_LOW else 0
            found = None
            if hang or stand:
                low = self.floor
                while low < self.floor + CHAMBER_TALL and not self.cavern(a, s, low):
                    low += 1
                high = low
                while high < self.floor + CHAMBER_TALL and self.cavern(a, s, high):
                    high += 1
                if high > low:
                    found = (low, high, hang, stand)
            self._spurs[key] = found
        return self._spurs[key]

    def standing(self, a, s, y, spur):
        """Whether rock is left standing at a cell, rather than taken out of it."""
        if not spur or not self.cavern(a, s, y):
            return False
        low, high, hang, stand = spur
        return bool((hang and y >= high - hang) or (stand and y < low + stand))

    def cut(self):
        for a in range(-OUTSIDE, FAR + 1):
            for s in range(-FAR_SIDE, FAR_SIDE + 1):
                x, z = self.at(a, s)
                top = height(self.patch, x, z)
                if top is None:
                    continue
                spur = self.spur(a, s)
                for y in range(self.floor, self.door_floor + MOUTH_HEIGHT):
                    if not self.void(a, s, y):
                        continue
                    if self.standing(a, s, y, spur):
                        continue
                    # The mouth's own few blocks are the one place the cave is meant to open.
                    if a > OPEN and y > top - ROOF:
                        self.dropped += 1
                        continue
                    self.cells[(a, s, y)] = True


def uphill(patch, door, floor):
    """The way into the hill: the compass direction whose ground rises most over the next stretch."""
    best, name = None, None
    for candidate, (dx, dz) in DIRECTIONS.items():
        rises = []
        for a in range(6, 26, 4):
            for s in range(-4, 5, 4):
                x = door[0] + dx * a - dz * s
                z = door[1] + dz * a + dx * s
                top = height(patch, x, z)
                if top is not None:
                    rises.append(top - floor)
        if not rises:
            continue
        mean = sum(rises) / len(rises)
        if best is None or mean > best:
            best, name = mean, candidate
    return name, (best or 0.0)


def section(plan, title, fixed, span, along, ys):
    """One vertical slice: `along` is the axis that runs across the page, `fixed` the other.

    A cave deeper or longer than the page is drawn coarsely rather than off its edge: the slice takes
    every second or third block when it has to, and says what it took.
    """
    across = max(1, (span[1] - span[0] + 1) // 56)
    down = max(1, (ys[1] - ys[0] + 1) // 26)
    print("\n%s  (every %d block%s across, %d down)"
          % (title, across, "" if across == 1 else "s", down))
    for y in range(ys[1], ys[0] - 1, -down):
        row = []
        for step in range(span[0], span[1] + 1, across):
            a, s = (step, fixed) if along == "a" else (fixed, step)
            x, z = plan.at(a, s)
            top = height(plan.patch, x, z)
            if top is None:
                row.append("?")
            elif y > top:
                row.append(" ")
            elif (a, s, y) in plan.cells and a == 0:
                row.append("M")
            elif (a, s, y) in plan.cells:
                row.append(".")
            elif y == top:
                row.append("o")
            else:
                row.append("#")
        print("%5d %s" % (y, "".join(row)))
    label = "a into the hill" if along == "a" else "s across the hill"
    print("      %s %d..%d" % (label, span[0], span[1]))


def plan_view(plan, y, span_a, span_s):
    across = max(1, (span_a[1] - span_a[0] + 1) // 56)
    rows = max(1, (span_s[1] - span_s[0] + 1) // 30)
    print("\nthe plan, at y %d  (every %d block%s in, %d across):"
          % (y, across, "" if across == 1 else "s", rows))
    for s in range(span_s[0], span_s[1] + 1, rows):
        row = []
        for a in range(span_a[0], span_a[1] + 1, across):
            x, z = plan.at(a, s)
            top = height(plan.patch, x, z)
            if top is None:
                row.append("?")
            elif (a, s, y) in plan.cells:
                row.append("M" if a == 0 else ".")
            elif y <= top:
                row.append("#")
            else:
                row.append(" ")
        print("%5d %s" % (s, "".join(row)))
    print("      a into the hill %d..%d, s across %d..%d" % (span_a[0], span_a[1],
                                                            span_s[0], span_s[1]))


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--world", required=True, help="saved world folder holding level.dat")
    parser.add_argument("--pin", default="goblins", help="the pin to cut the door at (default goblins)")
    parser.add_argument("--at", default=None, metavar="X,Z", help="cut at a block instead of a pin")
    args = parser.parse_args(argv[1:])

    world = pathlib.Path(args.world)
    if args.at:
        x, z = (int(v) for v in args.at.split(","))
        door = (x, 0, z)
        source = "the block given"
    else:
        door, where = pin_at(world, args.pin)
        if door is None:
            raise SystemExit("no pin called %r in %s - is the world saved since it was pinned?"
                             % (args.pin, world))
        source = str(where.relative_to(world))

    # The hillside read has to reach past the far wall, or the rooms beyond the radius are skipped as
    # ground nobody has looked at: the cave is as long as its own plan says, not as long as a guess.
    patch = ground(world, (door[0], door[2]), FAR + 4)
    here = height(patch, door[0], door[2])
    if here is None:
        raise SystemExit("no ground under the pin - are the chunks around it generated?")
    direction, rise = uphill(patch, (door[0], door[2]), here)
    print("%s: pinned %d, %d, %d (%s)" % (args.pin if not args.at else "door", door[0], door[1],
                                          door[2], source))
    print("ground at the door %d, the hill rises %.1f blocks towards %s"
          % (here, rise, direction))

    plan = Plan(door, here, direction, patch)
    print("\nthe cut: mouth at the door on %d, landing at %d, hall floor %d to ceiling %d"
          % (here, plan.floor, plan.floor, plan.floor + HALL_HEIGHT - 1))
    print("         the way in falls %d blocks over %d at the walk, one block down in two"
          % (DROP, RAMP))
    print("         two flank chambers and a larger one behind the hall, built from overlapping lobes")
    print("         %s cells of cave, %d dropped where the hill was too shallow to roof them"
          % (format(len(plan.cells), ","), plan.dropped))
    far = plan.at(FAR, 0)
    print("         the hill is entered towards %s; the far wall is at %d, %d"
          % (direction, far[0], far[1]))

    low_y = plan.floor - 5
    high_y = here + 12
    section(plan, "down the hill, through the door (fixed s = 0):",
            0, (-4, FAR + 2), "a", (low_y, high_y))
    section(plan, "across the hall, at a = %d (fixed a):" % HALL_BACK,
            HALL_BACK, (-FAR_SIDE - 2, FAR_SIDE + 2), "s", (low_y, high_y))
    plan_view(plan, plan.floor + 1, (-4, FAR + 2), (-FAR_SIDE - 2, FAR_SIDE + 2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
