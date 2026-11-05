# Rivendell - the build, the mountain and the water

> **The city's own ground is laid aside (`PLANTED = false` in `ArdaMap`), and the valley is pinned.** The
> dimension is the drawn height, rivers, lakes and mountains and nothing else, so the city stands on drawn
> ground: `Pins.SOURCE` puts `rivendell` at **5097, 75, -3010**, the middle of its own 554 by 512 footprint,
> with `/tmcity` centring the city on that pin, so the cut's low corner is **4820, 75, -3266**. The valley floor at 74, the 318 rim, the shoulder
> and the branch described below are the shape the city would be laid into - still in the source, behind one
> switch, and no longer generated.
>
> The campaign no longer lays the old square kit. A visit near the valley reads where `/tmcity` planted the
> city, stands the build's own marks in it, and leaves the Elves on the pinned ground until a city is there.

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

- Hand-built `Rivendell_Imladris`, raised by `/tmcity` on the pin and standing on the cut's low corner
  **4820, 75, -3266**: 584 pieces, 554 x 512 blocks (x 4820..5373, z -3266..-2755), bands from **y 75** to a
  crown of **318**.
- The build carries its **own water** - 763 `minecraft:water` blocks across 306 of its 584 pieces, not the
  none an earlier note claimed - which is the pool and channel inside the city. The ground round it is the
  generator's, so the two meet at the build's own mouth and the map does not get to choose where the city's
  water is.
- The cut it is planted from lives in `run/rivendell_art` (`rivendell.parts`, `_laid.txt`, 584 pieces).

## The world

- Arda is **384 blocks tall** (-64..319), the overworld's own height, so the city's crown at 318 stands
  one block under the sky (`d8faf61f`).
- `ArdaMap.liftTo(ceiling)` still exists for taller worlds: it raises the floor, the crown and the water
  together by whatever room the ceiling gives. At 384 it is **0**.

## The valley

- Floor **74**, and it is **544 by 502** centred on 4628, -3577: x 4356..4900, z -3828..-3326, which is
  four to five blocks *inside* the 554 by 512 footprint. Those columns are the pre-pin planting's and are
  kept as the shape that was measured, not as ground: no floor is generated any more (`PLANTED = false`),
  and with the city on the pin this profile sits 468 east and 567 north of where the city now stands. This is the shape the valley had at `82abe999`,
  and the one that reads as a straight wall: the rock stands at the **crown, 318**, from the floor's own
  edge outward, so the city is embedded in the mountain rather than set down beside it and there is no
  strip of ground between the two to be a seam. The build's base band is flush with its footprint on all
  four sides (measured from the 102 edge pieces), which is why a floor cut to the footprint and a shelf
  in front of it both left a gap.
- What changed it, and why it is back: `fef98351` made the climb start at the floor over
  `VALLEY_STEEP = 90`, which put a bank of terraces a hundred blocks from the city instead of a wall at
  its walls. The climb from the crown is restored and `VALLEY_STEEP` is gone.
- **The river's corridor is open again.** Within 24 blocks of the branch's own bent course the floor runs out
  and the climb fades back in (`smooth` of the distance over 24), so the water leaves the city between two
  slopes instead of through a notch in a wall. It was built in `1e3149f0`, softened in `c9d9f72f` and
  measured to the nearest point of the course in `3ac048cc`, then removed with the profile in `7ecabee0`;
  it is back beside the wall profile, which stays flush wherever the river does not run.
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
- **The branch leaves Rivendell on the west and runs round the valley to the Bruinen.** The city's 584
  pieces carry 302,049 water blocks over 40,321 columns; the water stands at **4820, -2963** with its
  surface at **82**, and the only water on the east edge is an aqueduct at y 239. The branch was built from
  5368, -2755, which carries no water of the build's at all. Both are world columns with the city on the
  pin; the legs below were measured from the old planting at 4352, -3833, so they shift with it by 468
  east and 567 north, while the drawn water at 4995, -3311 is the map's own and stays put. Its course is
  four legs - out west of the wall to 4740, south to z -2689, east below the city to 5420, and in to the
  drawn water at **4995, -3311**:
  **1,104 blocks** in all, twelve wide with 5 left and 7 right of the middle. The ground is the vale's
  **74** the whole way, so the river falls the eight blocks from the city's water at the wall and then
  arrives level with the drawn river. The massif gives way to the course at **0.6 blocks of height per block
  out** (about thirty degrees) over the whole bank, so the water leaves through a valley mouth rather than a
  slot, and the drop at the wall is the only fall on the way.
- **Two faces are still to open.** The massif gives way to the branch only where the branch runs: the drawn
  Bruinen's own banks, where it runs 96 blocks east of the walls with no branch beside it, still stand at
  the crown to the water's edge, because a drawn river's distance is only read out to **64** blocks and a
  cone cut off at 64 leaves the step it was cut to remove. And the city's spring - water at up to **317**
  in the build's own upper levels, ending at the east wall over x 5328..5373, z -2954..-2913 - has no course
  out of the build yet, so the ground outside it has nothing to slope to.
- **The ford is a bridge.** Nothing places a road or a bridge over the Bruinen yet; the join is water and
  banks only.
- **The join's step is closed.** The branch falls from the city's water to the vale's 74 on its way out
  and the drawn Bruinen stands at 74 too, so the two meet at the same level; a step was possible while the
  branch was initialised under the floor.
- **The vale's own river.** The long west-running river is gone in favour of the south-east branch. If
  the fork should show both arms, the old course can come back beside the new one.
- **The drawn Bruinen's gorge** is a mask-width channel (16-32 blocks) cutting about 240 blocks of rock.
  Worth a look before deciding whether the massif should give way around it.
- **The 90-block climb** (`VALLEY_STEEP`) is the number that decides whether the mountain reads as a
  hillside meeting the walls or as a bowl: 45 is steeper, 150 gentler.

## How to look at it

- **A world is only as new as its chunks.** Terrain is generated once per chunk, so a save written before
  a bake shows the old ground however new the code is. The test worlds are deleted after each round, which
  is what keeps that honest - so a screenshot leaves no save behind to read back, and the F3 coordinates on
  it are the whole record. There is no save on disk newer than 3 November because the 4 November worlds
  were removed after testing, not because the test was old.

    ./gradlew runClient
    /execute in tolkienmobs:arda run tp @s 5097 120 -3010     the pin, the valley's middle
    /tp @s 4820 92 -2963                                     the branch's mouth
    /tp @s 4995 92 -3311                                     where it meets the Bruinen
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
