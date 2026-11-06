"""What stands in the build's rim now: the save's own tops against the build's outline, column by column."""
import gzip
import os
import struct
import sys
import zlib
from collections import Counter, defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import surf_probe as sp  # noqa: E402  (chunk/unpack only; its tops() overwrites, so not used)

ART = os.path.join(HERE, "..", "run", "rivendell_art")
ORIGIN = (5200, 75, -3370)
BOX = (5200, 5753, -3370, -2859)  # lowX, highX, lowZ, highZ
AIR = {"minecraft:air", "minecraft:cave_air", "minecraft:void_air"}

outline = {}
watered = {}
for line in open(os.path.join(ART, "rivendell.parts"), encoding="utf-8"):
    name, dx, dy, dz, sx, sy, sz = line.split()
    dx, dy, dz = int(dx), int(dy), int(dz)
    path = os.path.join(ART, name)
    with gzip.open(path) as fh:
        tag = sp.NBT(fh.read())
    t = tag.u1()
    tag.name()
    root = tag.payload(t)
    pal = [p["Name"][1] for p in root["palette"][1][1]]
    for b in root["blocks"][1][1]:
        st = b.get("state", (3, 0))[1]
        nm = pal[st]
        if nm in AIR:
            continue
        px, py, pz = b["pos"][1][1]
        x = ORIGIN[0] + dx + px
        z = ORIGIN[2] + dz + pz
        y = ORIGIN[1] + dy + py
        key = (x, z)
        if y > outline.get(key, -1):
            outline[key] = y
        if "water" in nm and y > watered.get(key, -1):
            watered[key] = y


def tops(cx, cz):
    """Top non-air block per column, max across sections: {(x, z): (y, name)}."""
    out = {}
    c = sp.chunk(cx, cz)
    if c is None:
        return out
    base_x, base_z = cx * 16, cz * 16
    for sec in c["sections"][1][1]:
        y0 = sec["Y"][1]
        bs = sec.get("block_states")
        if bs is None:
            continue
        pal = [p["Name"][1] for p in bs[1]["palette"][1][1]]
        if all(n in AIR for n in pal):
            continue
        data = bs[1].get("data")
        if data is None:
            entries = [0] * 4096
        else:
            bits = max(4, (len(pal) - 1).bit_length())
            entries = sp.unpack(data[1], bits)
        for i in range(4096):
            nm = pal[entries[i]]
            if nm in AIR:
                continue
            y = y0 * 16 + (i >> 8)
            key = (base_x + (i & 15), base_z + ((i >> 4) & 15))
            if y > out.get(key, (-999, ""))[0]:
                out[key] = (y, nm)
    return out


surface = {}
lo_x, hi_x, lo_z, hi_z = BOX
for cx in range(lo_x >> 4, (hi_x >> 4) + 1):
    for cz in range(lo_z >> 4, (hi_z >> 4) + 1):
        surface.update(tops(cx, cz))
print("build columns %d, save columns %d" % (len(outline), len(surface)))

near_cache = {}


def nearest(x, z, reach=10):
    """Nearest built column's top and how far away it is."""
    key = (x, z)
    if key in near_cache:
        return near_cache[key]
    best = (99, None)
    for r in range(1, reach + 1):
        for dx in range(-r, r + 1):
            for dz in range(-r, r + 1):
                if max(abs(dx), abs(dz)) != r:
                    continue
                v = outline.get((x + dx, z + dz))
                if v is not None:
                    best = (r, v)
                    break
            if best[0] != 99:
                break
        if best[0] != 99:
            break
    near_cache[key] = best
    return best


# ---- the rim inside the footprint: empty build columns, how far above the wall beside them ----
diffs = []
for x in range(lo_x, hi_x + 1):
    for z in range(lo_z, hi_z + 1):
        if (x, z) in outline:
            continue
        edge = min(x - lo_x, hi_x - x, z - lo_z, hi_z - z)
        if edge > 7:
            continue
        t = surface.get((x, z))
        r, wall = nearest(x, z)
        if t is None or wall is None:
            continue
        diffs.append((edge, r, t[0] - wall, x, z, t[0], wall))

above = [d for d in diffs if d[2] > 3]
print("rim columns inside the box (edge<=7): %d, more than 3 above the wall: %d" % (len(diffs), len(above)))
by_group = Counter()
for edge, r, d, *_ in above:
    by_group[(edge, min(r, 6))] += 1
print("  (edge dist, dist to wall) -> count, for those above:")
for k in sorted(by_group):
    print("   ", k, by_group[k])
tall = sorted(above, key=lambda d: -d[2])[:12]
print("  tallest rim columns (x, z, top, nearest wall top, step, dist):")
for edge, r, d, x, z, top, wall in tall:
    print("    %5d %5d  top %3d  wall %3d  step %+3d  d %d edge %d  %s"
          % (x, z, top, wall, d, r, edge, surface[(x, z)][1]))

# ---- what the build itself reaches at its boundary: ledge or wall? ----
edges = {
    "west": [(lo_x, z) for z in range(lo_z, hi_z + 1)],
    "east": [(hi_x, z) for z in range(lo_z, hi_z + 1)],
    "north": [(x, lo_z) for x in range(lo_x, hi_x + 1)],
    "south": [(x, hi_z) for x in range(lo_x, hi_x + 1)],
}
for name, cols in edges.items():
    hist = Counter((min(outline.get(c, -1) // 50 * 50, 300)) for c in cols)
    low = sum(1 for c in cols if outline.get(c, -1) < 250)
    tall = sum(1 for c in cols if outline.get(c, -1) >= 300)
    print("%5s edge: %d columns, outline >=300: %d, <250: %d" % (name, len(cols), tall, low))
    print("      outline by 50s:", sorted(hist.items()))

print("east edge detail z=-3313 (x, outline, save top):")
print("  ".join("%d:%s/%s" % (x, outline.get((x, -3313), "-"),
                              (surface[(x, -3313)][0] if (x, -3313) in surface else "-"))
                 for x in range(5738, 5762)))
print("east edge detail z=-3100:")
print("  ".join("%d:%s/%s" % (x, outline.get((x, -3100), "-"),
                              (surface[(x, -3100)][0] if (x, -3100) in surface else "-"))
                 for x in range(5738, 5762)))
print("north edge detail x=5650 (z, outline, save top):")
print("  ".join("%d:%s/%s" % (z, outline.get((5650, z), "-"),
                              (surface[(5650, z)][0] if (5650, z) in surface else "-"))
                 for z in range(-3364, -3340)))

# ---- how wide is a bare-floor band, and is there any wall to read a skyline from? ----
def runs(vals, start, step):
    out = []
    for i, v in enumerate(vals):
        k = start + step * i
        if out and out[-1][2] == v:
            out[-1][1] = k
        else:
            out.append([k, k, v])
    return out

for row_z in (-3313, -3230, -3150):
    vals = [outline.get((x, row_z), None) for x in range(5480, 5754)]
    print("outline along z=%d (5480..5753), runs of: " % row_z)
    print("   " + "  ".join("%d..%d:%s" % (a, b, v) for a, b, v in runs(vals, 5480, 1)))

for radius in (6, 16, 32):
    bare = 0
    found = 0
    for name, cols in edges.items():
        for (x, z) in cols:
            if outline.get((x, z), -1) > 80:
                continue
            bare += 1
            sky = -1
            for dx in range(-radius, radius + 1):
                for dz in range(-radius, radius + 1):
                    v = outline.get((x + dx, z + dz))
                    if v is not None and v > sky:
                        sky = v
            if sky > 80:
                found += 1
    print("bare-floor boundary columns: %d; a skyline within %d finds a wall for %d"
          % (bare, radius, found))

print("ground east of the box at z=-3313:")
print("   " + "  ".join("%d:%s" % (x, surface[(x, -3313)][0] if (x, -3313) in surface else "-")
                        for x in range(5754, 5954, 10)))

print("how much fill a deck rule would have to put back:")
fill_cols = 0
fill_blocks = 0
fill_max = 0
for x in range(lo_x - 32, hi_x + 33):
    for z in range(lo_z - 32, hi_z + 33):
        if lo_x <= x <= hi_x and lo_z <= z <= hi_z:
            continue
        bx = min(max(x, lo_x), hi_x)
        bz = min(max(z, lo_z), hi_z)
        if outline.get((bx, bz), -1) > 76:
            continue
        inner = surface.get((bx, bz))
        outer = surface.get((x, z))
        if inner is None or outer is None:
            continue
        need = inner[0] - outer[0]
        if need > 4:
            fill_cols += 1
            fill_blocks += need
            fill_max = max(fill_max, need)
print("   columns %d, blocks %d, deepest %d" % (fill_cols, fill_blocks, fill_max))

# ---- just outside the box: does the ground leave the wall's height cleanly? ----
steps = []
for x in range(lo_x - 6, hi_x + 7):
    for z in range(lo_z - 6, hi_z + 7):
        if lo_x <= x <= hi_x and lo_z <= z <= hi_z:
            continue
        # nearest column on the box, one step in
        bx = min(max(x, lo_x), hi_x)
        bz = min(max(z, lo_z), hi_z)
        inner = surface.get((bx, bz))
        outer = surface.get((x, z))
        if inner is None or outer is None:
            continue
        r, wall = nearest(bx, bz)
        if wall is None:
            continue
        steps.append((abs(inner[0] - outer[0]), x, z, inner[0], outer[0], wall))
steps.sort(reverse=True)
print("outside-adjacent cliffs (|inner - outer| >= 8): %d of %d"
      % (sum(1 for s in steps if s[0] >= 8), len(steps)))
for s in steps[:12]:
    print("    %5d %5d  inner %3d  outer %3d  step %2d  wall %3d"
          % (s[1], s[2], s[3], s[4], s[0], s[5]))
