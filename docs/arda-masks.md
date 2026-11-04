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

## The baked layers

`tools/arda_masks.py` crops the three masks to the world frame at **8 blocks to the pixel** — 3072 by
2304 — and writes them as `src/main/resources/assets/tolkienmobs/arda/`:

| file | size | marked |
| --- | --- | --- |
| `rivers.png` | 53 kB | 2.2% |
| `bruinen.png` | 1.4 kB | 4.3%, 400 x 400 at **3 blocks to the pixel** - the Bruinen's own corner |
| `lakes.png` | 11 kB | 1.2% |
| `mountains.png` | 363 kB | 23.8% above half brightness |
| `height.png` | 7.9 MB, sixteen-bit topography | sea 16.8% of the frame |

Run it from the mod root whenever the masks or `Reference/work/calibration.json` change. `ArdaMasks`
reads whatever is in the assets; a missing image leaves that mask empty and the drawing answers as it
did before.

The river and lake censuses below were taken at the sixteen-block bake; the rule they describe is the
same at eight, and the counts have not been re-measured.

## What the game does with them

- **The ground.** `height.png` is `Final Height.png` at eight blocks to the pixel, unsigned 16-bit, and
  it is the ground now: interpolated between its pixels from the sea's own 63 up to the crown at 318, so
  the country rolls where the map rolls. The Shire reads 116 where the drawing had 68, Rohan 170, Mordor's
  plateau 224, the Misty Mountains 178. A column the map calls sea keeps the drawing's bed, because the
  map's zero is the sea's height.
- **Mountains.** The heightmap already draws the ranges, so the range mask no longer stands on top of the
  ground: it used to lift every bright column toward **318 blocks**, which flattened the ridges the
  heightmap had shaped into a plateau at the ceiling. `mountains.png` is still baked and the probe still
  reads it, but the terrain follows the heights the map draws, and a range is the map's own shape.
- **Rivers.** The mask says where the water is. The water's surface is the average height of the
  drawn ground over a square of ±64 blocks about the column, so it follows the fall of the country
  instead of standing at one level; the bed is four blocks under it, and the column is cut down to the
  bed. Measured over all 39,613 river columns of 16 blocks: water from 38 to 306, median 141, and within
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
