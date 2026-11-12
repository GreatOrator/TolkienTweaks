"""Audit the structure templates for structure void, and strip what a structure-block save would never write.

A structure block does not save a structure void cell at all - the block is invisible and has no entry in the
file - which is how a builder says "leave the ground here alone": a cell that is not listed is never placed. A
template that lists `minecraft:structure_void` is therefore either hand-made or was written by another tool, and
the listed cell will be laid (or, depending on the placer, laid as an invisible hole). Either way it is not a
save.

    python tools/structure_void_audit.py            # report every template
    python tools/structure_void_audit.py --apply     # drop void cells and dead palette entries
"""
import argparse
import collections
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
ROOT = HERE.parent / 'src/main/resources/data'
VOID = 'minecraft:structure_void'

sys.path.insert(0, str(HERE))
import grab_build as gb


def state_key(entry):
    return (entry.get('Name'), tuple(sorted((entry.get('Properties') or {}).items())))


def audit(path):
    root = gb.read_nbt(path.read_bytes())
    entries = root.get('palette', [])
    keys = [state_key(entry) for entry in entries]
    blocks = root.get('blocks', [])
    used = collections.Counter(int(block['state']) for block in blocks)
    void_states = {i for i, key in enumerate(keys) if key[0] == VOID}
    void_cells = sum(count for state, count in used.items() if state in void_states)
    air_cells = sum(count for state, count in used.items() if keys[state][0] == 'minecraft:air')
    unused = [i for i in range(len(keys)) if i not in used]
    duplicates = [key for key, count in collections.Counter(keys).items() if count > 1]
    return {
        'path': path,
        'root': root,
        'size': [int(v) for v in root['size']],
        'listed': len(blocks),
        'void_palette': len(void_states),
        'void_cells': void_cells,
        'air_cells': air_cells,
        'unused': unused,
        'duplicates': duplicates,
    }


def strip(found):
    """The template as a structure-block save would have written it: no void cells, no dead palette entries."""
    root, keys, blocks = found['root'], [state_key(e) for e in found['root']['palette']], found['root']['blocks']
    kept = [block for block in blocks if keys[int(block['state'])][0] != VOID]
    order, index = [], {}
    for block in kept:
        key = keys[int(block['state'])]
        if key not in index:
            index[key] = len(order)
            order.append(key)
    palette = [{'Name': name, 'Properties': dict(properties)} if properties else {'Name': name}
               for name, properties in order]
    for block in kept:
        block['state'] = index[keys[int(block['state'])]]
    root['palette'] = gb.NbtList(10, palette)
    root['blocks'] = gb.NbtList(10, kept)
    return root, kept, palette


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apply', action='store_true', help='rewrite the templates')
    parser.add_argument('--only', default='', help='limit to paths containing this text')
    args = parser.parse_args(argv[1:])

    paths = sorted(p for p in ROOT.rglob('*.nbt') if args.only in str(p))
    print('%d templates under %s' % (len(paths), ROOT))
    totals = collections.Counter()
    changed = []
    for path in paths:
        try:
            found = audit(path)
        except Exception as error:  # a template that cannot be read is a finding too
            print('UNREADABLE %s: %s' % (path, error))
            totals['unreadable'] += 1
            continue
        size = found['size']
        volume = size[0] * size[1] * size[2]
        if found['void_palette'] or found['void_cells']:
            totals['templates_with_void'] += 1
            totals['void_cells'] += found['void_cells']
            print('void %5d cells, %d palette entries, %5d listed of %6d, air %5d  %s'
                  % (found['void_cells'], found['void_palette'], found['listed'], volume,
                     found['air_cells'], path.relative_to(ROOT)))
        totals['templates'] += 1
        totals['listed'] += found['listed']
        totals['sparse'] += 1 if found['listed'] < volume else 0
        totals['air'] += found['air_cells']
        totals['dead_entries'] += len(found['unused'])
        totals['duplicate_states'] += len(found['duplicates'])
        if args.apply and (found['void_cells'] or found['unused'] or found['duplicates']):
            before = cell_map(found['root'])
            root, kept, palette = strip(found)
            path.write_bytes(gb.write_nbt(root))
            after = cell_map(gb.read_nbt(path.read_bytes()))
            expected = {key: value for key, value in before.items() if key[0][0] != VOID}
            if after != expected:
                raise SystemExit('read-back of %s does not match the strip' % path)
            changed.append(path)
    print('---')
    print('templates %d, sparse (fewer entries than the box) %d, listed blocks %d, air listed %d'
          % (totals['templates'], totals['sparse'], totals['listed'], totals['air']))
    print('templates carrying structure void %d, void cells %d, dead palette entries %d, duplicated states %d'
          % (totals['templates_with_void'], totals['void_cells'], totals['dead_entries'], totals['duplicate_states']))
    if args.apply:
        print('rewritten %d templates' % len(changed))
        for path in changed:
            print('  ', path.relative_to(ROOT))
    else:
        print('report only; pass --apply to rewrite')
    return 0


def cell_map(root):
    """Every cell as {(name, properties, pos): nbt}, so a rewrite can be compared in full."""
    keys = [state_key(entry) for entry in root['palette']]
    return {(keys[int(b['state'])][0], keys[int(b['state'])][1], tuple(int(v) for v in b['pos'])): b.get('nbt')
            for b in root['blocks']}


if __name__ == '__main__':
    raise SystemExit(main(sys.argv))
