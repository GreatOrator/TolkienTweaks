package com.greatorator.tolkienmobs.datagen.loot;

import com.greatorator.tolkienmobs.datagen.loot.functions.TolkienItemsModifier;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public TolkienGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
    @Override
    protected void start() {
        /* Plants */

        /* Chests */
        add("bronze_coin_in_village_temple_chest", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_temple")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("shipwreck_supply", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_temple")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f,
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));

        /* Entities */
        add("add_bronze_coin_in_zombie_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/zombie")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_zombie_villager_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/zombie_villager")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_skeleton_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/skeleton")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_wither_skeleton_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/wither_skeleton")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_creeper_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/creeper")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_gold_coin_in_ghast_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/ghast")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_GOLD.getId().toString(), 0.25f
                )
        ));
        add("add_gold_coin_in_blaze_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/blaze")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_GOLD.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_husk_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/husk")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_magma_cube_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/magma_cube")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_drowned_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/drowned")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_gold_coin_in_elder_guardian_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/elder_guardian")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_GOLD.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_guardian_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/guardian")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_stray_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/stray")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_enderman_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.fromNamespaceAndPath("minecraft", "entities/enderman")).build()
                },
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Global Loot Modifiers";
    }
}
