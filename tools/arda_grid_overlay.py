"""Renders the drawn countries' cell boxes over the reference terrain, to measure the drawing against it.

Usage, from the mod root:  python tools/arda_grid_overlay.py [out.png]

The reference is `Reference/ME_Terrain_albedo.png` cropped to the world frame through the calibration
worldgen uses (`Reference/work/calibration.json`), so a box drawn here is where the country's cells fall on
the same ground the heights and the water were cut from. Pass a path to write somewhere other than
`build/arda-grid-overlay.png`.
"""

import json
import os
import sys

from PIL import Image, ImageDraw

REFERENCE = "Reference"
WEST, NORTH, CELL = -5120, -10240, 512
BLOCKS = 24

# The countries east of the Misty Mountains, as `ArdaMap.draw()` fills them: columns and rows, inclusive.
BOXES = (
    ("MIRKWOOD 27-33", 27, 5, 33, 18, (255, 60, 60)),
    ("spooky 30-32", 30, 16, 32, 18, (255, 120, 120)),
    ("gladden 25-26", 25, 6, 26, 20, (150, 255, 150)),
    ("IRON HILLS 35-41", 35, 6, 41, 7, (255, 160, 40)),
    ("RHUN 34-43", 34, 5, 43, 22, (255, 235, 60)),
    ("ROHAN 23-29", 23, 20, 29, 24, (60, 220, 255)),
    ("dagorlad 30-36", 30, 23, 36, 25, (200, 160, 255)),
    ("MORDOR 37-44", 37, 24, 44, 26, (255, 60, 255)),
    ("NURN 37-43", 37, 27, 43, 29, (80, 255, 80)),
    ("waste 44-47", 44, 4, 47, 35, (200, 200, 200)),
)


def calibration():
    cal = json.load(open(os.path.join(REFERENCE, "work", "calibration.json")))
    return (cal["blocks_per_8192_pixel"]["x"], cal["origin_block"]["x"],
            cal["blocks_per_8192_pixel"]["z"], cal["origin_block"]["z"])


def main():
    out = sys.argv[1] if len(sys.argv) > 1 else os.path.join("build", "arda-grid-overlay.png")
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
    for label, from_column, from_row, to_column, to_row, colour in BOXES:
        box = [(from_column * CELL) // BLOCKS, (from_row * CELL) // BLOCKS,
               ((to_column + 1) * CELL) // BLOCKS, ((to_row + 1) * CELL) // BLOCKS]
        pen.rectangle(box, outline=colour + (255,), width=3)
        pen.text((box[0] + 4, box[1] + 2), label, fill=colour + (255,))
    for column in range(0, 49, 2):
        x = (column * CELL) // BLOCKS
        pen.line([x, 0, x, height], fill=(255, 255, 255, 60))
        pen.text((x + 1, 1), str(column), fill=(255, 255, 255, 220))
    for row in range(0, 37, 2):
        y = (row * CELL) // BLOCKS
        pen.line([0, y, width, y], fill=(255, 255, 255, 60))
        pen.text((1, y + 1), str(row), fill=(255, 255, 255, 220))

    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    frame.save(out)
    print("wrote %s %s" % (out, frame.size))


if __name__ == "__main__":
    main()
