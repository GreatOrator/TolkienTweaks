# Gundabad's gate — where the work stands

A handoff note for the gatehouse that stands in the pass before the hold, so the work can be put
down and picked up without re-reading the code. Written 13 November 2026.

## In one line

The gate is **eight structure templates**, 160 by 81 by 48, laid by `/tmgate` into a notch in Mount
Gundabad that is cut to the gate's own box — the walls stand at its edges at the height of its wings,
the drawn plateau climbs out of the floor fourteen blocks behind its back, and the pass opens south
into a courtyard. The procedural cut it was judged against is gone. Nothing of it is raised by the
world yet: it is a command, and a command is not allowed to be the only way a build ever stands
(`AGENTS.md`).

## What the gate is

Read out of `run/saves/world` (overworld) between five ammolite markers by `tools/gate_capture.py`,
and cut into 48-cubes because a structure block holds no more than that.

| | |
|---|---|
| size | 160 wide × 81 tall × 48 deep (4 × 2 × 1 cubes, 8 files) |
| cells | 622,080 — **every one listed** |
| blocks | 132,268: the gate's own 77,837, stone 45,442, gravel 8,989, air 489,812 |
| facade | the file's north face; **south** in the world |
| block entities | not carried — the sixteen hoppers arrive empty |

The gate's own palette, as built: netherrack 17,519, cobblestone 13,331, obsidian 10,131, soul sand
9,570, coal block 9,042, mossy cobblestone 8,999, cobblestone stairs 8,858, gray terracotta 320,
hopper 16, cobblestone wall 16, torch 16, dark oak stairs 10, cobblestone slab 9.

| piece | offset (x, y, z) |
|---|---|
| `gundabad_gate_0_0_0` | 0, 0, 0 |
| `gundabad_gate_1_0_0` | 48, 0, 0 |
| `gundabad_gate_2_0_0` | 96, 0, 0 |
| `gundabad_gate_3_0_0` | 144, 0, 0 |
| `gundabad_gate_0_1_0` | 0, 48, 0 |
| `gundabad_gate_1_1_0` | 48, 48, 0 |
| `gundabad_gate_2_1_0` | 96, 48, 0 |
| `gundabad_gate_3_1_0` | 144, 48, 0 |

The last column is sixteen wide and the top course thirty-three tall; the sizes are read from the
files, not written in the table, so a gate cut again with a course more or less still lays.

## Where it goes

One place, and it is the only place: the ground is drawn to the gate, so a gate laid anywhere else is
standing in a valley shaped for a different gate.

| | |
|---|---|
| box | x **5496…5655**, z **−6877…−6830** |
| floor | **187** (the gate's own base course) |
| top | 267 |
| facade | south, at z −6830; the back box is a low square on the north face, about 40 wide and 12–15 tall |

The numbers live in `ArdaMap` as `GATE_WEST`, `GATE_EAST`, `GATE_NORTH`, `GATE_SOUTH` and
`GATE_FLOOR`, and `ArdaMap.gateFront()` is the facade middle a caller places by. That the facade is
the south face was read, not assumed: the placed gate's solid count per z slice matches the
template's slices reversed (21,611 difference against 37,173 the right way round), so the file's
north face became the world's south face.

## The ground around it

The massif's own drawing is a plateau three hundred blocks up with thirty blocks of movement on it
and **no face anywhere** an 81-block gate could be set into — which is what the first trial in the map
showed. The country is therefore drawn as a pass (`ArdaMap.gundabadPass`), cut to the gate:

| | |
|---|---|
| floor inside the gate's box | **187, level to the block** — the roll starts only past the gate, because a roll that lifts a floor course by one is a gap under the whole build |
| the opening's width | narrowed **39 blocks on the west and 24 on the east** (`PASS_FOOT_WEST` / `PASS_FOOT_EAST`): the climb begins that far inboard of the footprint, so the gap the gate stands in is that much narrower |
| the mountain's foot | **187** — the floor itself, where the climb begins; the slope's shape is unchanged, it only starts sooner |
| inside the footprint | never above **267**, the build's own roof, so every cell of ground there is one the placing clears |
| the hall behind | the room the three rear passages open into is read back off the world (**x 5517-5654, y 190-226**) and carried north as one hall: floor at 187-189, side walls, roof at **227**, out until the rock stands over the roof and **3 blocks into it** (`HALL_INTO_ROCK`), on columns that stop where the mountain arrives (median 50, 3 at the east end, 54 at the most) |
| the floor's edge | the ground beside the footprint is bedded **up to y 188**, the floor's own top, six blocks out (`FLOOR_REACH` 6): fill only, and only where the ground is lower, so the rock at the ends is left as it is |
| the ends of the build | laid as **rock only** - the four end tiles carry no air, so the mountain is not cut back to the gate's own silhouette there and the ends are absorbed into the rock |
| the mountain round the gate | climbs at **two and a half blocks up per block out** (`PASS_STEP`): 202 at six blocks, 247 at twenty-four, the roof's 267 by about thirty-two |
| behind the plaza | **187** — level, and level for its own sake: the camp stands on it |
| at the corners | distance **across and behind together**, so the side and the back are one flank and not two ramps meeting in a crease |
| where the climb stops | at the country's own drawn surface - 293 to 319 round the courtyard - so everything past about fifty blocks is the drawing |
| its surface | broken by ±14 of folded noise and ±18 of crest wandering along the pass, fading in off the build's own edge |
 160 wide through the gate's depth and sixty blocks south, then opening |
| courtyard | 540 wide by z −6650, 756 at the mouth |
| floor along the pass | 187 at the gate, 175 at the mouth |

Inside the pass the ground is the floor, and outside it the mountain climbs **out of that floor at once**: two
and a half blocks of height for every block of distance from the footprint, a stair of two or three each step, so the
gate's ends are into the rock within about thirty blocks and the gate reads as nestled into the pass. The climb is
capped by the country's own drawn surface - `Math.min(drawn, floor + distance * PASS_STEP)` - so it stops of its own
accord at 293 to 319, about fifty blocks out, and past that the mountain is the drawing's, sloping away the way it
always did. Nothing is stood up above the drawn massif, so there is no crest to end in a face. The surface carries the
map's folded noise (±14, at twenty-eight and eleven blocks) and crests that wander eighteen either way along the pass,
fading in off the build's own edge so the gate still meets the ground exactly. The foot is the floor on every side and the climb runs out of it at two and a half blocks per block - which is the shape
that read right from every side. What is narrowed is the **opening**: the climb begins thirty-nine blocks inboard on the
west and twenty-four on the east, because the drawn massif does not reach the gate equally on the two sides, so the gap
the build stands in is that much narrower and the ends are into the rock. Inside the footprint the ground is held to
267 - the build's own roof - because a rock left above the build's cells would survive the placing and sit on the
parapet; outside it the mountain is free to stand over the roof.

`PASS_SINK` can lift that foot at the build's ends, and was 60 for a while to bring the mountain in twenty-four
blocks' worth of climb. That is right on paper and wrong in the world: the lift is a **step** at the footprint's edge,
so the sides began with a sixty-block face and had only twenty blocks of slope left before the drawn plateau's three
hundred. The back kept its slope through all of it, because the lift is shared out by distance and is zero behind the
plaza - which is exactly why the rear looked right and the sides went flat. If the foot is to come in again it has to
come in as a longer or a steeper climb, not as height at the edge: shortening this slope is what makes a wall of it.

Two other ways of bringing the foot in were tried and are wrong, and are worth not repeating: shifting the *whole climb*
inboard by twenty-four blocks (the pass floor stops being recognized as the floor, because the early return tests the
distance it has just changed, so the courtyard falls to the drawn ground - which in the pass is the map's 63-level
valley - and the slope is left six blocks to reach a three-hundred-block plateau, which is a wall), and counting
"ground south of the box" as outside the footprint (same 63-level fall, because the courtyard is the pass's floor
running on, not ground beside it). The lift is shared out by
which distance is doing the work (`outX / (outX + outZ)`), so the ends get all of it and the plaza none. The distance the climb is measured over is the distance from the footprint as a
whole - across beside the box, north behind the plaza, south down the pass, and both together at the corners - so the
sides and the back meet as one continuous flank; measured one axis at a time the two ramps ran into each other and left
a crease, which in a mountain is a gap.

Behind the gate there is no rock against the back at all. The gate stands in a pass and is the only way into the
keep behind it, so what is behind it is a floor an army can stand on: thirty-two blocks of plaza, level to the block
(`PASS_PLAZA`), and then the same climb as the sides - two and a half up per block out - until it meets the drawn
massif. The plaza is the pass's own width, so the mountain closes round it on three sides and the gate is the one way
in, which is the whole point of where it stands.

The mouth blends back into the Hithaeglir's low ground,
which is already 174–176 there, so the join has no seam.

### The build's own measurements — 13 November 2026

Read out of the eight templates cell by cell (`tools/grab_build.py`), with the box anchored at the site's own corner:
`WEST` 5496, `FLOOR` 187, `NORTH` -6877.

| | |
|---|---|
| solid cells | **132,268** (and 489,812 air cells carried with them) |
| x | 5496 … 5655 — **160 wide** |
| y | 187 … 267 — **81 tall**, its floor course on 187 and nothing above 267 |
| z | -6877 … -6830 — **48 deep** |
| feet | every column's lowest solid is the floor course: **187 across the whole footprint**, so the ground under the gate must be 187 and no higher |
| tops | the **two central towers** (x 5560 … 5592) and the **west end** (x 5496) reach the full **267**; the wings stand 12 to 47 above the floor; and between the towers, and again between a tower and its wing, the footprint is only the floor course — **the gate is open there, and air above it is what the mountain must not fill** |

So what the terrain has to honour is: floor 187 across the whole 160 by 48 footprint, a build 81 tall, its highest
rock at 267, and two central towers plus the west end standing on that full height.

## What the first world actually made — 13 November 2026

Read out of `run/saves/Mod Test` (the first world generated with the pass, region files written 04:29)
column by column, not guessed from the code:

| | |
|---|---|
| floor inside the box | 186–187 across all 160 × 48 columns |
| wall foot, west | 267 at x 5495, then 272 at 5492, 288 at 5486 — a step, then the drawing |
| wall foot, east | 267 at x 5656, then 274 at x 5660, 313 at x 5671 |
| behind the back | 202 at five north of it, 272 by twelve, 301 further out |
| courtyard | 186–188, with `rockpile` blocks scattered on it |

And the ring the drape bridges, measured the same way: east of the box the ground is 267 two blocks out
and 280 at eight, but **the courtyard wall was a rim** — at z −6700 the floor is 184, the wall foot 249,
the crest 282 at x 5400 and then 255 by x 5360, because the drawn country west of the courtyard is a
basin at about 250. A wall that rises above the ground behind it and then drops is a curved face with the
sky over it, which is what the owner photographed; the bank above is the answer to it, falling from the
crest at one in four and holding the ground up wherever the drawing would have sunk it.

So the pass was doing what it was told and the shape was still wrong in two ways, which is what the
owner saw: the drawn rock is **25 blocks higher on the east than the west**, so a wall that climbed
straight back into the drawing was too small a mountain to read on the left and one that would swallow
the whole side of the build on the right; and the climb itself was a smooth blend, which lays down
one-block treads of even width for eighty blocks along the wall — a pre-placed look. The shoulder and
the noise above are the answer to both.

## Decisions taken

- **Templates, not the cut.** A procedural cut was written and tried in the map (`/tmgate cut`); the
  build lays fast enough that nothing was gained and the cut could carry none of the hand-dressed
  detail. The cut is out of the pack (`35690fc4`).
- **Every cell is carried.** The first capture dropped the map's stone as "not the build" and the gate
  arrived standing in the air with nothing under it. The rock is the build: the gate was dug and
  dressed into a mountainside, and its skirts and retaining walls are that mountain.
- **The ground is cut to the gate, not the gate seated on ground.** The gate brings forty-eight blocks
  of its own mountain, so the world behind its mouth has to be at the gate's height within a few paces
  of the face; a wall that started at the floor would leave the build in a trench of its own making.
- **The mountain climbs out of the floor and stops at the drawing.** Every profile that stood ground up - a lip,
  a shoulder, a peak - ended as a face, because standing rock above the country is how a wall happens. The ground now
  climbs out of the floor at a fixed rate, stair-stepped, `Math.min(drawn, floor + distance * PASS_STEP)` with
  `PASS_STEP` at two and a half blocks per block: it reaches the roof's 267 about thirty-two blocks out, meets the
  drawn massif at fifty or so, and past that the mountain is the drawing's and slopes away as it always did.
- **... and the front stays open.** The pass's own floor runs south from the gate and widens into the courtyard, so
  nothing is stood up in front of it: the climb only exists where the ground is outside the pass. The peak only exists where the pass has not opened: by the courtyard the
  `opened` share has brought it down to the lip with the rest, so nothing is stood up in front of the gate.
- **The gate faces south, four-square.** The site lays it with `Rotation.NONE` from `ArdaMap.gatePlan()` - the middle
  of the box's north row - which fills exactly the same box (x 5496…5655, z −6877…−6830). Turned half round it filled
  the same box but faced **north**, into the mountain behind the plaza, with its back to the approach: the courtyard
  ended up behind the gate instead of in front of it.
- **The gate is the only way in.** It stands in a pass, so the ground behind it is not rock against its back: it is
  a plaza an army camps on, level for thirty-two blocks (`PASS_PLAZA`), with the mountain climbing out of it on every
  side but the gate. Behind is where the camp goes, and the keep is reached through the gate or by climbing the
  massif, which is what the pass is for.
- **The drape only ever adds.** `GundabadGate.drape` walks the ring around the footprint, reads the top
  of the gate at the nearest column and fills the ground up to it with the stone it already has, capped
  with the surface that was there. A drape that cut would lower the very walls the pass was shaped for.

## Open

1. **The gate is not raised by the world.** `/tmpin build` has no gate name — a pin key must be a
   template id or one of `PinCommands.BUILDINGS`, and the gate is eight templates, so a pin on
   `gundabad_gate_0_0_0` would raise one 48-cube. Needed: a `gundabadgate` name in `BUILDINGS`, a
   `Sites` entry that lays all eight from one corner at the fixed rotation, and a pin under it. Until
   then `/tmgate` is the only way the gate stands.
2. **The keep's pin is inside the new east wall.** It is at (5655, 189, −6877), four blocks east of the
   gate's box, and lays the keep over x 5655…5776, z −6877…−6694. The owner is moving it after the gate
   is settled. Note the rock closes behind the gate, so the keep's ground is the courtyard **south** of
   it, not behind it.
3. **The sky above the wings is carried as air** — the capture lists every cell, so the box is whole.
   `tools/gate_capture.py --trim-sky` drops air above the highest block of each column when the voids
   want adjusting. Not run.
4. **The climb has not been seen.** Two and a half blocks per block is a stair of two or three each step, which is
   what was asked for, but no world has been generated with it: unverified is whether that reads as rock rather than as
   a staircase, whether it reaches the drawing smoothly at ~50 blocks, and whether the gate's ends disappear into it.
5. **The Mod Test chunks are the first world's.** They carry the pass as it was before the shoulder, so
   the ground around the gate has to be made again — `tools/chunk_trim.py --radius` with the world
   closed and `--apply`, or a fresh world.
6. **`/tmgate place` is the site, and it has to be.** `here` is the dev tool that lays it at the player's feet and
   facing whichever way they look; `clear` takes the site's box down and `clear here` a box at the player. Every
   screenshot of the gate is only comparable while it lands in the same place.

## How to see it

1. Regenerate the ground: a fresh world, or close Mod Test and take those chunks out
   (`python tools/chunk_trim.py --world "run/saves/Mod Test" --radius <n> ... --apply`), then let them
   make again.
2. `/tmgate place` — lays the eight pieces at the box **unturned**, facade south toward the approach, dresses the
   seam (`GundabadGate.drape`) and reports how many blocks of
   ground the drape added.
3. `/tmgate clear` takes the gate's box out at the site; `clear here` takes out a box at the player's own
   position and facing, for a piece tried with `here`.

## Where the code is

| | |
|---|---|
| `faction/GundabadGate.java` | the parts table, `place`, `clear`, `drape`, `rotationFor`, `missing` |
| `faction/GateCommands.java` | `/tmgate place` (the site), `here`, `clear`, `clear here` (gamemasters) |
| `world/registration/ArdaMap.java` | `GATE_*`, `gateFront()`, `PASS_*`, `gundabadPass()` |
| `resources/data/tolkienmobs/structure/key_structures/gundabad_gate_*` | the eight templates, 1.7 MB |
| `tools/gate_capture.py` | the capture, with `--trim-sky` and `--from/--to` |
| `tools/ammolite_markers.py` | reads ammolite markers out of a save |
| `tools/chunk_trim.py` | takes generated chunks back out so the world makes them again |

## Commits

| | |
|---|---|
| `1a05a182` | Mount Gundabad gets a biome and a country of its own |
| `52249dce` | the gate carried as eight templates, with `/tmgate place` and `clear` |
| `35690fc4` | the procedural cut dropped; templates are the gate |
| `64111493` | the first pass through Gundabad (superseded) |
| `29af27c6` | the pass cut to the gate's box, `/tmgate site`, and the seam drape |
| `ade3f886` | the floor levelled under the gate |

Verification on all of it: `./gradlew compileJava -x createMinecraftArtifacts -q --no-build-cache`
green, and the terrain measured offline by mirroring the generator against the same rasters it reads.
Nothing has been generated or played.
