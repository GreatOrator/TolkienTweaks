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
LAKE_DEPTH = 6
RIVER_REACH, RIVER_DEPTH_MAX = 24, 16
OUTLET_X, OUTLET_Z, FORD_X, FORD_Z = 4352, -3530, 4995, -3311
BRUINEN_WATER, BRUINEN_BED = FLOOR + 8, 7
LEFT, RIGHT, BANK = 5, 7, 1.2
NARROW, FORD_DEPTH, SHALLOWING, SHALLOW = 0.30, 2.0, 0.55, 0.92
STEPS, SNAKE, TURNS = 256, 18.0, 1.5
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


def drawn_width(x, z):
    for reach in range(1, RIVER_REACH + 1):
        if not (river(x + reach, z) and river(x - reach, z) and river(x, z + reach) and river(x, z - reach)):
            return 2 * reach - 1
    return 2 * RIVER_REACH + 1


def drawn_water(x, z):
    wet = lake(x, z) or river(x, z)
    if not wet:
        return None
    ground = ground_height(x, z)
    surface = max(SEA, ground)
    deep = LAKE_DEPTH if lake(x, z) else min(drawn_width(x, z), RIVER_DEPTH_MAX)
    return surface, min(ground, surface - deep)


COURSE = ((4352, -3530), (4272, -3530), (4272, -3256), (4952, -3256), (4995, -3311))
LEG = [float(np.hypot(b[0] - a[0], b[1] - a[1])) for a, b in zip(COURSE, COURSE[1:])]
START = [float(sum(LEG[:index])) for index in range(len(LEG))]
TOTAL = float(sum(LEG))


def wander(share):
    return SNAKE * np.sin(share * TURNS * np.pi) * np.sin(np.pi * share)


def leg_of(share):
    along = min(1.0, max(0.0, share)) * TOTAL
    index = 0
    while index < len(LEG) - 1 and along >= START[index] + LEG[index]:
        index += 1
    return index


def into(share):
    index = leg_of(share)
    return min(1.0, max(0.0, (min(1.0, max(0.0, share)) * TOTAL - START[index]) / LEG[index]))


def course(share):
    index, at = leg_of(share), into(share)
    along_x = COURSE[index + 1][0] - COURSE[index][0]
    along_z = COURSE[index + 1][1] - COURSE[index][1]
    dir_x, dir_z = along_x / LEG[index], along_z / LEG[index]
    wobble = wander(share) * np.sin(np.pi * at)
    return (COURSE[index][0] + at * along_x - dir_z * wobble,
            COURSE[index][1] + at * along_z + dir_x * wobble)


def project(x, z):
    near, share, side, before, after = float("inf"), 0.0, 0.0, False, False
    for index, (start, end) in enumerate(zip(COURSE, COURSE[1:])):
        along_x, along_z = end[0] - start[0], end[1] - start[1]
        at = ((x - start[0]) * along_x + (z - start[1]) * along_z) / (along_x ** 2 + along_z ** 2)
        foot = min(1.0, max(0.0, at))
        foot_x, foot_z = start[0] + foot * along_x, start[1] + foot * along_z
        away = float(np.hypot(x - foot_x, z - foot_z))
        if away < near:
            near = away
            share = (START[index] + foot * LEG[index]) / TOTAL
            side = ((x - foot_x) * -along_z + (z - foot_z) * along_x) / LEG[index]
            before = index == 0 and at < 0.0
            after = index == len(LEG) - 1 and at > 1.0
    return share, side, near, before, after


def profile():
    levels, lowest = [BRUINEN_WATER], BRUINEN_WATER
    for step in range(1, STEPS + 1):
        x, z = course(step / float(STEPS))
        lowest = min(lowest, ground_height(int(round(x)), int(round(z))))
        levels.append(lowest)
    return levels


LEVELS = profile()


def bruinen(x, z, ground):
    share, _, _, before, after = project(x, z)
    if before or after:
        return None
    middle_x, middle_z = course(share)
    index = leg_of(share)
    along_x = COURSE[index + 1][0] - COURSE[index][0]
    along_z = COURSE[index + 1][1] - COURSE[index][1]
    side = ((x - middle_x) * -along_z + (z - middle_z) * along_x) / LEG[index]
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
