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
| `faction/Sites.java` | Seats a build at its pin with no drift, underpins it, sweeps the ground to its roofline, and plans a road for it. |
| `faction/Roads.java` | `ROUTES`: one `Route.Crossing` (Rivendell) and one `Route.Riverless` (Elledan's camp to the ford). |
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

## What the player is likely to ask next

Trees back in some form; the seam between old and new chunks; whether the two crags stood; whether the camp road
finished; widening the sweep's reach per build; reporting what a sweep came to.
