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
| hall | **19 wide, 43 long, 13 tall** - floor `g-40`, ceiling `g-28`, squared in plan and hewn in section |
| tunnels | a cave tunnel out of the hall's back wall and out of either side, so each chamber is reached through rock the way the rest of the cave is: overlapping balls wandering 3-5 blocks across, not a driven passage |
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

The landing is deliberately untouched by all of that, and the hall is worked but not machined. It is squared
in plan and hewn in section: its four corners are cut at 45 degrees three blocks in, its ceiling is
vaulted - set in two blocks from every wall above the eighth course - and its faces wander a block either
way on the same cell hash the caves use, so no two stretches of its wall are quite alike. Standing rock is
kept two blocks clear of it, so the hall is a room with rock round it; the only thing that pierces its
wall is the round head of a tunnel.

The hall is squared on purpose — it is the room somebody made — and the chambers are not. About **99,320
cells** at the test pin, with **none dropped**: on this plan the hill is deep enough to roof all of it.

The flood fill that used to be quoted here was measured on the plan before the hall was extended, so it no
longer describes this one: it reached 95,539 of 95,616 cells, and the 77 it could not reach were pockets
the noise shut inside the rock. With the hall 25 blocks longer and the chambers held clear of it, that
count wants taking again, and the drawing tool cannot be pointed at the save at the moment: the last chunk
trim took the door's own chunk back out, so `cave_section.py` exits with no ground under the pin.

Two rules hold the shape together:

- **The roof rule.** Every cell is held `ROOF = 3` blocks under the ground above it, so the mouth is the
  only way in. The `OPEN = 4` cells of the way in are exempt, because a mouth that does not break the
  surface is not a mouth.
- **The two implementations must agree.** `faction/GoblinCave.java` cuts it and `tools/cave_section.py`
  draws it, and they are the same numbers, the same integer division on the ramp and the same sines on
  the swell. Change one and the drawing stops being evidence.

## The hall as a structure

The hall is also written out as a vanilla structure file, so it can be furnished by hand — the one thing a
carve cannot do. `tools/hall_structure.py` reads the same `hall()` the cut uses (from `cave_section.py`,
which is kept in step with `GoblinCave`) and writes
`src/main/resources/data/tolkienmobs/structure/goblin_hall.nbt`.

It is the room, not an empty box: the hall's hollow is air and the other 2,343 cells of its box are stone,
so loading it anywhere builds a hewn hall you can walk into and decorate. An all-air file would change
nothing in a world that had already been carved and could not be decorated at all. **19 wide, 13 tall, 43
deep, 10,621 cells**, gzipped vanilla structure NBT at `DataVersion` 4790 — the version the shipped
`ch2_hall.nbt` carries, read from it rather than guessed. Both `grab_build.read_nbt` and a separate
checker read the file back to its last byte.

| step | |
|---|---|
| put it where a structure block looks | `<world>/generated/minecraft/structures/goblin_hall.nbt`, or a datapack's `data/tolkienmobs/structure/` |
| load it | a structure block in LOAD mode, name `goblin_hall`. Its axes are x across the hill, y up, z into the hill, and the hall's floor is y 0, so a block set to CORNER stands its bottom course on the floor |
| decorate it | by hand, in the world |
| save it back | SAVE from a structure block over the furnished hall, and the mod has a decorated hall to place after the carve |

At the test pin that corner is **x 5619, y 157, z −3535**, and the room it builds runs x 5619..5637 and
z −3535..−3493 — the near end is where the landing comes in. It needs no rotation: the file's x and z run
the same way as the world's, so loading it over the hall the cut already made puts its worn faces where
they already are instead of mirroring them.

The floor is deliberately not in the file: the cut leaves the hill's own floor under the hall and a
structure block's corner stands on it, so the same file sits right on whatever ground it is loaded onto.

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

- **The decorated hall being placed.** The craft is there — carve, furnish by hand, save the structure back
  — but nothing in `GoblinCave` loads `goblin_hall.nbt` yet; the carve puts no blocks down at all.
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
