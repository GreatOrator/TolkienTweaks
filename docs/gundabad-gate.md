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
| wall foot at the gate's edges (x 5495 / 5656) | **285** — eighteen above the top of the build's wings, so the ends sink in |
| the mountain round the gate | **285 at the build's own edge**, rising over 28 blocks to a **peak of 330** - sixty-three above the roof |
| the crest | held for 16 blocks, then falling at six in sixteen until the country's own ground is higher |
| its surface | broken by ±14 of folded noise and ±18 of crest wandering along the pass, both held off the build's edge |
| behind the gate | a **plaza**: level at 187 for **32 blocks** north of the box's back, then the mountain |
| the mountain behind the plaza | climbs to the same 330 over 56 blocks, holds the crest, then falls into the country |
| gap | 160 wide through the gate's depth and sixty blocks south, then opening |
| courtyard | 540 wide by z −6650, 756 at the mouth |
| floor along the pass | 187 at the gate, 175 at the mouth |

Inside the pass the ground is the floor. Outside it the ground is **drawn to the gate's footprint**: at the
build's own edge it already stands eighteen blocks above the top of the wings (`PASS_SINK`), so the ends of the
gate are sunk in rock rather than meeting it; over twenty-eight
blocks it climbs to a peak of 330, sixty-three above the roof, so the mountain stands over the build rather than
beside it; the crest is held for sixteen; and from there the slope falls at six in sixteen until the country's own
ground is higher than it and takes over, about a hundred blocks out. The surface carries the map's folded noise
(±14, at twenty-eight and eleven blocks) and crests that wander eighteen either way along the pass, held off the
build's own edge so the gate still meets the ground exactly where its ends sink in. The courtyard is untouched:
its floor is open, its walls are the country's and the peak has come down to the lip by the time the pass opens.

Behind the gate there is no rock against the back at all. The gate stands in a pass and is the only way into the
keep behind it, so what is behind it is a floor an army can stand on: thirty-two blocks of plaza, level to the block
(`PASS_PLAZA`), and then the mountain, which climbs to the same 330 over fifty-six blocks (`PASS_BACK_RISE`) and
falls away into the country beyond that crest. The plaza is the pass's own width, so the mountain closes round it on
three sides and the gate is the one way in - which is the whole point of where it stands.

The mouth blends back into the Hithaeglir's low ground,
which is already 174–176 there, so the join has no seam.

### What the first world actually made — 13 November 2026

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
- **The mountain is drawn to the footprint.** Standing ground up as a lip, then as a shoulder, then shaving
  it, each failed one way or the other; the shape the owner asked for is a mountain *round* the gate. So the ground
  at the build's own edge is the wings' height - the ends sink in - rises over twenty-eight blocks to a peak at 330
  (`PASS_PEAK`), holds it for sixteen (`PASS_CREST_KEEP`) and falls at six in sixteen (`PASS_FALL`) until the
  drawing is higher and takes over.
- **... and the front stays open.** The peak only exists where the pass has not opened: by the courtyard the
  `opened` share has brought it down to the lip with the rest, so nothing is stood up in front of the gate.
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
4. **The peak has not been seen.** It is written from the owner's brief - mountains surrounding the footprint,
   ends sunk, a peak over the roof, the front clear - and from the save's measurements, but no world has been
   generated with it: unverified is whether 330 reads as a mountain over the gate or a berm, whether the fall is
   slow enough to meet the drawing without a seam, and whether the build's ends sink as far as they want.
5. **The Mod Test chunks are the first world's.** They carry the pass as it was before the shoulder, so
   the ground around the gate has to be made again — `tools/chunk_trim.py --radius` with the world
   closed and `--apply`, or a fresh world.
6. **`/tmgate place` still lays at the player's feet** — a dev tool for trying the gate elsewhere.
   `/tmgate site` is the real one.

## How to see it

1. Regenerate the ground: a fresh world, or close Mod Test and take those chunks out
   (`python tools/chunk_trim.py --world "run/saves/Mod Test" --radius <n> ... --apply`), then let them
   make again.
2. `/tmgate site` — lays the eight pieces at the box, facade south, and reports how many blocks of
   ground the drape added.
3. `/tmgate clear` takes the gate's box out again; it does not take the drape down, and it is the
   player's position and facing that decide the box, so clear where you placed.

## Where the code is

| | |
|---|---|
| `faction/GundabadGate.java` | the parts table, `place`, `clear`, `drape`, `rotationFor`, `missing` |
| `faction/GateCommands.java` | `/tmgate place`, `/tmgate site`, `/tmgate clear` (gamemasters) |
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
