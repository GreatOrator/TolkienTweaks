"""Bakes the lake sheet ids Arda reads to give every drawn lake one water level.

The lakes are drawn at the height of the ground under them, so a sheet laid column by column inherits every
rise and fall of that ground and comes out as a staircase of water. What a lake has instead is one level, so
the game needs to know which sheet a column belongs to in order to look that level up: this writes one byte
per eight-block cell with the sheet's id, and the id, box and middle of each sheet beside it.

Run it from the mod root:  python tools/arda_lake_ids.py
"""

import os

import numpy as np
from PIL import Image
from scipy import ndimage

Image.MAX_IMAGE_PIXELS = None

ASSET = os.path.join("src", "main", "resources", "assets", "tolkienmobs", "arda", "lakes.png")
IDS = os.path.join("src", "main", "resources", "assets", "tolkienmobs", "arda", "lake-ids.png")
BOXES = os.path.join("src", "main", "resources", "assets", "tolkienmobs", "arda", "lake-boxes.txt")
BLOCKS = 8
WEST, NORTH = -5120, -10240
WATER = 0.3


def main():
    mask = np.array(Image.open(ASSET).convert("L")) / 255.0
    sheets, count = ndimage.label(mask > WATER)

    boxes = ndimage.find_objects(sheets)
    rows = []
    for index in range(1, count + 1):
        window = boxes[index - 1]
        rows.append((index,
                     WEST + window[1].start * BLOCKS, NORTH + window[0].start * BLOCKS,
                     WEST + window[1].stop * BLOCKS, NORTH + window[0].stop * BLOCKS))

    Image.fromarray(sheets.astype(np.uint8)).save(IDS)
    with open(BOXES, "w", encoding="utf-8", newline="\n") as out:
        for row in rows:
            out.write("%d %d %d %d %d\n" % row)
    print("wrote %s and %s: %d sheets" % (IDS, BOXES, count))


if __name__ == "__main__":
    main()
