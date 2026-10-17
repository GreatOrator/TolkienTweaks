---
push_when_confident: true
auto_push: true
git:
  push_on_success: true
---

## Changelog Updates
- **Requirement**: Whenever you complete a task, fix a bug, or add a feature, you must update `CHANGELOG.md` before finishing the session.
- **Format**: Follow the [Keep a Changelog](https://keepachangelog.com) standard under an `[Unreleased]` section.
- **Categories**: Group entries under:
  - `Added` for new features.
  - `Changed` for changes in existing functionality.
  - `Deprecated` for soon-to-be removed features.
  - `Removed` for now removed features.
  - `Fixed` for any bug fixes.
- **Tone**: Write concise, user-facing summary bullet points (do not just dump raw git commit logs).

# Project Core Guidelines & Agent Directives

> **CRITICAL AUTOMATION NOTE:** `push_when_confident` is explicitly set to **TRUE**. 
> Before pushing changes directly to the remote repository, you must guarantee that all local compilation, linting, and automated test steps pass with an exit code of `0`.

## Verification

The maintainer keeps a **test world for each biome**, so a change can be checked in game quickly
after a pull. Prefer that loop over a long local test: verify what can be verified locally
(`./gradlew compileJava`; a dev server through `./gradlew runServer` when world generation is
involved), then commit and push, saying plainly what was verified and what was not.

Keep commit messages short, sentence style, and focused on why the change is needed.

Anything under `src/generated/resources` is datagen output: after touching biomes, features,
placed features, tags or loot, regenerate and commit the generated JSON alongside the code.

## World generation facts worth not rediscovering

- **Which fluid fills a biome is not a biome property.** The overworld aquifer is built in
  `NoiseBasedChunkGenerator.createFluidPicker` from the dimension's `minecraft:overworld` noise
  settings alone (`sea_level: 63`, `aquifers_enabled: true`, `default_fluid: minecraft:water`), plus
  the aquifer noises in the noise router. No biome field, tag, feature, carver or surface rule is
  consulted, so a biome cannot switch its own fluid.
- Aquifer water therefore appears (a) everywhere empty below y=63, and (b) in local pockets whose
  level is `min(nearby surface, floorDiv(y, 40) * 40 + 20 + spread noise)`, which can sit far above
  sea level - water has been measured at y=95 inside a Mordor mountain whose columns top out at 160.
- `aquifers_enabled: false` is **not** a fix for wet biomes. Measured on seed 77777: water above sea
  level in Mordor columns went 260 -> 0, but water below sea level went 4 -> 15,412, because a
  disabled aquifer simply floods everything under the waterline.
- The lava lakes added by `MiscOverworldPlacements.LAKE_LAVA_SURFACE` only place surface lava
  (`rarity_filter 200`, `heightmap WORLD_SURFACE_WG`); they never remove water.
- Because of all that, Mordor and the Ash Mountains have their water swapped for lava on chunk load
  in `event/MordorLavaEvents`. It converts only columns whose surface biome is one of those two, and
  it writes into the sections directly: placing blocks through the chunk runs block placement, whose
  fluid interactions reach into neighbouring chunks and deadlock while the chunk is still loading.
- Mordor, the Ash Mountains and most other Tolkien biomes have `"carvers": {}`, and the mod's own
  `tolkien_caves` and `highland_caves` carvers (which deliberately carve `CAVE_AIR`) are registered
  and datagenned but attached to no biome, so they never run.

## Nurn, the watered half of that country

- Nurn's water is the aquifer's water, not a biome feature. Sea level is 63 and the lowland sits at
  or below it, so the aquifer floods it at y=62; measured over 289 chunks of the northern lowland,
  3681 of 14208 Nurn columns are water topped and the dry ones beside them are grass or dark stone.
  That standing water is what the biome's own lakes are cut into, and it is why a Nurn lake never
  shows as a body "above sea level" in a heightmap audit.
- Nurn places no lava of its own (`BiomeHelper.nurnGen` has no lava lake and no lava spring). The
  lava that turns up under Nurn columns belongs to the vanilla cave biomes - lush caves hold lava
  lakes down there in every biome of the world - and to the dimension floor below y=-54. Highest
  lava block in a Nurn biome cell measured 88 blocks below the surface, none at or above it.
- Do not give the Nurn lake feature a global flatness test. Nurn is eroded badlands country: over
  24 chunks of untouched terrain there was no disc of radius 24 whose ground varied by 3 blocks or
  less, and a feature that insisted on one never placed a single lake. `NurnLakeFeature` instead
  takes its water line from the lowest ground in the disc, digs only the ground within `MAX_BANK` of
  it, keeps the largest connected body, and lays grass `SHORE_WIDTH` blocks out from the water.
- A feature may only write to the chunks one step from the one being decorated: `ChunkStatus.FEATURES`
  has `blockStateWriteRadius(1)`, and every write beyond that is refused block by block with a
  `Detected setBlock in a far chunk` line in the log. A lake that reached further than that left its
  edge missing and flooded the log as the player walked, so `NurnLakeFeature` anchors itself at the
  middle of its own chunk, where it has `WRITE_LIMIT` blocks of room, and keeps both its water and
  its shore inside that. Keep any change to `MIN_RADIUS`, `MAX_RADIUS` or `SHORE_WIDTH` inside that
  budget rather than growing the lake past it.
- Nurn's floor is dark stone, not grass: `TolkienSurfaceRules.nurnSurface` lays grass only below
  `NURN_SHORE_HEIGHT` (y=68), the low shores and islands of the flooded lowland, and a lake lays its
  own meadow around water that sits higher. There is no way to ask a surface rule how far it is from
  water - it only sees its own column - so the two heights of grass are what "near water" means.
  A Nurn village therefore starts on dry ground, not on grass, and the lake shore can only reach one
  chunk from the water because of the write radius above.
- Vanilla badlands strata still colour a few Nurn columns at biome borders. The surface system and
  `execute if biome` both read the fuzzed biome manager, so within a few blocks of a wooded badlands
  or jungle cell the badlands terracotta banding runs even where the stored biome cell is Nurn
  (355 such blocks across 2272 fresh Nurn columns). It is a boundary effect, and it is also why a
  Nurn village can have a sandstone or terracotta block in its outskirts that the village did not
  place.
