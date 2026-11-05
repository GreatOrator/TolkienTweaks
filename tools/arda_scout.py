"""Scouts a window of the drawn world at a fine scale, so a location can be chosen before it is pinned.

The whole-map renders (`arda_roads.py`, `arda_grid_overlay.py`) are for arguing about countries. This
is for standing on the ground: a few cells at two to four blocks to the pixel, hillshaded from the
sixteen-bit topography, with the river and lake masks painted over it - the painted terrain shows water
as tan, so without the masks a river reads as a beach - a block grid to read coordinates off, and a
relief report for every candidate point handed to it.

    python tools/arda_scout.py                                  the Trollshaws, defaults
    python tools/arda_scout.py --centre 4608 -3072
    python tools/arda_scout.py --centre 4352 -3840 --span 1536 --at 4352,-3840,valley
    python tools/arda_scout.py --at 4864,-3328,camp --at 4995,-3311,ford --footprint 48

Every point in `--at` is reported as the ground a building would have to stand on: the height at its
middle, the lowest and highest ground within the footprint, the drop across it, whether water crosses
it, and how far the nearest river is. A tent wants two blocks of drop, a hall wants six, and a hall on a
bank wants a bank - the numbers are what decide which, so they are printed rather than eyeballed.

The calibration is the one worldgen uses (`Reference/work/calibration.json`), and the heights are
scaled the way `ArdaMap.groundHeight` scales them, so a number here is the number the world stands at.
"""

import argparse
import json
import os

import numpy as np
from PIL import Image, ImageDraw

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
REFERENCE = os.path.join(ROOT, "Reference")

WEST, NORTH, EAST, SOUTH = -5120, -10240, 19456, 8192
CELL = 512
SEA_LEVEL, CROWN, NO_GROUND = 63, 318, 0

Image.MAX_IMAGE_PIXELS = None
LAYERS = {
    "terrain": "ME_Terrain_albedo.png",
    "height": "Final Height.png",
    "river": "ME_SimpleTopoTerrain_Rivers_albedo.png",
    "lake": "ME_SimpleTopoTerrain_Lakes_albedo.png",
    "mountain": "AreaMasks_westernMountain.png",
}

_loaded = {}


def calibration():
    with open(os.path.join(REFERENCE, "work", "calibration.json")) as handle:
        cal = json.load(handle)
    return (cal["blocks_per_8192_pixel"]["x"], cal["origin_block"]["x"],
            cal["blocks_per_8192_pixel"]["z"], cal["origin_block"]["z"])


def raster(name):
    """One reference raster as numbers, read once per run."""
    if name not in _loaded:
        image = Image.open(os.path.join(REFERENCE, LAYERS[name]))
        if name == "terrain":
            _loaded[name] = np.array(image.convert("RGB"))
        elif name == "height":
            _loaded[name] = np.array(image).astype(np.float32)
        else:
            _loaded[name] = np.array(image.convert("L"))
    return _loaded[name]


def game_height(raw):
    """A heightmap sample as the world stands on it, or sea level where the mask has no ground."""
    ground = raw > NO_GROUND
    return np.where(ground, SEA_LEVEL + raw / 65535.0 * (CROWN - SEA_LEVEL), float(SEA_LEVEL))


def sample(box_x0, box_z0, box_x1, box_z1, blocks, scale):
    """The window as arrays: terrain colour, height, water, mountain - one value per output pixel."""
    sx, ox, sz, oz = calibration()
    columns = np.arange(box_x0 + scale // 2, box_x1, scale)
    rows = np.arange(box_z0 + scale // 2, box_z1, scale)
    pixel_columns = np.clip(((columns - ox) / sx).astype(int), 0, raster("terrain").shape[1] - 1)
    pixel_rows = np.clip(((rows - oz) / sz).astype(int), 0, raster("terrain").shape[0] - 1)
    mesh_rows, mesh_columns = np.meshgrid(pixel_rows, pixel_columns, indexing="ij")

    ground = raster("terrain")[mesh_rows, mesh_columns].astype(np.float32)
    height = game_height(raster("height")[mesh_rows, mesh_columns].astype(np.float32))
    water = (raster("river")[mesh_rows, mesh_columns] > 100) | (raster("lake")[mesh_rows, mesh_columns] > 100)
    bright = raster("mountain")[mesh_rows, mesh_columns]
    return ground, height, water, bright


def shade(ground, height, scale):
    """A hillshade from the height mask, lit from the north-west, so ridges read off the flat colour."""
    change_x = np.gradient(height, axis=1) / max(scale, 1)
    change_z = np.gradient(height, axis=0) / max(scale, 1)
    light = 1.0 + 0.30 * (-change_x - change_z)
    return np.clip(ground * np.clip(light, 0.45, 1.45)[..., None], 0, 255)


def paint(ground, height, water, bright, scale):
    image = shade(ground, height, scale)
    # The painted terrain draws water as sand, so the mask is what makes a river read as one.
    image[water] = image[water] * 0.35 + np.array([70.0, 120.0, 185.0]) * 0.65
    snow = bright > 200
    image[snow] = image[snow] * 0.5 + np.array([235.0, 240.0, 245.0]) * 0.5
    return Image.fromarray(image.astype(np.uint8), "RGB")


def grid(pen, box_x0, box_z0, width, height, scale):
    """A 128-block grid with the drawing's own cells picked out, labelled in block coordinates."""
    for x in range(box_x0 - box_x0 % 128, box_x0 + width * scale, 128):
        column = (x - box_x0) // scale
        if column < 0 or column >= width:
            continue
        cell = (x - WEST) % CELL == 0
        pen.line([column, 0, column, height], fill=(255, 255, 255, 90 if cell else 35),
                 width=2 if cell else 1)
        if cell:
            pen.text((column + 2, 2), str(x), fill=(255, 255, 255, 220))
    for z in range(box_z0 - box_z0 % 128, box_z0 + height * scale, 128):
        row = (z - box_z0) // scale
        if row < 0 or row >= height:
            continue
        cell = (z - NORTH) % CELL == 0
        pen.line([0, row, width, row], fill=(255, 255, 255, 90 if cell else 35),
                 width=2 if cell else 1)
        if cell:
            pen.text((2, row + 1), str(z), fill=(255, 255, 255, 220))


def mark(pen, box_x0, box_z0, scale, x, z, label, colour):
    column, row = (x - box_x0) // scale, (z - box_z0) // scale
    pen.rectangle([column - 7, row - 7, column + 7, row + 7], outline=colour, width=2)
    pen.line([column - 12, row, column + 12, row], fill=colour, width=1)
    pen.line([column, row - 12, column, row + 12], fill=colour, width=1)
    pen.text((column + 10, row - 16), "%s %d,%d" % (label, x, z), fill=colour)


def report(x, z, footprint, radius):
    """What a building would find under it at one point: the ground, the drop, the water."""
    half = max(footprint // 2, 2)
    step = max(4, footprint // 16)
    points = [(px, pz) for px in range(x - half, x + half + 1, step)
              for pz in range(z - half, z + half + 1, step)]
    sx, ox, sz, oz = calibration()
    heights, wet, bright = [], 0, 0
    for px, pz in points:
        column = int(np.clip((px - ox) / sx, 0, raster("height").shape[1] - 1))
        row = int(np.clip((pz - oz) / sz, 0, raster("height").shape[0] - 1))
        heights.append(float(game_height(raster("height")[row, column])))
        if raster("river")[row, column] > 100 or raster("lake")[row, column] > 100:
            wet += 1
        bright = max(bright, int(raster("mountain")[row, column]))
    heights = np.array(heights)

    rivers = raster("river") > 100
    rows, columns = np.nonzero(rivers)
    if rows.size:
        xs = ox + columns * sx
        zs = oz + rows * sz
        near = np.hypot(xs - x, zs - z)
        nearest = float(near.min())
    else:
        nearest = float("inf")

    return {
        "height": int(round(float(game_height(
            raster("height")[int(np.clip((z - oz) / sz, 0, raster("height").shape[0] - 1)),
                                 int(np.clip((x - ox) / sx, 0, raster("height").shape[1] - 1))])))),
        "low": int(round(float(heights.min()))),
        "high": int(round(float(heights.max()))),
        "drop": int(round(float(heights.max() - heights.min()))),
        "wet": wet * 100 // len(points),
        "mountain": bright,
        "river": int(round(nearest)),
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--centre", nargs=2, type=int, default=(4608, -3072), metavar=("X", "Z"),
                        help="middle of the window, in blocks (default the Trollshaws)")
    parser.add_argument("--span", type=int, default=3072, help="width of the window, in blocks")
    parser.add_argument("--scale", type=int, default=4, help="blocks to the pixel")
    parser.add_argument("--footprint", type=int, default=32,
                        help="how wide a building the report assumes, in blocks")
    parser.add_argument("--at", action="append", default=[], metavar="X,Z,NAME",
                        help="a candidate to mark and report; repeatable")
    parser.add_argument("--out", default=None, help="where to write the png")
    args = parser.parse_args()

    if not os.path.isdir(REFERENCE):
        print("no Reference folder at " + REFERENCE)
        return 1

    centre_x, centre_z = args.centre
    half = args.span // 2
    box_x0 = max(WEST, centre_x - half - (centre_x - half) % args.scale)
    box_z0 = max(NORTH, centre_z - half - (centre_z - half) % args.scale)
    box_x1 = min(EAST, centre_x + half)
    box_z1 = min(SOUTH, centre_z + half)
    width = (box_x1 - box_x0) // args.scale
    height = (box_z1 - box_z0) // args.scale

    ground, heights, water, bright = sample(box_x0, box_z0, box_x1, box_z1, None, args.scale)
    frame = paint(ground, heights, water, bright, args.scale)
    pen = ImageDraw.Draw(frame, "RGBA")
    grid(pen, box_x0, box_z0, width, height, args.scale)

    colours = [(255, 90, 90), (120, 255, 140), (255, 220, 60), (90, 200, 255),
               (255, 130, 240), (200, 170, 255), (255, 165, 60), (140, 240, 240)]
    print("%-18s %-14s %6s %6s %6s %5s %5s %6s %6s"
          % ("candidate", "block", "mid", "low", "high", "drop", "wet", "mtn", "river"))
    for index, wanted in enumerate(args.at):
        pieces = wanted.split(",")
        if len(pieces) < 3:
            print("  skipping %r - write X,Z,NAME" % wanted)
            continue
        x, z = int(pieces[0]), int(pieces[1])
        name = ",".join(pieces[2:])
        colour = colours[index % len(colours)] + (255,)
        mark(pen, box_x0, box_z0, args.scale, x, z, name, colour)
        found = report(x, z, args.footprint, args.span)
        print("%-18s %-14s %6d %6d %6d %5d %4d%% %6d %6d"
              % (name, "%d,%d" % (x, z), found["height"], found["low"], found["high"],
                 found["drop"], found["wet"], found["mountain"], found["river"]))

    out = args.out or os.path.join(ROOT, "build", "arda-scout.png")
    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    frame.save(out)
    # A copy small enough to look at in a chat window, since a full window is over a megabyte.
    copy = frame.copy()
    copy.thumbnail((1100, 1100), Image.LANCZOS)
    copy.save(os.path.splitext(out)[0] + ".jpg", quality=88)
    print("wrote %s %s at %d blocks to the pixel, window %d..%d by %d..%d"
          % (out, frame.size, args.scale, box_x0, box_x1, box_z0, box_z1))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
