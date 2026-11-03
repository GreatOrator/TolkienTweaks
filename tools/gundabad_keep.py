#!/usr/bin/env python3
"""Assemble the six dwarven tiles into the keep the chapter calls Gundabad.

`fortress_build.py` raises the tiles one at a time, each 61 square and standing on its own floor, so
any tile meets any tile. This lays all six down at once, in the arrangement the geomorph sheet draws
them in - two panels across, three down - and writes the result as one structure template:

    approach    vault        the way in, and the treasury behind it
    hall        colonnade    the great hall, and the piers beyond it
    cistern     spiral       the water hall, and the way down

The spiral is the one tile that leaves its own floor: its hall floor is thirteen blocks up its own
height, because the stair cuts below it. So the other five are lifted thirteen blocks to meet it and
every room in the keep is walked on one level, with the stair descending from it.

What sits under the five lifted rooms is left out of the file rather than filled with rock. A template
writes the air it carries, so air under a room would be a room taken out of the hillside the keep
stands in; a cell that is simply not in the file is ground nobody touched. The keep therefore cuts its
own rooms out of the ground and leaves the ground under the rest alone.

    python gundabad_keep.py            write the keep and check it
"""
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import grab_build as gb

EDGE = 61                   # tile edge, as fortress_build.py writes them
TOP = 26                    # the spiral's own height, which is the tallest tile
FLOOR = 14                  # the one level every room in the keep is walked on
LIFT = FLOOR - 1            # a surface tile is written with its floor at y 0

# The sheet, read the way it is drawn: two panels across, three down.
LAYOUT = (
    ("approach", 0, 0), ("vault", EDGE, 0),
    ("hall", 0, EDGE), ("colonnade", EDGE, EDGE),
    ("cistern", 0, 2 * EDGE), ("spiral", EDGE, 2 * EDGE),
)
WIDE = 2 * EDGE
DEEP = 3 * EDGE

HERE = pathlib.Path(__file__).resolve().parent.parent
SRC = HERE / "src" / "main" / "resources" / "data" / "tolkienmobs" / "structure"
OUT = SRC / "key_structures" / "gundabad.nbt"


def assemble():
    """The six tiles on one floor, in the arrangement the sheet draws them in."""
    blocks = {}
    for name, dx, dz in LAYOUT:
        path = SRC / ("ch2_%s.nbt" % name)
        if not path.exists():
            raise SystemExit("%s is missing: run fortress_build.py all first" % path.name)
        tile, _ = gb.blocks_from_template(gb.read_nbt(path.read_bytes()))
        lift = 0 if name == "spiral" else LIFT
        for (x, y, z), state in tile.items():
            blocks[(x + dx, y + lift, z + dz)] = state
    return blocks


def check(blocks):
    """Every room reachable from every other through the shared doorways."""
    def open_(x, z):
        return (blocks.get((x, FLOOR, z), ("minecraft:air",))[0] == "minecraft:air"
                and blocks.get((x, FLOOR + 1, z), ("minecraft:air",))[0] == "minecraft:air")

    left = {(x, z) for x in range(WIDE) for z in range(DEEP) if open_(x, z)}
    comps = []
    best = set()
    while left:
        start = left.pop()
        comp = {start}
        stack = [start]
        while stack:
            x, z = stack.pop()
            for step in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                n = (x + step[0], z + step[1])
                if n in left:
                    left.discard(n)
                    comp.add(n)
                    stack.append(n)
        comps.append(len(comp))
        if len(comp) > len(best):
            best = comp
    # A tile's middle is whatever the drawing put there - a plinth, a well, a dais - so what is asked
    # of each tile is how much of its floor the walk reaches, not whether one particular cell is air.
    rooms = []
    for name, dx, dz in LAYOUT:
        inside = sum(1 for (x, z) in best if dx <= x < dx + EDGE and dz <= z < dz + EDGE)
        rooms.append((name, inside))
    return best, sorted(comps, reverse=True), rooms


def main():
    blocks = assemble()
    if not gb.write_tile(blocks, (0, 0, 0), (WIDE, TOP, DEEP), OUT, keep_air=False):
        raise SystemExit("nothing to write")
    heights = [y for _, y, _ in blocks]
    best, comps, rooms = check(blocks)
    print("%-44s %d blocks  %.0f KB  %d kinds" % (
        OUT.relative_to(HERE), len(blocks), OUT.stat().st_size / 1024,
        len({state[0] for state in blocks.values()})))
    print("  %d x %d x %d, blocks standing from y %d to y %d" % (
        WIDE, DEEP, TOP, min(heights), max(heights)))
    print("  floor at y %d: %d cells on the walked route, %d pockets in all"
          % (FLOOR, len(best), len(comps)))
    for name, inside in rooms:
        print("   %-10s %5d of its own floor cells reached" % (name, inside))
    strays = [n for n in comps[1:] if n > 100]
    if strays:
        print("   !! %d pieces of floor cut off: %s" % (len(strays), strays[:6]))
    short = [name for name, inside in rooms if inside < 200]
    if short:
        print("   !! no route into: %s" % ", ".join(short))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
