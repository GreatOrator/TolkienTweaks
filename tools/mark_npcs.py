#!/usr/bin/env python3
"""Reads the NPC marks out of a saved world, and keeps them out of a cut.

An NPC mark is a block standing where a body is meant to go - ammolite by default, matched on its name
so the pack's own variants all count. The mark is not part of the build: this reports where each one is,
relative to the build's own corner, and takes the block out of the templates. A stripped mark is put
back as the stone around it rather than left as air, because the cell is somewhere a body stands and a
hole there is a hole in the floor.

    # where the marks are, before anything is cut
    python tools/mark_npcs.py --world "run/saves/Rivendell_Imladris" --corner 555,0,-706 --sink 12

    # and again from the pieces, writing the marks out beside them
    python tools/mark_npcs.py --cut run/rivendell_art --corner 555,0,-706 --sink 12 \
                              --out run/rivendell_art/rivendell.npcs

The mark goes in the cell a body's feet occupy - the block a body stands in, the air just above the
floor - and that cell is the spot reported. Nothing is buried and nothing is dug out: the cell is air in
the build already. A mark left inside the floor block instead is read with `--mark-in-floor`, which puts
the body on top of it, so both habits survive. The `.npcs` file is one line a body: `x y z`, in blocks from the corner the cut was taken from,
with the same sink the pieces are placed at, so the two tables are in one frame.

A mark that is not a body's is taken out of the report with `--not`, given the coordinates the report
prints. That is what a probe block is: a mark put down to measure something, which should be cleaned up
like the rest and not stand a guard where it was.
"""

import argparse
import collections
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import grab_build as gb

NEIGHBOURS = ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, -1, 0), (0, 0, 1), (0, 0, -1))


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
            # Region offset and local chunk are both in chunks; the whole sum is what becomes blocks.
            base_x = (region_x * 32 + local_x) * 16
            base_z = (region_z * 32 + local_z) * 16
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


def put_back(at, pos, mark):
    """The stone this cell should be: the commonest of the six around it that is not a mark itself."""
    x, y, z = pos
    around = collections.Counter()
    for dx, dy, dz in NEIGHBOURS:
        state = at.get((x + dx, y + dy, z + dz))
        if state and mark not in state[0] and state[0] != "minecraft:air":
            around[state] += 1
    if around:
        return around.most_common(1)[0][0]
    # Nothing solid beside it: the floor under it is the next best answer, and air the last.
    under = at.get((x, y - 1, z))
    if under and mark not in under[0]:
        return under
    return None


def marks_in_pieces(folder, mark):
    """Where the marks stand inside a set of pieces, and the pieces with them put back as stone."""
    found, names, stripped = [], set(), 0
    for path in sorted(folder.glob("*.nbt")):
        root = gb.read_nbt(path.read_bytes())
        palette = [gb.state_of(entry) for entry in root["palette"]]
        at = {}
        for block in root["blocks"]:
            at[tuple(block["pos"])] = palette[block["state"]]
        hits = [pos for pos, state in at.items() if mark in state[0]]
        if not hits:
            continue
        for state in palette:
            if mark in state[0]:
                names.add(state[0])
        for pos in hits:
            at[pos] = put_back(at, pos, mark)
        keep = [{"pos": gb.NbtList(3, pos), "state": state}
                for pos, state in sorted(at.items()) if state is not None]
        # Rebuild the palette from what is left: a state no block uses is still written into a template.
        slot, used = {}, []
        for block in keep:
            state = at[tuple(block["pos"])]
            if state not in slot:
                slot[state] = len(used)
                used.append(state)
            block["state"] = slot[state]
        root["blocks"] = gb.NbtList(10, keep)
        root["palette"] = gb.NbtList(10, [
            {"Name": state[0], **({"Properties": dict(state[1])} if state[1] else {})}
            for state in used])
        suffix = path.stem.split("_", 1)[1]
        tile_x, tile_y, tile_z = (int(part) for part in suffix.split("_"))
        for x, y, z in hits:
            found.append((tile_x * 48 + x, tile_y * 48 + y, tile_z * 48 + z))
        path.write_bytes(gb.write_nbt(root))
        stripped += len(hits)
    return sorted(found), sorted(names), stripped


def main():
    parser = argparse.ArgumentParser(description="Read NPC marks out of a world or a cut.")
    parser.add_argument("--world", help="saved world folder (the one holding level.dat)")
    parser.add_argument("--dim", default="", help="dimension folder; blank for the overworld")
    parser.add_argument("--cut", help="a folder of pieces to read the marks out of, and strip them")
    parser.add_argument("--corner", default="", help="the cut's corner, x,y,z, to report offsets from")
    parser.add_argument("--sink", type=int, default=0,
                        help="how far the pieces are placed below the world they were cut from")
    # The plain block rather than the bare word: the pack also has ammolite ores and a pane, and a mark
    # that matched the word would read an ore in the rock as a body waiting to be stood up.
    parser.add_argument("--mark", default="block_ammolite",
                        help="what marks a spot, matched on the block name")
    # For marks put into the floor rather than into the air above it: the body stands on the marked block.
    parser.add_argument("--mark-in-floor", dest="in_floor", action="store_true",
                        help="the mark was placed in the floor, so the body goes on top of it")
    # `not` is a keyword, so the flag and the name it lands on are not the same word.
    parser.add_argument("--not", dest="exclude", default="",
                        help="marks that are not bodies, as x,y,z;x,y,z in the frame reported")
    parser.add_argument("--label", action="append", default=[], metavar="X,Y,Z=NAME",
                        help="name a spot, given in the frame the marks are found in; repeatable")
    parser.add_argument("--out", help="where to write the .npcs table, if anywhere")
    args = parser.parse_args()
    if not args.world and not args.cut:
        parser.error("give --world, or --cut with the pieces already made")

    # A name belongs to the spot, so it is looked up before the frame is changed under it.
    labels = {}
    for entry in args.label:
        place, _, name = entry.partition("=")
        if not name.strip():
            parser.error("a label reads x,y,z=name, and '%s' has no name" % entry)
        labels[tuple(int(v) for v in place.split(","))] = name.strip()

    corner = None
    marks, names, stripped = [], [], 0
    if args.cut:
        marks, names, stripped = marks_in_pieces(pathlib.Path(args.cut), args.mark)
    else:
        marks, names = marks_in_world(args.world, args.dim, args.mark)

    rows = [(mark, labels.get(mark, "")) for mark in marks]

    if args.in_floor:
        rows = [((x, y + 1, z), name) for (x, y, z), name in rows]
    if args.corner:
        corner = tuple(int(part) for part in args.corner.split(","))
        rows = [((x - corner[0], y - corner[1], z - corner[2]), name) for (x, y, z), name in rows]
    # The same sink the pieces are placed at, so a body stands where the ground under it went.
    if args.sink:
        rows = [((x, y - args.sink, z), name) for (x, y, z), name in rows]

    excluded = set()
    if args.exclude:
        for part in args.exclude.split(";"):
            if part.strip():
                excluded.add(tuple(int(v) for v in part.split(",")))
    if excluded:
        kept = [row for row in rows if row[0] not in excluded]
        print("  left out as not a body: %d" % (len(rows) - len(kept)))
        rows = kept

    # Those not named by hand are handed out in the order they are read in - west to east, then
    # south to north - so the same marks always come back in the same order.
    rows.sort(key=lambda row: (row[0][0], row[0][2], row[0][1]))

    print("  marks found %d%s" % (len(rows), "" if not names else " (%s)" % ", ".join(names)))
    if stripped:
        print("  put back as the stone around them: %d" % stripped)
    for (x, y, z), name in rows[:12]:
        where = ("(%d %d %d in the world)" % (x + corner[0], y + corner[1], z + corner[2])
                 if corner else "")
        print("     %-10s %5d %4d %5d   %s" % (name or "-", x, y, z, where))
    if len(rows) > 12:
        print("     ... and %d more" % (len(rows) - 12))
    if args.out and rows:
        pathlib.Path(args.out).write_text(
            "".join("%s %d %d %d\n" % (name or "-", x, y, z) for (x, y, z), name in rows),
            encoding="utf-8")
        print("  written to %s" % args.out)
    return 0


if __name__ == "__main__":
    sys.exit(main())
