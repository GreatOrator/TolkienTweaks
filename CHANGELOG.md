# Tolkien Tweaks — Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Changed

- The build is ready for Minecraft 26.1.2. Building from source now needs Java 25 and Gradle 9.2.1, and the project compiles against NeoForge 26.1.2.116, GeckoLib 5.5.2, TerraBlender 26.1.2-26.1.2.0.3, Curios 15.0.0 and JEI 29.47.0.111. The source itself has not been ported yet, so this produces no runnable jar; it is noted because anyone building from source now needs the new toolchain.
- The rendering library is GeckoLib 5. Its package is `com.geckolib` now, and every geo model and animation has moved to `assets/tolkienmobs/geckolib/models` and `assets/tolkienmobs/geckolib/animations`, which is where 5 loads them from. The rock golem, huron, deep claw, romie walker, warg, elemental golem, mimic chest, tree ent, the six weapon-carrying orcs - brigand, goblin, duergar, haradrim, mordor orc and uruk hai - and the nine remaining weapon-carriers, the balrog, witch king, barrow wight, both fell spirits, minotaur, nazgul, oath breaker and troll, have been converted to 5's render-state API. Every held-item layer in the mod now builds its props from the render state.
- The thirty GeckoLib mobs that hold nothing are converted too. The batch is the seven bosses - the fell beast, goblin king, Gwaihir, the mithril and morgul-iron golems, Shelob and the Watcher - plus the swamp hag, the Mirkwood spider, the eight trading villagers, the six ambient animals and the seven passive mobs. Their variants, winter clothing, sizes and the props they only sometimes show all ride on the render state now, and their controllers are built the 5 way: the animatable is no longer passed to the controller, and the animation event hands out `controller()` and `animatable()` rather than getters.
- The three GeckoLib projectiles are converted as well - the thrown cobweb, the web trap and the tornado. They needed a render state of their own: 5 fills a living render state by casting the entity to a living one, and a projectile is not, so `TolkienProjectileRenderState` carries the same GeckoLib data on the plain entity state plus the life time the trap and the tornado scale themselves from. The trap's pop-in and the tornado's first frame are `scaleModelForRender` now, the blast's turn is `adjustRenderPose`, and the tornado's `getTick` override is gone, because 5 times animations from the tick it stamps on each render state itself.
- The five GeckoLib block entities are converted too - the well and the four lockable chests. Their renderers now take the provider's context at registration, which is the context 5 keeps for its block-model resolver, and they use 5's own `BlockEntityRenderState`: the models draw from constants, so there was nothing to carry over between extraction and drawing. Their controllers are built the 5 way as well, the well's idle and its triggered `fill` and each chest's triggered `box_open` alike.
- The Arda staff is converted, and it was the mod's only GeckoLib item renderer - the Morgul crystal and the boats use vanilla renderers. 5's `GeoItemRenderer` already draws through its own `GeoRenderState`, so the renderer itself needed nothing; the model's resource getters take the state now and the staff's idle controller is built the 5 way.
- The three NPC profession layers are converted too. Each is a subclass of 5's texture layer now, which is what a whole-model second pass with another texture is for, so the hand-written `reRender` call is gone. They are still attached to nothing - no renderer adds them, in this tree or in the 1.21 source they came from - so nothing visibly changes.
- The villager and wandering-trader bases are ported to 26.1, so the NPC family no longer fails on its own supertypes. Their imports follow the package moves (`Util` is under `net.minecraft.util`, `Villager` under `npc.villager`, the wandering trader under `npc.wanderingtrader`), their variant is saved through `ValueOutput`/`ValueInput` instead of `CompoundTag`, and the villager's never-read merchant-variant synced data is gone: 26.1 builds `VillagerData` from registry holders, and nothing in the mod looked at that copy. Their controllers behave as they did, including the idle check that a mob is not aggressive, and are built the 5 way described below.
- The six NPC subclasses are ported with them - dwarf, elf, hobbit, human, orc and southron - so the whole NPC family compiles. They needed the same two moved imports, and the human's winter flag saves through `ValueOutput`/`ValueInput` now and asks `isClientSide()` rather than reading the field. Nothing about how they breed, speak or dress has changed.
- The last three controller sets are converted - the dragon, the villager base and the wandering NPC - which finishes the GeckoLib 5 migration: no GeckoLib code in the mod is on the 4 API anymore. The dragon keeps all five of its controllers and their behaviour, so a fight still opens with its roar, its breath still runs wind-up, loop and tail as one sequence, and a swing made on the move still picks one of the walk attacks. Its `forceAnimationReset` calls are gone because 5 has no such call - a controller that stopped already cleared its animation, and the roar and breath already guarded on their own phase flags - and a swing in progress is held with `isAnimatingBones()`, because 5 resets a controller that returns `STOP` and would otherwise cut the swing off on its first frame.
- The 26.1 rename pass has started: `ResourceLocation` is `Identifier` throughout the source - 892
  places in 164 files - and `Util` has moved to `net.minecraft.util`. Nothing changes behaviour:
  resource paths, resource keys and codecs read exactly as before, and every call the two classes
  carried is the same call under the new name. `ModelResourceLocation` is a different class and is
  still waiting on the model system, so the build does not pass yet and this produces no jar.
- The mechanical half of the 26.1 source pass is done, and the tree went from 4,667 compile errors
  to 2,801. What moved: block and item properties spell `noCollission` as `noCollision` now, the
  item and block registries build their properties from a supplier rather than
  a finished object, `Level.isClientSide` and `Level.random` are the `isClientSide()` and
  `getRandom()` calls they became, `ResourceKey.location()` is `identifier()`, and the datagen tag
  providers follow the new tag API - the item provider now extends NeoForge's, a tag appender
  carries two type parameters, structure tags extend `KeyTagProvider`, and POI tags are keyed by
  `ResourceKey`. Spawn weights moved off `SpawnerData` and onto `addSpawn`, so a biome's spawn table
  reads the same numbers as before. One behaviour note: 26.1 dropped vanilla's `mushroom_grow_block`
  tag, and the mod's decay blocks now join `SUPPORTS_VEGETATION` - the tag a small mushroom reads -
  which is worth checking in game.
- Saved data is on 26.1's value pipeline: every entity and block entity now writes through
  `ValueOutput` and reads through `ValueInput` instead of being handed a `CompoundTag`. That covers
  the hosted mobs and their variant ids, the chested goat, Shadowfax and the Nazgul steed with the
  saddlebags they carry, the backpack (including the same contents it keeps on the item), the
  fireplace, piggy bank and trinket table inventories, the camo spawner's mob list and delays, the
  four lockable chests' key codes, the keystone and milestone settings, and the dragon's home
  position. Keys and nesting are unchanged, so worlds save and load the way they did; this build
  still produces no jar, because the rest of the source is mid-port.
- The three chested mobs - the goat, Shadowfax and the Nazgul steed - are back on their supertype.
  26.1 moved the chested horses to `animal.equine`, and with the import fixed their saddlebag
  inventories, chest state and sounds are the ones vanilla's own chested mobs use. One behaviour
  note: the goat's head-up warning now lasts its 40-tick scream rather than however long its attack
  goal happened to keep the pose up, so it reads as one clear warning before a charge.
- The camo spawner's spawn loop follows 26.1: it reads a candidate position and entity id with the
  one-argument tag calls, places a spawned mob with `snapTo`, keeps its weighted spawn table in a
  `WeightedList`, and reads a spawn egg's entity type the way vanilla does now. How it picks, places
  and delays a spawn is unchanged. The spawner's block entity is ported too, so its egg-selection
  messages and its client sync no longer stand in the way of the rest of the tree compiling; what
  it prints when an egg is added, counted or replaced reads exactly as before. One fix comes with
  it: a spawner's stored entity data is a different kind of component in 26.1, and the old read had
  turned into an unchecked cast that still compiled but would have thrown as soon as it ran on a
  configured spawner. The tooltip read now goes the way vanilla's own spawner tooltip does.
- Every message the mod sends a player now uses 26.1's own call for it: the chat lines go through
  the system-message call, and the three that flash above the hotbar - the backpack's "no room",
  the occupied sleeping bag and a refused sleep - through the overlay one. Nothing the player reads
  changes; it is the same text, in the same colour, in the same place on screen. The sleeping bag
  itself keeps its reason for a refused sleep: 26.1 hands that reason back as a record, so the
  "too far away" / "obstructed" line still shows above the hotbar when lying down is not allowed.
- A sleeping bag still blows up where a bed would: the "does a bed work here" test reads the
  dimension's own bed rule now, so it explodes in the Nether and the End exactly as before and
  stays intact in the Overworld. Nothing changes in a vanilla world; a datapack dimension that
  forbids beds now gets the default bed rule rather than the old flag, which is what a vanilla bed
  follows too. The rest of the block is ported with it and behaves as it always did: the same
  softer bounce on landing and the same placement of the second half.

## [4.4.83-Final] - 10.22.2026

### Added

- Mithril, morguliron and ammolite trims show on the armour icon. Those three are the mod's own materials, so vanilla's sprite atlas has no cut for them and a piece trimmed with one fell back to its plain icon; the datagen now bakes a trim sprite for each material and armour slot by putting vanilla's trim shape through the material's own colour palette - the same swap the game applies to vanilla trims - and each piece selects it by material just like the vanilla ones.
- The fell beast glides. While it was on the wing its animation file's `glide` clip was never asked for, so a beast soaring with nothing to chase beat its wings exactly like one in a fight; it now holds `glide` whenever it is flying without climbing, keeps `fly` for the climb, and still stoops with `dive` when it drops on prey.
- The fell beast really flies. Its own AI was holding it at exactly the height its navigator asked for, so it could never sink or climb on its own; it now beats its wings to climb at about two blocks a second, coasts between beats, and sinks slowly while it glides, holding its path with a wingbeat roughly every second. When its path leads well below it, it commits to a fast stoop instead of easing down. A player riding the beast still steers its height with their pitch.
- The fell beast's wingbeats are heard. Its flap sound was only ever played by a wingbeat routine nothing called, so a beast flying under its own AI moved in silence; each downstroke of its flight now sounds, quieter than the one that lifts a rider and at most about once a second, so a long climb does not turn into a siren.
- A stooping fell beast is heard coming. Its dive is silent, so a beast dropping out of the sky gave no sign of it; the air over its wings now plays while it stoops, and a rider's flight wind is driven by height as well as ground speed, so it no longer fades exactly when the beast drops straight down instead of flying flat.
- The dive wind has a sound of its own, captioned "The beast dives." It borrowed the elytra's wind, so with subtitles on a stooping fell beast read as an elytra whooshing; the beast now carries its own wind event, built from the mod's gust samples.
- The dive wind loops for the length of the stoop. Its gusts are three seconds long but were replayed on a one-second timer, so every one was cut off mid-gust and the seam was audible; the wind is now a purpose-made 7.25 second loop, assembled from the loud middles of those gusts and joined end to start inside a crossfade, recorded at the depth the dive plays at, that follows the beast and ends when the dive does.
- The fell beast's wings are heard while a player rides it. A rider's flight is simulated on their own client, so the beast's AI wingbeat never ran for it and the ride was silent; the wings now beat at the same configured volume and rate while it is being flown, and the beat is played from the server so players nearby hear it too, not just the rider.

### Changed

- The mithril, morguliron and ammolite trim palettes are proper ramps. Each is eight steps from a highlight down to a shadow, and the old strips jumped between extremes - mithril ran bright yellow, near-black, then three whites - so a trim built from them read as speckle rather than metal. The colours now also keep clear of the armour they sit on: mithril's trim is a deeper blue than its silver plate, morguliron's a cold green against its warm black, and ammolite's a blue that separates from its green. The trim icons and sprites are regenerated from the new palettes; the same strips are what the trim colours elsewhere read from.
- The fell beast's wingbeat can be tuned in the mod config. Its volume and the shortest gap between two beats were fixed in code; they are now `fellbeastWingbeatVolume` (default 2.0) and `fellbeastWingbeatInterval` in ticks (default 15, and 20 ticks is one second), read on every beat, so the loudness and cadence can be adjusted by ear without a new build.
- The dive wind's volume can be tuned in the mod config. It was fixed at 1.6 in code; it is now `fellbeastDiveWindVolume` (default 1.6), read while the wind plays, so unlike the wingbeat it can be changed without a new dive. Its pitch is `fellbeastDiveWindPitch` (default 1.0, since the loop is recorded at the depth the dive should sound) alongside it, so the wind can be made deeper or sharper by ear. The wind is heard on each client, so both are the listener's own settings rather than the server's.
- The Dwarven Mining enchant is Dwarven Excavation now, and its description says what it does: it breaks a slice across the face you struck, 3x3 at level I up to 9x9 at level IV, and sneaking breaks only the block you hit. It used to promise a vein of matching blocks, which the effect has not done since it was rewritten. Both languages carry the new wording; the enchantment's id is unchanged, so existing gear keeps it.
- The Dwarven Mining effect's field is `haste` rather than `range`. It was never the area - the area follows the enchantment's level - it is the amplifier on the mining speed buff the effect also hands out, so a datapack that set `"range"` on the effect wants `"haste"` now (nothing the mod ships sets either).
- A rider's dive is quiet. The wings beat for as long as a player flew the beast, even with its nose down; the beat now stops once the beast is dropping more than a block a second and steeper than `fellbeastDiveAngle` (default 45 degrees below horizontal), and comes back as soon as it levels out.
- `homeRadius` can be set as low as 8. Its floor was equal to its default, so the only direction the setting allowed was up.
- The config descriptions for `disableFakePlayer` and `paymentType` say what the two options actually do. One claimed a default it did not have, and the fake-player option was written as though turning it off disabled the effect rather than the fake players.

### Removed

- The `number_of_blocks` option is gone from the mod config. It set a range for the Dwarven Mining enchant, which takes its area from the enchantment's own level now - 3x3 at level 1 through 9x9 at level 4 - so nothing had read it since that change.

### Fixed

- Trimmed armour wears its trim on the icon again. 1.21.4 chooses the trimmed sprite with a `select` on the piece's trim material; the datagen still wrote the old `trim_type` predicates into the model's `overrides` array, which the game no longer reads, so every trimmed piece kept its plain icon however it was trimmed. Each of the twelve pieces now has a select definition over the eleven materials 1.21.4 ships, and resin - new in this version and missing from the old list - has its models too.
- Molten mithril and molten morgul iron are named when placed. The liquid blocks are registered as `mithril` and `morguliron`, while the language file only named `mithril_fluid` and `morguliron_fluid`, so a placed pool showed a raw key.
- Spawn eggs are drawn as eggs again, each in its creature's own two colours. 1.21.4 paints a spawn egg with vanilla's two-layer egg model, using the colours an item model definition tints it with, and the datagen was still writing the older model that named that template with no colours at all - so every egg in the game came out a flat untinted shape. Each of the 55 eggs now gets a definition carrying the two colours it was registered with, and the 55 model files that only pointed at the template are gone.
- Wall signs and hanging wall signs are named again. The blocks are registered as `sign_wall_<wood>` and `hanging_sign_wall_<wood>`, but the language file only named `wall_sign_<wood>` and `wall_hanging_sign_<wood>`, so a sign placed against a wall - and the title of the hanging sign screen you open on it - showed its raw key instead of its name; all seven woods carry the names the blocks actually ask for now.
- Boats and chest boats are named. Their items are `boat_<wood>` and `chest_boat_<wood>`, while the language file called them `<wood>_boat` and `<wood>_chest_boat`, so every boat in an inventory showed a raw key; the item names and both boat entity names follow the real ids now.
- Potted flowers are named. The blocks are `potted_flower_<flower>` and the language file used `potted_<flower>`, so a flower in a pot showed a raw key; the language helper now takes the potted block itself rather than rebuilding its name from the flower, which is what let the two drift apart.
- The second decay bloom block, pipeweed crops, the Arda portal, the Morgul crystal, the six projectiles, the simple trap and the two molten fluid types all have names now; none of them was in the language file at all.
- The camo spawner says which mob it selected or replaced. Both of those chat messages were missing from the language file, so the spawner printed raw keys at the player.
- The Chinese translation covers everything the English one does. The orc and southron villagers with all their trades, four biomes (Nurn, Forochel, the Forochel slopes and the Blue Mountains) and two spawn eggs had been left out, and every name above was missing from it too; the two files now hold the same 1719 keys.
- The fell beast's fireball and the boulder no longer crash the game when they are drawn in flight. Each kept the angles it sits at once it lands in a field that started empty, and its renderer read that field on every frame, grounded or not, so the first frame either one was on screen threw a NullPointerException and took the client down; the field starts at zero now.
- The milestone's dimension cost is a setting of its own again. It and the minimum tick speed were both saved under `minimum_tick_speed`, so the two could not be set apart and the dimension button's lowest allowed cost was whatever the tick speed was set to; the option is `dimension_cost` now.
- Milestones charge levels when the payment method is experience. That branch checked for and spent the configured payment item instead, so a jump priced in levels was refused for lack of coins, or paid with coins while the levels went untouched; it checks and spends levels now.
- The milestone list shows every milestone. It skipped whichever milestone came first in the list, its page count was one short whenever the total was just past a multiple of seven, and scrolling moved the page number without redrawing the list.
- The milestone's experience cost readout keeps its number. Clicking it clamped the cost shown to 1, because its bounds had been copied from the fixed payment icon beside it.
- Hallowed Earth hurts the undead again. Its `disableFakePlayer` option was read as a plain off switch that skipped the damage for every entity, not as a guard against fake players, so with the option on by default the block did nothing at all; it now spares fake players and hurts everything else it was always meant to.
- A milestone refuses a jump it cannot charge for. When the configured payment item no longer resolves to an item, the item branch skipped the charge and the teleport was free; the jump is refused with the usual payment message now.
- The fell beast roars, stoops and swings as it moves. Its animation file carries `roar`, `dive` and three `walk_attack` swings that no controller ever asked for, so the beast opened fights in silence and swung the same standing animation at a run; a bout of aggression now opens with `roar`, losing height on the wing plays `dive`, and a swing made on the move plays one of the `walk_attack` clips.
- The elemental golem's tornado, the web trap and the thrown cobweb are drawn again. None of the three had a client renderer registered, so the tornado and the cobweb ball were invisible and a web that hit the ground left nothing behind; each now registers its GeckoLib renderer, the cobweb draws from the model and texture that were already in the jar, and its idle animation is the one the animation file actually contains.
- The mallorn hanging wall sign is listed with the hanging signs. Its registration named the plain mallorn wall sign where the hanging wall sign was meant, so the game's hanging sign block entity claimed a block that is not one and never listed the one that is; the mod's own sign block entity already covered every hanging sign, so a placed sign was never affected.
- The tornado's funnel animates while it is on the field. Its animation clock was pinned to zero, so the rings never turned and the pillar stayed hidden below the spawn point; the entity reports its own age now, so the funnel rises and its rings spin through the strike.
- The fell beast's attacks use the animations its file has. Its ranged attack asked for `ranged` and its swimming movement and a leftover swim controller asked for `swim` and `swim_fast`, none of which the beast's file defines, so those controllers were left with nothing to play; the ranged attack now plays the beast's `fire`, the beast keeps beating its wings with `fly` while it swims, and the redundant swim controller is gone.
- The mithril and morguliron golems' ranged attack plays a swing. Both asked their animation file for `ranged`, which it does not contain, so the controller reset onto nothing and the golem stood still through the shot; they play their attack animation for it now.
- Gollum's swing no longer blanks his pose. His attack controller asked for an `attack` animation his file does not have, which reset the controller onto nothing and dropped him to his rest pose; he holds his idle pose through the swing instead.
- Villagers sit down properly. Their controller asked for a `sitting` animation that only the dwarf file carried, so an elf, hobbit, human, orc or southron villager who sat lost its pose and snapped back to idle; all six merchant animation files define it now.
- The swamp hag's attack and heal animations play. Her combat controller returned `STOP` in the same tick it set either animation, which makes GeckoLib throw the animation away, so she cast without moving; each branch keeps the controller running until the cast is done.
- Shadowfax's jump plays. The jump branch set the animation and then fell through to the controller's `STOP`, dropping it on the tick it started; the jump now runs for its length, after which running and walking take over again.
- The rock golem and the Istari stop holding a finished attack. Their combat controllers returned `CONTINUE` on every tick, so after a punch, rage, attack or heal ended the controller stayed live on its last frame and the mob kept that pose through idle and walk; both now hand the controller back once the animation is done, and the Istari's attack and heal play once instead of looping.
- The uruk-hai and mordor orcs animate their shots. Their ranged branch waited on the weapon flag, which is set on the server and never synced, so the client could not see it; it now reads the synced shooting flag the bow goal publishes.
- The tree ent and the swamp hag animate their throws. Their attack controllers keyed the throw on the weapon flag, which is an ordinary field set only on the server (and only for a bow in the main hand), so the client never saw it and the throw animation never played; the ranged goals now publish a synced shooting flag that both controllers read.
- Mobs that fight at range are marked as engaged while they throw. The game's throwing goal never touched the mod's aggressive flag, which only the melee and bow goals set, so a snowball, boulder or potion thrower fought with the flag stuck off and the attack animations waiting on it never played; the throwing goals now set it while they run and clear it when they stop.
- The fell beast breathes fire again, in the three parts its file was authored in. Its fire animation waited on the weapon flag, which is set only on the server and only for a bow in hand, neither of which the beast has, so its attack controller could not see it; the breath now runs off the beast's synced breathing flag and plays `fireStart` to wind up, `fireLoop` for as long as the breath lasts, and `fireEnd` once as it stops.
- The uruk-hai and mordor orcs shoot with the uruk bow they carry. The weapon check only looked for a vanilla bow in the main hand, and theirs is a mod bow in the off hand, so no archery goal was ever installed and the bow was decoration; a bow in either hand now chooses the bow goal. They fight as archers from then on, as any bow-carrying monster does.
- Hostile mobs animate their walk while they chase. Their walk controllers also required the mob not to be aggressive, and that flag stays on for as long as the attack goal runs, so a creature running down a target slid along in a static pose; walk now depends only on movement, and the fell beast keeps the faster prowl its file provides for a beast on the hunt.
- Hostile mobs keep breathing while they fight. Every hostile idle controller also required the mob not to be aggressive, so a creature that held a target stood frozen in its rest pose the moment it paused between swings; the idle animation now plays whenever it stands still, aggressive or not.
- The thrown cobweb breathes as it flies. Its animation controller stopped itself right after asking for the idle animation, which makes GeckoLib drop the animation and draw the web on its rest pose; the controller keeps running now, so the web pulses the way its file describes.

## [4.3.81 - Final] - 2026-10-21

### Added

- Added the Blue Mountains, a temperate mountain biome shaped like the Iron Hills but with the mod's mountain stone where vanilla stone would be, and with teal grass and water (#59B6BD).
- Mountain stone counts as stone for ore generation, so the Blue Mountains' underground carries the same ores as any other biome's.
- The Blue Mountains grow dwarven maples, and dwarven villages now start there as well as in the Iron Hills.
- Mountain stone also forms andesite-like veins through the Blue Mountains' stone, so the rock still shows in worlds that never apply the biome's surface rules, such as a single-biome world.
- Human NPCs spawned in a cold climate, such as Forochel, wear the winter clothing textures; humans spawned anywhere warmer keep their normal clothes. The outfit is chosen once, from the biome they spawned in, so it does not change if they later travel.
- Camouflage Smokers can be dyed: right-clicking with a dye tints the smoke a third of the way to that colour, another click with the same dye takes it to two thirds, and a third to the full colour, after which the next click returns it to plain smoke. A different dye starts again at a third. The dye is not used up.

### Changed

- The generated resources are written in the forms the game now uses, so a data run no longer rewrites the same eight hundred files: recipe ingredients carry the short `"item"` or `"#tag"` entry, biomes list their carvers flat and their music as weighted entries with a music volume, trim materials drop the retired item model index, village templates are saved at the current data version with the current block entity and attribute names, and leaf pile models state the UVs the game fills in anyway.
- Data generation runs as the client data run, the run NeoForge 21.4 provides for writing both assets and data; its classpath carries the client library the generator needs to boot. The generator now produces the 800 item model definitions, the fourteen boats included, instead of them being mirrored by hand.
- Blockstates and item models are written by the mod's own generator, since the NeoForge model generators this datagen relied on no longer exist; the generated JSON keeps the same shape.
- Armour sets are now described the way the game expects: each of Mithril, Morguliron and Ammolite has an equipment asset that names its worn texture, so the sets render on players and horses from one definition and the full-set effects are still recognised.
- Spawn eggs are rebuilt on the vanilla item with their two colours moved into the item model, and every mob still registers under its own entity id (the chest boats no longer share the plain boat's id).
- Tools and weapons are built from the game's tool materials again, so every mithril, morguliron and ammolite tool keeps its durability, enchantability, mining speed and damage, and is repaired by the ingot or gem it is made of; the shears keep their three durabilities and repair the same way. The elven and uruk bows stay enchantable even though they never wear out, and the trinkets still take the name of the potion inside them.
- Food and drink effects are carried by the consumable component, so drinks still tip the bottle back and foods still keep their bonuses.
- Tag, sound, particle and village-template datagen no longer asks for the file helper the modding API dropped.
- Recipe datagen builds its recipes through the game's recipe-provider instance, so crafted, smelted and stonecut items keep the recipe ids and unlock conditions they had before.
- Fireplace and trinket table recipes are built on the game's current recipe definitions: each ingredient now carries its own nested entry with its count, so the fireplace still smelts bread from three wheat, the trinket table still shows its fuel in JEI, and both still unlock on the same conditions.
- Potions are registered with the name the game now requires on them, so potions, splash potions and lingering potions keep the names they are shown with.
- Blocks and items are registered with the ids the game now reads while it builds them, so every block keeps its loot table and its name, block items show the name of their block, and items keep their inventory model.
- The clover and moss patches are drawn through the game's own model loader again, and camouflage blocks still take on the look of the block next to them.
- Block entity types are built directly, and the wood wall torches name the floor torch's loot table themselves, so torches still drop the way they did before.
- Creature renderers and models now use the per-frame render state the game switched to, so mobs, boats and thrown projectiles draw the same way they did before.
- Item colours and model properties are registered the way the game now reads them: a trinket's gem layer still takes the colour of the potion inside it, a bow still shows its three drawn frames at the same draw points, and a coin pouch or key ring still swaps its icon as it fills. Item model definitions now sit in `assets/tolkienmobs/items` and name those properties, replacing the old in-model predicates.
- Item model definitions are generated for every item: the model generator now writes `assets/tolkienmobs/items/<item>.json` for each item the mod registers, pointing at the item's model by default, and the bows, pouch, key ring and trinkets declare their dispatch and gem tint in the generator rather than in hand-written files.
- Loot is built on the game's current loot API: block drops keep their shears and silk-touch behaviour, the extra coin a loot modifier adds to a chest or mob drop is still rolled by its chance, and a trinket dropped from a table still picks a helpful potion when the loot is rolled.
- Human NPC winter clothing now follows the `tolkienmobs:climate/cold` biome tag instead of a hard-coded temperature, so a datapack can decide which biomes dress humans for the cold. Iron Hills, Barrow Downs and Hithaeglir count as cold alongside Forochel.
- Mob effects tick on the server now that the game only ticks them there, so the burning, drowning, fear, terror, nimble, sleep, elven life, flying and Eru effects still apply their damage, extra effects, particles and step height, the blacksmith and corrosion effects still repair and wear the player's gear, the lightning effect still calls a bolt down on the afflicted, and paralysing fear still stops the player's movement keys.
- Mobs now deal and take damage through the game's server-side damage hook, so every boss reaction still fires: Shelob still calls her spiders, the golems still apply their elemental effects, and the fell beast still puts a shield on cooldown. Mobs the bosses summon arrive as reinforcements or breeding results rather than anonymous spawns.
- Block entities work on the game's current APIs: a camouflage spawner still reads back the mobs stored in it and shows the chosen one turning above the block, a fireplace still accepts only fuel and takes its burn times from the game's fuel values, trinket tables and fireplaces still find their recipes, the lockable chests still name the key you open them with, and a milestone still charges the item configured as payment.
- Every container screen draws through the game's current widget pipeline: the chests, fireplace, trinket table, backpack, milestone, well, pouch and key screens keep their panels, progress arrows and fluid gauges, the camouflage spawner still turns its chosen mob on the mouse, and the greyed-out buttons still dim when they are switched off.
- Blocks answer the game's current neighbour-change hook, so waterlogged blocks still refill when a neighbour changes, wall torches, lanterns, wall mushrooms, decay blooms, patches and the Arda portal still break when their support is removed, dirt paths still revert to cobble, and a sleeping bag's two halves still link up and still bounce you when you land on them.
- The lightning bug and locust displays draw their parts through the renderer, so the bug body still draws cutout and the lightning bug's glow still overlays it as a separate translucent pass.

### Fixed

- The client gets past its loading screen again. The morgul crystal's renderer asked the vanilla end crystal model for glass, cube and base parts, which this version renamed and moved, so building the renderer threw and the game sat on its splash forever; the crystal now bakes its own copy of the layer it draws.
- Mobs that follow food no longer crash the game the moment one of them ticks. The game added a tempt range attribute that their follow goal reads, and the squirrel, rat, frog, gecko, thrush, crebain, goat, the two horses, the Istari and every heard animal now declare it.
- The molten mithril and morguliron blocks no longer name themselves as their own model parent. The model loader reads that as a loading loop and reported it sixty-four times while the game started; both models now carry nothing but the break particle, the way vanilla's water and lava do, since the fluid renderer draws the liquid itself.
- The Arda Staff draws its GeckoLib model again. Its item model still named the retired `builtin/entity` parent, so the staff could not be drawn at all; it now uses the game's special item model, which hands the staff to the GeckoLib renderer that was already registered for it, with the display transforms and break particles kept on the model underneath.
- The lightning bug and locust blocks no longer ask for a model parent the game dropped. Their block models named `builtin/entity`, which 1.21.4 no longer has, so the model could not be loaded and the block fell back to the missing model; the models are now bare like vanilla's chest - they draw nothing of their own and keep the block's break particles - while the block entity still draws the bug.
- Wooden ladders hang on the vanilla ladder model again. Their generated models named the model's parent without the `block/` folder, which no longer exists in the game's files, so every one of the seven ladders would have come out as the missing model.
- Data generation runs again. The port's last compile errors are gone: enchanted books are built through the game's enchantment helper, tag lookups, registry lookups, carver steps, the solid-render check, the fluid fog parameters and the JEI subtype interpreter all use the shapes the game now has, so the model, loot, tag and recipe providers can be regenerated from the build.
- Entity loot generation completes. The eight NPCs (humans, dwarves, elves, hobbits, Southrons, orcs and their traders) get the empty loot table vanilla gives villagers, and thrown ammo, the trap and the boats stop claiming a loot table they never had.
- The three shears attach their repair tag and tool rules while the item registers rather than in the item's static properties, so a data generation run, where the item registry is already frozen, no longer fails on them. The trinkets' default potion component is attached the same way.
- Barrels open towards the side they are placed against. The generator's direction variants rotate a model whose front is drawn on its top face (a barrel's lid) 180 degrees from vanilla's convention, so the nine barrels now ask for that offset and their blockstates match vanilla's.
- Wall blockstates are written in vanilla's order - the post first, then the four low sides north to west, then the four tall ones - rather than in whatever order the map of wall sides happened to iterate, so regenerating no longer rewrites every wall file.
- JEI shows the fireplace's recipes again. The game stopped sending recipes to the client, so the server now hands the fireplace recipe type to the client when it syncs a datapack, and the recipe category shows the recipes that arrive.
- The Blue Mountains no longer come out as bare stone: their floor keeps its grass, and mountain stone starts in the block beneath it instead of a band of dirt.
- The Camouflage Chest now works as a chest instead of being a plain block: it holds 27 slots, opens on right-click with the player's inventory below them, reads on a comparator, works with hoppers and pipes, and drops its contents when broken.
- Goats warn before they charge again: their melee hook had stopped being called, so the head-down warning never played.

## [4.3.81]  - 2026-10-18

### Added

- Added Forochel and Forochel Slopes biomes, cold snowy plains and mountain biomes based on vanilla snowy plains and snowy slopes.
- Added frozen house village structure (ttmhouse_frozen) that generates in the Forochel biome with human NPCs as residents.

### Fixed

- Generated entity loot tables had a potion id baked back into them, so a table's trinkets could only ever carry the one potion written into the file. The tables ship without the id again and pick a potion when the loot is rolled.

### Removed

- The spider tree's old `ttmspidertree` structure template is gone; nothing referenced it once the structure became `ttmtree_spider`.

## [4.2.80] - 2026-10-17

### Changed

- Barrows rise in clusters instead of standing alone. Barrow-downs now asks for one cluster every ten chunks, and each cluster seats one to five mounds on a single circle, a block or three apart, with its own random facing; a cluster that cannot seat a mound simply comes out smaller.
- The build number and the local changelog entry are written only when a jar is built, so loading the game for testing - client, server or data generation - no longer bumps the version or edits the changelog.

## - 2026-10-17
* Initial automated test build setup

## [4.2.054] - 2026-10-17

### Changed

- The dwarven, human and Nurn villages lay the mod's stone path block where a vanilla village lays a path. Dwarven and human streets, the stubs that close a road off and the fountain plaza still behave the way vanillas do - a tenth of a street still grows over, and street crossing water is still bridged - and the driveway and the porch at each house's door are the block outright. Nurn's roads, its road stubs and the plaza around its well walk on the block too, where the desert village they are built from walked on smooth sandstone; its walls, well and lamps stay dark stone.

### Fixed

- A Nurn lake no longer writes outside the chunks it is allowed to write, which filled the log with `Detected setBlock in a far chunk` errors as the player moved and left the edge of the lake and its grass shore cut off. The whole lake, water and shore, now stays inside the feature's write window.

### Entity AI and behavior

- Changed `meleeGoal` and `bowGoal` visibility from `private` to `protected` in `TolkienMonsterEntity` to allow subclass access.
- Adjusted flying behavior for `GreatEagleEntity` and `GwahirEntity` to fly more frequently by:
  - Reducing `StartFlyingBirdGoal` chance from 120 to 60 (2x more frequent takeoff)
  - Increasing `LandBirdGoal` chance from 240 to 480 (2x less frequent landing)
  - FellBeast behavior modified to save spawn location
  - Fixed bug with Witch King spawning with Fell Beast

### Villages

- Converted desert, dwarf, elven, hobbit, and human houses into villages with connected paths and driveways, wells or fountains, bells, farms, decorations, animals, and villagers. Existing Tolkien inhabitants are retained.
- Villages generate in Haradwaith, Iron Hills, Lorinand, Shire, and Gladden Fields respectively, using one shared placement grid instead of independent house clusters.

### Flying mobs

- Fixed actual takeoff and airborne movement for crebain, thrushes, great eagles, and Gwahir, including flight controls, navigation conflicts, and landing behavior.
- Enforced a 64-block horizontal flight radius and a ceiling 100 blocks above the spawn origin. Flight origin and flying state persist across saves; sitting stops flight.
- Added gliding and eagle dive attacks, updated flight animations, and made great eagles and Gwahir take off more often and land less often.

### Dwarven Miner

- Set the maximum enchantment level to 4: levels 1–4 mine full 3×3, 5×5, 7×7, and 9×9 areas.
- Mining follows the targeted face. Wall patterns extend one block below the target, with the remaining rows above; floor and ceiling patterns are centered on the target.
- Restored normal harvesting, drops, experience, durability, and cancellable break events. Added tool-tier and unbreakable-block checks; sneaking disables area mining, and mining stops when the tool breaks.

### Other entity and data updates

- Moved the ambient animal base to `TamableAnimal` and updated its subclasses; added synchronized gliding and aggression states.
- Added melee combat goals to several hostile mobs, adjusted goblin goal priorities, and exposed shared melee/bow goals to subclasses.
- Fixed the Goblin King's snowball projectile launch and configured its ranged combat independently of held bows.
- Updated potion assignments in ten generated entity trinket loot tables and reordered biome tag entries without changing their membership.
- Fixed the Witch King, the nazgul and the Mordor orc being rendered standing on their mounts: all three now play a sitting pose while riding, and the animation moves the bones their rigs actually have.

### World generation

- Rockpiles dot a biome instead of carpeting it: one placement now fills a 1-3 block patch rather than every surface column, and each chunk gets six placements instead of ten.
- Random rubble, stone spikes and stumps are sporadic decoration. Rubble makes half as many attempts per placement and drops to six placements per chunk; spikes and stumps pass a one-in-six chunk filter and place one or two, and are positioned from their own column's surface instead of the chunk corner's.
- Rock piles recognise a neighbour by block type instead of exact state, so grass carrying snow counts as grass.
- Mirkwood webs hang from the tree canopy. They read a heightmap that never contains a placed tree and ran in the decoration step ahead of the mirkwood trees, so nearly every attempt found open air, rock or sky; they now read the live heightmap and run after the trees, and four placements do the work of twenty.
- Mordor and the Ash Mountains no longer hold water. The dimension's aquifer fills those biomes under the sea level and in pockets high above it, which no biome setting can change, so chunks now turn that water into lava as they load. Terrain generated before this change is converted too.
- Nurn is the watered half of that country: dark stone ground broken up by cobbled patches, tan water, a full 1.0 of rainfall, and no lava of its own anywhere in the biome. Grass grows only near water - on the low shores and islands a few blocks above the water table, and on the meadow a lake lays around its own water - so the biome reads as dry ground with green at its edges.
- Nurn's lakes are dug by a feature of their own, at most two chunks across, and asked for once every 18 chunks so the lowland carries the occasional lake rather than a chain of them. Each one lays the grass meadow around its water, up to sixteen blocks out, and the whole lake stays inside the chunks a feature is allowed to write to, so nothing is left with a missing edge.
- Nurn's villages are the vanilla desert village rebuilt in dark stone. Every sandstone and terracotta shape is swapped for the dark stone block of the same kind, the farm rims that were sandstone stairs become deadwood log fences, the crops stay the desert crops, and the roads get their own terminator pool, because the desert pieces that close a road off are raw smooth sandstone and do not pass through the street templates. A Nurn village only starts on dry ground.

### Trinkets and loot

- A trinket's potion is chosen when its loot is rolled instead of while the loot tables are being built, so the trinkets from one table no longer all carry the same potion. A potion id written into a table by hand still pins that potion.
- Trinket loot only hands out benevolent potions: harming, poison, slowness, turtle master's slowness and the mod's own elemental, corrosion and terror potions no longer turn up on worn belts, rings and amulets.
- Generated biome tag files are written in a stable order, so two data generation runs in a row no longer rewrite them.

### Trinket table

- The table mends damaged gear as well as crafting trinkets. A damaged tool in the trinket slot, its repair material in the potion slot and an ammolite gem in the gem slot undo up to 1000 damage per gem and material, repairing the tool where it sits rather than moving it to the output slot.
- Which material repairs what follows the anvil's own rule, so mod tools take their own ingots, and the mod's shears - the one tool the anvil gives no material at all - now take their tier's repair item.
- The damage one gem and one material undo is a config value, `trinket_repair_amount`, defaulting to 1000.
- What repairs what is a data pack's decision too: an entry under `data/<pack>/tolkienmobs/repair_material/` names the tools, by item or by tag, and the material that mends them. An entry covering a tool replaces the anvil's rule for that tool, so a pack can hand the table a material the anvil would refuse or take away the one it would use.
- The mod ships no entries of its own and the anvil's rule stays the fallback, so an unmodified game repairs exactly as before. Entries are read when the world loads.
