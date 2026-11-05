# Pinned ground

Where a location stands, when somebody has walked the world and said so.

## Why this exists

The campaign places ground by asking the drawing: a country's middle, then a cell's middle, then the
nearest column inside that search whose footprint will sit level and dry. That answers "somewhere in
this part of the country", which is the right answer for a world nobody has looked at — but it is not
an answer to "here".

Scouting a fresh world and reporting coordinates is the better answer, so a location can now be
written down as the block it stands on. The table is `Pins.SOURCE`, and both things that place ground
read it before they read the drawing:

| what was scouted | key | asked by |
| --- | --- | --- |
| a **name** — a circle of ground announced on arrival, with folk standing in it | the place id: `rivendell`, `thorenhad`, `barachen`, `ford` | `Places.ensure` |
| a **building** — a template raised for the player who was sent to it | the template id: `tolkienmobs:key_structures/wild_tent`, `tolkienmobs:ttmruin_large_2` | `Sites.survey` |

## Pinning one

One line in `Pins.SOURCE`:

```java
"thorenhad", new BlockPos(4864, 157, -3328),
```

The y is the surface the scout stood on. It is a note, not a measurement: a building is still seated
by the laying, which reads the ground under the footprint and sinks or underpins it, so a y a block or
two out changes nothing. The x and z are taken as given.

## Pinning one in-world

`/tmpin place <id>` and `/tmpin build <name>` pin the caller's feet, apply to this world at once, and
print the source line to copy. `/tmpin list` shows what is pinned; `/tmpin remove <name>` forgets a
world pin. A world pin is saved with the world and wins over a source pin; a source pin holds in every
world, including one nobody has walked.

`/tmpin checklist` lists the ground both chapters ask for - six names and nine buildings - and how each
one stands: pinned in this world, pinned for every world, or still left to the drawing. It is the
scouting job in one answer, and it places nothing.

## What a pin changes

- **A name** stands exactly at the pinned block, and the spacing rule and country requirement are
  skipped — a name the books give a country does not have to be searched for in it. That is what lets
  a name stand where the drawing paints nothing at all: **the valley** is the case that matters, since
  no column of the map samples as `IMLADRIS` while the city is only planned. Pinned by hand, Rivendell
  is ground like any other and the pages waiting on Elrond, Glorfindel and Legolas stop waiting on the
  drawing.
- **A building** is written down at the pinned block and the map survey is skipped. The laying still
  measures its footprint and seats it, so a building pinned on ground the world made badly is lifted
  to the highest column and underpinned rather than left hanging — the pin decides *where*, the
  ground decides *how it sits*.
- **Anything not pinned** is surveyed exactly as before, so the campaign walks with an empty table.

## Chapter one's ground, and what to scout

| pages | what | key | note |
| --- | --- | --- | --- |
| 1, 5, 7, 12, 13, 15 | Rivendell, where the West's Elves stand | `rivendell` | the valley floor; unblocks every Elrond/Glorfindel page |
| 2, 4, 8 | Elladan's camp (a pitched tent) | `tolkienmobs:key_structures/wild_tent` | level ground, a walk from the road |
| 3 | the wood ruin the black horse is found in | `tolkienmobs:ttmruin_large_2` | about fourteen blocks across; a standing wall in the channels south of the road |
| 6 | the ford the beryl is carried to | `ford` | **must be water the road can cross** — the drawn cell has neither |
| 9 | Bandrith's, Sholdroth's and the third cave | `tolkienmobs:key_structures/troll_cave`, `.../troll_cave_2`, `.../troll_cave_3` | each is a crag cut by `tools/make_troll_cave.py`, so the ground wants a hillside |
| 10 | Barachen's fire | `barachen` | level ground south of the road |
| 2 (chapter two) | Glóin's camp at the pass mouth | `tolkienmobs:key_structures/gloin_camp` | 28 by 8 by 22, door in the middle of its south wall |
| later | Gundabad, Weathertop, the castle and the arena | `tolkienmobs:key_structures/gundabad` and the rest | Gundabad is 122 by 183 by 26; the rest want a hill or a courtyard |
