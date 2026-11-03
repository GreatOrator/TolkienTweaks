#!/usr/bin/env python3
"""Takes a build out of a saved world (or out of another structure file) and cuts it into
structure templates the mod can place.

Two sources and one output shape.

    # a build standing in a world
    grab_build.py --world "D:/saves/TolkienCraft" --from 1200,60,-880 --to 1210,70,-870 \
                  --out build/gundabad --name gundabad

    # a build already in a structure file, re-cut into smaller pieces
    grab_build.py --nbt somewhere/gloin_camp.nbt --out /tmp/tiles --name gundabad

The output is one .nbt per 48-cube - vanilla's own limit for a structure block, and the size
Sites judges a footprint by - plus a `.parts` table of the offset each piece stands at from the
build's own north-west bottom corner:

    gundabad_0.nbt 0 0 0
    gundabad_1.nbt 48 0 0

`Sites` places a template with its corner at the chosen spot, so a parts table is all a multi-piece
build needs: the offsets are the same kind the valley's own eleven pieces are laid by.

A build standing inside a mountain brings the mountain with it, so `--only` keeps just the blocks named
and turns everything else into air: the rooms come across, the hillside they were cut into does not.

Block entities (chest contents, sign text, spawners) are not carried: a palette of block states is
what a structure file is made of. Everything else - every block, every state, every air gap inside
the build's box - is kept, because air in a template is what carves a fortress out of a hillside.
"""

import argparse
import gzip
import io
import pathlib
import struct
import sys
import zlib
from collections import Counter

TILE = 48


# ---- NBT ---------------------------------------------------------------------------------------

class NbtList(list):
    """A list tag, carrying the type its elements are."""

    def __init__(self, element_type, items=()):
        super().__init__(items)
        self.element_type = element_type


class IntArray(list):
    pass


class LongArray(list):
    pass


class NbtByte(int):
    """A byte tag, kept distinct from an int so a list of them keeps its width."""


class NbtShort(int):
    pass


class NbtLong(int):
    pass


class NbtDouble(float):
    pass


class NbtReader:
    def __init__(self, data):
        self.data = data
        self.at = 0

    def u1(self):
        value = self.data[self.at]
        self.at += 1
        return value

    def u2(self):
        value = struct.unpack_from(">H", self.data, self.at)[0]
        self.at += 2
        return value

    def i4(self):
        value = struct.unpack_from(">i", self.data, self.at)[0]
        self.at += 4
        return value

    def i8(self):
        value = struct.unpack_from(">q", self.data, self.at)[0]
        self.at += 8
        return value

    def name(self):
        length = self.u2()
        value = self.data[self.at:self.at + length].decode("utf-8", "replace")
        self.at += length
        return value

    def tag(self, kind):
        if kind == 1:
            value = struct.unpack_from(">b", self.data, self.at)[0]
            self.at += 1
            return NbtByte(value)
        if kind == 2:
            value = struct.unpack_from(">h", self.data, self.at)[0]
            self.at += 2
            return NbtShort(value)
        if kind == 3:
            return self.i4()
        if kind == 4:
            return NbtLong(self.i8())
        if kind == 5:
            value = struct.unpack_from(">f", self.data, self.at)[0]
            self.at += 4
            return value
        if kind == 6:
            value = struct.unpack_from(">d", self.data, self.at)[0]
            self.at += 8
            return NbtDouble(value)
        if kind == 7:
            length = self.i4()
            value = self.data[self.at:self.at + length]
            self.at += length
            return value
        if kind == 8:
            return self.name()
        if kind == 9:
            element = self.u1()
            length = self.i4()
            return NbtList(element, [self.tag(element) for _ in range(max(0, length))])
        if kind == 10:
            compound = {}
            while True:
                child = self.u1()
                if child == 0:
                    return compound
                key = self.name()
                compound[key] = self.tag(child)
        if kind == 11:
            length = self.i4()
            return IntArray(self.i4() for _ in range(length))
        if kind == 12:
            length = self.i4()
            return LongArray(self.i8() for _ in range(length))
        raise ValueError("unknown tag type %d" % kind)


def read_nbt(data):
    if data[:2] == b"\x1f\x8b":
        data = gzip.decompress(data)
    reader = NbtReader(data)
    kind = reader.u1()
    reader.name()
    return reader.tag(kind)


NBT_TYPE = {dict: 10, NbtList: 9, IntArray: 11, LongArray: 12, str: 8, bytes: 7, int: 3, float: 5,
            NbtByte: 1, NbtShort: 2, NbtLong: 4, NbtDouble: 6}


def write_payload(value):
    if isinstance(value, dict):
        out = []
        for key, child in value.items():
            encoded = key.encode("utf-8")
            out.append(bytes([NBT_TYPE[type(child)]]) + struct.pack(">H", len(encoded)) + encoded)
            out.append(write_payload(child))
        out.append(b"\x00")
        return b"".join(out)
    if isinstance(value, NbtList):
        return (bytes([value.element_type]) + struct.pack(">i", len(value))
                + b"".join(write_payload(x) for x in value))
    if isinstance(value, IntArray):
        return struct.pack(">i", len(value)) + b"".join(struct.pack(">i", x) for x in value)
    if isinstance(value, LongArray):
        return struct.pack(">i", len(value)) + b"".join(struct.pack(">q", x) for x in value)
    if isinstance(value, NbtByte):
        return struct.pack(">b", value)
    if isinstance(value, NbtShort):
        return struct.pack(">h", value)
    if isinstance(value, NbtLong):
        return struct.pack(">q", value)
    if isinstance(value, NbtDouble):
        return struct.pack(">d", value)
    if isinstance(value, str):
        encoded = value.encode("utf-8")
        return struct.pack(">H", len(encoded)) + encoded
    if isinstance(value, bytes):
        return struct.pack(">i", len(value)) + value
    if isinstance(value, int):
        return struct.pack(">i", value)
    if isinstance(value, float):
        return struct.pack(">f", value)
    raise TypeError(type(value))


def write_nbt(root):
    return gzip.compress(b"\x0a\x00\x00" + write_payload(root), 9)


# ---- a build, as blocks ------------------------------------------------------------------------

def state_of(entry):
    """A palette entry as one comparable value: the name, and its properties sorted."""
    name = entry.get("Name", "minecraft:air")
    properties = entry.get("Properties") or {}
    return (name, tuple(sorted(properties.items())))


def blocks_from_template(root):
    palette = [state_of(entry) for entry in root["palette"]]
    blocks = {}
    for block in root["blocks"]:
        x, y, z = block["pos"]
        blocks[(x, y, z)] = palette[block["state"]]
    return blocks, tuple(root["size"])


def read_region(path, window=None):
    """Every chunk in one region file, or only those touching a window, as (chunkX, chunkZ, root)."""
    data = path.read_bytes()
    if len(data) < 8192:
        return
    region_x, region_z = int(path.name.split(".")[1]), int(path.name.split(".")[2])
    for index in range(1024):
        header = struct.unpack_from(">I", data, index * 4)[0]
        if header == 0:
            continue
        local_x, local_z = index % 32, index // 32
        if window:
            if ((region_x << 5) + local_x) * 16 + 15 < window[0] or ((region_x << 5) + local_x) * 16 > window[1]:
                continue
            if ((region_z << 5) + local_z) * 16 + 15 < window[2] or ((region_z << 5) + local_z) * 16 > window[3]:
                continue
        sector = header >> 8
        start = sector * 4096
        if start + 5 > len(data):
            continue
        length = struct.unpack_from(">I", data, start)[0]
        compression = data[start + 4]
        payload = data[start + 5:start + 4 + length]
        try:
            raw = zlib.decompress(payload) if compression == 2 else payload
            root = read_nbt(raw)
        except Exception:
            continue
        # 1.18 flattened the Level wrapper away; older chunks still wear it.
        if "Level" in root:
            root = root["Level"]
        yield index % 32, index // 32, root


def blocks_from_world(world, dimension, low, high):
    """Every block in a box of a saved world, as {pos: state} in world coordinates, and the size."""
    root = pathlib.Path(world)
    if dimension in ("", "overworld", "minecraft:overworld"):
        region_dir = root / "region"
        if not region_dir.is_dir():
            # 26.1 moved the overworld in with the other dimensions.
            region_dir = root / "dimensions" / "minecraft" / "overworld" / "region"
    else:
        namespace, _, name = dimension.partition(":")
        region_dir = root / "dimensions" / (namespace or "minecraft") / name / "region"
    if not region_dir.is_dir():
        raise SystemExit("no region folder at %s" % region_dir)

    paths = []
    for region_x in range((low[0] >> 4) >> 5, ((high[0] >> 4) >> 5) + 1):
        for region_z in range((low[2] >> 4) >> 5, ((high[2] >> 4) >> 5) + 1):
            path = region_dir / ("r.%d.%d.mca" % (region_x, region_z))
            if path.exists():
                paths.append(path)
    if not paths:
        raise SystemExit("no region files cover that box")

    window = (low[0], high[0], low[2], high[2])
    blocks = {}
    chunks = 0
    for region_path in paths:
        for local_x, local_z, chunk in read_region(region_path, window):
            base_x = chunk.get("xPos", (int(region_path.name.split(".")[1]) << 5) + local_x) * 16
            base_z = chunk.get("zPos", (int(region_path.name.split(".")[2]) << 5) + local_z) * 16
            if base_x + 15 < low[0] or base_x > high[0]:
                continue
            if base_z + 15 < low[2] or base_z > high[2]:
                continue
            for section in chunk.get("sections", []):
                states = section.get("block_states")
                if not states:
                    continue
                section_y = section.get("Y", 0)
                palette = [state_of(entry) for entry in states.get("palette", [])]
                if not palette:
                    continue
                data = states.get("data")
                bit_count = max(4, (len(palette) - 1).bit_length())
                per_long = 64 // bit_count
                mask = (1 << bit_count) - 1
                packed = [value & 0xFFFFFFFFFFFFFFFF for value in data] if data else []
                for index in range(4096):
                    x, z, y = index & 15, (index >> 4) & 15, index >> 8
                    world_x, world_y, world_z = base_x + x, section_y * 16 + y, base_z + z
                    if not (low[0] <= world_x <= high[0] and low[1] <= world_y <= high[1]
                            and low[2] <= world_z <= high[2]):
                        continue
                    if packed:
                        value = (packed[index // per_long] >> ((index % per_long) * bit_count)) & mask
                    else:
                        value = 0
                    if value >= len(palette):
                        value = 0
                    blocks[(world_x, world_y, world_z)] = palette[value]
    size = (high[0] - low[0] + 1, high[1] - low[1] + 1, high[2] - low[2] + 1)
    return blocks, size, low


# ---- cutting and writing -----------------------------------------------------------------------

def write_tile(blocks, origin, size, path, keep_air):
    palette = []
    indices = {}
    listed = []
    for y in range(size[1]):
        for z in range(size[2]):
            for x in range(size[0]):
                state = blocks.get((origin[0] + x, origin[1] + y, origin[2] + z))
                if state is None:
                    state = ("minecraft:air", ())
                if state[0] == "minecraft:air" and not keep_air:
                    continue
                if state not in indices:
                    indices[state] = len(palette)
                    entry = {"Name": state[0]}
                    if state[1]:
                        entry["Properties"] = dict(state[1])
                    palette.append(entry)
                listed.append({"pos": IntArray((x, y, z)), "state": indices[state]})
    if not listed:
        return False
    root = {
        "size": IntArray(size),
        "palette": NbtList(10, palette),
        "blocks": NbtList(10, listed),
        "entities": NbtList(10, []),
        "DataVersion": 4790,  # the version this pack's own structures are saved at
    }
    path.write_bytes(write_nbt(root))
    return True


def cut(name, blocks, size, low, out, keep_air):
    out.mkdir(parents=True, exist_ok=True)
    parts = []
    for tile_y in range(0, size[1], TILE):
        for tile_z in range(0, size[2], TILE):
            for tile_x in range(0, size[0], TILE):
                tile = (min(TILE, size[0] - tile_x), min(TILE, size[1] - tile_y),
                        min(TILE, size[2] - tile_z))
                tile_name = "%s_%d_%d_%d" % (name, tile_x // TILE, tile_y // TILE, tile_z // TILE)
                origin = (low[0] + tile_x, low[1] + tile_y, low[2] + tile_z)
                if write_tile(blocks, origin, tile, out / (tile_name + ".nbt"), keep_air):
                    parts.append((tile_name + ".nbt", tile_x, tile_y, tile_z))
    table = out / (name + ".parts")
    table.write_text("".join("%s %d %d %d\n" % part for part in parts), encoding="utf-8")
    return parts


def main():
    parser = argparse.ArgumentParser(description="Cut a world's build into structure templates.")
    parser.add_argument("--world", help="saved world folder (the one holding level.dat)")
    parser.add_argument("--dim", default="", help="dimension folder; blank for the overworld")
    parser.add_argument("--from", dest="low", help="north-west bottom corner, x,y,z")
    parser.add_argument("--to", dest="high", help="south-east top corner, x,y,z")
    parser.add_argument("--nbt", help="a structure file to re-cut instead of a world")
    parser.add_argument("--out", help="folder to write the pieces into")
    parser.add_argument("--name", help="the pieces are named after this")
    parser.add_argument("--no-air", action="store_true", help="drop air, so the build carves nothing")
    parser.add_argument("--only", help="keep only these block names (comma separated); the rest becomes air")
    parser.add_argument("--report", action="store_true", help="count blocks and stop, writing nothing")
    parser.add_argument("--pack", default="src/main/resources/assets/tolkienmobs/blockstates",
                        help="the pack's own blockstates, to report names the mod does not have")
    args = parser.parse_args()
    if not args.report and (not args.out or not args.name):
        parser.error("--out and --name are needed to write pieces")

    if args.nbt:
        root = read_nbt(pathlib.Path(args.nbt).read_bytes())
        blocks, size = blocks_from_template(root)
        low = (0, 0, 0)
    elif args.world and args.low and args.high:
        low = tuple(int(v) for v in args.low.split(","))
        high = tuple(int(v) for v in args.high.split(","))
        if any(lo > hi for lo, hi in zip(low, high)):
            raise SystemExit("--from must be the north-west bottom corner: %s is not below %s"
                             % (args.low, args.high))
        blocks, size, low = blocks_from_world(args.world, args.dim, low, high)
    else:
        parser.error("give --nbt, or --world with --from and --to")

    if args.only:
        allowed = {name.strip() for name in args.only.split(",") if name.strip()}
        blocks = {pos: state for pos, state in blocks.items() if state[0] in allowed}
        if not blocks:
            raise SystemExit("--only matched no blocks")

    counts = Counter(state[0] for state in blocks.values())
    tiles = ((size[0] + TILE - 1) // TILE, (size[1] + TILE - 1) // TILE, (size[2] + TILE - 1) // TILE)
    print("size      %d x %d x %d blocks (%d positions)"
          % (size[0], size[1], size[2], len(blocks)))
    print("tiles     %d x %d x %d = %d at %d-cube"
          % (tiles[0], tiles[1], tiles[2], tiles[0] * tiles[1] * tiles[2], TILE))
    print("kinds     %d distinct block states" % len({state for state in blocks.values()}))
    print("top blocks:")
    for block, count in counts.most_common(12):
        print("   %-44s %d" % (block, count))

    pack = pathlib.Path(args.pack)
    if pack.is_dir():
        missing = set()
        for name in counts:
            if not name.startswith("tolkienmobs:"):
                continue
            short = name.split(":", 1)[1]
            if not (pack / (short + ".json")).exists():
                missing.add(name)
        if missing:
            print("names this pack has no blockstate for (they will load as air):")
            for name in sorted(missing):
                print("   %s  (%d)" % (name, counts[name]))

    if args.report:
        return
    parts = cut(args.name, blocks, size, low, pathlib.Path(args.out), not args.no_air)
    print("wrote %d piece(s) and %s.parts into %s" % (len(parts), args.name, args.out))


if __name__ == "__main__":
    sys.exit(main())
