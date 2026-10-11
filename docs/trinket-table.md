# Trinket table crafting

The table infuses an existing trinket with a potion type. The recipes that create
blank trinkets still use the vanilla crafting table; potion infusion uses only
`tolkienmobs:trinket` recipes and the trinket table.

## Slots and crafting

| Inventory index | Slot | Accepted input |
| --- | --- | --- |
| 0 | Trinket | One trinket |
| 1 | Potion | One potion (generated recipes use `minecraft:potion`) |
| 2 | Fuel | Ammolite gems only |
| 3 | Output | Extraction only |

A craft takes 100 ticks (5 seconds at 20 TPS). It consumes one trinket, one potion,
and the recipe's fuel count. A normal potion leaves a glass bottle in its input
slot. Remove the bottle before inserting the next potion.

The output is one copy of the input trinket with its stored potion type replaced.
Its name, active state, and other components are preserved. Any registered potion
type is supported, including water and modded potion types. Potions without a
stored potion type cannot craft. Custom potion effects and colors are not copied;
the infusion uses the potion type, as the original recipe did.

An occupied output blocks crafting without consuming inputs. Changing an input
resets progress. The table saves its inventory and progress with the world.

## Generating recipes

`TolkienRecipeProvider` generates one recipe for each of the seven trinkets.
The output item is determined by the input trinket, so there is no separate result
item or output count argument:

```java
trinketRecipe(pRecipeOutput, TolkienItems.TRINKET_RING,
        SizedIngredient.of(TolkienItems.GEM_AMMOLITE.get(), 1), Items.POTION);
```

This uses `TrinketRecipeBuilder` and generates
`data/tolkienmobs/recipe/trinket/trinket_ring_from_trinket.json`:

```json
{
  "type": "tolkienmobs:trinket",
  "trinket": "tolkienmobs:trinket_ring",
  "fuel": { "item": "tolkienmobs:gem_ammolite", "count": 1 },
  "potion": "minecraft:potion"
}
```

Existing custom infusion datapacks must rename `result` to `trinket` and
`ingredient` to `fuel`. The recipe ID and serializer ID are unchanged.

Run `./gradlew runData` with Java 21 to regenerate resources. Run
`./gradlew runGameTestServer` for the trinket table regression tests, including
all seven trinkets, recipe serialization, blocked outputs, invalid inputs, and
input changes during crafting.
