"""The Bruinen where the branch joins it, and the ground across the water to the west."""
import os
import sys
from collections import Counter

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import surf_probe as sp  # noqa: E402

AIR = {"minecraft:air", "minecraft:cave_air", "minecraft:void_air"}
# West of the city (west wall 5200), round the branch's join with the drawn Bruinen.
WIN = (4750, 5200, -3300, -2900)


def tops(cx, cz):
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
        entries = [0] * 4096 if data is None else sp.unpack(data[1], max(4, (len(pal) - 1).bit_length()))
        for i in range(4096):
            nm = pal[entries[i]]
            if nm in AIR:
                continue
            y = y0 * 16 + (i >> 8)
            key = (base_x + (i & 15), base_z + ((i >> 4) & 15))
            if y > out.get(key, (-999, ""))[0]:
                out[key] = (y, nm)
    return out


seen = {}
missing = 0
for cx in range(WIN[0] >> 4, (WIN[1] >> 4) + 1):
    for cz in range(WIN[2] >> 4, (WIN[3] >> 4) + 1):
        t = tops(cx, cz)
        if not t:
            missing += 1
            continue
        for (x, z), v in t.items():
            if WIN[0] <= x <= WIN[1] and WIN[2] <= z <= WIN[3]:
                seen[(x, z)] = v

print("window x %d..%d z %d..%d: %d columns, %d chunks missing" % (WIN + (len(seen), missing)))

wet = sorted((x, z, y) for (x, z), (y, n) in seen.items() if "water" in n)
print("water columns: %d" % len(wet))
if wet:
    print("  levels:", Counter(w[2] for w in wet).most_common(8))
    print("  x %d..%d  z %d..%d" % (min(w[0] for w in wet), max(w[0] for w in wet),
                                    min(w[1] for w in wet), max(w[1] for w in wet)))
    byz = {}
    for x, z, y in wet:
        byz.setdefault(z, []).append(x)
    for z in sorted(byz)[::10]:
        xs = byz[z]
        print("  z %d  water x %d..%d (%d)" % (z, min(xs), max(xs), len(xs)))

best = {}
for (x, z), (y, nm) in seen.items():
    cell = (x >> 5, z >> 5)
    if y > best.get(cell, (-999, 0, 0, ""))[0]:
        best[cell] = (y, x, z, nm)
print("highest ground, one per 32x32 square:")
for y, x, z, nm in sorted(best.values(), reverse=True)[:10]:
    print("  %d %d %d  %s" % (x, y, z, nm.replace("minecraft:", "")))

# The bank across the water: west from the channel's edge at the join's own latitude.
if wet:
    mid = min((z for z in set(w[1] for w in wet)), key=lambda k: abs(k + 3086))
    row = sorted(x for x, z, _ in wet if z == mid)
    print("across the water at z %d, from x %d west:" % (mid, min(row)))
    print("  " + " ".join("%d:%s" % (x, seen[(x, mid)][0] if (x, mid) in seen else "-")
                          for x in range(min(row) - 2, min(row) - 66, -4)))
    print("and north-south along x %d:" % min(row))
    print("  " + " ".join("%d:%s" % (z, seen[(min(row), z)][0] if (min(row), z) in seen else "-")
                          for z in range(-3120, -3040, 8)))
