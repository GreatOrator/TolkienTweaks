"""What stands at the spring's rear: water columns and the ground outside the build's edge."""
import gzip
import os
import sys
from collections import Counter

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import surf_probe as sp  # noqa: E402

ART = os.path.join(HERE, "..", "run", "rivendell_art")
ORIGIN = (5200, 75, -3370)
BOX = (5200, 5753, -3370, -2859)  # lowX, highX, lowZ, highZ
AIR = {"minecraft:air", "minecraft:cave_air", "minecraft:void_air"}
WIN = (5540, 5900, -3160, -2920)  # around the spring's rear, past the east wall

outline = {}
watered = {}
for line in open(os.path.join(ART, "rivendell.parts"), encoding="utf-8"):
    name, dx, dy, dz, sx, sy, sz = line.split()
    dx, dy, dz = int(dx), int(dy), int(dz)
    with gzip.open(os.path.join(ART, name)) as fh:
        tag = sp.NBT(fh.read())
    t = tag.u1()
    tag.name()
    root = tag.payload(t)
    pal = [p["Name"][1] for p in root["palette"][1][1]]
    for b in root["blocks"][1][1]:
        nm = pal[b.get("state", (3, 0))[1]]
        if nm in AIR:
            continue
        px, py, pz = b["pos"][1][1]
        x = ORIGIN[0] + dx + px
        z = ORIGIN[2] + dz + pz
        if not (WIN[0] <= x <= WIN[1] and WIN[2] <= z <= WIN[3]):
            continue
        y = ORIGIN[1] + dy + py
        if y > outline.get((x, z), -1):
            outline[(x, z)] = y
        if "water" in nm and y > watered.get((x, z), -1):
            watered[(x, z)] = y


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
for cx in range(WIN[0] >> 4, (WIN[1] >> 4) + 1):
    for cz in range(WIN[2] >> 4, (WIN[3] >> 4) + 1):
        for (x, z), v in tops(cx, cz).items():
            if WIN[0] <= x <= WIN[1] and WIN[2] <= z <= WIN[3]:
                seen[(x, z)] = v

print("build columns in the window %d, save columns %d" % (len(outline), len(seen)))

wet = sorted((x, z, y) for (x, z), (y, n) in seen.items() if "water" in n)
print("water columns: %d" % len(wet))
if wet:
    print("  levels:", Counter(w[2] for w in wet).most_common(6))
    print("  x %d..%d  z %d..%d" % (min(w[0] for w in wet), max(w[0] for w in wet),
                                    min(w[1] for w in wet), max(w[1] for w in wet)))
    out = [w for w in wet if not (BOX[0] <= w[0] <= BOX[1] and BOX[2] <= w[1] <= BOX[3])]
    print("  outside the build: %d" % len(out), out[:6])
    buildwatered = sorted((x, z, y) for (x, z), y in watered.items())
    print("  the build's own water in the window: %d columns" % len(buildwatered),
          "levels", Counter(w[2] for w in buildwatered).most_common(4))

print("cross-sections (x: top), the east wall is x %d" % BOX[1])
for z in (-3080, -3060, -3045, -3030, -3015):
    print("  z %d  " % z + " ".join("%d:%s" % (x, seen[(x, z)][0] if (x, z) in seen else "-")
                                    for x in range(5660, 5861, 10)))

print("the build's outline and the ground along the east edge (x: outline/save):")
for z in (-3060, -3030, -3000):
    print("  z %d  " % z + " ".join("%d:%s/%s" % (x, outline.get((x, z), "-"),
                                                  seen[(x, z)][0] if (x, z) in seen else "-")
                                    for x in range(5740, 5801, 4)))
