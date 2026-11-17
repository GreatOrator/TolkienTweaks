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

**The drape's two guards, and the deck line.** `OUTLET_LIFT` (24, water at the build's edge that is outflow) is the
city's and reads aright for the village. `DECK_LIFT` (2, a floor tile over the planted corner) did not: the village's
floor is sixteen blocks over its base, so nothing outside its walls was ever read as a deck and the whole ring was
settled to the tops of the walls. That is fixed by the cut saying what its own floor is - a `deck 16` line in
`hobbiton.parts` - and the drape taking the number from the cut, with the city's two kept as the answer for a cut that
says nothing. The straight band down the south edge's water was seen in game and is not the village's wall: the drape
works there.

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

**The village stands itself, and the towns have names, 16 November.** The Shire was three commands a hand had to type
in the right order, with a world rebuilt in between if the river had moved - which is how a build is tested and no way
to ship one. `Hobbiton.autospawn` raises it on the first start of a world, from the cut shipped in the jar at
`data/tolkienmobs/structure/hobbiton/` (143 pieces and the table, fourteen megabytes beside the city's), at
`ArdaMap.hobbitonPlan()`: the cut laid, the country cleared out of it, and the ring dressed, a band of columns and a
few pieces a tick, because seven hundred blocks of ring in one call is a stall at the exact moment a player is waiting.
The dressing is the city's own `Dressing` job, stepped by the village - a second drape rule for a second build is how a
country ends up with two ideas about where a wall stops. A note beside the cut (`_stands.txt`) is what says the village
is up, and it says *what* stood it as well as where: the cut's own fingerprint (its piece count and a CRC of its table,
`143-fe676c68` as shipped - the table lists every piece, its corner and its size and the `deck` line, so a re-cut, a moved
piece or another floor all read differently, and the whole answer costs a few hundred bytes at a start rather than the
fourteen megabytes of the pieces), a `DRESS_VERSION` a hand bumps when the drape's rules move, and seven probes of the
ground: the top of a column and the block it stands on. A start that finds the print or the version changed raises the village again,
so a re-cut and a ring fixed in code now reach a world that already holds a village instead of only worlds made after
them; a start that finds most of the probes moved raises it too, which is what catches chunks trimmed or a world replaced under
the note; a column counts as moved when its top has moved more than six blocks or the block it stands on is not the block
the note wrote, and most of the seven have to have moved, so one grown tree or one trampled grass block does not cost a
village. The probes read only chunks the level already holds - asking the level for one would generate it on the
start-up thread, the thing this whole pass exists to avoid - so a start that holds none leaves the question to the first
tick somebody is near enough to make it free. The three commands still work and are still the way to re-cut
one.

**The village's own folk, and the hill they stand on, 16 November.** The chunks under the village are the Shire's, so the
game populated them as they were generated - on ground the pass then cleared and laid again. The first raises paid for
that: **eighteen** `Hobbit hit the ground too hard` deaths in one of them, bodies falling with the hill they stood on. A
spawn *rule* is the wrong gate there, because a chunk-generation spawn is finalised before it has a position: the body is
refused as it joins the level instead, while the pass holds the footprint, which is the first moment both the body and its
ground are known. With that gate the same raise logs **no deaths at all**, and runs a little quicker for not carrying
them - 920 ticks (46.0 s), against 1242 (62.1 s) on the run before. Once the ground has settled, `Hobbiton.inhabit`
stands a deliberate handful: **eight** of the Shire's own folk, on columns whose top is within a block of the cut's own
deck with room to stand and something to stand on, so roofs, tree crowns and the hillside outside the walls are passed
over. They are persistent, so a player who comes back finds the same village, and a village raised again over itself is
topped up rather than doubled. The line that says so is part of the measurement: `8 of the Shire's folk are standing in
the village at -700, 87, -512, 0 were there already`.

**The village's folk are somebody, 16 November.** The old pack's clone database holds twenty-two hobbits and fifteen
skins under `customskins/hobbits`; thirteen of those are modern 64x64 art that sits on this model (the two legacy 64x32
ones are left out rather than drawn wrong), so the village now keeps thirteen, one per face. Each is stood with a name
taken from the pack - Belba Butcher, Gararic Rumblebelly, Gomatrudis Proudmead, Ingunde Silentfoot, Isembard Underlake,
Leufroy Swiftfoot, Marcus Hairyfoot, Nick Greenhill, Godomar Barrowes, Amanda Brockhouse, Melissa Headstrong,
Regnetrudis Fallohide, and Hal Cotton, the one name that is ours because the pack's seventh male body has a skin and no
name on it. The name is what finds the face: `HobbitRender` already asks for `middle-earth/<slug>.png` for any named
hobbit, the same rule the tall folk are drawn by, so the thirteen files were copied in beside Frodo's and Bilbo's and no
renderer changed. A name already in the village is not stood twice, so a village raised again over itself keeps its
people. The line now says who: `13 of the Shire's folk are standing in the village at -700, 87, -512, 0 were there
already - Amanda Brockhouse, ...`.

The water out of the village is no longer a rule named after the Shire. `ArdaMap.Channel` is a drawn channel whose level
is a *build's*: the level it leaves at, the grade it falls at, the reach of its corridor and the course the mask drew,
one entry per place, with the village's level read as its pin plus the nine its water stands over it. A second build
with a channel out of it is one more entry and no new code. The Shire's own levels are unchanged: 96 at the wall, one
block in forty of run, 80 at the Y.

The towns are `Places.TOWNS` - Hobbiton, Bree and Rivendell - and a sign board now cycles only those the world has
actually stood, instead of every planted name in the catalogue including barrows and a scout's fire. Rivendell and
Hobbiton are stood at their pins on the first start (`Places.standTowns`), with a `TOWN_RADIUS` of 280 rather than a
camp's 32, so walking into either from any road announces it. **Bree has a catalogue entry, a name and a place among the
towns, and no ground yet** - the first thing it needs is a build and a pin.

`/tmriver at` and `/tmriver along <from> <to> [step]` print what the drawing and the grading make of a column or of a
line of them - ground, water, the valley's ceiling, and how much the valley takes off - read out of `ArdaMap` itself
rather than re-derived in a script. That is the instrument for the next round of tuning, and for the rivers the owner
walks past as the map goes on.

**What the rise costs, measured, 16 November.** A dedicated server on a fresh level, nothing else running, the pass
asked to raise the village with nobody in the world: **1326 chunks of ground** under the 551x468 footprint, asked for
two at a time on tickets and waited for over 1996 ticks; **143 pieces laid in 9.8 s** (68.6 ms a piece, worst 349 ms);
**1290 bands of 200 columns cleared - 257,868 columns and 1.99 million blocks - in 9.5 s**; **701 dress steps in 3.3 s**;
the whole pass about 60 s of wall clock over 1200 ticks against a 5 ms budget. Three ways of doing it were measured and
thrown out. `setChunkForced` on every chunk in one tick generated all 1326 on the server thread and held a tick for
**49 s**. `ServerChunkCache.getChunkFuture` is worse than it sounds - it parks the main thread on the worker pool
(`BlockableEventLoop.managedBlock`) and held a tick for the watchdog's full **60 s**. Laying the cut with the plain
`2` flags ran a shape update per block, which asked the level for the neighbour chunk each time: **11 s** of laying.
The one long tick that is left is **43.2 s**, the game finishing the 1326 newly generated chunks' sections and light in
one go - the same 43 s that used to land in the first piece of the cut (`hobbiton_0_0_0.nbt`, 42.3 s). It was paid in
one tick because the whole region had been generated before anything was written into it, and the drain is the
engine's. The pass now asks for a chunk and takes it in the same tick, a chunk at a time, so nothing is ever pending
for the region as a whole. **That shape is measured now, 16 November, and it holds.** `raiseShireOnStart` in the mod's
server settings takes the gate off at start, and `tools/raise_shire.sh` boots a level of its own with the switch set,
waits for the line, prints the numbers and puts `server.properties` and the config back afterwards. On a fresh level
with **nobody online** the village stood in **1200 ticks (60.0 s)**, and 1210 (60.5 s) on the run before it: a warm
build, 1326 chunks of ground, 143 pieces, 1290 bands and 701 dress steps, and no tick went near the sixty-second
watchdog. That is the run that was missing, and
it is one command away from being repeated. What keeps the cost off a player's first start is still the gate - the
village is raised only while somebody is within `REACH` 192 of it, so the chunks are in a view, ticking and being
finished as the player arrives - plus `/hobbiton raise` for an admin who wants it up in an empty world, and the config
for a server that would rather prepare it in the dark. **Not seen in a client session**: whether the deck, the mouths
and the ring look right is still a walk to Hobbiton, and 60 s is wall clock on a warm build, not a bound on a cold
one. The pass keeps its own tick count now, because the measurement is the line that says it stands.

**Where the roads go next, and it is not built.** The Shire to Bree, Bree to Rivendell, and Glóin's camp to Gundabad -
three routes for what `Roads.ROUTES` has two of, and the village is now a town a road can end at. Nothing of it is laid;
the Shire's own ring is dressed with no road, because `Roads.forGround` answers for the valley and a route to a village
that had no ground until this week is a route planned against a map that did not have it in.

**What has not been checked.** None of the river work has been seen in a world: the bypass's new bend and its fall, the
branch's valley and its mouth at the wall, the fork, and the merged channel's run to the bay are all unbuilt territory.
A world made before 16 November holds the old course and cannot show any of it. The bar's fix is the one piece that can
be seen in the existing world, by running the drape again.

## The portal between the worlds - built 16 November

The ask, in the owner's words: **a portal made in the overworld puts the traveller in Hobbiton, and the way back puts
them at the overworld's spawn point.** Not the same coordinates on both sides, which is what the portal used to do.

It was one method, as expected. `ArdaPortalBlock.getPortalDestination` used to take the traveller's own x and z as the
crossing in the other world; it now takes a place:

- **overworld to Arda:** the crossing is `ArdaMap.hobbitonGate()` - (-470, 97, -123), the gravel of the village's own
  crossing that the owner walked to and gave as the Shire's arrival.
- **Arda to the overworld:** the crossing is the overworld's respawn data (`ServerLevel.getRespawnData().pos()`), which
  is the world's spawn point, so the way home is always the same doorway.
- Both ends then do what they always did: a portal standing within 16 blocks of the crossing is used, and if there is
  none one is built and lit on the ground there, so a traveller never arrives somewhere with no way back.

Still open, and cheap to change when it is next looked at: the arrival doorway is built of ammolite (`buildExit`), so a
doorway that is auto-built inside the Shire looks like the hold's rather than the village's; and nothing checks whether
the village has been raised before letting somebody through into it.

## The Shire's milestone - built 16 November

The travel stone of the village stands at the paving the owner marked, (-503, 101, -314), in the village's north-east
where the paths meet: `ArdaMap.hobbitonMilestone()`, stood with the hold's stone on every start
(`MilestoneHandler.onServerStarted`). That mark is gravel rather than one of the mod's ammolite markers, so the stone is
tried in the spot and then on top of it, and the paving is not dug up for it. A world older than the stone gets it on its
next start, and one that has it keeps it.

## The river that did not drape - open, 16 November

The owner pointed at (-265, 98, -72): stepped grass terraces running down into water on the village's north-east, straight
edges and a sand lip - country that was cleared and never settled. It is inside the footprint, not in the ring, and that
is the reason: the drape settles the columns *outside* the cut's box, while inside it the clear takes each column down to
the cut's own floor and leaves it, so any column the cut does not fill - the river corridor running through the north-east
of the village - is left as a staircase beside the water. Settling those columns too is a change to the drape's own rule,
it wants `DRESS_VERSION` bumped so the fix reaches villages that already stand, and it would be re-run over whatever the
river's mouths look like now. Not started: the fix is easy to describe and easy to get wrong beside the water.

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

**Red for the door, green for the boards - 16 November.** Two more inks, both the drawn gold art with its three shades
re-read as a ramp, so the outline and the light sit where the artist put them: `available_locked` (red `!`) and
`board_available` / `board_turn_in` (green `!` and `?`), codes ``-`` of the same font. Red is the hall's own
page in a herald's hand while the player is below `STRANGER`: the mark used to be silence there, and silence is the one
reading nobody can tell from a hall with no work left. It asks `hallPage` - the herald's own front, not a speaker's page
down the road - so a red mark cannot stand over a body who would hand his page over freely. Cyan keeps its meaning and is
now the ink of the wandering traveller's errand, which is what `QuestGivers` has always spawned.

**A mark over the notice boards - 16 November, built.** The board had no state that could carry a mark, since the paper
lives in the player's pack and a blockstate is the same for everyone looking at the block. It therefore grew an empty
block entity (`notice_board` type, no data, no ticker, no packets) purely to have a renderer, `NoticeBoardMarkRenderer`,
which hangs the mark as a **nameplate** through `submitNameTag` - so the billboard, the distance scaling and the hiding
behind ground are vanilla's, not ours - 2.35 blocks up, clear of the frame's top rail. What it says comes from
`BoardMarks`, which reads the reader's own inventory: no contract means green `!`, a finished one means green `?`, and
work still in hand means no mark, since the board only repeats that it is carried. It answers to `questBadge` too, the
same client switch as the scroll over a giver's head (on by default), so a player who would rather the world carried no
marks turns one setting off and the boards go bare while the plates read as they always did. Existing boards in a world
get the entity when their chunk next loads.

**The marks are the artwork, not letters - 16 November.** The sheet the owner drew (`quest-marks-v0-....webp`, two shapes by
three inks) was cut into six icons on its own pixel grid - a 4x11 `!` and an 8x11 `?`, black outline and three shades of
fill - and each icon is a glyph of a font of its own: `assets/tolkienmobs/font/quest_marks.json` pointing at
`assets/tolkienmobs/textures/font/quest_marks/<shape>_<ink>.png`. The plate names one glyph and puts the mark font on it
(`FontDescription.Resource`), so what it draws is still a letter and the nameplate is untouched in kind - no render pass,
no texture pass, no culling to write. White is work in hand, gold the mainline, cyan an errand; the shape is still the
state. The white `?` has no state that can wear it, since work in hand is by definition the `!`, so it ships with its code
(``) reserved. Two things are unverified because no client has drawn them: the size (11px, about 1.4x the name) and
the seat (ascent 10, one pixel below the baseline).

**The badge over a giver's head - built 16 November, off by default.** The cheap half of the item-over-head idea: a scroll
(`item_scroll`) hung on the giver's own head bone, drawn by the same `BlockAndItemGeoLayer` the hand item already uses, and
shown for the turn-in state only. It goes in that layer rather than a second one because every `BlockAndItemGeoLayer`
shares one data ticket (`CONTENTS`), so two of them on one render state would each read the other's list - the badge is a
second entry in the same list, and `submitItemStackRender` picks its pose by bone name: `head` lifts it 1.15 and draws it
at 0.65 scale, the hand keeps its own offset. `TolkienMobsConfig.questBadge` (client, default true) is the switch for every
mark the world draws - the notice board's as well as the scroll - and the plate's own mark is never touched by it, so the
text `?` and the scroll can be put side by side; the state it reads is `QuestMarkRenderer.asksForReturn`, which asks the
same call the plate asks, so the two marks cannot disagree. It is bone-locked: it turns and ducks with the head and the
ground hides it the way it hides him, which is the trade for not building a billboard pass. **Not looked at**: no client
ran, so where the scroll actually sits over a man's head and a hobbit's - and whether 0.65 at 1.15 reads at ten paces - is
unverified.

**The mustering halls are pinned in their towns, not generated - 16 November.** There is one hall per alliance and a
dozen towns to come, so a hall scattered by a spread stood in a field before the town that owns it existed. The three
definitions are gone from `TolkienStructureProvider.STRUCTURES` and with them the generated structure, structure set,
template pool and `has_structure` tag files (`runData` regenerates the rest cleanly); the templates stay in
`data/tolkienmobs/structure/faction_hall_<alliance>.nbt`. In their place is a pin per town: `/tmpin hall add <town>` puts
the hall where the caller stands and prints the `Pins.SOURCE` line, `/tmpin hall remove <town>` forgets it, and the town
argument is suggested from the catalogue's towns - Hobbiton, Bree, Rivendell - rather than typed freehand. The key is
`hall:<town>` (`TownHalls.key`) and the template is chosen from the town's alliance, which is the one thing the caller
does not have to say.

**The hall stands itself - 17 November.** `TownHalls.raise` reads the pin and lays the town's own template, unturned,
with its ground course in the block the builder stood on (the pin is then the hall's floor, not a slab one course above
it), asking for every chunk the box touches first and leaving a hall that already stands alone. It waits for its town,
because a town's pass lays, clears and dresses the ground a hall stood early would be taken away with: `TownHalls.waits`
asks `Hobbiton.complete` (the village's note, written as its pass ends) and `CityCommands.complete` (the valley's marker
at its last band). Three hands raise it - the village at the end of its own pass, before its folk are stood so nobody is
placed inside a wall; the next start, for a world whose town already stands; and `/tmpin hall add` itself, when the town
is finished, which says so in the reply and otherwise says the hall is waiting for the town. **Not looked at:** no hall
has stood in a town in game, so the seat, the 16x16 box against the village's own paving, and the front of the build are
all unverified.

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

- **Sign posts** - the towns are the filter now (`Places.TOWNS`, stood at their pins on the first start), so a board
  cycles the blank and Hobbiton / Bree / Rivendell and nothing else. What is left is the placer: a post stood at a
  road's junction, its arm turned to the side the town lies, and `setDestination(board, place)` called with the
  distance measured for it. Nothing places posts today, and no posting pass waits on it - a hand-placed post already
  reads right.
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
