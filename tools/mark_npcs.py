#!/usr/bin/env python3
"""Reads the NPC marks out of a saved world, and keeps them out of a cut.

An NPC mark is a block standing where a body is meant to go - ammolite by default, matched on its name
so the pack's own variants all count. The mark is not part of the build: this reports where each one is,
relative to the build's own corner, and takes the block out of the templates so no piece ships a marker.

    # where the marks are, before anything is cut
    python tools/mark_npcs.py --world "run/saves/Rivendell_Imladris" --corner 555,0,-706

    # and again from the pieces, writing the marks out beside them
    python tools/mark_npcs.py --cut run/rivendell_art --corner 555,0,-706

The mark goes in the cell a body's feet occupy - the block a body stands in - and that cell is the spot
reported. The `.npcs` file is one line a body: `x y z`, in blocks from the corner the cut was taken from,
with the same sink the pieces are placed at, so the two tables are in one frame.
"""

import argparse
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import grab_build as gb


def region_dir(world, dimension):
    root = pathlib.Path(world)
    if dimension in ("", "overworld", "minecraft:overworld"):
        for guess in (root / "region", root / "dimensions" / "minecraft" / "overworld" / "region"):
            if guess.is_dir():
                return guess
        return root / "region"
    namespace, _, name = dimension.partition(":")
    return root / "dimensions" / (namespace or "minecraft") / name / "region"


def section_blocks(states, mark):
    """Every cell of one section holding a mark, as positions inside the section."""
    palette = [gb.state_of(entry) for entry in (states.get("palette") or [])]
    if not any(mark in state[0] for state in palette):
        return []
    wanted = {index for index, state in enumerate(palette) if mark in state[0]}
    data = states.get("data")
    if not data:
        # One state for the whole section: either all of it or none of it.
        return list(range(4096)) if wanted else []
    bit = max(4, (len(palette) - 1).bit_length())
    per, mask = 64 // bit, (1 << bit) - 1
    packed = [value & 0xFFFFFFFFFFFFFFFF for value in data]
    found = []
    for index in range(4096):
        value = (packed[index // per] >> ((index % per) * bit)) & mask
        if value in wanted:
            found.append(index)
    return found


def marks_in_world(world, dimension, mark):
    """Where the marks stand, in world coordinates, and which block names were used."""
    found, names = [], set()
    for path in sorted(region_dir(world, dimension).glob("*.mca")):
        region_x, region_z = int(path.name.split(".")[1]), int(path.name.split(".")[2])
        for local_x, local_z, chunk in gb.read_region(path):
            base_x = region_x * 32 + local_x * 16
            base_z = region_z * 32 + local_z * 16
            for section in (chunk.get("sections") or []):
                states = section.get("block_states") or {}
                hits = section_blocks(states, mark)
                if not hits:
                    continue
                palette = [gb.state_of(entry) for entry in (states.get("palette") or [])]
                names.update(state[0] for state in palette if mark in state[0])
                section_y = section.get("Y", 0)
                for index in hits:
                    found.append((base_x + (index & 15),
                                  section_y * 16 + (index >> 8),
                                  base_z + ((index >> 4) & 15)))
    return sorted(found), sorted(names)


def marks_in_pieces(folder, mark):
    """Where the marks stand inside a set of pieces, as offsets from the pieces' own corner."""
    found, names, stripped = [], set(), 0
    for path in sorted(folder.glob("*.nbt")):
        root = gb.read_nbt(path.read_bytes())
        palette = [gb.state_of(entry) for entry in root["palette"]]
        keep = []
        removed = []
        for block in root["blocks"]:
            state = palette[block["state"]]
            if mark in state[0]:
                names.add(state[0])
                removed.append(tuple(block["pos"]))
            else:
                keep.append(block)
        if not removed:
            continue
        # The piece's own offset is in the parts table; the rest is local to the piece.
        suffix = path.stem.split("_", 1)[1]
        tile_x, tile_y, tile_z = (int(part) for part in suffix.split("_"))
        for x, y, z in removed:
            found.append((tile_x * 48 + x, tile_y * 48 + y, tile_z * 48 + z))
        root["blocks"] = gb.NbtList(10, keep)
        # And the palette without the states nothing points at any more, so a stripped piece carries no
        # trace of the mark at all - an entry no block uses would still be written into the template.
        slot, used = {}, []
        for block in keep:
            if block["state"] not in slot:
                slot[block["state"]] = len(used)
                used.append(palette[block["state"]])
            block["state"] = slot[block["state"]]
        root["palette"] = gb.NbtList(10, [
            {"Name": state[0], **({"Properties": dict(state[1])} if state[1] else {})}
            for state in used])
        path.write_bytes(gb.write_nbt(root))
        stripped += len(removed)
    return sorted(found), sorted(names), stripped


def main():
    parser = argparse.ArgumentParser(description="Read NPC marks out of a world or a cut.")
    parser.add_argument("--world", help="saved world folder (the one holding level.dat)")
    parser.add_argument("--dim", default="", help="dimension folder; blank for the overworld")
    parser.add_argument("--cut", help="a folder of pieces to read the marks out of, and strip them")
    parser.add_argument("--corner", default="", help="the cut's corner, x,y,z, to report offsets from")
    parser.add_argument("--sink", type=int, default=0,
                        help="how far the pieces are placed below the world they were cut from")
    # The plain block rather than the bare word: the pack also has ammolite ores and a pane, and a
    # mark that matched the word would read an ore in the rock as a body waiting to be stood up.
    parser.add_argument("--mark", default="block_ammolite",
                        help="what marks a spot, matched on the block name")
    parser.add_argument("--out", help="where to write the .npcs table, if anywhere")
    args = parser.parse_args()
    if not args.world and not args.cut:
        parser.error("give --world, or --cut with the pieces already made")

    corner = None
    marks, names, stripped = [], [], 0
    if args.cut:
        marks, names, stripped = marks_in_pieces(pathlib.Path(args.cut), args.mark)
    else:
        marks, names = marks_in_world(args.world, args.dim, args.mark)

    if args.corner:
        corner = tuple(int(part) for part in args.corner.split(","))
        marks = [(x - corner[0], y - corner[1], z - corner[2]) for x, y, z in marks]
    # The same sink the pieces are placed at, so a body stands where the ground under it went.
    if args.sink:
        marks = [(x, y - args.sink, z) for x, y, z in marks]

    print("  marks found %d%s" % (len(marks), "" if not names else " (%s)" % ", ".join(names)))
    if stripped:
        print("  blocks taken out of the pieces: %d" % stripped)
    for x, y, z in marks[:12]:
        if corner:
            print("     %5d %4d %5d   (%d %d %d in the world)"
                  % (x, y, z, x + corner[0], y + corner[1], z + corner[2]))
        else:
            print("     %5d %4d %5d" % (x, y, z))
    if len(marks) > 12:
        print("     ... and %d more" % (len(marks) - 12))
    if args.out and marks:
        pathlib.Path(args.out).write_text("".join("%d %d %d\n" % m for m in marks), encoding="utf-8")
        print("  written to %s" % args.out)
    return 0


if __name__ == "__main__":
    sys.exit(main())
