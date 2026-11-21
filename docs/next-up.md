# Next up

The working state of the campaign, written so a session picking this up cold does not have to guess. **Read this first**,
then `docs/chapter-two-state.md` for the chapter being walked and `docs/chapter-three.md` for the one after.
`docs/HANDOFF.md` is the long history - how each thing came to be - and `docs/arda-roads.md` the road design.

**Last touched:** 21 November 2026 - the owner walked a fresh world, `Work Verification`, and signed off the Shire,
the road network and the Carrock, then started it again with the passage clear and found the hold's door open at the
end of it. The mustering hall is turned half round (its board faced away from the path), the
Shire's banner is drawn round the portal crossing so it is seen on stepping through, and the keep is moved twelve east
so its own doorway stands in the gate's chamber instead of ending at a wall; a road now also begins from the block a
player is set down on, because Gundabad's own mark is sixty-four blocks from its road's end. Read *21 November* below.
**Before that, 20 November** - the Shire is raised on every world's start now (`raiseShireOnStart` defaults
`true`), because the arrival portal opens on the village's own crossing, -470, 97, -123, and the clearing pass took
the doorway with it; a dedicated server's `pause-when-empty-seconds` is lifted for the length of that pass, measured
rather than assumed: a fresh level with nobody online stood the village in **1053 ticks (52.7 s)** and had its pause
back afterwards. Before that, 19 November - a road that met one channel twice is walked again with its crossing held
straight, planned against the drawing with `tools/roadprobe`; a road no longer begins under a build that is still
going up (the valley's road waited for the city and left a gap in Road Test 7); the twelve builds the owner has
visited are marked at the block a traveller should arrive at, which `/tmtp`, the page's needle and the sign boards all
read; and a leg that runs beside a river now steps the line inland instead of refusing the road. Before that,
18 November - every walked road is erased and a road is now planned from its two ends by `RoadPlan`, kept in the save
and laid from that plan; **one road has been laid in a world and it was laid under a river**, the plan now reads its
line a block at a time to find the water, and that fix has since been run in a world and signed off (see *21 November*). Before that, 17 November - the
mustering halls are pinned per town and raise themselves, the marks the world draws are behind one client switch
that ships on, and the Shire's south cliff is dressed by a new bank rule (seen on 21 November); the Carrock is cut,
trimmed of its base plate, pinned at 8307, 187, -4165 and stood there by the world on the first start. **Signed off in
game that day:** Hobbiton's hall (and its pin, now in `Pins.SOURCE`) and every quest mark, icon for icon - neither
needs another hand. Read *21 November* first for the walk that signed the Shire, the roads and the Carrock off, then *20 November* for
the Shire's start, *17 November* for the halls and the marks, and *The Shire map* for the village itself.

The whole Gundabad line is walked and settled, the way in past the hold's door with it; chapter two is proven to
page 32 and **paused** there; the road east of Glóin's camp is the old forest road, planned from its two ends with both
crossings in its plan;
the keep's floor is **settled at `KEEP_FLOOR` 176**, and its entrance was moved twelve east on 21 November so the hold's
own doorway stands in the gate's chamber, with the passage to it **walked and the door open** on 21 November. The Shire
is **seen whole** and the road network is signed off; the quest walk is still paused where it was, at Carn Dûm.

## Where we stopped

- **Chapter two works to page 32 (2-7).** Played in the test world from the chapter-one seam through the Gundabad pages:
  the courtyard scene, Glóin's camp, Ulmug's fires, the hold raised when its page is taken, the vault, Gimli in his own
  cave, and the captain and the wraith below the stair. It reads as written; nothing in it is waiting on a fix.
- **Paused at page 33.** The four left - 33 Steps of Evil I, 34 Steps of Evil II, 35 Evil Tidings, 36 Fire & Ice - are
  all Carn Dûm ground, and **Carn Dûm is what quests go to next**.
- **The keep's placement is settled, and the world is what settled it.** A fresh world drew the keep exactly at its plan
  and still had no way in: the keep's rooms stood on **200** with the gate's chamber on **189**, so the chamber opened
  under them and the keep's floor was the chamber's ceiling. The number that was wrong is `KEEP_FLOOR`: the template's
  rooms are lifted thirteen courses over its base, so the pin is **176**, not 187. See `docs/gundabad-keep.md`.

## 21 November 2026 - the walked world, and the four things it changed

The owner walked a fresh world, `Work Verification`. **The Shire stood before the portal was used and was ready when he
came through**; the road network laid itself and the bridges did not double back over a channel; the Carrock spawned in
perfectly. Three things came back to change.

- **The mustering hall is turned half round.** The board stood at the end of the build away from the path it sits on.
  `TownHalls` lays the hall with `Rotation.CLOCKWISE_180` about its box's own middle - same pin, same floor, same
  footprint, front the other way - and reads it back with `Sites.wholeAt` turned, so a hall standing the other way is
  laid again rather than left. One standing unturned is swept out of its box first: the file does not carry every cell
  of its 16x15x16 box (972 air, 1307 missing of 3840), so a turned lay over an unturned hall would leave the old walls
  where the file says nothing. Its bottom course is complete in the file, so the floor comes back with the lay.
- **The Shire's banner is drawn round the portal.** `Places.standTowns` preferred a town's mark, and the Shire's mark
  is (-150, 114, -382) - four hundred blocks from the crossing at (-470, 97, -123), which is further than the town's
  own 280-block radius, so stepping out of the portal was the one place the village was never announced. `Places` now
  has an `ANNOUNCE` point per town, read from `ArdaMap.hobbitonGate()` - the same constant the portal crosses at - and
  a town given one is re-stood when the point moves, so a world holding the old centre is moved to the new one.
- **The keep's entrance stands in the chamber now** - the twelve-east move and the doorway above.

**Two guards were still owed, and both are fixed.** The hall's was `Sites.wholeAt` read turned - the strict reading,
every sampled block - so **one block a player changed inside the hall made the next start sweep it and lay it again over
their work**. `TownHalls` now writes down which way it laid the hall, against the pin, in a file of its own (`halls`),
and asks that first: a hall recorded turned is returned from without touching a block, and a world made before the
record existed has the way it stands read off its own blocks - the two orientations compared by a majority, so a changed
block cannot move the answer - and then noted. Only a hall the record says was laid the other way is taken out and laid
again. The keep's guard asked only for the plan this build writes, so a world that already held the hold at the older
5516 would have had a second one laid at 5528, 110 of its 122 columns over the first. `ArdaMap.keepOlderPlans()` is
asked too - 5516 at 176, and at the 187 the plan used between 15 and 18 November - and a world holding either keeps its
hold, with only its mouth dressed. **The twelve-east shift is what a world made after 21 November gets; it is not a
migration.**

**A second fresh world found the passage thirteen blocks short, and the clear was the whole of it.** The hall laid its
floor, its roof and its two walls for the fifty-three blocks to the keep's wall, and the hold stood at its plan with the
door the plan was written around - 5571..5575, inside the chamber - but the passage between them was never opened: the
inside was cleared with `base.offset(x, y, z)`, in which the cell's own y and z are added to the gate's corner as well,
so the air was written about 377 courses up and seven thousand blocks north, where there is nothing. What stood between
the hold's mouth and the hall was therefore thirteen blocks of the mountain's own rising ground, from z -6910 to
-6922 - `clearMouth`'s eight blocks out from the wall being the only rock ever taken out of it, which is what made the
two read as a tunnel stopping short. `extendHall` now builds the cell from the corner and the passage's own x, and
`undressBack` - the strip behind the gate, which had the same double offset in its y - is fixed with it. Nothing in the
plan moves: the passage still ends on the keep's wall at -6930, its inside is cleared to the course before that wall -
the wall itself is never cleared, because the way in is the template's doorway in it and a hole the passage's width
would take the door out on every start after the first - and a world that already holds the hold is re-cut on its
next start, because the hall is carried on every pass.

**A road also now begins from the block a player is set down on.** `RoadCorridor.look` reached 32 blocks, and a road's
end stands at a build's door while the build's own mark can be much further in: Gundabad's road begins at the gate's
courtyard, 5578, -6769, and the hold's mark is 5578, 190, -6833, sixty-four blocks away - so a player teleported to
the hold stood outside the reach of its own road and nothing began. The log shows exactly that: the road's first
section began at world start from 5704, -6680 and then waited on a head whose chunks nobody was near, and its next
sections only began at 19:39 when a teleport to the Carrock loaded chunks of its line. The reach is 96 now. (At
Glóin's camp nothing was wrong: the Gundabad road joins the old forest road at 8583, -3617, three hundred and fifty
blocks east of the camp.)

**Seen 21 November, in a fresh world:** the hall laid turned, its board on the path side; the Shire's banner fired where
it should, at the portal; and the keep stood at 5528 with its door at 5571..5575 inside the chamber. **Then walked, with
the clear in:** the owner started that world again and reports the hold's door open - the passage runs from the gate's
hall to the template's own doorway and the way in is there, which was the thirteen blocks above. The rooms past the
door were not reported on separately. A world that already holds the hold at the older 5516 keeps it.

## 20 November 2026 - the Shire stands at every world's start

The portal out of the overworld opens on the village's own crossing, -470, 97, -123, which is inside the footprint
the clearing pass works over (x -700..-149 by z -512..-44), so a village raised by a player's arrival is a village
that takes that player's doorway away: the pass begins on the first tick after the crossing is used and the
ammolite doorway goes about ten seconds later. `raiseShireOnStart` now defaults `true`, so the village is raised at
the world's start, about a minute into a fresh world, before anybody can walk through to it.

A start is exactly when a dedicated server is empty, so the pass holds the server's own `pause-when-empty-seconds`
at 0 for its length and gives the number back when the village stands. That half was measured, not assumed: a fresh
level with the property at 5 s and nobody online logs `Server empty for 5 seconds, pausing` part way through the
pass and never takes another step, because the paused branch of `MinecraftServer.tickServer` returns before the tick
event and before the level's own tick. With the lift, the village stood at -700, 87, -512 in **1053 ticks (52.7 s)**
and the log read `the server's pause when empty is back to 5 s`. A server killed outright mid-pass keeps the zero,
which costs it its idle pause and nothing else, as it keeps the level's forced-chunk tickets until the next start
re-runs the pass.

**Seen 21 November:** the owner came through the crossing into the village and found the deck, the mouths, the ring
and the joins right - this is the walk that paragraph used to owe. A world that sets `raiseShireOnStart = false` keeps the older rule, the pass beginning when
somebody comes within `REACH` 192, and `/hobbiton raise` still asks for it at any time. The commit is `0e2c70f2`.

## The list

| # | Item | Status | Where the detail is |
|---|------|--------|---------------------|
| 1 | **Carn Dûm's ground** - the camps on its borders and the wight's hall | **next** | nothing has looked at it yet; `carndum` is only a place canon'd to `ANGMAR` |
| 2 | **The roads** - the whole network, rebuilt from two ends per road | **eleven roads in `Roads.NETWORK`**, each as the two ends it was pinned between: Gundabad's road (5578, -6769 to 8583, -3617), the old forest road, the four legs of the vale and the high pass, the wood ruin's nine-block connector, Rivendell to the meeting plaza and the plaza on to Glóin's camp, Rivendell to Elladan's camp, and the short leg at Rivendell's south gate. **Signed off 21 November:** the network laid itself in a fresh world and the crossings did not double back; see *21 November* for the one limit | `RoadPlan`, `/tmroad plan`/`build`/`pin`; see 18 November below |
| 3 | **Chapter two, pages 33-36** | paused on 1 | `docs/chapter-two-state.md` |
| 4 | **Chapter three** - eleven pages, prose written | not started | `docs/chapter-three.md` |
| 5 | **The keep's placement** | **settled**: `KEEP_FLOOR` 176 - rooms' floor 189, walked on 190, level with the passage. **Entrance moved twelve east on 21 November** (*The keep's doorway* below); a world that already holds the hold at 5516 keeps it (`ArdaMap.keepOlderPlans()`), so nothing is laid twice. **Walked 21 November:** the passage clears to the door and the door is open | `docs/gundabad-keep.md` |
| 6 | **Sign posts** | on hold | below, with the recipe |
| 7 | **Chinese** | on hold | whole, until a full public release |
| 8 | Chapter four onward, the finale, the job boards (quest 37) | not started | `FINALE_OPEN = false`, closed by design |
| 9 | **The Shire map** - Hobbiton | placed, cleared, draped; river rerouted; **raised at every world's start since 20 November**, and **seen whole on 21 November** - ready when the owner came through the portal, joins and south edge with it. Nothing is owed | `run/hobbiton_art`, `/tmcut`, `rivers.png`; see below |
| 10 | **Town halls** - one pinned per town, raised once the town is finished | Hobbiton's stands, was called perfect, and is **turned half round since 21 November** so its board faces the path; the way it was laid is recorded per pin, so one changed block no longer sweeps and re-lays it; its pin is in `Pins.SOURCE`; Bree has no town yet | `TownHalls`, `PinCommands`, `/tmpin hall`; see 17 November below |
| 11 | **The Carrock** - the shaped rock in the Anduin | cut, base plate trimmed, pinned at 8307, 187, -4165, **stood by the world on the first start**, bedded into the river with stone, and **seen standing on 21 November** - the owner signed it off | `Carrock`, `Pins.SOURCE`, `data/tolkienmobs/structure/carrock/`; see 17 November below |

## The keep's doorway - answered in a world, 20-21 November

The question this section used to carry was whether the hold's own doorway stood in the gate's passage. **It did not.**
Read out of the template itself: the south wall carries four five-wide, seven-tall openings at local x 13..17, 43..47,
74..78 and 104..108, and behind them the wall is two courses deep over most of the passage's twenty-nine columns and
**twelve deep at local 58..63**, where an inner mass stands. The plan was written around the far opening (104..108),
which at `KEEP_X` 5516 is world 5620..5624 - twenty-nine blocks east of the passage's last column, 5591 - so the
chamber ended at wall over twenty-six of its columns, and the neighbouring openings only clipped its two edges.

**The fix is the one the owner gave:** the whole box moves **twelve east**, `KEEP_X` 5528, which puts the 43..47
opening at world **5571..5575** - inside the chamber, four blocks west of its axis 5577 because the openings stand on
a thirty-block beat. `DOOR_X` follows it (106 to 45), so `clearMouth` dresses the ground in front of the door the hold
is actually entered by. And the gate **no longer cuts a passage through that wall**: `extendHall` used to cut a hole
the passage's own width, fourteen courses deep, on every start - which on a first start the template then wrote over,
and on any later start took the doorway with it. The passage now stops at the wall, and the door is the template's own.

**The box still fits the crown** (x 5496..5656): 5528..5649 leaves seven blocks to the east. `KEEP_FLOOR` 176 is
unchanged, the rooms' floor 189 and the doorway's floor 189, level with the passage. **A world that already holds the
hold keeps it:** `ArdaMap.keepOlderPlans()` names the 5516 corner at 176 and at the 187 the plan used between 15 and 18
November, and a hold found on either is left standing with only its mouth dressed - the shift is the making of a world
after 21 November, not a migration. **Seen 21 November:** the passage clear from the gate's hall to the template's own
doorway, with the door open at the end of the walk - the thirteen blocks that stopped it are the paragraph above.

## 19 November 2026 - a road waits for its build, and the visited builds have marks

Two changes, both compiled with `./gradlew compileJava -x createMinecraftArtifacts -q --no-build-cache` (exit 0) and
neither seen in a game.

- **A road no longer begins under a build that is still going up.** The ground begins every road the world is given - a
  chunk the road passes through starts it, and so does a player within 32 blocks - and a city is raised band by band with
  its ring dressed after the last band, so the road out of Rivendell graded country the dressing then changed under it.
  That is Road Test 7's gap: the strip from the first bridge to the road's own end, with the road's own note saying it was
  down so a second laying owed nothing. `Roads.BUILDS` now names the build each of the valley's three roads leaves and
  `Roads.waits` asks `TownHalls.waits` (the same answer a town's hall waits on); `RoadCorridor.mayBegin` holds the road
  until the build is finished and the ground asks again a second later. Only a road that names its build is held.
  **Still owed:** a game check - the road out of the valley should begin by itself once the city is whole, and the strip
  already missing should come back after `/tmroad forget` at the gap and one visit. If the pass refuses it, the chat says
  which leg and why; that sentence is the next thing to read.
- **The visited builds are marked, and one answer feeds `/tmtp`, the needle and the boards.** `Marks` holds the owner's
  own fifteen blocks - Rivendell, Hobbiton, Thorenhad/Elladan's camp, Barachen's fire, the wood ruin, the three crags and
  Matuna's lair, Glóin's camp, the goblin fires and Gundabad - keyed as the commands ask, and `/tmtp` answers a name with
  its mark before the place or the pin. `Waypoint` reads the same marks for a site's page, a named place and the step after
  it; a body the campaign stands keeps its own block. A mark is not a pin, so no build is seated anywhere new. **Gundabad
  is a town** in `Places.TOWNS` now (`<- town` on the owner's list), and a town with no place yet is stood on its mark, so
  a board's distance and the banner's circle are measured from the block a player can stand on. **Still owed:** one in-game
  look at a jump per name, one page's needle, and a board's town list with Gundabad on it.

## 18 November 2026 - the roads are planned, not walked

Six commits on `main`, none pushed: `f1c7497e` the planner and the erased network, `408426c7` a crossing it will not
bridge refusing and its banks taken from the run's ends, `28421690` the width read from the ground, `eb308473` the plan
kept in the save and a failed leg laying nothing, `cbab2367` both ends named and refusals said in the chat, `63e3eeb0` the
line read a block at a time so a river is bridged. Every one compiled with
`./gradlew compileJava -x createMinecraftArtifacts -q`. **One road has been laid in a world, and it was laid wrong** - see
*Still open from tonight*.

- **The walked network is erased.** `Roads.ROUTES` is empty, and with it the bend table, the kinds, the cuttings, the banded
  list and the build names: the valley's road and its crossing, the Thorenhad road, Elladan's branch, Barachen's camp, the
  wood ruin and its link, the mountain way, the ford way, the three cave roads, the high pass road with its bridge and its
  climb, Glóin's road with its stream crossing and the road east, and the old forest road with both of its fords. The
  builds are all still standing, so the network comes back one place at a time. **Signed off 18 November:** the owner
  compared all twenty-one walked routes against the seven roads that replaced them that night and found no destination missing -
  the network came back as fewer, longer sections over the same ground, which is the whole of what the planner was for.
- **A road is now two ends and a plan.** `RoadPlan` walks the drawing - `ArdaMap.sample`, sixteen blocks a lane, ±192 wide -
  and prices grade over 0.35, turn, and river depth. No chunk is read or generated to plan one, and it leaves a bend every
  320 blocks so the pass's own search only has to sit each leg on the ground. Water on the chosen line becomes a
  `Route.Crossing` with its banks from the drawn ground and its deck two over the drawn water.
- **Three commands, one road.** `/tmroad plan <x> <z> [kind]` reads the country and says the sections, laying nothing;
  `/tmroad build <x> <z> [kind]` lays them from the block underfoot; `/tmroad pin <x> <z> <x> <z> [kind]` lays them from
  two named ends, so a leg is pinned by the ground it crosses and the same command answers the same road from anywhere -
  and two ends on one block are refused by name. The world steps each pass while the ground under its head is loaded, so a
  pass whose head is not loaded waits where it stands and the ground begins it again. **The width is
  automatic:** start on a town's ground (Hobbiton or Rivendell, or the approach out of either) and the road takes the bands -
  7 paved inside 32 blocks, 5 through 96, 3 beyond - and anywhere else it is a wild track 3 wide. The word after the
  coordinates overrides that: `wild` 3, `wide` 5, `main` 7 of country surface, `urban` 7 paved everywhere, `town` the bands
  wherever it starts. The worked example, from Glóin's camp's road end at 8231, -3641: `/tmroad plan 8539 -3668` then
  `/tmroad pin 8231 -3641 8539 -3668` - about three hundred blocks of wild track with one span, and the log has to name a
  bank near the old 8520/8481 pair. That is the leg whose first laying went under the Ford, so it is the one to try again
  after `RoadPlan.dense`.
- **A crossing it will not bridge lays nothing.** Water wider than 96 columns, no bank within 96 blocks, or a run that turns
  back on itself refuses the whole road and says why - in the command's answer, and in the log for a road the world asked
  for. The span's banks are taken from the two ends of the river run (back from where the road meets water, on from where it
  leaves it), not from the run's middle.
- **A leg the search cannot walk lays nothing.** No straight fallback survives: the whole road is given up, nothing is
  graded, no note is written saying it is down (so it can be planned again over better ground), and the leg, its two ends
  and the reason go to the log and - within 128 blocks of the road - to the chat. `Descent.why` tells the two faults apart:
  a leg longer than one window of the search wants another mark in it, ground with no way through it is country the road
  cannot cross at all. A pass whose step throws was swallowed by the tick that carries it; that is a warning in the log
  now.
- **A planned road survives a restart.** The plan is kept in `road_plans` beside the road notes - its sections, their
  kinds, their spans and their bends - and read back at server start into the tables the pass asks, after the previous
  world's planned roads have been taken back out. A road laid in an earlier session is still there to find with
  `/tmroad forget` and to lay again, and the start writes `Roads: N planned sections read back from the save` when it does.

- **The raise stopped halfway, and the fix is signed off in game.** A band's far end is ground no standing player loads, and the raise
  would not move on until every piece of the band was down, so it laid what it could see and waited in silence for ever -
  which is what "Rivendell only goes about half way" was. The numbers, out of Road Test 3's own save: band 0 is 132 pieces,
  23 of them inside a ten-chunk view of the corner the player was teleported to, the median 341 blocks out, the furthest
  624, and 71 never inside a view of anywhere they stood. After five empty passes the raise now makes the ground for the
  nearest waiting piece and lays it, one piece a pass, and logs one line per band saying what it is waiting on. **Road
  Test 7, a fresh world and no commands:** `raised from the pin` at 09:44:12, and every band went down with `/tmcity band`
  never run - 09:46:13, 09:47:54, 09:49:27, 09:50:46, 09:51:45 and `the whole city is down` at 09:52:26 - each asking
  for made ground exactly once, which is the ceiling kept.

- **The world no longer builds a city; only the raise does - both halves signed off.** Two changes on top of the stalled-band fix:
  a structure start is now probed for the cut's own blocks (a dozen interior samples of the piece the city is planted on, a
  majority enough) before the raise, `/tmcity summon`, the band hand or the dresser treats a city as standing; and no
  structure set is emitted for an assembly, so worldgen never places one. A world made before this keeps its start - it is
  ignored rather than removed, and the raise plants the city from the pin over the top. **Road Test 6, fresh, no commands:**
  `tolkienmobs:rivendell` appears in no chunk of the `arda` region files, so the worldgen half is settled. **The raise half
  is settled by Road Test 7** - a fresh world with no commands in it, the city planted from the pin and stood band by band,
  so the raise is the only thing building one (what went wrong before is the band-turn fix below).

- **A band turn threw away the band it turned to, and the raise finished a city with one floor in it - signed off in
  game.** Road Test 6's log: band 1 of 6 down, then bands 2, 3, 4, 5 and `the whole city is down` all inside **720 ms**,
  and the save has stone at the pin's column at 75 and 80 with air at 123 - the floor and nothing over it. The turn moved
  the marker and set `raisingBand` to the band just written while clearing `raisingOrder`, so the next pass believed the
  new band's order was already built, and an empty order is every piece down. The turn now leaves `raisingBand = -1`, so
  the pass that reads the marker builds that band's order and lays it; a band that closes with nothing in hand warns by
  name instead of passing quietly. **Road Test 7, fresh, no commands, the owner teleported to 5200, -3033 and left
  standing:** one `out of made ground` line per band and no warning line, the bands closing sixty to ninety seconds apart
  instead of all at once, `the whole city is down`, and the dresser's `19643 columns cut down, 35386 filled up, 320 left
  for the water` - and the owner found the city standing whole, which is the check that matters, since both earlier runs
  said "finished" too. **The valley's folk are the world's own, and that half is closed:** the seventeen `Elf suffocated
  in a wall` lines Road Test 6's floor was laid over and the eighteen in Road Test 7's are natural spawns the valley's
  own spawning walks into the footprint as it goes down - the raise itself stands no Elf anywhere. Every body the pack
  does stand is seated on ground looked for first: `Campaign`'s Glorfindel and Gimli by `floorNear`, `Sites`' keepers by
  `room`, `QuestGivers`' campaign givers by `groundNear` with twelve tries - so nothing here has to wait for a band, and
  no fix is owed.

### Still open from the road tests (18-19 November)

**Signed off in a world, 21 November.** The owner walked the network in `Work Verification` and calls it fixed: the
roads laid, the crossings stood bank to bank and no section doubled back over a channel. What follows is the record of
what those checks were, kept because its numbers are what a re-cut is judged against. **Two things in it are still
unmeasured:** a climb of more than a few blocks throwing the search off - the owner's one complaint, in the mountains
near Rivendell - and `road_plans.dat`, which has never been written and read back in a world.

- **The bridge has not been seen.** The first road laid in a world went under a river more than four blocks deep with no
  span over it: the plan was cut into sections from water read at the sixteen-block steps, and the channel fell between two
  dry samples. `RoadPlan.dense` now reads every block of the line, water being a column the drawing gives a bed below its
  own surface. **The check:** `/tmroad forget` on the bad road, re-pin the leg with fresh ends, and look for more than one
  section in the chat and a `Road plan <id>: span …, deck …` line in the log. If it still comes out as one section, the
  water test itself is wrong for that channel - `sample.river()` false where the world has water - and `ArdaMap.column` is
  the next thing to read.
- **A crossing was read down the river instead of across it, and that fix is unrun too.** The test world's last two pins
  (18 November, 04:15 and 04:18) were both refused on the mountains' channel - `the water at 6422, -3616 is 112 columns
  wide` and `no dry ground within 96 blocks of 6471, -3645` - and the drawn channel there is only forty to fifty-six
  columns across, so neither refusal was about the water. The span's axis was taken from the run's own two ends, which
  are a few blocks apart, and a road crossing a north-south channel almost square rounds to a north-south run: the banks
  were then searched *up* the channel, into the wide sheet that opens north of it (536 columns at z -3560), instead of
  across it. `RoadPlan.cut` reads both axes now and `pick` takes the narrower standing reading. **The check:** the same
  two pins again - `6398 -3654` to `6481 -3655` is the short one on the screenshots - and the chat should say `2 sections`
  with a span of about forty columns rather than nothing at all. If it still refuses, the next thing to read is `dry()`
  itself: the reading at 6471, -3645 that walks west across the channel should find the west bank at about 6420.
- **The crossing fix works, and the deck it planned was missing - fixed in `98e397cd`, unrun.** `pin 6398 -3654 8229
  -3654` planned and laid three sections, and the log's span is `6668, -3799 to 6668, -3748, deck 195`: fifty-one
  columns, the ford's own size, so nothing about the width was wrong. The pass stood the two abutments and no middles.
  Read out of the save at x 6668, the oak ends sit at z -3807..-3799 and -3748..-3740 and everything between them is
  water at 188-192, with the road's paving stopping at each abutment: `bridgePlan` walked the deck from the near bank
  towards the low coordinate only, so a span running the other way (bank at -3799, far bank at -3748, southwards) ran
  out at its first step. The gap is read from both ends now and the middles laid in the span's own direction, turned
  about for the other heading. **The check:** `/tmroad forget` on this road, then the same pin again - the sweep clears
  the two old ends, so the span is stood from scratch - and the deck should be planks and pillars from bank to bank,
  with `BRIDGE done in 10 steps` instead of 3 (two ends and seven middles for 51 columns). If the deck comes out
  but the pieces sit off the band or a piece short, the rotation the other way is what to look at:
  `Rotation.CLOCKWISE_90` for a southward span's middles and `CLOCKWISE_180` for an eastward one.
- **The ground already laid under water stays.** Forgetting a road clears its notes, not its blocks; the causeway has to be
  filled in by hand or the new road laid beside it.
- **No restart has been tested at all.** `road_plans.dat` has never been written and read back in a world. The check is one
  start: the log line, then `/tmroad forget` while standing on a road laid before the stop, then the command again.
- **A road planned twice stood twice - fixed, unrun.** The old forest road to the far side of the river east of Mirkwood
  was planned at 11818, -3801 and again at 11824, -3851; both plans were live after the restart and both laid at once,
  seven sections each (the log at 04:52 names both and at 04:53 shows both stepping "since the pass was last picked up").
  Two causes: `/tmroad forget` cleared the note of the section at the hand only, leaving the other six to be resumed, and
  a restored plan is in `ROUTES`, so `RoadCorridor` began it on a chunk load. Now `/tmroad forget` drops the whole road
  (every section whose id begins with it, from the passes, the list and `road_plans`) and a planned section with no note
  is never begun by the world - it waits for a command, while a road begun and unfinished still resumes. **The check on
  the existing mess:** stand near the old road and forget twice, once per road - each reply names the road and says how
  many sections went - then delete the regions and pin once;   nothing else should lay. Then restart and confirm no planned
  road begins by itself before a command is given.
- **A channel was crossed twice, and the walk will not dip in and out again - fixed in `ee907452`, unrun.** The same road's single clean plan
  (one plan, one pass per section, 05:07) still held two spans over one channel: `bridge_1` 11759, -3882 to 11710, -3882
  and `bridge_2` 11739, -3908 to 11685, -3908, joined by `_2` running *east* from 11694, -3882 to 11755, -3908 - a
  hairpin, and what looked like two roads and two bridges were the plan's own doubled crossing (the save has decks at
  z -3885..-3879 and z -3911..-3905, no third plan). The lane walk priced water per sixteen-block step, so a dip into the
  channel's edge and back out was cheap. It now keeps the count of dry lanes since the water in its own state and prices
  water met inside `DRY_AFTER_CROSSING` (4 lanes) at `CROSS_AGAIN` on top of the toll. **The check:** `/tmroad forget`,
  then the same build again - the plan should name *one* span over that channel instead of two, and no section should
  run east between two spans. The old decks stay standing: the sweep only clears timber inside a span's own box, so
  clear the ground at 11680..11760, -3920..-3870 before judging what the new one looks like. A port of the walk over a
  channel with an island in it takes one run under the new rule and two under the old; the drawing itself has not been
  re-planned. **And the probe's own doubled crossing is fixed, off the drawing only, 19 November.** `6300, -3580` to
  `6800, -3580` planned a 45-wide span at 6418, -3612, a section running back west to 6383, -3585 and a 56-wide span at
  6399, -3585 over the same water. A trace of the walk showed why: its readings are sixteen blocks apart, and between two
  of them it stepped two lanes onto the one dry block at 6441, -3580, on the channel's ragged edge, and back into the
  lane beside it. Both readings were dry and nine dry blocks apart, so neither `DRY_AFTER_CROSSING` nor the bank margin
  could see the water, while the staircase the plan is really read along went through it twice - and because a span's
  banks are read along one row, the first span ran on past the block the line left the water at, so the next section
  began beyond it and ran back. A plan now records that its line leaves water and meets water again inside
  `DRY_AFTER_CROSSING` lanes, and `RoadPlan.of` walks that leg a second time with a lane change refused on every step
  that leaves water: a straight crossing, on the lane the road reached the bank on. The first walk is untouched, so a
  leg whose line was sound plans the same line it did. **Probed:** that leg is three sections with one 45-wide span at
  6424, -3627 (deck 188) and no section running back, and every other leg probed - the roads in `Roads.NETWORK`, the 6398, -3654 pin and the 6420, -3560 bank leg - are byte for byte what they were, so no road already
  laid moves. **Not run in a world:** this is a planning change and nothing has laid it; the next fresh world is where
  the one-span crossing shows.
- **The laid network is in the code, and the test world is no longer the only place it exists.** `Roads.NETWORK` names
  eleven roads, each as the two ends it was pinned between and at the three-wide track, planned from the drawing when
  the class loads: Gundabad's road (5578, -6769 to 8583, -3617), the old forest road east of Mirkwood to Glóin's camp
  (11813, -3901 to 8229, -3654, five sections with spans at 11649, -4080 and 8551, -3626), the four legs of the vale
  and the high pass (4515, -2446 to 4705, -3169; 4528, -2541 to 4624, -2480; 4776, -3063 to 4684, -3050; 4819, -2887
  to 4647, -3315), the wood ruin's nine-block connector (4787, -3059 to 4778, -3059), Rivendell to the meeting plaza
  and the plaza on to Glóin's camp (5105, -3413 to 5605, -3622; 5606, -3622 to 8229, -3654), Rivendell to Elladan's
  camp (5200, -3033 to 4647, -3315) and the short leg at Rivendell's south gate (5111, -3399 to 5072, -3221), with the
  ids their pins were given, so the geometry in a fresh world is the geometry in the test world. `Roads.planned` is
  what tells a session plan from a named road now; a named road is begun by any chunk of its ground that loads, as the
  walked network was. **What to check on the fresh world:** the start log should carry a `Road plan <id>: …` line for
  each section before the world is entered, and the roads should lay themselves as the ground comes in - two bridges
  on the old forest road and none on the high pass legs. That the roads cover the same destinations as the twenty-one
  walked routes is not a guess: the owner read the two lists against each other and signed it off, so nothing from the
  old network is still owed.
- **Road Test 2 laid nothing, and the reason is where the roads are.** The fresh world was entered at Rivendell
  (raised at 5200..5753 x, -3370..-2859 z) and left there: the old forest road runs 8229, -3654 to 11813, -3901 and the
  high pass legs sit at 4515..4819 x, so the nearest road line to the city's west edge is 380 blocks away, and the
  corridor begins a road only from a chunk of its own recorded line. Nothing refused and nothing errored - the logs had
  no road line in them beyond `Roads: 11 planned sections read back from the save`, and that line was the old world's.
  The save's ground agrees: besides Rivendell's own bands, only a narrow strip at x ~4790 for z -2880..-3312 has real
  chunks, the rest of the arda map along the roads has never been generated, and the old forest road has no region file
  at all past its west end. Two lines of instrumentation were added so the next start cannot be ambiguous: the start
  says `Roads: <n> sections in hand, <m> read back from the save`, and each pass says `Road <id>: begun from the ground
  at <x>, <z>`. **The rule is now the player as well as the chunk:** once a second `RoadCorridor.look` begins every road
  a player stands within 32 blocks of the line of, from where they are, so a road no longer waits for a chunk that has
  already loaded once; a road the ground refused is kept refused for the run (`RoadCommands.REFUSED`) rather than
  re-searched every second, and forgetting it clears that. **What to check:** launch, go to 4819, -2887 (Barachen's camp,
  the one place the ground is generated that G's line touches) or anywhere within 32 blocks of a leg's line and read the
  log - a `begun from the ground` line means the corridor found it and the road is laying, or refused with a reason in
  the chat within 128 blocks. Nothing at all in the log within 32 blocks of a road means neither path is seeing it.
- **A leg that runs beside a river stepped inland instead of refusing - fixed and planned offline, 19 November.**
  Reading the water a block at a time meant a leg along a bank was measured at the pool its own entry stood in: planning
  6420, -3560 to 6600, -3560, both ends inside the mountains' wide bend, refused with `the water at 6420, -3560 is 110
  columns wide, past the 96 this will bridge`. A refused leg is now walked a second time with the bank priced - a dry
  lane within `BANK_MARGIN` (3 lanes, 48 blocks) of drawn water costs `BANK_COST` (400) a step, so the line steps inland
  along the bank - and water at a leg's own two ends is the bank rather than a crossing, so a pin in the shallows begins
  on the first dry block of its own line. Only the second walk pays for the margin, so every leg that planned before
  plans the same line now. **Planned against the drawing with `tools/roadprobe`, no world:** that leg now plans one
  section, 6436, -3576 to 6589, -3560 - the pin in the water, the road beginning sixteen blocks inland of it; the
  signed-off geometry is unchanged, `11813, -3901` to `8229, -3654` still spanning at 11649, -4080 (52 wide, deck 170)
  and 8551, -3626 (46, deck 188), and `6398, -3654` to `8229, -3654` still spanning 52 at 6668, deck 195, which are the
  numbers the test world laid; a line that is water from end to end still refuses. **Not run in a world:** nothing has
  laid one of these roads, so what the pass makes of a section that begins on a bank rather than at the pin is
  unverified. **The instrument:** `tools/roadprobe` plans a leg against `ArdaMap` alone - no chunk, no world, no client -
  with `./gradlew -I tools/roadprobe.gradle roadProbe -PprobeArgs="leg WILD <x1> <z1> <x2> <z2>"`, and `sample`/`along`
  print what the drawing gives a column or a line of them.
- **`plan` is a look, not a plan.** Nothing is written to `road_plans` by it, so a road surveyed before a restart has to be
  surveyed again.
- **Untimed.** The planner reads one sample per lane per step and now every block of the chosen line; nothing has measured
  a long leg on a live server, and a four-thousand-block pin is the one to try.

## 17 November 2026 - the halls, the marks' switch, and the south edge

Four commits on `main`, none pushed: `067ae6cd`, `a0e8b645`, `56fba972`, `4b66b978`. Every one of them compiled
(`./gradlew compileJava -x createMinecraftArtifacts`); only the halls have been seen in a world.

**The mustering halls are pinned in their towns and raise themselves.** The three `faction_hall_*` worldgen definitions
are gone, with the twelve generated files (`structure`, `structure_set`, `template_pool`, `has_structure`), because one
hall per alliance for a dozen towns to come stood in a field before the town that owns it existed; the templates stay in
`data/tolkienmobs/structure/`. `/tmpin hall add|remove <town>` pins one where the caller stands - key `hall:<town>`,
template from the town's alliance (`tolkienmobs:faction_hall_west` for all three towns so far), towns suggested from
`Places.towns()`: hobbiton, bree, rivendell - and `TownHalls.raise` stands it: ground course in the block the builder
stood on, the box's chunks asked for, the template's own blocks as the guard against laying a second. It waits for its
town, because a town's pass lays, clears and dresses the ground a hall stood early would be taken away with:
`TownHalls.waits` asks `Hobbiton.complete` (the village's note) and `CityCommands.complete` (the valley's marker at its
last band). Three hands raise it - the end of the village's own pass, before its folk are stood; the next start in a
world whose town already stands; and the pin command itself, which says in its reply whether the hall is standing or
waiting. **Seen in game:** Hobbiton's hall stood at the pin in the owner's `Progress Check 1` world and he called it
perfect - the seat, the height and the front as the template was cut are all right, so it stays unturned. The pin it was
taken at is now the permanent one: **-529, 101, -74**, read back out of that world's `pins.dat` and written into
`Pins.SOURCE` under `TownHalls.key("hobbiton")`, so every world gets the Shire's hall without it being pinned again.

**The marks the world draws answer to one switch, which now ships on.** `questBadge` gates both the scroll over a
turn-in giver's head and `BoardMarks`' green mark over the notice boards, and defaults `true` where it defaulted
`false`; a plate's own mark is never touched by it, so a player who clears the world of marks still reads what is in
front of him. An existing config file that says `false` keeps its answer. **Seen in game:** the owner looked at the
marks and the icons are perfect - nine of them, the inks and the shapes both - so nothing about the art, the font, the
sizes or the seat moves from here; the badge's place over a head and the board mark's lift drew no complaint either and
are kept as they are.

**The Carrock - cut, trimmed and stood by the world.** The rock the owner shaped in the Anduin, boxed by
four ammolite markers in the `Arda` save's overworld (all at y 36 in the river: x 16694..16735, z 5627..5711), is cut to
`data/tolkienmobs/structure/carrock/`: three 48-cube pieces and a `carrock.parts` table, **42 x 57 x 85**, air, the
river's water and the markers all dropped. Seen standing at the pin, the first cut also carried the ground it was boxed
through - a plate six blocks deep or less, coming out of the water at the south end as **three tongues of bare dirt** -
so every column whose highest block stood at or under that plate's top (y 5) was cut away: 2,860 blocks in 1,189
columns, nothing above them touched, the rock's foot left whole, and the spruce's fourteen hanging leaf columns kept.
The pieces now hold **36,079 blocks in ten kinds**. `Carrock` holds the one name (`carrock`), the size and the anchor -
**its low north-west corner** - and `Pins.SOURCE` now carries that corner for this world: **8307, 187, -4165**, the block
`/tmcut place carrock 8307 187 -4165` is given. **It stands itself now:** `Carrock.autospawn` runs in `MilestoneHandler`
with the gate, the keep and the cave, reads that pin, lays the three pieces from it and writes a note beside the cut in
the save's folder - dimension, corner and the table's fingerprint - so every later start leaves it alone. A world whose
note is missing or whose pin has moved is laid again rather than cut about, since the pieces carry no air. **And it is
bedded into the river:** `Carrock.seat` reads down every column that carries one of the build's own blocks on its base
course, counts the water under it, and fills that water with plain stone only when the read ends on ground - a column
already standing on stone, or hanging over a cave or open air, is left alone. It runs with the autospawn and on every
start after it, so a world that has held the rock since before there was a bed gets one next time it comes up. **Open:** the
rock has not been seen standing from this pass - the first start after this ships is the check, and the pin's
`_stands.txt` beside `cuts/carrock/` is where that start leaves its word.

**The Shire's south cliff.** The drape never cut outside a deck, and a village at the foot of its own country read that
rule as an eleven-block cliff along its south edge (`Progress Check 1`, x -430..-438, z -45: village ground 97-98,
country 108-109). `Dressing.BANK_BLOCKS` (3) now cuts a bank that stands three blocks or more over the build's own
floor, to the same join every other column gets, while the shallow grooves round a city's open faces stay as they were.
`Hobbiton.DRESS_VERSION` is 2, so the village re-raises and re-dresses on the next start. The full note is in *The Shire
map* below.

### Still open from today

- **Bree's hall.** `bree` is a town in the catalogue with no build of its own, so `waits` has nothing to wait for and a
  hall pinned there stands straight away - and would be laid over whenever Bree is built. Give Bree a build and a
  completion answer before pinning one there.
- **The south edge and the joins are seen, 21 November.** The owner walked the village in `Work Verification` - raised
  on the world's start, before the portal was used - and called it right: the cliff at x -430..-438, z -45, the
  rerouted river, the two joins and the drape reading a wooded cut all stand in a world made after the mask edit.
  Nothing from the Shire is owed.

## The Shire map - cut 15 November, placed 16 November 2026

**Where it stands.** The build is cut and in a test world at pin **(-700, 87, -512)**; `place`, `clear` and `drape`
have all three been run on it, and since 20 November every world raises it on its first start with no command typed.
The river is now **rerouted round the west of it, a branch drawn out of its south mouth, and the two brought
together in a Y south of the village** (16 November - see below); the mask change needs a world made
after it. What has still not been seen in a world is any of the river work, or the drape reading a village's outline
from under its trees.

**The pass, in order** (a cut lives in `<save>/tolkienmobs/cuts/<name>/`, or in the jar beside the city's):

    cp -r run/hobbiton_art "<save>/tolkienmobs/cuts/hobbiton"
    /tmcut table hobbiton                  (143 pieces over 2 bands, x 0..550, z 0..467, y 0..89)
    /tmcut place hobbiton -700 87 -512
    /tmcut clear hobbiton -700 87 -512
    /tmcut drape hobbiton -700 87 -512

Anything drawn in `rivers.png` only reaches chunks generated **after** the edit, so a world that already holds the old
course has to be replaced to see the reroute - and `drape` must run after `clear` in that new world, or its outline is
the cut buried in Arda's ground.

**What each word does.** `place` lays the pieces from the cut's own low corner, no rotation. `clear` takes two bites:
everything above the cut's top comes off, then every position the cut leaves empty between its top and its bottom has
the country taken out, so Arda's stone under the canopies and the gravel in the old bed go. `drape` is the city's own
pass, generalised: it settles the ring outside the footprint to the build's outline, easing back over
`Dressing.DRAPE_REACH` (32) blocks, and leaves water at the edge alone where it is the build's outflow.

**The drape's two guards, and the deck line.** `OUTLET_LIFT` (24, water at the build's edge that is outflow) is the
city's and reads aright for the village. `DECK_LIFT` (2, a floor tile over the planted corner) did not: the village's
floor is sixteen blocks over its base, so nothing outside its walls was ever read as a deck and the whole ring was
settled to the tops of the walls. That is fixed by the cut saying what its own floor is - a `deck 16` line in
`hobbiton.parts` - and the drape taking the number from the cut, with the city's two kept as the answer for a cut that
says nothing. The straight band down the south edge's water was seen in game and is not the village's wall: the drape
works there.

**The south edge's cliff - 17 November, fixed but unseen.** Read out of the owner's `Progress Check 1` world rather than
seen: at x -430..-438 the last row of the footprint (z -45) has the village's own ground at 97-98 and the drawn country
at 108-109 on the other side, so the boundary is an eleven-block raw face - the wall photographed from (-504, 101, -46).
It is the deck rule read the other way round: the drape never cuts outside a deck because cutting round a face the build
leaves open trenches the country, and a village that sits *below* its own country gets a cliff from the same rule.
`Dressing.BANK_BLOCKS` (3) now separates the two - a column standing three blocks or more over the build's own floor is
dressed like every other column, cut to the join at the wall and eased back over the reach, while a groove of a block or
two round a city's open faces is left alone, and nothing at or below the floor is cut. `Hobbiton.DRESS_VERSION` is 2, so
the next start re-raises and re-dresses the village; **nothing has seen the new rule run**, and the village's own walls
(stone-brick faces at y 114 over paving at 100, x -518..-525) are build, not ground, and are untouched by it.

**21 November:** the village was walked whole and this was not called out again; kept as the record of what the
drape does inside a footprint rather than as work owed.

**What has been checked, and what has not.** Every step compiled (`./gradlew compileJava -x createMinecraftArtifacts`)
and the mask work was measured against the game's own 30% water threshold. None of that round had been run in a world
when it was written. **21 November it was:** the owner walked `Work Verification`, the village raised on the world's
start before the portal was used, and found it right - joins, south edge and wooded drape with it.

**The cut.** The player's own map `Hobbiton_Divici` is a hand-built Shire in its overworld, marked by four ammolite
blocks at (‑456, 6, 276), (94, 6, 276), (‑456, 6, 743) and (94, 6, 743): **551 × 468 across, base y 6, content y 8…95**,
palette vanilla throughout - 148 names, no modded blocks - so nothing had to be remapped. It is cut whole into
`run/hobbiton_art`: 143 pieces of 48-cube plus `hobbiton.parts`, 14 MB, every block kept except the four markers
(`cut_world.py --without tolkienmobs:block_ammolite`), verified block for block against a separate count of the world -
5,079,392 distinct positions on both sides - and rendered top-down to confirm the village, the pond and the field strips
came across whole.

**The height.** The Shire stands at **y 102** where the build goes, not the 68 its cells are painted at: read in game at
(-631, -436) the bank is grass at 102. Measured against the drawn channel's own water - 96 there - the pin is **(-700,
87, -512)**: the build's water sits 9 over its base so it lands at 96, level with the channel, and its grass line (16
over its base) at **103, one block over the bank**. Footprint x -700…-149, z -512…-45.

**The river - rerouted west, the branch cut, and a Y south of the village.** Drawn 16 November into `rivers.png`,
measured against the game's own 30% water threshold, and the picture of it is `run/river_reroute/shire_y_interpretation.png`
with the script that made it beside it (`shire_y_plan.py`). What it does:

- **The bypass.** The drawn channel used to cross the footprint's north-west corner - its east bank at x -584 at
  z -472, inside by 116 - and leave by the west wall at z -190. It is now slid west there: the channel is cut at z -600
  and redrawn as a bend west to x -744 by z -404, easing back to its own course at z -204, so its east bank stands 20
  to 28 blocks clear of the wall over the whole crossing and 4 at the mouth, where the water is meant to meet it. The
  west arm of the village's river comes out at z -203..-176 against water at 95-96 - the join the pin was chosen for.
- **The branch.** From the south mouth (28 wide at x -234..-178, water 96, bed 86) a channel runs south-west along
  `(-206,-46) (-230,-6) (-270,40) (-320,80) (-380,112) (-450,140) (-520,165) (-590,190) (-652,212) (-712,232)
  (-770,246)`: **56 wide at the wall narrowing to 36** at the fork, one block of fall per 40 of run.
- **The Y.** The drawn river stopped dead at z 40 (water 80), so it is carried on south from its own end and the two
  meet at **(-770, 246)**, water 80-81, and run on as one channel south-west down the vale - 76 at z 300, 70 by z 400 -
  into the head of the bay at about (-1000, 508), where the heightmap is sea from (-910, 340) west.

**Why the branch needs code.** The village's water is 96 and the country straight outside its south wall is drawn at
105…113 (the drape settles the ring at the mouth back to 96-98, then the hillside is the drawing's). A drawn river's
water is laid at the ground, so a branch drawn there would stand thirteen blocks over the village's own water and the
south arm would end against its own wall under it. So the branch is the one drawn channel whose level is not the
drawing's: `ArdaMap.SHIRE_WATER` (96, the village's own - base 87 plus the nine its water stands at) less one block per
`SHIRE_GRADE` (40) of run along its course, read for any column within `SHIRE_REACH` (64) of that course and taken as a
minimum in `drawnWater`, so nothing outside the branch moves. Everything else follows from that number: the bed is cut
under the surface (85 at the wall, 78 at the fork), so the branch is a gorge thirteen deep at the wall that opens to two
at the fork, and the ground beside it is graded against the water that is really there.

**The fall at the corner is deliberate.** The ground west of the channel is 5-9 blocks over the channel's own line, so
the bypass's water is up to 7 over the river it leaves at z -600; the game's 24-block level reach cuts that into a short
chute at the corner, north of the build and out of the village's sight, and everything downstream of it descends
properly. Accepted when the route was chosen.

**The bar across the mouth - fixed in the drape, 16 November.** The save showed four to six blocks of land lying across
the water where the village's west arm meets the drawn channel. The mouth itself was left open on purpose - a pass that
fills the ground over a build's outflow dams it - but the ring's columns in front of a mouth clamp to the mouth's own
column, so every one of them read as water and was left alone with it, and the country's own bank stood eight blocks
out. `Dressing.openMouth` now takes the ring's ground out at a mouth: down to `CityCommands.MOUTH_DEPTH` (3) under the
water's top where the ground stands over that bed, and untouched where the column is already the drawn river, so the join
is the drawing's channel meeting the build's rather than a trench dug through the river to reach it.

**The gap in the water - filled by the same cut, 16 November.** The bar went, and left the bed behind it: the mouth's
water stands in the build, the drawing's channel stands in the ring, and between them lay a strip three blocks deep with no
water in it at all, so the branch came out of the north mouth and stopped. `openMouth` fills what it takes out back up to
the level the mouth carries the water out at - never higher, so the fill cannot stand as a wall of water over the country
- and the city's water and the drawing's are one sheet across it. **This is a drape change, not a world change: `/tmcut
drape hobbiton -700 87 -512` again in the existing world clears the bar and fills the strip.**

**The river's banks - a valley instead of a slot, every drawn river, 16 November.** The save showed the Shire's branch
as a canal in a trench: its bed is cut under water the code reads from the village, and nothing was ever told to bring the
hillside down to it, because a drawn river normally stands at the height of the ground it is drawn on and its banks are the
country. That is only true where the channel's level is the drawing's own, though: the same trench is cut wherever the
mountain mask lifts the ground over a river, and by the same cause. So `ArdaMap.riverBank` is the other half of every such
cut - the dale's `daleBank` with the water's own edge taken out of it.

It is a raster of the whole map, grown out from every cell the drawing marks as river water (lakes aside: their shore has
`SHORE_CUT` of its own) one cell at a time, seeded at water + 1 and rising `RIVER_STEEP_MIN`…`RIVER_STEEP_MAX` (3…16
blocks a cell, a third of a block to two per block of run) until the ground beside it is the country's own again, never
further than `RIVER_BANK` (192) out. One rate could not serve both ends: at a half a block per block a valley fifty deep
wants a hundred blocks of run, and a channel the mask has lifted two hundred blocks of ground over wants four hundred, so
the reach would either leave a face standing at its own rim or bite a quarter of a cell into every river in the world. The
rate is read per channel instead - the tallest country `RIVER_SAMPLE` (96) blocks round the water, less the water, divided
by the reach, held between the two - and rounded up, so the wall always reaches the country before the raster ends. Read
between cells like the dale's bank, with `RIVER_ROLL` (2) of roll faded by how much the valley actually takes off the
column, so a bank it shaves by a block keeps its edge.

The dale's water is left to the dale: `buildRiverBanks` seeds nothing where `ArdaMasks.daleRun` reaches, and where the two
rasters overlap the lower wins, so the Bruinen is graded once, by `DALE_RISE` toward `DALE_BANK`, and the Shire's branch
is graded by this pass alone - `shireBank` and its `SHIRE_SHELF`/`SHIRE_SHOULDER`/`SHIRE_BANK`/`SHIRE_ROLL` are gone, and
the `SHIRE_WATER`/`SHIRE_GRADE`/`SHIRE_REACH` level rule is untouched.

Measured on the drawn ground against the ceiling. The branch: at z 40 the ground comes down from 108 at x -380 to 94
beside water of 93…96 and climbs back to 105 by x -220; at z 140 from 112 to 89 and back to 104 by x -260. An ordinary
drawn river, at z -560 and z -620 west of the build, is untouched: the ceiling stands 1…10 blocks over the country along
the whole of both banks, so nothing is graded. The deepest lift over river water in the world - 204 blocks, at (15840,
7616) - is cut into a valley and closes to within a dozen blocks of the 285…301 country at the raster's rim. **This is
world generation: it needs chunks made after it.**

**The village stands itself, and the towns have names, 16 November.** The Shire was three commands a hand had to type
in the right order, with a world rebuilt in between if the river had moved - which is how a build is tested and no way
to ship one. `Hobbiton.autospawn` raises it on the first start of a world, from the cut shipped in the jar at
`data/tolkienmobs/structure/hobbiton/` (143 pieces and the table, fourteen megabytes beside the city's), at
`ArdaMap.hobbitonPlan()`: the cut laid, the country cleared out of it, and the ring dressed, a band of columns and a
few pieces a tick, because seven hundred blocks of ring in one call is a stall at the exact moment a player is waiting.
The dressing is the city's own `Dressing` job, stepped by the village - a second drape rule for a second build is how a
country ends up with two ideas about where a wall stops. A note beside the cut (`_stands.txt`) is what says the village
is up, and it says *what* stood it as well as where: the cut's own fingerprint (its piece count and a CRC of its table,
`143-fe676c68` as shipped - the table lists every piece, its corner and its size and the `deck` line, so a re-cut, a moved
piece or another floor all read differently, and the whole answer costs a few hundred bytes at a start rather than the
fourteen megabytes of the pieces), a `DRESS_VERSION` a hand bumps when the drape's rules move, and seven probes of the
ground: the top of a column and the block it stands on. A start that finds the print or the version changed raises the village again,
so a re-cut and a ring fixed in code now reach a world that already holds a village instead of only worlds made after
them; a start that finds most of the probes moved raises it too, which is what catches chunks trimmed or a world replaced under
the note; a column counts as moved when its top has moved more than six blocks or the block it stands on is not the block
the note wrote, and most of the seven have to have moved, so one grown tree or one trampled grass block does not cost a
village. The probes read only chunks the level already holds - asking the level for one would generate it on the
start-up thread, the thing this whole pass exists to avoid - so a start that holds none leaves the question to the first
tick somebody is near enough to make it free. The three commands still work and are still the way to re-cut
one.

**The village's own folk, and the hill they stand on, 16 November.** The chunks under the village are the Shire's, so the
game populated them as they were generated - on ground the pass then cleared and laid again. The first raises paid for
that: **eighteen** `Hobbit hit the ground too hard` deaths in one of them, bodies falling with the hill they stood on. A
spawn *rule* is the wrong gate there, because a chunk-generation spawn is finalised before it has a position: the body is
refused as it joins the level instead, while the pass holds the footprint, which is the first moment both the body and its
ground are known. With that gate the same raise logs **no deaths at all**, and runs a little quicker for not carrying
them - 920 ticks (46.0 s), against 1242 (62.1 s) on the run before. Once the ground has settled, `Hobbiton.inhabit`
stands a deliberate handful: **eight** of the Shire's own folk, on columns whose top is within a block of the cut's own
deck with room to stand and something to stand on, so roofs, tree crowns and the hillside outside the walls are passed
over. They are persistent, so a player who comes back finds the same village, and a village raised again over itself is
topped up rather than doubled. The line that says so is part of the measurement: `8 of the Shire's folk are standing in
the village at -700, 87, -512, 0 were there already`.

**The village's folk are somebody, 16 November.** The old pack's clone database holds twenty-two hobbits and fifteen
skins under `customskins/hobbits`; thirteen of those are modern 64x64 art that sits on this model (the two legacy 64x32
ones are left out rather than drawn wrong), so the village now keeps thirteen, one per face. Each is stood with a name
taken from the pack - Belba Butcher, Gararic Rumblebelly, Gomatrudis Proudmead, Ingunde Silentfoot, Isembard Underlake,
Leufroy Swiftfoot, Marcus Hairyfoot, Nick Greenhill, Godomar Barrowes, Amanda Brockhouse, Melissa Headstrong,
Regnetrudis Fallohide, and Hal Cotton, the one name that is ours because the pack's seventh male body has a skin and no
name on it. The name is what finds the face: `HobbitRender` already asks for `middle-earth/<slug>.png` for any named
hobbit, the same rule the tall folk are drawn by, so the thirteen files were copied in beside Frodo's and Bilbo's and no
renderer changed. A name already in the village is not stood twice, so a village raised again over itself keeps its
people. The line now says who: `13 of the Shire's folk are standing in the village at -700, 87, -512, 0 were there
already - Amanda Brockhouse, ...`.

The water out of the village is no longer a rule named after the Shire. `ArdaMap.Channel` is a drawn channel whose level
is a *build's*: the level it leaves at, the grade it falls at, the reach of its corridor and the course the mask drew,
one entry per place, with the village's level read as its pin plus the nine its water stands over it. A second build
with a channel out of it is one more entry and no new code. The Shire's own levels are unchanged: 96 at the wall, one
block in forty of run, 80 at the Y.

The towns are `Places.TOWNS` - Hobbiton, Bree and Rivendell - and a sign board now cycles only those the world has
actually stood, instead of every planted name in the catalogue including barrows and a scout's fire. Rivendell and
Hobbiton are stood at their pins on the first start (`Places.standTowns`), with a `TOWN_RADIUS` of 280 rather than a
camp's 32, so walking into either from any road announces it. **Bree has a catalogue entry, a name and a place among the
towns, and no ground yet** - the first thing it needs is a build and a pin.

`/tmriver at` and `/tmriver along <from> <to> [step]` print what the drawing and the grading make of a column or of a
line of them - ground, water, the valley's ceiling, and how much the valley takes off - read out of `ArdaMap` itself
rather than re-derived in a script. That is the instrument for the next round of tuning, and for the rivers the owner
walks past as the map goes on.

**What the rise costs, measured, 16 November.** A dedicated server on a fresh level, nothing else running, the pass
asked to raise the village with nobody in the world: **1326 chunks of ground** under the 551x468 footprint, asked for
two at a time on tickets and waited for over 1996 ticks; **143 pieces laid in 9.8 s** (68.6 ms a piece, worst 349 ms);
**1290 bands of 200 columns cleared - 257,868 columns and 1.99 million blocks - in 9.5 s**; **701 dress steps in 3.3 s**;
the whole pass about 60 s of wall clock over 1200 ticks against a 5 ms budget. Three ways of doing it were measured and
thrown out. `setChunkForced` on every chunk in one tick generated all 1326 on the server thread and held a tick for
**49 s**. `ServerChunkCache.getChunkFuture` is worse than it sounds - it parks the main thread on the worker pool
(`BlockableEventLoop.managedBlock`) and held a tick for the watchdog's full **60 s**. Laying the cut with the plain
`2` flags ran a shape update per block, which asked the level for the neighbour chunk each time: **11 s** of laying.
The one long tick that is left is **43.2 s**, the game finishing the 1326 newly generated chunks' sections and light in
one go - the same 43 s that used to land in the first piece of the cut (`hobbiton_0_0_0.nbt`, 42.3 s). It was paid in
one tick because the whole region had been generated before anything was written into it, and the drain is the
engine's. The pass now asks for a chunk and takes it in the same tick, a chunk at a time, so nothing is ever pending
for the region as a whole. **That shape is measured now, 16 November, and it holds.** `raiseShireOnStart` in the mod's
server settings takes the gate off at start, and `tools/raise_shire.sh` boots a level of its own with the switch set,
waits for the line, prints the numbers and puts `server.properties` and the config back afterwards. On a fresh level
with **nobody online** the village stood in **1200 ticks (60.0 s)**, and 1210 (60.5 s) on the run before it: a warm
build, 1326 chunks of ground, 143 pieces, 1290 bands and 701 dress steps, and no tick went near the sixty-second
watchdog. That is the run that was missing, and
it is one command away from being repeated.

**The switch is on by default now, 20 November.** The portal out of the overworld opens on the village's own crossing,
(-470, 97, -123), which is inside the footprint the pass clears - x -700..-149 by z -512..-44 - so a village raised by
an arrival is a village that takes the traveller's doorway with it: the pass begins on the first tick after the crossing
is used and the ammolite doorway is gone about ten seconds later. Raised at the start it stands about a minute into a
fresh world, before anybody can walk through to it. **And a start is exactly when a server is empty**, so the pass lifts
the server's own `pause-when-empty-seconds` while it runs and puts it back when the village stands: measured on a fresh
level with the property at 5 s and nobody online, the log reads `Server empty for 5 seconds, pausing` part way through
and the pass never takes another step - the paused branch of `MinecraftServer.tickServer` returns before the tick event
and before the level's own tick, so there is nothing for the pass to be woken by. The lift is
`DedicatedServer.setPauseWhenEmptySeconds(0)` for the pass's length, given back on the pass finishing, on an unreadable
cut and on the server stopping with the pass half done, so `server.properties` is written twice and ends as it began. A
server killed outright mid-pass keeps the zero, which costs it its idle pause and nothing else - as it keeps the level's
forced-chunk tickets until the next start re-runs the pass.

A world that sets `raiseShireOnStart = false` keeps the older rule: the village is raised only while somebody is within
`REACH` 192 of it, so the chunks are in a view, ticking and being finished as the player arrives. `/hobbiton raise`
still asks for it at any time in either world, and a world that already holds the village is left alone. **Not seen in a
client session**: whether the deck, the mouths and the ring look right is still a walk to Hobbiton, and 60 s is wall
clock on a warm build, not a bound on a cold one. The pass keeps its own tick count, because the measurement is the line
that says it stands.

**Where the roads go next, and it is not built.** The Shire to Bree, and Bree on to Rivendell - two routes for
towns that have no road between them, now that Gundabad's road joins the old forest road at 8583, -3617. Nothing of
either is laid; the Shire's own ring is dressed with no road, because `Roads.forGround` answers for the valley and a
route to a village that had no ground until this week is a route planned against a map that did not have it in.

**What has not been checked.** None of the river work has been seen in a world: the bypass's new bend and its fall, the
branch's valley and its mouth at the wall, the fork, and the merged channel's run to the bay are all unbuilt territory.
A world made before 16 November holds the old course and cannot show any of it. The bar's fix is the one piece that can
be seen in the existing world, by running the drape again.

## The portal between the worlds - built 16 November

The ask, in the owner's words: **a portal made in the overworld puts the traveller in Hobbiton, and the way back puts
them at the overworld's spawn point.** Not the same coordinates on both sides, which is what the portal used to do.

It was one method, as expected. `ArdaPortalBlock.getPortalDestination` used to take the traveller's own x and z as the
crossing in the other world; it now takes a place:

- **overworld to Arda:** the crossing is `ArdaMap.hobbitonGate()` - (-470, 97, -123), the gravel of the village's own
  crossing that the owner walked to and gave as the Shire's arrival.
- **Arda to the overworld:** the crossing is the overworld's respawn data (`ServerLevel.getRespawnData().pos()`), which
  is the world's spawn point, so the way home is always the same doorway.
- Both ends then do what they always did: a portal standing within 16 blocks of the crossing is used, and if there is
  none one is built and lit on the ground there, so a traveller never arrives somewhere with no way back.

One thing left, and cheap to change when it is next looked at: the arrival doorway is built of ammolite (`buildExit`), so
a doorway that is auto-built inside the Shire looks like the hold's rather than the village's. The second half of this
note is settled - the village is raised at the world's start now, so there is nothing to arrive ahead of.

## The Shire's milestone - built 16 November

The travel stone of the village stands at the paving the owner marked, (-503, 101, -314), in the village's north-east
where the paths meet: `ArdaMap.hobbitonMilestone()`, stood with the hold's stone on every start
(`MilestoneHandler.onServerStarted`). That mark is gravel rather than one of the mod's ammolite markers, so the stone is
tried in the spot and then on top of it, and the paving is not dug up for it. A world older than the stone gets it on its
next start, and one that has it keeps it.

## The river that did not drape - settled, not a fault

The owner pointed at (-265, 98, -72): stepped grass terraces running down into water on the village's north-east, straight
edges and a sand lip - country that was cleared and never settled. It is inside the footprint, not in the ring, and that
is the reason: the drape settles the columns *outside* the cut's box, while inside it the clear takes each column down to
the cut's own floor and leaves it, so any column the cut does not fill - the river corridor running through the north-east
of the village - is left as a staircase beside the water. Settling those columns too is a change to the drape's own rule,
it wants `DRESS_VERSION` bumped so the fix reaches villages that already stand, and it would be re-run over whatever the
river's mouths look like now.

**Settled: it is not a fault.** The owner confirmed the terraces read as the village's own bank rather than as ground
that was missed, so the drape's rule stands as it is - no `DRESS_VERSION` bump, no second pass over the mouths, and
nothing owed at -265, 98, -72. The paragraph above is kept as the reason the rule is what it is.

## The old forest road - history, since re-planned from its two ends

This is the first road the walk laid and called right in game, and its walked form is gone: `Roads.ROUTES` holds no
hand-written route now, and the same road is `tolkienmobs:roads/planned_11813_-3901_8229_-3654` in `Roads.NETWORK`,
planned from its two ends against the drawing. What it laid is worth remembering, because the plan is judged against it:

- **The walked road.** 8231, -3641 to 11842, -3877, three thousand eight hundred blocks, five wide, the hundred and one
  ammolite marks each a bend, graded on halves so the terraces are stairs rather than jumps.
- **Mirkwood.** The road walked up into the canopy until the nine hardened leaves were tagged `minecraft:leaves`
  (`0e5e98fb`); it walks the floor now.
- **The two crossings, as they were stood** - nine-wide decks of `bridge_end`/`bridge_middle` with oak-log piers drawn
  to the bed. These are the old walked bridges; the plan now spans the same rivers at 8551, -3626 (46 wide, deck 188)
  and 11649, -4080 (52 wide, deck 170), which is the geometry a fresh world gets.

| crossing | old route | banks (east → west) | deck | abutments' base | middles' base | water |
| --- | --- | --- | --- | --- | --- | --- |
| Old Ford | `OLD_FORD_BRIDGE` | 8520, -3668 → 8481, -3668 | 189 | 188 | 187 | 186 |
| Forest River | `FOREST_RIVER_BRIDGE` | 11775, -3841 → 11736, -3841 | 169 | 168 | 167 | 167 |

A road already laid keeps its heights, so the old decks stay where they are; `/tmroad forget` at the crossing and its
camp end is still how a leg is re-laid. A span is planned as a `Route.Crossing` from its banks, and a road leaves water
to it within `CROSSING_ACROSS` (12 blocks) of the span's line. `docs/arda-roads.md`, section *Water a road was told
about*, has the design.

## Quests - chapter two

Eleven pages, 26 to 36, written and wired; **played through page 32** and working. The page table is in
`docs/chapter-two-state.md`. What a session needs:

- **Gundabad's bodies:** Gimli in his own cave east of the gate, on the room's dais (5703, 188, -6676); the captain on
  the throne (5546, 204, -7036) with the wraith three blocks along (5546, 204, -7039) - both placed from
  `ArdaMap.keepThrone()`/`keepThroneWatch()`, so they follow the hold.
- **The gate and cave** stand themselves as the server comes up; the coal lattice door is x 5575-5581, y 190-199,
  z -6856 and opens for `item_keystone` within 12 blocks.
- **The keep** stands itself, and its placement is **settled at `KEEP_FLOOR` 176** - its rooms' floor 189,
  level with the passage - and the walk from the gate's hall to its own door is **signed off on 21 November**
  (item 5).
- **Pages 33-36** are Carn Dûm ground and are paused (items 1 and 3).

**Red for the door, green for the boards - 16 November.** Two more inks, both the drawn gold art with its three shades
re-read as a ramp, so the outline and the light sit where the artist put them: `available_locked` (red `!`) and
`board_available` / `board_turn_in` (green `!` and `?`), codes ``-`` of the same font. Red is the hall's own
page in a herald's hand while the player is below `STRANGER`: the mark used to be silence there, and silence is the one
reading nobody can tell from a hall with no work left. It asks `hallPage` - the herald's own front, not a speaker's page
down the road - so a red mark cannot stand over a body who would hand his page over freely. Cyan keeps its meaning and is
now the ink of the wandering traveller's errand, which is what `QuestGivers` has always spawned.

**A mark over the notice boards - 16 November, built.** The board had no state that could carry a mark, since the paper
lives in the player's pack and a blockstate is the same for everyone looking at the block. It therefore grew an empty
block entity (`notice_board` type, no data, no ticker, no packets) purely to have a renderer, `NoticeBoardMarkRenderer`,
which hangs the mark as a **nameplate** through `submitNameTag` - so the billboard, the distance scaling and the hiding
behind ground are vanilla's, not ours - 2.35 blocks up, clear of the frame's top rail. What it says comes from
`BoardMarks`, which reads the reader's own inventory: no contract means green `!`, a finished one means green `?`, and
work still in hand means no mark, since the board only repeats that it is carried. It answers to `questBadge` too, the
same client switch as the scroll over a giver's head (on by default), so a player who would rather the world carried no
marks turns one setting off and the boards go bare while the plates read as they always did. Existing boards in a world
get the entity when their chunk next loads.

**The marks are the artwork, not letters - 16 November.** The sheet the owner drew (`quest-marks-v0-....webp`, two shapes by
three inks) was cut into six icons on its own pixel grid - a 4x11 `!` and an 8x11 `?`, black outline and three shades of
fill - and each icon is a glyph of a font of its own: `assets/tolkienmobs/font/quest_marks.json` pointing at
`assets/tolkienmobs/textures/font/quest_marks/<shape>_<ink>.png`. The plate names one glyph and puts the mark font on it
(`FontDescription.Resource`), so what it draws is still a letter and the nameplate is untouched in kind - no render pass,
no texture pass, no culling to write. White is work in hand, gold the mainline, cyan an errand; the shape is still the
state. The white `?` has no state that can wear it, since work in hand is by definition the `!`, so it ships with its code
(``) reserved. Two things are unverified because no client has drawn them: the size (11px, about 1.4x the name) and
the seat (ascent 10, one pixel below the baseline).

**The badge over a giver's head - built 16 November, off by default.** The cheap half of the item-over-head idea: a scroll
(`item_scroll`) hung on the giver's own head bone, drawn by the same `BlockAndItemGeoLayer` the hand item already uses, and
shown for the turn-in state only. It goes in that layer rather than a second one because every `BlockAndItemGeoLayer`
shares one data ticket (`CONTENTS`), so two of them on one render state would each read the other's list - the badge is a
second entry in the same list, and `submitItemStackRender` picks its pose by bone name: `head` lifts it 1.15 and draws it
at 0.65 scale, the hand keeps its own offset. `TolkienMobsConfig.questBadge` (client, default true) is the switch for every
mark the world draws - the notice board's as well as the scroll - and the plate's own mark is never touched by it, so the
text `?` and the scroll can be put side by side; the state it reads is `QuestMarkRenderer.asksForReturn`, which asks the
same call the plate asks, so the two marks cannot disagree. It is bone-locked: it turns and ducks with the head and the
ground hides it the way it hides him, which is the trade for not building a billboard pass. **Not looked at**: no client
ran, so where the scroll actually sits over a man's head and a hobbit's - and whether 0.65 at 1.15 reads at ten paces - is
unverified.

**The mustering halls are pinned in their towns, not generated - 16 November.** There is one hall per alliance and a
dozen towns to come, so a hall scattered by a spread stood in a field before the town that owns it existed. The three
definitions are gone from `TolkienStructureProvider.STRUCTURES` and with them the generated structure, structure set,
template pool and `has_structure` tag files (`runData` regenerates the rest cleanly); the templates stay in
`data/tolkienmobs/structure/faction_hall_<alliance>.nbt`. In their place is a pin per town: `/tmpin hall add <town>` puts
the hall where the caller stands and prints the `Pins.SOURCE` line, `/tmpin hall remove <town>` forgets it, and the town
argument is suggested from the catalogue's towns - Hobbiton, Bree, Rivendell - rather than typed freehand. The key is
`hall:<town>` (`TownHalls.key`) and the template is chosen from the town's alliance, which is the one thing the caller
does not have to say.

**The hall stands itself - 17 November.** `TownHalls.raise` reads the pin and lays the town's own template, unturned,
with its ground course in the block the builder stood on (the pin is then the hall's floor, not a slab one course above
it), asking for every chunk the box touches first and leaving a hall that already stands alone. It waits for its town,
because a town's pass lays, clears and dresses the ground a hall stood early would be taken away with: `TownHalls.waits`
asks `Hobbiton.complete` (the village's note, written as its pass ends) and `CityCommands.complete` (the valley's marker
at its last band). Three hands raise it - the village at the end of its own pass, before its folk are stood so nobody is
placed inside a wall; the next start, for a world whose town already stands; and `/tmpin hall add` itself, when the town
is finished, which says so in the reply and otherwise says the hall is waiting for the town. **Not looked at:** no hall
has stood in a town in game, so the seat, the 16x16 box against the village's own paving, and the front of the build are
all unverified.

## Settled - do not re-open

- **The Gundabad gate is final.** `GundabadGate.autospawn` stands it at `ArdaMap.gatePlan()` and leaves a standing gate
  alone on two answers: the netherrack course, and the pieces' own blocks through `Sites.standsAt`. `/tmgate place`
  re-lays; `clear` is the way back.
- **The captain of the Dourhands is down once, and stays down.** `DUGORIM_DOWN_TAG` on the hand that fells him; seen in
  game, felled again, did not come back.
- **The wraith was never broken.** Its mark was always in place and it behaved - killed once, gone.
- **The inner keep's stair is final and open.** No door goes over it; the hold is a place to explore, page 29 stays
  dialogue.
- **The chapter-one seam is final.** The courtyard scene between pages 25 and 26 ran smoothly as staged;
  `docs/departure-scene.md` is a record, not a task.
- **The spikes are settled** as decoration with incidental damage.
- **The river's north-east bank is settled - not a fault.** The stepped terraces at -265, 98, -72 are the drape's own
  rule working: it settles the columns outside the cut's box and leaves those inside it to the cut. Confirmed in the
  world, so the rule stays and nothing is re-dressed.
- **The biome audit is closed whole** - eleven findings applied, the Barrow-downs' deadwood kept on purpose, the four new
  leaf textures called right. `docs/arda-biome-canon.md`.
- **The keep's placement is settled at `KEEP_FLOOR` 176** (18 November, x 5528 since 21 November). `ArdaMap` carries
  `KEEP_X` 5528, `KEEP_Z` -7112, `KEEP_FLOOR` 176 - the rooms' floor 189, level with the passage - with the box x 5528…5649,
  z -7112…-6930 and the south wall at -6930 where `keepWall()` ends the hall; `GundabadKeep.autospawn` reads the
  template's own blocks through `Sites.standsAt` and carries the hall to the keep's wall every pass. The x moved twelve
  east on 21 November so the hold's own doorway stands in the chamber, and a fresh world walked on 21 November found it
  there - with the passage between thirteen blocks short, because the hall's clear was addressed to the wrong cells. The
  coordinates are fixed (see the 21 November paragraph), a world that already holds the hold is re-cut on its next
  start, and the walk on 21 November found the door open at the end of it.

## On hold, and what unblocks each

- **Sign posts** - the towns are the filter now (`Places.TOWNS`, stood at their pins on the first start), so a board
  cycles the blank and Hobbiton / Bree / Rivendell and nothing else. What is left is the placer: a post stood at a
  road's junction, its arm turned to the side the town lies, and `setDestination(board, place)` called with the
  distance measured for it. Nothing places posts today, and no posting pass waits on it - a hand-placed post already
  reads right.
- **Chinese** - unblocked by a public release; the rework's lines are translated then, not line by line.

## Not started

- **The Shire to Bree, and Bree on to Rivendell.** The two town roads nothing carries yet. Gundabad's road is in
  `Roads.NETWORK` now, running from the gate at 5578, -6769 to the old forest road at 8583, -3617, so the hold no
  longer waits on one. Bree has a catalogue entry and no ground, so its end waits on a build and a pin first.
- **Chapter three.** Eleven pages (3-1 to 3-11) written, nothing in `Campaign`. Before code it needs ground, bodies and
  objects: Mitheithel, Stoneyford, **the Watchers**, the dead stones, Dol Guldur and the Moria camp; five bodies
  (Aragorn, Guthwine, Manwyn, Narmos, Rurain) and the wight as the Ancient Spirit; two props and one charm. Its first
  question decides the rest: **whether Stoneyford and the Watchers are built here or already stand in the map's world.**
- **Chapter four onward**, the finale (`FINALE_OPEN = false`, one front in `FRONTS`, closed by design), and the notes'
  job boards (quest 37).

## Small, recorded, harmless

- `Places` canon's the place `gloin` to `HITHAEGLIR` while the camp's site is in `GLADDEN`. Nothing targets
  `placeTarget("gloin")` today; a page that did would point into the mountains.
- `Wet.FORD` and `carry` are unused now that both crossings are bridges - kept with the path intact for a future ford.
- A road already laid is never taken back: `surfaceAt` reads the old embankment as ground, and `forget` clears only the
  saved alignment and grade.

## The rules this project is walked by

- One `CHANGELOG.md` bullet per change, in the long `- **Claim.** …` prose.
- Commit to `main` from `TolkienTweaks/`; **never push**.
- "Verified" means `./gradlew compileJava -x createMinecraftArtifacts -q --no-build-cache` exits 0, and the answer always
  says what was **not** seen in game. Never claim a playtest.
- Java files are CRLF; docs are mixed - check before writing - and patches go through a `build/` script that is deleted
  after.
- `runData` cannot run while the client is open, and `src/generated/resources/.cache` has to be deleted or it writes
  nothing.
- `docs/HANDOFF.md` is the long history and is worth reading before changing a system it explains.
