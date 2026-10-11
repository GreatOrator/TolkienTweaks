# Tolkien biomes in normal worlds

## Installation and world creation

Install **TerraBlender for NeoForge 1.21.1, version 4.1.0.8 or a compatible 4.1.x
release**, on both clients and servers alongside Tolkien Tweaks. Gradle includes
it automatically for development runs; the published mod declares it as required.

Create a world using Minecraft's **Default** world type. Tolkien biomes now appear
alongside vanilla biomes without selecting a single-biome world or installing a
custom world preset. Existing worlds gain the new distribution in newly generated
chunks; explored chunks retain their saved biomes and terrain. Use a fresh world
to see the new distribution without boundaries against older terrain.

With commands enabled, use `/locate biome tolkienmobs:shire` (or another ID below).
Biomes need the right climate and a Tolkien region, so every biome is not
necessarily close to spawn.

## Distribution

`TolkienOverworldRegion` registers a TerraBlender Overworld region with weight 2.
TerraBlender's default vanilla region weight is 10. This is a relative region
weight, not a guaranteed percentage of blocks; unchanged climates in Tolkien
regions still use vanilla biomes, and other mods can add their own regions.

These substitutions apply **inside Tolkien regions**. Vanilla regions retain
Minecraft's biome choices, and ocean, river, and cave climate mappings remain
available in both regions.

| Tolkien biome ID | Uses the climate locations of |
| --- | --- |
| `mirkwood` | Dark forest |
| `mirkwood_spooky` | Old-growth spruce taiga |
| `iron_hills` | Windswept gravelly hills |
| `hithaeglir` | Frozen peaks |
| `fangorn_forest` | Old-growth pine taiga |
| `mordor` | Eroded badlands |
| `ash_mountains` | Jagged peaks |
| `marshes` | Swamp |
| `lorinand` | Birch forest |
| `barrow_downs` | Windswept hills |
| `old_forest` | Forest |
| `shire` | Plains |
| `firien` | Flower forest |
| `haradwaith` | Desert |
| `dagorlad` | Savanna plateau |
| `gladden` | Sunflower plains |

All IDs use the `tolkienmobs:` namespace. Adjust the replacements or region weight
in `TolkienOverworldRegion` to change distribution.

## Why single-biome worlds worked

Biome registration makes a biome available to the registry and single-biome
selector. It does not add it to the normal Overworld's climate map. The region
registration now supplies that missing connection. Surface rules are registered
with TerraBlender's active manager and include the existing mountain rules plus
grass/dirt, desert, and Mordor surfaces.

The biome and feature registries also need to share one datagen context. They are
now bootstrapped together through `TolkienDataRegistryProvider`, and biome tags
are generated using that registry lookup.

Finally, Minecraft requires shared placed features to follow a consistent order
across biomes. `TolkienBiomeGenerationSettings` derives that order from vanilla
Overworld biomes, deduplicates repeated entries, and puts custom features after
vanilla features in a stable order. This preserves each biome's feature set while
preventing feature-order cycles in mixed-biome worlds. When adding biome features,
use this builder and rerun the feature-order test after adding biome modifiers.

## Development checks

With Java 21:

```sh
./gradlew runData
./gradlew runGameTestServer
```

The worldgen tests sample Minecraft's normal Overworld noise generator to find
all 16 biomes, check their Overworld tags, retain vanilla biome availability, and
check the combined vanilla/Tolkien feature graph after biome modifiers apply.
The GameTest world itself is flat, so the test explicitly initializes the normal
preset with TerraBlender's server initialization routine.
