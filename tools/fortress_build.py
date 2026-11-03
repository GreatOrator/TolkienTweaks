"""Build the chapter-two dwarven fortress tiles as structure NBT.

The geomorph drawings (tools/geomorphs) supply the layout and the features;
this turns them into squared dwarven masonry. Every tile is 61x61 blocks with
open doorways at blocks 13-17 and 43-47 on all four faces, which is the
convention the pack's example schematics use, so any tile meets any tile.

Usage: python fortress_build.py [name]
Writes ../src/main/resources/data/tolkienmobs/structure/ch2_<name>.nbt
"""
import pathlib
import random
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import grab_build as gb

W = 61            # tile edge, blocks
H = 13            # piece height: floor at y=0, tops out at y=12
CEIL_HALL = 12
CEIL_GAL = 9
DOORS = ((13, 17), (43, 47))

ROCK = "minecraft:deepslate"
FLOOR = "tolkienmobs:angainor"
FLOOR_ALT = "tolkienmobs:angainor_bricks"
WALL = "tolkienmobs:angainor_bricks"
CHISEL = "tolkienmobs:chiseled_angainor_bricks"
SMOOTH = "tolkienmobs:smooth_angainor"
SLAB = "tolkienmobs:slab_angainor_bricks"
STAIRS = "tolkienmobs:stairs_angainor_bricks"
GOLD = "minecraft:gold_block"
LAMP = "minecraft:lantern"
CHAIN = "minecraft:chain"
GRAVEL = "minecraft:gravel"
COBBLE = "minecraft:cobblestone"
CRACKED = "tolkienmobs:cracked_angainor_bricks"
COBBLED = "tolkienmobs:cobbled_angainor"
MOSSY = "tolkienmobs:mossy_cobbled_angainor"
BAR = "minecraft:iron_bars"


class Plan:
    def __init__(self, seed=0):
        self.b = {}
        self.rnd = random.Random(seed)

    def put(self, x, y, z, name, props=()):
        if 0 <= x < W and 0 <= y < H and 0 <= z < W:
            self.b[(x, y, z)] = (name, tuple(props))

    def air(self, x, y, z):
        if 0 <= x < W and 0 <= y < H and 0 <= z < W:
            self.b[(x, y, z)] = ("minecraft:air", ())

    def fill(self, x0, y0, z0, x1, y1, z1, name, props=()):
        for x in range(min(x0, x1), max(x0, x1) + 1):
            for y in range(min(y0, y1), max(y0, y1) + 1):
                for z in range(min(z0, z1), max(z0, z1) + 1):
                    self.put(x, y, z, name, props)

    def clear(self, x0, y0, z0, x1, y1, z1):
        for x in range(min(x0, x1), max(x0, x1) + 1):
            for y in range(min(y0, y1), max(y0, y1) + 1):
                for z in range(min(z0, z1), max(z0, z1) + 1):
                    self.air(x, y, z)

    def stairs(self, x, y, z, facing, name=STAIRS):
        self.put(x, y, z, name, (("facing", facing), ("half", "bottom"), ("shape", "straight")))

    def slab(self, x, y, z, name=SLAB, top=False):
        self.put(x, y, z, name, (("type", "top" if top else "bottom"),))

    def open_at(self, y=1):
        """Set of (x,z) with a walkable floor at y."""
        return {(x, z) for (x, yy, z), (n, _) in self.b.items() if yy == y and n == "minecraft:air"}


# ---- pieces -----------------------------------------------------------------

def floor_slab(p):
    """Floor over the whole footprint so tiles join without seams."""
    for x in range(W):
        for z in range(W):
            edge = x < 3 or x > W - 4 or z < 3 or z > W - 4
            name = FLOOR_ALT if edge else FLOOR
            p.put(x, 0, z, name)


def outer_wall(p):
    """Three blocks thick, rising the full height, with chiseled courses."""
    for x in range(W):
        for z in range(W):
            if not (x < 3 or x > W - 4 or z < 3 or z > W - 4):
                continue
            for y in range(H):
                name = WALL
                if y == 8 or (y in (1, 11) and (x + z) % 8 == 0):
                    name = CHISEL
                elif (x * 7 + z * 13) % 41 == 0:
                    name = CRACKED
                p.put(x, y, z, name)


def gallery_and_partition(p):
    """Five-wide ring gallery, and the wall between it and the hall."""
    # partition frame (3 thick) between gallery and hall
    p.fill(8, 0, 8, 10, CEIL_HALL - 1, W - 9, WALL)
    p.fill(8, 0, 8, W - 9, CEIL_HALL - 1, 10, WALL)
    p.fill(W - 11, 0, 8, W - 9, CEIL_HALL - 1, W - 9, WALL)
    p.fill(8, 0, W - 11, W - 9, CEIL_HALL - 1, W - 9, WALL)
    # pilaster courses on the hall face
    for i in range(9, W - 8, 10):
        if 26 <= i <= 34:
            continue          # keep the archway mouths clear
        p.put(11, 1, i, CHISEL)
        p.put(11, 10, i, CHISEL)
        p.put(i, 1, 11, CHISEL)
        p.put(i, 10, 11, CHISEL)
    # carve the gallery ring
    p.clear(3, 1, 3, W - 4, CEIL_GAL - 1, 7)
    p.clear(3, 1, W - 8, W - 4, CEIL_GAL - 1, W - 4)
    p.clear(3, 1, 3, 7, CEIL_GAL - 1, W - 4)
    p.clear(W - 8, 1, 3, W - 4, CEIL_GAL - 1, W - 4)
    # gallery ceilings
    p.fill(3, CEIL_GAL, 3, W - 4, CEIL_GAL, 7, WALL)
    p.fill(3, CEIL_GAL, W - 8, W - 4, CEIL_GAL, W - 4, WALL)
    p.fill(3, CEIL_GAL, 3, 7, CEIL_GAL, W - 4, WALL)
    p.fill(W - 8, CEIL_GAL, 3, W - 4, CEIL_GAL, W - 4, WALL)
    # carve the hall
    p.clear(11, 1, 11, W - 12, CEIL_HALL - 1, W - 12)
    # hall ceiling
    p.fill(8, CEIL_HALL, 8, W - 9, CEIL_HALL, W - 9, WALL)
    for x in range(8, W - 8, 4):
        for z in range(8, W - 8, 4):
            p.put(x, CEIL_HALL, z, CHISEL)


def doorways(p):
    """Open 5-wide ways through the outer wall at the quarter points."""
    for a, b in DOORS:
        for y in range(1, 8):
            for i in range(a, b + 1):
                # north and south
                for d in range(0, 4):
                    p.air(i, y, d)
                    p.air(i, y, W - 1 - d)
                    p.air(d, y, i)
                    p.air(W - 1 - d, y, i)
        # arches over each opening
        for i in range(a - 1, b + 2):
            p.put(i, 8, 2, CHISEL)
            p.put(i, 8, W - 3, CHISEL)
            p.put(2, 8, i, CHISEL)
            p.put(W - 3, 8, i, CHISEL)
        # a smooth course in the floor, so each gate reads as a gate
        for i in range(a, b + 1):
            p.put(i, 0, 1, SMOOTH)
            p.put(i, 0, W - 2, SMOOTH)
            p.put(1, 0, i, SMOOTH)
            p.put(W - 2, 0, i, SMOOTH)
        # iron gate posts
        for y in range(1, 8):
            p.put(a - 1, y, 2, CHISEL)
            p.put(b + 1, y, 2, CHISEL)
            p.put(a - 1, y, W - 3, CHISEL)
            p.put(b + 1, y, W - 3, CHISEL)
            p.put(2, y, a - 1, CHISEL)
            p.put(2, y, b + 1, CHISEL)
            p.put(W - 3, y, a - 1, CHISEL)
            p.put(W - 3, y, b + 1, CHISEL)


def archways(p):
    """Four ways between gallery and hall, on the axes."""
    for x0, x1, z0, z1 in ((27, 33, 8, 10), (27, 33, W - 11, W - 9),
                           (8, 10, 27, 33), (W - 11, W - 9, 27, 33)):
        p.clear(x0, 1, z0, x1, 8, z1)
        for a in range(x0 - 1, x1 + 2):
            for b in range(z0, z1 + 1):
                p.put(a, 9, b, CHISEL)


def colonnade(p):
    """Two rows of squared piers, aligned to the ceiling ribs."""
    for cx in (20, 40):
        for cz in (20, 30, 40):
            p.fill(cx, 1, cz, cx + 1, CEIL_HALL - 1, cz + 1, WALL)
            for dx in (0, 1):
                for dz in (0, 1):
                    p.put(cx + dx, 1, cz + dz, CHISEL)
                    p.put(cx + dx, 9, cz + dz, CHISEL)
                    p.put(cx + dx, CEIL_HALL - 2, cz + dz, CHISEL)
            # capital ring
            for d in range(-1, 3):
                for e in (-1, 2):
                    p.put(cx + d, CEIL_HALL - 2, cz + e, CHISEL)
                    p.put(cx + e, CEIL_HALL - 2, cz + d, CHISEL)


def vault_ribs(p):
    """Corbelled ribs across the hall at the pier lines."""
    for cz in (20, 30, 40):
        p.fill(11, CEIL_HALL - 1, cz, W - 12, CEIL_HALL - 1, cz, WALL)
    # wall corbels springing the vault
    for i in range(11, W - 11):
        for y in (CEIL_HALL - 1,):
            if p.b.get((i, y, 11), ("", ()))[0] == "minecraft:air":
                p.stairs(i, y, 11, "north")
            if p.b.get((i, y, W - 12), ("", ()))[0] == "minecraft:air":
                p.stairs(i, y, W - 12, "south")
            if p.b.get((11, y, i), ("", ()))[0] == "minecraft:air":
                p.stairs(11, y, i, "west")
            if p.b.get((W - 12, y, i), ("", ()))[0] == "minecraft:air":
                p.stairs(W - 12, y, i, "east")


def dais_and_throne(p):
    """Raised seat at the north end, with steps down to the aisle."""
    p.fill(24, 1, 12, 36, 2, 18, SMOOTH)
    p.fill(24, 1, 19, 36, 1, 19, SMOOTH)          # first step
    for x in range(24, 37):
        p.put(x, 3, 12, CHISEL)                    # back course
    # relief panels either side of the north way, which stays open
    for x in list(range(22, 27)) + list(range(34, 39)):
        for y in range(4, 9):
            p.put(x, y, 11, CHISEL if (x + y) % 3 else WALL)
    # throne
    p.fill(29, 3, 14, 31, 3, 15, SMOOTH)
    p.fill(29, 4, 14, 31, 6, 14, CHISEL)           # back
    p.put(30, 4, 15, GOLD)
    for dx in (-1, 1):
        p.put(30 + dx * 3, 3, 15, WALL)
        p.put(30 + dx * 3, 4, 15, LAMP)
    # flanking piers
    for dx in (24, 36):
        p.fill(dx, 3, 13, dx, 8, 13, CHISEL)
        p.put(dx, 9, 13, LAMP)


def aisle_and_benches(p):
    """The long approach and the benches along it."""
    for z in range(20, 50):
        for x in range(29, 32):
            p.put(x, 1, z, FLOOR_ALT)
    for z0 in (24, 36):
        for x in (13, 45):
            for z in range(z0, z0 + 6):
                p.slab(x, 1, z)
                p.slab(x + (1 if x < 30 else -1), 1, z)


def rubble(p):
    """Collapsed stone in the corners, and a little gravel spill."""
    spots = [(13, 13), (13, 47), (47, 13), (47, 47), (14, 30), (46, 30)]
    for cx, cz in spots:
        for _ in range(22):
            x = cx + p.rnd.randint(-2, 2)
            z = cz + p.rnd.randint(-2, 2)
            if not (11 <= x <= W - 12 and 11 <= z <= W - 12):
                continue
            p.put(x, 1, z, p.rnd.choice((COBBLED, COBBLE, GRAVEL, ROCK, MOSSY)))
            if p.rnd.random() < 0.25:
                p.put(x, 2, z, p.rnd.choice((COBBLED, GRAVEL, CRACKED)))


def lighting(p):
    for i in range(6, W - 6, 9):
        for y, z in ((CEIL_GAL - 1, 5), (CEIL_GAL - 1, W - 6)):
            p.put(i, y, z, LAMP)
        for y, x in ((CEIL_GAL - 1, 5), (CEIL_GAL - 1, W - 6)):
            p.put(x, y, i, LAMP)
    for cz in (20, 30, 40):
        for cx in (24, 30, 36):
            p.put(cx, CEIL_HALL - 2, cz + 2, CHAIN)
            p.put(cx, CEIL_HALL - 3, cz + 2, LAMP)


def gallery_detail(p):
    """Pilasters and a barred cell gate or two along the ring, for texture."""
    for i in range(9, W - 8, 10):
        for y in range(1, CEIL_GAL):
            p.put(3, y, i, CHISEL)
            p.put(W - 4, y, i, CHISEL)
            p.put(i, y, 3, CHISEL)
            p.put(i, y, W - 4, CHISEL)
    for a, b in ((20, 24), (36, 40)):
        for y in range(1, 6):
            for z in range(a, b + 1):
                p.put(z, y, 3, BAR)
            for x in range(a, b + 1):
                p.put(3, y, x, BAR)


def build_hall(seed=7):
    p = Plan(seed)
    floor_slab(p)
    outer_wall(p)
    gallery_and_partition(p)
    doorways(p)
    archways(p)
    colonnade(p)
    vault_ribs(p)
    dais_and_throne(p)
    aisle_and_benches(p)
    rubble(p)
    lighting(p)
    gallery_detail(p)
    return p


TILES = {"hall": build_hall}


def main():
    name = sys.argv[1] if len(sys.argv) > 1 else "hall"
    if name not in TILES:
        raise SystemExit("unknown tile %s (have %s)" % (name, ", ".join(TILES)))
    p = TILES[name]()
    out = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "resources" \
        / "data" / "tolkienmobs" / "structure" / ("ch2_%s.nbt" % name)
    ok = gb.write_tile(p.b, (0, 0, 0), (W, H, W), out, keep_air=True)
    if not ok:
        raise SystemExit("nothing to write")
    from collections import Counter
    counts = Counter(state[0] for state in p.b.values())
    print("wrote %s  %d blocks  %.0f KB  %d kinds"
          % (out, len(p.b), out.stat().st_size / 1024, len(counts)))
    for n, c in counts.most_common(10):
        print("   %-46s %d" % (n, c))
    return 0


if __name__ == "__main__":
    sys.exit(main())
