"""Bakes the river names raster Arda reads at worldgen.

The rivers mask is 21 separate networks: the drawing breaks at confluences, at fords and where a channel
is thinner than the bake's threshold, so a course cannot be traced from a source to a mouth through it.
What the mask does give is which channel each water cell belongs to, and the components that are a single
named river are named here by the middle of their own extent.

Only the components a name can be given to without guessing are listed. The rest stay 0 and the game reads
them as the generic river, which is honest: a component that carries three rivers cannot be called one of
them.

The output is one byte per eight-block cell, 0 where the water has no name of its own, and the ids are the
ones ArdaMasks and ArdaMap read. Run it from the mod root:  python tools/arda_river_names.py
"""

import os

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


def main():
    mask = np.array(Image.open(ASSET).convert("L"))
    components, count = ndimage.label(mask > WATER)
    centres = ndimage.center_of_mass(mask > WATER, components, range(1, count + 1))

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
    Image.fromarray(raster).save(OUT)
    print("wrote %s: %d x %d, %d named cells" % (OUT, raster.shape[1], raster.shape[0],
                                                 int((raster > 0).sum())))
    missing = [name for name_id, (name, _) in NAMES.items() if name_id not in named]
    if missing:
        print("no component found for: " + ", ".join(missing))


if __name__ == "__main__":
    main()
