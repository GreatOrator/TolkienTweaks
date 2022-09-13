package com.greatorator.tolkientweaks.datagen;

import com.greatorator.tolkientweaks.TolkienContent;
import com.greatorator.tolkientweaks.TolkienTweaks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;


/**
 * Created by brandon3055 on 28/2/20.
 */
public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, TolkienTweaks.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Block Items - Metals & Gems
        getBuilder(TolkienContent.COIN_POUCH.get().getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(modLoc("item/coin_pouch")));
        simpleItem(TolkienContent.ITEM_COIN_BRONZE.get());
        simpleItem(TolkienContent.ITEM_COIN_SILVER.get());
        simpleItem(TolkienContent.ITEM_COIN_GOLD.get());
        simpleItem(TolkienContent.ITEM_COIN_MITHRIL.get());
//        simpleItem(TolkienContent.COIN_POUCH.get());
//        blockItem(TTMContent.ORE_MITHRIL.get());
//        blockItem(TTMContent.FENCE_GATE_MALLORN.get(), modLoc("block/fence_gate_mallorn_fence_gate"));

        // Sleeping Bags
//        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
//
//        List<String> sleepingBags = Arrays.asList("black", "blue", "brown", "cyan", "gray", "green", "light_blue", "light_gray", "lime", "magenta", "orange", "pink", "purple", "red", "white", "yellow");
//        for (String color : sleepingBags) {
//            simpleMod(itemGenerated, "item/sleeping_bag_"+color);
//        }
//        getBuilder(TTMContent.BOTTLE_FANCY.get().getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(modLoc("item/bottle_empty")));

        //region Trinkets
//        trinketItem(TTMContent.TRINKET_AMULET.get(), modLoc("item/trinket_amulet"), modLoc("item/trinket_amulet"));

        //region Spawn Eggs
            // Ambient
//        eggItem(EntityGenerator.EGG_TTMRAT.get());
    }

    private ItemModelBuilder simpleMod(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", modLoc(name));
    }

    private void simpleItem(Item item) {
        simpleItem(item, "item");
    }

    @SuppressWarnings("ConstantConditions")
    private void simpleItem(Item item, String textureFolder) {
        ResourceLocation reg = item.getRegistryName();
        simpleItem(item, new ResourceLocation(reg.getNamespace(), textureFolder + "/" + reg.getPath()));
    }

    @SuppressWarnings("ConstantConditions")
    private void simpleItem(Item item, ResourceLocation texture) {
        ResourceLocation reg = item.getRegistryName();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture);
    }

    private void handheldItem(Item item) {
        handheldItem(item, "item");
    }

    @SuppressWarnings("ConstantConditions")
    private void handheldItem(Item item, String textureFolder) {
        ResourceLocation reg = item.getRegistryName();
        handheldItem(item, new ResourceLocation(reg.getNamespace(), textureFolder + "/" + reg.getPath()));
    }

    @SuppressWarnings("ConstantConditions")
    private void handheldItem(Item item, ResourceLocation texture) {
        ResourceLocation reg = item.getRegistryName();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", texture);
    }

    @SuppressWarnings("ConstantConditions")
    private void trinketItem(Item item, ResourceLocation texture1, ResourceLocation texture2) {
        ResourceLocation reg = item.getRegistryName();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture1)
                .texture("layer1", texture2 + "_gem");
    }

    @SuppressWarnings("ConstantConditions")
    private void eggItem(Item item) {
        ResourceLocation reg = item.getRegistryName();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/template_spawn_egg"));
    }

    private void blockItem(Block block) {
        if (block == null) return;
        ResourceLocation reg = block.getRegistryName();
        blockItem(block, new ResourceLocation(reg.getNamespace(), "block/" + reg.getPath()));
    }

    private void blockItem(Block block, ResourceLocation blockModel) {
        if (block == null) return;
        ResourceLocation reg = block.getRegistryName();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile(blockModel));
    }

    private void dummyModel(Block block) {
        dummyModel(block.asItem());
    }

    private void dummyModel(Item item) {
        getBuilder(item.getRegistryName().getPath())
                .parent(new ModelFile.UncheckedModelFile("builtin/generated"));
    }

    @Override
    public String getName() {
        return "Tolkien Tweaks - Mobs Edition Item Models";
    }
}
