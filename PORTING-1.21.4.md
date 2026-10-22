# Porting Tolkien Tweaks to 1.21.4

Working branch: `port/1.21.4`. The untouched 1.21.1 code is on `main` / the backup branch.
This file is the handoff: it records what is done, what is measured, and what to do next.

## Where the port stands

| | |
|---|---|
| Target | Minecraft **1.21.4**, NeoForge **21.4.161** |
| Compile errors at the start | **5,720** in 425 files |
| Compile errors now | **463** distinct sites |
| Runnable | no — the tree does not compile yet |

Dependency versions, all verified to exist and resolve:

| Dependency | Version | Note |
|---|---|---|
| TerraBlender | 1.21.4-4.3.0.2 | 1.21.3 has 4.2.0.1; there is no Curios/JEI for 1.21.3 |
| Curios | 10.0.1+1.21.4 | 9.x is 1.21.1-only, 10.x starts at 1.21.4 |
| JEI | 20.0.0.4 | jei-1.21.4-neoforge |
| GeckoLib | 4.8.5 | stays on the 4.x API; 1.21.5+ only ships GeckoLib 5 |
| Parchment | none | never published for 1.21.4, so the build uses NeoForge mappings |

## Local setup the port needs

- JDK 21: `C:\Users\ben_d\.gradle\jdks\eclipse_adoptium-21-amd64-windows.2`
- Compile: `JAVA_HOME=<jdk21> ./gradlew compileJava`
- `-Xmaxerrs 20000` is set in [build.gradle](build.gradle) so one run reports everything.
  Remove it once the port compiles.
- `com.mojang:jtracy` is only on `https://libraries.minecraft.net`, hence the extra
  repository in [build.gradle](build.gradle).

Decompiled 1.21.4 sources used to check every signature live at `C:\Users\ben_d\mc1214`.
To regenerate them:

```bash
unzip -oq ~/.gradle/caches/neoformruntime/intermediate_results/decompile_*.jar -d /c/Users/ben_d/mc1214
```

(the 1.21.4 jar is the one whose `EntitySpawnReason.java` matches 1.21.4 behaviour).

Migration reference for the later hops: the Kaupenjoe tutorial repo
`Tutorials-By-Kaupenjoe/NeoForge-Tutorial-1.21.X`, branches `61-Update1.21.3`,
`62-update1.21.4`, `63-update1.21.5`, `64-Update1.21.6`, `65-Update1.21.10`, `66-Update1.21.11`.

## What is already done (commit 14e15f3c)

- Build stack moved to 1.21.4, TerraBlender range in
  [neoforge.mods.toml](src/main/templates/META-INF/neoforge.mods.toml) raised to `[4.3.0.2,4.4)`.
- Pass 1 renames, scripted in [tools/port-1214-pass1.pl](tools/port-1214-pass1.pl):
  - `DirectionProperty` → `EnumProperty<Direction>` (the class is gone; `EnumProperty` now carries it)
  - `MobSpawnType` → `EntitySpawnReason`
  - `getMinBuildHeight()`/`getMaxBuildHeight()` → `getMinY()`/`getMaxY()`
  - `ItemInteractionResult` → `InteractionResult` (`PASS_TO_DEFAULT_BLOCK_INTERACTION` →
    `TRY_WITH_EMPTY_HAND`, `SKIP_DEFAULT_BLOCK_INTERACTION` → `SUCCESS`)
  - `InteractionResultHolder<ItemStack>` → `InteractionResult` (`sidedSuccess(...)` → `SUCCESS`)

The script is idempotent and safe to re-run.

## Remaining work, in the order that unblocks the most

### 1. Model datagen — DONE

NeoForge 21.4 **removed** `net.neoforged.neoforge.client.model.generators`
(`ModelFile`, `BlockModelBuilder`, `BlockStateProvider`, `ExistingFileHelper`,
`ConfiguredModel`, `VariantBlockStateBuilder`). Only `loaders/` and `template/` remain.
It also removed `ExistingFileHelper` outright, so NeoForge's source cannot be copied back in
(it is LGPL-2.1 anyway). The replacement lives in
`src/main/java/com/greatorator/tolkienmobs/datagen/modelgen/` and keeps the old class and
method names, so the providers only needed their imports changed.

That package now holds: `ModelFile` (incl. `ExistingModelFile`/`UncheckedModelFile`),
`ModelBuilder` (textures, `render_type`, the element/face/rotation DSL, `customLoader`),
`BlockModelBuilder`, `ItemModelBuilder` (overrides), `ConfiguredModel`, `ItemLayerModelBuilder`,
`VariantBlockStateBuilder`, `MultiPartBlockStateBuilder`, `ModelProvider` (shape helpers and the
`models/` path rule: a name with a slash is used verbatim, a bare name gets the folder prefix),
`BlockModelProvider`, `ItemModelProvider`, the full `BlockStateProvider` shape library, and an
`ExistingFileHelper` stand-in (it only ever gated error messages, so it accepts everything).
`ItemModelProvider` also emits the 1.21.4 item-model definitions under `assets/<ns>/items/`.

Imports are swapped in `TolkienBlockStateProvider`, `TolkienItemModelProvider`,
`BlockModelHelper` and `BlockModelBuilders`; none of the four has a remaining error.

#### Two behavioural differences that were not obvious

- `VariantBlockStateBuilder#forAllStatesExcept` collapses ignored properties into the variant key
  (stairs ignore `waterlogged`). Treating a state that merely *has* the property as skippable
  drops every state, which produced `"variants": {}` for stairs and fence gates. Fixed and now
  covered by the harness below.
- `GatherDataEvent` lost `includeServer()`/`includeClient()`: one event now covers a whole run,
  so `TolkienDataGenerator` adds every provider through `event.addProvider(...)`. The `--all`
  argument in `build.gradle`'s `data` run has not been re-checked against 21.4 yet — verify it on
  the first `runData`.
- Render types are passed as bare names (`"cutout"`) as the call sites always did, so they are
  normalised to `minecraft:` before being written; a bare name parsed as a `ResourceLocation`
  would otherwise become `tolkienmobs:cutout`.

#### Verification without a compiling mod

`runData` needs the whole mod to compile, so the generator is checked independently instead:
[tools/modelgen-verify/run.sh](tools/modelgen-verify/run.sh) compiles `datagen.modelgen` on its
own, generates a spread of vanilla shapes, and compares every file it writes with the same file
in the vanilla 1.21.4 client jar. It boots vanilla `Bootstrap` outside FML by installing an empty
`LoadingModList` first.

Last run: **62 files compared, 54 byte-identical, 3 identical apart from multipart ordering,
5 expected differences.** The ordering differences are NeoForge's direction order
(north/south/west/east), which is what the committed
`assets/tolkienmobs/blockstates/fence_deadwood.json` already contains, so they are correct for this
repo. The expected differences are: the furnace blockstate (vanilla switches model on `lit`, which
the harness call does not), three `iron_trapdoor_*` models (they carry `render_type`, as the mod's
committed door/trapdoor models do), and the block-item definition pointing at the generated
`item/<name>` model instead of the block model — self-consistent, since that model is generated.

Still to do: once the mod compiles, `./gradlew runData` and a JSON-aware diff of
`src/generated/resources` against the committed 5,001 files. Compare parsed JSON, not bytes.

### 2. Entity renderers and models — DONE

1.21.2 split every renderer into renderer + render state, and GeckoLib 4.8 followed. All of it
now compiles: **no errors remain under `entity/**/render` or `entity/**/model`**.

The API facts worth keeping, because they are not obvious from the docs:

- `EntityRenderer<T, S extends EntityRenderState>`; `MobRenderer<T, S extends LivingEntityRenderState, M>`;
  `LivingEntityRenderer#getTextureLocation(S)` is abstract, and `extractRenderState(T, S, float)`
  is where per-frame data is copied. `LivingEntityRenderer#scale(S, PoseStack)` replaces the old
  `poseStack.scale(...)` inside a `render` override.
- `Model#renderToBuffer` is `final`, and `EntityModel` has no no-argument constructor:
  models pass `root` to `super(...)` and drop their `renderToBuffer`/`root()` overrides
  (`Model#root()` is final too).
- `HierarchicalModel` is gone, but the helpers moved onto `Model`: `animateWalk(AnimationDefinition, pos, speed, speedFactor, amplifier)` and `animate(AnimationState, AnimationDefinition, ageInTicks[, speed])`.
  Animation states travel to the renderer as fields on a custom render state and are filled with
  `state.<name>.copyFrom(entity.<name>)`.
- `ArrowRenderer<T, S extends ArrowRenderState>` and `BoatRenderer` both gained a state parameter.
  `BoatRenderer` no longer exposes a model/texture pair: custom boats extend `AbstractBoatRenderer`
  and answer `model()` and `renderType()`, with `BoatModel.createBoatModel()` /
  `BoatModel.createChestBoatModel()` as the layer definitions (`ChestBoatModel` is gone).
- GeckoLib 4.8: `GeoModel#getModelResource` and `#getTextureResource` take the asking
  `GeoRenderer<T>` as a second argument; `GeoEntityRenderer` renders from `EntityRenderState` and
  no longer has `render(T, yaw, partial, ...)` — pose work belongs in
  `preRender(PoseStack, T, BakedGeoModel, MultiBufferSource, VertexConsumer, boolean isReRender, float, int, int, int)`.
  `GeoRenderLayer#render` gained a trailing `int colour`, and `getDefaultBakedModel` now wants the renderer.
- **`preRender` runs again for every render layer**, with `isReRender == true`, so one-time pose
  scale has to be guarded or layers double it. All the converted renderers do that.

The 55 GeckoLib renderers were converted by [tools/port-renderers.pl](tools/port-renderers.pl)
(render overrides that only called `super` are deleted; scale-only ones become guarded `preRender`),
the 61 GeoModels by [tools/port-geomodels.pl](tools/port-geomodels.pl), and the seven boat renderers
by [tools/port-boats.pl](tools/port-boats.pl). The gecko and swarm were the only two vanilla models:
both moved to `EntityModel` with `GeckoRenderState`/`LivingEntityRenderState`, and the boulder and
fell beast fireball share `ThrownProjectileRenderState`. `MorgulCrystalRenderState` carries the
crystal's spin time, and `DimensionSpecialEffects#getSunriseColor` no longer exists (the override
was deleted; vanilla has no sunrise colour to begin with).

#### Baked block models — DONE

`ChameleonBlockDynamicBakedModel`, `PatchModel`, `UnbakedPatchModel` and `PatchModelLoader` compile again.
1.21.4 rebuilt this whole corner of the client, and none of it is guessable from the old call sites:

- `BakedModel` moved to `net.minecraft.client.resources.model`. `ItemOverrides` is **gone** (item rendering has
  its own `ItemModel`s now), `isCustomRenderer()` is gone, and `getTransforms()` is abstract, so a hand-written
  model returns `ItemTransforms.NO_TRANSFORMS`. `getParticleIcon()` and `getQuads(state, side, random)` survive as
  deprecated abstracts.
- NeoForge's `BakedModelWrapper` is gone too; vanilla's `DelegateBakedModel` is the wrapper base now, and it
  already forwards quads, model data, particle icon, render types and transforms, so the camouflage model only
  overrides the three methods that matter.
- NeoForge's geometry API (`IGeometryLoader`, `IUnbakedGeometry`, `IGeometryBakingContext`) is replaced by
  vanilla's model pipeline. A custom loader implements `UnbakedModelLoader<T extends UnbakedModel>`, the model
  implements `UnbakedModel` (or `ExtendedUnbakedModel`, which is the non-deprecated `bake` with a trailing
  `ContextMap`), and baking reads sprites from `ModelBaker#sprites()`. Loaders register on `ModelEvent.RegisterLoaders`
  and `UnbakedModelParser` dispatches a model's `"loader"` id to them, so `tolkienmobs:patch` keeps working
  unchanged from the JSON side.
- `InventoryMenu.BLOCK_ATLAS` became `TextureAtlas.LOCATION_BLOCKS`.
- `FaceBakery#bakeQuad` is static and takes a trailing per-face light emission (0 for the patch).
- `ModelEvent.ModifyBakingResult` no longer has `getModels()`. It carries the `ModelBakery.BakingResult`, whose
  `blockStateModels()` map is still a plain `HashMap` at that point (`BakingCompleted` is what wraps it
  unmodifiable), so replacing a block state's model means mutating that map.

Still open in this area, and separate work: `FastColor` → `ARGB` in `TrinketComponent`, and the block entity
renderers.

### 3. Registration and data — partly done

Done in this pass:

- **Entity types and spawn eggs** (`TolkienEntities`, was 133 errors, now 0). `EntityType.Builder#build`
  takes a `ResourceKey<EntityType<?>>`, so every type is registered through a
  `registerEntity(name, builder)` helper that makes the key from the registry name. That also fixed the
  chest boats, which used to build against the plain boat's id and so shared its key. `DeferredSpawnEggItem`
  is gone: spawn eggs are plain `SpawnEggItem(EntityType, Properties)` and the two colours live in the item
  model definition (`minecraft:model` with two `minecraft:constant` tints), which vanilla's
  `ItemModelGenerators#generateSpawnEgg` writes. `TolkienEntities.SPAWN_EGG_COLORS` keeps the colours for that
  datagen step. `ENTITY_TYPE` is registered before `ITEM` in `BuiltInRegistries`, and the register events fire
  in that order, so the egg's `type.get()` is safe.
- **Armour**: `ArmorMaterial` is a plain record in `net.minecraft.world.item.equipment`, not a registry entry,
  and it carries a `ResourceKey<EquipmentAsset>` instead of model layers. Each set now renders from
  `assets/tolkienmobs/equipment/<set>.json` with layer textures at
  `textures/entity/equipment/{humanoid,humanoid_leggings,horse_body}/<set>.png`. The old art was a 2x
  nearest-neighbour blow-up (128x64) of the 64x32 layout, so [tools/port-equipment-assets.pl](tools/port-equipment-assets.pl)
  halves it exactly and copies the 64x64 horse armour art across.
  `ArmorItem#getMaterial` is gone, so `TolkienArmorItem` identifies its set through
  `DataComponents#EQUIPPABLE`'s asset id, and the durability now comes from the material record.
  Trim materials dropped `item_model_index` and take the equipment-asset map instead.
- **Food**: effects moved off `FoodProperties` onto the `Consumable` component, and the eat/drink animation
  with them (`ItemUseAnimation`; `UseAnim` is the old name), so `TolkienFood` no longer overrides
  `getUseAnimation`. Each food is now a `FoodProperties` plus a `Consumable` in `TolkienFoods`.
- **Chat**: `sendSystemMessage` only exists on `ServerPlayer`; the 45 `Player` calls became
  `displayClientMessage(component, false)` ([tools/port-chat-messages.pl](tools/port-chat-messages.pl) matches the
  argument with balanced parentheses because it is usually a `Component.translatable(...).withStyle(...)` chain).
- **Datagen plumbing**: NeoForge 21.4 removed `ExistingFileHelper` and `GatherDataEvent#getExistingFileHelper`.
  Tag providers no longer take it, `SoundDefinitionsProvider` is `(PackOutput, modId)`,
  `ParticleDescriptionProvider` is `(PackOutput)`, and the village template provider reads through
  `GatherDataEvent#getResourceManager(PackType.SERVER_DATA)`.

Still open in this area:

- **Recipe datagen** — done. `TolkienRecipeHelper` is now an abstract `RecipeProvider` with instance helpers,
  `TolkienRecipeProvider` extends it and implements `buildRecipes()`, and a nested `Runner` (a
  `RecipeProvider.Runner` that also answers `getName()`) is what datagen registers. The facts that were not
  guessable from the call sites:
  - `RecipeProvider(HolderLookup.Provider, RecipeOutput)` keeps the output on the provider, so the shape helpers
    lost their `RecipeOutput` parameter but gained nothing else (`slab(category, slab, base)`,
    `pressurePlate(plate, base)`, `wall(category, wall, base)`, `woodFromLogs`, `woodenBoat`, `chestBoat`).
    The mod's own helpers keep theirs, which is why the provider body still passes `pRecipeOutput`.
  - `ShapedRecipeBuilder.shaped` and `ShapelessRecipeBuilder.shapeless` now take a `HolderGetter<Item>` first;
    `RecipeProvider` exposes protected `shaped(...)`, `shapeless(...)` and `tag(...)` wrappers around its own
    `items` lookup, so the mod calls those instead of the static builders.
  - `RecipeBuilder#save(RecipeOutput, ResourceLocation)` is gone: it takes a `ResourceKey<Recipe<?>>`, and the
    `String` overload throws when the id equals the recipe's default id, so the explicit-id sites use
    `recipeKey(...)`.
  - `Ingredient#getItems()` became `items()`, a `Stream<Holder<Item>>`; the criteria that only wanted a name use
    `firstItem(...)`.
  - NeoForge's `IConditionBuilder` is gone; nothing in the mod used it beyond declaring the interface.
- **Blocks and potions** — done. `TolkienBlocks` and `TolkienPotions` compile; three API facts were not
  visible from the call sites:
  - `BlockEntityType.Builder` is gone. Each block entity type is now `new BlockEntityType<>(factory, blocks...)`,
    so `BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>(...))` replaces the old `.of(...).build(null)`.
    The NeoForge constructor still refuses an empty block list, which is what the old builder warned about.
  - `BlockBehaviour.Properties#lootFrom` is gone too; a block that drops like another one now names that
    block's loot table with `overrideLootTable(Optional<ResourceKey<LootTable>>)`. The seven wood wall torches
    go through a `blockLoot(name)` helper producing `<modid>:blocks/<name>`, which is the key the floor torch
    already has by default, so the wall torches keep dropping like it.
  - `Potion` gained a constructor argument, and it is **not** the registry id: it is the suffix the potion
    names are built from (`item.minecraft.potion.effect.<name>`, and the splash/lingering variants). The mod's
    lang datagen already keyed those names on the registry ids, so `TolkienPotions.potion(name, effects...)`
    passes one string to both the registration and the potion.
- **Registry ids on properties** — done. 1.21.4 reads the id out of `BlockBehaviour.Properties`/`Item.Properties`
  *inside the block/item constructor* (`effectiveDrops()`/`effectiveDescriptionId()` for blocks, the description id
  and model for items), and throws `Block id not set` when it is missing, so this was a startup crash rather than
  cosmetics. Every registration now goes through the `DeferredRegister` helper that stamps the id before the factory
  runs:
  - [tools/port-registry-ids.pl](tools/port-registry-ids.pl) moved the properties out of the factory argument in
    586 block registrations and 203 item registrations — `() -> new X(args, Properties.of()…)` became
    `properties -> new X(args, properties)` plus a trailing properties argument. It rewrites the three mod helpers
    (`registerBlock`, `registerNoItem`, `registerBlockWithoutBlockItem`) and the 66 sign blocks that were registered
    directly on `BLOCKS`. Plain `new Item(props)` registrations became `ITEMS.registerSimpleItem`.
  - `registerBlock` now registers its item with `ITEMS.registerSimpleBlockItem`. That is not only the id: `BlockItem`
    no longer answers its block's description id, so the block description prefix is what keeps the `block.<modid>.*`
    lang keys (which is all the lang datagen writes) matching the item names.
  - Properties are now built in the static initialiser rather than lazily at registration, so anything they copied
    from another mod block had to go. The 14 wood wall and hanging wall signs used
    `Properties.ofFullCopy(MALLORN_SIGN.get())`; they spell out the same chain the standing sign passes instead.
  - Hand-written: the spawn eggs in `TolkienEntities` and the two liquid blocks in `TolkienFluids`.

  The remaining 287 errors are all outside registration and outside `entity`: block entities, items, recipes,
  network components, the client event handlers and a few datagen helpers.

### 3b. Entities — DONE

The ~180 errors in `com.greatorator.tolkienmobs.entity` were almost all one API move: damage is server-only now.

- `Entity#hurt(DamageSource, float)` is `final void`; the override point is
  `hurtServer(ServerLevel, DamageSource, float)`. Every boss and monster reaction moved there and lost its
  `if (!(this.level() instanceof ServerLevel)) return false;` guard, because the server level is an argument now.
  A plain (non-living) entity has to implement `hurtServer` at all: `BaseTrapEntity` and `TornadoEntity` answer
  `false` to keep their old "not damageable" behaviour, and `MorgulCrystalEntity` moved its explosion there.
- `LivingEntity#isInvulnerableTo(ServerLevel, DamageSource)`; for a plain entity the equivalent is
  `isInvulnerableToBase(DamageSource)`.
- `doHurtTarget(ServerLevel, Entity)` on both `Mob` and `LivingEntity`, and `customServerAiStep(ServerLevel)`.
  A caller outside a goal takes the level from `Goal#getServerLevel(Entity)`; `EntityType#create(Level, EntitySpawnReason)`
  now needs the reason, so babies pass `BREEDING`, riders pass `JOCKEY` and spawned projectiles/traps `MOB_SUMMONED`.
- `Entity#isAlliedTo(Entity)` is final. A subclass widens it by overriding `considersEntityAsAlly(Entity)`, which
  is where the dragon's rider-and-mount rule went.
- `Leashable#dropLeash()` lost its arguments, `Entity#kill(ServerLevel)` takes the level, and
  `getPickedResult(HitResult)` is `getPickResult()` — `Mob` already returns the spawn egg, so the dragon's override
  was redundant and went away.
- `tryCheckInsideBlocks()` / `checkInsideBlocks()` are private internals. `LivingEntity.aiStep` calls
  `applyEffectsFromBlocks()` right after travel and `move()` does it for plain entities, so the dragon's manual call
  in `travel` and the crystal's call in `tick` were deleted rather than ported.
- `InteractionResult` is a sealed interface of records; `sidedSuccess(boolean)` became `SUCCESS` on the client and
  `SUCCESS_SERVER` on the server.
- `TargetingConditions.Selector` is `boolean test(LivingEntity, ServerLevel)`, so every targeting predicate takes two
  parameters. `Level#getNearbyEntities` is gone in favour of `ServerLevel#getNearestEntity(list, …)` and
  `getEntitiesOfClass`, and `Level#getGameRules()` is `ServerLevel`-only.
- Smaller moves: `ItemCooldowns#addCooldown` takes an `ItemStack`; `Boat`/`ChestBoat` take a `Supplier<Item>` for the
  item they drop; `Snowball`/`ThrownPotion` take an `ItemStack`; `BlockParticleOption` carries its block position in
  the constructor instead of a `setPos` call; `AbstractArrow.inGround` is the protected `isInGround()`; and
  `FlyingPathNavigation#setCanPassDoors` no longer exists (the setting was a no-op for flight).
- The bosses' reaction messages: `LivingEntity` has no `displayClientMessage` any more, so
  `GeneralUtility.sendMessage(target, component)` sends them to a player and quietly ignores a mob attacker.
- Behaviour recovered while porting: the goat's charge telegraph (`checkAndPerformAttack` lost its distance
  parameter, so the override had stopped being called) and the block-dust particles of the ent and the elemental
  golem, which now carry the position the particle texture is picked from.

### 4. Datagen output

Regenerate everything under `src/generated/resources` once the providers build, and commit
it alongside the code.

## Then

Launch a dev server, fix runtime failures, run the game tests, then take the 1.21.4 tree
forward one hop at a time using branches `63`–`66`. The GeckoLib 5 rewrite is the single
biggest item in those later hops.
