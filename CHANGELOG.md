# Tolkien Tweaks — Changelog

All notable changes to this project will be documented in this file.

## [5.0.1] - 2026-10-25
* Structural mod updates and compilation builds


## [Unreleased]

### Added

- The Misty Mountains rise into a range worth the name. The overworld's height is vanilla's noise and no biome can change it, so the summits are built: one tall stone cone every dozen chunks or so, a six- to ten-block foot climbing thirty to sixty blocks, seated into the slope it stands on and capped in snow over its upper half. They go in after the trees, so nothing is planted on a summit, and because the terrain underneath is untouched the valleys between the peaks keep their shape. How often they appear and how tall they grow are one line each in the placed feature and the feature itself.
- A key held in either hand now shows a camouflage block as itself when the key carries that block's code, so a camo keystone can be found among the stone it is imitating; the dev tool shows every camo block as itself while it is held in either hand. Both take effect as soon as the item is in hand: a chunk's mesh is built once and kept, so the sections holding a camo block are rebuilt when the hands change.
- A coin pouch with coins in it banks them when you right-click a piggy bank. The bank's own menu opens as before for an empty pouch or for anything else in hand, and whatever the bank has no room for stays in the pouch.
- The camo keystone's pulse has a length of its own. A pulse used to last thirty ticks with nothing on screen to change it; it now keeps a length beside the delay, saved with the keystone, and the screen shows both numbers - the delay first, then the pulse - so each redstone mode has the duration it uses.
- The backpack can carry a magnet. Three upgrade tiers - iron, gold and mithril - pull loose items from sixteen, thirty-two and sixty-four blocks away, filling the backpack first and the player's inventory second, and a fourth toggle button sits beside the backpack's other toggles to switch the pull on and off. The pull runs whether the bag is worn, held or standing on the ground beside you, and its screen does not have to be open for it. `M` flips the same switch from anywhere without opening the bag, and the button follows the key even while the backpack's screen is open. The best installed tier sets the reach, the pull is a plain radius that stone and walls do not stop, and a drop still counting down - something just thrown or dropped - is left where it lies. Each tier is crafted from the one before it: a U of iron, gold or mithril ingots around the upgrade base or the previous magnet, with an ammolite gem between the prongs. The setting is saved with the backpack, so a tier keeps its state across a reload, and a backpack saved before the magnet existed loads with the magnet switched on.
- A placed backpack is picked up by sneaking and using it, whatever is in hand. It folds back into its item with everything it held - storage, upgrades, bucket slots, tank, magnet tier and every toggle - and the sleeping bag or campfire it had put out is taken back in first. The item goes straight into your inventory when there is room and drops at your feet when there is not, and sneaking also takes priority over filling the tank, so a full bucket cannot drink from a pack you meant to lift. Using it without sneaking still opens it.

### Changed

- The mod's storage speaks 26.1's transfer API everywhere now, and the compile's removal and deprecation notices are back on. Every menu slot, block entity, tank and item-held container was still using the item-handler API 26.1 marked for removal, so the build suppressed both warning categories and no longer reported anything it otherwise would. A menu slot now reads and writes its handler directly, a tank is a plain resource handler, and the pouch, ring and key keep their contents in the item component through a handler of their own - the one place the old API had no replacement, because a slot also replaces a slot's contents outright and NeoForge's component handler only inserts and extracts. Nothing a player sees changes: the backpack's storage, upgrade and bucket slots, the piggy bank, fireplace, trinket table, camouflage fluid block, coin pouch, key ring and key item screens open, quick-move and filter as they did, a backpack's tank still fills and drains through the same bucket slots, and a fireplace still consumes its fuel and the trinket table its gem and material. The only warnings the compile still prints come from JEI's own recipe API, which is a separate port.
- Mordor's stone spikes are twice as tall. The lumps stood seven to ten blocks high, which read as boulders beside the biome's rock piles; they run fourteen to twenty now, and the rare needle - one spike in sixty - forty to eighty where it used to be twenty to forty. A cone's base is built from a fraction of its height, so that fraction was halved to match and the spikes keep the slender profile they had instead of spreading out into hills.
- A village's site check reads the terrain column itself now. It used to compare the surface heightmap with the ocean floor heightmap and call the column wet when they disagreed; the column is now read directly and its highest block asked whether it holds a fluid, so a lake still refuses a site and open ground, ice or snow still allow one. This is a clearer statement of the same rule, not a change in where a village may stand.
- The 26.1 launch no longer warns that the mod uses `@OnlyIn`. The annotation, with its `Dist` import, is gone from the sixteen places the mod's own classes carried it - the camo blocks' tick effects, the milestone's block and its client state update, the bow and arrow tooltips, the cobweb constructor and the three custom particles - because 26.1 no longer strips members by side, so it gated nothing. The client-only classes are still client-only, because only the client registers them.
- Data generation now refuses to write a village house whose doorway marker is not on its own template's front edge. Such a marker can never join a house to a street - the game drops the whole house - and the failure used to surface only as a town centre standing alone in a world. `./gradlew runData` now stops with the house's style, the marker's position, the template's size and the column it should have used, so an entrance coordinate copied from a house of another depth or width is caught before it ships.
- A village house pool no longer offers vanilla's second small plains farm. 26.1 ships only one of the pair, so that entry could never place anything; the plains-style villages of the Shire, the elven wood, the dwarven halls, the frozen north and the Glidden now offer the house and the farm that exists, and the desert styles around Nurn keep both of their farms.
- The mod's own version restarts at 5.0 for this line. The jar is 5.0.0 and the build-number task rolls it up to 5.0.1, 5.0.2 and so on, so a 5.0 build can never be mistaken for one of the 4.3 releases.
- The generated data is regenerated for 26.1. Each biome's fog, sky, water fog, ambient sound, music and particles are written as environment attributes now, with its grass, foliage and water colours beside them; the forest flowers still place the same number over the same spread, because that count moved onto the placed feature that uses them; a recipe result no longer repeats a count of one; a trim material no longer names an ingredient; and the tag that lets the mod's jungle bushes take root is called `supports_vegetation`, which is 26.1's name for the old mushroom-grow tag. Nothing a player sees changes - the fog colours, the flower density, every recipe, every trim and every village template keep their values, and the mod's five loot modifiers are re-saved unchanged.
- The mod compiles for Minecraft 26.1.2. The last of the source is ported - an armour slot now asks the item whether it can be equipped rather than testing for a class that no longer exists, wall blockstates name the four side properties as vanilla does now, music disc translations take their song id from the holder, a jigsaw village states its centre distance the new way, and the bug block's collision hook takes the two arguments every block gained. Nothing a player sees changes; the build itself is what was broken.
- The dwarven hammer still breaks the blocks around the one you mine, and text carried between the mod's screens and the server still arrives as the same JSON - 26.1 moved the block-break event into its own class and dropped the old component-to-JSON helper, so both go through their new equivalents.
- Every spawn egg still hatches the mob it names, and the eggs from a dispenser still work: 26.1 carries the egg's creature on the item's own properties instead of handing it to the item's constructor.
- The mithril, morgul-iron and ammolite armour trims keep the same palettes and the same hover colours - 26.1 builds a trim material from its palette name and its description alone, and the registry paths still point at the mod's own palettes. Their generated JSON loses the `ingredient` field the old format carried, which nothing reads.
- A lockable chest's code still persists, with one caveat: the way an entity attachment is saved changed, and a bare string cannot be written the new way, so a chest locked by an earlier version starts at the default "Enter Code" and needs its code set once. After that it saves and reloads as before.
- The camo keystone opens its key settings for a creative player who sneaks and uses it - the same fix as the lockable chests, since it asked the client's screen for the shift key from inside the server's own branch. Using it without sneaking still runs the ordinary key flow.
- The camo smoker still takes a dye's colour: three clicks with one dye deepen the smoke towards it, a fourth clears it, and a different dye starts again at one. 26.1 stores a dye's colour on the item itself rather than in its class, so the smoker now reads it from the stack, which means a dye from another mod that registers its colour the same way tints the smoke too.
- The mod's mushrooms still take root on its own decay-grow soil, and still on ordinary ground only in dim light. Nothing a player sees changes - 26.1 moved the three-way soil answer those mushrooms ask for into the game's own utility package.
- All four lockable chests open their lock settings for a creative player who sneaks and uses one - single and double, treasure and plain alike. The check asked the client's screen for the shift key from inside the server's own branch, where it could never be true, and 26.1 removed that call outright; it now reads the sneaking flag the server already has. Using a chest without sneaking still runs the ordinary lock and key flow, and the chests' contents, opening and comparator output are untouched.
- The leaves that drift down in the mod's woods keep their colour, still taken from the oak leaves overhead at the spot they fall, and a message meant only for operators still reaches exactly the operators. Both were internal calls 26.1 renamed - a block's tint is asked for as a tint source now, and the op list is keyed by name and id rather than by game profile - so nothing a player sees changes.
- The mod's mob effects and its two enchantments behave as they did: fear still withers, blinds, weakens and slows, sleep still lays a player down for the night, the burning effect still sets fire to whatever it touches and hits wither skeletons harder, and the dwarven miner and elven fleetfoot enchantments still grant haste and speed with a 3x3-to-9x9 dig. 26.1 renamed the slowness, haste and speed effects and moved the wither skeleton to its new skeleton package, so the code now names them as vanilla does; nothing about their strength, duration or chance changed. The thunder effect still calls a bolt down on the block the target stands on. One thing to check in game: a player paralysed by fear now has their movement keys cleared rather than their movement itself, because 26.1 keeps a player's movement private and computes it before the mod's hook runs, so the effect's own server tick is what holds them still.
- The seven wood barrels still spill everything inside them when broken: 26.1 drops a container's contents itself when the block goes, so the barrel's own removal hook is gone and a broken barrel behaves as it did. Its lid, its opening stat, the piglins' annoyance at an opened barrel and its comparator output are untouched.
- A full-grown bramble bush still hurts whoever walks through it, a rock pile and a patch still keep their shape and their offsets, and both wall mushrooms still sit on a wall as before - the plant blocks needed the shape 26.1 gives a bush's codec, and the bramble bush's collision hook the same two new arguments every block now receives.
- The camouflage spawner explains itself again: its item still names the creature it will spawn, and its two hint lines still follow when the item carries no spawn data. 26.1 moved a block's tooltip onto its item, so the camo spawner gets its own item for that line - the id, model and name are unchanged. A creative player who shift-clicks one now also gets its settings menu, which the old shift check could never see on the server.
- An Arda portal still drops an orc out now and then when it stands in a dark place, and still takes a player through it in the same time; it now asks the world whether monsters may spawn, which is the check vanilla's nether portal uses. Leaf piles still tint their fallen leaves with the oak leaves overhead and still slow you down when you walk through a deep one.
- The breeze still turns windy when it rains and stormy when it thunders, a world's seed is still read from its own settings, and the debug helpers still write and read their bounding boxes under the same keys - all internal calls that 26.1 renamed, with nothing a player sees changed.
- Riding through a teleporter still brings the mount along: the mod's teleport helper asked for the server from the entity and moved things with two calls that 26.1 renamed or removed, so it now takes the server from the entity's level and snaps a position and both rotations the way the old call did. A rider is still put back on its mount at the offset it came off, so a horse and its rider arrive together just as before.
- The fog of the dark woods is back. Mirkwood still closes in to half its distance, Mirkwood, the spooky Mirkwood and Fangorn still call fog in at any height, the barrow downs and the old forest still do so at floor level, and the overworld's fog colour still dims with the daylight - all of it now through the hooks 26.1 kept for fog, since the API the mod used before was removed. One thing does not come back with it: the mod's overworld used to raise the clouds to y=128, and clouds are vanilla's again.
- Every food and drink still gives exactly what it gave before: 26.1 renamed some vanilla effects, so the mod's food table now names them by their new names, but lemas, honey cake, cram, monster flesh, the insects, the acorns, honey, bramble berries, miruvor and grog keep their nutrition, their saturation, when they can be eaten, whether they are bitten or drunk, and every effect with its chance, duration and strength.
- The molten mithril and molten morgul-iron keep their colour and their haze at the edge of vision: 26.1 moved a fluid's still, flowing and overlay sprites out of the client extensions and onto the fluid's own model, so each metal registers its sprites and tint through that model now, while the fog colour and fog range still come from the fluid type. No texture, tint or fog value changed.
- The six woods' leaves now drop a leaf now and then as they do in vanilla: 26.1 split the leaves block into tinted and untinted halves that carry their own falling-leaf particle, and each of the mod's - mallorn, mirkwood, culumalda, lebethron, fangornoak and dwarven maple - uses the particle it already had registered. The flowers keep the effects they gave: nothing about their regeneration, resistance, speed or anything else changed, only the internal names 26.1 gave those effects.
- The six village professions keep their names, their work sounds and every offer they had - the coin trader's four coins, the grocer's food and drinks, the pet merchant's insects and acorns, the smith's and tailor's trinkets with a potion already bound to them, and the junk trader's odds and ends. 26.1 moved villager offers out of code and into datapack registries, so those offers are now trade-set data the professions point at, and the trinket's potion is rolled by the mod's existing random-trinket loot function. The village template generator needed the new NBT reads for the same version, so it still derives the ready-to-use house, street and villager templates.
- The shared block base no longer carries a lore flag it could not draw: 26.1 removed the block-side tooltip hook, so the blocks that show a lore line - the sleeping bags and placards - get it from their own items, which is where the text already came from. Nothing a player sees changes.
- Placards still explain themselves in the inventory, and the camouflage fluid block reads the bucket it holds again: 26.1 moved a block's tooltip onto its item, so the placard's gold lore line comes from its own item now, and the fluid block asks for the container's fluid through the item access 26.1 expects.
- The fireplace, piggy bank and trinket table still drop what is inside them when broken, and a milestone still forgets its record when it is taken down: 26.1 removed the block hook that did all four, so each block entity now cleans up after itself as it is removed. A milestone in creative also opens its settings when shift-clicked, which the old server-side check could never see, and the trinket table's potion bottle and particle follow 26.1's item template.
- The camouflage chest keeps its contents on the way out: 26.1 drops a container's contents itself when the block goes, so the chest's old removal hook is gone and a broken chest still spills what was in it. Its comparator output, opening and closing sounds and the count of players looking inside work as before, so the chest compiles again.
- The backpack's placer no longer leaves its sleeping bag and campfire behind: 26.1 removed the block hook that used to clean them up, so the backpack block entity takes them back out as it is removed instead. The backpack's open key is on 26.1's key-category API too - it now shows under its own "Tolkien Tweaks" group in Controls exactly as before - and with it the whole backpack family compiles.
- The build is ready for Minecraft 26.1.2. Building from source now needs Java 25 and Gradle 9.2.1, and the project compiles against NeoForge 26.1.2.116, GeckoLib 5.5.2, TerraBlender 26.1.2-26.1.2.0.3, Curios 15.0.0 and JEI 29.47.0.111. The source itself is ported as well: `./gradlew compileJava` now finishes clean on Java 25. The generated data has been regenerated as well; `./gradlew runData` now finishes clean and the committed data is in the same format 26.1 expects. No jar has been built or tested in game yet.
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
- The keystone and the four keyed chests still click, buzz and crack the same way when a wrong key
  is turned in them. The break sound they play is one of the few sound events 26.1 still holds by
  reference rather than as a finished event, so it is unwrapped at the call; the noise itself is
  the same one at the same volume and pitch.
- A milestone still charges travel out of your inventory and only your inventory: the item payment
  reads the same 36 slots it always did, so what is in an armour or offhand slot is still not
  spent on a jump. Nothing about the cost, the counting or the order items leave the pack changed.
- Milestones are still remembered in the world, with the same entries in the same shape: position,
  the milestone's id, its dimension, its name, the two costs and which players know it. One thing
  moves - 26.1 names a world's saved data after the mod that owns it, so the list lives in
  `data/tolkienmobs/milestone_data.dat` instead of `data/milestone_data.dat`. A world carried over
  from 4.4.83 therefore starts with an empty milestone list; the old file is left on disk, not
  deleted or rewritten.
- The milestone screen is ported to 26.1's rendering, and it looks and behaves as it did: the same
  GUI, the same page buttons and counter, the same click-to-adjust numbers, and the same cost
  buttons beside each known milestone. Two small tidies come with it - the name field was being
  drawn twice over itself and now draws once, and holding shift or control still steps a number ten
  or sixty-four at a time. The buttons underneath it are ported with it - the mod's own button, the
  number stepper, the toggle and the greyscale button - and they draw and press as before, including
  a right click stepping a toggle backwards. The last two buttons, the plain icon button and the
  hover-textured one, are ported as well, which finishes the button set; neither is placed by a
  screen yet, so the only visible effect is that a hover-textured button would now actually show its
  hovered frame, which it never did before.
- The key and keystone screens are ported to 26.1's rendering too. Both look and behave as they did -
  the same panel, the same key-code field and the same keep-key, redstone and delay controls - and
  both now draw the code field once instead of twice over itself. Holding shift or control still
  steps the delay ten or sixty-four at a time, typing in the field still swallows the keyboard until
  you click away or press escape, and a right click still flips a toggle backwards. The key and
  keystone menus behind them are ported with them; nothing a player does in either screen changes.
  Every other menu in the mod is ported with them, so the backpack's locked slot, the coin pouch's
  and key ring's no-stacking guard and the chests all keep exactly the clicks they accepted before.
- A held backpack still locks the slot it is carried in, so it cannot be stashed inside itself, and
  it is still the same slot whether you carry it in your selected hotbar slot or in your offhand.
  Its crafting pane still marks results as player-crafted the way it did, and still fills its result
  slot from the first matching recipe. The backpack screen is ported with it: the same nine upgrade
  panels, the same fluid tank, and the same sleeping-bag, campfire and upgrade toggles, with a right
  click still stepping a toggle backwards. The tank's fluid level reads its colour and texture from
  the game's own fluid models now, and its tooltip appears at the pointer rather than offset from it.
- The camouflage spawner screen is ported too. Its delay and range steppers, the spawn-count and
  cluster controls, the player, requirement, particle and redstone switches and the spawn-all toggle
  all read and send what they did, and its mob preview still turns to follow the cursor - it now
  uses the game's own inventory entity drawing instead of the mod's copy of it.
- The fireplace and trinket table screens are ported with them, and both still show their crafting
  progress the way they did: the arrow fills across and the fire or trinket rises from the bottom of
  its frame. The fireplace still names the fuel left in a burning slot when you hover it.
- The four lockable chest screens - chest, double chest, treasure chest and double treasure chest -
  are ported as well. Each still takes a key code in its field, shows the hint when the field is
  empty, and saves the code when you press the button; the field is drawn once now instead of twice
  over itself.
- The piggy bank and the three item screens - coin pouch, key item and key ring - are ported too.
  They look and behave as before, and a right click over a pouch, ring or key item slot is still
  ignored, so a stack in one of them cannot be split or picked up. The camouflage chest and
  camouflage fluid screens are ported with them, and hovering the fluid screen's bucket slot still
  explains how the tank is filled. The backpack's upgrade panel finishes the set: every container
  screen in the mod is ported now, and each keeps its panels, slots, buttons and tooltips where they
  were.
- The entity renderers that do not go through GeckoLib are on 26.1's render pipeline: the seven wood
  boats and their chest versions, the boulder, the fell beast fireball, the Tolkien arrow and the
  Morgul crystal. Nothing about how they look changes - a boat still rocks and rows, a thrown boulder
  still spins in flight and settles at a fixed angle on landing, and the crystal still bobs, turns
  and shows its base only while it stands on one.
- The block entities that draw something of their own are on 26.1's pipeline as well: the trinket
  table's four floating items, the fireplace's two inputs and its output, the milestone's floating
  name, the lightning bug and the locust, and the camouflage spawner's spinning preview mob. They
  look and move as they did - the trinket items still turn where they sit, the fireplace still shows
  what it is burning, the milestone still shows its name to the players who know it, the bugs still
  drift and glow, and the spawner still turns the mob it will spawn. The preview mob is drawn
  through the same call the game uses for a mob in the world, so it appears exactly as that mob
  does; the placard, which draws nothing, is unchanged.
- The mod's own vertex pipeline is on 26.1 as well - the render state, the light matrix, the
  delegating and transforming vertex consumers and the vertex format cache under `handler/render`,
  together with the small vector helpers they read. This is the last of the rendering code written
  against the old API. None of it is called by the game, so nothing a player sees changes; it is
  noted because it is the end of the rendering port. One helper went with it: a fluid's colour was
  read from its fluid type, and 26.1 keeps that on the fluid's baked model instead, so the unused
  fluid-colour helper had nothing left to read.
- The mod's event listeners register themselves on 26.1's terms: the annotation no longer names a
  bus, so the fourteen classes that declared one - the client key handler, the trinket tint, the
  config, the server and client event sets, the balrog and player events and the rest - declare only
  their mod id and sides, and the loader decides the bus from each event the method listens to. No
  listener was dropped and none changed sides; the mod's hooks fire in the same places as before.
- The mod's particles are on 26.1's particle classes: the seven wood flames, the lightning bugs, the
  leaf falls, the fell beast's breath, the wind and the tinted smoke. Nothing they do changes - a
  flame still steps through its frames as it ages, a leaf still tumbles, drifts and settles, the
  breath still brightens once it lands, and the dyed smoke still takes the colour it was spawned
  with. A leaf's frame is picked from the frames that exist now rather than by mapping twelve
  invented indices onto them, so a leaf can show any of its sprites.
- The Watcher and its attack goal are ported, so the lake boss compiles with the rest of the boss
  family on its way. It fights exactly as it did: the same 300 health, 17 damage and armour, the
  same drowning or sleeping effect it inflicts on whoever it bites, the same regeneration and speed
  lines it says while you fight it, and the same blue notched health bar. It follows its swimming
  supertype into the fish package, its speed buff uses the effect's current name, its last attacker
  is read through a reference that may have been cleared, and its bar is named with an id - none of
  which the player can see or feel.
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
- The whole boss family is ported, so every boss in the mod compiles on 26.1. Each boss bar is now
  named with an id, which is the one thing 26.1's bar asks for that the mod had no answer to; the
  colours, the notched overlay, the names and the health they track are untouched, and a boss still
  shows its bar to a player the moment it is seen. Their anger, speed buffs and reinforcements work
  as they did: the golems keep the same twenty-to-forty-second grudge, and a goblin king or Shelob
  still call for help under hard difficulty. What players can notice: the fell beast now snaps its
  rider to its back and returns home by the renamed home calls, and a fireball's breath cloud still
  deals its damage - the effect it uses is spelled `instant_damage` now, same effect, same id. The
  balrog's magma blocks still refuse to be broken.
- The elemental golem, the tree ent and the Istari are ported with them. Their anger is kept the
  same way the golems' is - the same twenty-to-forty-second window, the same target, the same
  "who hit me" rule - and the tree ent still does not take kindly to being disturbed. The only
  differences a player could meet are the golem's slow, which is the same effect under its current
  name, and an angry mob that was saved before the update coming back calm, because the anger is
  stored in the shape the new game expects. The Istari's attack and heal still play once per turn.
- The swamp hag and Gollum finish the controller port: neither needs the old animation reset any
  more, because the animation they ask for is started on its own. The hag still picks the same
  potion - healing, then slowness, poison or weakness on the same conditions - and still throws a
  splash potion, only made with the class 26.1 splits splash potions onto. Gollum's swing still
  holds his idle pose, which is all it ever did.
- The monster family is ported, so every hostile mob compiles: the Mirkwood spider, the warg, the
  Romie walker and the shared monster base. Nothing about how they fight has changed - a spider
  still snatches one item out of your hand on the same cooldown and drops it at your feet with the
  same pickup delay, and it can still spawn with a skeleton rider in the same one-in-a-hundred case.
  A warg still occasionally turns up carrying an orc and still gets wet in rain or water, and the
  walker's halloween pumpkin still never drops when it dies.
- The boats are ported - all seven woods, each with a plain and a chest boat, and their items. The
  game moved the boat classes into a package of their own, which is the whole of the change; a boat
  still places and floats as it did, a chest boat still carries a chest, and each boat still
  remembers which wood it is made of when the world is saved and loaded.
- The three birds are ported - the thrush, the crebain and the great eagle - and the gecko's model
  with them. They behave as before: the eagle still hunts chickens, sheep and cows, the crebain
  still flocks and is still tempted by insects, and a bred thrush chick is still owned by whoever
  tamed its parent, even if that player is offline. The gecko still walks and idles with the same
  gait, because the model's two animations are baked once and then played as they were.
- The passive family is ported, so the auroch and the mumakil compile along with the base class
  they share. You hear no difference: an auroch still moos, hurts and dies with the cow's sounds
  and still clops on the same step, it can still be milked with the same sound, and the mumakil
  still borrows that step. Both still shy away from bees.
- The entity tree is complete: the cobweb projectile, the web trap and the dragon's flight
  pathfinder are ported, so every mob, boss, projectile, boat, model and renderer in the mod now
  compiles on 26.1. They behave as before - a web only catches spiders, it still floats up out of
  water, and the fell beast still refuses to fly through fire.
- The block model system is ported, so the moss and clover patches and the seven camouflage blocks
  compile. 26.1 no longer bakes a block model: a block collects its parts from a model that is
  handed its state, position and a random source, which is exactly what a patch needs to know how
  far to grow towards its neighbours, so both patches look as they did - the moss still has its
  shaggy edges and the clover still draws in the cutout pass. One thing moves: a patch's shape now
  comes from its blockstate file rather than a model file, so `moss_patch` and `clover_patch` name
  the patch model by type there instead. A camouflage block still wears whatever block it is
  sitting next to, and still shows itself while you hold the dev tool or a key.
- The item family is ported, so the item registry and every item class compile. 26.1 has no sword,
  pickaxe or armour class any more - the components an old `SwordItem` or `ArmorItem` used to add are
  now set by the item properties - so the mod's tools and armour are plain items that apply the same
  properties themselves. Nothing you hold or wear changes: the swords and pickaxes keep their damage
  and mining tags, the armour keeps its durability, toughness, equip sound and repair ingot, the
  dwarven hammer still breaks a plane and still mines like a pickaxe, and the horse armour still
  fits only horses and stacks to one. The enchantment glint on the Ammolite and named weapons, the
  gold lore line, and the regeneration, fire resistance and haste sets a full suit of armour grants
  are all unchanged, as is every tooltip's text. One rule now travels by a different road: a bow
  that never breaks still refuses Mending and Unbreaking, which 26.1 checks through the enchantment
  support check rather than a separate anvil-only method.
- The loot modifier, the loot function behind a trinket's potion and the two item model properties are
  ported, which closes out the item side of the source. The coin drops that ride along with a chest or
  a mob kill behave as before - same tables, same 25% weights - and the trinket still rolls its
  beneficial potion when the loot is dropped; the loot modifier simply gained the sort priority 26.1
  now asks every modifier for, left at the default so the drops arrive in the same order as they used
  to. The bow's draw progress and the coin pouch's and key ring's fullness, which pick the item
  model's frame, are read through the new owner-aware property signature and measure exactly what
  they measured before, so the sprites and frames are unchanged.
- The three loot table providers are ported, so the whole loot side of the source compiles. What
  drops and how much is unchanged: the same blocks drop themselves and their ores roll the same
  ranges, the same cooked food replaces a mob's meat when it dies on fire, and the backpack and
  sleeping bag still keep the contents and the name they had when they are broken. The generated
  loot tables do not move - the pieces that were renamed or reshaped in 26.1 write the same JSON
  they wrote before.
- The recipe datagen is ported, so every shape the mod's recipes are built from compiles again. What
  is made and how is unchanged: the same planks, stairs, walls, panes, dyes, ingots and cooking
  recipes are generated from the same ingredients and unlocked by the same items. The one visible
  difference is in the recipe book: 26.1 asks each cooking recipe which tab it belongs to instead of
  working it out from the result, so the helpers say so explicitly - stone goes to the blocks tab and
  ores to the misc tab, which is where the committed recipes already put them.
- The fireplace's alloy smelting and the trinket table's potion recipe are ported, which finishes the
  recipe family: both still read the same files and craft the same results. A fireplace still
  consumes its ingredients in any order and hands back the first output, and a trinket table still
  keeps the trinket's own name and state while it stamps the potion onto it. Their recipe files are
  unchanged, and the trinket table's recipe preview in JEI still shows the trinket as its output.
- The client registration class is ported, so the mod's renderers, model layers, block entity types
  and custom block-model loader register against 26.1 again. Signs still draw with vanilla's sign
  renderer and boats still bake their own models; nothing about how they look changes. One look is
  lost for now: 26.1 removed the per-dimension effects hook that gave the overworld shorter clouds
  and the mod's own fog colour, so vanilla's sky settings apply until the dark-forest fog is rebuilt
  on the fog events that replaced it.
- The mod's tree shapes are ported, so the branching trunks, the spheroid leaf blobs and the root
  features compile again. Every tree still looks the way it did: same branch counts and angles, same
  leaf radius and shag factor, same roots, and the same generated tree features. The only change is
  underneath - 26.1 hands these placers the world they are generating into, so a block placed from
  one can now be chosen with the terrain in view.
- The configured features are ported, so the world's declared features compile again. Trees, ores,
  blobs, roots and flowers are built from the same numbers as before. Flowers needed a new shape
  rather than a rename: 26.1 removed the random-patch feature the mod used, and moves that scatter to
  the placement instead, so each flower patch now counts its 96 tries and spreads them 7 by 3 blocks
  there. A patch still comes up as the same clump of flowers on grass, and still refuses stone, sand
  and water. The weighted list behind the roots a tree blends into its base is 26.1's `WeightedList`
  now, so those roots still come up six parts living root to one part dirt.
- The particle description data provider is ported, so the datagen side of the particles compiles.
  Every flame, the lightning bugs, the fell beast's breath, the wind and the leaf particles still
  declare the same single sprite they did, and the tinted smoke still borrows vanilla's twelve
  campfire smoke frames, so the generated particle descriptions are byte for byte what they were.
- The biome layer is ported, so all twenty biomes register against 26.1 again and every biome keeps
  the colours, particles, ambience and music it had. 26.1 moved a biome's fog and sky colours, its
  ambient particles and sounds and its background music out of the biome's effects and onto the
  biome's environment attributes, so they are declared there now - the same values, in the same
  biomes, including the ash falling over Mordor and the mallorn leaves of Lothlórien. The cave
  carvers are ported with them and still line their walls with dirt or dark stone, and the two
  jigsaw structures still stop building at the same distance from where they start. The generated
  biome files do not match this yet: they are rewritten by the next data run, which has to wait for
  the rest of the source to compile.
- The backpack and the camouflage chest hand their inventories out through 26.1's transfer API, so
  hoppers, pipes and other mods see the same slots they did. The pack's storage and tank and the camo
  chest's twenty-seven slots are still the containers they were, and they still answer the older item
  and fluid interfaces the menus are built on. Buckets - of water, lava or any mod's fluid - and the
  pack's own two bucket slots now fill and empty through 26.1's item access, which is what replaces
  the old container-item calls: a bucket still swaps for an empty one, in your hand or in the slot it
  sits in. A backpack saved by the 1.21.4 build keeps its contents, because both storages still read
  the tag keys they used to write. The two fluids no longer declare a translucent render layer
  themselves - 26.1 picks it from the fluid's own textures - so make sure mithril and morgul iron
  still look right in a world.

### Fixed

- Breaking a placed backpack in creative gives the backpack back, contents and all. The block builds its drop from a stack of itself, and the block has no block item - its item is registered on its own so it can place the pack - so that stack came out empty and the drop evaporated, taking whatever the pack held with it. The drop now names the item outright, which is what the loot table already does in survival, and a broken pack no longer waits for something inside it before it drops anything.
- A fireplace no longer loses the food it could not hand back. Its "is the output full" test read slot 0 of the *input* handler, so it answered for the raw food being cooked rather than for the cooked food already waiting to be taken, and the result itself was only checked loosely - by item, and against a hard-coded 64 - while the actual placement could still come up empty after the ingredients had already been taken, leaving a line on stderr and nothing else. The check and the write now come from one plan: the output slots are simulated first, two results share a slot only when both fit there, and nothing leaves the inputs until every result has a slot waiting for it. A result that carries components also no longer overwrites an existing stack of the same item, so a named dish keeps its name instead of taking the name of whatever lands beside it.
- The key ring stores one key per slot. It merged keys against a fixed limit of 64 while every key stacks to one and its slot holds one, so a ring could be handed a count its slot cannot hold; the limit is the key's own stack size now.
- The backpack's tank keeps its contents between empty and full, whatever it is handed. A negative amount - half a written menu value, say - was passed straight to 26.1's handler, which refuses one outright instead of clipping it, so a bad write would have thrown inside the block entity's own tick; an amount over the tank's capacity was written through as well. Emptying the tank goes through the same clamp, and the amount setter no longer writes `Integer.MAX_VALUE` into a tank it believes to be over 128000 mB, a number the tank can never reach.
- The mod's fog is back in the biomes that asked for it. The ported fog handler halved a far plane that 26.1 defaults to 1024 blocks rather than to the view distance, so halving it still left the fog past the horizon: the woods, the barrow downs and Forochel looked as clear as a plain. Fog now comes from each biome's own fog start and end distances, which is how 26.1 asks for it - the Misty Mountains close to 38 blocks in a pale mist, Fangorn to 80 in the grey of old woodsmoke, the Old Forest and the two Mirkwoods to 96, the barrow downs to 64, and Forochel and its slopes to a snow-blind white at 96 - and the sky and cloud ends follow the same distance, so a mist closes overhead and not just along the ground. The handler, its smoothing and its daylight dimming are gone, since the biome attributes replace all three.
- The Misty Mountains no longer shed dwarven maple leaves. The biome's ambient particles are removed, so snow is the only thing that falls there.
- Trees in Fangorn and the Old Forest no longer stand on a floating disc of podzol. 26.1's own ground decorator scans upward from the lowest trunk block and writes whatever state its provider hands back, and a plain provider always hands one back, so the carpet landed a block above the grass with every tree sitting on it. The mod brings its own decorator, which scans down instead and replaces only the highest solid block of a column, and only when that block is soil: grass, a flower or a snow layer above the soil is stepped over, while a trunk log or a leaf stops it untouched.
- Mordor's rubble, rock piles, stone spikes and dead trees generate again, and a rock pile can be placed by hand there. All of them asked for grass - the rubble and the piles wanted grass under or beside them, the spikes wanted grass at the foot, and the dead trees went through a filter that only lets a tree stand where its sapling would survive - a test dark stone fails. They now accept bare rock as ground, brambles take root in it too, and the dead trees skip the sapling check and stand with no grass patch beneath them.
- All 55 spawn eggs show a sprite again instead of the missing-model checkerboard. 26.1 dropped the two-layer egg template and the pair of tints that went with it - vanilla now ships one flat image per mob and each egg's item definition points straight at it - so every egg was naming a model the game no longer has. The images are painted during data generation from art the mod already has, so they need no new assets: each egg is the shape a spawn egg is known for, filled with that mob's own 16x16 entity icon, which is how vanilla's eggs show a mob's face. The icon is pulled most of the way towards the colour the egg registered with while keeping its own light and dark, so the face still reads and the eggs still fall into the groups the old tinted eggs did - the horde, the ring-bearers, the beasts, the little things, the free peoples and the bosses each share a hue - and the rim of the egg is that colour outright. Shading is worked out from the egg itself, a dark rim and a body lit at the crown and shaded at the base. The two horses have no icon of their own, so their eggs get a face drawn from a plain set of shapes instead. A new egg still needs only its one line in the entity registration, and any single egg can later be replaced by hand-drawn art without touching the generator.
- The frozen villages of Forochel build their houses. A village house is joined to its street through a doorway marker that sits one block outside its front wall, and the frozen house - six blocks deeper than the human house whose numbers it borrows - had inherited the human's marker position, which fell well inside its own walls. A marker that deep can never find a street without growing into the house it belongs to, so the house was chosen and then dropped, leaving the town centre standing alone among houses that never appeared. It now carries its marker on its own deep front, one block past its porch, with the village's path block beneath it.
- The four lantern families and the two watcher statues bake again, so their blocks and items show their own models instead of the missing-model placeholder. 26.1 decides a face's translucency by sampling the piece of the texture that face's UVs cover, and refuses the whole model when that rectangle falls outside the image - "Cannot compute translucency out of bounds" and an "Unable to bake model" line for every one of its blockstates. The lanterns' glass box had a single face UV'd to 17 instead of 16, and the watchers' wing planks reached 21.5 on a texture that is only sixteen units wide, so both have been brought inside 0..16. A block model's UVs are always in that range whatever `texture_size` declares; the watchers were authored against a 32-unit grid, which the game does not scale.
- The fallen-leaf piles generate in all six woods again - mallorn, mirkwood, culumalda, lebethron, fangorn oak and dwarven maple - and they lie on the ground they belong to. Two things kept them away: the modifier that keeps decoration clear of structures answered with no positions at all, so every pile's placed feature was left with nothing to place, and the piles then took their height from a heightmap that counts leaves, which in a wood answers with the top of the canopy, so the soil test under it failed. The modifier now passes every position through, since the landmark system it consulted is no longer in the mod, and a pile finds its own floor by walking down its column to the first cell with soil - or water - beneath it, so it lies in a clearing, on open ground and under a tree alike.
- Mordor's stone spikes stand up out of the ground instead of hiding inside it. The spike wrote only into cells that already held rock or soil, which is every cell a spike reaching above the surface never touches, so each attempt spent itself replacing nothing. It now finds the ground under the plants and snow of its column and builds upward through the air from there, out of dark stone, in the same shapes as before - a low lump most times, and once in sixty a narrow needle twenty to forty blocks tall - stopping at anything that has a shape of its own.
- The lanterns' glass box no longer has a missing texture on its top and bottom. Those two faces named a texture variable the model never declared, so the game fell back to the magenta-and-black checkerboard the moment you looked down on a lantern; they now sample the same glass as the four sides, in all three lantern families - elven, morgul and silmaril - and in their item models too.
- The backpack opens without closing the world, and its block draws its own texture again. 26.1's menu slot casts its handler to a writable item handler the first time a screen fills its slots from the server, and the mod's slot list only spoke the read-and-insert form, so opening a backpack threw a `ClassCastException` before a single slot was drawn; the slot list now answers that write itself. The block was also pointing at its texture in the item atlas, which a block model cannot sample - the game rejected the model outright and left the placed backpack invisible - so the texture now lives in the block folder with the rest of the block textures, at the same 32x32 size the model was drawn against.
- A dedicated server starts again. The mod is loaded on a server before any world is, and two classes the server must load named client-only types outright: the leaf pile asked the client's block-colour list for the oak leaf tint, and the falling-leaf packet did the same while drawing, so block registration and then packet registration stopped with `NoClassDefFoundError: net/minecraft/client/renderer/block/BlockAndTintGetter` and the run ended in "Mod loading has failed". The tint now sits behind a hook the client fills in during its own setup - a server simply answers "no tint" - and the packet hands its drawing to a client-only class of its own, so neither class the server loads mentions a client type. A server now starts, prepares a spawn area, generates and saves a world, and logs no error; the client still drops a pile's leaves in the colour of the oak overhead.
- A launch no longer buries the console in the same deprecation notices. 26.1 marks NeoForge's item and fluid handler API for removal in favour of its transfer API, and the mod's menus and block entities still speak the old one, so every compile printed a removal warning for each use - about a hundred lines before the game had even started. The build now leaves the deprecation and removal lint categories off, and every other warning stays on. The four `Note:` lines that remain are javac's own summary of those categories and only go away when the handlers are ported.
- Mordor's stone spikes stand on all of the biome's rock, and stand in its lakes too. Only plain dark stone counted as ground, yet Mordor's and the Ash Mountains' surface rules also finish columns on cobbled dark stone and smooth dark stone wherever their stone noise says so, so those columns could never carry one; all three are ground now. A spike also fills the water or lava it passes through, so its foot plugs into the rock under a lake and the spire rises out of it. Before, the fluid cells were skipped and the stone only appeared above the lake, hanging in mid-air, which is why the biome's lowlands - where the lakes are - carried none at all.
- The seven trinkets wear in Curios, and its window lists the slots they go in. Curios defines its slot types but grants none of them to a player on its own - a slot only exists on an entity when a mod's own `curios/entities` data says so - and the mod granted exactly one, the backpack's `back`, which is why the backpack had a home while the trinkets had nowhere to go and the window showed a single slot. Players now carry the head, necklace, body, hands, belt, charm and ring slots. The amulet was also tagged into `curios:amulet`, a slot no version of Curios defines, so an amulet was valid nowhere; its tag is now `curios:necklace`, which is what Curios calls that slot. The trinkets keep their ids, names, potions and effects.
- A fluid tank's level now rises from the floor of the gauge. Each sixteen-pixel row of fluid was placed at the top of its own tile rather than at its bottom, so a part-filled row floated up to fifteen pixels above the tank's base and left a gap under it - a backpack's 3,000 mB in 128,000 drew its one-pixel sliver near the middle of the bottom tile instead of sitting on the bottom of the gauge. Rows are now placed by their bottom edge and take the bottom of the fluid sprite, so the level touches the floor and the rows meet without a seam.
- A fireplace no longer closes the world. It took a piece of fuel by writing back one item fewer than it had read, and an empty slot reads as a count of zero, so the very first tick of a fireplace with nothing in its fuel slot asked the slot for minus one item and the game stopped with `Expected value to be non-negative: -1`. The fuel slot is now only written when it holds something that actually burns, and a single piece of fuel empties the slot cleanly. Fuels still add their burn time and are used one at a time as before.
- The camo keystone offers all three of its redstone modes again, and the numbers that go with them. A left click on the mode button landed on 26.1's empty click hook, so the press callback ran without the face moving and the keystone only ever showed the mode it was already in - the lever, the button and the pressure plate were reachable by right-click alone and looked like missing options. A left click now steps the button on, and every toggle in the mod's screens steps with it. The screen also opens on the mode the keystone is actually set to instead of always assuming the lever.
- The trinket table crafts trinkets again. 26.1 asks a slot whether it accepts an item before it will report how much room that slot has, and the table's output slot answered no to everything - it only ever changes through a direct write, and its menu slot refuses placement on its own - so the table judged its own output full and never finished a recipe. Repairing was unaffected, which is why the table still mended tools. A trinket, a potion and an ammolite now come out of it, the potion leaves an empty bottle behind, and the trinket keeps its potion's effect.
- An active key ring picks up the keys lying in your inventory. It skipped every key that carried components, which is every key that has been given a code, so it never collected one; it now stows any key of the mod's key tag. The key in either hand is left alone, because a keystone and a lockable chest only read the stack in hand.
- The placard's item names itself and shows its lore line. Its item is its own registry entry rather than a plain block item, so it looks its name up under an item key the language file never had, and the same for the gold line about cycling the sign's type; both keys are written now, in English and Chinese.
- The Ring of Aglaral sits in the quest tab instead of the tools tab, beside the keys it belongs with.
- A backpack's magnet gathers without its screen being open. The scan only ever looked for a bag the player was wearing or carrying, so a backpack standing on the ground had nothing to fill and gave up on every pass, and a worn bag without a magnet could be chosen ahead of the one in hand and stop the scan at its zero reach. It now reads both hands as well as the inventory, steps past a bag that has no magnet fitted, and falls back to the nearest placed backpack whose own reach covers the player - only chunks that are already loaded are looked through, so nothing is pulled into memory. The bag the key and the button flip is the same one the scan fills.
- A carried backpack shows its sleeping bag and campfire buttons again, and pressing one puts the bed or the fire in front of the player. The model is a placed block and a worn one has no front, so both buttons had been hidden and the upgrades looked missing; a bag in hand now deploys ahead of its owner, and a placed bag still deploys ahead of itself.
- The backpack's screen opens with its upgrade window closed, and the magnet's button sits in the toggle row between the campfire button and the upgrade button instead of hanging over the right edge of the panel. All four buttons move a little to make the row fit inside the panel.
- A camouflage spawner starts the way a vanilla spawner behaves: a player within sixteen blocks wakes it, it respects the mob's own spawn requirements rather than ignoring them, it does not wait for redstone, and it shows its flames. The detect range was four blocks and both of those first two settings were on, so a fresh spawner kept to itself and then spawned where nothing should have. The particle, player, redstone and spawn-as-group buttons also read backwards - each showed the opposite of the setting it toggled - and the redstone and spawn-group choices were never written to the save at all, so both came back at their defaults. A spawner read from an older save now falls back to those same vanilla numbers instead of zero ranges and zero delays.

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
