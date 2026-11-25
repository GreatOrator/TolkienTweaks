"""The ground `ArdaMap` answers for a column, reproduced from the masks the game itself reads.

`ArdaMasks` bakes the reference rasters to eight blocks to the pixel into
`src/main/resources/assets/tolkienmobs/arda/` and reads them back with interpolations of its own: a
Catmull-Rom spline across the four height pixels astride a column, and a bilinear cover between the
four mountain pixels around it. The ground is then lifted by the mountain mask's own share of
`MASK_GAIN` and broken up by `ragged`, the folded noise that turns the mask's blurred brightness into
ridges instead of a cone.

This is that path in Python, so a tool can answer the ground the world will stand at rather than an
approximation of it:

    import arda_ground
    arda_ground.heights(6878, -6509)                  the height the world puts there
    arda_ground.detail(6878, -6509)                    ...and what made it: drawn, lifted, broken

What it does not reproduce is what `ArdaMap` does to the ground after the drawing: the bank grading
beside a drawn channel, the vale under the city, Bree's hill and the Gundabad pass. A drawn sea is not
answered either - its bed is read from the country's relief and the map's own noise, and it is not a
height anything stands on - so `heights` answers the waterline there and `detail` says `sea`.

Vectorised, because a scout reads a whole window at once: the arithmetic is the game's, done to
arrays, including the wrap of its 64-bit hash.
"""

import os
from collections import namedtuple

import numpy as np
from PIL import Image

Image.MAX_IMAGE_PIXELS = None

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(ROOT, "src", "main", "resources", "assets", "tolkienmobs", "arda")

# The frame the masks are baked over, as ArdaMasks reads them: eight blocks to the pixel.
WEST, NORTH, BLOCKS = -5120, -10240, 8
# The drawing's two ends, and the waterline inside it, as ArdaMap reads them.
BLACK_Y = 0
WHITE_Y = 318
SEA_LEVEL = 25
# The climb the mountain mask adds at its brightest, and the ceiling the ground is held under.
MASK_GAIN = 64
MOUNTAIN_CEILING = 420
LOWEST = -60
# The ridge noise's amplitude, and the brightness below which a column is left exactly as drawn.
CRAG = 42
RAGGED_FLOOR = 0.05

_LONG = np.int64
_MASK24 = _LONG(0xFFFFFF)

_ground = None
_rock = None
_peak = 1.0
_rows = 0
_columns = 0

Ground = namedtuple("Ground", "height drawn lift shift sea")


def _load():
    """The baked masks, read once: the topography as the game reads it, and the mountain brightness."""
    global _ground, _rock, _peak, _rows, _columns
    if _ground is not None:
        return
    heights = os.path.join(ASSETS, "height.png")
    mountains = os.path.join(ASSETS, "mountains.png")
    for path in (heights, mountains):
        if not os.path.isfile(path):
            raise SystemExit("no baked mask at " + path + " - build the arda assets first")
    _ground = np.array(Image.open(heights)).astype(np.float64)
    _rock = _encoded(np.array(Image.open(mountains).convert("L")).astype(np.float64))
    _rows, _columns = _ground.shape
    _peak = _rock.max() / 255.0


def _encoded(value):
    """The byte the game gets out of a grey pixel, which is not the byte in the file.

    `ArdaMasks.coverage` reads its masks through `BufferedImage.getRGB`, and a grey PNG is stored in the
    linear grey colour space: Java converts each pixel to sRGB on the way out, so 202 reads as 230. The
    conversion is per pixel and rounded to a byte, so it is applied here the same way rather than to the
    blended average, which is worth a quarter of a level.
    """
    grey = value / 255.0
    linear = grey <= 0.0031308
    encoded = np.where(linear, grey * 12.92, 1.055 * np.power(grey, 1.0 / 2.4) - 0.055)
    return np.round(encoded * 255.0)


def _split(block_x, block_z):
    """The columns as flat arrays and the shape they came in, so scalars and meshes take one path."""
    x = np.asarray(block_x, dtype=np.float64)
    z = np.asarray(block_z, dtype=np.float64)
    x, z = np.broadcast_arrays(x, z)
    return x.ravel(), z.ravel(), x.shape


def _round(value):
    """`Math.round`: half up, not half to even as numpy rounds."""
    return np.floor(value + 0.5)


def _spline(v0, v1, v2, v3, t):
    """Catmull-Rom through four values: the curve from v1 to v2, with v0 and v3 setting their slopes."""
    return (v1 + 0.5 * t * (v2 - v0)
            + t * t * (2.0 * v0 - 5.0 * v1 + 4.0 * v2 - v3)
            + 0.5 * t * t * t * (3.0 * (v1 - v2) + v3 - v0))


def drawn(block_x, block_z):
    """The heightmap's own sample of a column, 0 where the drawing has no ground there."""
    _load()
    x, z, shape = _split(block_x, block_z)
    across = (x - WEST) / BLOCKS
    along = (z - NORTH) / BLOCKS
    inside = (across >= 0.0) & (along >= 0.0) & (across <= _columns - 1) & (along <= _rows - 1)

    west = np.floor(np.where(inside, across, 0.0)).astype(np.int64)
    north = np.floor(np.where(inside, along, 0.0)).astype(np.int64)
    share_x = across - west
    share_z = along - north

    c0 = np.maximum(0, west - 1)
    c1 = west
    c2 = np.minimum(_columns - 1, west + 1)
    c3 = np.minimum(_columns - 1, west + 2)
    r0 = np.maximum(0, north - 1)
    r1 = north
    r2 = np.minimum(_rows - 1, north + 1)
    r3 = np.minimum(_rows - 1, north + 2)

    grid = _ground
    bands = [_spline(grid[r, c0], grid[r, c1], grid[r, c2], grid[r, c3], share_x)
             for r in (r0, r1, r2, r3)]
    raw = _spline(bands[0], bands[1], bands[2], bands[3], share_z)

    # A spline overshoots a ridge and undershoots a coast: the answer is held between the four pixels
    # the column actually stands between.
    low = np.minimum(np.minimum(grid[r1, c1], grid[r1, c2]), np.minimum(grid[r2, c1], grid[r2, c2]))
    high = np.maximum(np.maximum(grid[r1, c1], grid[r1, c2]), np.maximum(grid[r2, c1], grid[r2, c2]))
    raw = np.minimum(high, np.maximum(low, raw))
    return np.where(inside & (raw > 0.5), _round(raw), 0.0).reshape(shape)


def rock(block_x, block_z):
    """How bright the mountain mask is over a column, from nothing to its own peak."""
    _load()
    x, z, shape = _split(block_x, block_z)
    across = (x - WEST) / BLOCKS - 0.5
    along = (z - NORTH) / BLOCKS - 0.5
    inside = ((across >= -0.5) & (along >= -0.5)
              & (across <= _columns - 0.5) & (along <= _rows - 0.5))

    across = np.where(inside, across, 0.0)
    along = np.where(inside, along, 0.0)
    west = np.floor(np.maximum(0.0, across)).astype(np.int64)
    north = np.floor(np.maximum(0.0, along)).astype(np.int64)
    east = np.minimum(_columns - 1, west + 1)
    south = np.minimum(_rows - 1, north + 1)
    share_x = np.clip(across - west, 0.0, 1.0)
    share_z = np.clip(along - north, 0.0, 1.0)

    mask = _rock
    northwards = mask[north, west] * (1.0 - share_x) + mask[north, east] * share_x
    southwards = mask[south, west] * (1.0 - share_x) + mask[south, east] * share_x
    cover = (northwards * (1.0 - share_z) + southwards * share_z) / 255.0
    return np.where(inside, cover / _peak if _peak > 0.0 else 0.0, 0.0).reshape(shape)


def _hash(cell_x, cell_z, salt):
    """The map's own hash, to the bit: a 64-bit wrap and an arithmetic shift, as Java's long does."""
    # The products wrap, as Java's long does: that wrap is the hash, so numpy's warning is silenced.
    with np.errstate(over="ignore"):
        value = (cell_x.astype(_LONG) * _LONG(374761393)
                 + cell_z.astype(_LONG) * _LONG(668265263)
                 + _LONG(salt) * _LONG(1442695040888963407))
        value = (value ^ np.right_shift(value, 13)) * _LONG(1274126177)
        value = value ^ np.right_shift(value, 16)
    return (value & _MASK24) / float(0xFFFFFF)


def _fractal(block_x, block_z, size, salt):
    """One octave of the map's hash noise, smooth between its cells."""
    cell_x = np.floor_divide(block_x, size)
    cell_z = np.floor_divide(block_z, size)
    fraction_x = _smooth((block_x % size) / float(size))
    fraction_z = _smooth((block_z % size) / float(size))

    north_west = _hash(cell_x, cell_z, salt)
    north_east = _hash(cell_x + 1, cell_z, salt)
    south_west = _hash(cell_x, cell_z + 1, salt)
    south_east = _hash(cell_x + 1, cell_z + 1, salt)

    north = north_west + (north_east - north_west) * fraction_x
    south = south_west + (south_east - south_west) * fraction_x
    return north + (south - north) * fraction_z


def _smooth(value):
    return value * value * (3.0 - 2.0 * value)


def ragged_shift(block_x, block_z, bright):
    """How far `ragged` moves a lifted column: two folded octaves, the amplitude following rock squared."""
    x, z, shape = _split(block_x, block_z)
    bright = np.asarray(bright, dtype=np.float64).ravel()
    # The noise is read at whole blocks, as the generator reads it.
    x = np.floor(x).astype(np.int64)
    z = np.floor(z).astype(np.int64)

    crest = 1.0 - np.abs(_fractal(x, z, 128, 11))
    crest = crest + 0.5 * (1.0 - np.abs(_fractal(x, z, 32, 12)))
    shift = (crest / 1.5 - 0.5) * CRAG * bright * bright
    return np.where(bright <= RAGGED_FLOOR, 0.0, shift).reshape(shape)


def detail(block_x, block_z):
    """One column, term by term: the ground the game answers and what it was made of."""
    parts = _parts(block_x, block_z)
    return Ground(*(int(np.ravel(value)[0]) for value in parts[:4]),
                  bool(np.ravel(parts[4])[0]))


def heights(block_x, block_z):
    """The ground the world stands at over these columns, the waterline where the drawing has no ground."""
    return np.ravel(_parts(block_x, block_z)[0]).reshape(np.broadcast(
        np.asarray(block_x), np.asarray(block_z)).shape)


def _parts(block_x, block_z):
    """height, drawn, lift, shift, sea - as arrays over the columns asked for."""
    raw = np.asarray(drawn(block_x, block_z), dtype=np.float64).ravel()
    bright = np.asarray(rock(block_x, block_z), dtype=np.float64).ravel()
    sea = raw <= 0.0

    drawn_height = np.where(sea, float(SEA_LEVEL), BLACK_Y + _round(raw / 65535.0 * (WHITE_Y - BLACK_Y)))
    lift = np.where(sea, 0.0, _round(bright * MASK_GAIN))
    shift = np.where(sea, 0.0, _round(np.ravel(ragged_shift(block_x, block_z, bright))))
    raised = np.where(bright <= RAGGED_FLOOR, drawn_height + lift, drawn_height + lift + shift)
    height = np.where(sea, float(SEA_LEVEL), np.clip(raised, LOWEST, MOUNTAIN_CEILING))
    shape = np.broadcast(np.asarray(block_x), np.asarray(block_z)).shape
    return tuple(np.ravel(part).reshape(shape) for part in
                 (height, drawn_height, lift, shift, sea))
