package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienFluids;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Objects;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienItemModelProvider extends ItemModelProvider {
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

        // Backpack Upgrades
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_BASE.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_CRAFTING.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_SLEEPING.get());
        basicItem(TolkienItems.ITEM_BACKPACK_UPGRADE_CAMPFIRE.get());

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

        // Crop
        basicItem(TolkienItems.PIPEWEED_ITEM.get());
        basicItem(TolkienItems.PIPEWEED_SEEDS.get());

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
        saplingItem(TolkienBlocks.SAPLING_MIRKWOOD);
        saplingItem(TolkienBlocks.SAPLING_MALLORN);
        saplingItem(TolkienBlocks.SAPLING_CULUMALDA);
        saplingItem(TolkienBlocks.SAPLING_LEBETHRON);
        saplingItem(TolkienBlocks.SAPLING_FANGORNOAK);
        saplingItem(TolkienBlocks.SAPLING_DEADWOOD);
        saplingItem(TolkienBlocks.MUSHROOM_BLOOM_DECAY);
        saplingItem(TolkienBlocks.MUSHROOM_DECAY_BLOOM);

        basicItem(TolkienItems.RECORD_RIVENDELL.get());

        buildTool(TolkienItems.SWORD_MITHRIL.get());
        buildTool(TolkienItems.SWORD_MORGULIRON.get());
        buildTool(TolkienItems.SWORD_AMMOLITE.get());
        buildTool(TolkienItems.PICKAXE_MITHRIL.get());
        buildTool(TolkienItems.PICKAXE_MORGULIRON.get());
        buildTool(TolkienItems.PICKAXE_AMMOLITE.get());
        buildTool(TolkienItems.AXE_MITHRIL.get());
        buildTool(TolkienItems.AXE_MORGULIRON.get());
        buildTool(TolkienItems.AXE_AMMOLITE.get());
        buildTool(TolkienItems.SHOVEL_MITHRIL.get());
        buildTool(TolkienItems.SHOVEL_MORGULIRON.get());
        buildTool(TolkienItems.SHOVEL_AMMOLITE.get());
        buildTool(TolkienItems.HOE_MITHRIL.get());
        buildTool(TolkienItems.HOE_MORGULIRON.get());
        buildTool(TolkienItems.HOE_AMMOLITE.get());
        buildTool(TolkienItems.SHEARS_MITHRIL.get());
        buildTool(TolkienItems.SHEARS_MORGULIRON.get());
        buildTool(TolkienItems.SHEARS_AMMOLITE.get());

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

    public ItemModelBuilder buildTool(Item item) {
        return basicItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    public ItemModelBuilder buildTool(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }
}
