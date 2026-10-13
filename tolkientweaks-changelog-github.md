# Tolkien Tweaks — Changelog

## October 12, 2026

Changes committed or merged into `main` on October 12 (UTC−07:00).

### Villages

- Converted desert, dwarf, elven, hobbit, and human houses into villages with connected paths and driveways, wells or fountains, bells, farms, decorations, animals, and villagers. Existing Tolkien inhabitants are retained.
- Villages generate in Haradwaith, Iron Hills, Lorinand, Shire, and Gladden Fields respectively, using one shared placement grid instead of independent house clusters.
- Added village pool, template, and structure-tag datagen, preserving original house templates and saved structure IDs. Added [village configuration and editing instructions](VILLAGES.md).

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

### Development and upgrade notes

- Added regression GameTests and fixtures for flight, mining, and villages, with namespace selection in development run configurations.
- Run `gradlew runData`, then rebuild to generate the new village resources. Villages appear in newly generated chunks.

**Source:** [main changes for the day](https://github.com/GreatOrator/TolkienTweaks/compare/85765e359126da5fa7fd5685ec7ac53707b7e467...3f4f16618f9c772b6856778dc0cb190ef2359e88), including [#17](https://github.com/GreatOrator/TolkienTweaks/pull/17), [#18](https://github.com/GreatOrator/TolkienTweaks/pull/18), and [#19](https://github.com/GreatOrator/TolkienTweaks/pull/19).
