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

The stair has somewhere to arrive. Two of the example schematics collected beside the drawings - the
barred undercroft and the hall next to it - are re-cut in the hold's own stone and laid under the south
row, twelve blocks below the hold's floor, which is exactly where the spiral's landing is. They are not
the geomorphs and make no claim to be: the drawings give the hold, and the notes ask for a fortress
inside a fortress, which no drawing gives ground for. These are the ground for it, held to the same
61-square quarter-point convention so they join the hold above.

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
FLOOR = 14                  # the one level the hold above is walked on
LIFT = FLOOR - 1            # a surface tile is written with its floor at y 0
LOWER = 1                   # the inner keep's floor: one room's height under the hold
LOWER_WALK = LOWER + 1      # where a player stands in it, which is where the stair lands
LIGHT = "tolkienmobs:block_camo_glowstone"   # a light that wears the look of the stone around it
LIGHT_STEP = 15                             # one set into the floor about every fifteen blocks
LIT = {"minecraft:lantern", "minecraft:chain"}   # what the sheets hang, and what the hold does without
FILL = "minecraft:deepslate"    # the stone under the rooms that are lifted above their own ground

# The sheet, read the way it is drawn: two panels across, three down.
LAYOUT = (
    ("approach", 0, 0), ("vault", EDGE, 0),
    ("hall", 0, EDGE), ("colonnade", EDGE, EDGE),
    ("cistern", 0, 2 * EDGE), ("spiral", EDGE, 2 * EDGE),
)
# The inner keep: the undercroft with its cells, and the hall beside it, under the south row.
INNER = (
    ("2tl", 0, 0), ("2tr", EDGE, 0),
    ("2ml", 0, EDGE), ("2mr", EDGE, EDGE),
    ("2bl", 0, 2 * EDGE), ("2br", EDGE, 2 * EDGE),
)
WIDE = 2 * EDGE
DEEP = 3 * EDGE
INNER_BOX = (0, 0, WIDE, DEEP)          # x0, z0, x1, z1 - the whole footprint, all six pieces

HERE = pathlib.Path(__file__).resolve().parent.parent
SRC = HERE / "src" / "main" / "resources" / "data" / "tolkienmobs" / "structure"
OUT = SRC / "key_structures" / "gundabad.nbt"
SCHEMATICS = HERE / "Templates" / "schematics"
# The pack's blockstates live in both trees: hand-written ones in main, the generated ones in
# generated/resources, and a block may be in either.
BLOCKSTATES = (HERE / "src" / "main" / "resources" / "assets" / "tolkienmobs" / "blockstates",
               HERE / "src" / "generated" / "resources" / "assets" / "tolkienmobs" / "blockstates")

# ---- the example structures: 61 x 12 x 61 slices of the valley the rooms were cut in ----------

STRUCTS = HERE / "Templates" / "nbtstructures"

# What each of their blocks becomes in this hold. What is taken is the shape - the rooms and the stone
# that walls them - and the material is the hold's own, which is also what stops the inner keep reading
# as somebody else's ruin inside a dwarven fortress. Their soil and their rock become the hold's rock;
# their doors are dropped, because a door is two blocks with a hinge and what the shape needs is the
# opening.
KEEP_OUT = {
    "minecraft:stone_bricks": "tolkienmobs:angainor_bricks",
    "minecraft:stone_brick_slab": "tolkienmobs:slab_angainor_bricks",
    "minecraft:stone_brick_stairs": "tolkienmobs:stairs_angainor_bricks",
}
DROP = {"minecraft:dirt": FILL, "minecraft:stone": FILL, "minecraft:oak_door": "minecraft:air"}


def carry(state):
    """One of their blocks as one of the hold's."""
    name, props = state
    if name in DROP:
        return (DROP[name], ())
    return (KEEP_OUT.get(name, name), props)


# ---- the keep ------------------------------------------------------------------------------

ROOM = 6                    # the hollow kept above the cave's floor; the hold's floor is its roof


def inner_tile(name):
    """One of the example structures laid whole, with the three floors it carries.

    Each file is a 61 by 12 by 61 cut of the rooms as they were built: a course at its base, a gallery
    over that, two floors over that, and their own stairs and ladder between them. Twelve courses is
    exactly the storey the hold leaves empty under its own floor, so the file is laid as it stands from
    the stair's floor up - its stone re-materialled into the hold's, its soil and rock read as the
    hold's rock, its doors dropped for the openings they leave.
    """
    path = STRUCTS / (name + ".nbt")
    if not path.exists():
        raise SystemExit("%s is missing" % path)
    blocks, (wide, high, deep) = gb.blocks_from_template(gb.read_nbt(path.read_bytes()))

    out = {}
    for x in range(wide):
        for z in range(deep):
            for y in range(FLOOR):
                out[(x, y, z)] = (FILL, ())
            for y in range(min(high, FLOOR - LOWER)):
                state = blocks.get((x, y, z))
                out[(x, LOWER + y, z)] = carry(state) if state else ("minecraft:air", ())
    return out


def join_inner(blocks, cx, cz):
    """Drive a passage between every chamber the two files leave cut off from the stair.

    Both files carry the valley's own mouths, so a cave can end in a wall a block from the next one. A
    party must not be handed an inner keep it cannot walk, so the chambers are joined the way the
    hold's own tiles are joined: a passage three wide and four high, standing on the stair's floor.
    """
    x0, z0, x1, z1 = INNER_BOX

    def open_(x, z):
        return (blocks.get((x, LOWER_WALK, z), ("minecraft:air",))[0] == "minecraft:air"
                and blocks.get((x, LOWER_WALK + 1, z), ("minecraft:air",))[0] == "minecraft:air")

    def carve(x, z):
        for y in range(LOWER_WALK, LOWER_WALK + 4):
            blocks.pop((x, y, z), None)
        blocks[(x, LOWER, z)] = (FILL, ())

    # The files carry the valley's mouths too, so a cave can break a tile's edge and open the inner
    # keep to the sky. The hold's stone goes back along the four edges of the box.
    for y in range(LOWER, FLOOR):
        for x in range(x0, x1):
            blocks[(x, y, z0)] = (FILL, ())
            blocks[(x, y, z1 - 1)] = (FILL, ())
        for z in range(z0, z1):
            blocks[(x0, y, z)] = (FILL, ())
            blocks[(x1 - 1, y, z)] = (FILL, ())

    for _ in range(12):
        left = {(x, z) for x in range(x0, x1) for z in range(z0, z1) if open_(x, z)}
        comps = []
        while left:
            start = left.pop(); comp = {start}; stack = [start]
            while stack:
                x, z = stack.pop()
                for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    n = (x + dx, z + dz)
                    if n in left:
                        left.discard(n); comp.add(n); stack.append(n)
            comps.append(comp)
        comps.sort(key=len, reverse=True)
        if len(comps) <= 1:
            return
        main = comps[0]
        for comp in comps[1:]:
            ax, az = min(comp)
            bx, bz = min(main, key=lambda c: abs(c[0] - ax) + abs(c[1] - az))
            for x in range(min(ax, bx), max(ax, bx) + 1):
                for dz in (-1, 0, 1):
                    carve(x, az + dz)
            for z in range(min(az, bz), max(az, bz) + 1):
                for dx in (-1, 0, 1):
                    carve(bx + dx, z)
            main |= comp


# ---- the light ------------------------------------------------------------------------------

NOT_FLOOR = ("stairs", "slab", "ladder", "bars", "fence", "wall", "door", "trapdoor",
             "lantern", "chain", "water", "lava", "glowstone")


OPEN = {"minecraft:air", "minecraft:water"}


def clear(state):
    """Whether a player can be in this cell: air, or the water of a flooded room they can swim."""
    return state[0] in OPEN


def plain(state):
    """Whether a block can stand in as a floor: whole, and not part of a way up or a light."""
    return not any(state[0].endswith(suffix) for suffix in NOT_FLOOR)


def floor_beside(blocks, x0, z0, level, reach):
    """The nearest cell to (x0, z0) that is plain floor with two clear courses over it."""
    for ring in range(reach + 1):
        for dx in range(-ring, ring + 1):
            for dz in range(-ring, ring + 1):
                if max(abs(dx), abs(dz)) != ring:
                    continue
                x, z = x0 + dx, z0 + dz
                if not (0 <= x < WIDE and 0 <= z < DEEP):
                    continue
                under = blocks.get((x, level - 1, z))
                if not under or not plain(under):
                    continue
                if not clear(blocks.get((x, level, z), ("minecraft:air", ()))):
                    continue
                if not clear(blocks.get((x, level + 1, z), ("minecraft:air", ()))):
                    continue
                return (x, level - 1, z)
    return None


def light_rooms(blocks):
    """One camo glowstone in the floor every fifteen blocks, in place of the drawings' lanterns.

    A camo glowstone is a full block of light fifteen that wears the look of the stone it stands in, so
    set into a floor it is invisible and the room above it is lit: no lamp to hang, nothing to see, and
    nothing standing in a corner for a player to knock away in the dark. The sheets' own lanterns and
    the chains they hang from come out - that is the light the drawings carry, and this is the light the
    hold is built with.
    """
    for pos in [pos for pos, state in blocks.items() if state[0] in LIT]:
        blocks.pop(pos, None)
    lit = []
    for level in (FLOOR, LOWER_WALK):
        for x in range(LIGHT_STEP // 2, WIDE, LIGHT_STEP):
            for z in range(LIGHT_STEP // 2, DEEP, LIGHT_STEP):
                spot = floor_beside(blocks, x, z, level, LIGHT_STEP // 2)
                if spot and spot not in lit:
                    blocks[spot] = (LIGHT, ())
                    lit.append(spot)
    return lit


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
    # Two buried rooms, taken out of the files as the hollows they are, with the hold's stone
    # standing where the files carry hillside.
    for name, dx, dz in INNER:
        for (x, y, z), state in inner_tile(name).items():
            blocks[(x + dx, y, z + dz)] = state
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
            # The spiral tile stands on the hold's own floor, so it reaches all the way down; below that
            # floor its own rock and air would bury the inner keep it is standing on, so down there it
            # writes only the stair's well and leaves the rooms under it alone.
            if name == "spiral" and y < LIFT and (abs(x - 30) > 6 or abs(z - 30) > 6):
                continue
            blocks[(x + dx, y + lift, z + dz)] = state
    cut_stair_way(blocks, cx, cz, r)
    join_inner(blocks, cx, cz)
    light_rooms(blocks)
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
        return (clear(blocks.get((x, level, z), ("minecraft:air",)))
                and clear(blocks.get((x, level + 1, z), ("minecraft:air",))))

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

    print("  %d camo glowstone set into the floors" % sum(1 for state in blocks.values() if state[0] == LIGHT))

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
