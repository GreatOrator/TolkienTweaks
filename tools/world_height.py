"""The top block, and the top water, of chosen columns in a saved world.

A screenshot says what a place looks like; this says what the generator actually put there, so a
profile measured against the masks can be checked against the blocks that were written. Columns are
given as x,z pairs and are reported in the order asked for, with the highest non-air block and the
highest water block in each column.
"""

import argparse
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))

import grab_build as gb


def region_dir(world, dimension):
    root = pathlib.Path(world)
    if not dimension:
        return root / "region"
    namespace, _, name = dimension.partition(":")
    if not name:
        namespace, name = "minecraft", namespace
    return root / "dimensions" / (namespace or "minecraft") / name / "region"


def tops(world, dimension, columns):
    wanted = {(x, z) for x, z in columns}
    chunks = {(x >> 4, z >> 4) for x, z in wanted}
    top, water = {}, {}
    for path in sorted(region_dir(world, dimension).glob("*.mca")):
        region_x, region_z = int(path.name.split(".")[1]), int(path.name.split(".")[2])
        here = {cell for cell in chunks if (cell[0] >> 5) == region_x and (cell[1] >> 5) == region_z}
        if not here:
            continue
        for local_x, local_z, chunk in gb.read_region(path):
            chunk_x, chunk_z = (region_x << 5) + local_x, (region_z << 5) + local_z
            if (chunk_x, chunk_z) not in here:
                continue
            base_x, base_z = chunk_x * 16, chunk_z * 16
            for section in chunk.get("sections", []):
                states = section.get("block_states") or {}
                palette = [gb.state_of(entry) for entry in (states.get("palette") or [])]
                if not palette:
                    continue
                data = states.get("data")
                bit = max(4, (len(palette) - 1).bit_length())
                per_long, mask = 64 // bit, (1 << bit) - 1
                packed = [value & 0xFFFFFFFFFFFFFFFF for value in data] if data else []
                base_y = section.get("Y", 0) * 16
                for index in range(4096):
                    value = (packed[index // per_long] >> ((index % per_long) * bit)) & mask if packed else 0
                    state = palette[value] if value < len(palette) else palette[0]
                    if state[0] == "minecraft:air":
                        continue
                    column = (base_x + (index & 15), base_z + ((index >> 4) & 15))
                    if column not in wanted:
                        continue
                    y = base_y + (index >> 8)
                    if state[0].endswith("water") and y > water.get(column, (-999, ""))[0]:
                        water[column] = (y, state[0])
                    if y > top.get(column, (-999, ""))[0]:
                        top[column] = (y, state[0])
    return top, water


def main():
    parser = argparse.ArgumentParser(description="Top block and top water of chosen columns.")
    parser.add_argument("--world", required=True, help="saved world folder (the one holding level.dat)")
    parser.add_argument("--dim", default="", help="dimension folder; blank for the overworld")
    parser.add_argument("--cols", required=True,
                        help="columns as 'x,z' pairs, separated by ';' or spaces")
    args = parser.parse_args()
    columns = [tuple(int(v) for v in pair.split(","))
               for pair in args.cols.replace(";", " ").split()]
    top, water = tops(args.world, args.dim, columns)
    for column in columns:
        print("%-22s top %-30s water %s"
              % (column, top.get(column), water.get(column)))


if __name__ == "__main__":
    main()
