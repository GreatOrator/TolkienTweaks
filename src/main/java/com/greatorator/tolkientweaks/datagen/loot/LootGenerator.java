package com.greatorator.tolkientweaks.datagen.loot;


import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 10/3/20.
 */
public class LootGenerator extends LootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> lootTables = ImmutableList.of(
            Pair.of(BlockLootGenerator::new, LootParameterSets.BLOCK),
            Pair.of(EntityLootGenerator::new, LootParameterSets.ENTITY),
            Pair.of(ChestLootGenerator::new, LootParameterSets.CHEST)
    );

    public LootGenerator(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        //map.forEach((p_218436_2_, p_218436_3_) -> LootTableManager.validateLootTable(validationtracker, p_218436_2_, p_218436_3_));
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return lootTables;
    }

    @Override
    public String getName() {
        return "Tolkien Tweaks - Loot Tables";
    }
}
