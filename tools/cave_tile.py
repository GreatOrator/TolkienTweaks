#!/usr/bin/env python3
"""Carve a set 4 (cave) geomorph panel into a dwarven tile for the inner keep.

Set 5 gave the hold its six rooms, authored as squared masonry from the drawings because a hand-drawn
wall is thinner than a block. Set 4 is the cave dungeon, and the inner keep is the level where the hold
stops being squared: its panels are taken as drawn - rock where the sheet has ink, open where it does
not - and given the same 61-square box and the same quarter-point doorways as the hold above, so the
stair's landing opens into them and the two tiles join each other.

This is the one place in the hold that is a pixel-for-block build, and deliberately: a cave is rubble
and outcrop, and a cave drawn in squared masonry is a ruin, not a cave.

    python cave_tile.py a b        panels r1c1 .. r3c2, written as ch2_inner_a, ch2_inner_b
"""
import pathlib
import sys

import numpy as np
from PIL import Image

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import fortress_build as fb
import geom_extract as ge
import grab_build as gb

SHEET = pathlib.Path(__file__).resolve().parent / "geomorphs" / "geomorph-set-4-smaller.jpg"
W = ge.TILE                 # 61, the box every tile in the hold shares
HEIGHT = 12                 # the inner keep's own storey, roofed by the hold's floor
CAVE = 7                    # headroom in the caves, low the way a dwarven undercroft is low
DOOR_H = 5
DOORS = fb.DOORS

ROCK = "minecraft:deepslate"
FLOOR = "tolkienmobs:angainor"
FLOOR_ALT = "tolkienmobs:angainor_bricks"
CHISEL = "tolkienmobs:chiseled_angainor_bricks"
SMOOTH = "tolkienmobs:smooth_angainor"
CRACKED = "tolkienmobs:cracked_angainor_bricks"
COBBLED = "tolkienmobs:cobbled_angainor"


def panel_masks():
    """Every panel of the sheet as a 61-square rock/floor mask, by its key."""
    image = Image.open(SHEET).convert("L")
    pixels = np.asarray(image, dtype=np.uint8)
    high, wide = pixels.shape
    ink = pixels < 200
    cols = ge.spans(ink.mean(axis=0), wide)
    rows = ge.spans(ink.mean(axis=1), high)
    if len(cols) != 2 or len(rows) != 3:
        raise SystemExit("the sheet is not two panels across and three down: %s x %s" % (rows, cols))
    masks = {}
    for ri, (y0, y1) in enumerate(rows, start=1):
        for ci, (x0, x1) in enumerate(cols, start=1):
            sub = ink[y0:y1 + 1, x0:x1 + 1]
            ys = np.where(sub.any(axis=1))[0]
            xs = np.where(sub.any(axis=0))[0]
            box = (x0 + int(xs[0]), y0 + int(ys[0]), x0 + int(xs[-1]) + 1, y0 + int(ys[-1]) + 1)
            density = ge.cell_density(ink, box)
            masks["r%dc%d" % (ri, ci)] = density >= ge.otsu(density)
    return masks


def clean(open_mask, radius=2, need=0.55):
    """Open cells smoothed: the rubble drawn inside a room is ink too, and it must not become rock.

    A hand-drawn cave carries hatching, stones and stalagmites inside its own rooms, and read at one
    block per cell every one of them is a pillar. A cell is open when most of the cells around it are.
    """
    high, wide = open_mask.shape
    total = open_mask.astype(int)
    pad = np.zeros((high + 1, wide + 1), dtype=int)
    pad[1:, 1:] = total.cumsum(0).cumsum(1)
    out = np.zeros((high, wide), dtype=bool)
    for y in range(high):
        y0, y1 = max(0, y - radius), min(high, y + radius + 1)
        for x in range(wide):
            x0, x1 = max(0, x - radius), min(wide, x + radius + 1)
            inside = pad[y1, x1] - pad[y0, x1] - pad[y1, x0] + pad[y0, x0]
            out[y, x] = inside >= need * (y1 - y0) * (x1 - x0)
    return out


def chambers(open_mask):
    """The open cells as components, largest first."""
    left = {(x, z) for z in range(W) for x in range(W) if open_mask[z, x]}
    comps = []
    while left:
        start = left.pop()
        comp = {start}
        stack = [start]
        while stack:
            x, z = stack.pop()
            for step in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                n = (x + step[0], z + step[1])
                if n in left:
                    left.discard(n)
                    comp.add(n)
                    stack.append(n)
        comps.append(comp)
    comps.sort(key=len, reverse=True)
    return comps


def open_rooms(mask):
    """The panel's rooms and the largest of them; specks of ink-cleared ground are dropped."""
    comps = chambers(clean(~mask))
    kept = [comp for comp in comps if len(comp) >= 64]
    rooms = set().union(*kept) if kept else set()
    return rooms, (max(kept, key=len) if kept else set())


def shaft(p, main, cells):
    """Clear a doorway from the face inward until it stands in the panel's own cave."""
    for x, z in cells:
        if (x, z) in main:
            return
        for y in range(1, DOOR_H + 1):
            p.air(x, y, z)
        p.put(x, 0, z, SMOOTH)


def components(p):
    """The tile's walkable floor as components, largest first."""
    def open_(x, z):
        return p.get(x, 1, z) == "minecraft:air" and p.get(x, 2, z) == "minecraft:air"

    left = {(x, z) for x in range(W) for z in range(W) if open_(x, z)}
    comps = []
    while left:
        start = left.pop()
        comp = {start}
        stack = [start]
        while stack:
            x, z = stack.pop()
            for step in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                n = (x + step[0], z + step[1])
                if n in left:
                    left.discard(n)
                    comp.add(n)
                    stack.append(n)
        comps.append(comp)
    comps.sort(key=len, reverse=True)
    return comps


def drive(p, comp, main):
    """Cut a passage from one chamber to another: masons would not leave a room walled off."""
    ax, az = min(comp)
    bx, bz = min(main, key=lambda cell: abs(cell[0] - ax) + abs(cell[1] - az))
    path = [(x, az) for x in range(min(ax, bx), max(ax, bx) + 1)]
    path += [(bx, z) for z in range(min(az, bz), max(az, bz) + 1)]
    for x, z in path:
        for dx in (-1, 0, 1):
            for dz in (-1, 0, 1):
                px, pz = x + dx, z + dz
                if not (0 <= px < W and 0 <= pz < W):
                    continue
                for y in range(1, DOOR_H + 1):
                    p.air(px, y, pz)
                p.put(px, 0, pz, FLOOR)


def join_caves(p):
    """Join every chamber and every doorway to the panels' main cave."""
    for _ in range(20):
        comps = components(p)
        if len(comps) <= 1:
            return
        main = comps[0]
        for comp in comps[1:]:
            drive(p, comp, main)
            main = main | comp


def cave_tile(rooms, main, key):
    """The panel as a tile: ink is rock, the rooms are the cave, roofed at HEIGHT."""
    p = fb.Plan(seed=sum((i + 1) * ord(c) for i, c in enumerate(key)), top=HEIGHT)
    # The whole box is rock first. A tile is placed in ground it does not know, and only what it carries
    # is certain, so the cave is cut out of stone the tile brings with it.
    p.fill(0, 0, 0, W - 1, HEIGHT - 1, W - 1, ROCK)
    for x, z in sorted(rooms):
        edge = x < 8 or x > W - 9 or z < 8 or z > W - 9
        p.put(x, 0, z, FLOOR_ALT if edge else FLOOR)
        for y in range(1, CAVE + 1):
            p.air(x, y, z)
    # The hold's own stone where the rock meets a room: a kerb a mason would have left, and the one
    # thing that says this is the fortress's undercroft and not a hole somebody found.
    for x, z in rooms:
        for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            nx, nz = x + dx, z + dz
            if not (0 <= nx < W and 0 <= nz < W) or (nx, nz) in rooms:
                continue
            p.put(nx, 1, nz, CHISEL if (nx + nz) % 7 else CRACKED)
            p.put(nx, 0, nz, COBBLED)
    for a, b in DOORS:
        for i in range(a, b + 1):
            for cells in ([(i, d) for d in range(30)], [(i, W - 1 - d) for d in range(30)],
                          [(d, i) for d in range(30)], [(W - 1 - d, i) for d in range(30)]):
                shaft(p, main, cells)
    join_caves(p)
    return p


def check(p):
    """Every doorway reaches the largest room the panel draws."""
    def open_(x, z):
        return p.get(x, 1, z) == "minecraft:air" and p.get(x, 2, z) == "minecraft:air"

    left = {(x, z) for x in range(W) for z in range(W) if open_(x, z)}
    comps = []
    best = set()
    while left:
        start = left.pop()
        comp = {start}
        stack = [start]
        while stack:
            x, z = stack.pop()
            for step in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                n = (x + step[0], z + step[1])
                if n in left:
                    left.discard(n)
                    comp.add(n)
                    stack.append(n)
        comps.append(len(comp))
        if len(comp) > len(best):
            best = comp
    doors = [(i, 0) for a, b in DOORS for i in range(a, b + 1)]
    doors += [(i, W - 1) for a, b in DOORS for i in range(a, b + 1)]
    doors += [(0, i) for a, b in DOORS for i in range(a, b + 1)]
    doors += [(W - 1, i) for a, b in DOORS for i in range(a, b + 1)]
    reached = sum(1 for cell in doors if cell in best)
    return len(best), comps, reached, len(doors)


def main():
    keys = sys.argv[1:]
    if len(keys) != 2:
        raise SystemExit("give two panel keys, as in: cave_tile.py r2c1 r2c2")
    masks = panel_masks()
    out_dir = (pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "resources"
               / "data" / "tolkienmobs" / "structure")
    failed = False
    for name, key in zip(("inner_a", "inner_b"), keys):
        if key not in masks:
            raise SystemExit("no panel called %s (have %s)" % (key, ", ".join(masks)))
        rooms, main = open_rooms(masks[key])
        p = cave_tile(rooms, main, key)
        target = out_dir / ("ch2_%s.nbt" % name)
        if not gb.write_tile(p.b, (0, 0, 0), (W, HEIGHT, W), target, keep_air=True):
            raise SystemExit("%s: nothing to write" % name)
        cells, comps, reached, doors = check(p)
        print("%-9s %-6s %5d blocks  %4.0f KB  largest room %4d cells  %d of %d door cells reached"
              % (name, key, len(p.b), target.stat().st_size / 1024, cells, reached, doors))
        if reached < doors:
            print("   !! %d door cells do not reach the room - the panel's own walls close them off"
                  % (doors - reached))
            failed = True
        if len(comps) > 1:
            print("   .. %d chambers in all: %s" % (len(comps), comps[:8]))
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
