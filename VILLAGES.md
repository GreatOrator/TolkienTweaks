# Tolkien villages

The six house structure IDs now start villages using Minecraft's jigsaw village generator. Each village has Tolkien houses, paths with short driveways, a well or fountain with a bell, farms, decorations, animals, and the matching Tolkien NPC as its residents instead of vanilla villagers. Existing Tolkien inhabitants saved in the houses are retained.

| House style | Biome | Paths, farms, and decorations |
| --- | --- | --- |
| Desert | Haradwaith | Vanilla desert village |
| Dwarf | Iron Hills | Vanilla plains village |
| Elven | Lorinand | Vanilla plains village |
| Hobbit | Shire | Vanilla plains village |
| Human | Gladden Fields | Vanilla plains village |
| Orc | Mordor | Vanilla desert village |

Each style's residents are the mod's own villager-style NPC, chosen by the `resident` field of its `Village` entry: desert/Haradwaith uses Southron, dwarf uses Dwarf, elven uses Elves, hobbit uses Hobbit, human uses Human, and orc uses Orc. Wandering trader variants are never used. Datagen copies vanilla's three villager templates (`nitwit`, `baby`, `unemployed`, weights 1/1/10) once per style into `tolkienmobs:village/<style>/villagers/` and swaps the stored entity for that NPC. Every jigsaw socket that asks vanilla for a villager - your houses' resident connectors as well as vanilla town centers, farms and decorations - is redirected there through a pool alias.

## Generate the resources after merging

Run with Java 21:

```sh
./gradlew runData
./gradlew build
```

On Windows, use `gradlew.bat`. The village code needs the resulting JSON and NBT resources before it can generate villages. Datagen replaces the old standalone house placement sets with one `tolkienmobs:villages` set and writes prepared house and road templates under `src/generated/resources/data/tolkienmobs/structure/village/`. Its normal stale-output cleanup removes the old house structure-set JSONs.

Villages appear in newly generated chunks. Existing buildings in explored chunks stay where they are. Original structure IDs are retained so saved structure starts remain readable.

## Change biomes and village frequency

- `TolkienStructureProvider.STRUCTURES` lists the allowed biomes for each house/village ID. These also generate the biome tags.
- `TolkienVillageProvider.VILLAGES_BY_STYLE` chooses each village's house, vanilla infrastructure style, and the `resident` NPC that replaces vanilla villagers.
- `TolkienVillageProvider.VILLAGERS` lists the vanilla villager templates copied for every style; keep the names and the weights in `bootstrapPools` in step with vanilla.
- `TolkienVillageProvider.bootstrapSet` controls the shared village placement grid: spacing **32 chunks**, separation **8 chunks**. This matches vanilla village frequency settings. These settings describe candidate start chunks; biome suitability still controls whether a village generates.
- `TolkienVillageProvider.SIZE` controls jigsaw expansion depth; `MAX_DISTANCE` limits expansion from the center. Increasing distance must also respect Minecraft's structure size limit, including terrain adjustment padding.
- `bootstrapPools` controls the house/farm weights and town-center template. Plains styles use a fountain; desert uses a well.

All six styles share a placement set, so only one style can start at a given candidate location. Their original independent house clusters no longer generate after datagen.

## Edit the houses

Keep editing the original templates in `src/main/resources/data/tolkienmobs/structure/`:

- `ttmhouse_desert.nbt`
- `ttmhouse_dwarf.nbt`
- `ttmhouse_elven.nbt`
- `ttmhouse_hobbit.nbt`
- `ttmhouse_human.nbt`
- `ttmhouse_orc.nbt`

Then run datagen again. `TolkienVillageTemplateProvider` updates the older NBT format and creates village copies, adding an entrance porch and a resident connector, plus the three resident templates each style needs. It also clears captured bed occupancy flags. Blocks and saved inhabitants inside the original house are retained.

If you move an entrance or resize a house, update its `Village` entry in `TolkienVillageProvider`:

- `entrance`: the floor block immediately inside the doorway, on the template's outside edge, in original local coordinates. The generator adds the porch one block farther outward and its attachment one block above the floor.
- `orientation`: the outward facing direction, for example `east_up` or `west_up`.
- `residentFloor`: a solid floor block with space for a villager directly above it, in original local coordinates. The connector restores that floor after placing the resident.

Derived roads move their house connections to the road template's outer edge and add driveways. This lets the larger Tolkien houses attach without overlapping the reserved road footprint. Vanilla road shapes, weights, terrain matching, and processors are retained. Pool aliases apply to these Tolkien village starts; vanilla villages continue using their own house and street pools.

## Verify changes

```sh
./gradlew runGameTestServer -PgameTestNamespaces=tolkienmobs_villages
```

The tests construct provider registries in memory and write temporary datagen output outside the repository. They cover all five styles, biome filtering, seeded layouts, resource preparation, and full village placement. They do not update your checked-in generated resources.
