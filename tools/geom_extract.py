"""Extract the six geomorph panels from an upscaled sheet and classify each
61x61 block cell as open floor or rock, by ink density.

Usage:
    python geom_extract.py <sheet> [panel-key]
      panel-key: r{1..3}c{1..2}, or "all" for every panel's plan
"""
import os
import sys
import numpy as np
from PIL import Image

TILE = 61          # blocks per geomorph edge (10 squares x 6, plus border)
OUT = os.path.join("..", "build", "geomorph_masks")


def blank_runs(profile, thresh, min_len):
    runs, start = [], None
    for i, v in enumerate(profile < thresh):
        if v and start is None:
            start = i
        elif not v and start is not None:
            if i - start >= min_len:
                runs.append((start, i - 1))
            start = None
    if start is not None and len(profile) - start >= min_len:
        runs.append((start, len(profile) - 1))
    return runs


def spans(profile, total, min_gap=10, margin=20):
    blank = blank_runs(profile, 0.005, min_gap)
    out, cursor = [], 0
    for s, e in blank:
        if s - cursor > margin:
            out.append((cursor, s - 1))
        cursor = e + 1
    if total - cursor > margin:
        out.append((cursor, total - 1))
    return out


def otsu(values, bins=256):
    """Vectorised Otsu threshold over [0,1]."""
    v = np.asarray(values, dtype=np.float64)
    hist, edges = np.histogram(v, bins=bins, range=(0.0, 1.0))
    centres = (edges[:-1] + edges[1:]) / 2.0
    w0 = np.cumsum(hist).astype(np.float64)
    w1 = hist.sum() - w0
    cs = np.cumsum(hist * centres)
    total = cs[-1] if cs.size else 0.0
    m0 = cs / np.maximum(w0, 1.0)
    m1 = (total - cs) / np.maximum(w1, 1.0)
    between = w0 * w1 * (m0 - m1) ** 2
    return float(centres[int(np.argmax(between))])


def cell_density(ink, box, tile=TILE):
    """Ink fraction per tile cell, in cell-local coordinates."""
    x0, y0, x1, y1 = box
    w, h = x1 - x0, y1 - y0
    sub = ink[y0:y0 + h, x0:x0 + w]
    yi = np.clip(((np.arange(h) + 0.5) * tile / h).astype(int), 0, tile - 1)
    xi = np.clip(((np.arange(w) + 0.5) * tile / w).astype(int), 0, tile - 1)
    rows = np.repeat(yi[:, None], w, axis=1)
    cols = np.repeat(xi[None, :], h, axis=0)
    den = np.zeros((tile, tile), dtype=np.float64)
    cnt = np.zeros((tile, tile), dtype=np.float64)
    np.add.at(den, (rows, cols), sub.astype(np.float64))
    np.add.at(cnt, (rows, cols), 1.0)
    return den / np.maximum(cnt, 1.0)


def ascii_plan(mask, title):
    lines = [f"--- {title} ---",
             "     " + "".join(str((c // 10) % 10) for c in range(mask.shape[1])),
             "     " + "".join(str(c % 10) for c in range(mask.shape[1]))]
    for r in range(mask.shape[0]):
        lines.append(f"{r:4d} " + "".join("#" if mask[r, c] else "." for c in range(mask.shape[1])))
    return "\n".join(lines)


def edge_openings(mask):
    res = {}
    sides = {
        "north (row 0)": mask[0, :],
        "south (row 60)": mask[-1, :],
        "west (col 0)": mask[:, 0],
        "east (col 60)": mask[:, -1],
    }
    for name, arr in sides.items():
        runs, start = [], None
        for i, v in enumerate(arr):
            if not v and start is None:
                start = i
            elif v and start is not None:
                runs.append((start, i - 1))
                start = None
        if start is not None:
            runs.append((start, len(arr) - 1))
        res[name] = runs
    return res


def main():
    path = sys.argv[1]
    only = sys.argv[2] if len(sys.argv) > 2 else None
    im = Image.open(path).convert("L")
    a = np.asarray(im, dtype=np.uint8)
    h, w = a.shape
    ink = (a < 200)

    cols = spans(ink.mean(axis=0), w)
    rows = spans(ink.mean(axis=1), h)
    print(f"{os.path.basename(path)} {w}x{h}")
    if len(cols) != 2 or len(rows) != 3:
        print(f"  !! unexpected panel layout cols={cols} rows={rows}")
        return 1

    os.makedirs(OUT, exist_ok=True)
    base = os.path.splitext(os.path.basename(path))[0]
    for ri, (y0, y1) in enumerate(rows, start=1):
        for ci, (x0, x1) in enumerate(cols, start=1):
            key = f"r{ri}c{ci}"
            if only and only not in ("all", key):
                continue
            sub = ink[y0:y1 + 1, x0:x1 + 1]
            ys = np.where(sub.any(axis=1))[0]
            xs = np.where(sub.any(axis=0))[0]
            box = (x0 + int(xs[0]), y0 + int(ys[0]), x0 + int(xs[-1]) + 1, y0 + int(ys[-1]) + 1)
            dimw, dimh = box[2] - box[0], box[3] - box[1]
            den = cell_density(ink, box)
            t = otsu(den)
            mask = den >= t
            pct = np.percentile(den, [1, 10, 25, 50, 75, 90, 99])
            print(f"\n  {key}: {dimw}x{dimh}px {dimw / TILE:.2f}x{dimh / TILE:.2f} px/block "
                  f"otsu={t:.3f} rock={mask.mean():.2f}")
            print("      density p1/p10/p25/p50/p75/p90/p99: "
                  + " ".join(f"{p:.3f}" for p in pct) + f"  mean {den.mean():.3f}")
            for name, runs in edge_openings(mask).items():
                print(f"      {name}: open {runs}")
            if only in ("all", key):
                print(ascii_plan(mask, f"{base} {key}"))
            img = Image.fromarray((mask * 255).astype(np.uint8)).resize(
                (TILE * 10, TILE * 10), Image.NEAREST)
            img.save(os.path.join(OUT, f"{base}_{key}.png"))
    return 0


if __name__ == "__main__":
    sys.exit(main())
