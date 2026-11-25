"""Answers what the reference masks say about a block, for working on builds and terrain.

The rasters in `Reference/` are the maps Arda is cut from, at their own resolution - about four
blocks to the pixel - so they can be read while a build is being placed instead of only through the
worldgen: what the ground stands at, whether a river or a lake runs through a column, how bright the
mountain mask is, and where the nearest river pixel is.

    python tools/mask_probe.py 4995 -3311
    python tools/mask_probe.py --near 4900 -3322            nearest river within 400 blocks
    python tools/mask_probe.py --near 4900 -3322 --radius 900 --layer river

The calibration is the one worldgen uses (`Reference/work/calibration.json`): block x = origin + blocks
per pixel * pixel x, and the same in z. The height is the same sixteen-bit topography the game reads, read
between the drawing's black at 0 and its white at 318 - the span `ArdaMap` has used since it stopped reading
black as the old sea of sixty-three - and lifted by the mountain mask's own sixty-four blocks the way
`ArdaMap.groundHeight` lifts it, so the number is the ground the drawing puts there. A column the drawing has
no ground for is sea. What neither tool adds is the generator's own raggedness on top, which is a few blocks
either way.
"""

import argparse
import json
import os
import sys

import numpy as np
from PIL import Image

Image.MAX_IMAGE_PIXELS = None

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
REFERENCE = os.path.join(ROOT, "Reference")
LAYERS = {
    "river": "ME_SimpleTopoTerrain_Rivers_albedo.png",
    "lake": "ME_SimpleTopoTerrain_Lakes_albedo.png",
    "mountain": "AreaMasks_westernMountain.png",
    "height": "Final Height.png",
}
# The drawing's two ends, as ArdaMap reads them: black is y0, white is y318, and its waterline is y25.
BLACK_Y = 0
WHITE_Y = 318
SEA_LEVEL = 25
# The climb the mountain mask adds at its brightest, ArdaMap.MASK_GAIN.
MASK_GAIN = 64
NO_GROUND = 0

_cache = {}


def calibration():
    with open(os.path.join(REFERENCE, "work", "calibration.json")) as handle:
        cal = json.load(handle)
    return (cal["blocks_per_8192_pixel"]["x"], cal["origin_block"]["x"],
            cal["blocks_per_8192_pixel"]["z"], cal["origin_block"]["z"])


class Mask:
    """One reference raster, read as numbers."""

    def __init__(self, name):
        self.path = os.path.join(REFERENCE, LAYERS[name])
        self.name = name

    @property
    def raster(self):
        if self.name not in _cache:
            image = Image.open(self.path)
            # The topography is sixteen bits to the pixel and is read as it stands.
            _cache[self.name] = (np.array(image) if image.mode.startswith("I")
                                 else np.array(image.convert("L")))
        return _cache[self.name]

    def pixel(self, block_x, block_z):
        sx, ox, sz, oz = calibration()
        column = int((block_x - ox) / sx)
        row = int((block_z - oz) / sz)
        return row, column

    def at(self, block_x, block_z):
        row, column = self.pixel(block_x, block_z)
        raster = self.raster
        if not (0 <= row < raster.shape[0] and 0 <= column < raster.shape[1]):
            return None
        return int(raster[row, column])

    def near(self, block_x, block_z, radius):
        """The nearest marked column, in blocks: (x, z, distance) or None."""
        sx, ox, sz, oz = calibration()
        raster = self.raster > 100 if self.name in ("river", "lake") else self.raster > 0
        row0, column0 = self.pixel(block_x - radius, block_z - radius)
        row1, column1 = self.pixel(block_x + radius, block_z + radius)
        row0, column0 = max(0, row0), max(0, column0)
        row1 = min(raster.shape[0], row1 + 1)
        column1 = min(raster.shape[1], column1 + 1)
        rows, columns = np.nonzero(raster[row0:row1, column0:column1])
        if rows.size == 0:
            return None
        xs = ox + (columns + column0) * sx
        zs = oz + (rows + row0) * sz
        distance = np.hypot(xs - block_x, zs - block_z)
        best = int(np.argmin(distance))
        return float(xs[best]), float(zs[best]), float(distance[best])


def drawing_height(raw):
    """A topography sample read between the drawing's black and white, before the mask lifts it."""
    if raw is None or raw <= NO_GROUND:
        return None
    return BLACK_Y + int(round(raw / 65535.0 * (WHITE_Y - BLACK_Y)))


def mountain_lift(bright, peak):
    """The blocks the mountain mask adds over a column: its own share of MASK_GAIN."""
    if not bright or peak <= 0:
        return 0
    return int(round(bright / float(peak) * MASK_GAIN))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("x", type=int, nargs="?", help="block x")
    parser.add_argument("z", type=int, nargs="?", help="block z")
    parser.add_argument("--near", nargs=2, type=int, metavar=("X", "Z"),
                        help="find the nearest marked column instead of reading one")
    parser.add_argument("--radius", type=int, default=400, help="how far to look, in blocks")
    parser.add_argument("--layer", choices=sorted(LAYERS), default="river",
                        help="which layer --near searches")
    args = parser.parse_args()

    if not os.path.isdir(REFERENCE):
        print("no Reference folder at " + REFERENCE)
        return 1

    if args.near:
        x, z = args.near
        found = Mask(args.layer).near(x, z, args.radius)
        if found is None:
            print("no %s within %d blocks of %d, %d" % (args.layer, args.radius, x, z))
            return 0
        print("%s nearest %d, %d is %.0f, %.0f - %.0f blocks away"
              % (args.layer, x, z, found[0], found[1], found[2]))
        return 0

    if args.x is None or args.z is None:
        parser.print_help()
        return 1

    print("block %d, %d" % (args.x, args.z))
    drawn = drawing_height(Mask("height").at(args.x, args.z))
    bright = Mask("mountain").at(args.x, args.z) or 0
    lifted = mountain_lift(bright, int(Mask("mountain").raster.max()))
    if drawn is None:
        print("  height   sea - the drawing has no ground under this column")
    else:
        note = ""
        if lifted:
            note = " (%d as the drawing has it, %d lifted by the mask)" % (drawn, lifted)
        if drawn + lifted < SEA_LEVEL:
            note += " - under the sea's %d, which the game holds a land column up to" % SEA_LEVEL
        print("  height   %d blocks%s" % (drawn + lifted, note))
    print("  river    %s" % ("yes" if Mask("river").at(args.x, args.z) > 100 else "no"))
    print("  lake     %s" % ("yes" if Mask("lake").at(args.x, args.z) > 100 else "no"))
    print("  mountain %d/255%s" % (bright, " - white is a crest" if bright > 127 else ""))
    return 0


if __name__ == "__main__":
    sys.exit(main())
