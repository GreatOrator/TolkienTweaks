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

## The baked layers

`tools/arda_masks.py` crops the three masks to the world frame at **16 blocks to the pixel** — 1536 by
1152 — and writes them as `src/main/resources/assets/tolkienmobs/arda/`:

| file | size | marked |
| --- | --- | --- |
| `rivers.png` | 23 kB | 2.24% |
| `lakes.png` | 4 kB | 1.21% |
| `mountains.png` | 132 kB | 8.1% above half brightness |

Run it from the mod root whenever the masks or `reference/work/calibration.json` change. `ArdaMasks`
reads whatever is in the assets; a missing image leaves that mask empty and the drawing answers as it
did before.

## What the game does with them

- **Mountains.** Brightness is height: white is a crest, black is a plain. Each column's brightness
  lifts the drawn ground toward **318 blocks** - the crown the city's massif reaches - smoothly, because the mask is blurred. The drawing
  still decides what the country is and what its ground sits at; the mask decides how high it stands.
  The drawing may raise ground to **420** now, which leaves the 512-tall world's ceiling room.
- **Rivers.** The mask says where the water is. The water's surface is the average height of the
  drawn ground over a square of ±64 blocks about the column, so it follows the fall of the country
  instead of standing at one level; the bed is four blocks under it, and the column is cut down to the
  bed. Measured over all 39,613 river columns of 16 blocks: water from 38 to 297, median 72, and within
  five blocks of its own ground on 86.2% of the channel, none of it above the crown.
- **Lakes.** The same rule with a ±192-block square, so a lake reads as the basin it sits in rather
  than as a plateau, and never below the sea's own height of 63. Bed six blocks under the surface.
  Measured over 21,346 columns: surface 70 to 283, median 72, bed 64 to 66.

Water is given to the generator as a per-column height (`Sample.water`), not as one constant, which is
what lets a river fall and a lake sit in its basin.

## Still open

- The heightmap's own absolute heights are not used yet: the world's ground still comes from the
  drawing, with the mask as relief on top. Reading `Final Height.png` as the ground itself would rebuild
  the whole map's topography, and would have to be reconciled with the hand-built Rivendell valley, whose
  floor is at 74.
- The Bruinen's gorge still runs to the drawn ford at 63. That is where the mask's Bruinen is not, and
  the ford's real coordinate and water level are still pending from the player.
