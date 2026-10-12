# Tolkien Tweaks — Consolidated Changelog

- **Period:** October 9–12, 2026, inclusive, using UTC commit dates.
- **Version:** 4.1.776 → **4.1.808**
- **Repository:** [GreatOrator/TolkienTweaks](https://github.com/GreatOrator/TolkienTweaks)
- **Main snapshot:** [c70a6578](https://github.com/GreatOrator/TolkienTweaks/commit/c70a6578e734b4b0ca9e35a66e115c386b757289)

This changelog consolidates the repository changes during this period, including the work completed in this task. Repeated fixes, generated-file refreshes, and merges are grouped by feature. No commits dated October 9 were found in the examined history; the recorded changes begin October 10. The latest Chinese provider changes are present in the main snapshot above.

## Backpacks

- Added the ability to open a backpack directly from the player's hand. Sneak and right-click a block to place it.
- Added Curios back-slot support and an **Open Backpack** key binding, defaulting to **B**.
- Made backpack contents travel with the item, including inventory, installed upgrades, settings, and stored fluid. Backpack drops now copy the contents component from the block entity.
- Updated menus and screens dynamically when upgrades change, including available inventory slots, upgrade controls, fluid capacity, and fluid display.
- Improved synchronization for backpacks opened from the hand or Curios slot, including bucket processing and saving changes to the backpack item.
- Corrected sleeping-bag and campfire buttons so each deploys the intended object, and improved switching between the two.
- Added placement-space feedback when a sleeping bag or campfire cannot be deployed.
- Removed the superseded backpack upgrade update packets and adjusted the remaining placement/settings packet handling.

## Camouflage spawners and blocks

- Expanded camouflage spawners to store up to **six mob types**, with a spawn count for each type.
- Added **random selection** and **spawn as a group** modes.
- Added controls to remove an individual mob or clear the configured mob list.
- Added network updates for individual mob counts, clearing entries, and the new spawn mode, along with saved mob-count data.
- Updated the spawner screen with mob selection, individual count controls, new icons, and clearer mode labels.
- Corrected the displayed entity preview when selecting a mob.
- Adjusted control visibility for single-mob versus multiple-mob configurations.
- Fixed initialization and comparison of spawner settings to include the new mode.
- Prevented camouflage models from recursively copying another chameleon block's model.
- Adjusted camouflage selection so a lone adjacent grass block uses the block's default state instead of being copied.

## Trinket table crafting

- Moved potion infusion from vanilla crafting to the **Trinket Table**.
- Added table recipes for all seven trinkets: **ring, amulet, charm, belt, glove, hat, and cloak**.
- Implemented dedicated trinket, potion, and ammolite input handling, with the infused trinket delivered to the output slot.
- Infusion preserves the trinket type and its unrelated item data while replacing its potion effect.
- Added correct ingredient consumption, potion-bottle return, output-space checks, and handling for ingredients changed during processing.
- Reworked recipe input, serialization, recipe building, and automatic recipe generation through `TolkienRecipeProvider`.
- Updated table menu, renderer, recipe display support, and screen positioning.

## Overworld biomes

- Integrated all **16 Tolkien biomes** into normal Overworld generation through **TerraBlender**.
- Added biome-region mapping and surface rules for the custom biomes.
- Connected biome and feature registration to the data-generation registry pipeline.
- Fixed inconsistent feature ordering and duplicate feature entries that could cause generation cycles when custom and vanilla biomes meet.
- Updated biome classification tags and handling of optional category references.

## Structures

### Registration and generation

- Added an editable structure definition system using `TolkienStructures`, `TolkienStructureProvider`, and `SurfaceStructures`.
- Added automatic generation of configured structures, template pools, placement sets, and biome tags.
- Added `TemplateLayoutStructure` for local terrain alignment, multiple-building layouts, roof-depth offsets, and companion structures.
- Added natural placement of the existing hobbit-house template in the Shire.
- Retained the old `shire_ruin` structure identifier for saved-chunk compatibility, without its own natural placement schedule.

### Cave and maze branches

- Configured `cave.nbt` and `maze.nbt` as single main pieces, with only their corresponding `_side` templates eligible for branching.
- Added randomized branch continuation and stopping, bounded by expansion depth and distance.
- Repaired NBT jigsaw connector references and updated obsolete copper-ore references in the cave and Gollum-cave templates.
- Placed the cave beneath **Mirkwood** and the maze beneath **Hithaeglir**, with configured start heights from **Y −32 to 0**.
- Added surface-biome eligibility checks so underground cave biomes do not override the intended biome restriction.

### Structure placement catalogue

| Structure/template | Biome | Placement |
|---|---|---|
| `ttmhouse_hobbit` | Shire | Ground level; registered as `hobbit_house` |
| `cave` and `cave_side` | Mirkwood | Underground, one main piece with randomized side branches |
| `maze` and `maze_side` | Hithaeglir | Underground, one main piece with randomized side branches |
| `ttmbarrow` | Barrow Downs | Ground level |
| `ttmdark_tower` | Mordor | Ground level, with a nearby warg pit |
| `ttmgollum_cave` | Hithaeglir | Roof approximately 32 blocks below the local surface |
| `ttmhouse_desert` | Haradwaith | Ground-level clusters of 3–5 houses |
| `ttmhouse_dwarf` | Iron Hills | Ground-level clusters of 3–5 houses |
| `ttmhouse_elven` | Lorinand / Lothlorien | Ground-level clusters of 3–5 houses |
| `ttmhouse_human` | Gladden Fields | Ground-level clusters of 3–5 houses |
| `ttminn_desert` | Haradwaith | Sparse ground-level placement |
| `ttmruin_large` and `ttmruin_small` | Dagorlad | Ground level, sharing one sparse placement grid |
| `ttmspidertree` | Mirkwood Spider Forest | Sparse ground-level placement |
| `ttmswamp_hag_hut` | Marshes | Sparse ground-level placement |
| `ttmwargpit` | Mordor | Tower companion, mostly buried with its top two layers above the local surface |

- House clusters use **5–10 blocks of clearance between building edges**, random rotations, and a local terrain/biome check for each building.
- Inns, spider trees, and swamp huts use a **12-chunk spacing / 6-chunk separation** configuration.
- Both ruin sizes share a **4-chunk spacing / 3-chunk separation** grid, limiting placement to one ruin candidate per 4×4-chunk region.
- Warg pits generate naturally with dark towers rather than from an independent placement set.
- Ground and roof alignment use each structure's footprint center; exposure at the edges can vary on slopes.

## Localization and generated data

- Rebuilt the Simplified Chinese provider from the current US entry list.
- Localized **594 second-field values**, including names, interface labels, messages, tooltips, stone/gem/potion names, and descriptions. Added missing spawner labels and the Old Forest biome name.
- Translated **104 nonempty lore, secondary-lore, and instruction values**, preserving five intentionally empty fields.
- Corrected misleading translations, including remaining key uses, entering/changing key codes, and portal activation instructions.
- Preserved translation keys, format placeholders, color/style codes, and numeric values.
- Added or refreshed English and Chinese language resources. Main includes a regenerated `zh_cn.json`; the task's translation commits themselves changed provider source only.
- Refreshed generated biome tags, backpack loot data, trinket recipes/advancements, structure resources, and entity loot tables. Entity loot refreshes include changes to the potion IDs selected by the random-trinket loot function.

## Build and release preparation

- Bumped the mod version from **4.1.776 to 4.1.808**.
- Updated Curios from **9.0.13 to 9.2.0** and switched its dependency repository to `maven.theillusivec4.top`.
- Added **TerraBlender 1.21.1-4.1.0.8** as a declared runtime dependency.
- Updated CI to use **JDK 21**.
- Updated Gradle property assignment syntax to address deprecation warnings.
- Removed completed TODO entries and later removed the temporary trinket, biome, and structure documentation files.
- Added regression checks during development for trinkets, biome generation, structures, and layouts. The older trinket/biome/structure GameTest classes were later removed; the catalogue GameTests remain. Their shared `trinket_table_empty.nbt` fixture was subsequently removed from main, so those tests currently require that fixture to run.

## Reverted work

The proposed removal of 48 Java files was **fully reverted**. Those classes and the associated JEI comments were restored. The class-removal proposal is not part of the final feature changes in this changelog.

## Source references

The entries above were checked against commit diffs and the final main snapshot, rather than commit titles alone.

| Area | Representative commits |
|---|---|
| Backpack refresh and portability | [b95167ad](https://github.com/GreatOrator/TolkienTweaks/commit/b95167ad), [710f48b6](https://github.com/GreatOrator/TolkienTweaks/commit/710f48b6), [db07f603](https://github.com/GreatOrator/TolkienTweaks/commit/db07f603), [1dfbc717](https://github.com/GreatOrator/TolkienTweaks/commit/1dfbc717) |
| Spawners and camouflage | [09911d4e](https://github.com/GreatOrator/TolkienTweaks/commit/09911d4e), [806017c1](https://github.com/GreatOrator/TolkienTweaks/commit/806017c1) |
| Trinket table | [e1c66084](https://github.com/GreatOrator/TolkienTweaks/commit/e1c66084), [579a2d37](https://github.com/GreatOrator/TolkienTweaks/commit/579a2d37), [034da8f1](https://github.com/GreatOrator/TolkienTweaks/commit/034da8f1) |
| Overworld biomes | [3fb19f26](https://github.com/GreatOrator/TolkienTweaks/commit/3fb19f26) |
| Structure system and catalogue | [a86a8321](https://github.com/GreatOrator/TolkienTweaks/commit/a86a8321), [906cab4c](https://github.com/GreatOrator/TolkienTweaks/commit/906cab4c), [709a29c5](https://github.com/GreatOrator/TolkienTweaks/commit/709a29c5), [c0d83e3d](https://github.com/GreatOrator/TolkienTweaks/commit/c0d83e3d), [cd569554](https://github.com/GreatOrator/TolkienTweaks/commit/cd569554) |
| Chinese localization | [718d32ac](https://github.com/GreatOrator/TolkienTweaks/commit/718d32ac), [8e6e3531](https://github.com/GreatOrator/TolkienTweaks/commit/8e6e3531), [0c7005b7](https://github.com/GreatOrator/TolkienTweaks/commit/0c7005b7) |
| Build and version | [fe836646](https://github.com/GreatOrator/TolkienTweaks/commit/fe836646), [aa7fc500](https://github.com/GreatOrator/TolkienTweaks/commit/aa7fc500), [c70a6578](https://github.com/GreatOrator/TolkienTweaks/commit/c70a6578) |
| Cleanup reversal | [76f49db4](https://github.com/GreatOrator/TolkienTweaks/commit/76f49db4) |
