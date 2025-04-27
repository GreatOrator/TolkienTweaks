package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienFluids;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienItemModelProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

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
        buttonItem(TolkienBlocks.DWARVEN_STONE_BUTTON, TolkienBlocks.DWARVEN_STONE);
        buttonItem(TolkienBlocks.DWARVEN_STONE_BRICKS_BUTTON, TolkienBlocks.DWARVEN_STONE_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS_BUTTON, TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_DWARVEN_STONE_BUTTON, TolkienBlocks.COBBLED_DWARVEN_STONE);
        buttonItem(TolkienBlocks.SMOOTH_DWARVEN_STONE_BUTTON, TolkienBlocks.SMOOTH_DWARVEN_STONE);
        buttonItem(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS_BUTTON, TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS);
        buttonItem(TolkienBlocks.ELVEN_MARBLE_BUTTON, TolkienBlocks.ELVEN_MARBLE);
        buttonItem(TolkienBlocks.ELVEN_MARBLE_BRICKS_BUTTON, TolkienBlocks.ELVEN_MARBLE_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS_BUTTON, TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_ELVEN_MARBLE_BUTTON, TolkienBlocks.COBBLED_ELVEN_MARBLE);
        buttonItem(TolkienBlocks.SMOOTH_ELVEN_MARBLE_BUTTON, TolkienBlocks.SMOOTH_ELVEN_MARBLE);
        buttonItem(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS_BUTTON, TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS);
        buttonItem(TolkienBlocks.MOUNTAIN_STONE_BUTTON, TolkienBlocks.MOUNTAIN_STONE);
        buttonItem(TolkienBlocks.MOUNTAIN_STONE_BRICKS_BUTTON, TolkienBlocks.MOUNTAIN_STONE_BRICKS);
        buttonItem(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS_BUTTON, TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS);
        buttonItem(TolkienBlocks.COBBLED_MOUNTAIN_STONE_BUTTON, TolkienBlocks.COBBLED_MOUNTAIN_STONE);
        buttonItem(TolkienBlocks.SMOOTH_MOUNTAIN_STONE_BUTTON, TolkienBlocks.SMOOTH_MOUNTAIN_STONE);
        buttonItem(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS_BUTTON, TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS);

        wallItem(TolkienBlocks.WALL_DWARVEN_STONE, TolkienBlocks.DWARVEN_STONE);
        wallItem(TolkienBlocks.WALL_DWARVEN_STONE_BRICKS, TolkienBlocks.DWARVEN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_DWARVEN_STONE_BRICKS, TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_DWARVEN_STONE, TolkienBlocks.COBBLED_DWARVEN_STONE);
        wallItem(TolkienBlocks.WALL_SMOOTH_DWARVEN_STONE, TolkienBlocks.SMOOTH_DWARVEN_STONE);
        wallItem(TolkienBlocks.WALL_CHISELED_DWARVEN_STONE_BRICKS, TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_DARK_STONE, TolkienBlocks.DARK_STONE);
        wallItem(TolkienBlocks.WALL_DARK_STONE_BRICKS, TolkienBlocks.DARK_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_DARK_STONE_BRICKS, TolkienBlocks.CRACKED_DARK_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_DARK_STONE, TolkienBlocks.COBBLED_DARK_STONE);
        wallItem(TolkienBlocks.WALL_SMOOTH_DARK_STONE, TolkienBlocks.SMOOTH_DARK_STONE);
        wallItem(TolkienBlocks.WALL_CHISELED_DARK_STONE_BRICKS, TolkienBlocks.CHISELED_DARK_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_ELVEN_MARBLE, TolkienBlocks.ELVEN_MARBLE);
        wallItem(TolkienBlocks.WALL_ELVEN_MARBLE_BRICKS, TolkienBlocks.ELVEN_MARBLE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_ELVEN_MARBLE_BRICKS, TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_ELVEN_MARBLE, TolkienBlocks.COBBLED_ELVEN_MARBLE);
        wallItem(TolkienBlocks.WALL_SMOOTH_ELVEN_MARBLE, TolkienBlocks.SMOOTH_ELVEN_MARBLE);
        wallItem(TolkienBlocks.WALL_CHISELED_ELVEN_MARBLE_BRICKS, TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS);
        wallItem(TolkienBlocks.WALL_MOUNTAIN_STONE, TolkienBlocks.MOUNTAIN_STONE);
        wallItem(TolkienBlocks.WALL_MOUNTAIN_STONE_BRICKS, TolkienBlocks.MOUNTAIN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_CRACKED_MOUNTAIN_STONE_BRICKS, TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS);
        wallItem(TolkienBlocks.WALL_COBBLED_MOUNTAIN_STONE, TolkienBlocks.COBBLED_MOUNTAIN_STONE);
        wallItem(TolkienBlocks.WALL_SMOOTH_MOUNTAIN_STONE, TolkienBlocks.SMOOTH_MOUNTAIN_STONE);
        wallItem(TolkienBlocks.WALL_CHISELED_MOUNTAIN_STONE_BRICKS, TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS);

        generated(TolkienBlocks.LEAFPILE_MALLORN.getId().getPath(), TolkienMobsMain.prefix("block/leaves_mallorn"));
        generated(TolkienBlocks.LEAFPILE_MIRKWOOD.getId().getPath(), TolkienMobsMain.prefix("block/leaves_mirkwood"));
        generated(TolkienBlocks.LEAFPILE_CULUMALDA.getId().getPath(), TolkienMobsMain.prefix("block/leaves_culumalda"));
        generated(TolkienBlocks.LEAFPILE_LEBETHRON.getId().getPath(), TolkienMobsMain.prefix("block/leaves_lebethron"));
        generated(TolkienBlocks.LEAFPILE_FANGORNOAK.getId().getPath(), TolkienMobsMain.prefix("block/leaves_fangornoak"));
        generated(TolkienBlocks.LEAFPILE_DWARVEN_MAPLE.getId().getPath(), TolkienMobsMain.prefix("block/leaves_dwarven_maple"));
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

        withExistingParent(TolkienEntities.EGG_TTMGECKO.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMRAT.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMSQUIRREL.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMFROG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMTHRUSH.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMCREBAIN.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMSWARM.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMGREATEAGLE.getId().getPath(), mcLoc("item/template_spawn_egg"));

        withExistingParent(TolkienEntities.EGG_TTMAUROCH.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMMUMAKIL.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMGOAT.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMSHADOWFAX.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMNAZGULSTEED.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMGOLLUM.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMISTARI.getId().getPath(), mcLoc("item/template_spawn_egg"));

        withExistingParent(TolkienEntities.EGG_TTMHUMAN.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMDWARF.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMELVES.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMHOBBIT.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMSOUTHRON.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMORC_TRADER.getId().getPath(), mcLoc("item/template_spawn_egg"));

        withExistingParent(TolkienEntities.EGG_TTMBARROW.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMOATHBREAKER.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMFELLSPIRIT.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMBRIGAND.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMHARADRIM.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMROMIEWALKER.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMMIMICCHEST.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMMORDORORC.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMURUKHAI.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMDUERGAR.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMGOBLIN.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMMIRKWOODSPIDER.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMROCKGOLEM.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMHURON.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMMINOTAUR.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMDEEPCLAW.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMTROLL.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMTREEENT.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMSWAMPHAG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMWARG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMELEMENTALGOLEM.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMNAZGUL.getId().getPath(), mcLoc("item/template_spawn_egg"));

        withExistingParent(TolkienEntities.EGG_TTMGOBLINKING.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMBALROG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMSHELOB.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMWITCHKING.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMWATCHER.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMMITHRILGOLEM.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMMORGULIRONGOLEM.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMGWAHIR.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(TolkienEntities.EGG_TTMFELLBEAST.getId().getPath(), mcLoc("item/template_spawn_egg"));
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

    // Shoutout to El_Redstoniano for making this
    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
        final String MOD_ID = MODID; // Change this to your mod id

        if(itemDeferredItem.get() instanceof ArmorItem armorItem) {
            trimMaterials.forEach((trimMaterial, value) -> {
                float trimValue = value;

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = armorItem.toString();
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath())
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(itemDeferredItem.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace()  + ":item/" + trimNameResLoc.getPath()))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                        "item/" + itemDeferredItem.getId().getPath()));
            });
        }
    }
}
