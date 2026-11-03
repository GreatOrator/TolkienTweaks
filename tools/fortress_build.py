"""Build the chapter-two dwarven fortress tiles as structure NBT.

The geomorph drawings (tools/geomorphs, set 5) supply the layout and the features; this turns
each panel into squared dwarven masonry. Every tile is 61x61 with open doorways at blocks 13-17
and 43-47 on all four faces - the convention the pack's example schematics use - so any tile
meets any tile without being matched.

    python fortress_build.py all            every tile
    python fortress_build.py hall vault     named tiles
"""
import pathlib
import random
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import grab_build as gb

W = 61                      # tile edge, blocks
H_MAX = 26                  # tallest piece we write
H_DEEP = 20                 # how far below a tile's floor it may cut while building
DOORS = ((13, 17), (43, 47))
IN = (11, 49)               # interior of the hall behind the gallery ring
RING = (3, 7)               # gallery ring, five wide

ROCK = "minecraft:deepslate"
FLOOR = "tolkienmobs:angainor"
FLOOR_ALT = "tolkienmobs:angainor_bricks"
WALL = "tolkienmobs:angainor_bricks"
CHISEL = "tolkienmobs:chiseled_angainor_bricks"
SMOOTH = "tolkienmobs:smooth_angainor"
SLAB = "tolkienmobs:slab_angainor_bricks"
SLAB_SMOOTH = "tolkienmobs:slab_smooth_angainor"
STAIRS = "tolkienmobs:stairs_angainor_bricks"
PARAPET = "tolkienmobs:wall_angainor_bricks"
PARAPET_CHISEL = "tolkienmobs:wall_chiseled_angainor_bricks"
GOLD = "minecraft:gold_block"
LAMP = "minecraft:lantern"
CHAIN = "minecraft:chain"
WATER = "minecraft:water"
GRAVEL = "minecraft:gravel"
COBBLE = "minecraft:cobblestone"
CRACKED = "tolkienmobs:cracked_angainor_bricks"
COBBLED = "tolkienmobs:cobbled_angainor"
MOSSY = "tolkienmobs:mossy_cobbled_angainor"
BAR = "minecraft:iron_bars"


class Plan:
    def __init__(self, seed=0, top=H_MAX):
        self.b = {}
        self.rnd = random.Random(seed)
        self.top = top

    def get(self, x, y, z):
        return self.b.get((x, y, z), ("minecraft:air", ()))[0]

    def put(self, x, y, z, name, props=()):
        if 0 <= x < W and -H_DEEP <= y < self.top and 0 <= z < W:
            self.b[(x, y, z)] = (name, tuple(props))

    def air(self, x, y, z):
        if 0 <= x < W and -H_DEEP <= y < self.top and 0 <= z < W:
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

    def shift(self, dy):
        """Move the piece up, so a tile dug below its floor still writes from 0."""
        self.b = {(x, y + dy, z): st for (x, y, z), st in self.b.items()}
        self.top += dy

    def post(self, x, y, z, name=PARAPET):
        self.put(x, y, z, name, (("up", "true"), ("east", "low"), ("north", "low"),
                                 ("south", "low"), ("west", "low"), ("waterlogged", "false")))


# ---- shared shell -----------------------------------------------------------

def shell(p, hall_top, gal_top=9, ring=True, door_h=7):
    """Floor, outer wall, gallery ring, partition frame, doorways and ceilings."""
    for x in range(W):
        for z in range(W):
            edge = x < 3 or x > W - 4 or z < 3 or z > W - 4
            p.put(x, 0, z, FLOOR_ALT if edge else FLOOR, ())
    for x in range(W):
        for z in range(W):
            if not (x < 3 or x > W - 4 or z < 3 or z > W - 4):
                continue
            for y in range(hall_top):
                name = WALL
                if y == door_h + 1 or (y in (1, door_h) and (x + z) % 8 == 0):
                    name = CHISEL
                elif (x * 7 + z * 13) % 41 == 0:
                    name = CRACKED
                p.put(x, y, z, name, ())
    if ring:
        p.fill(8, 0, 8, 10, hall_top - 1, 10, WALL)
        p.fill(8, 0, 8, W - 9, hall_top - 1, 10, WALL)
        p.fill(W - 11, 0, 8, W - 9, hall_top - 1, W - 9, WALL)
        p.fill(8, 0, W - 11, W - 9, hall_top - 1, W - 9, WALL)
        for i in range(9, W - 8, 10):
            if 26 <= i <= 34:
                continue
            p.put(11, 1, i, CHISEL)
            p.put(11, gal_top - 2, i, CHISEL)
            p.put(i, 1, 11, CHISEL)
            p.put(i, gal_top - 2, 11, CHISEL)
        p.clear(3, 1, 3, W - 4, gal_top - 1, 7)
        p.clear(3, 1, W - 8, W - 4, gal_top - 1, W - 4)
        p.clear(3, 1, 3, 7, gal_top - 1, W - 4)
        p.clear(W - 8, 1, 3, W - 4, gal_top - 1, W - 4)
        p.fill(3, gal_top, 3, W - 4, gal_top, 7, WALL)
        p.fill(3, gal_top, W - 8, W - 4, gal_top, W - 4, WALL)
        p.fill(3, gal_top, 3, 7, gal_top, W - 4, WALL)
        p.fill(W - 8, gal_top, 3, W - 4, gal_top, W - 4, WALL)
        # solid above the gallery, so the tile reads as rock with a passage in it
        if hall_top - 1 > gal_top:
            p.fill(3, gal_top + 1, 3, W - 8, hall_top - 1, 7, WALL)
            p.fill(3, gal_top + 1, W - 8, W - 4, hall_top - 1, W - 4, WALL)
            p.fill(3, gal_top + 1, 3, 7, hall_top - 1, W - 8, WALL)
            p.fill(W - 8, gal_top + 1, 3, W - 4, hall_top - 1, W - 8, WALL)
    else:
        p.fill(8, 0, 8, 10, hall_top - 1, 10, WALL)
        p.fill(8, 0, 8, W - 9, hall_top - 1, 10, WALL)
        p.fill(W - 11, 0, 8, W - 9, hall_top - 1, W - 9, WALL)
        p.fill(8, 0, W - 11, W - 9, hall_top - 1, W - 9, WALL)
    p.clear(IN[0], 1, IN[0], IN[1], hall_top - 1, IN[1])
    p.fill(8, hall_top, 8, W - 9, hall_top, W - 9, WALL)
    for x in range(8, W - 8, 4):
        for z in range(8, W - 8, 4):
            p.put(x, hall_top, z, CHISEL)
    doorways(p, door_h)
    archways(p, gal_top)


def doorways(p, door_h=7):
    for a, b in DOORS:
        for y in range(1, door_h + 1):
            for i in range(a, b + 1):
                for d in range(0, 4):
                    p.air(i, y, d)
                    p.air(i, y, W - 1 - d)
                    p.air(d, y, i)
                    p.air(W - 1 - d, y, i)
        for i in range(a - 1, b + 2):
            p.put(i, door_h + 1, 2, CHISEL)
            p.put(i, door_h + 1, W - 3, CHISEL)
            p.put(2, door_h + 1, i, CHISEL)
            p.put(W - 3, door_h + 1, i, CHISEL)
        for i in range(a, b + 1):
            p.put(i, 0, 1, SMOOTH)
            p.put(i, 0, W - 2, SMOOTH)
            p.put(1, 0, i, SMOOTH)
            p.put(W - 2, 0, i, SMOOTH)
        for y in range(1, door_h + 1):
            p.put(a - 1, y, 2, CHISEL)
            p.put(b + 1, y, 2, CHISEL)
            p.put(a - 1, y, W - 3, CHISEL)
            p.put(b + 1, y, W - 3, CHISEL)
            p.put(2, y, a - 1, CHISEL)
            p.put(2, y, b + 1, CHISEL)
            p.put(W - 3, y, a - 1, CHISEL)
            p.put(W - 3, y, b + 1, CHISEL)


def archways(p, gal_top=9, spans=None):
    spans = spans or ((27, 33, 8, 10), (27, 33, W - 11, W - 9),
                      (8, 10, 27, 33), (W - 11, W - 9, 27, 33))
    for x0, x1, z0, z1 in spans:
        p.clear(x0, 1, z0, x1, gal_top - 1, z1)
        for a in range(x0 - 1, x1 + 2):
            for b in range(z0, z1 + 1):
                p.put(a, gal_top, b, CHISEL)


def lamps_ring(p, gal_top=9):
    for i in range(6, W - 6, 9):
        p.put(i, gal_top - 1, 5, LAMP)
        p.put(i, gal_top - 1, W - 6, LAMP)
        p.put(5, gal_top - 1, i, LAMP)
        p.put(W - 6, gal_top - 1, i, LAMP)


def rubble(p, spots):
    for cx, cz in spots:
        for _ in range(24):
            x = cx + p.rnd.randint(-2, 2)
            z = cz + p.rnd.randint(-2, 2)
            if not (11 <= x <= W - 12 and 11 <= z <= W - 12):
                continue
            p.put(x, 1, z, p.rnd.choice((COBBLED, COBBLE, GRAVEL, ROCK, MOSSY)))
            if p.rnd.random() < 0.25:
                p.put(x, 2, z, p.rnd.choice((COBBLED, GRAVEL, CRACKED)))


# ---- the six tiles ----------------------------------------------------------

def build_hall(p):
    """Set 5, middle left: the great hall."""
    colonnade(p, top=11)
    for cz in (20, 30, 40):
        p.fill(11, 11, cz, W - 12, 11, cz, WALL)
    for i in range(11, W - 11):
        for y in (11,):
            if p.get(i, y, 11) == "minecraft:air":
                p.stairs(i, y, 11, "north")
            if p.get(i, y, W - 12) == "minecraft:air":
                p.stairs(i, y, W - 12, "south")
            if p.get(11, y, i) == "minecraft:air":
                p.stairs(11, y, i, "west")
            if p.get(W - 12, y, i) == "minecraft:air":
                p.stairs(W - 12, y, i, "east")
    # raised seat at the north end
    p.fill(24, 1, 12, 36, 2, 18, SMOOTH)
    p.fill(24, 1, 19, 36, 1, 19, SMOOTH)
    for x in range(24, 37):
        p.put(x, 3, 12, CHISEL)
    for x in list(range(22, 27)) + list(range(34, 39)):
        for y in range(4, 9):
            p.put(x, y, 11, CHISEL if (x + y) % 3 else WALL)
    p.fill(29, 3, 14, 31, 3, 15, SMOOTH)
    p.fill(29, 4, 14, 31, 6, 14, CHISEL)
    p.put(30, 4, 15, GOLD)
    for dx in (-1, 1):
        p.put(30 + dx * 3, 3, 15, WALL)
        p.put(30 + dx * 3, 4, 15, LAMP)
    for dx in (24, 36):
        p.fill(dx, 3, 13, dx, 8, 13, CHISEL)
        p.put(dx, 9, 13, LAMP)
    # approach and benches
    for z in range(20, 50):
        for x in range(29, 32):
            p.put(x, 1, z, FLOOR_ALT)
    for z0 in (24, 36):
        for x in (13, 45):
            for z in range(z0, z0 + 6):
                p.slab(x, 1, z)
                p.slab(x + (1 if x < 30 else -1), 1, z)
    rubble(p, [(13, 13), (13, 47), (47, 13), (47, 47), (14, 30), (46, 30)])
    lamps_ring(p)
    for cz in (20, 30, 40):
        for cx in (24, 30, 36):
            p.put(cx, 10, cz + 2, CHAIN)
            p.put(cx, 9, cz + 2, LAMP)
    gallery_detail(p)
    return 12, 12


def colonnade(p, top=11):
    for cx in (20, 40):
        for cz in (20, 30, 40):
            p.fill(cx, 1, cz, cx + 1, top, cz + 1, WALL)
            for dx in (0, 1):
                for dz in (0, 1):
                    p.put(cx + dx, 1, cz + dz, CHISEL)
                    p.put(cx + dx, top - 2, cz + dz, CHISEL)
            for d in range(-1, 3):
                for e in (-1, 2):
                    p.put(cx + d, top - 1, cz + e, CHISEL)
                    p.put(cx + e, top - 1, cz + d, CHISEL)


def gallery_detail(p):
    for i in range(9, W - 8, 10):
        for y in range(1, 8):
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


def build_approach(p):
    """Set 5, top left: the winding way in - dog-legged, with two chambers."""
    # two walls force the way to turn twice
    p.fill(11, 1, 24, 40, 8, 26, WALL)
    p.fill(20, 1, 34, 49, 8, 36, WALL)
    for x in range(11, 41):
        p.put(x, 9, 24, CHISEL)
        p.put(x, 9, 26, CHISEL)
    for x in range(20, 50):
        p.put(x, 9, 34, CHISEL)
        p.put(x, 9, 36, CHISEL)
    # gate piers at the turn
    for x in (11, 49):
        p.fill(x, 1, 24, x, 8, 26, CHISEL)
    # north chamber: a cell block against the east side, clear of the way in
    p.fill(36, 0, 13, 44, 0, 21, FLOOR_ALT)
    for y in range(1, 8):
        for z in range(13, 22):
            p.put(36, y, z, BAR)
            p.put(44, y, z, CHISEL)
    for z in (13, 21):
        p.fill(36, 1, z, 44, 8, z, WALL)
    # south chamber: a mustering hall with a low bench ring
    p.fill(13, 1, 44, 47, 1, 47, WALL)
    for x in range(14, 47):
        p.slab(x, 2, 44, SLAB_SMOOTH)
    for z in range(44, 48):
        p.slab(13, 2, z, SLAB_SMOOTH)
        p.slab(47, 2, z, SLAB_SMOOTH)
    # braziers at the turns
    for z in (23, 27, 33, 37):
        p.put(15, 1, z, CHISEL)
        p.put(15, 2, z, LAMP)
        p.put(45, 1, z, CHISEL)
        p.put(45, 2, z, LAMP)
    rubble(p, [(13, 13), (47, 13), (30, 30)])
    lamps_ring(p)
    for i in range(14, 48, 6):
        p.put(i, 8, 5, LAMP)
        p.put(i, 8, W - 6, LAMP)
    return 12, 12


def build_vault(p):
    """Set 5, top right: the treasure vault - rounded, crenellated inside."""
    cx = cz = 30
    r = 16
    # outer ring of the chamber
    for x in range(cx - r, cx + r + 1):
        for z in range(cz - r, cz + r + 1):
            dx, dz = abs(x - cx), abs(z - cz)
            if max(dx, dz) <= r and dx + dz <= r + 8:
                p.fill(x, 1, z, x, 9, z, WALL)
    for x in range(cx - r + 2, cx + r - 1):
        for z in range(cz - r + 2, cz + r - 1):
            dx, dz = abs(x - cx), abs(z - cz)
            if max(dx, dz) <= r - 2 and dx + dz <= r + 6:
                p.clear(x, 1, z, x, 9, z)
    # crenellation: every third block of the inner face juts out
    for x in range(cx - r + 2, cx + r - 1):
        for z in range(cz - r + 2, cz + r - 1):
            dx, dz = abs(x - cx), abs(z - cz)
            if max(dx, dz) == r - 2 and dx + dz <= r + 6 and (x + z) % 3 == 0:
                p.fill(x, 1, z, x, 5, z, CHISEL)
    # floor inlay and the strongroom door on the west side
    for x in range(cx - 8, cx + 9):
        for z in range(cz - 8, cz + 9):
            if (x + z) % 4 == 0:
                p.put(x, 0, z, SMOOTH)
    # the way in, open to head height, with a raised portcullis over it
    p.clear(cx - r, 1, cz - 3, cx - r + 2, 6, cz + 3)
    p.fill(cx - r, 7, cz - 3, cx - r + 2, 7, cz + 3, BAR)
    p.fill(cx - r, 1, cz - 4, cx - r + 1, 7, cz - 4, CHISEL)
    p.fill(cx - r, 1, cz + 4, cx - r + 1, 7, cz + 4, CHISEL)
    p.fill(cx - r, 8, cz - 4, cx - r + 1, 8, cz + 4, CHISEL)
    # gold on the plinth at the centre
    p.fill(cx - 2, 1, cz - 2, cx + 2, 1, cz + 2, CHISEL)
    p.fill(cx - 1, 2, cz - 1, cx + 1, 2, cz + 1, GOLD)
    for dx, dz in ((0, 0), (-1, -1), (1, 1), (-1, 1), (1, -1)):
        p.put(cx + dx * 3, 3, cz + dz * 3, LAMP)
    # ribs springing to the chamber ceiling
    for i in range(cx - 10, cx + 11, 5):
        p.fill(i, 9, cz - 12, i, 9, cz + 12, CHISEL)
    rubble(p, [(cx - 12, cz - 12), (cx + 12, cz + 12)])
    lamps_ring(p)
    return 12, 12


def build_colonnade(p):
    """Set 5, middle right: four free-standing piers under a coffered ceiling."""
    for cx in (20, 40):
        for cz in (20, 40):
            p.fill(cx, 1, cz, cx + 2, 11, cz + 2, WALL)
            for dx in range(3):
                for dz in range(3):
                    p.put(cx + dx, 1, cz + dz, CHISEL)
                    p.put(cx + dx, 10, cz + dz, CHISEL)
            for d in range(-1, 4):
                for e in (-1, 3):
                    p.put(cx + d, 11, cz + e, CHISEL)
                    p.put(cx + e, 11, cz + d, CHISEL)
    # arcade screens along the north and south walls
    for z in (12, 48):
        for x in range(13, 49, 3):
            p.fill(x, 1, z, x, 7, z, PARAPET)
            p.put(x, 8, z, CHISEL)
        for x in range(13, 49):
            if (x - 13) % 3:
                p.put(x, 8, z, SLAB)
    # the well in the north-east corner
    p.fill(43, 1, 14, 47, 1, 18, CHISEL)
    p.fill(44, 1, 15, 46, 1, 17, WATER)
    p.fill(44, 2, 15, 44, 4, 15, PARAPET)
    p.fill(46, 2, 15, 46, 4, 17, PARAPET)
    p.fill(44, 4, 15, 46, 4, 17, SLAB)
    p.put(45, 5, 16, CHAIN)
    p.put(45, 4, 16, LAMP)
    # hanging lights between the piers
    for cx in (24, 30, 36):
        for cz in (24, 36):
            p.put(cx, 11, cz, CHAIN)
            p.put(cx, 10, cz, LAMP)
    rubble(p, [(14, 14), (46, 46), (30, 30)])
    lamps_ring(p)
    return 12, 12


def build_cistern(p):
    """Set 5, bottom left: the water hall - a sunken basin and alcoves."""
    cx = cz = 30
    r = 7
    for x in range(cx - r, cx + r + 1):
        for z in range(cz - r, cz + r + 1):
            if (x - cx) ** 2 + (z - cz) ** 2 <= r * r:
                p.put(x, 1, z, WATER)
                p.put(x, 2, z, "minecraft:air")
    for x in range(cx - r - 1, cx + r + 2):
        for z in range(cz - r - 1, cz + r + 2):
            d = (x - cx) ** 2 + (z - cz) ** 2
            if r * r < d <= (r + 1) ** 2:
                p.put(x, 1, z, CHISEL)
    # the spring column at the centre, water falling round it
    p.fill(cx, 1, cz, cx, 4, cz, CHISEL)
    p.put(cx, 5, cz, WATER)
    for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
        p.put(cx + dx, 1, cz + dz, WATER)
    # three columns round the basin
    for ax, az in ((18, 18), (42, 18), (18, 42)):
        p.fill(ax, 1, az, ax + 1, 11, az + 1, WALL)
        for dx in (0, 1):
            for dz in (0, 1):
                p.put(ax + dx, 1, az + dz, CHISEL)
                p.put(ax + dx, 9, az + dz, CHISEL)
    # alcoves cut into three walls, with a lamp in each
    for x0 in (16, 24, 32, 40):
        p.clear(x0, 1, 11, x0 + 3, 5, 12)
        p.put(x0 + 1, 4, 11, LAMP)
        p.clear(x0, 1, 49, x0 + 3, 5, 48)
        p.put(x0 + 1, 4, 49, LAMP)
    for z0 in (16, 24, 32, 40):
        p.clear(11, 1, z0, 12, 5, z0 + 3)
        p.put(11, 4, z0 + 1, LAMP)
        p.clear(49, 1, z0, 48, 5, z0 + 3)
        p.put(49, 4, z0 + 1, LAMP)
    for i in range(14, 48, 6):
        p.put(i, 11, 20, CHAIN)
        p.put(i, 10, 20, LAMP)
        p.put(i, 11, 40, CHAIN)
        p.put(i, 10, 40, LAMP)
    rubble(p, [(13, 13), (47, 47)])
    lamps_ring(p)
    return 12, 12


def build_spiral(p):
    """Set 5, bottom right: the way down - a helical stair in the middle of the room."""
    cx = cz = 30
    r = 4                       # the opening is 9x9, so the stair turns inside the room
    deep = 13                   # hall floor at 0, landing twelve blocks below it
    for x in range(cx - r - 1, cx + r + 2):
        for z in range(cz - r - 1, cz + r + 2):
            if max(abs(x - cx), abs(z - cz)) == r + 1:
                p.fill(x, -deep, z, x, 0, z, WALL)
    p.fill(cx - 1, -deep, cz - 1, cx + 1, 0, cz + 1, WALL)
    p.clear(cx - r, 0, cz - r, cx + r, 0, cz + r)
    # one tread per block round the newel, one block down each
    a = 3
    ring = ([(x, cz - a) for x in range(cx - a, cx + a + 1)]
            + [(cx + a, z) for z in range(cz - a + 1, cz + a + 1)]
            + [(x, cz + a) for x in range(cx + a - 1, cx - a - 1, -1)]
            + [(cx - a, z) for z in range(cz + a - 1, cz - a, -1)])
    treads = ring[:12]
    for i, (x, z) in enumerate(treads):
        y = -i
        hx, hz = ring[i - 1] if i else (cx - a, cz - a + 1)
        dx, dz = hx - x, hz - z
        facing = ("north" if dz < 0 else "south" if dz > 0
                  else "east" if dx > 0 else "west")
        p.stairs(x, y, z, facing)
        p.fill(x, -deep, z, x, y - 1, z, WALL)
    # the foot: a small landing with light in it
    p.fill(cx - 3, -deep + 1, cz - 3, cx + 3, -deep + 1, cz + 3, FLOOR_ALT)
    p.put(cx, -11, cz + 2, LAMP)
    p.put(cx + 2, -11, cz - 2, LAMP)
    # a parapet round the opening, open on the north-west where the stair starts
    for x in range(cx - r - 1, cx + r + 2):
        for z in range(cz - r - 1, cz + r + 2):
            if max(abs(x - cx), abs(z - cz)) == r + 1 and not (x <= cx - r and z <= cz - r):
                p.post(x, 1, z)
    p.put(cx, 1, cz, CHISEL)
    p.put(cx, 2, cz, LAMP)
    rubble(p, [(cx - 9, cz + 9), (cx + 9, cz + 9)])
    lamps_ring(p)
    p.shift(deep)
    return 12, 26, deep + 1


TILES = {
    "hall": (12, 13, 1, build_hall),
    "approach": (12, 13, 1, build_approach),
    "vault": (12, 13, 1, build_vault),
    "colonnade": (12, 13, 1, build_colonnade),
    "cistern": (12, 13, 1, build_cistern),
    "spiral": (12, 26, 14, build_spiral),
}


# ---- checks -----------------------------------------------------------------

def check(p, floor_y=1):
    """Largest reachable floor component; every doorway has to belong to it."""
    y = floor_y
    def open_(x, z):
        return p.get(x, y, z) == "minecraft:air" and p.get(x, y + 1, z) == "minecraft:air"

    left = {(x, z) for x in range(W) for z in range(W) if open_(x, z)}
    best = set()
    while left:
        start = left.pop()
        comp = {start}
        stack = [start]
        while stack:
            x, z = stack.pop()
            for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                n = (x + dx, z + dz)
                if n in left:
                    left.discard(n)
                    comp.add(n)
                    stack.append(n)
        if len(comp) > len(best):
            best = comp

    doors = all((i, 0) in best and (i, W - 1) in best and (0, i) in best and (W - 1, i) in best
                for a, b in DOORS for i in range(a, b + 1))
    reached = sum(1 for (x, z) in best if z in range(3, 8) or z in range(W - 8, W - 3))
    return len(best), doors, "ring cells reached %d" % reached


def main():
    names = sys.argv[1:] or ["hall"]
    if names == ["all"]:
        names = list(TILES)
    out_dir = (pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "resources"
               / "data" / "tolkienmobs" / "structure")
    for name in names:
        if name not in TILES:
            raise SystemExit("unknown tile %s (have %s)" % (name, ", ".join(TILES)))
        hall_top, top, floor_y, interior = TILES[name]
        p = Plan(seed=sum((i + 1) * ord(c) for i, c in enumerate(name)), top=top)
        shell(p, hall_top)
        interior(p)
        target = out_dir / ("ch2_%s.nbt" % name)
        if not gb.write_tile(p.b, (0, 0, 0), (W, top, W), target, keep_air=True):
            raise SystemExit("%s: nothing to write" % name)
        cells, doors, note = check(p, floor_y)
        from collections import Counter
        counts = Counter(state[0] for state in p.b.values())
        print("%-10s %5d blocks  %4.0f KB  %2d kinds  floor reach %4d  doors %s  %s"
              % (name, len(p.b), target.stat().st_size / 1024, len(counts), cells,
                 "ok" if doors else "BROKEN", note))
        if not doors:
            print("   !! a doorway does not connect to the rest of the tile")
    return 0


if __name__ == "__main__":
    sys.exit(main())
