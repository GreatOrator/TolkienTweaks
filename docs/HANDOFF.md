# Where the work stands

This is the note that survives a compacted conversation. The transcript can be summarised away; this file is not. Read
it before starting, then `CHANGELOG.md` for the per-change history and `docs/arda-roads.md` for how the roads are
designed. Update it at the end of a session rather than trusting the chat to be there next time.

## The goal

Chapter one of Arda: every scouted structure and every finalised road stands or is laid automatically in every world,
with no commands typed. A player walks the chapter and the world has already built it.

## Standing rules

- One `CHANGELOG.md` bullet per change.
- Commit to `main`; never push.
- Never playtest; the player relaunches and reports.
- "Verified" means `./gradlew compileJava -x createMinecraftArtifacts` exits 0. Say plainly when something is only
  compiled and not seen in a world.
- Commit generated JSON beside the code that generates it.
- Edits to Java are byte-level scripts in `build/tmp/` (files are CRLF; `sed -i` strips it and long heredocs truncate).
  Generated biome JSON is LF by `.gitattributes`.

## Where things live

| File | What it does |
| --- | --- |
| `faction/Pins.java` | The scouted spots: `SOURCE`, crag pins, the ford marker. "The pin wins, the marker only informs facing." |
| `faction/Keepers.java` | The bodies the line has stood, by tag, UUID and last ground, so a keeper is found in an unloaded chunk. |
| `faction/Sites.java` | Seats a build at its pin with no drift, underpins it, sweeps the ground to its roofline, and plans a road for it. |
| `faction/Roads.java` | `ROUTES`: one `Route.Crossing` (Rivendell) and two `Route.Riverless` (Elledan's camp to the ford, Barachen's fire to the mark); `BENDS` and `HALVES` by route. |
| `faction/RoadCommands.java` | The road pass: sweep, bridge, pillars, search, grade, lips, and `Progress` (the road's own record). |
| `faction/Dressing.java` | The city's own drape and the check that plans a road when one is not down. |
| `faction/RoadPaint.java` | Roads painted at world generation, as a pure function of the column. |
| `world/registration/ArdaChunkGenerator.java` | Draws every column to `ArdaMap`, lays the water, cuts Moria's halls. |
| `world/registration/TolkienBiomeGenerationSettings.java` | What a biome generates: only the mod's own features, and no carvers. |
| `run/logs/latest.log` | The evidence. `grep "Road \|laid in\|will not take\|Dressing"` answers most questions. |

## Where it stood on 8 November 2026

Done and seen in a world by the player: the camp, Barachen's camp, the wood tower and one crag place at their pins; the
camp-to-ford road lays; the Rivendell crossing lays in full (`laid in 53 steps, 2621 columns`).

Done and only compiled (not seen): two refused crags re-attempting, the ground past plants, the sweep that carries the
ground around every build to its roofline, no vanilla world generation in Arda, and roads spanning a hole.

A keeper is now asked of the world by its tag rather than found in a box round his post, a body left outside his reach
of the post is walked back to it, and every stood body is written down in the save (`tolkienmobs:keepers`) so he is
found in an unloaded chunk rather than stood again. The crag's troll, the Witch-king and the three of his ring keep the
same note (a tag may own several bodies, and the ring is stood only for the slots its count leaves empty): compiled, not
yet seen in a world.

The camps stand their own men now. `Sites.CAMP_OWNERS` gives Thorenhad Elladan and Barachen's fire Barachen, wearing
the campaign's giver tags and written into `tolkienmobs:keepers` under the tag `Campaign.keeperWith` asks for, so the
line finds the body the world stood instead of standing a second one; a nameless keeper already at a named camp is
taken away. A camp the world raised from its pin is not laid again at its place's middle (`Sites.raisedAt` guards
`standTent`), and Barachen's fire gets Barachen's cut rather than Elladan's. Compiled, not yet seen in a world.

Roads can be bent through recorded points now: `Roads.BENDS` holds a list of blocks per route, and the walk is split
into a leg per bend so the line is forced through each one. A route with no entry there is walked exactly as before,
one leg from the door to the far end. The Thorenhad road is bent through the three marks walked in Roads 3 - 4875, 74, -3179, then 4885, 74, -3133,
then 4903, 74, -3102. Its first alignment, before them, was 1016 stations long: it looped north and east
and back to the far end, which is why it went through the ford.
Compiled, not yet seen in a world.
A road already in progress keeps its saved alignment, so `/tmroad forget` at the build clears the note and
the site's road flag, and the next tick walks it again with whatever bends are recorded for it.

A road is one of three kinds now, and the kind is its width: a wild track three blocks wide, the transition's five, a
settlement's own seven, chosen per column by how far it stands outside the walls - so a road widens as it comes in to
a gate instead of being one width end to end. A hairpin's apron is one kind wider than the run it turns, and a wild
edge is worn by one column in three. `Roads.HALVES` records a route that is one kind the whole way, which is laid at
that width exactly.

Barachen's fire has its track: from the camp's south door at 4820, 76, -2886 to the mark at 4892, 74, -3097 where the
Thorenhad road runs past, wild and three wide the whole way. Compiled, not yet seen in a world.

## Open

- **Two crags** were refused by `plane`/`ground()`; the fix reads ground past plants and a refusal now names the column
  and the block. If one is still refused the log says why — water is still a refusal, and that means a re-pin.
- **Runtime files are per world.** `CityCommands.folder(level)` resolves `<save>/tolkienmobs/rivendell`, and the
  jar cut is the source of pieces (`cut()` tries the jar first, then that folder). Nothing in the mod writes beside the
  game directory any more; a world that used the old folder re-dresses once.
- **Crags are seated fifteen blocks in** (nine before), so the rock skirt and the bare shell are underground and only
  the mouth and the crown's rock show. A crag already standing keeps the seat it was placed with; the new seat applies
  to a crag built after the change.
- **The camp road** stopped mid-grade in the last session: `SEARCH done` with no `GRADE done` after it. A pass steps
  only while a player is within `ROAD_REACH` (96) of the block it has reached, so a road that looks stopped may just be
  waiting. Check for `laid in` before calling it a failure.
- **Vanilla structures** still reach the four vanilla biomes the dimension borrows (`deep_ocean`, `warm_ocean`,
  `frozen_ocean`/`deep_frozen_ocean`, `river`, `beach`): shipwrecks, ocean ruins, buried treasure. Mod biomes are in no
  vanilla structure tag, so nothing vanilla places on land.
- **Arda's ground grows only what is ours.** No vanilla features at all, so the country is furnished by the mod's own
  tree features through `neoforge/biome_modifier/tree/arda_*.json` (broadleaf, woodland, Imladris) and by
  `plants/arda_cover.json`, which lays the new ground cover over every land biome in the `arda_cover` tag. Chunks made
  before the change keep what they have, so a seam is expected at the old edge.
- A build already standing in an older world keeps its old seat; the sweep does not re-seat it.

- **A road's note is checked, not believed.** `Progress` writes `at` (where the road met the build) and `block`
  (what stood there) with `down`, and `RoadCommands.standing` only takes the note while that block is still the block;
  `down()` and `RoadJob.restore` both go through it. This is why the crossing never started in the Scouting World:
  `.../overworld/data/tolkienmobs/roads.dat` held `rivendell -> down` with no probe, `Dressing.start` therefore saw
  `roadDown`, the ring was dressed, `_dressed.txt` was written `1 1`, and no pass was ever planned. The old note is now
  stale by construction and the crossing is planned on the next load. `/tmroad build` answered
  `Index 2 out of bounds for length 2` because `ALREADY_DOWN` carried a two-number far side; it carries three now.
- **Mallorns no longer grow in Imladris.** The valley is `tolkienmobs:imladris`, so `#tolkienmobs:arda_mallorn` (that
  biome, and only it) carpeted the city's own floor in golden crowns the cut cannot clear: a cut lays its pieces and
  leaves the country beside them. The tag names `tolkienmobs:lorinand` now. Chunks already generated keep their trees,
  so the current world's valley stays wooded until those chunks are remade.
- **The drape no longer floors the ring with leaves.** Its skin was the motion-blocking top of the column - a leaf
  block on a wooded column - and it laid that back down at the cap. The skin now comes from under the crown and the
  tree above it is cleared with the column.

- **Arda's sky is y 320, not 319.** `arda_type` is `min_y -64, height 384`, so `level.getMaxY()` is 320 and the last
  standable block is 319. The city cut's guards compared a piece's top against `getMaxY()` as if it were standable, so
  tops at exactly 320 were laid; `lay()` now clips every piece with a `BoundingBox` whose top is `getMaxY() - 1`, and
  both guards refuse `>= getMaxY()`. The `sectionPos out of bounds` errors in the 18:17 log came from those writes. The
  `DUMMY block entity` warnings at y 320-326 in `Scouting World` are already in those chunks (from a city raised six
  blocks higher, before the pin moved down); clipping stops new ones, but the old records stay until those chunks are
  remade.

- **The test world's above-sky records are gone.** `Scouting World` carried 1,436 chunk sections at section Y 20
  (y 320-335, 442 of them holding blocks) and 4 block entities at y 320-326, all from a city laid before the cut was
  clipped to the sky. `build/tmp/ceiling_entities.py` (scan|strip, untracked, rebuilt on demand) reads the Anvil region
  and POI files directly and drops `block_entities` with y >= 320 and `sections`/POI `Sections` with Y >= 20, backing up
  the files first. Run over `run/saves/Scouting World/dimensions/tolkienmobs/arda`: 5 regions rewritten, 4,800 chunks
  compared against the backup with `clean(original) == live` holding for every one, so nothing but those records moved.
  Backups at `build/tmp/arda-region-backup/20261107-190006` and `.../20261107-190141`. A section's `Y` is an absolute
  section coordinate (-4..19 here), stored as a **signed byte**: read unsigned, -4..-1 look like 252..255 and a filter
  on "Y >= 20" deletes the bottom four sections of every chunk. That mistake was made and restored from the first
  backup before the strip above.

- **The valley's crowd is counted, not re-stood.** `Campaign.crowd` stood `VALLEY_FOLK - stood` bodies on every pass
  while `stood` counted only the marks answered that pass, so a full city (all marks taken, `stood` 0) stood 24 new
  elves every second: the 19:39 session's files hold 3,439 Elves, 2,570 of them `valley_folk`, and 5,211 items, with
  `Can't keep up` climbing from 2s to 8.4s. It now counts the loose walkers first (`looseFolk`), and `nearby` searches
  the home radius rather than 6 blocks so a wanderer is not doubled. The bodies the old loop made are still in the
  world's `entities` files and will keep the test world slow until they are stripped; the ceiling stripper's approach
  works on `entities` region files too.

- **The test world's folk are back to 24.** `build/tmp/arda_folk.py` (report|strip|verify) splices each chunk's
  `Entities` list in place, so nothing but that list moves. Arda held 10,353 entities: 5,211 dropped items, 3,439 Elves,
  2,570 `valley_folk` and 620 bodies wearing a mark slot. Kept: one body per mark (15 slots), the three Elves of the line
  (tagged by name, not by slot, and untouched) and six loose walkers - 24 in all - and every item removed, 8,377 entities
  gone, 1,976 left. Verified by rebuilding all 2,823 chunks from the backup and comparing bytes, and by checking every
  other entity type's count is unchanged. Backup: `build/tmp/arda-entity-backup/20261108-033557` (the original) and
  `.../033640`. A mark's tag is `tolkienmobs:valley_folk:N`, **not** the base tag: a first pass that matched the base tag
  kept 620 slot duplicates, was restored byte-for-byte from the backup, and was re-run with the slot rule.

- **One body for each Elf of the line.** Arda held 45 Glorfindels (all stacked at 5614,250,-3210), 43 Elronds and 29
  Legolases: a keeper the campaign could not find within its reach was stood again wherever the player happened to be.
  One body per name tag is kept - the one nearest the stand `VALLEY_STANDS` writes for it around `rivendell`'s centre,
  5206,136,-3033 from `places.dat` - and 114 were dropped (all `entityttmhuman`, the keepers' own body type; the 270
  Elves are untouched). Kept: Elrond at 5183,132,-3027 (inside the valley), Glorfindel at 5614,250,-3210 and Legolas at
  5400,158,-3218 (both still outside the 128-block reach, so a visit will stand the valley's own beside them). Nothing
  points at these bodies by UUID - `quest_ledger.dat`'s givers name none of them - so the stand is the whole of the
  choice. Verified by rebuilding all 2,823 chunks from the pre-cleanup backup (10,353 entities) with both rules
  applied, and matching the live bytes chunk for chunk; only items, surplus folk and the 114 humans changed.

## What the player is likely to ask next

Trees back in some form; the seam between old and new chunks; whether the two crags stood; whether the camp road
finished; widening the sweep's reach per build; reporting what a sweep came to.
