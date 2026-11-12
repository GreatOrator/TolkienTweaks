# Where the work stands

This is the note that survives a compacted conversation. The transcript can be summarised away; this file is not. Read
it before starting, then `CHANGELOG.md` for the per-change history and `docs/arda-roads.md` for how the roads are
designed. Update it at the end of a session rather than trusting the chat to be there next time.

## The goal

Chapter one of Arda: every scouted structure and every finalised road stands or is laid automatically in every world,
with no commands typed. A player walks the chapter and the world has already built it.

## Standing rules

- One `CHANGELOG.md` bullet per change.
- Commit to `main`; never push.
- Never playtest; the player relaunches and reports.
- "Verified" means `./gradlew compileJava -x createMinecraftArtifacts` exits 0. Say plainly when something is only
  compiled and not seen in a world.
- Commit generated JSON beside the code that generates it.
- Edits to Java are byte-level scripts in `build/tmp/` (files are CRLF; `sed -i` strips it and long heredocs truncate).
  Generated biome JSON is LF by `.gitattributes`.

## Where things live

| File | What it does |
| --- | --- |
| `faction/Pins.java` | The scouted spots: `SOURCE`, crag pins, the ford marker. "The pin wins, the marker only informs facing." |
| `faction/Keepers.java` | The bodies the line has stood, by tag, UUID and last ground, so a keeper is found in an unloaded chunk. |
| `faction/Sites.java` | Seats a build at its pin with no drift, underpins it, sweeps the ground to its roofline, and plans a road for it. |
| `faction/Roads.java` | `ROUTES`: Rivendell's crossing, Elladan's camp's branch (`ELLADAN_BRANCH`, wild), the Thorenhad main road (`THORENHAD_ROAD`, banded, 4633,-3331 to the ford), Barachen's fire's track; `BENDS`, `KINDS`, `BANDED`, `BUILDS` by route. |
| `faction/RoadCommands.java` | The road pass: sweep, bridge, pillars, search, grade, lips, and `Progress` (the road's own record). |
| `faction/Dressing.java` | The city's own drape and the check that plans a road when one is not down. |
| `faction/RoadPaint.java` | Roads painted at world generation, as a pure function of the column. |
| `world/registration/ArdaChunkGenerator.java` | Draws every column to `ArdaMap`, lays the water, cuts Moria's halls. |
| `world/registration/TolkienBiomeGenerationSettings.java` | What a biome generates: only the mod's own features, and no carvers. |
| `run/logs/latest.log` | The evidence. `grep "Road \|laid in\|will not take\|Dressing"` answers most questions. |

A column's ground for the road pass is read by `RoadCommands.surfaceAt`, which walks down through everything
`RoadPaint.cover` calls cover - leaves, trunks, vines, snow, replaceable plants - to the soil. Both use that one
method on purpose: the reading and the corridor clearing must agree, or the clearing takes away what the road was
standing on and leaves it in the air. Roads 7 had road blocks sitting on oak leaves at the junction above
Barachen's camp, and seventy-eight columns of the camp's road left hanging when the canopy went.

The city raises itself in `CityCommands.rouse`: one piece at a time while a player is within 160 blocks of its box,
nearest piece first, and only into chunks already made - `lay(..., false)` never generates ground for a piece, so
the city fills in as it is walked. `lay(..., true)` is the hand's own path (`/tmcity band`) and still makes its
ground. The cadence follows `MinecraftServer.getCurrentSmoothedTickTime()` between four ticks and twenty. Two
consequences: a city nobody walks does not finish, and therefore the road that waits on it does not plan; and a
band can sit half-laid until the player's view reaches its far pieces.

Sugar cane is placed only by `TolkienPlacedFeatures.PLACED_SHORE_REEDS`: `not(water)` at the position *and*
`would_survive` for the cane itself, because either one alone leaves a case - the refusal lets a position above a
channel through, and survival alone accepts a bed with water round it. The marsh names that feature too. Note
that `runData` cannot run while the client is open (it rewrites `build/moddev/artifacts/minecraft-patched-*.jar`
and Windows locks it); the placed-feature and biome JSONs for that change were hand-written in its shape,
and the first `runData` after the client was closed confirmed them (three files written, no differences).

The fen's trees are `TolkienPlacedFeatures.PLACED_SWAMP_TREES`, vanilla's `swamp_oak` planted exactly as
vanilla plants it (weighted 2/3 a chunk, in square, `surface_water_depth_filter(2)`, `HEIGHTMAP_OCEAN_FLOOR`,
`would_survive` an oak sapling). Vanilla's own `trees_swamp` could not be used: the generator keeps only the
mod's features, so the marsh's data listed no trees at all. The Dead Marshes still name vanilla's and still
have none, which is their look; note that every other vanilla list in both fens is stripped the same way -
the fen's grass, flowers, dead bush, mushrooms, pumpkin and lily are now the mod's own features as well
(`PLACED_MARSH_GRASS`, `PLACED_MARSH_FLOWERS`, `PLACED_DEAD_BUSH`, `PLACED_MARSH_BROWN_MUSHROOMS`,
`PLACED_MARSH_RED_MUSHROOMS`, `PLACED_PUMPKINS`), each carrying vanilla's modifier list in order; the lily
reuses `PLACED_WATERLILY`. A vanilla placed feature asked for by name is stripped and grows nothing, so a new
fen plant is a copy of vanilla's list under a mod key.

## Where it stood on 8 November 2026

Done and seen in a world by the player: the camp, Barachen's camp, the wood tower and one crag place at their pins; the
camp-to-ford road lays; the Rivendell crossing lays in full (`laid in 53 steps, 2621 columns`).

Done and only compiled (not seen): two refused crags re-attempting, the ground past plants, the sweep that carries the
ground around every build to its roofline, no vanilla world generation in Arda, and roads spanning a hole.

A keeper is now asked of the world by its tag rather than found in a box round his post, a body left outside his reach
of the post is walked back to it, and every stood body is written down in the save (`tolkienmobs:keepers`) so he is
found in an unloaded chunk rather than stood again. The crag's troll, the Witch-king and the three of his ring keep the
same note (a tag may own several bodies, and the ring is stood only for the slots its count leaves empty): compiled, not
yet seen in a world.

The camps stand their own men now. `Sites.CAMP_OWNERS` gives Thorenhad Elladan and Barachen's fire Barachen, wearing
the campaign's giver tags and written into `tolkienmobs:keepers` under the tag `Campaign.keeperWith` asks for, so the
line finds the body the world stood instead of standing a second one; a nameless keeper already at a named camp is
taken away. A camp the world raised from its pin is not laid again at its place's middle (`Sites.raisedAt` guards
`standTent`), and Barachen's fire gets Barachen's cut rather than Elladan's. Compiled, not yet seen in a world.

Roads can be bent through recorded points now: `Roads.BENDS` holds a list of blocks per route, and the walk is split
into a leg per bend so the line is forced through each one. A route with no entry there is walked exactly as before,
one leg from the door to the far end. The Thorenhad road is bent through the three marks walked in Roads 3 - 4875, 74, -3179, then 4885, 74, -3133,
then 4903, 74, -3102. Its first alignment, before them, was 1016 stations long: it looped north and east
and back to the far end, which is why it went through the ford.
Compiled, not yet seen in a world.
A road already in progress keeps its saved alignment, so `/tmroad forget` at the build clears the note and
the site's road flag, and the next tick walks it again with whatever bends are recorded for it.

A road is one of three kinds, and the kind is its width: a wild track three blocks wide, the transition's five, a
city's own seven. `Roads.BANDED` names the roads the bands decide - Rivendell's crossing and the Thorenhad main road -
so those are a road where they meet a build and a track in between; every other route is a wild build's own way out
and is laid wild from its door, width and surface both. A route naming a kind in `Roads.KINDS` overrides both (none
named yet). A hairpin's apron is one kind wider than the run it turns, and a wild edge is worn by one column in three.
`Roads.BUILDS` says which build a road leaves from where the road has a name of its own, so forgetting the road at a
camp still clears the camp's flag.

The Thorenhad road's camp branch is recorded: Elladan's camp walks out of its door at 4648, 90, -3314 down its own
paving to a bend at 4648, 89, -3321 - where the camp's stone path ends - and then over to the main road's own start
mark at 4633, 91, -3331 as a wild track three wide (`ELLADAN_BRANCH`). The turn is about sixty degrees, taken at the
paving's end, so the branch comes down onto the main road along the line that road runs rather than carrying straight
on past the junction into the trees. The main road runs from that mark
the ford being its other end - from which it runs banded down to the ford's road end at 4972, 86, -3075. The camp is
dressed first, so the branch is the road its own approach plans; the main road is planned by standing near its end and
running `/tmroad build`. The camp's old main-road note is under `tolkienmobs:key_structures/elledancamp`, which no
route uses now, so it is ignored rather than resumed.

Roads are begun by ground now, not by a build being found: a pass is the world's, held by road, and any
chunk a dry road passes through starts that road from whichever end the player meets it - stepping while the
ground under its head is loaded, waiting when it is not, so a road survives a walk away or a logout. The end
it is met at is the end it lays from: `RoadJob.outward` is set at planning time from the point that asked
(the chunk's centre, a build's middle) and measured along the road's own line - its door, the bends in the
order the road walks them, its far end - rather than through the hill between them, so a player on a bend is
placed by the bend they are beside. A dry road met at its far end has its stations turned round and
its two ends' ground swapped before the grade is laid, so the player facing the road sees it come towards them
rather than fill in behind a hill three hundred blocks away. The note keeps the alignment in laying order, so
a pass carried over a restart resumes the same way. A crossing is still begun by its city's dressing, because
a bridge needs its span and abutments first, and keeps its own order - its geometry is the span, not a place.
`RoadCommands.PASSES` is the one store, so a build's approach and a road met in the open cannot plan two passes
over the same stretch.

A road already laid keeps the width and the surface it was laid with: `/tmroad forget` at the build then a re-walk
lays the new kind over it, but columns the old road paved or widened are not taken back.

Barachen's fire has its track: from the camp's south door at 4820, 76, -2886 to the mark at 4892, 74, -3097 where the
Thorenhad road runs past, wild and three wide the whole way. Compiled, not yet seen in a world.

## The session that stopped here: 8 November, Roads 5

Committed, compiled, not seen in a world: `6f6f829c` furnished the water biomes; `3b70723d` made the Thorenhad road
a main road (`Kind.MAIN`, seven wide, of the country's own surface, removed from `BANDED`); `aa747f35` made its grade
answer to its own column's ground within the same cut-and-fill, water keeping the relief because the river is the
bridge's; `b5bc3323` stopped `shore_reeds` placing cane in a water column.

The player made `Scouting World - Roads 5` and reported three things from it: the Thorenhad road is significantly
better but still lifts toward its far end and stays there to the termination point; Barachen's road digs down at its
end but does terminate properly; and the rivers were standing full of sugar cane across their width, which was the
`WORLD_SURFACE_WG` bed read described in `b5bc3323`.

**Markers walked in Roads 5**, read with `tools/ammolite_markers.py` (6 `block_ammolite`, 4 groups):

| marker | position | the player's word for it |
| --- | --- | --- |
| 4741, 81, -3092 | cave end | three cave ends for the chapter's four crags, two of them converging on one |
| 4741, 81, -3086 | cave end | as above |
| 4741, 81, -3082 | cave end | as above |
| 4786, 78, -3082 | not yet explained | 24 south of the wood ruin's own `.end` at 4787, 78, -3058 |
| 4819, 76, -2897 | bend | guides the track away from Barachen's camp, 11 south of its door |
| 4895, 74, -3075 | road end | where Barachen's track should go, nearer the ford than the 4892, 74, -3097 in `ROUTES` |

So no cave marker is missing: three markers for four caves is what the player described. What is missing is the
**pairing** - which of the three at x 4741 ends Bandrith's, Hoklond's, Sholdroth's or Matuna's road, and which two
share one. It cannot be derived: the three sit within ten blocks of each other, the three crags are 620 to 720 blocks
north of them and Matuna's lair is 63. A wrong pairing lays a road to the wrong crag.

**Not yet measured.** An attempt to read the two reported ends out of the save scanned the arda dimension for
`minecraft:dirt_path` and found 1,004 paths in 18 cells, so the save reads; but a `gb.blocks_from_world` read of the
box round the ford end (4880, 60, -3130 to 5000, 110, -3030) came back with no blocks at all, which was the probe's own bug - it took
the topmost block of each column, and the topmost block in a saved section is air, so every column read as
air and no road could be found. The chunks were there all along; `blocks_from_world` reads this save fine. Both defects are
therefore still unmeasured: the player's F3 coordinates for the lift and for the dig are what settles which chunks to
look at. The suspicion on the lift is the two `SETTLE_PASSES` sweeps that run after the ground clamp - they answer to
the pinned ends and not to the ground, so a far end whose `endGround` reads a mound can carry the last stretch up
above its own ground; on the dig it is the opposite, a pinned end below the ground it meets.

**The lift at the ford end is explained and fixed, only compiled.** Read out of Roads 5 by a probe over the road's own
columns (surface in the main road's mix with stone under it): three hundred stations level at y 80 while the ground
either side ran 74 to 79, four to five blocks of stone under the surface the whole way. The grade was *seeded* from
`relief`, the middle of the ground eight stations either side, so it began as a shelf and the +/-4 band could only
stop it drifting further. A route that names its kind now seeds from its own column's ground instead. The dig at
Barachen's end is the same rule seen the other way round and should move with it. Neither has been seen in a world:
re-laying over the old road will keep reading the old fill as ground, so only a fresh world or pristine ground shows
the difference.

**The lift and the digs are one fault, and it is ground that was never made.** Roads 6 was graded with `f635badd` in
it, and the shelf stayed, because the seeding was not the cause. `surfaceAt` read heights off chunks nobody had
generated, `fillVoid` carried the nearest readable station across them, and each route's middle became a flat line at
the height of the stations its own ends stood on. The save's own notes (`dimensions/minecraft/overworld/data/tolkienmobs/
roads.dat`: `approach` is the line, `height` the grade) show it: Barachen's camp 241 stations at 74 while its country
rises to 90; `troll_cave`, `_2`, `_3` at 75 up Eregion, climbing only at the crag end, which had been visited. The same
missing ground steered the search into the corridor it could read, which is why three cave roads share one lane at
x 4832 and two of them set off south first (cave 2 and cave 3: 909 stations within seven blocks). `surfaceAt` now
calls `level.getChunk` first. Not yet seen in a world, and it costs chunk generation during the walk; a fresh world
will show whether the grades and the three separate lines come out as country roads.

**The ruin's road now ends on Barachen's own road.** The scout set two marks in Roads 7: 4787, 78, -3082 below the
ruin - one block from the bend already recorded - and 4892, 74, -3044, which stands on Barachen's camp road's own
line. `Sites.WOOD_RUIN` now leaves the ruin's south edge at 4787, -3058, runs south through 4787, -3082 and east to
4892, -3044, so the ruin joins the track the camp uses instead of ending in open country on the crag pin; the crags'
three roads still share their pins without it. The ruin measures 4783..4795 by -3058..-3047 of stone brick, spruce
fence and trapdoor, so its footprint is that box less the row the road leaves on. Not yet seen in a world.

**Wild tracks were cutting notches through terraces; they now step up them.** A route naming no kind was held to the
same four blocks of cut and fill as a named road and measured from `relief`, which on stepped ground sits below the
terrace being crossed - hence the slot in the cave-road images, five blocks into the hillside at the median at
4573, -2414. Such a route now takes its own column's ground with a band of one block (`WILD_BAND`), the sweeps making
a terrace a two-station step; named roads keep `CUT_FILL`. Not yet seen in a world.

*(Answered in Roads 7 - see above.)* **The wood ruin's road was laid end to end with nothing to join.** Its line runs
4741, -3092 to 4787, -3058, and the world has road within a block of both ends, so the laying is whole. But 4787,
-3058 stands in open country: the cave corridor that passes Barachen's camp runs at x 4832, forty-six blocks east,
and the main road is two hundred away to the south-west. If the ruin is meant to join that corridor, it is a route
change - the far end or a bend - not a laying fault, and the shared pin at 4741, -3092 is what it ties to now.

**The next three moves**, in order: the two graded ends - the lift at the Thorenhad road's far end and the dig at
Barachen's - once the player's F3 coordinates name the chunks to read; then the five new roads in a world, since none
of them has been seen laid and a 700-block road between countries is where a search is most likely to fall back on the
straight line (the log says `no walked way to ..., so the straight line is laid instead` when it does); then the crag
footprints these routes assume, twenty-eight either side of the middle, against what `Sites` measures for a crag.

Reminder for the next session: `forget` then `build` re-lays a road, but the blocks already laid are read back by
`surfaceAt` as ground, so a re-lay over an old embankment will keep riding it. Only pristine ground shows a grading
change.

### The network as walked

Eleven markers now read out of Roads 5, every one the player described and nothing scanned that was not named:

| marker | what it is |
| --- | --- |
| 4460, 124, -2477 | the crag Bandrith's cave, marker on its pin |
| 4508, 123, -2429 | the crag Sholdroth's cave |
| 4568, 111, -2511 | the crag Hoklond's cave |
| 4633, 92, -3081 | the crag Matuna's lair |
| 4741, 81, -3082 | a cave road's end, for the nearest cave |
| 4741, 81, -3086 | a cave road's end, for the next cave |
| 4741, 81, -3092 | a cave road's end shared by the last two, and the start of the wood ruin's road |
| 4786, 78, -3082 | the wood ruin road's bend |
| 4819, 76, -2903 | Barachen's track's first bend, 11 south of its door |
| 4896, 74, -2980 | Barachen's track's second bend |
| 4895, 74, -3075 | where Barachen's track terminates |

**The cave pairing is by closest to furthest**, measured from the three pins: Matuna's lair (108 blocks) is nearest
and takes 4741, 81, -3082; Hoklond's cave (604) is next and takes 4741, 81, -3086; Bandrith's (670) and Sholdroth's
(698) are the last two and share 4741, 81, -3092. The shared pin is also where the wood ruin's road begins: the ruin's
own `.end` at 4787, 78, -3058, through the bend at 4786, 78, -3082, to 4741, 81, -3092 - which is what ties the ruin,
the four crags and the cluster into one network.

**Barachen's track** is walked as door 4820, 76, -2886 - bend 4819, 76, -2903 - bend 4896, 74, -2980 - terminus
4895, 74, -3075, replacing the single straight leg that `ROUTES` holds now.

**Asked of the player before the routes are written**, since both change where a road is graded to:

1. Is a cave road's crag end the recorded `.end` pin, 27-28 blocks out from the middle of the crag (4515, 121, -2446;
   4624, 107, -2479; 4562, 120, -2398; 4689, 86, -3050), or the marker standing at the crag's own middle (4460, 124,
   -2477 and the others)? The `.end` pins are what `Pins` calls "the ground a road arriving at a crag has to meet".
2. Is Barachen's terminus the marker at 4895, 74, -3075, or the 4892, 74, -3097 that `ROUTES` still names?

Answered: the `.end` pins are the crag ends, and Barachen's terminus stays 4892, 74, -3097. The five routes and
the three bends are written in `Roads` - the four crags, the ruin's connector and Barachen's two bends - and
`RoadCorridor` starts a pass for anything in `ROUTES`, so nothing else has to be told about them.

## Open

- **Two crags** were refused by `plane`/`ground()`; the fix reads ground past plants and a refusal now names the column
  and the block. If one is still refused the log says why — water is still a refusal, and that means a re-pin.
- **Runtime files are per world.** `CityCommands.folder(level)` resolves `<save>/tolkienmobs/rivendell`, and the
  jar cut is the source of pieces (`cut()` tries the jar first, then that folder). Nothing in the mod writes beside the
  game directory any more; a world that used the old folder re-dresses once.
- **Crags are seated fifteen blocks in** (nine before), so the rock skirt and the bare shell are underground and only
  the mouth and the crown's rock show. A crag already standing keeps the seat it was placed with; the new seat applies
  to a crag built after the change.
- **The camp road** stopped mid-grade in the last session: `SEARCH done` with no `GRADE done` after it. A pass steps
  only while a player is within `ROAD_REACH` (96) of the block it has reached, so a road that looks stopped may just be
  waiting. Check for `laid in` before calling it a failure.
- **Vanilla structures** still reach the four vanilla biomes the dimension borrows (`deep_ocean`, `warm_ocean`,
  `frozen_ocean`/`deep_frozen_ocean`, `river`, `beach`): shipwrecks, ocean ruins, buried treasure. Mod biomes are in no
  vanilla structure tag, so nothing vanilla places on land.
- **Arda's ground grows only what is ours.** No vanilla features at all, so the country is furnished by the mod's own
  tree features through `neoforge/biome_modifier/tree/arda_*.json` (broadleaf, woodland, Imladris) and by
  `plants/arda_cover.json`, which lays the new ground cover over every land biome in the `arda_cover` tag. Chunks made
  before the change keep what they have, so a seam is expected at the old edge.
- **The default trees are grown, not laid.** `natural_tree_1..3` are `Feature.TREE` configurations now, shaped by
  `OpenGrownTrunkPlacer` (limb settings in `LimbsConfig`) from the three templates kept in `structure/natural`;
  `NaturalTreeFeature` and its feature registrations are gone. The measurements they were built from: bole 10/13/40,
  foot 0/2/3, limbs 5/6/10 with one or two more, crown radius 5/11/19, templates 13/24/46 tall. Unseen in a world -
  check a Trollshaws or Eryn Vorn wood for the foot reading as roots, for a crown that still reads as a rounded dome,
  and for a limb ever ending inside a neighbouring tree's trunk.
  **Arda's worldgen is read from the pack's JSON**, not from the code bootstrap: `TolkienConfiguredFeatures` and
  `TolkienPlacedFeatures` only feed the data generator, and `./gradlew runData` has to follow any change to a
  configured or placed feature. Missing that is what stopped Roads 7 loading - the generated JSON still named the
  retired `tolkienmobs:natural_tree_1..3` features, and the registry refused it as unbound. `runData` rewrote just
  those three files (`written: 3`), so the rest of the pack is unchanged.
  **The trees need their ground filter.** `NaturalTreeFeature` judged soil itself; `Feature.TREE` only knows what
  the placement gives it, and the tree placement's heightmap is `OCEAN_FLOOR`, which ignores leaves but counts
  logs - so with no filter a tree stands on the last trunk's top and a wood grows to build height (measured: 3,091
  wood columns above y 150 in 61x61 at the ruin, crowns at 319). The three placed features pass `Blocks.OAK_SAPLING`
  so the sapling's own survival test is the ground rule. Roads 7's already-generated chunks still hold the stacked
  trees; the fix shows in chunks generated after it.
- A build already standing in an older world keeps its old seat; the sweep does not re-seat it.

- **A road's note is checked, not believed.** `Progress` writes `at` (where the road met the build) and `block`
  (what stood there) with `down`, and `RoadCommands.standing` only takes the note while that block is still the block;
  `down()` and `RoadJob.restore` both go through it. This is why the crossing never started in the Scouting World:
  `.../overworld/data/tolkienmobs/roads.dat` held `rivendell -> down` with no probe, `Dressing.start` therefore saw
  `roadDown`, the ring was dressed, `_dressed.txt` was written `1 1`, and no pass was ever planned. The old note is now
  stale by construction and the crossing is planned on the next load. `/tmroad build` answered
  `Index 2 out of bounds for length 2` because `ALREADY_DOWN` carried a two-number far side; it carries three now.
- **Mallorns no longer grow in Imladris.** The valley is `tolkienmobs:imladris`, so `#tolkienmobs:arda_mallorn` (that
  biome, and only it) carpeted the city's own floor in golden crowns the cut cannot clear: a cut lays its pieces and
  leaves the country beside them. The tag names `tolkienmobs:lorinand` now. Chunks already generated keep their trees,
  so the current world's valley stays wooded until those chunks are remade.
- **The drape no longer floors the ring with leaves.** Its skin was the motion-blocking top of the column - a leaf
  block on a wooded column - and it laid that back down at the cap. The skin now comes from under the crown and the
  tree above it is cleared with the column.

- **Arda's sky is y 320, not 319.** `arda_type` is `min_y -64, height 384`, so `level.getMaxY()` is 320 and the last
  standable block is 319. The city cut's guards compared a piece's top against `getMaxY()` as if it were standable, so
  tops at exactly 320 were laid; `lay()` now clips every piece with a `BoundingBox` whose top is `getMaxY() - 1`, and
  both guards refuse `>= getMaxY()`. The `sectionPos out of bounds` errors in the 18:17 log came from those writes. The
  `DUMMY block entity` warnings at y 320-326 in `Scouting World` are already in those chunks (from a city raised six
  blocks higher, before the pin moved down); clipping stops new ones, but the old records stay until those chunks are
  remade.

- **The test world's above-sky records are gone.** `Scouting World` carried 1,436 chunk sections at section Y 20
  (y 320-335, 442 of them holding blocks) and 4 block entities at y 320-326, all from a city laid before the cut was
  clipped to the sky. `build/tmp/ceiling_entities.py` (scan|strip, untracked, rebuilt on demand) reads the Anvil region
  and POI files directly and drops `block_entities` with y >= 320 and `sections`/POI `Sections` with Y >= 20, backing up
  the files first. Run over `run/saves/Scouting World/dimensions/tolkienmobs/arda`: 5 regions rewritten, 4,800 chunks
  compared against the backup with `clean(original) == live` holding for every one, so nothing but those records moved.
  Backups at `build/tmp/arda-region-backup/20261107-190006` and `.../20261107-190141`. A section's `Y` is an absolute
  section coordinate (-4..19 here), stored as a **signed byte**: read unsigned, -4..-1 look like 252..255 and a filter
  on "Y >= 20" deletes the bottom four sections of every chunk. That mistake was made and restored from the first
  backup before the strip above.

- **The valley's crowd is counted, not re-stood.** `Campaign.crowd` stood `VALLEY_FOLK - stood` bodies on every pass
  while `stood` counted only the marks answered that pass, so a full city (all marks taken, `stood` 0) stood 24 new
  elves every second: the 19:39 session's files hold 3,439 Elves, 2,570 of them `valley_folk`, and 5,211 items, with
  `Can't keep up` climbing from 2s to 8.4s. It now counts the loose walkers first (`looseFolk`), and `nearby` searches
  the home radius rather than 6 blocks so a wanderer is not doubled. The bodies the old loop made are still in the
  world's `entities` files and will keep the test world slow until they are stripped; the ceiling stripper's approach
  works on `entities` region files too.

- **The test world's folk are back to 24.** `build/tmp/arda_folk.py` (report|strip|verify) splices each chunk's
  `Entities` list in place, so nothing but that list moves. Arda held 10,353 entities: 5,211 dropped items, 3,439 Elves,
  2,570 `valley_folk` and 620 bodies wearing a mark slot. Kept: one body per mark (15 slots), the three Elves of the line
  (tagged by name, not by slot, and untouched) and six loose walkers - 24 in all - and every item removed, 8,377 entities
  gone, 1,976 left. Verified by rebuilding all 2,823 chunks from the backup and comparing bytes, and by checking every
  other entity type's count is unchanged. Backup: `build/tmp/arda-entity-backup/20261108-033557` (the original) and
  `.../033640`. A mark's tag is `tolkienmobs:valley_folk:N`, **not** the base tag: a first pass that matched the base tag
  kept 620 slot duplicates, was restored byte-for-byte from the backup, and was re-run with the slot rule.

- **One body for each Elf of the line.** Arda held 45 Glorfindels (all stacked at 5614,250,-3210), 43 Elronds and 29
  Legolases: a keeper the campaign could not find within its reach was stood again wherever the player happened to be.
  One body per name tag is kept - the one nearest the stand `VALLEY_STANDS` writes for it around `rivendell`'s centre,
  5206,136,-3033 from `places.dat` - and 114 were dropped (all `entityttmhuman`, the keepers' own body type; the 270
  Elves are untouched). Kept: Elrond at 5183,132,-3027 (inside the valley), Glorfindel at 5614,250,-3210 and Legolas at
  5400,158,-3218 (both still outside the 128-block reach, so a visit will stand the valley's own beside them). Nothing
  points at these bodies by UUID - `quest_ledger.dat`'s givers name none of them - so the stand is the whole of the
  choice. Verified by rebuilding all 2,823 chunks from the pre-cleanup backup (10,353 entities) with both rules
  applied, and matching the live bytes chunk for chunk; only items, surplus folk and the 114 humans changed.

## What the player is likely to ask next

Trees back in some form; the seam between old and new chunks; whether the two crags stood; whether the camp road
finished; widening the sweep's reach per build; reporting what a sweep came to.

The drawing's default country is `Country.TEMPERATE_SEA`, so any cell a fill misses is sea, and the shore is read
off the ground now, not off the blended cell height, which is what stopped a cell drawn at 70 beside a sea drawn at
38 from painting an eighty-block beach across country nowhere near water. `build/tmp/grid_holes.py` rebuilds the
grid from `draw()` (fills and the mountains' `line()`) and lists the landlocked holes left: (13,22)-(16,22), (22,22),
(27,19), (24,13)-(24,14), (23,5)-(23,6), (15,27), (21,31), (34,30), (37,30)-(42,30) and (2,19) are still sea in the
middle of land. Firien's western cell and its southern foot are filled.
`build/tmp/fill_all.py` rebuilds the grid from `draw()` - the fills and the mountains' `line()`, which the
first checker silently skipped - and fills every cell closed in by land on five or more of its eight sides,
with one override: the cell below Lindon goes to Lindon, not to Ered Luin behind it. Filling a cell uncovers
the next, so it sweeps until the list settles (four sweeps, 44 cells); `build/tmp/check_holes.py` exits non-zero
if any landlocked hole is left. The drawing's default is `Country.TEMPERATE_SEA`, so a country without a `fill`
is sea, and the inland seas the map does draw (Rhun, Nurnen) survive because they reach open water.
Every `Country` in the enum now has cells but `IMLADRIS`, which is the valley laid over the Trollshaws' cells
and blended back into them, and the three seas, which are the grid's default. `python build/tmp/check_holes.py`
prints the countries with no cells as well as the landlocked holes, so a new country is added to the enum, given
a `fill`, and checked there.

A country's tags and the lists it inherits are read together. `UMBAR` was registered `hostile` but took `gondorSpawning()`, so the corsairs' country had no monster in it at all; it takes `haradwaithSpawning()` now. `MINHIRIATH` is `density/sparse` but sat in `#tolkienmobs:arda_broadleaf`, so culumalda and lebethron grew over an empty coast: it is out of that tag, and its tints are grey-green (`#82996a` grass, `#6e8060` leaves) instead of blue. `CARDOLAN` and `ENEDWAITH` keep Eriador's and Dunland's ground and spawns, and `DORWINION` keeps the Gladden's peaceful list - all three match their tags. No surface rule names any of the five, so they wear `overworldLikeFloor()`'s grass over dirt: right for the four temperate ones, but Umbar is hot and rainless beside Harad's sand, so if it should read as a desert coast it needs a `SAND`/`SANDSTONE` rule of its own.`runData` after touching any of these writes just the biome files (`written: 2` here).

Umbar's floor is its own rule now (`TolkienSurfaceRules.umbarSurface`): sand below y 68, the biome's olive grass
above it, and sandstone under the sand. The fallback gave it grass over dirt in a rainless country beside Harad's
dunes. `arda_cover`'s short grass will not stand on sand, so the low coast is bare and the hills are the only green
- and only chunks generated after the rule show it. Surface rules are code, not JSON: no `runData` is needed for
this, unlike a changed configured or placed feature.

The tag-versus-list check now covers the older countries, and the rule is: a country's registered tags, its spawn
list, its surface rule and its `arda_*` memberships are read together. Ered Mithrin (`hostile`) had no monster and
takes `hithaeglirSpawning()`; Eregion (`peaceful`) had Firien's Hurons and has `eregionSpawning()`, Firien's list
without them; Brown Lands and Emyn Muil (`density/sparse`) are out of `#tolkienmobs:arda_broadleaf`; Khand
(`desert`) shares Harad's sand rule now; Brown Lands and Eastern Waste (`wasteland`, `dry`) get `wastelandFloor()` -
coarse dirt, gravel outcrops, grass only by water. `SOUTHRON` and `ORC_TRADER` were the only land spawns without a
charge, added where they spawn (`haradwaithSpawning`, `mordorSpawning`, `nurnSpawning`), which rewrote the spawn
costs of haradwaith, khand, umbar, mordor, ash_mountains and nurn. Dagorlad is tagged plains and temperate, so it
keeps the grass floor. The ten water biomes give their fish no spawn charge either - vanilla's river and ocean do
the same, so that is not a defect.


Umbar's shore split was checked before anything else touched it: `UMBAR` is drawn at 70+/-8, sea level is 63 and
Belfalas (68+/-5) is the northern neighbour, so `UMBAR_SHORE_HEIGHT` 68 leaves a five-block beach and stops the sand
at the Belfalas border. It was left as it was. Dagorlad and the Brown Lands are not a hostile/peaceful pair - neither
carries `HOSTILE` or `PEACEFUL`, and both spawn `dagorladSpawning` (brigand, warg, mordor orc, uruk, swarm), so both
are hostile in effect; Dagorlad joined `wastelandFloor()`, which is surface only. If one of them should be peaceful,
its tag and its spawn list both need changing, and neither was.

The canon floors added to `TolkienSurfaceRules`: `waterBed()` for river, river_anduin, river_celduin, river_gwathlo,
river_lune, lake, lake_evendim, long_lake, sea_of_rhun, sea_of_nurnen; `snowFloor()` for forochel, forochel_slopes,
ered_mithrin; mountain stone for moria; `highlandFloor()` for cardolan, rhudaur, dunland, ettenmoors, barrow_downs,
emyn_muil, trollshaws, eregion; `wetFloor()` for enedwaith, gladden; `woodlandFloor()` for mirkwood, mirkwood_spooky,
eryn_vorn, old_forest, fangorn, lorianand, wild_wood, firien. Eleven countries stay on the shared fallback -
belfalas, dale, dorwinion, eriador, gondor, ithilien, lindon, minhiriath, rhun, rohan, shire - because grass over
dirt is the ground they want and a rule would only duplicate it. `MUD` was missing from the class's block constants
(`TolkienSurfaceRuleData` has its own), which the first compile caught.

The two floors are now measured rather than estimated. A throwaway datagen provider built the overworld's own
`RandomState` (`RandomState.create(settings, noiseParams, 0L)`) and sampled `Noises.SURFACE` over every column of the
drawn Dunland cells (columns 12-23, rows 22-26) and Mirkwood cells (columns 27-33, rows 5-18), walking y across each
country's drawn height band (`Country.height()` plus or minus `relief()`), 552,960 and 903,168 samples. The noise is
near-normal about zero with a standard deviation of 0.31 and a range near -1.5..1.45, which is why the first bands
were off in both directions: stone at 0.45+ was 8.3% of the highlands, and podzol over -0.15..0.25 was 46.9% of the
woods. The bands in the code measure 3.0% rock, 7.9% coarse dirt and 89.0% grass on Dunland, and 2.6% coarse dirt,
24.9% podzol and 72.4% grass on Mirkwood. `steep()` is not counted, so cliffs add stone on top of those shares. The
probe and its datagen registration are deleted; these numbers are the record.

Elladan's camp at Thorenhad came out as a tower of camps, one every tick, up to build height, with Elladan
standing on the ground at the bottom of it. Two placements were each laying themselves on their own output.
`Campaign.standTent` is the fallback for a camp whose own pinned site never rose: it seated the cut on the highest
column under its footprint and laid it again on every look while `Sites.raisedAt` answered null, and once one copy
stood the heightmap answered with that copy's roof, so every look was one camp higher - its own note claimed laying it
twice was laying the same blocks, which stopped being true the moment the ground was the camp. `Sites.build` had the
same shape in a narrower window: the site's `built` record was written after the keeper, animal and stone were dressed,
so a throw at the door left the walls standing and unrecorded, and the next tick seated the next copy on the roof.
`standTent` now seats on the place's own y - the ground the name was walked on, which cannot move - and records the
camp through the new `Sites.laid`; `Sites.build` writes its record straight after the sweep and before the dressing.
Worlds that took the tower keep it: it is blocks, not data, and it has to be cleared where it stands.

A build also cannot be laid over itself now. `Sites.standsAt` reads a template's own blocks from its file and
answers whether a majority of them already stand at a corner, scanning a band of six blocks above and below because
the hands that seat a cut measure different ground (the world's pass takes the highest column, the campaign's fallback
takes the place's own y). `Sites.build` asks it before underpin and placement, `Campaign.standTent` before its own lay,
and `standingBlocks` now reads a site's record through the same guard so a record a few blocks off still counts as
standing. Road pieces and city pieces are not guarded on purpose: their positions come from a route or a band rather
than from measuring their own ground, and re-laying one is the same blocks in the same place.

The ruin came out of the ground packed with it. `ttmruin_large_2` is 14 by 29 by 13, every one of its 182 columns
has a block at y 0 (101 grass, 38 dirt, planks - the cut carries its own pad), and 101 columns are hollow above that.
Seated on the highest ground under the footprint, the hill stands inside those walls, and `Sites.sweep` only ever cut
*above* the roofline: for a column carrying the keep that is y 26-28, so the ground came up to the keep's top and the
ruin read as a mound. The sweep now reads the template's own top *and* bottom per column and its block list in one pass
(`Sites.lines`), cuts the inside down to the build's floor, skips any block the template itself stands, and leaves
fluids alone; the ring outside still eases from the roofline to the country. The place needle now asks
`Sites.stoneGround(level, place)`, which resolves a place's `<place>.stone` pin to the milestone standing on it (or the
ground it will stand on), so Rivendell's arrow goes to the stone at 5499, 150, -3080 rather than the city pin at 5200,
75, -3370.

Bandrith's and Hoklond's crags stood above the ground, and the seat was written down instead of measured:
`SUNK_CRAG` was `Seat(15, 16, ...)` and `plane()` clamps its sink between those two numbers, so every crag went down
fifteen or sixteen blocks. `troll_cave.nbt` is 56 by 36 by 56 with 42,292 blocks: layers 0 to 9 are full 2285-block rock
slabs, the chambers and tunnels are air from y 6 to 22, the way in is cut through the east flank at y 6 on x 46, z 31,
and the grass crown is at y 27. Fifteen blocks therefore buried the tunnel eleven blocks under the hill's own flank and
left the crown eleven above the highest ground - a mound sitting on a hillside with its entrance inside it. Seating by
the crown instead (this was tried first) buries the way in by the hill's own height, which is a crag nobody can enter:
the mouth has to be the datum. Each crag now has a `CRAG_MOUTHS` entry - the mouth's column and the layer its floor
sits on, read off each of the four cuts with a flood fill of their outside air (y 6 for the three the stone asks in,
y 15 for Matuna's lair) - so `plane()`'s doorway branch sets the mouth's floor at the ground the road arrives on. The
crown is still read through `lines()` and cached in `CROWNS`, now as the cap that bounds how far a crag may be set in.

The wood ruin's door was the corner of its floor plate rather than where its road arrives: `DOORWAYS` named `(0, 0)`
while the `WOOD_RUIN.end` pin is corner + 5 east on the same north face, so the seat measured the ground five blocks
west of the way in and the travel stone stood there. The plate is uniform at layer 0 - every one of its 182 columns has
a block there and the layers above are open across the whole footprint - so the arrival column is the valid sill, and
`doorX` is now 5. The same audit found the camps' doors right, found `SEAT_SINK` (4) clamping the doorway sink on
broken ground, and found Barachen's `.keeper` and `.end` pins two layers lower than the same arithmetic gives at
Elladan's camp.

## Where we stopped - 9 November 2026

The chapter-one ground is settled pending a playtest: the crags are seated by the mouth each cut opens (`CRAG_MOUTHS`,
measured off the four templates) and the wood ruin's doorway points at where its road arrives. Both are blocks, so
crags and ruins already standing in a world keep the seat they were given.

The biomes have just been audited against canon - `docs/arda-biome-canon.md` holds twelve findings with what each fix
costs, and the list of what already stands. **None of it is applied**: it is waiting on a decision, not on work.

The next sitting is chapter two's builds and roads, done the way chapter one's were - a `key_structures` cut per
beat, a site pinned in `Sites`/`Pins`, a place canon'd to its country, and the road laid from the previous build -
and then the cutscene that carries the player from chapter one's last page into chapter two's first. The pages are
already written (`docs/chapter-two-state.md`, pages 26-36) and never walked; the seam scene is that file's open
decision 3, and is the one piece of chapter two with nothing built at all.

- **Reeds and irises stand in the Gladden, and three lands have their trees.** `tolkienmobs:reed` is `SugarCaneBlock`'s
  plant under the mod's own model (`models/block/reed.json`, `textures/block/reed.png`, both brought in from the
  owner's `reed-1.19` pack), placed three high along water through `plants/add_reeds`; `tolkienmobs:flower_iris` is a
  flower like the others, through `plants/add_flower_iris`. The three new woods are `pine` (Eryn Vorn, Mirkwood
  Spooky), `holly` (Eregion) and `cypress` (Ithilien), each with `leaves_*` and `hardened_leaves_*`. Their leaf
  textures are first drafts: `textures/block/leaves_pine.png`, `leaves_holly.png`, `leaves_cypress.png`. If a texture
  is replaced, only the PNG changes - the blocks and the features do not care.
- **The reed's trunk is sugar cane's logic, not ours.** `TolkienReedBlock extends SugarCaneBlock`, so it keeps sugar
  cane's support test and growth; only the shape and the texture are the mod's. Anything that looks wrong about reeds
  standing where they should not is vanilla's own rule, not a new one.


## The seam scene - recovered, 10 November 2026

The parked note this replaces was written before the old pack was read. The scene, its script and its
cast are recovered and written up in `docs/departure-scene.md`: `FellowshipDepart1` on Gandalf (1138
ticks) spawns the household, speaks the departure and sends the eight walking, then `ElrondBye` says
the last five lines. It was never a camera scene - the old world has no camera in it - so the port needs
a tick-driven beat list and a keeper that can walk to a mark, and no camera machinery at all.

Two things are still owed by the owner: the marker set and its legend (one spot per body, plus the road
the nine leave by), and eight faces - `bilbo`, `gandalf`, `aragorn`, `boromir`, `frodo`, `samwise`,
`merry`, `pippin`. Elrond, Legolas, Gimli and Gloin are already drawn. The ground the seam was blocked
on is done: the camp and the pass road are pinned and built (87f2217a).

Nothing of the scene is built yet.


## Where we stopped - 12 November 2026

Chapter one's ground is settled and the chapter-two line stands where `docs/chapter-two-state.md` says: eleven
pages, 26 to 36, written and wired, never walked. Since the last note the shelter camp is seated on its pin and
seen in game, the goblin cave cuts and dresses itself as the player comes near, the key is stocked from
`tolkienmobs:chests/dungeonkey`, and the courtyard seam is built and driven from `Campaign.tick`.

The one thing to read before touching a structure file: a structure block never saves a structure void cell, so a
**sparse** template is how a build leaves its ground alone, and a dense one takes a cube of hillside with it. That
is `docs/structure-void.md`, with `tools/structure_void_audit.py` to check any file or the whole tree.

Next, in order: walk 26 to 36 in a fresh world; gate the inner keep's stair behind the plans; then place the ten
`ch2_*` templates and the eight key templates that have no ground. The Pinnath Fenui came off the list on 12
November 2026 - the goblin fires are a place now, written from the cave's pin rather than described as a
bearing. The full open list, with what each one costs, is the last section of `docs/chapter-two-state.md`.
