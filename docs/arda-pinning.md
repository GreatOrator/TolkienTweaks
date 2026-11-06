# Pinning a location

Everything needed to put a location where it was scouted: the commands, what a pin does to placement, and
the ground the two chapters still want.

Ground is otherwise **surveyed from the drawing** - a country's middle, then a cell's middle, then the
nearest column inside that search that will take the footprint. That answers "somewhere in this part of the
country", which is the right answer for a world nobody has walked. A scout flying a generated world, finding
the gap between the crags or the dry bench above the water, is answering "here" - and a pin is how that
answer is kept.

## The commands

Creative, or a gamemaster-level operator (op 2), standing in **Arda**. The pin is read by the campaign's own
placement, so an overworld coordinate would mean nothing.

| command | what it does |
| --- | --- |
| `/tmpin place <id>` | Pins a catalogue **name** at the caller's feet: `rivendell`, `thorenhad`, `barachen`, `ford`, `keystone`, `carndum`, or any of the sixteen place ids. |
| `/tmpin build <name>` | Pins a **building** at the caller's feet: `camp` / `elledancamp`, `tent`, `ruin`, `cave` / `cave1`, `cave2`, `cave3`, `gloincamp`, `gundabad` / `keep`, `weathertop`, `castle`, `arena`, or a full template id. |
| `/tmpin list`, or no argument | What this world has pinned, and what the build pins for every world. |
| `/tmpin checklist` | The fifteen locations the two chapters ask for, each marked `this world`, `every world` or `not pinned`, with a count at the top. Places nothing. |
| `/tmpin remove <name>` | Forgets this world's pin for that name. A pin written into the build is in the jar, not the world, and this does not touch it. |

`place` and `build` are separate branches because one word can be both: Weathertop is a name announced on
arrival and a hilltop template raised for a page, and so are Gundabad and Gloin's camp.

## The two kinds of location

| kind | key it is pinned under | who reads it | what it is |
| --- | --- | --- | --- |
| a **name** | the catalogue id, e.g. `rivendell` | `Places.ensure` | a circle of ground announced on arrival, with folk standing in it |
| a **building** | the template id, e.g. `tolkienmobs:key_structures/elledancamp` | `Sites.survey` | ground raised for the player who was sent to it |

## What a pin changes

- **A name** stands exactly at the pinned block. The cell survey is skipped, and so is the one-name-per-cell
  spacing rule. This is also the only way a name stands where the drawing paints nothing at all: the valley,
  whose columns never sample as Imladris, so pinning `rivendell` is what lets the pages that need Elrond,
  Glorfindel and Legolas be finished.
- **A building** has its spot taken as given; the survey is skipped. The laying still measures the footprint
  and seats it - highest column under it, fill underneath - so the pin decides *where* and the ground decides
  *how it sits*. A building pinned on ground the world made badly is lifted and underpinned, not left hanging.
- **Anything unpinned** is surveyed from the drawing exactly as before, so an empty table works.
- **The y** is a note of the surface the scout stood on. It is not used to seat a building, so a y a block or
  two out is not an error.

## A pin in the world's own generation

The pins above are read while the game runs: `Places` and `Sites` ask for one when a page needs a location.
A build can also be pinned into the world's **generation**, so it is standing in a world nobody has walked,
with no command typed and no pass left to run.

A pinned build is one template and one anchor block, placed by a structure type and a placement of its own:

- **`tolkienmobs:pinned` structure** places the pieces of one pool at the chunk's own corner, rounded out by
  the anchor's place inside that chunk, and seats them on the ground under them - the start is projected onto
  `WORLD_SURFACE_WG` and bearded beneath. The same anchor therefore stands on the same hillside in every
  world, because the ground under it is drawn rather than walked.
- **`tolkienmobs:pinned` placement** answers for exactly one chunk, the one holding the anchor, and no other.
  Vanilla's `random_spread` cannot be asked for a block at all: its cells are a hash of the seed, which is
  why every scattered build in the mod is a scatter.
- The anchor is a **block**, not a cell. A cell is the drawing's precision and a build needs the block; a
  cell middle works anyway, being 512 blocks from its neighbour's and always on a chunk corner, which is how
  Thorenhad's camp is pinned.

What it costs the world: nothing at startup. A structure's start places its pieces as the chunks they touch
are made, so a one-piece camp and a five-hundred-piece city both arrive the way the terrain around them does
- when someone walks there.

The first is `tolkienmobs:thorenhad_camp`: Elladan's camp, `key_structures/elledancamp`, pinned at
Thorenhad's own cell middle, **4864, 0, -3328**, in the Trollshaws. Every build the campaign names follows
the same shape - one `Pinned` line in `TolkienStructureProvider.PINNED_BUILDS`, and the datagen writes the
structure, its one-element pool, the set carrying the placement, and the `has_structure/` tag.

## Keeping a pin for every world

Every pin prints its own source line:

```
For every world, add to Pins.SOURCE: Map.entry("thorenhad", new BlockPos(4864, 157, -3328)),
```

Send that line back and it goes into `Pins.SOURCE`, which holds in every world including one nobody has
walked. Lookup order is **world pin -> source pin -> the drawing**: a spot chosen in this save outranks one
written into the build.

## The ground to scout

### Chapter one - the Trollshaws and the valley

| pages | location | pin as | what to look for |
| --- | --- | --- | --- |
| 1, 2, 5, 6, 7, 8, 12, 13, 14, 15 | Rivendell, where the Elves stand | `place rivendell` | pinned at **5200, 75, -3370** - the cut's own low corner of its 554 by 512 footprint, which is the block `/tmcity` plants the corner on, covering x 5200..5753 by z -3370..-2859 - on ground the map now lays for it - its floor at 74 and its massif at the crown 318 from the footprint's edge outward (`PLANTED = true`); **unblocks every page whose giver or turn-in is Elrond, Glorfindel or Legolas**, and chapter two's 2-1, 2-10 and 2-11 come back to the same city |
| 2, 3, 4, 5, 8, 9, 10, 11, 12 | Thorenhad, Elladan's camp | `place thorenhad` | level ground a walk east of the valley, off the road |
| 2, 4, 8 | Elladan's camp itself | `build camp` | 16 by 8 by 16; about 16 by 16 of level ground |
| 3 | the wood ruin the black horse lies in | `build ruin` | a standing wall in the channels south of the road; about fourteen blocks across |
| 6 | the ford the beryl is carried to | `place ford` | **water the road can cross** - the drawn cell has neither water nor the road |
| 9 | Bandrith's, Sholdroth's and the third cave | `build cave`, `cave2`, `cave3` | three hillsides; each crag is cut rock and seats nine to ten blocks into the ground |
| 10, 11 | Barachen's fire | `place barachen` | level ground south of the road |
| 14 | Matuna's lair | *(covered by the caves)* | the page reuses a crag, so it needs no pin of its own |

### Chapter two - the Misty Mountains and Angmar

| page | location | pin as | what to look for |
| --- | --- | --- | --- |
| 2-1, 2-4 | Gloin's camp at the pass mouth | `build gloincamp` | 28 by 8 by 22, door in the middle of its south wall; the Gladden side of the High Pass |
| 2-3, 2-6, 2-7 | Gundabad | `build gundabad` | **122 by 183 by 26** - the biggest ground ask in the pack; a Hithaeglir shelf that will take it. No pin is written for it today, so it searches out from the range's middle, and a pin is what would stop that search |
| 2-5 | the vault at the source of the Bruinen | `place keystone` | the ruin the key-stone sits in |
| 2-8, 2-11 | Carn Dum | `place carndum` | the camps on its borders; the wight is a hunt, so the circle is all the ground it needs |

### The last act

| page | location | pin as | what to look for |
| --- | --- | --- | --- |
| the castle | the Witch-king's keep | `build castle` | Angmar, ground for a castle |
| the arena | beneath the castle | `build arena` | the shelf it was cut for |

### What needs no pin

Five quarries are stood up ahead of the player rather than at a place: the Giant in the valley of the
giants, Ulmug's goblins, Dugorim below Gundabad's stair, the barrow-wight in Carn Dum, and the Rider.

The rest of the catalogue - `greenway`, `barrows`, `hobbiton`, `bree`, `weathertop`, `tharbad`, `oldforest`,
`vault`, `gloin`, `gundabad` as a name - is vocabulary no page in either chapter names by id.

## Checking progress

`/tmpin checklist` prints the fifteen - the six names first, then the nine buildings - with the ones still
leaning on the drawing marked `not pinned`, and a count at the top. Its keys come from the `Sites`
constants, so a checklist line and a placement can never disagree about what counts as pinned.

## Notes and traps

- A pin on a key nothing references yet does nothing. Pin it anyway if you like; it applies the moment the
  page or the building names that key.
- A world pin lives in the save. A source pin lives in the jar. `/tmpin remove` only removes the first.
- Pinning a name skips the spacing rule deliberately - two names may be pinned inside one cell.
- A building still has to find ground: the pin is a target, not a guarantee, and `build` waits until the
  player is within four chunks before it lays anything.

## See also

`arda-pins.md` - the pin table itself, and what a pin is. `arda-faces.md` - the named cast and their art.
