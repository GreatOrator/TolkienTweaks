# Next up

The working state of the campaign, written down so a fresh session does not have to guess at it. Read this first,
then `docs/chapter-two-state.md` for the chapter being walked and `docs/chapter-three.md` for the one after.

**Last touched:** 15 November 2026 - the old forest road laid east of Glóin's camp with both crossings standing as
bridges (final), the Gundabad gate finished, and the sign posts put on hold until there is a second town to point at.

## Where the roads stand

Laid and seen in game, called right by the player:

- **The old forest road.** `OLD_FOREST_ROAD` in `Roads.ROUTES`: 8231, -3641 to 11842, -3877, three thousand eight
  hundred blocks, five wide (`Kind.WILD_WIDE`), every one of the hundred and one ammolite marks a bend. Graded on
  halves - a step is 0.5 blocks and the slab *on top* of the road block is the half (`MAX_STEP`), so the terraces are
  stairs rather than jumps. "Definitely smoother" was the word for it.
- **Mirkwood.** The road climbed into the canopy because `TolkienHardenedLeavesBlock` is a plain `Block`, so nothing
  had tagged it: the no-leaves heightmap the road reads its ground from counted the hardened leaves as ground. All nine
  hardened leaves are in `minecraft:leaves` now, beside their own leaves (`0e5e98fb`), and the road walks the floor.
- **Both the old forest road's crossings.** Final as of 15 November: nine-wide decks on `bridge_end`/`bridge_middle`
  pieces with oak-log piers drawn to the bed, the Old Ford at 189 and the Forest River at 169, both called right in
  game. Nothing more to do on either.

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
(5546, 204, -7036) with the wraith three blocks along at (5546, 204, -7039), the coal lattice door at x 5575-5581, y 190-199, z -6856
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

- **Sign posts - on hold, and working.** Legibility is confirmed in game and the pieces seat: the shaft, plinth and
  cap need nothing, and an arm on each side of a post reads right. Held for a reason that is not the blocks: a board can
  only point at a place a world has planted, and **Rivendell is the only town so far**.
  `SignPostArmBlockEntity.destinations()` offers every planted place in `Places.catalogue()`, so a board would just as
  happily read *Matuna's Lair*. What to do when more towns exist: give `Places.Name` a kind - it is
  `record Name(String id, @Nullable Alliance alliance, boolean beryl)` today - or add a `Places.TOWNS` set, and filter
  `destinations()` to it, so a hand-placed board cycles `""` ↔ the towns. An automatic post is then only a placer that
  stands an arm on the side of the post the town lies and calls `setDestination(board, place)`, which measures the
  distance itself; the flat end against the shaft is already automatic. Nothing places posts in the world today - every
  one is set by hand - so no posting pass is waiting on it.
- **Spikes - settled (15 November 2026).** `spike_stone`, `spike_wooden`: eight stepped plates, drop themselves, two
  points of general damage on the game's hurt timer. Seen in game and called right, damage included - they are
  decoration, and a spike that does nothing is a texture, so the two points are the piece doing its job rather than a
  figure to balance. Nothing else was raised, so the shape's walkability and the stone half of the sheet stay as they
  are.
- **The seam scene** - the company leaving Rivendell. Recovered in `docs/departure-scene.md`; nothing built. Two things
  are owed by the owner before it can be: the marker set with its legend (one spot per body, plus the road the nine
  leave by) and eight faces - `bilbo`, `gandalf`, `aragorn`, `boromir`, `frodo`, `samwise`, `merry`, `pippin`. Elrond,
  Legolas, Gimli and Glóin are already drawn.
- **The Gundabad gate is final.** `GundabadGate.autospawn` stands it at `ArdaMap.gatePlan()` (facade south) as the
  server comes up - the eight pieces, the floor bedded, the seam dressed, the hall carried into the mountain - and a gate
  already standing is now left alone on two answers rather than one: the netherrack course in the west wing, and the
  pieces' own blocks through `Sites.standsAt`. That matters because re-laying writes the templates' air over whatever has
  been built on the footprint since. `/tmgate place` stays the deliberate re-lay; `clear` is the way back. The cave east
  of it cuts itself the same way, and `/tmpin build keep` now refuses rather than writing a pin that would move a
  planned hold.
- **The keep's plan is settled and its guard is fixed** (15 November 2026). `ArdaMap` now carries the plan the note
  walked: `KEEP_X` 5516, `KEEP_Z` -7112, `KEEP_FLOOR` 187 - box x 5516…5637, z -7112…-6930, floor top 189, flush with the
  passage's floor, its south wall at -6930 where the hall now ends. It read 5471/-7100/176, whose floor sat eleven
  blocks under the passage. The captain and the wraith are placed from `keepThrone()`/`keepThroneWatch()`, so they moved
  with the hold and now stand at **(5546, 204, -7036)**, three blocks ahead of him at (5546, 204, -7039); the owner's
  old mock-up of the seat, the slab and red carpet at (5501, 192, -7024), is left standing west of the hold and can go.
  `GundabadKeep.autospawn` asked whether the doorway's floor was deepslate - which the mountain's own rock is - so it
  answered "already standing" in every world and **the hold has never been laid**; it reads the template's own blocks
  through `Sites.standsAt` now, with the box opened first so an unvisited hold is read rather than assumed missing.
  Nothing has been seen in game: `/tmgate keep` is the way to look at it, and a world that took the older plan would
  keep that copy wherever it stood.
- **The captain of the Dourhands is down once, and stays down** (15 November 2026). The quest line was walked in game
  through 2-7, and Dugorim kept being stood again after he fell: the seat is populated from the page, and the page stays
  in the book until Gloin takes it, so the hunt stood a new dwarf every pass with the page already met. He now wears a
  mark of his fall on the player who ends him - `DUGORIM_DOWN_TAG`, the same scoreboard-tag answer the wraith behind him
  has had all along - so the empty seat stays empty for that hand. Seen in game and confirmed: after the fix he was
  killed once more and did not come back. The wraith's own mark has still not been walked, though it is the same answer
  written the same way.
- **The biome canon audit is settled** - twelve findings in `docs/arda-biome-canon.md`, closed 9 November 2026 in its
  own verdict section: eleven applied (gold Lorinand, no culumalda in the Mark, the right peoples in Emyn Muil, Khand,
  Cardolan and Minhiriath, the neon greens recoloured, snow over rock on the Misty Mountains, the Gladden's marsh and its
  reeds and iris, the dark conifer, Mirkwood's fir south, Eregion's holly, Ithilien's southern evergreen), and the
  Barrow-downs' deadwood kept on purpose - the downs read as an empty field without it. Nothing is owed on it: the four
  new leaf textures (conifer, fir, holly, southern evergreen) were seen and called right on 15 November 2026, so the
  audit is closed whole.
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
