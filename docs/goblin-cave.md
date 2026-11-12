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
| lay the hall again | `/tmcave dress` | puts `goblin_hall.nbt` back over the hall the cut made, without cutting - for a hall furnished since. |
| read a pin offline | `python tools/pins.py --world "run/saves/<world>"` | the store is under `dimensions/minecraft/overworld/data/tolkienmobs/pins.dat`, not the world's own `data/`. |
| draw before cutting | `python tools/cave_section.py --world "run/saves/<world>" --pin goblins` | two vertical sections and a plan, against the save's real ground. |

`goblins` is in `Places.CATALOGUE` and canon'd to `HITHAEGLIR`, announced as "The Goblin Fires" over
"Ulmug's fires in the caves of the Pinnath Fenui". It has **no site and no template**, so nothing in
`Sites` places it - the name is written from this same pin instead, because the place's id and the pin's key
are the one string `goblins`: `Places.ensure` takes the pin ahead of any survey, so the name cannot drift from
the cut, and `Campaign.tick` asks for it beside `GoblinCave.raised`, so a player arriving at the mouth is told
where they are with no command typed. `/tmpin place goblins` is how a scout moves the name and the cave
together.

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

It is the room, not an empty box: the hall's hollow is air, the course beneath it is a floor, and the
other 3,160 cells of its box are stone, so loading it anywhere builds a hewn hall you can walk into and
decorate. An all-air file would change nothing in a world that had already been carved and could not be
decorated at all. **19 wide, 14 tall, 43 deep, 11,438 cells**, gzipped vanilla structure NBT at
`DataVersion` 4790 — the version the shipped `ch2_hall.nbt` carries, read from it rather than guessed.
Both `grab_build.read_nbt` and a separate checker read the file back to its last byte.

| step | |
|---|---|
| put it where a structure block looks | `<world>/generated/minecraft/structures/goblin_hall.nbt`, or a datapack's `data/tolkienmobs/structure/` |
| load it | a structure block in LOAD mode, name `goblin_hall`. Its axes are x across the hill, y up, z into the hill, and the file's bottom course is the hall's floor, so a block set to CORNER stands that floor under the room |
| decorate it | by hand, in the world |
| save it back | SAVE from a structure block over the furnished hall, and the mod has a decorated hall to place after the carve |

At the test pin that corner is **x 5619, y 156, z −3535**, and the room it builds runs x 5619..5637 and
z −3535..−3493 — the near end is where the landing comes in. It needs no rotation: the file's x and z run
the same way as the world's, so loading it over the hall the cut already made puts its worn faces where
they already are instead of mirroring them.

The floor is in the file: the bottom course is stone from wall to wall, so loading it on bare ground builds
a floor instead of leaving the room to sit on whatever happens to be there. That is why the file is one
course taller than the room, and why a structure block's corner goes one block lower than the hall's own
floor - at the test pin, y 156.

**The cut lays it.** `GoblinCave.dress` places the file into the hollow the carve has just made, turned to
face whichever way the hill runs, so a cut cave comes out a furnished hall rather than a bare hole. `/tmcave
cut` reports where it was laid; `/tmcave dress` lays it again without cutting, which is the way a hall
furnished by hand gets back in - save the decorated room over `goblin_hall.nbt`, ask for it, and every
world's cave has it.

**It is furnished now.** The file in the pack is the hall after decoration: **15 palette entries** where it
had two - spruce and dark oak slabs for benches and shelves, spruce wood for posts, cobwebs in the corners,
the log bench, the cooking rack and a lit soul campfire - and it carries its block entities rather than
dropping them, so the lockable chest loads with its `chest_code` and the campfire with its cooking times.
The box is unchanged at **19 × 14 × 43, 11,438 cells**, with the same 3,160 stone and the same bottom
course, which is why the corner above still lands it exactly over the carved room. The placement reads the
file's own width rather than the room's figure, so a hall saved back a course wider or narrower than the
room still lands centred on the way in instead of overhanging the wall it was measured from.

## The fires

The hall is ground the world raises; the bodies in it are the campaign's. `GoblinCave.keep` runs on the
same clock as the other keepers and does one thing: while a player is carrying page 27's hunt and is
within **64 blocks** of the door, it stands **Ulmug** beside the hall's log bench and **five goblins** on
its floor - once each, found again by tag, so the next look tops up what was killed instead of standing a
second king. A player who walks in before Glóin has spoken finds the room furnished and nobody home, which
is the point: the fires are the page's quarry, and a hall of hostiles is not put down on a road nobody
holds a page for.

Ulmug is the pack's goblin king wearing his name. The bench is found by walking the room rather than by an
offset written into the code, so a hall furnished a second time moves him with the furniture.

**The page asks for six.** Page 27 counts the five folk and the king: `GOBLINS_GOAL` is `GoblinCave.FOLK`
plus one, and the kills that answer it are the hall's - a goblin wearing the fires' tag counts, a goblin
met in the wild does not, and Ulmug counts by his own tag although the page names a goblin by kind. Kill
the king first and the page still wants the five; clear the five first and it wants him. The hall's dead
are topped up while the page is in hand, so the six are always there to be finished.

The book says his name. The story leaf draws `Campaign.aside(story)` under the count - one gold line,
"Ulmug, king of the fires: kill him too, and the fires are out" - because a leaf that only ever said *Slay
6 Goblin* counted a king it never named. It is a line and not a second counter: the six are one count, and
a page showing five goblins and a king apart would be asking for two things where the chapter asks one.

**One of the folk carries the key.** The hall's chest is set to `DungeonKey`, and one of the five goblins is
tagged as its carrier as the hall stands them - one body, not a purse each. When the carrier falls,
`GoblinCave.dropKey` reads the code off the hall's own lockable chest and cuts a `master_key` to it (3
uses, reusable), so the key always fits the chest in the room it was taken from and a hall saved again
with a new code moves both together. A hall that has given its key up arms no second carrier that
session, and a hall with no chest in it gives none up at all.

**The key is stocked from the table its code names.** The code is not only a lock: `GoblinCave.stock`
reads it as a loot table's name - `DungeonKey` names `tolkienmobs:chests/dungeonkey`, and a full id names
a table anywhere in the pack - and rolls that table into the key's own fifteen slots as the key is made.
So the goblin's key carries the hoard as well as the lock, and several chests set to the same code are one
lock with one hoard behind it: that is what lets a single key serve more than one chest. A code that names
no table leaves the key empty and the lock still works. The shipped `dungeonkey` table is a starter -
replace it and every key cut to that code follows it.

## The cave the world cuts

No player is asked to run anything. `GoblinCave.raised` is called from the campaign clock for every player in
Arda; once one comes within ninety-six blocks of the goblin pin, with the chunk loaded, it cuts the cave and
lays the hall in it. It is the same `cut` the command makes, so the design has one home; `/tmcave` remains a
development tool for walking a cave a world has not made yet, and for the report it prints.

A hall that is already there is recognised by its own log bench, which is the room's record rather than a flag:
a world cut by hand before this existed is never cut twice, and a world whose hall is missing gets it back on
the next approach. The pin is remembered for the session in `RAISED`, so a player walking the hill pays for one
cut, not one a tick. Nothing is announced in chat - the cave is simply there when the hill opens.

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

- **The camp outside the door.** It is the shelter, and it is raised: `Sites.SHELTER_CAMP` stands 33 blocks
  west of the door, and its pin is in `Pins.SOURCE` beside the door's own, so every world gets both. The
  cut does nothing for it - a camp is a building, and the cut is the cave.
- **A camp of bodies in the chambers.** The hall's fires stand while page 27 is carried; the tunnels
  behind the hall have nobody in them.
- **The fire.** "Put the fire out" is figurative — it means Ulmug — so no fire block is required. A camp
  fire outside the door would still read right.
- **A road to it.** None exists from the camp north-east yet; chapter two's line ("the road north will be
  mine again") implies one.

## Caveats worth remembering

- The cut only takes blocks out. Making the design **smaller** later leaves the old air standing; a
  smaller cave wants a fresh spot or a fill.
- Re-running `/tmcave cut` at the same pin is safe and gives the same cave.
- The template is the hall, not a copy of it: saving a decorated room back over `goblin_hall.nbt` changes
  every world's cave at its next cut or `/tmcave dress`, so what is in that file is what the pack ships.
- The cut asks for its chunks, so it will generate ground that nobody has walked — intentional for
  looking at a cave, not something to do near a player's builds by accident.

## The mouth is walled until the scroll is read

`GoblinCave.cut` ends by walling the mouth up with the hill's own stone (`skinOf` reads the flank, so the wall is the
hillside and not a plug of something else).

The wall is the hole's own shape, not a box drawn round the door. `mouthCell` takes the cells `carve` opens at the
way in - the three courses through the surface at the door, and the ramp's first steps behind them, which are cut
*above* the ground and so are the trench a player sees over a three-deep plug. Both `seal` and `open` walk exactly
those cells: one fills the air, the other takes stone back out, so opening hands back the cut's own shape and cannot
eat a block the cut never opened.

The sill is the **pin's own height**, not the ground read at the door. Once the wall stands, the top of the column at
the pin is the wall's roof, so a sill measured there had climbed seven blocks by the time the scroll was read: the
opening cleared the sky above the plug and left the plug standing. The pin was written down before the wall existed
and does not move.

Page 2-2 hands out a **bronze key** coded `CampStorage` with **zero uses and no mode**. `KeyItem` spends a key only
when its mode is set, so zero uses is the one setting that never consumes it. The key's own fifteen slots carry the
camp's stores - four cram and the **ashen scroll** (`tolkienmobs:item_scroll`) - because a chest opened with the
right key opens `KeyItemContainer`, which is the key's inventory rather than the chest's.

The **shelter camp's chest** is set to the same code when the camp is laid, and on every look at a camp already
standing (`Sites.codeCampChest`), so a camp raised before this change still opens to the new key.

Reading the scroll within `SCROLL_REACH` (16 blocks) of the cave pin opens the mouth, spends one scroll and says
`campaign.tolkienmobs.cave.open`: *"The scroll crumbles to dust as the cave opens."*

The scroll is spent, and the wall is laid again when the page is finished with, so the key's stores are made good:
while the page is carried and the key is in the pack, a key with no scroll in its slots is given one back. A key
holding the dust of the last wall would leave the second walk of the page with no way in.

Settling 2-2 walls it up again for the next player, unless another player in the same world is still carrying the
page (`Campaign.sealMouthBehind`) - the wall is the world's, not the finisher's.

Page 2-2 now has two lines. Its **closing** is Gloin's word on the key - *"We found this key we think will help
find the goblin entrance, take it and see if it will help."* - which the book carries while the page is walked.
His account of the fires themselves - Ulmug's fire, the pass's traffic, the road north - is
`campaign.tolkienmobs.page.goblins.tale`, spoken once when the key is handed over.

## Ulmug's body

The king the cave stands is `ENTITY_TTM_GOBLINKING`, and the first king anything ever spawned crashed the server on
creation: `GoblinKingEntity` built its snowball goal in a field initialiser, while `Mob`'s own constructor calls
`registerGoals` before any initialiser on the class being built, so the selector was handed a null goal and died
hashing it. The goal is made in `registerGoals` now. Nothing to do with the cave; the cave is simply the first caller.
