package com.greatorator.tolkienmobs.handler.recipes;

import com.greatorator.tolkienmobs.recipe.TrinketProcessType;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class TrinketRecipeBuilder {
    private final Ingredient middleInput;
    private Ingredient topOptional = Ingredient.EMPTY;
    private Ingredient bottomOptional = Ingredient.EMPTY;
    private final ItemLike output;
    private final int count;
    private TrinketProcessType mode = TrinketProcessType.CRAFT;

    public TrinketRecipeBuilder(Ingredient middleInput, ItemLike output, int count) {
        this.middleInput = middleInput;
        this.output = output;
        this.count = count;
    }

    public static TrinketRecipeBuilder inscribe(ItemLike middle, ItemLike output, int count) {
        return new TrinketRecipeBuilder(Ingredient.of(middle), output, count);
    }

    public static TrinketRecipeBuilder inscribe(TagKey<Item> middle, ItemLike output, int count) {
        return new TrinketRecipeBuilder(Ingredient.of(middle), output, count);
    }

    public static TrinketRecipeBuilder inscribe(Ingredient middle, ItemLike output, int count) {
        return new TrinketRecipeBuilder(middle, output, count);
    }

    public TrinketRecipeBuilder setTop(Ingredient topOptional) {
        this.topOptional = topOptional;
        return this;
    }

    public TrinketRecipeBuilder setBottom(Ingredient bottomOptional) {
        this.bottomOptional = bottomOptional;
        return this;
    }

    public TrinketRecipeBuilder setMode(TrinketProcessType processType) {
        this.mode = processType;
        return this;
    }

    public void save(RecipeOutput consumer, ResourceLocation id) {
        var result = output.asItem().getDefaultInstance();
        result.setCount(count);

        var recipe = new TrinketRecipe(
                middleInput, result, topOptional, bottomOptional, mode);

        consumer.accept(id, recipe, null);
    }
}
