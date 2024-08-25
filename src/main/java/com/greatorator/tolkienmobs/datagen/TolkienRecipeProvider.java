package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.util.TolkienTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public TolkienRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
            //Smelting & Blasting
        List<ItemLike> MITHRIL_SMELTABLES = List.of(TolkienItems.RAW_MITHRIL,
                TolkienBlocks.ORE_MITHRIL, TolkienBlocks.ORE_END_MITHRIL, TolkienBlocks.ORE_NETHER_MITHRIL, TolkienBlocks.ORE_DEEPSLATE_MITHRIL);
        List<ItemLike> MORGULIRON_SMELTABLES = List.of(TolkienItems.RAW_MORGULIRON,
                TolkienBlocks.ORE_MORGULIRON, TolkienBlocks.ORE_END_MORGULIRON, TolkienBlocks.ORE_NETHER_MORGULIRON, TolkienBlocks.ORE_DEEPSLATE_MORGULIRON);
        List<ItemLike> AMMOLITE_SMELTABLES = List.of(TolkienItems.GEM_AMMOLITE,
                TolkienBlocks.ORE_AMMOLITE, TolkienBlocks.ORE_END_AMMOLITE, TolkienBlocks.ORE_NETHER_AMMOLITE, TolkienBlocks.ORE_DEEPSLATE_AMMOLITE);

        oreSmelting(pRecipeOutput, MITHRIL_SMELTABLES, RecipeCategory.MISC, TolkienItems.INGOT_MITHRIL.get(), 0.25f, 200, "ingot_mithril");
        oreSmelting(pRecipeOutput, MORGULIRON_SMELTABLES, RecipeCategory.MISC, TolkienItems.INGOT_MORGULIRON.get(), 0.25f, 200, "ingot_morguliron");
        oreSmelting(pRecipeOutput, AMMOLITE_SMELTABLES, RecipeCategory.MISC, TolkienItems.GEM_AMMOLITE.get(), 0.25f, 200, "gem_ammolite");

        oreBlasting(pRecipeOutput, MITHRIL_SMELTABLES, RecipeCategory.MISC, TolkienItems.INGOT_MITHRIL.get(), 0.25f, 100, "ingot_mithril");
        oreBlasting(pRecipeOutput, MORGULIRON_SMELTABLES, RecipeCategory.MISC, TolkienItems.INGOT_MORGULIRON.get(), 0.25f, 100, "ingot_morguliron");
        oreBlasting(pRecipeOutput, AMMOLITE_SMELTABLES, RecipeCategory.MISC, TolkienItems.GEM_AMMOLITE.get(), 0.25f, 100, "gem_ammolite");

        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(TolkienItems.DUST_MITHRIL), RecipeCategory.MISC, TolkienItems.INGOT_MITHRIL, 0, 200)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(TolkienItems.DUST_MITHRIL.get()))
                .save(pRecipeOutput, TolkienMobsMain.prefix(TolkienItems.INGOT_MITHRIL.get().getDescriptionId() + "_from_smelting"));
        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(TolkienItems.DUST_MORGULIRON), RecipeCategory.MISC, TolkienItems.INGOT_MORGULIRON, 0, 200)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(TolkienItems.DUST_MORGULIRON.get()))
                .save(pRecipeOutput, TolkienMobsMain.prefix(TolkienItems.INGOT_MORGULIRON.get().getDescriptionId() + "_from_smelting"));

        makeMaterialRecipes(pRecipeOutput, TolkienItems.INGOT_MITHRIL.get(), TolkienItems.NUGGET_MITHRIL.get(), TolkienBlocks.BLOCK_MITHRIL.get());
        makeMaterialRecipes(pRecipeOutput, TolkienItems.INGOT_MORGULIRON.get(), TolkienItems.NUGGET_MORGULIRON.get(), TolkienBlocks.BLOCK_MORGULIRON.get());
        makeGemRecipes(pRecipeOutput, TolkienItems.GEM_AMMOLITE.get(), TolkienBlocks.BLOCK_AMMOLITE.get());

        //Everything Else
        stairBuilder(TolkienBlocks.STAIRS_MITHRIL.get(), Ingredient.of(TolkienBlocks.BLOCK_MITHRIL.get())).group("mithril")
                .unlockedBy("has_mithril", has(TolkienBlocks.BLOCK_MITHRIL.get())).save(pRecipeOutput);
        stairBuilder(TolkienBlocks.STAIRS_MORGULIRON.get(), Ingredient.of(TolkienBlocks.BLOCK_MORGULIRON.get())).group("morguliron")
                .unlockedBy("has_morguliron", has(TolkienBlocks.BLOCK_MORGULIRON.get())).save(pRecipeOutput);
        stairBuilder(TolkienBlocks.STAIRS_MALLORN.get(), Ingredient.of(TolkienBlocks.PLANKS_MALLORN.get())).group("mallorn")
                .unlockedBy("has_mallorn", has(TolkienBlocks.PLANKS_MALLORN.get())).save(pRecipeOutput);
        stairBuilder(TolkienBlocks.STAIRS_MIRKWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_MIRKWOOD.get())).group("mirkwood")
                .unlockedBy("has_mirkwood", has(TolkienBlocks.PLANKS_MIRKWOOD.get())).save(pRecipeOutput);
        stairBuilder(TolkienBlocks.STAIRS_CULUMALDA.get(), Ingredient.of(TolkienBlocks.PLANKS_CULUMALDA.get())).group("culumalda")
                .unlockedBy("has_culumalda", has(TolkienBlocks.PLANKS_CULUMALDA.get())).save(pRecipeOutput);
        stairBuilder(TolkienBlocks.STAIRS_LEBETHRON.get(), Ingredient.of(TolkienBlocks.PLANKS_LEBETHRON.get())).group("lebethron")
                .unlockedBy("has_lebethron", has(TolkienBlocks.PLANKS_LEBETHRON.get())).save(pRecipeOutput);
        stairBuilder(TolkienBlocks.STAIRS_FANGORNOAK.get(), Ingredient.of(TolkienBlocks.PLANKS_FANGORNOAK.get())).group("fangornoak")
                .unlockedBy("has_fangornoak", has(TolkienBlocks.PLANKS_FANGORNOAK.get())).save(pRecipeOutput);
        stairBuilder(TolkienBlocks.STAIRS_DEADWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_DEADWOOD.get())).group("deadwood")
                .unlockedBy("has_deadwood", has(TolkienBlocks.PLANKS_DEADWOOD.get())).save(pRecipeOutput);

        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.SLAB_MITHRIL.get(), TolkienBlocks.BLOCK_MITHRIL.get());
        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.SLAB_MORGULIRON.get(), TolkienBlocks.BLOCK_MORGULIRON.get());
        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.SLAB_MALLORN.get(), TolkienBlocks.PLANKS_MALLORN.get());
        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.SLAB_MIRKWOOD.get(), TolkienBlocks.PLANKS_MIRKWOOD.get());
        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.SLAB_CULUMALDA.get(), TolkienBlocks.PLANKS_CULUMALDA.get());
        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.SLAB_LEBETHRON.get(), TolkienBlocks.PLANKS_LEBETHRON.get());
        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.SLAB_FANGORNOAK.get(), TolkienBlocks.PLANKS_FANGORNOAK.get());
        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.SLAB_DEADWOOD.get(), TolkienBlocks.PLANKS_DEADWOOD.get());

        pressurePlate(pRecipeOutput, TolkienBlocks.PRESSURE_PLATE_MITHRIL.get(), TolkienBlocks.BLOCK_MITHRIL.get());
        pressurePlate(pRecipeOutput, TolkienBlocks.PRESSURE_PLATE_MORGULIRON.get(), TolkienBlocks.BLOCK_MORGULIRON.get());
        pressurePlate(pRecipeOutput, TolkienBlocks.PRESSURE_PLATE_MALLORN.get(), TolkienBlocks.PLANKS_MALLORN.get());
        pressurePlate(pRecipeOutput, TolkienBlocks.PRESSURE_PLATE_MIRKWOOD.get(), TolkienBlocks.PLANKS_MIRKWOOD.get());
        pressurePlate(pRecipeOutput, TolkienBlocks.PRESSURE_PLATE_CULUMALDA.get(), TolkienBlocks.PLANKS_CULUMALDA.get());
        pressurePlate(pRecipeOutput, TolkienBlocks.PRESSURE_PLATE_LEBETHRON.get(), TolkienBlocks.PLANKS_LEBETHRON.get());
        pressurePlate(pRecipeOutput, TolkienBlocks.PRESSURE_PLATE_FANGORNOAK.get(), TolkienBlocks.PLANKS_FANGORNOAK.get());
        pressurePlate(pRecipeOutput, TolkienBlocks.PRESSURE_PLATE_DEADWOOD.get(), TolkienBlocks.PLANKS_DEADWOOD.get());

        buttonBuilder(TolkienBlocks.MITHRIL_BUTTON.get(), Ingredient.of(TolkienBlocks.BLOCK_MITHRIL.get())).group("mithril")
                .unlockedBy("has_mithril", has(TolkienBlocks.BLOCK_MITHRIL.get())).save(pRecipeOutput);
        buttonBuilder(TolkienBlocks.MORGULIRON_BUTTON.get(), Ingredient.of(TolkienBlocks.BLOCK_MORGULIRON.get())).group("morguliron")
                .unlockedBy("has_morguliron", has(TolkienBlocks.BLOCK_MORGULIRON.get())).save(pRecipeOutput);
        buttonBuilder(TolkienBlocks.MALLORN_BUTTON.get(), Ingredient.of(TolkienBlocks.PLANKS_MALLORN.get())).group("mallorn")
                .unlockedBy("has_mallorn", has(TolkienBlocks.PLANKS_MALLORN.get())).save(pRecipeOutput);
        buttonBuilder(TolkienBlocks.MIRKWOOD_BUTTON.get(), Ingredient.of(TolkienBlocks.PLANKS_MIRKWOOD.get())).group("mirkwood")
                .unlockedBy("has_mirkwood", has(TolkienBlocks.PLANKS_MIRKWOOD.get())).save(pRecipeOutput);
        buttonBuilder(TolkienBlocks.CULUMALDA_BUTTON.get(), Ingredient.of(TolkienBlocks.PLANKS_CULUMALDA.get())).group("culumalda")
                .unlockedBy("has_culumalda", has(TolkienBlocks.PLANKS_CULUMALDA.get())).save(pRecipeOutput);
        buttonBuilder(TolkienBlocks.LEBETHRON_BUTTON.get(), Ingredient.of(TolkienBlocks.PLANKS_LEBETHRON.get())).group("lebethron")
                .unlockedBy("has_lebethron", has(TolkienBlocks.PLANKS_LEBETHRON.get())).save(pRecipeOutput);
        buttonBuilder(TolkienBlocks.FANGORNOAK_BUTTON.get(), Ingredient.of(TolkienBlocks.PLANKS_FANGORNOAK.get())).group("fangornoak")
                .unlockedBy("has_fangornoak", has(TolkienBlocks.PLANKS_FANGORNOAK.get())).save(pRecipeOutput);
        buttonBuilder(TolkienBlocks.DEADWOOD_BUTTON.get(), Ingredient.of(TolkienBlocks.PLANKS_DEADWOOD.get())).group("deadwood")
                .unlockedBy("has_deadwood", has(TolkienBlocks.PLANKS_DEADWOOD.get())).save(pRecipeOutput);

        doorBuilder(TolkienBlocks.DOOR_MITHRIL.get(), Ingredient.of(TolkienBlocks.BLOCK_MITHRIL.get())).group("mithril")
                .unlockedBy("has_mithril", has(TolkienBlocks.BLOCK_MITHRIL.get())).save(pRecipeOutput);
        doorBuilder(TolkienBlocks.DOOR_MORGULIRON.get(), Ingredient.of(TolkienBlocks.BLOCK_MORGULIRON.get())).group("morguliron")
                .unlockedBy("has_morguliron", has(TolkienBlocks.BLOCK_MORGULIRON.get())).save(pRecipeOutput);
        doorBuilder(TolkienBlocks.DOOR_DURIN.get(), Ingredient.of(TolkienBlocks.BLOCK_AMMOLITE.get())).group("ammolite")
                .unlockedBy("has_ammolite", has(TolkienBlocks.BLOCK_AMMOLITE.get())).save(pRecipeOutput);
        doorBuilder(TolkienBlocks.DOOR_MALLORN.get(), Ingredient.of(TolkienBlocks.PLANKS_MALLORN.get())).group("mallorn")
                .unlockedBy("has_mallorn", has(TolkienBlocks.PLANKS_MALLORN.get())).save(pRecipeOutput);
        doorBuilder(TolkienBlocks.DOOR_MIRKWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_MIRKWOOD.get())).group("mirkwood")
                .unlockedBy("has_mirkwood", has(TolkienBlocks.PLANKS_MIRKWOOD.get())).save(pRecipeOutput);
        doorBuilder(TolkienBlocks.DOOR_CULUMALDA.get(), Ingredient.of(TolkienBlocks.PLANKS_CULUMALDA.get())).group("culumalda")
                .unlockedBy("has_culumalda", has(TolkienBlocks.PLANKS_CULUMALDA.get())).save(pRecipeOutput);
        doorBuilder(TolkienBlocks.DOOR_LEBETHRON.get(), Ingredient.of(TolkienBlocks.PLANKS_LEBETHRON.get())).group("lebethron")
                .unlockedBy("has_lebethron", has(TolkienBlocks.PLANKS_LEBETHRON.get())).save(pRecipeOutput);
        doorBuilder(TolkienBlocks.DOOR_FANGORNOAK.get(), Ingredient.of(TolkienBlocks.PLANKS_FANGORNOAK.get())).group("fangornoak")
                .unlockedBy("has_fangornoak", has(TolkienBlocks.PLANKS_FANGORNOAK.get())).save(pRecipeOutput);
        doorBuilder(TolkienBlocks.DOOR_DEADWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_DEADWOOD.get())).group("deadwood")
                .unlockedBy("has_deadwood", has(TolkienBlocks.PLANKS_DEADWOOD.get())).save(pRecipeOutput);

        trapdoorBuilder(TolkienBlocks.TRAPDOOR_MITHRIL.get(), Ingredient.of(TolkienBlocks.BLOCK_MITHRIL.get())).group("mithril")
                .unlockedBy("has_mithril", has(TolkienBlocks.BLOCK_MITHRIL.get())).save(pRecipeOutput);
        trapdoorBuilder(TolkienBlocks.TRAPDOOR_MORGULIRON.get(), Ingredient.of(TolkienBlocks.BLOCK_MORGULIRON.get())).group("morguliron")
                .unlockedBy("has_morguliron", has(TolkienBlocks.BLOCK_MORGULIRON.get())).save(pRecipeOutput);
        trapdoorBuilder(TolkienBlocks.TRAPDOOR_MALLORN.get(), Ingredient.of(TolkienBlocks.PLANKS_MALLORN.get())).group("mallorn")
                .unlockedBy("has_mallorn", has(TolkienBlocks.PLANKS_MALLORN.get())).save(pRecipeOutput);
        trapdoorBuilder(TolkienBlocks.TRAPDOOR_MIRKWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_MIRKWOOD.get())).group("mirkwood")
                .unlockedBy("has_mirkwood", has(TolkienBlocks.PLANKS_MIRKWOOD.get())).save(pRecipeOutput);
        trapdoorBuilder(TolkienBlocks.TRAPDOOR_CULUMALDA.get(), Ingredient.of(TolkienBlocks.PLANKS_CULUMALDA.get())).group("culumalda")
                .unlockedBy("has_culumalda", has(TolkienBlocks.PLANKS_CULUMALDA.get())).save(pRecipeOutput);
        trapdoorBuilder(TolkienBlocks.TRAPDOOR_LEBETHRON.get(), Ingredient.of(TolkienBlocks.PLANKS_LEBETHRON.get())).group("lebethron")
                .unlockedBy("has_lebethron", has(TolkienBlocks.PLANKS_LEBETHRON.get())).save(pRecipeOutput);
        trapdoorBuilder(TolkienBlocks.TRAPDOOR_FANGORNOAK.get(), Ingredient.of(TolkienBlocks.PLANKS_FANGORNOAK.get())).group("fangornoak")
                .unlockedBy("has_fangornoak", has(TolkienBlocks.PLANKS_FANGORNOAK.get())).save(pRecipeOutput);
        trapdoorBuilder(TolkienBlocks.TRAPDOOR_DEADWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_DEADWOOD.get())).group("deadwood")
                .unlockedBy("has_deadwood", has(TolkienBlocks.PLANKS_DEADWOOD.get())).save(pRecipeOutput);

        wall(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.WALL_MITHRIL.get(), TolkienBlocks.BLOCK_MITHRIL.get());
        wall(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.WALL_MORGULIRON.get(), TolkienBlocks.BLOCK_MORGULIRON.get());
        fenceBuilder(TolkienBlocks.FENCE_MALLORN.get(), Ingredient.of(TolkienBlocks.PLANKS_MALLORN.get())).group("mallorn")
                .unlockedBy("has_mallorn", has(TolkienBlocks.PLANKS_MALLORN.get())).save(pRecipeOutput);
        fenceBuilder(TolkienBlocks.FENCE_MIRKWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_MIRKWOOD.get())).group("mirkwood")
                .unlockedBy("has_mirkwood", has(TolkienBlocks.PLANKS_MIRKWOOD.get())).save(pRecipeOutput);
        fenceBuilder(TolkienBlocks.FENCE_CULUMALDA.get(), Ingredient.of(TolkienBlocks.PLANKS_CULUMALDA.get())).group("culumalda")
                .unlockedBy("has_culumalda", has(TolkienBlocks.PLANKS_CULUMALDA.get())).save(pRecipeOutput);
        fenceBuilder(TolkienBlocks.FENCE_LEBETHRON.get(), Ingredient.of(TolkienBlocks.PLANKS_LEBETHRON.get())).group("lebethron")
                .unlockedBy("has_lebethron", has(TolkienBlocks.PLANKS_LEBETHRON.get())).save(pRecipeOutput);
        fenceBuilder(TolkienBlocks.FENCE_FANGORNOAK.get(), Ingredient.of(TolkienBlocks.PLANKS_FANGORNOAK.get())).group("fangornoak")
                .unlockedBy("has_fangornoak", has(TolkienBlocks.PLANKS_FANGORNOAK.get())).save(pRecipeOutput);
        fenceBuilder(TolkienBlocks.FENCE_DEADWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_DEADWOOD.get())).group("deadwood")
                .unlockedBy("has_deadwood", has(TolkienBlocks.PLANKS_DEADWOOD.get())).save(pRecipeOutput);

        fenceGateBuilder(TolkienBlocks.FENCE_GATE_MALLORN.get(), Ingredient.of(TolkienBlocks.PLANKS_MALLORN.get())).group("mallorn")
                .unlockedBy("has_mallorn", has(TolkienBlocks.PLANKS_MALLORN.get())).save(pRecipeOutput);
        fenceGateBuilder(TolkienBlocks.FENCE_GATE_MIRKWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_MIRKWOOD.get())).group("mirkwood")
                .unlockedBy("has_mirkwood", has(TolkienBlocks.PLANKS_MIRKWOOD.get())).save(pRecipeOutput);
        fenceGateBuilder(TolkienBlocks.FENCE_GATE_CULUMALDA.get(), Ingredient.of(TolkienBlocks.PLANKS_CULUMALDA.get())).group("culumalda")
                .unlockedBy("has_culumalda", has(TolkienBlocks.PLANKS_CULUMALDA.get())).save(pRecipeOutput);
        fenceGateBuilder(TolkienBlocks.FENCE_GATE_LEBETHRON.get(), Ingredient.of(TolkienBlocks.PLANKS_LEBETHRON.get())).group("lebethron")
                .unlockedBy("has_lebethron", has(TolkienBlocks.PLANKS_LEBETHRON.get())).save(pRecipeOutput);
        fenceGateBuilder(TolkienBlocks.FENCE_GATE_FANGORNOAK.get(), Ingredient.of(TolkienBlocks.PLANKS_FANGORNOAK.get())).group("fangornoak")
                .unlockedBy("has_fangornoak", has(TolkienBlocks.PLANKS_FANGORNOAK.get())).save(pRecipeOutput);
        fenceGateBuilder(TolkienBlocks.FENCE_GATE_DEADWOOD.get(), Ingredient.of(TolkienBlocks.PLANKS_DEADWOOD.get())).group("deadwood")
                .unlockedBy("has_deadwood", has(TolkienBlocks.PLANKS_DEADWOOD.get())).save(pRecipeOutput);

        planksFromLogs(pRecipeOutput, TolkienBlocks.PLANKS_MALLORN, TolkienBlocks.LOG_MALLORN, 4);
        planksFromLogs(pRecipeOutput, TolkienBlocks.PLANKS_MIRKWOOD, TolkienBlocks.LOG_MIRKWOOD, 4);
        planksFromLogs(pRecipeOutput, TolkienBlocks.PLANKS_CULUMALDA, TolkienBlocks.LOG_CULUMALDA, 4);
        planksFromLogs(pRecipeOutput, TolkienBlocks.PLANKS_LEBETHRON, TolkienBlocks.LOG_LEBETHRON, 4);
        planksFromLogs(pRecipeOutput, TolkienBlocks.PLANKS_FANGORNOAK, TolkienBlocks.LOG_FANGORNOAK, 4);
        planksFromLogs(pRecipeOutput, TolkienBlocks.PLANKS_DEADWOOD, TolkienBlocks.LOG_DEADWOOD, 4);

        woodFromLogs(pRecipeOutput, TolkienBlocks.WOOD_MALLORN, TolkienBlocks.LOG_MALLORN);
        woodFromLogs(pRecipeOutput, TolkienBlocks.WOOD_MIRKWOOD, TolkienBlocks.LOG_MIRKWOOD);
        woodFromLogs(pRecipeOutput, TolkienBlocks.WOOD_CULUMALDA, TolkienBlocks.LOG_CULUMALDA);
        woodFromLogs(pRecipeOutput, TolkienBlocks.WOOD_LEBETHRON, TolkienBlocks.LOG_LEBETHRON);
        woodFromLogs(pRecipeOutput, TolkienBlocks.WOOD_FANGORNOAK, TolkienBlocks.LOG_FANGORNOAK);
        woodFromLogs(pRecipeOutput, TolkienBlocks.WOOD_DEADWOOD, TolkienBlocks.LOG_DEADWOOD);

        oneToOneConversionRecipe(pRecipeOutput, Items.LIGHT_GRAY_DYE, TolkienBlocks.FLOWER_SIMBELMYNE.get(), "light_gray_dye", 2);
        oneToOneConversionRecipe(pRecipeOutput, Items.RED_DYE, TolkienBlocks.FLOWER_MIRKWOOD.get(), "red_dye", 2);
        oneToOneConversionRecipe(pRecipeOutput, Items.ORANGE_DYE, TolkienBlocks.FLOWER_ALFIRIN.get(), "orange_dye", 2);
        oneToOneConversionRecipe(pRecipeOutput, Items.GREEN_DYE, TolkienBlocks.FLOWER_ATHELAS.get(), "green_dye", 2);
        oneToOneConversionRecipe(pRecipeOutput, Items.WHITE_DYE, TolkienBlocks.FLOWER_NIPHREDIL.get(), "white_dye", 2);
        oneToOneConversionRecipe(pRecipeOutput, Items.CYAN_DYE, TolkienBlocks.FLOWER_SWAMPMILKWEED.get(), "cyan_dye", 2);
        oneToOneConversionRecipe(pRecipeOutput, Items.PINK_DYE, TolkienBlocks.FLOWER_LILLYOFTHEVALLEY.get(), "pink_dye", 2);

        addLampRecipe(pRecipeOutput,TolkienBlocks.ELVEN_LANTERN, TolkienBlocks.TORCH_MALLORN, TolkienItems.INGOT_MITHRIL);
        addLampRecipe(pRecipeOutput,TolkienBlocks.MORGUL_LANTERN, TolkienBlocks.TORCH_MIRKWOOD, TolkienItems.INGOT_MORGULIRON);
        addLampRecipe(pRecipeOutput,TolkienBlocks.SILMARIL_LANTERN, TolkienBlocks.TORCH_LEBETHRON, TolkienItems.GEM_AMMOLITE);
        addTorchRecipe(pRecipeOutput, TolkienBlocks.TORCH_MALLORN, TolkienBlocks.PLANKS_MALLORN);
        addTorchRecipe(pRecipeOutput, TolkienBlocks.TORCH_MIRKWOOD, TolkienBlocks.PLANKS_MIRKWOOD);
        addTorchRecipe(pRecipeOutput, TolkienBlocks.TORCH_CULUMALDA, TolkienBlocks.PLANKS_MALLORN);
        addTorchRecipe(pRecipeOutput, TolkienBlocks.TORCH_LEBETHRON, TolkienBlocks.PLANKS_MALLORN);
        addTorchRecipe(pRecipeOutput, TolkienBlocks.TORCH_FANGORNOAK, TolkienBlocks.PLANKS_MALLORN);
        addTorchRecipe(pRecipeOutput, TolkienBlocks.TORCH_DEADWOOD, TolkienBlocks.PLANKS_MALLORN);

        leafPileRecipe(pRecipeOutput, TolkienBlocks.LEAFPILE_MALLORN, TolkienBlocks.LEAVES_MALLORN);
        leafPileRecipe(pRecipeOutput, TolkienBlocks.LEAFPILE_MIRKWOOD, TolkienBlocks.LEAVES_MALLORN);
        leafPileRecipe(pRecipeOutput, TolkienBlocks.LEAFPILE_CULUMALDA, TolkienBlocks.LEAVES_MALLORN);
        leafPileRecipe(pRecipeOutput, TolkienBlocks.LEAFPILE_LEBETHRON, TolkienBlocks.LEAVES_MALLORN);
        leafPileRecipe(pRecipeOutput, TolkienBlocks.LEAFPILE_FANGORNOAK, TolkienBlocks.LEAVES_MALLORN);

        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_RED, Blocks.RED_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_BLUE, Blocks.BLUE_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_BLACK, Blocks.BLACK_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_BROWN, Blocks.BROWN_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_CYAN, Blocks.CYAN_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_GRAY, Blocks.GRAY_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_GREEN, Blocks.GREEN_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_LIME, Blocks.LIME_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_MAGENTA, Blocks.MAGENTA_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_ORANGE, Blocks.ORANGE_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_PINK, Blocks.PINK_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_PURPLE, Blocks.PURPLE_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_WHITE, Blocks.WHITE_CARPET);
        sleepingRecipe(pRecipeOutput, TolkienBlocks.SLEEPING_BAG_YELLOW, Blocks.YELLOW_CARPET);

        upgradeRecipe(pRecipeOutput, TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE, TolkienItems.ITEM_BACKPACK_UPGRADE_BASE, TolkienItems.GEM_AMMOLITE);
        upgradeRecipe(pRecipeOutput, TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID, TolkienItems.ITEM_BACKPACK_UPGRADE_BASE, TolkienItems.BOTTLE_FANCY);
        upgradeRecipe(pRecipeOutput, TolkienItems.ITEM_BACKPACK_UPGRADE_CRAFTING, TolkienItems.ITEM_BACKPACK_UPGRADE_BASE, Blocks.CRAFTING_TABLE);
        upgradeRecipe2(pRecipeOutput, TolkienItems.ITEM_BACKPACK_UPGRADE_SLEEPING, TolkienItems.ITEM_BACKPACK_UPGRADE_BASE, TolkienTags.Items.SLEEPING_BAG);
        upgradeRecipe(pRecipeOutput, TolkienItems.ITEM_BACKPACK_UPGRADE_CAMPFIRE, TolkienItems.ITEM_BACKPACK_UPGRADE_BASE, TolkienItems.GEM_AMMOLITE);

        swordRecipe(pRecipeOutput, TolkienItems.SWORD_MITHRIL.get(), TolkienItems.INGOT_MITHRIL.get());
        swordRecipe(pRecipeOutput, TolkienItems.SWORD_MORGULIRON.get(), TolkienItems.INGOT_MORGULIRON.get());
        swordRecipe(pRecipeOutput, TolkienItems.SWORD_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get());

        pickRecipe(pRecipeOutput, TolkienItems.PICKAXE_MITHRIL.get(), TolkienItems.INGOT_MITHRIL.get());
        pickRecipe(pRecipeOutput, TolkienItems.PICKAXE_MORGULIRON.get(), TolkienItems.INGOT_MORGULIRON.get());
        pickRecipe(pRecipeOutput, TolkienItems.PICKAXE_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get());

        axeRecipe(pRecipeOutput, TolkienItems.AXE_MITHRIL.get(), TolkienItems.INGOT_MITHRIL.get());
        axeRecipe(pRecipeOutput, TolkienItems.AXE_MORGULIRON.get(), TolkienItems.INGOT_MORGULIRON.get());
        axeRecipe(pRecipeOutput, TolkienItems.AXE_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get());

        shovelRecipe(pRecipeOutput, TolkienItems.SHOVEL_MITHRIL.get(), TolkienItems.INGOT_MITHRIL.get());
        shovelRecipe(pRecipeOutput, TolkienItems.SHOVEL_MORGULIRON.get(), TolkienItems.INGOT_MORGULIRON.get());
        shovelRecipe(pRecipeOutput, TolkienItems.SHOVEL_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get());

        hoeRecipe(pRecipeOutput, TolkienItems.HOE_MITHRIL.get(), TolkienItems.INGOT_MITHRIL.get());
        hoeRecipe(pRecipeOutput, TolkienItems.HOE_MORGULIRON.get(), TolkienItems.INGOT_MORGULIRON.get());
        hoeRecipe(pRecipeOutput, TolkienItems.HOE_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get());

        shearsRecipe(pRecipeOutput, TolkienItems.SHEARS_MITHRIL.get(), TolkienItems.INGOT_MITHRIL.get());
        shearsRecipe(pRecipeOutput, TolkienItems.SHEARS_MORGULIRON.get(), TolkienItems.INGOT_MORGULIRON.get());
        shearsRecipe(pRecipeOutput, TolkienItems.SHEARS_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get());

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.BLOCK_HALLOWED.get().asItem(), 8)
                .define('W', Blocks.GRASS_BLOCK)
                .define('P', TolkienItems.GEM_AMMOLITE)
                .pattern("WWW")
                .pattern("WPW")
                .pattern("WWW")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(TolkienItems.GEM_AMMOLITE))
                .save(pRecipeOutput);

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.STONE_PATH.get().asItem(), 8)
                .define('A', Items.IRON_PICKAXE)
                .define('M', Blocks.MOSSY_COBBLESTONE)
                .pattern("MMM")
                .pattern("MAM")
                .pattern("MMM")
                .unlockedBy("has_iron", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
                .save(pRecipeOutput);

//        ShapedRecipeBuilder
//                .shaped(RecipeCategory.BUILDING_BLOCKS, TolkienBlocks.PLACARD.get().asItem(), 1)
//                .define('M', TolkienBlocks.PLANKS_MALLORN)
//                .define('A', TolkienItems.INGOT_MITHRIL)
//                .define('-', Items.STICK)
//                .pattern("AMA")
//                .pattern("AMA")
//                .pattern("- -")
//                .unlockedBy("has_mithril", InventoryChangeTrigger.TriggerInstance.hasItems(TolkienItems.INGOT_MITHRIL.get()))
//                .save(pRecipeOutput);
    }

    protected static void oreSmelting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void planksFromLogs(RecipeOutput output, ItemLike planks, ItemLike log, int count) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, count).requires(log).group("planks").unlockedBy("has_logs", has(log)).save(output);
    }

    private void addLampRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike block, ItemLike ingot) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
                .define('W', ingot)
                .define('P', block)
                .pattern("WWW")
                .pattern("WPW")
                .pattern("WWW")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block))
                .save(recipeOutput);
    }

    private void upgradeRecipe(RecipeOutput recipeOutput, Supplier<? extends Item> result, ItemLike input1, ItemLike input2) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
                .define('W', input1)
                .define('P', input2)
                .pattern("W")
                .pattern("P")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(input1))
                .save(recipeOutput);
    }

    private void upgradeRecipe2(RecipeOutput recipeOutput, Supplier<? extends Item> result, ItemLike input1, TagKey<Item> input2) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
                .define('W', input1)
                .define('P', input2)
                .pattern("W")
                .pattern("P")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(input1))
                .save(recipeOutput);
    }

    private void addTorchRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike block) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem(),  4)
                .define('W', Items.STICK)
                .define('P', block)
                .pattern("P")
                .pattern("W")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block))
                .save(recipeOutput);
    }

    private void sleepingRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike block) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
                .define('W', Items.LEATHER)
                .define('P', block)
                .define('S', Blocks.WHITE_CARPET)
                .pattern("SPP")
                .pattern("WWW")
                .unlockedBy("has_wool", InventoryChangeTrigger.TriggerInstance.hasItems(block))
                .save(recipeOutput);
    }

    private void leafPileRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike block) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem(), 6)
                .define('P', block)
                .pattern("PPP")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block))
                .save(recipeOutput);
    }

    private void makeGemRecipes(RecipeOutput recipeOutput, Item gem, Block block ) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, gem, 4)
                .requires(block.asItem())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                .pattern("II ")
                .pattern("II ")
                .define('I', gem)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(recipeOutput);
    }

    private void pickRecipe(RecipeOutput recipeOutput, Item output, Item ingot) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, output)
                .pattern("III")
                .pattern(" S ")
                .pattern(" S ")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void axeRecipe(RecipeOutput recipeOutput, Item output, Item ingot) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, output)
                .pattern("II")
                .pattern("IS")
                .pattern(" S")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void shovelRecipe(RecipeOutput recipeOutput, Item output, Item ingot) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, output)
                .pattern("I")
                .pattern("S")
                .pattern("S")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void swordRecipe(RecipeOutput recipeOutput, Item output, Item ingot) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, output)
                .pattern("I")
                .pattern("I")
                .pattern("S")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void hoeRecipe(RecipeOutput recipeOutput, Item output, Item ingot) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, output)
                .pattern("II")
                .pattern(" S")
                .pattern(" S")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void shearsRecipe(RecipeOutput recipeOutput, Item output, Item ingot) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, output)
                .pattern(" I")
                .pattern("I ")
                .define('I', ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void helmetRecipe(RecipeOutput recipeOutput, Item output, Item ingot, Item ingot2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, output)
                .pattern("III")
                .pattern("IAI")
                .define('I', ingot)
                .define('A', ingot2)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void chestRecipe(RecipeOutput recipeOutput, Item output, Item ingot, Item ingot2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, output)
                .pattern("IAI")
                .pattern("III")
                .pattern("III")
                .define('I', ingot)
                .define('A', ingot2)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void leggingRecipe(RecipeOutput recipeOutput, Item output, Item ingot, Item ingot2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, output)
                .pattern("III")
                .pattern("IAI")
                .pattern("I I")
                .define('I', ingot)
                .define('A', ingot2)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void bootRecipe(RecipeOutput recipeOutput, Item output, Item ingot, Item ingot2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, output)
                .pattern("IAI")
                .pattern("I I")
                .define('I', ingot)
                .define('A', ingot2)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    private void makeMaterialRecipes(RecipeOutput recipeOutput, Item ingot, Item nugget, Block block ) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                .requires(block.asItem())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                .requires(ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', nugget)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput, TolkienMobsMain.prefix(nugget.getDescriptionId() + "_to_ingot"));
    }

    protected static void oneToOneConversionRecipe(RecipeOutput p_299023_, ItemLike p_176553_, ItemLike p_176554_, @Nullable String p_176555_) {
        oneToOneConversionRecipe(p_299023_, p_176553_, p_176554_, p_176555_, 1);
    }

    protected static void oneToOneConversionRecipe(RecipeOutput p_301230_, ItemLike p_176558_, ItemLike p_176559_, @Nullable String p_176560_, int p_176561_) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, p_176558_, p_176561_).requires(p_176559_).group(p_176560_).unlockedBy(getHasName(p_176559_), has(p_176559_)).save(p_301230_, MODID + ":" + getConversionRecipeName(p_176558_, p_176559_));
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput pRecipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}