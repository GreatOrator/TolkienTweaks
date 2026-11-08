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
- **The camp road** stopped mid-grade in the last session: `SEARCH done` with no `GRADE done` after it. A pass steps
  only while a player is within `ROAD_REACH` (96) of the block it has reached, so a road that looks stopped may just be
  waiting. Check for `laid in` before calling it a failure.
- **Vanilla structures** still reach the four vanilla biomes the dimension borrows (`deep_ocean`, `warm_ocean`,
  `frozen_ocean`/`deep_frozen_ocean`, `river`, `beach`): shipwrecks, ocean ruins, buried treasure. Mod biomes are in no
  vanilla structure tag, so nothing vanilla places on land.
- **Arda is bare now.** No vanilla features means no trees, grass, flowers, ores or snow in new chunks; only the mod's own
  rubble, stumps, mallorn, flowers and legacy ores. Chunks made before the change keep what they have, so a seam is
  expected. A mod-owned tree pass is the open piece of that.
- A build already standing in an older world keeps its old seat; the sweep does not re-seat it.

## What the player is likely to ask next

Trees back in some form; the seam between old and new chunks; whether the two crags stood; whether the camp road
finished; widening the sweep's reach per build; reporting what a sweep came to.
