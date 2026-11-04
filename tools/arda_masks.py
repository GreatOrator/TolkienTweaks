"""Bakes the reference masks into the rasters Arda reads at worldgen.

The masks are 8192 by 8192 and the world frame is a crop of that canvas; calibration.json
records the transform (about four blocks per pixel, origin -7546, -13561). This script crops
each mask to the world frame at a fixed number of blocks per pixel and writes it as a PNG next
to the mod's other assets, which is the only form the game can read.

Four layers come out of it: the rivers and lakes masks as cover - how much of each cell the
drawing marks, so a bank falls where the drawing put it instead of on the cell grid - the
mountain mask as eight-bit brightness (white is a crest), and the topography as unsigned
sixteen-bit.

Run it from the mod root:  python tools/arda_masks.py
"""

import json
import os

import numpy as np
from PIL import Image

Image.MAX_IMAGE_PIXELS = None

REFERENCE = "Reference"
OUT = os.path.join("src", "main", "resources", "assets", "tolkienmobs", "arda")
BLOCKS_PER_PIXEL = 8
WORLD_X0, WORLD_X1 = -5120, 19456
WORLD_Z0, WORLD_Z1 = -10240, 8192

# Cover a cell needs before the game calls it water, in the 0-255 the mask is written in.
WATER = 77

# The river the hand-built city runs out to. A twelve-block channel cannot be placed on the
# eight-block pixels the whole frame is baked at, so the Bruinen's own corner is baked a second
# time, finer, on its own: three blocks to the pixel, which is close to the reference's own 3.95.
BRUINEN_X0, BRUINEN_X1 = 4200, 5400
BRUINEN_Z0, BRUINEN_Z1 = -4100, -2900
BRUINEN_BLOCKS = 3


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


def coverage(path, sx, ox, sz, oz, threshold=100, subsamples=4, chunk=128):
    """How much of each cell the mask marks, 0-255.

    A cell is eight blocks across and the mask's own pixel is about four, so a river crossing a cell
    marks half of it and one running along its edge marks a quarter. Taken as one sample, nearest,
    every one of those becomes a whole cell and the bank steps eight blocks at a time; kept as a
    share, the bank can land where the drawing actually put it.
    """
    width = (WORLD_X1 - WORLD_X0) // BLOCKS_PER_PIXEL
    height = (WORLD_Z1 - WORLD_Z0) // BLOCKS_PER_PIXEL
    source = np.array(Image.open(path).convert("L"))
    step = BLOCKS_PER_PIXEL / float(subsamples)
    offsets = (np.arange(subsamples) + 0.5) * step
    xs = (np.arange(width) * BLOCKS_PER_PIXEL + WORLD_X0)[:, None] + offsets[None, :]
    columns = np.clip(((xs - ox) / sx).astype(int), 0, source.shape[1] - 1)
    out = np.zeros((height, width), np.uint8)
    total = float(subsamples * subsamples)
    for row0 in range(0, height, chunk):
        row1 = min(height, row0 + chunk)
        zs = (np.arange(row0, row1) * BLOCKS_PER_PIXEL + WORLD_Z0)[:, None] + offsets[None, :]
        rows = np.clip(((zs - oz) / sz).astype(int), 0, source.shape[0] - 1)
        covered = np.zeros((row1 - row0, width), np.uint16)
        for a in range(subsamples):
            for b in range(subsamples):
                covered += source[np.ix_(rows[:, a], columns[:, b])] > threshold
        out[row0:row1] = np.round(covered * (255.0 / total)).astype(np.uint8)
    return out


def window(path, sx, ox, sz, oz, x0, x1, z0, z1, blocks, threshold):
    """One corner of a mask, at a finer blocks-to-the-pixel than the whole frame is baked at."""
    source = np.array(Image.open(path).convert("L"))
    width = (x1 - x0) // blocks
    height = (z1 - z0) // blocks
    xs = np.arange(width) * blocks + x0
    zs = np.arange(height) * blocks + z0
    columns = np.clip(((xs - ox) / sx).astype(int), 0, source.shape[1] - 1)
    rows = np.clip(((zs - oz) / sz).astype(int), 0, source.shape[0] - 1)
    out = source[np.ix_(rows, columns)]
    return np.where(out > threshold, 255, 0).astype(np.uint8)


def report(name, image, path, raster):
    extra = ""
    if raster.dtype == np.uint8 and image.mode == "L":
        extra = " water %5.1f%%" % (100.0 * (raster >= WATER).mean())
    print("%-13s %dx%d %-6s %5.1f%% marked%s  %7.1f kB" % (
        name, image.size[0], image.size[1], image.mode, 100.0 * (raster > 0).mean(), extra,
        os.path.getsize(path) / 1024.0))


def main():
    os.makedirs(OUT, exist_ok=True)
    sx, ox, sz, oz = calibration()
    for source, name in (("ME_SimpleTopoTerrain_Rivers_albedo.png", "rivers.png"),
                         ("ME_SimpleTopoTerrain_Lakes_albedo.png", "lakes.png")):
        raster = coverage(os.path.join(REFERENCE, source), sx, ox, sz, oz)
        image = Image.fromarray(raster)
        path = os.path.join(OUT, name)
        image.save(path)
        report(name, image, path, raster)
    for source, name in (("AreaMasks_westernMountain.png", "mountains.png"),
                         ("Final Height.png", "height.png")):
        raster = crop(os.path.join(REFERENCE, source), sx, ox, sz, oz)
        image = Image.fromarray(raster)
        path = os.path.join(OUT, name)
        image.save(path)
        print("%-13s %dx%d %-6s %7.1f kB" % (name, image.size[0], image.size[1], image.mode,
                                             os.path.getsize(path) / 1024.0))
    raster = window(os.path.join(REFERENCE, "ME_SimpleTopoTerrain_Rivers_albedo.png"), sx, ox, sz, oz,
                    BRUINEN_X0, BRUINEN_X1, BRUINEN_Z0, BRUINEN_Z1, BRUINEN_BLOCKS, 100)
    image = Image.fromarray((raster > 0).astype(np.uint8) * 255)
    path = os.path.join(OUT, "bruinen.png")
    image.save(path)
    print("%-13s %dx%d %-6s %5.1f%% marked  %7.1f kB" % (
        "bruinen.png", image.size[0], image.size[1], image.mode, 100 * (raster > 0).mean(),
        os.path.getsize(path) / 1024.0))


if __name__ == "__main__":
    main()
