#!/usr/bin/env python3
"""Reads back the spots a world has been given by `/tmpin`.

A pin is a `SavedData` - `tolkienmobs:pins` - so a spot chosen with the command is written into
the world's own data folder and holds only for that world; a line copied out of the command's
answer into `Pins.SOURCE` is the one that holds for every world. Once the game has saved, the
world's pins are readable, which is what this is for: a door scouted in game is read here rather
than typed back in from a debug screen.

    python pins.py --world "run/saves/Road Test"

`/save-all` in game (or leaving to the world list) is what puts the file down: the store is
marked dirty when it changes and written with the rest of the world's data.

The key names are whatever `/tmpin` was told - `goblins`, or a template id from `Pins.SOURCE`.
The block is the one the scout stood on, so its y is the surface they were standing at and not a
measurement of the ground under a footprint.
"""
import argparse
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import grab_build as gb


def pins_in(node):
    """Every {key, at} pair anywhere in a tag, whatever shape the file wrapped them in."""
    found = []
    if isinstance(node, dict):
        if "key" in node and "at" in node:
            try:
                found.append((str(node["key"]), tuple(int(v) for v in node["at"])))
            except (TypeError, ValueError):
                pass
        else:
            for child in node.values():
                found.extend(pins_in(child))
    elif isinstance(node, list):
        for child in node:
            found.extend(pins_in(child))
    return found


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--world", required=True, help="the save folder, e.g. run/saves/Road Test")
    args = parser.parse_args(argv[1:])

    folder = pathlib.Path(args.world)
    if not folder.is_dir():
        raise SystemExit("no world at %s" % folder)

    # The store is written under a `data` folder, and which one depends on the level that asked for
    # it: the overworld's lands at dimensions/minecraft/overworld/data, not at the world's own root.
    seen = 0
    for path in sorted(folder.rglob("*.dat")):
        if "data" not in path.parts:
            continue
        try:
            root = gb.read_nbt(path.read_bytes())
        except Exception:
            continue
        rows = pins_in(root)
        if not rows:
            continue
        print("%s:" % path.relative_to(folder))
        for key, at in rows:
            print("  %-48s %d %d %d" % (key, at[0], at[1], at[2]))
        seen += len(rows)

    if not seen:
        raise SystemExit("no pins in %s - a pin is written when the world saves" % folder)
    print("\n%d pin(s)" % seen)


if __name__ == "__main__":
    main(sys.argv)
