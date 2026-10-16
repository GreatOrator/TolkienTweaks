---
push_when_confident: true
auto_push: true
git:
  push_on_success: true
---

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
