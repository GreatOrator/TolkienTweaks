"""Save panel crops and mask overlays so the extraction can be eyeballed
against the original drawing.

Usage: python geom_panels.py <sheet>
Writes ../build/geomorph_masks/<sheet>_<key>_orig.png  and ..._overlay.png
"""
import os
import sys
import numpy as np
from PIL import Image, ImageDraw

import geom_extract as gx

OUT = gx.OUT


def main():
    path = sys.argv[1]
    im = Image.open(path).convert("L")
    a = np.asarray(im, dtype=np.uint8)
    h, w = a.shape
    ink = a < 200
    cols = gx.spans(ink.mean(axis=0), w)
    rows = gx.spans(ink.mean(axis=1), h)
    os.makedirs(OUT, exist_ok=True)
    base = os.path.splitext(os.path.basename(path))[0]
    print(f"{base}: cols {cols} rows {rows}")
    for ri, (y0, y1) in enumerate(rows, start=1):
        for ci, (x0, x1) in enumerate(cols, start=1):
            key = f"r{ri}c{ci}"
            sub = ink[y0:y1 + 1, x0:x1 + 1]
            ys = np.where(sub.any(axis=1))[0]
            xs = np.where(sub.any(axis=0))[0]
            box = (x0 + int(xs[0]), y0 + int(ys[0]),
                   x0 + int(xs[-1]) + 1, y0 + int(ys[-1]) + 1)
            crop = im.crop(box)
            crop.save(os.path.join(OUT, f"{base}_{key}_orig.png"))

            den = gx.cell_density(ink, box)
            t = gx.otsu(den)
            mask = den >= t
            cw, ch = crop.size
            ov = crop.convert("RGB")
            d = ImageDraw.Draw(ov)
            sx, sy = cw / gx.TILE, ch / gx.TILE
            for r in range(gx.TILE):
                for c in range(gx.TILE):
                    if mask[r, c]:
                        d.rectangle([c * sx, r * sy, (c + 1) * sx - 1, (r + 1) * sy - 1],
                                    fill=(220, 40, 40))
            # blend so the underlying line work still reads
            ov = Image.blend(crop.convert("RGB"), ov, 0.45)
            d = ImageDraw.Draw(ov)
            for i in range(gx.TILE + 1):
                col = (0, 90, 255) if i % 5 == 0 else (120, 160, 255)
                d.line([(i * sx, 0), (i * sx, ch)], fill=col, width=1)
                d.line([(0, i * sy), (cw, i * sy)], fill=col, width=1)
            ov.save(os.path.join(OUT, f"{base}_{key}_overlay.png"))
            print(f"  {key}: {cw}x{ch} otsu={t:.3f} rock={mask.mean():.2f}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
