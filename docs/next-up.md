# Next up

The working state of the campaign, written down so a fresh session does not have to guess at it. Read this first,
then `docs/chapter-two-state.md` for the chapter being walked and `docs/chapter-three.md` for the one after.

**Last touched:** 15 November 2026 - the old forest road laid east of Glóin's camp, and its two crossings standing as
bridges.

## Where the roads stand

Laid and seen in game, called right by the player:

- **The old forest road.** `OLD_FOREST_ROAD` in `Roads.ROUTES`: 8231, -3641 to 11842, -3877, three thousand eight
  hundred blocks, five wide (`Kind.WILD_WIDE`), every one of the hundred and one ammolite marks a bend. Graded on
  halves - a step is 0.5 blocks and the slab *on top* of the road block is the half (`MAX_STEP`), so the terraces are
  stairs rather than jumps. "Definitely smoother" was the word for it.
- **Mirkwood.** The road climbed into the canopy because `TolkienHardenedLeavesBlock` is a plain `Block`, so nothing
  had tagged it: the no-leaves heightmap the road reads its ground from counted the hardened leaves as ground. All nine
  hardened leaves are in `minecraft:leaves` now, beside their own leaves (`0e5e98fb`), and the road walks the floor.
- **The Old Ford's bridge.** Stood and seen: a nine-wide deck on `bridge_end`/`bridge_middle` pieces with oak-log piers
  drawn to the bed. The first thing it did was read as a low culvert, so it and the Forest River's were both raised a
  block.

Compiled and **not yet seen** (`ed220cc9`): both decks one block up and their bases with them.

| crossing | route | banks (east → west) | deck | abutments' base | middles' base | water |
| --- | --- | --- | --- | --- | --- | --- |
| Old Ford | `OLD_FORD_BRIDGE` | 8520, -3668 → 8481, -3668 | 189 | 188 | 187 | 186 |
| Forest River | `FOREST_RIVER_BRIDGE` | 11775, -3841 → 11736, -3841 | 169 | 168 | 167 | 167 |

**The next move is the rebuild, and it is three runs of one command:**

1. At each crossing, `/tmroad forget` then `/tmroad build`. The sweep clears the span's old timbers before it stands
   the new pieces, so a forgotten and rebuilt bridge is one deck, not two stacked.
2. At the road's camp end (8231, -3641), `/tmroad forget` then `/tmroad build`, so the approaches re-grade up onto
   189 and 169. A road already laid keeps its saved heights otherwise.

What to look at: whether each deck sits flush on its bank (that is the whole point of the raise), whether the piers
reach the bed under both spans, and whether the road now steps *up* onto the deck from either side.

### How a crossing works now

A span is not something a road lays. It is a `Route.Crossing` - its banks, its deck, the base its pieces stand at and
the pieces themselves - and it is built by the same pass machinery as `GLOIN_BRIDGE` and `HIGH_PASS_BRIDGE`. Two
consequences worth knowing before touching them:

- **The pieces are stood from the near bank down the span's axis**, so a crossing is written from its east bank to its
  west one whatever direction the road walks it. Declaring it the other way round stands the middles off the end.
- **A road leaves the water to the span.** The pass asks `Roads.water(x, z, across)` - which answers from the routes
  themselves, not from a table kept beside them - skips any water column on a crossing (a ford, `Wet.FORD`, is still
  the road's own bed and is laid by `carry`) and pins every station of itself standing in that water to the crossing's
  `deckY`, so the bank sweeps up onto the deck half a block a station. The reach is `CROSSING_ACROSS` = 12 blocks off
  the span's line: the line is the scout's and the road is a search between marks, and matched within three blocks
  neither crossing was met at all.

`docs/arda-roads.md` carries the design; the section *Water a road was told about* is the one that changed.

### Not started

**The roads from Glóin's camp to Gundabad.** Asked for on 15 November, not begun. `ROUTES` stops at the old forest
road: `GLOIN_ROAD_EAST` runs 6493, -3662 → 8234, -3590 and the camp's own spur is at 8215..8242, -3674..-3553.
Nothing goes west or north toward the hold at `ArdaMap.keepPlan()` (5471, 176, -7100), whose gate approach is around
5577, -6830 - and pages 2-1 to 2-3 are a walk a player takes, so the walk needs a road under it: a new `Route` (or
legs of one, with a `Route.Crossing` for any water on the way), pinned in `Pins.SOURCE` if it needs a mark.
`/tmroad` is the tool. **Work the line out from the map; do not guess it from these numbers.**

## Quests - chapter two

Eleven pages, 26 to 36, written and wired. **Nothing in the chapter has been played end to end.** The full list is
`docs/chapter-two-state.md`; what matters to a session picking this up:

Standing, compiled, unseen: the shelter camp (seen), the goblin cave cutting and dressing itself as a player nears, the
Gundabad gate and its chamber, the keep at its plan, Gimli at the dais (5703, 188, -6676), the captain on the throne
(5501, 193, -7024) with the wraith three blocks south, the coal lattice door at x 5575-5581, y 190-199, z -6856
(opens for `item_keystone` within 12 blocks), page 2-5's vault, and `DugorimFight`'s boss bar and plate.

**Open, in the order worth taking them:**

1. **Walk 26 → 36 in a fresh world.** Watch: that the camp reads as a camp; that Gundabad's site search finds ground
   level enough for a 122-by-183 footprint in the Hithaeglir; that the stair reads as the way down; and that Carn Dûm's
   border camps are findable from the page's direction.
2. **The inner keep's stair is not gated.** It is open, so a player can walk down before the plans allow it; page 29 is
   the beat that would open a door over it.
3. **The chapter-one seam is not staged** - *Hasten Their Departure* is a walk back to the library; if the company is
   ever shown leaving it belongs between pages 25 and 26, and `docs/departure-scene.md` has the scene recovered.
4. **Chinese is deferred** for the lines the rework changed, as with chapter one. Last pass, not a running cost.

Smaller, recorded and harmless for now: `Places` canon's the place `gloin` to `HITHAEGLIR` while the camp's site is in
`GLADDEN`. Nothing targets `placeTarget("gloin")` today, but a page that did would point into the mountains.

Not built at all: no chapter three onward; `FINALE_OPEN = false` and `FRONTS` holds `Alliance.WEST` alone, so the
Rider → castle → arena → Witch-king road stays closed by design; the notes' job boards (quest 37) are not started.

## Quests - chapter three

`docs/chapter-three.md` is the whole of it: eleven pages (3-1 to 3-11) from the pack's `CH3-Fires in the South`
(quests 56 and 60-69, dialogs 200-228), prose written for each. **Nothing of it is in `Campaign`.**

Before any code it needs ground, bodies and objects: Mitheithel, Stoneyford, **the Watchers**, the dead stones, Dol
Guldur and the dwarf camp in Moria; five bodies (Aragorn, Guthwine, Manwyn, Narmos, Rurain) and the wight as the
Ancient Spirit; two props and one charm. Its own open questions are at the foot of that file, and the first decides the
shape of the rest: **whether Stoneyford and the Watchers are built here or already stand in the map's world.**

## Loose ends outside the quests

- **Sign posts.** Four blocks - `sign_post_plinth`, `_shaft`, `_cap`, `_arm` - plus `sign_post_arm_double` for a place
  that sends a traveller two ways: one cell, two boards, `level` 0 or 2, a plain click steps the lower destination and a
  crouching click the upper (`destination2`/`distance2`). All bedrock-hard, no recipe, no drop. The generated item
  definitions have been written by a datagen run since. Not seen in game, and worth watching: whether the two lines are
  legible inked in black on the board's own faces, whether the arm's four `y` rotations are the intended ones (south 0,
  east 90, north 180, west 270) and the flat end meets the shaft from every face, whether the cap's two pixels line up
  with a shaft's top of 16, and whether the double board reads as one sign rather than two.
- **Spikes.** `spike_stone`, `spike_wooden`: eight stepped plates, drop themselves, two points of general damage on the
  game's hurt timer. Not seen: whether two points a half second reads as a nuisance rather than a wall, whether the
  stepped shape is walkable rather than a trip hazard, and whether the stone half of the sheet lines up.
- **The seam scene** - the company leaving Rivendell. Recovered in `docs/departure-scene.md`; nothing built. Two things
  are owed by the owner before it can be: the marker set with its legend (one spot per body, plus the road the nine
  leave by) and eight faces - `bilbo`, `gandalf`, `aragorn`, `boromir`, `frodo`, `samwise`, `merry`, `pippin`. Elrond,
  Legolas, Gimli and Glóin are already drawn.
- **The Gundabad gate** is finished (`gate-complete`) and lays itself as the server comes up, but is reached only by
  `/tmgate place` - no pin raises it, so the "no commands on the map" rule is not met for it yet. The keep is planned
  underground at (5516, 187, -7112); two flags must change before a page may raise it, `ON_THE_PIN` and
  `SELF_DRESSED`, or the seating pass lifts it 78 blocks onto the crown and the sweep quarries a 122 × 183 pit.
  `docs/gundabad-gate.md` and `docs/gundabad-keep.md` have the rest.
- **The biome canon audit** - twelve findings in `docs/arda-biome-canon.md`, **none applied**. Waiting on a decision,
  not on work.
- **A road already laid is never taken back.** `surfaceAt` reads the old embankment as ground on a re-lay, and columns
  the old road paved or widened stay as they are; `forget` clears only the saved alignment and grade. Only pristine
  ground shows a grading change.
- **`Wet.FORD` and `carry` are unused** now that both crossings are bridges - kept, with the code path intact, for the
  day a crossing wants a ford rather than a span.

## The rules this project is walked by

- One `CHANGELOG.md` bullet per change, in the long `- **Claim.** …` prose.
- Commit to `main` from `TolkienTweaks/`; **never push**.
- "Verified" means `./gradlew compileJava -x createMinecraftArtifacts -q --no-build-cache` exits 0, and the answer
  always says what was **not** seen in game. Never claim a playtest.
- Java files are CRLF; patch through a `build/` script and delete it after.
- `runData` cannot run while the client is open, and `src/generated/resources/.cache` has to be deleted or it writes
  nothing.
- `docs/HANDOFF.md` is the long history - how each of these came to be - and is worth reading before changing a
  system it explains.
