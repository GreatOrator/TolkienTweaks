# Porting Tolkien Tweaks to 1.21.4

Working branch: `port/1.21.4`. The untouched 1.21.1 code is on `main` / the backup branch.
This file is the handoff: it records what is done, what is measured, and what to do next.

## Where the port stands

| | |
|---|---|
| Target | Minecraft **1.21.4**, NeoForge **21.4.161** |
| Compile errors at the start | **5,720** in 425 files |
| Compile errors now | **5,184** in 382 files |
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

### 1. Model datagen — 2,394 errors, 4 files

`TolkienBlockStateProvider` (1,214), `TolkienItemModelProvider` (554),
`BlockModelHelper` (346), `BlockModelBuilders` (280).

NeoForge 21.4 **removed** `net.neoforged.neoforge.client.model.generators`
(`ModelFile`, `BlockModelBuilder`, `BlockStateProvider`, `ExistingFileHelper`,
`ConfiguredModel`, `VariantBlockStateBuilder`). Only `loaders/` and `template/` remain.
It also removed `ExistingFileHelper` outright, so NeoForge's source cannot be copied back in
(it is LGPL-2.1 anyway). A replacement is being written in
`src/main/java/com/greatorator/tolkienmobs/datagen/modelgen/`, keeping the old class and
method names so the providers only need their imports changed.

Done so far in that package (compiles clean against 1.21.4): `ModelFile`, `ModelBuilder`
(textures, `render_type`, the element/face/rotation DSL), `BlockModelBuilder`,
`ItemModelBuilder` (overrides), `ConfiguredModel`, `VariantBlockStateBuilder`,
`MultiPartBlockStateBuilder`, `ModelProvider` (the shapes and parents the mod uses, and the
`models/` path rule: a name with a slash is used verbatim, a bare name gets the folder prefix),
and an `ExistingFileHelper` stand-in.

Still to write: `BlockStateProvider` (the shape library — stairs, slab, wall, fence, gate,
button, plate, door, trapdoor, pane, log, sign, directionals — plus `getVariantBuilder`
and `getMultipartBuilder`), `BlockModelProvider`/`ItemModelProvider` subclasses,
`ItemLayerModelBuilder` with `ModelBuilder#customLoader`, then swap the imports in
`TolkienBlockStateProvider`, `TolkienItemModelProvider`, `BlockModelHelper` and
`BlockModelBuilders` and delete the last `net.neoforged...generators` imports.

1.21.4 also needs item-model **definitions**: `assets/<ns>/items/<name>.json` containing
`{"model":{"type":"minecraft:model","model":"tolkienmobs:item/<name>"}}`. Vanilla ships
1,385 of these; the mod needs 885. `ItemModelProvider` should emit them alongside
`models/item/*.json`.

The call sites use a small vocabulary — `models().cubeAll/cubeBottomTop`,
`blockWithItem`, `stairsBlock`, `slabBlock`, `wallBlock`, `doorBlockWithRenderType`,
`trapdoorBlockWithRenderType`, `directionalBlock`, `paneBlockWithRenderType`,
`blockItem`, `modLoc`, `mcLoc`, `blockTexture`, `basicItem`. Reimplementing those as a
local JSON-emitting helper keeps all 1,579 lines of provider code intact.

Verification plan: `src/generated/resources` already holds the 5,001 JSONs these providers
produce, so once the mod compiles, `./gradlew runData` and a JSON-aware diff against the
committed files is the acceptance test. Compare parsed JSON, not bytes: key order inside an
object is not meaningful and Gson writes them in insertion order.

### 2. Entity renderers and models — the render-state rework

1.21.2 split every renderer into renderer + render state. In 1.21.4:

- `MobRenderer<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>>`
- `HierarchicalModel` and `ListModel` **no longer exist**; models extend `EntityModel<S>` or `Model`
- `GuiGraphics.blit` takes a `RenderType::guiTextured`-style function first (60 call sites)
- GeckoLib 4.8.5's `GeoEntityRenderer.render` has a different signature (108 call sites)

55 renderers, 61 GeoModels and 56 animation controllers are affected.

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
