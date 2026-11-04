#!/usr/bin/env python3
"""Cuts a build out of a saved world into 48-cube pieces, one chunk at a time.

`grab_build.py` holds the whole box in memory before it writes anything, which is fine for a house and
not fine for a city: 554 x 244 x 512 is sixty-nine million cells. This reads each chunk once, decodes it
with numpy, and drops every block straight into the piece that owns it, so memory is the size of the
blocks rather than of the box.

    python tools/cut_world.py --world run/saves/Rivendell_Imladris \\
                              --from 555,12,-706 --to 1108,255,-195 \\
                              --out run/rivendell_art_new --name rivendell

The pieces are named and placed exactly as `grab_build.py` names them - `name_x_y_z.nbt` with a
`name.parts` table of the offset each piece stands at from the box's own north-west bottom corner - so
`/tmcity` reads the folder the same way. Nothing is culled: a cell that holds a block is written out.
"""

import argparse
import pathlib
import sys
from array import array

import numpy as np

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import grab_build as gb

TILE = 48
CELLS = 17  # 48 cubed is 110,592, which fits under 2^17


def region_dir(world, dimension):
    root = pathlib.Path(world)
    if dimension in ("", "overworld", "minecraft:overworld"):
        for guess in (root / "region", root / "dimensions" / "minecraft" / "overworld" / "region"):
            if guess.is_dir():
                return guess
        raise SystemExit("no region folder in %s" % root)
    namespace, _, name = dimension.partition(":")
    return root / "dimensions" / (namespace or "minecraft") / name / "region"


def main():
    parser = argparse.ArgumentParser(description="Stream a world's build into 48-cube pieces.")
    parser.add_argument("--world", required=True, help="saved world folder (the one holding level.dat)")
    parser.add_argument("--dim", default="", help="dimension folder; blank for the overworld")
    parser.add_argument("--from", dest="low", required=True, help="north-west bottom corner, x,y,z")
    parser.add_argument("--to", dest="high", required=True, help="south-east top corner, x,y,z")
    parser.add_argument("--out", required=True, help="folder to write the pieces into")
    parser.add_argument("--name", default="build", help="what the pieces are called")
    parser.add_argument("--tile", type=int, default=TILE, help="piece width; vanilla's own limit is 48")
    args = parser.parse_args()

    low = tuple(int(v) for v in args.low.split(","))
    high = tuple(int(v) for v in args.high.split(","))
    if any(lo > hi for lo, hi in zip(low, high)):
        raise SystemExit("--from must be the north-west bottom corner")
    size = tuple(high[i] - low[i] + 1 for i in range(3))
    tile = args.tile
    counts = tuple(-(-size[i] // tile) for i in range(3))
    out = pathlib.Path(args.out)
    out.mkdir(parents=True, exist_ok=True)

    # One table of block states for the whole build, and one array of packed blocks per piece: the
    # state's number high, the cell's number inside the piece low.
    states, numbers = [], {}
    pieces = {}

    print("box    %d x %d x %d at %s" % (size[0], size[1], size[2], (low,)))
    print("pieces %d x %d x %d = %d at %d-cube"
          % (counts[0], counts[1], counts[2], counts[0] * counts[1] * counts[2], tile))

    window = (low[0], high[0], low[2], high[2])
    chunks, read = 0, 0
    index = np.arange(4096, dtype=np.uint64)
    for path in sorted(region_dir(args.world, args.dim).glob("*.mca")):
        region_x, region_z = int(path.name.split(".")[1]), int(path.name.split(".")[2])
        for local_x, local_z, chunk in gb.read_region(path, window):
            base_x = chunk.get("xPos", (region_x << 5) + local_x) * 16
            base_z = chunk.get("zPos", (region_z << 5) + local_z) * 16
            if base_x + 15 < low[0] or base_x > high[0] or base_z + 15 < low[2] or base_z > high[2]:
                continue
            chunks += 1
            for section in chunk.get("sections", []):
                block_states = section.get("block_states") or {}
                palette = block_states.get("palette") or []
                if not palette:
                    continue
                names = [gb.state_of(entry) for entry in palette]
                if all(name[0] == "minecraft:air" for name in names):
                    continue
                section_y = section.get("Y", 0) * 16
                if section_y + 15 < low[1] or section_y > high[1]:
                    continue
                data = block_states.get("data")
                if data:
                    packed = np.array([value & 0xFFFFFFFFFFFFFFFF for value in data], dtype=np.uint64)
                    bit = max(4, (len(names) - 1).bit_length())
                    per_long = 64 // bit
                    mask = np.uint64((1 << bit) - 1)
                    values = (packed[index // per_long]
                              >> ((index % per_long) * np.uint64(bit))) & mask
                else:
                    values = np.zeros(4096, dtype=np.uint64)
                slots = values.astype(np.int64)
                air = np.array([name[0] == "minecraft:air" for name in names], dtype=bool)
                x = (index & np.uint64(15)).astype(np.int64) + base_x
                z = ((index >> np.uint64(4)) & np.uint64(15)).astype(np.int64) + base_z
                y = (index >> np.uint64(8)).astype(np.int64) + section_y
                keep = (~air[slots]
                        & (x >= low[0]) & (x <= high[0])
                        & (y >= low[1]) & (y <= high[1])
                        & (z >= low[2]) & (z <= high[2]))
                chosen = np.nonzero(keep)[0]
                if not len(chosen):
                    continue
                x, y, z, slots = x[chosen], y[chosen], z[chosen], slots[chosen]
                read += len(chosen)
                for name in names:
                    if name[0] != "minecraft:air" and name not in numbers:
                        numbers[name] = len(states)
                        states.append(name)
                table = np.array([numbers.get(name, 0) for name in names], dtype=np.int64)
                state_number = table[slots]
                tile_x = (x - low[0]) // tile
                tile_y = (y - low[1]) // tile
                tile_z = (z - low[2]) // tile
                key = (tile_x * counts[1] + tile_y) * counts[2] + tile_z
                for one in np.unique(key):
                    one = int(one)
                    here = key == one
                    combined, tz = divmod(one, counts[2])
                    tx, ty = divmod(combined, counts[1])
                    cell_x = x[here] - low[0] - tx * tile
                    cell_y = y[here] - low[1] - ty * tile
                    cell_z = z[here] - low[2] - tz * tile
                    cell = cell_x + cell_z * tile + cell_y * tile * tile
                    packed = ((state_number[here].astype(np.uint64) << np.uint64(CELLS))
                              | cell.astype(np.uint64))
                    pieces.setdefault((tx, ty, tz), array("Q")).extend(packed.tolist())
            if chunks % 200 == 0:
                print("   %d chunks, %d blocks" % (chunks, read))

    print("read   %d chunks, %d blocks in %d states" % (chunks, read, len(states)))
    mask = (1 << CELLS) - 1
    parts, written = [], 0
    for (tx, ty, tz) in sorted(pieces):
        ox, oy, oz = low[0] + tx * tile, low[1] + ty * tile, low[2] + tz * tile
        dims = (min(tile, size[0] - tx * tile), min(tile, size[1] - ty * tile),
                min(tile, size[2] - tz * tile))
        blocks = {}
        for packed in pieces[(tx, ty, tz)]:
            packed = int(packed)
            cell = packed & mask
            state = states[packed >> CELLS]
            blocks[(ox + cell % tile, oy + cell // (tile * tile),
                    oz + (cell // tile) % tile)] = state
        name = "%s_%d_%d_%d" % (args.name, tx, ty, tz)
        if gb.write_tile(blocks, (ox, oy, oz), dims, out / (name + ".nbt"), False):
            parts.append((name + ".nbt", tx * tile, ty * tile, tz * tile,
                          dims[0], dims[2], dims[1]))
            written += len(blocks)
    (out / (args.name + ".parts")).write_text(
        "".join("%s %d %d %d\n" % part for part in parts), encoding="utf-8")
    print("wrote  %d pieces, %d blocks, into %s" % (len(parts), written, out))
    return 0


if __name__ == "__main__":
    sys.exit(main())
