#!/usr/bin/env python3
"""Draws Bucklebury's two bridge pieces to the shape a span stands them in.

A piece is not a bridge in the abstract: `RoadCommands.bridgePlan` stands it against a bank and reads its own box
for everything else. The measurements the plan holds a piece to are all in this file's two draws:

    x            along the span, from the bank into the water. An end's corner on x 0 is the bank column, and
                 the plan starts the first middle `endWidth` columns from that bank, so the box is the deck and
                 nothing else - a piece with a margin past its deck opens water where it reaches.
    z            across the span, nine columns exactly (`RoadPlan.HALF` 4 either side of the line). The near end
                 is stood turned half round, so its z is mirrored: a deck seven wide would cover `low..low+6` at
                 one bank and `low+2..low+8` at the other, two columns off each other and short at both edges.
    bottom row   `bridgeBase` for a middle - the water's own surface row, `deck - 2` - and `endBase`, one over
                 it, for an end. So the deck that is walked on is row 1 of an end and row 2 of a middle, and
                 both meet the road at `deck`.
    piers        the support block (`Roads.BRIDGES` names cobblestone wall) somewhere in each pier column, and
                 `pillarsColumns` draws down from the lowest one it finds in a column, to the bed.

The parapet is therefore `stone_brick_wall` and the piers are `cobblestone_wall`: a cobblestone parapet would be
found by that search in every column it runs through and drawn down to the bed, a curtain of pier under the edge
of the deck. The middle carries its piers at its own centre, so a span of middles is propped every twenty columns
rather than hanging between its abutments.

    python tools/bridge_build.py            # draw both pieces and say what they are

The two files replace `bucklebury_end.nbt` and `bucklebury_middle.nbt` in the jar's `bridges` folder. The owner's
own cut is kept in this repository's history if these are not wanted.
"""
import pathlib
import sys
from collections import Counter

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import grab_build as gb

PIECES = HERE.parent / 'src/main/resources/data/tolkienmobs/structure/bridges'

STONE = 'minecraft:stone_bricks'
MOSSY = 'minecraft:mossy_stone_bricks'
CRACKED = 'minecraft:cracked_stone_bricks'
PIER = 'minecraft:cobblestone_wall'
PARAPET = 'minecraft:stone_brick_wall'
SPRUCE = 'minecraft:spruce_planks'
SPRUCE_SLAB = 'minecraft:spruce_slab'
TORCH = 'minecraft:torch'

# A wall running with the span, and one standing alone as a pier.
WALL_ALONG = {'up': 'true', 'north': 'none', 'south': 'none', 'east': 'low', 'west': 'low',
              'waterlogged': 'false'}
POST = {'up': 'true', 'north': 'none', 'south': 'none', 'east': 'none', 'west': 'none',
        'waterlogged': 'false'}
SLAB_TOP = {'type': 'top', 'waterlogged': 'false'}

# One letter a block, for the draw the tool prints.
LETTER = {STONE: '#', MOSSY: '#', CRACKED: '#', PIER: 'p', PARAPET: 'W', SPRUCE: 'w',
          SPRUCE_SLAB: 's', TORCH: '*'}

END_LONG, END_TALL, WIDE = 17, 12, 9
MIDDLE_LONG, MIDDLE_TALL = 20, 4
# The two towers across the deck's edges: the column they stand at, and which way the deck lies from there.
TOWER_X = (1, 2)
TOWERS = ((0, 1), (WIDE - 1, -1))
TOWER_TOP = 9


def weathered(x, y, z):
    """A stone brick with the moss and the cracks placed by where it stands, so the draw is the same every run."""
    if (x * 3 + z * 5 + y) % 13 == 0:
        return MOSSY
    if (x * 5 + z * 7 + y * 2) % 17 == 0:
        return CRACKED
    return STONE


class Piece:
    """A piece as cells, with the three numbers the plan reads: x along the span, y up, z across."""

    def __init__(self, long_, tall, wide):
        self.size = (long_, tall, wide)
        self.blocks = {}

    def put(self, x, y, z, name, **properties):
        self.blocks[(x, y, z)] = (name, tuple(sorted(properties.items())))

    def counts(self):
        return Counter(name for name, _ in self.blocks.values())


def end():
    """The abutment: two rows of stone, the parapet and the two towers that make the bridgehead."""
    piece = Piece(END_LONG, END_TALL, WIDE)
    for x in range(END_LONG):
        for z in range(WIDE):
            piece.put(x, 0, z, weathered(x, 0, z))   # the abutment's mass, sitting at the waterline
            piece.put(x, 1, z, weathered(x, 1, z))   # the deck, flush with the road at `deck`
    for x in (4, 5, 12, 13):
        for z in (0, WIDE - 1):
            piece.put(x, 0, z, PIER, **POST)         # the piers, drawn from here down to the bed
    for x in range(3, END_LONG):
        for z in (0, WIDE - 1):
            piece.put(x, 2, z, PARAPET, **WALL_ALONG)
    for start, inward in TOWERS:
        shaft, cap = (start, start + inward), (start, start + inward, start + 2 * inward)
        for x in TOWER_X:
            for z in shaft:
                for y in range(2, TOWER_TOP + 1):
                    piece.put(x, y, z, weathered(x, y, z))
            for z in cap:
                piece.put(x, TOWER_TOP + 1, z, SPRUCE)
            for z in (cap[0], cap[-1]):
                piece.put(x, TOWER_TOP + 2, z, SPRUCE_SLAB, **SLAB_TOP)
        piece.put(TOWER_X[-1], TOWER_TOP + 2, shaft[-1], TORCH)
    return piece


def middle():
    """The span between the abutments: a deck with a parapet, propped at its own centre."""
    piece = Piece(MIDDLE_LONG, MIDDLE_TALL, WIDE)
    for x in range(MIDDLE_LONG):
        for z in range(WIDE):
            piece.put(x, 1, z, weathered(x, 1, z))   # the deck's underside
            piece.put(x, 2, z, weathered(x, 2, z))   # the deck, at the same `deck` the ends meet
    for x in (9, 10):
        for z in (0, WIDE - 1):
            piece.put(x, 0, z, PIER, **POST)
    for x in range(MIDDLE_LONG):
        for z in (0, WIDE - 1):
            piece.put(x, 3, z, PARAPET, **WALL_ALONG)
    return piece


def write(piece, name):
    path = PIECES / (name + '.nbt')
    gb.write_tile(piece.blocks, (0, 0, 0), piece.size, path, False)
    return path


def verify(piece, path):
    """Reads the file back and holds it to the four numbers the plan stands a piece by."""
    root = gb.read_nbt(path.read_bytes())
    size = tuple(int(v) for v in root['size'])
    if size != piece.size:
        raise SystemExit('%s came back as %s, not %s' % (path.name, size, piece.size))
    palette = [gb.state_of(entry) for entry in root['palette']]
    back = {}
    for block in root['blocks']:
        x, y, z = (int(v) for v in block['pos'])
        back[(x, y, z)] = palette[int(block['state'])]
    if back != piece.blocks:
        raise SystemExit('%s came back different from the draw' % path.name)
    long_, tall, wide = size
    if wide != 2 * 4 + 1:
        raise SystemExit('%s is %d across; a span is %d' % (path.name, wide, 2 * 4 + 1))
    deck = 1 if tall == END_TALL else 2
    for x in range(long_):
        for z in range(wide):
            if (x, deck, z) not in back:
                raise SystemExit('%s has no deck at %d, %d' % (path.name, x, z))
    piers = sum(1 for name, _ in back.values() if name == PIER)
    if not piers:
        raise SystemExit('%s carries no pier, so nothing is drawn under it' % path.name)
    for (x, y, z), (name, _) in back.items():
        if name == PARAPET and y < deck + 1:
            raise SystemExit('%s has brick wall at row %d, where the piers are read' % (path.name, y))
    return root, back, piers


def show(piece, path, piers):
    long_, tall, wide = piece.size
    print('%s  %d long x %d tall x %d across  %d cells'
          % (path.name, long_, tall, wide, len(piece.blocks)))
    for name, count in sorted(piece.counts().items()):
        print('   %-40s %d' % (name, count))
    print('   piers %d  (the support the table draws to the bed)' % piers)
    deck = 1 if tall == END_TALL else 2
    for y in range(tall):
        rows = [''.join(LETTER[piece.blocks[(x, y, z)][0]] if (x, y, z) in piece.blocks else '.'
                        for z in range(wide)) for x in range(long_)]
        if all(row.count('.') == wide for row in rows):
            continue
        print('   row %-2d %s' % (y, 'deck' if y == deck else ''))
        for x, row in enumerate(rows):
            print('      x=%-2d %s' % (x, row))
    print()


def main():
    written = []
    for piece, name in ((end(), 'bucklebury_end'), (middle(), 'bucklebury_middle')):
        path = write(piece, name)
        root, back, piers = verify(piece, path)
        written.append((piece, path, piers))
    for piece, path, piers in written:
        show(piece, path, piers)
    print('wrote %s' % ', '.join(path.name for _, path, _ in written))
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
