"""Bakes the river names raster Arda reads at worldgen.

The rivers mask is 21 separate networks: the drawing breaks at confluences, at fords and where a channel
is thinner than the bake's threshold, so a course cannot be traced from a source to a mouth through it.
What the mask does give is which channel each water cell belongs to, and the components that are a single
named river are named here by the middle of their own extent.

A component is not always one river. The Lune and the Brandywine meet in the drawing, and the lake at
Evendim's head fuses them into one network, so the component that carries both cannot be named by its
middle: that is what called the Brandywine the Lune for its whole course through the Shire. A component
that holds two of the split's ends is divided instead, by water distance from those ends - every cell
belongs to the nearer one - so the boundary falls where the two courses actually part, at the junction.

Only the components a name can be given to without guessing are listed. The rest stay 0 and the game reads
them as the generic river, which is honest: a component that carries three rivers cannot be called one of
them.

The output is one byte per eight-block cell, 0 where the water has no name of its own, and the ids are the
ones ArdaMasks and ArdaMap read. Run it from the mod root:  python tools/arda_river_names.py
"""

import os
from collections import deque

import numpy as np
from PIL import Image
from scipy import ndimage

Image.MAX_IMAGE_PIXELS = None

ASSET = os.path.join("src", "main", "resources", "assets", "tolkienmobs", "arda", "rivers.png")
OUT = os.path.join("src", "main", "resources", "assets", "tolkienmobs", "arda", "river-names.png")
BLOCKS = 8
WEST, NORTH = -5120, -10240
WATER = 77

# id: (name, the middle of the component's own extent, in blocks)
NAMES = {
    1: ("anduin", (9352, 2752)),
    2: ("celduin", (13248, -2920)),
    3: ("gwathlo", (4208, -1472)),
    4: ("lune", (808, -2456)),
}
# How near a component's middle a name has to be to claim it, in blocks. Anything further is another river.
REACH = 1500

# The two rivers the drawing fuses into one component, with points along each one's own course - the ends
# and the bends between them, read off the drawn line. A component holding points of both is divided
# between them by water distance: a cell belongs to the river whose nearest point is nearer, so the border
# falls where the two courses part, at the junction the drawing made. The Lune keeps a cell the two reach
# at once, since it is listed first and its course is the shorter of the two.
SPLIT = {
    4: ("lune", [(-776, 64), (-890, -200), (-890, -600), (-600, -790), (-100, -790), (150, -760)]),
    5: ("brandywine", [(455, -124), (415, 600), (300, 1150), (-152, 1808), (440, -400), (480, -900),
                       (600, -1400), (950, -1900), (1400, -2400), (1500, -2700), (1200, -3400),
                       (800, -4400), (650, -4700)]),
}


def divide(raster, components, water):
    """Splits the one component that carries two of the split's ends between them, cell by cell."""
    rows, cols = np.nonzero(water)
    column = int(round((455 - WEST) / BLOCKS))
    row = int(round((-124 - NORTH) / BLOCKS))
    near = int(np.argmin((cols - column) ** 2 + (rows - row) ** 2))
    component = int(components[rows[near], cols[near]])
    inside = components == component
    cell_rows, cell_cols = np.nonzero(inside)

    assign = np.zeros(components.shape, np.uint8)
    queue = deque()
    for name_id, (name, course) in sorted(SPLIT.items()):
        for x, z in course:
            column = int(round((x - WEST) / BLOCKS))
            row = int(round((z - NORTH) / BLOCKS))
            near = int(np.argmin((cell_cols - column) ** 2 + (cell_rows - row) ** 2))
            seed_row, seed_col = int(cell_rows[near]), int(cell_cols[near])
            if assign[seed_row, seed_col] == 0:
                assign[seed_row, seed_col] = name_id
                queue.append((seed_row, seed_col))
            print("%-9s %6d, %6d -> cell %6d, %6d" % (
                name, x, z, WEST + seed_col * BLOCKS, NORTH + seed_row * BLOCKS))
    while queue:
        row, column = queue.popleft()
        for dr in (-1, 0, 1):
            for dc in (-1, 0, 1):
                rr, cc = row + dr, column + dc
                if (0 <= rr < assign.shape[0] and 0 <= cc < assign.shape[1]
                        and water[rr, cc] and components[rr, cc] == component and assign[rr, cc] == 0):
                    assign[rr, cc] = assign[row, column]
                    queue.append((rr, cc))

    raster[inside] = assign[inside]
    for name_id, (name, _) in sorted(SPLIT.items()):
        cells = np.nonzero(assign == name_id)
        if cells[0].size:
            print("%-9s keeps %5d cells of the split component, x %6d..%6d z %6d..%6d" % (
                name, cells[0].size, WEST + cells[1].min() * BLOCKS, WEST + (cells[1].max() + 1) * BLOCKS,
                NORTH + cells[0].min() * BLOCKS, NORTH + (cells[0].max() + 1) * BLOCKS))


def main():
    mask = np.array(Image.open(ASSET).convert("L"))
    water = mask > WATER
    components, count = ndimage.label(water)
    centres = ndimage.center_of_mass(water, components, range(1, count + 1))

    named = np.zeros(count + 1, np.uint8)
    for index, (row, column) in enumerate(centres, 1):
        x = WEST + column * BLOCKS
        z = NORTH + row * BLOCKS
        best = None
        for name_id, (name, (nx, nz)) in NAMES.items():
            distance = ((x - nx) ** 2 + (z - nz) ** 2) ** 0.5
            if distance <= REACH and (best is None or distance < best[0]):
                best = (distance, name_id, name)
        if best is not None:
            named[index] = best[1]
            print("%-9s component %2d at (%6d, %6d), %.0f blocks from the name" % (
                best[2], index, int(x), int(z), best[0]))

    raster = named[components]
    divide(raster, components, water)
    Image.fromarray(raster).save(OUT)
    print("wrote %s: %d x %d, %d named cells" % (OUT, raster.shape[1], raster.shape[0],
                                                 int((raster > 0).sum())))
    present = set(np.unique(raster).tolist())
    missing = [name for name_id, (name, _) in NAMES.items() if name_id not in present]
    missing += [name for name_id, (name, _) in SPLIT.items() if name_id not in present]
    if missing:
        print("no component found for: " + ", ".join(missing))


if __name__ == "__main__":
    main()
