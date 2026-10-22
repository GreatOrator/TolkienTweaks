package com.greatorator.tolkienmobs.datagen;

import com.google.gson.JsonObject;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienFluids;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import com.greatorator.tolkienmobs.datagen.modelgen.ItemModelBuilder;
import com.greatorator.tolkienmobs.datagen.modelgen.ItemModelProvider;
import com.greatorator.tolkienmobs.datagen.modelgen.ModelFile;
import com.greatorator.tolkienmobs.datagen.modelgen.ItemLayerModelBuilder;
import com.greatorator.tolkienmobs.datagen.modelgen.ItemModelDefinition;
import com.greatorator.tolkienmobs.datagen.modelgen.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienItemModelProvider extends ItemModelProvider {
    /**
     * Every trim material 1.21.4 ships, in the order vanilla lists them. The old 1.20 codes gave
     * each one a float for the model's {@code trim_type} predicate; 1.21.4 selects on the trim
     * material itself, so only the list is needed now.
     */
    private static final List<ResourceKey<TrimMaterial>> TRIM_MATERIALS = List.of(
            TrimMaterials.QUARTZ,
            TrimMaterials.IRON,
            TrimMaterials.NETHERITE,
            TrimMaterials.REDSTONE,
            TrimMaterials.COPPER,
            TrimMaterials.GOLD,
            TrimMaterials.EMERALD,
            TrimMaterials.DIAMOND,
            TrimMaterials.LAPIS,
            TrimMaterials.AMETHYST,
            TrimMaterials.RESIN);

    public TolkienItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        /* Items */
            // Metals & Gems
        basicItem(TolkienItems.RAW_MITHRIL.get());
        basicItem(TolkienItems.DUST_MITHRIL.get());
        basicItem(TolkienItems.NUGGET_MITHRIL.get());
        basicItem(TolkienItems.INGOT_MITHRIL.get());
        basicItem(TolkienFluids.MITHRIL_FLUID_BUCKET.get());
        basicItem(TolkienItems.RAW_MORGULIRON.get());
        basicItem(TolkienItems.DUST_MORGULIRON.get());
        basicItem(TolkienItems.NUGGET_MORGULIRON.get());
        basicItem(TolkienItems.INGOT_MORGULIRON.get());
        basicItem(TolkienFluids.MORGULIRON_FLUID_BUCKET.get());
        basicItem(TolkienItems.GEM_AMMOLITE.get());

            // Quest
        basicItem(TolkienItems.ITEM_BERYL.get());
        basicItem(TolkienItems.ITEM_FORTRESSMAP.get());
        basicItem(TolkienItems.ITEM_WATCHERHEART.get());
        basicItem(TolkienItems.ITEM_WATCHERHEART_CRACKED.get());
        basicItem(TolkienItems.ITEM_KEYSTONE.get());
        basicItem(TolkienItems.ITEM_DARKSADDLE.get());
        basicItem(TolkienItems.ITEM_ARTIFACT.get());
        basicItem(TolkienItems.ITEM_BLANKPAPER.get());
        basicItem(TolkienItems.ITEM_FANCYARMOR.get());
        basicItem(TolkienItems.ITEM_FANCYCLOTH.get());
        basicItem(TolkienItems.ITEM_FANCYHAMMER.get());
        basicItem(TolkienItems.ITEM_FANCYHELM.get());
        basicItem(TolkienItems.ITEM_FANCYKEY.get());
        basicItem(TolkienItems.ITEM_FANCYPICK.get());
        basicItem(TolkienItems.ITEM_FANCYSHIELD.get());
        basicItem(TolkienItems.ITEM_FANCYSHIELD2.get());
        basicItem(TolkienItems.ITEM_FANCYSWORD.get());
        basicItem(TolkienItems.ITEM_FANCYSWORD2.get());
        basicItem(TolkienItems.ITEM_LETTER.get());
        basicItem(TolkienItems.ITEM_SCROLL.get());
        basicItem(TolkienItems.ITEM_SCROLL2.get());
        basicItem(TolkienItems.ITEM_SPECIALFLOWER.get());
        basicItem(TolkienItems.ITEM_STORYBOOK.get());
        basicItem(TolkienItems.ITEM_STORYBOOK2.get());
        basicItem(TolkienItems.ITEM_STORYBOOK3.get());
        basicItem(TolkienItems.ITEM_STORYBOOK4.get());
        basicItem(TolkienItems.ITEM_WORNARMOR.get());
        basicItem(TolkienItems.ITEM_WORNHELM.get());
        basicItem(TolkienItems.ITEM_WORNKEY.get());
        basicItem(TolkienItems.ITEM_WORNPICK.get());
        basicItem(TolkienItems.ITEM_WORNSHIELD.get());
        basicItem(TolkienItems.ITEM_WORNSHIELD2.get());
        basicItem(TolkienItems.ITEM_WORNSWORD.get());
        basicItem(TolkienItems.ITEM_WOVENBASKET.get());
        basicItem(TolkienItems.ITEM_WRITTENPAPER.get());
        basicItem(TolkienItems.ITEM_PUNGENTHERB.get());
        basicItem(TolkienItems.ITEM_LOCKPICK.get());
        basicItem(TolkienItems.ITEM_BROKENSWORD.get());
        basicItem(TolkienItems.ITEM_REFORGEDSWORD.get());
        basicItem(TolkienItems.ITEM_MAGIC_CLOTH.get());
        basicItem(TolkienItems.ITEM_KEYFRAGMENT.get());
        basicItem(TolkienItems.ITEM_OILYKEY.get());
        basicItem(TolkienItems.ITEM_MITHRILNUGGET.get());
        basicItem(TolkienItems.ITEM_REMAINS.get());
        basicItem(TolkienItems.ITEM_RUNE_STONE.get());

            // Coins & Tokens
        basicItem(TolkienItems.ITEM_COIN_BRONZE.get());
        basicItem(TolkienItems.ITEM_COIN_SILVER.get());
        basicItem(TolkienItems.ITEM_COIN_GOLD.get());
        basicItem(TolkienItems.ITEM_COIN_MITHRIL.get());
        basicItem(TolkienItems.ITEM_DARKSIGIL.get());
        basicItem(TolkienItems.ITEM_FACTIONCOIN.get());
        basicItem(TolkienItems.ITEM_FACTIONTOKEN.get());
        basicItem(TolkienItems.ITEM_CAVECOMPLETE.get());
        basicItem(TolkienItems.ITEM_WATCHERCOMPLETE.get());
        basicItem(TolkienItems.ITEM_TOKEN_EASTERN_ALLIANCE.get());
        basicItem(TolkienItems.ITEM_TOKEN_WESTERN_ALLIANCE.get());
        basicItem(TolkienItems.ITEM_EXPERIENCE_ORB.get());

            // Mob Drops
        basicItem(TolkienItems.CREBAIN_FEATHER.get());
        basicItem(TolkienItems.BIRD_FEATHER.get());
        basicItem(TolkienItems.MUMAKIL_LEATHER.get());
        basicItem(TolkienItems.MONSTER_FUR.get());
        basicItem(TolkienItems.GOLEM_STONE.get());
        basicItem(TolkienItems.GOLEM_STONE_AIR.get());
        basicItem(TolkienItems.GOLEM_STONE_EARTH.get());
        basicItem(TolkienItems.GOLEM_STONE_FIRE.get());
        basicItem(TolkienItems.GOLEM_STONE_WATER.get());
        basicItem(TolkienItems.GOLEM_STONE_SUMMON.get());
        basicItem(TolkienItems.MORGUL_CRYSTAL.get());

        // Backpack Upgrades
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_BASE.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE_2.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_2.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_3.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_4.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_5.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_CRAFTING.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_SLEEPING.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_CAMPFIRE.get());
        basicItem(TolkienItems.TEMPLATE_MITHRIL.get());
        basicItem(TolkienItems.TEMPLATE_MORGULIRON.get());
        basicItem(TolkienItems.TEMPLATE_AMMOLITE.get());

        // Food
        basicItem(TolkienItems.LEMBAS.get());
        basicItem(TolkienItems.CRAM.get());
        basicItem(TolkienItems.HONEY_CAKE.get());
        basicItem(TolkienItems.MONSTER_FLESH.get());
        basicItem(TolkienItems.INSECT.get());
        basicItem(TolkienItems.GOLDEN_INSECT.get());
        basicItem(TolkienItems.TREE_ACORN.get());
        basicItem(TolkienItems.GOLDEN_TREE_ACORN.get());
        basicItem(TolkienItems.FOOD_HONEY.get());

        // Drink
        basicItem(TolkienItems.MIRUVOR.get());
        basicItem(TolkienItems.GROG.get());
        basicItem(TolkienItems.DRINK_PERSONAL_BLACKSMITH.get());
        basicItem(TolkienItems.DRINK_ERU_BLESSING.get());
        basicItem(TolkienItems.DRINK_ELF_FLEETFOOT.get());
        basicItem(TolkienItems.DRINK_ENT_DRAUGHT.get());
        basicItem(TolkienItems.DRINK_ELF_VITALITY.get());

        // Crop
        basicItem(TolkienItems.PIPEWEED_ITEM.get());
        basicItem(TolkienItems.PIPEWEED_SEEDS.get());
        basicItem(TolkienItems.BRAMBLES_BERRY.get());

        // Blocks
        basicItem(TolkienBlocks.DOOR_MITHRIL.asItem());
        buttonItem(TolkienBlocks.MITHRIL_BUTTON, TolkienBlocks.BLOCK_MITHRIL);
        wallItem(TolkienBlocks.WALL_MITHRIL, TolkienBlocks.BLOCK_MITHRIL);
        basicItem(TolkienBlocks.DOOR_MORGULIRON.asItem());
        buttonItem(TolkienBlocks.MORGULIRON_BUTTON, TolkienBlocks.BLOCK_MORGULIRON);
        wallItem(TolkienBlocks.WALL_MORGULIRON, TolkienBlocks.BLOCK_MORGULIRON);
        basicItem(TolkienBlocks.DOOR_DURIN.asItem());

        basicItem(TolkienBlocks.DOOR_MALLORN.asItem());
        buttonItem(TolkienBlocks.MALLORN_BUTTON, TolkienBlocks.PLANKS_MALLORN);
        fenceItem(TolkienBlocks.FENCE_MALLORN, TolkienBlocks.PLANKS_MALLORN);
        basicItem(TolkienBlocks.DOOR_MIRKWOOD.asItem());
        buttonItem(TolkienBlocks.MIRKWOOD_BUTTON, TolkienBlocks.PLANKS_MIRKWOOD);
        fenceItem(TolkienBlocks.FENCE_MIRKWOOD, TolkienBlocks.PLANKS_MIRKWOOD);
        basicItem(TolkienBlocks.DOOR_CULUMALDA.asItem());
        buttonItem(TolkienBlocks.CULUMALDA_BUTTON, TolkienBlocks.PLANKS_CULUMALDA);
        fenceItem(TolkienBlocks.FENCE_CULUMALDA, TolkienBlocks.PLANKS_CULUMALDA);
        basicItem(TolkienBlocks.DOOR_LEBETHRON.asItem());
        buttonItem(TolkienBlocks.LEBETHRON_BUTTON, TolkienBlocks.PLANKS_LEBETHRON);
        fenceItem(TolkienBlocks.FENCE_LEBETHRON, TolkienBlocks.PLANKS_LEBETHRON);
        basicItem(TolkienBlocks.DOOR_FANGORNOAK.asItem());
        buttonItem(TolkienBlocks.FANGORNOAK_BUTTON, TolkienBlocks.PLANKS_FANGORNOAK);
        fenceItem(TolkienBlocks.FENCE_FANGORNOAK, TolkienBlocks.PLANKS_FANGORNOAK);
        basicItem(TolkienBlocks.DOOR_DEADWOOD.asItem());
        buttonItem(TolkienBlocks.DEADWOOD_BUTTON, TolkienBlocks.PLANKS_DEADWOOD);
        fenceItem(TolkienBlocks.FENCE_DEADWOOD, TolkienBlocks.PLANKS_DEADWOOD);
        basicItem(TolkienBlocks.DOOR_DWARVEN_MAPLE.asItem());
        buttonItem(TolkienBlocks.DWARVEN_MAPLE_BUTTON, TolkienBlocks.PLANKS_DWARVEN_MAPLE);
        fenceItem(TolkienBlocks.FENCE_DWARVEN_MAPLE, TolkienBlocks.PLANKS_DWARVEN_MAPLE);
        saplingItem(TolkienBlocks.SAPLING_MIRKWOOD);
        saplingItem(TolkienBlocks.SAPLING_MALLORN);
        saplingItem(TolkienBlocks.SAPLING_CULUMALDA);
        saplingItem(TolkienBlocks.SAPLING_LEBETHRON);
        saplingItem(TolkienBlocks.SAPLING_FANGORNOAK);
        saplingItem(TolkienBlocks.SAPLING_DEADWOOD);
        saplingItem(TolkienBlocks.SAPLING_DWARVEN_MAPLE);
        saplingItem(TolkienBlocks.MUSHROOM_BLOOM_DECAY);
        saplingItem(TolkienBlocks.MUSHROOM_DECAY_BLOOM);

        buttonItem(TolkienBlocks.DARK_STONE_BUTTON, TolkienBlocks.DARK_STONE);
        buttonItem(TolkienBlocks.DARK_STONE_BRICKS_BUTTON, TolkienBlocks.DARK_STONE_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_DARK_STONE_BRICKS_BUTTON, TolkienBlocks.CRACKED_DARK_STONE_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_DARK_STONE_BUTTON, TolkienBlocks.COBBLED_DARK_STONE);
        buttonItem(TolkienBlocks.SMOOTH_DARK_STONE_BUTTON, TolkienBlocks.SMOOTH_DARK_STONE);
        buttonItem(TolkienBlocks.CHISELED_DARK_STONE_BRICKS_BUTTON, TolkienBlocks.CHISELED_DARK_STONE_BRICKS);
        buttonItem(TolkienBlocks.MOSSY_COBBLED_DARK_STONE_BUTTON, TolkienBlocks.MOSSY_COBBLED_DARK_STONE);
        buttonItem(TolkienBlocks.MOSSY_DARK_STONE_BRICKS_BUTTON, TolkienBlocks.MOSSY_DARK_STONE_BRICKS);
        buttonItem(TolkienBlocks.DWARVEN_STONE_BUTTON, TolkienBlocks.DWARVEN_STONE);
        buttonItem(TolkienBlocks.DWARVEN_STONE_BRICKS_BUTTON, TolkienBlocks.DWARVEN_STONE_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS_BUTTON, TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_DWARVEN_STONE_BUTTON, TolkienBlocks.COBBLED_DWARVEN_STONE);
        buttonItem(TolkienBlocks.MOSSY_COBBLED_DWARVEN_STONE_BUTTON, TolkienBlocks.MOSSY_COBBLED_DWARVEN_STONE);
        buttonItem(TolkienBlocks.MOSSY_COBBLED_DWARVEN_STONE_BUTTON, TolkienBlocks.MOSSY_COBBLED_DWARVEN_STONE);
        buttonItem(TolkienBlocks.MOSSY_DWARVEN_STONE_BRICKS_BUTTON, TolkienBlocks.MOSSY_DWARVEN_STONE_BRICKS);
        buttonItem(TolkienBlocks.SMOOTH_DWARVEN_STONE_BUTTON, TolkienBlocks.SMOOTH_DWARVEN_STONE);
        buttonItem(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS_BUTTON, TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS);
        buttonItem(TolkienBlocks.MOUNTAIN_STONE_BUTTON, TolkienBlocks.MOUNTAIN_STONE);
        buttonItem(TolkienBlocks.MOUNTAIN_STONE_BRICKS_BUTTON, TolkienBlocks.MOUNTAIN_STONE_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS_BUTTON, TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_MOUNTAIN_STONE_BUTTON, TolkienBlocks.COBBLED_MOUNTAIN_STONE);
        buttonItem(TolkienBlocks.SMOOTH_MOUNTAIN_STONE_BUTTON, TolkienBlocks.SMOOTH_MOUNTAIN_STONE);
        buttonItem(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS_BUTTON, TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS);
        buttonItem(TolkienBlocks.MOSSY_COBBLED_MOUNTAIN_STONE_BUTTON, TolkienBlocks.MOSSY_COBBLED_MOUNTAIN_STONE);
        buttonItem(TolkienBlocks.MOSSY_MOUNTAIN_STONE_BRICKS_BUTTON, TolkienBlocks.MOSSY_MOUNTAIN_STONE_BRICKS);
        buttonItem(TolkienBlocks.ELVEN_MARBLE_BUTTON, TolkienBlocks.ELVEN_MARBLE);
        buttonItem(TolkienBlocks.ELVEN_MARBLE_BRICKS_BUTTON, TolkienBlocks.ELVEN_MARBLE_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS_BUTTON, TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_ELVEN_MARBLE_BUTTON, TolkienBlocks.COBBLED_ELVEN_MARBLE);
        buttonItem(TolkienBlocks.SMOOTH_ELVEN_MARBLE_BUTTON, TolkienBlocks.SMOOTH_ELVEN_MARBLE);
        buttonItem(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS_BUTTON, TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS);
        buttonItem(TolkienBlocks.MOSSY_ELVEN_MARBLE_BRICKS_BUTTON, TolkienBlocks.MOSSY_ELVEN_MARBLE_BRICKS);
        buttonItem(TolkienBlocks.MOSSY_COBBLED_ELVEN_MARBLE_BUTTON, TolkienBlocks.MOSSY_COBBLED_ELVEN_MARBLE);
        buttonItem(TolkienBlocks.IRON_STONE_BUTTON, TolkienBlocks.IRON_STONE);
        buttonItem(TolkienBlocks.IRON_STONE_BRICKS_BUTTON, TolkienBlocks.IRON_STONE_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_IRON_STONE_BRICKS_BUTTON, TolkienBlocks.CRACKED_IRON_STONE_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_IRON_STONE_BUTTON, TolkienBlocks.COBBLED_IRON_STONE);
        buttonItem(TolkienBlocks.SMOOTH_IRON_STONE_BUTTON, TolkienBlocks.SMOOTH_IRON_STONE);
        buttonItem(TolkienBlocks.CHISELED_IRON_STONE_BRICKS_BUTTON, TolkienBlocks.CHISELED_IRON_STONE_BRICKS);
        buttonItem(TolkienBlocks.MOSSY_IRON_STONE_BRICKS_BUTTON, TolkienBlocks.MOSSY_IRON_STONE_BRICKS);
        buttonItem(TolkienBlocks.MOSSY_COBBLED_IRON_STONE_BUTTON, TolkienBlocks.MOSSY_COBBLED_IRON_STONE);
        buttonItem(TolkienBlocks.ANGAINOR_BUTTON, TolkienBlocks.ANGAINOR);
        buttonItem(TolkienBlocks.ANGAINOR_BRICKS_BUTTON, TolkienBlocks.ANGAINOR_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_ANGAINOR_BRICKS_BUTTON, TolkienBlocks.CRACKED_ANGAINOR_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_ANGAINOR_BUTTON, TolkienBlocks.COBBLED_ANGAINOR);
        buttonItem(TolkienBlocks.SMOOTH_ANGAINOR_BUTTON, TolkienBlocks.SMOOTH_ANGAINOR);
        buttonItem(TolkienBlocks.CHISELED_ANGAINOR_BRICKS_BUTTON, TolkienBlocks.CHISELED_ANGAINOR_BRICKS);
        buttonItem(TolkienBlocks.MOSSY_ANGAINOR_BRICKS_BUTTON, TolkienBlocks.MOSSY_ANGAINOR_BRICKS);
        buttonItem(TolkienBlocks.MOSSY_COBBLED_ANGAINOR_BUTTON, TolkienBlocks.MOSSY_COBBLED_ANGAINOR);

        wallItem(TolkienBlocks.WALL_DWARVEN_STONE, TolkienBlocks.DWARVEN_STONE);
        wallItem(TolkienBlocks.WALL_DWARVEN_STONE_BRICKS, TolkienBlocks.DWARVEN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_DWARVEN_STONE_BRICKS, TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_DWARVEN_STONE, TolkienBlocks.COBBLED_DWARVEN_STONE);
        wallItem(TolkienBlocks.WALL_MOSSY_COBBLED_DWARVEN_STONE, TolkienBlocks.MOSSY_COBBLED_DWARVEN_STONE);
        wallItem(TolkienBlocks.WALL_MOSSY_DWARVEN_STONE_BRICKS, TolkienBlocks.MOSSY_DWARVEN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_MOSSY_COBBLED_DWARVEN_STONE, TolkienBlocks.MOSSY_COBBLED_DWARVEN_STONE);
        wallItem(TolkienBlocks.WALL_SMOOTH_DWARVEN_STONE, TolkienBlocks.SMOOTH_DWARVEN_STONE);
        wallItem(TolkienBlocks.WALL_CHISELED_DWARVEN_STONE_BRICKS, TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_DARK_STONE, TolkienBlocks.DARK_STONE);
        wallItem(TolkienBlocks.WALL_MOSSY_COBBLED_DARK_STONE, TolkienBlocks.MOSSY_COBBLED_DARK_STONE);
        wallItem(TolkienBlocks.WALL_MOSSY_DARK_STONE_BRICKS, TolkienBlocks.MOSSY_DARK_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_DARK_STONE_BRICKS, TolkienBlocks.DARK_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_DARK_STONE_BRICKS, TolkienBlocks.CRACKED_DARK_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_DARK_STONE, TolkienBlocks.COBBLED_DARK_STONE);
        wallItem(TolkienBlocks.WALL_SMOOTH_DARK_STONE, TolkienBlocks.SMOOTH_DARK_STONE);
        wallItem(TolkienBlocks.WALL_CHISELED_DARK_STONE_BRICKS, TolkienBlocks.CHISELED_DARK_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_MOUNTAIN_STONE, TolkienBlocks.MOUNTAIN_STONE);
        wallItem(TolkienBlocks.WALL_MOUNTAIN_STONE_BRICKS, TolkienBlocks.MOUNTAIN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_MOUNTAIN_STONE_BRICKS, TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_MOUNTAIN_STONE, TolkienBlocks.COBBLED_MOUNTAIN_STONE);
        wallItem(TolkienBlocks.WALL_SMOOTH_MOUNTAIN_STONE, TolkienBlocks.SMOOTH_MOUNTAIN_STONE);
        wallItem(TolkienBlocks.WALL_CHISELED_MOUNTAIN_STONE_BRICKS, TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_MOSSY_COBBLED_MOUNTAIN_STONE, TolkienBlocks.MOSSY_COBBLED_MOUNTAIN_STONE);
        wallItem(TolkienBlocks.WALL_MOSSY_MOUNTAIN_STONE_BRICKS, TolkienBlocks.MOSSY_MOUNTAIN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_ELVEN_MARBLE, TolkienBlocks.ELVEN_MARBLE);
        wallItem(TolkienBlocks.WALL_MOSSY_ELVEN_MARBLE_BRICKS, TolkienBlocks.MOSSY_ELVEN_MARBLE_BRICKS);
        wallItem(TolkienBlocks.WALL_ELVEN_MARBLE_BRICKS, TolkienBlocks.ELVEN_MARBLE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_ELVEN_MARBLE_BRICKS, TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_ELVEN_MARBLE, TolkienBlocks.COBBLED_ELVEN_MARBLE);
        wallItem(TolkienBlocks.WALL_SMOOTH_ELVEN_MARBLE, TolkienBlocks.SMOOTH_ELVEN_MARBLE);
        wallItem(TolkienBlocks.WALL_CHISELED_ELVEN_MARBLE_BRICKS, TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS);
        wallItem(TolkienBlocks.WALL_MOSSY_COBBLED_ELVEN_MARBLE, TolkienBlocks.MOSSY_COBBLED_ELVEN_MARBLE);
        wallItem(TolkienBlocks.WALL_IRON_STONE, TolkienBlocks.IRON_STONE);
        wallItem(TolkienBlocks.WALL_MOSSY_IRON_STONE_BRICKS, TolkienBlocks.MOSSY_IRON_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_IRON_STONE_BRICKS, TolkienBlocks.IRON_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_IRON_STONE_BRICKS, TolkienBlocks.CRACKED_IRON_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_IRON_STONE, TolkienBlocks.COBBLED_IRON_STONE);
        wallItem(TolkienBlocks.WALL_SMOOTH_IRON_STONE, TolkienBlocks.SMOOTH_IRON_STONE);
        wallItem(TolkienBlocks.WALL_CHISELED_IRON_STONE_BRICKS, TolkienBlocks.CHISELED_IRON_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_MOSSY_COBBLED_IRON_STONE, TolkienBlocks.MOSSY_COBBLED_IRON_STONE);
        wallItem(TolkienBlocks.WALL_ANGAINOR, TolkienBlocks.ANGAINOR);
        wallItem(TolkienBlocks.WALL_MOSSY_ANGAINOR_BRICKS, TolkienBlocks.MOSSY_ANGAINOR_BRICKS);
        wallItem(TolkienBlocks.WALL_ANGAINOR_BRICKS, TolkienBlocks.ANGAINOR_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_ANGAINOR_BRICKS, TolkienBlocks.CRACKED_ANGAINOR_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_ANGAINOR, TolkienBlocks.COBBLED_ANGAINOR);
        wallItem(TolkienBlocks.WALL_SMOOTH_ANGAINOR, TolkienBlocks.SMOOTH_ANGAINOR);
        wallItem(TolkienBlocks.WALL_CHISELED_ANGAINOR_BRICKS, TolkienBlocks.CHISELED_ANGAINOR_BRICKS);
        wallItem(TolkienBlocks.WALL_MOSSY_COBBLED_ANGAINOR, TolkienBlocks.MOSSY_COBBLED_ANGAINOR);

        withExistingParent(TolkienBlocks.LEAFPILE_MALLORN.getId().getPath(), TolkienMobsMain.prefix("block/leafpile_mallorn2"));
        withExistingParent(TolkienBlocks.LEAFPILE_MIRKWOOD.getId().getPath(), TolkienMobsMain.prefix("block/leafpile_mirkwood2"));
        withExistingParent(TolkienBlocks.LEAFPILE_CULUMALDA.getId().getPath(), TolkienMobsMain.prefix("block/leafpile_culumalda2"));
        withExistingParent(TolkienBlocks.LEAFPILE_LEBETHRON.getId().getPath(), TolkienMobsMain.prefix("block/leafpile_lebethron2"));
        withExistingParent(TolkienBlocks.LEAFPILE_FANGORNOAK.getId().getPath(), TolkienMobsMain.prefix("block/leafpile_fangornoak2"));
        withExistingParent(TolkienBlocks.LEAFPILE_DWARVEN_MAPLE.getId().getPath(), TolkienMobsMain.prefix("block/leafpile_dwarven_maple2"));
        generated(TolkienBlocks.MOSS_PATCH.getId().getPath(), TolkienMobsMain.prefix("block/patch/moss"));
        generated(TolkienBlocks.CLOVER_PATCH.getId().getPath(), TolkienMobsMain.prefix("block/patch/clover"));

        sign(TolkienBlocks.MALLORN_SIGN);
        sign(TolkienBlocks.MIRKWOOD_SIGN);
        sign(TolkienBlocks.CULUMALDA_SIGN);
        sign(TolkienBlocks.LEBETHRON_SIGN);
        sign(TolkienBlocks.FANGORNOAK_SIGN);
        sign(TolkienBlocks.DEADWOOD_SIGN);
        sign(TolkienBlocks.DWARVEN_MAPLE_SIGN);

        sign(TolkienBlocks.MALLORN_HANGING_SIGN);
        sign(TolkienBlocks.MIRKWOOD_HANGING_SIGN);
        sign(TolkienBlocks.CULUMALDA_HANGING_SIGN);
        sign(TolkienBlocks.LEBETHRON_HANGING_SIGN);
        sign(TolkienBlocks.FANGORNOAK_HANGING_SIGN);
        sign(TolkienBlocks.DEADWOOD_HANGING_SIGN);
        sign(TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN);

        basicItem(TolkienBlocks.MITHRIL_BARS.asItem());
        basicItem(TolkienBlocks.MORGULIRON_BARS.asItem());
        basicItem(TolkienBlocks.PANE_AMMOLITE.asItem());
        blockWithOwnFlatTexture(TolkienBlocks.LADDER_MALLORN);
        blockWithOwnFlatTexture(TolkienBlocks.LADDER_MIRKWOOD);
        blockWithOwnFlatTexture(TolkienBlocks.LADDER_CULUMALDA);
        blockWithOwnFlatTexture(TolkienBlocks.LADDER_LEBETHRON);
        blockWithOwnFlatTexture(TolkienBlocks.LADDER_FANGORNOAK);
        blockWithOwnFlatTexture(TolkienBlocks.LADDER_DEADWOOD);
        blockWithOwnFlatTexture(TolkienBlocks.LADDER_DWARVEN_MAPLE);

        basicItem(TolkienItems.MALLORN_BOAT.get());
        basicItem(TolkienItems.MALLORN_CHEST_BOAT.get());
        basicItem(TolkienItems.MIRKWOOD_BOAT.get());
        basicItem(TolkienItems.MIRKWOOD_CHEST_BOAT.get());
        basicItem(TolkienItems.CULUMALDA_BOAT.get());
        basicItem(TolkienItems.CULUMALDA_CHEST_BOAT.get());
        basicItem(TolkienItems.LEBETHRON_BOAT.get());
        basicItem(TolkienItems.LEBETHRON_CHEST_BOAT.get());
        basicItem(TolkienItems.FANGORNOAK_BOAT.get());
        basicItem(TolkienItems.FANGORNOAK_CHEST_BOAT.get());
        basicItem(TolkienItems.DEADWOOD_BOAT.get());
        basicItem(TolkienItems.DEADWOOD_CHEST_BOAT.get());
        basicItem(TolkienItems.DWARVEN_MAPLE_BOAT.get());
        basicItem(TolkienItems.DWARVEN_MAPLE_CHEST_BOAT.get());

        basicItem(TolkienItems.RECORD_EREBOR.get());
        basicItem(TolkienItems.RECORD_HOBBITS.get());
        basicItem(TolkienItems.RECORD_MINASTIRITH.get());
        basicItem(TolkienItems.RECORD_MURDERFROG.get());
        basicItem(TolkienItems.RECORD_BOMBADIL.get());
        basicItem(TolkienItems.RECORD_REDER.get());
        basicItem(TolkienItems.RECORD_RIVENDELL.get());
        basicItem(TolkienItems.RECORD_LOTHLORIEN.get());
        basicItem(TolkienItems.RECORD_WILLOW.get());
        basicItem(TolkienItems.RECORD_FUMBLE.get());
        basicItem(TolkienItems.RECORD_EDORAS.get());
        basicItem(TolkienItems.RECORD_WBATTLE.get());
        basicItem(TolkienItems.HYPE_HORN.get());
        basicItem(TolkienItems.HOBBIT_RING.get());
        basicItem(TolkienItems.RING_OF_HOPE.get());

        trinketItem(TolkienItems.TRINKET_RING.get(), TolkienMobsMain.prefix("item/trinket_ring"), TolkienMobsMain.prefix("item/trinket_ring_gem"));
        trinketItem(TolkienItems.TRINKET_AMULET.get(), TolkienMobsMain.prefix("item/trinket_amulet"), TolkienMobsMain.prefix("item/trinket_amulet_gem"));
        trinketItem(TolkienItems.TRINKET_BELT.get(), TolkienMobsMain.prefix("item/trinket_belt"), TolkienMobsMain.prefix("item/trinket_belt_gem"));
        trinketItem(TolkienItems.TRINKET_CHARM.get(), TolkienMobsMain.prefix("item/trinket_charm"), TolkienMobsMain.prefix("item/trinket_charm_gem"));
        trinketItem(TolkienItems.TRINKET_HAT.get(), TolkienMobsMain.prefix("item/trinket_hat"), TolkienMobsMain.prefix("item/trinket_hat_gem"));
        trinketItem(TolkienItems.TRINKET_GLOVE.get(), TolkienMobsMain.prefix("item/trinket_glove"), TolkienMobsMain.prefix("item/trinket_glove_gem"));
        trinketItem(TolkienItems.TRINKET_CLOAK.get(), TolkienMobsMain.prefix("item/trinket_cloak"), TolkienMobsMain.prefix("item/trinket_cloak_gem"));

        handheldItem(TolkienItems.SWORD_WITCHKING.get());
        handheldItem(TolkienItems.SWORD_URUK.get());
        handheldItem(TolkienItems.SWORD_MITHRIL.get());
        handheldItem(TolkienItems.SWORD_MORGULIRON.get());
        handheldItem(TolkienItems.SWORD_AMMOLITE.get());
        handheldItem(TolkienItems.PICKAXE_MITHRIL.get());
        handheldItem(TolkienItems.PICKAXE_MORGULIRON.get());
        handheldItem(TolkienItems.PICKAXE_AMMOLITE.get());
        handheldItem(TolkienItems.AXE_MITHRIL.get());
        handheldItem(TolkienItems.AXE_MORGULIRON.get());
        handheldItem(TolkienItems.AXE_AMMOLITE.get());
        handheldItem(TolkienItems.SHOVEL_MITHRIL.get());
        handheldItem(TolkienItems.SHOVEL_MORGULIRON.get());
        handheldItem(TolkienItems.SHOVEL_AMMOLITE.get());
        handheldItem(TolkienItems.HOE_MITHRIL.get());
        handheldItem(TolkienItems.HOE_MORGULIRON.get());
        handheldItem(TolkienItems.HOE_AMMOLITE.get());
        handheldItem(TolkienItems.SHEARS_MITHRIL.get());
        handheldItem(TolkienItems.SHEARS_MORGULIRON.get());
        handheldItem(TolkienItems.SHEARS_AMMOLITE.get());
        handheldItem(TolkienItems.DWARVEN_HAMMER.get());

        trimmedArmorItem(TolkienItems.HELMET_MITHRIL);
        trimmedArmorItem(TolkienItems.CHESTPLATE_MITHRIL);
        trimmedArmorItem(TolkienItems.LEGGINGS_MITHRIL);
        trimmedArmorItem(TolkienItems.BOOTS_MITHRIL);
        basicItem(TolkienItems.MITHRIL_HORSE_ARMOR.get());
        trimmedArmorItem(TolkienItems.HELMET_MORGULIRON);
        trimmedArmorItem(TolkienItems.CHESTPLATE_MORGULIRON);
        trimmedArmorItem(TolkienItems.LEGGINGS_MORGULIRON);
        trimmedArmorItem(TolkienItems.BOOTS_MORGULIRON);
        basicItem(TolkienItems.MORGULIRON_HORSE_ARMOR.get());
        trimmedArmorItem(TolkienItems.HELMET_AMMOLITE);
        trimmedArmorItem(TolkienItems.CHESTPLATE_AMMOLITE);
        trimmedArmorItem(TolkienItems.LEGGINGS_AMMOLITE);
        trimmedArmorItem(TolkienItems.BOOTS_AMMOLITE);
        basicItem(TolkienItems.AMMOLITE_HORSE_ARMOR.get());

        basicItem(TolkienItems.ITEM_DEV_TOOL.get());
        basicItem(TolkienItems.ITEM_DEV_DEBUG_TOOL.get());
        basicItem(TolkienItems.GALADHRIM_ARROW.get());
        basicItem(TolkienItems.UTUMNO_ARROW.get());

        basicItem(TolkienItems.BRONZE_KEY.get());
        basicItem(TolkienItems.SILVER_KEY.get());
        basicItem(TolkienItems.GOLD_KEY.get());
        basicItem(TolkienItems.MITHRIL_KEY.get());
        basicItem(TolkienItems.MASTER_KEY.get());

        // A spawn egg is vanilla's single egg model drawn through two tints, so every egg needs a
        // definition of its own carrying its two colours; without them the template draws the
        // untinted egg. The colours are the ones the egg was registered with.
        TolkienEntities.SPAWN_EGG_COLORS.forEach((egg, colors) ->
                definition(egg.get(), spawnEggDefinition(colors)));

        // Item model definitions. The game reads dispatch trees and tints from
        // assets/<ns>/items/, so the items below declare them here rather than as overrides
        // on the model, which 1.21.4 ignores.
        definition(TolkienItems.ELVEN_BOW.get(), bowDefinition(TolkienItems.ELVEN_BOW.get()));
        definition(TolkienItems.URUK_BOW.get(), bowDefinition(TolkienItems.URUK_BOW.get()));

        definition(TolkienItems.COIN_POUCH.get(), fullnessDefinition(TolkienItems.COIN_POUCH.get()));
        definition(TolkienItems.KEY_RING.get(), fullnessDefinition(TolkienItems.KEY_RING.get()));

        definition(TolkienItems.TRINKET_RING.get(), trinketDefinition(TolkienItems.TRINKET_RING.get()));
        definition(TolkienItems.TRINKET_AMULET.get(), trinketDefinition(TolkienItems.TRINKET_AMULET.get()));
        definition(TolkienItems.TRINKET_BELT.get(), trinketDefinition(TolkienItems.TRINKET_BELT.get()));
        definition(TolkienItems.TRINKET_CHARM.get(), trinketDefinition(TolkienItems.TRINKET_CHARM.get()));
        definition(TolkienItems.TRINKET_GLOVE.get(), trinketDefinition(TolkienItems.TRINKET_GLOVE.get()));
        definition(TolkienItems.TRINKET_HAT.get(), trinketDefinition(TolkienItems.TRINKET_HAT.get()));
        definition(TolkienItems.TRINKET_CLOAK.get(), trinketDefinition(TolkienItems.TRINKET_CLOAK.get()));

        // The Arda Staff is a GeckoLib model. 1.21.4 has no builtin/entity parent to hang it
        // on, so its definition names the GeckoLib special renderer and keeps item/arda_staff
        // as the base model that carries the display transforms and the particle sprite.
        definition(TolkienItems.ARDA_STAFF.get(), ItemModelDefinition.special(
                modelOf(BuiltInRegistries.ITEM.getKey(TolkienItems.ARDA_STAFF.get())),
                ResourceLocation.fromNamespaceAndPath("geckolib", "geckolib")));
    }

    /** An un-drawn bow, swapping to the three draw frames while it is being used. */
    private JsonObject bowDefinition(Item item) {
        ResourceLocation model = modelOf(BuiltInRegistries.ITEM.getKey(item));
        return ItemModelDefinition.condition(TolkienMobsMain.resLoc("pulling"),
                ItemModelDefinition.rangeDispatch(TolkienMobsMain.resLoc("pull"),
                        ItemModelDefinition.model(withSuffix(model, "_pulling_0")),
                        ItemModelDefinition.entry(0.65F, ItemModelDefinition.model(withSuffix(model, "_pulling_1"))),
                        ItemModelDefinition.entry(0.9F, ItemModelDefinition.model(withSuffix(model, "_pulling_2")))),
                ItemModelDefinition.model(model));
    }

    /** A coin pouch or key ring, switching to a fuller sprite at four thresholds. */
    private JsonObject fullnessDefinition(Item item) {
        ResourceLocation model = modelOf(BuiltInRegistries.ITEM.getKey(item));
        return ItemModelDefinition.rangeDispatch(TolkienMobsMain.resLoc("fullness"),
                ItemModelDefinition.model(model),
                ItemModelDefinition.entry(0.01F, ItemModelDefinition.model(withSuffix(model, "_fullness_0"))),
                ItemModelDefinition.entry(0.33F, ItemModelDefinition.model(withSuffix(model, "_fullness_1"))),
                ItemModelDefinition.entry(0.66F, ItemModelDefinition.model(withSuffix(model, "_fullness_2"))),
                ItemModelDefinition.entry(0.99F, ItemModelDefinition.model(withSuffix(model, "_fullness_3"))));
    }

    /**
     * A spawn egg: vanilla's two-layer egg template, with its base and its spots painted by the
     * two colours the egg registered. The colours are plain RGB, so they are given full alpha
     * here - a tint without one draws nothing.
     */
    private JsonObject spawnEggDefinition(TolkienEntities.SpawnEggColors colors) {
        return ItemModelDefinition.model(mcLoc("item/template_spawn_egg"),
                ItemModelDefinition.constantTint(0xFF000000 | colors.background()),
                ItemModelDefinition.constantTint(0xFF000000 | colors.highlight()));
    }

    /** A trinket: the metal layer untinted, the gem layer tinted by its potion. */
    private JsonObject trinketDefinition(Item item) {
        return ItemModelDefinition.model(modelOf(BuiltInRegistries.ITEM.getKey(item)),
                ItemModelDefinition.constantTint(0xFFFFFFFF),
                ItemModelDefinition.tintSource(TolkienMobsMain.resLoc("trinket")));
    }

    private static ResourceLocation withSuffix(ResourceLocation location, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + suffix);
    }

    private ItemModelBuilder generated(String name, ResourceLocation... layers) {
        return buildItem(name, "item/generated", 0, layers);
    }

    public void sign(Supplier<? extends SignBlock> sign) {
        withExistingParent(blockName(sign), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + blockName(sign)));
    }

    private String blockName(Supplier<? extends Block> block) {
        return BuiltInRegistries.BLOCK.getKey(block.get()).getPath();
    }

    private ItemModelBuilder trinketItem(Item item, ResourceLocation texture, ResourceLocation overlay) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture)
                .texture("layer1", overlay);
    }

    private ItemModelBuilder buildItem(String name, String parent, int emissivity, ResourceLocation... layers) {
        ItemModelBuilder builder = withExistingParent(name, parent);
        for (int i = 0; i < layers.length; i++) {
            builder = builder.texture("layer" + i, layers[i]);
        }
        if (emissivity > 0) builder = builder.customLoader(ItemLayerModelBuilder::begin).emissive(emissivity, emissivity, 0).renderType("minecraft:translucent", 0).end();
        return builder;
    }

    private ItemModelBuilder saplingItem(DeferredBlock<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(MODID,"block/" + item.getId().getPath()));
    }

    public void buttonItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    private void blockWithOwnFlatTexture(DeferredBlock<Block> block) {
        withExistingParent(block.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", blockTexture(block));
    }

    private ResourceLocation blockTexture(DeferredBlock<Block> block) {
        return ResourceLocation.fromNamespaceAndPath(MODID,"block/" + block.getId().getPath());
    }

    public ItemModelBuilder handheldItem(Item item) {
        return basicItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    public ItemModelBuilder handheldItem(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    /**
     * A trimmed piece of armour: one model per trim material, plus an item model definition that
     * picks between them by the stack's trim material. 1.21.4 selects on the material itself - the
     * old {@code trim_type} predicate lived in the model's {@code overrides} array, which the game
     * no longer reads, so trimmed armour kept its untrimmed icon whatever was applied to it.
     */
    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
        if (!(itemDeferredItem.get() instanceof ArmorItem armorItem)) {
            return;
        }

        // 1.21.4 dropped ArmorItem#getEquipmentSlot: the slot lives in the equipable data
        // component now, so read it back from there.
        Equippable equippable = armorItem.components().get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return;
        }

        String armorType = switch (equippable.slot()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> "";
        };

        String armorItemPath = itemDeferredItem.getId().getPath();
        ResourceLocation armorTexture = ResourceLocation.fromNamespaceAndPath(MODID, "item/" + armorItemPath);
        List<ItemModelDefinition.Case> cases = new ArrayList<>();

        for (ResourceKey<TrimMaterial> trimMaterial : TRIM_MATERIALS) {
            String material = trimMaterial.location().getPath();
            String trimModelPath = armorItemPath + "_" + material + "_trim";
            // The trim sprite is cut at load time by vanilla's paletted permutation atlas rather
            // than shipped as a png, so the file helper is told it exists instead of asked.
            ResourceLocation trimTexture = ResourceLocation.withDefaultNamespace("trims/items/" + armorType + "_trim_" + material);
            existingFileHelper.trackGenerated(trimTexture, PackType.CLIENT_RESOURCES, ".png", "textures");

            getBuilder(trimModelPath)
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", armorTexture)
                    .texture("layer1", trimTexture);

            cases.add(ItemModelDefinition.when(trimMaterial.location().toString(),
                    ItemModelDefinition.model(ResourceLocation.fromNamespaceAndPath(MODID, "item/" + trimModelPath))));
        }

        // The untrimmed icon, drawn unless one of the materials above is applied.
        withExistingParent(armorItemPath, mcLoc("item/generated")).texture("layer0", armorTexture);

        definition(itemDeferredItem.get(), ItemModelDefinition.select(mcLoc("trim_material"),
                ItemModelDefinition.model(armorTexture), cases.toArray(new ItemModelDefinition.Case[0])));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Items";
    }
}
