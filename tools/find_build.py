#!/usr/bin/env python3
"""Finds the box a build occupies in a saved world, so it can be cut.

A build standing alone in a void or flat world is the only thing above the ground, so its own extent
is the answer: this reads every chunk that is there and reports the lowest and highest block in each
direction, how many blocks that box holds and what they are made of.

    python tools/find_build.py --world "run/saves/rivendell_imladris"

The numbers printed are for grab_build.py --from/--to, in its own order: north-west bottom corner
first, south-east top corner second.
"""

import argparse
import pathlib
import sys
from collections import Counter

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import grab_build as gb


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
    parser = argparse.ArgumentParser(description="The box a build occupies in a world.")
    parser.add_argument("--world", required=True, help="saved world folder (the one holding level.dat)")
    parser.add_argument("--dim", default="", help="dimension folder; blank for the overworld")
    parser.add_argument("--top", type=int, default=12, help="how many block names to list")
    args = parser.parse_args()

    low = [None, None, None]
    high = [None, None, None]
    counts = Counter()
    chunks = 0
    for path in sorted(region_dir(args.world, args.dim).glob("*.mca")):
        region_x, region_z = int(path.name.split(".")[1]), int(path.name.split(".")[2])
        for local_x, local_z, chunk in gb.read_region(path):
            chunks += 1
            base_x = chunk.get("xPos", (region_x << 5) + local_x) * 16
            base_z = chunk.get("zPos", (region_z << 5) + local_z) * 16
            for section in chunk.get("sections", []):
                states = section.get("block_states") or {}
                palette = [gb.state_of(entry) for entry in (states.get("palette") or [])]
                if not palette:
                    continue
                data = states.get("data")
                section_y = section.get("Y", 0)
                bit = max(4, (len(palette) - 1).bit_length())
                per_long, mask = 64 // bit, (1 << bit) - 1
                packed = [value & 0xFFFFFFFFFFFFFFFF for value in data] if data else []
                for index in range(4096):
                    if packed:
                        value = (packed[index // per_long] >> ((index % per_long) * bit)) & mask
                    else:
                        value = 0
                    state = palette[value] if value < len(palette) else palette[0]
                    if state[0] == "minecraft:air":
                        continue
                    x = base_x + (index & 15)
                    y = section_y * 16 + (index >> 8)
                    z = base_z + ((index >> 4) & 15)
                    counts[state[0]] += 1
                    for axis, value in enumerate((x, y, z)):
                        if low[axis] is None or value < low[axis]:
                            low[axis] = value
                        if high[axis] is None or value > high[axis]:
                            high[axis] = value

    if low[0] is None:
        raise SystemExit("no blocks at all - the world is empty in that dimension")
    print("chunks read   %d" % chunks)
    print("blocks        %d" % sum(counts.values()))
    print("box           %d x %d x %d" % (high[0] - low[0] + 1, high[1] - low[1] + 1,
                                          high[2] - low[2] + 1))
    print("corner        %d,%d,%d" % tuple(low))
    print("opposite      %d,%d,%d" % tuple(high))
    print("tiles at 48   %d x %d x %d" % (-(-(high[0] - low[0] + 1) // 48),
                                          -(-(high[1] - low[1] + 1) // 48),
                                          -(-(high[2] - low[2] + 1) // 48)))
    print("top blocks:")
    for name, count in counts.most_common(args.top):
        print("   %-44s %d" % (name, count))
    return 0


if __name__ == "__main__":
    sys.exit(main())
