"""Stocks the campaign ruin's chests from the pack's own desolate-ruins table.

The ruin the missing rider is found at was cut and raised in a world, so its chests came across as chests
and nothing else: no loot table, no contents, and a player who climbs into the ruin finds five empty boxes
where the notes have them find the rider's gear. The pack already generates a table for exactly this ground
- `tolkienmobs:chests/desolate_ruins` - so the chests are pointed at it rather than filled here, which
keeps the ruin in step with the loot table every other ruin in the pack draws from.

Run from the mod directory:

    python tools/stock_ruin_chests.py [--dry-run]

The template is rewritten in place, and everything that is not a chest's block entity is left exactly as it
was: the file is parsed into a typed tree and written back out, so no other tag is reordered or dropped.
"""
import argparse
import gzip
import io
import struct

TEMPLATE = 'src/main/resources/data/tolkienmobs/structure/ttmruin_large_2.nbt'
LOOT_TABLE = 'tolkienmobs:chests/desolate_ruins'

# ---- a typed NBT tree -------------------------------------------------------------------------------
# Every value is kept with the tag type it was read as, because writing a byte back as an int is how a
# template comes back subtly different from the file the world wrote.


class Tag:
    __slots__ = ('kind', 'value')

    def __init__(self, kind, value):
        self.kind = kind      # the NBT tag id: 1 byte ... 11 int array, 12 long array
        self.value = value

    def __repr__(self):
        return 'Tag(%d, %r)' % (self.kind, self.value)


class Reader:
    def __init__(self, data):
        self.b = data
        self.i = 0

    def u1(self):
        v = self.b[self.i]
        self.i += 1
        return v

    def u2(self):
        v = struct.unpack_from('>H', self.b, self.i)[0]
        self.i += 2
        return v

    def i4(self):
        v = struct.unpack_from('>i', self.b, self.i)[0]
        self.i += 4
        return v

    def i8(self):
        v = struct.unpack_from('>q', self.b, self.i)[0]
        self.i += 8
        return v

    def name(self):
        n = self.u2()
        v = self.b[self.i:self.i + n].decode('utf-8')
        self.i += n
        return v

    def payload(self, kind):
        if kind == 1:
            return Tag(kind, self.signed(1))
        if kind == 2:
            return Tag(kind, self.signed(2))
        if kind == 3:
            return Tag(kind, self.signed(4))
        if kind == 4:
            return Tag(kind, self.signed(8))
        if kind == 5:
            v = struct.unpack_from('>f', self.b, self.i)[0]
            self.i += 4
            return Tag(kind, v)
        if kind == 6:
            v = struct.unpack_from('>d', self.b, self.i)[0]
            self.i += 8
            return Tag(kind, v)
        if kind == 7:
            n = self.i4()
            v = self.b[self.i:self.i + n]
            self.i += n
            return Tag(kind, v)
        if kind == 8:
            return Tag(kind, self.name())
        if kind == 9:
            elem = self.u1()
            n = self.i4()
            return Tag(kind, (elem, [self.payload(elem) for _ in range(n)]))
        if kind == 10:
            fields = {}
            while True:
                child = self.u1()
                if child == 0:
                    return Tag(kind, fields)
                # The name is read before the value on purpose: Python evaluates the right-hand side of a
                # subscript assignment first, which would read the payload before the name it belongs to.
                key = self.name()
                fields[key] = self.payload(child)
        if kind == 11:
            n = self.i4()
            v = list(struct.unpack_from('>%di' % n, self.b, self.i))
            self.i += 4 * n
            return Tag(kind, v)
        if kind == 12:
            n = self.i4()
            v = list(struct.unpack_from('>%dq' % n, self.b, self.i))
            self.i += 8 * n
            return Tag(kind, v)
        raise ValueError('tag %d' % kind)

    def signed(self, width):
        fmt = {1: '>b', 2: '>h', 4: '>i', 8: '>q'}[width]
        v = struct.unpack_from(fmt, self.b, self.i)[0]
        self.i += width
        return v


def read_root(data):
    r = Reader(data)
    kind = r.u1()
    if kind != 10:
        raise ValueError('root is not a compound')
    r.name()
    return r.payload(10)


def write_payload(tag):
    k = tag.kind
    if k == 1:
        return struct.pack('>b', tag.value)
    if k == 2:
        return struct.pack('>h', tag.value)
    if k == 3:
        return struct.pack('>i', tag.value)
    if k == 4:
        return struct.pack('>q', tag.value)
    if k == 5:
        return struct.pack('>f', tag.value)
    if k == 6:
        return struct.pack('>d', tag.value)
    if k == 7:
        return struct.pack('>i', len(tag.value)) + tag.value
    if k == 8:
        raw = tag.value.encode('utf-8')
        return struct.pack('>H', len(raw)) + raw
    if k == 9:
        elem, items = tag.value
        return bytes([elem]) + struct.pack('>i', len(items)) + b''.join(write_payload(i) for i in items)
    if k == 10:
        out = bytearray()
        for name, child in tag.value.items():
            raw = name.encode('utf-8')
            out += bytes([child.kind]) + struct.pack('>H', len(raw)) + raw + write_payload(child)
        out += b'\x00'
        return bytes(out)
    if k == 11:
        return struct.pack('>i', len(tag.value)) + struct.pack('>%di' % len(tag.value), *tag.value)
    if k == 12:
        return struct.pack('>i', len(tag.value)) + struct.pack('>%dq' % len(tag.value), *tag.value)
    raise ValueError('tag %d' % k)


def write_root(root):
    return b'\x0a\x00\x00' + write_payload(root)


def main():
    parser = argparse.ArgumentParser(description='Point the campaign ruin\'s chests at the desolate-ruins table.')
    parser.add_argument('--dry-run', action='store_true', help='say what would change and write nothing')
    args = parser.parse_args()

    raw = gzip.decompress(open(TEMPLATE, 'rb').read())
    root = read_root(raw)
    palette = root.value['palette'].value[1]
    blocks = root.value['blocks'].value[1]

    chests = 0
    already = 0
    for entry in blocks:
        state = entry.value['state'].value
        name = palette[state].value.get('Name').value
        if not name.endswith('_chest') and name != 'minecraft:chest':
            continue
        chests += 1
        nbt = entry.value.get('nbt')
        if nbt is None:
            nbt = Tag(10, {})
            entry.value['nbt'] = nbt
        table = nbt.value.get('LootTable')
        if table is not None and table.value == LOOT_TABLE:
            already += 1
            continue
        nbt.value['id'] = Tag(8, name.split(':', 1)[1] if ':' in name else name)
        nbt.value['LootTable'] = Tag(8, LOOT_TABLE)

    print('%s: %d chest blocks, %d already stocked, %d to stock'
          % (TEMPLATE, chests, already, chests - already))
    if not chests:
        raise SystemExit('no chests in the template - is this the right ruin?')
    if args.dry_run:
        return
    if already == chests:
        print('nothing to write')
        return
    with gzip.GzipFile(TEMPLATE, 'wb', mtime=0) as out:
        out.write(write_root(root))
    print('wrote %s' % TEMPLATE)


if __name__ == '__main__':
    main()
