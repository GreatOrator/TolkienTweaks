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

**Still pending:** nothing is set at the ford any more - the water's height along the course is read
off the ground, capped by the build's own outlet, and `BRUINEN_FORD_WATER` is gone. The ford's
coordinate is still the drawing's 3840, -3328.

## Verification

- `./gradlew compileJava` exit 0 (with `-x createMinecraftArtifacts` while the dev client is
  running) is the bar. The agent does not playtest; heights are checked by mirroring the
  generator's arithmetic in a script, never by generating a world.

## What the heightmap did to the valley

The ground is the reference topography now (see `docs/arda-masks.md`), and the valley floor is still the
hand-laid 74 - in a 384-tall save. The ground moves with the world's own ceiling: the generator reads it
and lifts the floor, the crown, the shoulder and the Bruinen's outlet together, so that the build's crown
stands one block under the sky and nothing else changes. A 512-tall world gets a lift of **128** - floor
**202**, crown **446**, outlet water **210** - and a 384-tall save gets **nothing at all**, which is why the
old save keeps the ground it was made with.

At 128 the country round the valley is 156, so the valley stands 46 above it and the Bruinen leaves
through 236 blocks of rock at the wall, easing to grade by the ford. At 0 the city sits 86 blocks down
inside its massif, as it did before.

Worth knowing before re-seating: the nearest column of the masked Bruinen to the drawn ford at 3840,
-3328 is **1,176 blocks away, at 5016, -3320** - 465 from the valley centre. So the mask does not put a
ford where the drawing does.

## What is next

- The reference masks in `reference/` are calibrated to the world (`reference/work/`,
  `calibration.json`): about **4 blocks per 8192-pixel**, isotropic, canvas origin at
  block **-7546, -13561**. Rivers, lakes and mountains can now be rebuilt from them, and the
  drawn rivers are known to be within half a cell on average and four cells on the worst.
  See `docs/arda-masks.md`.
- The ford should follow the masked course of the Bruinen once the rivers are rebuilt.
