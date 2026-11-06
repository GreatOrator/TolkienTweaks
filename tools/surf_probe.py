"""The surface round the build, read block by block out of the save's own sections."""
import gzip
import math
import os
import struct
import zlib

REGION = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                      "run", "saves", "Scouting World", "dimensions", "tolkienmobs", "arda", "region")
AIR = {"minecraft:air", "minecraft:cave_air", "minecraft:void_air"}


class NBT:
    def __init__(self, d):
        self.d = d
        self.i = 0

    def u1(self):
        v = self.d[self.i]; self.i += 1; return v

    def u2(self):
        v = struct.unpack_from(">H", self.d, self.i)[0]; self.i += 2; return v

    def i4(self):
        v = struct.unpack_from(">i", self.d, self.i)[0]; self.i += 4; return v

    def name(self):
        n = self.u2(); s = self.d[self.i:self.i + n].decode("utf-8", "replace"); self.i += n; return s

    def payload(self, t):
        if t == 1:
            v = struct.unpack_from(">b", self.d, self.i)[0]; self.i += 1; return v
        if t == 2:
            v = struct.unpack_from(">h", self.d, self.i)[0]; self.i += 2; return v
        if t == 3:
            return self.i4()
        if t == 4:
            v = struct.unpack_from(">q", self.d, self.i)[0]; self.i += 8; return v
        if t == 5:
            v = struct.unpack_from(">f", self.d, self.i)[0]; self.i += 4; return v
        if t == 6:
            v = struct.unpack_from(">d", self.d, self.i)[0]; self.i += 8; return v
        if t == 7:
            n = self.i4(); v = self.d[self.i:self.i + n]; self.i += n; return v
        if t == 8:
            return self.name()
        if t == 9:
            et = self.u1(); n = self.i4(); return (et, [self.payload(et) for _ in range(n)])
        if t == 10:
            out = {}
            while True:
                tt = self.u1()
                if tt == 0:
                    return out
                nm = self.name()
                out[nm] = (tt, self.payload(tt))
        if t == 11:
            n = self.i4(); v = list(struct.unpack_from(">%di" % n, self.d, self.i)); self.i += 4 * n; return v
        if t == 12:
            n = self.i4(); v = list(struct.unpack_from(">%dq" % n, self.d, self.i)); self.i += 8 * n; return v
        raise ValueError("tag %d" % t)


def chunk(cx, cz):
    p = os.path.join(REGION, "r.%d.%d.mca" % (cx >> 5, cz >> 5))
    if not os.path.exists(p):
        return None
    data = open(p, "rb").read()
    idx = 4 * ((cx & 31) + (cz & 31) * 32)
    off = struct.unpack_from(">I", data, idx)[0]
    if off == 0:
        return None
    start = (off >> 8) * 4096
    ln = struct.unpack_from(">I", data, start)[0]
    kind = data[start + 4]
    blob = data[start + 5:start + 4 + ln]
    raw = gzip.decompress(blob) if kind == 1 else zlib.decompress(blob) if kind == 2 else blob
    n = NBT(raw)
    t = n.u1()
    n.name()
    return n.payload(t)


def unpack(arr, bits):
    per = 64 // bits
    mask = (1 << bits) - 1
    out = []
    for v in arr:
        u = v & 0xFFFFFFFFFFFFFFFF
        for k in range(per):
            out.append((u >> (k * bits)) & mask)
    return out


_tops = {}


def tops(cx, cz):
    """Top non-air block of every column in a chunk: {(x&15, z&15): (y, name)}."""
    if (cx, cz) in _tops:
        return _tops[(cx, cz)]
    c = chunk(cx, cz)
    out = {}
    if c is not None:
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
                entries = unpack(data[1], bits)
            for i in range(4096):
                name = pal[entries[i]]
                if name in AIR:
                    continue
                x, z, y = i & 15, (i >> 4) & 15, (i >> 8) & 15
                out[(x, z)] = (y0 * 16 + y, name)
    _tops[(cx, cz)] = out
    return out


def at(x, z):
    t = tops(x >> 4, z >> 4)
    return t.get((x & 15, z & 15))


def walk(tag, fixed, start, step, n=31, every=10):
    out = []
    for k in range(n):
        v = start + step * k
        h = at(v, fixed) if tag == "x" else at(fixed, v)
        out.append("%d:%s" % (v, "-" if h is None else h[0]))
    print("  ".join(out))


print("floor 5400,-3100 =", at(5400, -3100), " NE corner 5700,-3340 =", at(5700, -3340))
print("west wall 5200 (x out west):")
walk("x", -3100, 5200, -10); walk("x", -2960, 5200, -10)
print("east wall 5753 (x out east):")
walk("x", -3100, 5753, 10); walk("x", -3230, 5753, 10)
print("north wall -3370 (z out north):")
walk("z", 5400, -3370, -10); walk("z", 5280, -3370, -10); walk("z", 5650, -3370, -10)
print("south wall -2859 (z out south):")
walk("z", 5400, -2859, 10); walk("z", 5280, -2859, 10); walk("z", 5650, -2859, 10)
