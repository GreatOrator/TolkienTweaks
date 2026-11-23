"""Take the ground out of a bridge piece, so a template is bridge and nothing else.

A bridge captured out of a world comes with the world in it: the air of the space the piece stood in, the water
it crossed, and the dirt, grass and stone of the banks the abutments sit in. None of that is the bridge. Left in,
the piece carries a slab of bank into every span it is stood in, and `RoadCommands` sweeps only timbers before it
stands one - so the ground in the template is laid as part of the bridge.

A cell that is dropped is a cell that is not listed, and a cell that is not listed is never placed: the same
answer a builder gets from a structure block's void. The palette is pruned to the states that are left, because a
state nothing names is a state a structure block would not have written.

    python tools/bridge_pieces.py                 # say what is in each piece
    python tools/bridge_pieces.py --apply         # drop the ground and rewrite the pieces

The pieces are read and written through `grab_build`, so a file that came out of a structure block goes back as
one: gzip, root compound, `palette` and `blocks`. `DataVersion` and `size` are left exactly as they were, and each
rewritten file is read back and compared cell for cell before the next one is touched.

**What counts as ground** is the set below - air in all three of its names, water, the dirt and grass family, plain
stone with the stones that come out of a terrain pass beside it, and the sand, gravel and clay of a river's bed and
bars. A block a bridge may be made of is never in it: `stone_bricks` is not `stone`, and `cobblestone` is a deck
somebody chose. The bed is ground even where a span's own piers come down onto it, because the piers are what the
piece carries: `RoadCommands.pillarsColumns` draws each one on down to the first block that is neither air nor water,
so a piece that shipped its bed would lay a shelf of gravel under every bridge stood from it, and pull the piers up
short of the bed besides. Anything the tool does not expect is printed as it stands, so a piece holding something odd
says so instead of quietly losing it.
"""
import argparse
import collections
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import grab_build as gb

PIECES = HERE.parent / 'src/main/resources/data/tolkienmobs/structure/bridges'

GROUND = {
    'minecraft:air', 'minecraft:cave_air', 'minecraft:void_air',
    'minecraft:water',
    'minecraft:dirt', 'minecraft:coarse_dirt', 'minecraft:rooted_dirt', 'minecraft:podzol',
    'minecraft:grass_block', 'minecraft:short_grass', 'minecraft:tall_grass', 'minecraft:fern',
    'minecraft:stone', 'minecraft:andesite', 'minecraft:granite', 'minecraft:diorite',
    'minecraft:tuff', 'minecraft:deepslate', 'minecraft:calcite', 'minecraft:dripstone_block',
    'minecraft:sand', 'minecraft:red_sand', 'minecraft:gravel', 'minecraft:clay',
}


def state_key(entry):
    return (entry.get('Name'), tuple(sorted((entry.get('Properties') or {}).items())))


def look(path):
    root = gb.read_nbt(path.read_bytes())
    keys = [state_key(entry) for entry in root.get('palette', [])]
    blocks = root.get('blocks', [])
    counts = collections.Counter(int(block['state']) for block in blocks)
    size = [int(v) for v in root['size']]
    return root, keys, blocks, counts, size


def report(path):
    root, keys, blocks, counts, size = look(path)
    volume = size[0] * size[1] * size[2]
    print('%s  %s  %d listed of %d cells in the box'
          % (path.relative_to(PIECES.parent.parent.parent), 'x'.join(str(v) for v in size),
             len(blocks), volume))
    kept = dropped = 0
    for index, key in enumerate(keys):
        count = counts.get(index, 0)
        if not count:
            continue
        name = key[0]
        if name in GROUND:
            dropped += count
            print('  drop %5d  %s' % (count, name))
        else:
            kept += count
            print('  keep %5d  %s%s' % (count, name, ' ' + str(dict(key[1])) if key[1] else ''))
    for index, key in enumerate(keys):
        if index not in counts:
            print('  dead palette entry  %s' % (key[0],))
    print('  -> %d bridge cells kept, %d ground cells dropped, %d after'
          % (kept, dropped, kept))
    return root, keys, blocks, counts


def clean(path, root, keys, blocks):
    before = {tuple(int(v) for v in b['pos']): (keys[int(b['state'])][0], keys[int(b['state'])][1], b.get('nbt'))
              for b in blocks}
    kept = [block for block in blocks if keys[int(block['state'])][0] not in GROUND]
    order, index = [], {}
    for block in kept:
        key = keys[int(block['state'])]
        if key not in index:
            index[key] = len(order)
            order.append(key)
    for block in kept:
        block['state'] = index[keys[int(block['state'])]]
    root['palette'] = gb.NbtList(10, [{'Name': name, 'Properties': dict(properties)} if properties
                                      else {'Name': name} for name, properties in order])
    root['blocks'] = gb.NbtList(10, kept)
    path.write_bytes(gb.write_nbt(root))
    after_root, after_keys, after_blocks, _, _ = look(path)
    expected = {pos: cell for pos, cell in before.items() if cell[0] not in GROUND}
    got = {tuple(int(v) for v in b['pos']):
           (after_keys[int(b['state'])][0], after_keys[int(b['state'])][1], b.get('nbt')) for b in after_blocks}
    if got != expected:
        raise SystemExit('%s came back different from the clean' % path)
    return len(kept)


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('--apply', action='store_true', help='rewrite the pieces')
    parser.add_argument('--only', default='', help='limit to file names containing this text')
    args = parser.parse_args(argv[1:])

    paths = sorted(p for p in PIECES.glob('*.nbt') if args.only in p.name)
    if not paths:
        raise SystemExit('no pieces in %s' % PIECES)
    for path in paths:
        root, keys, blocks, counts = report(path)
        if args.apply:
            print('  wrote %s: %d cells' % (path.name, clean(path, root, keys, blocks)))
        print()
    if not args.apply:
        print('report only; pass --apply to drop the ground')
    return 0


if __name__ == '__main__':
    raise SystemExit(main(sys.argv))
