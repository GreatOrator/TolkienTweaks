package com.greatorator.tolkienmobs.datagen.loot;

import com.greatorator.tolkienmobs.datagen.loot.functions.TolkienItemsModifier;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The mod's items added to vanilla's own tables.
 *
 * <p>Only a little goes in here, and only where it reads as belonging: coin in the pockets of the
 * things that would carry a purse, and the odd reagent where a vanilla mob is already the closest
 * thing the overworld has to the mod's. Each entry names the vanilla table it patches, so a
 * conflict with another mod's patch is a merge rather than a replacement.
 */
public class TolkienGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public TolkienGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }

    @Override
    protected void start() {
        /* Chests */

        // The till of a village temple.
        coin("bronze_coin_in_village_temple_chest", "minecraft:chests/village/village_temple", TolkienItems.ITEM_COIN_BRONZE.get(), 0.25F);

        // The strongbox of a wrecked ship, which used to patch the temple chest by mistake.
        coin("shipwreck_supply", "minecraft:chests/shipwreck_supply", TolkienItems.ITEM_COIN_BRONZE.get(), 0.25F);
        coin("silver_coin_in_shipwreck_treasure", "minecraft:chests/shipwreck_treasure", TolkienItems.ITEM_COIN_SILVER.get(), 0.25F);

        // A raider's camp is a camp of Men, and men carry coin.
        coin("bronze_coin_in_pillager_outpost", "minecraft:chests/pillager_outpost", TolkienItems.ITEM_COIN_BRONZE.get(), 0.30F);
        coin("faction_coin_in_pillager_outpost", "minecraft:chests/pillager_outpost", TolkienItems.ITEM_FACTIONCOIN.get(), 0.15F);

        /* Entities */

        // The undead of the overworld: the coin they were buried with.
        coin("add_bronze_coin_in_zombie_kill", "minecraft:entities/zombie", TolkienItems.ITEM_COIN_BRONZE.get(), 0.25F);
        coin("add_bronze_coin_in_zombie_villager_kill", "minecraft:entities/zombie_villager", TolkienItems.ITEM_COIN_BRONZE.get(), 0.25F);
        coin("add_bronze_coin_in_husk_kill", "minecraft:entities/husk", TolkienItems.ITEM_COIN_BRONZE.get(), 0.25F);
        coin("add_bronze_coin_in_drowned_kill", "minecraft:entities/drowned", TolkienItems.ITEM_COIN_SILVER.get(), 0.25F);

        // Skeletons carried a purse, and the colder and older the skeleton the better it was.
        coin("add_silver_coin_in_skeleton_kill", "minecraft:entities/skeleton", TolkienItems.ITEM_COIN_SILVER.get(), 0.25F);
        coin("add_silver_coin_in_stray_kill", "minecraft:entities/stray", TolkienItems.ITEM_COIN_SILVER.get(), 0.25F);
        coin("add_bronze_coin_in_bogged_kill", "minecraft:entities/bogged", TolkienItems.ITEM_COIN_BRONZE.get(), 0.25F);
        coin("add_silver_coin_in_wither_skeleton_kill", "minecraft:entities/wither_skeleton", TolkienItems.ITEM_COIN_SILVER.get(), 0.25F);

        // The fiery and the eldritch, which hold the better coin.
        coin("add_bronze_coin_in_creeper_kill", "minecraft:entities/creeper", TolkienItems.ITEM_COIN_BRONZE.get(), 0.25F);
        coin("add_bronze_coin_in_magma_cube_kill", "minecraft:entities/magma_cube", TolkienItems.ITEM_COIN_BRONZE.get(), 0.25F);
        coin("add_gold_coin_in_ghast_kill", "minecraft:entities/ghast", TolkienItems.ITEM_COIN_GOLD.get(), 0.25F);
        coin("add_gold_coin_in_blaze_kill", "minecraft:entities/blaze", TolkienItems.ITEM_COIN_GOLD.get(), 0.25F);
        coin("add_silver_coin_in_enderman_kill", "minecraft:entities/enderman", TolkienItems.ITEM_COIN_SILVER.get(), 0.25F);

        // The sea's guardians keep the sea's coin.
        coin("add_silver_coin_in_guardian_kill", "minecraft:entities/guardian", TolkienItems.ITEM_COIN_SILVER.get(), 0.25F);
        coin("add_gold_coin_in_elder_guardian_kill", "minecraft:entities/elder_guardian", TolkienItems.ITEM_COIN_GOLD.get(), 0.25F);

        // The raiders are Men, and Men take a purse into a raid.
        coin("add_bronze_coin_in_pillager_kill", "minecraft:entities/pillager", TolkienItems.ITEM_COIN_BRONZE.get(), 0.30F);
        coin("add_bronze_coin_in_vindicator_kill", "minecraft:entities/vindicator", TolkienItems.ITEM_COIN_BRONZE.get(), 0.30F);
        coin("add_silver_coin_in_evoker_kill", "minecraft:entities/evoker", TolkienItems.ITEM_COIN_SILVER.get(), 0.30F);

        // A witch gathers herbs, and the mod has a herb worth gathering.
        add("pungent_herb_in_witch_kill", new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.fromNamespaceAndPath("minecraft", "entities/witch")).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(TolkienItems.ITEM_PUNGENTHERB.getId().toString(), 0.35F)
        ));
    }

    /** Adds one coin to one vanilla table, at a flat chance per roll. */
    private void coin(String name, String table, Item coin, float chance) {
        add(name, new TolkienItemsModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(Identifier.parse(table)).build()
                },
                IGlobalLootModifier.DEFAULT_PRIORITY,
                Map.of(BuiltInRegistries.ITEM.getKey(coin).toString(), chance)
        ));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Global Loot Modifiers";
    }
}
