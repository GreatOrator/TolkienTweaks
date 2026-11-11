#!/usr/bin/env python3
"""Takes the chunks a cut generated back out of a world's files, and leaves everything else alone.

`GoblinCave.cut` asks for every chunk its plan touches before it cuts, so ground nobody has walked
gets made. When the cave moves - a new pin, a changed plan - the ground the old cut made is still
sitting in the save. This takes those chunks out of the world's region, entities and poi files, so
the world makes them again from the seed, and touches no other chunk.

Whole region files are not the unit here on purpose: a region file holds 32 by 32 chunks, so the four
files this cave lands in hold 4,096 chunks between them, and deleting them would take a 1,024 block
square of somebody's world to get rid of fifty-four chunks.

    python chunk_trim.py --world "run/saves/Serious Test" --pin goblins
    python chunk_trim.py --world "run/saves/Serious Test" --pin goblins --apply

Nothing is written without `--apply`, and nothing is written while the world is open: Minecraft holds
`session.lock` while a save is loaded and would write its own copy of the chunks back over the top.
Each file is copied to a backup folder outside the save before it is rewritten, and the rewritten file
is read back and checked - every chunk's sector inside the file, every length inside its sectors, no
two chunks sharing a sector - before the next one is touched.

`--margin` widens the sweep by whole chunks, for ground somebody stood on while looking at the cave.
`--radius` sweeps a square of chunks around the pin instead, for ground made by walking rather than by
the cut. `--into` names the direction the hill rises if the save should not be read to work it out.
It is read from the ground under the pin by default, so **once a cut's own chunks have been taken out
pass `--into`**: there is no ground left under the door to read, and the tool says so.
"""
import argparse
import pathlib
import shutil
import struct
import sys
import time

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import cave_section as cs

SECTOR = 4096                # bytes in a region file's sector
HEADER = SECTOR * 2          # 1024 chunk locations, then 1024 timestamps
SIDE = 32                    # chunks a region file holds, each way
FOLDERS = ("region", "entities", "poi")


def world_open(world):
    """Whether a save is loaded: Minecraft holds session.lock while it is."""
    lock = world / "session.lock"
    if not lock.exists():
        return False
    try:
        with open(lock, "r+b"):
            return False
    except OSError:
        return True


def chunks_of(x0, x1, z0, z1):
    """Every chunk of a block rectangle, as {(region x, region z): {index in the region}}."""
    out = {}
    for cx in range(x0 >> 4, (x1 >> 4) + 1):
        for cz in range(z0 >> 4, (z1 >> 4) + 1):
            out.setdefault((cx >> 5, cz >> 5), set()).add((cx & 31) + (cz & 31) * SIDE)
    return out


def read_region(path):
    """A region file as (locations, timestamps, chunks): each chunk its own bytes, padded."""
    data = path.read_bytes()
    if len(data) < HEADER:
        raise SystemExit("%s is shorter than a region header" % path)
    locations, stamps, chunks = [], [], []
    for index in range(SIDE * SIDE):
        entry = struct.unpack_from(">I", data, index * 4)[0]
        stamps.append(struct.unpack_from(">I", data, SECTOR + index * 4)[0])
        locations.append((entry >> 8, entry & 0xFF))
    for index, (offset, count) in enumerate(locations):
        if offset == 0 and count == 0:
            chunks.append(None)
            continue
        if count == 0:
            raise SystemExit("%s keeps chunk %d outside itself; refusing to touch the file"
                             % (path, index))
        start, end = offset * SECTOR, (offset + count) * SECTOR
        if end > len(data):
            raise SystemExit("%s runs off its own end at chunk %d" % (path, index))
        chunks.append(data[start:end])
    return locations, stamps, chunks


def write_region(path, stamps, chunks):
    """Packs the chunks that are left back into a region file, in index order."""
    locations = bytearray(SECTOR)
    times = bytearray(SECTOR)
    body = bytearray()
    offset = 2
    for index, blob in enumerate(chunks):
        if blob is None:
            continue
        count = (len(blob) + SECTOR - 1) // SECTOR
        struct.pack_into(">I", locations, index * 4, (offset << 8) | count)
        struct.pack_into(">I", times, index * 4, stamps[index])
        body += blob[:count * SECTOR].ljust(count * SECTOR, b"\0")
        offset += count
    path.write_bytes(bytes(locations) + bytes(times) + bytes(body))
    return offset - 2


def check_region(path):
    """Reads a rewritten file back: every chunk inside the file, no two of them sharing a sector."""
    data = path.read_bytes()
    if len(data) % SECTOR:
        raise SystemExit("%s is not a whole number of sectors" % path)
    total = len(data) // SECTOR
    taken, chunks = set(), 0
    for index in range(SIDE * SIDE):
        entry = struct.unpack_from(">I", data, index * 4)[0]
        offset, count = entry >> 8, entry & 0xFF
        if offset == 0 and count == 0:
            continue
        if offset < 2 or count == 0 or offset + count > total:
            raise SystemExit("%s has a chunk pointing outside itself at %d" % (path, index))
        length = struct.unpack_from(">I", data, offset * SECTOR)[0]
        if not 5 <= length <= count * SECTOR:
            raise SystemExit("%s has a chunk %d bytes long in %d sectors at %d"
                             % (path, length, count, index))
        if taken & set(range(offset, offset + count)):
            raise SystemExit("%s has two chunks in one sector at %d" % (path, index))
        taken |= set(range(offset, offset + count))
        chunks += 1
    return chunks, len(data)


def trim(path, indexes, apply, backup, world, says):
    """Takes some chunks out of one file. Returns (chunks before, removed, bytes before, after)."""
    locations, stamps, chunks = read_region(path)
    here = sum(1 for blob in chunks if blob is not None)
    gone = [index for index in indexes if chunks[index] is not None]
    if not gone:
        says.append("  %s: nothing of the cut in it" % path.name)
        return here, 0, path.stat().st_size, path.stat().st_size
    before = path.stat().st_size
    if not apply:
        says.append("  %s: %d of %d chunks, %s" % (path.name, len(gone), here, human(before)))
        return here, len(gone), before, before
    kept = [path.relative_to(world)]
    place = backup / kept[0]
    place.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(path, place)
    for index in gone:
        chunks[index] = None
    write_region(path, stamps, chunks)
    after_chunks, after = check_region(path)
    if after_chunks != here - len(gone):
        raise SystemExit("%s came back with %d chunks, not %d" % (path, after_chunks, here - len(gone)))
    says.append("  %s: %d of %d chunks taken out, %s -> %s (backed up)"
                % (path.name, len(gone), here, human(before), human(after)))
    return here, len(gone), before, after


def human(size):
    for unit in ("B", "KB", "MB"):
        if size < 1024 or unit == "MB":
            return "%.1f %s" % (size, unit) if unit != "B" else "%d B" % size
        size /= 1024.0
    return "%d B" % size


def rectangle(args, world):
    """The block rectangle the cut reaches, as the cut itself works it out."""
    if args.at:
        x, z = (int(v) for v in args.at.split(","))
        door = (x, 0, z)
    else:
        door, where = cs.pin_at(world, args.pin)
        if door is None:
            raise SystemExit("no pin called %r in %s" % (args.pin, world))
    if args.radius:
        cx, cz = door[0] >> 4, door[2] >> 4
        span = args.radius + args.margin
        return (cx - span) << 4, ((cx + span + 1) << 4) - 1, (cz - span) << 4, ((cz + span + 1) << 4) - 1, \
            "a square of %d chunks around the pin" % (span * 2 + 1)
    if args.into and args.into != "auto":
        direction = args.into
    else:
        patch = cs.ground(world, (door[0], door[2]), cs.FAR + 4)
        here = patch.get((door[0], door[2]))
        if here is None:
            raise SystemExit("no ground under the pin: is the chunk there?")
        direction, _ = cs.uphill(patch, (door[0], door[2]), here)
    dx, dz = cs.DIRECTIONS[direction]
    sx, sz = -dz, dx
    xs, zs = [], []
    for a in (-cs.OUTSIDE, cs.FAR):
        for s in (-cs.FAR_SIDE, cs.FAR_SIDE):
            xs.append(door[0] + dx * a + sx * s)
            zs.append(door[2] + dz * a + sz * s)
    margin = args.margin * 16
    return (min(xs) - margin, max(xs) + margin, min(zs) - margin, max(zs) + margin,
            "the cut's own rectangle, into the hill towards %s" % direction)


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--world", required=True, help="saved world folder holding level.dat")
    parser.add_argument("--pin", default="goblins", help="the pin the cut's door was at")
    parser.add_argument("--at", default=None, metavar="X,Z", help="a block instead of a pin")
    parser.add_argument("--dimension", default="tolkienmobs/arda", help="which dimension's chunks")
    parser.add_argument("--into", default="auto", choices=["auto"] + list(cs.DIRECTIONS),
                        help="the way the hill rises, or auto to read the save for it")
    parser.add_argument("--margin", type=int, default=0, help="whole chunks to widen the sweep by")
    parser.add_argument("--radius", type=int, default=0, help="chunks around the pin instead")
    parser.add_argument("--apply", action="store_true", help="write the files, not just report")
    parser.add_argument("--backup", default=None, help="where the copies go (default: run/chunk-trim-backup)")
    args = parser.parse_args(argv[1:])

    world = pathlib.Path(args.world)
    if not (world / "level.dat").exists():
        raise SystemExit("%s is not a save: no level.dat in it" % world)
    x0, x1, z0, z1, what = rectangle(args, world)
    wanted = chunks_of(x0, x1, z0, z1)
    chunks = sum(len(v) for v in wanted.values())
    print("%s: %s" % (world.name, what))
    print("  blocks x %d..%d, z %d..%d - %d chunks in %d region files"
          % (x0, x1, z0, z1, chunks, len(wanted)))
    if args.apply and world_open(world):
        raise SystemExit("that save is open in Minecraft: close the world first, or the game will "
                         "write its own chunks back over these")

    folder = world / "dimensions" / args.dimension
    if not folder.is_dir():
        raise SystemExit("no %s in %s" % (args.dimension, world))
    stamp = time.strftime("%Y%m%d-%H%M%S")
    backup = pathlib.Path(args.backup) if args.backup else \
        pathlib.Path(__file__).resolve().parent.parent / "run" / "chunk-trim-backup" / stamp
    says, total_before, total_after, total_gone = [], 0, 0, 0
    for name in FOLDERS:
        for (rx, rz), indexes in sorted(wanted.items()):
            path = folder / name / ("r.%d.%d.mca" % (rx, rz))
            if not path.exists():
                continue
            here, gone, before, after = trim(path, indexes, args.apply, backup, world, says)
            total_before += before
            total_after += after
            total_gone += gone
    print("\n".join(says) if says else "  nothing of the cut is in those files")
    if args.apply:
        print("  %d chunks taken out, %s -> %s" % (total_gone, human(total_before), human(total_after)))
        print("  the files that were rewritten are copied under %s" % backup)
    else:
        print("  %d chunks to take out; nothing was written (add --apply)" % total_gone)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
