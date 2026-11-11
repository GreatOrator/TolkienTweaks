# The goblin cave — page 27's ground

A handoff note for the cave *Troublesome Goblins* is hunted in, so it can be put down and picked up
without re-deriving it. Everything here is either measured or built; what is not built is listed at
the end rather than implied.

## Why it is a cave and not a template

Page 27 says the page's ground is "the goblin fires of the Pinnath Fenui". The pack's other chapter-two
ground is either a pinned site (`Sites.GLOIN_CAMP`, `Sites.GUNDABAD`) or a place the drawing already
makes (`keystone`, `carndum`). A cave is neither: there is no build to place and the drawing has no
cave in it.

What was actually offered was a WorldPainter export of the old world's goblin caves — `Templates/schematics/goblincaves.schematic`,
786 KB on disk and **166 MB uncompressed**, **1022 × 79 × 1022 cells, 92.9% stone**, with 110,908
cobblestone and 8,315 glowstone placed in it and no wood, torch, fire or water at all. Measured by
`tools/schematic_probe.py`. That is a kilometre of painted world with a cave in it, not a build with a
hillside round it, and it is an order of magnitude past Gundabad (7.9M cells, 195k built), which was
already too big to place.

So the decision: **the cave is cut, not placed.** The export is a source for the *shape*, not a
structure. Its air is the cave, its stone is WorldPainter's fill, and Arda draws its own stone — which
means there is no palette to re-material and nothing to ship.

## The pin is the door

The cave is cut at whatever block the place `goblins` is pinned to, so the door can be moved by walking
to a new spot rather than by editing code.

| step | command | note |
|---|---|---|
| name the ground | `/tmpin place goblins` | in Arda, creative or op 2. Writes the caller's feet into the world's pins and prints the `Pins.SOURCE` line. |
| find the door | `/tmtp goblins` | the pin, if the world has one. |
| cut the cave | `/tmcave cut` (or bare `/tmcave`) | asks for the chunks, reads the ground, cuts, reports the count. |
| read a pin offline | `python tools/pins.py --world "run/saves/<world>"` | the store is under `dimensions/minecraft/overworld/data/tolkienmobs/pins.dat`, not the world's own `data/`. |
| draw before cutting | `python tools/cave_section.py --world "run/saves/<world>" --pin goblins` | two vertical sections and a plan, against the save's real ground. |

`goblins` is in `Places.CATALOGUE` and canon'd to `HITHAEGLIR`, announced as "The Goblin Fires" over
"Ulmug's fires in the caves of the Pinnath Fenui". It has **no site and no template**, so nothing in
`Sites` places it, and `/tmpin place goblins` is the only thing that gives it ground.

The pin's `y` is where the scout stood and is **not used**: the cut reads the ground under the door
itself, block by block.

## The shape

The way in is found, not given: the cut asks the ground which of the four compass directions rises most
over 6–26 blocks and goes that way, so the cave always runs into the hill.

Measured from the door's own ground `g`:

| part | numbers |
|---|---|
| mouth | **5 wide, 7 tall**, at `g`; one block cleared in front of it |
| way in | **5 wide, 7 tall**, falls **40** blocks, one block down in two, so **80** blocks of ramp |
| landing | 7 wide, 5 tall, floor `g-40`, runs into the hall's back wall |
| hall | **17 wide, 18 long, 13 tall** - floor `g-40`, ceiling `g-28` |
| passages | 7 wide, 7 tall, one out of each side of the hall to a flank chamber. The hall's far wall is solid: the back chamber is not reached through the hall |
| flank tunnels | a string of overlapping balls from each flank chamber into the back one, wandering 3-5 blocks across, so it is a tunnel rather than a corridor |
| flank chambers | four overlapping lobes each, about 63 blocks along the way in by 44 across and up to 16 tall, mirrored either side of the hall |
| back chamber | four lobes, about 76 by 69 and up to 20 tall - the largest of the three |
| far wall | 220 blocks past the door |

Then rock is left standing in the caverns rather than taken out of them: the wall wanders off its own
reach by two hash terms (a wobble over four blocks, a finer one over two), the floor carries rubble, and
about one column in eight keeps a stalactite hanging from the ceiling or a stalagmite rising off the
floor, up to five blocks. **Nothing is placed and no material is chosen** - only rock is taken out, so the
hillside's own stone is what stays standing. The hash is an integer hash of the cell, computed the same
way in `GoblinCave` and `cave_section.py`, so the spurs sit in the same place every cut; if the two ever
disagree the drawing stops being evidence and becomes a lie.

The hall and the landing are deliberately untouched by all of that: they are the worked rooms, and the
roughness is what the caves behind them have instead.

The hall is squared on purpose — it is the room somebody made — and the chambers are not. About **95,616
cells** at the test pin, with **7 dropped** where the roof rule refused: every one of those is at the
mouth, where a 7-tall way in comes within 3 blocks of the ground, so the rest of the plan is under the
hill. That is six times what the two-chamber cave came to.

Flood filling the plan from its mouth reaches 95,539 of those cells. The 77 it cannot reach are pockets
the noise shut inside the rock: no opening on any side, and nothing to see from the cave itself. The
same fill shows the flank chambers' own swell reaching across the hall's line between about `a` 106 and
144, so the hall's far end opens straight into both flanks as well as through the 7-wide passages out of
its sides - the passages are the narrow way in, the merge is the wide one.

Two rules hold the shape together:

- **The roof rule.** Every cell is held `ROOF = 3` blocks under the ground above it, so the mouth is the
  only way in. The `OPEN = 4` cells of the way in are exempt, because a mouth that does not break the
  surface is not a mouth.
- **The two implementations must agree.** `faction/GoblinCave.java` cuts it and `tools/cave_section.py`
  draws it, and they are the same numbers, the same integer division on the ramp and the same sines on
  the swell. Change one and the drawing stops being evidence.

## Where it stands

-Cut and drawn at the test pin **5628, 196, -3621** in `run/saves/Serious Test`: ground 197 as the save
reads now, hill rising south, floor 157, hall ceiling 169, far wall 5628, -3401. The cave's own chunks
have since been taken back out of that save (`tools/chunk_trim.py`), so the ground will be made again
when it is walked and its exact height may land a block or two from this reading - the cave is measured
from the door's ground either way, so the shape does not move with it. **That pin is a test** — it sits ~530 blocks
  from the departure plaza and ~2,600 blocks west of Glóin's camp's own pin (8215, 224, -3674), not at
  the pass.
- The real door is still to be pinned: the plan is a **special pin on the High Pass road**, about 1,200
  blocks from Glóin's camp, once the shape is settled. Nothing in the code assumes the test pin.
- Nothing in Arda carves today: every generated biome has `"carvers": []`, so this is a hand rather than
  worldgen. A world whose chunks were made before the cave will show nothing until `/tmcave cut` is run.

## What is not built

- **The camp outside the door.** The old cave had one; the cut is only the cave. A camp is a template
  `Sites` could place by its door (`DOORWAYS` carries a sill and a facing), or a new small build.
- **Ulmug and his folk.** `/tmcave cut` places no blocks and no bodies: the cave is empty. Page 27's
  quarry is counted in prose, so this is not a blocker, but a hunt in an empty cave is not a fight.
- **The fire.** "Put the fire out" is figurative — it means Ulmug — so no fire block is required. A camp
  fire outside the door would still read right.
- **A road to it.** None exists from the camp north-east yet; chapter two's line ("the road north will be
  mine again") implies one.

## Caveats worth remembering

- The cut only takes blocks out. Making the design **smaller** later leaves the old air standing; a
  smaller cave wants a fresh spot or a fill.
- Re-running `/tmcave cut` at the same pin is safe and gives the same cave.
- The cut asks for its chunks, so it will generate ground that nobody has walked — intentional for
  looking at a cave, not something to do near a player's builds by accident.
