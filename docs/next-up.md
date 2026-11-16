# Next up

The working state of the campaign, written so a session picking this up cold does not have to guess. **Read this first**,
then `docs/chapter-two-state.md` for the chapter being walked and `docs/chapter-three.md` for the one after.
`docs/HANDOFF.md` is the long history - how each thing came to be - and `docs/arda-roads.md` the road design.

**Last touched:** 16 November 2026 - the Shire map is placed, cleared and draped in a test world, and the drawn
river is rerouted round it. Read *The Shire map* below for exactly where that stands before touching any of it.

The whole Gundabad line is walked and settled; chapter two is proven to page 32 and
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
| 9 | **The Shire map** - Hobbiton | placed, cleared, draped; river rerouted; **join and drape unseen after the fixes** | `run/hobbiton_art`, `/tmcut`, `rivers.png`; see below |

## The Shire map - cut 15 November, placed 16 November 2026

**Where it stands.** The build is cut and in a test world at pin **(-700, 87, -512)**; `place`, `clear` and `drape` have
all three been run on it. The river is now **rerouted round the west of it, a branch drawn out of its south mouth, and
the two brought together in a Y south of the village** (16 November - see below); the mask change needs a world made
after it. What has still not been seen in a world is any of the river work, or the drape reading a village's outline
from under its trees.

**The pass, in order** (a cut lives in `<save>/tolkienmobs/cuts/<name>/`, or in the jar beside the city's):

    cp -r run/hobbiton_art "<save>/tolkienmobs/cuts/hobbiton"
    /tmcut table hobbiton                  (143 pieces over 2 bands, x 0..550, z 0..467, y 0..89)
    /tmcut place hobbiton -700 87 -512
    /tmcut clear hobbiton -700 87 -512
    /tmcut drape hobbiton -700 87 -512

Anything drawn in `rivers.png` only reaches chunks generated **after** the edit, so a world that already holds the old
course has to be replaced to see the reroute - and `drape` must run after `clear` in that new world, or its outline is
the cut buried in Arda's ground.

**What each word does.** `place` lays the pieces from the cut's own low corner, no rotation. `clear` takes two bites:
everything above the cut's top comes off, then every position the cut leaves empty between its top and its bottom has
the country taken out, so Arda's stone under the canopies and the gravel in the old bed go. `drape` is the city's own
pass, generalised: it settles the ring outside the footprint to the build's outline, easing back over
`Dressing.DRAPE_REACH` (32) blocks, and leaves water at the edge alone where it is the build's outflow.

**What is still open.** The drape's two guards were written for the city: `OUTLET_LIFT` (24, water at the build's edge
that is outflow) and `DECK_LIFT` (2, a floor tile over the planted corner). Hobbiton's floor is sixteen blocks over its
base, so `DECK_LIFT` never fires - everything outside its walls is read as a roofline and may be cut. The open question
is the straight band running down the middle of the water at the build's south edge: if it is the drawn channel's own
edge showing through the wider carved river it is fine, and if it is the village's cut wall standing in the water then
the drape needs a guard it does not have - never cut the ring below the country's own ground where the wall is water.

**What has been checked, and what has not.** Every step compiled (`./gradlew compileJava -x createMinecraftArtifacts`)
and the mask work was measured against the game's own 30% water threshold. None of the last round has been run in a
world; the tree reading, the two joins and the drape's behaviour on a wooded build are all unseen.

**The cut.** The player's own map `Hobbiton_Divici` is a hand-built Shire in its overworld, marked by four ammolite
blocks at (‑456, 6, 276), (94, 6, 276), (‑456, 6, 743) and (94, 6, 743): **551 × 468 across, base y 6, content y 8…95**,
palette vanilla throughout - 148 names, no modded blocks - so nothing had to be remapped. It is cut whole into
`run/hobbiton_art`: 143 pieces of 48-cube plus `hobbiton.parts`, 14 MB, every block kept except the four markers
(`cut_world.py --without tolkienmobs:block_ammolite`), verified block for block against a separate count of the world -
5,079,392 distinct positions on both sides - and rendered top-down to confirm the village, the pond and the field strips
came across whole.

**The height.** The Shire stands at **y 102** where the build goes, not the 68 its cells are painted at: read in game at
(-631, -436) the bank is grass at 102. Measured against the drawn channel's own water - 96 there - the pin is **(-700,
87, -512)**: the build's water sits 9 over its base so it lands at 96, level with the channel, and its grass line (16
over its base) at **103, one block over the bank**. Footprint x -700…-149, z -512…-45.

**The river - rerouted west, the branch cut, and a Y south of the village.** Drawn 16 November into `rivers.png`,
measured against the game's own 30% water threshold, and the picture of it is `run/river_reroute/shire_y_interpretation.png`
with the script that made it beside it (`shire_y_plan.py`). What it does:

- **The bypass.** The drawn channel used to cross the footprint's north-west corner - its east bank at x -584 at
  z -472, inside by 116 - and leave by the west wall at z -190. It is now slid west there: the channel is cut at z -600
  and redrawn as a bend west to x -744 by z -404, easing back to its own course at z -204, so its east bank stands 20
  to 28 blocks clear of the wall over the whole crossing and 4 at the mouth, where the water is meant to meet it. The
  west arm of the village's river comes out at z -203..-176 against water at 95-96 - the join the pin was chosen for.
- **The branch.** From the south mouth (28 wide at x -234..-178, water 96, bed 86) a channel runs south-west along
  `(-206,-46) (-230,-6) (-270,40) (-320,80) (-380,112) (-450,140) (-520,165) (-590,190) (-652,212) (-712,232)
  (-770,246)`: **56 wide at the wall narrowing to 36** at the fork, one block of fall per 40 of run.
- **The Y.** The drawn river stopped dead at z 40 (water 80), so it is carried on south from its own end and the two
  meet at **(-770, 246)**, water 80-81, and run on as one channel south-west down the vale - 76 at z 300, 70 by z 400 -
  into the head of the bay at about (-1000, 508), where the heightmap is sea from (-910, 340) west.

**Why the branch needs code.** The village's water is 96 and the country straight outside its south wall is drawn at
105…113 (the drape settles the ring at the mouth back to 96-98, then the hillside is the drawing's). A drawn river's
water is laid at the ground, so a branch drawn there would stand thirteen blocks over the village's own water and the
south arm would end against its own wall under it. So the branch is the one drawn channel whose level is not the
drawing's: `ArdaMap.SHIRE_WATER` (96, the village's own - base 87 plus the nine its water stands at) less one block per
`SHIRE_GRADE` (40) of run along its course, read for any column within `SHIRE_REACH` (64) of that course and taken as a
minimum in `drawnWater`, so nothing outside the branch moves. Everything else follows from that number: the bed is cut
under the surface (85 at the wall, 78 at the fork), so the branch is a gorge thirteen deep at the wall that opens to two
at the fork, and the ground beside it is graded against the water that is really there.

**The fall at the corner is deliberate.** The ground west of the channel is 5-9 blocks over the channel's own line, so
the bypass's water is up to 7 over the river it leaves at z -600; the game's 24-block level reach cuts that into a short
chute at the corner, north of the build and out of the village's sight, and everything downstream of it descends
properly. Accepted when the route was chosen.

**The bar across the mouth - fixed in the drape, 16 November.** The save showed four to six blocks of land lying across
the water where the village's west arm meets the drawn channel. The mouth itself was left open on purpose - a pass that
fills the ground over a build's outflow dams it - but the ring's columns in front of a mouth clamp to the mouth's own
column, so every one of them read as water and was left alone with it, and the country's own bank stood eight blocks
out. `Dressing.openMouth` now takes the ring's ground out at a mouth: down to `CityCommands.MOUTH_DEPTH` (3) under the
water's top where the ground stands over that bed, and untouched where the column is already the drawn river, so the join
is the drawing's channel meeting the build's rather than a trench dug through the river to reach it.

**The gap in the water - filled by the same cut, 16 November.** The bar went, and left the bed behind it: the mouth's
water stands in the build, the drawing's channel stands in the ring, and between them lay a strip three blocks deep with no
water in it at all, so the branch came out of the north mouth and stopped. `openMouth` fills what it takes out back up to
the level the mouth carries the water out at - never higher, so the fill cannot stand as a wall of water over the country
- and the city's water and the drawing's are one sheet across it. **This is a drape change, not a world change: `/tmcut
drape hobbiton -700 87 -512` again in the existing world clears the bar and fills the strip.**

**The river's banks - a valley instead of a slot, every drawn river, 16 November.** The save showed the Shire's branch
as a canal in a trench: its bed is cut under water the code reads from the village, and nothing was ever told to bring the
hillside down to it, because a drawn river normally stands at the height of the ground it is drawn on and its banks are the
country. That is only true where the channel's level is the drawing's own, though: the same trench is cut wherever the
mountain mask lifts the ground over a river, and by the same cause. So `ArdaMap.riverBank` is the other half of every such
cut - the dale's `daleBank` with the water's own edge taken out of it.

It is a raster of the whole map, grown out from every cell the drawing marks as river water (lakes aside: their shore has
`SHORE_CUT` of its own) one cell at a time, seeded at water + 1 and rising `RIVER_STEEP_MIN`…`RIVER_STEEP_MAX` (3…16
blocks a cell, a third of a block to two per block of run) until the ground beside it is the country's own again, never
further than `RIVER_BANK` (192) out. One rate could not serve both ends: at a half a block per block a valley fifty deep
wants a hundred blocks of run, and a channel the mask has lifted two hundred blocks of ground over wants four hundred, so
the reach would either leave a face standing at its own rim or bite a quarter of a cell into every river in the world. The
rate is read per channel instead - the tallest country `RIVER_SAMPLE` (96) blocks round the water, less the water, divided
by the reach, held between the two - and rounded up, so the wall always reaches the country before the raster ends. Read
between cells like the dale's bank, with `RIVER_ROLL` (2) of roll faded by how much the valley actually takes off the
column, so a bank it shaves by a block keeps its edge.

The dale's water is left to the dale: `buildRiverBanks` seeds nothing where `ArdaMasks.daleRun` reaches, and where the two
rasters overlap the lower wins, so the Bruinen is graded once, by `DALE_RISE` toward `DALE_BANK`, and the Shire's branch
is graded by this pass alone - `shireBank` and its `SHIRE_SHELF`/`SHIRE_SHOULDER`/`SHIRE_BANK`/`SHIRE_ROLL` are gone, and
the `SHIRE_WATER`/`SHIRE_GRADE`/`SHIRE_REACH` level rule is untouched.

Measured on the drawn ground against the ceiling. The branch: at z 40 the ground comes down from 108 at x -380 to 94
beside water of 93…96 and climbs back to 105 by x -220; at z 140 from 112 to 89 and back to 104 by x -260. An ordinary
drawn river, at z -560 and z -620 west of the build, is untouched: the ceiling stands 1…10 blocks over the country along
the whole of both banks, so nothing is graded. The deepest lift over river water in the world - 204 blocks, at (15840,
7616) - is cut into a valley and closes to within a dozen blocks of the 285…301 country at the raster's rim. **This is
world generation: it needs chunks made after it.**

**What has not been checked.** None of the river work has been seen in a world: the bypass's new bend and its fall, the
branch's valley and its mouth at the wall, the fork, and the merged channel's run to the bay are all unbuilt territory.
A world made before 16 November holds the old course and cannot show any of it. The bar's fix is the one piece that can
be seen in the existing world, by running the drape again.

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
