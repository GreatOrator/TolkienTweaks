# Tolkien Tweaks — Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]
### Added
- **The city generates from the jar, as a cut of 584 pieces.** A `tolkienmobs:piece_layout` structure carries a
  cut's corner and its piece table - a template and the step from the corner, for each of them - and places every
  piece there with no ground reading, no rotation and no terrain adaptation, so Rivendell stands from 5200, 75,
  -3370 as it was scouted rather than waiting for an operator. The table is not transcribed: the data generator
  reads the cut's own `.parts` file in the jar, so `worldgen/structure/rivendell.json` and `/tmcity band` put the
  same piece on the same block. The pieces cost nothing at world start - each places itself as the chunk it
  touches is made - and nothing is bearded under them, the floor being the map's own ground.
- **The city ships with the mod.** The cut - 584 pieces, a hundred and four megabytes of them, with the two
  tables that place them - is in the jar under `data/tolkienmobs/structure/rivendell`, so a world gets Rivendell
  with nothing downloaded beside the pack. `CityCommands` asks the jar first and falls back to a file the jar
  does not carry in `run/rivendell_art`, so a re-cut can still be dropped in and looked at without rebuilding,
  while a folder left over from an old cut can no longer quietly win over the shipped one. The marker
  `_laid.txt` stays in the game directory: it is where this world planted the city, not part of the cut.
- **A build can be pinned into the world's own generation, and Elladan's camp is the first.** Every structure the
  mod generates was scattered by vanilla's random spread, which cannot be asked for a particular block - its
  cells are a hash of the seed - so a place a page walks to could only be planted by hand. A `tolkienmobs:pinned`
  placement now answers for exactly one chunk, the one holding its anchor, and a `tolkienmobs:pinned` structure
  places a pool's pieces at that chunk's corner rounded out by the anchor's place inside it, seating them on the
  ground under them. `tolkienmobs:thorenhad_camp` is Elladan's camp, `key_structures/elledancamp`, pinned at
  Thorenhad's own cell middle, 4864, 0, -3328, in the Trollshaws; the anchor and the pieces are written from one
  `Pinned` line in `TolkienStructureProvider`, so they cannot drift apart. Generation costs nothing at world
  start, because a structure's pieces place themselves as the chunks they touch are made.
- **The road out of Rivendell is settled, and the notes say what it does.** Three records still described the
  provisional pass - the road "painted straight in and straight out along the span's own line", its shape
  "still being argued about" - which stopped being true when the approach became a search with a graded
  profile, eased corners and a half-step lip. They now record the road as built and as shipped: the pass
  lives in the jar beside the two bridge pieces, the alignment is the one thing a generation rule would have
  to bring with it, and rerunning the hand on the same ground reproduces the road.
- **The road no longer climbs what it cannot walk round, and no longer drops.** The bridge approach was
  forced straight for sixty-five blocks; a fresh generation stands a fifty-nine-block rock fin on the bank
  astride that line, so the graded road climbed the fin and plunged off its end sixteen blocks before the
  deck. The search now runs all the way to the launch onto the abutment and only the last sixteen blocks
  are pinned to the span's line, the profile is swept to at most one block of rise or fall a station so a
  fall reads as half-steps rather than a drop, and the column the alignment runs through is laid whatever
  the ground under it does, so the road narrows against a rock face instead of breaking over it.
- A road finishes on its step sweep rather than on the ground clamp, so no station keeps the two- or three-block
  riser the clamp was putting back after the sweeps had already pulled the profile a step at a time.
- The hairpin apron is laid only where the alignment doubles back, not at every bend of more than thirty degrees,
  so an ordinary corner no longer widens the band out over the bank it is turning beside.
- The road carries only its own three columns over water and leaves the rest of the band at the bank, instead of
  laying a stone-and-gravel shelf out onto the river at the wide stretches.
- **An ordinary corner is eased rather than hinged.** Every corner was cut back at most ten blocks, which
  is barely more than a hinge at walking speed. A bend now opens out as far as its legs allow, up to
  twenty-four blocks either side, so the road arcs round it and a walker carries speed through; a
  switchback stays tight at ten, since easing one wide would swing the road off the shelf it is on. The
  wider arc reaches ground the search never walked, so it is closed back until it stays off the river,
  out of the city's footprint, and on ground the corner's own cut and fill can answer for.
- **A step's slab is the lip of the terrace above it, not the foot of the one below.** The half-step slab went
  into the lower column, where a bottom slab fills the bottom of its cell: the walker dropped a block and a
  half onto it and still had to jump to climb back. The slab is now the higher column's lip, half a block under
  the terrace floor with the terrace below as the other half, so a fall walks as block, slab, block, slab in
  both directions. Only the two positive neighbours were compared as well, which left the slabs as a sliver off
  one corner of a diagonal step; the lip now follows every side of the terrace.

- **The switchbacks are hairpins, not hinges.** The searched walk turns within a single block, so its
  corners were right angles a walker could not take. The alignment is simplified to the vertices that
  carry its shape, and every corner left is now cut back ten blocks along both legs and joined by a curve;
  the surface is widened by two blocks either side where the road is turning, so the inside of a hairpin
  is an apron to walk round rather than a lip to fall off.

- **The road switchbacks where the hill is too steep to follow.** The descent paid linearly for height
  change, so on a uniform face it simply walked the fall line, and the fall line here is steeper than any
  road: the profile was left choosing between holes in the grade and embankments of stone. A step now costs
  its length, twelve times the square of any grade above a third of a block per block, and a little for
  every change of direction - so the walk runs along the contours, turns where the face is too steep to
  fall down, and comes back lower, and the road's profile is then the ground's own, smoothed.

- **The road hugs the hillside instead of spanning it.** Pinned between the path's 134 and the bridge's 76,
  the graded profile became a ramp: where the mountain fell faster than the ramp the road stood in the air
  on twelve-block stone columns, and every column whose cut exceeded the budget was left as a hole, so the
  stretches over the water's edge were laid while solid ground under the same road was missing. The profile
  now follows the ground - a moving average along the alignment takes the bumps out, and eight passes hold
  the result within twelve blocks of the ground - and the descent search pays linearly for height change,
  so a long gentle line is preferred to a short steep one.

- **The road's corridor is cleared of canopy.** Cutting a road through the trollshaws' woods took the
  trunks away with the ground but left their crowns hanging over the grade, so a cut stretch sailed under
  a floating roof. Every column the road lays now has leaves and logs cleared from one block over its
  surface to sixteen blocks above it, and the pass reports how many it took.

- **The graded road reads the ground, not the canopy, and its grade has the last word.** The first graded
  run reported a steepest step of eighteen blocks and stood stone columns up to the treetops:
  `MOTION_BLOCKING` answers with leaves, and the ground clamp ran after the grade clamp, undoing it. The
  road now reads past leaves and trunks to the soil, applies the cut-and-fill budget first and the grade
  last, and carries its last verge columns over the water on stone while its grade is above the water
  line rather than stopping short of the bank. The bridge also lost its last middle: the loop asked for
  eighteen columns of room when a middle plus the far end need seventeen, so the run closed one piece
  short and left open water in front of the far abutment.

- **The road is graded, and the bridge stands where the markers are.** The first road followed the ground,
  and the ground under the valley's own path is a wall face - 134 at the path's last block, 109 six blocks
  out, 74 at the river - so painted it read as a staircase down a cliff. It now grades: the pass searches
  its own way down the live heightmap with a weighted walk that prefers level ground and pays for every
  block of rise or fall, so it contours and switchbacks where the face is a wall, then gives that line a
  profile clamped to 0.45 blocks of rise per block of travel - cutting into the hillside above the line and
  standing it on stone where the hill falls away, to a budget of twelve blocks either way. Two anchoring
  errors in the bridge are fixed: a template turned through half a circle is anchored at the far corner of
  its own box, so the east end now lands on 5055 instead of eight blocks out in the water, and the middles
  are laid from the east cursor by their own width instead of from their west edge, which had left a gap of
  open water before the far abutment.


- **The road out of Rivendell, and the bridge over the Bruinen.** `/tmroad build` paints the approach
  from the path's last block at 5200, 134, -3034 down the wall and along z -3075, stands the bridge from
  the shipped `bridge_end` and `bridge_middle` pieces - two ends and four middles over the fifty columns
  between the bank markers at 5055 and 5006 - draws every pillar the pieces carry down through air and
  water to the bed under it, and carries the road straight out of the far abutment. Both ends are met on
  the span's own line, because a road arriving diagonally would need junction pieces the cut does not
  have. The surface is the one `docs/arda-roads.md` settled: three bands by distance from the walls, a
  per-block hash, and a bottom slab on the low side of every one-block rise.

- **The ground outside the walls is dressed to the build's own outline.** `/tmcity drape` - and the
  end of `/tmcity band` and `summon`, so it is part of laying the city rather than a second word -
  reads all 584 pieces' templates, takes the highest block of the build in each of the 554 by 512
  columns, and then cuts every column in a 32-block ring outside the footprint down to its nearest
  wall column and fills it up to it, easing back into the drawn country at the ring's edge.
  Water columns are left exactly as they are, which is what keeps the river's mouth and the
  spring's outlet open rather than filled to the walls. The outline comes from the cut, not from
  the world, so it measures the city and not the rock the city stands in.
- **The branch runs again, from the new mouth to the drawn Bruinen.** With the planted ground laid, the
  city's water still had no cut out of it: the massif stood at 318 across the hundred and forty-seven blocks
  from the west wall at 5200, -3067 to the drawn river at 5054, -3086. `BRANCH` is on and the course is those
  two columns - the city's water at 82 at the wall, the vale's 74 at the join - so the map cuts a twelve-wide
  channel whose walls climb from the water at 1.2 blocks per block out, and opens the massif along it at 0.6.
  The spring at the rear, 239 over a floor at 74, still has no cut of its own.
- **The city's own ground is drawn round the pin again, and the river is what is left.** The planted valley
  - its floor at 74, its massif at the crown 318 from the footprint's edge outward, and its empty north-east
  corner filled with rock - was switched off and still keyed to the old pin at 4352, -3833, which is why the
  ground round the planted city was plain vale and `/tmcity apron` had to invent a shelf 153 blocks up. The
  frame now follows the pinned corner 5200, -3370: its middle at 5476, -3114, the empty corner over
  x 5645..5753 by z -3370..-3311, and the mouth at 5200, -3067. The ground is laid again, and the branch
  from the mouth to the drawn Bruinen at 5054, -3086 is left uncut - the one piece of terrain still to draw.
- **The shoulder and the water line open the chunks they read.** A heightmap is only filled for a chunk
  the world has opened, and the shoulder reads rings up to a hundred and twenty-eight blocks past the
  walls while the water line reads a hundred and sixty: from the middle of a city this size that is far
  outside the chunks a player keeps loaded, so the ground out there read as nothing and the blend was
  written against a datum that was not there. `Sites.height` now opens the column's chunk before asking
  for its height, the way laying a piece already does.
- **The build's own water gets somewhere to land, which the shoulder cannot give it.** The apron reads dry
  columns only - a wet one is dropped from the rim datum and left exactly as it was - so where water leaves
  a wall the ground outside kept whatever height it had: the river leaves at 82 onto a vale already at 74,
  which it falls on its own, while the spring reaches the east wall at 239 over a shelf 124 blocks below it
  and simply hangs there. `/tmcity apron` now also runs a water line: every column of the footprint's edge
  that carries water is an outlet, and where that water stands more than twelve blocks over the first dry
  ground outside it, the ground is built up into a bank under it - a bed one block below the water, falling
  one block per block out until it meets the ground, or 160 blocks out. Nothing is cut and no wet column is
  touched. The report adds the outlets found and the columns built out.
- **The three new camps come out of their builder's footing.** `elledancamp`, `wild_tent` and
  `snow_camp` carried a ring of white, purple, black and magenta wool round their floor - the pinks are
  magenta in the template - with a target block at each of two opposite corners, one block inside a
  20-wide box. The ring is cut and each template cropped to what stands, so the footprints are 16 by 16
  rather than 20 by 20, and only the camps' own wool is left in them.
- **Rivendell's pin is the corner the raised city stands on.** `rivendell` moves from 5097, 75, -3010 to
  **5200, 75, -3370**, the low corner of the build put down in the scouted world, so the city covers
  x 5200..5753 by z -3370..-2859 with its crown at 318. Every column measured from the pin moved 103 east
  and 360 north with it: the water at 5200, -3067, the east wall's aqueduct at 5748, -2859, the spring over
  x 5708..5753 by z -3058..-3017, and the branch's legs at 5120, -2793 and 5800. The drawn river is the
  map's own and did not move.
- **The pin is the corner, not the middle.** `rivendell` at 5097, 75, -3010 is the block the cut's low
  corner goes on, so `/tmcity origin` and `summon` plant the corner there from whatever block the caller
  is on, and the message says so. The city covers x 5097..5650 by z -3010..-2499 with its crown at
  318; every column the valley prose names moved with the corner.
- **The unused valley floor says which planting it was cut for.** Its 544 by 502 profile, centred on
  4628, -3577, is the shape the valley had before the pin; the doc now says so rather than leaving it
  reading as ground the city stands on.
- **The city's own columns follow the pin.** Every world coordinate the valley's water, its spring and
  the walkthrough were described at was measured from the planting at 4352, -3833; with the city on the
  pin they move 468 east and 567 north, so the branch's mouth is 4820, -2963, the spring ends over
  x 5328..5373, and the mouth is 387 blocks from the drawn Bruinen rather than 681. The drawn water
  itself is the map's own and did not move.
- **`/tmcity` plants the city on the pin.** In Arda the corner comes off the `rivendell` pin rather than
  off the caller's feet, so `origin` and `summon` put the same 554 by 512 footprint in the same place
  from whatever block the caller is on, at the pin's own y, and the planting message says the pin
  decided it. That lands the cut's low corner at 4820, 75, -3266 and the crown at 318; nowhere but
  Arda changes, where there is no pin to obey.
- **The valley is pinned where it was scouted, not where it was worked out.** The scouted pin is
  **5097, 75, -3010**, read back out of the world's own `pins.dat` - `/tmpin` keeps a pin in the save and
  prints the source line, it does not write the source - and the planting corner is **4820, 75, -3266**,
  derived from the cut's own 584-piece table rather than from a remembered footprint. The first pass
  had the middle computed from the old planting, which was the right size in the wrong place.
- **The city marker is read once, not once a visit.** `CityCommands.plantedCorner` keeps the corner it
  read for the dimension and every write to the marker drops it, so dressing a planted city costs a field
  read on the tick rather than a file read every time a player walks near the valley.
- **Rivendell is pinned, and the valley stopped building its stand-in.** `Pins.SOURCE` holds
  `Map.entry("rivendell", new BlockPos(5097, 75, -3010))` - the middle of the cut's own 554 by 512
  footprint, planted from the corner 4820, 75, -3266 - and `Campaign` no longer lays the old square kit at
  all: `standValley`, its pieces, their seating and their stone-naming are gone. A visit to the valley now
  reads where `/tmcity` planted the city, stands the build's own marks and the crowd in it, and leaves the
  Elves on the pinned ground until a city is there. `/tmpin` prints its source line as a `Map.entry(...)`,
  which is the shape the table takes.
- **The apron blends from the rim, not from the build's corner.** `Sites.apron` reads the highest dry
  column on the ring just outside the footprint and ramps down from that, and it leaves wet columns alone so
  a river leaving a build keeps its channel. On even ground the two datum agree; on a hillside the shoulder
  now meets the highest ground at the wall instead of cutting a canyon - the trade is that a low side is
  filled up to it.
- **The Arda docs hold one copy of each fact again.** Rivendell's pages were listed differently in three
  places, and wrongly in all three: a page needs the city whenever its giver or turn-in is Elrond,
  Glorfindel or Legolas, which is 1, 2, 5, 6, 7, 8, 12, 13, 14, 15 and 2-1, 2-10, 2-11. The guide now
  carries the lists the code actually has, `arda-pins.md` keeps the table and the source-line format and
  leaves the commands and the scouting to it, and `commands.md` points at it rather than repeating what a
  pin changes. Gundabad is also noted as unpinned today, and Rivendell's ground as drawn rather than cut,
  since the city's own floor is behind `PLANTED = false`.
- **The pinning instructions are in one file.** `docs/arda-pinning.md` collects what was spread across
  several conversations: the `/tmpin` commands and what each does to placement, the two kinds of key and who
  reads them, the world-pin-over-source-pin order, the fifteen locations the two chapters ask for with what
  ground each needs, which five quarries need no pin at all, and the traps worth knowing. `docs/arda-pins.md`
  points at it rather than repeating the commands.
- **The cast's faces are written down.** `docs/arda-faces.md` records the order the renderer asks in -
  keeper, named file, alliance cloth, variant face - the slug rule (`Gloin` is `gloin.png`), the cast and
  the file each wears, and what the faction folders are for. It also records that `maelgrin.png`,
  `guthlac.png` and `zarabi.png` are byte-for-byte copies of the three `quest_giver_*` cloths: a herald can
  be redrawn on his own, but he will not follow a repainted cloth until the copy is taken again.
- **Gimli has a face, and four named files are repainted.** `middle-earth/gimli.png` closes the last named
  body that had no art - the dwarf beside Gundabad's well - and `elrond.png`, `gloin.png`, `legolas.png` and
  `ranger.png` are redrawn in place, so every named giver and the camp keeper's fallback now wear current
  art. All of them are 64 by 64 like the rest of the folder, which is what the human model's sheet is.
- **The three heralds have files of their own.** The faction folders' `quest_giver` skins are copied into
  the named-giver folder as `maelgrin.png`, `guthlac.png` and `zarabi.png`, so a herald resolves by name
  before his alliance's cloth. They are copies rather than moves on purpose: those three files are what
  every giver of the West, East and Harad wears, so moving them would leave every one of them without a
  skin and a warning on each load. The art is unchanged for now - the point is that each herald can be
  redrawn on its own without touching the cloth the rest of his front wears.
- **Barachen has a face of his own.** `middle-earth/barachen.png` is drawn and dropped into the named-giver
  folder, which is where the renderer already looks for a giver whose name it knows: a body wearing the
  West's work and the name "Barachen" is looked for as `barachen.png` before the alliance's cloth, so the
  art applies with no code of its own. Nothing else moves; a pack without the file still falls back to the
  West's giver skin in silence.
- **Elladan's camp is a cut of its own.** `key_structures/elledancamp.nbt` - 20 by 8 by 20, 940 blocks,
  no entities - is `Sites.ELLADAN_CAMP`, canon'd to the Trollshaws and pinned to Thorenhad's cell, and
  `Campaign.CAMP_TENT` lays it at the named camp in place of the Rangers' tent. The tent keeps both of its
  jobs: it is still what the camp errand pitches and still what the world generator stands in the wilds, so
  nothing else moves. `/tmpin build camp` and `elledancamp` name the new cut, and the scouting checklist
  counts it instead of the tent.
- **`/tmpin checklist` says how much of the map has been walked.** The command lists the fifteen locations
  the two chapters ask for - six names and nine buildings - each marked as pinned in this world, pinned for
  every world, or still left to the drawing, with a count at the top. It places nothing: it is the checklist
  of what scouting still owes, so progress is readable in game rather than by reading two chapters of pages
  and remembering which ones needed ground.
- **A location can be pinned where you stand, by `/tmpin`.** `Pins` is now saved with the world as well as
  written into the build: `/tmpin place <id>` pins a catalogue name at the caller's feet, `/tmpin build <name>`
  pins a template by a short name (`camp`, `ruin`, `cave2`, `gloincamp`, `castle`...) or by its own id, and
  both print the exact `Pins.SOURCE` line so a spot scouted once is kept for every world by copying. A world
  pin outranks a source pin, which outranks the drawing; `/tmpin list` and `/tmpin remove` manage them, and it
  answers to the same caller as the other arrangement commands, inside Arda only.
- **A location is pinned to the block it was scouted on.** `faction/Pins.java` is a table of hand-chosen
  points, read before the drawing by both of the things that place ground: `Places.ensure` puts a name
  exactly at its pinned block, skipping the cell survey and the spacing rule, and `Sites.survey` writes a
  building's spot down as given, leaving the laying to seat the footprint on the ground the world actually
  made. An empty table is the working default and behaves exactly as before. The pin is also the way a name
  stands where the drawing paints nothing - the valley, which no column samples as `IMLADRIS`, can be pinned
  by hand and stop holding the pages that need Elrond, Glorfindel or Legolas. `docs/arda-pins.md` records the
  two key forms and the chapter-one ground still to scout.
- **Ground is scouted before a location is settled.** `tools/arda_scout.py` renders a window of the drawn
  world at two to four blocks to the pixel, hillshaded from the sixteen-bit topography and with the river and
  lake masks painted over it - the painted terrain draws water as sand, so a river reads as a beach without
  the masks - under a 128-block grid labelled in block coordinates, so a spot is read off the ground instead
  of guessed from a cell. Each `--at X,Z,name` is marked on the render and reported as the ground a building
  would have to stand on: the height at its middle, the lowest and highest ground within the footprint, the
  drop across it, the share of it under water, and the distance to the nearest river. The first survey with it
  found the camp and the ruin flat and a hundred-odd blocks off the Bruinen, a dry 174-block bench for the
  valley, and the ford's pinned cell off the water by 1,155 blocks and off the drawn road besides.
- **`/tmtp` jumps to any of the drawing's 49 countries.** A quick map for walking the regions while no
  build is pinned: `/tmtp <region>` reads the country's middle from `ArdaMap.anchor`, puts the caller eight
  blocks over its ground - over the water, for the three seas - and moves them into Arda from wherever they
  were. Tab completion offers every name and `/tmtp list` prints them. The points are computed rather than
  typed, so they follow the drawing when a fill moves; a country whose build is settled gets one line in
  `TeleportCommands.LOCKED` and lands on the build instead. `docs/commands.md` records it, and
  `docs/arda-teleports.md` points at it.
- **The road's surface is specified: three widths, three bands, and a mix for each.** `docs/arda-roads.md`
  now records what a road will be laid in. Width is set per stretch as the builds are placed - wild at 7
  blocks, urban at 5, and rural at 3 for a spur - and the surface is chosen by distance from a pinned cell:
  gravel, coarse dirt, dirt path and grass beyond 64 blocks; cobblestone, dirt path, coarse dirt, mossy
  cobblestone and a little grass from 64 down to 16; and inside 16 that same mix with dirt path, coarse dirt
  and grass swapped for stone path, stone bricks and mossy stone bricks, which is the surface the mod's own village streets
  already use. The mix is picked per block from a hash of the column so it is seamless across a chunk border,
  and a one-block rise becomes a cobblestone bottom slab. Nothing is generated yet.
- **The roads, surveyed before any ground is moved.** `docs/arda-roads.md` records where the roads in Arda
  can come from - the canon map's line symbology, the places the mod already pins to cells, and the drawing's
  own grid - why they cannot be traced in pixels (the two references are in different projections, and road
  ink is the same ink as labels), and seven candidate routes with their cell waypoints. `tools/arda_roads.py`
  renders them over the reference terrain so the routes can be argued about first. Nothing is generated yet.
- **More of the drowned dead, and pools that hold a few of them.** A placement now searches nine blocks
  around its column instead of five and lays up to two bodies on separate beds, and four placements are
  rolled per chunk rather than two, so a deep pool is not left with a single corpse in it.
- **The drowned dead are laid in the pools again.** The bodies feature had been reading the block the
  placement handed it as the bed, but that column is the block above the ground - so it was never solid,
  and no body was ever placed. It now finds the water itself, within two blocks of the placement and ten
  blocks down, and asks a bed only to have two blocks of water over it.
- **Marsh pools lie in the fen instead of on it.** A pool's water line is now the middle ground of its own
  disc rather than the height of the one column the feature happened to start on, and the block or two of
  ground standing over that line is cut instead of left as an island: no more sheets standing above their
  shore, and no more dirt rings inside the water.
- **The Sea of Rhun ends in Rhun, not in the eastern waste.** The waste's fill runs the whole eastern edge
  of the map and was painted after Rhun, so the three columns east of Rhun's own cells came out as waste and
  the sea ended against it on that side. Columns 44-46, rows 16-22 - the shore east and north of the water -
  are painted Rhun after everything else, so the sea is ringed by its own country.
- **The drawing is measured against the reference the masks came from, and the offsets do not hold.** The
  table in `docs/arda-map-gaps.md` was measured from a map that is not in the repository; rendered over
  `Reference/ME_Terrain_albedo.png` - the painted terrain every mask is cut from - the eastern fills are
  already on the ground they name, and the Sea of Rhun falls at columns 41-46 rows 17-22, inside Rhun's own
  cells, not at the table's 40-43 rows 12-15. The fills are unchanged; `tools/arda_grid_overlay.py` renders
  the comparison so it can be repeated.
- **Mountains are ridges and gullies, not smooth hills.** The drawn ground was raised toward the crown by
  the mountain mask's brightness, and that brightness is blurred, so every range came out as a cone with
  nothing on it but its own gradient. Ridged noise is now folded into the climb over each range's flank -
  crests, spurs and the gullies between them, at a hundred blocks and at thirty - scaled by the square of
  the mask's brightness, so a plain beside a range is left exactly as drawn. The mask's whitest columns are
  still pinned at the crown, and a crag may stand up to about twenty blocks over it, within the ceiling.
- **The drawn ground runs in curves, not a grid of squares.** The heightmap was interpolated between two
  pixels, which turns every eight-block pixel into a flat facet whose contours run straight through it and
  turn at its edge - the squared-off terraces any drawn hill showed, whatever shape the drawing had. The
  ground is now a Catmull-Rom spline across four pixels, so each takes its slope from its neighbours and the
  facets join without a crease; the answer is held between the four pixels a column stands between, so the
  spline cannot overshoot a ridge or dig under a coast.
- **A lake lies at its lowest ground, and the rest of the sheet keeps its cliffs.** The level was the mean
  of the ground the sheet is drawn over, which sits partway up the sheet's own slope: every column under it
  was cut away, so the sheet ended in a dam of its own making several blocks high all the way round,
  Nurnen's the worst of them. The level is now the lowest ground of the sheet, so the water fills the basin
  the drawing gives it and the higher ground inside the mask is left as the land and the cliff it is.
- **A lake shore is measured in sixteen directions, and only low ground is cut.** Eight rays counted a
  diagonal step as one instead of one and a half, so the ramp's contours were square rings round the water
  whatever shape the drawing had, and ground standing well over the sheet was pulled down to it, which put
  a cone round every lake. The ramp is sixteen rays a block at a time, and a shore is only cut when it
  stands within six blocks of the water it faces.
- **A lake shore is a beach, not a wall.** A drawn headland, and the land just outside the sheet, now
  stand one block over the water where they meet it and climb a block for every block further out, so the
  rise in the middle of a lake and the ground along its edge walk down into the sheet instead of ending in
  a cliff. The grade never undercuts the bank that holds the water in.
- **Lake water shallows into its edge.** The bed is graded by how much of a column the drawn sheet covers,
  one block deep at the mask's edge and full depth inside it, instead of a flat shelf and a five-block wall
  that showed through the water as terraces; the lake id is also searched two cells out, so a rim column
  keeps its sheet's level instead of falling back to the ground it stands on.
- **A lake ends in a shore, not a cliff.** Ground more than four blocks over a sheet's level is left as
  land instead of being cut down to the bed, and water within that band shallows to one block at the edge,
  so Nurnen and Rhun meet their shores in water you can wade instead of a wall of stone. The load log now
  reports, per sheet, its level, how far the drawn ground rises over it, and how much of it falls inside
  that four-block band.
- **Each drawn lake is one level.** The lakes are measured once per drawn sheet and laid at that sheet's
  own level, with the bed cut to it, so the Sea of Nurnen and the Sea of Rhun are sheets of water rather
  than waterfalls down the terrain under them. Baked by `tools/arda_lake_ids.py`.
- **The Dead Marshes are a marsh again.** Emyn Muil's fill covered the Dead Marshes' whole rectangle - the
  two fills were the same cells, and Emyn Muil was painted last - so the marsh generated as Emyn Muil from
  edge to edge. Emyn Muil is now the row north of the marsh.
- **The marsh warns you when it deepens.** Each step of the dread puts a line on the action bar and a low
  sound with it, so a player being worn down can tell before the slowness has them, rather than discovering
  it at the bottom of the marsh with no idea how far in they are.
- **The Dead Marshes' dread deepens with the stay.** The dread now climbs a step at half a minute and
  another at a minute and a half, to a ceiling of two, using the same timer that brings the figures; it
  falls back to nothing when the player leaves and starts again from the bottom on returning.
- **Marsh figures end for everyone.** A figure now comes apart for any player who gets within six blocks of
  it, and carries a saved mark so a figure read back from disk - after a restart or a chunk reload - is
  discarded on its first tick instead of being left standing in the marsh as a real spirit.
- **The Dead Marshes keep their dead for those who linger.** After a minute in the marsh, figures of the
  drowned begin to stand out in the fog eighteen to twenty-six blocks away, facing the player, and come apart
  into soul-light when approached; they come more often the longer the stay lasts, and leaving resets it.
- **The Dead Marshes are haunted.** Corpse-lights stand over the pools around a player, a cry comes out of
  the water now and then, and the drowned dead stir where they lie - bubbles, a low sound through the water
  and a slow roll of the body - without any of them ever leaving the bed.
- **The Dead Marshes weigh on you.** A dread of the marsh itself takes the speed from your legs and the
  strength from your arm while you stand in it, with no wither and no blindness: nothing here is looking
  at you, the dead are only lying under the water. It fades a moment after you step out.
- **The great rivers are named.** `tools/arda_river_names.py` bakes the river mask's components into a
  names raster, so the Anduin, the Celduin, the Gwathlo and the Lune are their own biomes instead of all
  reading as the generic river; the rest of the drawing's water stays generic, because it fuses
  tributaries into one component and one name cannot stand for three rivers.
- **Rivers and lakes have biomes of their own.** Water used to read as the vanilla river wherever it
  lay; it now has a generic `river` and `lake` for the sheets the drawing does not name, and its own biome
  for each lake it does - Evendim, the Sea of Rhun, the Sea of Nurnen and the Long Lake - so a player can
  tell where they are standing from the biome name alone.
- **The Dead Marshes are their own biome.** The drowned battlefield between the Emyn Muil and the
  Dagorlad is no longer more of the Wetwang: `dead_marshes` closes the fog at forty blocks, darkens the
  sky and the water to the colours of a fen under cloud, and keeps the fell spirits out of its spawn list.
- **The dead lie in the water.** A `dead_bodies` feature lays fell spirits on the bed of standing water,
  two tries a chunk, and refuses any column that is dry or too shallow: they are laid flat with their AI
  off and no gravity, so they hold the bottom of the pool and never walk the bank.
- **The marshes hold pools of their own.** A `marsh_pool` feature digs shallow, level pools into the fen,
  two tries a chunk, kept inside its own chunk so the bodies laid after it find them: neither the drawing
  nor the mask has water in the Dead Marshes or the Midgewater, only in the Wetwang's channels.
- **The Midgewater and the Wetwang are marshes.** The fen east of Bree and the marsh at the Entwash's
  mouth are painted as marsh cells of their own, so the marsh biome reaches them rather than Eriador.
- **Lake Evendim is the mask's own.** Nenuial was thought to be the one lake of the west the mask draws
  nothing for, and was drawn by hand at Annuminas; the mask does draw it, so the hand-drawn ellipse is
  gone and the lake is the drawn one.
- **The halls are Moria whatever the surface is called.** The rectangle the biome answers in lies under
  a cell the drawing gives to Lorinand, so the country check refused it and the chamber read as Lorinand;
  the rock below the roof is the halls' own now, and the surface keeps its biome.
- **Rivers have sloped banks.** The water stands one block deep at the narrowed bank and deepens a
  block per block inward, reaching the eleven-block cap about twelve blocks out, instead of holding
  the full depth right up to the land - which is what made a river a square hole in the ground. The
  slope is measured from the cut bank, not the drawing's, or it would begin on dry land.
- **A river is at most eleven blocks deep.** With the channels a third narrower the old sixteen-block
  cap was a canyon under a brook; the bed still follows the channel's width, up to eleven.
- **The rivers are a third narrower.** The bake marks channels forty blocks across and more, which
  is a river with no banks worth the name; a sixth of the width is taken off each side, measured across
  the channel, down to a floor of three blocks.
- **The Barrow Downs are on the map again.** Dunland was painted after them and reached back over
  their two cells, so the country had a biome and nothing to stand in; the Downs are painted last.
- **Rivers and lakes keep their water.** Ground within twelve blocks of drawn water is raised to the
  water's own level where it stood lower, so a channel cut into a slope no longer floods the country
  on its low side.
- **The rock under the Misty crest has a room in it.** A chamber is cut beneath Moria's roof, so the
  halls' biome can be stood in instead of only read on the debug screen.
- **There is a teleport sheet for the map.** `docs/arda-teleports.md` lists one point per country,
  at the middle of the largest block of cells it is drawn in, with the height the masks give that column
  plus eight - so a region can be looked at from a command instead of by walking to it.
- **Moria is ground of its own, under the mountain.** The poster puts the halls below the Misty
  Mountains, so the surface over the Dimrill Dale keeps the range's biome and its drawn ground and the
  rock beneath that crest, below its roof, reads as Moria: dark, dry, close-fogged, and reached by
  digging rather than by walking. The biome source answers it by height, which is the one place the map
  has of putting a country at a depth instead of a place.
- **Cardolan, Minhiriath and Enedwaith are their own countries, and Eriador keeps Bree-land.** Eriador is
  the whole land west of the Misty Mountains, so one country carrying its name labelled the Shire, Arnor's
  old realm and the empty coast at once, and the poster's three southern countries had nowhere to be asked
  for. The masks keep ground only on rows 22 and 23 south of the Shire - the row below is sea - so the
  three are painted on the quarter-cell grid there: Minhiriath's grey empty coast west, Cardolan's downs
  between the Baranduin and the Greyflood, and Enedwaith's wet interior against Dunland. Eregion stays
  Hollin's name, under the mountains east of them.
- **The drawing has quarter-cell patches, and Umbar and Dorwinion are on it.** A cell is 512 blocks, so
  a haven or a shore of vineyards could only ever be a whole county: Dorwinion was Rhun's steppe and
  Umbar the Haradwaith's dune. The same drawing is now also painted at 128 blocks, laid over the cells
  for countries smaller than one, while the 48 by 36 grid keeps every calibrated coordinate - the valley,
  the place spacing and the canon cells are untouched. Umbar is a dry warm coast with brown hills behind
  it and Dorwinion a green warm shore on the Sea of Rhun's north-west corner.
- **The map's weather follows the climate reference, and every biome now has its data.** Temperature and
  rainfall are taken from the Koppen zones the climate map draws instead of being guessed per country: Cfb
  oceanic down the western seaboard, Dfb continental across Mirkwood, Dale, Rhudaur and the Trollshaws, ET
  tundra on the Blue Mountains, Angmar, the Ettenmoors, Ered Mithrin, the Iron Hills and the Misty crest,
  Csa round Gondor, Ithilien and Belfalas, Cfa in Rohan, BSk in Rhun, the Brown Lands and the Dagorlad, and
  BWh from Mordor through Khand to Harad. The generated biome data had gone stale with twelve biomes missing
  from it entirely - a world using any of them would have failed to find the biome - and `runData` writes all
  of them now.
- **Ithilien, the Grey Mountains and Rhudaur stop borrowing a neighbour's ground.** Ithilien read as
  Firien's hills, Ered Mithrin as the Iron Hills and Rhudaur as the Barrow-downs, so three countries on the
  poster had no weather, colour or life of their own: a warm southern land between the Anduin and Mordor's
  wall, a grey cold range north of Mirkwood, and cold troll-fells under the Ettenmoors.
- **Nine countries and their biomes are on the map, and Rhun and the empty east get their own.** Dale below
  Erebor, Eregion west of the mountains, the Ettenmoors, Eryn Vorn, the Brown Lands, the Emyn Muil, Belfalas,
  Khand and the eastern waste were all on the poster and not in the dimension: they read as Eriador, Firien,
  Dunland, Gladden or the Haradwaith's dunes. Rhun is dry steppe now rather than Gladden's wet grass, and the
  eastern edge is a wasteland of its own instead of Harad's desert, which had laid dunes along the northern
  cold-lands.
- **The masks alone make the ground now.** The city's valley floor, its 318 rim, the shoulder and the
  branch cut out of them were laid over the drawing at every column within reach of one footprint, which
  put a 244-block pit into ground the masks read at 154 to 173 and then argued about the walls of it. They
  sit behind one switch, off: the world is the drawn height, the drawn rivers and lakes and the drawn
  mountains, and a build is to be sited against that ground rather than the ground bent round a build.
- **The massif gives way to the river's own ends, not only its length.** The give-way read a column
  beyond the course's start as having no water to fall away from, so the mouth - water at 82 three blocks
  from the rock - was the one place the wall was left standing, and the join the same. A course's ends are
  water too, the city's at the mouth and the Bruinen's at the join, and the ground climbs away from them at
  the same grade as from the length.
- **The crown's summit rolls a few blocks below its own height.** A top standing at exactly 318 is a table:
  every contour on it is the crown's own outline, so the mountain's edge reads as a cut however the flank is
  graded. Three blocks of roll over the ridge's own scale make it a surface without raising the ceiling.
- **The massif gives way to the branch at a mountainside's grade, so the river no longer leaves the city
  between two walls.** The rock stood at the crown wherever the city's floor rectangle ended - 318, against
  the branch's water at 82 - and the water was cut through it as a slot whose walls went up at 1.2 blocks
  for each block out until they met the plateau. The ground now climbs away from the branch's own course at
  0.6 blocks of height per block, about thirty degrees, over the whole bank: the river leaves through a
  valley mouth, and the rock either side of the join rises as a slope. The plateau is only lowered where the
  slope would otherwise cut a step into it, which is what the removed corridor scaling failed to do.

- **The river now leaves Rivendell where the build's water leaves it, and reaches the Bruinen.** The
  branch's course was a straight line from 4900, -3322 - a point with no build water within 48 blocks - to
  the ford. The city's water stands against its west wall at 82, so the course is four legs: out west of
  the wall, south round the valley's own side, east below the city, and in to the drawn Bruinen at 4995,
  -3311. 1,104 blocks, continuous and twelve wide, falling the eight blocks to the vale's 74 at the wall
  and meeting the drawn river at its own level. The wander now falls to nothing at each corner, so the
  channel is one river rather than a river with a step at every turn.

- **The branch's water starts at the build's own level, 82, not two blocks under the floor.** The city carries
  its water out at the floor plus eight - mapped from its 584 pieces, 302,049 water blocks whose surface at
  the mouth is 82 - but the branch was initialised at 72, ten blocks lower, so the water at the mouth stood
  over a channel it could not run into. The branch also starts at 4900, -3322, which carries no build water:
  the city's low mouth is the west edge at 4352, -3530, and its east edge carries only a high aqueduct.

- **The river's corridor is open again at Rivendell's exit.** Within 24 blocks of the branch's bent course the
  valley floor runs out and the climb fades back in, so the water leaves the city between two slopes rather
  than through a notch cut in a wall. The corridor was built, softened and then dropped with the old valley
  profile; it is back beside the wall profile, and measured to the nearest point of the course so the bend
  is not walled either.
- **The build carries water after all.** 763 `minecraft:water` blocks across 306 of the city's 584 pieces, not
  the none an earlier note claimed: that is the pool and channel inside the city, and the ground has to meet
  it at the build's own mouth rather than the other way round.

- **Vanilla's water lakes and water springs are gone from Arda.** They are placed by the biome rather than the
  map, and a placed lake fills to a level of its own - which is the sheet of water standing three or four
  blocks over Arda's ground, and the pond sitting inside the valley. Both are filtered out of the biome
  builder and removed from the sixteen generated biome files; lava lakes and lava springs are untouched.

- **Water stands at the ground the drawing gives it, not at a graded average.** The surface was the mean of
  the drawn ground over ±64 blocks for a river and ±192 for a lake, which lifted water over the land wherever
  the drawing dips - a lake sheet three or four blocks above its own shore. The drawing's water is already
  drawn at its own level, so the top of the water is now the column's own ground with the bed cut under it,
  and a river follows the land down instead of being averaged across the country round it.

- **The reference set is sorted out, and so is the world a test needs.** The new canon map is a different
  projection from the masks - the coordinates for Rivendell land on Swanfleet on it - so it is a list of
  places rather than a layer; the terrain albedo is the painted source the masks were cut from, and the rom
  curves are finer computed relief. `docs/arda-masks.md` now says what each is good for, and
  `docs/rivendell-state.md` warns that a save older than the bake shows the old ground. `tools/arda_view.py` renders what the water arithmetic answers for a patch of
  the map without loading a world.

- **The river depth cap is settled at sixteen, and it is not provisional.** The mask's rivers really do run
  that wide in canon, so the wide bands are the drawing being right rather than something to narrow down to
  a channel; the note now says so, instead of leaving the cap looking like an open question.

- **The mask rules are written down.** `docs/arda-masks.md` now carries the decisions behind the terrain -
  the mountain mask's white pinned at 318, the river and lake masks as solid placement, the height map as
  the locator, river depth following width up to sixteen, and eight blocks to the pixel - with the open
  questions beside them, so the reasoning is not left only in the conversation that produced it.

- **A drawn river's bed is as deep as the channel is wide.** A three-block channel is three deep in the
  middle and a five-block one five, rising a block per block from each bank, in place of the flat four
  blocks every river used to get. The bed is capped at **16**: the reference paints its rivers as bands
  thirty blocks and more across, and a bed as deep as a band is wide is a canyon, not a river.

- **A mountain's whiteness is its height again, pinned at 318.** The range mask is read over the drawn
  ground at eight blocks to the pixel, interpolated, and scaled by the mask's own brightest pixel, which
  is the pin: a drawn summit and the city's crown stand at the same sky, black leaves the ground as the
  heightmap drew it, and every column between takes its share of the climb, which is what turns a bright
  patch of the mask into a range with flanks rather than a plateau.

- **River and lake banks fall where the drawing put them.** The two masks are baked as the share of each
  eight-block cell the drawing marks instead of one sample taken nearest, and the game reads that share
  interpolated between cells and calls a column water at a third of one: an oblique river used to be a
  staircase of eight-block steps, and its bank now follows the drawn line. No cell the drawing touches is
  left stranded without water beside it, so a thin tributary is still a channel.

- **The ground is the heightmap's own shape, read at eight blocks to the pixel.** The bake went from
  sixteen blocks to the pixel to eight, so ridges and river bends are twice as sharp, and the mountain
  mask no longer lifts a bright column toward the crown: that lift flattened every ridge it touched to the
  ceiling, and the height rasters already carry the ranges, so the mask is reference and probe only now.

- **The reference masks are pinned with a probe.** `tools/mask_probe.py` answers height, river, lake and
  mountain brightness at any block, and the nearest river to one, straight from `Reference/` at the
  rasters' own resolution - so a build or a river can be placed against the map rather than against
  anyone's recollection of it. `tools/arda_masks.py` now names the folder `Reference`, as it is on disk.


- **The Bruinen joins at 4995, -3311, and the mask is read finely enough to say so.** The river layer is
  baked again at three blocks to the pixel over the corner the city stands in (`bruinen.png`), and
  `ArdaMasks.river` reads that bake before the sixteen-block one inside its window: the nearest water to
  the outlet is 95 blocks east at 4995, -3311, not the 5008, -3328 the coarse pixel suggested, and the
  branch's join is moved onto it.
- **The branch winds from the city to the Bruinen instead of running straight.** Its ends are fixed - the
  build's outlet at 4900, -3322 and the joined drawn river at 5008, -3328 - and the course between them
  wanders up to 18 blocks on one and a half turns, with the channel, the bed and the gorge cut following
  it. A straight cut from one water to another is a canal, not a river.
- **The mountain stands at the crown at the city's walls again.** The floor is 544 by 502 inside the
  554 by 512 footprint and the rock is at 318 from its edge outward, which is the shape the valley had
  before `fef98351` turned the climb into a bank a hundred blocks out; `VALLEY_STEEP`, the shelf and the
  river corridor are gone with it. `/tmcity origin` still caps its base at the valley floor, because the
  corner is now under the crown.
- **The valley's floor runs out along the river, so the branch leaves the city on flat ground.** With the
  floor ending inside the south wall, the climb stood a bank across the branch's own course; the floor
  now follows the course for 18 blocks either side of it.
- **The floor runs three blocks under the build, and out past the drawn Bruinen.** A shelf of any
  width leaves a seam of flat ground the city does not stand on, so the valley's floor now ends three
  blocks inside the walls on the west, north and south and the climb starts under the build's own outer
  band. East it runs to 5040, past the Bruinen the map draws at 5008, and a drawn river now answers
  before the floor as well as before the massif - otherwise the floor would bury the river the branch
  runs out to meet.
- **`/tmcity origin` plants on the valley floor, not on the climb under the corner.** With the terrain
  starting inside the walls the ground at the corner is a dozen blocks up the hillside, which would have
  stood the whole build in the air.

- **The mountain's foot stands at the wall, ten blocks over the build's base.** The shelf is gone: the  floor is cut to the footprint and the climb begins at the first block outside it from 84, so the  city's own lowest band is under the hillside instead of standing in a strip of lawn. The east keeps  its hundred blocks, which is where the branch runs out to the drawn Bruinen.- **Four blocks of shelf, so the city's lowest band is inside the hillside.** Twelve still left a step  of floor between the walls and the climb, and the build's ragged edge standing in it. At four the  ground is already at 84-90 when it reaches the lowest band, so the city is laid into the hill rather  than set down beside it. The east keeps its hundred blocks for the river's mouth.- **The hillside comes back to the walls on three sides, and the east keeps its shelf.** A hundred  blocks of lawn all round left the mountain a field's width from the city. The shelf is twelve blocks  on the west, north and south - enough to keep the build's ragged edge out of the rock - and a hundred  on the east only, because the drawn Bruinen meets the branch there at the floor's own level and a  climb in between would put the river two hundred blocks above the city.- **The city stands on a lawn, not at the bottom of a pit.** The valley's floor was cut to the build's  own footprint, so the mountain's foot began in the same block as its walls: the ground climbed  immediately outside the build all the way round, and the city sat in a trench that hugged it. The  floor is the footprint plus a hundred blocks of level country on every side (`VALLEY_LAWN`), so the  climb starts a hundred blocks out. The `shoulder()` shelf goes with it - it stood rock at the crown  inside the build's own empty corner, which the lawn makes unnecessary.- **The city is planted on the ground under its corner, and the valley's water runs below its floor.**  `/tmcity origin` used the caller's own altitude, so a corner planted from the air left the whole build  hanging over the valley with the ground falling away from it; it now takes the ground height at the  corner's column. The vale's floor was eight blocks *above* the city floor, which put the country's  lowest ground higher than the valley's water and left a river unable to leave: the vale's foot is the  floor at 74 and the branch runs at 72, out of the build's own south edge at 4900, -3322.- **The branch from Imladris meets the Bruinen instead of ending at a ford the map does not have.** The  Bruinen the map draws runs into the massif the valley was planted over, so the rock buried it and the  branch had nowhere to go: a drawn river now answers before the mountain does, off the valley's floor.  The branch is the fork's lower arm - out of the valley at 4900, -3312 and east to the drawn Bruinen at  5008, -3328 - and the drawn river cuts its own gorge through the rock from there.- **The branch from Imladris keeps water under it all the way to the ford.** Its last stretch shallowed  to nothing, so the river stopped a little short of the crossing it is meant to feed: it now keeps two  blocks under it however shallow it comes, and narrows to seven tenths of its width instead of half.- **River beds slope from the middle out instead of lying flat with a cliff at the bank.** Every block of  a river used to sit the same seven under its surface, so the water met the land in a wall and the river  read as a square of water cut into the ground. The bed now rises from the deepest point to the water's  edge, and a drawn river's depth is measured off the drawing itself - eight probes out from the column,  full depth once three quarters of them are water. The Bruinen also narrows to about half its width as  it comes to the ford and shallows to nothing there, so it ends in a crossing rather than an edge.- **The mountain rises out of the ground at Rivendell's feet instead of standing beside it.** The rim  began at the crown, so the first block outside the city's floor was the top of a two-hundred-and-  forty-four-block face: whatever the range did further out, what met the build was a wall. The rock  now climbs from the floor to the crown over ninety blocks, so the city sits in a mountainside.- **The drawn range no longer stands as a blade in the vale, and the floor is the build's own footprint.**  The mountain mask is a ridge sixteen blocks wide, and raising it where the vale had already brought  the country down left a thin wall of rock alongside the city; the vale's blend now runs after the  mask, so the ridge stands only where the country does. The floor rectangle is 277 x 256 from the  valley's centre - exactly the build - so the rock meets the walls instead of leaving a ledge round  them.- **The mountain's cliff stands outside Rivendell's walls instead of through them.** The valley's floor  was 272 x 251 blocks from its centre, but the build is 277 x 256: the massif's inner face, which starts  at the floor's edge at the full crown, was standing five blocks inside the city's own outer wall, so a  wall of raw rock rose out of the build where the ground had been cut for it. The floor now covers the  build with four blocks of ledge to spare, and the rock begins where the walls end.- **Arda is a vanilla-height world again.** The dimension was 512 blocks tall, putting its ceiling at 447  - a hundred and twenty-eight above anything the overworld can reach - to leave the massif room over  Imladris. The ground round the city moves instead now, so the dimension is 384 again: the highest  block is 319, the city's crown at 318 stands one under it, and the world is one a player can walk the  height of.- **The country round Rivendell settles to the vale instead of standing on the city's rim.** Past the  massif's foot the heightmap is blended toward the height the city's water sits at, over nine hundred  blocks, and the massif's fall is shortened from four hundred to three hundred and forty so its foot  lands on the ford. Where the map is above the city the ground comes down to it, and where the city has  been raised the ground comes up; either way the valley belongs to the country round it and the river  leaves it at grade rather than in a cut eighty-four blocks deep.- **Rivendell stands as high as the world it is made in allows.** The city and its rock were planted at  74 and 318 in a world whose sky was 319. A world made since Arda was raised to 512 has a hundred and  twenty-eight blocks of room above that, so the valley floor, the crown, the shoulder and the outlet's  water all rise by whatever the ceiling gives - and by nothing at all in a save that has no room,  which keeps the ground it was made with. The generator reads its own ceiling and does this once,  before the first column; a 512 world gets floor 202 and crown 446, a 384 world gets 74 and 318.- **The Bruinen descends the course the way the country does.** Its profile is read forward from the  outlet now - the surface takes the ground wherever the ground has fallen under it and never rises  again - rather than being the lowest ground downstream, which held the whole course at the depth of  the ford. In the raised world that turns the ravine into a channel that is deep at the wall and at  grade by the ford.- **The Bruinen's water is read off the ground, not set at the ford.** The channel's height at any  point on the course is the lowest ground between it and the ford, capped by the build's own outlet  at 82, and no constant is left at the far end. In the valley as it stands - floor 74, country 156 -  there is no ground under those 82 blocks for the river to slope to, so it runs level at 82 and the  cut does the descending; once the valley is re-seated the same rule puts the water on the ground and  the river slopes to the ford on its own. The profile is read once and kept, under a lock, because  chunks are generated in parallel.- **Arda's ground is the reference heightmap now, not the drawing's flat countries.** `height.png` is  baked at sixteen blocks to the pixel from `Final Height.png` and read interpolated, so the country  rolls and climbs where the map does instead of standing at one height per cell: the Shire at 116 where  the drawing had 68, Rohan at 170, Mordor's plateau at 224, the Misty Mountains at 178, the ground at  Imladris 160 - from the sea's own 63 up to the crown at 318, with the mountain mask's white still  sharpening the crests. A column the map calls sea keeps the drawing's bed, because the map's zero is  the sea's height and a bed there would be a dry ocean.- **The massif's ridge comes down to the city's crown.** 318 is one block below the ceiling of a save  made before Arda was raised to 512, so the old ridge of 380 plus or minus 45 was being cut off flat in  any world that was not made since. The ridge still wanders in and out, but its top is the crown itself  now - level with the roofs it shuts in, and with the mask's white.- **No river runs below the sea.** The mask's rivers are laid at the grade of their own ground, and near a  coast that grade falls under sea level; the surface is held at 63 there, so the last stretch of a river  meets the sea rather than digging a trench beside it.- **Mountain ranges take their height from the reference mask instead of from the drawn bands.**
  `ArdaMasks` reads the mountain mask baked to sixteen blocks to the pixel over the world's own frame,
  and a column's brightness - white at the crest, black on the plain - lifts the ground toward the
  crown: a full-brightness column stands at 318, the height the city's own massif tops out at, so the
  mask's ranges and the build reach the same sky, and a black column stays exactly where the drawing
  put it. Ranges now follow their real courses: the Misty Mountains are a
  wandering wall rather than a five-point line with a half-width of two cells, and the Grey Mountains,
  Mordor's ring and the White Mountains stand where the map puts them. The drawing still says what
  the country is and what its ground sits at; the mask says how high it stands.

- **Rivers and lakes are the mask's now, and their water stands at the grade of the ground they cut
  into.** The seven rivers the drawing named as polylines are gone, and so are the two inland seas it
  laid down as rectangles: the river and lake masks answer instead. Each column of water sits at the
  average height of the ground about it - within five blocks of its own ground on 87% of the channel
  measured - so a river falls from the mountains to the sea the way the country under it falls,
  instead of standing at one level from end to end with a cliff at each end. A river's bed lies four
  blocks under the water and a lake's six, and a lake never drains below the sea's own height.

- **`tools/arda_masks.py` bakes the masks into the mod's assets.** It crops each 8192 mask to the
  world's frame at sixteen blocks to the pixel - 1536 by 1152 - using the calibration in
  `reference/work/calibration.json`, and writes `assets/tolkienmobs/arda/{rivers,lakes,mountains}.png`,
  a hundred and sixty kilobytes together. The game reads whatever is in the assets, so the masks can be
  re-baked without touching the code, and a build without them still makes a world.

- **The gorge is the width the build gives its river: twelve blocks, five to the left of the course and
  seven to the right.** The channel the city carries out of the west wall is twelve across with its middle
  block on the line the outlet and the ford draw, and not symmetrical about that block. The cut is measured
  across the course now - five blocks one way, seven the other - rather than as a radius of a fixed
  half-width, so the river leaves the build at its own width instead of widening or pinching a block at the
  mouth.

- **The Bruinen leaves the city in a gorge and runs to the ford, instead of a hillside sliced by a
  trench.** The build carries its water out of the west wall at 82 with its bed seven blocks under it, a
  little above the valley floor, and outside the walls the massif stands at the city's crown - so a channel
  cut the way the drawing's rivers cut one (every column flattened to 63) met two hundred and thirty blocks
  of rock and came out as the terraced cliff at the front of the valley. The river is cut as its own gorge
  now: from the outlet at 4356, -3528 to the ford the drawing pins in the Trollshaws at 3840, -3328, with
  the water falling from the build's 82 to the level the map's rivers stand at, a bed seven blocks under it,
  walls that rise a little over one block for each block out instead of standing sheer, ribs in the walls so
  they are not contours, and a channel that shallows into the crossing over the last fifth of the run. The
  cut takes the massif's flank and the country beyond the ridge alike, so the ground outside the west wall
  slopes down to the water rather than crowning over it. Columns now carry the height their water stands at
  rather than assuming the world's own, which is what lets the river leave the city at 82.

- **The mountain meets the city at the city's own roofline, all the way round.** The last change put the
  foot inside the build, but the climb still started at the valley floor, so the ground the build met was
  240 blocks under its own crown and did not climb back to 318 until 140 blocks out from the walls - the
  city stood in a pan with a cliff of its own making at the rim, and the transition the build was meant
  to embed into was a hundred and forty blocks away. The rock now stands at the crown of the build
  (318) where it meets it, on every side, and climbs to the ridge from there: 4 to 5 blocks inside the
  footprint, which is where the floor's rectangle ends, the ground outside the walls is level with the
  ground inside them, and the city replaces that rock as it is planted, so what is left is the walk from
  the build onto the mountainside rather than a step down onto a lawn. The corner the mountain stood in
  still reads as it did, because it was already standing at the crown.

- **The mountain's foot stands inside the city, and the apron that dressed the join is gone.** The valley
  floor was 586 by 544 blocks for a 554 by 512 city, which left sixteen blocks of flat ground outside
  every wall for a ramp to be walked across - the wide, stepped apron the last rounds kept producing,
  whatever the flank above it was doing. The floor is 544 by 502 now, a few blocks smaller than the
  footprint on every side, so the rising ground starts inside the build: the lowest band is laid into the
  rock, and the rock shows wherever the build has nothing of its own. Planting no longer runs the skirt
  round the walls either, since a ramp walking the ground down to the city's base would cut away the rock
  the build is meant to sit in; `/tmcity apron` is still there for a city on open ground. There is no
  join left to dress, so nothing has to be tuned to make one look natural.

- **A world keeps the height it was made with, so an old save cuts Arda's mountains off flat.** Arda was
  raised to 512 blocks so the massif round Imladris could stand over the city's crown, but a dimension's
  height is fixed the moment its world is made, and a save copied from an older one carries the old height
  with it. The world this round was measured in ends at y 335 (25 sections, -64 to 335), so every summit
  the map draws above that was cut off at the ceiling: a range that stops in the sky, which is exactly
  what a flat-topped mountain looks like and very hard to tell from a range drawn badly. Nothing in the
  terrain could be tuned out of it. The generator now says so once, naming the ceiling it found and the
  height the massif wants, rather than leaving it to be guessed at.

- **The massif stands closer to the city.** 220 blocks of climb put the summits 190 to 270 blocks out from
  the walls, which is further than the valley is wide; they are 150 to 210 out now, at about 1.8 blocks of
  rise for every block walked.

- **The mountain no longer ends in a cliff of its own making, and its flank is a mountainside rather than
  a wall.** Two things cut the massif off. The rib noise on the flank was still 12 blocks high where the
  climb met the ridge and dropped to nothing beyond it, so there was a sheer face running the whole length
  of the ridge that nothing had asked for; the ribs now fade out before the ridge as well as in from the
  floor. And the climb of the last change put the same 306 blocks of rise into 130 blocks - three up for
  every one out - which is a staircase wall up close; it is 220 blocks now, so the ground climbs about
  1.4 blocks for every block out, and the far side runs down over 400 blocks instead of 260, so the range
  carries on into the country instead of stopping at a rim a couple of hundred blocks away.

- **The massif round Imladris stands at the city's walls instead of across a lawn from it.** The same
  306 blocks of rise were spread over 240 blocks of climb, which put the ridge two hundred blocks out and
  left the ground between the city and the rock as a wide, evenly stepped apron - the mountain read as cut
  away from the build. The climb is 130 blocks now and steepest at its foot, so the rock begins a few
  paces past the walls, and the flank carries 12 blocks of rib noise so the even contours that stacked as
  terraces break up into spurs and gullies.

- **The mountains round Imladris are drawn as a range instead of a ring.** One height at one distance,
  rising on four straight sides to one summit, is a wall with a pyramid in it - which is why the corner
  came out as a spike and nothing read as mountains wrapping round the city. The ridge now wanders in and
  out by a third of its distance and its summits rise and fall 45 blocks either side of 380, its foot is
  measured as a distance from the floor's rectangle so its corners are rounded, and the climb is a curve
  with some of the straight line left in it at the bottom.

- **The city's missing corner is a shelf of rock at roof height, not a point.** The build leaves x
  4797..4905 by z -3833..-3774 empty above its lowest band - the mountain was there - and the rock now
  fills that whole rectangle level with the roofs it meets, falling away outside the walls over 110 blocks
  into the massif's foot. It stops at the gap's own edges inside the footprint, so it buries nothing.

- **The mountain the city's north-east corner was cut into is put back.** The build carries no blocks
  above its lowest band in that corner - 73 by 47 of it, against the north and east walls - because the
  rock stood there rather than the city. The valley raises that rock again: from the floor at the notch's
  inner edges to the city's own crown at 318 in the corner, and back down outside the walls over 60 blocks
  into the massif's climb, so the corner reads as a spur of the mountain with the city cut round it.

- **Arda is tall enough to build over Imladris in.** The world was 384 blocks tall, so its ceiling was 319
  and the city's crown 318: nothing in Arda could stand above the city's roof, and the mountains meant to
  close over the valley had to stop a block short of it. The world is 512 blocks now, which leaves 128
  above that crown for ground to stand in.

- **The mountains round Imladris stand over the city instead of ringing it below the roof.** The crest
  was 300 - eighteen blocks under the city's 318 - and a quarter of a kilometre out, so from the city's
  top the ground read as a distant green edge and the corners looked bare. The crest is 380 now, 62 above
  the crown, reached over the same 260 blocks of climb, so the ridge crosses the city's own height 224
  blocks from its walls and tops out above its skyline; the fall beyond it is 260 blocks, so the
  mountainside comes back down into the Trollshaws at a gentler angle than it went up.

- **Chapter two's state is written down.** With the valley done and other work next, the quest line's
  place needed to survive the gap: `docs/chapter-two-state.md` records the eleven pages as built - givers,
  quarries, ground and rewards - where each piece of ground is pinned, the two decisions left open (gating
  the inner keep's stair, naming the Pinnath Fenui), what is not built after `2-11`, and the loose end in
  the camp's place, canon'd to the mountains while its site sits in Gladden.

- **A page that names a dwarf at his fire points at the fire.** The needle read a page naming a body as a
  place the map already has, and Glóin is not one of those: his camp is ground the campaign pitches when
  the page is handed over, so `groundOf` had no answer for him and the road showed no direction at all.
  The ground a page's body is standing on is now asked for before the place the map would name, which
  also puts Gimli's page on the hold rather than on the country the hold stands in.

- **`/tmcity` names the city's own ground, and can put you on it.** The corner it printed was a
  bookkeeping corner of the cut's frame, more than five hundred blocks off the build, so a city that had
  been laid in full read as an empty hillside. Every message now speaks in the box the pieces fill,
  `goto` stands the caller on the middle of it, and the corner remembers which dimension it was planted
  in, so bands cannot be laid in a country nobody is looking at.
- **The planted corner tells the valley where its people go.** The marked spots are written in the cut's
  frame, so `/tmcity origin` now hands that frame to the campaign as well: the city stands up with its
  crowd already knowing where they live, instead of needing the origins wired in a second place.
- **The city can be stood up in a world before it is packed.** The cut lives in `run/rivendell_art` with a
  table of where each piece goes, so `/tmcity origin` plants its corner where the caller stands,
  `/tmcity band` lays the next band - a whole city is two thousand chunks, and those are generated chunk
  by chunk - and the last band blends the shoulder with `Sites.apron`. The pieces are read from the folder
  rather than the jar, so a re-cut is picked up by walking back to it, and the lowest band is underpinned
  as it is laid.
- **The ground meets a build instead of stopping at its wall.** A template has no terrain adaptation of
  its own - `beard_thin` and `beard_box` are jigsaw settings read while chunks generate, so a structure
  laid at runtime met the hill in a cliff. `Sites.apron` cuts or fills the rings just outside a footprint
  toward the level the build stands at, tapering the difference so no column moves more than six blocks,
  and `underpin` is public now so the same pass can put ground under a piece that was sunk into a slope
  rather than set on one.
- **The city keeps a crowd, and none of it is anybody.** Rivendell's six walkers are enough for a square
  and not for a city seven hundred blocks across, which reads as a place abandoned rather than a quiet
  one. The valley now keeps twenty-four people at once: the fifteen spots marked in the build are
  answered first, one body to a spot and no spot twice, and the rest are put down wherever the city has
  ground under it. None is a giver, none carries a name, and each is homed where it stands so the crowd
  keeps its own quarter and gathers at the bell the city already carries. The count includes the marked
  spots, so fifteen marks leave nine to fill; the six named walkers and the three Elves of the line are
  still the only ones who answer to anything.
- **Chapter two walks on to the hold.** Past Glóin's fire the line runs `2-2`, the goblin fires of the
  Pinnath Fenui; `2-3`, the hold itself; and `2-4`, the plans read back at the camp table. Gundabad is
  ground the campaign pitches now - `key_structures/gundabad` stands in the Hithaeglir as a site the
  way the camp and the castle do - so "go to Gundabad" is a keep that is already standing when the
  player arrives, not ground they have to be told about.
- **A structure file cannot leave a cell alone, so the keep writes stone instead.** Written with air
  dropped, the keep went into the file with no rooms in it at all - 151,055 cells against a box of
  580,476 - because a cell the writer skips is a cell the game reads as air, which hollows the rooms
  and takes the hillside under them away at the same time. Both directions are written now: deepslate
  through the course under the five lifted rooms, and each tile's own air over it - the schematics'
  rooms were being poured full of that stone until their boxes were written as air first.
- **The flooded storey is flooded everywhere.** The water the pieces carried only stood where it
  happened to settle, and the stair's well is cut after they are laid, so its foot was dry. The builder
  lays the course itself now, after the lanterns and chains are out of the floor: one block of water over
  every walkable cell of the lower floor, the base of the spiral's stair included, stopping one cell
  short of the outer wall so the pool cannot run out where a piece opens on its edge.
- **The hold keeps what was set into it by hand.** The scan cut the inner keep's six pieces back out
  of the test world but not the six tiles the hold above is drawn from, so a throne put into the hall
  stayed in the world and out of the build. Both are cut now: a lifted tile brings back the thirteen
  courses it carries and the spiral the whole twenty-six, written at the height it was cut at so no
  padding of air stands over the roof, and holding back only the camo glowstone the builder sets into
  the floors. The throne of dark stone and its two chiseled Angainor guard posts are part of Gundabad.
- **A rescan no longer lights a room twice.** Pieces cut from the world carry the last build's camo
  glowstone back with them, and the grid went on setting a second one beside every group of them - 63
  extra in the storey. A stretch of floor that already holds a light is left alone now.
- **The joiner reads the flooded floor the way the walk does.** Driving a passage between the chambers
  counted only air as open, so a chamber reached across the water looked shut and went unjoined: the
  well, behind the stair raised in it, was left as a second pocket. Water is open in both now.
- **Chapter two runs to the end of its line.** The notes wrote eleven pages and four were built, so
  the West stopped at the plans. The other seven are in: the key-stone's vault at the source of the
  Bruinen, Gimli at the stair into the inner keep and Dugorim below it, the camps on the borders of
  Carn Dum, the Dark Sigil carried out of them, Elrond reading it in his library, and Drugoth's dragon
  wight fought in Carn Dum. With the eleventh page the front is settled, and the finale stays shut
  behind its own flag instead of opening the moment the chapter ends.
- **Gimli keeps the stair at Gundabad.** A named keeper like the Elves: his father's height on a
  man's model, stood from the hold's own site beside the spiral's well while the inner keep is the
  step in hand, and found by his tag afterwards so a reloaded world keeps one dwarf at the stair.
- **The hold is lit from its own floors.** A camouflage glowstone every fifteen blocks, set into the
  floor course at both walk levels, carries the rooms: light fifteen, wearing the look of the stone it
  stands among, so there is no fixture to see and none for a player to knock out in the dark. The
  lanterns and chains the geomorph sheets hang come out, and water - the flooded storey is lit through
  it and swims the same as air walks.
- **The bottom storey comes back flooded.** The six inner-keep pieces were re-cut from the placed build
  in the structure test world, so the water standing in the lower rooms is part of the hold rather than
  something one save did to it.
- **A placed build can be read back out of a saved world.** `tools/world_scan.py` finds the corner a
  template went down at, compares the world with the template block by block, and cuts the inner keep
  pieces out again, so a build decorated in the test world can be brought back into the pack instead of
  being rebuilt by hand.
- **The inner keep is all six of the example structures.** `2tl` and `2tr`, `2ml` and `2mr`, `2bl` and
  `2br` go down in the drawing's own two-across, three-down grid, laid whole from the stair's floor up
  with their stone re-materialled and their soil read as the hold's rock. Each carries three floors and
  its own stairs and ladder, so the storey under the hold is a small fortress of its own rather than two
  rooms beside the stair; the stair's well is carried empty through all six and the course under the
  hold's floor is solid over the lot. 3,621 cells on the inner route at the stair's level, every piece on
  it.
- **The inner keep is a level under the hold, reached by the spiral.** The chapter's notes ask for a
  fortress inside the fortress and no drawing gives ground for one, so the ground is two of the example
  schematics that came with the drawings - the open hall and the barred undercroft - laid twelve blocks
  under the south row, on the level the stair's landing already stands at, and re-cut in the hold's own
  stone: their stone brick and cobble would read as somebody else's ruin inside a dwarven fortress, so
  what is taken from them is the shape. Each tile is lifted by its own lowest floored course, the stair's
  well is carried empty through them and a door cut in its wall and a way cut west from it until it
  stands in a room, and the keep is one template still, 122 by 183 by 26.
- **The inner keep is the example structures laid whole, three floors of them.** `2ml` and `2mr` came
  as 61 by 12 by 61 cuts of the valley the rooms were built in - a gallery at the base, two floors over
  it, stairs and a ladder between - and the hold's inner storey is exactly twelve courses, so they go
  down as they stand, their stone re-materialled into the hold's and their soil read as the hold's
  rock. The spiral tile, which stands on the hold's own floor and so reached all the way down, was
  burying the rooms it stands on: below that floor it now writes only its own well. 1,695 cells on the
  inner route at the stair's level, both files on it, the landing inside 2mr's hall.
- **Set 4 is cut as a cave dungeon of its own, and nothing places it yet.** `tools/cave_tile.py` takes a
  panel of the cave sheet at one block per cell - ink rock, open cells room, twelve blocks of tile with
  the cave seven high - digs the quarter-point doorways in from each face, smooths the hatching drawn
  inside the rooms out of the mask so it does not stand up as pillars, and drives a passage to any
  chamber the drawing walls off. `ch2_cave_a` and `ch2_cave_b` are `r2c1` and `r2c2`, 61 square, 40 of
  40 door cells reached on both.
- **The dwarven tiles are Gundabad.** The six geomorph tiles are assembled by `tools/gundabad_keep.py`
  into one keep, `key_structures/gundabad.nbt` - the sheet's own arrangement, two panels across and three
  down, 122 by 183 by 26 - with the five surface rooms lifted thirteen blocks so every room and the stair
  down are walked on one floor. The file writes its whole box, stone under the lifted rooms and the
  rooms' own air above it, so the keep cuts its rooms out of the hillside without taking the ground
  under them with it, and the chapter's open question about which build stands for the fortress is
  answered with ground the mapmaker drew.
- **The way down is a helical stair in the middle of the room.** The lower-right tile now cuts thirteen
  blocks below its own floor and winds one tread per block about a newel, so the stair is the room's
  centrepiece and no tile gives up its floor plan for a stairwell. The old gallery flight is gone, and the
  reading of the drawing's symbols is corrected with it - the concentric arcs are a well or pool, and the
  ruled marks with arrowheads are stairs, pointing the way they run.
- **The dwarven hold is built from the drawings, not from the example tiles.** The geomorph
  sheets are split and read into a 61 by 61 plan by `tools/geom_extract.py`, and
  `tools/fortress_build.py` raises the first tile from that plan in Angainor masonry: a hall
  behind a five-wide gallery ring, so the eight quarter-point doorways meet any neighbouring
  tile without being matched.
- **The other five tiles of the hold are raised, one of them two storeys.** The winding approach,
  the crenellated vault, the four-pier colonnade and the water hall join the great hall, and
  `ascent` carries a seven-wide flight up to a parapetted gallery over a double-height hall - the
  first tile with a level change, and the pattern the rest of the vertical work can follow.
- **A finished page offers the way back.** A story page that is done and handed over more than
  forty-eight blocks behind the player shows `Return` in the book, which asks the server to walk
  them back to the giver. The server checks the page again, refuses to move somebody who is
  already there or ground that has been built over, and answers at most once every twenty
  seconds - so the chapter does not need a stone outside every hall.
- **Stones travel with the buildings that carry them.** A milestone block written into a template
  is named by the visit that lays it, instead of being planted at a coordinate written in code.
- **The plaza keeps its air.** The valley's square clears sixteen blocks of air over its own
  footprint when it is laid, so a hillside or a stand of trees no longer grows through the paving.
- **Travellers with untaken work move on.** A naturally arrived giver whose offer nobody took
  after half an hour is discarded along with the offer; one whose work was accepted stays, since
  the page is carried back to them.

- **A line that runs out says what is left of the war.** A settled page already named the body who
  holds the next one, but a front's last page named nobody: the book emptied and the chapter said
  nothing. The last page of a line now names every front that still has pages, and once none has,
  the Rider - so the campaign is never handed back to the player without a direction.
- **A player who joins carrying nothing is told again.** The pointer is repeated on login for a
  player whose book is empty, because the gap between two pages outlives a logout.
- **The rule is written down.** `docs/war-of-the-three-fronts.md` carries the no-empty-page rule
  and the three questions a new page has to answer; `AGENTS.md` carries the short form.

- **The beryl says how it is used.** The stone's tooltip now names the one thing it does: right-click to raise it aloft, because an item whose whole use is a right click over open air is an item that reads as decoration.

- **The last step of the stone's page says what it wants.** Standing on the ground a page of the beryl names now says so once, with the one thing the page left to be guessed: hold the stone aloft there, in the air rather than on the milestone. Raising it out of reach now names the place and how far off it still is, and a place is read as a circle of ground rather than a sphere in the air - a ford is walked on its bank, not on the water its middle was surveyed on.

- **The stone's page now carries a direction.** A page of the beryl that names ground - "hold the stone aloft in the ford" - is settled by holding rather than by arriving, so it has no spot, and the needle read it as a page that names nowhere. It now points at the ground such a page names, with its distance, and the ground is written into the world when it never was.

- **The page that names the ford now has a ford.** A story page handed over by a speaker, rather than offered by a herald, no longer goes unchecked: the ground it names is written into the world when it has never been, so the book's place has a direction and the needle appears. The ford itself is placed below the valley, down the road, instead of at the player's feet.

- **A speaker the book names is always somewhere reachable.** If the valley's square has no room for an Elf at the step they keep, they are stood on open ground in front of the player rather than not stood at all, and the log records it.

- **A speaker is stood beside their step rather than lost to it.** Glorfindel stands eight blocks south of the valley's middle, and that step is only walkable ground if the square was laid exactly there; in a wood it was not, the ground check answered nothing, and he was never stood at all. Keepers now look outward from their step for ground that will take them.

- **The beryl is put in the hand the moment Glorfindel says "take this".** The stone used to be handed over when the survey it is lent for was taken from him, which is a visit later, so the line that lends it could be heard with nothing in the pack.

- **The missing rider's report is carried back, not asked for again.** Finding the black horse now puts the report to Elladan in the book itself, because the bridle is the subject of that beat and not a page he hands over. Walking back with the saddle in the pack and speaking to him finishes it in one visit, instead of a player being sent to the camp twice for one errand.

- **A finished page says whose hand the next one is in.** Between steps the book is empty and the needle went blank, which left a player who had just settled a page with no idea whether the fault was theirs. Settling a page now names the body holding the next one and the place they are kept in, and the needle keeps pointing at that town until the page is taken.

- **A page to a built place is finished by standing in it, not by finding its exact middle.** The reach that counts as arriving was a flat twelve blocks, measured against the centre of the footprint - so a player standing in the ruin they had walked to, under its own milestone, was still a dozen blocks short and the page never settled. The reach now follows the building's size.

- **The road's needle is drawn above the hotbar rather than at the top of the screen.** Vanilla draws a boss bar across the top centre, and any boss in the neighbourhood covered the needle completely; the plate now sits where nothing in the game or in the pack reaches it.

- **A hall's milestone stands in the middle of the hall.** The stone a taken page plants at the hall was put at the herald's own feet, which is wherever he happened to be standing and could be the inside of the man himself; the room he stands in is read instead - the floor he can walk across without leaving it - and the stone goes at its middle.

- **A page now points the way to the ground it names.** An arrow at the top of the screen gives the bearing and a rounded distance to the place the page in hand sends the player, and points at the town a named body is kept in when the page asks for a person rather than a place; it is a hint and not an answer, so the country still has to be crossed, and it clears itself when the page is paid.

- **The chapter plants its own milestones.** A stone is planted on the ground a page names and beside the body that handed it over, the first time the page is taken, so the halls and Rivendell have a way back that is not the road again. A stone still only lists itself to a player who has stood at it, so travel is unlocked by arriving rather than by being sent.

- **The two pages that promise a conversation are written down as a fight on purpose.** Barachen says to speak with the Giant and Legolas offers an escort to the Wood-trolls, and both end in a body on the ground: it is the reference misleading the player, not the line softening a parley into a hunt. The pages carry that note now, so the next pass over them does not read the invitation as a stage direction.

- **The beryl's page no longer names a closing line it does not have.** A survey is settled where it is asked and is never read back, so its closing key was a page in the leaf that would have printed as its own name; the page carries no closing, as the cave search's does not.

- **The pages hand over what the reference gives them, and the choices are written down.** Most of the chapter's non-coin rewards were unpicked: the reference gives the first two pages bread, the report at the camp a piece of equipment, the cave hand-in a random reward and the last page an earring. They are Cram, Lembas, an ammolite sword, a potion of healing at the camp and the West's own charm now - drawn from the pack's food, drinks, gear and trinkets - real gear, not the decorative swords and picks that do nothing - with ores, the herb and a second trinket deliberately left out. `docs/chapter-one-rewards.md` holds the page-by-page table, the pool the choices come from and the reason for each, so the next chapter starts from a decision rather than from the pool again.

- **The herald asks a rank before he hands the chapter over.** A hall opens on a rank - nothing appears over the herald's head until the player has made a name for itself - and the chapter now begins at his bench, so the opening page is behind that rank like every other page he holds. The speakers along the line are not gated: Elrond and Elladan hand their own pages to whoever has come that far, and the only body asking for a name is the one standing in a hall.

- **The chapter starts at the West's herald, and the road takes it from there.** A hall is where a player is given work, and a chapter that opened in a valley gave the herald nothing to do but hand over the front's own three pages. He hands the opening page over at his bench - Elrond is still the one who hears it, at the end of the walk - and every speaker after that is the one the reference names: Elrond, Elladan, Glorfindel, Barachen, Legolas and back to Elrond. So the gate of the valley stops being a giver's post and the stand that put Aragorn on it is gone with the page that needed him.

- **Chapter one's pages are checked against the reference's own quest list.** The document carries the dialogue tree and, separately, a spec for each quest - its type, what completes it, what it pays - and the two do not read the same way. The spec makes the ford its own quest, completed where the beryl is raised, and the return to Glorfindel the quest after it, so what had just been folded into one page is two again. It also makes the three caves three area kills in the deepest chamber of each rather than three troll hunts: the troll that keeps a chamber is what clears it, and the token it drops is what the camp asks for, so the three hunt pages the old campaign wrote between the search and the camp are gone. The line is fifteen pages for eighteen quests, and each one now stands where the reference puts it.

- **Chapter one is walked as the reference walks it: a relay of speakers rather than a bench.** The campaign's pages were all handed over at a hall's herald, so the old campaign's eighteen quests lost the thing that makes them a story - each one handed on by the mouth that finished the last. Every page names the body who holds it and the body who takes it back now, and the line follows the reference's own order rather than its numbering: the trail is regained before the caves are searched. So Aragorn meets the player at the gate of the valley and Elrond takes the news, Elrond sends them to Thorenhad and Elladan takes it, Elladan hands the wisdom, the ford and the caves, Glorfindel hands the beryl and hears the ford, Barachen hands the Giant and Elladan hears it, and Legolas hands the Wood-troll and sends the tidings back to the valley. A giver who is not the one holding the page points the player at the one who is, and the rank a hall asks of a stranger no longer stands between a player and the Elf who opens the chapter. The Elves are stood by reading the line - the page ahead is theirs to hand over, the one in the book theirs to take back - rather than by the stage windows they used to be written into.

- **The cells Arda's drawing gets wrong are written down, with the chapter that will walk into each.** `docs/arda-map-gaps.md` holds the nine countries measured cell by cell against `middleearth.jpeg` - Mirkwood two cells east of the map, the Sea of Rhun seven, the Iron Hills and Mordor five, Rohan three, Nurn six and seven wide where the map draws four, the White Mountains twenty-four cells across where the map draws ten, Ered Luin running twelve rows too far south, and four columns of invented Harad down the eastern edge - so that the first chapter to survey ground in one of them opens with a decision about it. Nothing is pinned in any of those countries yet, so they can still be moved without moving a build; the file names what a move has to carry when one is made.

- **The campaign happens in Arda and nowhere else.** Pages, places and pitched ground were served to any level a player stood in, and a level that is not drawn by `ArdaMap` has no country to survey - so a walk page taken in the overworld could not be finished, and a site there fell back to the older pick of "a spot out in front of the player". The commissions now refuse the overworld outright (`Places.ensure`, `Places.tick` and `Sites.plan`), so the vanilla world keeps its own generation and its own business, and everything the campaign does happens on the ground it draws. Work is taken and counted there too: a page cannot be accepted outside Arda, and a creature killed outside it advances nothing.

- **The camp and the three crags stand in one place each.** Those four were the last builds with no country written down: they were pitched forty-eight to ninety-six blocks in front of whoever took the page, so two players on one server were handed two camps and two of every crag, and neither could find the one the other meant. All four are canon'd to the Trollshaws and pinned to a cell - the camp at (17, 13), the first crag at (17, 15), the second at (18, 16) and the third at (19, 16) - so every player walks to the same door. A camp stands one keeper however many players raise it, since the ground is shared and would otherwise have put two men at one fire. The hills themselves were widened to three cells by five to hold them, which is room the map has and the old two-by-three did not.

- **The names chapter one walks to have ground of their own.** A name was surveyed from the middle of its country, and a country's middle is one cell, so every name in a country came back in that one cell: measured on the drawing, Rivendell, Elladan's camp and Barachen's fire all fell inside a single Trollshaw cell fifty to a hundred and twenty blocks apart, three places a player crosses in a minute and each announcing itself over the last, and the rider's ruin came back beside the camp that sends the player to it. A name now keeps a cell of clear ground from every name already written, and the four the maps pinpoint are asked for their own cells instead of their country's middle - the valley at the north-western corner of the Trollshaws, Elladan's camp a walk east of it, Barachen's fire south of the road, and the ruin in the channels south of it. Measured after the change, Thorenhad stands seven hundred and ninety-two blocks south-east of Rivendell and Barachen's fire nine hundred and thirty-five south of it, with the ruin a cell of its own between the road and the camp.

- **The Trollshaws are drawn again, and Rivendell stands at the mountains' feet.** The drawing painted the Trollshaws into rows 12-14 of the map and then filled Firien over the same rows, so the country ended up with no cells at all: `anchor` answered the world origin for it, and every name the books put there had nowhere to be surveyed. In Arda that made Elladan's camp and Barachen's fire impossible to place - a walk page to either could never be finished - and dropped the rider's ruin into whatever land lay within a kilometre of the origin, which is the Shire. Firien now starts below them, so the Trollshaws are six cells on the western slopes of the Misty Mountains, and Rivendell is asked for in that country rather than in Eriador, whose middle is Bree and the Weather Hills and which therefore stood the valley four thousand blocks west of the mountains the maps set it under.

- **Elladan's skin is the size his model reads.** The art drawn for him was a 64x32 skin, the layout the old pack's art was drawn in, while `human.geo.json` reads a 64x64 sheet - so his sheet was sampled against the wrong scale and the left arm and left leg, which exist only in the lower half of a 64x64 skin, were drawn from empty pixels. The file is 64x64 now: the top half is untouched and the left limbs are the right ones mirrored face by face, which is how the game itself converts a legacy skin. It was the last of the pack's named skins still in the old layout - Elrond's, Legolas', Glorfindel's and the Ranger's were already converted.

- **Three crags, one for each cave the notes name.** The stone's cave page asked in the same hill three times: the campaign cut one crag and raised it again for Bandrith, Hoklond and Sholdroth, so three errands walked to three copies of one cave. `tools/make_troll_cave.py` now takes `--seed`, `--out` and `--vary`, and the pack ships `troll_cave_2` and `troll_cave_3` beside the first - the same generator asked twice more, with the hill's own radius and the chambers nudged as well as the seed redrawn, so the second and third read as other hills rather than as the first with other moss. `Sites.CAVE_VARIANTS` holds them in the order the page asks, the campaign pitches the crag of the asking it has reached, and a crag already walked into is replaced by the next one rather than walked to twice. The shipped crag still comes back byte for byte from the generator's defaults.

- **Elladan wears his own face at Thorenhad.** He is a camp keeper, and a keeper was given one file for the job - the Ranger at the fire - before any named skin was asked for, so the art drawn for him could never be seen. A keeper now looks for the file of his own name first and falls back to the Ranger's face when nobody has drawn it, which is what every named giver already did.

- **The rider's bridle is the proof of the report.** The dark saddle was an item with nothing to do: the notes have the player take the horse's tack in the ruin and carry it back to Elladan, so the page that finds the rider now puts the saddle in the player's hand, and the report at the camp is not heard without it - Elladan says what is missing instead of paying for a tale that cannot be told, and takes the saddle when he hears it.

- **The campaign ruin's chests are stocked.** The ruin was cut and raised in a world, so its chests came across with no loot table and opened empty. They point at the pack's own `tolkienmobs:chests/desolate_ruins` now, applied by `tools/stock_ruin_chests.py`, which rewrites the template as a typed tree so nothing else in it is reordered or dropped.

- **What comes after chapter one is written down.** `docs/after-chapter-one.md` records what the campaign already has, and the gaps that will bite in order - Gundabad with no place or keep, Annuminas with no Arnor on the drawing and no city in the pack, Moria and Forochel with biomes and no campaign ground, the East and Harad lines at three pages against the West's seventeen - so the next chapter starts from a decision rather than from a survey.

- **The ruin the rider is found at is the campaign's own ruin, not the world's.** `ttmruin_large` is a structure the world generator places across the wild, so building it for the *Missing Rider* page raised a copy of ground the player could already find out there and left the ruin the notes send them to as one of many. The page now raises `ttmruin_large_2`, which nothing ever generates, so the wall the bridle is found at is a building of its own; the generated ruin keeps its worldgen definition untouched.

- **Chapter one is the notes' chapter one, and the old pages are gone.** The West's line was a set of pages this campaign invented between the notes' beats; it is the notes' eighteen quests now, *Troubles of Rivendell* to *Hasten Their Departure*, in the notes' own order, and the twelve pages that had no quest behind them - the first and last musters, the south road, the barrow hunt, the walk to the valley, the hilltop, the hunters, the raised ford, Bree, Tharbad and the Old Forest - are removed rather than left standing beside it, because a line answering two books at once cannot be checked against either. The notes' *Hiding in the Dark* is four pages (the stone asked at the bottom of three caves, then the troll of each, named as the notes name them), Barachen is spoken to rather than merely walked to, and the ford and the return to Glorfindel share one page because a stone's asking is settled in the hand that lent it - two pages of talk with the same Elf back to back would be one errand told twice. Standing stays at the flat tenth throughout, and the Giant now carries the notes' own purse (four silver, sixteen bronze, fifty experience) beside the Wood-troll's. The Chinese lines that changed are translated; the new ones are English until the rework has been played.

- **Chapter one is complete: the rider beats, the counsel, and the camp that hears them.** The ruin is the pack's own ruin template, stood in the Trollshaws by the map rather than declared as ground, so the finding has a wall to be found at. The notes' *Missing Rider* is two pages instead of one (the walk to the ruins in the narrow channels, where the closing line tells of the black horse found there, and the report Elladan hears at his camp), the notes' *Wisdom of Lord Glorfindel* and *Regaining the Trail* are pages of talk, and *Where is the Horse and the Rider?* now plays where the ids put it - the second page of the chapter, the walk to Thorenhad - rather than at the end. Elladan stands at his camp from that page to the Giant's, and Glorfindel now stands in the valley for any page of talk that names him, not only for the stone's work, which is the first thing his skin is worn for.
- **Glorfindel wears his own face.** The skin drawn for him was in `faction_west/`, which the renderer never reads for a named giver; it now sits at `middle-earth/glorfindel.png` with Elrond's and Legolas' art, where the hall's named-skin lookup finds it.
- **A page's purse is read on a hover, not a new layout.** The story leaf's reward row is unchanged; resting the pointer on it says what the page pays in coin and experience, and a page that pays none draws nothing at all.
- **The Wood-troll pays the notes' purse, and his tale is carried back to Elrond.** Pages can now carry coin and experience as well as standing and courage (`Page` has silver, bronze and experience, zero on every page but this one), and the kill pays the notes' eight silver and forty bronze and its fifty experience. The notes' *Hasten Their Departure* is a page of its own instead of being folded into the closing line: a page of talk owed to Elrond, handed by Legolas and settled by speaking to Elrond in the valley - who is now stood while a page names him, so the library is not empty when the player arrives. Taking the page also pitches the pack's own troll cave ahead, so Matuna is met at his lair rather than in an open field. A page of talk is never read back, so a page that settles itself now says its closing line when it settles rather than leaving the words unread in a book it has already left.
- **The valley is peopled by Elves, and its two named ones wear their own faces.** The six who walk the square were the human body the line's givers use; they are the pack's own Elves now (`entityttmelves`), and the home-keeping that stops a villager stroll walking out of a room moved down into `TolkienVillagerEntity`, so every one of the mod's folk placed with a home stays in it rather than only the human. Elrond and Legolas keep the human model their art is drawn for, and wear `middle-earth/elrond.png` and `middle-earth/legolas.png` - the named-skin path the hall heralds already use - converted from the 64x32 skins in `CustomSkins` to the model's 64x64.
- **The Wood-troll Matuna is the guest house's errand, and Elrond's to hear.** Legolas names the creature the Giant was the shape of and shows the player where to track one down: the notes' Aid of Mirkwood, priced at their twelve hundred taken at a tenth, carried back to Elrond with the line about the Nazgul's designs and the shadow on his heart. The pack has no wood-troll, so the campaign stands one cave-troll up ahead of the player, names him, tags him and counts that body alone - the page names a creature rather than a kind of mob, so no troll met in a cave can settle it.
- **Rivendell stands.** The valley is built out of seven elven pieces - a square at its middle, a library, two houses, a gate, a lamp at each corner of the square and a banner either side of the gate - laid at one floor so the buildings meet the paving, with dirt under each so nothing on a slope hangs. Six Elves walk the square, named, homed to it and never givers.
- **The line moves to the guest house to hear Legolas.** Elrond's audience hands on to the notes' guest room, walked to in the same valley at the notes' two hundred and fifty taken at a tenth, with Legolas stood in it to hear the tale and hand on the Wood-troll hunt.
- **The line comes back to Imladris to hear Elrond out.** After the Giant the notes send the player back to Elrond, who can name the creature and arrange the audience with the Mirkwood company: the walk to the valley is the page, priced at the notes' two hundred and fifty taken at a tenth, and Elrond now stands in the valley to hand on the guest room where Legolas is staying.
- **The scout's report pays off as a Giant in the valley.** The West's line gains the notes' hunt for the creature Barachen saw: a tree-ent, since the notes have it standing among the trees as tall as they are and then have Elrond say the player could not have faced a true Onod. Elladan hands the page at the camp and takes it back, so he now stands from the cave page until the Giant's has been taken off him rather than only while the stone is in somebody's hand.
- **The West's line walks on to Barachen's camp.** The cave search ends with the stone going back to Elladan and, in the notes, with a scout's report of a Giant in the Trollshaws: so the page after the caves is a walk west along the Great East Road to Barachen's fire, a named place canon to the Trollshaws rather than a hall the campaign raises. He stands there from the page that walks to him until the page he hands has been taken off him, and his slot is read off the line rather than written down a second time, so a page inserted above his cannot leave him standing at the wrong step. He wears the Elves' own giver tags, so the same right-click hands and takes his pages.
- **The Trollshaws are a country of their own.** The upland wood between Rhudaur and the Misty Mountains had no biome of its own: a player walking west out of Rivendell crossed Firien's hollies or Eriador's fields, and the hill-trolls the place is named for had nowhere to be. It is a temperate upland forest now - beech and oak below, pine on the higher slopes, ferns, fallen timber, boulders and stumps where the rock shows through - drawn higher and rougher than Eregion below it, and tagged hostile so its trolls, wargs and Rhudaur brigands keep it. Its climate is the old-growth birch slot, which nothing else in the region had taken, plus the forest slot's cool dry half; on the map it takes the north end of the old Firien cells, under Rhudaur and against the mountains. Elladan's camp at Thorenhad is canon to it now, so the camp and the caves about it stand in the country the books put them in.

- **Thorenhad is a tent with Elladan at it.** The camp was a name with a man standing in open grass, which is not a camp. It now lays the pack's own tent down on the ground the name is for, seated on the highest column under its footprint with the drop filled beneath it so it stands on a slope instead of hanging off one side of it, and Elladan is stood at the fire in the same look - there is never an Elf waiting in a field for a tent that never comes. It is laid straight from the template rather than through `Sites`, because a camp in the Trollshaws is one camp for the whole world and `Sites` keeps one site per player, which is already holding the cave this page was walked to.

- **Thorenhad stands in the Trollshaws.** Elladan's camp was a name the map was free to draw anywhere near whoever carried the cave page, and the Trollshaws are the western slopes of the Misty Mountains - the country this drawing calls Firien - so the camp is now pinned there and every world puts it on the same side of the mountains as the valley it is a day's walk from. Nothing is built at it; it is a camp, and it stays one.

- **The beryl changes hands at Elladan's camp, not in the valley.** The old campaign's notes end the cave search at Thorenhad rather than at Rivendell: Glorfindel leaves the stone in the player's care for the caves, and it is Elladan who takes it back to him and sends the player on to the next lead. The campaign now does the same. Thorenhad is a name of the place catalogue - a camp in the Trollshaws, drawn by the map rather than built - and Elladan stands at it while the cave page is the step in hand, wearing the same giver tags as the Elf in the valley, so the right-click that hands a page also takes the finished one and the stone with it. Glorfindel now waits only for the ford: once the stone's work is the caves, the hand at the end of the walk is Elladan's.

- **The three caves have their names back.** The old campaign's notes did not send a player to "any three caves": they named three troll-caves - Bandrith, Hoklond and Sholdroth - and gave each one its own quest, its own completion line and its own token, with the beryl held aloft at the bottom of each coming up dark because the Nazgûl was never there. The page now carries the names, and the stone says which cave it is being asked in as it stays dark, which is the answer the notes promised: the beryl says where he has not been as much as where he has. The three are asked in order, so the second asking is Hoklond's and the third Sholdroth's rather than three anonymous holes.

- **The cave the stone is asked in is cut and raised by the campaign.** The old campaign named three troll-caves on a hand-drawn map, and the mod's own caves are a Mirkwood jigsaw a country away from Eriador, so a player carrying the stone's second asking could search a whole country with no cave in it. The campaign cuts its own: `tools/make_troll_cave.py` builds a crag 56 by 36 by 56 with the inside taken out of it - four chambers under a lair with a hearth and bones in it, corridors between them, stalactites over them, moss, webs and dead brush on the floor, and a mouth cut open on the eastern flank - and writes it as a template under `key_structures`, so it is placed the way the camp and the hilltop are placed. While the cave page is the step in hand the campaign pitches one of these on the road ahead, once, and the three holds are asked in the rock the campaign itself put there. The template is a file rather than a build, so the tool that writes it is in the tree beside it.

- **The stone's first asking is the ford, as it was on the old map.** The stone was one page of caves, which skipped the asking the old campaign opened with: the beryl held aloft at the Ford of Bruinen, the last ground anyone knows the missing Rider touched, and carried straight back to Glorfindel. It is two pages now. The first is one landmark and one hold, finished by standing in the ford and raising it - and the ford is ground the place catalogue already names, so a world that has never been told where the ford is has it named from the map rather than the page waiting for an operator. The cave search that follows is the old campaign's three troll-caves without the hand-written coordinates: any three caves will do, asked at the bottom of each, which is sixteen blocks of rock over the player's head rather than the eight that only prove they are inside one. The lent stone is given back only after the last of its pages, so the ford cannot leave a player hunting caves with empty hands, and both pages are Glorfindel's to hand and to take.

- **The stone's work is Glorfindel's, and the stone goes back to him.** The beryl was lent by an Elf whom nothing in this campaign ever put a body on, so a Ranger handed out an Elf-lord's stone and the work paid itself in a field. The West's line gains the step before it: the herald says plainly that Glorfindel of Rivendell has asked for the player's presence, and that page is the walk to the valley - nothing in the pack has ever built Rivendell, so it is a name of the place catalogue the map puts in the west of Eriador rather than in whichever country the page happened to be taken in. Glorfindel stands in it while the stone's work is the step in hand, a campaign giver like a herald with a stone rather than a country's war to hand out, and the four caves complete the page rather than pay it: the last reading is the stone's piece, and then the player walks back to the valley and gives it to him, which is where it is paid and where the lent beryl goes back with the work. A herald asked for a page that belongs to somebody else now says whose it is instead of handing it over.

- **The Elf-lord's stone is held up, and it is the ground that answers.** The beryl was described, praised and never given: the page that pays for it was finished by being read, so the answers the stone has for five places in the world could not be heard, because nobody was ever handed one. The herald now puts it in the player's hand as he says hold out your hand, and the page is a survey of caves: the stone is held up - it is the one thing in a pack that is used rather than carried - and it wants rock over its head, so above ground it says plainly that it has nothing for open air. Underground it answers the cave it is held in, which is the question the old campaign asked it when the caves were hand-placed: five cave readings are written and the cave's own blocks pick one, so a cave keeps its answer and two caves do not say the same thing. Three caves complete that second page and each has to be a walk from the last, because two mouths of one system are still one cave, and asking twice in the same one is told so rather than counted. A stone put down is a loan spent rather than a stone lost, so it comes back to the hand it was lent to. The stone no longer answers by itself when a player walks into marked ground - that was the ground talking over the player's head, and it left the item with nothing to do.

- **A page no longer prints where it was taken as though it were where it is.** Every page in the book is drawn with "Taken at x, y, z" under the giver's name, which for an errand taken at a bench is a true and harmless thing. Ground the campaign builds is chosen where the player is standing when they take the page and does not exist yet, so that line printed their own feet, and a walk to Weathertop read as a walk to where they already were. A page naming built ground says nothing there now, and the leaf closes the gap rather than drawing an empty line.

- **The crown is met in his own house before he is fought in the ring.** The castle the war raises was a building the player walked into and left again, and the only Witch-king in the campaign was the one standing on the sand at the end - so the fortress was scenery and the meeting the last page promised had already happened somewhere else. He is now waiting inside it while the castle's page is carried: seated on a chair that draws nothing of itself - his own idle clip plays the saddle pose while he is a
passenger, which is how he sits a fell beast - without a mind of his own so he does not pace the halls, and as unkillable there as he is behind his ring, and it is him who says where the war is settled - come to the sand. The chair is looked for as air to stand in under a roof, nearest the middle, so the meeting happens in a hall and not on a courtyard's flagstones. The chair itself is now the block the castle build names
at 3850, 107, -7425, and the search is kept only for a build that names none. When the page moves on he is gone, and the seat is empty when it is looked into again.

- **The ring holds at noon.** The three Ring-wraiths that keep the crown, and the crown himself, stand where the campaign stood them in daylight as well as in dark: all of them are built on the same body as the mod's other servants of the dark, which walks out of the sun and into shade - the right habit on a road, and the wrong one on an arena floor with no shade in it to reach. The two sun goals are dropped from the bodies this fight is made of and from nothing else, so the Rider, and every Ring-wraith a player meets elsewhere, still keeps its habits.

- **The crown is fought for on the arena's floor, and never alone.** The last act was a Witch-king stood on the first patch of air over the middle of a castle and killed where he waited, which made the arena the campaign raises the one building the war never used. He now comes up in the middle of the arena's sand, and three of the Nine come up round him on the ring - two the player has met before and one they have not - and while one of those keepers still stands, nothing lands on him at all: the page asks for four bodies and not for one. The shield is an event rather than a rule inside the boss, so it belongs to the campaign's own Ring-wraith and to nothing else an operator or a pack might spawn. The arena therefore comes before the crown instead of after it, the crown's line and the arena's are both said over the sand once the fight is over, and the keepers are stood once with him rather than topped up, because a keeper who falls has been dealt with.

- **The campaign ends in the arena it raises, and the Witch-king waits in a room.** The fortress was the last thing the war built and the crown was the last thing it said, which left the arena - named by the ladder's own rung and by the words the campaign closes on - standing in the pack and never once raised, and left the Witch-king stood at the first two blocks of air over the middle of a courtyard. The campaign's own slot now walks four last acts instead of three: once he is felled, a page names the arena, the map gives it ground in Angmar the way it gave the fortress its own, and the war says its last over empty sand rather than over the body in the hall. He also has to be somewhere a roof covers him and a wall stands within reach, and that room is looked for across the building rather than straight up its middle, so a keep with a hall in it puts him in the hall instead of on the flagstones with the gate at his back. The book draws each of the four under its own name now, where it called all of them the Rider's page; the translations are in both languages, so the arena's title, its place and its closing line read in either.

- **Angmar generates in the overworld too, not only on the drawn map.** The new biome was wired to the map's country, and the map only reaches Arda: the region's climate had no cell for it, so a plain overworld never grew one while every other Tolkien biome did. It takes the frozen plain's dry third - ground cold enough to snow and too dry for the taiga that fills the rest of it - which is the one stretch of the far north the Misty Mountains' claims and Forochel's had left free, so no biome gives up ground for it.

- **A template says what it carries, and what it leaves alone.** Every build handed over now follows one rule: the air *outside* a building is left out of the file entirely, so the ground it generates into is not carved into a square, and the air *inside* it stays air, so terrain does not fill the rooms. `tools/WorldScan extract_enclosed` applies it mechanically - solid blocks kept, air that cannot reach the edge of the box kept, air that can reach it dropped, which is the difference between a room and the weather. Weathertop was re-cut under it, keeping 411 enclosed air cells and dropping 513,192 outside ones, and the faction hall brief now says to leave the outside empty instead of pouring structure void into it. The builds the campaign raises for its story - the camp, the hilltop, the fortress and the arena - moved into `data/tolkienmobs/structure/key_structures`, with their ids, their template pools and the structure provider all repointed, so the keys of the map sit in one drawer rather than among the villages.

- **Weathertop is raised, not merely named.** The West's walk to Weathertop is the campaign's first page to a landmark rather than a camp: a hilltop cut out of a finished build and carried as a template, 121 by 40 by 121, with its open sky recorded as structure void so the terrain it lands on shows through instead of being carved away. It comes out of `run/saves/weathertop` by way of `tools/WorldScan`, which grew a `layers` mode to find the floor by counting what each layer holds and an `extract_void` mode to write air as void. The building path grew with it: a site now waits for every chunk its footprint covers rather than only the one at its middle, clears a template that records its air and leaves the ground standing for one that records void, and searches a quarter of the building's width at a time out to the building's own size, so a hundred-block hilltop gets more than the single candidate the tent's step left it.

- **The body at a built camp is a ranger, not a passing villager.** A keeper was one of the mod's folk in whichever face its variant rolled, which is what a person in a village wears rather than what the man at a fire a page sent you to should wear. Keepers now ask for one file of their own, `textures/entity/middle-earth/ranger.png`, before the variant is looked at - and the file is optional in the same way a herald's own face is, so a pack without the art gets an ordinary human at the fire instead of a checkerboard. Whether a body is a keeper is a tag, and a tag is a server fact, so it is synched the way the givers' clothes are.

- **A page says which way the work lies, not only which country.** A country is a haystack: a site is one hashed point inside a five-hundred-block cell, and a page that named only the country sent the player out to search it. The country line now carries an eight-point bearing as well - "The work lies in the Shire, away to the north-east" - which costs nothing to say and turns a search back into a walk, while still leaving the ground unnamed.

- **`/tmquest page` says where it put the ground.** A page names a country and leaves the walking to the player, which leaves an operator testing a site with no way to know which way to walk: the command now prints the coordinates it chose, and only the command does.

- **Quarry a page owes is collected on the road, not at the bench.** A page used to put its mobs down the moment it was taken, a dozen or two blocks in front of the player, so every step of the campaign arrived within sight of the herald who described it. A hunt now holds its quarry back for a minute and a half after the page is taken, and only pays it out once the player is out under open sky and a hundred blocks from that bench - then stands it up forty to sixty blocks ahead instead of twenty. The Rider keeps his own arrival but waits on the same clock, and stands further off when he comes. One hand-over owes one meeting, so a quarry killed is a hunt that moves on rather than a road that restocks itself.

- **A quest site is chosen from the map, before any of its ground exists.** Arda's terrain is a drawing, not noise the world has to make: `ArdaMap` already answers, for any column, what country it is in, how high it stands and whether a river runs there. Ground for a structure is now chosen and judged from that drawing rather than from the player's facing, which means the spot is settled before a chunk of it is generated, that it is dry and level and in the right country by construction, and that it is the same ground for everybody - a page can name a country and be right. The country a structure belongs in is read from the structure's own biome list, so the tent's wilds and the campaign castle's Mordor needed nothing written down twice. A world the map does not draw keeps the older answer, and the build still judges the real ground before it lays anything down, since the drawing carries no warp and no trees.

- **The campaign has its ending back: a castle it raises itself, and the Witch-king waiting in it.** The castle and the arena have been in the pack for as long as the mod has, registered but never in a structure set, which means no world has ever generated one and nothing has ever pointed at them - the campaign ended on the Rider and simply stopped. The campaign's own slot now walks through three last acts instead of one: the Rider, then a castle whose ground is chosen where the player is standing when they take the page and raised when they come within sight of it, then what is waiting inside. The Witch-king is stood up in that castle - the first body the campaign has put in a building rather than on a road - at the first place in it where a body fits, tagged so a reloaded world finds the same one instead of a fresh one every look, and named off the registry so the page cannot ask for a boss the pack does not have. Settling him pays the rung the courage ladder has been holding back for the arena, and the campaign says its last words there. A herald whose own front is long finished now has something left to hand over, which is what the mark over his head meant all along.

- **Built ground gets its own groundwork.** A site was only placeable on ground that was already level, which a tent will find in most meadows and a castle will find almost nowhere, so the site now builds at the highest column under its own footprint rather than at the height where the player happened to be standing - a hillside through the middle of a building being rather harder to excuse than a gap under one wall - and clears the box the template is about to fill before it places it, so no treetop comes up through a tent and no corner is left standing in a courtyard. The box is the template's own and the plane is its own, and it is the only thing in the campaign that takes blocks out of a world rather than putting them in, which is why the ground is still only chosen in open country a long way from where the page was taken.

- **The campaign can build its own ground now, and the tent was only the first thing it built.** Every building the mod has was the world generator's or an operator's: nothing in it had ever put a template down itself, which is why the arena and the castle have sat unbuilt for want of a campaign to place them. That is now a thing it can do, and it does it for any template there is. A page that names built ground - `site:tolkienmobs:wild_tent` today, an arena or a castle later - has its spot chosen from where the player is standing when they take it, forty-eight to ninety-six blocks on and nudged off their line so it is not always dead ahead, and nothing is written to the world until they are within four chunks, because at that range the chunk is loaded anyway and no chunk is force-loaded to hold a building nobody may visit. The footprint judged before anything is placed is the template's own size, read from the file at build time: every sampled column within tolerance of the site's height, over solid ground and out of standing water, with the tolerance growing with the building - two blocks across a tent is a hillside through the middle of it, and two blocks across a castle courtyard is flat. A spot that will not take it is searched for in rings around the written one, and if none of them will, nothing is built and the next look tries again. A camp is the same machinery with a body at the fire, which is all that makes it a camp; taking a second page to ground the player has not reached yet keeps the site they already have rather than pitching a rival beside it, which is what a quest in pieces needs.

- **The West's line is twelve pages, and half of it is walked or heard rather than killed.** Three pages took the West from its own door to the barrows and then stopped, so the front read as three fights and a heap of dead. It runs to twelve now: after the barrows the herald puts Glorfindel's stone in the player's hand and tells them what it is for - the Elf-lord's beryl, carried south once before and never brought back - and from there the line is three walks and three conversations between the fighting. The walks go to Weathertop, the ford and drowned Tharbad, all of them ground the place catalogue already names, and the herald will not hand one out in a world that has not named that ground, because a page nobody can walk is a dead end in a book. The conversations are read rather than handed over: one at the herald's own bench, and two from whichever giver a player asks, so Bree's gossip and the Old Forest's indifference are things somebody in the world has to be asked for. The front's last words moved with the end of the line, so the page that finishes the West now carries the closing line rather than the third page that used to.

- **The errands are four shapes now, and only one of them is a country's.** Every offer outside a front was either goods wanted or beasts wanted, and the only other work in the mod paid standing, so a player who spent a month in one country spent it killing the same two armies and reading the same two sentences. Two more errands join them: ground to be walked - taken only from places this world has actually named, in the dimension the player stands in, so a walk can always be finished - and a body to be heard out, which is the one errand whose work is the telling. That one is a leaf of the office's own, so every giver in the world has the same tale to finish and none of it had to be written per giver: it is taken at the hand-over and told when the player comes back and asks for the rest, because hearing somebody out in the same breath as being handed the work is not an errand at all. The four errands are drawn evenly and the country's work keeps the chance the config gives it, so a run of errands is now a run of different things.

- **The beryl has something to say about marked ground.** Glorfindel's stone has been in the pack since long before any of this, carrying a memory from the old campaign - it was held up in caves to see whether the Enemy had been there - and nothing in this build ever asked it anything. The place banner now has that second half: come into named ground with the stone in your pack and, once the place has finished naming itself, the same banner comes back over with the place's name and the stone's answer under it. It is one `if` in the machinery that already runs, and the answer waits its seven seconds so the two banners are read as two things rather than as one flicker; it also refuses to arrive if the player has walked out of the place or put the stone away, because a line about the ground under their feet is worth nothing anywhere else. Which ground answers is a mark on the place's own entry in the catalogue, and what it says is `place.tolkienmobs.<id>.beryl` in each language - so a place's sheet is what makes the stone react there. The five the Enemy has passed are marked now - the Green Way, the barrow downs, Weathertop, the ford and Tharbad - each with the stone's answer of its own, while Bree, Hobbiton and the Old Forest stay ground it has no opinion about, which is itself worth knowing.

- **A refusal is said to your face, and a page re-read keeps its last line.** Being turned away by a herald was a chat line and a sound: the man himself said nothing, which is a strange thing for the one body in the campaign who is all voice. He now says it on the same leaf a page is handed over on, with his answer for a heading and one paragraph under it, and no replies, because there is nothing to answer. The leaf also remembers what it is: a page already in the book, opened again from the herald who gave it, starts at the last thing he said rather than at the question the player has already answered - it used to open at the opening, so reading a page twice meant answering it twice.

- **A herald can have a face of his own.** The three who hand the fronts out were one cloth each: Maelgrin, Guthlac and Zarabi all wore their alliance's single giver skin, so the campaign's three named people were three copies of the same soldier in three colours. A hall's herald now looks for art drawn for him first, at `textures/entity/middle-earth/<name>.png` - `maelgrin.png` for the West's, `guthlac.png` and `zarabi.png` for the others - and wears the alliance's cloth when that file is not there, so the portraits can be drawn one at a time and a pack that never draws them looks exactly as it did. The name asked for is the one the body is actually wearing, so renaming a herald in the world looks for the art of the name he now carries, and a named file that nobody has drawn is passed over without a word, since only the alliance's art is the mod's own promise.

- **A page can be walked to, or heard out.** Every page the book held counted kills or read the pack, so the campaign could not ask a player to go somewhere or to hear somebody out - the two things its own writing does most. Two shapes of work join the ones the givers hand out: a location page names a place from the catalogue's own list, or a point of its own with a reach round it, and is done by standing in it; a dialogue page names a page of talk to be read to the end, or a body to be spoken to, and is done by hearing the words out, with the leaf saying which page it was on the way out and only an answered leaf counting, so one shut unread finishes nothing. Both settle where they happen rather than being carried back to a hall, through the same hand-in as any other page, because reaching a place and hearing a conversation are each their own proof and there is nothing for a giver to check. `/tmquest <targets> page <kind> <target> [goal]` writes one of any shape into a book for trying them out, the target written the way a page writes it: `place:greenway`, `at:100,64,-20,16`, `leaf:campaign.tolkienmobs.page.muster` or `person:Maelgrin`.

- **A page can name the body it goes back to.** A finished page was paid by whatever giver a player reached first, which made every page local: there was no way to write work that sends them to another hall, and no way for a giver to say that a page in hand is somebody else's to take. A page may now name one of the mod's own - take this back to Guthlac - and only that body pays it: the giver who handed it over wears the work's ordinary mark, the body it is owed to wears the question mark instead, drawn only as far as a nameplate can be read, and a giver asked for a page that is not his says whose it is rather than going quiet. A page that names nobody is paid by whoever reaches first, so every page already in a world reads exactly as it did.

- **The page that ends a front has the last word of it.** A front's line simply stopped: the last page paid its standing and its courage and the front was over, with nothing said about what had been walked or what was left behind. Each front's last page now carries a closing line - what the quiet over the barrows costs, what the East has bought with a sealed scroll, what the sand will not say about the mûmak - spoken the moment that page is paid, and drawn on the page in the book but only once the work is done, because it is the one thing on the leaf a player cannot read too early.

- **The doorway guards stand on the step, not in the room.** They were baked into the hall's interior either side of each doorway, which put two soldiers in the path of anyone walking in and left the approach itself unwatched. All four of a hall's now stand outside, centred on the doorway's own columns one block out from the wall, on the ground the hall already carries so nothing has to be levelled under them afterwards, facing away from the building. Inside is unchanged: the warden, the envoy and the giver still hold the room, the near guard still says its line as a player comes up to the door, and the pose is the same frozen sway turned to a face.

- **A guard's shield shows its face.** The shield was hung a quarter turn out from the arm, so from the front a player saw the edge of a plate rather than the alliance's colours on it - the heraldry was right and unwatchable. It hangs square across the forearm now, low on the arm rather than at the shoulder and facing the way the guard faces, so the design looks back at whoever comes up the step.

- **Each hall hangs its own banner now.** The halls held their alliance's staff and its doorway guards, and nothing of the alliance itself on the walls, so the three buildings read as rooms that happened to belong to somebody. Each of the three is dressed with its own side's mark - the West's blue field under a white skull and a crowned flower, the East's black field under a white flower in a rhombus, Harad's grey field under a light rhombus and bar beneath a red-and-orange circle - hung on the outer face of the wall either side of both doorways, in the block beside each opening and above the heads of the guards posted below, so a hall says whose it is the moment a player steps through. The designs are the same list the guards' shields are cut from, so the banner over a doorway and the shield beneath it are one heraldry rather than two drawings of it, and each banner keeps its layers in the order they were cut. They hang as wall banners rather than standing ones, because a banner standing on the floor would stand in the very block a doorway guard already holds.

- **All three heraldries are in the mod now, not just in a world.** The West's blue field under a white skull and a crowned flower, the East's black field under a white flower in a rhombus, and Harad's grey field under a light rhombus and bar beneath a red-and-orange circle - transcribed layer for layer, in the order they were cut, from the designs themselves. A shield is a banner worn on an arm, so the guards at the hall doors now wear the shields that go with the halls they stand in, and the same list is what the halls' own banners will be hung from.

- **A hall keeps its door now.** Each of the three halls had a warden, an envoy and a giver inside and nothing at the door, so the building read as a room rather than as a place that belongs to somebody. Two soldiers of the hall's own alliance now stand either side of each of its two doorways - the blade in the right hand they were already armed with, and a shield on the left arm that nobody has to hand them - with no brain and no patrol, posed by the same idle the warden and the envoy stand in, so they sway at their post, turn to a player who comes in and settle back to it. The nearest one says its line as you step through, once a visit, and none of them lifts a hand while you are under the roof. They are baked into the three hall templates beside the staff, so a hall placed from a saved world keeps its door too, and a doorway pair is no longer counted as a patrol already sent.

- **A place says its name as you come into it.** The campaign's ground had names only inside the book: nothing in the world told a player they had come to the Green Way or crossed onto the barrow downs, so every road read as the same road. Standing where a place has been set now introduces it - the name over a four-word line under it, both bold and italic, the name in the ink of the country that holds the ground and ground held by nobody written plainly - arriving in a second, staying five and leaving in one, which is the timing the campaign's own notes had written out as command blocks. A player is told once ever, so the banner is an arrival rather than a notification, and the sightings are saved with the world. `/tmplace add <id> [radius]` sets a name at the caller's feet, `where` says what they are standing in, `list` reads the catalogue back with where each name has been put, and `forget` clears what a player has been shown so a builder can walk the same road twice. A place is a circle around a point rather than a border, because a world can say where its towns are but not what shape its country is, and where two overlap the smaller one is the one a player is told they have reached. The eight names are the West's, because it is the West whose line is written; the other two are a list here and a line in each language when their turns come.

- **Walking a front is what makes a player brave.** A campaign page paid one courage deed, the same as a villager's errand, so a player could carry three fronts to the end and still stand where the ladder starts: pinned by the first terror they met. A front's pages now pay courage beside their standing, rising along the line - two deeds for the first muster, four for the second, seven for the third - because the page that sends a player to their own country's barrows or sand is not a body count. Three fronts come to thirty-nine deeds, forty-eight with the nine the hand-ins already pay, which is stouthearted: the terror slows a player and no longer blinds them, and the fearless rung is still owed to the arena, which is where the story wants it. The Rider is left alone, because felling one of the Nine already pays five deeds and his page asks for him twice.

- **A page's quarry is on the ground when the page is taken.** A front's third page asks for wights or a mûmak, and those are a country rather than a view: a player who took the page in the West could spend an evening riding barrow downs that their world had not put within a day of them, and the errand the herald had just described read as a wild goose chase. Taking such a page now puts part of its quarry on the ground ahead, permanent and marked, with a line saying so - the same courtesy the campaign already does the Rider on the road, done for lesser quarry. What is put down is capped at three - which is the whole of the two wights and the one mûmak the pages ask for today, and only part of a longer hunt - and it is put down once, so a page taken twice does not double the mobs.

- **The Rider is not ended by dying.** His page asked for the Nazgûl once, so the one thing the whole campaign is built on - the kill that changes nothing - happened once, paid the page, and was never mentioned again; the second meeting, the one the act turns on, only ever existed in a sentence. The page asks for him twice now: fall him, and the campaign stands him up again on the road ahead once the player is away from where they met him, with a line saying he has lost nothing, and it is that second fall which settles the page. None of the body changes - the fight, the drop and the death message are the ordinary ones - because the lie the campaign tells is that all of it worked.

- **An operator can pay a book off without earning it.** `/tmquest <targets> complete [count]` settles what the targets are carrying exactly as a hand-in does: the standing, the reward, the courage deed, and a campaign page moving its front on. Three pages a front is a week of play, and a test does not need the week. It takes an optional count so a chain can be walked one page at a time, says plainly when there is nothing in hand to settle, and pays a fetch page without its goods being taken out of the pack, which is the one part of a hand-in a command cannot reproduce.

- **Every front ends on ground of its own.** A front was two soldier pages and then the Rider, so the three lines were the same two errands in three colours and the country a player had chosen never asked anything of them that the other two would not have. Each front carries a third page now: the West sends them to the barrow downs for the wights that have been opening the barrows from the inside, Harad to the deep sand for a mûmak out of its own host, and the East keeps the steward's scroll it already had. A page belongs to the campaign by what it asks for rather than by the kind of work it is, so a hunt of wights is not mistaken for the Rider's page - the campaign's own page is the one that names the Nazgûl - which would otherwise have ended the war on the first wight felled and left the three fronts standing unsettled behind it.

- **The book has a voice.** The ledger now sounds like the thing it is: a leaf turns under the story arrows and the two bookmarks, the covers open when the book is first shown and close when it is put away, a seal is pressed into wax when a giver's page is taken, and coins are counted out when a page is settled - where a villager's cheerful "yes" and the level-up chime used to stand in for both. The five effects are synthesised from noise and decaying oscillators rather than sampled, so they carry no licence, weigh five to nine kilobytes each, and are mono Ogg Vorbis at 44.1 kHz like the rest of the pack. A sound a screen plays has no place in the world, so the client looking at the page plays it, and each of the five carries a subtitle in English and Chinese.


- **The herald speaks.** His two moments have a voice now: the refusal that turns a stranger away, and the hand-over of a page. Both were read by a local voice model (Piper, `en_GB-alan-medium`) and are played at the herald's own position, so the words come from the body in front of the player; the chat line stays, because a player with the sound down, or reading in Chinese, still has to be told what happened. The lines are mono Ogg Vorbis at 44.1 kHz, and both carry a caption in English and Chinese.


- **A page is spoken, not only summarised.** The campaign script's best writing never reached the game: a page was a name, a place and a count, and a herald's whole part in it was five chat lines, so the conversation the West's line opens on - a Ranger's cold hall, a late scout, the horn - arrived as a notice. Taking a page from a herald now opens it as parchment and lets it speak: the speaker's name, the words, the two answers a player can give, his reply to whichever was given, and a row under it that puts the page away. The first page written is the West's first muster, and its words are the script's own. A page whose prose has not been written keeps the hand-over it had, because a screen with nothing on it is worse than a chat line: the guard is one set of page names, so giving a page a voice is naming it there and writing its keys. The words travel as keys rather than as text, so a page is read in the player's own language, and the name at the top is the name the body in front of the player is actually wearing, so the page cannot contradict its own nameplate.

- **`/tmcity relay` lays the city again where it already stands.** A corner is planted once and the bands
  walk up from it, so a piece re-cut after the city was laid is never laid at all - the counter is at the top
  and `band` has nothing left to give, which is what a re-lay after a cut change looked like. `relay` keeps
  the corner and the dimension and puts the counter back at the bottom, so one `/tmcity band 6` writes the
  amended pieces over the standing city.

- **`/tmcity summon` puts the whole city down in one word, in whatever world the caller stands in.** The
  city could only be raised by planting a corner and then walking the bands up, which is the pair for
  building it by hand but three commands and a remembered corner for anyone who just wants the build alone
  in an empty world. `summon` plants at the caller's feet and then lays every band and the shoulder, so a
  flat world can be turned into the build and nothing else.

- **`/tmcity folk` stands the city's people, and `summon` stands them as it lays the city.** The valley's
  people were only ever stood by the page that builds the valley as a place, so a city put down with
  `/tmcity` in a fresh country came out empty - no Elrond, no Legolas, no walkers. The three Elves of the
  line now stand at the marks the build carries, the fifteen unnamed walkers at theirs, and the rest of the
  crowd wherever the city has ground under it - all of it read out of the cut's own `rivendell.npcs` table
  rather than written down a second time in code, so a re-cut carries its people with it.

- **The shoulder of a city now says what it came to.** `/tmcity apron` answered only that it had run over
  four rings, which is the same answer whether it reshaped a hillside or moved nothing: it now counts the
  columns it cut down, the ones it built up, and the ones it left standing because they were more than six
  blocks off the city's base. Whether a city reads as a block cut out of the ground is now a number rather
  than something to squint at from across a valley.

- **Every command the pack registers is written down in one place.** `docs/commands.md` holds the five
  roots - `/tmcity`, `/tmquest`, `/tmplace`, `/tmfaction` with its two flat aliases, and `/tmcourage` - each
  hand's syntax and what it answers, who may run them, and, at the end, the walkthrough for standing
  Rivendell up in a world: the Arda cell and the corner that centres the footprint on it, the ceiling that
  decides how high the base may be, the band-by-band route, the shoulder and what its counts mean, and the
  re-cut loop.

- **Imladris is its own country: a hidden valley cut into the Trollshaws at the mountains' feet.** The city
  had nowhere of its own to stand - the ground under it was Trollshaws from edge to edge, drawn at 84 with
  sixteen blocks of relief, so a 554-block build dropped there either lost its crown to the ceiling or sat
  in a pit with a rock wall around it. Arda now lays an `imladris` floor at 74 with two blocks of relief
  across 640 by 580 blocks centred on the valley's own cell, blended back into the Trollshaws over a
  64-block rim, and the biome is what the books describe: oak and beech on the floor with pine holding the
  walls, elanor and fallen mallorn leaves, springs and ferns and loud water, elves rather than hill-trolls,
  and grass over mossy rock. The name `rivendell` is surveyed from the valley's middle now, so the place is
  written where the city goes rather than wherever the hills around it would take it - and a survey asks
  the ground what country it is in rather than asking the drawing, because a valley cut into the grid has
  no cell of its own to be found in. The command walkthrough plants from the valley floor now, rather
  than from the 72-block corner the Trollshaws' own ground needed.

### Fixed

- Set the drape's ring back to thirty-two blocks: at sixty-four the pass reached past the branch the river
  leaves the city by and overwrote the water, and the river left the build. Thirty-two keeps the settled
  ring off the outflow.
- Spread the climb beside drawn water over a hundred and sixty blocks instead of ninety-six (`MOUTH_RUN`),
  with `WATER_REACH` out to a hundred and seventy-six so the reach still passes the climb: the massif's
  flank across the Bruinen stood at 319 within a hundred blocks of water at 74 and read as a wall, and
  now rises over nearly twice the ground.
- Hold the massif's line behind the city: `giveWay` opened the ground to any drawn river within a hundred
  blocks, including the Bruinen past the valley's east side, which brought the eastern flank down to 74
  and cut a valley the city has no mouth into. Only water on the branch's own side opens the massif now.
- Double the drape's reach from thirty-two blocks to sixty-four, so the ground is settled further out from
  the walls before it eases back to the drawn country and artifacts near the build fall inside the ring.
- Hold the spring in instead of letting it run out of the back of the build: water at the build's edge
  over `OUTLET_LIFT` above the floor is a spring, so the ground outside is banked to it and never cut,
  where it used to be left open and the water cut itself a false river down the flank. The city's
  outflow at the floor is still left open.
- Stop the drape leaving a pillar of ground wherever a pool reached a column's top: water out in the ring
  is a pool on the drawn ground and not the city's channel, so it is read down to the ground under it
  and cut with it instead of being skipped.
- Slope the river out of the city instead of dropping it at the wall: its surface fell the whole eight
  blocks to the vale at the first block of its course, which is a waterfall at the city's mouth. It now
  falls at most one block in twelve of run and meets the vale ninety-six blocks down the course.
- Stop the drape cutting the ground down to a deck: where the build's edge is floor tiles at 75 and the
  crown stands 243 above them, the outside ground was cut to 75 and the trench's face was the
  mountain's own shape. A boundary column topping out at or under 76 now takes the ground inside the
  box as its height and is filled, never cut - the wall the city is seen against is its roofline.
- Reach the valley's floor to the build's own outside edge: it stopped four blocks short, which left a
  strip of crown standing inside the footprint at 318 and read as a four-block wall at the river's
  mouth and again at the spring. The drape now takes that strip down on a world made before this,
  and counts a column as a watercourse only where water is its own top - water buried in the build,
  like the spring's aqueduct at 239, was leaving a hole in the draping in front of the wall.
- Open the ground round the Bruinen at one curve instead of two grades: the water's bank and the massif's
  cone met in a two-hundred-block seam just outside the west wall, because the branch opened the ground
  at 1.2 blocks per block and the cone cut it at 6, and the line where one gave way to the other was a
  face. `MOUTH_RUN = 96` climbs from the water to the crown in one smooth curve read from the middle of
  the channel, the drawn river's walk reaches 100 blocks and finds its edge a block at a time, and
  `MOUNTAIN_SLOPE` and `BRUINEN_BANK` are gone.
- **The massif stands at the roofline all round the city, and the range is a square.** What `giveWay`
  gives a river is a cone: rock no higher than the water's level plus `MOUNTAIN_SLOPE` blocks per block
  out. At six tenths that cone only reaches the crown (318 - 82) / 0.6 = 390 blocks from the water, so it
  lay over the massif's whole south side - read off the save, the south wall stands at 255 one block out
  and climbs back to 318 only a hundred and forty blocks from the build, and the north wall falls to 272
  and takes forty-five blocks to climb back. Six blocks per block out is a gorge wall's grade, near
  enough vertical: the rock returns to the crown within forty blocks of the river, so the channel keeps
  the slot it had and the mountains stand at 318 out to the walls. The shelf's own edge came in from 175
  to 96 blocks past the build, its fall from 340 to 200, and the ridge no longer swings in and out -
  `RIDGE_SWING` is read at nothing - so the range is a square rather than a ring.
- **A laying run that stops partway no longer has to be run twice.** The band mark was only written
  after the whole call, so a run that ended early left the mark at the band it started from: the world
  held bands the next call did not know about, and the pieces already down were laid again before it
  could get to the ones that were missing. The mark is now written as each band goes down, and each band
  logs `Rivendell: band N of M is down`, so a short run resumes at the band it reached and the log says
  where the last one stopped.

- **The map's ground is the ground, with no mountain left standing over it.** Arda cuts the noise's own
  terrain down to the height the drawing gives a column, but it did so by reading back the chunk's topmost
  solid block, and a heightmap that had not seen the noise yet answered with the valley floor: the column kept
  three hundred blocks of stone under a cap of grass at the ceiling, which is the flat-topped wall standing
  beside Rivendell. A column is now made the drawn one outright - air above its height, rock below - so no
  noise mountain can survive inside a valley.

- **The valley reaches the data as well as the code.** A country's biome and Arda's stem list are written by
  a data run, and neither had been written since the valley was added: the dimension still named thirty-five
  biomes and there was no `worldgen/biome/imladris.json`, so the registry had no such biome and the dimension
  failed to parse before a world could open. A data run writes both, the biome tags pick the valley up
  (forest, temperate, peaceful, and a new magical), and the source reads its biomes from the registry rather
  than from the stem's list - so a list that falls behind the map cannot close a world again.

- **The needle points at the body, not at the middle of the town.** A page that names somebody now
  points at the ground that body is standing on - read off him while he is out there, off the line's
  own stand points when he is not - instead of the middle of his place. Glorfindel's page led the
  needle into the Rivendell fountain, because the middle of the valley's square is a fountain.
- **"Speak with Glorfindel", not "speak with person:Glorfindel".** The prefix that says what a target
  is was being printed into the objective line.
- **Stones stay where buildings are.** A page of talk no longer plants a milestone where it sends the
  player, and a stone is only planted at the place a page was taken when that place is a room, so the
  line no longer scatters stones through camps and open ground.
- **The ford the beryl is carried to has its name back, and the page says how far off it is.** The
  crossing was catalogued as "The Ford", so a page that sent the player there read as an unnamed
  river crossing; it is the Ford of Bruinen now, the name Glorfindel's own words use. A page whose
  ground is far off also says the distance and the bearing once, beside the needle, since a line on a
  HUD gives a direction and no sense of how much road it is.
- **The cave search has a needle at last.** The crag the stone's cave page is asked in is pitched when
  the page is taken, and the campaign reads a pitched crag as a site - but the needle only looked at a
  site for the pages whose kind is ground, and a stone page asking for caves is neither ground nor a
  spot, so it resolved to nothing and the HUD went blank. It now points at the crag the page is being
  asked in, and says which way and how far, the same way it does for a named place.
- **The needle is looked at again instead of once.** It was worked out when a page was taken, handed in
  or read, and never again - but the ground a page names can appear after the page does: the cave page's
  crag is pitched once the page is in hand, and a place is named the first time somebody carries a page
  that names it. The clock now compares the mark with the one last sent and sends it when it differs, so
  a page cannot sit in the book with a direction that was never computed.
- **A finished page's leftover site no longer blocks the cave search.** A crag is the one thing the
  stone's cave page cannot be answered without, and the campaign refused to cut one while the player
  held a site of another kind - including ground belonging to a page already settled. A site nobody is
  walking to any more is ground the crag may have; one whose page is still carried is left alone.
- **The crag has its troll.** The stone's cave page is finished by killing the troll that keeps the
  chamber, and the crags the campaign cuts were empty - forty thousand blocks of rock apiece, no bodies
  and no spawner - so the search of the caves could not be paid at all. A keeper is stood at the bottom
  of the deepest chamber once the page is carried and the crag is built, named for the cave being asked,
  and never replaced: a kill at depth moves the page on to the next crag.
- **The needle follows the keeper into the chamber.** A cave page pointed at the middle of the crag, which
  is where the player walks in: once inside, the arrow was a bearing to the ground under their own feet.
  It points at the troll instead while he is standing, and says nothing at all inside a chamber that is
  empty, since the page asks for a kill at depth rather than a walk. The next asking brings it back.
- **The cave search pays mithril, not a keepsake.** Bilbo's Nugget is a cosmetic token carried by another
  page and worth nothing to a smith, so the end of the search now hands over four Mithril Nuggets instead.
- **The needle knows the Wood-troll's lair.** Matuna's page names a creature rather than a kind of mob, so
  it had no place, no spot and no person for the needle to point at and sent nothing at all - the one page
  whose whole errand is a walk into the wood. It now points at the lair the campaign pitched when the page
  was taken, and follows the creature himself once he is standing.
- **The valley keeps one of each Elf.** Rivendell's square was read by counting the bodies carrying the
  valley's tag, which kept a visit from standing a seventh and never dismissed a spare: a world visited
  many times carried close to a dozen of each name. Each of the six now has a slot on its tag, the first
  body wearing a slot stays, every other is sent on, and a missing one is stood again where it walks.
- **A page's question mark sits over the body it is owed to, and no other.** Elrond wore the West's giver
  clothes and a `campaign_giver` tag, so the nameplate read him as the front's herald and marked any West
  story page as his business - a gold `?` over Elrond for a Wood-troll kill that is Legolas' to take, on
  the one body that could only answer "that one belongs to Legolas". The book now marks a body by name
  for the page it is owed, and a speaker of the line is no longer offered work in a herald's name.
- **And the body holding the next page keeps its mark.** A speaker answers a herald's right-click and
  wears the herald's clothes, so the plate now asks the line who holds the page rather than asking the
  clothes: Legolas keeps his `!` while the departure page is his to hand, and no other Elf has one.
- **The East and the Harad leave the chain until their lines are written.** Their pages are still in the
  notes and their halls and heralds still stand, but neither front hands anything: `PAGE_LINES` holds the
  West's line alone, `FRONTS` is what the chain, the herald marks and the end-of-line sentence all read,
  and the next chapter's road - the Rider, the castle, the crown - is closed by name, since the old
  all-three-settled gate would otherwise have sprung the moment chapter one ended. A player who walks
  the West's last page is told the road has been written no further, instead of being sent to two fronts
  that cannot be walked.
- **The north's ground is named for chapter two.** *Last Refuge*'s places are in the catalogue: Glóin's camp
  at the east end of the pass, Gundabad and the vault on its eastern road (all Misty Mountain ground), the
  key-stone's vault at the source of the Bruinen in the Trollshaws, and Carn Dûm in Angmar - so a page of the
  next chapter can name its ground and have it placed where the books put it. `docs/chapter-two-last-refuge.md`
  maps the notes' eleven pages to their givers, quarries and ground, and names the two decisions the chapter
  still needs: which build stands for Gundabad, and what the notes' Dragon Wight is made of.
- **Chapter two opens at Glóin's camp.** The old world's dwarf camp is in the pack as a captured structure
  and is raised in the Misty Mountains when Elrond hands the new page over, so the road north ends at a fire
  that is already standing. Glóin is stood at its door - a human model at a dwarf's height, wearing
  `gloin.png` - and the page pays what the notes put on it: the dwarves' thanks, five silver and thirty-three
  bronze. Two fixes came with him: the two blocks the capture used to mark its door and front post are plain
  earth now, and a giver's name is finally flattened before it is looked for as a file, so `Glóin` asks for
  `gloin.png` instead of `glin.png` (the same was true of every accented name in the pack).
- **Glóin's camp stands at the east end of the High Pass.** First in the mountains and then below Caradhras,
  and neither was where the old world built it: the notes put the camp north of the valley, at the ground the
  High Pass drops into, which the drawing names Gladden. It is canon'd there and pinned to the pass's mouth,
  and the place line and the page's wording name the High Pass again.
- **A tool for taking a build out of a world.** `tools/grab_build.py` reads a saved world's region files, or
  an existing structure file, and cuts the build into 48-cube templates with a `.parts` table of the offsets
  they stand at - which is what a large build needs, since neither a structure block nor `Sites` will take
  one piece bigger than that. It reports the block palette before writing anything, so a name this pack no
  longer has shows up as what it will become: air. Cutting the camp and reading it back gives the same 4,928
  blocks; the world-reading half has no world in this checkout to be tested against yet.
- **The Trollshaws' data is generated data again.** The biome was added to the generator and its tag list, but the files in the tree were a hand-written copy of what that generator writes, so the country was in the code and in none of the data: an old tree feature the game had dropped, and no tags at all, because the tag files were last written before the country existed. A data run writes both properly - the wood is plains oak and beech with birch on the lower ground and the uplands' pines above them, ferns, lichen, gravel, clay and sand where the rock shows, boulders, stumps and fallen timber - and the country is picked up by `overworld`, `forest`, `temperate`, `hostile` and `windswept` from the tags on the biome itself rather than being named in any of them. Nothing is lost by it: the run rewrote the two language files and both came back with the same 2187 keys.
- **Arda's map and the dimension name the same biomes again.** The Trollshaws were added to the map's countries but not to the dimension's biome list, which is written beside it in the map's own order - seas, the north and the seaboard, the north-west, the Misty Mountains and the countries either side of them. The source compares the two counts as it is built and refuses a world where they differ, so the dimension failed to parse before a map could open. It is named between Firien and Lothlorien, where the map draws it.
- **The Trollshaws no longer stop a world loading.** The biome's wood was drawn with `minecraft:trees_birch_and_oak`, which 26.1 took out of the game; the biome file was a hand-copied list stood beside the code that generates it, and its copy had not been regenerated since. A feature name nothing defines leaves the placed-feature registry unbound, and the registry is loaded before any world is, so the whole pack failed at the load screen rather than in the wood. Every other feature the biomes name was checked against the game's own registry beside it; this was the only name in the pack that had gone.
- **A building the campaign raises sits in its ground rather than on a plinth of it.** A site was seated on the highest column under its own footprint and filled downward from there, so even ground chosen for being the flattest available still left the build standing as far above the world as the footprint's own fall-away, on a bare square wall of its own dirt with the sill a good way up in the air. A site is now set half that drop into the ground, four blocks at the most, so the ground comes back up around its foot and the slope shows through the bottom of the building instead of standing beside it. Flat ground is untouched by it: a site with nothing falling away beneath it has nothing to sink into, and keeps the seat it always had.

- **Angmar has a country of its own.** The northern barrow country was standing in for it, which made the Witch-king's realm the Barrow Downs with another name. It now has its own biome: a subarctic rocky upland, below freezing so its weather falls as snow, with thin cold soil over broken stone, grass only in the hollows, boulders and rubble, deadwood, stumps and brambles, wind-beaten spruces, a far grey fog, and a spawn list of orcs, uruks, wargs, wolves and crebain. The map's Angmar country, the fortress and the arena all name it, so the ground matches the country that was drawn.

- **The fortress and the arena say Angmar in the data too.** Both were seated in Angmar by the canon table, but their structure definitions still named Mordor, which is a claim nobody reads any more and everybody would have believed. Their biome list is now Angmar's, and the two generated `has_structure` tags were brought into line with it rather than left to a datagen run.

- **The last act happens in Angmar.** Weathertop was the first landmark to be seated by the map rather than by whoever took the page; the Witch-king's fortress and the arena beneath it are now seated the same way, in the country the books give them. Both were written into the canon table, which means they are surveyed from the middle of Angmar and may drift two map cells from it, so the campaign's finale stands in the same part of the world for every world and every hall - and, as before, a castle page already in hand is re-surveyed rather than kept. The arena also gained the id the table needs, so a location page can name it directly.

- **A landmark may drift, but only within its own country.** Asking the map for the middle of Eriador puts Weathertop in the right part of the world, but the middle of a country is not necessarily ground a hundred-and-twenty-block building will take, and a survey that refuses there falls back to pitching the site in front of the player again. A landmark's search is now held to its own country within two map cells of that middle - so it can take the cell next door when the exact one is a river or a slope, and cannot wander off into whatever else the map draws - and only widens past its country if nowhere within that reach will take it. Camps and the other non-canonical builds are unaffected.

- **A landmark stands where the map puts it, and a page is a walk.** Two faults put Weathertop beside the herald's own hall. The survey begins in the cell the player is standing in, so a structure whose biome list fits the country they are already in was seated at their feet - the site came out at the hall's own coordinates, and the log then refused to build it there. Sites now keep a floor distance of 192 blocks from the page-taker. Weathertop was the other half of the problem: it is Amon Sul, a hill in the heart of Eriador, and it should not depend at all on where the page was taken, so the landmarks the books place are surveyed from `ArdaMap.anchor`, the middle of the cells the drawing gives their country. A site of one is surveyed again even when a page to it is already waiting, which costs nothing - the answer is the same spot for every player - and corrects a site pitched before the map was asked where the landmark belonged. Camps and the fortress keep their biome lists and are still looked for around the player.

- **A hilltop in the trees can be seated.** Site ground was read off the motion-blocking heightmap, and the top of the world in a wood is a trunk: a 121-wide footprint under a Mirkwood canopy measured as a cliff from trunk to grass, so every candidate spot was refused and the page waited on a hilltop that was never going to stand (the log said only "No ground within 48 blocks"). The read now walks down past logs and leaves to the ground the column is actually made of. The flatness rule also no longer refuses outright: it still prefers ground that is level within tolerance and returns the first such spot it meets, but when there is none it keeps the gentlest ground it saw and builds there - standing on the highest column and letting the hill show through what the building left empty, which is the same bargain a build struck into terrain already makes. The fill under the floor now reaches as far down as the ground fell away, up to twenty-four layers, so a building on a bank gets a bank under it instead of six layers of dirt and an air gap.

- **Everything the mod puts down fills under itself, found or forced.** Two mechanisms, one rule. A structure the world generates is bearded: the game fills the ground beneath the piece up to the terrain around it, and every generated structure the mod ships already asks for `beard_thin` - the provider now says so for every surface structure rather than skipping the ones given a top offset or a companion, so a re-run of the data cannot drop the fill under the pieces that stand highest. Underground pieces keep `none`, since there is nothing beneath them to fill. Ground the campaign raises itself is not bearded by anyone - it is placed block by block after the world is generated - so it fills its own: every column of the footprint is put down to the first solid block below, six layers at most, which is what stopped the tent standing over the sea.

- **Structure void is not a hole: it is a block.** Every template the campaign has raised until now cleared its footprint first, and the hundred-block hilltop cut for Weathertop was going to carry structure void through its open sky so the ground it landed on would show. Neither was needed, and the void was worse than unneeded: this version's template placement does not skip it - nothing in `StructureTemplate` or `StructurePlaceSettings` mentions the block at all - and `StructureVoidBlock` is an invisible six-pixel cube, so a template full of it raises an invisible lattice wherever it is placed. That is what was showing up in the earlier converted builds. Empty cells in a template already mean "leave what is here alone" (the tent records air and so still clears its own box; the castle records nothing and never touched the ground), so the Weathertop template records no air at all, the box clearing is gone rather than special-cased, and `WorldScan` gained a `stripvoid` mode which has been run over the older converted templates to take the lattice back out of them.

- **A site a hundred blocks across can be raised without dropping half of it.** Three things assumed a tent. The build only asked whether the chunk at the site's middle was loaded, and a block written into an unloaded chunk is quietly discarded - so anything wider than a chunk would have been built in pieces, or in one piece and a blank. The footprint was cleared box-and-all before the template went down, which for a build carrying structure void across its open sky would have cut a crater the size of the building first; only the cells the template actually fills are cleared now, and its own air does the rest. And the search for ground stepped by half the building's width, which for a hilltop a hundred wide meant one candidate and no second chance, so the step is a quarter of it and the reach grows with the build.

- **A walk the world had not named is named by the map instead of refused.** The herald hands out walk pages only where a place has been placed in the world, and a placement has only ever been made by an operator standing at the spot - so on a world where nobody had done that, the West's line stopped dead at Weathertop and stayed there, with the book saying the ground had not been named yet and nothing anywhere to say how it ever would be. The map already draws every name in the catalogue, so a walk whose ground is unplaced now asks it where that place would stand, writes the answer down as a placement, and hands the page; the banner fires on arrival as it does at a hand-placed one. A place an operator has placed is never moved.

- **A site stands on ground rather than over a hole.** A building is set at the highest column under it, so on a bank or a shoreline the lower side of it was left floating with air or sea showing under a wall. Every column of the footprint is now filled back up from the building's own base to the first solid block, the way a village does it, for a few layers at most - so the tent on the beach in the test sits on earth, and nothing gets a plinth hung off a cliff.

- **A site the ground refuses is no longer silent.** Real ground was looked for within eight blocks of the spot the map picked, and if none of it would take the building nothing was built and nothing was said - so a player could stand on the printed point, watch nothing happen, and have no way to tell a hard spot from a broken page. The search now reaches forty-eight blocks, which is the order of the drift between the drawing and the world, and a site that still will not stand is written to the log once rather than swallowed.

- **Walking through a leaf pile no longer crashes the game.** The pile asked the client for a tinted falling leaf, handing the particle engine a colour to go with a particle type that was registered without one; the engine casts what it is given back to its own type, and that cast brought the game down the first time a foot went through a pile. The pile and the pack it sends for a client both ask for the plain maple leaf now, which is the same leaf the biome's own wind already drops.

- **A page in the book can be read to the end.** The leaves were narrow enough that an errand came out as "Reach somewhe..." and the line at the foot as "Hand a finished quest to...", which left the player holding work they could not name. The book is wider, the ask wraps onto a second line where the row has the height for one, the count has moved up beside the giver's name to free the foot of the row, and the foot hint wraps instead of being cut. Four errands still fit one leaf, because the room the hint needs is measured before the rows are sized rather than taken out of them.

- **`/tmquest page` can be run at all.** The branch was written to hang off the command root, but its brackets left it nested inside the target selector, so the literal was read as a player's name and the page was never built. It is a sibling of the selector now and writes the page into the book of whoever ran it.

- **Turning the story leaf crashed the game.** The line that says what a page pays handed the translation a string builder, which is not one of the things a translatable argument may be, and the game took the exception rather than the page: a player turning to the East's steward page - the one that wants a scroll carried and pays nothing in standing or goods - was looking at a crash before they could read it. It passes the words now, and a page that pays nothing in goods draws no reward line at all instead of announcing a reward of nothing.

- **The Rider is met on the road, not at the counter.** Taking the campaign's last page at a hall's bench stood the Nazgûl up ten to fifteen blocks from the player at the moment of the hand-over - on the hall's roof or in its yard, beside the body who had just written the page - which read as a conjuring trick rather than the meeting the page promises; and the body that was stood up was remembered only in the running session, so a reloaded world forgot him, and a player carrying the page in a country he had since left could be left with a hunt that had nothing left to hunt. He is owed the road now: the campaign remembers where the page was taken and stands him up once the player is out under open sky and a ride away from that door, a little ahead of them on the ground, with a line in dark purple saying that something is waiting; the same walk brings him back after a reload, because a Rider is found by the tag he wears before a second one is ever stood up. A player who has already felled him is not met by another, since the kill is counted while the page is still carried and a finished page is what tells the two apart.

- **A hall's giver says what he is, and what he has.** His nameplate carried his name and nothing else: no office, and no mark unless the player had already reached the rank of stranger with his country - while he hands his page over to anyone who asks, so a player standing in front of a body holding work was told by the plate that there was nothing there, and the page he was given a moment later proved it wrong. He now wears his office beside his name in the grey of a subtitle - Maelgrin, Herald of the West - and the mark and the page now answer to one rule instead of two: the gold **!** of work on offer appears only to a player the country will actually deal with, and the herald refuses the page below that same rank instead of handing it to someone his own plate had told there was nothing here. The grey **!** of work in hand and the **?** of work to hand in still appear to whoever carries his page, whatever their rank, and a hand-in is never refused. The mark still stops when his front has nothing left to give, and it is read from two places rather than one: the quest sync, and the book the player is holding, so a page taken before the sync carried a giver's id still marks the body that handed it over, and a page of a front marks that front's herald even when it remembers no id at all - the gold **!** of "work to take" standing over a body whose page the player is already carrying was the reading that could never be true. The office line no longer depends on the alliance the clothes are copied into either - a hall placed from a template can reach a client with that copy still empty, and a herald read as an ordinary man lost his office while keeping the gold name the server had given him - so a body named for a front is recognised by the name he wears as well.

- **The story leaf turns.** A player can carry a page from each of the three fronts at once, and the leaf drew whichever of them came first in the book and then stopped - the other two were in hand with no arrow, no count and nothing saying they existed, so a campaign in progress read as a single page. The heading now carries **<** 2 / 3 **>**: the arrows turn to the next front's page, the count says how many are open, and an end dims rather than vanishing so the count always reads true. The bounties leaf, which answered a player holding three of the campaign's own pages with "no bounty in hand - a giver out there has work", now says where those pages are.

- **Stranger was written in ink that could not be read.** The rank's colour was near-white (0xD8D8D8) in a book drawn in dark ink on parchment (0xFFE9D8B4), so the rung a player had just been told they had not reached was the one line of the ledger they could not read. It is slate now, and the chat colour is left alone - a name in chat is written on the dark, not on paper.

- **The story leaf reads its page whole.** The step's name and the place it happens were drawn as one line each, so a heading that fitted was followed by "Slay 12 soldiers of H..." and "the Green Way, where the..." - the two things that say what the page is and where it is, both cut off at the width of the leaf. The objective and the place now wrap onto a second line the way the prose under them already did, and the summary gives up one of its lines to make the room.

- **A page of the campaign says what it is.** Six of the seven soldier pages carried no name and no place, so the story leaf drew the objective alone and the gold heading a campaign step is supposed to have never appeared - the page read as one more errand with a name under it. Each of them is named now - the first muster, the long road south, the watch on the marches, the second levy, the sun on the sand, the gathering at the ford - with the ground it happens on written under it, translated into Chinese beside the English.

- **A hall's giver keeps the hall.** The herald stood at his bench with his brain on, so he pathed to the door, walked out of it and was next seen somewhere in the country; a player who came back to a hall they had already met found an empty room and concluded the hall had never been staffed. The home area the hall wrote into him was a list of ints where the game reads a block position back, so it never loaded and nothing was holding him; he is planted the way the warden and the envoy at their benches already are, in the template and on load alike, so he holds the spot the hall put him in - and he turns slowly to whoever comes through the door, a giver who cannot leave but is not a statue either.

- **A hall's giver is named in gold.** His name was written as the plain white text a template holds, so the one body in a hall carrying the campaign's pages read like any nameless traveller. He wears the front's gold now, in a hall just cut and in one already standing in a saved world, which is the same gold the mark over his head is drawn in.

- **The alliance guard's switch switches the guard off.** Its entity id was written with a leading space, so the switch never resolved to anything the game has - it logged that it named no such mob and left the guard spawning with the switch off. The id names only the mob now.

- **A front moves on when its page is paid.** The campaign noted the page it handed over at no progress, and then looked for that exact record when the page was settled - but a settled page has been advanced by every blow the player landed, so the records never matched and the herald handed the same page out again for as long as the player kept finishing it. Pages are now recognised by their terms - what they ask for, of whom, and how many - so the first page paid moves the front to the next one.

- **The campaign's own page is recognised while it is being handed over.** Whether a page was the Rider's was read from what the front had already noted as handed over, which is nothing at the moment he is handed over, so handing him out never spawned the Rider he asks for. It is read from what the page asks for instead, which is the Nazgûl, so the Rider is stood up when the page is taken and a second herald will not write a second copy of him.

- **One page per front, whoever hands it over.** A second herald of the same alliance - an old hall beside a new one, or a hall beside a giver stood up by the command - used to hand over a second copy of the step already in the book. Only the copy the front noted last could ever be paid, so the front could not move on at all. A herald whose front is already being carried now says so instead of writing it again.

- **A new world starts with no quests in it.** A world's campaign and ledger are held in maps that live as long as the game does, and only a world with a save file put them right: a fresh world opened in the same session was handed the last world's stages, its book and its giver clock, so a brand new player opened a book full of other worlds' work. A world with no file now starts those maps empty.

- **A campaign giver says when there is nowhere to stand.** The command had two answers, and the one for "a giver is already standing here" was also the one for "there is no ground within reach", so it claimed to have found the giver it had failed to place. There are three answers now, and the third names the ground as the problem.

- **Halls cut before the givers were named name their herald.** An old hall's "Herald of the West" is still the body that hands out that front's pages, so on load he is given the name the book writes down - Maelgrin, Guthlac or Zarabi - unless a pack has named him something of its own.

- **A campaign giver is stood on ground, not on the player.** `/tmquest <targets> campaign_giver <alliance>` dropped the NPC in the very block the operator stood in, so it rose inside them and could land in a wall, a river or the air; it now looks for solid dry ground a few blocks off, turns to face them, and reports how many actually stood up. The alliance it is handed is checked against the three real ids too - `Alliance.byId` answers the West for anything it does not recognise, so a typo quietly dressed Maelgrin and reported success - and the lookup for a giver already standing no longer walks every entity in the level. Both answers are translated now instead of being English literals written into the command.

- **The giver skins load.** A texture id on this version is the pack path itself, extension included - which is why the human variants are built as human1.png - so the three alliance skins asked the pack for a file with no extension at all, were reported missing and were drawn as the magenta checker. They name their .png now, and the renderer still falls back to the human's own clothes if a pack has no such art.

- **A giver without its art keeps its own clothes.** The renderer now asks the pack whether the alliance skin is really there, once per file per reload, before drawing it: a missing file leaves an ordinary human in the clothes of its own folk instead of the magenta missing-texture checker, and a single line in the log names the file that was looked for.

- **Two item models stop grumbling in the log.** The tobacco pipe and the fancy bottle were the only element-drawn models with no `particle` texture named, which the client warns about every time it loads models; both name one now, taken from the art they already draw with. Nothing about how they look changes.

- **The giver skins are in the same pixel format as the rest of the mod's art.** All three were saved as plain 8-bit RGBA, as every other skin in the mod is, instead of one paletted map and one image with no alpha channel - the client's own PNG reader, not Java's, is what has to take them, and this gives it the only form it has never had trouble with.

- **Quest givers can no longer be killed.** A giver is made invulnerable and persistent on its way into the world, so a mob that wanders into the scene cannot end a quest by killing the one thing that can hand it in - and givers already standing in a saved world are protected on their next load.

- **Sleepnesia keeps every sleeper's sleep to themselves.** An effect is one object shared by everything holding it, and the draught kept its slumber in a field of that object: the first sleeper in a world put everyone who came after them to bed for good, and nobody stood up again. Whether a sleeper is still under is now read from their own instance of the effect, they are stood up a few ticks before it lapses - once it has gone there is no tick left to do it - and only a player is laid down, so a boss handing the draught to something that is not a player no longer breaks on the cast. The movement modifier it added and never reliably took away is gone with it; the slowness and blindness it already applied are what keep a sleeper still.

- **The blessing of Eru cures what it is meant to cure.** It is an instant effect, and an instant effect is never ticked, so the cleansing written as a tick never ran at all - whatever the blessing was drunk or eaten from did nothing. The cure happens where the game applies an instant effect now, and it takes only harmful effects: the old test compared an effect instance against the category enum, which is never equal, so the draught was also stripping every buff its drinker carried, miruvor and elven vitality among them.

- **The book reads without its shadow.** Every line of the ledger is now drawn flat instead of with the drop shadow vanilla puts behind text. A shadow is drawn for the dark of the world behind a nameplate; on parchment it sat under dark ink on a light page and made names and numbers look smudged. The dim, gold and blue inks are a shade darker to make up the contrast the shadow was adding.

- **A new world no longer freezes on the way in.** Angmar's biome was registered and named by the tags, but its definition was never written out: every other biome has a file under `worldgen/biome/`, and the one key with nothing behind it made the biome registry refuse to freeze, which crashed the client while a world was loading. The data generator wrote `angmar.json`, and with it the biome list in `dimension/arda.json` that Arda's biome source counts itself against - the line whose absence would have failed the dimension the same way.

- **A building that is already standing is not raised a second time.** Taking the page again - a campaign
  walked through twice, or a second player sent to the same landmark - pitched the site afresh, and the
  survey put it back on ground its own first copy had already changed, so the crag went up beside the one
  already there. A built site now answers every page that names it, and a landmark stands once in a world
  rather than once for each player sent to it.

- **Gimli is stood at the hold he keeps.** The reach that decides when a keeper is put down was written for a
  camp, and it was measured from the middle of the building: Gundabad is a hundred and twenty by a hundred
  and eighty, so its own door is a hundred blocks from its middle and a player standing in the hall was
  never close enough for the dwarf to be stood. The reach now grows with the building, and the search for
  one already standing uses the same distance.

- **A city taller than the sky is refused out loud.** The world drops a block above its own ceiling without a
  word, so planting Rivendell on Arda's surface laid the top two of its six bands into nothing and left its
  towers cut off at the build limit, with the command reporting them as laid. `origin` now says how far over
  the sky the city would stand and the highest y the corner can be planted at, and `band` leaves those pieces
  out by name instead of counting them.

- **`/tmcity band` says which bands it laid, not how far up the city it has got.** Its count is how many bands
  to lay, and it was reported as "band 3 of 6" whatever number was passed: a caller reading the count as "which
  band" typed 1, 2, 3, laid six bands in three calls, and then found the last three had nothing to do because
  the city was already whole. The line now names the bands it laid ("bands 1-6 of 6 down"), counts what is
  still waiting ("3 bands to go"), and says plainly when there is nothing left.

- **A piece of the city keeps its own size, so a cut whose box is not a multiple of 48 lays whole.** Every
  piece was taken for a 48 cube, which is true of most of a cut and not of the slivers along its edges: the
  top band of the new Rivendell cut is four blocks tall, and a sky check that added 47 to its bottom refused
  the band as though it would reach past the ceiling.

- **A world made before the valley no longer refuses to open, and gains the biome.** Arda's biome list is
  stored with the world, and the valley added a thirty-sixth entry to a saved thirty-five, so the dimension
  aborted on load with "the two lists are written together and cannot be edited apart". The source no
  longer reads the world's copy at all: it names its biomes as ids and resolves every one of them from the
  registry, so a world made earlier opens with the real imladris biome rather than a stand-in, and its next
  save writes the full list.
### Added

- **The campaign has an ending: the Rider is everybody's page.** A front is two soldier pages now, and the Nazgûl is not the last of them - all three countries ask for him together once every line is settled, any herald hands him over, and all three pay the 200 standing between them, since he was feared by all and sent after by none of them alone. Settling him writes the war's end into the quest book's story leaf, and a herald with no page left says where the war stands instead of idling.

- **The herald speaks when handing over the Rider page.** When a herald gives the player the campaign's final page - the hunt for the Nazgûl - he says one line in gold before the book takes it: "All three fronts are settled. The Rider is yours." The same sentence is shared by all three heralds, and it is translated into Chinese as well.

- **The Rider is spawned by the campaign and outlives his corpse.** When a player accepts the Rider page from a herald, a campaign-spawned Nazgûl appears 10-15 blocks away, tagged as the campaign's Rider for that player. The Rider can be killed normally - he takes damage, drops loot, and shows a death message - but the campaign remembers that this was the campaign's Rider, not a random Nazgûl. When the Rider dies, he is removed from the tracking map, and can be respawned fresh for the next stage. This implements the campaign's narrative that the Rider cannot be truly killed: the player sees him die, but he returns unharmed on the next page.

- **Campaign pages can now differ per alliance.** The campaign's page line was a single list shared by all three fronts; it is now a map of alliance to page list, so each front can have its own sequence of pages. The East front now has a third page - a FETCH of the Witch-king's scroll, one of the mod's `item_scroll` - standing in for the campaign's page 9, "The Steward of the South". It is handed over and handed in like any other page and pays the act's turn; the arena the scroll names is neither placed nor revealed yet, and where the scroll is taken from is not yet pinned to a village. This infrastructure enables the rest of the campaign's staged pages to be added as fixed quests at known locations rather than random errands.

- **Named campaign givers can be spawned at faction halls.** A new system allows named NPCs like Maelgrin (West), Guthlac (East), and Zarabi (Harad) to be spawned at their respective faction halls. These givers carry the `tolkienmobs:campaign_giver` tag and are automatically named according to their alliance. The `ensureCampaignGiver` method checks if a campaign giver already exists nearby before spawning a new one, preventing duplicates. A new command `/tmquest <targets> campaign_giver <alliance>` allows operators to spawn these givers manually for testing or staging. This infrastructure enables the campaign's staged pages to be handed out by fixed NPCs at known locations rather than random travellers.

- **A third page on every front: the Rider.** A front no longer only counts soldiers. Page 0 asks for six of one rival's, page 1 for twelve of the other's, and page 2 asks for one Nazgûl for two hundred standing, so the line changes kind as it goes. Standing is now paid for any kind of page rather than faction work alone, which is what lets a story page be a hunt, and the Rider is named from the registry so a page can never ask for a mob this world does not have.

- **A second page on every front, and the campaign line is a table now.** Page 0 asks for six soldiers of one rival army for sixty standing; page 1 asks for twelve of the other rival's for a hundred and twenty. `Campaign.PAGE_GOALS` is the line, so a front has as many pages as that list is long and adding one is a single number. The ledger's courage row also sits with the countries now, one row-spacing under Harad, instead of pinned to the foot of the leaf.

- **A giver turns away a player who is already carrying all the work they can hold.** Right-clicking a giver with a full book of errands now answers "You look too busy to help right now." instead of silently refusing the new work. Handing in a finished quest and taking the herald campaign page still work at any fullness, since neither is an errand competing for a row.

- **The quest book has a bounties leaf and a story leaf.** The quest bookmark no longer splits the work down the middle of the spread and hides whatever did not fit: **Bounties** holds the errands, hard-limited to what one leaf was measured to show, and **Story** holds the campaign step alone, with the room the other entries took given over to its description. A campaign page is exempt from the bounty cap, so a book full of errands never blocks the mainline.

- **A campaign stage counter, and the herald has a page.** faction/Campaign keeps how far each player has got on each of the three fronts, saved with the world and synced to its owner. Page 0 of every front is written - six soldiers of a rival army for sixty standing - and the hall herald hands it over directly rather than leaving it as an offer to browse. The front only moves on when the ledger settles that exact page, so a page dropped or cleared from the book leaves the front where it was, and the mark over his head stops once a front has nothing left to give.

- **The herald's gate is cut.** His gold mark is drawn only for a player who is at least a **stranger** to his alliance - the first rank above outsider, 100 points of ordinary hall work - and a mark for anyone below is dropped, so the hall's herald keeps a bare name until the country knows you. The mark stands with no page behind it yet, which is the campaign saying it has somewhere to begin and not yet what; until a page is written, right-clicking him answers that there is no work.

- **The hall staff move, a little.** The warden and the envoy are frozen at their tables on purpose, so their life is drawn rather than walked: each turns slowly about the place it was set and lifts its head from the work now and then, and turns to look at a player who comes up to the counter before settling back to the work, in entity/util/TableIdle. It needs no new tag - the hall tags they already carry, and having no brain, are what make them staff - so a hall placed earlier comes alive without being cut again.

- **A hall has a herald.** The third body the campaign wanted stands in every alliance hall now, at the workbench the warden does not use, cut into the structure beside the warden and the envoy. He is a human in all three halls, because the skin he wears is the quest-giver art of his alliance, and he carries the giver, the alliance and the campaign tags, so his clothes and his gold mark are right from the first load. He walks the room rather than standing on it: the hall gives him a home area six blocks out from the middle, and a walk target that would leave it is pulled back inside, so a hall looks lived in without the herald wandering off. He has nothing to hand out yet - the pages and the rank that opens them are still owed - so he walks it silently, and turns to face a player who comes within five blocks of him. A herald already standing in a hall placed before this walks too: the join repair gives an alliance-tagged giver its brain back and a home area if it has none, so an older hall comes alive without being cut again.

- **The giver who stands in a hall is dressed for it.** The one body in each hall wears a skin of its own, one per alliance - `faction_west/quest_giver_west.png`, `faction_east/quest_giver_east.png` and `faction_harad/quest_giver_harad.png` - chosen by the scoreboard tag that names the alliance it speaks for. A giver met out in the world carries no such tag and keeps the clothes of its own folk, because the people who walk a country are ordinary people. All three are human-sized, so any race the mod ships can be pointed at a file.

- **A giver shows what it has for you, and which line it belongs to.** The mark over a quest giver is three states now instead of one: an exclamation mark when there is work here you have not taken, the same mark dimmed grey while you carry work from here and have not finished it, and a question mark when it is done and can be handed in. The ink is the line - blue for an ordinary errand, gold for the mainline - so the campaign's own business stands apart from a farmer's fence at a glance, and a giver is the mainline's by carrying the `tolkienmobs:campaign` scoreboard tag rather than by anything about the quest it holds. It is drawn onto the nameplate, so it costs no texture, no second render pass and no per-tick work, and it is read per player from the quest sync: the same giver can be a gold mark to one player and a bare name to the next. For the mark to follow the right body, a quest now remembers the id of the NPC that handed it out rather than only the name it was given; work already in a book carries no id and simply marks nobody.

- **The war's three acts are written down.** `docs/war-of-the-three-fronts.md` holds the West's questline page by page in the older quest-sheet style - who speaks, what they say, the ground the errand happens on, what it pays, and which of the mod's quest kinds can express it today. The Rider is killable and comes back, because a Nazgul that cannot be hurt reads as a bug while one that dies and returns tells the truth. A closing section lists what a line for the East or Harad would change, so the other two fronts are a rewrite rather than a redesign.

- **The fortress and the arena are structures of the mod now.** The Witch-king's fortress and the arena beneath it are registered builds, so either can be put down without carrying a template about: `/place structure tolkienmobs:boss_castle` and `/place structure tolkienmobs:boss_arena`, both cut from Mordor's own stone. Neither is drawn into a world by the generator - where they stand is the campaign's business - so Mordor is not littered with fortresses waiting to be found; giving one a structure set later is a one-line change.

- **Courage is earned, and the dark learns to let go of a player who has.** A player now holds a courage of their own, counted in deeds and read at the foot of the ledger's alliances leaf beneath the war horn that answers fear. Work handed in is a deed, standing in terror while it lands is a deed - once a minute at most, so the endurance is the deed and not the ticking - and felling one of the Nine or their master is worth several. The ladder is six rungs long and each rung takes exactly one thing from the terror: first the legs are given back at the price of a heavy, noticeable slowness, then the blindness, then the weakness, and from Fearless up the crippling terror fails to land at all; at the last rung even the dread aura of the Watcher and the Spider is refused, and only its wither still bites. When a scream fails to pin a player who has earned that much, one line appears under the item name and fades: *You do not flinch.* An operator can put a courage total where they like with `/tmcourage <targets> <deeds>`.

- **The war horn answers the Nazgul.** A horn of the campaign's own, blown on a note of its own: every player within twenty-four blocks of it is freed of the crippling terror and the dread aura, the one who sounded it included, and then it will not sound again for twenty seconds. It clears fear and nothing else - poison, corrosion and the rest are left alone - so a Nazgul met without it is still everything a Nazgul is.

- **The boss fortress and its arena are cut from Mordor's own stone.** Both builds stand in dark stone now - cobbled, brick, smooth and mossy - with Morgul iron where the iron bars were, and every plank, log, stair, slab, fence, gate, door, trapdoor, sign, ladder and torch that was oak, spruce, birch or jungle is mirkwood instead. Wool, glass, books and the growing things are left as the builder left them.- **The work can be arranged by hand.** `/tmquest <targets> give [count]` fills a book with work drawn the way a giver draws it, `quit` sets aside everything a player carries, and `spawn` stands a real giver with a real offer beside them at once - skipping the wait and the one-giver rule, so a scene can be staged or the system tried without a walk in the world. Like the faction commands it answers only to a gamemaster-level operator or a player in creative.

- **Work can be set aside.** A quest a player has changed their mind about - a hunt for mobs they cannot reach, an errand taken on too eagerly - can be abandoned from the book: a small **x** closes the giver's line on the quests page, and the first click only arms it, turning the button into a question, so a mis-click costs nothing. Setting work aside pays nothing and takes nothing out of the pack, and the place it held in the book is free again at once, so a player who puts one down can still take up another.

- **A pack can set the work, the numbers and the folk who hand it out.** Everything a giver draws on now lives in `[server.quests]` of the common config: the items a fetch quest may ask for, the mobs a hunt may ask for, the goods and the coin it pays, both bounds of each kind's goal, what a faction quest is worth in standing, and how often a giver in a country offers politics at all. The tables hold registry names, so a pack puts work to its own items and mobs without a line of code. It also names its own folk, because a giver is now one name and one title drawn separately from two tables and joined with a space - ten of each is a hundred travellers - while both tables left empty keep the mod's own eight, which stay translated. A quest keeps the terms it was taken on, so a change here reaches the next offer rather than the work already in someone's book.

- **Questing: a traveller with work turns up, and a book to keep it in.** Every so often a named traveller now arrives near a player - sixteen to twenty-eight blocks off, on dry solid ground, never inside a block - and stays put: he is persistent, so the work he is holding is still there when the player comes back for it, and a gold **?** sits on his nameplate until someone takes it. Right-clicking him hands over the work, and right-clicking any of them with a finished quest hands it in, so a giver who has wandered off is never a lost quest. Three shapes of work are drawn: **faction** work kills three to six soldiers of a rival alliance and pays standing with the country the giver stands in; **fetch** work asks for four to twelve of one item from a pool of nine, read straight off the pack; and **hunt** work kills three to seven of one mob. Goods and coin pay the last two, sized by how much was asked for, because a farmer has no standing to give away - so questing is worth doing whether or not a player cares who holds the West.

- **The quest book opens the ledger on a Quests tab.** A player is handed the book the first time they take work on, and only ever one: the quests belong to the player, so the paper is only how they are read. **J** opens the same screen on its Alliances page, and the two tabs sit under one title, because who a player stands with and what they are doing are two halves of one question. Each quest shows its picture, its objective, a progress bar and what it pays; a fetch quest fills its bar as the goods are gathered, a kill quest as the kills land, and a finished one says so until it is handed in. Up to four are carried at once, and a fifth is refused rather than silently dropped.

- **A workbench is furniture two blocks wide.** The factions work at a bench of their own now, drawn from a model that reaches a full block past the cell it stands in - tabletop across, a hutch along the back and a shelf over it - so its outline follows the drawing instead of blocking a plain cube, and the overhanging half is as solid as it looks. It turns to face whoever places it, drops itself when broken, and waits in the function tab; its item is drawn at half the size of a block item, so the bench fills the icon rather than swamping the hotbar and the hand.

- **Faction envoys sell for coin and token.** Every hall now keeps a merchant at its counter, and the shelves he opens are your rank with that side - Guest, Friend and Kinsman, one shelf each. Every offer costs faction coin and at least one faction token, and the top shelf carries a piece of the alliance's own: the Star of Earendil in the West, the Morgul Knot in the East and the Sun-brass Amulet in Harad.

- **A contract is titled with the work it asks for.** One taken from a board is named after the task - how many soldiers of which alliance - so a pack of them can be read without opening a tooltip. The tooltip is unchanged: employer, objective, progress and pay.

- **A contract board posts mercenary work, and a contract is the paper it is written on.** Standing with the three alliances could only be earned by killing and by handing tokens over, so there was no work to take. Right-clicking a contract board now posts a contract for the alliance the player stands best with, against one of its rivals: slay between four and eight of that side soldiers, drawn fresh once a day and different for each player reading the same board. The terms live on the contract item, so the paper and the work are one thing - a contract thrown away is work abandoned, and two contracts are two stacks. Every soldier of the target alliance a player kills moves every contract they carry against it, the count shows over the hotbar, and a finished contract is paid at a board for its reward in standing with the employer while both its rivals lose half of it, so serving one side is felt by the other two. The board is drawn with its own model now - a structure wider than the block it stands in, whose outline is carried as the block shape, so a click lands on the parts that hang over - though it still has no screen; the contract is a rolled scroll of its own now, drawn at 32 by 32 from the art painted for it rather than on vanilla paper.
- **An operator can set and read a player's standing by command.** Standing is earned in the world, which is no use when a world is being built, a scene staged or a rank tested: `/tmfaction set <players> <west|east|harad> <amount>` now puts a standing exactly where it is asked for, and `/tmfaction get <players> [west|east|harad]` reads one back, or all three on a single line in each alliance's own colour. Both also answer to the flat names `/tmsetfaction` and `/tmgetfaction`, so either order comes naturally, and everything the command writes goes through the same bounds as a kill or a hand-in - never below the floor a country turns hostile at, never above the ceiling - and is sent to the player it happened to, so their ledger redraws at once. Like any world-shaping command they are for a gamemaster-level operator or a player building in creative.
- **The Alliance Guard has a spawn egg, and a switch of its own.** The soldier the factions muster had no egg, so there was no way to meet one without first making a country hostile, and no way to test the liveries without `/summon`. It is in the spawn tab beside the other hostiles now, with `enableFactionGuard` under `[server.mobs]` so a pack can turn it off like any other mob. An egg names no side, so a guard that arrives without one takes a random alliance on its first tick and wears that side's skins - an explicit `Alliance` in a summon is kept. The three alliance tokens, the one thing else this work added, were already in the treasure tab beside the faction coin.
- **Standing with the three alliances, and a ledger to read it by.** A world now keeps a standing for every player with the West, the East and Haradwaith separately, because the three are rivals rather than a single score. Killing a soldier of one alliance raises the two that are not his own by five and costs his own the same, so a player who clears an orc camp is noticed by the West; handing the tokens of an alliance to its warden is worth ten points a token to that alliance and costs each rival half again as much, so swearing to one side is felt by the other two. A loss is felt in full, down to the floor of hostile: killing your way through a country's soldiers is one way to become unwelcome there and handing its rivals tokens is the other. There are six ranks from Outsider to Kinsman over a point total, and a total below zero is **Hostile**, which is where a country starts sending its guards. Every change is drawn on a screen of its own: **J** opens the Ledger of Alliances, which shows each alliance's rank, its bar, the next rank and the country it holds, all read off what the server last sent the player rather than off anything the client keeps. Killing, handing in and logging in are the three moments a standing moves, and each tells the player in chat or over the ledger.
- **Each alliance builds a hall in its own countries.** `tolkienmobs:faction_hall` is cut into three variants that share one plan and differ in the wood the hall is built in, so the West's hall stands in its countries, the East's in Mordor's, and Haradwaith's in the desert; each is a rare structure so a hall is found rather than stumbled over, and its shell is structure void so the country's ground is only replaced inside it. A warden waits at each hall with two scoreboard tags on him, one naming him a faction envoy and one naming his alliance; right-clicking him with that alliance's tokens hands over every one in the pack, and a hall also raises two of its own soldiers when a player it will not have comes within forty blocks of the warden.
- **Each hall stands its front's named giver.** A hall placed a body called "Herald of the West", "of the East" or "of Haradwaith" and nothing else: he handed out that front's campaign pages, but he carried no name of his own and nothing said the work he held was a front's pages rather than an errand. He is **Maelgrin** in the West's hall, **Guthlac** in the East's and **Zarabi** in Haradwaith's now - the names the pages are written for - and he carries the `tolkienmobs:campaign_giver` tag `ensureCampaignGiver` looks for, so a hall and `/tmquest <targets> campaign_giver` agree on which body is the front's own instead of the command standing a second one beside him. The book writes his name under the page he hands over, so a page taken at a hall says who gave it. A hall generated before this keeps the herald it already has; he is marked as the front's giver when he loads, so an old world does not gain a second one standing beside him. The three templates are cut again from `faction_hall.nbt` and every block of them is unchanged - only the body at the bench is.
- **A campaign page says what it is called and where it happens.** A page was a kind, a count and a price, so the story leaf at the back of the book could only ever say what to count: the West's muster, the East's scroll and the Rider all read as errands that happened nowhere in particular. A page now carries a name and a place, both translation keys, and the leaf draws the name over the objective and the place under the sentence of what the step is for - so the two steps the design names now read as **The Steward of the South**, met on the road through Nurn, and **The Rider's Word**, met wherever he chooses to be met. A page with no name of its own is left blank rather than given an empty heading, and the leaf falls back to the objective, so the muster pages read exactly as they did before.
- **A country that will not have you sends its soldiers.** An alliance a player is hostile with now patrols its own ground: within a hundred ticks it looks for a chance to place one of its soldiers twelve to twenty blocks off in open country, and two when the player is standing at a hall. The cap is counted on what is already standing nearby rather than on a timer, so a player who kills the patrol is met by another and a player who runs is simply left behind, up to three at a time. All three field a new **Alliance Guard**, one soldier drawing on each alliance's own painted set - eight liveries in `faction_west`, six in `faction_east` and six in `faction_harad`, and each alliance's variant rolls only inside its own set - and armed with ammolite in the West and morgul iron in the south and the east. A guard is a soldier rather than a night creature, so it stands in daylight, and it drops issued kit with the coin its alliance pays it in. It also takes its blade the first moment it stands, because 26.1's `Mob.finalizeSpawn` no longer hands a mob its equipment: a guard is placed by its faction rather than spawned naturally, so nothing else would ever have armed it.
- **The Shire grows pipe-weed, and a pipe blows shapes in the smoke.** Pipe-weed has been a crop with seeds and a block since before this and grew nowhere in the world, so the only way to farm it was to find the seeds in a chest. It now stands wild across the Shire as a meadow weed, taking hold on turf and soil as readily as on a tilled bed, in three strains: **Longbottom Leaf**, the leaf the mod already had under the plain name pipe-weed and the one in the most patches, beside **Old Toby** and **Southlinch** in fewer. Each strain is a crop and a seed of its own, so a garden tells you which leaf it will give, and a ripe plant gives its leaf and its seeds. A **tobacco pipe** - two planks and a stick - is drawn on until the bowl catches and spends one leaf of whichever strain the smoker is carrying, then blows that strain's shape into the air: a smoke ring for Longbottom, a little ship under sail for Old Toby, a long winged serpent for Southlinch. Each strain leaves a brief, small comfort as well - a little Absorption, Regeneration or Saturation - and a draw released before it catches spends nothing. A hobbit village grows its own leaf too: each of the three burrow plans now lays a nine-plant bed of tilled turf beside its door, mostly Longbottom with a plant of Old Toby and one of Southlinch, so a Shire town keeps a garden of it without a villager having to find the seeds. Its fields are leaf fields as well - the farm a Shire village draws is the vanilla plan with every row replanted in that same mix, so no wheat, carrots, potatoes or beetroots stand among the burrows, and the farm still joins a street from its own front edge. The plot is built like a hobbit's own, too: the one-block wall vanilla rings it with is lebethron, the wood the burrows are already built and lit in, rather than oak, and the composter in its corner is a lebethron barrel that opens toward the rows.
- **A draw that runs its full length smokes the bowl by itself.** Holding the pipe past the end of the draw used to achieve nothing at all: the draw completed, the bowl stayed packed and the use ended, so a smoker who held a moment too long had held the button for no reason. A completed draw now burns the bowl exactly as a released one does. That also makes a hold a loop: the draw ends with an empty bowl, so a smoker still holding the button packs the next leaf and draws again, one bowl at a time, and the pipe stops on its own when there is no leaf left to pack. Running out no longer fills the chat either - the "no pipe-weed" line is shown over the hotbar instead, where a held button refreshing it a few times a second cannot pile up lines in chat.
- **A draw on the pipe shows how far it has come.** Holding the pipe on a loaded bowl gave no sign of how long to hold: let go too soon and nothing was smoked at all, and nothing on the screen said where that line was. A short bar now appears over the hotbar while a draw is under way, filling in the bowl's own flower colour and carrying a notch at the shortest draw that counts - a release before the fill passes it only warms the leaf, and the fill takes the strain's pale smoke colour once it is past, which is the point where letting go will actually smoke. It sits between the item name line and the experience bar, and it is drawn only while a loaded pipe is in use, so an idle pipe shows nothing.
- **Each of the mod's villages stocks its houses from its own chest table.** Three of them built their houses with an empty chest, and two borrowed a table that belonged to a ruin. `tolkienmobs:chests/frozen_house` is now the Lossoth house's larder - cram, dried cod, leather, hide and the iron traded for at the coast; `tolkienmobs:chests/orc_house` an orc's den of grog, meat, bone, the coin it took off the last one through, gunpowder and a scavenged blade; `tolkienmobs:chests/desert_house` a Harad house of waybread, leather and what the caravan brought back; and `tolkienmobs:chests/nurn_house` a Nurn farmhouse of wheat, potato and beetroot, a hoe and very little else, because everything else is taken. The hobbit village's second burrow now uses the baker's pantry that had been written for it and wired to nothing, and the whole assignment lives in one place: a village's houses are prepared during data generation, so that is where each plan's chests are stamped with the village's table. Nurn builds the desert house and eats from a Nurn table, which is the point of doing it there rather than in the plans.
- **The overworld's own mobs drop the mod's coin in more of the places it belongs.** A bogged carries the bronze its drowned kin do, the pillagers and vindicators carry bronze and the evoker silver, since raiders are Men with a purse, and a witch - who has always gathered herbs - now and then carries the pungent herb. A shipwreck's strongbox carries silver beside the bronze the wreck's supply chest already offered.

### Changed

- **Imladris is a vale inside a massif, not a pan beside a hill.** The city is carved into the sides of a
  mountain whose crown it reaches, so the ground has to be there before it is planted: the valley floor is
  the city's own foot with a field's width around it, and the ground outside it climbs straight from the floor
  to y 300 - the map's own ceiling, just under the city's crown - before it is handed back to the Trollshaws
  over a second fall. The city's carved flanks run up into ground already as high, and the ridge is what a
  player sees from outside rather than a wall of the city's own stone standing on a plain.

- **The generator says which shape it is drawing, once, when it loads.** A client that is already running
  keeps the classes it started with, and a world made in it is generated by the old code however new the
  source is - which is how three test runs in a row tested terrain from before the fix. The chunk generator
  now logs one line at load naming the shape it draws, so the log answers "is this the new build" before a
  world is made rather than after.

- **The city's shoulder is a ramp instead of a step.** Four rings and a six-block cap could only meet
  ground already near the build's base, so the rim of a city on a hillside was left as the cut face it
  complains about: 5,518 of its columns were skipped for being too far off to meet in one step. The shoulder
  now walks out from the walls at about twenty-one degrees and stops where it has met the ground, cutting the
  hill down or building the hollow up a block at a time, keeping the grass on ground it moves and counting
  what it cut, what it filled and what already stood on the line.
- **The valley is centred on the city planted in it.** Its middle sat 276 east and 263 south of the city's
  own middle, so a city planted at 4352, -3833 stood with part of its footprint against the valley's wall.
  The middle is 4628, -3577 - the planted corner plus half of the 554 by 512 footprint - and the rim is 128
  blocks of climb rather than 64, so the ground outside the walls is a mountainside a player walks down rather
  than a cliff to look at.

- **The Wood-troll is Legolas' page at both ends.** A page owed to a body is handed and taken by that body alone, so naming Legolas as the turn-in is what keeps the hall's herald from giving out the hunt for a creature it has never seen - the same shape the Giant already had at Elladan's camp.
- **The Elves of the line stand at their doors.** Glorfindel is out on the square's southern road, Elrond at the library's door and Legolas at the guest house's, instead of all three on the middle of the place, where the buildings now are.
- **The duergar are the East's.** They are dwarves who took the Enemy's service, so the peoples table says so rather than reading as though every dwarf were Erebor's.
- **Every people in the notes is placed with one of the three sides.** The notes kept a faction per people, and this pack keeps three sides, so `Alliance` now records which is which: Elves of any kind, Dwarves, Hobbits and the Men of the West are the West; Mordor, Angmar, Isengard, Orcs, Goblins, Uruk-hai, Trolls and the Easterlings are the East; the Haradrim, the Corsairs of Umbar, Khand and the Variags are the Harad.
- **The notes' races and realms are read as the three fronts.** Where the document names the payer as the High Elves, the Dwarves or Gondor, the line takes it as the front the act belongs to and pays West, East or Harad standing, since a page pays one of the three countries and never a people.
- **The notes' standing is taken at a flat tenth.** Their ladder ran to five or ten thousand and this one tops out at fifteen hundred, so the rule is now one divisor rather than a figure per page: the cave hand-over's hundred pays ten and the Giant's nine hundred pays ninety. Pages the notes never priced keep the figures this line has always paid them.
- **The line's standing is the notes' figures scaled to this pack's ladder, and says so.** The notes were written against a ladder running to five or ten thousand; this one tops out far lower, so a page they price at a hundred pays twenty-five here and the Giant's nine hundred pays fifty. That rule is now written above the page list rather than rediscovered each time a page is added.
- **The Giant is worth fifty of the Elves' standing, not nine hundred.** The notes pay the kill nine hundred, which is most of a rank in this pack for a single fight; the page now pays fifty and keeps its courage, so the Giant is a step in the line rather than a shortcut up it.
- **The cave hand-over pays in goods as well as standing.** The notes end the search with a choice of equipment, an enchantment and ore; a page does not roll loot in this pack, so the camp now hands over a reforged blade and four mithril nuggets with the standing it already paid - twenty-five of the Elves', not the hundred the notes wrote, which is most of a rank here.
- **A cleared chamber pays its cave completion token, and the camp wants all three back.** The notes' cave quests each pay a token and the camp asks for three of them with the beryl, so the item the pack already shipped as proof of killing the troll is now actually handed out: one per cave troll felled at depth. Elladan refuses the stone while fewer than three are in hand and says how many are missing, and the hand-over spends the tokens and the beryl together.
- **A troll cave is cleared by its troll, not by standing in it.** The notes write the three caves as area kills of a cave troll rather than as ground to hold the beryl over, so the stone's caves page now moves on a cave troll killed under deep rock rather than on the raise: the raise is the reading it always was, and a chamber with its keeper still standing says so instead of paying the page. Depth is read off the body, so a troll dragged up to the mouth and killed there does not count.
- **The troll cave is set into its ground by its grass line.** Its template is cut in two halves - bare rock from its foot to y 8, grass from y 9 to its crown, with the mouth's sill at y 6 - so seated the way a house is seated, on the highest column under it, it stood on that rock skirt with the grass a good six blocks above the ground it had been dropped on, and the lower half read as stone that should have been buried. A building now says how deep it wants to be set instead of every piece being given one rule: a hall, tent, house or arena asks for no floor and keeps what it had - half the drop under its footprint, never more than four blocks - while the cave asks for nine, which puts its grass at the ground it is placed on, and never more than ten, so the worst a footprint this size can leave it is one block low.
- **The ledger is an open book, and both halves sit on one spread.** The ledger was one narrow panel with the alliances and the quests behind two tabs; it is a leather book now, with two parchment leaves, a shadow down the gutter and its two bookmarks stitched over the spine. The alliances bookmark keeps the standings on the left leaf and the work in hand on the right, so who a player stands with and what they are doing are read together; the quests bookmark spreads the carried work across both leaves, two entries to a page. Each quest also records who offered it and where, so the book prints the giver and the place the work was taken under its objective, with a sentence of what the work is for and its progress at the foot of the entry - and the giver is kept as the key it was translated from, so the book reads the name in whatever language its reader uses.

- **Every faction hall is built around workbenches, with its warden and envoy at one each.** The four tables a hall has are workbenches now, set against its side walls and turned to face the aisle so the two blocks a bench is drawn across stay clear of the way through, and the three alliance halls were cut again from the build as it now stands in the test world - its sea-lantern lighting and opened-up floor included. The warden works at the western bench, the nearer of its pair to the front door, and the envoy at the bench across the aisle, each standing on the side its own bench is worked from.

- **Every faction hall carries a notice board.** One stands outside each hall's front door, to the right of it as you walk up, so the hall is where mercenary work is taken on and where a finished contract is paid.

- **The notice board is drawn twice the size of the model it is cut from.** A model file cannot say this - an element may not reach past 32 pixels on any axis, and the board is already at that limit - so the drawing is scaled instead: the model is wrapped at bake time and its quads are moved as they are collected, which keeps the block in the chunk mesh with its occlusion and materials. The block outline grows with it, about the middle of its ground face, so a click still lands on the wood. Placed, it now stands 3.25 blocks wide and 2.5 tall.

- **The notice board turns to face whoever places it.** It had a single orientation, so a board set against a wall could show its back; it now takes a facing the way the well does, is placed with its front toward the player, and carries an outline per direction so the parts that hang over stay clickable whichever way it is turned.

- **The eggs are split by who each mob is, not only by its colour.** Eight of them carried the same face in eight skin tones - the plain Man, the hill-man, the brigand, the alliance soldier, the two Southron traders, the Haradrim and the Istari - so a chest of them could not be sorted without reading every tooltip. They differ now by what is on their head: a shaggy beard for the hill-man, a cowl for the brigand, a steel cap with a nasal down the face for the alliance soldier, a cloth wrap with a fold at each temple for the desert folk, a pointed hat over a long beard for the wizard, and the plain Man left bare-headed. The mouth on every face is a short line rather than a bar across it, because the lip shadow that followed it read as a small beard on all of them. The little things have faces of their own as well - two eyes too large for the face over a wide grin for Gollum, eyes on the crown of the head and a mouth ruled right across for the frog, eyes at the rim and a closed smile for the gecko, a pointed snout for the rat, two front teeth for the squirrel, and no face for the swarm at all, just specks - and the wraiths are told apart the same way: a helm with a dark slot for the Nazgûl, a crown of iron spikes for the Witch-king, the open hood for the spirits. The wolves gained pricked ears and a tapering muzzle and the horses a shaded one, so neither reads as a masked man any more.
- **Every spawn egg is repainted in the vanilla idiom.** Each egg had been its own mob's icon poured into an egg-shaped hole with the group's colour mixed through it, which muddied every face and, once the colour batching went, left several eggs almost blank - a black fell beast, steed or crebain showed nothing on a black shell. All fifty-six are drawn the way vanilla draws an egg now: a smooth shell in the mob's own colour, lit at the crown and shaded at the base behind a darker rim, with a face set into the middle of it - slitted orc eyes over tusks, four spider eyes above fangs, a mumakil's tusks either side of its trunk, a mimic's lid of teeth, a watcher's one great eye, a mumakil's and a minotaur's horns above a broad snout. Faces are built in code from a small alphabet of markings, eyes, teeth and lit glows, so a mob that is more than a man or a beast gets a face of its own rather than a recolour, and every face colour is held far enough from its shell to stay legible on a dark egg. The birds carry a bill rather than a smudge: crebain and the two eagles a bone hook tapering to a point under a heavy brow, with the corners of the mouth dark either side of it, and the thrush a small pointed bill between two wide-set eyes.
- **Loading a pipe is a gesture of its own.** The pipe loaded, drew and smoked on one held button, so there was no way to tell a loaded pipe from an empty one: a quick tap did nothing at all and looked exactly like a tap that had worked. Tapping a pipe with leaf in the pack now loads the bowl - one leaf leaves the pack and stays in the pipe, the bowl takes that strain's colour and the tooltip names it - and it stays loaded until it is smoked, so a pipe can be filled before a walk and lit on the way. Holding the button draws on a loaded bowl and smoking empties it again; sneaking and using tips the leaf back into the pack, so a pipe is never stuck holding a strain the smoker has changed their mind about. Smoking is also quicker and worth more: a draw is one second rather than two, the cooldown half a second, and the comfort lasts a minute (thirty seconds for Southlinch) rather than ten, so the leaf is no longer spent in less time than it took to notice.
- **A pipe blows its shapes in dots, not in clouds.** The ring, the ship and the dragon were all drawn from vanilla's campfire smoke, one puff of which is most of a block across: sixteen points a hand's width apart merged into a single cloud, so all three strains gave off what looked like the same puff with a silhouette in it. The figures are now twice the size and are drawn with a fifth-size smoke of their own, `tolkienmobs:pipe_smoke`, while the puff at the mouth stays a full plume.
- **Each leaf says what smoking it does.** The three strains are the same plant with three different comforts, and nothing in the pack said which was which - the bush told you a colour and the smoke told you a shape, but the reason to choose one over another was invisible. Every leaf now carries a line in the Shire's own words: hardiness for Longbottom Leaf, mending for Old Toby, a filled belly for Southlinch.
- **The tobacco pipe is a tool, not a plant.** It sat in the natural blocks tab among the pipe-weed it is packed with, where nothing you hold belongs; it now sits in the tools tab beside the coin pouch, the key ring and the hobbit ring.
- **A pipe's draw and its strains' comforts are settings.** The draw, the shortest draw that still smokes and the cooldown were three numbers in the pipe's own code, and the three strains' effects lasted as long as the enum said, so a pack that wanted a slower, longer smoke or a bowl worth a whole afternoon had no way to ask for one. They now live in a `[server.pipeweed]` section of the config: `drawTicks`, `minDrawTicks` and `cooldownTicks`, and a duration per strain - `longbottomDuration`, `oldTobyDuration`, `southlinchDuration`, all in ticks, twenty to the second. The shortest draw is held under the full draw whatever the two say, so a value set above it cannot leave no draw able to finish, and the numbers are read where they are used, so a change shows on the next pipe lit rather than the next world.
- **Two of the pipe's Chinese lines were saying the wrong thing.** Tipping a leaf back used 倒回 - "poured back", which is what one does with a pocket full of loose tobacco, not with the single leaf knocked out of a bowl - and the empty-bowl tooltip read 烟锅：空的 where a tooltip wants the terse 烟锅：空. They now read 你把长谷烟叶收回了背包 and 烟锅：空. The rest of the pipe's Chinese was already idiomatic and is unchanged, as are the three leaves' names and their lines.
- **A pipe's smoke is the colour of the leaf in it.** All three strains burned to the same grey plume, so the one thing a smoker actually watches was the same whether they packed Longbottom, Old Toby or Southlinch. Each strain now draws its plume in the colour its flowers are drawn in, lifted a third of the way toward the pale centre of the bloom so it reads as smoke rather than paint: pink for Longbottom, salmon for Old Toby and orchid for Southlinch, and the thin thread the pipe gives off while it is being drawn on takes the same colour, so it is clear what is about to be smoked. The two messages the pipe shows were sitting under the item's old name and came out as raw keys; they now sit under the pipe itself, so an empty pipe says you have no pipe-weed and a finished smoke names the leaf that went into it.
- **A pipe's bowl shows which leaf it is packed with.** The bowl was a solid lump of the pipe's own wood, so the one part of the pipe that holds the leaf was the one part that never showed it. Its opening now draws a packed-leaf texture of its own, heaped a little over the rim, and that leaf is coloured with the strain that is actually in the bowl: loading a pipe puts a leaf in it, and the bowl is drawn in that leaf's flower colour until it is smoked. The colour is the flower at three quarters strength rather than full: leaf down in a bowl sits out of the light, and full-strength Old Toby salmon was close enough to the pipe's honey-gold wood to read as bare wood. An empty bowl takes a dull ash brown, so a pipe lying on a shelf tells you whether it is loaded or burnt out. The pipe's tooltip says the same in words - **Bowl: Longbottom Leaf**, or **Bowl: empty** - and because the bowl is a state of the pipe rather than a reading of the pack, it says the right thing in a chest or an item frame and keeps saying it after the smoker has picked up a different strain.
- **The pipe-weed plant is redrawn.** Each strain's eight growth stages were a few stepped lines with three flat flower dots; they now carry a slender leaning stem, lance-shaped leaves set alternately up it with pale midribs and a shaded underside, and a head of blossoms that opens as the plant ripens - two buds, then four, then six, then a full head at the last stage - so a ripe plot reads as a mass of flower from a distance and you can see when to cut it. The three strains keep the old heights and the same eight stages exactly (7, 14, 25, 29, then full 32 pixels), and differ only in tone: sage leaf with dusty rose flowers, olive-gold with amber, grey-green with mauve.
- **Each strain of pipe-weed has its own leaf and seed in the inventory.** Longbottom, Old Toby and Southlinch differ as plants, as seeds in the ground and in the smoke they leave, but their two item icons were one picture copied three times, so a chest of one strain looked like a chest of another. Each has its own now: a sage leaf carrying one of the plant's pink blossoms for Longbottom, an olive-gold leaf with its salmon for Old Toby and a grey-green leaf with its violet for Southlinch - the blooms picked from the very colours the crops flower in at their last stage, so a harvested leaf is recognisably the plant it was cut from - with the seed piles shaded to follow: warm brown, golden, grey-violet. The three share one leaf and one shading, so what reads is three strains of the same plant rather than three plants.
- **Every village now farms in its own wood.** The Shire's field had been rebuilt as a hobbit's - lebethron wall, barrel in the corner - while the other villages still put vanilla's oak at the edge of their plots. The dwarves' fields are walled in dwarven maple, the men of the Vales' in culumalda, the elves' in mallorn, and the two desert villages' in the wood their own houses are cut from: mirkwood in Mordor and deadwood in Nurn. A desert plot's rim is smooth sandstone stairs rather than a fence, so it is rebuilt as the post it stands in for, and the jungle trapdoors over its channels become the country's own; the plains plots change only their wood. Every plot keeps the composter, which is where a farmer works - the Shire's barrel is the one exception - and every field still joins a street from its own front edge. Measured after `/place structure` on a flat world: the dwarven village drew five fields, each of dwarven maple and no oak anywhere in them, and the orcish village both of its desert fields in mirkwood.
- **A mob's drops now read as a purse rather than a shopping list.** Every table is built from the same three parts: what the creature physically is - hide, meat, feathers, a wraith's remains, a golem's stones - which is certain on a player kill; the purse of coin that matches its standing; and the one odd thing worth hunting it for. A regular mob's table now names at most six things, a boss is allowed more, and the pools that used to make the numbers unreadable are gone: the rock and elemental golems had eleven independent pools each, the swamp hag ten and the Watcher eleven, and both golems now give two rolls from a single weighted pool of their six stones.
- **Faction coin, faction tokens and dark sigils are carried only by the mobs they mean something to.** They used to drop from every hostile creature in the mod, which made a barrow-wight's sigil worth the same as a haradrim's and left the factions meaning nothing. The orc-kin, the trolls and the human raiders carry the faction coin, the orc-kin alone the token that raises standing, and the sigil only the dead and Mordor's servants. Everything else keeps the coin it had, at the tier its standing deserves: bronze for the rabble, silver for the undead and the elite, gold for a Nazgul, mithril for a boss.
- **The golems are no longer better to mine than an ore vein.** They dropped a mithril block and a morguliron block - nine ingots apiece, free - alongside all six of their stones. Their bodies now give the stones, and a cut of worked metal only sometimes: an ingot or two, not a block.
- **The beasts of the field have drops of their own.** The aurochs, the goats and Shadowfax all gave exactly the same three things, so a legendary horse of the Third Age was worth a side of beef. Goats give mutton, aurochs beef and their hides, the mumakil leather, meat and the bone of something that size, and Shadowfax gives the hide of a horse out of legend and nothing else. The Istari, a person rather than a monster, no longer leaves monster flesh behind.
- **Village chests hold a village's goods instead of a stronghold's.** A hobbit hole used to give diamond tools and an elven house golden ones, which was richer than any chest vanilla puts in a village. Each culture is now stocked in its own idiom and capped at iron: the hobbits with farm tools and a well-kept pantry, the dwarves with a pick, ore and coal, the elves with waybread, a draught and the arrows of the Galadhrim - the elven bow the one good thing in the room, at the end of the weights - and the houses of Men with grog, bread, the household's letters and an iron sword on the wall.
- **The metal ores climb a ladder again.** Mithril and morguliron each had their deepslate ore paying three to nine - more than the End and Nether ores above it - while the End rung paid two to five. Each now gains exactly one on the way up: stone one to three, deepslate two to four, End three to five, Nether four to seven. Deepslate still pays better than the stone it was, because it is the harder rock to break; it no longer out-earns the rungs above it. Ammolite's four had always run in order and are unchanged.
- **Chest loot runs the right way round.** The desolate ruins gave diamonds in threes to sevens at the highest weight in the table, so the best thing in a ruin was also the most common; the barrow chest's loudest entries were music discs and horse armour. The coin, the iron and the bone are now what a ruin is mostly made of, with the ammolite and the relic at the tail.
- **A boss always leaves its trinket.** The trinket is the signature of a fight - there is one per boss, already rolled for its effect - but it sat behind a one-in-four roll, so the reward for the fight was usually absent and the same boss could be killed for nothing at all. Every boss now leaves its trinket whenever a player makes the kill, and the roll decides which effect it carries rather than whether it appears. The Enraged Fell Spirit, the one creature in the boss tag that carried no trinket at all, now leaves the cloak - the shadow a wraith wears instead of a body. The Nazgul's cloak and Gollum's ring are certain too, since each is the reason to go looking for the creature that carries it. The mimic chest is the one trinket left to chance, where Looting still improves it.
- **The four lair tables that were copies of each other are four tables.** The swamp hag's hut and the spider tree gave identical loot, and the minotaur's hoard was the warg pit's with the coin changed. Each now holds what its occupant would have: the hag her herbs, her bottles and what she has been feeding; the spider its silk, its eyes and what it wrapped up; the pack its bones and offal; the minotaur what it has taken off everyone who came before.

### Fixed

- **The ledger keeps its text on the page.** Every line was drawn at whatever width it happened to be inside a 176-pixel panel, so a long objective or a rank name ran off the edge of the screen. Lines are measured before they are drawn now and either trimmed with an ellipsis or wrapped onto the leaf, and the book shrinks to fit a smaller window rather than hanging off it.

- **The notice board loads instead of showing a missing cube.** The upper rail was authored standing up and turned flat by a 90-degree element rotation, which put its bounds at y 38.5 - past the 32 the game allows, and it is checked before any rotation is applied - so the whole model was thrown away and the block fell back to an untextured cube. The rail is laid flat in the model at the same place, and the model carries display transforms now, so its icon and held form are scaled instead of drawn full size.

- **The world's milestone file was read lazily, so a change made early in a session could be lost.** A saved data is decoded on its first lookup rather than when the world loads, and decoding replaces the map, so a milestone placed, renamed or broken before that read would be dropped by it. The file is now read as the server finishes starting, before a player or a block can touch a milestone, so every change lands in the map the world holds and is saved.
- **A standing a command set never reached the ledger.** A world's faction file is not read when a player joins but lazily, on the first lookup of the saved data, and reading it replaces the standings held in memory. The setter wrote the new standing first and read the file afterwards, so that read threw the write away and the sync which followed told the player the numbers the file already had - the command answered correctly while the ledger, the ranks and the guards kept the old standing. The file is now read before the standings are read, written or sent, so a player who has just logged in is shown what the world holds rather than the empty row built for them, and a set sends the value it stored even when that is the value already there.
- **The ledger of alliances drew its rows empty.** Every alliance's colour is written as `0xRRGGBB`, and the screen handed those straight to the text and bar calls, which read a colour's top byte as alpha - so the three names, their ranks, the point totals and the bars themselves all came out fully transparent and a row showed nothing but its token and its background. They are drawn opaque now, and a bar fills in the rank's own colour.
- **A hall's warden introduced himself as a brace of JSON.** The hall's warden was placed with `{"text":"Warden of the West"}` written into its custom name, which 26.1 takes whole as literal text rather than parsing, so the name tag read the braces and the word `text`. The three halls now carry the plain name, and the tool that cuts them writes one.
- **Killing a soldier cost his alliance nothing.** A loss was held at the bottom of the rank the player already held, but a rank is read off the point total, so at a threshold the rule froze the number: six brigands killed moved the West by zero. Losses are now counted in full, which is what makes a country turn hostile from a player's own violence as well as from a rival's tokens.
- **Mithril ore was listed as an iron-tier block.** `tolkienmobs:ore_mithril` and `tolkienmobs:ore_nether_mithril` were named a second time in `#minecraft:needs_iron_tool`, a leftover from before all four variants of every ore went into `#minecraft:needs_diamond_tool` through the mithril, morguliron and ammolite ore tags. It changed nothing in play, because the diamond tag covers all twelve ore blocks, but it made mithril the one ore of the three whose declarations disagreed with its siblings. The iron tag now names no ore at all, so mithril, morguliron and ammolite ask for the same pickaxe everywhere.
- **A chest in Forochel, in an orc house and in the second hobbit burrow generated empty.** The three plans carry a chest but no loot table, because a village builds its houses from copies prepared during data generation and nothing ever put a table on them; the baker's pantry was written and generated but no template named it. Every village house now carries its own table, and the five prepared copies that changed were regenerated.
- **`shipwreck_supply` patched the village temple chest.** The global loot modifier named for a shipwreck's supply chest pointed at `minecraft:chests/village/village_temple` - the same table as the modifier above it - so a shipwreck kept an empty hold and a temple drew the coin twice. It names the shipwreck's own table now.
- **An acorn never fell from a mallorn.** Every tree the world places is built from the hardened leaves, the untinted face of the same leaves, and that table gave shears the plain leaves, a sapling and sticks - but not the acorn the plain leaves' own table carries. Since no tree is ever built from the plain leaves, the acorn never dropped where a mallorn actually grows and a squirrel was the only way to find one in Lorinand. The hardened table now carries the plain one's extra drop as well, so the two differ only in which leaves shears hand back.
- **The barrow grave and the desolate ruins multiplied their loot by accident.** A count function sat on the pool rather than on the items in it, so every item a chest produced had its count rolled twice on top of the pool's own three to five rolls. The counts belong to the items they describe now.

## [5.1.635] - 2026-10-28

### Added

- **Arda**, a second overworld of the mod's own, reached through the Arda portal. It stands at the overworld's height, under its sky and its clock, on the same coordinates, and no vanilla biome grows there: the country it holds is drawn rather than computed. A grid of 48 by 36 countries, the proportions of the maps themselves and so 24,576 by 18,432 blocks, is read through the world's own noise, which warps a border by up to 192 blocks so a drawn line comes out organic, and the height is blended between neighbouring countries with a smoothstep, which is what turns a drawn coast into a shore and a drawn wall into a slope. The Misty Mountains run from Mount Gundabad to the Gap of Rohan, which is what leaves Eregion and the Vales on their flanks; Ered Luin walls the western seaboard behind Lindon; Mirkwood lies behind the Anduin with the Wild Wood and the Brown Lands below it; and Mordor stands in its ring of ash - Ered Lithui across the north and Ephel Duth down the west - with Nurn and the Sea of Nurnen inside and below it. The map's own seas become ocean, banded by latitude so the water off Forodwaith is frozen and the water off Haradwaith warm; a land column within three blocks of the waterline reads as a beach; and the seven rivers the maps name - the Anduin, Brandywine, Greyflood, Isen, Celduin, Forest River and Entwash - are cut where they are drawn. The height a country is drawn at is the height it stands at, so every range rises where the map puts it and every field lies flat where the map has none; the world's own noise still supplies what it is good at, the aquifers, the veins, the deep stone and the caves, and water is laid to sea level wherever the map draws ground below it. A lit Arda portal carries you between the two worlds at the same coordinates, arriving at a doorway already standing within sight of the crossing or, when there is none, at an ammolite doorway the mod builds and lights where you stand, so a traveller is never left without a way home; the staff lights a portal on either side, and a place in Arda is the same place it is in the overworld, so a base built at the Shire's origin comes out in the Shire's fields.
- Seven countries of Arda had no biome of the mod's own to stand on, so each has one. **Wild Wood** covers the wood between the Anduin and the Brown Lands, which lets the Old Forest stay the Barrow-downs country its own lore asks for; **Rohan** is the Mark, the grass between the Misty Mountains and the white wall; the **White Mountains** are the wall itself, snow on the tops and stone under the feet; **Dunland** is the wild hill country north of the Isen; **Lindon** is the Grey Havens' coast behind Ered Luin; **Gondor** is the fields between the white wall and the sea; and **Eriador** is the empty middle of the north where Arnor's towns once stood. Each carries the plants, the fog and the mobs of the country it stands for: the Mark and Gondor are the free peoples' fields with their riders and their townsfolk, Eriador keeps company and hobbits, Lindon keeps elves, Wild Wood keeps goblins, spiders and brigands, and the White Mountains are troll country and wild as they should be.
- Arda's countries know which of them are safe to walk at night. A `tolkienmobs:peaceful` tag names the free peoples' heartlands - the Shire, Lindon, Eriador, Rohan, Gondor, Lórinand, the Gladden fields, Forochel, the Blue Mountains and the Iron Hills - and no biome in it spawns a hostile mob, so a traveller can cross the west of the map as safely as the Shire has always been; a `tolkienmobs:hostile` tag names the enemy's country and the wilds that answer to it, and those biomes keep their hostile spawns whatever folk live among them, so a road through Mordor or the spiders' wood stays dangerous. Two tags of the maps' own reading, `tolkienmobs:snowy` and `tolkienmobs:windswept`, are published beside them.
- **The southrons' and the orcs' traders are met in the countries that keep them.** Haradwaith's creature list carries a southron trader beside the mumakil - one entry of five, so a meeting every sixty-odd chunks of the country - while Mordor and the Ash Mountains, which share one list, carry an orc trader as the only creature they have, at a hundredth of their chunks; Nurn keeps the same trader among its farmers. A trader is placed only while a chunk is being made, never by the tick pass that keeps topping up the mobs around a player, so a country seeds its merchants as it is drawn instead of gathering them around whoever stands still in it, and each one arrives with vanilla's own visit timer. Measured in a fresh world: four southron traders over 256 chunks of Haradwaith and one orc trader over 256 of Mordor, both carrying `spawn_type: CHUNK_GENERATION`.
- **The seven countries drawn for Arda are met in the overworld too.** They were only in Arda's biome list, so a player exploring the overworld's Tolkien region could cross the whole of it without ever standing in Rohan or Gondor, however much of the map those countries cover. Five take the vanilla slot whose ground they share - the Mark the savanna's grass, Ered Nimrais the stone peaks, Dunland the last windswept hills, Lindon the cherry grove's green land, and Gondor the meadow rather than the Shire's plains - and two take cells the region's own slots had left over: the Wild Wood the warm rolling half of the forest, whose flat half is Lothlórien's, and Eriador the cool dry flats beside the Shire's fields. No older biome of the mod gives ground up for them, and nothing of vanilla's the region had not already renamed. Measured in a fresh overworld with no player online, all seven answer `/locate biome` within 2,500 blocks of the origin - the nearest, the Wild Wood, 45 blocks out - and each stands on the ground where it was found.

- **A traveller's tent is met in the wilds.** `wild_tent` is a structure of its own now, one piece dropped straight onto the ground it was found on, with a `#tolkienmobs:has_structure/wild_tent` tag of the twelve countries a road crosses rather than any that has towns of its own: the Wild Wood, the two Mirkwoods, Fangorn, the Old Forest, the Barrow Downs, Dagorlad, the Marshes, Firien, Hithaeglir, Dunland and Eriador. It is placed once every twenty-eight chunks, eight chunks of separation, so a tent is a find on the way somewhere rather than a feature of the country. Measured in a fresh world, `/locate structure` answered a tent 1,189 blocks from the Arda origin and it stood at -798,69,-877 in `tolkienmobs:eriador` with its campfire and its chest twelve blocks apart, exactly as the template lays them out.
- **A wilderness tent keeps a traveller's chest.** `tolkienmobs:chests/wild_tent` rolls two to four times, with one bonus roll, over six things a night out in the wild actually asks for: cram in ones to threes, two to four torches, four to eight arrows, a bottle of grog, flint and steel, and pipe-weed in ones or twos. The weights favour the food and the light, so a tent usually gives you something to eat and something to see by, and the pipe-weed only now and then. The `wild_tent` template's own chest names the table, so a tent that stands in a world fills as it is first opened.

### Changed

- **The hobbit village is built from three plans now, drawn equally often.** It used to put up the one hobbit hole over and over; it now builds that house beside two burrows cut into their own hills - a small one with a garden, a cellar, a smithy corner and a lodestone in its yard, and a large one terraced up a slope with a courtyard, a well and a quartz-walled room - so a village of the Shire is a mix of all three. Each plan was taken out of the build as it stands, its ground and its gardens part of the template, the empty corners of its rectangle written as structure void so they clear nothing they stand over, and its interiors left as air; every chest in a burrow is emptied and given `tolkienmobs:chests/hobbit_baker`, a baker's pantry of bread, cookies, wheat, cram, honey cake, pumpkin pie and honey. A plan's doorway socket is written on its own front edge, at the row its garden is walked on, and the village reaches no more than four rows below that row.
- **Five of the mod's villages light their streets with a lamppost of their own.** They used to stand vanilla's lamp - a fence post with four torches round its top on the plains, a sandstone post with one torch in the desert - because a street draws its light posts from the vanilla decor pool. The posts the mod has always carried are used now, the one that belongs in each country: the cobblestone and oak post of the human and dwarven villages, the black stone and soul fire post of Mordor, and the sandstone post with its sea lantern in Harad and in Nurn, where its stone is recoloured dark with the rest of the village. Each carries a decor socket at the foot of its post, and each village points its vanilla style's decor pool at a pool of its own which keeps vanilla's own draw - tree, flowers, hay, cactus, nothing - at vanilla's own weights, so only the light post changes; Nurn is the one that keeps no cacti, a cactus garden not being what a farmed lowland looks like. The elven, hobbit and frozen villages keep the vanilla posts they had. Measured after placing a village of each style in a flat world: 6 posts in a Harad village, 4 in a Mordor one, 2 in a Nurn one, and not one of vanilla's, every post with its foot on the ground and its head four rows above it.
- **The dwarven village is built from the five plans it was drawn out as.** It used to put up one house over and over; it now builds the two halls that fill it out, the windmill that stands behind them, and the smithy and the meadhall that front its square. The halls are what the house pool picks most - weight 6 each - and the windmill next at 3, while the smithy and the meadhall are not in the pool at all: they stand on the village's square, so each is built exactly once. Each plan was taken out of the build as it stands, so its plot's own ground is part of the template, the empty corners of its rectangle are written as structure void and so clear nothing they stand over, and the interiors stay air; every chest in a build was emptied and given `tolkienmobs:chests/dwarf_house`, and the smithy, which had none, was given one. Measured in two villages generated in a fresh world: both halls, the windmill, the smithy with its four forge blocks and the meadhall with its blue banners, one of each, with 1,200 and 1,369 blocks of the mod's path laid between them.
- **The human village is built from the five plans it was drawn out as.** It used to put up one house over and over; it now builds the triple house a street grows up around, the single house and the market that fill it out, and the inn and the church that stand about once in a village. The triple house is the plan the house pool picks most - weight 6 - the single house and the market next at 3 each, and the inn and the church once each at 1, so a village is mostly the triple house and its neighbours with an inn or a church only now and then. Each plan was taken out of the build as it stands, so its plot's own ground - the yards, the paths, the floor each house stands on - is part of the template, while the empty corners of its rectangle are written as structure void and so clear nothing they stand over; the interiors stay air, and every chest in a plan is emptied and given `tolkienmobs:chests/human_house`. Each plan carries its own doorway socket on its own south wall, at the column of the outermost door that faces the street - the triple house's south door one row in from its wall, not the doors of its two wings - and the market, which is an open square with no door at all, takes a point in the middle of its front. Measured in a village generated in a fresh world: three triple houses, two single houses and one market, with 3,102 blocks of the mod's path laid as the streets between them, where a house is only kept if its doorway reads on its own front edge.
- **The dwarven ranges are stocked with the mod's own goats.** The Iron Hills and the Blue Mountains carry the battle goats in place of vanilla's sheep and mountain goats - one in a hundred of them arrives with a dwarf already in the saddle - and vanilla goats now graze Forochel, its slopes and the Misty Mountains, which had nothing of their own below the snow line.
- **The three trades that name a building of their own work at the mod's own block.** The grocer worked at a bamboo block, the banker (the coin trader) at a gold block and the trinket smith at ancient debris - vanilla stand-ins, so the trades named for a fireplace, a piggy bank and a trinket table could only be taken at blocks that had nothing to do with them. A fireplace is the grocer's work block now, a piggy bank the banker's and a trinket table the trinket smith's, read from the blocks' own states, so a player can set up a shop for any of the three out of the mod's own furniture. The pet merchant, the junk trader and the trinket tailor keep the vanilla blocks they had. Measured in a dev world, a dwarf left beside each of the three takes the trade its block names - `tolkienmobs:grocery_store`, `tolkienmobs:coin_trader` and `tolkienmobs:trinket_smith` - remembering the job site on the block it stood beside, and the world's own poi data names a site at each of the three.

### Fixed

- **The small hobbit burrow could never stand in a village.** The village drew it, the game refused it, and the village simply showed no house where it should have stood - the burrow's front garden sits seven rows above the plan's lowest row, and a piece that dips more than four rows below its own doorway socket is dropped without a word. Measured in fresh worlds: five rows was refused on both burrows, four joined the street every time, and the small burrow appeared in no village at all. The plan is now cut down to its garden - the three rows taken off are solid hillside under it, which a village buries anyway - and its doorway and resident coordinates were written against the trimmed template; data generation now fails a plan whose doorway sits deeper than four rows above its lowest row, so another one cannot ship unnoticed.
- **A village's church never stood in it.** The church is thirty-one blocks across where every house is fifteen, so the game's own piece fitting, which draws a building onto the side of a street, refused it almost every time the house pool picked it: measured over village after village, not one had a church, and the dwarven meadhall and smithy - twenty blocks across - were dropped the same way. A village with plans like these now paves its meeting point out into a twenty-one block square, and those plans front the square instead of a street. The meeting point's own street ends are moved out to the square's edge, so the streets leave from there and nobody grows across the paving, and the buildings stand on doorway sockets the square writes for them, drawn ahead of any street by placement priority, so exactly one of each stands in every village. A village grows one step deeper to make up for the street ends a square full of buildings costs it. Measured in a fresh world: a human village holds one church (its two enchanting tables) and one inn, a dwarven one a meadhall and a smithy (four forge blocks), each village still laying 1,300 to 1,500 blocks of street.
- **Arda's ground was left as bare stone wherever its map disagreed with the overworld's noise.** Arda reuses `minecraft:overworld`'s noise settings, and the rule that dresses a biome is gated by `abovePreliminarySurface` - a guess at how high the ground stands, read from the noise itself. Arda keeps that noise but throws its height away in favour of the map's, so most drawn columns failed the guess by tens of blocks, the material rules were skipped with it, and the ground came out as the stone the generator had written, with grass only where the guess happened to land. Measured at the reported spot in a fresh world, 269 of 289 Shire columns had stone as their top block, with the flowers placed on it. Arda now lays its own surface from the same materials, unguarded, with vanilla's rule after them for the map's vanilla biomes and for the bedrock and deepslate: that same patch is all grass. The generator also rocks over the noise's own voids under the drawn top, since they were cut for heights the map does not draw - so the caves a player walks into are the carvers' own, cut from the drawn ground, and the brick under the map is solid.
- **A beach could open in the middle of a country.** The shore was read off a column's height alone, so any drawn ground standing within three blocks of sea level became `minecraft:beach`: the Shire is drawn at 68 with four blocks of relief, so its low fields turned to sand with no water in sight - measured at block 6,199 the whole blend is Shire and the top was sand at 65 for hundreds of blocks. A shore now needs a drawn sea among the countries the column blends, and is read off the blended ground rather than the relief laid on it; that spot is the Shire again, and its edges still read as beach where they meet the water.
- **Dry ground in a cell the map drew as sea read as open ocean.** A column's biome came from the cell it was drawn in, so ground that the blend had raised well above the waterline was still named for the sea beside it: at the Dunland-Rohan border, block 6600,1462, the ground stands at 71 under grass and read `minecraft:deep_ocean`. A sea cell whose ground stands dry now takes the biome of the heaviest land country beside it, so that border is Rohan and Dunland again.
- **The drawn water could be left hanging over the ground the carvers took.** The heightmaps were read from the noise's surface rather than the ground the reshape wrote, so the surface rules and the features placed from ground that was no longer there, and the carvers - which cut from the noise's terrain, not the map's - could open a cave through the bed of a drawn sea or river and leave its water standing over air. The heightmaps are re-read from the drawn ground now, and every flooded column is laid again after the carvers: stone where a cave took the bed, water over it to the waterline, and caves enclosed under the bed left alone. Measured in a fresh world at the reported warm-ocean patch, all 49 columns hold water from their bed to sea level with no air in it, and the coast that showed a floating river now runs water to the ground all the way along.
- **Every one of the mod's folk was named "Human" in the villager language table.** A race's no-trade name was written by hand as that one word, so the entry that tells a dwarf from a hobbit named all eight races a man; each race writes its own name now, and the entries read "Dwarf", "Elf", "Hobbit", "Human", "Southron" and "Orc" as they should. The same table had the pet merchant and the junk trader entered the wrong way round - the pet merchant named "Junk Trader" and the junk trader "Pet Merchant", in the per-race entries and in the profession names the game shows alike - and each now names its own trade.
- **The mod's folk were all introduced as "Villager".** 26.1 asks an entity for a type name, and a villager answers with its profession's name - so one that holds no trade, which is every dwarf, hobbit, elf, man, southron and orc of the mod until it finds a work block, called itself a villager in its name tag, in the summon message and in the death messages. A folk without a trade is named after its own kind again, while one with a trade still names itself after the trade, the way vanilla's own villagers do and the way the title of the screen it trades in does: summoned in a dev world, a dwarf comes out as `Dwarf`, a hobbit as `Hobbit`, an elf as `Elf`, a man as `Human`, a southron as `Southron` and an orc as `Orc`, and the same dwarf given `minecraft:armorer` comes out as `Armorer` and given the mod's `tolkienmobs:coin_trader` as `Coin Trader`. Vanilla's own villagers are untouched.
- **The Nazgûl steed's seat no longer points the same way whichever way the animal walks.** Its rider was placed by a fixed pair of world-axis offsets, so a Nazgûl who sat on the saddle facing north hung off the animal's side as soon as the steed faced east. The seat is now the steed's own passenger attachment point - a point in the animal's own frame, which vanilla turns by its yaw - so the rider keeps the saddle at every heading while the seat's height stays where it was. Measured in the dev world at four headings, the rider stands at 0.30 back-right of the middle at 0 degrees, 0.25 left and 0.30 forward at 90, and the same seat mirrored on through 180 and 270, always 1.375 blocks above the steed's feet.
- **A rider sat inside the animal rather than on its back.** All three of the mod's mounts placed a passenger 0.65 blocks above their feet, a figure that belongs under a much shorter animal: the saddle each model carries sits at 1.375 blocks for Shadowfax and the Nazgûl steed and 1.4 for the battle goat, whose body begins at 0.672 and whose back is drawn at 1.375. A dwarf jockey therefore came out buried in the goat's chest, and a Nazgûl in its steed. A rider now stands on the saddle - measured in the dev world, a jockey goat's dwarf at 141.4 over a goat standing at 140.0, and a Nazgûl at 141.375 over its steed.
- **None of the mod's mounts could wear a saddle in 26.1.** Vanilla names the mobs a saddle goes on in `#minecraft:can_equip_saddle`, which is the tag the saddle item's own equippable component reads, and the `Saddleable` interface the port removed is what used to allow the mod's goats and horses; the battle goat and the Nazgûl steed each kept a bare `this.isSaddled()` in the code their jockeys spawn from, which in 26.1 does nothing at all, because a saddle now lives in the entity's own saddle slot. The three mounts are in that tag, and a jockey equips the saddle its model draws, so a jockey goat or steed arrives carrying `minecraft:saddle` in its saddle slot and the saddle the model has always carried is finally drawn; a player can saddle and steer one again once it is tamed.
- **A country's own flocks could not be placed in it at all.** Vanilla's animal rule asks for `#minecraft:animals_spawnable_on` underfoot, which is grass and nothing else, so every sheep, cow, pig, chicken, horse, donkey, rabbit, wolf, goat and bear a country's list carries - and the mod's own aurochs and mumakil - was refused wherever the mod's map paints ground that is not grass: Haradwaith's sand, Nurn's dark stone, the Iron Hills' and the White Mountains' stone, gravel and snow, and the mod's own rock under Mordor and the Ash Mountains. Haradwaith could not place a single one of its mumakil and Nurn not one of its sheep or aurochs. Both passes ask the same rule, so the chunk generation pass that seeds a country as it is drawn refused them exactly as the tick pass did - the daylight the mod had blamed was never the trouble, and there was no daylight in it at all: a chunk being drawn reads as lit. A new `tolkienmobs:country_pasture` block tag names that ground, and the animals a country lists are given it in addition to vanilla's own rule, answered only inside the mod's biomes, so a vanilla desert is still no pasture for a sheep and nothing outside the countries changes. Measured in a fresh world with no player online, where the generation pass is the only thing that can place a mob: 7 mumakil over 64 chunks of Haradwaith, where the same patch of the same seed had none before the change; 3 sheep over 64 chunks of the Iron Hills; and 5 sheep and 2 aurochs over 256 chunks of Nurn, one auroch standing on `tolkienmobs:cobbled_dark_stone`. The Shire's own animals are unchanged, being on grass vanilla already counted.
- **A country's own people were written out as pigs, and met nowhere.** The mod's NPCs - hobbits, men, elves, dwarves and the traders - were registered the way vanilla registers a villager, as a `MISC` mob, and 26.1 will not spawn one of those from biome data: its spawner leaves the misc list out of the categories it rolls and refuses a `MISC` entry outright, and the spawn data quietly swaps such a mob for a pig as it is built. Every NPC entry in the generated biome data therefore read `minecraft:pig`, and no hobbit, elf, dwarf or man was ever met in the field. They are `CREATURE` mobs now - the only category 26.1 spawns out of a biome's list - and their entries sit in each country's own creature list with a spawn placement of their own, so the Shire's hobbits, Lindon's and Lórinand's elves, the Iron Hills' dwarves, and the folk of Rohan, Gondor, Eriador, the Gladden fields and Forochel live where the map puts them again. Data generation now refuses to write a country's list against a category that cannot spawn, so an NPC added later cannot quietly become a pig.
- **Forochel and its slopes were missing from Arda.** No country in the drawing named them, and Arda's biome list is written from that drawing, so neither biome could ever be placed - which also kept the frozen village, the only structure that stands in Forochel, out of the world altogether. The Ice Bay's snowfield now takes the north-west coast above Lindon, with the slopes climbing out of it toward the north end of Ered Luin. Measured in a fresh world, the snowfield stands at 72 and the slopes at 110, and the nearest frozen house is 97 blocks from the coast.
- Things placed against the drawn ground now read the map's water as well as its land. The generator answered every heightmap it was asked for with the drawn ground, even where the map lays water over it, and a village's site check begins at that height and walks down to the first block that is not empty - so a flooded column approved itself, because soil under a lake is dry soil, and a village could be seated on the lake bed. The heightmap type is honoured now: the world surface and the motion-blocking maps are read above the water, and only the ocean floor is the ground beneath it.
- **The cold countries could place none of their animals on snow.** Vanilla's floor test for a land spawn asks for a full top face, and a snow layer never has one, so a snowed column was refused before any spawn rule was reached - no rule of the mod's could put an animal back on a drift, whatever the pasture tag said, and nor could the mod's own floor. Forochel, Forochel Slopes and every range top above the snow line were shut to the polar bears, rabbits and goats their lists carry. The floor is replaced along with the rule now: a country's ground is vanilla's own, or the country pasture with a drift lying on it - the drift counts as the floor itself, since snow only ever settles on a full block - and only inside a country of the mod's, so a vanilla plain keeps vanilla's floor and its snow keeps its quiet. The vanilla animals a country lists each keep the rule vanilla gave them as well, rather than losing it to the pasture clause. Measured in a fresh world with no player online: 30 polar bears and 24 goats placed over 256 chunks of Forochel and its slopes, where the same ground placed none before.

## [5.0.607] - 2026-10-26

### Added

- The config file can switch the mod's content off, in bulk or one mob at a time. `[server.mobs].disableAllMobs` keeps every Tolkien mob out of the world at once, and directly beneath it each of the mod's 55 mobs has a switch of its own - `enableGoblin`, `enableMordorOrc`, `enableShelob` and the rest, each naming the entity it stands for and grouped as ambient, passive, NPC, hostile and boss mobs - so a single boss or animal can be turned off without touching the others. The master switch overrides them all, and a mob is left alone until it leaves a world it was already in. `[server.blocks].disableAllBlocks` and `[server.items].disableAllItems` take the mod's blocks and items out of the creative tabs and out of the recipes that make them, including another pack's recipe for one of them, and put them back when switched on again - a `/reload` is enough. Everything already placed, carried or stored is untouched. A switched-off mob is refused whichever way it was asked for, since every route into a world ends at the same point; note that vanilla's `/summon` still reports success when the entity is refused, because its own command does not look at whether the entity landed.
- A pack can tune the numbers behind the backpack and the camo spawner. `[server.backpack]` sets how far each magnet tier reaches - 16, 32 and 64 blocks by default - and the magnet items' lore states whatever is set, so a tooltip never names a reach the item does not have. `[server.spawner]` sets what a newly placed camo spawner starts with: its detect, spawn, count and cluster numbers and its two spawn delays, vanilla's by default, with the spawner's own screen still able to change any one of them. `[server.gameplay]` gains `trinketCraftTime` and `fireplaceCookTime`, the ticks either block spends on one piece of work, five seconds each by default.
- `[server.worldgen].tolkienRegionWeight` sets how much of a new world belongs to the mod's region: the mod's share is `weight / (weight + 50)`, so the default 7 is about an eighth of the overworld and 1 is about a fiftieth. The region is built into the biome source a world is created with, so a change shows in worlds generated afterwards.

### Changed

- The config file is grouped by who reads each setting. Everything the world decides is under `[server]`, in sections of its own for mobs, blocks, items, gameplay, milestones, the fell beast and world generation, and the two fell beast wind volumes - which each client hears at its own setting rather than the server's - are under `[client]`. `[server.milestone].item` is now `paymentItem`; every other value keeps its meaning and its range, and the options inside a section have lost the prefixes they no longer need. An existing `tolkienmobs-common.toml` is not carried over: the file is rewritten in the new layout on the next start, so anything changed from the defaults has to be set again once.
- The Tolkien biomes now cover about a tenth of a fresh overworld instead of about four per cent, and the twenty of them are close to evenly met. The region's weight beside vanilla went from 2 to 7, and the biomes that only inherited one of vanilla's small climate slots - Firien, the Marshes, Mordor, Ash Mountains, Hithaeglir, Fangorn, Mirkwood Spooky, the Gladden fields, Forochel Slopes, the Iron Hills, the Blue Mountains, the Barrow Downs, Lórinand and Dagorlad - were given parameter cells cut out of the three slots that hold most of the region: vanilla's plains, forest and desert. Each takings follows the biome's own climate and erosion, so a range takes the steep cells, a marsh the flat ones, and Dagorlad and Mordor the hot dry corners of the desert. Measured over 169 columns of a fresh world, every one of the twenty biomes is now within about 2,500 blocks of a random origin, where before fourteen of them sat between 4,700 and 6,100 blocks out.
- All twelve of the mod's flowers are now about as common wherever they grow: a patch of roughly a dozen blooms every other chunk, in the Shire and the Old Forest, the Barrow Downs, the Blue Mountains, Lórinand, Firien, both Mirkwoods, the Marshes and Mordor. They had all shared one profile - a patch every thirty-two chunks - so how often a player met one came down to how much of the ground accepted a flower, and the range ran from about four blooms per chunk on the Barrow Downs to one on a Blue Mountains slope. The patches stay patches: the mountains, the downs and the wastes ask for more attempts because their ground keeps few, the meadows fewer because theirs keeps nearly all, so a patch is about as full everywhere. Each count was measured in a world of that one biome rather than guessed.

### Fixed

- A camo spawner started life with a four-block detect range instead of vanilla's sixteen, so it only ran with a player almost on top of it. The numbers it was handed were in the wrong order; it now starts at vanilla's sixteen and follows `[server.spawner]`. A spawner already built keeps the numbers it saved, so only newly placed ones change.
- Mirkwood and Mirkwood Spooky close in at half the view again, the way both woods did before 5.0. Their fog was set by hand while the mod still drew its own overworld fog; 26.1 reads it from the biome, and the distance it reads is measured against a 1024-block default rather than the view, so a wood that had no distances of its own showed nothing until the far horizon. Both now end their terrain, sky and cloud fog at 96 blocks, in the grey-green their floor casts. The five woods and downs that reported the same in testing - Fangorn, the Old Forest, the Barrow Downs, Forochel and its slopes, and the Misty Mountains - keep the fog they were given then.
- The Blue Mountains' mountain stone counts as ground for the loose rock the way vanilla stone and Mordor's dark stone already did. It is what the dwarven range is built of below its grass and what its caves are cut into, so a pile or a lump of rubble can now sit on it instead of needing soil under every candidate column; the cobbled form counts too.
- Eight of the mod's flowers, the ammolite ore and the rat and gecko were attached only to `minecraft:plains`, `minecraft:dark_forest` and `minecraft:badlands`. The mod's region shares the overworld's climate space with those biomes rather than deleting them, so the patches of them that remain kept the flowers and the animals - but not one of the mod's own biomes grew any of the eight, and a player exploring them could walk the whole of Firien or Barrow Downs without meeting a single one of their flowers. Aeglos now also grows in the Blue Mountains, alfirin, athelas and mallos in Firien, lissuin and niphredil in Lórinand, simbelmyne over the Barrow Downs, and the lily of the valley in the Shire and the Old Forest; ammolite ore is in Mirkwood as well as its dark forest; rats live in the Shire, the Gladden fields, the Old Forest and the Barrow Downs, and geckos in Haradwaith, Nurn and Dagorlad. Every vanilla biome they already named keeps what it had. The elanor, mirkwood-flower, swamp-milkweed and brambles flowers and the squirrel and frog spawns are untouched: the mod's biomes already place them directly, and their modifier entries stay as they were for the vanilla patches.

## [5.0.606] - 2026-10-25

### Added

- The Misty Mountains rise into a range worth the name. Vanilla's height noise still shapes the world, so the summits are built on top of it: one tall stone cone every dozen chunks or so, a six- to ten-block foot climbing thirty to sixty blocks, seated into the slope it stands on and capped in snow over its upper half. They go in after the trees, and because the terrain underneath is untouched the valleys between the peaks keep their shape.
- A key held in either hand shows a camouflage block as itself when the key carries that block's code, so a keystone can be found among the stone it imitates; the dev tool does the same for every camo block while it is held. The sections holding a camo block are rebuilt when the hands change.
- A coin pouch with coins in it banks them when you right-click a piggy bank, and whatever the bank has no room for stays in the pouch. The bank's own menu still opens for an empty pouch or for anything else in hand.
- The camo keystone keeps a pulse length beside its delay, saved with the block, and its screen shows both numbers - the delay first, then the pulse - so each redstone mode has the duration it uses.
- The backpack can carry a magnet. Three tiers - iron, gold and mithril - pull loose items from 16, 32 and 64 blocks away, filling the backpack first and your inventory second, with a fourth toggle in the backpack's button row and `M` as a keybind that also moves that button while the screen is open. The pull works whether the bag is worn, held or standing on the ground, and its screen does not have to be open for it. The best installed tier sets the reach, walls and stone do not stop it, and a drop that is still counting down is left where it lies. Each tier is crafted from the one before it: a U of iron, gold or mithril around the upgrade base or the previous magnet, with ammolite between the prongs. The setting saves with the bag, and a bag saved before the magnet existed loads with the magnet switched on.
- A placed backpack is picked up by sneaking and using it, whatever is in hand. It folds back into its item with everything it held - storage, upgrades, bucket slots, tank, magnet tier, every toggle - and the sleeping bag or campfire it had put out is taken back in first. The item goes into your inventory when there is room and drops at your feet when there is not, and sneaking outranks filling the tank, so a full bucket cannot drink from a pack you meant to lift. Using it without sneaking still opens it.

### Changed

- The mod runs on Minecraft 26.1.2. Building now needs Java 25 and Gradle 9.2.1, against NeoForge 26.1.2.116, GeckoLib 5.5.2, TerraBlender 26.1.2-26.1.2.0.3, Curios 15.0.0 and JEI 29.47.0.111, and `./gradlew compileJava` and `./gradlew runData` both finish clean.
- The rendering library is GeckoLib 5. Its package is `com.geckolib`, and every model and animation now lives under `assets/tolkienmobs/geckolib`, which is where 5 loads them from. Every mob - the bosses, monsters, ambient and passive animals and the trading villagers - plus the three projectiles, the five block entities, the Arda staff and the three NPC profession layers are on 5's render-state API. Every held-item layer builds its props from the state, every controller is built the 5 way, and the dragon's `forceAnimationReset` calls are gone because 5 has no such call. The NPC layers are still attached to nothing, as in the source they came from, so nothing visible changes there.
- The rest of the tree is ported with it: the remaining entity families, the boats, items, blocks, block models and the whole menu, screen and button set, the particles and their descriptions, the entity and block-entity renderers, loot tables, loot modifiers and both recipe families, and the biome, carver, structure, tree and feature data. Saved data uses 26.1's value pipeline with the same keys, so a world loads as it did. Behaviour a player would notice is unchanged: a spider still snatches one item on the same cooldown, a warg still sometimes carries an orc, the walker's pumpkin still never drops, a chest boat still carries a chest, a broken barrel or chest still spills its contents, a boss still shows its bar the moment it is seen and the village professions make the same offers from the same trade sets, a held backpack still locks its slot, and a refused sleep still says why above the hotbar.
- A few things do behave differently, all deliberate. The overworld no longer raises its clouds to y=128, so clouds are vanilla's again. A player paralysed by fear has movement keys cleared rather than movement itself, because 26.1 keeps a player's movement private. An angry mob saved before this update returns calm, since anger is now stored in the shape 26.1 expects. The goat's head-up warning lasts its 40-tick scream rather than as long as its attack goal held the pose. A lockable chest saved by an earlier build starts at the default "Enter Code" and needs its code set once, because 26.1 cannot write a bare string as an entity attachment the way the old save did; after that it saves and reloads as before.
- A world's saved milestone list now lives in `data/tolkienmobs/milestone_data.dat`, which is where 26.1 names saved data. A world carried over from 4.4.83 therefore starts with an empty milestone list; the old file is left on disk, untouched.
- The mod's storage speaks 26.1's transfer API everywhere, so the compile's removal and deprecation notices are back on and only JEI's own recipe API still warns. Every menu slot, block entity, tank and item-held container was on the API 26.1 marked for removal; nothing a player sees changes - the backpack's storage, upgrade and bucket slots, the piggy bank, fireplace, trinket table, camouflage fluid block, coin pouch, key ring and key item screens open, quick-move and filter as they did.
- The generated data is regenerated for 26.1. Each biome's fog, sky, water fog, ambient sound, music and particles are environment attributes now, with its grass, foliage and water colours beside them; the forest flowers place the same number over the same spread; a recipe result no longer repeats a count of one; a trim material no longer names an ingredient; a cooking recipe states which recipe-book tab it belongs to; and the tag that lets the mod's jungle bushes take root is called `supports_vegetation`. Every value a player can see - fog colours, flower density, recipes, trims, village templates and the five loot modifiers - is unchanged.
- Mordor's stone spikes are twice as tall. The lumps stood seven to ten blocks high, which read as boulders beside the biome's rock piles; they run fourteen to twenty now, and the rare needle - one in sixty - forty to eighty where it used to be twenty to forty. A cone's base is built from a fraction of its height, so that fraction was halved and the spikes keep the slender profile they had.
- Data generation refuses to write a village house whose doorway marker is not on its own template's front edge. Such a marker can never join a house to a street - the game drops the whole house - which used to surface only as a town centre standing alone in a world. `./gradlew runData` now stops with the house's style, the marker's position, the template's size and the column it should have used.
- A village house pool no longer offers vanilla's second small plains farm, which 26.1 does not ship; the plains-style villages now offer the house and the farm that exists, and the desert styles around Nurn keep both of theirs. A village's site check reads the terrain column directly and asks its highest block whether it holds a fluid, instead of comparing two heightmaps - the same rule, stated plainly: a lake still refuses a site, open ground, ice and snow still allow one.
- Two things are worth a look in a world: the mod's decay blocks now join `supports_vegetation`, the tag a small mushroom reads, since 26.1 dropped the `mushroom_grow_block` tag they used to share, and the two molten metals no longer declare a translucent render layer of their own because 26.1 picks it from the fluid's textures.
- The mod's version restarts at 5.0. Its build number stands at 606, the number of commits behind this release, so the jar reports 5.0.606; from here the build-number task rolls it to 5.0.607, 5.0.608 and so on, and a 5.0 build can never be mistaken for a 4.3 release.

### Fixed

- A fireplace no longer closes the world. It took a piece of fuel by writing back one item fewer than it had read, and an empty slot reads as a count of zero, so the first tick of a fireplace with an empty fuel slot asked for minus one item and the game stopped with `Expected value to be non-negative: -1`. The slot is written only when it holds something that burns, and one piece of fuel empties it cleanly.
- The backpack opens without closing the world, and its block draws its own texture again. 26.1's menu slot casts its handler to a writable one when a screen fills its slots and the mod's list only spoke the read-and-insert form, so opening a backpack threw a `ClassCastException` before a slot was drawn. The block also named its texture in the item atlas, which a block model cannot sample, so the placed pack was invisible; the texture sits in the block folder now, at the same 32x32 size the model was drawn against.
- A dedicated server starts again. Two classes a server loads named client-only types outright - the leaf pile asked the client's block-colour list for a tint, and the falling-leaf packet did the same while drawing - so block registration and then packet registration stopped with `NoClassDefFoundError` and the run ended in "Mod loading has failed". The tint sits behind a hook the client fills in during its own setup and the packet hands its drawing to a client-only class, so a server generates and saves a world with no error; leaves still fall in the colour of the oak overhead.
- A fireplace no longer loses the food it could not hand back. Its "output full" test read the first slot of the *input* handler, the result was checked loosely against a hard-coded 64, and the write could still come up empty after the ingredients were taken. The check and the write come from one plan now: the output slots are simulated first, two results share a slot only when both fit there, nothing leaves the inputs until a result has a slot waiting, and a named dish keeps its name.
- The trinket table crafts trinkets again. 26.1 asks a slot whether it accepts an item before reporting how much room it has, and the table's output slot answered no to everything, so the table judged its own output full and never finished a recipe - which is why repairing still worked. A trinket, a potion and an ammolite come out of it again, the potion leaves an empty bottle behind, and the trinket keeps its effect.
- The backpack's tank keeps its contents between empty and full whatever it is handed: a negative amount is clipped rather than passed to a handler that refuses one and would have thrown inside the block entity's tick, an amount over capacity is clamped, and emptying goes through the same clamp.
- A fluid tank's level rises from the floor of the gauge. Each sixteen-pixel row was placed at the top of its own tile, so a part-filled row floated up to fifteen pixels above the tank's base and left a gap under it; rows are placed by their bottom edge now, so the level touches the floor and the rows meet without a seam.
- The key ring stores one key per slot, because it merged keys against a fixed 64 while every key stacks to one and its slot holds one. It also picks up keys lying in your inventory: it skipped every key carrying components, which is every key with a code, and now stows any key of the mod's key tag, leaving the key in hand alone.
- All 55 spawn eggs show a sprite again instead of the missing-model checkerboard. 26.1 dropped the two-layer egg template and its pair of tints, so every egg named a model the game no longer has. The images are painted during data generation from art the mod already has: the egg shape, filled with that mob's own 16x16 entity icon pulled towards the colour the egg registered with, a rim of that colour, and shading worked out from the egg itself. The eggs still fall into the groups the old tinted ones did - the horde, the ring-bearers, the beasts, the little things, the free peoples and the bosses - and the two horses, which have no icon, get a face drawn from plain shapes. A new egg still needs only its one line in the entity registration.
- Every item the mod registers on its own has its name back in English and Chinese. A registry-wide check found 46 showing a raw translation key: the backpack, the camouflage spawner, the sixteen sleeping bags and their lore line, the seven torches, the seven sign and seven hanging sign items and the seven trinket bases, plus the seven wall torches and the three armour trim materials. Each is registered as an item rather than as a block item, so the block's name never covered it.
- The Chinese names are tidier too. The Mirkwood torch was 阴郁木火把 - a second translation of "mirk" - while that wood's planks, signs and boat are all 黑森林木, so the torch, its wall block and its item are 黑森林木火把 now. The bark-on-all-sides block is 瑁珑木 rather than 瑁珑木木头, its stripped form 去皮瑁珑木, and a barrel 瑁珑木桶 rather than 瑁珑木木桶, for all seven woods; planks keep 木板, which is how vanilla writes 橡木木板.
- The placard's item names itself and shows its gold lore line, in English and Chinese. The Ring of Aglaral sits in the quest tab now, beside the keys it belongs with.
- The seven trinkets wear in Curios and its window lists their slots. Curios grants no slot on its own and the mod granted only the backpack's, so the trinkets had nowhere to go while the backpack had a home. Players carry head, necklace, body, hands, belt, charm and ring slots now, and the amulet is tagged `curios:necklace` - the slot Curios defines - instead of one no version has.
- The mod's fog is back in the biomes that asked for it. The woods, the barrow downs and Forochel looked as clear as a plain, because the ported handler halved a far plane that 26.1 defaults to 1024 blocks. Fog now comes from each biome's own fog start and end distances: the Misty Mountains at 38 blocks in a pale mist, Fangorn at 80 in the grey of old woodsmoke, the Old Forest and both Mirkwoods at 96, the barrow downs at 64, Forochel and its slopes at 96 in snow-blind white, with the sky and cloud ends following the same distance so a mist closes overhead and not just along the ground.
- The frozen villages of Forochel build their houses. The frozen house is six blocks deeper than the human house whose numbers it borrowed, so its doorway marker fell well inside its own walls, and a marker that deep can never find a street without growing into the house - the house was chosen and then dropped. It carries its marker one block past its own porch now.
- The four lantern families and the two watcher statues bake again. 26.1 decides a face's translucency from the piece of texture its UVs cover and refuses the model when that rectangle falls outside the image, which the lanterns' glass box and the watchers' wing planks did; both are inside 0..16 now. The lanterns' top and bottom faces also point at the glass of their four sides instead of a texture variable the model never declared, so looking down on one no longer shows the checkerboard.
- The fallen-leaf piles generate in all six woods again, and lie on the ground they belong to. The structure-avoidance modifier answered with no positions at all, and a pile then took its height from a heightmap that counts leaves - the canopy - so the soil test beneath it failed. The modifier passes every position through, since the landmark system it consulted is gone, and a pile finds its own floor by walking down to the first soil or water.
- Mordor's rubble, rock piles, stone spikes and dead trees generate again, and a rock pile can be placed by hand there. All of them asked for grass, which dark stone is not; they accept bare rock as ground now, brambles take root in it, and the dead trees skip the sapling check that only lets a tree stand where a sapling would survive.
- Mordor's stone spikes stand up out of the ground instead of hiding inside it, stand on all of the biome's rock, and rise out of its lakes. The spike only wrote into cells that already held rock or soil, which is every cell above the surface, so an attempt spent itself replacing nothing. It now finds the ground under plants and snow and builds upward through the air, fills the water or lava it passes so its foot plugs into the rock under a lake, and counts cobbled and smooth dark stone as ground alongside plain dark stone.
- The Misty Mountains no longer shed dwarven maple leaves; snow is the only thing that falls there.
- Trees in Fangorn and the Old Forest no longer stand on a floating disc of podzol. The mod brings its own ground decorator, which scans down and replaces only the highest solid block of a column when that block is soil, stepping over grass, a flower or a snow layer and stopping at a trunk log or a leaf.
- The camo keystone offers all three of its redstone modes again, with the numbers that go with them. A left click on the mode button landed on 26.1's empty click hook, so the button never stepped and the lever, stone button and pressure plate looked like missing options. A left click steps it on now, every toggle in the mod's screens steps with it, and the screen opens on the mode the keystone is actually set to.
- A camouflage spawner starts the way a vanilla spawner behaves: a player within sixteen blocks wakes it, it respects the mob's own spawn requirements, it does not wait for redstone and it shows its flames. Four of its toggles read backwards, each showing the opposite of the setting it changed, and the redstone and spawn-as-group choices were never written to the save. A spawner read from an older save now falls back to those vanilla numbers instead of zero ranges and zero delays.
- A backpack's magnet gathers without its screen being open. The scan only looked for a bag the player wore or carried, so a placed bag had nothing to fill, and a worn bag without a magnet could be chosen ahead of the one in hand and stop the scan at its zero reach. It reads both hands as well as the inventory now, steps past a bag with no magnet and falls back to the nearest placed bag whose reach covers the player, and only loaded chunks are searched. The bag the key and the button flip is the same one the scan fills.
- A carried backpack shows its sleeping bag and campfire buttons again, and pressing one puts the bed or fire in front of the player instead of leaving the upgrades looking missing. The screen's upgrade window opens closed, and the magnet's button sits in the toggle row between the campfire and upgrade buttons instead of hanging over the panel's edge.
- Breaking a placed backpack in creative gives the backpack back, contents and all. The drop was built from a stack of the block, which has no block item, so it came out empty and evaporated with whatever the pack held.
- A field that was drawn twice over itself is drawn once now: the milestone's name field, the key and keystone code fields and the lockable chest's code field.
- A camo spawner's spawn-egg tooltip no longer throws on a configured spawner: 26.1 stores a spawner's entity data as a different kind of component, and the old read had turned into an unchecked cast that compiled but would have failed the moment it ran.

## [4.4.83-Final] - 10.22.2026

### Added

- Mithril, morguliron and ammolite trims show on the armour icon. Those three are the mod's own materials, so vanilla's sprite atlas has no cut for them and a piece trimmed with one fell back to its plain icon; the datagen now bakes a trim sprite for each material and armour slot by putting vanilla's trim shape through the material's own colour palette - the same swap the game applies to vanilla trims - and each piece selects it by material just like the vanilla ones.
- The fell beast glides. While it was on the wing its animation file's `glide` clip was never asked for, so a beast soaring with nothing to chase beat its wings exactly like one in a fight; it now holds `glide` whenever it is flying without climbing, keeps `fly` for the climb, and still stoops with `dive` when it drops on prey.
- The fell beast really flies. Its own AI was holding it at exactly the height its navigator asked for, so it could never sink or climb on its own; it now beats its wings to climb at about two blocks a second, coasts between beats, and sinks slowly while it glides, holding its path with a wingbeat roughly every second. When its path leads well below it, it commits to a fast stoop instead of easing down. A player riding the beast still steers its height with their pitch.
- The fell beast's wingbeats are heard. Its flap sound was only ever played by a wingbeat routine nothing called, so a beast flying under its own AI moved in silence; each downstroke of its flight now sounds, quieter than the one that lifts a rider and at most about once a second, so a long climb does not turn into a siren.
- A stooping fell beast is heard coming. Its dive is silent, so a beast dropping out of the sky gave no sign of it; the air over its wings now plays while it stoops, and a rider's flight wind is driven by height as well as ground speed, so it no longer fades exactly when the beast drops straight down instead of flying flat.
- The dive wind has a sound of its own, captioned "The beast dives." It borrowed the elytra's wind, so with subtitles on a stooping fell beast read as an elytra whooshing; the beast now carries its own wind event, built from the mod's gust samples.
- The dive wind loops for the length of the stoop. Its gusts are three seconds long but were replayed on a one-second timer, so every one was cut off mid-gust and the seam was audible; the wind is now a purpose-made 7.25 second loop, assembled from the loud middles of those gusts and joined end to start inside a crossfade, recorded at the depth the dive plays at, that follows the beast and ends when the dive does.
- The fell beast's wings are heard while a player rides it. A rider's flight is simulated on their own client, so the beast's AI wingbeat never ran for it and the ride was silent; the wings now beat at the same configured volume and rate while it is being flown, and the beat is played from the server so players nearby hear it too, not just the rider.

### Changed

- The mithril, morguliron and ammolite trim palettes are proper ramps. Each is eight steps from a highlight down to a shadow, and the old strips jumped between extremes - mithril ran bright yellow, near-black, then three whites - so a trim built from them read as speckle rather than metal. The colours now also keep clear of the armour they sit on: mithril's trim is a deeper blue than its silver plate, morguliron's a cold green against its warm black, and ammolite's a blue that separates from its green. The trim icons and sprites are regenerated from the new palettes; the same strips are what the trim colours elsewhere read from.
- The fell beast's wingbeat can be tuned in the mod config. Its volume and the shortest gap between two beats were fixed in code; they are now `fellbeastWingbeatVolume` (default 2.0) and `fellbeastWingbeatInterval` in ticks (default 15, and 20 ticks is one second), read on every beat, so the loudness and cadence can be adjusted by ear without a new build.
- The dive wind's volume can be tuned in the mod config. It was fixed at 1.6 in code; it is now `fellbeastDiveWindVolume` (default 1.6), read while the wind plays, so unlike the wingbeat it can be changed without a new dive. Its pitch is `fellbeastDiveWindPitch` (default 1.0, since the loop is recorded at the depth the dive should sound) alongside it, so the wind can be made deeper or sharper by ear. The wind is heard on each client, so both are the listener's own settings rather than the server's.
- The Dwarven Mining enchant is Dwarven Excavation now, and its description says what it does: it breaks a slice across the face you struck, 3x3 at level I up to 9x9 at level IV, and sneaking breaks only the block you hit. It used to promise a vein of matching blocks, which the effect has not done since it was rewritten. Both languages carry the new wording; the enchantment's id is unchanged, so existing gear keeps it.
- The Dwarven Mining effect's field is `haste` rather than `range`. It was never the area - the area follows the enchantment's level - it is the amplifier on the mining speed buff the effect also hands out, so a datapack that set `"range"` on the effect wants `"haste"` now (nothing the mod ships sets either).
- A rider's dive is quiet. The wings beat for as long as a player flew the beast, even with its nose down; the beat now stops once the beast is dropping more than a block a second and steeper than `fellbeastDiveAngle` (default 45 degrees below horizontal), and comes back as soon as it levels out.
- `homeRadius` can be set as low as 8. Its floor was equal to its default, so the only direction the setting allowed was up.
- The config descriptions for `disableFakePlayer` and `paymentType` say what the two options actually do. One claimed a default it did not have, and the fake-player option was written as though turning it off disabled the effect rather than the fake players.

### Removed

- The `number_of_blocks` option is gone from the mod config. It set a range for the Dwarven Mining enchant, which takes its area from the enchantment's own level now - 3x3 at level 1 through 9x9 at level 4 - so nothing had read it since that change.

### Fixed

- Trimmed armour wears its trim on the icon again. 1.21.4 chooses the trimmed sprite with a `select` on the piece's trim material; the datagen still wrote the old `trim_type` predicates into the model's `overrides` array, which the game no longer reads, so every trimmed piece kept its plain icon however it was trimmed. Each of the twelve pieces now has a select definition over the eleven materials 1.21.4 ships, and resin - new in this version and missing from the old list - has its models too.
- Molten mithril and molten morgul iron are named when placed. The liquid blocks are registered as `mithril` and `morguliron`, while the language file only named `mithril_fluid` and `morguliron_fluid`, so a placed pool showed a raw key.
- Spawn eggs are drawn as eggs again, each in its creature's own two colours. 1.21.4 paints a spawn egg with vanilla's two-layer egg model, using the colours an item model definition tints it with, and the datagen was still writing the older model that named that template with no colours at all - so every egg in the game came out a flat untinted shape. Each of the 55 eggs now gets a definition carrying the two colours it was registered with, and the 55 model files that only pointed at the template are gone.
- Wall signs and hanging wall signs are named again. The blocks are registered as `sign_wall_<wood>` and `hanging_sign_wall_<wood>`, but the language file only named `wall_sign_<wood>` and `wall_hanging_sign_<wood>`, so a sign placed against a wall - and the title of the hanging sign screen you open on it - showed its raw key instead of its name; all seven woods carry the names the blocks actually ask for now.
- Boats and chest boats are named. Their items are `boat_<wood>` and `chest_boat_<wood>`, while the language file called them `<wood>_boat` and `<wood>_chest_boat`, so every boat in an inventory showed a raw key; the item names and both boat entity names follow the real ids now.
- Potted flowers are named. The blocks are `potted_flower_<flower>` and the language file used `potted_<flower>`, so a flower in a pot showed a raw key; the language helper now takes the potted block itself rather than rebuilding its name from the flower, which is what let the two drift apart.
- The second decay bloom block, pipeweed crops, the Arda portal, the Morgul crystal, the six projectiles, the simple trap and the two molten fluid types all have names now; none of them was in the language file at all.
- The camo spawner says which mob it selected or replaced. Both of those chat messages were missing from the language file, so the spawner printed raw keys at the player.
- The Chinese translation covers everything the English one does. The orc and southron villagers with all their trades, four biomes (Nurn, Forochel, the Forochel slopes and the Blue Mountains) and two spawn eggs had been left out, and every name above was missing from it too; the two files now hold the same 1719 keys.
- The fell beast's fireball and the boulder no longer crash the game when they are drawn in flight. Each kept the angles it sits at once it lands in a field that started empty, and its renderer read that field on every frame, grounded or not, so the first frame either one was on screen threw a NullPointerException and took the client down; the field starts at zero now.
- The milestone's dimension cost is a setting of its own again. It and the minimum tick speed were both saved under `minimum_tick_speed`, so the two could not be set apart and the dimension button's lowest allowed cost was whatever the tick speed was set to; the option is `dimension_cost` now.
- Milestones charge levels when the payment method is experience. That branch checked for and spent the configured payment item instead, so a jump priced in levels was refused for lack of coins, or paid with coins while the levels went untouched; it checks and spends levels now.
- The milestone list shows every milestone. It skipped whichever milestone came first in the list, its page count was one short whenever the total was just past a multiple of seven, and scrolling moved the page number without redrawing the list.
- The milestone's experience cost readout keeps its number. Clicking it clamped the cost shown to 1, because its bounds had been copied from the fixed payment icon beside it.
- Hallowed Earth hurts the undead again. Its `disableFakePlayer` option was read as a plain off switch that skipped the damage for every entity, not as a guard against fake players, so with the option on by default the block did nothing at all; it now spares fake players and hurts everything else it was always meant to.
- A milestone refuses a jump it cannot charge for. When the configured payment item no longer resolves to an item, the item branch skipped the charge and the teleport was free; the jump is refused with the usual payment message now.
- The fell beast roars, stoops and swings as it moves. Its animation file carries `roar`, `dive` and three `walk_attack` swings that no controller ever asked for, so the beast opened fights in silence and swung the same standing animation at a run; a bout of aggression now opens with `roar`, losing height on the wing plays `dive`, and a swing made on the move plays one of the `walk_attack` clips.
- The elemental golem's tornado, the web trap and the thrown cobweb are drawn again. None of the three had a client renderer registered, so the tornado and the cobweb ball were invisible and a web that hit the ground left nothing behind; each now registers its GeckoLib renderer, the cobweb draws from the model and texture that were already in the jar, and its idle animation is the one the animation file actually contains.
- The mallorn hanging wall sign is listed with the hanging signs. Its registration named the plain mallorn wall sign where the hanging wall sign was meant, so the game's hanging sign block entity claimed a block that is not one and never listed the one that is; the mod's own sign block entity already covered every hanging sign, so a placed sign was never affected.
- The tornado's funnel animates while it is on the field. Its animation clock was pinned to zero, so the rings never turned and the pillar stayed hidden below the spawn point; the entity reports its own age now, so the funnel rises and its rings spin through the strike.
- The fell beast's attacks use the animations its file has. Its ranged attack asked for `ranged` and its swimming movement and a leftover swim controller asked for `swim` and `swim_fast`, none of which the beast's file defines, so those controllers were left with nothing to play; the ranged attack now plays the beast's `fire`, the beast keeps beating its wings with `fly` while it swims, and the redundant swim controller is gone.
- The mithril and morguliron golems' ranged attack plays a swing. Both asked their animation file for `ranged`, which it does not contain, so the controller reset onto nothing and the golem stood still through the shot; they play their attack animation for it now.
- Gollum's swing no longer blanks his pose. His attack controller asked for an `attack` animation his file does not have, which reset the controller onto nothing and dropped him to his rest pose; he holds his idle pose through the swing instead.
- Villagers sit down properly. Their controller asked for a `sitting` animation that only the dwarf file carried, so an elf, hobbit, human, orc or southron villager who sat lost its pose and snapped back to idle; all six merchant animation files define it now.
- The swamp hag's attack and heal animations play. Her combat controller returned `STOP` in the same tick it set either animation, which makes GeckoLib throw the animation away, so she cast without moving; each branch keeps the controller running until the cast is done.
- Shadowfax's jump plays. The jump branch set the animation and then fell through to the controller's `STOP`, dropping it on the tick it started; the jump now runs for its length, after which running and walking take over again.
- The rock golem and the Istari stop holding a finished attack. Their combat controllers returned `CONTINUE` on every tick, so after a punch, rage, attack or heal ended the controller stayed live on its last frame and the mob kept that pose through idle and walk; both now hand the controller back once the animation is done, and the Istari's attack and heal play once instead of looping.
- The uruk-hai and mordor orcs animate their shots. Their ranged branch waited on the weapon flag, which is set on the server and never synced, so the client could not see it; it now reads the synced shooting flag the bow goal publishes.
- The tree ent and the swamp hag animate their throws. Their attack controllers keyed the throw on the weapon flag, which is an ordinary field set only on the server (and only for a bow in the main hand), so the client never saw it and the throw animation never played; the ranged goals now publish a synced shooting flag that both controllers read.
- Mobs that fight at range are marked as engaged while they throw. The game's throwing goal never touched the mod's aggressive flag, which only the melee and bow goals set, so a snowball, boulder or potion thrower fought with the flag stuck off and the attack animations waiting on it never played; the throwing goals now set it while they run and clear it when they stop.
- The fell beast breathes fire again, in the three parts its file was authored in. Its fire animation waited on the weapon flag, which is set only on the server and only for a bow in hand, neither of which the beast has, so its attack controller could not see it; the breath now runs off the beast's synced breathing flag and plays `fireStart` to wind up, `fireLoop` for as long as the breath lasts, and `fireEnd` once as it stops.
- The uruk-hai and mordor orcs shoot with the uruk bow they carry. The weapon check only looked for a vanilla bow in the main hand, and theirs is a mod bow in the off hand, so no archery goal was ever installed and the bow was decoration; a bow in either hand now chooses the bow goal. They fight as archers from then on, as any bow-carrying monster does.
- Hostile mobs animate their walk while they chase. Their walk controllers also required the mob not to be aggressive, and that flag stays on for as long as the attack goal runs, so a creature running down a target slid along in a static pose; walk now depends only on movement, and the fell beast keeps the faster prowl its file provides for a beast on the hunt.
- Hostile mobs keep breathing while they fight. Every hostile idle controller also required the mob not to be aggressive, so a creature that held a target stood frozen in its rest pose the moment it paused between swings; the idle animation now plays whenever it stands still, aggressive or not.
- The thrown cobweb breathes as it flies. Its animation controller stopped itself right after asking for the idle animation, which makes GeckoLib drop the animation and draw the web on its rest pose; the controller keeps running now, so the web pulses the way its file describes.

## [4.3.81 - Final] - 2026-10-21

### Added

- Added the Blue Mountains, a temperate mountain biome shaped like the Iron Hills but with the mod's mountain stone where vanilla stone would be, and with teal grass and water (#59B6BD).
- Mountain stone counts as stone for ore generation, so the Blue Mountains' underground carries the same ores as any other biome's.
- The Blue Mountains grow dwarven maples, and dwarven villages now start there as well as in the Iron Hills.
- Mountain stone also forms andesite-like veins through the Blue Mountains' stone, so the rock still shows in worlds that never apply the biome's surface rules, such as a single-biome world.
- Human NPCs spawned in a cold climate, such as Forochel, wear the winter clothing textures; humans spawned anywhere warmer keep their normal clothes. The outfit is chosen once, from the biome they spawned in, so it does not change if they later travel.
- Camouflage Smokers can be dyed: right-clicking with a dye tints the smoke a third of the way to that colour, another click with the same dye takes it to two thirds, and a third to the full colour, after which the next click returns it to plain smoke. A different dye starts again at a third. The dye is not used up.

### Changed

- The generated resources are written in the forms the game now uses, so a data run no longer rewrites the same eight hundred files: recipe ingredients carry the short `"item"` or `"#tag"` entry, biomes list their carvers flat and their music as weighted entries with a music volume, trim materials drop the retired item model index, village templates are saved at the current data version with the current block entity and attribute names, and leaf pile models state the UVs the game fills in anyway.
- Data generation runs as the client data run, the run NeoForge 21.4 provides for writing both assets and data; its classpath carries the client library the generator needs to boot. The generator now produces the 800 item model definitions, the fourteen boats included, instead of them being mirrored by hand.
- Blockstates and item models are written by the mod's own generator, since the NeoForge model generators this datagen relied on no longer exist; the generated JSON keeps the same shape.
- Armour sets are now described the way the game expects: each of Mithril, Morguliron and Ammolite has an equipment asset that names its worn texture, so the sets render on players and horses from one definition and the full-set effects are still recognised.
- Spawn eggs are rebuilt on the vanilla item with their two colours moved into the item model, and every mob still registers under its own entity id (the chest boats no longer share the plain boat's id).
- Tools and weapons are built from the game's tool materials again, so every mithril, morguliron and ammolite tool keeps its durability, enchantability, mining speed and damage, and is repaired by the ingot or gem it is made of; the shears keep their three durabilities and repair the same way. The elven and uruk bows stay enchantable even though they never wear out, and the trinkets still take the name of the potion inside them.
- Food and drink effects are carried by the consumable component, so drinks still tip the bottle back and foods still keep their bonuses.
- Tag, sound, particle and village-template datagen no longer asks for the file helper the modding API dropped.
- Recipe datagen builds its recipes through the game's recipe-provider instance, so crafted, smelted and stonecut items keep the recipe ids and unlock conditions they had before.
- Fireplace and trinket table recipes are built on the game's current recipe definitions: each ingredient now carries its own nested entry with its count, so the fireplace still smelts bread from three wheat, the trinket table still shows its fuel in JEI, and both still unlock on the same conditions.
- Potions are registered with the name the game now requires on them, so potions, splash potions and lingering potions keep the names they are shown with.
- Blocks and items are registered with the ids the game now reads while it builds them, so every block keeps its loot table and its name, block items show the name of their block, and items keep their inventory model.
- The clover and moss patches are drawn through the game's own model loader again, and camouflage blocks still take on the look of the block next to them.
- Block entity types are built directly, and the wood wall torches name the floor torch's loot table themselves, so torches still drop the way they did before.
- Creature renderers and models now use the per-frame render state the game switched to, so mobs, boats and thrown projectiles draw the same way they did before.
- Item colours and model properties are registered the way the game now reads them: a trinket's gem layer still takes the colour of the potion inside it, a bow still shows its three drawn frames at the same draw points, and a coin pouch or key ring still swaps its icon as it fills. Item model definitions now sit in `assets/tolkienmobs/items` and name those properties, replacing the old in-model predicates.
- Item model definitions are generated for every item: the model generator now writes `assets/tolkienmobs/items/<item>.json` for each item the mod registers, pointing at the item's model by default, and the bows, pouch, key ring and trinkets declare their dispatch and gem tint in the generator rather than in hand-written files.
- Loot is built on the game's current loot API: block drops keep their shears and silk-touch behaviour, the extra coin a loot modifier adds to a chest or mob drop is still rolled by its chance, and a trinket dropped from a table still picks a helpful potion when the loot is rolled.
- Human NPC winter clothing now follows the `tolkienmobs:climate/cold` biome tag instead of a hard-coded temperature, so a datapack can decide which biomes dress humans for the cold. Iron Hills, Barrow Downs and Hithaeglir count as cold alongside Forochel.
- Mob effects tick on the server now that the game only ticks them there, so the burning, drowning, fear, terror, nimble, sleep, elven life, flying and Eru effects still apply their damage, extra effects, particles and step height, the blacksmith and corrosion effects still repair and wear the player's gear, the lightning effect still calls a bolt down on the afflicted, and paralysing fear still stops the player's movement keys.
- Mobs now deal and take damage through the game's server-side damage hook, so every boss reaction still fires: Shelob still calls her spiders, the golems still apply their elemental effects, and the fell beast still puts a shield on cooldown. Mobs the bosses summon arrive as reinforcements or breeding results rather than anonymous spawns.
- Block entities work on the game's current APIs: a camouflage spawner still reads back the mobs stored in it and shows the chosen one turning above the block, a fireplace still accepts only fuel and takes its burn times from the game's fuel values, trinket tables and fireplaces still find their recipes, the lockable chests still name the key you open them with, and a milestone still charges the item configured as payment.
- Every container screen draws through the game's current widget pipeline: the chests, fireplace, trinket table, backpack, milestone, well, pouch and key screens keep their panels, progress arrows and fluid gauges, the camouflage spawner still turns its chosen mob on the mouse, and the greyed-out buttons still dim when they are switched off.
- Blocks answer the game's current neighbour-change hook, so waterlogged blocks still refill when a neighbour changes, wall torches, lanterns, wall mushrooms, decay blooms, patches and the Arda portal still break when their support is removed, dirt paths still revert to cobble, and a sleeping bag's two halves still link up and still bounce you when you land on them.
- The lightning bug and locust displays draw their parts through the renderer, so the bug body still draws cutout and the lightning bug's glow still overlays it as a separate translucent pass.

### Fixed

- The client gets past its loading screen again. The morgul crystal's renderer asked the vanilla end crystal model for glass, cube and base parts, which this version renamed and moved, so building the renderer threw and the game sat on its splash forever; the crystal now bakes its own copy of the layer it draws.
- Mobs that follow food no longer crash the game the moment one of them ticks. The game added a tempt range attribute that their follow goal reads, and the squirrel, rat, frog, gecko, thrush, crebain, goat, the two horses, the Istari and every heard animal now declare it.
- The molten mithril and morguliron blocks no longer name themselves as their own model parent. The model loader reads that as a loading loop and reported it sixty-four times while the game started; both models now carry nothing but the break particle, the way vanilla's water and lava do, since the fluid renderer draws the liquid itself.
- The Arda Staff draws its GeckoLib model again. Its item model still named the retired `builtin/entity` parent, so the staff could not be drawn at all; it now uses the game's special item model, which hands the staff to the GeckoLib renderer that was already registered for it, with the display transforms and break particles kept on the model underneath.
- The lightning bug and locust blocks no longer ask for a model parent the game dropped. Their block models named `builtin/entity`, which 1.21.4 no longer has, so the model could not be loaded and the block fell back to the missing model; the models are now bare like vanilla's chest - they draw nothing of their own and keep the block's break particles - while the block entity still draws the bug.
- Wooden ladders hang on the vanilla ladder model again. Their generated models named the model's parent without the `block/` folder, which no longer exists in the game's files, so every one of the seven ladders would have come out as the missing model.
- Data generation runs again. The port's last compile errors are gone: enchanted books are built through the game's enchantment helper, tag lookups, registry lookups, carver steps, the solid-render check, the fluid fog parameters and the JEI subtype interpreter all use the shapes the game now has, so the model, loot, tag and recipe providers can be regenerated from the build.
- Entity loot generation completes. The eight NPCs (humans, dwarves, elves, hobbits, Southrons, orcs and their traders) get the empty loot table vanilla gives villagers, and thrown ammo, the trap and the boats stop claiming a loot table they never had.
- The three shears attach their repair tag and tool rules while the item registers rather than in the item's static properties, so a data generation run, where the item registry is already frozen, no longer fails on them. The trinkets' default potion component is attached the same way.
- Barrels open towards the side they are placed against. The generator's direction variants rotate a model whose front is drawn on its top face (a barrel's lid) 180 degrees from vanilla's convention, so the nine barrels now ask for that offset and their blockstates match vanilla's.
- Wall blockstates are written in vanilla's order - the post first, then the four low sides north to west, then the four tall ones - rather than in whatever order the map of wall sides happened to iterate, so regenerating no longer rewrites every wall file.
- JEI shows the fireplace's recipes again. The game stopped sending recipes to the client, so the server now hands the fireplace recipe type to the client when it syncs a datapack, and the recipe category shows the recipes that arrive.
- The Blue Mountains no longer come out as bare stone: their floor keeps its grass, and mountain stone starts in the block beneath it instead of a band of dirt.
- The Camouflage Chest now works as a chest instead of being a plain block: it holds 27 slots, opens on right-click with the player's inventory below them, reads on a comparator, works with hoppers and pipes, and drops its contents when broken.
- Goats warn before they charge again: their melee hook had stopped being called, so the head-down warning never played.

## [4.3.81]  - 2026-10-18

### Added

- Added Forochel and Forochel Slopes biomes, cold snowy plains and mountain biomes based on vanilla snowy plains and snowy slopes.
- Added frozen house village structure (ttmhouse_frozen) that generates in the Forochel biome with human NPCs as residents.

### Fixed

- Generated entity loot tables had a potion id baked back into them, so a table's trinkets could only ever carry the one potion written into the file. The tables ship without the id again and pick a potion when the loot is rolled.

### Removed

- The spider tree's old `ttmspidertree` structure template is gone; nothing referenced it once the structure became `ttmtree_spider`.

## [4.2.80] - 2026-10-17

### Changed

- Barrows rise in clusters instead of standing alone. Barrow-downs now asks for one cluster every ten chunks, and each cluster seats one to five mounds on a single circle, a block or three apart, with its own random facing; a cluster that cannot seat a mound simply comes out smaller.
- The build number and the local changelog entry are written only when a jar is built, so loading the game for testing - client, server or data generation - no longer bumps the version or edits the changelog.

## - 2026-10-17
* Initial automated test build setup

## [4.2.054] - 2026-10-17

### Changed

- The dwarven, human and Nurn villages lay the mod's stone path block where a vanilla village lays a path. Dwarven and human streets, the stubs that close a road off and the fountain plaza still behave the way vanillas do - a tenth of a street still grows over, and street crossing water is still bridged - and the driveway and the porch at each house's door are the block outright. Nurn's roads, its road stubs and the plaza around its well walk on the block too, where the desert village they are built from walked on smooth sandstone; its walls, well and lamps stay dark stone.

### Fixed

- A Nurn lake no longer writes outside the chunks it is allowed to write, which filled the log with `Detected setBlock in a far chunk` errors as the player moved and left the edge of the lake and its grass shore cut off. The whole lake, water and shore, now stays inside the feature's write window.

### Entity AI and behavior

- Changed `meleeGoal` and `bowGoal` visibility from `private` to `protected` in `TolkienMonsterEntity` to allow subclass access.
- Adjusted flying behavior for `GreatEagleEntity` and `GwahirEntity` to fly more frequently by:
  - Reducing `StartFlyingBirdGoal` chance from 120 to 60 (2x more frequent takeoff)
  - Increasing `LandBirdGoal` chance from 240 to 480 (2x less frequent landing)
  - FellBeast behavior modified to save spawn location
  - Fixed bug with Witch King spawning with Fell Beast

### Villages

- Converted desert, dwarf, elven, hobbit, and human houses into villages with connected paths and driveways, wells or fountains, bells, farms, decorations, animals, and villagers. Existing Tolkien inhabitants are retained.
- Villages generate in Haradwaith, Iron Hills, Lorinand, Shire, and Gladden Fields respectively, using one shared placement grid instead of independent house clusters.

### Flying mobs

- Fixed actual takeoff and airborne movement for crebain, thrushes, great eagles, and Gwahir, including flight controls, navigation conflicts, and landing behavior.
- Enforced a 64-block horizontal flight radius and a ceiling 100 blocks above the spawn origin. Flight origin and flying state persist across saves; sitting stops flight.
- Added gliding and eagle dive attacks, updated flight animations, and made great eagles and Gwahir take off more often and land less often.

### Dwarven Miner

- Set the maximum enchantment level to 4: levels 1–4 mine full 3×3, 5×5, 7×7, and 9×9 areas.
- Mining follows the targeted face. Wall patterns extend one block below the target, with the remaining rows above; floor and ceiling patterns are centered on the target.
- Restored normal harvesting, drops, experience, durability, and cancellable break events. Added tool-tier and unbreakable-block checks; sneaking disables area mining, and mining stops when the tool breaks.

### Other entity and data updates

- Moved the ambient animal base to `TamableAnimal` and updated its subclasses; added synchronized gliding and aggression states.
- Added melee combat goals to several hostile mobs, adjusted goblin goal priorities, and exposed shared melee/bow goals to subclasses.
- Fixed the Goblin King's snowball projectile launch and configured its ranged combat independently of held bows.
- Updated potion assignments in ten generated entity trinket loot tables and reordered biome tag entries without changing their membership.
- Fixed the Witch King, the nazgul and the Mordor orc being rendered standing on their mounts: all three now play a sitting pose while riding, and the animation moves the bones their rigs actually have.

### World generation

- Rockpiles dot a biome instead of carpeting it: one placement now fills a 1-3 block patch rather than every surface column, and each chunk gets six placements instead of ten.
- Random rubble, stone spikes and stumps are sporadic decoration. Rubble makes half as many attempts per placement and drops to six placements per chunk; spikes and stumps pass a one-in-six chunk filter and place one or two, and are positioned from their own column's surface instead of the chunk corner's.
- Rock piles recognise a neighbour by block type instead of exact state, so grass carrying snow counts as grass.
- Mirkwood webs hang from the tree canopy. They read a heightmap that never contains a placed tree and ran in the decoration step ahead of the mirkwood trees, so nearly every attempt found open air, rock or sky; they now read the live heightmap and run after the trees, and four placements do the work of twenty.
- Mordor and the Ash Mountains no longer hold water. The dimension's aquifer fills those biomes under the sea level and in pockets high above it, which no biome setting can change, so chunks now turn that water into lava as they load. Terrain generated before this change is converted too.
- Nurn is the watered half of that country: dark stone ground broken up by cobbled patches, tan water, a full 1.0 of rainfall, and no lava of its own anywhere in the biome. Grass grows only near water - on the low shores and islands a few blocks above the water table, and on the meadow a lake lays around its own water - so the biome reads as dry ground with green at its edges.
- Nurn's lakes are dug by a feature of their own, at most two chunks across, and asked for once every 18 chunks so the lowland carries the occasional lake rather than a chain of them. Each one lays the grass meadow around its water, up to sixteen blocks out, and the whole lake stays inside the chunks a feature is allowed to write to, so nothing is left with a missing edge.
- Nurn's villages are the vanilla desert village rebuilt in dark stone. Every sandstone and terracotta shape is swapped for the dark stone block of the same kind, the farm rims that were sandstone stairs become deadwood log fences, the crops stay the desert crops, and the roads get their own terminator pool, because the desert pieces that close a road off are raw smooth sandstone and do not pass through the street templates. A Nurn village only starts on dry ground.

### Trinkets and loot

- A trinket's potion is chosen when its loot is rolled instead of while the loot tables are being built, so the trinkets from one table no longer all carry the same potion. A potion id written into a table by hand still pins that potion.
- Trinket loot only hands out benevolent potions: harming, poison, slowness, turtle master's slowness and the mod's own elemental, corrosion and terror potions no longer turn up on worn belts, rings and amulets.
- Generated biome tag files are written in a stable order, so two data generation runs in a row no longer rewrite them.

### Trinket table

- The table mends damaged gear as well as crafting trinkets. A damaged tool in the trinket slot, its repair material in the potion slot and an ammolite gem in the gem slot undo up to 1000 damage per gem and material, repairing the tool where it sits rather than moving it to the output slot.
- Which material repairs what follows the anvil's own rule, so mod tools take their own ingots, and the mod's shears - the one tool the anvil gives no material at all - now take their tier's repair item.
- The damage one gem and one material undo is a config value, `trinket_repair_amount`, defaulting to 1000.
- What repairs what is a data pack's decision too: an entry under `data/<pack>/tolkienmobs/repair_material/` names the tools, by item or by tag, and the material that mends them. An entry covering a tool replaces the anvil's rule for that tool, so a pack can hand the table a material the anvil would refuse or take away the one it would use.
- The mod ships no entries of its own and the anvil's rule stays the fallback, so an unmodified game repairs exactly as before. Entries are read when the world loads.
