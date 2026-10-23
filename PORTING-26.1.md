# Porting Tolkien Tweaks to 26.1.2

Working branch: `port/26.1.2`. The finished 1.21.4 tree is on `main` and is the one this
port starts from; [PORTING-1.21.4.md](PORTING-1.21.4.md) is this file's parent and records how
1.21.4 was reached.

This is a plan, not a handoff: nothing below has been done yet, and the site counts are
measurements of the 1.21.4 tree taken at the time of writing, so they are what the work will
actually meet.

## Where the tree is now

| | |
|---|---|
| Source | Minecraft **1.21.4**, NeoForge **21.4.161**, mod **4.4.83-Final** |
| Target | Minecraft **26.1.2**, NeoForge **26.1.2.116**, mod **5.0.0** |
| Java | 21 → **25** |
| Status | not started |

What the source tree contains, counted:

| Thing | Count |
|---|---|
| Java files | 851 (94,375 lines) |
| Files importing `software.bernie.geckolib` | **192** |
| — of those: `GeoEntityRenderer` / `GeoBlockRenderer` / `GeoItemRenderer` | 56 / 5 / 2 |
| — `GeoModel` | 62 |
| — `implements GeoEntity`/`GeoBlockEntity`/`GeoItem` | 59 |
| — `BlockAndItemGeoLayer` / `GeoRenderLayer` | 15 / 3 |
| GeckoLib assets | **56** `*.geo.json`, **45** `*.animation.json`, all under `assets/tolkienmobs/geo/` |
| Files using `MultiBufferSource` | **69** |
| Files importing `net.minecraft.client.renderer.RenderType` | 52 |
| Files with a hand-rolled pipeline layer | 3 (`handler/render/`) |
| `*Screen` subclasses / `AbstractContainerScreen` | 18 / 18 |
| Block entities | 14 |
| `DeferredRegister` holders | 23 files |
| `ResourceLocation` occurrences / files | **1,910** / **259** |
| Blockstate JSON / JSON totals (main + generated) | 588 / 6,084 |
| Mixins | **none** |
| TerraBlender / Curios / JEI call sites | 2 files / 1 file / 4 files |

Package sizes, for ordering work: `entity` 261, `block` 101, `handler` 85, `containers` 67,
`world` 67, `datagen` 53, `util` 45, `network` 45, `item` 38, `init` 34, `event` 11,
`particle` 9, `enchantment` 7, `recipe` 6, `integration` 5.

## Why 26.1.2, and why not the steps between

The cost of this port is **cumulative and paid once**, wherever the tree stops. The three
things that dominate it — GeckoLib 4 → 5, the item-class removal in 1.21.5, and the
render-state/submission rewrite that runs across 1.21.6 → 1.21.11 — are the same work whether
the destination is 1.21.5, 1.21.11 or 26.1. Landing on an intermediate version therefore buys
nothing and strands the mod on a version the ecosystem has already left.

That last point is not a hunch. Create's own development-status table marks **1.21.2–1.21.11
"Skipped"** and lists 26.1 as in progress; All the Mods 11 is assembled on **26.1.2**; FTB
Evolution 2 and FTB's 26.x testing pack are on **26.1.2**; and NeoForged's 26.1 release post
asks modders to start porting to 26.1 (while asking PRs to target 1.21.11 meanwhile). 1.21.5
itself is eighteen months old and was superseded by 1.21.6 within three months.

The versions since the current base:

| Version | Released | What it was |
|---|---|---|
| 1.21.5 | Mar 2025 | Content drop (Spring to Life) |
| 1.21.6 | Jun 2025 | Content drop (Chase the Skies) — GUI prepare/render split |
| 1.21.7 / 1.21.8 | Jun / Jul 2025 | Hotfixes for 1.21.6 |
| 1.21.9 | Sep 2025 | Content drop (Copper Age) — submission-based rendering |
| 1.21.10 | Oct 2025 | Hotfix for 1.21.9 |
| 1.21.11 | Dec 2025 | Content drop (Mounts of Mayhem) — `Identifier` rename |
| 26.1 / .1 / .2 | Mar–Apr 2026 | First 2026 drop, then two hotfixes |
| 26.2 | Jun 2026 | Content drop — Vulkan, `GpuFormat`, `VertexFormat` rewrite |
| 26.3 | Sep 2026 | Content drop, current latest |

**Why not 26.3.** It is stable and every dependency exists, but 26.2 is a second rendering
upheaval of its own (Vulkan support, blend factors, `GpuFormat`, the `VertexFormat` rewrite,
`BindGroupLayout`) and 26.3 sits on top of it. Landing on 26.1.2 first means the enormous
1.21.4 → 26.1 leap is debugged against a quiet branch, and the 26.1 → 26.2 pipeline work is
then a separate, smaller pass over code that already compiles. If the mod is only ever going
to one version, 26.3 is defensible — GeckoLib has a 5.5.7 release for it and JEI has a release
build — but expect the first two hundred errors to be rendering errors with four versions of
change behind them. 26.1.2 also has the better dependency *status* of the two (release builds
for GeckoLib and Curios).

## Target stack

```properties
minecraft_version=26.1.2
minecraft_version_range=[26.1.2,26.2)
neo_version=26.1.2.116
neo_version_range=[26.1.2,)
geckolib_version=5.5.2
terrablender_version=26.1.2.0.3
curios_version=15.0.0
jei_version=29.47.0.111
jei_mc_version=26.1.2
```

| Dependency | Version | Coordinate | Status |
|---|---|---|---|
| TerraBlender | 26.1.2-26.1.2.0.3 | `com.github.glitchfiend:TerraBlender-neoforge` | Beta (Glitchfiend marks every build this way) |
| Curios | 15.0.0+26.1.2 | `top.theillusivec4.curios:curios-neoforge` | Release |
| GeckoLib | 5.5.2 | `com.geckolib:geckolib-neoforge-26.1.2` — **group id changed** | Release |
| JEI | 29.47.0.111 | `mezz.jei:jei-26.1.2-common-api` / `-neoforge-api` / `-neoforge` | Beta only on this branch |
| ModDevGradle | 2.0.148 | `net.neoforged.moddev` | Release (Sep 2026) |

For reference, the other two branches: 26.2 has GeckoLib 5.5.6, Curios 16.0.0, TerraBlender
26.2-26.2.0.0.2; 26.3 has GeckoLib 5.5.7, Curios 17.0.0-beta.3, TerraBlender 26.3-26.3.0.0.9 and
JEI 31.14.0.72 as the newest release.

## What each hop contains, and what it costs here

| Hop | Headline change | Where it lands in this tree |
|---|---|---|
| 1.21.4 → 1.21.5 | `SwordItem`/`DiggerItem`/`ArmorItem` removed; equipment rework; weighted lists; block entity removal split | 3 item classes, 5 `inventoryTick` overrides, `TolkienArmorItem`, `TolkienBaseSpawner`, 4 container block entities |
| 1.21.5 → 1.21.6 | GUI split into prepare + render phases | 18 screens, the widget set, `FluidTankRenderer` |
| 1.21.8 → 1.21.9 | Rendering becomes submission-based | 69 files with `MultiBufferSource`, `handler/render/pipeline/`, the non-GeckoLib renderers |
| 1.21.10 → 1.21.11 | `ResourceLocation` → `Identifier`, package moves | 1,910 sites in 259 files; 2 sound instances |
| 1.21.11 → 26.1 | **Java 25**; loot type unrolling; Neo-side changes | `TolkienLootFunctions`, `TrinketRandomlyFunction`, `TolkienItemsModifier` |

### 1.21.4 → 1.21.5

The item base classes are gone, and what remains of their behaviour is a set of data
components: `WEAPON` for damage, `TOOL` for mining, `ARMOR` for protection, `BLOCKS_ATTACKS`
for blocking, with `ATTRIBUTE_MODIFIERS`, `MAX_DAMAGE`, `REPAIRABLE` and `ENCHANTABLE` filling
in the rest. `Item$Properties` gained `sword`, `pickaxe`, `axe`, `hoe`, `shovel`, `tool`,
`humanoidArmor` and `trimMaterial` to set them.

In this tree:

- `TolkienSwordItem extends SwordItem` → plain `Item` with `Properties#sword`, keeping the
  foil override and the lore line.
- `DwarvenHammerItem extends DiggerItem` → `Item` with `Properties#tool`, and its
  `BlockTags.MINEABLE_WITH_PICKAXE` becomes part of the `TOOL` component rather than a
  constructor argument.
- `TolkienArmorItem extends ArmorItem` → `Item` with `Properties#humanoidArmor`. This one is
  the most work of the three, because it reads armour state that also changes:
  `player.getArmorSlots()` is removed (line 61) and `player.getInventory().getArmor(n)` is
  removed (lines 73–76); `LivingEntity#equipment` and `EquipmentSlot` replace both.
  `inventoryTick(ItemStack, Level, Entity, int, boolean)` becomes
  `inventoryTick(ItemStack, ServerLevel, Entity, EquipmentSlot)` — there are five overrides in
  the tree: `TolkienArmorItem`, `TrinketItem`, `CoinPouchItem`, `HobbitRingItem`, `KeyRingItem`.
- `TolkienShearsItem extends ShearsItem` — `ShearsItem` is **not** in the primer's removal
  list, so this one is expected to survive; check it first and don't rework it by reflex.
- `EquipmentSlot.SADDLE` and the `Saddleable` interface are removed. The tree has no
  references to either (checked), so nothing to do.

Weighted lists move from `SimpleWeightedRandomList`/`WeightedRandomList` to `WeightedList`
plus `WeightedRandom` helpers that take a weight function:

- `TolkienBaseSpawner` — the field, the parse in `readAdditionalSaveData`, and `empty`/`single`
  (3 sites). `SpawnData.LIST_CODEC` is now a `WeightedList` of `SpawnData`.
- `TolkienCaveCarvers` (1 site) and `TolkienDecorators`'s `WeightedStateProvider` (1 site).

Block entity removal is split in two, and `Containers.dropContentsOnDestroy` is gone:

- `CamoChestBlock#onRemove` calls it directly.
- `FireplaceBlockEntity`, `PiggyBankBlockEntity` and `TrinketTableBlockEntity` each call
  `Containers.dropContents` from their own removal path; those move to
  `BlockEntity#preRemoveSideEffects`, and the neighbour updates move to
  `BlockBehaviour#affectNeighborsAfterRemoval` (`Containers#updateNeighboursAfterDestroy`).
- `BackpackBlock`, `FireplaceBlock` and `MilestoneBlock` also override `onRemove`.

Also worth checking here: `RecipeProvider#trimSmithing` and `SmithingTrimRecipeBuilder` now
take a `Holder`/key for the trim pattern, which touches any datagenned trim recipes.

### 1.21.5 → 1.21.6

GUI rendering becomes two phases. The prepare phase (`Gui#render`,
`AbstractContainerScreen#renderBg`, …) no longer draws; it submits `GuiElementRenderState`
objects into a `GuiRenderState` that `GuiGraphics` writes to, and a `GuiRenderer` sorts and
draws them later. Ordering no longer follows call order: it is sorted by scissor area, then by
pipeline order, then by texture hash, inside a tree of nodes grouped into strata.

In this tree: all 18 screens are `AbstractContainerScreen` subclasses, plus the widget set
(`TolkienButton`, `TexturedButton`, `IconButton`, `ToggleButton`, `GrayscaleButton`,
`NumberButton`), `FluidTankRenderer`, and the parts that draw something bespoke rather than a
vanilla widget — the milestone gauge and the tab/arrow decorations are the ones to look at
first, since explicit strata may be needed where draw order used to be implied.

### 1.21.8 → 1.21.9

Rendering becomes submission-based: renderers submit through `SubmitNodeCollector` instead of
drawing immediately. `EntityRenderer` and its layers use `submit`; `BlockEntityRenderer` gains
a render-state type, `extractRenderState` and `submit`; particles get extracted and submitted
too; model setup consumes render state rather than the entity.

In this tree: 69 files touch `MultiBufferSource`, and the renderers that are not GeckoLib's are
the ones this actually lands on — the projectiles (`BoulderRender`, `FellBeastFireballRender`,
`TolkienArrowRenderer`, `SimpleTrapRender`, `TornadoRender`), `MorgulCrystalRenderer`, the three
boat renderers, the camo spawner/locust/lightning bug renderers, `WatcherRender` and
`HumanRender`. `handler/render/` (`CCRenderState`, `CCRenderPipeline`, `CachedFormat`) is a
hand-rolled pipeline layer and will not port by rename; treat it as a small rewrite.

The 62 GeckoLib renderers are expected to move *with* GeckoLib 5 rather than individually,
which is why the GeckoLib job comes first.

### 1.21.10 → 1.21.11

Mostly a rename shuffle, but a wide one:

- `ResourceLocation` → `Identifier`, including method names, `ResourceKey#location` →
  `identifier`, `FriendlyByteBuf#readIdentifier/writeIdentifier`, `ResourceLocationArgument` →
  `IdentifierArgument`. **1,910 sites in 259 files** — script it, the way
  `tools/port-1214-pass1.pl` did the 1.21.4 renames.
- Most utility classes move under `net.minecraft.util`;
  `advancements.critereon` → `advancements.criterion`.
- `client.model` and `world.entity` are reorganised into subpackages by backing object, so
  every vanilla model/entity import in `entity/` (261 files) needs reimporting.
- The rendering side gets a further pass: `RenderType` → `RenderTypes`, `RenderSetup`,
  `GpuSampler`, and texture binding through samplers.
- `SoundInstance#getLocation` → `getIdentifier`, which touches `DiveWindSound` and
  `FlyingSound`.

### 1.21.11 → 26.1

- **Java 25.** The toolchain, the IDE (IntelliJ 2025.2+, Eclipse 2025-12) and the Gradle
  plugin all move with it. Vanilla also returns to being deobfuscated, which matters only if a
  non-official mapping set was ever used — it was not here.
- **Loot type unrolling.** Loot pools, functions, conditions and the various providers no
  longer wrap their registry entry in a `*Type` record: the registry takes the `MapCodec`
  directly and `getType()` becomes `codec()`. This tree registers one function type
  (`TolkienLootFunctions`, a `LootItemFunctionType` holding a `MapCodec`), used by
  `TrinketRandomlyFunction` and `TolkienItemsModifier`.
- Neo-side changes are summarised in the Neo changes page for 26.1
  (<https://docs.neoforged.net/primer/docs/26.1/neo/>) and have not been read in full here.

## The two jobs that are not a hop

### GeckoLib 4 → 5

This is the largest single block of work and it is unavoidable for any version above 1.21.4.
192 files import it; the shape changes, not just the names.

- **The package and coordinates change**: `software.bernie.geckolib` → `com.geckolib`,
  `software.bernie.geckolib:geckolib-neoforge-<mc>` → `com.geckolib:geckolib-neoforge-<mc>`.
  That is a cheap scripted rename, and it should be done in the same pass as the rest.
- **Renderers are render-state driven**: `GeoRenderer#getAnimatable` is gone, many parameters
  move onto `GeoRenderState`, and `GeoEntityRenderer#createBaseRenderState` becomes
  `createRenderState`.
- **Animation controllers lose the animatable**: `AnimationController` no longer takes it,
  `process` is renamed `startTick`, and `AnimationState` becomes internal — user code is
  directed at `AnimationTest` instead. 18 files build controllers, 18 use `RawAnimation`.
- **Model getters drop the renderer parameter** (`GeoModel`), and per-bone renderers are gone
  in favour of render layers.
- **Assets relocate**: GeckoLib 5 reads models from `assets/<modid>/geckolib/models/` and
  animations from `assets/<modid>/geckolib/animations/`, with the old path prefix/suffix
  handling removed from the resource references. Here that is 56 + 45 files moving out of
  `assets/tolkienmobs/geo/`, plus the same change in every `GeoModel` that builds a path.

There is **no GeckoLib 4 → 5 migration guide**. The changelogs are the record
(5.0-alpha1, then 5.3-alpha-3 which renamed `defaultRender` → `submitRenderTasks` and
`actuallyRender` → `buildRenderTask` and warned that more was coming). On 26.1 the API is the
settled 5.5 line, so read its changelog before starting and let the first converted entity set
the pattern for the other 191 files.

### Build stack

- `net.neoforged.moddev` **1.0.14 → 2.0.148**. MDG 2 is the stable plugin line and carries its
  own breaking changes; read the list before editing `build.gradle`.
- `java.toolchain.languageVersion = 25`, and the build must run on a JDK 25 toolchain.
- Three 1.21.4 workarounds in [build.gradle](build.gradle) need re-checking rather than
  deleting on sight: the `libraries.minecraft.net` repository (added for `com.mojang:jtracy`),
  the `clientDataAdditionalRuntimeClasspath "com.ibm.icu:icu4j:76.1"` line (a ModDevGradle
  1.0.14 classpath quirk), and `-Xmaxerrs 20000`. Keep the last one for the port itself; it
  is what makes one compile run report the whole tree.

## Ordered work list

1. **Build stack.** MDG 2, JDK 25, NeoForge 26.1.2, the four dependency bumps. *Done when*
   the project configures and `compileJava` fails only on source, not on the build.
2. **GeckoLib 5.** Package rename and asset moves first (scripted), then the renderers and
   controllers. *Done when* the 192 importing files compile against `com.geckolib`.
3. **Rename pass.** One idempotent script for `ResourceLocation` → `Identifier`, the `util`
   moves and the model/entity subpackages — the same shape as `tools/port-1214-pass1.pl`.
4. **Items and equipment.** The three removed base classes, the five `inventoryTick`
   signatures, and `TolkienArmorItem`'s armour lookups.
5. **Block entities.** `preRemoveSideEffects` / `affectNeighborsAfterRemoval`, and
   `dropContentsOnDestroy` in `CamoChestBlock`.
6. **Worldgen.** Weighted lists in `TolkienBaseSpawner`, `TolkienCaveCarvers`,
   `TolkienDecorators`; then TerraBlender (`TolkienMobsMain` lines 181–182) and the surface
   rules.
7. **Renderers.** `handler/render/` and the non-GeckoLib renderers, against the submission API.
8. **Screens.** 18 screens and the widget set against the prepare/render split.
9. **Datagen.** `TolkienDataGenerator`, the vendored `datagen/modelgen` package (NeoForge
   removed the model generators in 21.4, so it was written here — it needs a pass of its own),
   and the loot providers for the unrolled registry.
10. **Long tail.** JEI plugin (4 files), Curios (1), network payloads, the two sound instances.

## Verification

- `./gradlew compileJava` throughout, with `-Xmaxerrs` still set so one run reports everything.
- `./gradlew runData`, then a **parsed-JSON** diff of `src/generated/resources` against the
  6,084 committed files (not a byte diff). A second run must write 0 files.
- `tools/modelgen-verify/run.sh` compares the vendored generator's output against the vanilla
  client jar; it is tied to the 1.21.4 jar today and must be re-pointed at 26.1 before it is
  trusted again.
- `./gradlew runGameTestServer` covers the six registered namespaces (`tolkienmobs`,
  `tolkienmobs_flight`, `_mining`, `_villages`, `_coins`, `_fellbeast`).
- Worldgen needs the in-game loop, not a compile: one test world per biome, checking the
  TerraBlender region still places the Tolkien biomes and the surface rules still lay the
  right floor.
- The trim sprites are a cheap canary for the datagen stack — 12 PNGs from three palettes —
  and the palette check (every non-transparent pixel is a palette colour) should still pass
  afterwards.

## Open questions

- **TerraBlender's region API across 26.1.** Only two files call it, but they call it directly.
  Every branch after 1.21.4 ships it marked Beta; read the changelog before bumping rather
  than assuming `Region`/`Regions`/`SurfaceRuleManager` are unchanged.
- **JEI on 26.1.2 is beta only.** Four files use it, so the exposure is small, but the plugin
  may need re-writing against a moving API.
- **The Neo-side 26.1 changes** are summarised here from the primer's own overview, not read
  line by line.
- **26.2 is a rendering pass of its own** (Vulkan, `GpuFormat`, vertex-format rewrite,
  `BindGroupLayout`). If the mod goes there later, budget a second port, not a bump.
- The Kaupenjoe branch references listed in [PORTING-1.21.4.md](PORTING-1.21.4.md) stop at
  `66-Update1.21.11`; there is no equivalent 26.1 reference recorded there.

## References

- Porting primers, one per hop: [1.21.5](https://docs.neoforged.net/primer/docs/1.21.5/),
  [1.21.6](https://docs.neoforged.net/primer/docs/1.21.6/),
  [1.21.9](https://docs.neoforged.net/primer/docs/1.21.9/),
  [1.21.10](https://docs.neoforged.net/primer/docs/1.21.10/),
  [1.21.11](https://docs.neoforged.net/primer/docs/1.21.11/),
  [26.1](https://docs.neoforged.net/primer/docs/26.1/), and the
  [26.1 Neo changes](https://docs.neoforged.net/primer/docs/26.1/neo/).
- [NeoForge 26.1 release post](https://neoforged.net/news/26.1release/),
  [NeoForge versioning](https://docs.neoforged.net/docs/gettingstarted/versioning/),
  [ModDevGradle 2](https://neoforged.net/news/moddevgradle2/).
- [TerraBlender files](https://www.curseforge.com/minecraft/mc-mods/terrablender-neoforge/files/all),
  [GeckoLib files](https://www.curseforge.com/minecraft/mc-mods/geckolib/files/all),
  [Curios files](https://www.curseforge.com/minecraft/mc-mods/curios/files/all),
  [JEI files](https://www.curseforge.com/minecraft/mc-mods/jei/files/all).
- Ecosystem position: [Create development status](https://wiki.createmod.net/users/development-status),
  [All the Mods 11](https://www.curseforge.com/minecraft/modpacks/all-the-mods-11),
  [FTB packs](https://www.feed-the-beast.com/modpacks).
