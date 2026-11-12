# Structure void, air, and what a template does to the ground

A template's job in this mod is usually to *add* something to a hillside without taking the hillside away. The
difference between the two is not obvious from the file, and it cost us the shelter camp's outcrop, so it is
written down here.

## What the file says

A `.nbt` structure template is a box, a palette, and a list of placed cells:

- `size` — the box, in blocks.
- `palette` — every block state the template uses, each with a name and any properties.
- `blocks` — one entry per *placed* cell: a palette index and a position.

Nothing else. Cells that are not in `blocks` do not exist as far as the template is concerned, and that is the
whole mechanism:

| in the file | when the structure is placed |
| --- | --- |
| a cell listed as `minecraft:air` | the block is **set to air** — terrain there is cleared |
| a cell listed as any other block | the block is placed — terrain there is replaced |
| a cell **not listed at all** | nothing happens — terrain there **stays as it is** |

## What structure void is for

`minecraft:structure_void` is the builder's way of saying "leave this cell alone". In the world it is invisible
and has no collision; the inside of a structure built with it looks like open holes while the builder works.

It is **not saved**. The Minecraft Wiki is explicit:

> "Structure voids can be placed as part of a structure and are ignored when the structure file is saved. This
> results in the blocks at the location where the structure is placed being maintained and not being overridden.
> For example, if a player saves a 2 block high structure with air at the top and a structure void at the bottom,
> and loads it into a 2 block high area of stone, the top block becomes air but the bottom stays stone."
> — [Structure Void, Minecraft Wiki](https://minecraft.wiki/w/Structure_Void)

So the void block is a *tool for the structure block*, not a block that travels. What travels is the absence of
an entry. A save with void in it comes out **sparse**: the box is full size, and every void cell is simply
missing from `blocks`.

That also means a template that lists `minecraft:structure_void` in its `blocks` was not written by a structure
block — a hand edit or another tool put it there — and whether a given placer skips it is an implementation
detail nobody should rely on. **A template should never list structure void.** If a cell is meant to keep the
ground, leave it out.

## The trap

A saved build usually comes out **dense**: every cell in the box is written, air included. That is fine for a
floating build, and ruinous for one standing in a hill — the box's air is a cube of clearance around the build,
which is exactly the square of removed hillside we saw at the shelter camp.

Two ways to fix a dense template, and only one of them works:

- ~~Replace the outside air with `structure_void`~~ — the cells are still listed, so they are still described to
  the placer. It is not a save, and it is not safe.
- **Drop the outside cells entirely.** The template keeps the room's own air (so the room is carved) and lists
  nothing outside it (so the hill is untouched). This is what a structure block would have written.

## The audit

`tools/structure_void_audit.py` reads every template under `src/main/resources/data` and reports the shape of it:

```
python tools/structure_void_audit.py           # report
python tools/structure_void_audit.py --apply   # strip what a save would not have written
```

Run over the whole tree:

- **662 templates**, of which **400 are sparse** — fewer entries than their own box, so they leave ground alone.
- **42,763,308 listed blocks**, with **614,650 air cells** listed. Air in a template is deliberate: it is how a
  room is cut out of rock, and it is what the camp's own hollow is made of.
- **15 templates** still carried a `minecraft:structure_void` palette entry — `faction_hall` and its three
  variants, `ttmhouse_dwarf*`, `ttmhouse_human*`, `ttmhouse_hobbit_house_3`. In every one of them the entry was
  **dead**: zero cells referenced it. A leftover of however those files were last saved.
- **54 dead palette entries** and **31 duplicated states** across the tree — palette entries nothing references,
  and the same state listed twice.

`--apply` drops every listed void cell (there were none) and rebuilds each palette from the states actually used,
deduplicating and renumbering the blocks with it. A re-run of the audit now reads **0 templates carrying void, 0
void cells, 0 dead entries, 0 duplicated states**, with the listed-block and air totals exactly where they were —
42,763,308 and 614,650 — which is the check that no cell moved. Every rewritten file is read back and compared cell by cell
against what it was, name, properties, position and block entity data, so a rewrite can only ever remove palette
entries that no cell pointed at.

## For new builds

1. Save with a structure block; do not hand-write `structure_void` into a file.
2. Check the shape before shipping it: a build that sits in a hill should list far fewer entries than its box,
   and its air should be the room's own air, not the air around the outside.
3. A build that should clear the hill around it is dense on purpose — the crags and the camps' mouth boxes are.
