#!/usr/bin/env python3
"""Takes a build's dress ring out of a save, so the world makes that ground again.

`Dressing` dresses a ring of country round a build's footprint: every column within
`CityCommands.DRAPE_REACH` of the box that is not in the box itself. When the drape's rules change, or
when it laid something that wants to be gone, the only way to see the new rules on that ground is to
have it made again - the pass reads what is already there and cannot tell its own fill from the country.

The ring falls on chunk borders for a build whose footprint does, which Bree's does: the box is chunks
x 64..94 by z -16..25 and the reach round it is x 62..96 by z -18..27, so the ring is exactly the
chunks that lie wholly outside the footprint and **nothing of the build is in any of them**. Region
files are not deleted, only the ring's chunks in them: `chunk_trim` does the reading, the backup and
the read-back check.

    python tools/trim_ring.py --world "run/saves/Progress Check 1"
    python tools/trim_ring.py --world "run/saves/Progress Check 1" --apply

Nothing is written without `--apply`, and nothing is written while the world is open. The ring is
made again the next time the ground is asked for - a raise asks for every chunk of the reach - so a
world whose build raises itself from a note needs that note removed too, or the ring stays unmade.
`--footprint` names another build's box, in blocks, as x0,x1,z0,z1.
"""

import argparse
import pathlib
import sys
import time

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import chunk_trim as ct

FOLDERS = ct.FOLDERS
#: Bree's box: the cut's footprint, which is chunks x 64..94 by z -16..25.
BREE_BOX = (1024, 1519, -256, 415)
#: How far the drape dresses, in blocks: `CityCommands.DRAPE_REACH`.
REACH = 32


def chunks_of(x0, x1, z0, z1):
    """Every chunk of a block rectangle, as {(region x, region z): {index in the region}}."""
    out = {}
    for cx in range(x0 >> 4, (x1 >> 4) + 1):
        for cz in range(z0 >> 4, (z1 >> 4) + 1):
            out.setdefault((cx >> 5, cz >> 5), set()).add((cx & 31) + (cz & 31) * ct.SIDE)
    return out


def ring(box, reach):
    """The ring's chunks: the reach round the box, less every chunk that holds any of the box."""
    x0, x1, z0, z1 = box
    out = chunks_of(x0 - reach, x1 + reach, z0 - reach, z1 + reach)
    inside = chunks_of(x0, x1, z0, z1)
    for region, indexes in inside.items():
        out.get(region, set()).difference_update(indexes)
    return {region: indexes for region, indexes in out.items() if indexes}


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--world", required=True, help="saved world folder holding level.dat")
    parser.add_argument("--dimension", default="tolkienmobs/arda", help="which dimension's chunks")
    parser.add_argument("--footprint", default=None, metavar="X0,X1,Z0,Z1",
                        help="the build's box in blocks (default: Bree's, 1024,1519,-256,415)")
    parser.add_argument("--reach", type=int, default=REACH, help="how far the drape dresses")
    parser.add_argument("--apply", action="store_true", help="write the files, not just report")
    parser.add_argument("--backup", default=None, help="where the copies go (default: run/chunk-trim-backup)")
    args = parser.parse_args(argv[1:])

    world = pathlib.Path(args.world)
    if not (world / "level.dat").exists():
        raise SystemExit("%s is not a save: no level.dat in it" % world)
    box = BREE_BOX if not args.footprint else tuple(int(v) for v in args.footprint.split(","))
    if len(box) != 4:
        raise SystemExit("--footprint wants x0,x1,z0,z1")
    wanted = ring(box, args.reach)
    chunks = sum(len(indexes) for indexes in wanted.values())
    print("%s: the ring round x %d..%d by z %d..%d, %d blocks of reach"
          % (world.name, box[0], box[1], box[2], box[3], args.reach))
    print("  %d chunks in %d region files, none of them holding any of the build"
          % (chunks, len(wanted)))
    if args.apply and ct.world_open(world):
        raise SystemExit("that save is open in Minecraft: close the world first, or the game will "
                         "write its own chunks back over these")
    folder = world / "dimensions" / args.dimension
    if not folder.is_dir():
        raise SystemExit("no %s in %s" % (args.dimension, world))

    stamp = time.strftime("%Y%m%d-%H%M%S")
    backup = pathlib.Path(args.backup) if args.backup else \
        pathlib.Path(__file__).resolve().parent.parent / "run" / "chunk-trim-backup" / stamp
    says, gone_total, before_total, after_total = [], 0, 0, 0
    for name in FOLDERS:
        for (rx, rz), indexes in sorted(wanted.items()):
            path = folder / name / ("r.%d.%d.mca" % (rx, rz))
            if not path.exists() or path.stat().st_size < ct.HEADER:
                # A poi or entity file the game left empty has no chunks in it to take out.
                continue
            here, gone, before, after = ct.trim(path, indexes, args.apply, backup, world, says)
            gone_total += gone
            before_total += before
            after_total += after
    print("\n".join(says) if says else "  nothing is in those files")
    if args.apply:
        print("  %d chunks taken out, %s -> %s" % (gone_total, ct.human(before_total), ct.human(after_total)))
        print("  the files that were rewritten are copied under %s" % backup)
        print("  the ground is made again when the build next asks for it: a raise asks for every")
        print("  chunk of the reach, so a build that raises itself from a note needs the note removed")
    else:
        print("  %d chunks to take out; nothing was written (add --apply)" % gone_total)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
