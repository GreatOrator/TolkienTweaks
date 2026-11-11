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
holds the pass. The chambers are caverns - a flat floor, walls that swell and pinch round the compass,
a ceiling that domes over, and an alcove or two off each - because a goblin hall is a room somebody
made and the caves behind it are not. Both are the same plan in `GoblinCave`, down to the sines.

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
HALL_FRONT = HALL_BACK + 16   # and where it ends
HALL_SIDE = 7       # half its width
HALL_HEIGHT = 10    # the hall's own headroom
LANDING_HEIGHT = 5  # the landing's
LANDING_SIDE = 3    # half the landing's width
CHAMBER_AT = 94     # the caverns' middle, down the way in
CHAMBER_SIDE = 24   # and how far out to either side
CHAMBER_RA = 15     # their reach along the way in
CHAMBER_RS = 12     # and across
CHAMBER_HEIGHT = 13
PASSAGE_BACK = CHAMBER_AT - 2   # the way out of the hall to each cavern
PASSAGE_FRONT = CHAMBER_AT + 2
PASSAGE_NEAR = HALL_SIDE + 1
PASSAGE_FAR = 14
PASSAGE_HEIGHT = 5
FAR = CHAMBER_AT + CHAMBER_RA + 6        # the plan's own far wall
FAR_SIDE = CHAMBER_SIDE + CHAMBER_RS + 6  # and how wide it reaches
OPEN = 4            # the way in's own blocks, cut through the surface
OUTSIDE = 1         # blocks cut in front of the door, so it is not walled in
DIRECTIONS = {"north": (0, -1), "south": (0, 1), "west": (-1, 0), "east": (1, 0)}


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
        if 0 <= a <= 2 and sideways <= 1 and self.door_floor <= y <= self.door_floor + 3:
            return True
        if 1 <= a <= RAMP and sideways <= 1:
            low = self.ramp_floor(a)
            if low <= y <= low + 3:
                return True
        if (RAMP - 2 <= a <= HALL_BACK and sideways <= LANDING_SIDE
                and self.floor <= y <= self.floor + LANDING_HEIGHT - 1):
            return True
        if (HALL_BACK <= a <= HALL_FRONT and sideways <= HALL_SIDE
                and self.floor <= y <= self.floor + HALL_HEIGHT - 1):
            return True
        if (PASSAGE_BACK <= a <= PASSAGE_FRONT and PASSAGE_NEAR <= sideways <= PASSAGE_FAR
                and self.floor <= y <= self.floor + PASSAGE_HEIGHT - 1):
            return True
        return self.cavern(a, s, y)

    def cavern(self, a, s, y):
        """A cavern: a flat floor, a wall that swells and pinches, a ceiling that domes over."""
        if not (self.floor <= y < self.floor + CHAMBER_HEIGHT):
            return False
        t = (y - self.floor) / float(CHAMBER_HEIGHT - 1)
        for sign in (-1, 1):
            centre_s = CHAMBER_SIDE * sign
            if self.blob(a, s, y, CHAMBER_AT, centre_s, CHAMBER_RA, CHAMBER_RS, t):
                return True
            # An alcove or two, so the room is not one swept shape.
            if self.blob(a, s, y, CHAMBER_AT + 8, centre_s + 7, 7, 6, t, height=9):
                return True
            if self.blob(a, s, y, CHAMBER_AT - 9, centre_s - 3, 6, 5, t, height=9):
                return True
        return False

    def blob(self, a, s, y, centre_a, centre_s, reach_a, reach_s, t, height=None):
        """One rounded lump of a cavern: radii pinched near the ceiling, swelling round the compass."""
        span = height or CHAMBER_HEIGHT
        if y >= self.floor + span:
            return False
        share = t if height is None else (y - self.floor) / float(span - 1)
        shrink = 1.0 if share <= 0.4 else 1.0 - 0.5 * ((share - 0.4) / 0.6)
        da, ds = a - centre_a, s - centre_s
        if height is not None:
            shrink = 1.0 if share <= 0.5 else 1.0 - 0.55 * ((share - 0.5) / 0.5)
        else:
            angle = math.atan2(ds, da)
            swell = 1.0 + 0.20 * math.sin(3.0 * angle + 0.6) + 0.11 * math.sin(5.0 * angle - 1.3)
            shrink *= swell
        ra, rs = reach_a * shrink, reach_s * shrink
        if ra <= 0 or rs <= 0:
            return False
        return (da * da) / (ra * ra) + (ds * ds) / (rs * rs) <= 1.0

    def cut(self):
        for a in range(-OUTSIDE, FAR):
            for s in range(-FAR_SIDE, FAR_SIDE + 1):
                x, z = self.at(a, s)
                top = height(self.patch, x, z)
                if top is None:
                    continue
                for y in range(self.floor, self.door_floor + 4):
                    if not self.void(a, s, y):
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
    print("         caverns %d blocks across and %d tall, either side of the hall"
          % (CHAMBER_SIDE * 2, CHAMBER_HEIGHT))
    print("         %s cells of cave, %d dropped where the hill was too shallow to roof them"
          % (format(len(plan.cells), ","), plan.dropped))
    far = plan.at(FAR, 0)
    print("         the hill is entered towards %s; the far wall is at %d, %d"
          % (direction, far[0], far[1]))

    low_y = plan.floor - 5
    high_y = here + 12
    section(plan, "down the hill, through the door (fixed s = 0):",
            0, (-4, FAR + 2), "a", (low_y, high_y))
    section(plan, "across the hall, at a = %d (fixed a):" % CHAMBER_AT,
            CHAMBER_AT, (-FAR_SIDE - 2, FAR_SIDE + 2), "s", (low_y, high_y))
    plan_view(plan, plan.floor + 1, (-4, FAR + 2), (-FAR_SIDE - 2, FAR_SIDE + 2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
