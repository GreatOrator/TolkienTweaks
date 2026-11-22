# Bree - what to look at in game

Bree is sited, its hillside is generated, and the pass that raises the town is written. **None of it has been seen in a
world yet:** there is no `bree` cut, nothing has been raised, and everything below is what to look at once there is.
Read it as a walk with a verdict at each stop, and with the line to change under each verdict that comes back wrong.

## Where it stands

| the thing | where | how high |
| --- | --- | --- |
| the cut's corner (the marker course) | x 1024, z -256 | 117 |
| the footprint | x 1024 to 1519, z -256 to 415 | 496 by 672 |
| the town's own grass line, and the drape's deck | over the whole town | 122 |
| the rock's foot, four blocks inside the footprint | x 1515 to 1519 | 171 to 221, the build's own edge |
| the flank, and where it meets the country | x 1515 eastward | 220 at 1515, 30, meeting the country at x 1545 to 1607 |
| the drape's ring | x 992 to 1551, z -288 to 447 | eased into the country 32 out |
| the drawn stream | x 1565 to 1647, z 300 to 416 | 99 to 101, the drawing's own |

Two numbers matter more than the rest. The town's **floor is 122**: seven blocks of the ground under the box stand over
it (the survey read 101 to 130) and the rest under, so the town is seated on the mean rather than on either end. And the
**stream is 14 blocks clear of the drape's ring and 46 clear of the box**: nothing either pass does should reach it, and
the rock is told never to stand on a drawn water column.

## Raising it

```
/tmcut list                      what this world is holding - "bree" once the cut is in
/tmcut table bree                its size, and whether its table reads aright
/bree raise                      raise, clear and dress the town now
/bree drape                      dress the ring again, and lay nothing of the town
```

A world with the cut in it raises Bree on its own, either at a start after 200 ticks or as soon as a player comes within
192 blocks of the footprint. The log says so at every stage: the pieces and chunks to wait for, then `the town stands at
1024,117,-256 in N ticks (M s), dressed to its own outline`. That line, and `<save>/tolkienmobs/cuts/bree/_stands.txt`
beside it, is the pass finishing. Anything short of that line means it stopped part way - `/bree raise` starts it again,
and a town that was interrupted re-lays from the beginning, because the note is only written at the end.

**`/bree drape` is the last stage on its own.** It reads the cut's outline and dresses the ring at the town's own deck
and with its trees out of its shape - the same numbers the raise uses - then writes the note, and lays no piece of the
town and clears no country out of it. It is the pass for ground taken out and made again, or for a changed drape rule,
on a town that stands and whose insides a hand has touched since. It also replaces a raise the world has not begun yet
(the world's own waits 200 ticks and a player within 192 blocks), so it can be typed at a start. `/tmcut drape bree`
is *not* that pass: a cut's table with no `deck` line dresses to the city's deck of 2, and the town's floor stands 5
over its marker course, so its own paving would be read as a roofline and cut.

## 1. Does the hill melt into the build?

**Stand on the build's own hill first**, at the top of the cut's eastern third, and look east. What good looks like:

- **No face.** The build's hill does not end in a wall at x 1519. Its last few columns stand on generated rock, and the
  ground east of them carries on down at the hill's own angle - a block of fall for a block of run - until it eases into
  the drawn country. There is a place where the stone under your feet changes from the cut's own blocks to the world's;
  it should be a change of material on a slope, never a step.
- **The crest is your own height.** Walking the build's hilltop eastward, the generated crest should meet you about
  level (the build tops out at 121 in the cut, 224 in the world; the rock peaks at 220, four under it), not tower
  over the town and not fall away from it.
- **Nothing over the town.** From the crest, looking west, no rock stands over the town's streets or roofs. The rock
  begins at x 1515 and only outside the footprint's east edge; the four blocks it reaches in are covered by the cut's
  own columns laid after it.
- **The ends turn.** At z -256 and z 415 the flank should turn down into the country over about 32 blocks, not end in
  two square faces; walk out past each corner and back to see the turn.
- **The flank is broken, not a cone.** The slope carries the map's own folded noise, climbing in from the foot - ridges
  and hollows of a few blocks, finer than the hill's own shape.

| what you see | what it means | what to change |
| --- | --- | --- |
| A square cliff at x 1519, the cut's edge showing as a face | the rock's foot is not under the build: `BREE_FOOT` (4) is short for this cut | `BREE_FOOT` 4 to 8 - the foot then stands eight blocks inside, and eight more columns of the cut are laid over it |
| A lip of rock standing in the town's own hillside, inside x 1519 | the rock's top is above the build's: `BREE_EDGE_TOPS` are reading a height the cut does not carry there | re-read the table from the cut - the top of the build's east edge every 32 rows of its depth, in the cut's own height |
| The hill is a wall, or a cone with no shoulders | the flank's angle, and the noise that breaks it | `BREE_FALL` (1 is 45 degrees; 2 is a half block of fall per block of run) and `BREE_CRAG` (8 to 12), `BREE_CRAG_SCALE` (64 to 40 for sharper ridges). Nothing steeper than one block for one without changing the sum itself |
| The hill ends in two faces at the north and south | `BREE_WRAP` (32) is too short for this hill's height | 32 to 48 |
| The hill runs on into the drawn ground past where it meets it | it cannot: the rock stops the moment the country stands as high as it | nothing - the stop is x 1545 to 1607, measured |
| A bench or step 4 to 14 blocks proud, hugging the box's east edge for its first 30 blocks | the drape's fill over the rock. Measured: the ring stands up to 14 over the hill (worst at z -168), 9 to 13 over most of the east side, and nothing at 32 out | Look before touching it: it is a shoulder on a hundred-block flank. If it reads as a shelf, it is a change in `Dressing` - a ring column already standing at or over its cap should be left alone - and that is the drape the city and Hobbiton share, so both need looking at again |
| A stone wedge climbing to a roofline somewhere on the perimeter | a building stands on the cut's perimeter, and the ring fills up to its roof - the city's rule, wrong for a town on a plain | the same change, or tell `Dressing` the town's roofs are not its walls the way `trees` says a village's leaves are not |
| **Plates of bare stone standing in the air over the ring's trees**, air under the stone and the leaves still under that | the drape read the crown as ground: it stepped down from the leaves' top into the air a big crown hangs over, took that air for the column's skin, and filled from there, so its stone began above the ground and its top block had no ground to be. Found in `Progress Check 1`: the south band (x 1000..1200, z 416..447) held 1,609 stone-topped columns, **483 of them with air under the stone**, and a slice of the box's interior, which the drape never touches (x 1024..1080, z 340..419), held none | fixed on 22 November in `Dressing.dressColumn` and `openMouth`: the ground under a crown is read with `MOTION_BLOCKING_NO_LEAVES`, only a trunk is walked down from there, and a column with nothing solid under it is left exactly as it stands. **Ground already dressed keeps its plates** - a re-dress reads the plate as ground - so the fix is seen only in chunks generated since, or after a repair |

## 2. Is the stream untouched?

**Walk the stream's west bank**, from where it enters at z 300 southward to where it leaves at z 416, and then look back
west at the hill. What good looks like:

- **The water is the drawing's.** Same level as upstream and downstream of the town, no pool held up, no dry bed under
  stone, no shelf of water stepping down.
- **The bank may be steep, the water may not move.** Where the hill's flank reaches the stream's edge, the bank can
  stand up to nine blocks over the water; that is the hill meeting a stream, not a fault. What is a fault is the water
  itself displaced.
- **The channel is a channel.** A block or more of water across its own width, running the way it ran before, with the
  hill's rock stopping at the west bank and nothing laid on the east one.
- **The ring does not reach it.** The drape ends at x 1551 and the stream starts at 1565; the measured band x 1520 to
  1551 holds no drawn water at all. If the ring is doing anything to the bank there, the reach is wrong, not the stream.

| what you see | what it means | what to change |
| --- | --- | --- |
| The channel filled, water gone, a stony bed | the rock stood on a drawn water column: the water guard is not in the build you are running, or these chunks were generated before it was added | rebuild the jar, then regenerate those chunks - ground already generated keeps what it had, so a world made before the fix needs the region re-made or a fresh world |
| A bar of stone across the stream, water on both sides of it | the same, with the guard reading a mask that does not call that column water | check it against the drawn water at that z; a bar that is the drape's stone rather than rock means the ring reached further than 32, so `CityCommands.DRAPE_REACH` 32 to 24 |
| The stream at a different level than the reaches either side | the hill's rock stood in the channel, and the river pass then carved to the rock's height rather than the drawing's | the guard first; the level follows it |
| The hill's foot hanging over the water | expected where the flank meets the bank - up to nine blocks of bank | nothing |
| Water inside the town | the build's own, which by your rule is not the ground's business | nothing |

## 3. Did the drape work?

**Walk the whole ring at 32 blocks out** - x 992 and x 1551, z -288 and z 447 - and look back at the town from each
side. What good looks like:

- **No trench.** At the town's open faces - streets, gates, the plain's grass edge - the ground outside meets the paving
  at its own level. The deck rule is what does this: a column whose top is the town's floor (122) is a floor the country
  meets, never a roofline to be cut down to.
- **No wall.** Where the drawn country stands more than three blocks over the floor - the north and east sides, where
  the survey read up to 130 - it is cut back to the join and eased into the country over the 32 blocks. A bank, not
  a cliff.
- **The join is the build's shape at the wall and the country's at 32 out.** Walk out from the perimeter: the ground
  should leave the town's own line and be the drawn country again by the time you are 32 blocks out. No shelf where the
  two meet.
- **Trees are ground, not shape.** The ring takes trees out and meets the ground under the crown; no mats of leaves, no
  spike of stone under each crown.
- **The stream is not touched** - see above; the ring ends 14 blocks short of it.

| what you see | what it means | what to change |
| --- | --- | --- |
| A trench along an open face | the deck is reading too low, so the town's floor was taken for a roofline | `BREE_DECK_LIFT` (5) - the number `Dressing` reads as the town's floor, and the one the fallback table in the cut is not needed for |
| A wall of raw ground standing over the paving | the bank rule is too shallow for this country | `BANK_BLOCKS` 3 to 6 - the height the country has to stand over the floor before the ring cuts it |
| A shelf where the ring meets the country, 32 out | the ring is too short for the drop it has to carry | `CityCommands.DRAPE_REACH` 32 to 48 - shared with the city and Hobbiton, so both are dressed again to it |
| Leaf mats, or stone under each crown | the cut's trees were read as its shape | `Bree` passes `trees` true to `Dressing.begin`; if that is what you see, the flag did not reach |
| No ring at all - the drawn hill still raw against the perimeter | the pass stopped before its last stage, which is the dress, or the note says the town is finished when it is not | `/bree raise` again; the note is only written when the whole pass is done |
| The whole town rises again on each start, or on `/bree raise` | a probe column moved more than `PROBE_SLACK` (6) - a road, a ditch, a tree felled at one of the seven | move that probe in `PROBE`/`PROBES`, or delete `<save>/tolkienmobs/cuts/bree/_stands.txt` and raise once |
| The ring's rules changed but a world keeps the old ring | the note records `DRESS_VERSION` | 1 to 2, which makes a world that holds the town dress it again |
| A town waterway reaches the perimeter and the ring opens it | the drape's outflow rule, the city's own: the mouth is taken out and the water laid back over the bed | nothing - unless the town's water is not supposed to leave it, in which case the channel is at the outline's top and the answer is the cut, not the drape |

## The numbers, and which sum each feeds

| the number | now | what it does | what moves if it moves |
| --- | --- | --- | --- |
| `BREE_X`, `BREE_Y`, `BREE_Z` | 1024, 117, -256 | where the cut's corner is planted | the whole town, its hill's foot and the drape |
| `BREE_DECK_LIFT` | 5 | the floor over the marker course: grass line 122 | the seat, and the drape's floor |
| `BREE_BASE` | 14 | the cut's marker course, for reading its heights | the hill's foot height |
| `BREE_FOOT` | 4 | how far the rock reaches into the footprint | the join at the build's east edge |
| `BREE_FALL` | 1 | blocks of run per block of fall | the flank's angle; larger is gentler |
| `BREE_WRAP` | 32 | how far the crest carries past the ends | the corners |
| `BREE_CRAG`, `BREE_CRAG_SCALE` | 8, 64 | the noise on the flank | whether it reads as a hillside |
| `BREE_EDGE_ZS`, `BREE_EDGE_TOPS` | 22 rows, 68 to 118 | the build's own east edge, read from the save | the crest along the whole flank |
| `PROBE_SLACK`, `PROBES` | 6, seven columns | when the world decides the town is gone | whether a town re-raises itself |
| `DRESS_VERSION` | 1 | what the drape's rules were when it was written | whether a held world dresses again |
| `CityCommands.DRAPE_REACH` | 32 | how far out the ring dresses | the ring, city and Shire both |

## What has been seen, and what has not

**Raised and walked on 22 November.** The cut went into `Progress Check 1`, the town stood at 1024, 117, -256 in 935
ticks (46.8 s) - 462 pieces over 1,610 chunks - and the owner walked it and reported the build in. What came back was
the ring and not the hill: the plates of stone in the air above the trees, in the table above, which the save measured
at 483 columns with air under their stone in the south band alone and which are fixed in the pass as of the same day.
Nothing has been said yet about the hill's join, the crest, the stream's banks or the ring's joins, so section 1's and
section 2's verdicts are still owed a look.

The rock's numbers were checked against the drawing by arithmetic before any of it was raised - the foot heights,
where it meets the country, the peak at 220, and that no drawn water column is raised - and the drape's effect on the
hill the same way: up to 14 blocks of stone over it along the box's east edge, nothing at 32 out, and no drawn water
inside the ring's band. That is not a look at the ground.
