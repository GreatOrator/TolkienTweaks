package com.greatorator.tolkienmobs.datagen.loot;

import com.greatorator.tolkienmobs.datagen.loot.functions.TolkienItemsModifier;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
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
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_temple")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("shipwreck_supply", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_temple")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f,
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));

        /* Entities */
        add("add_bronze_coin_in_zombie_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/zombie")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_zombie_villager_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/zombie_villager")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_skeleton_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/skeleton")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_wither_skeleton_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/wither_skeleton")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_creeper_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/creeper")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_gold_coin_in_ghast_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/ghast")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_GOLD.getId().toString(), 0.25f
                )
        ));
        add("add_gold_coin_in_blaze_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/blaze")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_GOLD.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_husk_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/husk")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_magma_cube_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/magma_cube")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_drowned_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/drowned")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_gold_coin_in_elder_guardian_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/elder_guardian")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_GOLD.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_guardian_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/guardian")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_SILVER.getId().toString(), 0.25f
                )
        ));
        add("add_bronze_coin_in_stray_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/stray")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(
                        TolkienItems.ITEM_COIN_BRONZE.getId().toString(), 0.25f
                )
        ));
        add("add_silver_coin_in_enderman_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/enderman")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
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
