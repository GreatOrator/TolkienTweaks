#!/usr/bin/env python3
"""Finds the ammolite markers a scout placed in a saved world, and groups them.

A block of ammolite is how a location is marked: one where a build's doorway is, and where a road has
to start or end, one there too. Read back out of the save they are just blocks, so this finds them in
the region files and groups the ones that stand within a few blocks of each other - the pairs a
location with two entries is marked with - and reports every group with its own coordinates.

    python tools/ammolite_markers.py --world "run/saves/Scouting World" --dim tolkienmobs:arda

`--compare name:x,y,z` adds a known point (a build's pin, say) and prints the horizontal distance from
each group to it, nearest first, so a marker can be read against the ground it is supposed to mark
without going back into the game.

Only `tolkienmobs:block_ammolite` is looked for, not the ore: ore is generated ground, and a marker is
something a person put down. Anything else ammolite is reported at the end, so a scout who used the
pane or the ore sees that the scan found it and ignored it.
"""

import argparse
import collections
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))

import grab_build as gb

MARKER = "tolkienmobs:block_ammolite"
WATCHED = (MARKER, "tolkienmobs:pane_ammolite", "tolkienmobs:ore_ammolite")


def region_dir(world, dimension):
    """The region folder for a world, whether it is a 26.1 dimension layout or the older one."""
    root = pathlib.Path(world)
    if dimension in ("", "overworld", "minecraft:overworld"):
        for guess in (root / "region", root / "dimensions" / "minecraft" / "overworld" / "region"):
            if guess.is_dir():
                return guess
        raise SystemExit("no region folder in %s" % root)
    namespace, _, name = dimension.partition(":")
    return root / "dimensions" / (namespace or "minecraft") / name / "region"


def decode(states, section_y, base_x, base_z):
    """Every state in one section, as (name, x, y, z) - a palette read a section at a time."""
    palette = [gb.state_of(entry) for entry in (states.get("palette") or [])]
    if not palette:
        return
    data = states.get("data")
    bit = max(4, (len(palette) - 1).bit_length())
    per_long, mask = 64 // bit, (1 << bit) - 1
    packed = [value & 0xFFFFFFFFFFFFFFFF for value in data] if data else []
    for index in range(4096):
        if packed:
            value = (packed[index // per_long] >> ((index % per_long) * bit)) & mask
        else:
            value = 0
        name = palette[value][0] if value < len(palette) else palette[0][0]
        if name == "minecraft:air":
            continue
        yield (name, base_x + (index & 15), section_y * 16 + (index >> 8), base_z + ((index >> 4) & 15))


def scan(world, dimension, wanted):
    """Every block of the watched names in a dimension, and how many of each kind were seen."""
    folder = region_dir(world, dimension)
    if not folder.is_dir():
        raise SystemExit("no region folder at %s" % folder)
    found = []
    seen = collections.Counter()
    files = 0
    for path in sorted(folder.glob("r.*.*.mca")):
        region_x, region_z = int(path.name.split(".")[1]), int(path.name.split(".")[2])
        files += 1
        for local_x, local_z, chunk in gb.read_region(path):
            base_x = chunk.get("xPos", (region_x << 5) + local_x) * 16
            base_z = chunk.get("zPos", (region_z << 5) + local_z) * 16
            for section in chunk.get("sections", []):
                states = section.get("block_states") or {}
                palette = [gb.state_of(entry)[0] for entry in (states.get("palette") or [])]
                if not any(name in wanted for name in palette):
                    continue
                for name, x, y, z in decode(states, section.get("Y", 0), base_x, base_z):
                    if name in wanted:
                        found.append((name, x, y, z))
                        seen[name] += 1
    return found, seen, files


def groups(markers, within):
    """Markers grouped by standing within `within` blocks of another marker of the group, on all axes."""
    left = list(markers)
    out = []
    while left:
        group = [left.pop()]
        grew = True
        while grew:
            grew = False
            for marker in list(left):
                if any(all(abs(marker[axis] - other[axis]) <= within for axis in (1, 2, 3))
                       for other in group):
                    group.append(marker)
                    left.remove(marker)
                    grew = True
        group.sort(key=lambda marker: (marker[1], marker[2], marker[3]))
        out.append(group)
    out.sort(key=lambda group: (group[0][1], group[0][3]))
    return out


def parse_point(text):
    """`name:x,y,z`, which is how a known point is given on the command line."""
    name, _, numbers = text.partition(":")
    parts = numbers.split(",")
    if len(parts) != 3:
        raise SystemExit("a point is name:x,y,z, not %r" % text)
    return name, tuple(int(value) for value in parts)


def main():
    parser = argparse.ArgumentParser(description="Ammolite markers placed in a saved world.")
    parser.add_argument("--world", required=True, help="saved world folder (the one holding level.dat)")
    parser.add_argument("--dim", default="tolkienmobs:arda", help="dimension folder, namespace:name")
    parser.add_argument("--within", type=int, default=6, help="how close two markers count as one group")
    parser.add_argument("--compare", action="append", default=[],
                        help="name:x,y,z to measure every group against; may be repeated")
    args = parser.parse_args()

    found, seen, files = scan(args.world, args.dim, set(WATCHED))
    print("%d region files in %s, %d markers" % (files, args.dim, seen.get(MARKER, 0)))
    for name, count in sorted(seen.items()):
        if name != MARKER:
            print("   also saw %-34s %d, not counted as a marker" % (name, count))

    marks = [entry for entry in found if entry[0] == MARKER]
    if not marks:
        raise SystemExit("no %s anywhere - is this the right world and dimension?" % MARKER)

    known = [parse_point(text) for text in args.compare]
    for number, group in enumerate(groups(marks, args.within), 1):
        middle = (sum(m[1] for m in group) / len(group), sum(m[2] for m in group) / len(group),
                  sum(m[3] for m in group) / len(group))
        print("\n group %d: %d %s, at %.0f, %.0f, %.0f" % (
            number, len(group), "block" if len(group) == 1 else "blocks", middle[0], middle[1], middle[2]))
        for name, x, y, z in group:
            print("   %s at %d, %d, %d" % (name.replace("tolkienmobs:", ""), x, y, z))
        if known:
            away = []
            for name, (px, py, pz) in known:
                near = min(group, key=lambda m: (m[1] - px) ** 2 + (m[3] - pz) ** 2)
                flat = ((near[1] - px) ** 2 + (near[3] - pz) ** 2) ** 0.5
                away.append((flat, name, near[2] - py, near))
            away.sort()
            print("   nearest: " + ", ".join(
                "%s %.0f away (y %+d)" % (name, flat, dy) for flat, name, dy, _ in away[:3]))
    return 0


if __name__ == "__main__":
    sys.exit(main())
