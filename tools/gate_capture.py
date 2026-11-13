#!/usr/bin/env python3
"""Cuts the marked Gundabad gate out of a world as structure templates.

The gate is read straight off the map between five markers, and the cut keeps *everything* the box holds:
the gate's own materials, the map's rock around them, and the air. The rock is part of the build - the gate
was dug and dressed into a mountainside rather than set down on flat ground, and its skirts and retaining
walls are that mountain - so dropping it left the gate standing on nothing. Listing every cell also means a
template lays down exactly what was scouted: what stands in the test spot is what stood in the map.

The sky above each wing comes with it, as air as tall as the gate above the wing's own top. `--trim-sky` cuts
that back here instead, dropping air above the highest block of a column so the template ends at the
building and the terrain above is whatever the terrain is; without it the box is carried whole.

    python tools/gate_capture.py
    python tools/gate_capture.py --trim-sky
    python tools/gate_capture.py --from 464,12,-1056 --to 623,92,-1009 --name gundabad_gate

The output is 48-cubes plus the offsets to lay them by, which is what the mod's own multi-piece builds use
(`GundabadGate.PIECES`).
"""

import argparse
import pathlib
import shutil
import sys
from collections import Counter

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import grab_build as gb

MARKERS = ('tolkienmobs:block_ammolite', 'tolkienmobs:pane_ammolite', 'tolkienmobs:ore_ammolite')
AIR = ('minecraft:air', 'minecraft:cave_air')


def main():
    parser = argparse.ArgumentParser(description="Cut the Gundabad gate into structure templates.")
    parser.add_argument('--world', default='run/saves/world')
    parser.add_argument('--dim', default='')
    parser.add_argument('--from', dest='low', default='464,12,-1056')
    parser.add_argument('--to', dest='high', default='623,92,-1009')
    parser.add_argument('--name', default='gundabad_gate')
    parser.add_argument('--trim-sky', action='store_true',
                        help='drop air above the highest block of a column instead of keeping the box whole')
    parser.add_argument('--out', default='build/gate_pieces')
    parser.add_argument('--into', default='src/main/resources/data/tolkienmobs/structure/key_structures')
    args = parser.parse_args()

    low = tuple(int(v) for v in args.low.split(','))
    high = tuple(int(v) for v in args.high.split(','))

    blocks, size, low = gb.blocks_from_world(args.world, args.dim, low, high)
    print('box     %d x %d x %d at %s' % (size[0], size[1], size[2], low))

    # The markers are the scout's, not the build's: they are outside the box by design, so this only catches
    # a stray one and says so.
    stray = sum(1 for state in blocks.values() if state[0] in MARKERS)
    if stray:
        print('markers %d inside the box, dropped' % stray)
        blocks = {pos: state for pos, state in blocks.items() if state[0] not in MARKERS}

    solid = sum(1 for state in blocks.values() if state[0] not in AIR)
    print('build   %d cells: %d blocks, %d air, %d states'
          % (len(blocks), solid, len(blocks) - solid, len({s for s in blocks.values()})))

    if args.trim_sky:
        top = {}
        for (x, y, z), state in blocks.items():
            if state[0] in AIR:
                continue
            key = (x, z)
            top[key] = max(top.get(key, y), y)
        before = len(blocks)
        blocks = {pos: state for pos, state in blocks.items()
                  if state[0] not in AIR or pos[1] <= top.get((pos[0], pos[2]), -1)}
        print('sky     %d air cells above a wing dropped' % (before - len(blocks)))

    out = pathlib.Path(args.out)
    if out.exists():
        shutil.rmtree(out)
    parts = gb.cut(args.name, blocks, size, low, out, True)
    print('\npieces  %d' % len(parts))
    for name, dx, dy, dz in parts:
        print('  %-26s %3d %3d %3d' % (name, dx, dy, dz))

    into = pathlib.Path(args.into)
    into.mkdir(parents=True, exist_ok=True)
    for stale in into.glob(args.name + '_*.nbt'):
        stale.unlink()
    for path in sorted(out.glob('*.nbt')):
        shutil.copy2(path, into / path.name)
    print('\ncopied %d template(s) into %s' % (len(list(out.glob('*.nbt'))), into))

    counts = Counter(state[0] for state in blocks.values())
    print('\nkept:')
    for name, n in counts.most_common(20):
        print('  %-46s %d' % (name, n))


if __name__ == '__main__':
    sys.exit(main())
