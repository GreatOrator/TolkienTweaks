package com.greatorator.tolkienmobs.datagen.helpers;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.TolkienMobsMain.resLoc;

public class TolkienRecipeHelper extends RecipeProvider implements IConditionBuilder  {
    public TolkienRecipeHelper(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    protected static void woodListRecipe(RecipeOutput pRecipeOutput, RecipeCategory pCategory, String group, ItemLike planks, ItemLike log, ItemLike wood, ItemLike stairs, ItemLike slab, ItemLike plate, ItemLike button, ItemLike door, ItemLike trapdoor, ItemLike fence, ItemLike fenceGate, ItemLike sign, ItemLike hangingSign, ItemLike ladder, ItemLike boat, ItemLike chestBoat, Supplier<? extends Block> torch, Supplier<? extends Block> barrel, Supplier<? extends Block> leaves, ItemLike leafpile) {
        leafPileRecipe(pRecipeOutput, leaves, leafpile);
        stairBuilder(stairs, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        slab(pRecipeOutput, pCategory, slab, planks);
        pressurePlate(pRecipeOutput, plate, planks);
        buttonBuilder(button, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        doorBuilder(door, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        trapdoorBuilder(trapdoor, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        fenceBuilder(fence, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        fenceGateBuilder(fenceGate, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        planksFromLogs(pRecipeOutput, planks, log, 4);
        woodFromLogs(pRecipeOutput, wood, log);
        addTorchRecipe(pRecipeOutput, torch, planks);
        barrelRecipe(pRecipeOutput, barrel, planks, slab);
        signRecipe(pRecipeOutput, sign, planks);
        hangingSignRecipe(pRecipeOutput, hangingSign, planks);
        ladderRecipe(pRecipeOutput, ladder, planks);
        woodenBoat(pRecipeOutput, boat, planks);
        chestBoat(pRecipeOutput, chestBoat, planks);
    }

    protected static void woodListRecipeNoLeaves(RecipeOutput pRecipeOutput, RecipeCategory pCategory, String group, ItemLike planks, ItemLike log, ItemLike wood, ItemLike stairs, ItemLike slab, ItemLike plate, ItemLike button, ItemLike door, ItemLike trapdoor, ItemLike fence, ItemLike fenceGate, ItemLike sign, ItemLike hangingSign, ItemLike ladder, ItemLike boat, ItemLike chestBoat, Supplier<? extends Block> torch, Supplier<? extends Block> barrel) {
        stairBuilder(stairs, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        slab(pRecipeOutput, pCategory, slab, planks);
        pressurePlate(pRecipeOutput, plate, planks);
        buttonBuilder(button, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        doorBuilder(door, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        trapdoorBuilder(trapdoor, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        fenceBuilder(fence, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        fenceGateBuilder(fenceGate, Ingredient.of(planks)).group(group).unlockedBy("has_"+group, has(planks)).save(pRecipeOutput);
        planksFromLogs(pRecipeOutput, planks, log, 4);
        woodFromLogs(pRecipeOutput, wood, log);
        addTorchRecipe(pRecipeOutput, torch, planks);
        barrelRecipe(pRecipeOutput, barrel, planks, slab);
        signRecipe(pRecipeOutput, sign, planks);
        hangingSignRecipe(pRecipeOutput, hangingSign, planks);
        ladderRecipe(pRecipeOutput, ladder, planks);
        woodenBoat(pRecipeOutput, boat, planks);
        chestBoat(pRecipeOutput, chestBoat, planks);
    }

    protected static void stoneListRecipe(RecipeOutput pRecipeOutput, RecipeCategory pCategory, String group, ItemLike wall, ItemLike button, ItemLike stair, ItemLike block, ItemLike slab, ItemLike plate) {
        stairBuilder(stair, Ingredient.of(block)).group(group).unlockedBy("has_"+group, has(block)).save(pRecipeOutput);
        slab(pRecipeOutput, pCategory, slab, block);
        pressurePlate(pRecipeOutput, plate, block);
        buttonBuilder(button, Ingredient.of(block)).group(group).unlockedBy("has_"+group, has(block)).save(pRecipeOutput);
        wall(pRecipeOutput, pCategory, wall, block);
    }

    protected void gemListRecipe(RecipeOutput recipeOutput, Item gem, Block block, ItemLike pane) {
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
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pane, 16)
                .define('#', block)
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(block), has(block))
                .save(recipeOutput);
    }

    protected static void metalListRecipe(RecipeOutput pRecipeOutput, RecipeCategory pCategory, String group, ItemLike wall, ItemLike door, ItemLike trapdoor, Supplier<? extends Block> barrel, ItemLike button, ItemLike stair, ItemLike block, ItemLike slab, ItemLike plate, ItemLike bars, Supplier<? extends Block> lantern, ItemLike torch, Item ingot, Item nugget, Item arrow) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                .requires(block.asItem())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(pRecipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                .requires(ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', nugget)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(pRecipeOutput, TolkienMobsMain.prefix(nugget.getDescriptionId() + "_to_ingot"));
        addLampRecipe(pRecipeOutput,lantern ,torch ,ingot);
        stairBuilder(stair, Ingredient.of(block)).group(group).unlockedBy("has_"+group, has(block)).save(pRecipeOutput);
        slab(pRecipeOutput, pCategory, slab, block);
        pressurePlate(pRecipeOutput, plate, block);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, bars, 16)
                .define('#', ingot)
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(ingot), has(ingot))
                .save(pRecipeOutput);
        buttonBuilder(button, Ingredient.of(block)).group(group).unlockedBy("has_"+group, has(block)).save(pRecipeOutput);
        wall(pRecipeOutput, pCategory, wall, block);
        barrelRecipe(pRecipeOutput, barrel, block, slab);
        doorBuilder(door, Ingredient.of(block)).group(group).unlockedBy("has_"+group, has(ingot)).save(pRecipeOutput);
        trapdoorBuilder(trapdoor, Ingredient.of(block)).group(group).unlockedBy("has_"+group, has(ingot)).save(pRecipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, arrow)
                .requires(Items.STICK)
                .requires(TolkienItems.BIRD_FEATHER)
                .requires(ingot)
                .unlockedBy("has_mithril", has(ingot))
                .save(pRecipeOutput);
    }

    protected static void fireplaceRecipe(RecipeOutput recipeOutput, SizedIngredient ingredients1, SizedIngredient ingredients2, ItemLike result, int outputCount){
        FireplaceRecipeBuilder.fireplaceRecipe()
                .addIngredient(ingredients1)
                .addIngredient(ingredients2)
                .addOutput(new ItemStack(result, outputCount))
                .unlockedBy(getHasName(ingredients1.ingredient().getItems()[0].getItem()), has(ingredients1.ingredient().getItems()[0].getItem()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MODID, "fireplace/" + getItemName(result) + "_from_fireplace"));
    }

    protected static void toolListRecipe(RecipeOutput pRecipeOutput, RecipeCategory pCategory, String group, Item pick, Item axe, Item shovel, Item hoe, Item sword, Item shears, Item ingot) {
        ShapedRecipeBuilder.shaped(pCategory, pick)
                .pattern("III")
                .pattern(" S ")
                .pattern(" S ")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_"+group, InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(pCategory, axe)
                .pattern("II")
                .pattern("IS")
                .pattern(" S")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_"+group, InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(pCategory, shovel)
                .pattern("I")
                .pattern("S")
                .pattern("S")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_"+group, InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, sword)
                .pattern("I")
                .pattern("I")
                .pattern("S")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_"+group, InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(pCategory, hoe)
                .pattern("II")
                .pattern(" S")
                .pattern(" S")
                .define('I', ingot)
                .define('S', Items.STICK)
                .unlockedBy("has_"+group, InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(pRecipeOutput);
        ShapedRecipeBuilder.shaped(pCategory, shears)
                .pattern(" I")
                .pattern("I ")
                .define('I', ingot)
                .unlockedBy("has_"+group, InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(pRecipeOutput);
    }

    protected static void smithingUpgrade(RecipeOutput consumer, String type, Item output, ItemLike template, ItemLike input1, ItemLike input2) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(template), Ingredient.of(input1),
                        Ingredient.of(input2), RecipeCategory.MISC, output)
                .unlocks("has_template_" + type, InventoryChangeTrigger.TriggerInstance.hasItems(template))
                .save(consumer, (output + "-templateupgrade"));
    }

//    protected static void trinket(RecipeOutput recipeOutput, ItemLike trinket, ItemLike output, ItemLike potion) {
//        new TrinketRecipeBuilder(RecipeCategory.TOOLS, "ammolite", output, Ingredient.of(trinket), potion)
//                .unlockedBy("has_", InventoryChangeTrigger.TriggerInstance.hasItems(trinket))
//                .save(recipeOutput, TolkienMobsMain.prefix(output.asItem().getDescriptionId() + "_from_trinket_table"));
//    }

    protected static void smeltingList(ItemLike input, RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), category, output, experience, cookingTime)
                .unlockedBy("has_"+input, InventoryChangeTrigger.TriggerInstance.hasItems(input))
                .save(recipeOutput, TolkienMobsMain.prefix(output.asItem().getDescriptionId() + "_from_smelting"));
    }

    protected static void blastingList(ItemLike input, RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), category, output, experience, cookingTime)
                .unlockedBy("has_"+input, InventoryChangeTrigger.TriggerInstance.hasItems(input))
                .save(recipeOutput, TolkienMobsMain.prefix(output.asItem().getDescriptionId() + "_from_blasting"));
    }

    protected static void paneRecipe(RecipeOutput recipeOutput, ItemLike pSign, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pSign, 16)
                .define('#', material)
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
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

    protected void conversionRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike item1, ItemLike item2) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem(), 8)
                .define('A', item1)
                .define('M', item2)
                .pattern("AAA")
                .pattern("AMA")
                .pattern("AAA")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(item2))
                .save(recipeOutput);
    }

    protected static void ladderRecipe(RecipeOutput recipeOutput, ItemLike ladder, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ladder, 4)
                .group("sign")
                .define('#', material)
                .define('X', Items.STICK)
                .pattern("X X")
                .pattern("X#X")
                .pattern("X X")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }

    private static void barrelRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike plank, ItemLike slab) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, result.get().asItem())
                .define('A', plank)
                .define('M', slab)
                .pattern("AMA")
                .pattern("A A")
                .pattern("AMA")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(plank))
                .save(recipeOutput);
    }

    protected static void addLampRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike block, ItemLike ingot) {
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

    protected static void containerRecipe(RecipeOutput recipeOutput, RecipeCategory pCategory, String group, ItemLike container, ItemLike material1, ItemLike material2, ItemLike ingot) {
        ShapedRecipeBuilder.shaped(pCategory, container, 1)
                .group(group)
                .define('L', material1)
                .define('C', material2)
                .define('I', ingot)
                .pattern("LIL")
                .pattern("LCL")
                .pattern("LLL")
                .unlockedBy("has_"+group, InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
    }

    protected static void placardRecipe(RecipeOutput recipeOutput, ItemLike pSign, ItemLike planks, ItemLike ingot) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pSign, 3)
                .group("sign")
                .define('#', planks)
                .define('M', ingot)
                .define('X', Items.STICK)
                .pattern("#M#")
                .pattern("#M#")
                .pattern(" X ")
                .unlockedBy(getHasName(planks), has(planks))
                .save(recipeOutput);
    }

    protected static void signRecipe(RecipeOutput recipeOutput, ItemLike pSign, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pSign, 3)
                .group("sign")
                .define('#', material)
                .define('X', Items.STICK)
                .pattern("###")
                .pattern("###")
                .pattern(" X ")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }

    protected static void hangingSignRecipe(RecipeOutput recipeOutput, ItemLike sign, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign, 6)
                .group("hanging_sign")
                .define('#', material)
                .define('X', Items.CHAIN)
                .pattern("X X")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_stripped_logs", has(material))
                .save(recipeOutput);
    }

    protected void upgradeRecipe(RecipeOutput recipeOutput, Supplier<? extends Item> result, ItemLike input1, ItemLike input2) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, result.get().asItem())
                .define('W', input1)
                .define('P', input2)
                .pattern("W")
                .pattern("P")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(input1))
                .save(recipeOutput);
    }

    protected void upgradeRecipe2(RecipeOutput recipeOutput, Supplier<? extends Item> result, ItemLike input1, TagKey<Item> input2) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, result.get().asItem())
                .define('W', input1)
                .define('P', input2)
                .pattern("W")
                .pattern("P")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(input1))
                .save(recipeOutput);
    }

    protected void upgradeRecipe3(RecipeOutput recipeOutput, Supplier<? extends Item> result, ItemLike item1, ItemLike item2) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, result.get().asItem(), 1)
                .define('A', item1)
                .define('M', item2)
                .pattern("AAA")
                .pattern("AMA")
                .pattern("AAA")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(item2))
                .save(recipeOutput);
    }

    private static void addTorchRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike block) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem(),  4)
                .define('W', Items.STICK)
                .define('P', block)
                .pattern("P")
                .pattern("W")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block))
                .save(recipeOutput);
    }

    protected void sleepingRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike block) {
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

    private static void leafPileRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result, ItemLike block) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem(), 6)
                .define('P', block)
                .pattern("PPP")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block))
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

    protected static void oneToOneConversionRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike ingredient, @Nullable String group, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, resultCount).requires(ingredient).group(group).unlockedBy(getHasName(ingredient), has(ingredient)).save(recipeOutput, MODID + ":" + getConversionRecipeName(output, ingredient));
    }

    protected static void twoToTwoConversionRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike ingredient, @Nullable String group) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, 2).requires(output).requires(ingredient, 2).group(group).unlockedBy(getHasName(output), has(output)).save(recipeOutput, MODID + ":" + getConversionRecipeName(output, ingredient));
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput pRecipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    public static List<ItemStack> findItems(CraftingInput inv, List<Predicate<ItemStack>> predicates) {
        List<ItemStack> res = new ArrayList<>();
        BitSet matchedSlots = new BitSet(inv.size());

        for (var pred : predicates) {
            boolean found = false;
            for (int i = 0; i < inv.size(); i++) {
                if (!matchedSlots.get(i)) {
                    ItemStack stack = inv.getItem(i);
                    if (pred.test(stack)) {
                        res.add(stack);
                        matchedSlots.set(i);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) return List.of();
        }

        // check any unmatched slots for extraneous items
        for (int i = 0; i < inv.size(); i++) {
            if (!matchedSlots.get(i) && !inv.getItem(i).isEmpty()) return List.of();
        }

        return res;
    }

    public static boolean allPresent(CraftingInput inv, List<Predicate<ItemStack>> predicates) {
        return findItems(inv, predicates).size() == predicates.size();
    }

    protected String getId(String s) {
        return resLoc(s).toString();
    }
}
