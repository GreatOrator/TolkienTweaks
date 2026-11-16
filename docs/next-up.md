# Next up

The working state of the campaign, written so a session picking this up cold does not have to guess. **Read this first**,
then `docs/chapter-two-state.md` for the chapter being walked and `docs/chapter-three.md` for the one after.
`docs/HANDOFF.md` is the long history - how each thing came to be - and `docs/arda-roads.md` the road design.

**Last touched:** 15 November 2026. The whole Gundabad line is walked and settled; chapter two is proven to page 32 and
**paused** there; the road east of Glóin's camp is final with both bridges; the keep's placement is **on hold** until a
brand-new world is drawn. Work stopped here to go to a side project.

## Where we stopped

- **Chapter two works to page 32 (2-7).** Played in the test world from the chapter-one seam through the Gundabad pages:
  the courtyard scene, Glóin's camp, Ulmug's fires, the hold raised when its page is taken, the vault, Gimli in his own
  cave, and the captain and the wraith below the stair. It reads as written; nothing in it is waiting on a fix.
- **Paused at page 33.** The four left - 33 Steps of Evil I, 34 Steps of Evil II, 35 Evil Tidings, 36 Fire & Ice - are
  all Carn Dûm ground, and **Carn Dûm is what quests go to next**.
- **The keep's placement is on hold**, not tuned: the world it was looked at in was built around the older plan, so it is
  read from a brand-new world rather than adjusted in that one.

## The list

| # | Item | Status | Where the detail is |
|---|------|--------|---------------------|
| 1 | **Carn Dûm's ground** - the camps on its borders and the wight's hall | **next** | nothing has looked at it yet; `carndum` is only a place canon'd to `ANGMAR` |
| 2 | **The roads from Glóin's camp to Gundabad** | not started | work the line out from the map; `/tmroad` is the tool |
| 3 | **Chapter two, pages 33-36** | paused on 1 | `docs/chapter-two-state.md` |
| 4 | **Chapter three** - eleven pages, prose written | not started | `docs/chapter-three.md` |
| 5 | **The keep's placement** | on hold | `docs/gundabad-keep.md` |
| 6 | **Sign posts** | on hold | below, with the recipe |
| 7 | **Chinese** | on hold | whole, until a full public release |
| 8 | Chapter four onward, the finale, the job boards (quest 37) | not started | `FINALE_OPEN = false`, closed by design |
| 9 | **The Shire map** - Hobbiton, cut and waiting on a location | cut, unsited | `run/hobbiton_art`; see below |

## The Shire map - cut 15 November 2026, waiting on a location

The player's own map `Hobbiton_Divici` is a hand-built Shire in its overworld, marked by four ammolite blocks at
(‑456, 6, 276), (94, 6, 276), (‑456, 6, 743) and (94, 6, 743): **551 × 468 across, base y 6, content y 8…95**. Its
palette is vanilla throughout - 148 names, no modded blocks at all - so nothing has to be remapped.

It is cut whole into **`run/hobbiton_art`**: 143 pieces of 48-cube plus `hobbiton.parts`, **14 MB**, every block kept
except the four markers (`cut_world.py --without tolkienmobs:block_ammolite`), verified block for block against a
separate count of the world - 5,079,392 distinct positions on both sides, and the cut rendered top-down to confirm the
village, the pond and the field strips came across whole. Nothing is in the jar yet; nothing is placed.

**What it needs next:** a location in Arda's Shire, which is cells 8…11 by 19…21 - **x −1024…1023, z −512…1023**, drawn
at height 68 with ±4 of relief, its centre around **(0, 70, 255)**. The parts table's offsets run from the build's own
north-west bottom corner, so the pin that goes into `Sites` is that corner: everything extends +551 east and +468 south
of it. Once the pin is chosen the build wants a `Sites` entry and a placement pass like the gate's.

**The build's own river can be joined to a drawn one.** The cut's water is a pond in its south-west quarter with two
arms: one leaves its **west edge at local z ≈ 318**, one leaves its **south edge at local x ≈ 150…320**. Arda's west
channel comes into the Shire at (-600, -512) and runs south-south-west. **The Shire stands at y 102 here, not the 68 its
cells are painted at:** read in game at (-631, -436) the bank is grass at 102 and the block under the channel's surface
is dirt at 100. Measured against the channel's own water, the height that puts the village's river level with it is
**y 87** - the build's water sits 9 over its base, so the channel's surface there is 96 - and that brings the village's
grass line (16 over its base) to **103, one block over the bank at 102**, which is as near as a build of its own height
gets to sitting on the ground it was put on.

A corner at **(-700, 87, -512)** puts the west arm's mouth four blocks east of the channel's east bank - a butt join,
no digging. What it costs is the channel above it: the drawn river runs diagonally, so any 551 x 468 box that touches it
anywhere buries some of it, and this one covers the stretch from z -512 down to z -265 under the plateau's north-west
strip, 249 of the channel's cells. Pins further east buy the burial back at the price of a dug connector - **(-620, 87,
-512)** covers 80 of the 249 and wants an 84-block canal from the mouth, **(-600, 87, -500)** covers 32 and wants 104.
The south arm still ends at the plateau's edge at world x -586…-436, z 22 - a waterfall, or a short dug run, is the
choice there, as there is no drawn water on that side to meet.

**Laying it needs no city.** `/tmcut` reads any cut from the world's own `tolkienmobs/cuts/<name>/` - or the jar's
`data/tolkienmobs/structure/<name>/` - so a test world gets the map by copying `run/hobbiton_art` to
`<save>/tolkienmobs/cuts/hobbiton/` and running `/tmcut table hobbiton` to read the box, then
`/tmcut place hobbiton -700 87 -512`. Then `/tmcut clear hobbiton -700 87 -512` takes Arda's own ground back off the top
of it: the hill, the trees and the drawn channel standing over the build come down to the build's own top column by
column, so its shape reads against the terrain at a glance - the scar the player asked for, on purpose. It plants no
marker and dresses no ground: what goes down is the cut's blocks
and nothing else, which is what makes it the hand for asking this question rather than for shipping the map.

## Roads - final, nothing outstanding

Laid, raised and called right in game:

- **The old forest road.** `OLD_FOREST_ROAD` in `Roads.ROUTES`: 8231, -3641 to 11842, -3877, three thousand eight hundred
  blocks, five wide (`Kind.WILD_WIDE`), the hundred and one ammolite marks each a bend. Graded on halves, so the terraces
  are stairs rather than jumps.
- **Mirkwood.** The road walked up into the canopy until the nine hardened leaves were tagged `minecraft:leaves`
  (`0e5e98fb`); it walks the floor now.
- **Both crossings**, on nine-wide decks of `bridge_end`/`bridge_middle` with oak-log piers drawn to the bed:

| crossing | route | banks (east → west) | deck | abutments' base | middles' base | water |
| --- | --- | --- | --- | --- | --- | --- |
| Old Ford | `OLD_FORD_BRIDGE` | 8520, -3668 → 8481, -3668 | 189 | 188 | 187 | 186 |
| Forest River | `FOREST_RIVER_BRIDGE` | 11775, -3841 → 11736, -3841 | 169 | 168 | 167 | 167 |

If a deck ever has to move it is three runs of one command: `/tmroad forget` then `/tmroad build` at the crossing, and
again at the road's camp end (8231, -3641) so the approaches re-grade onto it - a road already laid keeps its heights.
A span is a `Route.Crossing` stood from its **east** bank down the axis, and a road leaves water to it within
`CROSSING_ACROSS` (12 blocks) of the span's line. `docs/arda-roads.md`, section *Water a road was told about*, has the
design.

## Quests - chapter two

Eleven pages, 26 to 36, written and wired; **played through page 32** and working. The page table is in
`docs/chapter-two-state.md`. What a session needs:

- **Gundabad's bodies:** Gimli in his own cave east of the gate, on the room's dais (5703, 188, -6676); the captain on
  the throne (5546, 204, -7036) with the wraith three blocks along (5546, 204, -7039) - both placed from
  `ArdaMap.keepThrone()`/`keepThroneWatch()`, so they follow the hold.
- **The gate and cave** stand themselves as the server comes up; the coal lattice door is x 5575-5581, y 190-199,
  z -6856 and opens for `item_keystone` within 12 blocks.
- **The keep** stands itself too, but its placement is on hold (item 5).
- **Pages 33-36** are Carn Dûm ground and are paused (items 1 and 3).

## Settled - do not re-open

- **The Gundabad gate is final.** `GundabadGate.autospawn` stands it at `ArdaMap.gatePlan()` and leaves a standing gate
  alone on two answers: the netherrack course, and the pieces' own blocks through `Sites.standsAt`. `/tmgate place`
  re-lays; `clear` is the way back.
- **The captain of the Dourhands is down once, and stays down.** `DUGORIM_DOWN_TAG` on the hand that fells him; seen in
  game, felled again, did not come back.
- **The wraith was never broken.** Its mark was always in place and it behaved - killed once, gone.
- **The inner keep's stair is final and open.** No door goes over it; the hold is a place to explore, page 29 stays
  dialogue.
- **The chapter-one seam is final.** The courtyard scene between pages 25 and 26 ran smoothly as staged;
  `docs/departure-scene.md` is a record, not a task.
- **The spikes are settled** as decoration with incidental damage.
- **The biome audit is closed whole** - eleven findings applied, the Barrow-downs' deadwood kept on purpose, the four new
  leaf textures called right. `docs/arda-biome-canon.md`.

## On hold, and what unblocks each

- **Sign posts** - held on a second town, not on the blocks. Legibility is confirmed and the shaft, plinth and cap seat
  right. A board can only point at a place a world has planted, **Rivendell is the only town so far**, and
  `SignPostArmBlockEntity.destinations()` offers every planted place in `Places.catalogue()` - so a board would happily
  read *Matuna's Lair*. When more towns exist: give `Places.Name` a kind (it is
  `record Name(String id, @Nullable Alliance alliance, boolean beryl)` today) or add a `Places.TOWNS` set, and filter
  `destinations()` to it, so a hand-placed board cycles `""` ↔ the towns. An automatic post is then a placer that stands
  an arm on the side the town lies and calls `setDestination(board, place)`, which measures the distance itself. Nothing
  places posts today, so no posting pass waits on it.
- **The keep's placement** - unblocked by a **brand-new world**, which is also how the rest of the quest walk is meant
  to be done. `ArdaMap` carries `KEEP_X` 5516, `KEEP_Z` -7112, `KEEP_FLOOR` 187 (box x 5516…5637, z -7112…-6930, floor
  top 189, south wall at -6930 where `keepWall()` ends the hall), the captain and wraith follow it, and the owner's old
  mock-up of the seat - slab and red carpet at (5501, 192, -7024) - is left standing west of the hold and can go.
  `GundabadKeep.autospawn` reads the template's own blocks through `Sites.standsAt`, with the box opened first, and
  carries the hall to the keep's wall every pass.
- **Chinese** - unblocked by a public release; the rework's lines are translated then, not line by line.

## Not started

- **The roads from Glóin's camp to Gundabad.** `ROUTES` stops at the old forest road (`GLOIN_ROAD_EAST` runs
  6493, -3662 → 8234, -3590, with the camp's spur at 8215..8242, -3674..-3553). Nothing goes west or north toward the
  hold at 5516, -7112, whose gate approach is around 5577, -6830, and pages 2-1 to 2-3 are a walk - so the walk wants a
  road under it: a new `Route` (or legs, with a `Route.Crossing` for water), pinned in `Pins.SOURCE` if it needs a mark.
- **Chapter three.** Eleven pages (3-1 to 3-11) written, nothing in `Campaign`. Before code it needs ground, bodies and
  objects: Mitheithel, Stoneyford, **the Watchers**, the dead stones, Dol Guldur and the Moria camp; five bodies
  (Aragorn, Guthwine, Manwyn, Narmos, Rurain) and the wight as the Ancient Spirit; two props and one charm. Its first
  question decides the rest: **whether Stoneyford and the Watchers are built here or already stand in the map's world.**
- **Chapter four onward**, the finale (`FINALE_OPEN = false`, one front in `FRONTS`, closed by design), and the notes'
  job boards (quest 37).

## Small, recorded, harmless

- `Places` canon's the place `gloin` to `HITHAEGLIR` while the camp's site is in `GLADDEN`. Nothing targets
  `placeTarget("gloin")` today; a page that did would point into the mountains.
- `Wet.FORD` and `carry` are unused now that both crossings are bridges - kept with the path intact for a future ford.
- A road already laid is never taken back: `surfaceAt` reads the old embankment as ground, and `forget` clears only the
  saved alignment and grade.

## The rules this project is walked by

- One `CHANGELOG.md` bullet per change, in the long `- **Claim.** …` prose.
- Commit to `main` from `TolkienTweaks/`; **never push**.
- "Verified" means `./gradlew compileJava -x createMinecraftArtifacts -q --no-build-cache` exits 0, and the answer always
  says what was **not** seen in game. Never claim a playtest.
- Java files are CRLF; docs are mixed - check before writing - and patches go through a `build/` script that is deleted
  after.
- `runData` cannot run while the client is open, and `src/generated/resources/.cache` has to be deleted or it writes
  nothing.
- `docs/HANDOFF.md` is the long history and is worth reading before changing a system it explains.
