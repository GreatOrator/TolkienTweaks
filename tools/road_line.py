#!/usr/bin/env python3
"""Reads a laid road back out of a saved world, as waypoints for `RoadPaint.LINES`.

A road laid by `/tmroad` is blocks like any other: the road's own centre line is not written down anywhere, but
it is still in the save as the surface the pass put there. This finds those columns, groups them into the pieces a
crossing splits them into, and turns each piece into waypoints - x, z, and the height of the surface - which is
exactly the shape a generated line is written in.

    python tools/road_line.py --world "run/saves/Scouting World" --dim tolkienmobs:arda \
        --box 4950,40,-3150,5300,180,-3020 --from 5200,-3034 --to 5006,-3075

`--histogram` prints what the box is made of instead, which is how the surface names below were found. `--step` is
how far apart the waypoints are; a road that bends sharply or switchbacks wants them closer together. `--wild` adds
the wild band's gravel and coarse dirt, which the terrain has everywhere, for a stretch paved too thinly to hold
together by itself.

A run that reports dozens of small pieces has found terrain and not a road: the world it was pointed at does not have
that road in it any more, and no amount of scanning will recover a line that was never saved.

The surface is the pass's own mix - `docs/arda-roads.md` settles the three bands - and the columns are found by their
highest block of one of those names. A tree that grew over the road does not hide it: the road block is still the
highest block of that name in the column.
"""

import argparse
import collections
import math
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))

import grab_build as gb

# The pass's own surface: the paving of the town and transition bands, the wild band's path, and the slab a step is
# laid with. None of these occur naturally in Arda's drawn ground, so a column with one on top is one the pass wrote.
SURFACE = (
    "tolkienmobs:stone_path",
    "minecraft:dirt_path",
    "minecraft:stone_bricks",
    "minecraft:mossy_stone_bricks",
    "minecraft:mossy_cobblestone",
    "minecraft:cobblestone",
    "minecraft:cobblestone_slab",
)

# The wild band's loose ground. Gravel and coarse dirt are all over the drawn terrain, so they cannot find a road by
# themselves - `--wild` adds them only where a paved stretch is too thin to hold together without them.
WILD = ("minecraft:gravel", "minecraft:coarse_dirt")

# The bridge's own timbers, which is what carries the road over the water: a crossing is a piece, not a surface.
TIMBER = ("minecraft:oak_planks", "minecraft:oak_slab", "minecraft:oak_log", "minecraft:oak_fence",
          "minecraft:oak_stairs", "minecraft:jungle_slab")


def box(text):
    parts = [int(value) for value in text.split(",")]
    if len(parts) != 6:
        raise SystemExit("a box is x0,y0,z0,x1,y1,z1")
    return (tuple(parts[:3]), tuple(parts[3:]))


def columns(blocks, names):
    """The highest block of one of `names` in every column: (x, z) -> y."""
    tops = {}
    for (x, y, z), state in blocks.items():
        if state[0] not in names:
            continue
        if y > tops.get((x, z), -10 ** 9):
            tops[(x, z)] = y
    return tops


def pieces(tops, step=1):
    """Columns grouped into touching pieces: a road, or each side of a crossing, is one piece."""
    left = set(tops)
    found = []
    while left:
        start = left.pop()
        piece = {start}
        queue = collections.deque([start])
        while queue:
            x, z = queue.popleft()
            for dx in (-1, 0, 1):
                for dz in (-1, 0, 1):
                    if dx == 0 and dz == 0:
                        continue
                    beside = (x + dx, z + dz)
                    if beside not in left:
                        continue
                    if abs(tops[beside] - tops[(x, z)]) > step:
                        continue
                    left.remove(beside)
                    piece.add(beside)
                    queue.append(beside)
        found.append(piece)
    found.sort(key=len, reverse=True)
    return found


def nearest(piece, x, z):
    return min(piece, key=lambda column: (column[0] - x) ** 2 + (column[1] - z) ** 2)


def walk(piece, first, last):
    """The shortest path through a piece from one column to the other, as a list of columns."""
    seen = {first: None}
    queue = collections.deque([first])
    while queue:
        x, z = queue.popleft()
        if (x, z) == last:
            break
        for dx in (-1, 0, 1):
            for dz in (-1, 0, 1):
                if dx == 0 and dz == 0:
                    continue
                beside = (x + dx, z + dz)
                if beside not in piece or beside in seen:
                    continue
                seen[beside] = (x, z)
                queue.append(beside)
    if last not in seen:
        return []
    path = [last]
    while path[-1] != first:
        path.append(seen[path[-1]])
    path.reverse()
    return path


def resample(path, tops, step):
    """Every `step` blocks along a path, with the surface's height there, smoothed so a one-block jitter is not a lip."""
    chosen = [path[0]]
    walked = 0.0
    for index in range(1, len(path)):
        walked += math.dist(path[index - 1], path[index])
        if walked >= step:
            chosen.append(path[index])
            walked = 0.0
    if chosen[-1] != path[-1]:
        chosen.append(path[-1])

    heights = [tops[column] for column in chosen]
    smoothed = []
    for index in range(len(heights)):
        window = heights[max(0, index - 2):index + 3]
        smoothed.append(sorted(window)[len(window) // 2])
    # The ends are what a marker walked, so they keep their own height rather than the window's.
    smoothed[0], smoothed[-1] = heights[0], heights[-1]
    return [(x, y, z) for (x, z), y in zip(chosen, smoothed)]


def line(name, points, half, per_row=4):
    """A `RoadPaint.Line` as Java, wrapped a few waypoints to the row."""
    rows = []
    for index in range(0, len(points), per_row):
        chunk = points[index:index + per_row]
        rows.append(", ".join("new int[]{%d, %d, %d}" % point for point in chunk))
    body = (",\n" + " " * 16).join("List.of(" + rows[0] + ("," if len(rows) > 1 else ""))
    if len(rows) > 1:
        body = "List.of(" + rows[0] + ",\n" + " " * 16 + (",\n" + " " * 16).join(rows[1:])
    return '            new Line("%s", %d,\n                    %s))' % (name, half, body)


def main():
    parser = argparse.ArgumentParser(description="A laid road, read back as waypoints.")
    parser.add_argument("--world", required=True)
    parser.add_argument("--dim", default="tolkienmobs:arda")
    parser.add_argument("--box", required=True, help="x0,y0,z0,x1,y1,z1 to look in")
    parser.add_argument("--from", dest="start", help="x,z the road starts near (the build's end)")
    parser.add_argument("--to", dest="end", help="x,z the road ends near (the far or bank end)")
    parser.add_argument("--step", type=int, default=8, help="blocks between waypoints")
    parser.add_argument("--half", type=int, default=3, help="the half-width written on the line")
    parser.add_argument("--min", type=int, default=24, help="ignore pieces smaller than this many columns")
    parser.add_argument("--histogram", action="store_true", help="print what the box is made of instead")
    parser.add_argument("--wild", action="store_true",
                        help="also key on the wild band's gravel and coarse dirt, which the terrain shares")
    args = parser.parse_args()

    low, high = box(args.box)
    blocks, size, origin = gb.blocks_from_world(args.world, args.dim, low, high)
    if not blocks:
        raise SystemExit("nothing in that box")

    if args.histogram:
        print("%d blocks in %s %s" % (len(blocks), args.world, args.box))
        for name, count in collections.Counter(state[0] for state in blocks.values()).most_common(30):
            print("   %-42s %d" % (name, count))
        return 0

    road = columns(blocks, set(SURFACE) | (set(WILD) if args.wild else set()))
    timber = columns(blocks, set(TIMBER))
    print("%d road columns, %d timber columns" % (len(road), len(timber)))
    found = [piece for piece in pieces(road) if len(piece) >= args.min]
    print("%d pieces of at least %d columns: %s" % (
        len(found), args.min, ", ".join(str(len(piece)) for piece in found)))

    start = tuple(int(value) for value in args.start.split(",")) if args.start else None
    end = tuple(int(value) for value in args.end.split(",")) if args.end else None
    for number, piece in enumerate(found, 1):
        xs = [column[0] for column in piece]
        zs = [column[1] for column in piece]
        ys = [road[column] for column in piece]
        print("\npiece %d: %d columns, x %d..%d, z %d..%d, y %d..%d" % (
            number, len(piece), min(xs), max(xs), min(zs), max(zs), min(ys), max(ys)))

        first, last = None, None
        if start:
            first = nearest(piece, *start)
        if end:
            last = nearest(piece, *end)
        if first and last and first != last:
            path = walk(piece, first, last)
            if not path:
                print("   no path through this piece between %s and %s" % (start, end))
                continue
            points = resample(path, road, args.step)
            print("   %d waypoints, %.0f blocks of road, first %s, last %s" % (
                len(points), len(path), points[0], points[-1]))
            print(line("the valley approach", points, args.half) if number == 1 else
                  line("the ford's far side", points, args.half))
            continue

        print("   from {%d, %d}, to {%d, %d}, heights %d..%d" % (
            min(xs), min(zs), max(xs), max(zs), min(ys), max(ys)))
    return 0


if __name__ == "__main__":
    sys.exit(main())
