# Tolkien villages

The six house structure IDs now start villages using Minecraft's jigsaw village generator. Each village has Tolkien houses, paths with short driveways, a well or fountain with a bell, farms, decorations, animals, and the matching Tolkien NPC as its residents instead of vanilla villagers. Existing Tolkien inhabitants saved in the houses are retained.

| House style | Biome | Paths, farms, and decorations |
| --- | --- | --- |
| Desert | Haradwaith | Vanilla desert village |
| Dwarf | Iron Hills, Blue Mountains | Vanilla plains village, paths in the mod's stone path |
| Elven | Lorinand | Vanilla plains village |
| Hobbit | Shire | Vanilla plains village |
| Human | Gladden Fields | Vanilla plains village, paths in the mod's stone path |
| Orc | Mordor | Vanilla desert village |
| Nurn | Nurn | Vanilla desert village in dark stone, on dry land, walking on the mod's stone path |

Each style's residents are the mod's own villager-style NPC, chosen by the `resident` field of its `Village` entry: desert/Haradwaith uses Southron, dwarf uses Dwarf, elven uses Elves, hobbit uses Hobbit, human uses Human, and orc uses Orc. Wandering trader variants are never used. Datagen copies vanilla's three villager templates (`nitwit`, `baby`, `unemployed`, weights 1/1/10) once per style into `tolkienmobs:village/<style>/villagers/` and swaps the stored entity for that NPC. Every jigsaw socket that asks vanilla for a villager - your houses' resident connectors as well as vanilla town centers, farms and decorations - is redirected there through a pool alias.

## Generate the resources after merging

Run with Java 21:

```sh
./gradlew runData
./gradlew build
```

On Windows, use `gradlew.bat`. The village code needs the resulting JSON and NBT resources before it can generate villages. Datagen replaces the old standalone house placement sets with one `tolkienmobs:villages` set and writes prepared house and road templates under `src/generated/resources/data/tolkienmobs/structure/village/`. Its normal stale-output cleanup removes the old house structure-set JSONs.

Villages appear in newly generated chunks. Existing buildings in explored chunks stay where they are. Original structure IDs are retained so saved structure starts remain readable.

## The stone path of the dwarven, human and Nurn villages

Three villages lay `tolkienmobs:block_stone_path` where a vanilla village lays its dirt path. What
that takes depends on where the dirt path comes from:

- **Streets.** Vanilla's plains street templates are dirt path, and `minecraft:street_plains` is
  what turns a tenth of that into grass and a street crossing water into a plank bridge. Rather
  than losing that, the dwarven and human road pools carry `tolkienmobs:stone_path_streets`, a copy
  of those same rules with one added at the end that lays the stone path. Nurn's streets are the
  desert ones and walk on smooth sandstone, so `tolkienmobs:nurn_roads` is the dark stone recolour
  with one rule put in front of it that lays the path block: its roads, its terminator stubs and
  the plaza around its well walk on the block, while the walls, the well and the lamp bases stay
  dark stone. Rules within one rule processor stop at the first match, which is why the path rule
  has to come first - as a second processor it would only ever see the block the template started
  with.
- **Terminators.** The pieces that close a road off are not in the street templates and carry
  vanilla's street rules in the vanilla pool, so a village that lays its own path gets a terminator
  pool of its own. Vanilla holds four plains terminators and two desert ones; keep
  `TolkienVillageProvider.terminatorNames` in step with that.
- **Town centre.** The fountain plaza is a quarter dirt path, so datagen writes the village a copy
  of it under `tolkienmobs:village/<style>/town_center` and swaps the paths in the palette *and* in
  the jigsaw sockets, whose `final_state` is set straight into the world after the processors run.
  A palette rename alone would leave six dirt paths around the fountain.
- **Houses and driveways.** These are templates datagen writes itself, so the path block is written
  in directly: each house's porch and the driveway that meets it. Nurn is in this group too, so the
  path at each of its houses' doors is the block as well.

A village only earns its own road pool and town centre when its vanilla style actually lays a dirt
path, which is why the elven and hobbit villages keep driving on the plains roads.

## Nurn's dark stone village

`ttmhouse_nurn` is the desert village recoloured rather than re-modelled. `NURN_DARK_STONE` rewrites sand, terracotta and every sandstone variant as the dark stone block of the same shape, `NURN_ROADS` is the same recolour with the mod's stone path laid where the streets and the plaza walked on smooth sandstone, and the road pool's own terminator pool replaces the vanilla desert terminators - those pieces are raw smooth sandstone and are not part of the street templates, so without their own pool they stayed sandstone while the rest of the village turned. The town centre, houses, farms, decorations and villagers all come from the same aliases as the other styles.

`NURN_FARMS` is that recolour plus the desert crops, and one rule in front of it: the rim a desert farm builds around its plots out of smooth sandstone stairs becomes `tolkienmobs:log_deadwood`. Recolouring the stairs kept their shape and read as stone stairs left standing in a field; the logs look like the fence the rim is standing in for. Because the farm rules run first, the recolour below never sees those blocks.

It is the only style that starts through `VillageStructure` instead of the vanilla jigsaw structure. That structure refuses a site whose surface heightmap sits above its ocean floor heightmap, so a Nurn village never starts in a lake. All other styles start anywhere in their biome as before. Nurn's floor is dark stone rather than grass, so the village no longer asks for grass before it agrees to a site.

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

Derived roads move their house connections to the road template's outer edge and add driveways. This lets the larger Tolkien houses attach without overlapping the reserved road footprint. Vanilla road shapes, weights and terrain matching are retained, as are vanilla's street processors - except in the villages that lay the mod's stone path, whose pools carry the copy of those rules described above. Pool aliases apply to these Tolkien village starts; vanilla villages continue using their own house and street pools.

## Verify changes

```sh
./gradlew runGameTestServer -PgameTestNamespaces=tolkienmobs_villages
```

The tests construct provider registries in memory and write temporary datagen output outside the repository. They cover all five styles, biome filtering, seeded layouts, resource preparation, and full village placement. They do not update your checked-in generated resources.
