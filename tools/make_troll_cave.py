"""Writes the troll-cave template: a crag with a hollow inside it, big enough to hunt trolls in.

The campaign raises its own ground as templates, so a cave the stone can be asked in has to be a file
rather than a place the world generator happens to put somewhere. This builds one: a hill of stone and
turf with four chambers under it, corridors between them, an entrance cut from the eastern flank at the
surface, stalactites over the chambers and the moss, bones and fire a troll would leave behind - then
writes it as the structure NBT that `Sites` places.

Everything outside the crag is left out of the file rather than written as air, so the world's own
ground and trees stay where they were and only the hill is added to them.
"""
import gzip
import math
import random
import struct

OUT = 'src/main/resources/data/tolkienmobs/structure/key_structures/troll_cave.nbt'
# The data version every template this pack already ships was written with - weathertop, the tent and
# the arena are all this - so a cave cut here loads in the same game they do.
DATA_VERSION = 4790

SIZE = (56, 36, 56)
CX, CZ = SIZE[0] // 2, SIZE[2] // 2
SEED = 20261101
random.seed(SEED)

AIR = 'minecraft:air'
GRASS = 'minecraft:grass_block'
DIRT = 'minecraft:dirt'
STONE = 'minecraft:stone'
DEEPSLATE = 'minecraft:deepslate'
COBBLE = 'minecraft:cobblestone'
MOSSY = 'minecraft:mossy_cobblestone'
GRAVEL = 'minecraft:gravel'
MOSS = 'minecraft:moss_block'
BONE = 'minecraft:bone_block'
MAGMA = 'minecraft:magma_block'
TWIGS = 'minecraft:dead_bush'
WEB = 'minecraft:cobweb'


def hill(x, z):
    """How high the crag stands at a column: a dome, dropping to nothing at its foot."""
    r = math.hypot(x - CX, z - CZ) / 27.0
    if r >= 1.0:
        return 0
    return int(10 + 18 * (1.0 - r * r))


CHAMBERS = (
    (CX - 10, 11, CZ - 9, 11.0),
    (CX + 11, 10, CZ + 8, 10.5),
    (CX - 5, 6, CZ + 13, 10.0),
    (CX + 4, 16, CZ - 13, 9.0),
)
# The trolls' own chamber, where the fire and the bones are.
LAIR = (CX + 1, 8, CZ + 1, 13.0)
CORRIDORS = (
    ((CX - 10, 11, CZ - 9), (CX + 1, 8, CZ + 1), 3.4),
    ((CX + 11, 10, CZ + 8), (CX + 1, 8, CZ + 1), 3.4),
    ((CX - 5, 6, CZ + 13), (CX + 1, 8, CZ + 1), 3.2),
    ((CX + 4, 16, CZ - 13), (CX + 1, 8, CZ + 1), 3.0),
)
# The way in: a corridor cut from the eastern flank's own surface down into the lair, so the mouth is a
# hole in the hillside rather than a tunnel hanging over the ground.
ENTRANCE = ((CX + 19, max(3, hill(CX + 19, CZ + 3) - 1), CZ + 3), (CX + 7, 9, CZ + 1), 3.2)

solid = {}


def put(x, y, z, block):
    if 0 <= x < SIZE[0] and 0 <= y < SIZE[1] and 0 <= z < SIZE[2]:
        solid[(x, y, z)] = block


def inside_sphere(cx, cy, cz, radius, x, y, z):
    return (x - cx) ** 2 + (y - cy) ** 2 * 1.35 + (z - cz) ** 2 <= radius * radius


def on_corridor(start, end, radius, x, y, z):
    steps = 28
    for step in range(steps + 1):
        t = step / steps
        px = start[0] + (end[0] - start[0]) * t
        py = start[1] + (end[1] - start[1]) * t
        pz = start[2] + (end[2] - start[2]) * t
        if inside_sphere(px, py, pz, radius, x, y, z):
            return True
    return False


# ---- the crag ----
for x in range(SIZE[0]):
    for z in range(SIZE[2]):
        top = hill(x, z)
        for y in range(top):
            depth = top - y
            if depth == 1:
                block = GRASS if y > 2 else STONE
            elif depth <= 4:
                block = DIRT
            elif y < top * 0.45:
                block = DEEPSLATE if random.random() < 0.65 else STONE
            else:
                block = STONE
            put(x, y, z, block)

# ---- the hollow ----
for x in range(SIZE[0]):
    for y in range(SIZE[1]):
        for z in range(SIZE[2]):
            if (x, y, z) not in solid:
                continue
            way_in = on_corridor(ENTRANCE[0], ENTRANCE[1], ENTRANCE[2], x, y, z)
            # Rock has to stay under the cave too: a chamber that cuts through the floor of the
            # template would be a hole into whatever the world has below it.
            if y < 6:
                continue
            # A roof has to stay over the cave, or the hill is a scoop rather than a hill. The one
            # exception is the way in, which is cut right up to the surface so the mouth stands open.
            if hill(x, z) - y < 3 and not (way_in and y <= hill(x, z)):
                continue
            carved = any(inside_sphere(c[0], c[1], c[2], c[3], x, y, z) for c in CHAMBERS)
            if not carved and inside_sphere(LAIR[0], LAIR[1], LAIR[2], LAIR[3], x, y, z):
                carved = True
            if not carved:
                carved = way_in or any(on_corridor(start, end, radius, x, y, z)
                                       for start, end, radius in CORRIDORS)
            if carved:
                solid[(x, y, z)] = AIR

# ---- what the trolls left ----
floors = []
ceilings = []
for (x, y, z), block in list(solid.items()):
    if block != AIR:
        continue
    below = solid.get((x, y - 1, z))
    above = solid.get((x, y + 1, z))
    if below not in (None, AIR):
        floors.append((x, y, z))
    if above not in (None, AIR):
        ceilings.append((x, y, z))

for x, y, z in floors:
    roll = random.random()
    if roll < 0.16:
        put(x, y - 1, z, MOSS if random.random() < 0.6 else MOSSY)
    elif roll < 0.22:
        put(x, y - 1, z, GRAVEL)
    elif roll < 0.235:
        put(x, y, z, TWIGS)

# Stalagmites up from the floor and stalactites down from the roof, kept clear of each chamber's middle
# so nothing is ever standing in the doorway.
for (cx, cy, cz, radius) in CHAMBERS + (LAIR,):
    for _ in range(int(radius * 2.4)):
        angle = random.uniform(0, math.tau)
        reach = random.uniform(radius * 0.35, radius * 0.85)
        x = int(cx + math.cos(angle) * reach)
        z = int(cz + math.sin(angle) * reach)
        floor = [y for (bx, y, bz) in floors if bx == x and bz == z]
        if not floor:
            continue
        y = max(floor)
        for step in range(random.randint(1, 3)):
            put(x, y + step, z, DEEPSLATE if random.random() < 0.5 else STONE)
        roof = [y for (bx, y, bz) in ceilings if bx == x and bz == z]
        if not roof:
            continue
        top = min(roof)
        for step in range(random.randint(1, 2)):
            put(x, top - step, z, DEEPSLATE if random.random() < 0.5 else STONE)

# The lair itself: a fire, a ring of stone round it, bones about it, and webs in its corners.
fx, fy, fz = LAIR[0], LAIR[1] - 1, LAIR[2]
near = sorted((abs(x - fx) + abs(z - fz), x, y, z) for (x, y, z) in floors
              if abs(x - fx) <= 3 and abs(z - fz) <= 3)
if near:
    _, fx, fy, fz = near[0]
    put(fx, fy - 1, fz, MAGMA)
    for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1), (1, 1), (-1, -1), (1, -1), (-1, 1)):
        put(fx + dx, fy - 1, fz + dz, COBBLE)
    for _ in range(16):
        angle = random.uniform(0, math.tau)
        reach = random.uniform(2.5, LAIR[3] - 1.0)
        x = int(fx + math.cos(angle) * reach)
        z = int(fz + math.sin(angle) * reach)
        floor = [y for (bx, y, bz) in floors if bx == x and bz == z]
        if floor:
            put(x, max(floor) - 1, z, BONE)

for x, y, z in floors:
    if random.random() < 0.02:
        if solid.get((x, y + 2, z)) in (STONE, DEEPSLATE):
            put(x, y + 1, z, WEB)

# ---- write the file ----
palette = []
indices = {}


def state_id(block):
    if block not in indices:
        indices[block] = len(palette)
        palette.append(block)
    return indices[block]


blocks = [(pos, state_id(block)) for pos, block in sorted(solid.items())]


def field(name, value):
    """One string tag: its name, then its value, each with its length in front."""
    label = name.encode('utf-8')
    data = value.encode('utf-8')
    return b'\x08' + struct.pack('>H', len(label)) + label + struct.pack('>H', len(data)) + data


root = bytearray()
root += b'\x03' + struct.pack('>H', 11) + b'DataVersion' + struct.pack('>i', DATA_VERSION)
root += b'\x09' + struct.pack('>H', 4) + b'size' + b'\x03' + struct.pack('>i', 3)
root += struct.pack('>iii', *SIZE)
root += b'\x09' + struct.pack('>H', 7) + b'palette' + b'\x0a' + struct.pack('>i', len(palette))
for block in palette:
    root += field('Name', block)
    root += b'\x00'
root += b'\x09' + struct.pack('>H', 6) + b'blocks' + b'\x0a' + struct.pack('>i', len(blocks))
for (x, y, z), state in blocks:
    root += b'\x09' + struct.pack('>H', 3) + b'pos' + b'\x03' + struct.pack('>i', 3)
    root += struct.pack('>iii', x, y, z)
    root += b'\x03' + struct.pack('>H', 5) + b'state' + struct.pack('>i', state)
    root += b'\x00'
root += b'\x09' + struct.pack('>H', 8) + b'entities' + b'\x0a' + struct.pack('>i', 0)
root += b'\x00'

with gzip.open(OUT, 'wb') as f:
    f.write(b'\x0a' + struct.pack('>H', 0) + bytes(root))

air_cells = sum(1 for _, state in blocks if palette[state] == AIR)
print('wrote %s: %d blocks (%d of them air), %d palette entries, size %s'
      % (OUT, len(blocks), air_cells, len(palette), SIZE))
