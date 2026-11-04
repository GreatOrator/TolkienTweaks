"""A plan view of what the generator answers for a patch of Arda.

Mirrors the water half of ArdaMap - the graded surface, the drawn river and lake beds, and the
Bruinen branch - so a patch can be looked at without loading a world. The valley's own massif is not
mirrored: this is for asking where water goes, not what the rock does.

    python tools/arda_view.py --x 4250 5150 --z -3900 -3150 --out run/arda_view.png
"""
import argparse

import numpy as np
from PIL import Image

SEA, FLOOR, CROWN = 63, 74, 318
VALLEY_X, VALLEY_Z, VALLEY_HX, VALLEY_HZ = 4628, -3577, 272, 251
VALLEY_RIM, VALLEY_FALL, VALLEY_VALE = 175, 340, 900
RIVER_GRADE, LAKE_GRADE, LAKE_DEPTH = 64, 192, 6
RIVER_REACH, RIVER_DEPTH_MAX = 24, 16
OUTLET_X, OUTLET_Z, FORD_X, FORD_Z = 4900, -3322, 4995, -3311
BRUINEN_WATER, BRUINEN_BED = FLOOR - 2, 7
LEFT, RIGHT, BANK = 5, 7, 1.2
NARROW, FORD_DEPTH, SHALLOWING, SHALLOW = 0.30, 2.0, 0.55, 0.8
STEPS, SNAKE, TURNS = 96, 18.0, 1.5
WEST, NORTH, BLOCKS = -5120, -10240, 8
FINE_WEST, FINE_NORTH, FINE = 4200, -4100, 3
WATER = 0.3

ASSETS = "src/main/resources/assets/tolkienmobs/arda/"


def load(name, grey=True):
    image = Image.open(ASSETS + name)
    return np.array(image.convert("L") if grey else image).astype(np.float64)


HEIGHT = load("height.png")
ROCK = load("mountains.png") / 255.0
RIVERS = load("rivers.png") / 255.0
LAKES = load("lakes.png") / 255.0
FINE_MASK = load("bruinen.png")
PEAK = ROCK.max()
ROWS, COLS = HEIGHT.shape


def _bilinear(raster, block_x, block_z, west=WEST, north=NORTH, scale=BLOCKS):
    across = (block_x - west) / float(scale) - 0.5
    along = (block_z - north) / float(scale) - 0.5
    if across < -0.5 or along < -0.5 or across > COLS - 0.5 or along > ROWS - 0.5:
        return 0.0
    left = int(min(max(0, np.floor(across)), COLS - 1))
    top = int(min(max(0, np.floor(along)), ROWS - 1))
    right, bottom = min(COLS - 1, left + 1), min(ROWS - 1, top + 1)
    fx = min(1.0, max(0.0, across - left))
    fz = min(1.0, max(0.0, along - top))
    first = raster[top, left] * (1 - fx) + raster[top, right] * fx
    second = raster[bottom, left] * (1 - fx) + raster[bottom, right] * fx
    return first * (1 - fz) + second * fz


def river(block_x, block_z):
    column = (block_x - FINE_WEST) // FINE
    row = (block_z - FINE_NORTH) // FINE
    if 0 <= column < FINE_MASK.shape[1] and 0 <= row < FINE_MASK.shape[0]:
        return FINE_MASK[row, column] > 100
    return _bilinear(RIVERS, block_x, block_z) >= WATER


def lake(block_x, block_z):
    return _bilinear(LAKES, block_x, block_z) >= WATER


def smooth(value):
    return value * value * (3.0 - 2.0 * value)


def beyond(x, z):
    return float(np.hypot(max(0.0, abs(x - VALLEY_X) - VALLEY_HX), max(0.0, abs(z - VALLEY_Z) - VALLEY_HZ)))


def vale(x, z, height):
    share = (beyond(x, z) - (VALLEY_RIM + VALLEY_FALL)) / float(VALLEY_VALE)
    if share >= 1.0:
        return height
    weight = 1.0 - smooth(min(1.0, max(0.0, share)))
    return int(round(FLOOR * weight + height * (1.0 - weight)))


def lift(x, z, height):
    rock = _bilinear(ROCK, x, z) / PEAK if PEAK > 0 else 0.0
    if rock <= 0.0 or CROWN <= height:
        return height
    return height + int(round(rock * (CROWN - height)))


def ground_height(x, z):
    mapped = _bilinear(HEIGHT, x, z)
    if mapped <= 0.5:
        return SEA
    return lift(x, z, vale(x, z, SEA + int(round(mapped / 65535.0 * (CROWN - SEA)))))


def grade(x, z, reach):
    total = sum(ground_height(x + dx, z + dz) for dx in (-reach, 0, reach) for dz in (-reach, 0, reach))
    return int(round(total / 9.0))


def drawn_width(x, z):
    for reach in range(1, RIVER_REACH + 1):
        if not (river(x + reach, z) and river(x - reach, z) and river(x, z + reach) and river(x, z - reach)):
            return 2 * reach - 1
    return 2 * RIVER_REACH + 1


def drawn_water(x, z):
    if lake(x, z):
        surface = max(SEA, grade(x, z, LAKE_GRADE))
        return surface, min(ground_height(x, z), surface - LAKE_DEPTH)
    if river(x, z):
        surface = max(SEA, grade(x, z, RIVER_GRADE))
        return surface, min(ground_height(x, z), surface - min(drawn_width(x, z), RIVER_DEPTH_MAX))
    return None


def wander(share):
    return SNAKE * np.sin(share * TURNS * np.pi) * np.sin(np.pi * share)


ALONG_X, ALONG_Z = FORD_X - OUTLET_X, FORD_Z - OUTLET_Z
LENGTH = float(np.hypot(ALONG_X, ALONG_Z))


def course(share):
    return (OUTLET_X + share * ALONG_X - ALONG_Z / LENGTH * wander(share),
            OUTLET_Z + share * ALONG_Z + ALONG_X / LENGTH * wander(share))


def profile():
    levels, lowest = [BRUINEN_WATER], BRUINEN_WATER
    for step in range(1, STEPS + 1):
        x, z = course(step / float(STEPS))
        lowest = min(lowest, ground_height(int(round(x)), int(round(z))))
        levels.append(lowest)
    return levels


LEVELS = profile()


def bruinen(x, z, ground):
    share = ((x - OUTLET_X) * ALONG_X + (z - OUTLET_Z) * ALONG_Z) / (ALONG_X ** 2 + ALONG_Z ** 2)
    if share < 0.0 or share > 1.0:
        return None
    middle_x, middle_z = course(share)
    side = ((x - middle_x) * -ALONG_Z + (z - middle_z) * ALONG_X) / LENGTH
    level = LEVELS[int(min(max(round(share * STEPS), 0), STEPS))]
    shallow = smooth(min(1.0, max(0.0, (share - SHALLOW) / (1.0 - SHALLOW))))
    bank = 1.0 - NARROW * shallow
    out = side - RIGHT * bank if side > 0 else -LEFT * bank - side
    if out <= 0.0:
        reach = min(1.0, abs(side) / max(1.0, RIGHT * bank if side > 0 else LEFT * bank))
        deep = 1.0 - reach * reach
        under = max(FORD_DEPTH, BRUINEN_BED * deep * (1.0 - shallow * SHALLOWING))
        return int(round(level - under)), int(round(level))
    wall = max(level, level + out * BANK)
    return int(round(min(ground, wall))), SEA


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--x", nargs=2, type=int, required=True)
    parser.add_argument("--z", nargs=2, type=int, required=True)
    parser.add_argument("--out", default="run/arda_view.png")
    parser.add_argument("--step", type=int, default=1)
    args = parser.parse_args()

    width = (args.x[1] - args.x[0]) // args.step
    depth = (args.z[1] - args.z[0]) // args.step
    picture = np.zeros((depth, width, 3), dtype=np.uint8)
    water_cells = branch_cells = 0
    for row in range(depth):
        z = args.z[0] + row * args.step
        for column in range(width):
            x = args.x[0] + column * args.step
            ground = ground_height(x, z)
            drawn = drawn_water(x, z)
            wet, surface = None, None
            if drawn is not None:
                wet, surface = drawn
                water_cells += 1
            cut = bruinen(x, z, ground)
            if cut is not None and cut[1] > SEA:
                wet, surface = cut
                branch_cells += 1
            shade = int(np.clip((ground - SEA) * 0.85, 0, 255))
            colour = (shade, int(shade * 0.92), int(shade * 0.75))
            if wet is not None:
                colour = (30, 90, 200) if surface > ground else (20, 60, 150)
            picture[row, column] = colour

    Image.fromarray(picture).save(args.out)
    print("%s  %dx%d  step %d  drawn-water columns %d  branch columns %d"
          % (args.out, width, depth, args.step, water_cells, branch_cells))
    print("branch levels: outlet %d  middle %d  join %d" % (LEVELS[0], LEVELS[STEPS // 2], LEVELS[STEPS]))
    joined = drawn_water(FORD_X, FORD_Z)
    print("drawn river at the join: surface %s bed %s" % joined if joined else "no drawn water at the join")
    print("ground at outlet %d, at join %d" % (ground_height(OUTLET_X, OUTLET_Z), ground_height(FORD_X, FORD_Z)))


if __name__ == "__main__":
    main()
