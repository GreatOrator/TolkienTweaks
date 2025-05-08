package com.greatorator.tolkienmobs.datagen.helpers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienLootHelper {
    private static final Set<ResourceKey<LootTable>> LOOT_TABLES = new HashSet<>();
    public static final Set<ResourceKey<LootTable>> IMMUTABLE_LOOT_TABLES = Collections.unmodifiableSet(LOOT_TABLES);

    public static final ResourceKey<LootTable> HOBBIT_GROCER = register("chests/hobbit_grocer");
    public static final ResourceKey<LootTable> HOBBIT_HOUSE = register("chests/hobbit_house");
    public static final ResourceKey<LootTable> ELVEN_HOUSE = register("chests/elven_house");
    public static final ResourceKey<LootTable> DWARF_HOUSE = register("chests/dwarf_house");
    public static final ResourceKey<LootTable> HUMAN_HOUSE = register("chests/human_house");
    public static final ResourceKey<LootTable> BARROW_CHEST = register("chests/barrow_chest");
    public static final ResourceKey<LootTable> BARROW_GRAVE = register("chests/barrow_grave");
    public static final ResourceKey<LootTable> SWAMP_HAG_HUT = register("chests/swamp_hag_hut");
    public static final ResourceKey<LootTable> SPIDER_TREE = register("chests/spider_tree");
    public static final ResourceKey<LootTable> WARG_PIT = register("chests/warg_pit");
    public static final ResourceKey<LootTable> GOLLUM_CAVE = register("chests/gollum_cave");
    public static final ResourceKey<LootTable> MINOTAUR_CHEST = register("chests/minotaur_chest");
    public static final ResourceKey<LootTable> MAZE_CHEST = register("chests/maze_chest");
    public static final ResourceKey<LootTable> DESOLATE_RUINS = register("chests/desolate_ruins");
    public static final ResourceKey<LootTable> TOWER_RUINS = register("chests/tower_chest");

    private static ResourceKey<LootTable> register(String id) {
        return register(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(MODID, id)));
    }

    private static ResourceKey<LootTable> register(ResourceKey<LootTable> id) {
        if (LOOT_TABLES.add(id)) {
            return id;
        } else {
            throw new IllegalArgumentException(id + " is already a registered built-in loot table");
        }
    }
}
