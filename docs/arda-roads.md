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

## What a road has to be in the game

- **Stateless.** Worldgen runs per chunk with no persistent state, so a road has to be a pure function of
  x and z — like the rivers, which read a mask, or like a cell polyline read arithmetically. No saved
  road registry, no structure pass.
- **Width and surface.** Settled together in *The surface, as decided* below: three widths, three bands by
  distance to a location, and one fixed per-block mix for each band.
- **Cut, fill, or paint.** Settled as paint. The road follows the ground and replaces its top block; the
  only ground it moves is a cobblestone slab on the low side of a one-block rise. Nothing cuts a corridor,
  so a road on a slope is a staircase - of half steps, at least.
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

## Candidate first routes (proposals; none checked in game)

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

`tools/arda_roads.py` renders these over the reference terrain, with the grid and the campaign's pinned
places, so a route can be seen against the ground before anything is built.

## The surface, as decided

**Width is set per stretch, not per route**, because which road is a highway and which is a track to a
cave is decided by what it serves - that is, as the builds are placed. Three widths, named for where each
belongs:

| width | blocks | half-width | where |
| --- | --- | --- | --- |
| `wild` | 7 | 3 | the default: open country, the roads between towns |
| `urban` | 5 | 2 | the last stretch into a settlement |
| `rural` | 3 | 1 | a spur: the track out to a cave or a camp |

The wild width's edge is ragged - a block of hash jitter on the half-width - because that is what the
reference surface shows; the narrower two keep a clean edge.

**Three bands, by distance from the nearest location**, in blocks, measured at generation time to a
*pinned* cell centre (`ArdaMap.centreOf`, the way `Places.CANON_CELL` pins Rivendell). It has to be a
pinned cell and not a surveyed site: the band is part of the surface `ArdaMap.sample` answers, which runs
before the place itself exists.

| band | distance | mix |
| --- | --- | --- |
| wild | beyond 64 | gravel ~40%, coarse dirt ~25%, grass block ~20%, dirt path ~15% |
| transition | 64 down to 16 | cobblestone ~50%, dirt path ~27%, coarse dirt ~10%, mossy cobblestone ~8%, grass block ~5% |
| town | inside 16 | the transition mix with dirt path -> stone path, coarse dirt -> stone, grass block -> mossy stone |

The percentages were read off two screenshots of the reference surface: a wild strip in open country, and a
transition strip at the edge of a place. The wild band is earth, gravel and grass wearing through; the
transition band is paved, with grass left only at the rim; the town band is that paving with nothing soft
left in it. The town swap is the one the mod's villages already make - `TolkienProcessorLists.stonePathStreets()`
replaces `DIRT_PATH` with `STONE_PATH` in the dwarven, human, Nurn and frozen street sets - so a road's
town band and a village's street are the same walking surface.

The mix is chosen **per block, from a hash of (x, z)**, never per blob: both reference strips are a
per-block scatter with no clumps, and a hash is the one form that survives a chunk border - the same
column gets the same block whichever chunk asks for it. Grass block is placed literally everywhere,
including sand and snow: a Harad road will have green squares in it, which is the chosen look.

**Steps.** Where two neighbouring road columns differ by exactly one block, the lower column's top becomes
a bottom `minecraft:cobblestone_slab`, so the rise is a half step rather than a full drop. That needs one
sample of the column ahead, which a road pass reads anyway. Gravel in the wild band is gravity-affected:
a wild road's gravel falls if the ground under it is ever removed - a note, not a problem.

## What is not decided

- Whether these seven routes are the right seven, and whether their bends match the canon map well enough.
- Which road towns get pinned cells, and to which cells: that is the first actual change, and it is small.
- Which blocks the town band's "stone" and "mossy stone" are: plain `minecraft:stone` and
  `minecraft:mossy_cobblestone`, or the mod's dark stone set.
- The band radii themselves (64, and 16 inside that), and which locations own a band: both fixed as the
  builds are placed.
- How a road crosses a river — ford today, bridge later.
- How a road behaves on the ground the drawn cells and the painted relief disagree about, Rivendell's
  approach being the obvious case.
- Whether the campaign's own road (chapter two speaks of the road east of Mirkwood) is built first, since no
  ground under it exists yet and a page already sends players along it.
