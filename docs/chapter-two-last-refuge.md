# Chapter two — Last Refuge

The notes' Section II, *Last Refuge*: the pursuit of the Nazgûl out of the Trollshaws, through the goblins of
the Misty Mountains, into the Dourhand fortress of Gundabad, and on to the wight and the dragon's corpse at
Carn Dûm. It is the West's line continued, not a new front: the dwarves stand with the West, and every page
speaks to a body already in the pack's cast or one it can stand.

This is the plan the work is built against. It is written down so the pages can be checked against the notes
rather than against memory, the same way chapter one's are.

## The eleven pages (26–36 of the West's line)

| # | Notes | Page | Giver → turns in | Kind and ground |
|---|-------|------|------------------|-----------------|
| 26 | 2-1 | Onward, Misty Mountains | Elrond → Glóin | `DIALOGUE`, Glóin's camp at the east end of the pass |
| 27 | 2-2 | Troublesome Goblins | Glóin → Glóin | `HUNT`, Ulmug, in the goblin fires of the Pinnath Fenui |
| 28 | 2-3 | Return to Gundabad I | Glóin → Glóin | `LOCATION`, Gundabad's Vault (the cornerstone and the plans) |
| 29 | 2-4 | Return to Gundabad II | Glóin → Glóin | `DIALOGUE`, the plans read at the camp |
| 30 | 2-5 | Key-Stone | Glóin → Glóin | `LOCATION`, the dwarf-vault at the source of the Bruinen |
| 31 | 2-6 | Last Refuge I | Glóin → Gimli | `DIALOGUE`, the secret entrance to the inner keep |
| 32 | 2-7 | Last Refuge II | Gimli → Glóin | `HUNT`, Dugorim, inside the keep |
| 33 | 2-8 | Steps of Evil I | Glóin → Glóin | `LOCATION`, the camps on the borders of Carn Dûm |
| 34 | 2-9 | Steps of Evil II | Glóin → Glóin | `ITEM`, the Dark Sigil off a wight at the fortress' door |
| 35 | 2-10 | Evil Tidings | Glóin → Elrond | `DIALOGUE`, Elrond's library |
| 36 | 2-11 | Fire & Ice | Elrond → Elrond | `HUNT`, Drugoth and the Dragon Wight, in Carn Dûm |

Rewards follow the notes at the tenth the whole campaign is priced at: their 500 becomes 50 standing, 700
becomes 70, 1200 becomes 120. The coin the notes put on each page is paid as written, in silver and bronze.

## The ground

- **Glóin's camp** — a place at the east end of the High Pass (`gloin`), north of the valley, on the ground
  the pass drops into on the Anduin side (Gladden) and pinned to that pass's mouth. Captured from the old
  world's own camp rather than modelled: a fire, tents and a dwarf waiting at the door.
- **Gundabad and its vault** — places (`gundabad`, `vault`) in the same range, north of the camp. The
  asset is the six geomorph tiles, assembled: `key_structures/gundabad.nbt` is 122 by 183 by 26, the
  sheet's own two-across, three-down arrangement, with every room on one floor. The old world's build is
  not carried, for the reasons in the last section, and the pack's `boss_castle` is the finale's and is
  not spent twice. The notes' outer fortress and its vault are both in the keep, and the inner keep is the level below
  it, reached by the spiral's stair.
- **The key-stone's vault** — a place (`keystone`) in the Trollshaws, at the source of the Bruinen: ruins
  with a vault still in them, and bears in the ruins.
- **Carn Dûm** — a place (`carndum`) in Angmar, which the drawing already has, and where the campaign's
  own castle and arena already stand. Chapter two ends in the same country the finale's war does.

## The bodies

- **Glóin** and **Gimli** are new keepers, stood like Elrond and Legolas are: a tag, a name, and a skin under
  `textures/entity/middle-earth/` if one is drawn for them.
- **Ulmug** (goblin), **Dugorim** (Dourhand dwarf) and **Drugoth** (Gaunt-lord) are named quarries, which is
  the power the campaign already has: a tagged body of an existing kind, named, and the kill counted for
  that body alone.
- **The Dragon Wight** is the notes' raised corpse and the pack has no such creature. It is either a named
  wight of an existing kind or the chapter needs an asset. Open question, and the last page's only one.

## The seam with chapter one

The notes end chapter one on *Hasten Their Departure*, and the old build staged it as a scene: the company
gathered before Elrond, then walking away. Nothing is staged today; the page is a walk back to the library.
If the scene is wanted it belongs here, between page 25 and page 26 — the Fellowship leaving is the same beat
as Elrond sending the player north, and a scene at the end of chapter one that is never seen again would be
the only one in the campaign.

## Deliberately not decided here

- How the **inner keep** is gated. The notes make page 31 the secret entrance, and the stair is open now:
  the way in wants a door the plans open, not a passage a player can simply walk down.
- Whether the notes' **Pinnath Fenui** is named ground or a direction: the notes describe it as the hills
  north-east of the camp, which a compass bearing already covers.

## Not carried: the old world's Gundabad

Measured, cut and rejected. It is written down so the same ground is not walked twice.

The fortress still stands in the old world and can be read again at:

    G:\GitHub\MC1_21_x\TolkienTweaks\run\backups\2026-11-01_10-39-03_Arda\Arda   (overworld, region/)
    corners  14473,9,2275  ->  14701,9,2546    both taken on the lowest level of the build

`tools/grab_build.py` reports 7.9M blocks inside that box: 6.7M of it is the mountain - stone 6.66M,
andesite 246k, diorite 255k, granite 247k, dirt 203k - against 195,107 built blocks. Gundabad there is not
a fortress standing on ground but rooms cut into a mountain, so the part worth having is small, buried, and
only means anything with the mountain around it: the treasury is 274 blocks, the forge 771, the goblin
works 9,153, the burnt place 992.

Carrying it was tried the way dwarven halls are actually built, as a plug of rock with the passage already
hollow inside it rather than a room shell standing in air. Three tiles were cut - a 48-cube of the vault and
its corridor (86,697 stone, 2,639 stone brick, 12,740 air, 265 KB once bedrock and snow were dropped), the
goblin works, and the treasury with the rock stripped off - and loaded into a flat map to be judged. The
answer was no. A structure file lays one fixed cube at a time and there is too much in play at that size for
it to be worked around, so the tiles were deleted and the asset is left for whatever replaces them.

The converted save reads the same build to the block, so nothing is lost by keeping the 1.21.1 backup as the
source. One thing the conversion did change is worth remembering: 26.1.2 no longer keeps the overworld in
`region/` but in `dimensions/minecraft/overworld/region/`, and the reader falls back to that.

## Building the hold

The fortress is raised from the geomorph drawings in `tools/geomorphs/` (set 5), not from the six
example schematics collected alongside them. Those are somebody else's tiles - generic ruined corridors,
no columns, no apse, no screens - so re-materialling them in Angainor stone would give a tidy ruin rather
than a dwarven hold. What they are good for is a ruler: read block by block they are 61 square and 9 to 12
high, with five-wide openings at blocks 13-17 and 43-47 on every face, which is squares 3 and 8 of ten.
The drawings carry openings at the same quarter points, at about fifteen pixels per block once upscaled,
so the conversion is settled rather than guessed.

`tools/geom_extract.py` splits a sheet into its six panels and classifies each cell as floor or rock by
ink density; `tools/geom_panels.py` writes the result over the artwork so it can be checked by eye.
Extraction recovers layout and proportion but not intent: a bench, a statue, a well and a curtain are all
just ink, and a hand-drawn curve arrives as a staircase at one block per cell. So the drawings are the
blueprint and the tiles are authored from them as squared masonry.

Every tile is 61 by 61 with open doorways at the quarter points on all four faces, so any tile meets any
tile without matching. Behind that wall each tile carries a five-wide gallery ring which all eight
doorways reach, so a tile's own connectivity is free and the chambers inside can be shaped however the
drawing wants. `ch2_hall` is the first of the six: a 39-wide hall behind the ring, a raised seat at the
north end, two rows of piers carrying the ceiling ribs, an aisle down the middle and benches along the
walls. The tiles are written to `data/tolkienmobs/structure/ch2_*.nbt` and can be placed in a test world
with `/place template tolkienmobs:ch2_hall`.

The six tiles, in the order the sheet reads:

    hall        the great hall: raised seat, two rows of piers carrying the ribs, aisle, benches
    approach    the way in: a dog-legged gallery, a barred cell block, a mustering room
    vault       a rounded, crenellated treasury, a plinth of gold, one raised portcullis
    colonnade   four free-standing piers, arcade screens along two walls, a well
    cistern     a water hall: sunken basin, spring column, alcoves cut into three walls
    spiral      the way down: a helical stair in the middle of its room, thirteen deep

Each stands on its own and can be walked with `/place template tolkienmobs:ch2_<name>`. `spiral` is the
one tile that leaves its own floor: it is 26 blocks tall with the hall floor thirteen blocks up, so it
wants placing thirteen blocks lower than its neighbours, and the stair arrives at the level below. A
helical stair about a newel in the middle of a room keeps every tile's floor plan whole - the stair is
the room's centrepiece instead of taking a square away from it.

The six are also assembled into the keep itself, by `tools/gundabad_keep.py`: the sheet's own
arrangement - approach and vault across the north, hall and colonnade in the middle, cistern and spiral
across the south - written whole to `key_structures/gundabad.nbt` at 122 by 183 by 26. The spiral is the
one tile that leaves its own floor, so the other five are lifted thirteen blocks to meet it and every
room is walked on one level. What sits under those five rooms is filled with stone, because a structure file cannot leave a cell
alone: a cell left out of it is written as air, and the ground there goes with it. Written whole, the
box holds deepslate under the lifted rooms and the rooms' own air above it, so the hold carves its
rooms out of the hillside and leaves the ground beneath them standing.

Under the south row is the **inner keep**, cut by the same tool from two of the example schematics that
came with the drawings - `2ml` and `2mr`, the open hall and the barred undercroft - laid at y 1 to 12,
which is exactly where the spiral's landing is: twelve blocks under the hold's floor, on the level the
stair delivers a player to. They are re-materialled into the hold's own stone on the way in, since what
is taken from them is the shape and their stone brick and cobble would read as somebody else's ruin
inside a dwarven fortress. Each is lifted by its own lowest floored course - these were cut out of a
valley, so their highest solid course is the grass on their crown, ten blocks above the rooms they
hold - the stair's well is carried empty through them, a door is cut in the wall of the well, and a way
is cut west from it until it stands in a room and not in a wall.

Reading the drawings' symbols, corrected by the mapmaker: the concentric arcs in the corner of the bottom
right tile are a **well or pool**, not a stair, and the short ruled marks with an arrowhead are **stairs,
pointing the way they run**. So the sets do draw level changes, and the arrows are the authority on which
way the steps go.

