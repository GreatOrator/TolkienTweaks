#!/usr/bin/env python3
"""Writes the goblin hall out as a vanilla structure file, so it can be decorated by hand.

The hall is carved, not placed: it is a room taken out of the hillside, and its shape comes from the
cave's own code - 43 blocks along the way in, 19 across at the widest, 13 tall, the four corners cut at
45 degrees, the ceiling vaulted in from the walls and both ends, and the faces worn a block either way
by the cell hash. That shape is the scaffolding for a decorated hall, so this writes it as a room: its
hollow is air and every other cell of its box is stone, which is the rock the hill is made of there.
Loading the file anywhere therefore builds a hewn hall to furnish, rather than an empty box that would
change nothing in a world and could not be decorated at all.

    python tools/hall_structure.py
    python tools/hall_structure.py --out "run/goblin_hall.nbt"

The file is gzipped NBT in the vanilla structure format, so a structure block reads it: put it in
`<world>/generated/minecraft/structures/` (or a datapack's `data/tolkienmobs/structure/`), put a
structure block at the hall's near end with its corner on the wall, and LOAD. The structure's own axes
are x across the hill, y up, z into the hill; the hall's floor is y = 0, so a structure block set to
"corner" there sits its bottom course on the hall's floor.

The axes deliberately run the same way the world's do. The cut reaches a cell at
`x = doorX - s` and `z = doorZ + a`, so a file written straight from `s` would sit mirrored
against the hall it came from and its worn faces would land on the wrong side of the room; the x cell
is written as `HALL_SIDE + 1 - s` instead, which puts the file's +x along the world's +x. Written that
way, loading it unrotated at the hall's own near corner (x 5619, y 157, z -3535 at the test pin) builds
the room the cut actually made.

The DataVersion is taken from a template the mod already ships, so the file is written at the version
this build speaks rather than one guessed at.
"""
import argparse
import gzip
import pathlib
import struct
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import cave_section as cs
import grab_build as gb

TAG_INT, TAG_STRING, TAG_LIST, TAG_COMPOUND = 3, 8, 9, 10
END = b"\x00"
SHIPPED = pathlib.Path(__file__).resolve().parent.parent / \
    "src/main/resources/data/tolkienmobs/structure/ch2_hall.nbt"
DEFAULT_OUT = pathlib.Path(__file__).resolve().parent.parent / \
    "src/main/resources/data/tolkienmobs/structure/goblin_hall.nbt"


def name(text):
    raw = text.encode("utf-8")
    return struct.pack(">H", len(raw)) + raw


def tag(tag_type, key, payload):
    return bytes([tag_type]) + name(key) + payload


def compound(key, payload):
    return tag(TAG_COMPOUND, key, payload + END)


def bare_compound(payload):
    """A compound as a list element: unlike a named child, it carries no tag id or name."""
    return payload + END


def int_tag(key, value):
    return tag(TAG_INT, key, struct.pack(">i", value))


def string_tag(key, value):
    return tag(TAG_STRING, key, name(value))


def list_tag(key, element, payloads):
    return tag(TAG_LIST, key,
               bytes([element]) + struct.pack(">i", len(payloads)) + b"".join(payloads))


def ints(values):
    return [struct.pack(">i", value) for value in values]


def read_nbt(path):
    raw = path.read_bytes()
    if raw[:2] == b"\x1f\x8b":
        raw = gzip.decompress(raw)
    return gb.read_nbt(raw)


def data_version():
    """The version this build speaks, read from a template that already ships with it."""
    try:
        root = read_nbt(SHIPPED)
    except Exception:
        return None
    if isinstance(root, dict) and "DataVersion" in root:
        return int(root["DataVersion"])
    return None


def build(version):
    """The hall as a room in the structure's own axes: hollow air, stone around it."""
    wide = 2 * (cs.HALL_SIDE + 1) + 1
    deep = cs.HALL_FRONT - cs.HALL_BACK + 1
    blocks = []
    hollow = 0
    for level in range(cs.HALL_HEIGHT):
        for a in range(cs.HALL_BACK, cs.HALL_FRONT + 1):
            for s in range(-(cs.HALL_SIDE + 1), cs.HALL_SIDE + 2):
                open_cell = cs.hall(a, s, level)
                hollow += open_cell
                # x runs the way the world's does (the cut reaches x = doorX - s), so an unrotated load
                # lands the worn faces where the cut leaves them instead of mirroring the room.
                blocks.append(bare_compound(
                    list_tag("pos", TAG_INT, ints(((cs.HALL_SIDE + 1) - s, level, a - cs.HALL_BACK)))
                    + int_tag("state", 0 if open_cell else 1)))
    payload = b"".join([
        int_tag("DataVersion", version),
        list_tag("size", TAG_INT, ints((wide, cs.HALL_HEIGHT, deep))),
        list_tag("palette", TAG_COMPOUND, [bare_compound(string_tag("Name", "minecraft:air")),
                                           bare_compound(string_tag("Name", "minecraft:stone"))]),
        list_tag("blocks", TAG_COMPOUND, blocks),
        list_tag("entities", TAG_COMPOUND, []),   # empty, as the shipped template writes it
    ])
    return compound("", payload), (wide, cs.HALL_HEIGHT, deep), len(blocks), hollow


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--out", default=str(DEFAULT_OUT), help="where to write the structure")
    args = parser.parse_args(argv[1:])

    version = data_version()
    if version is None:
        raise SystemExit("no DataVersion in %s: is the template still there?" % SHIPPED)
    data, size, count, hollow = build(version)
    out = pathlib.Path(args.out)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_bytes(gzip.compress(data))

    # Read it back the way the game would, so a broken file is found here and not in a world.
    root = read_nbt(out)
    blocks = root.get("blocks", [])
    read_size = tuple(int(v) for v in root.get("size", ()))
    palette = [entry.get("Name") for entry in root.get("palette", [])]
    if read_size != size or len(blocks) != count or palette != ["minecraft:air", "minecraft:stone"]:
        raise SystemExit("the file did not read back as written: size %s, %d blocks, palette %s"
                         % (read_size, len(blocks), palette))
    print("%s: %d x %d x %d, %d air and %d stone, DataVersion %d, %d bytes on disk"
          % (out.name, size[0], size[1], size[2], hollow, count - hollow, int(root["DataVersion"]),
             out.stat().st_size))
    print("  x across the hill %d..%d, y up 0..%d, z into the hill 0..%d"
          % (0, size[0] - 1, size[1] - 1, size[2] - 1))
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
