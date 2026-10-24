# Porting Tolkien Tweaks to 26.1.2

Working branch: `port/26.1.2`. The finished 1.21.4 tree is on `main` and is the one this
port starts from; [PORTING-1.21.4.md](PORTING-1.21.4.md) is this file's parent and records how
1.21.4 was reached.

This is a plan, not a handoff: only the build stack has been done, and the site counts are
measurements of the 1.21.4 tree taken at the time of writing, so they are what the work will
actually meet. The first compile against 26.1.2 stops at 9,658 errors, which is that
measurement's other half.

## Where the tree is now

| | |
|---|---|
| Source | Minecraft **1.21.4**, NeoForge **21.4.161**, mod **4.4.83-Final** |
| Target | Minecraft **26.1.2**, NeoForge **26.1.2.116**, mod **5.0.0** |
| Java | 21 → **25** |
| Status | build stack in place; GeckoLib 5 renamed, assets moved, fifty-three entities converted; the mechanical 26.1 pass (renames, properties, spawn weights, tag providers), the save-data move (`ValueOutput`/`ValueInput`) and the equine family are done; 2,307 errors left |

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
| `ResourceLocation` occurrences / files | **1,910** / **259** — now **0** / **0** |
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

In this tree, after the GeckoLib conversion, 14 files still touch `MultiBufferSource` — the ones
that are not GeckoLib's: the boulder and the fell beast fireball (`BoulderRender`,
`FellBeastFireballRender`, plain `EntityRenderer`s with a hand-built cube),
`MorgulCrystalRenderer`, the seven non-GeckoLib block entity renderers, the three NPC profession
layers and `handler/render/CCRenderState`. `handler/render/` (`CCRenderState`, `CCRenderPipeline`,
`CachedFormat`) is a hand-rolled pipeline layer and will not port by rename; treat it as a small
rewrite.

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

#### Done so far

- **The rename is scripted.** [tools/port-geckolib5.pl](tools/port-geckolib5.pl) rewrites the 192
  importing files and is idempotent: the group move, the package moves that are not where 5's
  name suggests (`PlayState` → `animation.object`, `AnimatableManager` → `animatable.manager`,
  `GeoRenderer` → `renderer.base`, `GeoBone`/`BakedGeoModel` → `cache.model`,
  `BlockAndItemGeoLayer` → `renderer.layer.builtin`, `AutoPlayingSoundKeyframeHandler` →
  `animation.keyframehandler`), the 38 `animation.*` wildcard imports expanded to what survives,
  the `AnimationState` and `EntityModelData` imports dropped (no 5 equivalent), and every model
  and animation id rebased.
- **The assets moved**, with `git mv` so their history follows: `assets/tolkienmobs/geo/` →
  `assets/tolkienmobs/geckolib/models/`, `assets/tolkienmobs/animations/` →
  `.../geckolib/animations/` (56 + 45 files). The id a model asks for is now the bare path,
  `monster/rockgolem`: GeckoLib strips the `geckolib/models/` root and the `.geo.json` suffix
  itself. All 55 GeckoLib ids the code asks for resolve to a moved file.
- **Fifty-three entities are converted**: the rock golem, in
  [RockGolemEntity](src/main/java/com/greatorator/tolkienmobs/entity/monster/RockGolemEntity.java),
  [RockGolemModel](src/main/java/com/greatorator/tolkienmobs/entity/monster/model/RockGolemModel.java)
  and [RockGolemRender](src/main/java/com/greatorator/tolkienmobs/entity/monster/render/RockGolemRender.java),
  then the huron, deep claw, romie walker and warg, then the elemental golem, mimic chest and
  tree ent, then the six weapon-carrying orcs: brigand, goblin, duergar, haradrim, mordor orc
  and uruk hai, and then the last nine holders: the balrog, witch king, barrow wight, both fell
  spirits, minotaur, nazgul, oath breaker and troll. All fifteen `BlockAndItemGeoLayer` sites are
  therefore done. The mimic's idle controller never stops: a dormant chest breathes with a
  different animation than an awake one. The tree ent picks between a punch and a stomp for its
  swing. The golem has a fourth controller and a one-shot rage; the warg adds a wet-shake latch
  and a 1.5x model scale; the troll is 1.6x and scales its club with it; the minotaur's charge and
  the balrog's whip are the two remaining ranged branches (the balrog's is gated on its aggressive
  flag, as before); the nazgul and witch king sit while riding; five of them keep a translucent
  render type. Between those twenty-three they cover the API the other entities use.
- **The thirty mobs that carry no props are converted** on top of them: the seven bosses (fell
  beast, goblin king, Gwaihir, the mithril and morgul-iron golems, Shelob, the Watcher), the swamp
  hag, the Mirkwood spider, the eight trading villagers (dwarf, elf, hobbit, human, orc, orc
  trader, southron, southron trader), the six ambient animals (crebain, frog, great eagle, rat,
  squirrel, thrush) and the seven passive ones (auroch, goat, Gollum, Istari, mumakil, nazgul
  steed, Shadowfax). None of them held an item, so no layer changed; what they needed instead was
  the rest of the state. Every constant scale became `scaleModelForRender`, including the ones the
  old `preRender` applied only to babies - the frog and squirrel also singled out variant 16, and
  the fell beast, Gwaihir, Shelob, both golems and the great eagle are simply drawn at their own
  size. The head turns follow each model's own bone: `head` almost everywhere, but `neck` on the
  eagles, `Head` on the hag, `Mumakil`'s `MumuNeck`, Gollum's `GollumHead`, and the two horses
  turn only in yaw. The props those models hid are now `hiddenBones`: the frog's insect and
  tongue, the thrush's snail and leg band, the giant spider's webbing, the goat's chest and
  saddle, Gollum's fish, Istari's book, and the steeds' chests, saddles and headpieces - the last
  two only while saddled, except Shadowfax, which never shows them. The human is the one that
  keeps two texture sets: `variant` says which face and a `winter` flag on the state says which
  folder, so a human keeps its face when it changes clothes. Their controllers are built the 5
  way as well: the animatable is no longer a constructor argument, the event hands out
  `controller()` and `animatable()`, `Animation.LoopType` gave way to `thenPlay`/`thenLoop`, and
  the `getAnimationState().equals(State.STOPPED)` guard is `!isAnimatingBones()`.
- **The villager and wandering-trader bases are ported.** Between them they had four kinds of
  blocker: `net.minecraft.Util` is `net.minecraft.util.Util`; `Villager` and its `VillagerData`,
  `VillagerProfession` and `VillagerType` moved to `net.minecraft.world.entity.npc.villager`, and
  `WanderingTrader` to `npc.wanderingtrader`; save data is `ValueOutput`/`ValueInput`
  (`getIntOr("Variant", 0)`); and `TolkienVillagerEntity`'s unused `MERCHANT_VARIANT` accessor had to
  go, because `VillagerData` is built from registry holders in 26.1 and the old code handed it the
  static profession constants. Nothing read it, so no behaviour is lost. Everything else in the two
  classes - `isPassenger`, `isAggressive`, `createMobAttributes`, `finalizeSpawn` - was failing only
  because the supertype itself did not resolve; the idle controllers keep their `!isAggressive()`
  guard. Both classes are error-free now, and so are the six subclasses that extend them - dwarf,
  elf, hobbit, human, orc and southron - which needed the same two imports, plus the human's winter
  flag moving to `ValueOutput`/`ValueInput` and `isClientSide()`. The whole `entity/npc` package
  compiles.
- **The last three controller sets are converted**, which finishes the API migration: `DragonEntity`
  (five controllers), `TolkienVillagerEntity` (idle and walk) and `TolkienWanderingEntity` (the same
  two). `Animation.LoopType.PLAY_ONCE` is `com.geckolib.animation.object.LoopType.PLAY_ONCE` now, and
  `forceAnimationReset()` is gone. That call needed a
  replacement only where a controller had to restart the *same* animation, and none of these did - the
  roar and the breath already guarded on `hasRoared` and `breathPhase`, and a controller that returned
  `STOP` has cleared its animation, so the next `setAnimation` starts from the top. The one real change
  is in the dragon's attack: 4 left a playing swing alone when its handler fell through to `STOP`, but
  5 resets the controller on `STOP`, so the handler now returns `CONTINUE` while
  `controller().isAnimatingBones()` and starts a swing only from a stopped controller.
  `getWingAnimation` returns a `RawAnimation` now rather than a clip name, so the three wing poses are
  built once.
- **The three NPC profession layers are converted, and none of them is attached to anything.** 4's
  layer override was one `render(...)` that called `getRenderer().reRender(...)` with another texture;
  5 splits the hook into `addRenderData` and `submitRenderTask`, and for a whole-model second pass
  with a different texture it ships exactly that as `TextureLayerGeoLayer`. Each layer is now
  `TextureLayerGeoLayer<T, Void, TolkienGeoRenderState>` constructed with the texture and
  `RenderTypes::armorCutoutNoCull`, which is the render type the old `RenderType.armorCutoutNoCull`
  call used, so the hand-written re-render is gone. Their type arguments follow their renderers, which
  are `GeoEntityRenderer<T, TolkienGeoRenderState>`. Nothing constructs any of the three - there is no
  `withRenderLayer`/`addRenderLayer` call for them in this tree or in the 1.21 source - so they are
  converted dead code rather than a working feature.
- **The item renderer is converted, and there is only one** - the Arda staff, across `CatalystRender`,
  `CatalystModel` and `CatalystItem`. `GeoItemRenderer<T>` is already `GeoRenderer<T, RenderData,
  GeoRenderState>` in 5: its state is GeckoLib's own `GeoRenderState.Impl`, so the renderer needed no
  state class and no hook of its own, and `getGeoItemRenderer()` and
  `createGeoRenderer(Consumer<GeoRenderProvider>)` are unchanged. What did change is the model's two
  resource getters and the idle controller. The mod has no second GeckoLib item renderer - the Morgul
  crystal and the boats are vanilla renderers - so the earlier note's "2 item renderers" was one too
  many.
- **The five block entities are converted** - the well and the four lockable chests. `GeoBlockRenderer`
  is a plain `BlockEntityRenderer<T, R>` in 5, its constructor takes the provider's context before
  the model (the renderer keeps the block model resolver for the crumbling overlay), and its `R`
  only has to be a `BlockEntityRenderState`. Because none of these renderers overrides a hook that
  names `RenderPassInfo<R>` - the models draw from constants - they use vanilla's
  `BlockEntityRenderState` rather than a state class of their own. Their controllers are the 5
  shape too, including the triggerable ones: `new AnimationController<T>(name, 0, test ->
  PlayState.STOP)` still backs a `triggerableAnim`, which is what 4's wiki pattern did as well.
- **The three projectiles are converted** - the thrown cobweb, the web trap and the tornado. They
  cannot share `TolkienGeoRenderState`: that one is a `LivingEntityRenderState`, and 5 fills the
  living half by casting the entity to a living one, which a projectile is not;
  [TolkienProjectileRenderState](src/main/java/com/greatorator/tolkienmobs/entity/util/TolkienProjectileRenderState.java)
  puts the same data map on the plain `EntityRenderState` and adds `lifeTime`, `blast` and `yRot`,
  captured in `addRenderData` because the entity is gone by draw time. What `preRender` did splits
  in two: the trap's 2x scale and the tornado's 1.25x are `scaleModelForRender`, both 0 on the
  projectile's first two ticks, and the blast's turn is `adjustRenderPose`. `TornadoEntity#getTick`
  is deleted rather than ported - 5 has no such hook, because it times animations from the tick it
  stamps on each render state itself.
- **Two bosses still carry errors that predate this pass**, and they are not GeckoLib's: the
  `ServerBossEvent` three-argument constructor is gone in 26.1 (it wants a `UUID` first, and every
  boss bar in the mod uses the old one), `MobEffects.MOVEMENT_SPEED` no longer resolves from the
  class, and `FellBeastFireballEntity`'s `getY()` is not attributed. Those belong to the effects
  and boss-bar work, not to the render conversion.
- **Held items moved from `renderStackForBone` to the render state.** `BlockAndItemGeoLayer` is
  now built with `(context, this)` - it needs the item model resolver - and a layer implements
  `getRelevantBones(T, O, R, float)` returning `List<RenderData>` and `addRenderData`, which stores
  that list under `BlockAndItemGeoLayer.CONTENTS`. Each entry is
  `RenderData.item(boneName, ItemDisplayContext, ItemStackRenderState)`, and the stack state comes
  from `RenderUtil.createRenderStateForItem(stack, this.itemModelResolver, displayContext,
  animatable)`, resolved during extraction because the entity is gone by render time. The pose work
  that used to be in `renderStackForBone` is `submitItemStackRender`, which has to push and pop its
  own pose around `super` - 5 does not do it for you, unlike 4's layer. Items a mob only shows
  while swinging or shooting are decided in `getRelevantBones` from the entity's flags, which is
  the last place those flags can still be read.
- **A model that hid a prop bone now hides it through the render state.** The mimic chest's tongue
  and legs, the tree ent's held boulder, the frog's insect and tongue, the thrush's snail and leg
  band, the spider's webbing, the goat's chest and saddle, Gollum's fish, Istari's book and the
  steeds' tack used to be `GeoBone#setHidden` calls in `setCustomAnimations`, which 5 no longer
  has. The model only ever sees the state, so the
  renderer puts the names on it — `renderState.hiddenBones` — while it still has the entity, and
  `adjustModelBonesForRender` calls `skipRender(true).skipChildrenRender(true)` on each, which is
  what 4's `setHidden` did to the whole subtree. The set is replaced on every pass, so a part that
  comes back is not still hidden.
- The renderer details the converted mobs settled:
  `getTextureLocation` needs no override at all when the model's texture getter is the answer,
  `getRenderType(R, Identifier)` replaces the old four-argument one (`RenderTypes.entityTranslucent`
  is 26.1's spelling of `itemEntityTranslucentCull`), model scaling is `scaleModelForRender`
  rather than `preRender`, and `forceAnimationReset()` is gone — the `isAnimatingBones()` guard
  covers what it was doing.

#### The 5 shape, as the jar actually has it

- `GeoEntityRenderer<T extends Entity & GeoAnimatable, R extends EntityRenderState>` still takes
  `(context, model)`, but `R` must be a type that **implements `GeoRenderState`**:
  `RenderPassInfo<R>` is bounded by it, and the `EntityRenderState` that GeckoLib's mixin makes
  into one is invisible to javac. A renderer therefore needs a state class of its own;
  [TolkienGeoRenderState](src/main/java/com/greatorator/tolkienmobs/entity/util/TolkienGeoRenderState.java)
  is the shared one — `extends LivingEntityRenderState implements GeoRenderState`, its own data
  map, a `TolkienVariant variant` field, because the variant is what every model used to read off
  the entity, and a `Set<String> hiddenBones` for the models that used to hide a prop bone. It has to re-declare `addGeckolibData`/`hasGeckolibData`/`getDataMap` over its
  own map, or the mixin's map and the class's would disagree at runtime.
- Renderer hooks: `createRenderState(T, Void)`, `addRenderData(T, Void, R, float)` (the last place
  the entity itself is still visible), `getTextureLocation(R)`, and
  `adjustModelBonesForRender(RenderPassInfo<R>, BoneSnapshots)`. That last one replaces the model's
  `setCustomAnimations` + `getAnimationProcessor().getBone(...)` head turn: the pitch and yaw come
  from `DataTickets.ENTITY_PITCH`/`ENTITY_YAW` on the state, and the bone is turned with
  `snapshots.ifPresent("h_head", bone -> bone.setRotation(...))` in radians.
- Model hooks: `getModelResource(GeoRenderState)`, `getTextureResource(GeoRenderState)`,
  `getAnimationResource(T)`, all returning `Identifier`.
- Controllers: `new AnimationController<T>(name, transitionTicks, test -> ...)`, driven by
  `test.isMoving()`, `test.setAndContinue(raw)` and `test.controller()`. `AnimationController.State`
  and `getAnimationState()` are gone — ask `controller().isAnimatingBones()` — and `PlayState.STOP`
  resets the controller, so a one-shot attack has to keep returning CONTINUE while it plays.
- 26.1 arrives with it: `Identifier` replaces `ResourceLocation`, `Util` moved to
  `net.minecraft.util.Util`, and entity save data is `ValueInput`/`ValueOutput`
  (`input.getIntOr("Variant", 0)`) instead of `CompoundTag`. The `npc` package is split too -
  `Villager`, `VillagerData`, `VillagerProfession` and `VillagerType` are under `npc.villager`, and
  `WanderingTrader` under `npc.wanderingtrader` - and `VillagerData` is a record of registry
  holders now, so the static `VillagerType`/`VillagerProfession` constants no longer build one.
- **Where that leaves the tree**: 4,667 errors in 407 files, down from 9,658, measured by handing
  every source file to javac at once (852 files). The rename pass below took that to 3,470 in 337. No model still uses `EntityModelData`,
  `AnimationState` or `setCustomAnimations`, and the `AnimationController` inference failures are
  **0**, down from 88: nothing in the tree builds a controller the 4 way. Not one remaining error
  names a GeckoLib symbol - what is left is plain 26.1 source work. What the
  converted mobs still carry is the ordinary 26.1 source work: `CompoundTag` save data that has to
  become `ValueInput`/`ValueOutput`, `Level.isClientSide` and `Level.random` which are now
  `isClientSide()` and `getRandom()`, `ServerBossEvent`'s new first argument, `ResourceLocation`
  and `net.minecraft.Util`, and the superclasses 26.1 moved (`WaterAnimal`,
  `AbstractChestedHorse`, `Villager`) - the same list the twenty-three already had.

### Rename pass — DONE

- `tools/port-261-pass1.pl` is one idempotent pass over `src/main/java`, in the shape of
  `tools/port-1214-pass1.pl`: it renames `ResourceLocation` → `Identifier` (the class kept its
  `net.minecraft.resources` package) and moves `net.minecraft.Util` → `net.minecraft.util.Util`.
  **892 renames in 164 files**, and 86 files had their `Util` import moved. A second run finds
  nothing and changes nothing.
- Both are whole-word renames, so `Identifier.fromNamespaceAndPath`, `Identifier.parse`,
  `ResourceKey::location` and the mod's own data helpers `readResourceLocation` /
  `writeResourceLocation` / `createResourceLocation` kept working untouched — those method names
  are the mod's API, not vanilla's class, so they were deliberately left alone.
- Nothing inside a string literal moved: the diff was read for it, and every resource path
  (`"block/lockable_chest"`, `"textures/block/chests/..."`, `"chest"`) is byte-for-byte the same.
  Only `.java` files were touched, so no JSON or asset was rewritten.
- Imports: the old `net.minecraft.resources.ResourceLocation` line is rewritten in place, a file
  that already imported `Identifier` does not get the line twice, and the import block is deduped
  after the pass.
- `net.minecraft.resources.ResourceLocation` now appears in **no file**. `net.minecraft.**Util`
  does not either — the class is at `net.minecraft.util.Util`, and `makeDescriptionId`,
  `getRandom` and `make` all still exist on it, so nothing had to change beyond the import.
- **`ModelResourceLocation` is not part of this and is not a rename.** 26.1 has no such class and
  no `BlockModelShaper` next to it: the baked-model registry now runs through `ModelManager`,
  `ModelBakery.BakingResult` and `ResolvedModel`, which is the model/render pass's job, not a
  script's. [TolkienEventBusEvents](src/main/java/com/greatorator/tolkienmobs/event/TolkienEventBusEvents.java)
  is the one file waiting on it.
- **Where that leaves the tree** (after the rename pass): **3,470 errors in 337 files**, down from 4,667 in 407. Not one
  remaining error names `Identifier` or `Util`. The drop is the whole difference — 1,197 errors
  and 70 files — and what is left is the rest of the 26.1 pass: 1,835 `cannot find symbol`, 399
  lost overrides, the `registerItem` signature, `CompoundTag.getList`, `Level.isClientSide` and
  `Level.random`, `Optional` save-data accessors, and the tag provider's `addOptionalTag`, which
  takes a `TagKey` in 26.1 rather than the `Identifier` it was being handed.

### The rest of the mechanical pass — DONE

Five more batches after the rename, each verified with the same whole-tree javac. The tree went
from **3,470 errors in 337 files to 2,801 in 320**.

- **Level accessors** (`tools/port-261-level-accessors.pl`). 26.1 made `Level.isClientSide` private
  and `Level.random` protected; `isClientSide()` and `getRandom()` replace them. 69 field reads and
  41 files. The `isClientSide` rewrite is tree-wide and safe (`.isClientSide` has no other meaning
  and the lookahead leaves `isClientSide()` alone); the `random` rewrite is driven by the javac
  lines, because plenty of other objects have a public `random` field that must stay.
- **`noCollission()` → `noCollision()`**, 147 sites. The 1.21.x spelling was the typo; 26.1 fixed it.
- **Registry properties** (`tools/port-261-register-props.pl`). The item and block helpers now take
  a `Supplier<Properties>` (or a `UnaryOperator<Properties>`), not a built object: 222 calls, of
  which 83 plain `new Item.Properties()` became the two-argument overload and 139 were wrapped in
  `() -> `. The three block helpers in [TolkienBlocks](src/main/java/com/greatorator/tolkienmobs/init/TolkienBlocks.java)
  keep their `Properties` parameter and pass `() -> properties`, so nothing about when the
  properties are built changed.
- **`ResourceKey.location()` → `identifier()`**, 34 sites, and the biome tag provider's 44
  `addOptionalTag(x.location())` calls now pass the `TagKey` itself, which is what the appender
  wants. `TagKey.location()` still exists and was left alone.
- **Tag providers.** The vanilla `ItemTagsProvider` is gone; the mod's extends NeoForge's
  `net.neoforged.neoforge.common.data.ItemTagsProvider` now, which takes no block-tag lookup, so
  `TolkienDataGenerator` no longer wires one in. `TagAppender` grew a second type parameter
  (`<E, T>`), which the POI provider's local variable needed. Structure tags extend
  `KeyTagProvider<Structure>` - `TagsProvider` has no `tag()` in 26.1 - and the provider appends
  `ResourceKey`s, which is what `village.structure()` already was.
- **Spawn weights** (`tools/port-261-spawner-data.pl`). `MobSpawnSettings.SpawnerData` is
  `(type, minCount, maxCount)` now; the weight is the second argument of `addSpawn`. 107 calls
  rewritten, and the four `AddSpawnsBiomeModifier` sites became `AddSpawnsBiomeModifier.singleSpawn`
  with a `Weighted<>`, because that modifier takes a `WeightedList<SpawnerData>`.
- **What is left** at 2,801: 1,614 `cannot find symbol` and 397 lost overrides, which are mostly
  the same handful of moved signatures repeating - entity save data (`ValueInput`/`ValueOutput`
  instead of `CompoundTag`, and `getIntOr`/`getBooleanOr` instead of `getInt`/`getBoolean`),
  `CompoundTag.getList` (33), the `InventoryChangeTrigger` package (31), `Optional`-returning save
  accessors (27 + 21), and `SpawnerData`-style container changes of the same kind this batch did.
  Behind those: the screen pass (`GuiGraphics`, `RenderType`, `ClickType`, 111 + 90 sites) and the
  model/render pipeline, which is where `ModelResourceLocation` and the hand-rolled `handler/render`
  code go.
- **One downward rename to check in game.** Vanilla's `BlockTags.MUSHROOM_GROW_BLOCK` is gone. The
  nearest thing is `SUPPORTS_VEGETATION`, which is what `VegetationBlock` - the supertype a small
  mushroom has in 26.1 - reads for placement, so the mod's `DECAY_GROW_BLOCK` is tagged there.
  That also lets other vegetation place on those blocks, which the old tag did not; it is the one
  entry in this batch that changes what the world does rather than only how it is spelled, so it
  wants the in-game mushroom check before it is called done.

### Save data — DONE

- **Every entity and block entity writes through `ValueOutput` and read through `ValueInput` now.**
  26.1 passes one of those two instead of a `CompoundTag`, and it no longer passes the
  `HolderLookup.Provider` alongside it: `saveAdditional(ValueOutput)`, `loadAdditional(ValueInput)`,
  `addAdditionalSaveData(ValueOutput)`, `readAdditionalSaveData(ValueInput)`. 115 methods in 57
  files (`tools/port-261-save-data.pl`), with the parameter renamed through each body so no stale
  `tag.` is left behind. The `Provider` is still reachable where a codec needs it, as
  `input.lookup()`.
- **Scalar readers became the Or-with-default calls.** `ValueInput#getInt`/`getString` return
  `Optional` in 26.1, so `tag.getInt("Variant")` is `input.getIntOr("Variant", 0)`,
  `getBooleanOr`, `getStringOr`, `getShortOr`, `getLongOr`, `getFloatOr`, `getDoubleOr`, `getByteOr`
  alike. Writes needed nothing: `ValueOutput#putInt`, `putString`, `putBoolean` and the rest match
  what `CompoundTag` had.
- **There is no `contains`.** A reader that used one asks the typed accessor instead:
  `input.getInt("redstoneMode").isPresent()` for a number, `input.child("bucketInventory").isPresent()`
  for a nested compound, and `input.read("FlightOriginX", Codec.DOUBLE).isPresent()` where the value
  is a double.
- **Nested handlers write themselves.** `ItemStackHandler` and `FluidTank` are
  `ValueIOSerializable`, so `output.put("inventory", handler.serializeNBT(regs))` is
  `handler.serialize(output.child("inventory"))` and the read side is
  `handler.deserialize(input.childOrEmpty("inventory"))` - same keys, same layout. The mod's
  `TolkienFluidTank` loses its `INBTSerializable<CompoundTag>` wrapper entirely, because
  `FluidTank` carries the ValueIO pair now (`tools/port-261-value-fixes.pl`).
- **Loose item lists** - the chested goat, Shadowfax, the Nazgul steed's saddlebags and the
  spawner's mob list - use `output.childrenList(name)` with `addChild()` per entry and
  `input.childrenListOrEmpty(name)` to read them back; a stack inside an entry is
  `entry.store("item", ItemStack.CODEC, stack)` and `entry.read("item", ItemStack.CODEC)`.
  `MobSpawnData` gained the matching `(ValueInput)` constructor and `save(ValueOutput)` and lost
  `toNBT()`.
- **The spawner's own payload** (`TolkienBaseSpawner`) takes ValueIO as well, and its weighted
  table moved from `SimpleWeightedRandomList` to `WeightedList`. Spawn data is read with
  `input.read("SpawnData", SpawnData.CODEC)` and would be written with
  `output.store(...)`, so the `NbtOps` round trip and the two hand-written `resultOrPartial`
  warnings are gone; an invalid spawn entry now just leaves the previous one in place.
- **`EntityHandler`**, the helper that tracks the dragon's home position and sleeping flag, sits on
  ValueIO too. Each bridge now carries three things: how to store its type, how to read it, and how
  to tell whether the key was present - which is what `optional()` needed to replace `contains`.
- **`TagValueOutput` / `TagValueInput` bridge the places that must stay tags:** the backpack's item
  component (`saveContents` builds a `TagValueOutput` and returns `buildResult()`, and the three
  `loadContents(CompoundTag, Provider)` call sites wrap the component `CompoundTag` in a
  `TagValueInput`), the update tags built from `saveWithoutMetadata`, and `EntityType.by`, which
  reads a spawned entity out of a `ValueInput` in 26.1.
- **The four `handleUpdateTag` overrides are deleted.** 26.1 removed the hook from `BlockEntity`;
  the client gets its copy through the value pipeline now, so nothing had to replace them.
- **What is left in these files is not save data:** the spawner's spawn loop still calls
  `CompoundTag.getList("Pos", 6)` and `contains("id", 8)` (a one-argument API now), the block
  entities still override `IBlockEntityExtension.onDataPacket`, and `MilestoneHandler` still builds
  a `SavedData.Factory` the old way. 2,307 errors remain tree-wide, down from 2,801.

### Equine family — DONE

- `net.minecraft.world.entity.animal.horse` is `net.minecraft.world.entity.animal.equine` in 26.1,
  and the three chested mobs that extend `AbstractChestedHorse` - the goat, Shadowfax and the
  Nazgul steed - were the only files importing the old package. With the import moved they compile:
  the supertype resolves, so the `inventory` container, `createInventory()` and the
  `hasChest`/`setChest` pair the three of them use come back with it, and 195 errors went with the
  package.
- Three signatures under them changed and needed no design work: `causeFallDamage` takes a `double`
  distance now, so each override's first parameter is `double`; `Entity#moveTo(x, y, z, yRot,
  xRot)` is `snapTo`, which is what the rider spawns in the goat's and the Nazgul steed's
  `finalizeSpawn` use; and `AbstractHorse#setStanding` takes the tick count it stands for rather
  than a flag, with `clearStanding()` beside it for the false case.
- **The goat's head-down warning** is the one place that needed a number. `setStanding(true)` became
  `setStanding(WARNING_TICKS)`, a 40-tick constant that is also how long its scream lasts, so the
  goat holds the pose for the warning instead of for as long as its attack goal happens to keep
  asking; every `setStanding(false)` became `clearStanding()`, which is the same pose dropped at
  once. Nothing else about the three mobs changed - their saddling, chests, sounds, variants and
  controllers read as before.

### Build stack — DONE

- `net.neoforged.moddev` **1.0.14 → 2.0.148**, `id 'idea'` applied explicitly (MDG 2 applies it
  only during sync, and the `idea { }` block needs it), wrapper **8.13 → 9.2.1** (the official
  26.1.2 MDK's version; NeoForge requires 9.1.0 or newer), foojay resolver 0.8.0 → 1.0.0.
- `java.toolchain.languageVersion = 25`. Gradle still launches on the JVM's JDK 17; the
  toolchain resolver downloaded Temurin 25.0.4.1 on the first build and it is what compiled
  the tree, so no JDK has to be installed by hand.
- The dependency bumps all resolve at the plan's versions (verified against the repositories,
  not just announced): `com.geckolib:geckolib-neoforge-26.1.2:5.5.2`,
  `com.github.glitchfiend:TerraBlender-neoforge:26.1.2-26.1.2.0.3`,
  `top.theillusivec4.curios:curios-neoforge:15.0.0+26.1.2`,
  `mezz.jei:jei-26.1.2-common-api` / `-neoforge-api` / `-neoforge:29.47.0.111`. GeckoLib 5 is
  still published from the same Cloudsmith repository, under the new `com.geckolib` group, so
  only the `includeGroup` changed.
- The data run is now `data { clientData() }` rather than a run named `clientData` with
  `type = "clientData"`, which is the MDK's shape and gives the task back the name `runData`
  that this file and [VILLAGES.md](VILLAGES.md) already use. `runClient`, `runServer`,
  `runGameTestServer` and `runData` all register.
- `clientDataAdditionalRuntimeClasspath "com.ibm.icu:icu4j:76.1"` is **gone**: it existed for a
  ModDevGradle 1.0.14 classpath quirk that the plugin's own docs say ended with Minecraft
  1.21.9. First `runData` will show whether anything still needs the client distribution's
  libraries; if it does, the line comes back as `dataAdditionalRuntimeClasspath`.
- `libraries.minecraft.net` stays (NeoForm still pulls `jtracy` from it).
- `-Xmaxerrs` is now `-PmaxCompileErrors`, default **1000**, not 20000. Gradle 9.2.1 cannot
  carry a report the size of this port's first compile: at 9,658 errors the daemon died with
  `OutOfMemoryError: Java heap space` while delivering the diagnostics, with the default heap
  and again at `-Xmx4G`, and the only thing lost was the summary. At 1,000 the build fails
  cleanly and prints `BUILD FAILED` with the error list. Raise the cap per run if a wider
  sweep is ever worth it.
- `org.gradle.jvmargs` went from `-Xmx1G` to `-Xmx4G -XX:MaxMetaspaceSize=1G` while chasing
  that, and to give the port's compile room. It is not what fixed the report.
- `[[dependencies]]` for `terrablender` in `neoforge.mods.toml` now asks for
  `[26.1.2.0.3,)`; the jar's own `version` is `26.1.2.0.3`, so the old `[4.3.0.2,4.4)` range
  would have refused to load.
- **Not done:** `mod_version` is still `4.4.${currentBuildNumber}`. It becomes `5.0.x` when
  the port is released, not before, so that a test jar cannot be mistaken for a release.

## Ordered work list

1. **Build stack — DONE.** MDG 2, JDK 25, NeoForge 26.1.2, the four dependency bumps. The
   project configures and `compileJava` fails only on source, not on the build: 9,658 errors
   in the tree, reported 1,000 at a time. See *Build stack — DONE* above for what was
   verified and the one workaround that had to change.
2. **GeckoLib 5 — DONE.** The rename, the asset moves and fifty-three entities are done (see above),
   which includes every `BlockAndItemGeoLayer` site, every mob renderer, the three projectile
   renderers, the five block entities, the one item renderer, the three profession layers and every
   controller in the tree. No error left in the build names a GeckoLib symbol and no
   `AnimationController` inference failure remains, so the 192 importing files all resolve against
   `com.geckolib`; what still fails is ordinary 26.1 source work.
3. **Rename pass — DONE** for `ResourceLocation` → `Identifier` and the `Util` move, in one
   idempotent script (see above). The *subpackage* moves that belong to the same family —
   `Villager` under `npc.villager`, `WaterAnimal`, `AbstractChestedHorse` under
   `animal.equine` — are done family by family as each one is touched, so they stay with the steps
   below rather than a script. The equine family is done (see *Equine family — DONE* above).
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

- `./gradlew compileJava` throughout. It reports 1,000 errors at a time by default
  (`-PmaxCompileErrors=5000` for a wider sweep) because Gradle dies carrying the whole list.
- The **total** error count needs `javac` on its own: write the classpath out with an init script
  (`tasks.register('printCompileCp') { doLast { file.text = sourceSets.main.compileClasspath.asPath } }`),
  then run `javac -J-Xmx4g -proc:none -Xmaxerrs 100000 -implicit:none -cp <classpath>
  @<every source file>`. That is how 7,671-after-the-GeckoLib-pass was measured without Gradle in
  the way. A single file can be checked the same way with `-sourcepath src/main/java`, which
  reports errors for that file while leaving its unported neighbours alone — that is how the rock
  golem was checked before the tree could build.
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
