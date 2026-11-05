"""Renders the proposed road routes over the reference terrain, to be looked at before any ground is moved.

Usage, from the mod root:  python tools/arda_roads.py [out.png]

The routes are cell-space waypoints from `docs/arda-roads.md`: `column, row` in the drawing's own grid,
interpolated for drawing only. A cell's middle is `(column - 10) * 512 + 256`, `(row - 20) * 512 + 256` in
blocks, which is `ArdaMap.centreOf`. The background is `Reference/ME_Terrain_albedo.png` cropped through
`Reference/work/calibration.json`, the same calibration worldgen uses, so the routes can be seen against the
ground they would cross.

Pass a path to write somewhere other than `build/arda-roads-proposal.png`.
"""

import json
import os
import sys

from PIL import Image, ImageDraw

REFERENCE = "Reference"
WEST, NORTH, CELL = -5120, -10240, 512
ORIGIN_COLUMN, ORIGIN_ROW = 10, 20
BLOCKS = 24

# name, colour, waypoints as (column, row)
ROUTES = (
    ("East-West Road", (255, 70, 70), ((12, 20), (13, 19), (15, 18), (16, 16), (17, 14), (18, 12))),
    ("Greenway", (120, 255, 120), ((12, 20), (12, 22), (13, 23), (14, 24))),
    ("Old Forest Road", (255, 220, 60), ((24, 10), (26, 9), (29, 9), (32, 9), (34, 8), (35, 6))),
    ("Iron Hills spur", (255, 160, 40), ((35, 6), (37, 7), (40, 7))),
    ("East March road", (255, 80, 220), ((35, 6), (36, 11), (36, 16), (35, 20), (34, 23))),
    ("Harad Road", (80, 200, 255), ((25, 28), (26, 30), (27, 31), (27, 33))),
    ("Anduin road", (190, 160, 255), ((23, 15), (23, 19), (25, 21), (26, 24))),
)

# the ground the world already names, from Places.CANON_CELL and Sites.CANON_CELL
PINS = (
    ("Rivendell", 18, 12), ("Thorenhad", 19, 13), ("Barachen", 18, 14), ("the Ford", 17, 13),
    ("wood ruin", 19, 14), ("wild tent", 17, 13), ("cave a", 17, 15), ("cave b", 18, 16),
    ("cave c", 19, 16), ("Gloin's camp", 24, 10),
)


def calibration():
    cal = json.load(open(os.path.join(REFERENCE, "work", "calibration.json")))
    return (cal["blocks_per_8192_pixel"]["x"], cal["origin_block"]["x"],
            cal["blocks_per_8192_pixel"]["z"], cal["origin_block"]["z"])


def world(column, row):
    """The middle of a drawn cell, in blocks - ArdaMap.centreOf."""
    return (column - ORIGIN_COLUMN) * CELL + CELL // 2, (row - ORIGIN_ROW) * CELL + CELL // 2


def point(block_x, block_z):
    return (block_x - WEST) // BLOCKS, (block_z - NORTH) // BLOCKS


def main():
    out = sys.argv[1] if len(sys.argv) > 1 else os.path.join("build", "arda-roads-proposal.png")
    sx, ox, sz, oz = calibration()
    source = Image.open(os.path.join(REFERENCE, "ME_Terrain_albedo.png")).convert("RGB")
    pixels = source.load()
    width = (19456 - WEST) // BLOCKS
    height = (8192 - NORTH) // BLOCKS
    frame = Image.new("RGB", (width, height))
    target = frame.load()
    for row in range(height):
        reference_row = int((NORTH + row * BLOCKS - oz) / sz)
        for column in range(width):
            reference_column = int((WEST + column * BLOCKS - ox) / sx)
            if 0 <= reference_column < source.width and 0 <= reference_row < source.height:
                target[column, row] = pixels[reference_column, reference_row]

    pen = ImageDraw.Draw(frame, "RGBA")
    for column in range(0, 49, 2):
        x = (column * CELL) // BLOCKS
        pen.line([x, 0, x, height], fill=(255, 255, 255, 50))
        pen.text((x + 1, 1), str(column), fill=(255, 255, 255, 210))
    for row in range(0, 37, 2):
        y = (row * CELL) // BLOCKS
        pen.line([0, y, width, y], fill=(255, 255, 255, 50))
        pen.text((1, y + 1), str(row), fill=(255, 255, 255, 210))

    for name, colour, waypoints in ROUTES:
        path = [point(*world(column, row)) for column, row in waypoints]
        pen.line(path, fill=colour + (235,), width=5, joint="curve")
        for x, y in path:
            pen.ellipse([x - 4, y - 4, x + 4, y + 4], fill=colour + (255,))
        pen.text((path[0][0] + 8, path[0][1] - 14), name, fill=colour + (255,))
        print("%-16s %s" % (name, ' -> '.join('%d/%d = %d,%d' % (c, r, *world(c, r)) for c, r in waypoints)))

    for label, column, row in PINS:
        x, y = point(*world(column, row))
        pen.rectangle([x - 5, y - 5, x + 5, y + 5], outline=(255, 255, 255, 255), width=2)
        pen.text((x + 8, y + 2), label, fill=(255, 255, 255, 235))

    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    frame.save(out)
    print("wrote %s %s" % (out, frame.size))


if __name__ == "__main__":
    main()
