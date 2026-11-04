# Rivendell - the build, the mountain and the water

A state note, last written 2026-11-04. It records what the generator does now, what the map says, and
what is still open, so the next session does not have to rebuild that from the commit log.

## What the map says

- The reference map is `middleearth.jpeg` at the mod's root, 10000 x 5455. The area that matters is
  cropped to `docs/map-rivendell.jpg` (regenerated with `python -c` on the jpeg, box 3900,900..6100,2500).
- **Rivendell** sits in the foothills on the western side of the Misty Mountains, with the High Pass
  east of it and the Trollshaws west.
- **The Bruinen (Loudwater)** rises in the Misty Mountains **north of Rivendell**, flows south past the
  valley and then south-west to join the **Mitheithel (Hoarwell)**; the **Last Bridge** stands on the
  East-West Road down there. It is the named river, and the one the drawn river mask carries past the
  city.
- **The Ford** is just south-east of Rivendell, where the **Old Forest Road** crosses the Bruinen - a
  bridge over the river on the map's dotted road, not a shallow in a stream.
- **The stream that leaves Rivendell has no name on the map.** It is the valley's own branch: it comes
  out of Imladris and joins the Bruinen. Where the map shows a fork, the lower arm is this branch, which
  is why the branch runs east-south-east to the drawn river and not west to nowhere.

## The city

- Hand-built `Rivendell_Imladris`, planted by `/tmcity` at **4352, 75, -3833**: 584 pieces, 554 x 512
  blocks (x 4352..4905, z -3833..-3322), bands from **y 75** to a crown of **318**.
- The build carries **no water blocks at all** - 0 of its 584 pieces contain `minecraft:water` - so every
  river in and around the valley is the generator's, and the water's route is ours to choose.
- The cut it is planted from lives in `run/rivendell_art` (`rivendell.parts`, `_laid.txt`, 584 pieces).

## The world

- Arda is **384 blocks tall** (-64..319), the overworld's own height, so the city's crown at 318 stands
  one block under the sky (`d8faf61f`).
- `ArdaMap.liftTo(ceiling)` still exists for taller worlds: it raises the floor, the crown and the water
  together by whatever room the ceiling gives. At 384 it is **0**.

## The valley

- Floor **74**, and it is **544 by 502** centred on 4628, -3577: x 4356..4900, z -3828..-3326, which is
  four to five blocks *inside* the 554 by 512 footprint. This is the shape the valley had at `82abe999`,
  and the one that reads as a straight wall: the rock stands at the **crown, 318**, from the floor's own
  edge outward, so the city is embedded in the mountain rather than set down beside it and there is no
  strip of ground between the two to be a seam. The build's base band is flush with its footprint on all
  four sides (measured from the 102 edge pieces), which is why a floor cut to the footprint and a shelf
  in front of it both left a gap.
- What changed it, and why it is back: `fef98351` made the climb start at the floor over
  `VALLEY_STEEP = 90`, which put a bank of terraces a hundred blocks from the city instead of a wall at
  its walls. The climb from the crown is restored and `VALLEY_STEEP` is gone.
- The rim **climbs from the lawn's edge** to the crown over `VALLEY_STEEP = 90` blocks (74, 80, 90, 106,
  138, 168, 212, 253, 318), holds the crown out to `VALLEY_RIM = 175`, then falls to the vale over
  `VALLEY_FALL = 340`.
- Past the massif's foot the country is blended toward the city's water level over `VALLEY_VALE = 900`
  blocks, settling back to the heightmap's 156-193 by **1,415** blocks out, so the foot itself stands on
  **74**, the city's own floor. It used to stand on 82, eight above the floor, which put the country's
  lowest ground higher than the water in the valley: a river could not leave, because the water in the
  floor was already below everything it had to reach.
- The north-east corner is plain floor now: the `shoulder()` shelf that stood rock at 318 inside the
  build's own empty gap is gone. It existed to fill a corner the mountain had cut, but a shelf at the
  crown inside the city is rock where the city has none, and with a lawn the mountain belongs a hundred
  blocks out.
- The mountain mask's ridge is sixteen blocks wide, and the vale's blend runs **after** the mask, so a
  lifted ridge cannot stand as a blade inside the valley (`51823803`).

## The water

- **The drawn Bruinen is nearest the valley at 4995, -3311**, ninety-five blocks east of the build's
  outlet and running north-south at about 101 degrees, read from the mask's own resolution: the river
  layer is baked again at **three blocks to the pixel** over x 4200..5400 by z -4100..-2900
  (`bruinen.png`), because a sixteen-block pixel cannot place a twelve-block channel. There are no river
  pixels on the city's own footprint.
- **A drawn river answers before the massif and before the floor**: the Bruinen cuts its own channel
  through both, so the branch meets it in open water instead of running into a wall of massif at 5005.
- **The branch out of Rivendell** runs from the build's own outlet at **4900, -3322** to the drawn
  Bruinen at **4995, -3311**: 96 blocks at **72**, two blocks under the valley's floor, twelve wide
  (5 left of the middle, 7 right), 2+ blocks of water the whole way, 7 at the middle, narrowing to 0.7 of
  its width into the join. Its course **wanders** up to 18 blocks off the straight line
  (`BRUINEN_SNAKE`, one and a half turns) and is exactly at the outlet and the join at both ends, so it
  leaves the city where the build opens and arrives on the drawn river. With the rock at the crown
  outside the floor, that course is a gorge through the massif - the gap in the wall the reference image
  shows - and it is cut by `bruinen()` wherever it runs.
- Where the two rivers meet there is a **two-block step**, 72 against the drawn river's 74, because the
  drawn river takes its level from the ground it is drawn on.
- The earlier course - 4356, -3528 west to the drawing's 3840, -3328 - **ended 1,176 blocks from any
  drawn river**: the "drawn ford" there is not on drawn water. That vale river is gone for now; whether
  both arms should stand is open (below).
- Both river kinds have a **bed that slopes from its middle out to the water's edge** (`2bbdcad2`), and
  drawn rivers take their depth from the drawing - eight probes at 6 and 12 blocks, full depth once
  three quarters are water.

## Open

- **The ford is a bridge.** Nothing places a road or a bridge over the Bruinen yet; the join is water and
  banks only.
- **The join's step.** The branch runs at 72 and the drawn Bruinen at whatever grade the drawing gives it;
  a 2 to 18 block step at the meeting point is possible until the drawn river is taught the same level.
- **The vale's own river.** The long west-running river is gone in favour of the south-east branch. If
  the fork should show both arms, the old course can come back beside the new one.
- **The drawn Bruinen's gorge** is a mask-width channel (16-32 blocks) cutting about 240 blocks of rock.
  Worth a look before deciding whether the massif should give way around it.
- **The 90-block climb** (`VALLEY_STEEP`) is the number that decides whether the mountain reads as a
  hillside meeting the walls or as a bowl: 45 is steeper, 150 gentler.

## How to look at it

    ./gradlew runClient
    /execute in tolkienmobs:arda run tp @s 4628 120 -3577     the valley's middle
    /tp @s 4900 100 -3322                                    the branch's start
    /tp @s 5008 100 -3328                                    where it meets the Bruinen
    /tmcity origin        in creative, standing on the floor: plants the city
    /tmcity band 8        repeat; /tmcity summon does all of it in one word
    /tmcity goto

## Commits behind this note (on main, none pushed)

`7a78a5ec` the lift, `994f905b` the river profile read downstream, `c2f994e7` the vale,
`e3482aa8` the heights visible to chunk threads, `d8faf61f` Arda back to 384, `7d35b2fb` the floor past
the walls, `51823803` the mask out of the vale and the floor on the build, `fef98351` the rim climbs,
`2bbdcad2` the sloping river beds, `177f117e` two blocks of water at the join, `3f920f09` the branch
meets the Bruinen.

## Verification

- `./gradlew compileJava` (with `-x createMinecraftArtifacts` while a dev client runs) exiting 0 is the
  bar. The agent does not playtest: heights are checked by mirroring the generator's arithmetic in a
  script, and anything only visible in game is named as unverified rather than claimed as tested.
