# Rivendell — where the build stands

A state note, written 2026-11-04, so the next session does not have to reconstruct it from
the commit log.

## The city

- Hand-built `Rivendell_Imladris`, planted by `/tmcity` at **4352, -3833**, footprint
  **554 x 512** blocks (x 4352..4905, z -3833..-3322), bands from **y 75** to a crown of
  **318**. Planting reports "City planted at ...; 6 bands, 584 pieces".
- The lowest band is laid into the ground; there is no apron. `/tmcity apron` still exists for
  a city on open ground, and is not run by planting.

## The mountain

- The valley override in `ArdaMap` is a flat floor at **74**, **544 x 502** blocks, centred on
  **4628, -3577** — the middle of the city, so the floor ends 4 to 5 blocks inside the
  footprint on every side.
- Outside the floor the rock stands at the city's own crown, **318**, and climbs from there to
  a ridge at **380 ± 45** over **175** blocks, then falls back into the drawn country over
  **400**. The climb is steepest at the foot, so the rock rises out of the roofline rather than
  out of a field.
- The north-east corner is not floor: `shoulder()` stands a shelf at 318 in the build's own gap
  (x 4797..4905 by z -3833..-3774) and runs it down to the floor over 110 blocks. That corner
  is what the build was cut into.
- The world is **512** tall (-64..447 usable), so the ridge is not cut off at the ceiling. A
  save made before Arda was raised keeps a 384-tall world and the generator says so once
  instead of flattening the summits silently.

## The Bruinen

- The build's channel leaves the west wall at **4356, y 82, -3528**, with its bed **7** blocks
  under that (measured at 4356, 75, -3529), **12 blocks wide, 5 to one side of the course and
  7 to the other, including the block the reading was taken on**.
- The river is cut as its own gorge from that outlet to the ford the drawing pins at
  **3840, -3328** (cell 17,13, Trollshaws). The water falls **82 -> 63** along the run, the
  bed lies 7 under it, the walls climb **1.2 blocks for each block out** (about 50 degrees),
  ribs break the walls up so they are not contours, and the channel shallows into the crossing
  over the last fifth.
- The cut is applied after both the valley and the drawn ground, so it opens the massif's
  flank, the corner shelf and the country beyond the ridge in one pass.
- **Still pending from the player:** the ford's real coordinate and the height its water stands
  at. Both are single constants (`BRUINEN_FORD_X`/`Z`, `BRUINEN_FORD_WATER`); the water level
  is currently 63, the height the drawing's own rivers stand at.

## Verification

- `./gradlew compileJava` exit 0 (with `-x createMinecraftArtifacts` while the dev client is
  running) is the bar. The agent does not playtest; heights are checked by mirroring the
  generator's arithmetic in a script, never by generating a world.

## What the heightmap did to the valley

The ground is the reference topography now (see `docs/arda-masks.md`), and the valley floor is still the
hand-laid 74. The map puts the country round Imladris at **160**, so the city now sits **86 blocks down**
inside its massif rather than at the foot of it, and the Bruinen's gorge - still falling to the drawn
ford at water 63 - is **93 blocks deep** where it ends. Nothing about the build itself has moved; the
land around it rose.

Re-seating is the next job: raise the city's own base to the heightmap's floor, drop the valley override
to match, and re-cut the gorge from the new outlet to the masked Bruinen.

Worth knowing before that: the nearest column of the masked Bruinen to the drawn ford at 3840, -3328 is
**1,176 blocks away, at 5016, -3320** - 465 from the valley centre. So the mask does not put a ford where
the drawing does, and adopting the mask's course moves the crossing about a kilometre east of it.

## What is next

- The reference masks in `reference/` are calibrated to the world (`reference/work/`,
  `calibration.json`): about **4 blocks per 8192-pixel**, isotropic, canvas origin at
  block **-7546, -13561**. Rivers, lakes and mountains can now be rebuilt from them, and the
  drawn rivers are known to be within half a cell on average and four cells on the worst.
  See `docs/arda-masks.md`.
- The ford should follow the masked course of the Bruinen once the rivers are rebuilt.
