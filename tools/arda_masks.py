"""Bakes the reference masks into the rasters Arda reads at worldgen.

The masks are 8192 by 8192 and the world frame is a crop of that canvas; calibration.json
records the transform (about four blocks per pixel, origin -7546, -13561). This script crops
each mask to the world frame at a fixed number of blocks per pixel and writes it as a PNG next
to the mod's other assets, which is the only form the game can read.

Four layers come out of it: the rivers and lakes masks as one bit each, the mountain mask as
eight-bit brightness (white is a crest), and the topography as unsigned sixteen-bit.

Run it from the mod root:  python tools/arda_masks.py
"""

import json
import os

import numpy as np
from PIL import Image

Image.MAX_IMAGE_PIXELS = None

REFERENCE = "reference"
OUT = os.path.join("src", "main", "resources", "assets", "tolkienmobs", "arda")
BLOCKS_PER_PIXEL = 16
WORLD_X0, WORLD_X1 = -5120, 19456
WORLD_Z0, WORLD_Z1 = -10240, 8192


def calibration():
    cal = json.load(open(os.path.join(REFERENCE, "work", "calibration.json")))
    return (cal["blocks_per_8192_pixel"]["x"], cal["origin_block"]["x"],
            cal["blocks_per_8192_pixel"]["z"], cal["origin_block"]["z"])


def crop(path, sx, ox, sz, oz, threshold=None):
    """The world frame of one 8192 mask, at BLOCKS_PER_PIXEL blocks to the pixel."""
    width = (WORLD_X1 - WORLD_X0) // BLOCKS_PER_PIXEL
    height = (WORLD_Z1 - WORLD_Z0) // BLOCKS_PER_PIXEL
    image = Image.open(path)
    # The topography is sixteen bits to the pixel and is read as it stands; the masks are grey.
    source = np.array(image) if image.mode.startswith("I") else np.array(image.convert("L"))
    xs = np.arange(width) * BLOCKS_PER_PIXEL + WORLD_X0
    zs = np.arange(height) * BLOCKS_PER_PIXEL + WORLD_Z0
    columns = np.clip(((xs - ox) / sx).astype(int), 0, source.shape[1] - 1)
    rows = np.clip(((zs - oz) / sz).astype(int), 0, source.shape[0] - 1)
    out = source[np.ix_(rows, columns)]
    if threshold is not None:
        out = np.where(out > threshold, 255, 0).astype(np.uint8)
    return out


def main():
    os.makedirs(OUT, exist_ok=True)
    sx, ox, sz, oz = calibration()
    jobs = [
        ("ME_SimpleTopoTerrain_Rivers_albedo.png", "rivers.png", 100, False),
        ("ME_SimpleTopoTerrain_Lakes_albedo.png", "lakes.png", 100, False),
        ("AreaMasks_westernMountain.png", "mountains.png", None, True),
        ("Final Height.png", "height.png", None, True),
    ]
    for source, name, threshold, graded in jobs:
        raster = crop(os.path.join(REFERENCE, source), sx, ox, sz, oz, threshold)
        image = Image.fromarray(raster) if graded else Image.fromarray((raster > 0).astype(np.uint8) * 255)
        path = os.path.join(OUT, name)
        image.save(path)
        marked = (raster > 0).mean()
        print("%-13s %dx%d %-6s %5.1f%% marked  %7.1f kB" % (
            name, image.size[0], image.size[1], image.mode, 100 * marked,
            os.path.getsize(path) / 1024.0))


if __name__ == "__main__":
    main()
