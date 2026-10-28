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
- **Arda does not take the surface from its noise settings.** It reuses `minecraft:overworld`'s
  settings, whose rule TerraBlender wraps into a *namespaced* source: a mod biome is painted by the
  registered `tolkienSurface()` and, where that returns null, by the vanilla rule under it - which is
  why mod biomes still get vanilla's bedrock and deepslate. Both rules are gated by
  `abovePreliminarySurface`, and that gate is `blockY >= preliminarySurfaceLevel(x,z) + surfaceDepth
  - 8`, read from the *noise*: Arda keeps the noise and replaces its height with `ArdaMap`'s, so most
  drawn columns fail a gate measured against ground nobody stands on, every material rule is skipped
  and the ground stays the stone the reshape wrote (269 of 289 Shire columns had stone on top).
  `ArdaChunkGenerator.buildSurface` therefore lays `TolkienSurfaceRules.ardaSurface()` itself - the same
  materials unguarded, then `SurfaceRuleData.overworld()` for the map's vanilla biomes and for
  bedrock/deepslate - so a change to the mod's materials belongs in `materials()` and reaches both
  worlds. The generator also rocks over the noise's own voids below the drawn top, because the surface
  pass runs *before* the carvers: without it a void cut for a height the map does not draw is a cave
  whose floor the rules would dress. Caves in Arda are therefore the carvers' own, cut from the drawn
  ground, and the drawn water is laid again after them (`seal`).
- **A country's biome is read from its ground, not its cell.** `ArdaMap.Sample` carries `shore` and
  `upland`: a column within `BEACH_HEIGHT` of sea level *with a drawn sea among the four cells it
  blends* is `minecraft:beach` (a height-only test made every low field in the Shire sand), and a sea
  cell whose blended ground stands more than `BEACH_HEIGHT` above the waterline is named for the
  heaviest land country beside it (a height-only test left dry grass reading `minecraft:deep_ocean`).
  Both tests use the blended `drawn` height, not the relieved one, so the noise on top of a plain
  cannot invent a coast.

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
- **The overworld's list of the mod's countries is `TolkienClimate.SLOT_REPLACEMENTS` plus
  `TolkienClimate.CLAIMS`, and nothing else.** A biome that is in neither list is drawn by `ArdaMap`
  and met in Arda alone, however complete the rest of its data is - `#tolkienmobs:overworld`, the
  feature and spawn modifiers and every climate, mob and tag it belongs to do not put it in the
  overworld. `ArdaBiomeSource` deliberately does not read either list, so adding a country to the
  overworld cannot disturb Arda's drawing. The seven countries the map called for all sit in one list
  or the other now: Rohan, the White Mountains, Dunland, Lindon and Gondor each rename the vanilla
  slot whose ground they share (savanna, stony peaks, windswept savanna, cherry grove, meadow), and
  the Wild Wood and Eriador take cells the region's own slots had left (the warm rolling half of the
  forest, the cool dry flats of the plains). Measured with `/locate biome` in a fresh overworld, all
  seven answer within 2,500 blocks of the origin.
- No structure is placed in those seven countries: every `has_structure/<name>` biome tag names the
  older biomes only, so no village, ruin or warcamp stands in Rohan, Gondor, Eriador, Lindon,
  Dunland, the Wild Wood or the White Mountains - in Arda or in the overworld.
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
- The mob switches are one boolean per mob (`enableGoblin`, `enableMordorOrc`, `enableShelob`, ...)
  under `[server.mobs]`, from a hand-written table in `TolkienMobsConfig.MOB_TOGGLES` that pairs each
  switch with the entity id it stands for. The table cannot be derived: the spec is built while the
  mod is still being constructed, so the mod's entities are not in the entity registry yet, and a
  display name for the comment would need a language file that is not loaded that early either. The
  price is that a mob added later needs a line there, which is what `warnOnMobsWithoutSwitch` is for:
  it reads the spawn eggs on startup and names in the log any mob with no switch of its own. The
  family headings ("Ambient mobs", "Passive mobs", "NPCs", "Hostile mobs", "Boss mobs") ride as the
  first comment line of the first switch in each group, since `ModConfigSpec` cannot write a comment
  that belongs to no entry.
- **`BuiltInRegistries.ENTITY_TYPE` and `BuiltInRegistries.ITEM` are *defaulted* registries in
  26.1**: an id no entry answers to comes back as the registry's default entry (an entity type, or
  air) rather than as null. Any config name that is resolved against them has to be checked with
  `containsKey` first; a bare `getValue` silently turns a typo into whichever mob the registry
  happens to default to. This was caught only by running a server and summoning the mob.
- `[server.spawner]`'s numbers are what a *new* camo spawner starts with: each spawner keeps its own
  copy in its NBT and its screen writes those, so a change in the file shows on the next one placed,
  not on the ones already standing. `CamoSpawnerBlockEntity` builds its `SpawnerRanges`/`SpawnerDelays`
  from the config at construction, and `spawnerMaxDelay()` never returns less than `spawnerMinDelay()`
  whatever the two say. The screen's buttons allow exactly the ranges the config does.
- The magnet's reach is read where it is used - `BackpackUpgrades.magnetRange()` asks the config for
  the tier - and `BackpackMagnet` sweeps a box the size of the largest configured tier, so raising
  one range widens every sweep. The three magnet items are `MagnetUpgradeItem`s because their lore
  states that reach: the language file holds `%s` and the item fills it from the config at tooltip
  time, which is why those three are registered without `setHasLore`.
- Vanilla badlands strata still colour a few Nurn columns at biome borders. The surface system and
  `execute if biome` both read the fuzzed biome manager, so within a few blocks of a wooded badlands
  or jungle cell the badlands terracotta banding runs even where the stored biome cell is Nurn
  (355 such blocks across 2272 fresh Nurn columns). It is a boundary effect, and it is also why a
  Nurn village can have a sandstone or terracotta block in its outskirts that the village did not
  place.

## Naming facts worth not rediscovering

- **26.1 names an entity through `getTypeName()`, not through its entity type.** `Entity.getName()`
  answers with the custom name, or otherwise `Entity.getTypeName()`, which is the type's own
  description - but `Villager` overrides it with its *profession's* name, and an unprofessed villager
  holds `minecraft:none`, whose name is "Villager". A mod entity extending `Villager` therefore
  introduces itself as a villager in its name tag, in vanilla's `/summon` message and in death
  messages until it takes a job, which is what made every one of the mod's folk a villager.
  `TolkienVillagerEntity.getTypeName()` answers the no-trade case with its own type's description.
  The wandering-trader branch is unaffected: `AbstractVillager` does not override the method, so
  `TolkienWanderingEntity` and the traders based on it have always shown their own names. The title
  of the screen a villager trades in is that same `getDisplayName()`, so a name change shows there
  too, and a driven race+trade table would have to keep it sensible.
- The lang table for the mod's villagers carries a race name (`entity.tolkienmobs.entityttmdwarf` =
  "Dwarf") beside a race-then-trade name for every profession, written by `addVillager`; its
  no-trade entry (`entity.tolkienmobs.entityttmdwarf.tolkienmobs.none`) writes the race it was
  handed. It used to hardcode "Human" there for every race, and the pet-merchant and junk-trader
  entries had each other's names - in the villager table and in the profession names the game shows
  (`entity.minecraft.villager.tolkienmobs.pet_merchant`) alike. Nothing reads the per-race keys today
  - the trade's own name is what shows - so those strings were inert rather than visible.
- **A POI type's blocks are what makes a block a work block.** Each of the mod's professions names
  its job site by comparing the POI holder against its own `PoiType`, so the grocers' bamboo block,
  the banker's gold block and the trinket smith's ancient debris were the only blocks those trades
  would work at. They are the mod's own furniture now - fireplace, piggy bank, trinket table - read
  off the block's own state definition. `BuiltInRegistries` declares `POINT_OF_INTEREST_TYPE` after
  `BLOCK`, so the block registry is filled when a POI type's supplier runs and it may name mod
  blocks. A POI is created where a block is placed, by a player and by a structure alike.
- A jobless villager only claims a POI that `#minecraft:acquirable_job_site` names, and the trade it
  takes is the first registered profession whose `heldJobSite` test passes for the POI at the position
  it remembered (`AssignProfessionFromJobSite` reads that POI again and walks the profession registry),
  so a site no profession names leaves the villager unprofessed and reading `minecraft:none`. Two
  traps cost a lot of time here: a villager summoned into a chunk that is not force-loaded is created
  and then dropped (`No entity was found` on the next command), and one that cannot path to the site
  takes no job at all - so read the summon's `Pos` back before believing a null result, and pave the
  ground first. A site's existence can be read straight out of the world instead: the saved poi data
  under `<level>/dimensions/minecraft/overworld/poi` holds one record per site, with the type as a
  string, so a `Sections`/`Records` NBT dump answers "is this block a work block" without a villager.
  Read the whole inflated record though - the region file's length field is the *compressed* size, and
  a buffer that size truncates the records after the first and invents a missing POI that is not.

## Mount and passenger facts worth not rediscovering

- **A riding entity's saved position is not its own.** 26.1's `Entity.saveWithoutId` writes `Pos`
  for a passenger as `(vehicle.getX(), this.getY(), vehicle.getZ())`, so `/data get entity` on a
  ridden mob always reports its mount's x/z with the passenger's own height. A seat, a jockey's
  place or any other riding offset therefore *reads* as zero however well it works - this is what
  made the steed's attachment-point seat look broken while it was placing the rider correctly.
  Measure a seat another way: teleport a witness entity to the rider (`execute as <rider>
  positioned as @s run tp <witness> ~ ~ ~` - `as` alone does not adopt the entity's position), print
  it from inside `positionRider`, or move the mount itself.
- A horse keeps a seat through `EntityAttachment.PASSENGER` on its entity type: `.attach(PASSENGER,
  x, y, z)` declares the point in the animal's own frame, `EntityAttachments.getClamped` turns it by
  the mount's yaw, and `Entity.positionRider` sets the rider to `mount.position() + point -
  passenger.getVehicleAttachmentPoint(...)`. `AbstractHorse.getPassengerAttachmentPoint` adds a
  rearing shift of `(0, 0.15 * standAnimO, -0.7 * standAnimO)`, which is zero for a standing horse,
  so a declared point lands exactly. Measured on the Nazgûl steed, whose seat is
  `(0.3, 1.375, 0.25)`: the rider stands at `(0.30, 1.375, 0.25)` at yaw 0, `(-0.25, 1.375, 0.30)`
  at 90, and the mirrors of those at 180 and 270.
