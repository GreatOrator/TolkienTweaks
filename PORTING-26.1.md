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
| Status | build stack in place; GeckoLib 5 renamed, assets moved, fifty-three entities converted; the mechanical 26.1 pass (renames, properties, spawn weights, tag providers), the save-data move (`ValueOutput`/`ValueInput`), the equine family and the camo spawner are done; 2,275 errors left |

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
- **What is left in these files is not save data:** the block entities still override
  `IBlockEntityExtension.onDataPacket`, and `MilestoneHandler` still builds a `SavedData.Factory`
  the old way. 2,275 errors remain tree-wide, down from 2,801.

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

### Camo spawner — DONE

- [TolkienBaseSpawner](src/main/java/com/greatorator/tolkienmobs/block/TolkienBaseSpawner.java)
  compiles. Its spawn loop read the candidate position out of the spawn tag with
  `CompoundTag.getList("Pos", 6)` and picked the entity id out with `contains("id", 8)`; both lost
  their type argument in 26.1, so they are `getListOrEmpty("Pos")` and `contains("id")`, and the
  three element reads are the `getDoubleOr(index, 0.0)` calls beside them (the loop already
  guarded on the list's size, so the default is never the value used).
- The two places a spawned entity was placed with `Entity#moveTo(x, y, z, yRot, xRot)` - once
  inside the load processor, once after the cluster and collision checks - use `snapTo`, the 26.1
  name for the same call.
- `EntityType.loadEntityRecursive(tag, level, reason, Function.identity())` takes an
  `EntityProcessor` instead of a `Function`, and `EntityProcessor.NOP` is the same identity
  transform. `clearSpawnData()` builds `WeightedList.of()` rather than
  `SimpleWeightedRandomList.empty()`.
- The block entity's spawn-egg read is a spawn-data call too:
  `SpawnEggItem#getType(ItemStack)` is static now and takes no registry access, so adding an egg to
  the camo spawner reads the entity type the same way it did.
- The block entity's own chat and packet hooks are on 26.1 as well, so `CamoSpawnerBlockEntity`
  compiles. `Player#displayClientMessage(Component, boolean)` is gone: the message goes through
  `Player#sendSystemMessage(Component)`, the one-argument call that replaced it, and the `boolean`
  that used to choose chat over the action bar is a separate method now (`sendOverlayMessage`) -
  these three messages were already chat, so the flag simply has no counterpart left to pass.
- The `onDataPacket` override is deleted rather than re-signed. Vanilla's `BlockEntity` has no
  such method in 26.1; the hook lives on NeoForge's `IBlockEntityExtension` as
  `onDataPacket(Connection, ValueInput)`, whose default is `self().loadWithComponents(input)`.
  The override here only called `super`, so the default does exactly what the class used to do by
  hand, and keeping it would have been a no-op with a new signature. `getUpdatePacket` and
  `getUpdateTag` are unchanged and carry the sync as before.
- [TolkienSpawner](src/main/java/com/greatorator/tolkienmobs/handler/interfaces/TolkienSpawner.java)
  compiles too, and its tooltip read needed care rather than a rename. `DataComponents.BLOCK_ENTITY_DATA`
  is a `TypedEntityData<BlockEntityType<?>>` in 26.1, not a `CustomData`, so the old
  `(CustomData) stack.getOrDefault(BLOCK_ENTITY_DATA, CustomData.EMPTY)` is a trap:
  `DataComponentGetter#getOrDefault(DataComponentType<? extends T>, T)` infers `T` from *both*
  arguments, so with a `CustomData` default it infers `Object` and the cast compiles unchecked -
  and throws `ClassCastException` on the first hover of a configured spawner, because the value it
  is handed is the entity data. `stack.get(DataComponents.BLOCK_ENTITY_DATA)` with a null check is
  what vanilla's own `Spawner#getSpawnEntityDisplayName` does; it reads through
  `TypedEntityData#copyTagWithoutId()` (`getUnsafe()` is deprecated) and needs no cast.
- The tag reads under it lost their shapes: `CompoundTag#contains(String, int)` is
  `contains(String)`, `getCompound` returns an `Optional`, and `getString` does too, so the nested
  read into the spawn entry is `getCompoundOrEmpty(key).getCompoundOrEmpty("entity").getStringOr("id", "")`.
  A key of the wrong type now reads as an empty id, which resolves to nothing and draws the same
  "Interact with Spawn Egg" hint lines as a missing key did.

### Chat messages — DONE

- `Player#displayClientMessage(Component, boolean)` is gone from the tree - 20 live calls in 10
  files - and the two messages it could send are two methods now. The flag was the difference, not
  a detail of one call, so the split is: `false` (chat) is `Player#sendSystemMessage(Component)`,
  `true` (the action bar) is `Player#sendOverlayMessage(Component)`. Three of those are overlay
  messages and moved across unchanged otherwise: the backpack's "no room", and the sleeping bag's
  "bed occupied" and its `startSleepInBed` failure.
- [tools/port-261-chat-messages.pl](tools/port-261-chat-messages.pl) does the rewrite, reading the
  flag rather than assuming it: the message is matched with balanced parentheses and the flag is
  whatever follows the last top-level comma. Three calls in `KeyItem` are commented out and were
  left alone, which is the one thing the script checks for by line.
- The calls' wording, colour and trigger are unchanged, so everything the player reads is exactly
  as it was; only the method that carries it moved.

### Sleeping bag — DONE

- The failure message is back. `Player.BedSleepingProblem` is a record wrapping a `Component` in
  26.1 rather than a class with `getMessage()`, so the accessor is `message()` - the same shape
  vanilla's own `BedBlock#useWithoutItem` uses when it reports why a player could not lie down.
  Nothing else about the branch moved: a refused sleep still shows the reason above the hotbar
  rather than in chat, which is where that text belongs.
- `Player#startSleepInBed` keeps its `Either<BedSleepingProblem, Unit>` return, and the base
  `Player` version is now a stub that starts sleeping and reports success; the checks that can fail
  - too far away, obstructed, a bed rule that forbids sleeping - live in `ServerPlayer`'s override,
  which the call still reaches because the instance is a server player.
- The bed-explosion check is on `BED_RULE` now, so this one was a behaviour question and not a
  rename. `BedBlock#canSetSpawn(Level)` read the dimension type's `bedWorks` flag; 26.1 replaced
  that flag with the `gameplay/bed_rule` environment attribute, and vanilla's own bed explodes on
  `bedRule.explodes()` rather than on a spawn-point check. The bag does the same:
  `level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos)`, then the same
  remove-both-halves-and-explode it always did.
- The rule can also carry an error message, which vanilla shows above the hotbar just before the
  blast; that line is here too. It is empty for the nether and the end, so a vanilla world sees no
  new text - it only appears in a datapack that sets `error_message` on a rule that explodes.
- For the vanilla dimensions the two ways of asking agree: the nether and the end both set
  `explodes: true`, the overworld sets `can_set_spawn: always`. A custom dimension that only clears
  `bedWorks` without giving a `bed_rule` now gets the default rule, which does not explode -
  exactly what a vanilla bed does there since 26.1, so the bag and the bed still behave alike.
- The rest of the block is ported as well, so `SleepingBagBlock` compiles. `Villager` is under
  `npc.villager` now, beside the other villager types. `fallOn`'s distance is a `double`, and the
  bag still softens the drop by the same three quarters - the number is the only thing that
  changed type. `Level#blockUpdated` is `updateNeighborsAt`, which is what vanilla's own
  `BedBlock#setPlacedBy` calls after writing the head half.
- One deprecation is deliberate: the `getSeed` override still calls `Mth.getSeed(int, int, int)`.
  26.1 deprecated both `Mth.getSeed` overloads and named no replacement, and vanilla's `BedBlock`
  seeds a bed the same way, so the only alternative is reimplementing vanilla's hash by hand for a
  decorative seed.

### Keyed chests and the keystone — DONE

- `Level#playSound(Entity, BlockPos, SoundEvent, SoundSource, float, float)` takes the event itself
  now, not a `Holder`, and `SoundEvents` is split: most of its fields are plain `SoundEvent`s, but
  `ITEM_BREAK` is still a `Holder.Reference<SoundEvent>` because `DataComponents.BREAK_SOUND` holds
  it that way. The four keyed chests and the keystone play exactly that event when a wrong key is
  turned, so their five calls unwrap it - `SoundEvents.ITEM_BREAK.value()` - rather than being
  rewritten to a different sound. Everything else they pass (`LEVER_CLICK`, the button and pressure
  plate clicks) is already a `SoundEvent` and needed no change.
- The `(Player) null` first argument still compiles against the `@Nullable Entity` parameter. It is
  now redundant rather than wrong, and was left alone so the diff is the one thing that had to move.

### Milestone — inventory access (DONE)

- `Inventory.items` is private now, and `getNonEquipmentItems()` is the accessor: it returns that
  same `NonNullList` (main inventory, no armour or offhand), not a copy, which is what both of the
  milestone's payment loops need - the first counts matching stacks, the second shrinks them, so
  they must see the live stacks rather than a snapshot.
- `Inventory#clearOrCountMatchingItems(predicate, amount, craftSlots)` would replace both loops
  with one call, but it also draws from the crafting grid and the carried stack. That is a nicer
  rule and a different one, so it is noted here rather than taken: the payment still comes from the
  inventory alone.

### Milestone saved data — DONE

- 26.1 removed `SavedData.Factory` and the `save`/`load` overrides with it: `SavedData` is a bare
  class with the dirty flag, and a subclass registers itself as a `SavedDataType` (id, factory,
  `Codec`). `SavedDataStorage` encodes that codec under the file's `data` key and parses the same
  key back, so nothing hand-rolls a `CompoundTag` any more.
- `MilestoneHandler` is a `SavedDataType` now - id `tolkienmobs:milestone_data`, `MilestoneHandler::new`
  and a codec that is a `listOf` the entries - and `getInstance` is
  `level.getDataStorage().computeIfAbsent(TYPE)`. The class keeps its static map, so the codec's
  `xmap` fills that map on decode and reads it on encode; the handler instance itself still carries
  no state.
- The entry codec keeps the old keys and their representations, so the file still reads like a
  hand-written one: `milestonePos` as the packed long, `milestoneUUID` as the int-array UUID,
  `milestoneWorld` and `players` as strings, and the two costs as ints. The name and costs are
  optional, and a missing name reads back as an empty string - which is what the old
  `CompoundTag#getString` did too.
- **The file moves.** A `SavedDataType`'s id resolves against the data folder by namespace
  (`Identifier#resolveAgainst` is `root/namespace/path`), so the world keeps this in
  `data/tolkienmobs/milestone_data.dat`, not the old `data/milestone_data.dat`. A world carried
  forward from 4.4.83 therefore starts with an empty milestone list; the old file is left where it
  is, untouched.
- Verified with a scratch NbtOps harness (not committed, since the mod cannot load a modded world
  yet) holding a copy of the codecs: a full entry round-tripped with its position, uuid, dimension,
  name, costs and player list intact; an entry with only the required keys loaded with an empty name
  and zero costs; an empty compound was rejected as a parse error rather than throwing. The encoded
  payload came out as `{data: [{milestoneCost:40, milestoneDimension:200, milestonePos:<long>,
  milestoneTitle:"Bag End", milestoneUUID:[I;...], milestoneWorld:"minecraft:overworld",
  players:[...]}]}`, which is the shape the old file had.

### Milestone screen — DONE

- The first screen through the prepare/render split, and the shape the other seventeen need.
  There is no `GuiGraphics` and no `render` to override: a screen's drawing is an extract pass that
  fills a render state, so `renderBg`, `renderLabels`, `renderTooltip` and the custom tail of
  `render` become `extractBackground`, `extractLabels`, `extractTooltip` and `extractRenderState`,
  each taking a `GuiGraphicsExtractor`. The pass order is fixed by `Screen`:
  `extractBackground` -> `extractRenderState` (which is where the renderables are drawn) ->
  deferred tooltips, so the background hook is where the GUI texture is blitted now.
- The drawing calls moved with it: `drawString` is `text`, `drawCenteredString` is `centeredText`,
  `renderFakeItem` is `fakeItem`, and `blit` takes a `RenderPipeline` instead of a `RenderType`
  supplier - `RenderPipelines.GUI_TEXTURED` for a plain GUI texture. `RenderSystem.setShaderTexture`
  is gone because the pipeline carries the texture state.
- `imageWidth` and `imageHeight` are final, so a screen that wants a taller GUI passes both to
  `AbstractContainerScreen`'s four-argument constructor. The milestone GUI is 176x196.
- Input arrives as events: `mouseClicked(MouseButtonEvent, boolean)` and `keyPressed(KeyEvent)`,
  with `x()`, `y()`, `button()`, `key()` and `isEscape()` on them. `Screen.hasShiftDown` and
  `hasControlDown` are gone - the modifiers ride on the event that caused the click, which is why
  `adjustNumberButton` takes the event now. `Button#onPress` takes that same event
  (`InputWithModifiers`), and `AbstractWidget#onClick` is `(MouseButtonEvent, boolean)`.
- A payload from the client goes through `ClientPacketDistributor.sendToServer`; the server-facing
  `PacketDistributor` kept only its send-to-players methods.
- Two things were dropped rather than translated: the name field is a renderable widget, and the
  screen drew it by hand in both `render` and `renderBg` on top of the widget pass, so it was drawn
  twice; and the page counter moved from the absolute-coordinate tail of `render` into
  `extractLabels`, whose pose already has the GUI origin applied, so it only needed its old offset
  inside the GUI.
- The widgets themselves were still on the old API at this point and were the rest of the screens
  job; this screen only had to call the methods that survived. They are ported now — see below.

### Button widgets — DONE

- `TolkienButton` and its three subclasses - `NumberButton`, `ToggleButton` and `GrayscaleButton` -
  are ported. A widget is drawn by an extract pass too, but through a different hook than a screen:
  `renderWidget(GuiGraphics, ...)` is `extractContents(GuiGraphicsExtractor, mouseX, mouseY, a)`.
  `Renderable#extractRenderState` is `final` on `AbstractWidget`, and it wraps the subclass's
  `extractContents` in the hover test, the tooltip refresh and the cursor request, so a widget only
  draws its own body.
- A blit needs a `RenderPipeline` (`RenderPipelines.GUI_TEXTURED`); `drawCenteredString` is
  `centeredText`; `fill` is unchanged. `GuiGraphics#pose()` is an `org.joml.Matrix3x2fStack` now, so
  the `NumberButton`'s 0.75 scale is `pushMatrix()` / `scale(x, y)` / `popMatrix()` instead of a
  `PoseStack`, and text lands on integer pixels (`text(font, str, int, int, color, dropShadow)`),
  which is why its two float coordinates are rounded at the call.
- `ToggleButton` carried a mod-only three-argument `onClick(double mouseX, double mouseY, int button)`
  that cycled the texture and is what a right click in a screen calls by hand, because vanilla only
  dispatches left clicks to a widget. That method is `onClick(MouseButtonEvent event)` now; the
  button behaviour is unchanged - button 1 steps back, anything else steps forward, then the press
  callback runs. Its two other overrides (`onClick(double, double)` and
  `mouseClicked(double, double, int)`) only called `super`, and the methods they overrode no longer
  exist, so they are gone rather than translated.
- Four screens still called the old three-argument form - `BackpackBlockScreen`,
  `CamoKeyStoneScreen`, `CamoSpawnerScreen` and `KeyCodeScreen` - which briefly showed as errors
  where they used to resolve. They sat inside each screen's own `mouseClicked(double, double, int)`,
  which had to be rewritten anyway; `CamoKeyStoneScreen` and `KeyCodeScreen` are ported now and use
  `toggleButton.onClick(event)`, and the other two keep the error until their own pass.
- `IconButton` and `TexturedButton` are `Button` subclasses rather than `TolkienButton` ones and
  are ported too, which closes the widget set. Neither has a super-only override left, and neither
  is constructed anywhere in the tree yet, so no call site moved with them.
- `TexturedButton` used to override `render(GuiGraphics, ...)` rather than `renderWidget`, and never
  called `super`, so `AbstractWidget` never set `isHovered` and its `yDiffTex` hover offset was dead
  code - the button only ever drew its idle frame. On the extract pass it overrides
  `extractContents`, which the base class calls *after* setting `isHovered`, so the hovered frame
  shows for the first time. That is a deliberate behaviour change, not a translation slip.

### Key screen and keystone screen — DONE

- Both are the milestone shape exactly: `extractBackground` blits the GUI, `extractLabels` draws the
  title and the greyed key code, `extractTooltip` keeps the button localisations and the name field's
  one-line hint, and `render` / `renderBg` / `renderLabels` / `renderTooltip` are gone. The GUI sizes
  are 176x62 and 176x115, passed to the four-argument constructor because `imageHeight` is final.
- Both drew the name field by hand in `render` and again in `renderBg` on top of the widget pass, so
  it was drawn twice over itself; it is only a widget now. That is the same tidy the milestone
  screen got.
- `mouseClicked(double, double, int)` is `mouseClicked(MouseButtonEvent, boolean)`, which fixed the
  two `toggleButton.onClick(x, y, btn)` calls the widget port left behind - they are
  `toggleButton.onClick(event)` now, and the number buttons take the event through
  `onPress(event)` with the shift/control steps read from it instead of `Screen.hasShiftDown()`.
- `keyPressed(int, int, int)` is `keyPressed(KeyEvent)`; the key field keeps the same rule - it
  swallows keys while focused except escape - and the GLFW key constant is `event.isEscape()`.
- Payloads go through `ClientPacketDistributor`, and `RenderSystem.setShaderTexture` is gone with the
  pipeline-bound blit.

### Key and keystone menus — DONE

- Both containers are ported with them. 26.1 replaced `ClickType` with `ContainerInput`
  (`PICKUP`, `QUICK_MOVE`, `SWAP`, `CLONE`, `THROW`, `QUICK_CRAFT`, `PICKUP_ALL`) and the hook is
  `clicked(int slotIndex, int buttonNum, ContainerInput containerInput, Player player)`. Neither
  container's override did anything but call `super` with the same four arguments, so both overrides
  and their imports are gone rather than re-signed - the inherited method is the one they were
  forwarding to. The other eleven containers are done too: seven of them were the same empty
  forward and lost the override and import, and four had a real body - the backpack's locked-slot
  guard (`ClickType.SWAP` is `ContainerInput.SWAP`), and the coin pouch, key item and key ring's
  "this stack does not stack" guards, which only throw the click away early and keep the enum's
  name out of their logic - so those four keep their override with the new parameter type. No
  `ClickType` is left anywhere in the tree.

### Backpack menu — DONE

- The held backpack locks the slot it is carried in, which is the hotbar slot the player has
  selected (`Inventory#selected` is private now; `getSelectedSlot()` reads it) or the offhand.
- `Item#onCraftedBy` lost its `Level` argument: it is `onCraftedBy(ItemStack, Player)` and reads
  `player.level()` itself. The call is still inside the container-level access, so it fires under
  exactly the conditions it did before, and the crafting result's "was this crafted by a player"
  bookkeeping is unchanged.
- `Recipe#assemble` lost its `RegistryAccess`: it is `assemble(T input)` now, so the crafting
  result is `assemble(container.asCraftInput())`. The rest of the grid update - `getRecipeFor`,
  `setRecipeUsed`, `isItemEnabled` and the three result-slot writes - is unchanged.

### Backpack screen — DONE

- The usual four hooks: `extractBackground` (the nine upgrade-variant panels, the tank overlay and
  the fluid), `extractLabels` (the three captions), `extractTooltip` (the button localisations and
  the tank), and no `render` override at all. The 256x256 size is a constructor argument now, and
  the upgrade panel's texture is a constant instead of an `Identifier` built every frame.
- `hasClickedOutside` lost its `mouseButton` argument: it is
  `(mouseX, mouseY, guiLeft, guiTop)` and the click event keeps the button. The screen's five-pixel
  wider-than-the-GUI test for the upgrade panel is otherwise the same.
- `renderSlotContents` was overridden only to call `super`, so it is gone rather than re-signed.
- The right-click on a toggle is `toggleButton.onClick(event)` again, and the three payloads go
  through `ClientPacketDistributor`.
- **`FluidTankRenderer` had to be ported with it**, because the tank draws through it and it was on
  `GuiGraphics` too. Its draw is `GuiGraphicsExtractor` plus a `Matrix3x2fStack` for the translate;
  the blend switches and `RenderSystem.setShaderColor` are gone - the GUI pipeline blends already and
  the tint rides on each blit's colour argument.
- Its sprite and tint are the bigger change: 26.1 replaced the per-draw
  `IClientFluidTypeExtensions#getStillTexture`/`getTintColor` calls with a baked `FluidModel` per
  fluid state. The still sprite is `getModelManager().getFluidStateModelSet().get(state)` ->
  `stillMaterial().sprite()`, already baked against the block atlas, so the old atlas lookup is
  gone; the tint is the model's `FluidTintSource#colorAsStack`, or white when the fluid declares no
  tint. The masked-UV tiling maths is unchanged.
- The tank's tooltip is requested at the cursor. The old code passed `mouseX - guiLeft`,
  `mouseY - guiTop` to a call that wanted absolute coordinates, so the line sat up and left of the
  pointer by the GUI's own offset; `setTooltipForNextFrame` takes the cursor position like every
  other tooltip does now.

### Camo spawner screen — DONE

- The same four hooks, with the 256x206 GUI as a constructor argument. `renderLabels` was four
  captions and is `extractLabels` with `text`; `renderTooltip` is `extractTooltip`; `renderBg` is
  `extractBackground`, which still blits the panel and then draws the chosen mob.
- **The mob preview is vanilla's again.** The screen carried a copy of 1.21's three-method
  `renderEntityInInventory*` chain - `Lighting.setupForEntityInInventory`, an
  `EntityRenderDispatcher#render` inside `drawSpecial`, a hand-rolled `PoseStack` - and none of that
  exists on the extract pass. 26.1 rebuilt the same feature around render states and still exposes
  it as `InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, x0, y0, x1, y1, size,
  offsetY, mouseX, mouseY, entity)`, which is what the inventory screen itself calls, so the copy
  is deleted and that call replaces it. The preview keeps its odd 403-wide box (the scissor clips it
  to the GUI) so it faces the cursor the way it did.
- The number buttons take `onPress(event)` with the shift/control steps read from the event, the
  right-click on a toggle is `toggleButton.onClick(event)`, and every payload goes through
  `ClientPacketDistributor`.

### Fireplace and trinket table screens — DONE

- Both are the simplest screens in the mod: a panel, two captions and a two-part progress display
  (an arrow that fills and a fire or trinket sprite that rises from the bottom). They need nothing
  but the four hooks - `extractLabels`, `extractBackground` and, for the fireplace, `extractTooltip`
  for the "remaining fuel" line.
- The progress blits keep their exact offsets and their clipped `u`/`v`, so the arrow still grows
  left to right and the flame still rises into the frame rather than being drawn whole and masked.
  Each blit is `RenderPipelines.GUI_TEXTURED` now, with the int `u`/`v` widening to the new float
  parameters.
- Both had a `render` override that called `renderBackground` itself; there is no such call in 26.1,
  and the base class already draws background, contents and tooltip in that order, so the overrides
  are deleted rather than translated. The `renderSlotContents` overrides were super-only and went
  the same way.
- Neither uses `FluidTankRenderer` after all - the tank work earlier was needed by the backpack, not
  by these two - so no call site moved with that change.

### Lockable chest screens — DONE

- All four - chest, double chest, treasure chest, double treasure chest - are the key screen's shape
  with a different payload: a 176x62 panel, one name field, one save button and a title. They are
  otherwise identical, and now byte-identical apart from their container type, payload class and
  class name, so a fix to one applies to all four.
- Each drew the name field by hand in `render` and again in `renderBg` on top of the widget pass; it
  is only a widget now, and `extractBackground` does nothing but blit the panel.
- `mouseClicked(MouseButtonEvent, boolean)` and `keyPressed(KeyEvent)` replace the integer-event
  forms, and the save payload goes through `ClientPacketDistributor`.
- None of the four had a tooltip of its own beyond the name field's hint, which stays on the field
  and is refreshed in `extractTooltip`.

### Piggy bank and the three item screens — DONE

- The piggy bank (176x221) and the coin pouch, key item and key ring (all 176x181, sharing
  `inventory_small.png`) are the plainest screens left: a panel and a title. Their heights are
  constructor arguments, `renderBg`/`renderLabels` are `extractBackground`/`extractLabels`, and their
  `render` overrides only re-drew the background and tooltip the base class already draws, so they
  are gone.
- The piggy bank's two captions sit at `-15`, left of the GUI's own origin, and still do - the label
  pass has the GUI origin applied and negative offsets are relative to it the same way they were.
- The three item screens swallow a right click over one of their own slots, which is what stops a
  stack in a pouch or ring from being split or picked up. That is `event.button() == 1` on
  `mouseClicked(MouseButtonEvent, boolean)` now, still returning `true` before the base class sees
  it. Their dead `int slot = hoveredSlot.getSlotIndex();` line - assigned and never read - went with
  the rewrite.

### Camouflage chest and fluid screens — DONE

- The chest is a panel and nothing else - it never overrode its labels, so the base class's title and
  inventory line are what it shows - with both label offsets moved up for its texture. Its height
  goes through the constructor and its draw is `extractBackground`; it still calls `super` first so
  the screen dim is drawn under the panel.
- The fluid screen adds a caption pair and one tooltip: hovering the bucket slot explains the tank
  in aqua. That is `setTooltipForNextFrame(font, component, x, y)`, which already took the cursor
  position, so the line lands where it did.
- Both had super-only overrides - the fluid screen's `mouseClicked`, `containerTick` and `render` - so
  they are deleted rather than re-signed. `RenderSystem.setShaderTexture` went with the pipeline
  blit.

### Backpack upgrade screen — DONE, and the screen set with it

- The last screen, and the smallest: an 86x86 panel, one caption at `-15` and no widgets of its own.
  Size to the constructor, `renderBg` to `extractBackground` (still calling `super` for the dim),
  `renderLabels` to `extractLabels`, and the super-only `renderSlotContents` and `render` overrides
  deleted.
- With it, **no file under `containers/screens` or `containers/widget` reports an error** - eighteen
  screens and eight widgets, all on the extract pass. What the tree still fails on is elsewhere.

### Non-GeckoLib entity renderers — DONE

- 26.1 replaced a renderer's `render(state, PoseStack, MultiBufferSource, packedLight)` with
  `submit(state, PoseStack, SubmitNodeCollector, CameraRenderState)`: a renderer no longer writes
  vertices, it hands the collector the model, pose, texture, light, overlay and outline colour, and
  the collector draws them in its own pass. A model is
  `submitModel(model, state, poseStack, texture, state.lightCoords, OverlayTexture.NO_OVERLAY,
  state.outlineColor, null)`; a single part, which is what the morgul crystal needs for its three
  separately transformed pieces, is `submitModelPart(part, poseStack, renderType, light, overlay,
  sprite)`. Both end with `super.submit(...)` so the name tag and leashes still go in.
- `RenderType` moved to `net.minecraft.client.renderer.rendertype`, and its factories live in
  `RenderTypes`. The old `entityCutoutNoCull` is now plain **`entityCutout`** - the unculled pipeline
  is the default and `entityCutoutCull` is the culled one - which matters for the crystal, whose
  faces are drawn from both sides.
- The seven boat renderers were the easiest: `BoatModel` moved to `client.model.object.boat`, and
  `AbstractBoatRenderer` now takes the texture in its constructor and does the drawing itself, so a
  boat renderer supplies only `model()`. The `renderType()` override has no replacement and is gone;
  the seven differ only in their wood name and texture path.
- `AbstractArrow` moved to `world.entity.projectile.arrow`, which was what kept the boulder, the
  fell beast fireball and the Tolkien arrow from being valid `EntityRenderer` type arguments at all -
  three one-line imports cleared most of this batch's errors.
- `getTextureLocation` on the three projectile-ish renderers was never an override and never called;
  the texture now rides on the submit call, so those methods are deleted.
- Left alone at the time: `block/custom/entity/renderer/*` (block entities) and `handler/render`
  (the vertex pipeline); both are ported further down.

### Block-entity renderers — DONE

- `BlockEntityRenderer<T>` is `BlockEntityRenderer<T, S extends BlockEntityRenderState>`: a renderer
  declares `createRenderState()`, fills that state in `extractRenderState(blockEntity, state,
  partialTicks, cameraPosition, breakProgress)` and draws in `submit(state, poseStack,
  submitNodeCollector, camera)`. The seven that draw something each get a small state class under
  `block/custom/entity/renderer/state/`.
- Items go through the provider's `ItemModelResolver`: `updateForTopItem(itemState, stack,
  displayContext, level, null, seed)` while extracting, `itemState.submit(poseStack, collector,
  state.lightCoords, OverlayTexture.NO_OVERLAY, 0)` while submitting. `state.lightCoords` is what
  `extractBase` already computes, so the trinket table and the fireplace no longer pack block and
  sky light themselves.
- `BlockEntityRenderState.blockState` is private with no getter, so a state keeps its own `facing`,
  read from `blockEntity.getBlockState()` in extraction.
- Text is `submitNodeCollector.submitText(poseStack, x, y, charSequence, dropShadow,
  Font.DisplayMode, light, color, backgroundColor, outlineColor)`; the milestone's see-through pass
  and its shadowed front pass are two submits over one pose, and the camera-facing rotation is
  `camera.orientation`. `RenderSystem.setShaderColor`/`enableBlend` are gone - the collector owns
  that state.
- A model's parts are `submitModelPart(part, poseStack, renderType, light, overlay, sprite,
  tintedColor, crumbling)`; the lightning bug's body and its additive glow are two batches over the
  same parts. `RenderType` is the moved `client.renderer.rendertype` one, so the bug's body is
  `RenderTypes.entityCutoutCull` - old `entityCutout` was the culled pipeline - and the glow stays
  `entityTranslucent`.
- A vanilla `Model` subclass that only ever draws parts extends **`Model.Simple`**. Extending raw
  `Model` makes the inherited `Function<Identifier, RenderType>` constructor parameter raw, whose
  single argument is `Object`, so `RenderTypes::entityCutout` cannot bind to it.
- The spawner's preview mob cannot reuse vanilla's `TrialSpawnerRenderer.extractSpawnerData` (it is
  package-private), so it repeats it: `entityRenderer.extractEntity(entity, partialTicks)`, copy the
  state's light onto the entity state, and spin and scale from `getoSpin`/`getSpin`. It draws with
  `entityRenderer.submit(displayEntity, camera, 0, 0, 0, poseStack, collector)`.
- The placard drew nothing before the port and draws nothing now; it keeps its registration with a
  plain `BlockEntityRenderState`.

### The `handler/render` vertex pipeline — DONE

- `net.minecraft.client.renderer.RenderType` is `client.renderer.rendertype.RenderType`. `format()`
  survived the move, so `bind(RenderType, MultiBufferSource)` still learns the vertex format from
  the type it is handed.
- `BlockAndTintGetter` is a client interface now - `net.minecraft.client.renderer.block` - and
  `LevelRenderer.getLightColor` is `getLightCoords(level, pos)`. `CCRenderState` and `LightMatrix`
  both follow that.
- `VertexFormatElement` is a record of `(id, index, type, normalized, count)` and has no `usage()`.
  The elements are the named constants `POSITION`, `COLOR`, `UV0`, `UV1`, `UV2`, `NORMAL` and
  `LINE_WIDTH`, so `CCRenderState.writeVert` and `CachedFormat` now compare the element to those
  instead of switching on a usage enum. `UV1` is the overlay and `UV2` the lightmap, which is the
  same split the old `index()` cases made, and an element that is none of them is still an
  unsupported format.
- `VertexConsumer` gained `setLineWidth(float)`, and `DelegatingVertexConsumer` forwards it and the
  single-argument `setColor(int)`. Without those two, `TransformingVertexConsumer` was left with
  abstract methods and did not compile.
- `Minecraft.useAmbientOcclusion()` is `Minecraft.getInstance().options.ambientOcclusion().get()`.
- 26.1 has no per-stack fluid tint. `IClientFluidTypeExtensions` no longer carries
  `getTintColor(FluidStack)` - a fluid's tint is declared on its baked
  `FluidModel#fluidTintSource` and read through a `FluidTintSource` - so
  `CCRenderState.setFluidColour(FluidStack[, int])` is gone. Nothing in the mod called it.
- `handler/vec` needed two accessor fixes to compile underneath this: 26.1 returns `Optional` from
  `CompoundTag#getDouble` and `#getCompound`, so `Vector3.fromNBT` reads `getDoubleOr(name, 0)` and
  `Cuboid6` reads `getCompoundOrEmpty`. Missing keys read as 0 and as an empty compound, the same
  values the old calls produced.

### `@EventBusSubscriber` - DONE

- 26.1's `EventBusSubscriber` has only `modid` and `value`: the `bus` member and its `Bus` enum are
  gone, so the annotation cannot say which bus a class listens on. `AutomaticEventSubscriber.inject`
  picks it per method instead - a `@SubscribeEvent` method whose event is an `IModBusEvent` is
  registered on `ModContainer.getEventBus()`, everything else on `IBindingsProvider.getGameBus()`.
- The port is therefore deleting the member: 14 files carried `bus = EventBusSubscriber.Bus.MOD` or
  `.GAME`, and each now reads `@EventBusSubscriber(modid = MODID)` or with `value = Dist.CLIENT`.
  `value` still defaults to `{CLIENT, DEDICATED_SERVER}`, so the sides each class loads on are
  unchanged; only the bus choice moved from the annotation to the event type.

### Particles - DONE

- `TextureSheetParticle` is gone. `SingleQuadParticle` is the sprite-sheet base now, it takes a
  `TextureAtlasSprite` in every constructor, and a subclass answers
  `getLayer(): SingleQuadParticle.Layer` instead of `getRenderType()`. The layers are `OPAQUE` and
  `TRANSLUCENT` on the particle atlas plus the terrain and item variants.
- `ParticleRenderType` is a record with only `SINGLE_QUADS`, `ITEM_PICKUP`, `ELDER_GUARDIANS` and
  `NO_RENDER`: the sheet types were folded into `Layer`, and the group comes from `getGroup()`, which
  `SingleQuadParticle` answers with `SINGLE_QUADS` for every sprite particle.
- Because the sprite is fixed at construction, `pickSprite` is gone and a particle no longer chooses
  its frame in `tick()`. `setSpriteFromAge(SpriteSet)` survives on `SingleQuadParticle`, so the two
  that step through frames as they age - the flames and the fell beast's breath - keep their
  `SpriteSet` field and call it exactly as before.
- `ParticleProvider#createParticle` takes a ninth `RandomSource` and may now return null, so all
  three providers grew that argument. `SpriteSet` offers `get(index, max)`, `get(RandomSource)` and
  `first()`, and the `RandomSource` the provider is handed is the one to pass to `get`.
- The six leaf registrations were eight-argument lambdas in `TolkienMobsMain`; a leaf takes its
  sprite now, so they share one `leafParticle(SpriteSet)` provider that passes
  `sprites.get(random)`. That also changes which frame shows: the old code mapped twelve invented
  indices onto the real frames, so sprites past the twelfth never appeared, while `get(random)`
  picks uniformly from the set.
- `net.minecraft.MethodsReturnNonnullByDefault` no longer exists - vanilla annotates with jspecify
  now - so it is off `TolkienParticleProvider`; `@ParametersAreNonnullByDefault` is still there.
  `DwarvenMinerEnchantmentEffect` carries the same annotation and still needs it removed, along with
  its own `MobEffects` change.

### The Watcher and its goal - DONE

- `WaterAnimal` moved to `world.entity.animal.fish`, and that one import was the whole of the
  Watcher's 79 errors: with the supertype unresolved, every inherited member read as a missing
  symbol - `moveControl`, `goalSelector`, `entityData`, `level()`, `getHealth()`, `canAttack`, the
  lot - and the 11 errors in `WatcherAttackGoal` were the same line by proxy.
- `MobEffects.MOVEMENT_SPEED` is `MobEffects.SPEED`. The effect itself is unchanged, so the speed
  buff the Watcher hands itself after a hit is still the same two-second amplifier-0 instance.
- `LivingEntity.lastHurtByPlayer` is an `EntityReference<Player>`, so the counterattack reads
  `getLastHurtByPlayer()` and returns early when it is null - which covers both a cleared reference
  and a remembered player who has since left - rather than asserting on the field.
- `ServerBossEvent` takes a `UUID` first. The Watcher names its bar with
  `Mth.createInsecureUUID(this.random)`, which is how vanilla's wither builds its own.

### The boss family - DONE

- The other ten bars now name themselves the same way, `Mth.createInsecureUUID(this.random)`:
  the balrog, fell beast, goblin king, Gwaihir, both golems, Shelob, the witch king, the murder frog
  and the killer squirrel. That one argument was ten errors each time and ten files' worth of them
  here; the colours, overlays and names are untouched.
- Being a `UUID` was only the first thing the family needed. Behind it sat the same package moves
  as everywhere else: `IronGolem` under `animal.golem`, `Turtle` under `animal.turtle`, `Chicken`,
  `Cow` and `Sheep` under their own animal packages, `AbstractVillager` under `npc.villager`,
  `Spider` under `monster.spider`, `Snowball` under `projectile.throwableitemprojectile`,
  `AbstractArrow` under `projectile.arrow` (which is also where `AbstractArrow.Pickup` lives now,
  so the fell beast's fireball still sets `DISALLOWED`), and `GameRules` under
  `world.level.gamerules`.
- `GameRules` is not just a move: the booleans became `GameRule<Boolean>` keys, so the goblin king's
  and Shelob's reinforcement check reads `getGameRules().get(GameRules.SPAWN_MOBS)` - the rule
  `doMobSpawning` was renamed to - rather than `getBoolean`.
- `MobEffects.MOVEMENT_SPEED` is `MobEffects.SPEED`, and `MobEffects.HARM` is
  `INSTANT_DAMAGE`; both are the same effects under their current names, so the two-second speed
  buff after a hit and the fireball's breath cloud are unchanged. `AreaEffectCloud#setParticle` is
  `setCustomParticle` now.
- `PathType.DANGER_FIRE` and `DAMAGE_FIRE` are `FIRE_IN_NEIGHBOR` and `FIRE`, which is what
  `Animal` itself sets: the same two maluses at the same values.
- `Level#getCurrentDifficultyAt` is a `ServerLevel` method, so the reinforcement spawns ask the
  server they are already holding. `Entity#causeFallDamage` takes a `double` fall distance.
- **The dragon** needed four renames of its own. `moveTo(x, y, z, yRot, xRot)` is `snapTo`, `isDay`
  is `isBrightOutside` - the same "not night and not a fixed-time dimension" answer -,
  `isControlledByLocalInstance` is `isLocalInstanceAuthoritative`, and `isInWaterOrBubble` is gone
  in favour of `isInWater`, which is the same `wasTouchingWater` flag.
- The home restriction moved and changed name with it: `hasRestriction` is `hasHome`,
  `getRestrictCenter` is `getHomePosition`, `getRestrictRadius` is `getHomeRadius` (an `int` now,
  where the dragon still answers with its config radius), `restrictTo` is `setHomeTo`, and both
  `isWithinRestriction` overloads are `isWithinHome`. The dragon keeps storing its own home in
  entity data, so `MoveToHomeGoal`, `DefendHomeGoal` and `FlyerWanderGoal` only needed the new
  names and a homing dragon still returns to the place it was spawned.
- **`NeutralMob`** now remembers when the anger ends instead of counting it down. The two golems
  store a `persistentAngerEndTime` and implement `getPersistentAngerEndTime` /
  `setPersistentAngerEndTime`, and `startPersistentAngerTimer` calls the interface's
  `setTimeToRemainAngry`, so the twenty-to-forty-second window is the same window seen from  the other end. The target it is angry at is an `EntityReference<LivingEntity>` rather than a `UUID`.
  The stored pair is the interface's own now, so a golem loaded from a pre-26.1 save comes back
  calm rather than mid-grudge - the only visible cost of the change.
- NeoForge's `BlockEvent.BreakEvent` is a class of its own now,
  `net.neoforged.neoforge.event.level.block.BreakBlockEvent`, so both balrog event classes take that
  instead. Nothing about the magma-block guard changes - it still cancels the break.
- **Verified:** whole-tree `javac` (JDK 25, 859 sources) at **1,179** errors, down 86 from the
  1,265 the tree stood at, with a per-file diff showing only removals: the ten bars' constructor
  errors, and then the 76 errors the rest of the family carried. Every file under `entity/boss`,
  the dragon, both ambient bosses and their goals now compile clean. Nothing was opened in game.

### The other `NeutralMob` users - DONE

- The elemental golem and the tree ent are the same shape as the two boss golems, so they took the
  same conversion: `remainingPersistentAngerTime` became a `persistentAngerEndTime` long, the two
  interface methods are `getPersistentAngerEndTime` / `setPersistentAngerEndTime`, the target is an
  `EntityReference<LivingEntity>`, and `startPersistentAngerTimer` calls `setTimeToRemainAngry`. The
  tree ent still refuses to be made angry the same way, and both still keep the 20-39 second window.
- The Istari carried an extra piece: it synced its countdown as an int (`DATA_REMAINING_ANGER_TIME`)
  and nothing on the client ever read it. The end time is server state now, so that accessor and its
  `define` entry are gone and the value is a plain long. One fewer synced int per Istari, and anger
  behaves exactly as before.
- The Istari also called `forceAnimationReset()` twice, in its attack and heal branches. 5 dropped
  the method - and the branch already guards on `!isAnimatingBones()`, so the controller is stopped
  by the time `setAnimation` runs and starts the clip either way. The swing and the heal still play
  once and hand the controller back. `SwampHagEntity` and `GollumEntity` make the same call and are
  still to port.
- The elemental golem had three more of its own: `MobEffects.MOVEMENT_SLOWDOWN` is `SLOWNESS` (the
  same effect, same id, so the slow it puts on whatever it hits is unchanged), its tornado placement
  is `snapTo(BlockPos, float, float)` rather than `moveTo(BlockPos, int, int)`, and its
  `causeFallDamage` takes a `double` fall distance - which it ignores anyway, since the golem never
  takes fall damage.
- **Verified:** whole-tree `javac` at **1,156** errors, 23 down from 1,179, with the per-file diff
  showing only the three files' errors removed and nothing new anywhere.

### The last two `forceAnimationReset` callers - DONE

- The swamp hag and Gollum were the only remaining callers, and neither needs a replacement. The
  hag's two branches already guard on `!isAnimatingBones()`, so the controller starts the attack or
  the heal from the top without being reset first. Gollum's swing branch does not guard at all: it
  sets the idle and returns `CONTINUE`, so the swing now simply holds the idle loop it was already
  on rather than restarting it - the same pose, since idle loops. No `forceAnimationReset` call is
  left in the source; the only mentions are the two comments explaining why.
- **The swamp hag's other errors** were its own port. `ThrownPotion` is split into
  `AbstractThrownPotion` and its two kinds now, and the hag throws a splash potion - it builds the
  stack with `Items.SPLASH_POTION` - so it constructs `ThrownSplashPotion`, which takes the same
  `(Level, LivingEntity, ItemStack)` arguments. `PotionContents#forEachEffect` takes a duration
  scale, so the drunken potion's effects apply at `1.0F`, which is the default it used before.
  `MOVEMENT_SPEED` and `MOVEMENT_SLOWDOWN` are `SPEED` and `SLOWNESS`, so the same four potion
  choices are picked on the same conditions.
- **Verified:** whole-tree `javac` at **1,147** errors, 9 down from 1,156 - the hag's eight and
  Gollum's one - with the per-file diff showing only those two files and nothing new anywhere.

### The monster family - DONE

- Three entity classes were left, and every one of them was a package move plus one call. The
  Mirkwood spider's `Skeleton` and `Spider` are under `monster.skeleton` and `monster.spider` now;
  the warg's `isInWaterRainOrBubble` is `isInWaterOrRain` (a bubble column is water, so the wet flag
  it sets behaves the same); and both of their `moveTo(x, y, z, yRot, xRot)` calls - the warg putting
  an orc on its back, the spider putting a skeleton jockey on its own - are `snapTo`.
- The spider's pickpocket reads `Inventory#getSelectedSlot()` because `selected` is private now. It
  still takes one item from the player's held stack and drops it with the same pickup delay.
- `armorDropChances[]` is a `DropChances` record on `Mob`, so the Romie walker's Halloween pumpkin
  uses `setDropChance(EquipmentSlot.HEAD, 0.0F)` - a helmet it puts on still never drops, which is
  what the array index said before.
- The base class, `TolkienMonsterEntity`, was five of the errors: the same two moved imports
  (`AbstractVillager` under `npc.villager`, `AbstractArrow` under `projectile.arrow`) that the boss
  family had. With those in, everything under `entity/monster` - entities, models and renderers -
  and the base compile clean.
- **Verified:** whole-tree `javac` at **1,131** errors, 16 down from 1,147, with the per-file diff
  showing only the four touched files' errors removed.

### The boat family - DONE

- `Boat`, `ChestBoat` and `AbstractBoat` all moved one package down, to
  `world.entity.vehicle.boat`. That single import was the whole of each of the fourteen entities' 17
  errors: with the supertype unresolved, `entityData`, `xo`/`yo`/`zo`, `setPos`, every `super.…`
  call and even the `EntityType<? extends X>` bound read as missing.
- The seven boat items (`item/custom/*BoatItem`) hold an `AbstractBoat` and import it from the same
  new package, so they took the same move.
- Clearing the entities also cleared far more than their own 238 errors: `TolkienEntities` went
  43 -> 1, `TolkienRegistration` 35 -> 21 and `TolkienMobsMain` 23 -> 9. A registry entry building an
  `EntityType` over an invalid boat type cannot check its own arguments, so one broken supertype was
  failing the registry around it.
- Nothing about the boats changed: the same wood type is saved and read back through the same
  `ValueOutput`/`ValueInput` keys, and a chest boat still carries its chest.
- **Verified:** whole-tree `javac` at **802** errors, 329 down from 1,131, with the per-file diff
  showing only removals - the fourteen entities, the seven items and the three registry files.

### The ambient birds and the gecko model - DONE

- All three birds - the thrush, the crebain and the great eagle - took `PathType.FIRE_IN_NEIGHBOR`
  and `PathType.FIRE` for the old `DANGER_FIRE`/`DAMAGE_FIRE` pair, at the same `-1` maluses, and a
  `double` fall distance on their `causeFallDamage`, which they override to return `false` anyway
  because they fly. The eagle also needed `Chicken`, `Cow` and `Sheep` by their own packages, which
  its `animal.*` import no longer covers.
- The thrush's breeding needed the most care: an owner is an `EntityReference<LivingEntity>` now, so
  the chick copies the parent's reference with `setOwnerReference(getOwnerReference())` rather than a
  UUID. That keeps a tamed thrush's chick owned by the same player even when that player is offline,
  which resolving the entity and setting it back would not.
- **The gecko model** needed the real change. 26.1 removed `Model#animate` and `#animateWalk`: a
  model bakes its `AnimationDefinition` against its own root once, `definition.bake(root)`, and then
  calls `applyWalk(animationPos, animationSpeed, speedFactor, scaleFactor)` or
  `apply(state, ageInTicks, speedFactor)`. The gecko bakes its walk and idle definitions in the
  constructor and applies them with the same numbers as before - a `2.0` walk speed factor and `2.5`
  scale, idle at `1.0` - so the lizard walks and breathes exactly as it did.
- **Verified:** whole-tree `javac` at **787** errors, 15 down from 802 - the birds' thirteen and the
  model's two - with the per-file diff showing only those four files and nothing new anywhere.

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
6. **Worldgen.** Weighted lists in `TolkienCaveCarvers` and `TolkienDecorators` (the spawner's is
   done, see *Camo spawner — DONE* below); then TerraBlender (`TolkienMobsMain` lines 181–182)
   and the surface rules.
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
