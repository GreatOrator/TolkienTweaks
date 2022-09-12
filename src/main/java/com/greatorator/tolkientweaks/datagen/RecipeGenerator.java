package com.greatorator.tolkientweaks.datagen;

import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 1/12/20
 */
public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        //Left in a couple of my builders so you can see how the recipe builders work
        components(consumer);
        specialty(consumer);
        magic(consumer);
        fireplace(consumer);
    }

    private static void components(Consumer<IFinishedRecipe> consumer) {

        // Cooking & Smelting Recipes
//        smeltingRecipe(TTMContent.DUST_MITHRIL.get(), TTMContent.INGOT_MITHRIL.get(), 0.35F, 200, consumer);
//        upgradeRecipe2(TTMContent.ITEM_BACKPACK_UPGRADE_SLEEPING.get(), consumer);
    }

    private static void specialty(Consumer<IFinishedRecipe> consumer) {
//        ShapedRecipeBuilder.shaped(TTMContent.GOLEM_STONE_SUMMON.get())
//                .pattern("MEM")
//                .pattern("ASF")
//                .pattern("OWO")
//                .define('W', TTMContent.GOLEM_STONE_WATER.get())
//                .define('A', TTMContent.GOLEM_STONE_AIR.get())
//                .define('M', TTMContent.BLOCK_MITHRIL.get())
//                .define('S', TTMContent.GOLEM_STONE.get())
//                .define('F', TTMContent.GOLEM_STONE_FIRE.get())
//                .define('E', TTMContent.GOLEM_STONE_EARTH.get())
//                .define('O', Items.OBSIDIAN)
//                .unlockedBy("has_golem_stones", has(TTMContent.GOLEM_STONE.get()))
//                .save(consumer);

//        ShapedRecipeBuilder.shaped(TTMContent.PLACARD.get())
//                .pattern("AMA")
//                .pattern("AMA")
//                .pattern("- -")
//                .define('M', ItemTags.createOptional(new ResourceLocation("forge", "planks")))
//                .define('A', TTMContent.INGOT_MITHRIL.get())
//                .define('-', Items.STICK)
//                .unlockedBy("has_mithril", has(TTMContent.INGOT_MITHRIL.get()))
//                .save(consumer);
    }

    public static void potions() {
//        BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(Potions.AWKWARD, TTMContent.MIRUVOR.get(), PotionGenerator.ENT_DRAUGHT.get()));
    }

    private static void magic(Consumer<IFinishedRecipe> consumer) {
    }

    private static void fireplace(Consumer<IFinishedRecipe> consumer) {
//        fireplaceRecipe1(Items.COOKED_SALMON, 50, 100, Items.SALMON, consumer);
//        fireplaceRecipe2(Items.GOLDEN_APPLE, 100, 500, Items.APPLE, Items.GOLD_INGOT, consumer);
//        fireplaceRecipe3(TTMContent.MONSTER_FLESH.get(), 50, 100, consumer, Items.COOKED_RABBIT, Items.COOKED_PORKCHOP, Items.COOKED_MUTTON, Items.COOKED_BEEF, Items.COOKED_SALMON, Items.COOKED_COD, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH);
    }

    // Helper Methods

    public static void upgradeRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("#  ")
                .pattern("---")
                .pattern("   ")
                .define('#', input1)
                .define('-', input2)
                .unlockedBy("has_" + input1.asItem().getRegistryName().getPath(), has(input1))
                .save(consumer, "tolkienmobs:upgrade_" + output.asItem().getRegistryName().getPath());
    }

    public static void upgradeRecipe2(IItemProvider output, Consumer<IFinishedRecipe> consumer) {
//        ShapedRecipeBuilder.shaped(output, 1)
//                .pattern("#  ")
//                .pattern("-  ")
//                .pattern("   ")
//                .define('-', ItemTags.createOptional(new ResourceLocation("forge", "sleeping_bags")))
//                .define('#', TTMContent.ITEM_BACKPACK_UPGRADE_BASE.get())
//                .unlockedBy("has_" + TTMContent.ITEM_BACKPACK_UPGRADE_BASE.get().asItem().getRegistryName().getPath(), has(TTMContent.ITEM_BACKPACK_UPGRADE_BASE.get()))
//                .save(consumer, "tolkienmobs:upgrade2_" + output.asItem().getRegistryName().getPath());
    }

    public static void sleepingRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("AAB")
                .pattern("CCC")
                .define('A', input1)
                .define('B', input2)
                .define('C', Items.LEATHER)
                .unlockedBy("has_wool", has(input1))
                .save(consumer, "tolkienmobs:sleepingbag_" + output.asItem().getRegistryName().getPath());
    }

    public static void fireplaceRecipe1(IItemProvider output, int experience, int cookTime, IItemProvider input1, Consumer<IFinishedRecipe> consumer){
//        FireplaceRecipeBuilder.fireplaceRecipe(output, experience, cookTime)
//                .ingredient(input1)
//                .build(consumer);
    }

    public static void fireplaceRecipe2(IItemProvider output, int experience, int cookTime, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer){
//        FireplaceRecipeBuilder.fireplaceRecipe(output, experience, cookTime)
//                .ingredient(input1)
//                .ingredient(input2)
//                .build(consumer);
    }

    public static void fireplaceRecipe3(IItemProvider output, int experience, int cookTime, Consumer<IFinishedRecipe> consumer, IItemProvider... input1){
//        FireplaceRecipeBuilder.fireplaceRecipe(output, experience, cookTime)
//                .ingredient(input1)
//                .build(consumer);
    }

    public static void chestRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("#-#")
                .pattern("###")
                .pattern("###")
                .define('#', input1)
                .define('-', input2)
                .unlockedBy("has_" + input1.asItem().getRegistryName().getPath(), has(input1))
                .save(consumer, "tolkienmobs:armor_" + output.asItem().getRegistryName().getPath());
    }

    public static void leggingRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("###")
                .pattern("#-#")
                .pattern("# #")
                .define('#', input1)
                .define('-', input2)
                .unlockedBy("has_" + input1.asItem().getRegistryName().getPath(), has(input1))
                .save(consumer, "tolkienmobs:armor_" + output.asItem().getRegistryName().getPath());
    }

    public static void bootRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("#-#")
                .pattern("# #")
                .define('#', input1)
                .define('-', input2)
                .unlockedBy("has_" + input1.asItem().getRegistryName().getPath(), has(input1))
                .save(consumer, "tolkienmobs:armor_" + output.asItem().getRegistryName().getPath());
    }

    public static void helmetRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("###")
                .pattern("#-#")
                .define('#', input1)
                .define('-', input2)
                .unlockedBy("has_" + input1.asItem().getRegistryName().getPath(), has(input1))
                .save(consumer, "tolkienmobs:armor_" + output.asItem().getRegistryName().getPath());
    }

    public static void swordRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("#")
                .pattern("#")
                .pattern("-")
                .define('#', input)
                .define('-', Items.STICK)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:tools_" + output.asItem().getRegistryName().getPath());
    }

    public static void axeRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("##")
                .pattern("#-")
                .pattern(" -")
                .define('#', input)
                .define('-', Items.STICK)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:tools_" + output.asItem().getRegistryName().getPath());
    }

    public static void shovelRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("#")
                .pattern("-")
                .pattern("-")
                .define('#', input)
                .define('-', Items.STICK)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:tools_" + output.asItem().getRegistryName().getPath());
    }

    public static void pickaxeRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("###")
                .pattern(" - ")
                .pattern(" - ")
                .define('#', input)
                .define('-', Items.STICK)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:tools_" + output.asItem().getRegistryName().getPath());
    }

    public static void hoeRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("##")
                .pattern(" -")
                .pattern(" -")
                .define('#', input)
                .define('-', Items.STICK)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:tools_" + output.asItem().getRegistryName().getPath());
    }

    public static void torchRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 16)
                .pattern("#")
                .pattern("-")
                .define('#', Items.COAL)
                .define('-', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:torch_" + output.asItem().getRegistryName().getPath());
    }

    public static void smeltingRecipe(IItemProvider output, IItemProvider input, float xp, int cook,Consumer<IFinishedRecipe> consumer) {
        CookingRecipeBuilder.smelting(Ingredient.of(input),
                output, xp, cook)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:cooked_" + output.asItem().getRegistryName().getPath());
    }

    public static void signRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 3)
                .pattern("###")
                .pattern("###")
                .pattern(" - ")
                .define('#', input1)
                .define('-', input2)
                .unlockedBy("has_" + input1.asItem().getRegistryName().getPath(), has(input1))
                .save(consumer, "tolkienmobs:sign_" + output.asItem().getRegistryName().getPath());
    }

    public static void slabRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 6)
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer);
    }

    public static void trapDoorRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 4)
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer);
    }

    public static void metalTrapDoorRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 4)
                .pattern("##")
                .pattern("##")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer);
    }

    public static void pressurePlateRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("##")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer);
    }

    public static void stairsRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 4)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer);
    }

    public static void barsRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer);
    }

    public static void storageRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:storage_" + output.asItem().getRegistryName().getPath());
    }

    public static void doorRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 3)
                .pattern("##")
                .pattern("##")
                .pattern("##")
                .define('#', input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer);
    }

    public static void fenceRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 3)
                .pattern("#-#")
                .pattern("#-#")
                .define('#', input1)
                .define('-', input2)
                .unlockedBy("has_" + input1.asItem().getRegistryName().getPath(), has(input1))
                .save(consumer);
    }

    public static void fenceGateRecipe(IItemProvider output, IItemProvider input1, IItemProvider input2, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(output, 1)
                .pattern("-#-")
                .pattern("-#-")
                .define('-', input1)
                .define('#', input2)
                .unlockedBy("has_" + input1.asItem().getRegistryName().getPath(), has(input1))
                .save(consumer);
    }

    public static void unstorageRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder
                .shapeless(output, 9)
                .requires(input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:unstorage_" + output.asItem().getRegistryName().getPath());
    }

    public static void dyeRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder
                .shapeless(output, 2)
                .requires(input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer, "tolkienmobs:dye_" + output.asItem().getRegistryName().getPath());
    }

    public static void plankRecipe(IItemProvider output, IItemProvider input, Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder
                .shapeless(output, 4)
                .requires(input)
                .unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input))
                .save(consumer);
    }

    @Override
    public void run(DirectoryCache cache) throws IOException {
        super.run(cache);
    }

    private static class ModBrewingRecipe implements IBrewingRecipe {
        private final Potion bottleInput;
        private final Item itemInput;
        private final ItemStack output;

        public ModBrewingRecipe(Potion bottleInput, Item itemInput, Potion output) {
            this.bottleInput = bottleInput;
            this.itemInput = itemInput;
            this.output = PotionUtils.setPotion(new ItemStack(Items.POTION), output);
        }

        @Override
        public boolean isInput(ItemStack input) {
            return PotionUtils.getPotion(input).equals(this.bottleInput);
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return ingredient.getItem().equals(this.itemInput);
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if(isInput(input) && isIngredient(ingredient)) {
                return this.output.copy();
            } else {
                return ItemStack.EMPTY;
            }
        }
    }

    public static class NBTIngredient extends net.minecraftforge.common.crafting.NBTIngredient {
        public NBTIngredient(ItemStack stack) {
            super(stack);
        }
    }

    @Override
    public String getName() {
        return "Tolkien Tweaks - Mobs Edition Recipes";
    }
}
