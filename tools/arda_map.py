"""Paints a window of `ArdaMap.sample()` as a map, with nothing built and nothing pinned in it.

This is the answer the generator starts from: the drawing's ground, its rivers and lakes, and the sea's
own countries, hillshaded from the same heights the world stands at, with the country grid's borders
drawn where `sample` changes country. A build or a pin is nowhere in it, so a spot chosen on this map is
chosen against the land alone.

    python tools/arda_map.py raster.bin out.png [--grid 2048] [--places places.json] [--title "..."]

The raster comes from the probe:

    ./gradlew -I tools/sampleprobe.gradle ardaSample -x createMinecraftArtifacts -q \\
        -PprobeArgs="map -5120 -10240 19456 8192 8 build/tmp/arda-map.bin"

Pass `--places` a JSON list of `["name", x, z]` (or `{"name": ..., "x": ..., "z": ...}`) to mark spots.
"""
import json
import struct
import sys

import numpy as np
from PIL import Image, ImageDraw, ImageFont

MAGIC = 0x41524441
MARGIN = 64
FIELDS = np.dtype([("h", ">i2"), ("w", ">i2"), ("f", "u1"), ("c", "u1")])

# The ground, from the waterline up: beach, grass, forest, the dry hills, rock, the high rock, snow.
LAND_STOPS = (
    (25, (198, 188, 150)),
    (33, (190, 178, 132)),
    (46, (152, 176, 118)),
    (68, (122, 158, 96)),
    (98, (108, 144, 84)),
    (135, (128, 140, 86)),
    (175, (142, 132, 100)),
    (215, (148, 142, 130)),
    (255, (162, 158, 152)),
    (300, (198, 198, 200)),
    (420, (242, 242, 246)),
)
SHALLOW = np.array((132, 194, 198), dtype=np.float64)
DEEP = np.array((26, 58, 112), dtype=np.float64)
RIVER = np.array((112, 172, 224), dtype=np.float64)


def load(path):
    with open(path, "rb") as handle:
        magic, width, height, step, x0, z0 = struct.unpack(">6i", handle.read(24))
        if magic != MAGIC:
            raise SystemExit("not a sample raster: " + path)
        raw = handle.read(width * height * 6)
    if len(raw) != width * height * 6:
        raise SystemExit("raster is short: %d bytes for %dx%d" % (len(raw), width, height))
    return width, height, step, x0, z0, np.frombuffer(raw, dtype=FIELDS).reshape(height, width)


def ground_colour(height):
    stops = np.array([stop for stop, _ in LAND_STOPS], dtype=np.float64)
    channels = [np.array([colour[index] for _, colour in LAND_STOPS], dtype=np.float64)
                for index in range(3)]
    return np.stack([np.interp(height, stops, channel) for channel in channels], axis=-1)


def shade_of(height, step):
    """A light from the north-west, level with one block of rise per block of run."""
    across = np.gradient(height, axis=1) / float(step)
    along = np.gradient(height, axis=0) / float(step)
    norm = np.sqrt(across * across + along * along + 1.0)
    lit = (0.5 * across + 0.5 * along + 0.7071) / (0.7071 * norm)
    return np.clip(lit, 0.62, 1.38)[..., None]


def borders(country):
    """Where `sample` changes country, and along the coast, one pixel wide."""
    edge = np.zeros(country.shape, bool)
    for axis in (0, 1):
        first = country[:, :-1] if axis == 1 else country[:-1, :]
        second = country[:, 1:] if axis == 1 else country[1:, :]
        found = (first != second) & (first != 255) & (second != 255)
        if axis == 1:
            edge[:, 1:] |= found
        else:
            edge[1:, :] |= found
    return edge


def paint(fields, step):
    height = fields["h"].astype(np.float64)
    level = fields["w"].astype(np.float64)
    flags = fields["f"]
    country = fields["c"].astype(np.int16)

    sea = (flags & 8) != 0
    # Water is where the ground stands under its own level. A sea country whose ground is above the
    # waterline is not a lake: it is dry land wearing an ocean's biome, and it is painted as that.
    water = (height < level) | ((flags & 2) != 0)
    depth = np.where(water, np.maximum(0.0, level - height), 0.0)

    rgb = ground_colour(height) * shade_of(np.where(water, level, height), step)
    stranded = (sea & ~water)[..., None]
    rgb = np.where(stranded, rgb * 0.55 + np.array((104, 144, 164)) * 0.45, rgb)

    share = np.clip(depth / 70.0, 0.0, 1.0)[..., None]
    wet = SHALLOW * (1.0 - share) + DEEP * share
    rgb = np.where(water[..., None], wet, rgb)
    river = ((flags & 2) != 0)[..., None]
    rgb = np.where(river, rgb * 0.45 + RIVER * 0.55, rgb)

    rgb[borders(country)] *= 0.60
    return np.clip(rgb, 0, 255).astype(np.uint8)


def font(size):
    for name in ("arialbd.ttf", "arial.ttf", "seguisb.ttf", "segoeui.ttf"):
        try:
            return ImageFont.truetype("C:/Windows/Fonts/" + name, size)
        except OSError:
            continue
    return ImageFont.load_default()


def label(draw, left, top, text, size, colour=(24, 24, 28), halo=(255, 255, 255)):
    face = font(size)
    for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1), (-1, -1), (1, 1), (-1, 1), (1, -1)):
        draw.text((left + dx, top + dy), text, font=face, fill=halo)
    draw.text((left, top), text, font=face, fill=colour)
    return draw.textbbox((left, top), text, font=face)


def places_from(path):
    with open(path, encoding="utf-8") as handle:
        loaded = json.load(handle)
    found = []
    for entry in loaded:
        if isinstance(entry, dict):
            found.append((str(entry["name"]), float(entry["x"]), float(entry["z"])))
        else:
            found.append((str(entry[0]), float(entry[1]), float(entry[2])))
    return found


def main(argv):
    if len(argv) < 2:
        raise SystemExit(__doc__)
    source = argv[1]
    out = argv[2] if len(argv) > 2 else source.rsplit(".", 1)[0] + ".png"
    grid, places, title = 0, [], ""
    index = 3
    while index < len(argv):
        if argv[index] == "--grid":
            grid = int(argv[index + 1])
            index += 2
        elif argv[index] == "--places":
            places = places_from(argv[index + 1])
            index += 2
        elif argv[index] == "--title":
            title = argv[index + 1]
            index += 2
        else:
            raise SystemExit("unknown argument " + argv[index])

    width, height, step, x0, z0, fields = load(source)
    painted = paint(fields, step)
    span_x, span_z = (width - 1) * step, (height - 1) * step

    caption = title or "Arda - ArdaMap.sample(), no builds, no pins"
    caption = "%s   x %d..%d   z %d..%d   %d blocks to the pixel" % (
        caption, x0, x0 + span_x, z0, z0 + span_z, step)
    canvas = Image.new("RGB", (max(width + MARGIN * 2, 24 + int(len(caption) * 9.2)),
                              height + MARGIN * 2), (250, 250, 248))
    canvas.paste(Image.fromarray(painted), (MARGIN, MARGIN))
    draw = ImageDraw.Draw(canvas)

    def across(block_x):
        return MARGIN + (block_x - x0) / step

    def along(block_z):
        return MARGIN + (block_z - z0) / step

    if grid:
        first = int(np.ceil(x0 / grid)) * grid
        for block_x in range(first, x0 + span_x + 1, grid):
            left = across(block_x)
            draw.line([(left, MARGIN), (left, MARGIN + height)], fill=(0, 0, 0, 40), width=1)
            label(draw, int(left) + 3, 8, str(block_x), 15)
        first = int(np.ceil(z0 / grid)) * grid
        for block_z in range(first, z0 + span_z + 1, grid):
            top = along(block_z)
            draw.line([(MARGIN, top), (MARGIN + width, top)], fill=(0, 0, 0, 40), width=1)
            label(draw, 4, int(top) + 1, str(block_z), 15)

    for name, block_x, block_z in places:
        left, top = across(block_x), along(block_z)
        if not (MARGIN <= left < MARGIN + width and MARGIN <= top < MARGIN + height):
            continue
        draw.ellipse([left - 5, top - 5, left + 5, top + 5], fill=(220, 40, 40), outline=(255, 255, 255))
        box = label(draw, int(left) + 8, int(top) - 22, name, 19)
        draw.line([(left, top), (left + 6, top - 4)], fill=(220, 40, 40), width=2)

    label(draw, MARGIN, MARGIN + height + 16, caption, 17)
    canvas.save(out)
    print("%s  %dx%d  step %d" % (out, canvas.width, canvas.height, step))


if __name__ == "__main__":
    main(sys.argv)
