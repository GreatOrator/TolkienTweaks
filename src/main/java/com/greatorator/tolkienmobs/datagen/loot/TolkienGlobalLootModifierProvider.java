package com.greatorator.tolkienmobs.datagen.loot;

import com.greatorator.tolkienmobs.datagen.loot.functions.TolkienItemsModifier;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public TolkienGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
    @Override
    protected void start() {
        add("bronze_coin_in_village_temple_chest", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_temple")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_zombie_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/zombie")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
    }
}
