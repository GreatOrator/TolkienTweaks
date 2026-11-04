# Arda's masks

What the reference rasters are, how they are fitted to the world, and what the game does with them.

## The calibration

`reference/` holds four 8192 by 8192 rasters: `Final Height.png` (16-bit height, ocean at 0),
`ME_SimpleTopoTerrain_Rivers_albedo.png`, `ME_SimpleTopoTerrain_Lakes_albedo.png` and
`AreaMasks_westernMountain.png`. They share a canvas, and that canvas is **not** the world's frame
— Arda is a crop of it.

The transform was fitted, not guessed: the drawing's seven river polylines were matched to the rivers
mask by distance transform with differential evolution and then Nelder-Mead, and the result is
isotropic to within a fifth of a block:

| | |
| --- | --- |
| blocks per 8192-pixel, x | **3.954** |
| blocks per 8192-pixel, z | **4.018** |
| canvas origin, block | **-7546, -13561** |

Written out: block x = -7546 + 3.954 * pixel x, block z = -13561 + 4.018 * pixel z. The world frame
x -5120..19456 by z -10240..8192 is therefore pixels x 614..6830, z 826..5414 of the canvas.

Residuals, mean and worst, of the drawing's rivers against the mask, in blocks:

| river | mean | worst |
| --- | --- | --- |
| Entwash | 150 | 380 |
| Isen | 218 | 549 |
| Celduin | 335 | 1270 |
| Greyflood | 400 | 1044 |
| Anduin | 428 | 2730 |
| Brandywine | 622 | 1566 |
| Forest River | 1326 | 2169 |

Half a cell on average, four cells on the worst. The drawing's lakes are further out again: the Sea of
Rhûn and the Sea of Núrnen as rectangles are some 2,700 to 2,800 blocks off in z, which is why the
inland seas are the mask's job now and not the drawing's.

The mountain mask lines up with the rest. Of its ridges, 43.3% stand on the heightmap's top 7.7% of
ground against 5.5% for the rest of the canvas, and the best line-up shift is 0 pixels across and 10
along — a hundred and sixty blocks, inside the noise.

## Where they live

`Reference/` in the mod root, committed with the project: eight files, 59 MB, of which `Final Height.png`
is 57 MB. `Final Height.png` is the sixteen-bit topography, `ME_SimpleTopoTerrain_Rivers_albedo.png` the
rivers, `..._Lakes_albedo.png` the lakes, `AreaMasks_westernMountain.png` the ranges, and
`work/calibration.json` the transform between the 8192 canvas and the world.

`tools/mask_probe.py` reads them at their own resolution while a build or a piece of terrain is being
worked on, which is finer than the game's bake can afford to be:

    python tools/mask_probe.py 4995 -3311                      what the masks say about one block
    python tools/mask_probe.py --near 4900 -3322 --radius 400   the nearest river, in blocks
    python tools/mask_probe.py --near 4900 -3322 --radius 900 --layer lake

It answers the height as the world will stand on it - the game's own 63 to 318 scaling - whether a river
or a lake runs through the column, and how bright the mountain mask is there. Nothing has to be baked for
it to be right: it reads `Reference/` directly, using the same calibration worldgen uses.

## What each mask is for

- **The mountain mask**, `AreaMasks_westernMountain.png`, is the ranges' height: its whitest pixels are the
  summits and they are pinned at **318 blocks**, the crown the city's massif reaches.
- **The river and lake masks** are solid: where they are marked is where the water is placed.
- **The final height mask**, `Final Height.png`, is the reference for pinpointing places - it is what lets a
  build or a landmark be matched to the Middle-earth map - and it also draws the ground's own shape.

## The baked layers

`tools/arda_masks.py` crops the three masks to the world frame at **8 blocks to the pixel** — 3072 by
2304 — and writes them as `src/main/resources/assets/tolkienmobs/arda/`:

| file | size | marked |
| --- | --- | --- |
| `rivers.png` | 84 kB | 2.6% touched, 2.3% water |
| `bruinen.png` | 1.4 kB | 4.3%, 400 x 400 at **3 blocks to the pixel** - the Bruinen's own corner |
| `lakes.png` | 13 kB | 1.2% water |
| `mountains.png` | 363 kB | 23.8% above half brightness |
| `height.png` | 7.9 MB, sixteen-bit topography | sea 16.8% of the frame |

Run it from the mod root whenever the masks or `Reference/work/calibration.json` change. `ArdaMasks`
reads whatever is in the assets; a missing image leaves that mask empty and the drawing answers as it
did before.

The rivers and lakes are written as **cover**: how much of each eight-block cell the drawing marks, from
0 to 255. The game reads that share, interpolated between the cells round it, and calls a column water at a
third of a cell — so a bank falls where the line was drawn rather than on the cell grid, and a tributary
drawn along a cell's middle is still a channel. Measured on the bake: no cell the drawing touches is left
without water beside it, and the drawn bands are wide - eight cells across at the median, twenty-one at the ninetieth, which is
sixty-four and a hundred and sixty-eight blocks.

The river and lake censuses below were taken at the sixteen-block bake; the rule they describe is the
same at eight, and the counts have not been re-measured.

## What the game does with them

- **The ground.** `height.png` is `Final Height.png` at eight blocks to the pixel, unsigned 16-bit, and
  it is the ground now: interpolated between its pixels from the sea's own 63 up to the crown at 318, so
  the country rolls where the map rolls. The Shire reads 116 where the drawing had 68, Rohan 170, Mordor's
  plateau 224, the Misty Mountains 178. A column the map calls sea keeps the drawing's bed, because the
  map's zero is the sea's height.
- **Mountains.** Brightness is height, and the pin is the crown: the mask's whitest pixels stand at
  **318 blocks** - the height the city's massif tops out at - and every column between takes its share of
  the climb from the ground the heightmap draws up to that pin. Of the drawn land, 28.6% stands under the
  mask, the median climb there is 37 blocks, and 0.5% of it reaches the crown. Read at eight blocks to
  the pixel and interpolated, an eight-block step never enters the flank.
- **Rivers.** The mask says where the water is. The water's surface is the average height of the
  drawn ground over a square of ±64 blocks about the column, so it follows the fall of the country
  instead of standing at one level; the bed lies as deep under it as the channel is wide - a three-block
  river is three deep in the middle, a five-block one five deep, rising a block per block to the banks -
  and the column is cut down to it, sixteen blocks at the
  deepest: the mask paints its rivers as bands thirty blocks and more across, and a bed that deep would be
  a canyon. Measured over all 39,613 river columns of 16 blocks: water from 38 to 306, median 141, and within
  five blocks of its own ground on 88.4% of the channel, none of it above the crown.
- **Lakes.** The same rule with a ±192-block square, so a lake reads as the basin it sits in rather
  than as a plateau, and never below the sea's own height of 63. Bed six blocks under the surface.
  Measured over 21,346 columns: surface 63 to 285, median 67, at the sea's own height on 46% of them, bed 64 to 66.

Water is given to the generator as a per-column height (`Sample.water`), not as one constant, which is
what lets a river fall and a lake sit in its basin.

## Still open

- The Bruinen used to be the one river not laid by the mask, because a sixteen-block pixel cannot place
  a twelve-block channel. It now has its own bake at **three blocks to the pixel** over
  x 4200..5400 by z -4100..-2900, read before the coarse mask inside that window. The nearest water to
  the city's outlet (4900, -3322) is **4995, -3311**, ninety-five blocks east and running north-south at
  about 101 degrees; that is where the branch joins.

## Decisions worth keeping

- **The built city wins where it stands, and the masks answer everywhere else.** The vale and its massif
  are laid over the drawing, and a drawn river still cuts through both, because the branch out of
  Rivendell has to meet the drawn Bruinen in the open.
- **The mountain mask's brightness is height, and its whiteness is the pin at 318.** It reaches pure white
  and is scaled by its own brightest pixel, so a drawn summit stands at the crown the city's massif reaches
  and drawn rock and built rock meet the same sky. Leaving the ranges to the heightmap instead was tried
  and reverted: the masks say what the ranges are.
- **The river and lake masks are solid.** Where they are marked is where the water goes; the bake only
  records how much of each cell is marked, so that a bank can fall between cells instead of on the grid.
- **The final height mask is the locator.** It is what matches a build or a landmark to the Middle-earth
  map, and it also draws the ground's own shape, from the sea's 63 up to the 318 crown.
- **A drawn river is as deep as it is wide, to a cap of sixteen.** A three-block channel is three deep in
  the middle and a five-block one five; the cap exists because the mask paints bands thirty blocks and
  more across, and a bed as deep as a band is wide is a canyon.
- **Eight blocks to the pixel** is the bake's resolution. It costs about 28 MB of masks at load - fourteen
  of height, seven each for rivers and lakes - and keeps a ridge to the shape the map draws.

## Open questions

- **Band or channel?** The rivers mask paints bands a median 31 blocks across at the narrowest crossing. If
  only the core of a band is water, narrowing it would let the depth rule follow a true channel width.
- The river and lake surface censuses were taken at the sixteen-block bake and have not been re-measured at
  eight.
- Nothing has been looked at in a world since the eight-block rework. The seam where the drawn ranges meet
  the city's massif, and the join where the deeper drawn Bruinen meets the branch, are where an error is
  likeliest.
