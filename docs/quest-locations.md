# Quest locations to scout

Every location the campaign's pages walk to, **except Rivendell** - already pinned at `5200, 75, -3370`,
the city's own low corner, so nothing more is wanted for it.

Fourteen left: nine in the Trollshaws - the chapter-one cluster and the key-stone's vault - one at the mouth
of the High Pass, one in the Misty Mountains, and three in Angmar, two of them the last act's.

## While you are out there

| command | pins | key |
| --- | --- | --- |
| `/tmpin place <id>` | a **name** - a circle of ground announced on arrival, with folk standing in it | the catalogue id: `thorenhad`, `ford`, `barachen`, `keystone`, `carndum` |
| `/tmpin build <name>` | a **building** - a template the campaign raises for the page-taker | the template id: `tolkienmobs:key_structures/elledancamp` |
| `/tmpin checklist` | the fifteen, marked `this world` / `every world` / `not pinned` | - |
| `/tmpin list` | what this world has pinned, and what the build pins for every world | - |

Both pin commands need **creative or gamemaster (op 2)** and only work standing in `tolkienmobs:arda`. Each
prints the `Pins.SOURCE` line for that spot - send the lines back and they go into the build so every world
has the same ground.

A pin decides **where**; the ground still decides **how it sits**. The footprint is measured, the build is
seated on its highest column and underpinned underneath, so the `y` you pin at is a note of where you stood,
not a measurement - a block or two out is not an error.

One place can be both kinds. Gundabad, Glóin's camp and Weathertop each have a name and a template; the two
branches are separate on purpose. Pin the branch the pages actually use (marked below), and the other only if
you want the name for later.

## Where the drawing would put them

Cell middles, as blocks: `x = (column - 10) * 512 + 256`, `z = (row - 20) * 512 + 256`. A pin overrides the
cell; a location with neither pin nor cell searches out from its country's middle.

| region | `/tmtp` point | covers |
| --- | --- | --- |
| Trollshaws | `4352, 166, -2816` | the whole chapter-one cluster and the key-stone |
| Gladden | `8192, 203, -3328` | Glóin's camp, at the pass's north side |
| Hithaeglir | `5632, 180, -4608` | Gundabad, in the range itself |
| Angmar | `4864, 204, -7168` | Carn Dûm, the castle and the arena |

## Chapter one - the Trollshaws

| pages | location | pin as | drawing | footprint | what to look for |
| --- | --- | --- | --- | --- | --- |
| 2, 4, 8 | Thorenhad, where Elladan is heard | `place thorenhad` | Trollshaws, cell `19,13` - `4864, -3328` | a 32-block circle | level ground a walk **east** of the valley, off the road |
| 2, 4, 8 | Elladan's camp itself, the ground those pages are spoken on | `build camp` | same cell, and already a worldgen anchor there (see below) | 16 by 16, 8 high | about 16 by 16 of level ground; the camp stands where its pages are heard |
| 3 | the wood ruin the black horse lies in | `build ruin` | Trollshaws, cell `19,14` - `4864, -2816` | 14 by 13, **29 high** | a standing wall in the narrow channels **south of the road**; tall and narrow rather than broad |
| 6 | the ford the beryl is carried to | `place ford` | Trollshaws, cell `17,13` - `3840, -3328` | a 32-block circle | **water the road can cross** - the drawn cell has neither water nor the road |
| 9 | the three caves (one page, three chambers) | `build cave`, `cave2`, `cave3` | `17,15` = `3840, -2304`; `18,16` = `4352, -1792`; `19,16` = `4864, -1792` | each 56 by 56, 36 high | three separate hillsides; a crag is cut rock and seats **nine to ten blocks into** the ground, so it wants a slope rather than a flat field |
| 10 | Barachen's fire | `place barachen` | Trollshaws, cell `18,14` - `4352, -2816` | a 32-block circle | level ground **south of the road** |

Elladan's camp is the one location here already placed for every world: `PINNED_BUILDS` carries it as a
generation anchor at `4864, 0, -3328`, the middle of Thorenhad's own cell, so a fresh world grows the camp
there whether anybody scouted it or not. A `/tmpin build camp` pin is still what a page-raised camp obeys, so
pin it only if you want the camp somewhere other than that cell's middle.

Barachen's camp - `build camp` is Elladan's, the other one is `tolkienmobs:key_structures/barachans_camp`,
16 by 16 by 9 - has no page raising it yet. Page 10 walks to the **name**, so `place barachen` is the pin
that matters.

## Chapter two - the pass, the range and Angmar

| pages | location | pin as | drawing | footprint | what to look for |
| --- | --- | --- | --- | --- | --- |
| 26, 29 | Glóin's camp at the pass mouth | `build gloincamp` | Gladden, cell `24,10` - `7424, -4864` | 28 by 22, 8 high; door in the middle of the south wall | the **Gladden side** of the High Pass, north of the valley and east of the ridge; about 28 by 22 of level ground |
| 28, 31, 32 | Gundabad | `build gundabad` | Hithaeglir, **no cell** - searches from the range's middle | **122 by 183, 26 high** | the biggest ask in the pack: a mountain shelf that will take a 122-by-183 footprint. A pin is what stops the search wandering |
| 30 | the vault at the source of the Bruinen | `place keystone` | Trollshaws, no cell - from the country's middle | a 32-block circle | the ruin the key-stone sits in, in the hills below the valley |
| 33, 34, 36 | Carn Dûm | `place carndum` | Angmar, no cell - from the country's middle | a 32-block circle | the camps on its borders; the wight is a hunt, so the circle is all the ground it needs |

The names `gloin` and `gundabad` exist in the catalogue but no page targets them today - the pages target the
person and the building. Pin them anyway if you want the ground kept for a later page.

## The last act

| what | pin as | drawing | footprint | what to look for |
| --- | --- | --- | --- | --- |
| the Witch-king's castle | `build castle` | Angmar | 46 by 46, 52 high | ground in Angmar for a castle |
| the arena beneath it | `build arena` | Angmar | 32 by 26, 8 high | the shelf it was cut for |

Both are held behind `FINALE_OPEN = false`, so no page reaches them yet; they are still ground worth pinning
while you are in Angmar.

## What needs no pin

- **Rivendell** - pinned already.
- **The five quarries**, stood up ahead of the player rather than at a place: the Giant in the valley of the
  giants (page 11), Ulmug's goblins at the Pinnath Fenui (27), Dugorim below Gundabad's stair (32), the
  barrow-wight in Carn Dûm (36), and the Rider. None of them is ground you choose.
- **Matuna's lair** (14) reuses one of the three crags.
- **The rest of the catalogue** - `greenway`, `barrows`, `hobbiton`, `bree`, `weathertop`, `tharbad`,
  `oldforest`, `vault` - is vocabulary no page in either chapter names by id.

## Sending the pins back

Each pin prints one line:

```
For every world, add to Pins.SOURCE: Map.entry("thorenhad", new BlockPos(4864, 157, -3328)),
```

Paste the fourteen back - or however many you got to - and they go into `Pins.SOURCE` in `faction/Pins.java`,
which holds for every world including one nobody has walked. Lookup order is world pin, then source pin, then
the drawing.

## The cave names

The stone's three caves are named in `Campaign.CAVE_NAMES` - **Bandrith**, **Hoklond**, **Sholdroth** - and
asked in that order, so `cave`/`cave1` is Bandrith's and `cave2` Hoklond's, which is how `/tmpin checklist`
labels them; `cave3` is Sholdroth's, the last the stone asks for, and the checklist calls it the third cave.
