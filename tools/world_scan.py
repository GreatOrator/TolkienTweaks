"""Find a placed template inside a saved world, check it, and cut pieces back out of it.

The structure test world is the build as it will look in play: the template placed once, then whatever
was put into it by hand afterwards. Reading it back is the other direction from building it - the world
is the source and the piece files are the output - so this finds the corner the template was placed at,
compares the world against what the template says should be there, and names the differences.

Blocks are compared by name, because a template writes a default state with no properties and a world
always writes them out; deepslate and deepslate[axis=y] are the same block. A difference in properties
is reported on its own, so a real one (a stair turned round, a lantern hung instead of stood) does not
hide among thousands of default states.
"""

import argparse
import collections
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))

import grab_build as gb
import gundabad_keep as gk

HERE = pathlib.Path(__file__).resolve().parent.parent
TEMPLATE = HERE / "src/main/resources/data/tolkienmobs/structure/key_structures/gundabad.nbt"
MOD = "tolkienmobs:"
SAMPLE = 4000


def region_dir(world, dimension):
    """The region folder for a world, whether it is a 26.1 dimension layout or the older one."""
    root = pathlib.Path(world)
    if dimension in ("", "overworld", "minecraft:overworld"):
        spread = root / "region"
        return spread if spread.is_dir() else root / "dimensions" / "minecraft" / "overworld" / "region"
    namespace, _, name = dimension.partition(":")
    return root / "dimensions" / (namespace or "minecraft") / name / "region"


def scan(world, dimension):
    """The block bounds of every chunk carrying the pack's own blocks, and the names found there."""
    folder = region_dir(world, dimension)
    if not folder.is_dir():
        raise SystemExit("no region folder at %s" % folder)
    names = set()
    xs, zs, ys = [], [], []
    files = 0
    for path in sorted(folder.glob("r.*.*.mca")):
        region_x, region_z = int(path.name.split(".")[1]), int(path.name.split(".")[2])
        files += 1
        for local_x, local_z, chunk in gb.read_region(path):
            carried = False
            for section in chunk.get("sections", []):
                states = section.get("block_states")
                if not states:
                    continue
                here = {entry.get("Name", "") for entry in states.get("palette", [])}
                if any(name.startswith(MOD) for name in here):
                    carried = True
                    names.update(name for name in here if name.startswith(MOD))
                    ys.append(section.get("Y", 0) * 16)
            if carried:
                xs.append((region_x * 32 + local_x) * 16)
                zs.append((region_z * 32 + local_z) * 16)
    if not xs:
        raise SystemExit("no %s blocks anywhere under %s" % (MOD, folder))
    low = (min(xs), min(ys), min(zs))
    high = (max(xs) + 15, max(ys) + 15, max(zs) + 15)
    return names, len(xs), files, low, high


def locate(blocks, template):
    """The corner the template went down at, voted for by its rarest block and scored by name."""
    counts = collections.Counter(state for state in template.values() if state[0] != "minecraft:air")
    scarce = [state for state in counts if state[0].startswith(MOD)]
    if not scarce:
        raise SystemExit("the template has no %s blocks to look for" % MOD)
    rare = min(scarce, key=lambda state: counts[state])
    here = [pos for pos, state in template.items() if state == rare]
    there = [pos for pos, state in blocks.items() if state == rare]
    if not there:
        raise SystemExit("no %r in the world - is this the right save?" % (rare,))
    solid = [(pos, state) for pos, state in template.items()
             if state[0] != "minecraft:air" and pos[0] % 7 == 0 and pos[1] % 5 == 0]
    if len(solid) > SAMPLE:
        step = len(solid) / SAMPLE
        solid = [solid[int(n * step)] for n in range(SAMPLE)]
    best, best_score = None, -1
    for corner in here:
        for world_pos in there:
            anchor = tuple(world_pos[axis] - corner[axis] for axis in range(3))
            score = sum(1 for pos, state in solid
                        if (blocks.get((pos[0] + anchor[0], pos[1] + anchor[1], pos[2] + anchor[2]))
                            or ("",))[0] == state[0])
            if score > best_score:
                best, best_score = anchor, score
    return best, best_score, len(solid)


def compare(blocks, template, anchor):
    """The template against the world cell by cell: exact, same block with another state, another block."""
    exact = 0
    states = collections.Counter()
    swapped = collections.Counter()
    where = collections.defaultdict(list)
    for pos, state in template.items():
        found = blocks.get((pos[0] + anchor[0], pos[1] + anchor[1], pos[2] + anchor[2]))
        if found == state:
            exact += 1
            continue
        name = found[0] if found else "nothing"
        if name == state[0]:
            states[(state, found)] += 1
        else:
            swapped[(state[0], name)] += 1
            if len(where[(state[0], name)]) < 5:
                where[(state[0], name)].append((pos, name))
    return exact, states, swapped, where


def cut(blocks, anchor, out):
    """The six inner-keep pieces as the world has them now, one file each."""
    out.mkdir(parents=True, exist_ok=True)
    written = []
    for name, dx, dz in gk.INNER:
        low = (anchor[0] + dx, anchor[1] + gk.LOWER, anchor[2] + dz)
        piece = {}
        for y in range(12):
            for z in range(gk.EDGE):
                for x in range(gk.EDGE):
                    state = blocks.get((low[0] + x, low[1] + y, low[2] + z))
                    if state is not None:
                        piece[(x, y, z)] = state
        path = out / (name + ".nbt")
        if gb.write_tile(piece, (0, 0, 0), (gk.EDGE, 12, gk.EDGE), path, keep_air=True):
            written.append((name, len(piece), path.stat().st_size / 1024, piece))
    return written


def against(pieces, folder):
    """Each cut piece against the file the keep was built from, after the same materiallling."""
    for name, cells, _, piece in pieces:
        path = folder / (name + ".nbt")
        if not path.exists():
            print("  %-4s no file at %s" % (name, path))
            continue
        old, size = gb.blocks_from_template(gb.read_nbt(path.read_bytes()))
        index = {(pos[0], pos[1], pos[2]): gk.carry(state) for pos, state in old.items()}
        same = sum(1 for pos, state in index.items() if piece.get(pos) == state)
        gone = collections.Counter(state[0] for pos, state in index.items() if piece.get(pos) is None)
        fresh = collections.Counter(state[0] for pos, state in piece.items()
                                    if pos not in index and state[0] != "minecraft:air")
        changed = sum(1 for pos, state in index.items()
                      if pos in piece and piece[pos] != state and piece[pos] is not None)
        print("  %-4s file %dx%dx%d -> cut %d cells; %d as before, %d changed state, %d gone"
              % (name, size[0], size[1], size[2], len(piece), same, changed, sum(gone.values())))
        if fresh:
            print("       %d blocks that were not there: %s"
                  % (sum(fresh.values()), ", ".join("%s x%d" % pair for pair in fresh.most_common(6))))


def main():
    parser = argparse.ArgumentParser(description="Read a placed template back out of a saved world.")
    parser.add_argument("--world", required=True, help="saved world folder (the one holding level.dat)")
    parser.add_argument("--dim", default="", help="dimension folder; blank for the overworld")
    parser.add_argument("--cut", help="folder to write the six inner keep pieces into")
    args = parser.parse_args()

    names, chunks, files, low, high = scan(args.world, args.dim)
    print("  %s: %d region files, %d chunks carry the pack's blocks" % (args.world, files, chunks))
    print("  bounds x %d..%d, y %d..%d, z %d..%d" % (low[0], high[0], low[1], high[1], low[2], high[2]))
    print("  %d kinds of its own blocks there" % len(names))

    blocks, size, _ = gb.blocks_from_world(args.world, args.dim, low, high)
    template, template_size = gb.blocks_from_template(gb.read_nbt(TEMPLATE.read_bytes()))
    anchor, score, tried = locate(blocks, template)
    print("  template corner: x %d, y %d, z %d  (%d of %d sampled cells are the same block)"
          % (anchor[0], anchor[1], anchor[2], score, tried))

    exact, states, swapped, where = compare(blocks, template, anchor)
    print("  %d of %d cells are exactly as the template has them" % (exact, len(template)))
    print("  %d cells are the same block in another state" % sum(states.values()))
    for (before, after), count in states.most_common(4):
        print("     %-40s -> %s x%d" % (str(before), after, count))
    print("  %d cells hold a different block" % sum(swapped.values()))
    for (before, after), count in swapped.most_common(10):
        sample = where[(before, after)][:3]
        print("     %-40s -> %-40s x%d %s" % (before, after, count, sample))

    if args.cut:
        pieces = cut(blocks, anchor, pathlib.Path(args.cut))
        for name, cells, kb, _ in pieces:
            print("  wrote %-4s %6d cells  %5.0f KB" % (name, cells, kb))
        print("  against the files it was built from:")
        against(pieces, gk.STRUCTS)


if __name__ == "__main__":
    main()
