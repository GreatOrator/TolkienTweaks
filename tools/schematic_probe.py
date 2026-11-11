#!/usr/bin/env python3
"""Measures a legacy .schematic, so a build can be judged before it is converted.

A `.schematic` is the old MCEdit/WorldEdit file: a gzipped NBT compound with `Width`, `Height`
and `Length`, one byte of numeric block id per cell in `Blocks` (high nibbles in `AddBlocks` when
the ids run past 255), a `Data` array and the entities. There is no palette, so nothing here can
say `minecraft:tuff` - it says id 1 and leaves the naming to whoever knows the table.

The numbers worth having before anything is cut out of one of these:

    dims      how big the file's box is.
    placed    the blocks that are not the capture's background - its air and its own rock. A cave
              cut out of a world comes with the world, and what was built into it is the only part
              that belongs to anybody. Gundabad's box was 7.9M cells and its build 195k, which is
              why it was cut to its rooms rather than shipped whole.
    box       the tight box round the placed blocks: what a crop would actually ship, and how much
              air inside that box is the cave's own volume.
    faces     air standing on the file's skin, which means the capture was cut through its own
              tunnels and the mouth is outside the file.
    map       the placed blocks from above, so a complex reads as one place or as several.

    python schematic_probe.py Templates/schematics/goblincaves.schematic

The NBT is read by `grab_build`'s reader, the one the structure tools already use; only `Blocks`
and `AddBlocks` are then read cell by cell.
"""
import gzip
import pathlib
import sys
import time

import numpy as np

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import grab_build as gb

# The ids a cave is likely to be built of, so the histogram below reads as blocks rather than
# numbers. Anything not named here is printed as its number; the old table ran to 1.12 and stops.
NAMES = {
    0: "air", 1: "stone", 2: "grass", 3: "dirt", 4: "cobblestone", 5: "planks", 7: "bedrock",
    8: "flowing_water", 9: "water", 10: "flowing_lava", 11: "lava", 12: "sand", 13: "gravel",
    14: "gold_ore", 15: "iron_ore", 16: "coal_ore", 17: "log", 18: "leaves", 20: "glass",
    24: "sandstone", 31: "tallgrass", 35: "wool", 37: "dandelion", 38: "poppy", 39: "brown_mushroom",
    40: "red_mushroom", 41: "gold_block", 42: "iron_block", 43: "double_stone_slab",
    44: "stone_slab", 45: "brick", 46: "tnt", 47: "bookshelf", 48: "mossy_cobblestone",
    49: "obsidian", 50: "torch", 51: "fire", 52: "spawner", 53: "oak_stairs", 54: "chest",
    61: "furnace", 62: "lit_furnace", 65: "ladder", 66: "rail", 67: "cobblestone_stairs",
    69: "lever", 70: "stone_pressure_plate", 71: "iron_door", 72: "wooden_pressure_plate",
    75: "redstone_torch", 76: "redstone_torch_lit", 77: "stone_button", 78: "snow_layer",
    79: "ice", 80: "snow", 81: "cactus", 82: "clay", 83: "reeds", 85: "fence", 86: "pumpkin",
    87: "netherrack", 88: "soul_sand", 89: "glowstone", 91: "jack_o_lantern", 95: "stained_glass",
    96: "trapdoor", 97: "monster_egg", 98: "stonebrick", 99: "brown_mushroom_block",
    100: "red_mushroom_block", 101: "iron_bars", 102: "glass_pane", 103: "melon",
    106: "vine", 107: "fence_gate", 110: "mycelium", 111: "waterlily", 112: "nether_brick",
    113: "nether_brick_fence", 114: "nether_brick_stairs", 116: "enchanting_table",
    121: "end_stone", 123: "redstone_lamp", 124: "redstone_lamp_lit", 126: "wooden_slab",
    128: "sandstone_stairs", 133: "emerald_block", 134: "spruce_stairs", 135: "birch_stairs",
    136: "jungle_stairs", 139: "cobblestone_wall", 155: "quartz_block", 156: "quartz_stairs",
    159: "stained_hardened_clay", 160: "stained_glass_pane", 161: "leaves2", 162: "log2",
    168: "prismarine", 169: "sea_lantern", 170: "hay_block", 172: "hardened_clay",
    173: "coal_block", 174: "packed_ice", 175: "double_plant", 179: "red_sandstone",
    181: "red_sandstone_double_slab", 182: "red_sandstone_slab", 183: "spruce_fence_gate",
    188: "spruce_fence", 193: "spruce_door", 198: "end_rod", 199: "chorus_plant",
    201: "purpur_block", 203: "purpur_pillar", 205: "purpur_slab", 206: "end_bricks",
    208: "grass_path", 210: "repeating_command_block", 211: "chain_command_block",
    213: "magma", 214: "nether_wart_block", 215: "red_nether_brick", 216: "bone_block",
    251: "concrete", 252: "concrete_powder",
}


def tight(grid):
    """The tight box round everything set in a (y, z, x) grid, as (x0, x1, y0, y1, z0, z1)."""
    xs = np.where(grid.any(axis=(0, 1)))[0]
    ys = np.where(grid.any(axis=(1, 2)))[0]
    zs = np.where(grid.any(axis=(0, 2)))[0]
    if not len(xs):
        return None
    return int(xs[0]), int(xs[-1]), int(ys[0]), int(ys[-1]), int(zs[0]), int(zs[-1])


def box_line(label, box, cells):
    x0, x1, y0, y1, z0, z1 = box
    wide, tall, deep = x1 - x0 + 1, y1 - y0 + 1, z1 - z0 + 1
    print("%-7s x %d..%d (%d)  y %d..%d (%d)  z %d..%d (%d)  = %s cells of %s"
          % (label, x0, x1, wide, y0, y1, tall, z0, z1, deep,
             format(wide * tall * deep, ","), format(cells, ",")))
    return wide, tall, deep


def main(argv):
    if len(argv) != 2:
        raise SystemExit(__doc__)
    path = pathlib.Path(argv[1])
    started = time.time()
    raw = path.read_bytes()
    data = gzip.decompress(raw) if raw[:2] == b"\x1f\x8b" else raw
    print("%s: %d bytes on disk, %d uncompressed" % (path.name, len(raw), len(data)))
    root = gb.read_nbt(data)
    width, height, length = int(root["Width"]), int(root["Height"]), int(root["Length"])
    cells = width * height * length
    print("dims %d x %d x %d = %s cells" % (width, height, length, format(cells, ",")))

    blocks = root["Blocks"]
    if len(blocks) != cells:
        print("Blocks holds %s, not %s: the file is not laid out as its header says"
              % (format(len(blocks), ","), format(cells, ",")))
    ids = np.frombuffer(blocks, dtype=np.uint8, count=min(cells, len(blocks)))
    add = root.get("AddBlocks")
    if add:
        nibbles = np.frombuffer(add, dtype=np.uint8)
        need = (len(ids) + 1) // 2
        nibbles = nibbles[:need]
        high = np.zeros(len(ids), dtype=np.uint16)
        high[0:len(ids):2] = nibbles >> 4
        high[1:len(ids):2] = nibbles[: len(ids) // 2] & 0x0F
        ids = ids.astype(np.uint16) + (high << 8)
        print("AddBlocks: the ids run past 255")

    solid = ids != 0
    print("solid %s of %s cells (%.2f%%), air %s"
          % (format(int(solid.sum()), ","), format(cells, ","), 100.0 * solid.sum() / cells,
             format(cells - int(solid.sum()), ",")))

    # MCEdit lays the array out x fastest, then z, then y: index = (y * Length + z) * Width + x.
    grid = solid.reshape((height, length, width))
    box = tight(grid)
    if box:
        box_line("solid", box, cells)
        x0, x1, y0, y1, z0, z1 = box
        print("        margins  west %d  east %d  down %d  up %d  north %d  south %d"
              % (x0, width - 1 - x1, y0, height - 1 - y1, z0, length - 1 - z1))

    # Which faces the build leaves open at all: a capture cropped through its own tunnels has air
    # standing on the file's skin, and that is a mouth cut off rather than a mouth.
    faces = {
        "west": grid[:, :, 0], "east": grid[:, :, -1],
        "north": grid[:, 0, :], "south": grid[:, -1, :],
        "down": grid[0, :, :], "up": grid[-1, :, :],
    }
    print("open faces: " + "  ".join("%s %s" % (name, int((~face).sum()))
                                     for name, face in faces.items()))

    # What was placed, as against the world it was placed in. A capture brings its air and its own
    # rock; counting the air as placed is what made this file's caves read as its build.
    used, tally = np.unique(ids, return_counts=True)
    rock = int(used[int(np.argmax(np.where(used == 0, -1, tally)))])
    matter = ~np.isin(ids, [0, rock])
    placed = int(matter.sum())
    print("\nplaced %s blocks; background is air and %s"
          % (format(placed, ","), NAMES.get(rock, "id:%d" % rock)))
    if not placed:
        raise SystemExit("nothing was placed: the whole file is its background")
    matter_grid = matter.reshape((height, length, width))
    placed_box = tight(matter_grid)
    wide, tall, deep = box_line("placed", placed_box, cells)
    x0, x1, y0, y1, z0, z1 = placed_box
    inside = grid[y0:y1 + 1, z0:z1 + 1, x0:x1 + 1]
    print("        in that box: air %s of %s cells (%.1f%%)"
          % (format(int((~inside).sum()), ","), format(wide * tall * deep, ","),
             100.0 * (~inside).sum() / (wide * tall * deep)))

    step = max(1, max(width, length) // 44)
    rows, top = [], 1
    for z0 in range(0, length, step):
        row = [int(matter_grid[:, z0:z0 + step, x0:x0 + step].sum())
               for x0 in range(0, width, step)]
        top = max(top, max(row))
        rows.append(row)
    print("\nplaced, from above (%d-block cells, # is densest):" % step)
    for row in rows:
        print("  " + "".join(" " if n == 0 else " .:-=+*#%@"[min(9, max(1, 9 * n // top))]
                              for n in row))

    # Where the placed blocks are: a kilometre of old world holds several builds, and only one of
    # them is the cave. Coarse cells are labelled into clusters so each build comes out with a box of
    # its own, and one build's scattering of rubble does not read as another's.
    grain = 8
    cy, cz, cx = -(-height // grain), -(-length // grain), -(-width // grain)
    padded = np.zeros((cy * grain, cz * grain, cx * grain), dtype=bool)
    padded[:height, :length, :width] = matter_grid
    coarse = padded.reshape(cy, grain, cz, grain, cx, grain).sum(axis=(1, 3, 5))
    lit_padded = np.zeros_like(padded)
    lit_padded[:height, :length, :width] = (ids == 89).reshape((height, length, width))
    coarse_lit = lit_padded.reshape(cy, grain, cz, grain, cx, grain).sum(axis=(1, 3, 5))

    seen = np.zeros(coarse.shape, dtype=bool)
    clusters = []
    for sy, sz, sx in zip(*np.nonzero(coarse > 0)):
        if seen[sy, sz, sx]:
            continue
        stack, members = [(int(sy), int(sz), int(sx))], []
        seen[sy, sz, sx] = True
        while stack:
            y, z, x = stack.pop()
            members.append((y, z, x))
            for dy in (-1, 0, 1):
                for dz in (-1, 0, 1):
                    for dx in (-1, 0, 1):
                        ny, nz, nx = y + dy, z + dz, x + dx
                        if 0 <= ny < cy and 0 <= nz < cz and 0 <= nx < cx:
                            if coarse[ny, nz, nx] and not seen[ny, nz, nx]:
                                seen[ny, nz, nx] = True
                                stack.append((ny, nz, nx))
        ys = [m[0] for m in members]
        zs = [m[1] for m in members]
        xs = [m[2] for m in members]
        cluster_box = (min(xs) * grain, min(width - 1, (max(xs) + 1) * grain - 1),
                       min(ys) * grain, min(height - 1, (max(ys) + 1) * grain - 1),
                       min(zs) * grain, min(length - 1, (max(zs) + 1) * grain - 1))
        count = int(sum(int(coarse[m]) for m in members))
        light = int(sum(int(coarse_lit[m]) for m in members))
        clusters.append((count, light, cluster_box))
    clusters.sort(reverse=True)
    print("\n%d cluster(s) of placed blocks; the first six:" % len(clusters))
    for count, light, cluster_box in clusters[:6]:
        x0, x1, y0, y1, z0, z1 = cluster_box
        print("  %8s blocks (%6s glowstone)  x %d..%d (%d)  y %d..%d (%d)  z %d..%d (%d)"
              % (format(count, ","), format(light, ","), x0, x1, x1 - x0 + 1,
                 y0, y1, y1 - y0 + 1, z0, z1, z1 - z0 + 1))

    # A cave is the air, not the placing: a capture of a dug cave is a box of rock with the cave
    # taken out of it, and its rooms read as holes. The world's own carvers leave thin tunnels
    # everywhere, so what matters is where the air is *thick* - that is where somebody worked.
    air_grid = ~grid
    rows, top = [], 1
    for z0 in range(0, length, step):
        row = [int(air_grid[:, z0:z0 + step, x0:x0 + step].sum())
               for x0 in range(0, width, step)]
        top = max(top, max(row))
        rows.append(row)
    print("\nair, from above (%d-block cells, # is thickest):" % step)
    for row in rows:
        print("  " + "".join(" " if n == 0 else " .:-=+*#%@@"[min(9, max(1, 9 * n // top))]
                              for n in row))

    # Where the open ground is. A column here is 79 cells, and a column that is mostly void is a
    # chamber rather than a tunnel; the world's own carvers leave a few cells in every column, so
    # the thresholds say how much of the region reads as worked space and where it begins and ends.
    column_air = air_grid.sum(axis=0)
    print("\ncolumns by how much of them is air, out of %d:" % height)
    for threshold in (4, 8, 16, 32, 48, 64):
        open_cells = column_air >= threshold
        if not open_cells.any():
            continue
        xs = np.where(open_cells.any(axis=0))[0]
        zs = np.where(open_cells.any(axis=1))[0]
        print("  %2d+ air  %9s columns (%5.2f%%)  x %d..%d (%d)  z %d..%d (%d)"
              % (threshold, format(int(open_cells.sum()), ","),
                 100.0 * open_cells.sum() / (width * length),
                 xs[0], xs[-1], xs[-1] - xs[0] + 1, zs[0], zs[-1], zs[-1] - zs[0] + 1))

    print("\nper layer, of %s:" % format(width * length, ","))
    layer = grid.sum(axis=(1, 2))
    placed_layer = matter_grid.sum(axis=(1, 2))
    busiest = max(1, int(placed_layer.max()))
    every = max(1, height // 24)
    for y in range(0, height, every):
        bar = "#" * int(40.0 * placed_layer[y] / busiest)
        print("  y %4d  solid %9s  placed %8s  %s"
              % (y, format(int(layer[y]), ","), format(int(placed_layer[y]), ","), bar))

    print("\npalette, the first twelve:")
    if ids.max(initial=0) < 256:
        counts = np.bincount(ids.astype(np.int64), minlength=256)
        for index in np.argsort(counts)[::-1][:12]:
            if counts[index] == 0:
                break
            print("  %-24s %8s  %5.1f%%"
                  % (NAMES.get(int(index), "id:%d" % index), format(int(counts[index]), ","),
                     100.0 * counts[index] / max(1, cells)))
    else:
        for index in np.argsort(tally)[::-1][:12]:
            print("  %-24s %8s  %5.1f%%"
                  % (NAMES.get(int(used[index]), "id:%d" % used[index]),
                     format(int(tally[index]), ","), 100.0 * tally[index] / max(1, cells)))
    print("\nread in %.1fs" % (time.time() - started))


if __name__ == "__main__":
    main(sys.argv)
