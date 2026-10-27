---
push_when_confident: true
auto_push: true
git:
  push_on_success: true
---

## Changelog Updates
- **Requirement**: Whenever you complete a task, fix a bug, or add a feature, you must update `CHANGELOG.md` before finishing the session.
- **Exception**: Tools that live outside the mod - the Amulet plugins such as `fill_modded_block.py`, and the audit scripts - are not part of the mod, so they get no changelog entry.
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
- A village house joins its street only from its own front edge. Datagen writes the
  `minecraft:building_entrance` jigsaw one block outside the house's front wall, and the marker has to
  sit on the template's outermost column in the direction it faces; one set inside the footprint asks
  the street to grow into the walls, the game refuses it and drops the whole house, so a village shows
  its town centre and no houses (that is what kept Forochel's houses away). An entrance coordinate in
  `TolkienVillageProvider.VILLAGES_BY_STYLE` is written against that house's own template, never
  against the template whose other numbers it borrows, and `TolkienVillageTemplateProvider
  .requireFrontEdge` fails `runData` when it is not.
- Mordor, the Ash Mountains and most other Tolkien biomes have `"carvers": {}`, and the mod's own
  `tolkien_caves` and `highland_caves` carvers (which deliberately carve `CAVE_AIR`) are registered
  and datagenned but attached to no biome, so they never run.

## Which climate a Tolkien biome gets

- The region does not make its own climate: `TolkienClimate.SLOT_REPLACEMENTS` renames twenty vanilla
  slots, so a biome is exactly as common as the slot it took.  
- `TolkienClimate.CLAIMS` moves cells between slots: each claim names a source slot, the temperature,
  humidity and erosion bands that suit the biome, and TerraBlender is asked to drop those vanilla
  points (`removeParameter`) while a point for the Tolkien biome is emitted in their place. The cell
  is *moved*, never shared - two points covering one climate tie, and the vanilla side of the tie
  stands. Whole-slot replacements win over per-point ones inside TerraBlender, so a claim cannot be
  written against a point of a slot that is also in `SLOT_REPLACEMENTS` without removing it first.
- The bands are matched by their middle, not their edges: TerraBlender's `ParameterUtils` erosion
  bands end at -0.375 and -0.2225 where vanilla's end at -0.38 and -0.22, so an `equals` test on a
  parameter silently matches nothing.
- Erosion, not temperature, is what makes a mountain: `EROSION_0..3` is the steep ground and
  `EROSION_4..6` the flat, and plain and forest slots hold every erosion band. A range is therefore
  given steep cells of plains or forest, not a whole peak slot - vanilla's peak and slope slots are
  tiny in world space, which is why the Ash Mountains, Hithaeglir and Forochel Slopes were
  unreachable before.
- The region weight is the mod's share of the world as `weight / (weight + 50)`, vanilla's overworld
  region weighing 50: 2 gave about 4% and 7 gives about 12%. Cold and hot cells are much rarer in
  world space than their share of the parameter list suggests, so the snowy plains cannot support
  Forochel, Forochel Slopes and Hithaeglir at once; two of the three have to take cool or mild cells
  and rely on their own biome temperature for the snow.

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
## Config facts worth not rediscovering

- One file, `config/tolkienmobs-common.toml`, registered as `ModConfig.Type.COMMON`, split into a
  `[server]` section for what the world decides and a `[client]` section for what a listener decides
  for themselves. The only genuinely client-side options so far are the two fell beast dive-wind
  volumes: `DiveWindSound` runs on each client and reads them from that client's own file, so they
  must stay out of `[server]` and must keep being read as plain static fields.
- The content switches cannot empty a registry, so they close the paths that hand content out.
  Mobs are refused in `EntityJoinLevelEvent`, which every route into a level passes through
  (`ServerLevel.addEntity` -> `entityManager.addNewEntity`), and entities with `loadedFromDisk()`
  are deliberately left alone so switching a mob off does not delete the ones already living in a
  world. Blocks and items are dropped from `BuildCreativeModeTabContentsEvent` (a mod bus event,
  registered by hand in the mod's constructor since a class cannot subscribe to both buses) and
  from `ModifyRecipeJsonsEvent`, which runs before the recipe JSON is deserialized and so also keeps
  the item out of the recipe book; it re-runs on `/reload`, which is what makes the switch
  reversible. `/summon` still answers `Summoned new X` when the mob was refused, because vanilla's
  `tryAddFreshEntityWithPassengers` does not look at whether the entity landed.
- **`BuiltInRegistries.ENTITY_TYPE` and `BuiltInRegistries.ITEM` are *defaulted* registries in
  26.1**: an id no entry answers to comes back as the registry's default entry (an entity type, or
  air) rather than as null. Any config name that is resolved against them has to be checked with
  `containsKey` first; a bare `getValue` silently turns a typo into whichever mob the registry
  happens to default to. This was caught only by running a server and summoning the mob.
- Vanilla badlands strata still colour a few Nurn columns at biome borders. The surface system and
  `execute if biome` both read the fuzzed biome manager, so within a few blocks of a wooded badlands
  or jungle cell the badlands terracotta banding runs even where the stored biome cell is Nurn
  (355 such blocks across 2272 fresh Nurn columns). It is a boundary effect, and it is also why a
  Nurn village can have a sandstone or terracotta block in its outskirts that the village did not
  place.
