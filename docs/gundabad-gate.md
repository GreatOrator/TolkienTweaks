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
| wall foot at the gate's edges (x 5495 / 5656) | **267** — the height of the build's own wings |
| walls eight blocks out | 277 west, 301 east |
| rock behind the back face | 200 at 3 north, 231 at 6, 277 at 10, **301 at 14** |
| gap | 160 wide through the gate's depth and sixty blocks south, then opening |
| courtyard | 540 wide by z −6650, 756 at the mouth |
| floor along the pass | 187 at the gate, 175 at the mouth |

Inside the pass the ground is the floor; past its edges the drawn rock comes back over sixteen blocks
at the gate (a face) and a hundred and fifty once the pass has opened (a hillside). The mouth blends
back into the Hithaeglir's low ground, which is already 174–176 there, so the join has no seam.

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
4. **Nothing has been seen in game with the new pass.** Unverified: the wall foot really arriving at
   267 at the gate's edges (that comes off the drawn rock, not off the build), whether a sixteen-block
   climb reads as a rock face or a quarry cut, the drape's fill, the courtyard's width, and whether the
   keep fits in it.
5. **The Mod Test chunks at the gate are old.** They were made with the first, wide pass; they need
   `tools/chunk_trim.py --radius` around the gate (world closed, `--apply`) or a fresh world before
   anything can be measured.
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
