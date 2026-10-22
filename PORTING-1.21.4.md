# Porting Tolkien Tweaks to 1.21.4

Working branch: `port/1.21.4`. The untouched 1.21.1 code is on `main` / the backup branch.
This file is the handoff: it records what is done, what is measured, and what to do next.

## Where the port stands

| | |
|---|---|
| Target | Minecraft **1.21.4**, NeoForge **21.4.161** |
| Compile errors at the start | **5,720** in 425 files |
| Compile errors now | **957** in ~130 files |
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

Still open in this area, and separate work: the block models and baked models
(`ChameleonBlockDynamicBakedModel`, `PatchModel`/`UnbakedPatchModel`, `ModelEvent.RegisterGeometryLoaders`
which 21.4 replaced, `FastColor` → `ARGB`) and the block entity renderers.

### 3. Registration and data

- `DeferredSpawnEggItem` is gone (112 hits) — spawn eggs are now a data component on
  `SpawnEggItem`; see `TolkienEntities` (266 errors).
- `Item.Properties`/`BlockBehaviour.Properties` need registry ids (283 + 575 sites).
- `TolkienArmorMaterials` (64) — `ArmorMaterial` is no longer a registry entry; equipment
  moved to `DataComponents#EQUIPPABLE` plus `models/equipment` JSON.
- `TolkienFoods` (34) — `FoodProperties` and the new `Consumable` component are now separate;
  `Item.Properties#food(FoodProperties, Consumable)`.
- `TolkienRecipeHelper` (226) — `shaped(...)` helper signatures and the new `RecipeDisplay`s;
  ingredient JSON moved to the holder-set form (`"minecraft:diamond"`, `"#c:ingots/copper"`).
- `sendSystemMessage`, `logBlock`, `MobEffectInstance` → `Holder<MobEffect>`.

### 4. Datagen output

Regenerate everything under `src/generated/resources` once the providers build, and commit
it alongside the code.

## Then

Launch a dev server, fix runtime failures, run the game tests, then take the 1.21.4 tree
forward one hop at a time using branches `63`–`66`. The GeckoLib 5 rewrite is the single
biggest item in those later hops.
