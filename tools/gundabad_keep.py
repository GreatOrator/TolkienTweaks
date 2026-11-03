#!/usr/bin/env python3
"""Assemble the six dwarven tiles into the keep the chapter calls Gundabad, and the inner keep under it.

`fortress_build.py` raises the tiles one at a time, each 61 square and standing on its own floor, so
any tile meets any tile. This lays all six down at once, in the arrangement the geomorph sheet draws
them in - two panels across, three down - and writes the result as one structure template:

    approach    vault        the way in, and the treasury behind it
    hall        colonnade    the great hall, and the piers beyond it
    cistern     spiral       the water hall, and the way down

The spiral is the one tile that leaves its own floor: its hall floor is thirteen blocks up its own
height, because the stair cuts below it. So the other five are lifted thirteen blocks to meet it and
every room in the keep is walked on one level, with the stair descending from it.

The stair has somewhere to arrive. Two panels of set 4, the cave sheet, are cut into the inner keep by
`tools/cave_tile.py` and laid under the south row, twelve blocks below the hold's floor, which is exactly
where the spiral's landing is. They are the hold's one pixel-for-block build, and deliberately so: a cave
is rubble and outcrop, and a cave drawn in squared masonry is a ruin. The notes ask for a fortress inside
the fortress, which no drawing gives ground for; this is the ground for it, held to the same 61-square
quarter-point convention so it joins the hold above.

What sits under the five lifted rooms is filled with stone. A structure file cannot leave a cell alone -
a cell left out of it is written as air, and the ground there goes with it - so the box is written whole,
deepslate under the lifted rooms and each tile's own air above it.

    python gundabad_keep.py            write the keep and check it
"""
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import grab_build as gb

EDGE = 61                   # tile edge, as fortress_build.py writes them
TOP = 26                    # the spiral's own height, which is the tallest tile
FLOOR = 14                  # the one level the hold above is walked on
LIFT = FLOOR - 1            # a surface tile is written with its floor at y 0
LOWER = 1                   # the inner keep's floor: one room's height under the hold
LOWER_WALK = LOWER + 1      # where a player stands in it, which is where the stair lands
FILL = "minecraft:deepslate"    # the stone under the rooms that are lifted above their own ground

# The sheet, read the way it is drawn: two panels across, three down.
LAYOUT = (
    ("approach", 0, 0), ("vault", EDGE, 0),
    ("hall", 0, EDGE), ("colonnade", EDGE, EDGE),
    ("cistern", 0, 2 * EDGE), ("spiral", EDGE, 2 * EDGE),
)
# The inner keep: the undercroft with its cells, and the hall beside it, under the south row.
INNER = (
    ("ch2_inner_a", 0, 2 * EDGE),
    ("ch2_inner_b", EDGE, 2 * EDGE),
)
WIDE = 2 * EDGE
DEEP = 3 * EDGE
INNER_BOX = (0, 2 * EDGE, WIDE, DEEP)   # x0, z0, x1, z1

HERE = pathlib.Path(__file__).resolve().parent.parent
SRC = HERE / "src" / "main" / "resources" / "data" / "tolkienmobs" / "structure"
OUT = SRC / "key_structures" / "gundabad.nbt"
# The pack's blockstates live in both trees: hand-written ones in main, the generated ones in
# generated/resources, and a block may be in either.
BLOCKSTATES = (HERE / "src" / "main" / "resources" / "assets" / "tolkienmobs" / "blockstates",
               HERE / "src" / "generated" / "resources" / "assets" / "tolkienmobs" / "blockstates")

# ---- the keep ------------------------------------------------------------------------------

def assemble():
    """The inner keep, the hold above it, and the stair between them."""
    # The whole box is written, air and all. A structure file cannot say "leave this cell alone": a cell
    # left out of it is written as air, and the ground there goes with it. What keeps the hillside whole
    # is stone - the course under the five lifted rooms is filled before anything is laid on it - so the
    # hold carves its own rooms and leaves the ground beneath them standing.
    blocks = {}
    for x in range(WIDE):
        for z in range(DEEP):
            for y in range(FLOOR):
                blocks[(x, y, z)] = (FILL, ())
    # The inner keep is cut as the hold's own kind of file, so it is laid the hold's way: read whole,
    # air and all, and lifted to sit on the stair's floor.
    for name, dx, dz in INNER:
        path = SRC / ("%s.nbt" % name)
        if not path.exists():
            raise SystemExit("%s is missing: run cave_tile.py first" % path.name)
        tile, _ = gb.blocks_from_template(gb.read_nbt(path.read_bytes()))
        for (x, y, z), state in tile.items():
            blocks[(x + dx, y + LOWER, z + dz)] = state
    cx, cz, r = EDGE + 30, 2 * EDGE + 30, 4      # the stair's well, in the hold's own coords
    # The well is empty all the way down: nothing of the inner keep may stand in the stair.
    for x in range(cx - r, cx + r + 1):
        for z in range(cz - r, cz + r + 1):
            for y in range(LOWER_WALK, FLOOR):
                blocks.pop((x, y, z), None)
    # Then the hold, in the sheet's order, the stair tile last so its own blocks are the last word.
    for name, dx, dz in LAYOUT:
        path = SRC / ("ch2_%s.nbt" % name)
        if not path.exists():
            raise SystemExit("%s is missing: run fortress_build.py all first" % path.name)
        tile, _ = gb.blocks_from_template(gb.read_nbt(path.read_bytes()))
        lift = 0 if name == "spiral" else LIFT
        for (x, y, z), state in tile.items():
            blocks[(x + dx, y + lift, z + dz)] = state
    cut_stair_way(blocks, cx, cz, r)
    return blocks


def cut_stair_way(blocks, cx, cz, r):
    """Open the wall of the well, and cut west until the way out stands in a room and not in a wall."""
    west = cx - r - 1
    door = [(west, y, z) for y in range(LOWER_WALK, LOWER_WALK + 5) for z in range(cz - 2, cz + 3)]
    for cell in door:
        blocks.pop(cell, None)
    for x in range(west - 1, max(west - 40, 0), -1):
        cells = [(x, y, z) for y in range(LOWER_WALK, LOWER_WALK + 4) for z in range(cz - 2, cz + 3)]
        if not any(cell in blocks for cell in cells):
            return x
        for cell in cells:
            blocks.pop(cell, None)
    return None


def walk(blocks, level, x0, z0, x1, z1):
    """The floor cells one level up that can be walked to, and the pockets left out."""
    def open_(x, z):
        return (blocks.get((x, level, z), ("minecraft:air",))[0] == "minecraft:air"
                and blocks.get((x, level + 1, z), ("minecraft:air",))[0] == "minecraft:air")

    left = {(x, z) for x in range(x0, x1) for z in range(z0, z1) if open_(x, z)}
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
    return best, sorted(comps, reverse=True)


def known(name):
    """Whether the pack has this block, so a name that does not exist cannot be written into a build."""
    if not name.startswith("tolkienmobs:"):
        return True
    filename = name.split(":", 1)[1] + ".json"
    return any((root / filename).exists() for root in BLOCKSTATES)


def main():
    blocks = assemble()
    for name in sorted({state[0] for state in blocks.values()}):
        if not known(name):
            raise SystemExit("!! %s is not in the pack's blockstates" % name)
    if not gb.write_tile(blocks, (0, 0, 0), (WIDE, TOP, DEEP), OUT, keep_air=True):
        raise SystemExit("nothing to write")
    heights = [y for _, y, _ in blocks]
    if min(heights) < 0:
        raise SystemExit("!! %d blocks stand below the template's own base and would be dropped"
                         % sum(1 for y in heights if y < 0))
    print("%-44s %d blocks  %.0f KB  %d kinds" % (
        OUT.relative_to(HERE), len(blocks), OUT.stat().st_size / 1024,
        len({state[0] for state in blocks.values()})))
    print("  %d x %d x %d, blocks standing from y %d to y %d" % (
        WIDE, DEEP, TOP, min(heights), max(heights)))

    hold, hold_pockets = walk(blocks, FLOOR, 0, 0, WIDE, DEEP)
    print("  hold, floor y %d: %d cells on the walked route, %d pockets" % (FLOOR, len(hold), len(hold_pockets)))
    short = []
    for name, dx, dz in LAYOUT:
        inside = sum(1 for (x, z) in hold if dx <= x < dx + EDGE and dz <= z < dz + EDGE)
        print("   %-10s %5d of its own floor cells reached" % (name, inside))
        if inside < 200:
            short.append(name)

    x0, z0, x1, z1 = INNER_BOX
    inner, inner_pockets = walk(blocks, LOWER_WALK, x0, z0, x1, z1)
    cx, cz = EDGE + 30, 2 * EDGE + 30
    # The middle of the well is the stair's newel, so the landing is asked for beside it.
    landing = (cx - 3, cz) in inner
    print("  inner keep, floor y %d: %d cells walked, %d pockets, stair landing %s" % (
        LOWER_WALK, len(inner), len(inner_pockets), "on the route" if landing else "CUT OFF"))
    for name, dx, dz in INNER:
        inside = sum(1 for (x, z) in inner if dx <= x < dx + EDGE and dz <= z < dz + EDGE)
        print("   %-10s %5d of its own floor cells reached" % (name, inside))
        if inside < 200:
            short.append(name)

    if short:
        print("   !! no route into: %s" % ", ".join(short))
        return 1
    if not landing:
        print("   !! the stair does not reach the inner keep")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
