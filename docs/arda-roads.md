# Roads in Arda — where they come from, and how they would be laid

Nothing here is built. This is the record of where the roads can come from and how they would be
derived, so the routes can be argued about before any ground is moved.

## What the references actually contain

- **`Reference/road flaten2.3.js` is a tool, not a record.** It is a WorldPainter script ("Road Flatten",
  by sijmen_v_b) that takes a *mask of a centre line* — `fileMask`, a PNG — and cuts and fills the terrain
  inside `distance` blocks of it, optionally painting a road layer and slabifying it. In other words the
  map's author drew the roads in WorldPainter as a mask, ran this, and the flattened road came out in the
  terrain. The script says how roads were *made*; it does not say where they are.
- **The terrain albedo carries no road layer.** `ME_Terrain_albedo.png` is biome and relief colouring; the
  road flattening is geometry, not paint, so there is no colour to key on.
- **The canon map is the only place roads are drawn.** `middle-earth2.png` and `middle-earth.jpeg` are the
  same map (Thierry Jungels, version 7, 8192 square) in two encodings, and both show the roads as black
  dotted and solid lines. They are drawn in the same ink as labels, borders and mountain hatching: a
  dark-neutral threshold catches **6.8%** of the image's pixels, so colour cannot separate a road from a
  letter or a ridge line. Automatic extraction would need thin-line and dash-topology work, and it would
  still need someone to decide which of the resulting threads are roads.

## Where the routes come from, in order

1. **Ground the world already names.** A road is useful when it joins somewhere a page can send a player,
   and the mod already has those points:
   - `ArdaMap.anchor(Country)` — the middle of a country's drawn cells, used as the survey's fallback.
   - `ArdaMap.centreOf(column, row)` — the middle of one drawn cell, 512 blocks across.
   - `Places.CANON_CELL` — `rivendell` 18/12, `thorenhad` 19/13, `barachen` 18/14, `ford` 17/13.
   - `Sites.CANON_CELL` — the wood ruin 19/14, the wild tent 17/13, the three troll caves 17/15, 18/16,
     19/16, and Glóin's camp at the mouth of the pass, 24/10.
   These are cells, not blocks, because that is the precision the drawing has.
2. **The drawing's own cell grid.** Routes are stored as cell-space waypoints, exactly the way
   `line(map, Country.HITHAEGLIR, 2, columns, rows)` already paints the Misty Mountains. The block path is
   the interpolation between waypoints at generation time, so a road follows the map at 512-block
   granularity — which is the resolution the country grid itself has.
3. **The canon map, for shape.** Where a route runs within a cell is chosen to match the dotted lines on
   `middle-earth2.png`: the East–West Road, the Greenway, the Old Forest Road, the Harad Road.

## Why the routes are not block-exact

The masks are baked at eight blocks to the pixel and carry no road layer, and the canon map's calibration
to this world's frame has never been established — `docs/arda-masks.md` records that it is a different
projection, where the block coordinates that land on Rivendell land on Swanfleet. Tracing roads block by
block from that map would import that error with false precision. Cell waypoints are the honest resolution.

## Two kinds of route

A route is a section in `Roads`. Where the road crosses water it is a `Route.Crossing`: the path's last block,
the two banks, the line of the span, the deck and the pieces that stand it up. Where it crosses none - which is
every road between two builds on the same bank - it is a `Route.Riverless`: the build's footprint and entry
point, and the far end a surveyor marked. Two ends and the distance between them is the whole route; the search
finds the way, the grade lays it, and there is nothing to bridge.

The roads are planned now rather than walked: `Roads.NETWORK` names eleven roads, each by the two blocks it runs
between, and `RoadPlan` works the way out from the drawing - a lane walk over `ArdaMap.sample` - and cuts the
result into sections, a span among them wherever the line crosses water. Three of the eleven are the valley's,
carried in `Roads.BUILDS` and waiting for the city to stand. A section ends where the next one begins rather
than at the far bank, so neither pass grades country the other has already laid. A road is also stepped from the
block its pass has reached rather than from the building it leaves, which is what lets a section fill in as it
is walked; and its grade is held within four blocks of the ground, so a hill is walked over instead of bored
through.

The far end is ground rather than a deck, which is why a dry road needs no water numbers at all: the grade arrives
on whatever the ground there is, so nothing has to be levelled onto a span.

## What a road has to be in the game

- **Stateless.** Worldgen runs per chunk with no persistent state, so a road has to be a pure function of
  x and z — like the rivers, which read a mask, or like a cell polyline read arithmetically. No saved
  road registry, no structure pass.
- **Width and surface.** Settled together in *The surface, as decided* below: three widths, three bands by
  distance to a location, and one fixed per-block mix for each band.
- **Cut, fill, or paint.** Settled as paint for worldgen, because a generated pass cannot know what has
  been built beside it. The first road built by hand is graded instead, cut into the hillside above its
  line and filled below it, because the ground under the valley's own approach is a wall face and a
  painted road there read as a staircase. Worldgen will have to answer the same question at this bank.
- **Water.** Shaping ground cannot bridge a river. `docs/rivendell-state.md` already says the Ford is
  water and banks with no bridge — "nothing places a road or a bridge over the Bruinen yet". A route that
  crosses water needs a ford, a bridge structure, or to stop at the bank.
- **Steep ground.** A road up the High Pass either switchbacks or is cut as a shelf; the reference's flat
  radius does neither, it just flattens whatever was there.

## What checking them against the map showed

Three things had to be settled before a route could even be drawn, and all three were found by putting the
two references in the same frame (`build/arda-roads-frame.jpg`) and then the routes over the canon map at
four blocks to the pixel (`build/arda-roads-detail.jpg`).

1. **The canon map is not in this world's frame.** Its Eriador sits several cells north of the drawing's:
   on the canon sheet Bree is at about row 11, while the world draws Bree-land at rows 19–21. Both sheets
   agree where the Misty Mountains and Mirkwood are, but the canon map's features are larger per cell and
   slide apart as they go south. So a road cannot be traced in canon pixels and written down as blocks. The
   canon map decides *topology* — which places a road joins, and which way it bends — not coordinates.
2. **A country's middle is not a town.** `anchor()` averages a country's cells, and Eriador is drawn as one
   band from row 6 to row 21, so `anchor(ERIADOR)` — the fallback for Bree and Weathertop — lands at about
   12/10: north of the Shire by nine cells. That is fine for finding a landmark hill somewhere in Eriador and
   wrong for a road, which has to start at a gate. Every road town needs a pinned cell of its own, the way
   `Places.CANON_CELL` pins Rivendell, the Ford and Barachen.
3. **The drawn cells and the painted relief disagree by up to a cell, and roads are less forgiving than
   countries.** Rivendell's cell, 18/12, is drawn as Trollshaws, but the terrain behind it rises into the
   ridge; a country can absorb that, a 512-block road cannot — it would climb the mountain instead of
   entering the valley.

## Candidate first routes (the first sketch; never built)

Cells are `column, row` in the drawing's own grid; a cell's middle is `(column - 10) * 512 + 256`,
`(row - 20) * 512 + 256` in blocks. Each waypoint is in a country the drawing actually paints: Eriador,
Old Forest, Barrow Downs, Dunland, Trollshaws, Gladden, Mirkwood, Dale, Iron Hills, Rhûn, Dagorlad, Gondor,
Harad, Lórien, Fangorn, Rohan.

| route | waypoints (cells) | joins |
| --- | --- | --- |
| East–West Road | 12/20, 13/19, 15/18, 16/16, 17/14 (the Ford), 18/12 | Bree — Weathertop — the Ford — Rivendell |
| Greenway | 12/20, 12/22, 13/23, 14/24 | Bree — the Old Forest — the Barrow Downs — Tharbad |
| Old Forest Road | 24/10, 26/9, 29/9, 32/9, 34/8, 35/6 | Glóin's camp — across Mirkwood — Dale — Erebor |
| Iron Hills spur | 35/6, 37/7, 40/7 | Erebor — the Iron Hills |
| East March road | 35/6, 36/11, 36/16, 35/20, 34/23 | Dale — down Rhûn's edge — the Dagorlad |
| Harad Road | 25/28, 26/30, 27/31, 27/33 | Minas Tirith — South Gondor — Near Harad |
| Anduin road | 23/15, 23/19, 25/21, 26/24 | Lórien — the Wold — Edoras |

The ends that are already pinned in code (Rivendell 18/12, the Ford 17/13, Glóin's camp 24/10, and the
troll caves) are trustworthy. The rest — Bree 12/20, Weathertop 15/18, Tharbad 14/24, Dale 35/6, Minas
Tirith 25/28, Edoras 26/24 — are proposals, read off the drawing's countries and the canon map's labels.

`tools/arda_roads.py` renders these over the reference terrain, with the grid and the campaign's pinned places,
so a route can be seen against the ground before anything is built. None of them was: the roads that exist were
planned from pinned ends in a test world, which is the network at the end of this file.

## The surface, as decided

**Width is set by the band a column stands in, and a route may name its own instead**, because which road is a
highway and which is a track to a cave is decided by what it serves - that is, as the builds are placed. Five
kinds now, named for where each belongs:

| kind | blocks | half-width | where |
| --- | --- | --- | --- |
| `wild` | 3 | 1 | open country: the track between places, out to a camp or a cave |
| `transition` | 5 | 2 | 64 down to 16 blocks of a settlement, where the paving starts |
| `urban` | 7 | 3 | inside 16 blocks of a settlement: its own street |
| `wild_wide` | 5 | 2 | a track walked out before its country is known: the country's own surface, two abreast |
| `main` | 7 | 3 | a road that carries traffic: a street's width, but the country's own surface |

The band decides the kind of a road that does not say otherwise, so an ordinary road widens as it comes in from the
country to a gate. A hairpin's apron is one kind wider than the run it turns - five through a switchback on a wild
track. The wild width's edge is ragged - one column in three of it left as the country has it, which is what the
reference surface shows; the wider two keep a clean edge.

**The bands are for roads, not for every build.** A settlement's own road is banded: seven blocks of its street
inside the wall's band, five where the paving starts, three out in the country, so it widens as it comes in to the
gate. A road that is a road rather than a build's way out can be banded too - the valley's main road runs from a camp to a
crossing and belongs to neither, so it is a road at the ends it serves and a track between. `Roads.BANDED` names
those, though the table is empty today: a planned road names its kind instead and is one width end to end.

**Every other build is a wild place**, a camp or a ruin or a cave mouth - nearly every build in the chapter - and its
own way out is a wild track from its own door: three blocks wide, the country's own gravel and dirt, no paving at all.
Naming the banded roads rather than the wild tracks means a build added later is wild until somebody says otherwise.

**A road may name its kind instead**, in `Roads.KINDS`, and is then laid at it from end to end: a road kept seven
blocks wide across a moor as much as through a gate, with that kind's mix all the way. A named kind overrides the
bands, and it is how a road that is deliberately one width is made - none is named in `Roads.KINDS` yet, though a
planned road is given its kind when it is planned.

**Three bands, by distance from the nearest location**, in blocks, measured at generation time to a
*pinned* cell centre (`ArdaMap.centreOf`, the way `Places.CANON_CELL` pins Rivendell). It has to be a
pinned cell and not a surveyed site: the band is part of the surface `ArdaMap.sample` answers, which runs
before the place itself exists.

| band | distance | mix |
| --- | --- | --- |
| wild | beyond 64 | gravel ~40%, coarse dirt ~25%, grass block ~20%, dirt path ~15% |
| transition | 64 down to 16 | cobblestone ~50%, dirt path ~27%, coarse dirt ~10%, mossy cobblestone ~8%, grass block ~5% |
| town | inside 16 | the transition mix with dirt path -> stone path, coarse dirt -> stone bricks, grass block -> mossy stone bricks |

The percentages were read off two screenshots of the reference surface: a wild strip in open country, and a
transition strip at the edge of a place. The wild band is earth, gravel and grass wearing through; the
transition band is paved, with grass left only at the rim; the town band is that paving with nothing soft
left in it. The town swap is the one the mod's villages already make - `TolkienProcessorLists.stonePathStreets()`
replaces `DIRT_PATH` with `STONE_PATH` in the dwarven, human, Nurn and frozen street sets - so a road's
town band and a village's street are the same walking surface. The band's other two swaps are the vanilla
`minecraft:stone_bricks` and `minecraft:mossy_stone_bricks`, so the loose ground of the transition band
becomes built masonry at the gate.

The mix is chosen **per block, from a hash of (x, z)**, never per blob: both reference strips are a
per-block scatter with no clumps, and a hash is the one form that survives a chunk border - the same
column gets the same block whichever chunk asks for it. Grass block is placed literally everywhere,
including sand and snow: a Harad road will have green squares in it, which is the chosen look.

**Steps.** The grade the pass lays is settled on halves, so a station's height falls either on a block or
halfway between two, and the surface of a column is read from that height alone: the road's own block, and, where the
height falls between two blocks, a bottom `minecraft:cobblestone_slab` on top of it. Neither a block nor a slab looks at
its neighbours to decide what it should be, which is what closes the corners and the diagonals of every terrace - a
column whose own height was whole but which stood beside a half one used to be left open, because the old rule only
edged a terrace where a neighbour stood exactly one block lower.

The slab sits on top of the road's block rather than sunk into it. A bottom slab in the column's own cell is half a
block lower than the block beside it, so a station the profile puts half a block *higher* would have been laid half a
block *lower*, and every rise built as a fall. Over the block, a column's surface is its own height whichever half it
falls on, and a walker never drops a half step in order to take one. Gravel in the wild band is gravity-affected: a
wild road's gravel falls if the ground under it is ever removed - a note, not a problem.

## What is not decided

- Whether the eleven roads in the network are the right eleven, and whether their bends match the canon map well
  enough.
- Which road towns get pinned cells, and to which cells: that is the first actual change, and it is small.
- The band radii themselves (64, and 16 inside that), and which locations own a band: both fixed as the
  builds are placed.
- How a road crosses a river — ford today, bridge later.
- How a road behaves on the ground the drawn cells and the painted relief disagree about, Rivendell's
  approach being the obvious case.
- Whether the network is the whole survey: the country between its roads is nobody's, and a road is only added by
  naming its two ends.

## Roads the world generates

A road that stands in every world cannot search for its alignment: worldgen answers for a chunk whose neighbours do not
exist yet, so the road has to be a pure function of x and z, exactly the way the drawn rivers are. That is
`RoadPaint`: a table of lines, each a list of block waypoints and a half-width, and the generator does the rest -
`ArdaChunkGenerator.reshape` cuts and fills the corridor to the road's grade, and `buildSurface` paves it after the
surface rules have dressed the new ground.

The table is empty as of 20 November: no `Line` is written, and every road the world lays is a `Route` planned
from two pinned ends and carried by the pass described below. A line remains the way a road with no build behind
it at either end would be painted by generation.

The grade is the line's own heights. A waypoint carries the height the scout stood at, the height between two
waypoints is a cubic through theirs - eased at each waypoint so the profile curves through a bend instead of kinking
there - and every column of the corridor takes the height of the nearest point of the line - so the strip is level across its width and a ramp along its length, and the ground above that height is
cut away while the ground below it is filled. That is the shelf, answered per column from the column's own
coordinates: a cutting where the hill stands over the line and an embankment where it falls away, with no heightmap
read and no search, because a chunk cannot see the ground it would have to compare itself with.

Two limits keep a line honest. The slope at each waypoint is the two legs either side of it averaged, held to a third
of the shallower one and levelled at a crest or a hollow, which is the easing that cannot overshoot - a cubic through
two heights can otherwise leave the interval between them, and a road dug for a hollow nobody walked is not what the
scout marked. And the shelf is held within four blocks of the ground the column already has, cutting and filling no
deeper than that and keeping the ground beyond it: waypoints are walked by hand and a pair a long way apart can promise
a grade the hill between them does not have, and without the bound that promise is answered with a trench. Both the
painted surface and the heights carvers and structure sites read are that same bounded shelf, so the road block and the
ground under it cannot disagree.

A line can be written either way. Waypoints that carry the height a scout walked are graded by those heights, which is
what a section read back out of a world with `tools/road_line.py` gives. Waypoints that are places put `0` where the
height would be, and the grade is then the ground's own: the drawn ground at each point along the line and eight
blocks either side of it, averaged, so the road follows the contours of whichever world raises it rather than the
contours of the one it was scouted in. That is the form a road baked between two markers takes - a marker says where
the road goes, and says nothing about a height that has to survive a change of terrain code or a different seed.

A line is for a road with no build behind it at either end. A build's own road is a route: `Roads` holds the network,
and a road planned at a build joins the same list; `RoadCommands` searches and grades it, and the world runs that pass
for whichever build a route has been planned for once that build is standing (`Roads.waits`) - the same
builder that laid the valley's road, so a road between two buildings never
needs a height written down for it. That the road is down is written into the world's own site record beside the
building it belongs to, so a restart finds it laid rather than searching and grading the same ground again.
A road that was stopped half-laid is carried the same way: the pass writes its own stage, the alignment the search
found, the grade the profile settled and how far it is laid at every turn of the pass and every few steps between, so
the next start continues the road instead of sweeping the bridge and cutting the shelf over again - and a world stopped
between two writes re-lays only the few stations those steps had reached. A pass is stepped, not run: a world dressing itself has one tick at a time, and no tick carries a whole bridge.
Each stage therefore works to a budget of columns rather than to a stage's worth of work - twelve columns of sweep or
pillars, one bridge piece, four thousand settled candidates of the search, two hundred and fifty-six columns of the
graded roads shared between the two of them, a thousand columns of terraces walked for lips. A piece is a whole
template of some five hundred blocks and a station a band of up to a hundred and twenty-one columns, so counting them
was counting the wrong thing: the bridge used to be stood in a single tick and the lips walked in the tick after the
last station, both of them tens of thousands of block writes at once.
A build the world raises gets its road from the same clock the dressing does: every approach to a camp that already
stands asks for the keeper, the stone and the road, and the pass is planned the first time and stepped a little on each
approach after that. A road four hundred blocks long is not one tick's work, and a player standing at a camp for a
minute or two is what lays it.
A stage says what it cost when it is done with - how many steps it took and what a step came to - and the pass says the
same of itself when the road is down, in the server log under `Road <route>:`. That is the only way a count of columns
can be checked against a tick: a budget is a guess until a real pass has been timed, and the line is where to look when
a road feels slow. A pass that was carried across a restart reports only the work it has done since it was picked up.

The search is the one stage that is not carried -
its state is a hundred thousand candidate blocks - so a world stopped inside it searches again, and everything after
it is picked up where it was left.

What it still cannot do is cross water: a road whose grade would stand at or under the waterline is left to the map,
since a crossing is a ford's or a bridge's business. And a line's grade is only as good as the waypoints written on
it - two markers six hundred blocks apart give a ramp the ground may not agree with, so a section is written down as
the markers it was actually walked between, a bend and a change of height getting one each.

Writing a road down is one `Line` in `RoadPaint.LINES`, with the markers as its waypoints - heights included - and the
width that stretch asks for. The surface is the per-column hash mix settled below, and the last twenty-four blocks
before either end are paved. Nothing else in the mod changes when a line is added, and no command has to be run in any
world: the road is in the next world that generates.

A painted road is painted before the features run, so a strip through wood would arrive with a forest growing out of
it. `RoadCorridor` clears the corridor when a chunk holding a road loads - leaves, logs, vines and replaceable plants
above the road's own surface, and the road's block written back where a trunk had taken it - because chunk generation
has no single place to answer for the trees of a neighbour, and the finished chunk is the first moment the road and
everything planted over it are both there. It only answers in Arda, and running it again costs nothing.

The corridor is the pavement plus two blocks of verge either side. The verge is cleared but not paved: a trunk standing
one block off the edge would otherwise be cut back to bare legs wherever its crown overhung the road, and a pole beside
the pavement reads worse than a road with a hem. Two blocks takes the trees that would stand on the verge and leaves
the wood behind it.

`tools/road_line.py` reads a laid road back out of a save as waypoints: it takes the pass's own surface names, groups
the columns into the pieces a crossing splits them into, and prints a line every `--step` blocks with the height of
the surface. It can only read a world that still has the road in it - a stretch whose chunks were never saved has no
line to recover, and a box of ordinary terrain reports as pieces of gravel rather than a road.

The pass below is still the shape a road with ground under it is laid by, and it is still the only thing that
bridges one: a generated line follows the grade its waypoints give, so the network's roads - the valley's three,
and the eight between the camps and Gundabad's gate - are all laid by the pass, a section at a time. A section
is promoted to a generated line only once it can be described without searching it.

## Water a road was told about

A span is not lining a road lays: it is a structure of its own - two abutments and the middles between them,
stood on piers drawn down to the bed - and it is a route already, a `Route.Crossing` with its banks, its deck,
the base its pieces stand at and the pieces themselves. `RoadPlan` reads a run of river columns on the planned
line as a crossing of that pattern - the banks, the axis and the deck over the water - and makes the span a
section of the road. `Roads.water` answers from those routes rather than from a table kept beside them: a span
is a fact about the water, so every span answers for every road. The two hand-written crossings of the old
forest road (`OLD_FORD_BRIDGE`, `FOREST_RIVER_BRIDGE`) went with the walked routes on 18 November.

What the road does with one is stop. The pass leaves the water to the crossing - the deck, its piers and its abutments
are that route's own pieces, laid by the pass that walks that route - and picks up on the far bank, so a road never
paints a ribbon across a river it has a bridge for. Two things still belong to the road:

- **The arrival.** Every station of it standing in the water is pinned to the crossing's walking surface before the
  steps settle, so the bank sweeps up onto the deck at half a block a station. Left to grade itself, the road reads the
  river as ground and dives in.
- **The reach.** A column is on the crossing when it stands between the span's banks on the span's own axis and within
  `CROSSING_ACROSS` - twelve blocks - of the line the pieces are stood on. That line is the scout's and the road is a
  search between marks: matched within a block or three of the marks, neither crossing was ever met at all.

The pieces are stood from the near bank *down* the span's axis, which is why a crossing is written from its east bank to
its west one whatever direction the road walks it. Its deck sits two over the water - the middles' base is `deck - 2`
and the abutments' `deck - 1` - and the stream's crossing east of Glóin's camp is the worked example of the numbers.

`Roads.Wet` keeps the two ways a road can lay water itself, for the day a crossing wants no bridge: `FORD` lays
the road's own surface at the surveyed height and makes the column under it solid down to the bed, leaving the
water standing over it; `SPAN` lays a deck across the band with a pier every fourth station. `Roads.water`
reports `SPAN` for every crossing today, and no route asks for a `FORD`.

## The first road, as built

The Rivendell approach is the first road with ground under it, and its shape is settled. It is laid with
`/tmroad build` rather than by the worldgen pass the rest of this document describes, and that is where it
stays for now: the shape is a search over the live heightmap with a graded profile cut and filled into it,
and chunk generation cannot search two hundred and forty square blocks of ground that do not exist yet.
What ships is the pass itself, in the jar, beside the two bridge pieces, so any world gets the same road by
running it on the same ground - the numbers below are one `Route` in `Roads`, the valley's, and a column's block is
decided by its coordinates, so nothing drifts between runs. It is also safe to run again: the bridge's own
timbers are swept out of the water first, and every column in the band is written to what the pass decides
it should be. This is the shape a generation rule will have to answer to, and the first thing such a rule
needs is an alignment it does not have to search.

**The waypoints are the four ammolite markers walked in the scouted world**, not a line read off a map:
the path's last block at 5200, 134, -3034, the two side-by-side markers being that block and the first
block outside the build; and the two banks at 5055, 74, -3075 and 5006, 74, -3075, one on either side of
the water.

**The fall is the approach, and it is graded rather than painted.** The wall outside the built path stands
at 134 and the water at 74, so the road has sixty blocks to lose over a hundred and forty-five. Painted,
that was a staircase down a cliff face - the ground six blocks out from the path is already at 109. The
pass therefore finds its own way down: a weighted walk over the live heightmap that prefers level ground
and pays for every block of rise or fall - twelve times the square of any grade above a third of a block
per block, and a little for every change of direction - so it runs along the contours, turns where the
face is too steep to fall down, and comes back lower. That turn is the switchback: the road's answer to
a grade the mountain will not give, where the alternative was a hole in the road or an embankment forty
blocks tall. The walk turns within a single block, so its corner is pulled straight and then cut
back into a curve, rather than left as a hinge at right angles. How far back depends on the corner: an
ordinary bend is eased as widely as its legs allow - up to twenty-four blocks either way - so the road
arcs round it and a walker carries speed through, while a doubling back is a switchback and stays tight,
at most ten blocks, because easing one wide would swing the road off the shelf it was searched onto. An
eased corner reaches ground the search never walked, so the arc is let out only as far as it stays off
the river, out of the city's footprint, and on ground within a corner's own cut-and-fill budget, and is
halved again until it does. Only a doubling back earns the apron with it: two blocks wider either side,
a hairpin to walk round. A road that is merely cornering - the right angle onto the bridge's own line,
say - keeps its ordinary width, so the bank stops bulging out over the river wherever the road turns. The surface is the ground itself, smoothed: a moving average along the alignment takes the bumps out,
and the result is held within twelve blocks of the ground - cutting into the hill above its line and
standing on stone where the hill falls away. The profile is also held to a step at a station: two sweeps pull
every station within half a block of its neighbours, so a fall is a stair of half steps rather than a drop and no step
in a road is ever a jump.
The sweeps have the last word, after the last pass and after the clamp - alternated, so both of the
pinned ends agree with the middle - because a clamp that ran last put the riser back on any station
whose ground stood two or three blocks over its neighbours - that is the step a walker sees. The
column the alignment runs through is laid whatever the ground under it is doing - against a
rock face the road narrows to its own line rather than stopping - so the surface walked on is never
broken by a patch the pass declined to cut or fill.
That is the reference's own road-flatten idea - which is also cut and fill - rather than the paint-only
surface this document settles for worldgen.

Two things the first graded run taught, both now in the pass. A heightmap answers with whatever stops
motion, tree crowns included, so the first run read the valley as a forest roof and stood stone columns
up to the leaves; the ground is now read past leaves and trunks to the soil under them. A global grade between the two ends was tried next and was worse in another way: where the mountain
falls faster than any walkable ramp, the ramp stands on stone columns the whole way and the road is
missing wherever the cut would exceed the budget. The profile now follows the ground, smoothed and
held within twelve blocks of it, so it hugs the hillside instead of spanning it. Where the road meets water it carries its own three columns over on
stone while its grade is above the water line, instead of stopping short of the bank and leaving a lip
for the river to sit against. The rest of the band stops at the bank, which is the edge: carrying the
whole width over the water laid a gravel shelf out onto the river at the wide stretches, which nothing
walked on and which took the water's edge with it.
The corridor's own columns then have their trunks and leaves cleared for sixteen blocks above the
road's surface, so a cut through the wood leaves no crown hanging where its tree used to be.

**Both ends are met along the span's own line.** The last leg of the approach is on z -3075, the line
the bridge itself lies on, and the far side leaves along that same line. A road that arrived diagonally
would meet the abutment at an angle, and the cut has no junction piece to dress that meeting: the two
pieces are a straight span, nine wide, with an abutment at one end. Straight in, straight out, both on the
bridge's axis, is the only approach the pieces can receive.

**Only the launch onto the abutment is forced straight, though - sixteen blocks of it.** A longer
forced line is not neutral ground. The first graded run forced sixty-five, and in a fresh generation
met a rock fin fifty-nine blocks tall standing on the bank astride it: the profile followed the ground
up onto the fin's top and plunged off its end sixteen blocks from the deck, which is a cliff in the road
no amount of smoothing inside the budget could take out. The search therefore runs all the way to the
far end of the run-in and walks round whatever the mountain puts in the way; only the last sixteen
blocks are pinned to the span's line, and the fillet that rounds the corner into it leaves nine of them
dead straight on the deck's axis.

**The span is fifty columns: two ends and four middles.** `bridge_end` is 9 long, `bridge_middle` is 8,
and the two banks are at 5055 and 5006 - fifty columns end to end - so 9 + 4 x 8 + 9 lands the far
abutment exactly on the far marker. The east end is turned about on y so that its own abutment, which the
cut carries at its low x, faces the city and the road arrives onto it.

A turned template is anchored at the far corner of its own box, not the near one: the first attempt
anchored the east end at its near corner and the piece landed eight blocks west and eight north, in the
water. It is now anchored on 5055, -3071, its own north-east corner. The middles had the same disease in
another form - they were laid from their west edge, which put the whole run seven blocks east and left a
gap of open water before the west abutment - so each is now anchored on the east cursor and the cursor
walks west by the piece's own width.

**The deck sits two over the water.** Middles carry their planks two blocks above their base, so at 74
the walking surface is 76 over water at 74; the ends carry a slab one block up instead, so they are
placed one higher and meet the middle within half a block - the same half step the road takes everywhere
else on a rise.

**The pillars are drawn to the bed.** The pieces carry six blocks of support, and the Bruinen is four or
five deep, so as cut they stop in mid-air. Every column of the span that holds an oak log has its log
carried down, through air and through water, to the first block that is neither - the bed under the span,
or the bank under an abutment - capped at forty-eight blocks in case there is no bed to find. A bridge
that stops at the water line is a bridge floating on the river.

## A second build's road

The numbers above are one line now rather than constants scattered through the pass: a `Route` in `Roads`,
carrying the build's own footprint, the path's end, the two banks, the span's line and width, the deck height,
the run-in and the exit, and the two pieces the bridge is stood up from. The valley's is the first entry. The
dressing looks a city's route up by its footprint when it comes to lay the road, and leaves the ground alone if
nobody has walked one for that build - a city with no road is better than a road laid to another city's numbers.
`/tmroad build` lays the road of the build nearest the caller, which is what makes it usable beside a second site.

What a route cannot carry is the alignment. Where the road goes between the path's end and the bank is searched on
the live ground, so a new build's road needs its two ends surveyed - a start block and a bank - and nothing else
about the country below it.
