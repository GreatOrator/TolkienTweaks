#!/usr/bin/env python3
"""Cuts a goblin cave against a pinned door, and draws it before any of it is carved.

The door is a pin: somebody stood at it and `/tmpin place goblins` wrote their feet down. This reads
that pin out of the world, reads the ground around it block by block out of the same save, and then
plans the cave the way the generator will cut it - a mouth at the door, a ramp down into the hill, a
landing, a hall and two chambers off it - against the hillside that is actually there.

    python cave_section.py --world "run/saves/Serious Test" --pin goblins

Three things are drawn, each of them a slice through the plan:

    down the hill      a vertical section along the way in, s = 0: the sky, the ground, the mouth,
                       the ramp, the landing and the hall, so the grade and the headroom read.
    across the hall    the same section turned, through the middle of the hall and both chambers.
    plan               the cave from above at the hall's own height.

`#` is rock, `.` is cave air, `M` the doorway, `o` the ground's own surface, and a blank is sky.

The one rule the plan keeps is that the cave never opens where nobody cut a mouth: every cell is
dropped if it comes within ROOF of the ground above it, so the hall's ceiling is whichever is lower,
what the plan asks for or what the hill allows. The number of cells that rule took is reported, since
a hall that had to be flattened to a crawlway is a hall in the wrong place.

Nothing is carved, placed or written: this is the drawing the carve is argued from.
"""
import argparse
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import grab_build as gb
import world_height as wh

ARDA = "tolkienmobs:arda"
ROOF = 3          # blocks of rock kept between a cave and the sky above it
DROP = 40          # how far the way in falls, from the door's own ground
RAMP = DROP * 2    # one block down in two, so the way in runs twice as far as it falls
HALL_BACK = RAMP + 6          # where the hall starts, in blocks past the door
HALL_FRONT = HALL_BACK + 16   # and where it ends
HALL_SIDE = 7      # half its width
HALL_HEIGHT = 10   # the hall's own headroom
LANDING_HEIGHT = 5 # the landing's, and the passages' out to the chambers
LANDING_SIDE = 3   # half the landing's width
FLOOR = -DROP      # every floor stands this far under the door
CHAMBER_SIDE = (12, 20)
CHAMBER_BACK = 2
CHAMBER_FRONT = 11
CHAMBER_HEIGHT = 6
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

    def __init__(self, door, floor, direction, patch):
        self.door = (door[0], door[2])
        self.door_floor = floor
        self.floor = floor - DROP
        self.patch = patch
        self.dx, self.dz = DIRECTIONS[direction]
        self.sx, self.sz = -self.dz, self.dx
        self.cells = {}
        self.dropped = 0
        self.highest = {}
        self.cut()

    def at(self, a, s):
        return (self.door[0] + self.dx * a + self.sx * s, self.door[1] + self.dz * a + self.sz * s)

    def ramp_floor(self, a):
        """The way in falls from the door's own ground to the floor, over the ramp's length."""
        # Integer division, as GoblinCave.rampFloor does it, so the drawing and the cut agree exactly.
        return self.door_floor - (a - 1) * DROP // (RAMP - 1)

    def want(self, a, s):
        """The plan's own void, before the hill is consulted: (bottom, top) or None."""
        if 0 <= a <= 2 and abs(s) <= 1:
            return self.door_floor, self.door_floor + 3
        if 1 <= a <= RAMP and abs(s) <= 1:
            low = self.ramp_floor(a)
            return low, low + 3
        # The landing runs to the hall's own back wall rather than stopping short of it: a cave whose
        # rooms are each closed off by a block of rock is a cave with no way through.
        if RAMP - 2 <= a <= HALL_BACK and abs(s) <= LANDING_SIDE:
            return self.floor, self.floor + LANDING_HEIGHT - 1
        if HALL_BACK <= a <= HALL_FRONT and abs(s) <= HALL_SIDE:
            return self.floor, self.floor + HALL_HEIGHT - 1
        # A passage out to each side chamber, or the chambers are two sealed rooms beside a hall.
        if HALL_BACK + 4 <= a <= HALL_BACK + 6 and abs(s) <= CHAMBER_SIDE[1]:
            return self.floor, self.floor + LANDING_HEIGHT - 1
        if (HALL_BACK + CHAMBER_BACK <= a <= HALL_BACK + CHAMBER_FRONT
                and CHAMBER_SIDE[0] <= abs(s) <= CHAMBER_SIDE[1]):
            return self.floor, self.floor + CHAMBER_HEIGHT - 1
        return None

    def cut(self):
        for a in range(-2, HALL_FRONT + 8):
            for s in range(-CHAMBER_SIDE[1] - 2, CHAMBER_SIDE[1] + 3):
                want = self.want(a, s)
                if want is None:
                    continue
                x, z = self.at(a, s)
                top = height(self.patch, x, z)
                if top is None:
                    continue
                low, high = want
                # The way in is the one place the cave is meant to open: its first few blocks are cut
                # through the surface, which is what makes a mouth a mouth rather than a buried room.
                ceiling = high if a <= 4 else min(high, top - ROOF)
                if ceiling < low:
                    self.dropped += high - low + 1
                    continue
                if ceiling < high:
                    self.dropped += high - ceiling
                for y in range(low, ceiling + 1):
                    self.cells[(a, s, y)] = True

    def void(self, a, s, y):
        return (a, s, y) in self.cells

    def ceiling_at(self, a, s):
        return max((y for (ca, cs, y) in self.cells if ca == a and cs == s), default=None)


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
            elif plan.void(a, s, y) and a == 0:
                row.append("M")
            elif plan.void(a, s, y):
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
            elif plan.void(a, s, y):
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

    patch = ground(world, (door[0], door[2]), 48)
    here = height(patch, door[0], door[2])
    if here is None:
        raise SystemExit("no ground under the pin - are the chunks around it generated?")
    direction, rise = uphill(patch, (door[0], door[2]), here)
    print("%s: pinned %d, %d, %d (%s)" % (args.pin if not args.at else "door", door[0], door[1],
                                          door[2], source))
    print("ground at the door %d, the hill rises %.1f blocks towards %s"
          % (here, rise, direction))

    plan = Plan(door, here, direction, patch)
    floor = here + FLOOR
    ceiling = floor + HALL_HEIGHT - 1
    print("\nthe cut: mouth at the door on %d, landing at %d, hall floor %d to ceiling %d"
          % (here, floor, floor, ceiling))
    print("         the way in falls %d blocks over %d at the walk, one block down in two"
          % (DROP, RAMP))
    print("         %s cells of cave, %d dropped where the hill was too shallow to roof them"
          % (format(len(plan.cells), ","), plan.dropped))
    far = plan.at(HALL_FRONT, 0)
    print("         the hill is entered towards %s; the hall's far wall is at %d, %d"
          % (direction, far[0], far[1]))

    low_y = floor - 5
    high_y = max(here + 12, here + 1)
    section(plan, "down the hill, through the door (fixed s = 0):",
            0, (-4, HALL_FRONT + 6), "a", (low_y, high_y))
    section(plan, "across the hall, at a = %d (fixed a):" % ((HALL_BACK + HALL_FRONT) // 2),
            (HALL_BACK + HALL_FRONT) // 2, (-CHAMBER_SIDE[1] - 2, CHAMBER_SIDE[1] + 2),
            "s", (low_y, high_y))
    rows = [s for (a, s, y) in plan.cells if y == floor + 1] or [0]
    plan_view(plan, floor + 1, (-4, HALL_FRONT + 6), (min(rows) - 1, max(rows) + 1))
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
