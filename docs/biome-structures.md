# Biome structures (Minecraft 1.21.1)

`TolkienStructures` registers the structure types and configured keys.
`TolkienStructureProvider.STRUCTURES` is the editable catalogue; `runData` generates
structure definitions, template pools, placement sets and allowed-biome tags.

## Current catalogue

All identifiers below are in the `tolkienmobs` namespace.

| Structure / NBT | Biome | Placement |
| --- | --- | --- |
| `ttmbarrow` | Barrow Downs | Ground level |
| `ttmdark_tower` | Mordor | Ground level, with a nearby warg pit |
| `ttmgollum_cave` | Hithaeglir | Roof 32 blocks below the local terrain surface |
| `ttmhouse_desert` | Haradwaith | 3–5 houses, ground level |
| `ttmhouse_dwarf` | Iron Hills | 3–5 houses, ground level |
| `ttmhouse_elven` | Lorinand | 3–5 houses, ground level |
| `ttmhouse_human` | Gladden Fields | 3–5 houses, ground level |
| `ttminn_desert` | Haradwaith | Ground level, sparse |
| `ttmruin_large`, `ttmruin_small` | Dagorlad | Ground level, shared placement grid |
| `ttmspidertree` | Mirkwood Spooky | Ground level, sparse |
| `ttmswamp_hag_hut` | Marshes | Ground level, sparse |
| `ttmwargpit` | Mordor | Top two layers above local ground, paired with a dark tower |
| `cave` | Mirkwood | Underground, one main piece plus random `cave_side` branches |
| `maze` | Hithaeglir | Underground, one main piece plus random `maze_side` branches |

### House clusters and ground height

`TemplateLayoutStructure` samples 3–5 houses per cluster. Each house receives a
random rotation, follows its own local terrain height and has a neighbor with
5–10 empty blocks between their bounding-box edges. Additional houses are placed
around existing houses; all pairs retain at least five blocks of horizontal
clearance. The layout is bounded to 112 blocks from its starting chunk origin.
A candidate that cannot fit all houses in the allowed biome is skipped.

Ground level means NBT layer Y=0 replaces the top terrain block at the footprint
center. Ground-level buildings use terrain blending; slopes can still affect how
the edges meet the terrain. Biome checks use each building's footprint center.

Gollum's cave uses `Layout.top(-32)`: its **roof**, rather than its foundation,
is 32 blocks below that surface. Warg pits use `Layout.top(2)`: the highest two
layers are above it. These offsets reference the footprint center; uneven ground
can cover more or less of a building at its edges. Buried structures use no
terrain blending, so the terrain is not pulled down to their foundations.

Every valid dark-tower start includes one warg pit, 5–10 blocks from its edges.
The pit has its own pool and configured structure for manual placement, but **no
independent natural placement set**. This avoids unrelated pits overlapping tower
complexes. Use `/locate structure tolkienmobs:ttmdark_tower` to find both.

### Frequency and minimum spacing

`spacing` and `separation` are measured in **chunks** (16 blocks). Each spacing
region proposes one candidate; biome checks can reject it. These are candidate
grids, not guaranteed counts in any particular biome.

| Group | Spacing / separation | Result |
| --- | --- | --- |
| House clusters | 24 / 16 | Separate compact clusters, minimum 272 blocks between start chunks |
| Barrow, dark tower, Gollum cave | 16 / 6 | Minimum 112 blocks between start chunks of the same kind |
| Inn, spider tree, swamp hut | 12 / 6 | Minimum 112 blocks between starts; even the largest footprint leaves over 64 blocks between copies |
| Dagorlad ruins | 4 / 3, shared set | At most one ruin of either size per 4×4-chunk region; minimum 64 blocks between starts and at least 47 blocks between footprint edges |
| Cave, maze | 32 / 8 | Existing sparse underground complexes |

The shared `dagorlad_ruins` set chooses one size with equal weight. Sharing a
set also enforces spacing **between** the two sizes. Its density is below the
requested maximum of one of each per four chunks. Frequency and spacing rules
apply to newly generated terrain using this version, not manually placed builds
or structures already saved under an earlier configuration.

The old `shire_ruin` ID remains registered so previously generated chunks can
load it. It has no natural placement set now. Small ruins generate in Dagorlad
through `ttmruin_small`; no new Shire ruins are scheduled.

## Editing or adding structures

Java paths are relative to `src/main/java/com/greatorator/tolkienmobs/`:

- `init/TolkienStructures.java`: keys and custom type registration.
- `datagen/TolkienStructureProvider.java`: NBT identifiers, biomes, layout and frequency.
- `world/components/structure/TemplateLayoutStructure.java`: local terrain alignment,
  cluster layout and 5–10 block gap constants.
- `world/components/structure/SurfaceStructures.java`: the jigsaw branching engine.
- `datagen/TolkienDataRegistryProvider.java`: combined registry bootstrap callbacks.
- `datagen/tags/TolkienBiomeTagProvider.java`: generates biome tags from definitions.

For a new standalone building:

1. Add `src/main/resources/data/tolkienmobs/structure/my_tower.nbt`.
   The `structure` folder is singular; NBT identifiers omit the `.nbt` suffix.
2. Add a key to `TolkienStructures`:

```java
public static final ResourceKey<Structure> MY_TOWER = key("my_tower");
```

3. Add an entry to `TolkienStructureProvider.STRUCTURES`:

```java
site(TolkienStructures.MY_TOWER, TolkienBiomes.SHIRE,
        16, 6, 193764840, Layout.single())
```

`site` uses the key as the NBT identifier. Its numeric arguments are spacing,
separation and salt. Use a distinct salt per independent placement set. Use
`Layout.houses()` for 3–5 buildings, or `Layout.top(-32)` for burial. For multiple
biomes or an NBT with a different identifier, use `Definition` directly as the
existing cave/maze entries demonstrate.

4. Generate resources with Java 21:

```shell
./gradlew runData
```

Windows: `gradlew.bat runData`. This sandbox:
`mise exec java@21 -- bash gradlew runData`.

Output goes under `src/generated/resources/data/tolkienmobs/`:

- `worldgen/structure/`
- `worldgen/template_pool/`
- `worldgen/structure_set/`
- `tags/worldgen/biome/has_structure/`

Commit the generated files with the Java edits. Edit the definitions and
regenerate; direct edits to generated JSON are overwritten. Avoid duplicate JSON
under `src/main/resources`. NBT files remain in `src/main/resources` as inputs.
The existing Gradle configuration includes both resource directories in the JAR.

## Testing in game and older NBTs

Rebuild the mod and restart the world after changing worldgen definitions.
Structures generate in **new chunks**, not retroactively in explored terrain.
With cheats/operator permissions, for example:

```mcfunction
/locate structure tolkienmobs:ttmhouse_desert
/locate structure tolkienmobs:ttmgollum_cave
/place structure tolkienmobs:ttmdark_tower
/place template tolkienmobs:ttmgollum_cave
```

`/place structure` previews the generated layout; `/place template` places only
the raw NBT. Manual placement does not demonstrate natural frequency or biome
eligibility. Underground structures are easier to inspect in spectator mode.

The supplied buildings retain their saved blocks, inventories and entities. The
cave and Gollum cave's obsolete `tconstruct:copper_ore` reference was changed to
`minecraft:copper_ore`. Minecraft applies its vanilla data upgrades to the old
NBTs, but cannot generally migrate renamed/removed mod content. Preview and
resave any additional legacy NBTs before enabling them. Structure void cells
preserve existing terrain; saved air clears it. `spawn_overrides: {}` keeps normal
biome spawning, while saved NBT entities are placed with the building.

## Cave and maze branches

`TolkienStructures.CAVE` and `TolkienStructures.MAZE` each have a starting pool
containing only their main NBT (`cave.nbt` or `maze.nbt`). The main piece appears
exactly once per structure. Side pieces never select the main template.

The editable defaults in `TolkienStructureProvider.STRUCTURES` are:

| Structure | Biome | Spacing / separation | Branch settings |
| --- | --- | --- | --- |
| `tolkienmobs:cave` | Mirkwood | 32 / 8 chunks | `new Branches("tolkienmobs:cave_side", 4, 3, 1)` |
| `tolkienmobs:maze` | Hithaeglir | 32 / 8 chunks | `new Branches("tolkienmobs:maze_side", 4, 3, 1)` |

The branch arguments are **side template**, **maximum depth**, **piece weight**,
and **stop weight**. The defaults bound expansion to four jigsaw levels. For
continuations, a side piece has weight 3 and stopping has weight 1. Minecraft
also skips pieces that collide or exceed the placement bounds. Thus counts and
rotations vary with the seed and location; 3:1 is not a guaranteed piece count.
The root connectors try a side piece before random continuation begins.
There is currently one side template per family, reused with random rotations.

Both definitions set the final `underground` argument to `true`. The main piece
starts at an absolute Y sampled from **-32 through 0**, shifted by `heightOffset`;
side pieces stay at the same level. There is no projection onto the surface and
no terrain blending. Their generation step is `underground_structures`.
`use_surface_biome: true` checks the biome of the land above the starting piece,
so underground cave biomes do not exclude a structure below Mirkwood/Hithaeglir.

Edit the biome list, height offset and branch settings in the definitions. The
Y range itself is in `SurfaceStructures.underground`. The maximum distance remains
80 blocks from the start center. A larger depth still respects collision and
distance limits. The catalogue buildings retain their configured ground-level or surface-relative placement.

For each family, `runData` generates the structure, placement set, biome tag and
three pools (six files total). The additional pools are:

- `cave_pool/side_pool` and `maze_pool/side_pool`: side template only.
- `cave_pool/branches` and `maze_pool/branches`: weighted side/empty choices.

The supplied NBT connectors were updated to use these pools:

| NBT | Jigsaw name | Jigsaw target | Target pool |
| --- | --- | --- | --- |
| `cave` | `tolkienmobs:cave` | `tolkienmobs:cave_side` | `tolkienmobs:cave_pool/side_pool` |
| `cave_side` | `tolkienmobs:cave_side` | `tolkienmobs:cave_side` | `tolkienmobs:cave_pool/branches` |
| `maze` | `tolkienmobs:maze` | `tolkienmobs:maze_side` | `tolkienmobs:maze_pool/side_pool` |
| `maze_side` | `tolkienmobs:maze_side` | `tolkienmobs:maze_side` | `tolkienmobs:maze_pool/branches` |

Keep these names and pools when resaving the NBTs. Their existing connector
positions and orientations were preserved. Jigsaws still resolve to air; stopped
branches therefore leave an open passage, rather than adding a closing wall.
The original buildings, inventories and maze minotaur are preserved.

Test in new chunks with `/locate structure tolkienmobs:cave` and
`/locate structure tolkienmobs:maze`. For manual previews use `/place structure`
with those same IDs. Run `runData` and rebuild after changing the definitions.
