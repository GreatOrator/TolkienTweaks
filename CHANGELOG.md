# Tolkien Tweaks — Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added

- Added the Blue Mountains, a temperate mountain biome shaped like the Iron Hills but with the mod's mountain stone where vanilla stone would be, and with teal grass and water (#59B6BD).
- Mountain stone counts as stone for ore generation, so the Blue Mountains' underground carries the same ores as any other biome's.
- The Blue Mountains grow dwarven maples, and dwarven villages now start there as well as in the Iron Hills.
- Mountain stone also forms andesite-like veins through the Blue Mountains' stone, so the rock still shows in worlds that never apply the biome's surface rules, such as a single-biome world.
- Human NPCs spawned in a cold climate, such as Forochel, wear the winter clothing textures; humans spawned anywhere warmer keep their normal clothes. The outfit is chosen once, from the biome they spawned in, so it does not change if they later travel.
- Camouflage Smokers can be dyed: right-clicking with a dye tints the smoke a third of the way to that colour, another click with the same dye takes it to two thirds, and a third to the full colour, after which the next click returns it to plain smoke. A different dye starts again at a third. The dye is not used up.

### Changed

- Blockstates and item models are written by the mod's own generator, since the NeoForge model generators this datagen relied on no longer exist; the generated JSON keeps the same shape.
- Armour sets are now described the way the game expects: each of Mithril, Morguliron and Ammolite has an equipment asset that names its worn texture, so the sets render on players and horses from one definition and the full-set effects are still recognised.
- Spawn eggs are rebuilt on the vanilla item with their two colours moved into the item model, and every mob still registers under its own entity id (the chest boats no longer share the plain boat's id).
- Food and drink effects are carried by the consumable component, so drinks still tip the bottle back and foods still keep their bonuses.
- Tag, sound, particle and village-template datagen no longer asks for the file helper the modding API dropped.
- Recipe datagen builds its recipes through the game's recipe-provider instance, so crafted, smelted and stonecut items keep the recipe ids and unlock conditions they had before.
- Fireplace and trinket table recipes are built on the game's current recipe definitions: each ingredient now carries its own nested entry with its count, so the fireplace still smelts bread from three wheat, the trinket table still shows its fuel in JEI, and both still unlock on the same conditions.
- Potions are registered with the name the game now requires on them, so potions, splash potions and lingering potions keep the names they are shown with.
- Blocks and items are registered with the ids the game now reads while it builds them, so every block keeps its loot table and its name, block items show the name of their block, and items keep their inventory model.
- The clover and moss patches are drawn through the game's own model loader again, and camouflage blocks still take on the look of the block next to them.
- Block entity types are built directly, and the wood wall torches name the floor torch's loot table themselves, so torches still drop the way they did before.
- Creature renderers and models now use the per-frame render state the game switched to, so mobs, boats and thrown projectiles draw the same way they did before.
- Human NPC winter clothing now follows the `tolkienmobs:climate/cold` biome tag instead of a hard-coded temperature, so a datapack can decide which biomes dress humans for the cold. Iron Hills, Barrow Downs and Hithaeglir count as cold alongside Forochel.
- Mobs now deal and take damage through the game's server-side damage hook, so every boss reaction still fires: Shelob still calls her spiders, the golems still apply their elemental effects, and the fell beast still puts a shield on cooldown. Mobs the bosses summon arrive as reinforcements or breeding results rather than anonymous spawns.
- Block entities work on the game's current APIs: a camouflage spawner still reads back the mobs stored in it and shows the chosen one turning above the block, a fireplace still accepts only fuel and takes its burn times from the game's fuel values, trinket tables and fireplaces still find their recipes, the lockable chests still name the key you open them with, and a milestone still charges the item configured as payment.
- Every container screen draws through the game's current widget pipeline: the chests, fireplace, trinket table, backpack, milestone, well, pouch and key screens keep their panels, progress arrows and fluid gauges, the camouflage spawner still turns its chosen mob on the mouse, and the greyed-out buttons still dim when they are switched off.
- Blocks answer the game's current neighbour-change hook, so waterlogged blocks still refill when a neighbour changes, wall torches, lanterns, wall mushrooms, decay blooms, patches and the Arda portal still break when their support is removed, dirt paths still revert to cobble, and a sleeping bag's two halves still link up and still bounce you when you land on them.
- The lightning bug and locust displays draw their parts through the renderer, so the bug body still draws cutout and the lightning bug's glow still overlays it as a separate translucent pass.

### Fixed

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
