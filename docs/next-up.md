# Next up

The working state of the campaign, written so a session picking this up cold does not have to guess. **Read this first**,
then `docs/chapter-two-state.md` for the chapter being walked and `docs/chapter-three.md` for the one after.
`docs/HANDOFF.md` is the long history - how each thing came to be - and `docs/arda-roads.md` the road design.

**Bucklebury is a cut in the jar now, and it was cut from the reference build rather than from the export, because the export has no block states.** `Reference/hobbitvillage.schem` and the 60 tiles in `run/bucklebury_art` are the same build cell for cell - 3,638,985 cells each way, none missing and none extra - but the tiles' palette names every block bare: 246,137 cells lost a state, so leaves come back at distance 7 and `persistent=false` (they decay), tall grass as two lower halves, falling water as level 0, horizontal trunks stood up and every stair, slab, wall, fence, door and bed unfaced. The cut in `src/main/resources/data/tolkienmobs/structure/bucklebury` is the reference's states with the export's modern names, carried over on exactly two renames (`grass` to `short_grass`, 37,874 cells; `grass_path` to `dirt_path`, 301), 80 pieces of 48 cubes or less in a 478 by 77 by 278 box, `deck` 13. **The table is verified against the files**: every piece present and listed, sizes agreeing, 3,638,985 cells placed once with no overlap, every cell identical to the reference, 207,962 carrying a state. **What is not in it:** block entities - chests, pots, campfires, signs, brewers, smokers and the cauldron arrive empty, as Hobbiton's pieces do. **Air is not written either, so the town must be cleared before it is placed** (`/tmcut clear bucklebury <x> <y> <z>` then `/tmcut place bucklebury <x> <y> <z>`), which is the order the Shire's own raising uses; 10,912 columns have a room in them that only a clear opens. **What placement still needs:** the corner and its height at the pin (1380, 91, -3432), which wants the art's west edge (ground 13 over its own corner) set at the drawn ground there, and then either a raising class of Bucklebury's own on the model of `Hobbiton` or the two commands by hand. **Not seen in a world:** no world was made and nothing was laid.

**The roots under the mallorn and the mirkwood never generated, and the cause is the first block of every strand.** The trace began at `logs().getFirst().below()` - the ground under the lowest trunk block - and `traceRoot` writes only into air or a replaceable block, so each strand returned before placing anything: nothing in the game ever grew a `living_roots` block under a mallorn, a hardened mallorn, a mirkwood, a hardened mirkwood or an Old Forest oak, and 26.1's log order being a set's means the start was not reliably the lowest log either. `place` now takes the foot from `TreeFeature.getLowestTrunkOrRootOfTree`, turns one strand per bearing from it (the first turned by a random share), and steps out one column at a time along the ground, laying the root on the first earth within one block up or two down - soil, rock, sand, moss, snow, gravel - and stopping at water, an occupied top and any drop past two. One strand in five ends early. **What to look at:** a mallorn in Lothlorien and a mirkwood oak, for roots running out from the foot of the trunk over the grass rather than a bare base; the count of blocks per tree is not measured yet, and the strands are five blocks long at most, which is the decorator's own number. **Not seen in a world:** compiled only - no tree grown, no biome walked.

**A build's shoulder is a declaration from its own file now, and an underground build has one too.** `Drape.declare(level, structure, corner, size)` reads the template with its trees discounted and fills in the whole declaration - `DEFAULT_REACH` 12, a deck of nought, fill on, `RULES` 1 - and `Drape.dress(level, structure, corner, size)` reads and dresses in one call, so the ordinary build no longer writes a `Declaration` by hand. `Drape.enclose(level, declaration)` is the member for a build cut into rock: the surface ring's heightmap answer belongs to the sky there, so it leaves everything above the roof and below the floor alone and fills the ring between them with the country's own stone, read from the first solid block above the build's roof in that column - which is the trench a capture's air cuts and leaves. Sky or water over a roof leaves that column to `dress`, the build's own blocks are never replaced, and water in the ring stays. **Nothing calls either yet:** the camp, the crag and the village keep the passes they had, so both are compiled and unplaced. **Not seen in a world:** no world was made or dressed with `declare`, `enclose` or the one-call `dress`.

**The shore under the village's edge is a straight bank from the build's own edge to the water now, and the seam's 13-block-plus faces fall from 262 to 41 on the same ground.** The owner's 28 November look on `Drape Test - 3` had the east edge's fill still not meeting the seam, with firien and culumalda crowns standing at the land. `join` capped its fill at `water + to - 1` - a cone from the nearest water - so under an edge standing 28 over a pond two blocks out the line was 80 against a wall of 107 and the fill was refused: a 27-block face at the seam. The line is the straight bank from the wall's top to the water's surface over the wall's distance from it now (`water + round((wall - water) * (to - 1) / (to + away - 2))`, clamped to the water), which is the old cone wherever the drop and the distance are equal. One seed (480923) raised twice, `Seam Old` against `Seam New`: the first step out from the cut's edge is 0..2 blocks on 1,701 of 2,038 columns and 13 or more on 262 in `Seam Old`, 0..2 on 1,981 and 13 or more on 41 in `Seam New`; the drape line goes `21043 filled, 2780 left alone, 3863 of the river taken` to `21346 filled, 2452 left alone, 3868 taken`. `DRESS_VERSION` 17. **The 41 left are the build's own rim standing over water** - `(x,-2918) 112 -> 82..86` on the south edge, `(1417,z) 107 -> 79..82` along the pond's east edge - where meeting the seam means burying the water's edge; the country's trees at those feet are the same columns, so the crown count barely moves (lebethron 734 to 750, culumalda 247 to 248, oak 100 to 81) and no leaf in the collar has ground more than two blocks over its own column's ground (`build/tmp/crowndepth.py`). **The next decision is the owner's:** bank those feet over the water (the seam meets, the pond and the river's margin go under the fill), or keep the water and the face - the ring cannot do both. **Not seen at a screen:** no client; the worlds are `run/Seam Old` and `run/Seam New`.

**The shoulder is laid through the river now: the bed is the column's ground, the water is ignored when the blocks go in, and any water the fill's top stays under keeps its place over the ground.** The owner asked for the cone to run through the river at the feet on 28 November and for the surface it takes to be counted. `join` reads the bed instead of skipping a water column (`waterline` walks from the top to the first of a fluid and ground, so a lily or a stand of seagrass is looked through rather than read as dry land), fills from the bed to the cone's cap, and leaves a column whose line is under its bed at full depth; nothing above the bed is cleared any more, which is the second half of the fix - the old clear took 502 columns of the Shire's own water on `Cone 1`'s ground, 188 of them with no ground laid in their place. `River 2`, fresh headless world: `13 cut, 20935 filled, 0 left for the water, 2720 left alone, 3931 of the river's surface taken (669 west, 138 north, 3124 east, 0 south)`, up in 1,123 ticks (56.2 s). `wall.py`: the step at the line is 0 to 2 blocks on 91 of 117 west columns and 95 of 140 north, 13-block-plus faces 8 and 25, longest runs 7 and 10 (`Cone 1`: 93/117, 91/140, 6, 29, 3, 11); east and south have no wall. `leafgrass.py`: 605 on a leaf or timber, 73 in the rim band (44 on `Cone 1`). `DRESS_VERSION` 16, `UNDRESSED` 4. **The cost is 3,931 columns of surface water, most of it the pond's near shore on the east** - the fill stands over the sheet for `wall - water level` columns out; capping one below the surface keeps every water block and shallows the same band. **Not seen at a screen:** no client; the world is `run/saves/River 2`.

**The shoulder is a cone now: one block down for every one out, so its run is the height it has to lose.** The owner's 28 November look found the drape counting tree tops on the village's line, a two-block lip under the top of the tall faces, and a one-block notch where the bank stopped short. All three were `join`'s own arithmetic: a fixed reach put the first column outside the wall a twelfth of the drop down and held a five-block edge out for twelve columns as a plate. `cap` is `clamp(top, wall - run, wall + run)` with `run` the horizontal distance to the nearest edge plus `CutCommands.wander` a quarter block, so a 25-block edge falls out over 25 blocks and a 5-block one over 5; the mountain's ring keeps its ease. The edge is read with `standing`, which walks down through leaves and wood to the ground, where the two heightmaps answered with a crown or a trunk; and a one-block step is only skipped now where the eight neighbours are level with it (`sloped`), so a step inside a bank is laid. `SHOULDER_REACH` is 32 and `DRESS_VERSION` 15. `Cone 1`, fresh headless world: `13 cut, 20953 filled, 0 left for the water, 2036 left alone`, up in 1,051 ticks (52.6 s) over 1,326 chunks - 0.6% more than the twelve-block shoulder. `wall.py`: the step at the line is 0 to 2 blocks on 93 of 117 sampled west columns and 91 of 140 north (18 and 2 in `Bare 1`); 13-block-plus faces are 6 west and 29 north, longest runs 3 and 11 columns. `leafgrass.py`: 590 on a leaf or timber, 44 in the rim band. **What is left is the shore rule and geometry:** `867,-2712` has the country at 82 and the river three blocks out, so the fill to the village's 98 is refused and the run is three columns. **Not seen at a screen:** no client; the world is in `run/saves/Cone 1`. **The water is priced, not built:** a cone laid through water touches 1,594 water columns with 21,378 blocks - 352 keep water over the fill, 1,242 come out of it; capping one below the surface keeps every water block and shallows the edge instead. The river foot stays the art's own bank for now.

**The village's downhill edge is a shoulder now, and the wall is a slope.** The owner's fresh world on 27 November showed the other half of the seam: the village's own hill comes down to the footprint's line at 89 to 98 and the land outside it is at 78 to 86, so the join was a face of dirt and stone one column wide - 66 columns along the west edge, 138 along the north, nine to eleven blocks high at the line. Nothing inside the footprint can mend that and nothing may be laid outside it under the old rule, so the rule is the old one at a shorter reach: a village with its ring off runs the ring's own join at `SHOULDER_REACH` 12 instead of sixty, `Dressing.Job` reads its reach as "the ring's sixty or a shoulder's twelve", the ring's mountain is planted only when the reach is the ring's (the owner's rejection stands), and the join measures the edge as the ground the village actually stands at - the clear's own bank included, without leaves. `Shoulder 2`: `1,184 chunks of ground` (1,050 before), `16,122 filled up, 0 cut down, 3,028 left alone, 0 of the build carried under the mountain`, the village up in 1,045 ticks (52.3 s). Measured back with `build/tmp/wall.py`, the west edge's 13-block-plus faces fell from 23 of 59 sampled columns to 2, its longest unbroken wall from 66 columns to 2; the north's from 56 of 93 to 15 of 70. **What is left is geometry, not a miss:** where the art's own hill stands within two blocks of the river - `867,-2708`, 98 in `Bare 1` too - the shore rule refuses a fill that would stand twelve over the water, so the bank keeps its face there. `leafgrass.py` reads 576 grass blocks on a leaf or on timber, 48 in the rim band against 1,830 in `Drape Test - 1`, and the rest is the art's own. `UNDRESSED` is 3 so an older world re-raises. **Not seen at a screen:** no client was run; the world is copied to `run/saves/Shoulder 2` to walk. The terraced skirt and the revetment are still unordered.

**The dressing's grass no longer stands on trees.** The owner's fresh world on 27 November, `Drape Test - 1`, read well where the ground is close - the join smooth, the dithering right - and wrong on the trees: plates of grass sitting on the crowns along the village's edge. The save answered it in one probe (`build/tmp/leafgrass.py`): 2,409 grass blocks inside the footprint stand directly on a leaf or a length of timber, 1,830 of them within the clear's own seventeen-block rim band, none in the 226-block strip of country west of the box that no pass touched. The cause is the bank's cap: `rim` measures the country with `MOTION_BLOCKING_NO_LEAVES`, so where the country stands in a tree the height the bank keeps falls inside it, the sweep takes everything above that height and leaves the leaf under it, and the grass went on the leaf - `grass_block` 85, four `hardened_leaves_lebethron` at 86 to 89, `grass_block` 90 at 866, -2519. The cap is walked down through the tree to the ground and the crown inside the bank is filled with dirt now; a crown over air or water is left alone. The two inside passes got the same rule: `skirt` leaves a column with anything standing on its ground alone - inside the village the trees are the village's own - and `dressInside` refuses to lay its grass top on tree material. **Checked:** `compileJava` exit 0, `git diff --check` clean, no lone LF. **Not seen in a world:** no world has been raised with the fix. **The next question is the tall faces:** with the skirt capped at `SKIRT_RISE` six, the village's west face (nine blocks over the country) and north face (nineteen) are still left raw, and the options for them are set out below in the rim paragraph and in `docs/dressing-state.md`.

**The Shire's rim is graded, and the measurement says where the seam actually is.** The village's own edge is dressed inside its footprint now, with the ring still off: the clear keeps a bank of the country at the cut's box line where the country stands over the build (`RIM_FEATHER` 14, wandered 0..3, capped 12, grass on top), and the drape grades the country up to the foot of the build's own ground over four to seven blocks (`SKIRT_REACH` 4 plus the wander, edges of three to six blocks, the country's own skin over its own dirt). The inside pass now runs with no ring: `Standing.step` no longer stops before `dressStep`, the job's reach is 0 so its chunk ring is the footprint alone, and the mountain's burial of the build's outer columns is the ring's alone - it was firing on 518 columns with no mountain round it. `DRESS_VERSION` 14, `UNDRESSED` 2. `Seam 2`: `8885 columns of its own edge kept a bank of the country`, every drape count 0 - including `0 of the build's own edge banked up` - and the south rim 88..83 over eleven columns against a flat 83 in `DoOver - 1`, west 92 against 91, north and east unchanged. **The zero is the useful number:** the Shire's cut is fully grounded, so no country survives inside its footprint to grade, and the west and north faces (nine and nineteen blocks) are the village's own hill. On 27 November those options were put again and ranked: a **terraced skirt** first - raise `SKIRT_RISE` and lay the bank in four-block steps over 8..12 columns, keeping the contour and the wander - then a **revetment and lane of the country's own stone** for the nineteen-block north face, then cutting the build's own hill back, then a tree belt, and the short outward shoulder last, because an underground site can never give outward ground. The long version, with what each costs, is `docs/dressing-state.md`. **Not seen in a client:** no client was opened on `Seam 1` or `Seam 2`. The dressing's whole state, the six options as they were put, and how each is checked are in `docs/dressing-state.md`.

**The drape's fill now runs down into water as a shore, because the walls the owner photographed were the drape's own plinth.** The pass in `run/logs/2026-11-25-2.log.gz` reads `0 columns cut down, 37063 filled up, 2656 left for the water, 6703 left alone, 0 cleared of a tree standing on nothing, 9663 inside the walls cut down to the cut` and then `the village stands at 866, 70, -2917 in 726 ticks` - it ran to the end, so nothing was left half-done and the note in `tolkienmobs/cuts/hobbiton/_stands.txt` is this site. **What the squares and the cliffs actually are:** the drawing at the cliff the owner stood at (1423, 79, -2723) reads 78 to 81 across the bank with the river bed at 67 under water at 78, and the world there stands at 96 to 100 - so the twenty-block wall beside the river is fill, not country. The two square patches are the same thing at the ring's north edge: 57 by 32 columns of fill capped with grass at 86 to 88 standing over the drawing's pond at 79 (the coordinates the owner sent, 1333..1389 by -2449..-2418, are that band, which is the drape's own 32-block reach). The holes are the 6,703 columns left alone and the 2,656 water columns, not a drape that stopped. Nothing was cut anywhere, which is why no cliff had been smoothed: the ring had only ever built up. **What changed:** `Job` reads the water across the whole ring before it dresses a column, a two-sweep distance in `mapWater`, and a fill is capped at `water level + distance - 1`; a column whose own ground is already over that line is left, so the rule can lower a fill and never cut country beside water. `DRESS_VERSION` is 3. **What to look at when it re-dresses:** the pond's north bank and the river's east edge (1420, -2722) for a slope instead of a face, the leg at 848..865, -2762..-2738 (a channel at 78 to 79 with fill at 89 to 95 either side) for the same, and the 32-block band north of the footprint at -2449..-2418. **The leg's fate is the owner's call and still open:** if the banks read as shores the leg stays, and if not the plan is to shift the build west and plug it - though a shift alone does not cover water, because `inside()` skips every fluid block. **Not seen in a world:** the change is compiled only; no world has been raised or re-dressed with it, and the extra stage has not been timed.

**The ring's fill is ground now, and the walls that are left are the village's own hill.** The owner's fresh world (`run/saves/Hobbiton Drape`) re-dressed with the shore rule in it - `the water is mapped over the ring - 29378 columns of it`, `47 columns cut down, 24417 filled up, 2656 left for the water, 13035 left alone, 0 cleared of a tree standing on nothing, 0 inside the walls cut down to the cut` - and his screenshots show the holes gone and the banks softened, with two things still grey and square. **Both were the fill's own body and shape.** Read from the save: country `83-85 dirt, 86 grass_block`, then `87-96 stone` and the old grass cap at 97, so a fill was eleven blocks of bare rock under a green top; and the shoulder held 15 to 16 blocks of fill level for twelve blocks out before dropping one in two, because the join eased - a flat plate at the village's height with a cliff at its outer edge, which is the "little bit of raised terrain" he photographed and the squares before it. `dressColumn` now keeps `SOIL_BLOCKS` three of dirt under a soil cap with stone below, and the join is the plain share of the reach: one block down for every one or two out, no plate. `DRESS_VERSION` is 4. **What the ring cannot do:** the sheer walls left are the cut's own hill - at 876, -2813 it is `70-91 stone, 92-95 dirt, 96 grass_block` on the drawn bed's sea pickle at 69, and the ring meets it at 103 on the south edge - so smoothing them means cutting the build (the inside pass did not: `0 inside the walls cut down to the cut`) or moving the village, and a plug is the only thing that removes the leg's water because the drape leaves every fluid column alone. The ponds inside the footprint (water 79 on a bed of 70 at 866..884, -2599) are the build's own and a blanket plug would fill those too. **What to look at:** the apron south and east of the village (1440, -2935 for the shoulder, 1430, -2917 for the join at the hill), the pond at 875, -2600, and the leg at 866..884, -2813. **Not seen in a world:** compiled only - nothing re-dressed, no client.

## Pick up here - 26 November 2026

- **The Shire is re-based and the village has moved.** `ArdaMap.HOBBITON_X/Y/Z` are 866, 70, -2917 - footprint 866..1417 by -2917..-2449, the pond's north and east banks - with the village's water on 79 and its grass on 86, and `Hobbiton.autospawn` raises the cut there. `Pins.SOURCE` carries the gate at 1103, 81, -2529 and the hall at 1037, 84, -2479, and the portal arrives at `hobbitonGate()`, 1141, 86, -2683. Committed `52275f10`.
- **No road is planned anywhere.** `Roads.NETWORK`, `Roads.BUILDS` and `Roads.TOWNS` are empty, `NETWORK_OPEN` is gone, `ArdaMap.CHANNELS` is empty and `RoadPaint.LINES` was already empty. The fourteen surveyed roads and the valley's three are written down in the changelog of 25-26 November, not in the code. Committed `b6ce6d3b`.
- **The drape now runs its fill down into water as a shore.** The walls in the second set of photographs were the drape's own fill, twenty blocks of it beside the river; the water is read across the ring before it is dressed and no fill stands more than one block over its distance to it. `DRESS_VERSION` is 3, so a world that holds the village re-dresses on the next start. The paragraph above has the numbers and what to look at.
- **The ring's fill is dirt-banded ground on a straight slope now, `DRESS_VERSION` 4.** The grey plates and the shelf at the hill's foot were the fill's stone body and its eased join; the walls left are the village's own hill, which only a cut of the build or a move can smooth. The paragraph above has the columns and what to look at.
- **Every path that reads the network was walked against the empty table and none of them breaks** - the paragraph below has it path by path. No code changed for that, so a session finding this should not go looking for a bug: nothing waits, nothing throws, and no site is recorded as roaded when it is not.
- **The owner is generating the world.** What to look at first, in order: the cut raised at the pond, and the drape's edge where it meets the drawn water (13.1% of the footprint is drawn water, which the village replaces); the village's banks and ring; then the ground east from the new gate toward the Brandywine, whose only crossing is at 1230, -3490.
- **If the pond site reads wrong, the fallback is already measured.** The only wholly dry 551x468 box near it is corner 880, -2592 - 0.00% water, ground median 85, eleven blocks of relief, 325 south of the point the owner marked. Taking it moves the corner and the floor together, and both pins follow by the same offsets.
- **Roads go back one line at a time.** A `new Named(id, kind, town, at(x0, z0), at(x1, z1))` in `Roads.NETWORK` is a whole road - `RoadPlan` works out the way, the bends, the water and the span from the two ends - and `/tmroad plan` / `/tmroad build` do the same at a hand for a leg that is not in the code yet. A road out of a city also wants its id in `BUILDS`; a road that ends on a town wants a `Gate` in `TOWNS`; both tables are empty with the network. `BRIDGES` still holds Bucklebury's pair (a block on the water at 438, -49) and the Last Bridge with no block on its water, because a bridge is found by the water and not by a road.
- **Do not commit:** `tools/sampleprobe/.../SampleProbe.java` (modified, waiting from before) and `logs/` (untracked). `build/tmp/*.bin` and the `*_places.py` there are the probes these numbers come from - uncommitted and regenerable with `./gradlew -I tools/sampleprobe.gradle ardaSample -PprobeArgs="map x0 z0 x1 z1 step out.bin" -x createMinecraftArtifacts -q`.

**The road network is cleared: `Roads.NETWORK` is empty and every road goes back in a line at a time.** Fourteen pinned ends stood there - Gundabad's road, the old forest road, the four high-pass legs, Rivendell's three, Elladan's leg, the shoulder leg, and the owner's three Bree-to-Shire stretches - and none of them survives the re-base: the Shire's village has moved 2,400 blocks south-east to the pond and Bree and Rivendell are being re-scouted, so a road whose end moves is a different road with a different id. `Roads.BUILDS` and `Roads.TOWNS` are emptied with it (they are keyed by road id), `NETWORK_OPEN` is gone so a road added back plans without a switch to find, and `network()` plans whatever the table holds. Nothing else about roads changed: `RoadPlan`, the `/tmroad` pass, `RoadPaint`'s generated lines (empty already), `RoadCorridor` and `SignPosts` are as they were, and a save's own plan store (`RoadPlans`) still restores the roads that save planned - `/tmroad forget` on the line drops the plan and leaves the blocks. **The order to rebuild them in:** the owner's own walks first, since a walked leg is a surveyed one - Bree to the new village gate, then the village's road on to Michel Delving and Bucklebury once those exist - and the vale's three when Rivendell's site is settled. **Not seen in a world:** compiled only; no world made, loaded or walked, and no road lifted.

**Nothing in the code assumes a road exists: the empty table was walked against every path that reads it, and no code changed for it.** `Roads.forGround` answers null and all three callers take it: `Dressing.start` makes the job's `roadDown` true the moment there is no route (`route == null || RoadCommands.down`), so the dressing finishes rather than waiting; `Dressing.Job.apply` sets its own `road` flag and logs "no road is walked for this build, so the ground is all it gets"; `Sites.road` returns, leaving the site's road flag clear on purpose so the day a route is walked the world lays it. `Roads.waits` and `Roads.hold` answer false and null, because `BUILDS` and `TOWNS` are empty and `gatesOf` returns nothing - no build waits for a road. `RoadCorridor.start` (chunk load) and `look` (once a second) loop over `Roads.ROUTES` and begin no pass and no span. `Roads.nearest` null is tested at both of its `/tmroad` call sites, which say "No build has a road walked for it yet" and "No road has been planned or walked here" and return 0; `/tmroad plan` and `/tmroad build` still plan and lay a leg named at a hand. `SignPosts.lines` returns nothing and `placeAll` stops on the empty list, so `signposts place` reports "0 planted on 0 laid roads here"; `RoadCommands.onServerStarted` logs "Roads: 0 sections in hand, 0 read back from the save", the line that exists to tell a world that lays no road from one whose roads are missing. `Roads.water` and `Roads.bridge` are only ever asked of a span that already exists, and the client `RoadHud` is the page needle, not a road. **One thing was checked closely rather than changed:** a city dressed with no route still writes `road=1` into its `_dressed.txt`, and that bit decides nothing - `Dressing.start` recomputes `roadDown` from the route's own note each time it looks, so a route added later (a rebuilt table, hence a restart) reopens the job, and a route laid by hand in a session is begun by the command itself. `docs/arda-roads.md` needed no change for it either. **Not seen in a world: read from the code, with nothing generated, loaded or walked, and `compileJava` was not rerun because no file changed.**

**Hobbiton raises itself at the pond: corner 866, 70, -2917, water on 79, grass on 86, and no branch.** `ArdaMap.HOBBITON_X/Y/Z` moved on 26 November 2026 and `Hobbiton.autospawn` lays the cut where `hobbitonPlan()` answers, so those three numbers are the move - a fresh world raises the village on the pond's north and east banks at start or on the approach, and a world that still holds the old village at -700, -512 keeps it and raises this one too, because `stands` keys its note on the corner. The footprint 866..1417 by -2917..-2449 is 13.1% drawn water (8,461 of 64,350 sampled columns) with dry ground 79, 82 and 87 at the tenth centile, median and ninetieth; the only wholly dry box of that size nearby is 325 blocks south at 880, -2592, and it is not the pond. The floor is 70 so the cut's water (79) is the drawn water's level at the footprint's edge (77..79 inside, 78..79 just outside the east edge) and its grass is 86, four over the country's median; a floor on the median would put the village's water three under the water outside its walls, which is the fault the old site was lowered seven for. The 770-block branch west, its grade of 90 and its reach of 64 are deleted with it and `CHANNELS` is empty: nothing is held down or graded at the pond. The pins moved by the cut's own offsets - `hobbiton` gate 1103, 81, -2529 (corner +237, +11, +388) and the hall 1037, 84, -2479 (+171, +14, +438) - and the portal's `hobbitonGate()` arrival follows on its own at 1141, 86, -2683. **What to look at first:** the cut raised at the pond, the drape's edge where it meets the drawn water inside the footprint, and the pond's banks outside it. **Not seen in a world:** compiled only - no cut placed, nothing generated, loaded or walked, and the two pins are carried over from the old village's offsets rather than read off a stand in the new one.

**The Shire's three towns are pinned: Hobbiton 866, 82, -2917, Bucklebury 1380, 91, -3432, Michel Delving -40, 104, -2740.** The owner fixed the three sites on 26 November 2026, reading them off the re-based drawing, and `Pins.SOURCE` carries them so they cannot be lost while the builds are worked out. **Hobbiton** is the west bank of the pond he marked - ground 81, dry, the hill behind it to the north-west and the Water to the south-west - and its pin moves off -463, 44, -124, the east gate of the village whose cut still stands at -700, -512, because the Shire's ground moved under the village when the drawing was re-based; the name moves first and the village's own corner (`HOBBITON_X`/`Z`) follows when the plan is re-sited, and a world that already holds the old village keeps its own saved pin. **Bucklebury** is the first ground east of the only crossing: 150 east and 60 south of the Brandywine bridge at 1230, -3490, ground 90, dry from the bridge's east foot and dry the whole 1,076 blocks on to Bree - the 36-block channel under the span is the only water on the Hobbiton to Bree route, which is the map's own order kept (Hobbiton west, one crossing, Bucklebury east, Bree furthest east). The reference sheet's label at pixel 795, 740 is this place - 1,476, -3,031 in blocks - and not Hobbiton, which the sheet does not label at all; the table in `build/tmp/canon_places.py` is corrected with it. **Michel Delving** is the flat shelf 906 blocks due west of Hobbiton, ground 103: four blocks of relief across a hundred, eight across two hundred, the land climbing away west and north with a shallow vale to the south-east, and a dry line from Hobbiton that never steps more than one block in a column, so the road west would grade without a cut. Its old neighbour further east, around 420, -2520, is the worse ground of the two - a stream cuts a gully 36 columns wide and the ground dips to 69 between Hobbiton and there. Neither new town has a cut, a template or a catalogue entry, so no key places anything yet: they are the ground written down for later: neither is in `Places.CATALOGUE`, so `/tmpin place` refuses them, and `/tmtp` finds them as a set point until a build or a catalogue entry gives them a town. **No worldgen was touched** - the drawing, its masks and every build's own corner are where they were. **Not seen in a world:** compiled only, no world was made, loaded or walked, and the three columns are `ardaSample`'s heights off the drawing rather than a stand taken in game.

**The frame question is settled: the drawing was made on the committed frame, and 1:1 would move the sea over the Shire.** The owner's map was hand-made at one pixel per block over a 24576-pixel square, which is three blocks per 8192-pixel and not the committed 3.954 and 4.018, so both placements were put against his own drawing. The three inland seas land 13, 65 and 104 blocks from their drawn rectangles at the committed frame and 661 to 4,132 blocks out at 1:1, where no single origin fits them; the art's coast is 950 to 2,810 blocks east of the drawn coast at the committed frame and 3,300 to 4,000 east at 1:1, inside the Shire's own columns from z -512 south and east of the world origin from z 800 south. The large rasters are exactly three times the committed ones (IoU 1.000 at a zero-pixel shift), so they add resolution and cannot move the frame; the height has no original, and `Reference/Final Height-large.png` is a nearest-neighbour enlargement. `docs/arda-masks.md` carries the measurements and the cost of a rebuild. Nothing was changed: no code, no asset and no chunk - **not seen in a world**, because nothing was generated. What remains open is what the last paragraph of the sea's entry says: the water is where the art paints it, 950 to 2,810 blocks east of the drawn cells, and making it match the cells is a re-cut of the art (or of the drawing), not a frame change.

**The sea shoals into its coasts, and the shallows are the shallow sea.** The owner's fresh world at -948, 97, 418
(grass at 25 at his feet, `minecraft:deep_ocean` on the water eight blocks west) asked whether the map reads its
distances right and why the coast is a hard edge, and the answer is that the coast sits where the drawing puts it
while the bed is what made it read as a wall. The masks are read block for block through the committed
calibration: at z 418 the raster's zero runs from -1060 to -940 and `ardaSample` reads those columns
`TEMPERATE_SEA` at 0 to 3 - the reference's own bight, 930 blocks from the Shire's cell centre - so no scale or
offset moved and the Shire is untouched (55 at -700, -512; 65 at 0, 0). What was wrong is the bed: every drawn sea
stood on the sea country's own 0 to 3, twenty-two to twenty-five blocks under the water right up to the sand, and
every sea column was `minecraft:deep_ocean`. `ArdaMasks.coastDistance` measures a sea column's distance to the
drawing's nearest land and `ArdaMap.shelf` raises the bed toward it - two blocks of water at the coast, one more per
eight blocks of run, to twenty-one, inside 176 blocks - so the bight on z 418 is now two to eight deep and the
Brandywine's estuary shelves from four to sixteen where it was twenty-three to twenty-six; `ArdaMap.seaBiome` puts
water twelve deep or less in `minecraft:ocean` (`minecraft:frozen_ocean` in the north) and the biome list grew its
69th line by `runData`. Open sea past the reach and every land column are unchanged. **Not seen in a world:**
compiled and sampled, no chunk generated and no client - the old bed stays in the Shire's bay until that ground is
made again, and the shallows' colour is still to be looked at. **If the owner wants the water physically farther
from Hobbiton,** the next thing is not a worldgen rule but the masks' Eriador coast: the hand-drawn cells put it
about 1,560 blocks west of where the reference paints it, and moving it is a re-cut of `height.png` (and the
Rivers/Lakes masks with it), not a shelf.

**The tools read the ground the world stands at, raggedness and all.** `tools/arda_ground.py` is
`ArdaMap.groundHeight` in Python - the baked masks the game itself reads, its Catmull-Rom spline between
the height pixels, its bilinear cover of the mountain mask, the mask's climb of sixty-four and `ragged`'s
two folded octaves - and `mask_probe.py` and `arda_scout.py` both answer heights through it now. Two things
only a check against the game finds: Java's `getRGB` hands back grey pixels sRGB-encoded, so 202 in the
file reads 230 to the game and is worth six blocks of climb, and `ragged`'s hash wraps its 64-bit products,
which the port repeats with numpy's own wrap. Checked against `ardaSample`: the 81 Hithaeglir columns that
read a mean 12 low are 81 of 81 exact, the flank at 6518, -6461 at 266 and the summit at 303; a 56-column
grid across the Trollshaws is 54 exact, the two off by 12 at 5000, -3000 and 5000, -2800 where the game
grades the Bruinen's bank down and the tools do not. **Open: the bank grading, the vale under the city,
Bree's hill and the Gundabad pass are still not modelled**, and a drawn sea still answers the waterline
rather than its bed. **Not seen in a world: compared with the game's sampling task, not in a client.**

**`tools/arda_scout.py` reads the drawing at the game's own ends too.** Its relief reports were out at both ends - sea 63, crown 318, no mask lift - so a hill stood at 174 where the game says 138 and a summit at 258 where the game says 303. It is black 0 to white 318 plus the mask's own 64 now, in the heights it reports and in the shade it draws from them. Checked against `ardaSample`: 121/121 at 4608, -3072, 138/138 at 4352, -3840, 307 against 303 at the summit, 117 against 115 at the ford. Across 81 columns of mountainside it read 12 blocks under the game until `ragged` was ported, which is done below. **Not seen in a world: compared with the game's sampling task, not in a client.**

**`tools/mask_probe.py` reads the drawing at the game's own ends again.** It still had the pre-re-base pair (sea 63, crown 318, every sample blended up from sixty-three), so its heights ran about fifty blocks high - 114 at 455, 192 where the game answers 64, 258 at the brightest summit where the game answers 303. It is black 0 to white 318 now, plus the mountain mask's share of 64, and a column under the waterline of 25 says so instead of being called sea. Checked against `ardaSample`: 64/64 at 400, 192; 73/73 at 400, 792; 58/58 at 400, 1392; 46 against 47 at -200, 192; 307 (243 + 64) against 303 at 6878, -6509 - the few blocks being `ragged`, which the probe models now (below). `docs/arda-masks.md`'s probe paragraph is corrected with it. `tools/arda_scout.py` carried the same pair, and is corrected with it. **Not seen in a world: compared with the game's sampling task, not in a client.**

**The Brandywine has its own name now, at the bake and in the world.** The river mask fuses the Lune and the Brandywine into one component - the Lune's arm to the Gulf at -776, 64 and the Baranduin's loop from Evendim through the Shire to the coast at -215, 1812, meeting near 400, -700 - so the component's middle named both `lune`, and every Brandywine column read `river_lune`. `tools/arda_river_names.py` now divides such a component by water distance from points along each river's own course: the Lune keeps 2,338 cells (x -1024..368 by z -792..528) and the Brandywine 10,647 (x -264..1864 by z -4840..1864) as id 5, answered by the new `TolkienBiomes.RIVER_BRANDYWINE` (0x2E6F72 water, temperature 0.5 so it is tagged temperate where the Lune is cold), with the biome in the source's overlays, the water-bed and sand rules, the lang, the dimension list and the two tags. **Already-generated ground does not change:** the name is read only while a chunk is generated, so `Generation Redux - 3` keeps `river_lune` over the whole Shire stretch, Sarn Ford and the 274 generated chunks of the new reach down to z 1,247 - the Lune's name and colour until those chunks are made again, and `river_brandywine` when they are. The 294 reach chunks south of z 1,248 take it on first generation. **Not seen in a world: compiled only**, and the one thing the files cannot settle is whether a new registry entry moves the numeric ids a saved chunk carries - the first load should show `tolkienmobs:shire` over the Shire and `tolkienmobs:river_lune` over its Brandywine water.

**The Brandywine is drawn to the sea now, and no world has that reach south of the Shire.** The reference river layer's line stopped at z 192, so the water at 455, 195 ends against a dry bank; the canon map carries the Baranduin on past Sarn Ford and south-west through Minhiriath to the coast north of the Greyflood's mouth. That reach is drawn on `ME_SimpleTopoTerrain_Rivers_albedo.png` from the old stroke's taper at 456, 160 through 415, 600 to a mouth at -215, 1812 - 1,780 blocks, opening from 50 to a 100-block estuary - and `rivers.png` gains 1,969 water cells at x -264..480 by z 144..1864 and loses none. Only those cells were written: the committed masks are stale against today's references (a fresh bake of the pre-extension reference differs from HEAD in 43,329 river cells and 2.87M height pixels), so a full `tools/arda_masks.py` run would drag unrelated ground along, and `lakes.png`, `mountains.png`, `height.png` and `bruinen.png` were put back. `river-names.png` was not rebaked either, because the extension lands in the component that already carries the Lune (its centre moves from 234 to 755 blocks from `lune`'s point, still inside `REACH`), so the new water answers 0 and the generic river biome while the old reach still reads `river_lune` - giving the Brandywine an id of its own is the open job. **To see it in `Generation Redux - 3`:** 274 of the 568 chunks the new water touches are already generated - the whole reach down to z 1,247, in `r.0.0`/`r.0.1`/`r.0.2` - and they keep their dry ground; the 294 south of z 1,248 are ungenerated and will draw the river on first visit. With the twelve-block banks and the twenty-four-block level read, 1,206 chunks are in scope, 622 of them already made. **Not seen in a world: drawn and baked only; no chunk has been generated from it.**
**The drawn sea is water again, and the Brandywine's mouth opens into it.** The owner's screenshot of the estuary at -213, 1801 showed the river ending on grass under a thirty-six block wall of country, and his logout position in `Generation Redux - 3` (-1474.35, 35.85, 3005.49, read from `players/data/380df991-...dat`) put the sea he meant 1,500 blocks away: that was the coarse country grid's sea, while the drawing's own sea is at the river's mouth. `ArdaMap.groundAt` had been building every raster-sea column from the half-kilometre grid cell it fell in, and `readDrawn` held a land country's ground at the waterline, so the bay Cardolan paints over stood dry at 60 to 73 (61 at -360, 1801 against 25 at -296, 1801). Such a column is now the sea's own country - its bed read from that country's height and relief, under 25, and no land clamp - so -360/-320/-304 at 1801 read 2/-1/-5 `TEMPERATE_SEA`, and the Shire is unchanged (65 at 0, 0; 55 at -700, -512; 84 at 1024, -256). `shore` now uses the height the column stands at rather than the pre-clamp one, which is why the coast had no sand, and `ArdaMasks.nearSea` counts the raster's zero within three pixels, so -296..-280 at z 1801 is a 24-32 block beach. The mouth's `MOUTHS` line in `tools/arda_masks.py` is carried two cells into the sea to (-280, 1864), because the channel's inset ate the stamped tip and left a dry four-block lip; 51 cells of `rivers.png` and the same of `river-names.png` (id 5) are re-stamped, and the sample now reads river to -254 with sea at -266 on z 1864. **To see it:** the mouth's water lay in x -304..-192 by z 1840..1880 (`r.-1.3`), and the coast strip in x -304..-192 by z 1792..1839; both are taken out of `Generation Redux - 3` now (24 + 24 chunks, backup `20261125-133825-before-brandywine-coast`, every remaining chunk read back unchanged), so they regenerate on the next visit. The bay west of x -304 is untouched: the save's own surface stands 42 at x -306, 57 at -320 and 55..72 from -336 to -352 (chunks -20..-22), so the grey face in the screenshot is that shelf, and a 17..47-block wall stands at the strip's new west edge until those chunks are made again. **Not seen in a world: compiled and sampled off the game, no chunk generated and the save not loaded.**

**Last touched:** 25 November 2026 - **The terrain re-base is signed off in a world.** A fresh world, made after fully quitting the client, came up with the rivers and the drape as they were, the Shire's party tree standing, and no plinth of the country left under a drawn crown in the fields; the owner's report is "perfection, issues cleared". Nothing under the terrain is waiting on a fix now, and what that world did not look at is said where it belongs: caves and the lava line under the new noise settings, Bree's join and Bree-hill, the camp pins, and the Shire's four edge columns with a drawn channel in them. A world raised before these fixes keeps its faults, because each runs when a build is raised or dressed and nothing repairs a world already raised. **Rivendell is off and the surveyed roads are paused while the site is re-scouted.** The city's massif is not laid (`ArdaMap.PLANTED = false`), the assembly that generated it at 5200, 75, -3370 is out of the table with its two generated files deleted, and both pins (`rivendell` and `rivendell.stone`) are gone, so no new world raises it there; `Roads.NETWORK_OPEN = false` keeps every surveyed leg out of the plan while the table itself is untouched. The cut stays in the jar - 584 pieces, 554 by 512, **244 bands from y 75 to a crown of 318** - and the new pin goes on the block the lowest band stands on, the city occupying that y to y + 243. A world that already holds the city keeps it; the old site's ground is the drawing again in any world made from here. The city's own passes are off with it: the staging plaza (`Plaza`, 5117, -3409) is drawn only while `ArdaMap.planted()` is true, `CityCommands.rouse` refuses a pin a world still holds, and the `rivendell.stone` stand is empty - so a saved world's old pin puts neither a disc, a cut nor a stone into the vale. Chunks already generated keep what the old massif made there. The vale also no longer reaches the water: `ArdaMap.vale` (which every drawn channel's level is read through) and the dale run - a 2,000-block follow of the Bruinen from the ford that held it at the city's floor 74 and graded 256 blocks of bank to it - are now off with the city, so no river is dropped into a canyon and no ground is terraced down in a circle around it.

**The road the owner walked from Bree to the Shire is the network's record now, and its Brandywine crossing is the Bucklebury bridge's.** He built the road himself on 25 November 2026 in `Generation Redux - 3`, which planned it as three stretches - `planned_1031_-34_480_-6` (Bree's hall front at 1031, -34 to 480, -6, at the town's bands, recorded laid with 587 stations of dirt path), `planned_480_-6_381_-5` (dry to 472, -49, the span from 456 to 420 on z -49 at deck 65, dry again to 381, -5) and `planned_381_-5_-149_-382` (bending at 133, 60, -182 and arriving on the village's east side) - so the whole leg runs 1031, -34 to -149, -382. `Roads.NETWORK` holds all three in the order he walked them, with the kinds he planned them at, and `Roads.TOWNS` names Bree's hall front under the first and the village gate under the last, so the hold is measured from those blocks again; the middle stretch meets no town. **Nothing is laid by the record yet:** `NETWORK_OPEN` is off, so his world keeps the road he built and a fresh world plans these three stretches only when the survey is reopened. **The crossing** is the section the store calls `_bridge_1`, and `Roads.BRIDGES` now names `Bucklebury` at 438, -49 - between banks 456 and 420 - with `bucklebury_end`/`bucklebury_middle` and `cobblestone_wall` as the support. The standard pair is what stands there today (oak planks at 65, water at 60 to 62), and naming the bridge does not lift it: the span is re-stood by `/tmroad build` with the caller at the crossing, which sweeps the five standard woods and stands the stone pair. The pair is 17 + 20 + 17 columns and this crossing is thirty-seven, so the plan lays both ends and one middle and seventeen of that middle's columns stand inside the far abutment - the deck row is the same either way, so it stands, but it is not the fit the Brandywine's fifty-four columns had. **Not seen in a world: compiled only, and the standing oak bridge has not been re-stood.**

**The Last Bridge's pieces are in the table, and the crossing is the one thing missing.** The owner drew and added the pair on 25 November 2026 - `last_end` and `last_middle` in `structure/bridges` - and the table now holds `Last Bridge` with them and with `wall_cobbled_dwarven_stone` as its support, because the piers are cobbled dwarven stone under a `wall_dwarven_stone_bricks` parapet rather than the stone bricks the older note guessed. The abutment is seventeen long and twelve high on its own bank, the span twenty, both nine columns across, and the deck sits on the same rows as Bucklebury's - the end's row 1 over the middles' row 2 - so `bridgePlan` stands it exactly as it stands that pair. **What is not named is the crossing:** the entry's block is null, `Roads.bridge` skips it, and every span still stands the standard pair until a block goes in on the water the owner's own road crosses. That is one block in one line. The table is read when a span first asks for it rather than at class load, since the support is a block this mod registers. The captures were cleaned with `tools/bridge_pieces.py` before they were shipped (1346 air cells out of the end, 610 out of the middle), because a listed air cell is placed and would clear the box the piece stands in. The reserved names the notes had used, `last_bridge_end`/`last_bridge_middle`, are not the files' names - a piece is named by its leaf name - so the table uses `last_end`/`last_middle`. **Not seen in a world: compiled only, and nothing has been stood from either piece.**

**The road between the Shire and Bree and the road between Bree and Rivendell are out of the mod.** At the owner's word on 25 November 2026 - he is laying that road himself as the next build - `Roads.NETWORK` no longer holds `planned_1032_-34_-150_-382` or `planned_4648_-3316_1031_-36`, and `Roads.TOWNS`, which named their two gates, is empty. The ends are recorded beside the table rather than left behind `NETWORK_OPEN`'s flag, so opening the survey again after Rivendell's site is re-scouted does not put either line back on the ground he is building over; a leg walked later is a new entry under its new pair. **Not seen in a world, and nothing to see:** the signed-off world's arda region holds no road on either line - the points probed read grass and drawn trees, and the Bree end of the line is not generated - and its `road_plans` store is empty. A world that has already laid either road keeps those blocks, since nothing in the mod lifts a road: `/tmroad forget` standing on the line drops its plan so it is not laid again, and the four reserved bridge pairs (`bucklebury_*`, `last_*`) stay in the jar for the owner's own road.

**The clear no longer leaves the country standing under a tree.** The newest world (`Generation - Redux 2`) has a plinth of the country under every drawn crown in the fields: at -260, -216 the cut draws rock to 41, dirt to 46, a mushroom block on 47 and one birch leaf at 70, and the world keeps all of that and then the country's stone 48..63, dirt 64..67, grass 68, short grass 69 and a birch crown - twenty-two blocks of hill from the drawn ground to the canopy. `CutCommands.standsOnOwnGround` kept the column because the tree's ground was at or under the cut's own top and a drawn tree's top is a canopy; it now also wants the block the tree stands on to be one the cut places at or under `Shape.foot`, the top of the cut's own ground without its trees. 837 columns in 72 clusters fail the new test and are cleared; 11,309 tree-top columns still pass. **Seen in a world on 25 November 2026:** a fresh world came up with the fields clear - no country plinth under a drawn crown - which is what the owner signed off. A world already raised keeps its plinths, because the clear is part of the raise.

**The first world that showed the plinths was raised by the classes from before the fix, which is why it showed them.** The client session was started at 15:29:36 on 24 November and stayed up through three world starts (15:30:12, 16:24:06, 16:30:57); `Dressing` was edited and compiled at 15:56:20 and committed at 15:57 as `b8ac882a`, and a class is loaded once per process - this process had already loaded the old `Dressing` when it dressed the Shire at 15:32. The world it left behind (`Generation - Redux`, saved 16:36) shows exactly the old behaviour and no more: at -545, -128 the `hobbiton_3_0_8` piece draws dirt to 46 with an oak trunk on 47..51 and the world holds the dirt and nothing over it, and the river at -714, -192 has its water cut and a bank laid over it. Both are what the commit was for. **Fully quit the client, then make a fresh world** is still the rule - the marker in an existing world says the village is dressed, so it will not be re-dressed however the code changes - and the restart that followed this paragraph is what put the fixes in front of a world.

**The ring's spring exception is gone.** The water guard exempted a spring, and the river beside the footprint's edge is read as exactly that: `dressColumn` clamps a ring column to the nearest wall column, the drawn river is that column's top, so `channel` is true and `watered` stands ten blocks over the village floor - past `OUTLET_LIFT`, so not the outflow - and the column was dressed as a spring, cut to the pool and capped with the ground under it. A column that is itself water is now skipped whatever `spring` says; the build's own pool is inside the walls and never cut as ring. **Seen in a world on 25 November 2026:** the drawn channel along the footprint's edge is left as the drawing has it, with the fields beside it dry.

**The party tree is the build's, and the inside cut no longer takes it for the country.** The newest save is plain about it: at -514, -100 the `hobbiton_3_0_8` piece draws dirt to 44 with an oak trunk on 45..48 and a leaf at 49, and the world holds the dirt at 44 and nothing over it, for the party tree and every other drawn tree in the footprint - sawn-off poles with the lanterns and cobwebs hanging where the crowns had been. The clear is not the fault (its shape is the cut's own blocks, a drawn tree is in it, and `standsOnOwnGround` leaves such a tree whole); the drape's inside cut is. It reads the outline the ring reads, and that outline leaves trees out on purpose so the ring does not stand a wall of stone beside every tree, so a party-tree column read as the grass at its foot and `dressInside` cut the crown as country standing five blocks over the village. `Dressing.Job` now keeps `whole`, the same outline taken with the trees in and filled by the same `holes` pass, and the inside cut asks that one. **Seen in a world on 25 November 2026:** the party tree is intact in a fresh world. A world that already lost its trees keeps them lost - nothing plants them back, and `DRESS_VERSION` is still 2 because re-raising the village would lay and clear the cut over whatever a hand has done since.

**The drape no longer cuts the river it crosses.** The river at the Shire's north-west carries a stone-walled trench through the water where the ring met it, three blocks deep: `Dressing.dressColumn` read a river column through a height that counts fluids, so the water's own surface was its `surface`, the ground search walked to the bed, and the column was cut and filled against the nearest wall's height like land. A column with water as its top, or in the block over its top, is now left exactly as it stands (the rule `Drape` keeps with `wet`), a spring the build holds up excepted. **Seen in a world on 25 November 2026:** the rivers are back to normal, the ring meeting the water instead of cutting a trench through it. A save that already holds the trench keeps it in its blocks.

**Arda has its own noise settings, and their sea level is the drawing's.** The water lying over the Shire was the vanilla aquifer's water table - the overworld's sixty-three - left in the noise's voids under ground that stands at forty to sixty-two; the reshape and the seal plug it after the fact, and they stay, but the table itself is the drawing's now. `TolkienDimensions.bootstrapNoise` registers `tolkienmobs:arda` from `NoiseGeneratorSettings.overworld(context, false, false)` - the vanilla factory, so the router, the noise, the stone and water, the surface rule, the spawn target and the aquifer/ore-vein/legacy switches are the overworld's - rebuilt with `ArdaMap.SEA_LEVEL` 25, and the level stem reads it (`"settings": "tolkienmobs:arda"`). Datagen writes it out (`worldgen/noise_settings/arda.json`, 111,529 bytes, `sea_level` 25); reading the overworld's record at run time is what this avoids - it cannot be read while the patch is being built, which the first attempt found by throwing out of `RegistrySetBuilder$RegistryStub.apply`. **Caves and lava:** a carved cave in the drawing's land is air now instead of water up to sixty-three, and carved water appears only below twenty-five, under the sea; lava is unchanged, its status still at y -54, so what a carver opens below -54 still fills with lava, and the drawn ground never reaches that low. **Seen in a world on 25 November 2026:** the fresh world runs on Arda's settings - the fields carry no sheet of water and water stands only where the drawing puts it. **Still unlooked at:** no cave and no lava pocket has been opened below the drawing's ground, where a carving is now air rather than the old water up to sixty-three. A save made before this keeps the overworld's settings in its `level.dat`, and the plugs are what keep that one dry.

**The water on the Shire's fields was the noise's aquifer, and it is rock now.** The newest save reads it plainly: in the box x -1200..200 by z -900..-200, 304,863 of 442,112 columns carry water where the drawing has land, and inside that box 81,724 columns had water as their top block and 36,012 more had it under their ground, with the water tops piled on 44 to 62 - 54,593 of them at exactly 62, one under the overworld's sea level. Arda borrows `minecraft:overworld`'s noise settings, so the aquifer fills every void under y 63, and `reshape` filled only air with stone under the drawn top: a column whose top was aquifer water kept it, because water is not air, and the surface rules cannot dress water - so the drawn ground arrived as a sheet terraced at its own contours, every field a shallow pond. The fill now writes stone over fluids as well as air, and the map lays its own water on top as before. `seal` also lays the drawn top back after the carvers, down to the first rock under it (up to `SEAL_DEPTH`, read through `topAt` so a road cut is not given a lid), because the carvers cut after the ground was drawn and the aquifer refills what they take out with the same sixty-three; and the plug under a channel's bed fills fluid as well as air, so a cave under a river cannot drain it. **Seen in a world on 25 November 2026:** the fields are dry. Chunks already in an older save keep their water. Caves under the drawn ground are still filled to sixty-three where a carver opened them; that is underground water rather than the visible sheet, and if it is wanted gone the water table itself has to be Arda's 25 rather than the overworld's.

**The Shire's village stands seven lower, and its water seven lower with it.** The cut was planted on the drawing's own median - 56 over a footprint that reads 36 to 71 - but the world walked from that drawing has its ground 47 to 51 round the village, so the cut stood seven over the country it was cut into, and the village's own water at 49 stood two over the ground at the edge of the cut, where nothing held it and it ran out over the fields. `HOBBITON_Y` 33 puts the grass line on 49 and the village's water on 42, five under the fields. The village's own marks went down by the same seven - the milestone 47, the crossing the portal arrives at 43, `hobbiton` 44 and `hall:hobbiton` 47 - because they are columns of the cut and not heights on the country, and the village's drape and its folk read the pin too, so they move with it. `Marks.VISITED`'s Shire point is left at 67: it is the drawing's ground at its own column, one over, and that column is the footprint's edge, where the country shows. The branch's fall is `HOBBITON_GRADE` 90, not 40 - leaving at 42 it reaches the drawn channel's own 25 at the fork, where forty put it nine under the river it joins. **Seen in a world on 25 November 2026:** the village stands on its country at the lower height and the fields beside the drawing's own channels are dry. Only the columns the drawing paints as channel carry water.

**The builds sit on the re-based drawing, and none of them had to be scouted to get there.** The drawing is the ground now, so `tools/sampleprobe` answers a site without a world: the Shire plants at 40 instead of 87 (site median 56 over 1,050 columns, so its grass line stands on 56), Bree at 71 instead of 117 (median 76 over 1,376, grass line 76), and their own marks came down with them - milestone 54, crossing 50, `hobbiton` 51, `hall:hobbiton` 54, `bree` and `hall:bree` 77 - while the village's carried branch follows `HOBBITON_Y` by itself. The Carrock's pin is 156 instead of 187, one course over the drawn channel whose water reads 154 and 155 across its box; the goblin door is 146 and the shelter camp's corner 143; Gundabad is unchanged (gate 187 against 186 to 193 along the facade, pass mouth 175 against 176). The campaign's camp pins were re-read too, since a pin above its ground is a keeper who never stands and a `/tmtp` that lands inside the country - Thorenhad 119, Barachen's fire 120, the ford 118, Elladan's camp 117, the ruin 121, Matuna's crag 122, the three crags 112/116/115, Gloin's camp 181 with its ends at 180. Rivendell's departure pins are untouched: the city and its plaza are off, so there is no plaza there to stand on. **Seen in a world on 25 November 2026:** the Shire raises at its re-based height and stands on its country - that is the world signed off above. **Still unlooked at:** no town, camp or rock has been walked at its re-based pin. Two sites are worth looking at first: Bree carries a 140-block knob inside its footprint that the drape will cut to the roofline, and four columns of the Shire's footprint have a drawn channel in them at its edge.

**The marks a jump uses were re-read too, and the numbers that stay put were measured rather than assumed.** `Marks` is what `/tmtp` puts a caller on and what a place's board measures to, so each was re-read as the drawn ground of its own column plus one: Shire 67, Thorenhad 117, Barachen 120, the ruin 121, the crags 115/117/116, Matuna 122, Gloin 180, goblins 145 (was 114, 158, 159, 160, 155/157/157, 161, 224, 196), and `TeleportCommands.LOCKED`'s Trollshaws point is 119, not 87. Left alone: Moria's floor 14 and roof 40, because the whole rectangle's ground now runs 108 to 225 with no column under 41, so the halls are inside stone either way and the numbers are a depth band, not a reading of the country; Gundabad's gate 187, keep 176, pass mouth 176 and door screen 190..199, which still match the drawing at 186..193 and 175; and the orc pieces at 185..189, which stand on the pass's own carved floor. **Open: the Witch-king's seat.** `Campaign.CASTLE_SEAT` (3850, 107, -7425) is 13 blocks over the drawn ground now, and it cannot be re-based from the drawing because it is a block inside a castle `Sites` seats by survey - its x and z are as instance-specific as its y. Either walk the new chair or hold the seat as an offset from the castle's corner, or the last act seats him in the air above it.

**The drawing is read with its black at y0, its white at y318 and its water at 25.** Both ends of the height raster were implicit - black at `SEA_LEVEL` and white at the city's crown - and `ArdaMasks.lift` blended the ground toward that crown by the mountain mask, so as the mask reached white the drawing's relief was multiplied out and every bright column came out at 318, one block under a 384-tall world's sky; that was the flat tops. `BLACK_Y` 0 / `WHITE_Y` 318 / `SEA_LEVEL` 25 are now what the map means (the setting it was proofed at in a terrain editor), and `ArdaMasks.raise` adds the mask (gain 64, the climb the drawing's own top of 254 needs to reach 318) instead of blending it. On the rasters the same formula now reads p50 126, p90 192, p99 271, max 318, and land within three blocks of 318 falls from 1.75% to 0.001%. The three drawn sea beds moved under the new waterline (`SEA_LEVEL - 27 / -25 / -23`) and Nurn's and Umbar's shore bands are `SEA_LEVEL + 5`. **Seen in a world on 25 November 2026:** the drawing reads through to its own crown - the flat tops at 318 are gone - with the water at 25. A world made before this keeps the old sea and the old flat tops in the chunks it already has. The rivers are the next step and are untouched: widths are still whatever the mask draws, and `riverDepth` still reads a raw raster value where a height was meant, which saturates at `RIVER_STEEP_MAX` - so every drawn river's valley climbs at sixteen blocks a cell today, whatever the country beside it stands at.

**The water's beds are mixed now, and they roll.** A sea's floor now carries two octaves of the map's noise (`seabed`, six blocks at ninety-six and twenty-seven, held under the waterline) instead of its country's five blocks of relief, so a bay is not one depth; a lake's bed adds four blocks of roll on the coverage's dome (`lakebed`). And `dressWater` lays the owner's palettes per column instead of leaving the surface rules to answer by biome - the bed 90/5/5/5/10/21/1/3 of 140 coarse dirt, stone, andesite, gravel, podzol, mossy cobble, dirt and cobble; the bank 30/4/15/3/1/1/1/1/1/1 of 58 coarse dirt, podzol, grass, gravel, stone, andesite, two dead corals, cobble and mossy cobble - drawn from a hash of the column's own coordinates, so the pattern has no chunk seams, with a second block under the top half the time. A sea's bed is dressed and its shore is not; a bank is a dry land column with water in one of four neighbours. The pass costs 324 height samples a chunk (an 18 by 18 ring, the extra column so shores do not step at chunk edges). `PLACED_RIVER_REEDS` stands the mod's reed at eight a chunk among the cane's twelve, on all ten water biomes. Compiled and generated; the Shire's own rivers were walked in the 25 November world, though no bed or bank was examined block by block.

**A drawn river is narrowed by the fall of the country and allowed to wander where it is flat.** The channel's inset is now the drawing's sixth plus up to ten blocks a side of gorge where the ground falls (`steepness`, read over eight blocks, full at 0.3 a block) and less up to nine of that sixth where it does not, both banks cut on their own from a 128-block noise - so the water's edge wanders inside the drawn channel instead of running parallel to it. Measured on the drawing over 120,000 channel cells: the fall reads p50 0.15, 32.5% of the channel takes the whole wander, 1.9% the whole gorge, about 4 blocks narrower through steep country and 7 wider across flat on a 40-block channel. `ArdaMap.riverDepth` also stopped reading the raw sixteen-bit raster value as a block height - that pinned `rise` to `RIVER_STEEP_MAX` sixteen on every channel, so every river valley climbed two blocks a block; read through the drawing's ends, the climb is three a cell for a river five blocks under its country and nine for one two hundred under. **Seen in a world on 25 November 2026:** the rivers read as valleys cut by their country rather than sixteen-block walls, which was the change to judge.

**A land country's ground is held at the waterline.** The reference paints its sea black and the soft edge of that black runs inland under cells the country grid calls land, so those columns read nought to six blocks and the reshape laid twenty-five blocks of sea over the Shire - water twenty over the ground with no channel drawn on it. `ArdaMap.readDrawn` holds every land country's column at `SEA_LEVEL` after the grading; sampled over x -1200..300, z -800..400 at 32 blocks, 79 of 1,786 columns flooded before and 10 now, all of them a drawn channel's own bed. **Seen in a world on 25 November 2026:** no sea lies over ground the drawing calls land, and water stands only in a drawn channel's own bed. (The world the flood was seen in answered y122 at Hobbiton where the older map answered y55.)

**A carried backpack no longer kills the tick at the top of the world.** `Progress Check 6` crashed at 17:40 on 23 November with `Index 24 out of bounds for length 24`: the player stood at 5785, 320, -3001 - the block above the overworld's last one, whose sections stop at 319 - carrying a magnet bag with drops on the ground. The magnet reads a carried bag through a copy of it that is not in the world (`BackpackBlockEntity.createHeld`), and that copy was put at `player.blockPosition()`; Neoforge writes an attachment read on a block entity down as a block change on its chunk, and that write indexes sections by y, so the copy asked for section 24 of a 24-section chunk. The copy now takes the player's x and z with its y clamped into the level's range (`getMinY()` to `getMaxY() - 1`), so the read lands in a section the chunk has. Nothing else about a held bag changed, and no block in the world is touched. Compiled only - the position has not been stood in again. The two 22 November crash reports are unrelated: 09:56 was a missing `Sites$Lines` class and 08:17 the watchdog after a sixty-second tick.

**The road out of the Shire now lays from the end the hand is at.** `Progress Check 6` on 23 November: the Shire leg's second dry stretch (`..._2`) began at the village gate at 16:11, searched to Hobbiton, stopped 40 blocks short with 31 stations held for the village's note - and never finished its grade or laid the arrival, before or after the village stood at 16:12:41. The hold had turned it to grade from the far bank of the Brandywine, about nine hundred blocks east of the player, and the tick only steps a pass while the chunk under its head is loaded. `alignDry` now grades from the end the pass was asked from and cuts the town's margin wherever it falls on that walk; `heldFirst` carries the direction in the note, `joint`/`join` take the grade's start height and put the arrival in front of the line, and a pass waiting at a town's edge is stepped on the arrival's ground. The Bree leg shows the other half: built by the hand at 16:28 and stopped 38 stations short, it never laid that arrival when Bree stood at 16:32:53 because the player had left for the Shire. The Shire leg was forgotten at 16:25 (`/tmroad forget`, three sections), so `Progress Check 6` only finds it again at a new start, and nothing lifts the ground the forgotten pass laid. The road still fills in as it is walked - standing at the gate lays what the loaded chunks cover and the rest follows the walk.

**Bucklebury's drawn bridge is held for Bucklebury.** The pair (`bucklebury_end`/`bucklebury_middle`, cobblestone-wall piers) is not stood on the Shire leg's crossing on the owner's call: the road from the Shire to Bree keeps the standard `bridge_end`/`bridge_middle`, and the drawn bridge waits in the jar for the Hobbiton to Bucklebury road. `Roads.BRIDGES` is empty again, so `pieces` falls back to the standard pair at every span; nothing in `Progress Check 6` was stood from either piece (its `_bridge_1` began and never reached the bridge stage), so no world holds a bridge that has to be lifted.

**The Shire leg now arrives on the village's east side.** The owner moved its Hobbiton end from the `/tmtp` point at -463, -124 to -150, -382 in `Progress Check 6` on 23 November, after the leg laid, so it is `planned_1032_-34_-150_-382` now and `Roads.TOWNS` measures the village hold from that east-edge block. `Pins.SOURCE`'s `hobbiton` point is unchanged - a pin is where the town's name stands, not where a road meets it, so `/tmtp` still puts a traveller at -463, 98, -124. A world that walked the old line keeps it under `..._1032_-34_-463_-124` as a different road, and the ground it laid stays; the re-ended leg has not been planned or laid yet.

**Open now.** Nothing has been laid since 23 November and every paragraph in this block is compiled only. `Progress Check 6` holds a Bree leg built by hand between 16:25:44 and 16:28:41 (103401 columns, stopped 38 stations short): its record reads `stage=GRADE`, `approach=4056` from 1031, -36 to 4648, -3316, `graded at=2288/57172`, no held, and its 16:46 relog said "24 sections in hand" while Bree stood at 16:32:53 with no arrival line. The Shire leg's `..._2` began at the village gate at 16:11:13 from -488, -120, reached SEARCH with 31 stations held and no GRADE, and was forgotten at 16:25:29 (`/tmroad forget`, "3 sections and 0 planned"), so the re-ended `planned_1032_-34_-150_-382` is not in that world's pass and the ground the old `..._1032_-34_-463_-124` line laid stays. The Last Bridge is still unnamed because the Bree leg crosses no drawn water ("Nothing on the way is water, so nothing is bridged", 11:39 on 22 November), and the Shire leg's `_bridge_1` (471, 90 to 418, 90, fifty-four columns of the Brandywine at deck 116) began and never reached the bridge stage, so no span has been stood from either pair there; `Roads.BRIDGES` now names the Bucklebury pair at the new road's crossing, as the top of this file says. A road end needs at least `EXIT_REACH` 16 blocks of dry ground between the pin and the water on each side - the span's own section reaches 16 back from each bank, 8 of that `RUN_IN` straight onto the bridge line, and the dry road up to it is only written when it is at least one `LANE` 16 long - and the water must lie inside the leg: water at an end is trimmed and nothing is bridged there. `BANK_SEARCH` 96 bounds the bank hunt, `MAX_SPAN` 96 bounds the water, and a dry lane within `BANK_MARGIN` 3 lanes (48 blocks) of water is priced as bank so the road steps inland. The Shire's own milestone was refused at 16:46 ("no milestone could be stood at -503, 101, -314; the ground there is Block{minecraft:gravel}") because the village raise left gravel there, and nothing was changed for it. The 17:40 crash is the backpack, fixed above; the two 22 November reports are unrelated - 09:56 a missing `Sites$Lines` class, 08:17 the watchdog after a sixty-second tick.

Before that, 22 November 2026 - the owner's fresh `Progress Check 2` showed **holes at Bree's join**: where the
ring smooths the town into the country there are bare blocks of ground with no canopy over them, looking for all the
world like trees the drape took out and never filled. The cause was found in the draw and fixed - read *22 November*
first. The same day's housekeeping, while that new world was starting: **the Arda crossing waits for the Shire** rather
than putting a doorway down in a footprint the village's own pass is about to clear; **a camp's keeper is stood in a
column a body can stand in** and only once the camp's blocks are there, instead of being left in the hill when the
marker pin is a few blocks under the floor; **the towns' milestones are asked for again on a clock** until the ground
takes them, the Shire's only once its note says the village is complete; and **every laid road is posted near both of
its ends**, so a player leaving a town meets a fingerpost before the road takes them out of sight of the walls. And
**Bree's mustering hall now has its ground**, marked in `Progress Check 3`: two blocks of ammolite set into the grass
at 1031 and 1032, 122, -32, the middle two blocks of the hall's 16-block front (the side the job board is on), written
into `Pins.SOURCE` as `hall:bree` and stood after the town's own pass rather than before it. **The drape is now one
pass with declarations** (`Drape`): an outline read from either a cut folder or a structure template, and a build's
ring written as one line - box, reach, deck, trees, fill, version - with a camp's sweep already moved onto it unchanged.
**The country standing inside the walls is taken off too**: the ring is only the ring, and inside the footprint the
ground was the build's alone - which is right for a cut that fills its columns to its own top and wrong for a cut that
carves a valley, whose floors are left under the hill above them. A column inside the walls whose country stands
`BANK_BLOCKS` (3) or more over the top of the cut in that column has the country cut off down to that top: the cut's
own top course stays and nothing below it is touched, so no wall, floor or roof can be cut, and shallow grooves are
left as grooves. Counted with the rest - the log and `/tmcity drape` now end `N inside the walls cut down to the cut`.
Measured against the cut's own pieces, Rivendell's inner columns already stand within a block or two of the cut's top
at the edges sampled, so this changes little there: the raw face at the city's rim is the city's own terraced edge.

**A column with no ground under it is cleared, not walked past**: the city drape's ground search stops forty blocks
under a column's top, and a column that found nothing in that depth was left exactly as it stood - right for a cave,
wrong for a build's own carve. Rivendell's cut took the hill out from under its oaks and their crowns hung ninety-four
blocks over the city floor, which is the shelf and the raw face in that screenshot. The tree in such a column now
comes off (nothing is filled and nothing is joined: rock in the air is rock over a hole), and the pass counts both
what it leaves alone and what it clears that way, so the log and `/tmcity drape` say it without anybody reading
blocks: `18902 cut, 35078 filled, 320 water, N left alone, M cleared of a tree standing on nothing`.

**Bree's road to the Shire is in the network**: the leg the owner walked in `Progress Check 3` (five wide, from
Bree's hall front at 1032, -34) is a `Named` entry now, read back out of that world's own `road_plans.dat`, where it
was saved with its bends and its plan, and `roads.dat`, where it is recorded laid. Its first walk ended at -150, 114
- the village's east edge line but a hundred and fifty blocks south of the footprint, out in open wood - so the leg
is re-ended at the Shire's gate, -463, -124. An id is a road's two ends, so that renamed the road to
`..._1032_-34_-463_-124`; in `Progress Check 3` the old id is still planned and finished, and is a different road to
the pass until `/tmroad forget` is run on it (its northern bulge at 242, 227 is three hundred blocks from anything
the new line uses). Named roads are begun by the ground that crosses them, so the re-ended leg needs no command - it
lays as its chunks load - and the gravel the first attempt laid stays standing, because nothing lifts a laid road.

**Rivendell's road to Bree is in the network too**: marked by the owner in `Progress Check 4` on 22 November, five
wide, from the vale's road junction at 4648, -3316 - the block beside the join the three vale roads make at 4647,
-3315 - to Bree's own hall front at 1031, -36. The pin was first given with the whole far end wrong, the town's x
negated and the hall's height of 123 taken for its z (`..._4648_-3316_-1031_123`), which sent it two thousand
blocks out into Eriador; both numbers are corrected, and since an id is a road's two ends the corrected leg is
`..._4648_-3316_1031_-36` - a different road to the pass, not the old one moved. `Progress Check 4` has already laid
the last stretch of the first line (-803, 143 to -1031, 123) and holds the rest at `SEARCH`: `/tmroad forget` there
drops its plan and progress so it is not re-laid beside the corrected road, and the ground it laid stays, because
nothing in the mod lifts a road. The named leg needs no command - a named road is begun by the ground that crosses
it - and it has not been walked since the correction.

Before that, 21 November - the owner walked a fresh world, `Work Verification`, and signed off the Shire,
the road network and the Carrock, then started it again with the passage clear and found the hold's door open at the
end of it. The mustering hall is turned half round (its board faced away from the path), the
Shire's banner is drawn round the portal crossing so it is seen on stepping through, and the keep is moved twelve east
so its own doorway stands in the gate's chamber instead of ending at a wall; a road now also begins from the block a
player is set down on, because Gundabad's own mark is sixty-four blocks from its road's end. **The roads now post
themselves** - a fingerpost every thousand blocks and one at each junction, **signed off in a world on 21 November** -
and a leg too steep to climb is planned as the switchbacks it needs. Read *21 November* below.

**22 November - the holes at Bree's join.** The drape read every ring column's top from
`MOTION_BLOCKING_NO_LEAVES`, which answers with the highest block that is not a leaf; under a bough reaching out over
air that is the bough, one block over nothing. Read there, a column had its crown cleared - the leaves over the bough
stand above it - then found air, was taken for a column with nothing solid under it, and was returned from untouched:
the canopy gone and the ground left at the country's level, one per crown along the join. Measured by dressing a copy
of `Progress Check 2` headless with counters in the pass: **32,799 of the ring's 44,090 columns ended in that return**,
and the columns sampled at the south join read `surface 128 top 126 skin Air` - air two blocks under a crown. The
columns are now read from their blocks, down through air, leaves and wood to the first ground (`Dressing.ground`, up to
`COLUMN_DEPTH` 40), and `Dressing.tree` knows this mod's own `leaves_*`, `log_*` and `wood_*` names as well as
vanilla's, since its logs and wood are in no tag. `DRESS_VERSION` stays 1: `/bree drape` dresses an existing ring
again. **The fix is compiled and the cause is measured; the re-dressed ring has not been walked in game.**
**Before that, 20 November** - the Shire is raised on every world's start now (`raiseShireOnStart` defaults
`true`), because the arrival portal opens on the village's own crossing, -470, 97, -123, and the clearing pass took
the doorway with it; a dedicated server's `pause-when-empty-seconds` is lifted for the length of that pass, measured
rather than assumed: a fresh level with nobody online stood the village in **1053 ticks (52.7 s)** and had its pause
back afterwards. Before that, 19 November - a road that met one channel twice is walked again with its crossing held
straight, planned against the drawing with `tools/roadprobe`; a road no longer begins under a build that is still
going up (the valley's road waited for the city and left a gap in Road Test 7); the twelve builds the owner has
visited are marked at the block a traveller should arrive at, which `/tmtp`, the page's needle and the sign boards all
read; and a leg that runs beside a river now steps the line inland instead of refusing the road. Before that,
18 November - every walked road is erased and a road is now planned from its two ends by `RoadPlan`, kept in the save
and laid from that plan; **one road has been laid in a world and it was laid under a river**, the plan now reads its
line a block at a time to find the water, and that fix has since been run in a world and signed off (see *21 November*). Before that, 17 November - the
mustering halls are pinned per town and raise themselves, the marks the world draws are behind one client switch
that ships on, and the Shire's south cliff is dressed by a new bank rule (seen on 21 November); the Carrock is cut,
trimmed of its base plate, pinned at 8307, 187, -4165 and stood there by the world on the first start. **Signed off in
game that day:** Hobbiton's hall (and its pin, now in `Pins.SOURCE`) and every quest mark, icon for icon - neither
needs another hand. Read *21 November* first for the walk that signed the Shire, the roads and the Carrock off, then *20 November* for
the Shire's start, *17 November* for the halls and the marks, and *The Shire map* for the village itself.

The whole Gundabad line is walked and settled, the way in past the hold's door with it; chapter two is proven to
page 32 and **paused** there; the road east of Glóin's camp is the old forest road, planned from its two ends with both
crossings in its plan;
the keep's floor is **settled at `KEEP_FLOOR` 176**, and its entrance was moved twelve east on 21 November so the hold's
own doorway stands in the gate's chamber, with the passage to it **walked and the door open** on 21 November. The Shire
is **seen whole** and the road network is signed off; the quest walk is still paused where it was, at Carn Dûm.

## Where we stopped

- **Arda's terrain is signed off in a world - 25 November 2026.** A fresh world, made after fully quitting the client, shows the drawing's ground without the sheet of water it used to carry, the rivers and the drape as they were, the Shire's party tree standing and no country plinth left under a drawn crown; the owner's report is "perfection, issues cleared". What that world did not look at is named in the paragraphs above: caves and the deep lava line, Bree's join and hill, the camp pins, and the Shire's four edge columns with a drawn channel in them.
- **Chapter two works to page 32 (2-7).** Played in the test world from the chapter-one seam through the Gundabad pages:
  the courtyard scene, Glóin's camp, Ulmug's fires, the hold raised when its page is taken, the vault, Gimli in his own
  cave, and the captain and the wraith below the stair. It reads as written; nothing in it is waiting on a fix.
- **Paused at page 33.** The four left - 33 Steps of Evil I, 34 Steps of Evil II, 35 Evil Tidings, 36 Fire & Ice - are
  all Carn Dûm ground, and **Carn Dûm is what quests go to next**.
- **The keep's placement is settled, and the world is what settled it.** A fresh world drew the keep exactly at its plan
  and still had no way in: the keep's rooms stood on **200** with the gate's chamber on **189**, so the chamber opened
  under them and the keep's floor was the chamber's ceiling. The number that was wrong is `KEEP_FLOOR`: the template's
  rooms are lifted thirteen courses over its base, so the pin is **176**, not 187. See `docs/gundabad-keep.md`.

## 21 November 2026 - the walked world, and the four things it changed

The owner walked a fresh world, `Work Verification`. **The Shire stood before the portal was used and was ready when he
came through**; the road network laid itself and the bridges did not double back over a channel; the Carrock spawned in
perfectly. Three things came back to change.

- **The mustering hall is turned half round.** The board stood at the end of the build away from the path it sits on.
  `TownHalls` lays the hall with `Rotation.CLOCKWISE_180` about its box's own middle - same pin, same floor, same
  footprint, front the other way - and reads it back with `Sites.wholeAt` turned, so a hall standing the other way is
  laid again rather than left. One standing unturned is swept out of its box first: the file does not carry every cell
  of its 16x15x16 box (972 air, 1307 missing of 3840), so a turned lay over an unturned hall would leave the old walls
  where the file says nothing. Its bottom course is complete in the file, so the floor comes back with the lay.
- **The Shire's banner is drawn round the portal.** `Places.standTowns` preferred a town's mark, and the Shire's mark
  is (-150, 114, -382) - four hundred blocks from the crossing at (-470, 97, -123), which is further than the town's
  own 280-block radius, so stepping out of the portal was the one place the village was never announced. `Places` now
  has an `ANNOUNCE` point per town, read from `ArdaMap.hobbitonGate()` - the same constant the portal crosses at - and
  a town given one is re-stood when the point moves, so a world holding the old centre is moved to the new one.
- **The keep's entrance stands in the chamber now** - the twelve-east move and the doorway above.

**Two guards were still owed, and both are fixed.** The hall's was `Sites.wholeAt` read turned - the strict reading,
every sampled block - so **one block a player changed inside the hall made the next start sweep it and lay it again over
their work**. `TownHalls` now writes down which way it laid the hall, against the pin, in a file of its own (`halls`),
and asks that first: a hall recorded turned is returned from without touching a block, and a world made before the
record existed has the way it stands read off its own blocks - the two orientations compared by a majority, so a changed
block cannot move the answer - and then noted. Only a hall the record says was laid the other way is taken out and laid
again. The keep's guard asked only for the plan this build writes, so a world that already held the hold at the older
5516 would have had a second one laid at 5528, 110 of its 122 columns over the first. `ArdaMap.keepOlderPlans()` is
asked too - 5516 at 176, and at the 187 the plan used between 15 and 18 November - and a world holding either keeps its
hold, with only its mouth dressed. **The twelve-east shift is what a world made after 21 November gets; it is not a
migration.**

**A second fresh world found the passage thirteen blocks short, and the clear was the whole of it.** The hall laid its
floor, its roof and its two walls for the fifty-three blocks to the keep's wall, and the hold stood at its plan with the
door the plan was written around - 5571..5575, inside the chamber - but the passage between them was never opened: the
inside was cleared with `base.offset(x, y, z)`, in which the cell's own y and z are added to the gate's corner as well,
so the air was written about 377 courses up and seven thousand blocks north, where there is nothing. What stood between
the hold's mouth and the hall was therefore thirteen blocks of the mountain's own rising ground, from z -6910 to
-6922 - `clearMouth`'s eight blocks out from the wall being the only rock ever taken out of it, which is what made the
two read as a tunnel stopping short. `extendHall` now builds the cell from the corner and the passage's own x, and
`undressBack` - the strip behind the gate, which had the same double offset in its y - is fixed with it. Nothing in the
plan moves: the passage still ends on the keep's wall at -6930, its inside is cleared to the course before that wall -
the wall itself is never cleared, because the way in is the template's doorway in it and a hole the passage's width
would take the door out on every start after the first - and a world that already holds the hold is re-cut on its
next start, because the hall is carried on every pass.

**A road also now begins from the block a player is set down on.** `RoadCorridor.look` reached 32 blocks, and a road's
end stands at a build's door while the build's own mark can be much further in: Gundabad's road begins at the gate's
courtyard, 5578, -6769, and the hold's mark is 5578, 190, -6833, sixty-four blocks away - so a player teleported to
the hold stood outside the reach of its own road and nothing began. The log shows exactly that: the road's first
section began at world start from 5704, -6680 and then waited on a head whose chunks nobody was near, and its next
sections only began at 19:39 when a teleport to the Carrock loaded chunks of its line. The reach is 96 now. (At
Glóin's camp nothing was wrong: the Gundabad road joins the old forest road at 8583, -3617, three hundred and fifty
blocks east of the camp.)

**Seen 21 November, in a fresh world:** the hall laid turned, its board on the path side; the Shire's banner fired where
it should, at the portal; and the keep stood at 5528 with its door at 5571..5575 inside the chamber. **Then walked, with
the clear in:** the owner started that world again and reports the hold's door open - the passage runs from the gate's
hall to the template's own doorway and the way in is there, which was the thirteen blocks above. The rooms past the
door were not reported on separately. A world that already holds the hold at the older 5516 keeps it.

## 20 November 2026 - the Shire stands at every world's start

The portal out of the overworld opens on the village's own crossing, -470, 97, -123, which is inside the footprint
the clearing pass works over (x -700..-149 by z -512..-44), so a village raised by a player's arrival is a village
that takes that player's doorway away: the pass begins on the first tick after the crossing is used and the
ammolite doorway goes about ten seconds later. `raiseShireOnStart` now defaults `true`, so the village is raised at
the world's start, about a minute into a fresh world, before anybody can walk through to it.

A start is exactly when a dedicated server is empty, so the pass holds the server's own `pause-when-empty-seconds`
at 0 for its length and gives the number back when the village stands. That half was measured, not assumed: a fresh
level with the property at 5 s and nobody online logs `Server empty for 5 seconds, pausing` part way through the
pass and never takes another step, because the paused branch of `MinecraftServer.tickServer` returns before the tick
event and before the level's own tick. With the lift, the village stood at -700, 87, -512 in **1053 ticks (52.7 s)**
and the log read `the server's pause when empty is back to 5 s`. A server killed outright mid-pass keeps the zero,
which costs it its idle pause and nothing else, as it keeps the level's forced-chunk tickets until the next start
re-runs the pass.

**Seen 21 November:** the owner came through the crossing into the village and found the deck, the mouths, the ring
and the joins right - this is the walk that paragraph used to owe. A world that sets `raiseShireOnStart = false` keeps the older rule, the pass beginning when
somebody comes within `REACH` 192, and `/hobbiton raise` still asks for it at any time. The commit is `0e2c70f2`.

## 22 November 2026 - Bree is sited, and its hill is the world's

The owner's cut of Bree-town, converted into a save and marked at its four corners with ammolite, measures **496 by 672
by 108**: markers at y 14, highest block 121, about 7.5 million blocks above the marker course. The town's own ground
lies at 19 to 35 and its roofs top out at 63; the eastern 166 blocks - a third of the box - are Bree-hill, a treed slope
climbing to 121 that is cut off square where the markers stop.

**The site is pin (1024, -256).** Box x 1024..1519 by z -256..415, standing in cells 12/19 and 12/20 only, on ground
101 to 130 - a drop of 28, which is the Shire village's own - with no water inside it and the stream 58 east at its
southern end. The middle of the drawn cell 12/20, 1280, 256, where the East-West Road and the Greenway are drawn to
meet, falls inside the box in its south half. The one arrangement that put the town's own road junction on that middle
(1135, 31) was measured and refused: 44 blocks of fall, mean 110, 367 wet samples.

**The seat is the town's grass line.** `ArdaMap.BREE_DECK_LIFT` is 5 - the markers are at 14 and the plain's grass and
paving at 19 - so the pin's y 117 puts the grass line on 122, the mean of the ground surveyed. The drape is given the
same number, so a column whose top is the paving is a floor the country meets rather than a roofline it is cut down to.

**Bree-hill is laid by the world, not dressed to it.** `ArdaMap.breeHill` carries the build's own east edge out into
Eriador. At the foot - four blocks inside the footprint, over which the cut's columns are cleared and laid again - the
rock stands at the build's own top for that depth, read along its edge from the save (`BREE_EDGE_ZS`, `BREE_EDGE_TOPS`:
68 at the north corner, 118 where the hill is highest, 33 at the south end). East of the foot the flank falls a block
for a block until it meets the drawn country, and there it stops; the crest is eased to the country over 32 blocks past
either end, so the hill turns its corners; the flank carries the map's own folded noise, faded in off the foot; and a
column the drawing paints as river or lake is never raised, so the stream at the town's southern foot is left where it
is. The rock's highest block is 220 at (1515, 30), and it meets the drawn country between x 1545 and 1607 - west of the
stream everywhere.

**The raise is Hobbiton's pass with the village taken out.** `Bree.autospawn` asks for the ground a chunk a tick, lays
the cut piece by piece at `ArdaMap.breePlan()` (1024, 117, -256), clears the country out of the footprint a band of
columns at a time, and hands the rest to `Dressing.begin` with the town's own deck and its trees left out of its shape.
`/bree raise` asks for it at any time; a world otherwise begins at a start, or when somebody is within 192 blocks.
`Bree.complete` answers from the note alone. `Pins.SOURCE` carries the town at its walked arrival point, 1059, 123, -31
(it was `ArdaMap.breeCentre()`, 1272, 122, 80, which is a fifth of the way across the footprint from the west gate).

**The cut ships in the jar, as of 22 November.** The 462 pieces and their table are in
`src/main/resources/data/tolkienmobs/structure/bree/` - 91 MB, in the size of the other cuts - so every world raises
the town with nothing dropped into its save first. `CutCommands` asks the jar before the save, so a copy left in a save
folder cannot quietly win over it; `Bree` still writes `_stands.txt` into the played world's own
`tolkienmobs/cuts/bree/`, which is the note and not the cut.

That shipping answers a fault the owner found on the 22nd: the cut had only ever been left in `Progress Check 1`'s
save, that world was deleted, and the new `Progress Check 2` started with no cut at all - `autospawn` said so in the
log (`no bree cut to raise - nothing in ...\cuts\bree, and nothing in the jar beside it`) and stood nothing, which is
the warning the pass was given for exactly this. The cut is taken out of the `Bree-Town` save, which still holds the
build:

    python tools/grab_build.py --world "run/saves/Bree-Town" --from 11504,14,7856 --to 11999,121,8527 \
        --out src/main/resources/data/tolkienmobs/structure/bree --name bree

A rebuild of the jar is what puts it in a world; no restart of the save is needed, because `/bree raise` reads the cut
when it is asked. A cut regenerated into `run/` is read by nothing - `CutCommands.folder` is the save's own folder.

Its table needs no `deck` line: the pass passes its own. `tools/grab_build.py` cuts at 48 to a piece, and the table
carries each edge piece's own size - Bree ends in a 16-wide column and a 12-tall course - which is what the box the
ring and the clearing band are worked from is read as.

**What to walk once it is in:** `docs/bree.md` - the hill at the build's edge, the stream at the southern foot and the
drape's ring, each with what good looks like and the line to change under each thing that comes back wrong.

A world that already holds the town is left alone; one whose cut is replaced raises it again, because the note records
the table's fingerprint.

### Raised in a world, and the ring's plates - 22 November

**The town stood.** In `Progress Check 1` it was laid at 1024, 117, -256 in **935 ticks (46.8 s)**, 462 pieces over
1,610 chunks, and the drape reported **23,316 columns cut, 37,942 filled, none left for the water**. The owner walked it
and reported the build in. What came back wrong was one edge piece: **plates of bare stone standing in the air above
the ring's trees**, with the leaves still under them, on many of the town's edges - and the same pass dresses the city
and Hobbiton, so both wear it.

**The save named the drape, not the hill.** Read straight out of `Progress Check 1`, the south band of the ring
(x 1000..1200, z 416..447) held **1,609 columns topping in stone, 483 of them with air under the stone** - thirty per
cent - and a slice of the box's own interior, which the drape never touches (x 1024..1080, z 340..419), held **none**. Every one of the plates is that: a
column whose top is stone with air between it and its ground, and grass at the ground under it.

**The cause is how the ring read a tree.** `dressColumn` took its column height from `MOTION_BLOCKING`, whose answer
over a wood is the top of the leaves, and then walked down while the block was a leaf or a log. A big crown hangs over
air, so the walk stopped in that air: the column's `skin` was `cave_air` at five or ten blocks over its own ground, the
fill loop then started at `top + 1` - above the ground - and the cap's block, `skin.isSolid() ? skin : STONE`, had no
soil to be, so it was laid as `STONE`. A plate of rock in the air, one per crown, placed by the drape at the height the
crown used to be. The leaves left under it are the same reading: the removal loop only ever cleared down to `top`.

**Fixed the same day in the shared pass.** `Dressing.dressColumn` and `Dressing.openMouth` take the column's height
from `MOTION_BLOCKING_NO_LEAVES` - the ground under a crown - and walk down only a trunk from there; the removal loop
clears the whole crown to that ground; and a column with nothing solid under it is **left exactly as it stands**,
neither filled nor cut, so no fill can begin in the air and no cut can take the ground out from under a plate. The
fill's top block is the skin itself and never stone. `DRESS_VERSION` stays at 1 on purpose: a re-dress reads a plate as
ground and leaves it, so asking every held world to dress again would buy nothing.

**The test world's ring has been taken out, so the fix can be seen in it.** **308 chunks** - every chunk of
x 992..1551, z -288..447 that lies wholly outside the footprint, which is the drape's own reach and holds no part of
the build - were taken out of `Progress Check 1`'s Arda region, entity and poi files by the new
`tools/trim_ring.py`, which reuses `chunk_trim`'s reader, backup and read-back check:

    python tools/trim_ring.py --world "run/saves/Progress Check 1" --apply

The town's own chunks are untouched - the grass at 1060, 140 is still 122, the courtyard at 1272, 80 is still dirt to
127 under cobble at 128, 1512, 200 still carries the hill's trees to 207 - and the ring now reads as unmade. The two
notes beside the cut, `_stands.txt` and `_dressed.txt`, were removed with it, so the town is raised again next - by
the world's own pass, by `/bree raise`, or, if it is typed in the ten seconds the world waits before beginning, by
`/bree drape`, which dresses the fresh ring and lays nothing. Either way the ground comes back as the drawing and the
hill make it and the ring is dressed by the fixed pass; 935 ticks took the last raise. The rewritten files are under `run/chunk-trim-backup/20261122-060456`.
**Nothing has been re-seen: the ring is unmade until the world is opened.**

**The drape can be asked for on its own now.** `/bree drape` is the raise's last stage without the rest: it reads the
cut's outline and dresses the ring at the town's own deck and with its trees out of its shape, at the same 5 ms a tick,
and writes `_stands.txt` when it finishes, so the world neither raises the town again nor dresses it twice. No piece of
the town is laid and no country is cleared out of it, which is what makes it the pass for the ring whose ground has
been taken out and made again. It also replaces a raise the world has not begun - that one waits 200 ticks and a player
within 192 blocks - so it can be typed at a start. `/tmcut drape bree` is not this pass: a cut's table with no `deck`
line is dressed to the city's deck of 2, and Bree's floor stands 5 over its marker course.

**What the fix does not reach.** Ground already dressed keeps its plates. Seeing the fix in any other world means
chunks generated since it, or one of the two things below.

1. **A repair pass** - the only way to mend country already dressed. Walk the ring, take out stone that has air within
   three blocks under it and the ground further down, then dress the column again from that ground. It needs a rule
   that tells the drape's own fill from Bree-hill's rock (the hill is drawn ground and stands on itself; the fill was
   laid over air) and a command to ask for it, since `/bree` has only `raise`. Moderate work, small risk, and it mends
   the city's and the Shire's rings with the same pass.
2. **Cap the fill** - the largest change, and the one to agree before making. The ring fills every column whose ground
   stands under `wall`, the build's own edge at the nearest box column, easing to the ground over 32 blocks: on the
   town's edges that is what makes the stone skirt up to the roofs, and 37,942 filled columns in this raise. Stopping
   the fill above a couple of blocks - cutting instead, or leaving the ground - would change the silhouette of every
   dressed build, so it is the owner's call and wants a fresh world to judge it in.

## The list

| # | Item | Status | Where the detail is |
|---|------|--------|---------------------|
| 1 | **Carn Dûm's ground** - the camps on its borders and the wight's hall | **next** | nothing has looked at it yet; `carndum` is only a place canon'd to `ANGMAR` |
| 2 | **The roads** - the whole network, rebuilt from two ends per road | **eleven roads in `Roads.NETWORK`**, each as the two ends it was pinned between: Gundabad's road (5578, -6769 to 8583, -3617), the old forest road, the four legs of the vale and the high pass, the wood ruin's nine-block connector, Rivendell to the meeting plaza and the plaza on to Glóin's camp, Rivendell to Elladan's camp, and the short leg at Rivendell's south gate. **Signed off 21 November:** the network laid itself in a fresh world and the crossings did not double back; see *21 November* for the one limit | `RoadPlan`, `/tmroad plan`/`build`/`pin`; see 18 November below |
| 3 | **Chapter two, pages 33-36** | paused on 1 | `docs/chapter-two-state.md` |
| 4 | **Chapter three** - eleven pages, prose written | not started | `docs/chapter-three.md` |
| 5 | **The keep's placement** | **settled**: `KEEP_FLOOR` 176 - rooms' floor 189, walked on 190, level with the passage. **Entrance moved twelve east on 21 November** (*The keep's doorway* below); a world that already holds the hold at 5516 keeps it (`ArdaMap.keepOlderPlans()`), so nothing is laid twice. **Walked 21 November:** the passage clears to the door and the door is open | `docs/gundabad-keep.md` |
| 6 | **Sign posts** | **built**: `SignPosts` plants one post every 1000 blocks of a laid road and one at each junction, arms naming the towns that way with their distances, the double arm where two towns lie down one road; a plinth already standing answers a target, so it runs on a clock and by hand without stacking. **Signed off in a world, 21 November** - five faults found in the first post and fixed the same day (a doubled post at a join, arms at the bottom, a town on the wrong arm, east/west arrows turned inward, posts on the road), the lettering with them, and the corrected posts confirmed by the owner | `SignPosts`, `/tmsign place`; see *The fingerposts* below |
| 7 | **Chinese** | on hold | whole, until a full public release |
| 8 | Chapter four onward, the finale, the job boards (quest 37) | not started | `FINALE_OPEN = false`, closed by design |
| 9 | **The Shire map** - Hobbiton | placed, cleared, draped; river rerouted; **raised at every world's start since 20 November**, and **seen whole on 21 November** - ready when the owner came through the portal, joins and south edge with it. Nothing is owed | `run/hobbiton_art`, `/tmcut`, `rivers.png`; see below |
| 10 | **Town halls** - one pinned per town, raised once the town is finished | Hobbiton's stands, was called perfect, and is **turned half round since 21 November** so its board faces the path; the way it was laid is recorded per pin, so one changed block no longer sweeps and re-lays it; its pin is in `Pins.SOURCE`; **Bree is sited at 1024, -256 and raises itself from its cut (22 November)**, and its hall is still to come | `TownHalls`, `PinCommands`, `/tmpin hall`; see 17 November below |
| 11 | **The Carrock** - the shaped rock in the Anduin | cut, base plate trimmed, pinned at 8307, 187, -4165, **stood by the world on the first start**, bedded into the river with stone, and **seen standing on 21 November** - the owner signed it off | `Carrock`, `Pins.SOURCE`, `data/tolkienmobs/structure/carrock/`; see 17 November below |

## The fingerposts - built 21 November

**A post every thousand blocks and one at each junction.** `SignPosts` asks `RoadCommands.down` of every dry
section, turns each laid road into the line its sections' own ends and bends run along, and marks it every 1000
blocks measured along that line - so a road that switchbacks is not posted twice on one bend and unposted on the
next. Junctions are read from the network's own ends: three roads end on 4647, -3315 and two on 8229, -3654, and
the Gundabad road ends on the old forest road's line rather than on its end, so an end against an end and an end
against a line, both within 12 blocks, are found. The arms are turned on the ways each junction's roads leave it.

**The arms say what the blocks already know.** A town is down an arm when it lies within sixty degrees of it and
further than 64 blocks away - a post standing inside the village points at nothing - and it goes on the arm it
lies most nearly down and on no other, because a crossing's arms are different roads and a town down one of them
is not a town down the other. The nearest takes the first board and the next takes the second. The distance is the
block entity's own measure, straight line from the board, and the name is the one `Places.towns` already carries.

**A post is planted once**, and the plinth is the marker: a target with one already beside it is answered without
a block being touched. That is what lets the placer run on a clock (every 400 ticks), from `/tmsign place`, and in
any world without stacking posts, and a shoulder in a chunk nobody has made is not read at all, so a road posts
itself as the country around it loads.

**A post near a chunk border no longer waits for the column across it.** The verge is read outward from the
target in a ring, and a column in a chunk nobody has made was passed over - but the whole target was held back
with it, so a post whose ring reached over a border answered the same nothing every twenty seconds however long
the road had been walked. The unread column is skipped now and the nearest loaded one is planted; only a target
whose whole ring is in unmade country is left for a later pass. **Not seen in a world.**

**A post no longer stands beside itself.** The plinth is the marker, and the standing check read a couple of blocks
either way from one column's height - but a column holding a post answers with the top of that post, five courses above
its own plinth, and the ring a post is stood in reaches up to `CLEAR` 12 blocks from the mark. Read that way the post
just planted was invisible from the next sweep on, and the clock stood another beside it every twenty seconds at the
next free verge: `Progress Check 4` grew three posts at 4771, 4776 and 4781, -3068, on a shoulder with no crossing on
it. The check now walks the whole ring the post would be given, reading every column from its own height and a whole
post down to its foot. **Not seen in a world.**

**Five faults came back from the world on 21 November**, and all five were the placer's rather than the boards'.
The Gundabad crossing stood **two posts ten blocks apart** - a thousand-block mark that fell beside the junction
- so a mark within 32 blocks of a junction is that junction's post now, and a join found twice is folded into one.
The arms hung **at the bottom**, on the lowest shaft: they go beside the top course, flush under the cap, and step
down a course when a cell is already taken. **Rivendell stood on the Gundabad arm** because both lie west of the
join and a sixty-degree sector cannot tell two roads apart; a town now goes to the arm it lies most nearly down.
The **east and west arrows pointed in** - the blockstate turned the south-drawn model with a north-drawn model's
compass, so `facing=east` showed it facing west - and they point outward now on all twenty-four variants, the
lettering and the block's own shape with them. And the posts stood **on the road**: five blocks off a 3-wide line
and inside the road that joins it, because the verge was measured from the road's own kind. The verge is now the
nearest column clear of every line in the network by the band's half-width and three blocks more.

**A sixth came back the same day: the lettering stood off the board.** The double arm read `Hobbiton` and `Rivendell`
correctly, but half a board away from the plank, past its far end, and on one face only. A pose turned by `Axis.YP`
and a model turned by a blockstate's `y` do not turn the same way for the same number - the blockstates are the
chest's, where a south-drawn model at 90 faces west, and the pose at 90 faces east - so the lettering's yaw is
written as the negation of the model's, east 90 and west 270, and it starts at the shaft end again. **Seen and signed off in a world on
21 November.**

**Seen in a world on 21 November** as those five faults, all corrected the same day, and **signed off by the owner** once the lettering followed the arms. Three
limits are worth knowing: a road is posted only once it is down, no post stands on a bridge (the dry sections either
side are joined across it), and a third town down one arm's road is left off, since an arm carries two boards and
no more.

## The switchbacks - surveyed 21 November, closed unexercised 22 November

**No leg of the network climbs more than half its own run.** `RoadPlan` breaks a leg into switchbacks when its
two marks stand more than `LAYABLE_GRADE` 0.5 apart per block - 160 blocks over the 320 between marks - and the
drawing does not do that anywhere on the eleven roads. Every 320-block leg of all eleven was read from the game's
own masks - `arda/height.png` raised toward the 318 crown by `arda/mountains.png`, then the crags `ragged` adds on
42 blocks of rock - which is the ground `ArdaMap.sample` answers the plan with, less the pass's own carve and the
vale blend: 47 legs, and the steepest is **0.23**.

**The leg most likely to need them** is the old forest road east of Glóin's camp, the climb from **6893, -3638**
(ground 220) to **7215, -3642** (ground 293), 73 blocks over 322 - two blocks in nine. Nothing is expected of the
plan there: one mark at the 320-block beat each side, no traverse marks and no hairpins, and the pass lays the
climb at its usual half-block a station. Next steepest are the Gundabad road's climb over the hold's ridge - 6252,
-6062 (ground 254) to 6473, -5830 (308), 0.17 - and its descent off the same ridge, 7832, -4405 to 8053, -4173,
0.22.

**Read rather than seen.** The masks were calibrated against two known heights - the Carrock, mask 188 and world
187, and the Shire crossing, 106 and 97 - and the saved world's plan store is empty (a 30-byte `road_plans.dat`),
so the marks the world actually holds could not be read back. The walk is still worth making because the plan's
own line wanders up to 32 blocks either side of the pin-to-pin chord, and a leg that runs up a crag is the one
thing that could put a climb near the trigger: a switchback at that climb means the line is steeper than the
drawing's chord and should be written down, and no switchback means the placer's guard is what the walk confirmed.

**The wall that makes them fire** is the Gundabad pass's own: away from the corridor floor the ground rises
`PASS_STEP` 2.5 blocks per block, and a leg pinned across it is broken into traverses at once. **The test leg** is
**5610, -6790 to 5706, -6790** - 96 blocks east, the ground 185 under the first pin and 311 under the second, so the
climb is **126 over 96**, 1.31 a block, two and a half times the trigger. It cuts into **five traverses 47 wide**, and
every leg between them climbs at 0.45 or less: the road works north and south of the pin line by 47 blocks on its way
east, some 390 blocks of road for the climb, with four hairpins at the marks - **5629, -6743** (184), **5648, -6837**
(218), **5668, -6743** (261) and **5687, -6837** (299).

**To fire it without laying anything**, stand on the east pin and plan to the west one:

    /tmroad plan 5610 -6790        (standing at 5706, -6790)

`plan` names one end and takes the block underfoot for the other, and lays nothing. Chat answers "1 section at 3 wide,
planned, nothing laid"; the bend count is in the log, one line a section (`logs/latest.log`):

    Road plan <id>: <id>, 5706, -6790 to 5610, -6790, 4 bends

**Four bends means the branch fired.** Zero means it did not, and the leg wants a steeper pin - 5610, -6810 to 5706,
-6810 climbs 119 over 96 and takes the same five traverses. To see the hairpins on the ground instead, `/tmroad pin
5610 -6790 5706 -6790` names both ends and lays at once: a real road of about 390 blocks in that world, which `/tmroad
forget` unplans but does not unbuild.

**A leg shorter than `LANE` 4 - 64 blocks - never reaches the switchbacks:** `chain` gives up on the span, so no marks
are stored and the road is laid straight. 64 is the floor. At exactly 64 the same wall gives three traverses 91 wide,
a hairpin 182 blocks apart for 64 of advance, which is a stranger road than the 96-block leg for no less walking.

**These heights are the world's, not the drawing's:** every figure above is the top block of the saved world
`Progress Check 1`, read with `tools/world_height.py`, which stands a block or two above the ground the plan samples.
The bend count is what to check rather than the width - the width moves out in half again (47 to 70) if a traverse
comes out steeper, and five traverses is what the arithmetic gives for any climb between 107 and 152 blocks over 96.
**Not seen in a game:** no leg has been planned across that wall yet, so the branch is unexercised by a walk.

**Closed 22 November, on the owner's call.** No leg of the eleven roads reaches the trigger, so the only leg that
would put the branch through its paces is this test one across the pass wall, and the owner has taken the survey
and the arithmetic for it and left the branch unexercised rather than walk it. That is a limit worth carrying:
`switchbacks` and `marks` have never run against real ground, and the log line above - four bends on that leg -
is the signature to compare against the first time a road does climb more than half its run.

## The keep's doorway - answered in a world, 20-21 November

The question this section used to carry was whether the hold's own doorway stood in the gate's passage. **It did not.**
Read out of the template itself: the south wall carries four five-wide, seven-tall openings at local x 13..17, 43..47,
74..78 and 104..108, and behind them the wall is two courses deep over most of the passage's twenty-nine columns and
**twelve deep at local 58..63**, where an inner mass stands. The plan was written around the far opening (104..108),
which at `KEEP_X` 5516 is world 5620..5624 - twenty-nine blocks east of the passage's last column, 5591 - so the
chamber ended at wall over twenty-six of its columns, and the neighbouring openings only clipped its two edges.

**The fix is the one the owner gave:** the whole box moves **twelve east**, `KEEP_X` 5528, which puts the 43..47
opening at world **5571..5575** - inside the chamber, four blocks west of its axis 5577 because the openings stand on
a thirty-block beat. `DOOR_X` follows it (106 to 45), so `clearMouth` dresses the ground in front of the door the hold
is actually entered by. And the gate **no longer cuts a passage through that wall**: `extendHall` used to cut a hole
the passage's own width, fourteen courses deep, on every start - which on a first start the template then wrote over,
and on any later start took the doorway with it. The passage now stops at the wall, and the door is the template's own.

**The box still fits the crown** (x 5496..5656): 5528..5649 leaves seven blocks to the east. `KEEP_FLOOR` 176 is
unchanged, the rooms' floor 189 and the doorway's floor 189, level with the passage. **A world that already holds the
hold keeps it:** `ArdaMap.keepOlderPlans()` names the 5516 corner at 176 and at the 187 the plan used between 15 and 18
November, and a hold found on either is left standing with only its mouth dressed - the shift is the making of a world
after 21 November, not a migration. **Seen 21 November:** the passage clear from the gate's hall to the template's own
doorway, with the door open at the end of the walk - the thirteen blocks that stopped it are the paragraph above.

## 19 November 2026 - a road waits for its build, and the visited builds have marks

Two changes, both compiled with `./gradlew compileJava -x createMinecraftArtifacts -q --no-build-cache` (exit 0) and
neither seen in a game.

- **A road no longer begins under a build that is still going up.** The ground begins every road the world is given - a
  chunk the road passes through starts it, and so does a player within 32 blocks - and a city is raised band by band with
  its ring dressed after the last band, so the road out of Rivendell graded country the dressing then changed under it.
  That is Road Test 7's gap: the strip from the first bridge to the road's own end, with the road's own note saying it was
  down so a second laying owed nothing. `Roads.BUILDS` now names the build each of the valley's three roads leaves and
  `Roads.waits` asks `TownHalls.waits` (the same answer a town's hall waits on); `RoadCorridor.mayBegin` holds the road
  until the build is finished and the ground asks again a second later. Only a road that names its build is held.
  **Still owed:** a game check - the road out of the valley should begin by itself once the city is whole, and the strip
  already missing should come back after `/tmroad forget` at the gap and one visit. If the pass refuses it, the chat says
  which leg and why; that sentence is the next thing to read.
- **The visited builds are marked, and one answer feeds `/tmtp`, the needle and the boards.** `Marks` holds the owner's
  own fifteen blocks - Rivendell, Hobbiton, Thorenhad/Elladan's camp, Barachen's fire, the wood ruin, the three crags and
  Matuna's lair, Glóin's camp, the goblin fires and Gundabad - keyed as the commands ask, and `/tmtp` answers a name with
  its mark before the place or the pin. `Waypoint` reads the same marks for a site's page, a named place and the step after
  it; a body the campaign stands keeps its own block. A mark is not a pin, so no build is seated anywhere new. **Gundabad
  is a town** in `Places.TOWNS` now (`<- town` on the owner's list), and a town with no place yet is stood on its mark, so
  a board's distance and the banner's circle are measured from the block a player can stand on. **Still owed:** one in-game
  look at a jump per name, one page's needle, and a board's town list with Gundabad on it.

## 18 November 2026 - the roads are planned, not walked

Six commits on `main`, none pushed: `f1c7497e` the planner and the erased network, `408426c7` a crossing it will not
bridge refusing and its banks taken from the run's ends, `28421690` the width read from the ground, `eb308473` the plan
kept in the save and a failed leg laying nothing, `cbab2367` both ends named and refusals said in the chat, `63e3eeb0` the
line read a block at a time so a river is bridged. Every one compiled with
`./gradlew compileJava -x createMinecraftArtifacts -q`. **One road has been laid in a world, and it was laid wrong** - see
*Still open from tonight*.

- **The walked network is erased.** `Roads.ROUTES` is empty, and with it the bend table, the kinds, the cuttings, the banded
  list and the build names: the valley's road and its crossing, the Thorenhad road, Elladan's branch, Barachen's camp, the
  wood ruin and its link, the mountain way, the ford way, the three cave roads, the high pass road with its bridge and its
  climb, Glóin's road with its stream crossing and the road east, and the old forest road with both of its fords. The
  builds are all still standing, so the network comes back one place at a time. **Signed off 18 November:** the owner
  compared all twenty-one walked routes against the seven roads that replaced them that night and found no destination missing -
  the network came back as fewer, longer sections over the same ground, which is the whole of what the planner was for.
- **A road is now two ends and a plan.** `RoadPlan` walks the drawing - `ArdaMap.sample`, sixteen blocks a lane, ±192 wide -
  and prices grade over 0.35, turn, and river depth. No chunk is read or generated to plan one, and it leaves a bend every
  320 blocks so the pass's own search only has to sit each leg on the ground. Water on the chosen line becomes a
  `Route.Crossing` with its banks from the drawn ground and its deck two over the drawn water. **A leg too steep to
  climb is handed over as switchbacks:** the pass settles its surface on half a block a station, so nothing steeper than
  one in two can be built at all, and the plan now tests every leg against that and breaks a steeper one into traverses
  across the fall, each mark standing off the line far enough that the leg it carries holds its climb. Marks only - the
  pass's own grade and hairpins are untouched, and a leg that was walkable plans exactly the marks it did.
- **Three commands, one road.** `/tmroad plan <x> <z> [kind]` reads the country and says the sections, laying nothing;
  `/tmroad build <x> <z> [kind]` lays them from the block underfoot; `/tmroad pin <x> <z> <x> <z> [kind]` lays them from
  two named ends, so a leg is pinned by the ground it crosses and the same command answers the same road from anywhere -
  and two ends on one block are refused by name. The world steps each pass while the ground under its head is loaded, so a
  pass whose head is not loaded waits where it stands and the ground begins it again. **The width is
  automatic:** start on a town's ground (Hobbiton or Rivendell, or the approach out of either) and the road takes the bands -
  7 paved inside 32 blocks, 5 through 96, 3 beyond - and anywhere else it is a wild track 3 wide. The word after the
  coordinates overrides that: `wild` 3, `wide` 5, `main` 7 of country surface, `urban` 7 paved everywhere, `town` the bands
  wherever it starts. The worked example, from Glóin's camp's road end at 8231, -3641: `/tmroad plan 8539 -3668` then
  `/tmroad pin 8231 -3641 8539 -3668` - about three hundred blocks of wild track with one span, and the log has to name a
  bank near the old 8520/8481 pair. That is the leg whose first laying went under the Ford, so it is the one to try again
  after `RoadPlan.dense`.
- **A crossing it will not bridge lays nothing.** Water wider than 96 columns, no bank within 96 blocks, or a run that turns
  back on itself refuses the whole road and says why - in the command's answer, and in the log for a road the world asked
  for. The span's banks are taken from the two ends of the river run (back from where the road meets water, on from where it
  leaves it), not from the run's middle.
- **A leg the search cannot walk lays nothing.** No straight fallback survives: the whole road is given up, nothing is
  graded, no note is written saying it is down (so it can be planned again over better ground), and the leg, its two ends
  and the reason go to the log and - within 128 blocks of the road - to the chat. `Descent.why` tells the two faults apart:
  a leg longer than one window of the search wants another mark in it, ground with no way through it is country the road
  cannot cross at all. A pass whose step throws was swallowed by the tick that carries it; that is a warning in the log
  now.
- **A planned road survives a restart.** The plan is kept in `road_plans` beside the road notes - its sections, their
  kinds, their spans and their bends - and read back at server start into the tables the pass asks, after the previous
  world's planned roads have been taken back out. A road laid in an earlier session is still there to find with
  `/tmroad forget` and to lay again, and the start writes `Roads: N planned sections read back from the save` when it does.

- **The raise stopped halfway, and the fix is signed off in game.** A band's far end is ground no standing player loads, and the raise
  would not move on until every piece of the band was down, so it laid what it could see and waited in silence for ever -
  which is what "Rivendell only goes about half way" was. The numbers, out of Road Test 3's own save: band 0 is 132 pieces,
  23 of them inside a ten-chunk view of the corner the player was teleported to, the median 341 blocks out, the furthest
  624, and 71 never inside a view of anywhere they stood. After five empty passes the raise now makes the ground for the
  nearest waiting piece and lays it, one piece a pass, and logs one line per band saying what it is waiting on. **Road
  Test 7, a fresh world and no commands:** `raised from the pin` at 09:44:12, and every band went down with `/tmcity band`
  never run - 09:46:13, 09:47:54, 09:49:27, 09:50:46, 09:51:45 and `the whole city is down` at 09:52:26 - each asking
  for made ground exactly once, which is the ceiling kept.

- **The world no longer builds a city; only the raise does - both halves signed off.** Two changes on top of the stalled-band fix:
  a structure start is now probed for the cut's own blocks (a dozen interior samples of the piece the city is planted on, a
  majority enough) before the raise, `/tmcity summon`, the band hand or the dresser treats a city as standing; and no
  structure set is emitted for an assembly, so worldgen never places one. A world made before this keeps its start - it is
  ignored rather than removed, and the raise plants the city from the pin over the top. **Road Test 6, fresh, no commands:**
  `tolkienmobs:rivendell` appears in no chunk of the `arda` region files, so the worldgen half is settled. **The raise half
  is settled by Road Test 7** - a fresh world with no commands in it, the city planted from the pin and stood band by band,
  so the raise is the only thing building one (what went wrong before is the band-turn fix below).

- **A band turn threw away the band it turned to, and the raise finished a city with one floor in it - signed off in
  game.** Road Test 6's log: band 1 of 6 down, then bands 2, 3, 4, 5 and `the whole city is down` all inside **720 ms**,
  and the save has stone at the pin's column at 75 and 80 with air at 123 - the floor and nothing over it. The turn moved
  the marker and set `raisingBand` to the band just written while clearing `raisingOrder`, so the next pass believed the
  new band's order was already built, and an empty order is every piece down. The turn now leaves `raisingBand = -1`, so
  the pass that reads the marker builds that band's order and lays it; a band that closes with nothing in hand warns by
  name instead of passing quietly. **Road Test 7, fresh, no commands, the owner teleported to 5200, -3033 and left
  standing:** one `out of made ground` line per band and no warning line, the bands closing sixty to ninety seconds apart
  instead of all at once, `the whole city is down`, and the dresser's `19643 columns cut down, 35386 filled up, 320 left
  for the water` - and the owner found the city standing whole, which is the check that matters, since both earlier runs
  said "finished" too. **The valley's folk are the world's own, and that half is closed:** the seventeen `Elf suffocated
  in a wall` lines Road Test 6's floor was laid over and the eighteen in Road Test 7's are natural spawns the valley's
  own spawning walks into the footprint as it goes down - the raise itself stands no Elf anywhere. Every body the pack
  does stand is seated on ground looked for first: `Campaign`'s Glorfindel and Gimli by `floorNear`, `Sites`' keepers by
  `room`, `QuestGivers`' campaign givers by `groundNear` with twelve tries - so nothing here has to wait for a band, and
  no fix is owed.

### Still open from the road tests (18-19 November)

**Signed off in a world, 21 November.** The owner walked the network in `Work Verification` and calls it fixed: the
roads laid, the crossings stood bank to bank and no section doubled back over a channel. What follows is the record of
what those checks were, kept because its numbers are what a re-cut is judged against. **The climb is planned around
now** - a leg steeper than the grade can lay is handed over as switchbacks rather than as one impossible leg, see *A
road is now two ends and a plan* - but **it is still unmeasured in a world**, as is `road_plans.dat`, which has never
been written and read back.

- **The bridge has not been seen.** The first road laid in a world went under a river more than four blocks deep with no
  span over it: the plan was cut into sections from water read at the sixteen-block steps, and the channel fell between two
  dry samples. `RoadPlan.dense` now reads every block of the line, water being a column the drawing gives a bed below its
  own surface. **The check:** `/tmroad forget` on the bad road, re-pin the leg with fresh ends, and look for more than one
  section in the chat and a `Road plan <id>: span …, deck …` line in the log. If it still comes out as one section, the
  water test itself is wrong for that channel - `sample.river()` false where the world has water - and `ArdaMap.column` is
  the next thing to read.
- **A crossing was read down the river instead of across it, and that fix is unrun too.** The test world's last two pins
  (18 November, 04:15 and 04:18) were both refused on the mountains' channel - `the water at 6422, -3616 is 112 columns
  wide` and `no dry ground within 96 blocks of 6471, -3645` - and the drawn channel there is only forty to fifty-six
  columns across, so neither refusal was about the water. The span's axis was taken from the run's own two ends, which
  are a few blocks apart, and a road crossing a north-south channel almost square rounds to a north-south run: the banks
  were then searched *up* the channel, into the wide sheet that opens north of it (536 columns at z -3560), instead of
  across it. `RoadPlan.cut` reads both axes now and `pick` takes the narrower standing reading. **The check:** the same
  two pins again - `6398 -3654` to `6481 -3655` is the short one on the screenshots - and the chat should say `2 sections`
  with a span of about forty columns rather than nothing at all. If it still refuses, the next thing to read is `dry()`
  itself: the reading at 6471, -3645 that walks west across the channel should find the west bank at about 6420.
- **The crossing fix works, and the deck it planned was missing - fixed in `98e397cd`, unrun.** `pin 6398 -3654 8229
  -3654` planned and laid three sections, and the log's span is `6668, -3799 to 6668, -3748, deck 195`: fifty-one
  columns, the ford's own size, so nothing about the width was wrong. The pass stood the two abutments and no middles.
  Read out of the save at x 6668, the oak ends sit at z -3807..-3799 and -3748..-3740 and everything between them is
  water at 188-192, with the road's paving stopping at each abutment: `bridgePlan` walked the deck from the near bank
  towards the low coordinate only, so a span running the other way (bank at -3799, far bank at -3748, southwards) ran
  out at its first step. The gap is read from both ends now and the middles laid in the span's own direction, turned
  about for the other heading. **The check:** `/tmroad forget` on this road, then the same pin again - the sweep clears
  the two old ends, so the span is stood from scratch - and the deck should be planks and pillars from bank to bank,
  with `BRIDGE done in 10 steps` instead of 3 (two ends and seven middles for 51 columns). If the deck comes out
  but the pieces sit off the band or a piece short, the rotation the other way is what to look at:
  `Rotation.CLOCKWISE_90` for a southward span's middles and `CLOCKWISE_180` for an eastward one.
- **The ground already laid under water stays.** Forgetting a road clears its notes, not its blocks; the causeway has to be
  filled in by hand or the new road laid beside it.
- **No restart has been tested at all.** `road_plans.dat` has never been written and read back in a world. The check is one
  start: the log line, then `/tmroad forget` while standing on a road laid before the stop, then the command again.
- **A road planned twice stood twice - fixed, unrun.** The old forest road to the far side of the river east of Mirkwood
  was planned at 11818, -3801 and again at 11824, -3851; both plans were live after the restart and both laid at once,
  seven sections each (the log at 04:52 names both and at 04:53 shows both stepping "since the pass was last picked up").
  Two causes: `/tmroad forget` cleared the note of the section at the hand only, leaving the other six to be resumed, and
  a restored plan is in `ROUTES`, so `RoadCorridor` began it on a chunk load. Now `/tmroad forget` drops the whole road
  (every section whose id begins with it, from the passes, the list and `road_plans`) and a planned section with no note
  is never begun by the world - it waits for a command, while a road begun and unfinished still resumes. **The check on
  the existing mess:** stand near the old road and forget twice, once per road - each reply names the road and says how
  many sections went - then delete the regions and pin once;   nothing else should lay. Then restart and confirm no planned
  road begins by itself before a command is given.
- **A channel was crossed twice, and the walk will not dip in and out again - fixed in `ee907452`, unrun.** The same road's single clean plan
  (one plan, one pass per section, 05:07) still held two spans over one channel: `bridge_1` 11759, -3882 to 11710, -3882
  and `bridge_2` 11739, -3908 to 11685, -3908, joined by `_2` running *east* from 11694, -3882 to 11755, -3908 - a
  hairpin, and what looked like two roads and two bridges were the plan's own doubled crossing (the save has decks at
  z -3885..-3879 and z -3911..-3905, no third plan). The lane walk priced water per sixteen-block step, so a dip into the
  channel's edge and back out was cheap. It now keeps the count of dry lanes since the water in its own state and prices
  water met inside `DRY_AFTER_CROSSING` (4 lanes) at `CROSS_AGAIN` on top of the toll. **The check:** `/tmroad forget`,
  then the same build again - the plan should name *one* span over that channel instead of two, and no section should
  run east between two spans. The old decks stay standing: the sweep only clears timber inside a span's own box, so
  clear the ground at 11680..11760, -3920..-3870 before judging what the new one looks like. A port of the walk over a
  channel with an island in it takes one run under the new rule and two under the old; the drawing itself has not been
  re-planned. **And the probe's own doubled crossing is fixed, off the drawing only, 19 November.** `6300, -3580` to
  `6800, -3580` planned a 45-wide span at 6418, -3612, a section running back west to 6383, -3585 and a 56-wide span at
  6399, -3585 over the same water. A trace of the walk showed why: its readings are sixteen blocks apart, and between two
  of them it stepped two lanes onto the one dry block at 6441, -3580, on the channel's ragged edge, and back into the
  lane beside it. Both readings were dry and nine dry blocks apart, so neither `DRY_AFTER_CROSSING` nor the bank margin
  could see the water, while the staircase the plan is really read along went through it twice - and because a span's
  banks are read along one row, the first span ran on past the block the line left the water at, so the next section
  began beyond it and ran back. A plan now records that its line leaves water and meets water again inside
  `DRY_AFTER_CROSSING` lanes, and `RoadPlan.of` walks that leg a second time with a lane change refused on every step
  that leaves water: a straight crossing, on the lane the road reached the bank on. The first walk is untouched, so a
  leg whose line was sound plans the same line it did. **Probed:** that leg is three sections with one 45-wide span at
  6424, -3627 (deck 188) and no section running back, and every other leg probed - the roads in `Roads.NETWORK`, the 6398, -3654 pin and the 6420, -3560 bank leg - are byte for byte what they were, so no road already
  laid moves. **Not run in a world:** this is a planning change and nothing has laid it; the next fresh world is where
  the one-span crossing shows.
- **The laid network is in the code, and the test world is no longer the only place it exists.** `Roads.NETWORK` names
  eleven roads, each as the two ends it was pinned between and at the three-wide track, planned from the drawing when
  the class loads: Gundabad's road (5578, -6769 to 8583, -3617), the old forest road east of Mirkwood to Glóin's camp
  (11813, -3901 to 8229, -3654, five sections with spans at 11649, -4080 and 8551, -3626), the four legs of the vale
  and the high pass (4515, -2446 to 4705, -3169; 4528, -2541 to 4624, -2480; 4776, -3063 to 4684, -3050; 4819, -2887
  to 4647, -3315), the wood ruin's nine-block connector (4787, -3059 to 4778, -3059), Rivendell to the meeting plaza
  and the plaza on to Glóin's camp (5105, -3413 to 5605, -3622; 5606, -3622 to 8229, -3654), Rivendell to Elladan's
  camp (5200, -3033 to 4647, -3315) and the short leg at Rivendell's south gate (5111, -3399 to 5072, -3221), with the
  ids their pins were given, so the geometry in a fresh world is the geometry in the test world. `Roads.planned` is
  what tells a session plan from a named road now; a named road is begun by any chunk of its ground that loads, as the
  walked network was. **What to check on the fresh world:** the start log should carry a `Road plan <id>: …` line for
  each section before the world is entered, and the roads should lay themselves as the ground comes in - two bridges
  on the old forest road and none on the high pass legs. That the roads cover the same destinations as the twenty-one
  walked routes is not a guess: the owner read the two lists against each other and signed it off, so nothing from the
  old network is still owed.
- **Road Test 2 laid nothing, and the reason is where the roads are.** The fresh world was entered at Rivendell
  (raised at 5200..5753 x, -3370..-2859 z) and left there: the old forest road runs 8229, -3654 to 11813, -3901 and the
  high pass legs sit at 4515..4819 x, so the nearest road line to the city's west edge is 380 blocks away, and the
  corridor begins a road only from a chunk of its own recorded line. Nothing refused and nothing errored - the logs had
  no road line in them beyond `Roads: 11 planned sections read back from the save`, and that line was the old world's.
  The save's ground agrees: besides Rivendell's own bands, only a narrow strip at x ~4790 for z -2880..-3312 has real
  chunks, the rest of the arda map along the roads has never been generated, and the old forest road has no region file
  at all past its west end. Two lines of instrumentation were added so the next start cannot be ambiguous: the start
  says `Roads: <n> sections in hand, <m> read back from the save`, and each pass says `Road <id>: begun from the ground
  at <x>, <z>`. **The rule is now the player as well as the chunk:** once a second `RoadCorridor.look` begins every road
  a player stands within 32 blocks of the line of, from where they are, so a road no longer waits for a chunk that has
  already loaded once; a road the ground refused is kept refused for the run (`RoadCommands.REFUSED`) rather than
  re-searched every second, and forgetting it clears that. **What to check:** launch, go to 4819, -2887 (Barachen's camp,
  the one place the ground is generated that G's line touches) or anywhere within 32 blocks of a leg's line and read the
  log - a `begun from the ground` line means the corridor found it and the road is laying, or refused with a reason in
  the chat within 128 blocks. Nothing at all in the log within 32 blocks of a road means neither path is seeing it.
- **A leg that runs beside a river stepped inland instead of refusing - fixed and planned offline, 19 November.**
  Reading the water a block at a time meant a leg along a bank was measured at the pool its own entry stood in: planning
  6420, -3560 to 6600, -3560, both ends inside the mountains' wide bend, refused with `the water at 6420, -3560 is 110
  columns wide, past the 96 this will bridge`. A refused leg is now walked a second time with the bank priced - a dry
  lane within `BANK_MARGIN` (3 lanes, 48 blocks) of drawn water costs `BANK_COST` (400) a step, so the line steps inland
  along the bank - and water at a leg's own two ends is the bank rather than a crossing, so a pin in the shallows begins
  on the first dry block of its own line. Only the second walk pays for the margin, so every leg that planned before
  plans the same line now. **Planned against the drawing with `tools/roadprobe`, no world:** that leg now plans one
  section, 6436, -3576 to 6589, -3560 - the pin in the water, the road beginning sixteen blocks inland of it; the
  signed-off geometry is unchanged, `11813, -3901` to `8229, -3654` still spanning at 11649, -4080 (52 wide, deck 170)
  and 8551, -3626 (46, deck 188), and `6398, -3654` to `8229, -3654` still spanning 52 at 6668, deck 195, which are the
  numbers the test world laid; a line that is water from end to end still refuses. **Not run in a world:** nothing has
  laid one of these roads, so what the pass makes of a section that begins on a bank rather than at the pin is
  unverified. **The instrument:** `tools/roadprobe` plans a leg against `ArdaMap` alone - no chunk, no world, no client -
  with `./gradlew -I tools/roadprobe.gradle roadProbe -PprobeArgs="leg WILD <x1> <z1> <x2> <z2>"`, and `sample`/`along`
  print what the drawing gives a column or a line of them. `marks` plans the same leg and prints the block of every
  bend it recorded, which is what a laid road's own `approach` in `roads.dat` is compared against.
- **`plan` is a look, not a plan.** Nothing is written to `road_plans` by it, so a road surveyed before a restart has to be
  surveyed again.
- **Untimed.** The planner reads one sample per lane per step and now every block of the chosen line; nothing has measured
  a long leg on a live server, and a four-thousand-block pin is the one to try.

## 17 November 2026 - the halls, the marks' switch, and the south edge

Four commits on `main`, none pushed: `067ae6cd`, `a0e8b645`, `56fba972`, `4b66b978`. Every one of them compiled
(`./gradlew compileJava -x createMinecraftArtifacts`); only the halls have been seen in a world.

**The mustering halls are pinned in their towns and raise themselves.** The three `faction_hall_*` worldgen definitions
are gone, with the twelve generated files (`structure`, `structure_set`, `template_pool`, `has_structure`), because one
hall per alliance for a dozen towns to come stood in a field before the town that owns it existed; the templates stay in
`data/tolkienmobs/structure/`. `/tmpin hall add|remove <town>` pins one where the caller stands - key `hall:<town>`,
template from the town's alliance (`tolkienmobs:faction_hall_west` for all three towns so far), towns suggested from
`Places.towns()`: hobbiton, bree, rivendell - and `TownHalls.raise` stands it: ground course in the block the builder
stood on, the box's chunks asked for, the template's own blocks as the guard against laying a second. It waits for its
town, because a town's pass lays, clears and dresses the ground a hall stood early would be taken away with:
`TownHalls.waits` asks `Hobbiton.complete` (the village's note) and `CityCommands.complete` (the valley's marker at its
last band). Three hands raise it - the end of the village's own pass, before its folk are stood; the next start in a
world whose town already stands; and the pin command itself, which says in its reply whether the hall is standing or
waiting. **Seen in game:** Hobbiton's hall stood at the pin in the owner's `Progress Check 1` world and he called it
perfect - the seat, the height and the front as the template was cut are all right, so it stays unturned. The pin it was
taken at is now the permanent one: **-529, 101, -74**, read back out of that world's `pins.dat` and written into
`Pins.SOURCE` under `TownHalls.key("hobbiton")`, so every world gets the Shire's hall without it being pinned again.

**The marks the world draws answer to one switch, which now ships on.** `questBadge` gates both the scroll over a
turn-in giver's head and `BoardMarks`' green mark over the notice boards, and defaults `true` where it defaulted
`false`; a plate's own mark is never touched by it, so a player who clears the world of marks still reads what is in
front of him. An existing config file that says `false` keeps its answer. **Seen in game:** the owner looked at the
marks and the icons are perfect - nine of them, the inks and the shapes both - so nothing about the art, the font, the
sizes or the seat moves from here; the badge's place over a head and the board mark's lift drew no complaint either and
are kept as they are.

**The Carrock - cut, trimmed and stood by the world.** The rock the owner shaped in the Anduin, boxed by
four ammolite markers in the `Arda` save's overworld (all at y 36 in the river: x 16694..16735, z 5627..5711), is cut to
`data/tolkienmobs/structure/carrock/`: three 48-cube pieces and a `carrock.parts` table, **42 x 57 x 85**, air, the
river's water and the markers all dropped. Seen standing at the pin, the first cut also carried the ground it was boxed
through - a plate six blocks deep or less, coming out of the water at the south end as **three tongues of bare dirt** -
so every column whose highest block stood at or under that plate's top (y 5) was cut away: 2,860 blocks in 1,189
columns, nothing above them touched, the rock's foot left whole, and the spruce's fourteen hanging leaf columns kept.
The pieces now hold **36,079 blocks in ten kinds**. `Carrock` holds the one name (`carrock`), the size and the anchor -
**its low north-west corner** - and `Pins.SOURCE` now carries that corner for this world: **8307, 187, -4165**, the block
`/tmcut place carrock 8307 187 -4165` is given. **It stands itself now:** `Carrock.autospawn` runs in `MilestoneHandler`
with the gate, the keep and the cave, reads that pin, lays the three pieces from it and writes a note beside the cut in
the save's folder - dimension, corner and the table's fingerprint - so every later start leaves it alone. A world whose
note is missing or whose pin has moved is laid again rather than cut about, since the pieces carry no air. **And it is
bedded into the river:** `Carrock.seat` reads down every column that carries one of the build's own blocks on its base
course, counts the water under it, and fills that water with plain stone only when the read ends on ground - a column
already standing on stone, or hanging over a cave or open air, is left alone. It runs with the autospawn and on every
start after it, so a world that has held the rock since before there was a bed gets one next time it comes up. **Open:** the
rock has not been seen standing from this pass - the first start after this ships is the check, and the pin's
`_stands.txt` beside `cuts/carrock/` is where that start leaves its word.

**The Shire's south cliff.** The drape never cut outside a deck, and a village at the foot of its own country read that
rule as an eleven-block cliff along its south edge (`Progress Check 1`, x -430..-438, z -45: village ground 97-98,
country 108-109). `Dressing.BANK_BLOCKS` (3) now cuts a bank that stands three blocks or more over the build's own
floor, to the same join every other column gets, while the shallow grooves round a city's open faces stay as they were.
`Hobbiton.DRESS_VERSION` is 2, so the village re-raises and re-dresses on the next start. The full note is in *The Shire
map* below.

### Still open from today

- **Bree's hall.** `bree` is a town in the catalogue with no build of its own, so `waits` has nothing to wait for and a
  hall pinned there stands straight away - and would be laid over whenever Bree is built. Give Bree a build and a
  completion answer before pinning one there.
- **The south edge and the joins are seen, 21 November.** The owner walked the village in `Work Verification` - raised
  on the world's start, before the portal was used - and called it right: the cliff at x -430..-438, z -45, the
  rerouted river, the two joins and the drape reading a wooded cut all stand in a world made after the mask edit.
  Nothing from the Shire is owed.

## The Shire map - cut 15 November, placed 16 November 2026

**Where it stands.** The build is cut and in a test world at pin **(-700, 87, -512)**; `place`, `clear` and `drape`
have all three been run on it, and since 20 November every world raises it on its first start with no command typed.
The river is now **rerouted round the west of it, a branch drawn out of its south mouth, and the two brought
together in a Y south of the village** (16 November - see below); the mask change needs a world made
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

**The south edge's cliff - 17 November, fixed but unseen.** Read out of the owner's `Progress Check 1` world rather than
seen: at x -430..-438 the last row of the footprint (z -45) has the village's own ground at 97-98 and the drawn country
at 108-109 on the other side, so the boundary is an eleven-block raw face - the wall photographed from (-504, 101, -46).
It is the deck rule read the other way round: the drape never cuts outside a deck because cutting round a face the build
leaves open trenches the country, and a village that sits *below* its own country gets a cliff from the same rule.
`Dressing.BANK_BLOCKS` (3) now separates the two - a column standing three blocks or more over the build's own floor is
dressed like every other column, cut to the join at the wall and eased back over the reach, while a groove of a block or
two round a city's open faces is left alone, and nothing at or below the floor is cut. `Hobbiton.DRESS_VERSION` is 2, so
the next start re-raises and re-dresses the village; **nothing has seen the new rule run**, and the village's own walls
(stone-brick faces at y 114 over paving at 100, x -518..-525) are build, not ground, and are untouched by it.

**21 November:** the village was walked whole and this was not called out again; kept as the record of what the
drape does inside a footprint rather than as work owed.

**What has been checked, and what has not.** Every step compiled (`./gradlew compileJava -x createMinecraftArtifacts`)
and the mask work was measured against the game's own 30% water threshold. None of that round had been run in a world
when it was written. **21 November it was:** the owner walked `Work Verification`, the village raised on the world's
start before the portal was used, and found it right - joins, south edge and wooded drape with it.

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
it is one command away from being repeated.

**The switch is on by default now, 20 November.** The portal out of the overworld opens on the village's own crossing,
(-470, 97, -123), which is inside the footprint the pass clears - x -700..-149 by z -512..-44 - so a village raised by
an arrival is a village that takes the traveller's doorway with it: the pass begins on the first tick after the crossing
is used and the ammolite doorway is gone about ten seconds later. Raised at the start it stands about a minute into a
fresh world, before anybody can walk through to it. **And a start is exactly when a server is empty**, so the pass lifts
the server's own `pause-when-empty-seconds` while it runs and puts it back when the village stands: measured on a fresh
level with the property at 5 s and nobody online, the log reads `Server empty for 5 seconds, pausing` part way through
and the pass never takes another step - the paused branch of `MinecraftServer.tickServer` returns before the tick event
and before the level's own tick, so there is nothing for the pass to be woken by. The lift is
`DedicatedServer.setPauseWhenEmptySeconds(0)` for the pass's length, given back on the pass finishing, on an unreadable
cut and on the server stopping with the pass half done, so `server.properties` is written twice and ends as it began. A
server killed outright mid-pass keeps the zero, which costs it its idle pause and nothing else - as it keeps the level's
forced-chunk tickets until the next start re-runs the pass.

A world that sets `raiseShireOnStart = false` keeps the older rule: the village is raised only while somebody is within
`REACH` 192 of it, so the chunks are in a view, ticking and being finished as the player arrives. `/hobbiton raise`
still asks for it at any time in either world, and a world that already holds the village is left alone. **Not seen in a
client session**: whether the deck, the mouths and the ring look right is still a walk to Hobbiton, and 60 s is wall
clock on a warm build, not a bound on a cold one. The pass keeps its own tick count, because the measurement is the line
that says it stands.

**The two town roads are in the network, and a leg lays up to the town that is still going up.** The Shire to Bree
and Bree on to Rivendell are both in `Roads.NETWORK`, and `Progress Check 4` is why they could not simply be laid:
the Shire's stretch arriving at the village (`..._2`) was begun at 11:23 on 22 November while the village was still
rising and never reported laid, and the Bree leg's arrival was taken out with Bree's ring nineteen seconds after the
whole 4049-block road reported laid. Holding the whole leg until both towns had written their notes was the first
answer, and it is safe and slow: four thousand blocks of open country none of which the town would ever touch.
`Roads.TOWNS` now names the ground each town is met on, `Roads.hold` answers which end of a *section* arrives there
- a leg over water is a section per span and per dry stretch, and only the arriving one is graded on the town's
ground - and a pass that finds one end held lays from the other, stops `SHORT_OF_TOWN` 40 blocks clear of the town,
and waits at that edge for the town's note. The held stations are kept in the pass's own note, so a restart in the
middle of the wait resumes where it stopped, and the arrival is graded as a second profile from the height the road
stopped at to the town's finished ground. **Not seen in a world: neither leg has been walked with the partial hold,
in this world or a fresh one.**

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

One thing left, and cheap to change when it is next looked at: the arrival doorway is built of ammolite (`buildExit`), so
a doorway that is auto-built inside the Shire looks like the hold's rather than the village's. The second half of this
note is settled - the village is raised at the world's start now, so there is nothing to arrive ahead of.

## The Shire's milestone - built 16 November

The travel stone of the village stands at the paving the owner marked, (-503, 101, -314), in the village's north-east
where the paths meet: `ArdaMap.hobbitonMilestone()`, stood with the hold's stone on every start
(`MilestoneHandler.onServerStarted`). That mark is gravel rather than one of the mod's ammolite markers, so the stone is
tried in the spot and then on top of it, and the paving is not dug up for it. A world older than the stone gets it on its
next start, and one that has it keeps it.

## The river that did not drape - settled, not a fault

The owner pointed at (-265, 98, -72): stepped grass terraces running down into water on the village's north-east, straight
edges and a sand lip - country that was cleared and never settled. It is inside the footprint, not in the ring, and that
is the reason: the drape settles the columns *outside* the cut's box, while inside it the clear takes each column down to
the cut's own floor and leaves it, so any column the cut does not fill - the river corridor running through the north-east
of the village - is left as a staircase beside the water. Settling those columns too is a change to the drape's own rule,
it wants `DRESS_VERSION` bumped so the fix reaches villages that already stand, and it would be re-run over whatever the
river's mouths look like now.

**Settled: it is not a fault.** The owner confirmed the terraces read as the village's own bank rather than as ground
that was missed, so the drape's rule stands as it is - no `DRESS_VERSION` bump, no second pass over the mouths, and
nothing owed at -265, 98, -72. The paragraph above is kept as the reason the rule is what it is.

## The old forest road - history, since re-planned from its two ends

This is the first road the walk laid and called right in game, and its walked form is gone: `Roads.ROUTES` holds no
hand-written route now, and the same road is `tolkienmobs:roads/planned_11813_-3901_8229_-3654` in `Roads.NETWORK`,
planned from its two ends against the drawing. What it laid is worth remembering, because the plan is judged against it:

- **The walked road.** 8231, -3641 to 11842, -3877, three thousand eight hundred blocks, five wide, the hundred and one
  ammolite marks each a bend, graded on halves so the terraces are stairs rather than jumps.
- **Mirkwood.** The road walked up into the canopy until the nine hardened leaves were tagged `minecraft:leaves`
  (`0e5e98fb`); it walks the floor now.
- **The two crossings, as they were stood** - nine-wide decks of `bridge_end`/`bridge_middle` with oak-log piers drawn
  to the bed. These are the old walked bridges; the plan now spans the same rivers at 8551, -3626 (46 wide, deck 188)
  and 11649, -4080 (52 wide, deck 170), which is the geometry a fresh world gets.

| crossing | old route | banks (east → west) | deck | abutments' base | middles' base | water |
| --- | --- | --- | --- | --- | --- | --- |
| Old Ford | `OLD_FORD_BRIDGE` | 8520, -3668 → 8481, -3668 | 189 | 188 | 187 | 186 |
| Forest River | `FOREST_RIVER_BRIDGE` | 11775, -3841 → 11736, -3841 | 169 | 168 | 167 | 167 |

A road already laid keeps its heights, so the old decks stay where they are; `/tmroad forget` at the crossing and its
camp end is still how a leg is re-laid. A span is planned as a `Route.Crossing` from its banks, and a road leaves water
to it within `CROSSING_ACROSS` (12 blocks) of the span's line. `docs/arda-roads.md`, section *Water a road was told
about*, has the design.

## The named bridges - named 23 November, art still to build

A span is the standard bridge's - nine wide, `bridge_end` and `bridge_middle` from the jar's `bridges` folder, oak-log
piers drawn to the bed - unless a block on its water is named in `Roads.BRIDGES`. That table is the whole of how a
crossing gets a bridge of its own: a name for the log, a block on the water, the two piece names and the block the
pieces' own piers are made of.
`Roads.bridge(span)` answers which span covers a named bridge, the way `Roads.water` answers for a column - between the
banks on the span's own axis and within half the deck plus `BRIDGE_LEEWAY` 8 of its line - and it is asked by the ground
rather than by the span's section number, because `_bridge_<n>` counts from the end the plan walked from and moves the
moment the plan meets another river before it. `RoadCommands.pieces` asks it once when the pass reaches the span, so the
plan, the stand and `pillarsColumns` all take the same pair and the same material; the sweep before the pieces are stood
still clears only the five standard woods, because clearing stone out of the box the abutments stand in would take the
bank, and a stone bridge laid twice stands its pieces over the ones already there.

- **Bucklebury** - the span on the road the owner walked from Bree to the Shire, named in the table at 438, -49: the
  section the store calls `planned_480_-6_381_-5_bridge_1`, between banks 456 and 420 on z -49 at deck 65. The drawn
  pair is in the jar, cut from the owner's own build of the Brandywine crossing with the ground taken out (see
  below); its piers are cobblestone walls, which is the support the entry names, and the parapet above the deck is
  stone brick. It was held back while the Shire leg was the mod's and is named now that the crossing is his own
  road's. The pair is 17 + 20 + 17 columns for a thirty-seven-column crossing, so the plan lays both ends and one
  middle with the far abutment over most of it: it stands, but it is not the fit the Brandywine's had.
- **The Last Bridge** - the East-West Road's crossing of the Mitheithel. Its pair is `last_end` and
  `last_middle` in the jar, and its table line went in on 25 November 2026 with no block: the leg that would have
  crossed the river, `planned_4648_-3316_1031_-36`, plans as one dry section against the drawing - *"Nothing on the
  way is water, so nothing is bridged"*, 11:39 on 22 November - and it left the table on 25 November. The entry
  answers no span until the owner names a block on the water his own road crosses there, and its piers are
  cobbled dwarven stone, not the stone bricks this note used to guess.

The **Last Bridge**'s pair is in the jar, so its table line stands it wherever its crossing is named; a bridge
named before its art arrives is the fallback case, where
`pieces` falls back to the standard pair and the log says `the Last Bridge bridge is not in the jar (last_bridge_end
and last_bridge_middle), so its crossing is stood with the standard bridge` once for the pass. The files go in `src/main/resources/data/tolkienmobs/structure/bridges/`, beside
the standard pair, and a piece is named by its leaf name alone wherever it is written down - `RoadPlan`'s
`END_PIECE`, a `Roads.BRIDGES` line, a world's `road_plans.dat` - so `RoadCommands.BRIDGE_PIECES` is the one place a
bridge's path is spelled. A piece is nine columns across - the plan's `HALF`, four either side of the line -
and any length along the span: `bridgePlan` reads each piece's own length and repeats the middle as far as the water
needs. The support has to be a block only the piers are made of, the standard bridge's being oak log under a plank deck,
because a support the deck itself is built from would carry every column of the span to the bed.

**Bucklebury's two pieces, as drawn** (23 November). The owner's cut of the crossing was the right bridge in the
wrong box, so the pair kept for the Shire to Bucklebury road is drawn from it to the four numbers `bridgePlan` holds a
piece to.
`tools/bridge_build.py` writes both files and reads each one back cell for cell; nothing in either is ground, so a
span stands the bridge and nothing else. The cut itself is in this repository's history (`71e6270d`, `a6a7b30a`)
if the drawn pair is not wanted.

- **Nine columns across.** `HALF` 4 either side of the span's line, and the box is the deck edge to edge - the
  cut's seven columns inside an eleven-wide box would have covered `low` to `low + 6` at one bank and `low + 2` to
  `low + 8` at the other, because the near end is stood turned half round and its z is mirrored.
- **The end is seventeen long, the middle twenty.** The end's deck's bank edge sits on local x 0, so the plan's
  first middle begins where the end's deck does. The Shire crossing's fifty-four columns (471 to 418) lay as
  17 + 20 + 17 with nothing open and nothing doubled; the cut's twenty-long end left 454 to 452 as open water.
- **The deck is on the row the road arrives at.** The ends stand at `endBase`, one over the middles'
  `bridgeBase`, so the walked course is row 1 of the end and row 2 of the middle, and both are at `deck`. The
  cut's deck was three courses thick and four rows over the water, which would have stood the bridge two blocks
  proud of the road at both bridgeheads.
- **Only the piers are cobblestone wall.** The parapet is `stone_brick_wall`, because `pillarsColumns` takes the
  lowest support block it finds in each column and draws down from it: a cobblestone parapet running the length of
  the deck would have been found in every column it crossed and drawn to the bed as a curtain of pier under the
  edge of the deck.
- **The piers are on both the end and the middle.** The end's at x 4-5 and 12-13 of its length, the middle's at
  9-10, all on z 0 and 8 under the parapet, so a span of one end, one middle and one end is drawn to the bed at
  five places and a run of middles is propped every twenty columns. The cut's middle carried none, which would
  have left its twenty columns hanging.
- **The bridgehead.** Two stone-brick towers to row 9, spruce plank caps, spruce slab seats and a torch each, with
  the parapet starting clear of them at x 3, so the gate between the towers is five of the nine columns; column
  x 0 is open deck so the road walks straight on.

Not seen in a world: no span has been stood from either piece. The two things the first laying has to answer are
whether the towers read right at the bank and whether the deck meets the road flush at both abutments.


## Quests - chapter two

Eleven pages, 26 to 36, written and wired; **played through page 32** and working. The page table is in
`docs/chapter-two-state.md`. What a session needs:

- **Gundabad's bodies:** Gimli in his own cave east of the gate, on the room's dais (5703, 188, -6676); the captain on
  the throne (5546, 204, -7036) with the wraith three blocks along (5546, 204, -7039) - both placed from
  `ArdaMap.keepThrone()`/`keepThroneWatch()`, so they follow the hold.
- **The gate and cave** stand themselves as the server comes up; the coal lattice door is x 5575-5581, y 190-199,
  z -6856 and opens for `item_keystone` within 12 blocks.
- **The keep** stands itself, and its placement is **settled at `KEEP_FLOOR` 176** - its rooms' floor 189,
  level with the passage - and the walk from the gate's hall to its own door is **signed off on 21 November**
  (item 5).
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
- **The switchbacks are closed unexercised, 22 November.** The survey of all 47 legs of the eleven roads found the
  steepest at 0.23, so the traverse branch fires nowhere in the drawing, and the owner has called it closed rather
  than walk the pass-wall test. The test leg itself is written above (5610, -6790 to 5706, -6790: five traverses 47
  wide, four bends) if the branch ever needs signing off. Nothing is owed.
- **The inner keep's stair is final and open.** No door goes over it; the hold is a place to explore, page 29 stays
  dialogue.
- **The chapter-one seam is final.** The courtyard scene between pages 25 and 26 ran smoothly as staged;
  `docs/departure-scene.md` is a record, not a task.
- **The spikes are settled** as decoration with incidental damage.
- **The river's north-east bank is settled - not a fault.** The stepped terraces at -265, 98, -72 are the drape's own
  rule working: it settles the columns outside the cut's box and leaves those inside it to the cut. Confirmed in the
  world, so the rule stays and nothing is re-dressed.
- **The biome audit is closed whole** - eleven findings applied, the Barrow-downs' deadwood kept on purpose, the four new
  leaf textures called right. `docs/arda-biome-canon.md`.
- **The keep's placement is settled at `KEEP_FLOOR` 176** (18 November, x 5528 since 21 November). `ArdaMap` carries
  `KEEP_X` 5528, `KEEP_Z` -7112, `KEEP_FLOOR` 176 - the rooms' floor 189, level with the passage - with the box x 5528…5649,
  z -7112…-6930 and the south wall at -6930 where `keepWall()` ends the hall; `GundabadKeep.autospawn` reads the
  template's own blocks through `Sites.standsAt` and carries the hall to the keep's wall every pass. The x moved twelve
  east on 21 November so the hold's own doorway stands in the chamber, and a fresh world walked on 21 November found it
  there - with the passage between thirteen blocks short, because the hall's clear was addressed to the wrong cells. The
  coordinates are fixed (see the 21 November paragraph), a world that already holds the hold is re-cut on its next
  start, and the walk on 21 November found the door open at the end of it.

## On hold, and what unblocks each

- **Chinese** - unblocked by a public release; the rework's lines are translated then, not line by line.

## Not started

- **The two town roads walked end to end.** Both are in `Roads.NETWORK`, and both now stop at a town that is still
  being raised and lay the arrival when it stands, so what is left is the walk: the Shire's east side at -150, -382 to
  Bree's hall front at 1032, -34, and Bree on to the vale's junction at 4648, -3316 - checking that each arrival is
  the ground its town dressed rather than the ground the town was later built over.
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

- **The village's worn ground has a repair, and it is a hand's**: `/hobbiton heal` puts the cut's own grass back over every column of the cut's
  footprint where the world holds plain `dirt` or `coarse_dirt` at the height the cut draws grass, with nothing solid or wet over it. It is not on
  a clock and nothing calls it: the wear is the village's own folk walking, so it returns, and a pass that watches a village for a living is idle
  work. It asks the whole cut rather than a circle round the caller, because the world says the circle is worth little: `Generation Redux - 3`
  holds 5,450 worn columns in the footprint and 5,446 of them are further than 64 blocks from the spot behind the muster hall. **Run by the owner
  on 25 November 2026, and the patch in the picture did not change** - nor could it: the 25 bare columns under the camera lie past the cut's
  southern edge at z -45, where the cut draws nothing and the repair never looks, and untouched country is mottled the same way (1.1-1.7% bare),
  so that brown is the Shire's own top rather than a skipped pass. The widened pass has not been run; the 5,450 columns still read as dirt.
  It reads the cut the jar holds, so it is worth running in a world raised from that cut and not in one raised before a re-cut.

- **A biome key added without its JSON is a world that will not open.** 25 Nov: `Generation Redux - 3` stopped at
  registry load with `Unbound values ... [tolkienmobs:river_brandywine]` because `4b6782fa` added the key, the
  maker, the biome source overlay, the dimension list, both tags and both lang lines but did not run `runData`, so
  `data/tolkienmobs/worldgen/biome/river_brandywine.json` did not exist. Datagen writes it now and nothing else in
  `src/generated/resources` moved. Whenever a datapack-registry entry is added - a biome, a dimension, a structure -
  run `runData` in the same commit and look for its file in `git status`.

- **The apron is built and measured, not yet walked.** 26 Nov: `Hobbiton`'s `DRESS_VERSION` is 5 and `Dressing`'s inside
  cut grades a bank of `APRON_CAP = 8` or less over `APRON_WIDTH = 4` before it reaches the cut's own top (`apron`,
  `shoulder`, `earth`), refusing banks over eight, columns whose roof course is built, and columns with no shoulder
  within four. A new world raised headless on the dedicated server (`runServer`, level `Apron Check`) dressed itself in
  38 s: 9,871 columns inside the walls over the cut, 0 ring cuts, 39,881 fills, 2,656 left for the water. The save reads
  back 256 columns still three or more over a real roof - 120 in the three-to-eight band (the top row of a graded bank,
  by design) and 136 at the footprint's edge, which is the ring's join and the unbuilt hem's business. The 48-cube step
  histogram of the dressed ground is 1% >= 3 across a cube edge (max 8 east-west, 23 north-south) against 1% and 26-27
  inside a cube: no worse. Nothing was walked - the shape is numbers only, and the band left at full height has not been
  looked at.

- **Stray water at the edge is drained, and the apron is next if it is not enough.** 26 Nov: `EDGE_WATER = 3` in `Dressing`,
  `stray(x, z)` and `drain(level, x, z, surface)`, so a pool within three blocks of the box edge that is not within three of
  a cut water column is turned to dirt and then dressed by the ordinary join; `DRESS_VERSION = 6`. Same seed and corner as
  `Apron Check` on the dedicated server (`Water Check`): 6,789 left alone against 7,798, 40,864 filled against 39,881, 15
  cut outside the walls against none, 2,656 left for the water both times. Not looked at in game. The answer to "if this
  does not fix it": yes - the lift-only hem already measured (reach 96, smoothstep, cap 24, the four rim tables) is the
  apron round the high points, and it is the next thing to build if the pools are still not the whole of it.

- **The mountain over the high points is built, not yet shown.** 26 Nov: `HEM_INSET = 4`, `HEM_REACH = 45`,
  `HEM_CREST = 3`, `HEM_TALL = 10` in `Dressing`, with `tall(at)`, `edge(x, z)`, `crest(x, z, at)` for the columns
  inside the crest and `mound(outX, outZ, at, top)` raising the ring's cap over the same 45 blocks; `DRESS_VERSION = 7`.
  Compiles; the one headless attempt ran a stale build (no new counter in the log), so the shape is not measured and
  not looked at. Next: dress a new world and read the `carried under the mountain` count, then look at the same two
  spots; if the crest is too high or stands too far in, it is one constant each.

- **The mountain now reaches the world.** 26 Nov: `mountain(x, z, at)` and `mouth(x, z)` in `Dressing`, `onFlank`
  bypassing the shore clamp and the tap-water guard for ring columns on the mountain's flank; the crest raises the
  build's outer four blocks (`crest`), lifts the ring's cap (`mound`), and only the outflow stays open. Headless `Hill
  Check`: 46,525 filled, 2,487 left alone, 2,112 carried under the mountain, 27,806 on its flank. Not looked at. Next:
  the owner's new world on this build, and the shape tuned from the pictures - `HEM_CREST` for height, `HEM_REACH` for
  spread, `HEM_TALL` for how much of the perimeter is buried, `HEM_INSET` for how far the crest sits inside.

- **Mountain tuned to the owner's four answers.** 26 Nov: `HEM_REACH = 60`, `HEM_DROP = 3`, `HEM_TALL = 4`, the
  `mouth()` guard deleted so the outflow is buried; `DRESS_VERSION = 8`. Headless `Ring Check`: 49,584 filled,
  95 left alone, 38,464 on the flank, 518 carried under the mountain. Not looked at. If the ring is still short
  somewhere, the remaining suspects are the `tall` gate at the very edge of the build's low fronts, and columns the
  cut leaves empty (`outline` of `MIN_VALUE`), which no mountain can be hung from.

- **The mountain's edge and its missing places are fixed; the edge tree is not.** 26 Nov: `DRAPE_REACH` 32 -> 60,
  `HEM_FEATHER = 2`, `mound` signed so the same line fills where the country is low and cuts where it is high, and the
  mountain's line now replaces the ring's join on its flank; `DRESS_VERSION = 9`. Headless `Smooth Check`: 91,533
  filled, 67 cut, 54,111 on the flank, 1,216 left alone, dressed in 47 s. Not looked at. The tree-shaped pieces at the
  edge are open: the ring reads ground from the top of the column down, and a crown that reaches out over the build's
  own edge is the case it has always been weakest at - if they are still there, the next step is to read such a
  column's ground from the ring's side rather than its own.

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

The lip and the water beside a cut are answered in `Dressing` (a one-block join is left alone, and a cut stops at the surface of the water within `EDGE_WATER` of it). What is still open is the spot at 859/124/-2769, the one the owner keeps coming back to: the village's own edge there stands nine blocks over the water at its foot, and no hill is planted because `HEM_TALL` measures the edge against the village's own deck (three there) and asks for four - the owner's own rule, "ring every edge where the build is 4+ over the deck". Measuring it against the country and the water just outside the edge instead would plant the hill there and bury the cliff, at the cost of ring much more of a village that sits on a plateau; that answer is the owner's to give. The water walls are not reproduced from the coordinates given (848/-2863 and 1347/-2457), where the reading shows grass to the water's own level - one coordinate of a wall still standing would pin it. A tree on the build's own edge remains the one shape the ring has no answer for.
- **The seam and the pad.** 26 Nov, second batch of screenshots: the lip round the whole build, a stone step in a   field on the east side, a hollow beside the west edge that refuses to meet the village, and the river walled off
  after the drape. The cause was one thing read three ways: the ring clamped every column outside the footprint to
  the edge of the forty-eight-block tile the cut was laid in, and the village fills only part of that tile - its east
  edge 25 blocks short, its south 12 - so the whole of those sides was skipped as having no wall to join, and the
  country's own steps stood where the join belonged. `Dressing.held` now walks in along the row and then the column
  to the nearest filled column (`HEM_HELD = 64`), a column of the tiles the cut leaves empty is dressed by that join,
  the crest no longer loses its two-block feather, the bank rule is measured over the build's own top rather than the
  deck, and the mountain's flank answers the shore rule; `DRESS_VERSION = 11`. Headless `Pad Check 3`: 71,715 filled,
  9,523 cut, 30,361 left alone, 65,016 on the flank (the owner's world on the previous build: 75,715 / 33 / 26,265 /
  54,096). At 1416/1417 the ground now steps 91 to 91 rather than 91 to 89; the hollow at 861/-2784 ramps 88 to 80 in
  ones instead of standing six deep; the bank at 1455/-2862 meets the sheet at 79. Not looked at in game. Open: the
  hollow's floor stays at its pond's own level, because the shore rule holds a fill down beside water, and burying it
  would bury the pond - the owner's call; country steps more than twelve blocks past the south edge, and the mid-field
  step at 1450/-2725, are the country's own and stand under a bank of three; and a tree standing in the new pad keeps
  its crown over ground that is now at the village's own height, which may be the "trees blocking it" the owner
  meant at 12.20.18 - whether those crowns come off is theirs to say.

**The ring stops cutting under the water's own level, and the country under the village's trees comes off, `DRESS_VERSION` 12.**
A fresh world on this build reads `9525 cut, 71638 filled, 4980 left for the water, 30394 left alone, 45383 inside the
walls cut down to the cut, 518 of the build carried under the mountain, 65029 of the ring on its flank`; the owner's
world ten on the build before it read 9,415 inside, so the 36,000 columns that moved are the country under the village's
crowns. **Why they moved:** `dressInside` compared the country against `whole`, the cut's top with its trees counted. At
1412..1422 by -2521..-2507 the cut's outline is 85..88 and its whole 94..106, the world stood its country at 99..102 in
those columns and the field four blocks east was graded to 87 - `surface - roof` was two at most, so a twelve-block wall
of country stayed up under each grove, which is the standing wall in two of the owner's screenshots, and passing a bank
where there was none is why no apron ran. The measure is the outline now, with the whole only where the cut draws no
ground under a crown, and the clear skips leaves and wood so a crown is not taken with the country it hid. **Why the
water moved:** the cut's guard against standing below a sheet was `beside <= EDGE_WATER`, three blocks, and water spreads,
so a cut thirteen blocks back from a lake left it a way in; it now holds at `shoreLevel(x, z)` wherever the ring holds
water. **What to look at in the next world:** the north lake's bank (1354..1370 by -2440..-2424) and the west one
(832..848 by -2596..-2582) for stepped ground *above* the sheet and no face at the waterline, and the grove at
1412..1422 by -2521..-2507 for the wall gone with the crowns intact. **Still open, and the owner's to call:** the
1-block terraces are the country's own and stay; the fill's material was left as it was at his word that he is not
concerned with the ground's type; and the trees that look as though they stand in water are on a bank seven over it at
1360, -2430 - if they still read as drowned in a picture, the answer is the sheet's own level, not the dressing. **Not
seen in a world:** nothing was opened in a client on this build, and a re-dress of world ten could not be saved, so the
before and after are two worlds of the same shape rather than one world twice.


**The trees and the pond are answered, `DRESS_VERSION` 13.** A fresh world on this build (`run/Dress 13`) reads `13,264 cut, 71,838 filled, 0 left for the water, 31,088 left alone, 0 orphaned, 0 inside the walls cut to the cut, 518 covered, 65,016 of the ring on its flank`; the build before it cut 45,149 columns inside the walls. **Why the inside went to nothing:** every one of those columns was a tree. `dressInside` asked `MOTION_BLOCKING` for the country's height, that height is the canopy, so a column under a crown measured its tree over the cut's own ground, read as a bank past `APRON_CAP`, and was cut flat to that ground - a pit in the shape of the crown with the country beside it left standing between the crowns, which is the chunks with pillars shaped like the trees above that the owner pointed at. Read by its ground it is not a bank at all, so nothing inside the village is cut. **Why the water moved:** the mouth test compared the held column's water with `box.lowY()` and `OUTLET_LIFT`, so the Shire's pond - 79 over a base of 70 - answered as the city's outflow along its whole edge, and every one of those columns had a channel three deep dug in front of it and filled to 79. It now needs `watered[at] > surface`: water the build holds up over the country, not water lying where the drawing put it. The same window of the save reads 711 thin water columns against 3,763 - the rest of them were the channels. **Still open, and not touched this build:** the ring's own reach is 60 blocks from the box on every side with `share` taken as `hypot`, so the dressed band is 60 blocks wide at a side and 85 at a corner, which is the distance from the build the owner sees vary round corners and sides; the mountain ring's stepped fill is untouched; and `Dress 13` reports `0 left for the water` where `Hobbiton - 11` reported 4,980, which no change here explains - the two are different worlds and this project's rule is that two seeds are not one world twice. **Not seen in a world:** no client was opened on this build, and the two worlds' counters are not a before and after of one world.


**The ring is off, and the village is laid as a cut laid alone.** The owner's 26 November answer was to start over: take away the mountain round the build and stop the dress, so a fresh world shows what the village does to the country with nothing dressed over it. That is now one setting, `dressShireRing`, off by default. The ring was never separable into the four things it drew - the join, the shoulder, the shore beside water and the mountain over the build's high points are a single pass over a single rectangle of chunks, and the mountain is its outermost ring - so one switch takes all of it. The pass ends after the footprint is cleared, and it asks vanilla for the footprint's own 1050 chunks rather than 1326. `Bare 1` reads `the village stands at 866, 70, -2917 in 534 ticks (26.7 s), on the country as the cut lays it, with the ring left alone`, with thirteen of the folk stood and the hall at 1037, 83, -2479, and no drape line in the log at all. The note records `0` in place of the `DRESS_VERSION`, so the two cannot be confused: turning the ring back on re-dresses, turning it off re-clears. **The next question this is meant to put on the table:** what a lone cut actually needs at its edge. Four rounds of rules - a lip, a shoulder, a shore, a mouth, a mountain - were each answering a seam that the cut itself makes, and the honest reading of the owner's screenshots is that the village's own edge should be in the cut's own pieces: a finished ground of the build's own blocks laid under and beside it by the piece that carries its wall, so there is no country to change at all. That is a change to the cut's table and its 143 pieces, not to the drape, and it is worth measuring on a bare world first. **Not seen in a world:** the bare village has not been opened in a client.

**A trunk that stands on nothing now comes off a cut, and the Shire's bare posts are gone.** The posts in the owner's 26 November screenshot of `DoOver - 1` are the cut's own blocks, not the country's: 269 columns of the box are topped by wood, all `minecraft:oak_wood`, and the cut holds exactly the 5,781 the world held. They read as pillars because they hang - at 1319, -2507, `oak_wood` 90..95 with air 86..89 over `short_grass` 85 - and the clear could not see them: it takes country over the cut's top, skips a column the cut draws nothing in, and never questions a block the cut draws. `CutCommands.strays` sweeps every column of the box from the top of the world to the cut's floor and takes a trunk with air under it, upward as far as it is wood; a trunk on ground or on its own roots stays, and crowns are never questioned. `Hobbiton.UNDRESSED` is 1, so `DoOver - 1` (noted 0) raises again on the next start. **Not seen in a world:** compiled only. **What is left in that save after it:** the leafy trees standing inside the box are the cut's own crowns (its 152,386 drawn `oak_leaves` are all still in the world) and the country's culumalda and lebethron, which the clear leaves where the cut draws nothing - they are trees, not posts, and nothing in this change touches them.

**The bounce at the build's edge is not a blend yet, and the measurements say what one would cost.** Read off `DoOver - 1`, the box's rim against the country ten to twenty blocks outside it: west `866` stands 96.9 over 86.5, a mean step of 10.3 and 40 at the worst; south `-2917` stands 109.7 over 88.5, a mean of 21.2 and 54 at the worst; east `1441` is 84.5 over 83.9, level; north `-2450` is 83.2 under 89.8, a hollow. So the 45 degree dirt and gravel faces are the cut's own hill ending at the rectangle's rim, and a 1:1 slope that met it honestly would want twenty to fifty blocks of country on the south and west sides - the mountain again, in the one place the owner has said twice he does not want it. **The cheap way out is inward, not outward:** feather the cut's own outermost six to eight columns down to the country's surface, so the village's land runs out instead of stopping in a face. It adds no ground, leaves every column inside that band exactly as the cut draws it, and touches only the rim of the art - about nine thousand columns of one pass, against the sixty-odd thousand of ground the old ring worked on every side. The alternatives measured and rejected for now: putting the country back up to the rim (the mountain), and caping the faces in the cut's own soil (cosmetic, and it keeps a twenty-block wall). **Not started:** this is the shape of the next change, not a change; the owner's call is which way the feather runs, and a fresh world is what it needs to be seen in.

**The land's trees come off the Shire whole now, and `Pillar Check` is the world to look at.** The owner's 26 November reading was right: the bare stems in `DoOver - 1` are the country's culumalda and lebethron, not the art's. Two things let them through - the sweep above a cut's top began at the heightmap, which the template's own flags leave stale, and `standsOnOwnGround` kept any column whose tree's foot touched the build's ground, drawing or not. The sweep now starts at `box.highY() + 16` and a column is kept only where every block of its tree is the cut's. `Pillar Check` against `DoOver - 1`, same box: country wood 5,439+2,390 to 273+89, country hardened leaves 56,490 to 4,452, country-tree tops over the cut's own top 16,617 to 0, drawn `oak_leaves` 152,317 to 152,281 (the art intact). The drawn posts over air went too (oak_wood 5,781 to 2,770) - `strays` takes any trunk with nothing under it - and 77 wood-topped columns remain where a drawn post still has something beneath it. **Look at:** the field east and north of the courtyard the owner photographed (1330..1400 by -2530..-2470), the drawn oaks around the party garden for crowns still whole, and the pond's edge. **Not seen in a client:** compiled and raised headless only; `Pillar Check` is on disk at `run/Pillar Check` for the owner to open.
