# Rivendell - the build, the mountain and the water

> **The city's own ground is laid aside (`PLANTED = true` in `ArdaMap`), and the valley is pinned.** The
> dimension is the drawn height, rivers, lakes and mountains and nothing else, so the city stands on drawn
> ground: `Pins.SOURCE` puts `rivendell` at **5200, 75, -3370**, the cut's own low corner, so the city covers
> x 5200..5753 by z -3370..-2859 with its crown at 318. The valley floor at 74, the 318 rim, the shoulder
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

- Hand-built `Rivendell_Imladris`, raised by `/tmcity` on the pin, standing on the cut's low corner
  **5200, 75, -3370**: 584 pieces, 554 x 512 blocks (x 5200..5753, z -3370..-2859), bands from **y 75** to a
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
  kept as the shape that was measured, not as ground: no floor is generated any more (`PLANTED = true`),
  and with the city on the pin this profile sits 848 east and 463 south of where the city now stands. This is the shape the valley had at `82abe999`,
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
- The rim **stands at the crown from the first block outside the walls.** The floor is the build's own
  footprint (`VALLEY_HALF_X` 277, `VALLEY_HALF_Z` 256), so the rock begins where the build ends, holds the
  crown out to `VALLEY_RIM = 96` blocks past it - a square, not a ring, with `RIDGE_SWING` read at nothing -
  and then falls to the vale over `VALLEY_FALL = 200`. It used to hold the crown only out to 175, and then
  to stop the floor four blocks inside the walls, which left a strip of crown standing inside the build
  that read as a wall of its own at every face the build leaves open - the river's mouth and the spring.
- **The ground outside is dressed to the build once the last band is down.** The drape pass reads the
  cut's 584 templates rather than the world, takes the highest block of the build in each of the 554 by
  512 columns, and cuts or fills every column in a 32-block ring outside the footprint to its nearest
  wall column, easing back into the drawn country at the ring's edge. Water columns are left alone, so
  the mouth and the spring stay open. The outline is what decides the join, and it is not the crown:
  the west wall reads 82 at the outlet, 99 at z -3050, 141 at -3030 and 188 at -3010, so the ground
  beside the river comes down to the build's own roofline there instead of standing at 318 beside it.
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

- **The rock over a deck is the roofline.** The build's edge is not always a wall: over much of the east
  and west it is the floor's own tiles at 75, with nothing built above them, while the crown stands 243
  blocks higher over that deck inside the box. Reading the deck as the wall cut the ground outside down to
  75 and opened a trench round the city whose face was the mountain's own shape. A boundary column at or
  under `DECK_TOP = 76` now takes the ground inside the box as its height and is only ever filled, never
  cut: the mountain at a deck's back is the city's back wall.
## The water

- **A pool out in the ring is ground.** The drape left every wet column alone, on the reading that water at
  the rim is the city's channel - but the channel is answered before that test, so every pool and spring basin
  out in the ring was skipped while the columns round it were cut and filled, leaving a pillar of ground
  standing wherever one puddle reached the top of a column. A wet column that is not the channel is now read
  down to the ground under it, and the cut begins at the water's surface so the pool goes with the ground.
- **The drawn Bruinen is nearest the city's mouth at 5054, -3086**, a hundred and forty-seven blocks west of the build's
  outlet and running north-south at about 101 degrees, read from the mask's own resolution: the river
  layer is baked again at **three blocks to the pixel** over x 4200..5400 by z -4100..-2900
  (`bruinen.png`), because a sixteen-block pixel cannot place a twelve-block channel. There are no river
  pixels on the city's own footprint.
- **A drawn river answers before the massif and before the floor**: the Bruinen cuts its own channel
  through both, so the branch meets it in open water instead of running into a wall of massif at 5005.
- **The branch leaves Rivendell on the west and runs round the valley to the Bruinen.** The city's 584
  pieces carry 302,049 water blocks over 40,321 columns; the water stands at **5200, -3067** with its
  surface at **82**, and the only water on the east edge is an aqueduct at y 239. The branch was built from
  5748, -2859, which carries no water of the build's at all. Both are world columns with the city on the
  pin; the legs below were measured from the old planting at 4352, -3833, so they shift with it by 848
  east and 463 south, while the drawn water at 4995, -3311 is the map's own and stays put. Its course is
  four legs - out west of the wall to 5120, south to z -2793, east below the city to 5800, and in to the
  drawn water at **4995, -3311**:
  **1,104 blocks** in all, twelve wide with 5 left and 7 right of the middle. The ground is the vale's
  **74** the whole way, so the river falls the eight blocks from the city's water at the wall and then
  arrives level with the drawn river. The massif gives way to the course over `MOUTH_RUN = 96` blocks of
  climb, and not at a grade: the ground leaves the water almost level, takes most of its height across the
  middle of the mouth and eases into the crown at the shelf's own width, so the bank and the cone are one
  curve and there is no line where either stops applying. Two grades were drawn first and each was wrong
  in its own way: six tenths never reaches the crown inside 390 blocks and laid the cone over the whole
  south flank (the walls stood at 255 and the peaks read two hundred blocks out), and six put the crown
  back within forty but left the rock at 240 four blocks from ground at 100, because the branch's own
  bank opened the ground at one and a fifth and the cone cut it at six - a seam two hundred blocks high,
  which is the wall the save showed at x 5196 just outside the west wall.
- **One face is still to open.** The massif gives way to the branch only where the branch runs: the drawn
  Bruinen's own banks, where it runs 96 blocks east of the walls with no branch beside it, still stand at
  the crown to the water's edge, because a drawn river's distance is only read out to **64** blocks and a
  cone cut off at 64 leaves the step it was cut to remove.
- **The spring has a way out, and the shoulder could never have given it one.** The spring's water reaches
  the east wall over x 5708..5753 by z -3058..-3017 - read off the save it tops at **239**, with a lower
  basin at 184 and steps between - and the shelf outside it stands at **115**, a hundred and twenty-four
  blocks under the water. The apron never saw any of that: `rim` drops a wet column from the datum and
  `side` leaves it exactly as it was, so the ground outside a water column kept whatever height it had.
  `Sites.waterline` reads the water instead of the ground: every column of the footprint's edge that
  carries water is an outlet, and where that water stands more than `WATER_HANG` **twelve** blocks over
  the first dry ground outside it, the ground is built up into a bank under it - a bed one block below the
  water, falling **one block in one** until it meets the ground, or at `WATER_REACH` 160. Nothing is cut
  and no wet column is touched. `/tmcity apron` runs it after the shoulder. The mouth does nothing at all,
  hanging eight blocks over the vale's 74, which the branch falls on its own; the spring gets one bank for
  each of its **thirty-two** edge columns at 239, eighty-seven to a hundred and six columns long and down
  to the shelf at 115 - three thousand and ninety-two columns, about two hundred thousand blocks of dirt.
- **The branch is cut again, at the new mouth.** `BRANCH` is on and the course runs from 5200, -3067 to
  the drawn Bruinen at 5054, -3086 - a hundred and forty-seven blocks, the city's water at 82 at the wall
  falling to the vale's 74 at the join - so the map cuts a twelve-wide channel through the massif and opens
  the rock along it on the same curve, over `MOUTH_RUN` blocks of climb from the middle of the channel. The spring at the rear, water at 239 over a floor
  at 74, still has no cut of its own.
- **The ford is a bridge.** The join itself is still water and banks, but the Bruinen at the city's mouth is
  no longer crossed on foot: `/tmroad build` stands the shipped `bridge_end` and `bridge_middle` pieces over
  the fifty columns between the bank markers at 5055 and 5006 on z -3075, draws every pillar they carry down
  to the bed, and paints the road straight in and straight out along the span's own line.
- **The join's step is closed.** The branch falls from the city's water to the vale's 74 on its way out
  and the drawn Bruinen stands at 74 too, so the two meet at the same level; a step was possible while the
  branch was initialised under the floor.
- **The vale's own river.** The long west-running river is gone in favour of the south-east branch. If
  the fork should show both arms, the old course can come back beside the new one.
- **The drawn Bruinen's gorge** is a mask-width channel (16-32 blocks) cutting about 240 blocks of rock.
  Worth a look before deciding whether the massif should give way around it.
- **The 90-block climb** (`VALLEY_STEEP`) is the number that decides whether the mountain reads as a
  hillside meeting the walls or as a bowl: 45 is steeper, 150 gentler.

- **The spring is banked in, not drained out of the back.** Where a boundary column of the build carried
  water high over the floor - the spring's basin at 239, its lower pool at 143 - the drape left the ground
  outside alone entirely, and the water ran out of the wall and down the flank, cutting itself a false river
  behind the build with the ring's ground still being cut to the spring's roofline around it. Water at the
  edge more than `OUTLET_LIFT = 24` over the floor is now read as a spring held up in the build rather than
  its outflow: the outside ground is banked to the water's own surface and never cut, so the pool is held
  in. The city's own outflow, the branch eight blocks over the floor, is untouched and still runs out.
- **The river comes down from the city instead of falling off it.** The surface was read off the ground
  outright, and the ground under the outlet is the vale's 74 while the city's water stands at 82: the branch
  left the wall at 82, fell eight blocks at the first block of its course and ran level from there, which
  read as a waterfall at the city's own mouth. `BRUINEN_GRADE = 12` holds the fall to one block in twelve of
  run, so the surface leaves the city at 82 and meets the vale ninety-six blocks down the course; the channel
  and its walls follow the same level, because `mouthRise` and the bed are both read from it.
## How to look at it

- **The ring is thirty-two blocks deep.** `DRAPE_REACH` was doubled to sixty-four and is back to
  thirty-two: the pass settles the ground that far out before easing it to the drawn country, and at
  sixty-four the ring reached past the branch the river leaves the city by, so the pass overwrote the water
  and the river left the build. Thirty-two is the distance at which the settled ring still meets the drawn
  country at the outflow without touching it; the join at the wall is unchanged either way - the first block
  out is cut or filled to the build's own outline.

- **The rock holds its line at the city's back.** `giveWay` walks a hundred blocks in eight directions looking
  for drawn water and brings the ground down to whatever it finds, which is what opens the massif into a valley
  round a river. The drawn Bruinen runs past the valley's east side as well, and giving way to it there brought
  the whole eastern flank down to the river at 74 - a valley the city has no mouth into, cut into the back of
  the rock. Only water on the branch's own side of the valley now opens the massif, measured against the
  floor's middle: the valley drains west, so that is the one side the rock gives way to drawn water from, and
  the east stands at the crown until `VALLEY_FALL` hands it back to the drawn country.
    ./gradlew runClient
    /execute in tolkienmobs:arda run tp @s 5477 120 -3114     the city's middle
    /tp @s 5200 92 -3067                                     the branch's mouth
    /tp @s 4995 92 -3311                                     where it meets the Bruinen
    /tmcity origin        in creative, standing on the floor: plants the city
    /tmcity band 8        repeat; /tmcity summon does all of it in one word
    /tmcity goto

- **The mountain across the Bruinen is a bank, not a wall.** The ground beside drawn water climbs on
  `mouthRise` over `MOUTH_RUN` blocks, and that run was ninety-six: from the Bruinen at 74 the massif's west
  flank stood at 319 within a hundred blocks of the water, which reads across the river as a wall rather than
  as the valley's own side. `MOUTH_RUN` is a hundred and sixty now, and `WATER_REACH` a hundred and
  seventy-six so the walk still passes the climb: the bank leaves the water at 74 and keeps climbing, standing
  about 180 sixty-five blocks out where it used to stand near 270, and reaching the crown only well beyond the
  river.
- **The far bank was measured against a pin, and left standing.** The user's pins put a knoll at
  4963, 128, -3145 under its spruce canopy, the drawn Bruinen at 74 running x 5013..5063 fifty blocks west
  of it, and the opposite bank at 5061, 75, -3136. Between them the ground climbs 74 to 135 over those fifty
  blocks and 80 to 155 over the sixty-four east of the water. `mouthRise` over `MOUTH_RUN` 160 allows 131 at
  the knoll's run, so the knoll stands three blocks *under* the curve the bank is drawn on rather than above
  it: it is the shape the curve asks for, made by the drawn country, and `giveWay` has nothing to cut.
  Shortening the run would raise that ceiling rather than lower it, and lengthening it to where it would
  reach the knoll - four hundred - drops the massif's own west skirt to about 117 by x 5120, leaving the
  drape to fill a two-hundred-block cliff thirty-two blocks from the wall. The far bank is left as it is.

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
