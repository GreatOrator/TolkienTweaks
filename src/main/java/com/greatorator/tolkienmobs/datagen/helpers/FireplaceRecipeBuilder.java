package com.greatorator.tolkienmobs.datagen.helpers;

import com.greatorator.tolkienmobs.recipe.FireplaceRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FireplaceRecipeBuilder implements RecipeBuilder {
    private final List<SizedIngredient> ingredients = new ArrayList<>();
    private final List<ItemStackTemplate> outputs = new ArrayList<>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    public static FireplaceRecipeBuilder fireplaceRecipe() {
        return new FireplaceRecipeBuilder();
    }

    private FireplaceRecipeBuilder() {
        // Private constructor to enforce the use of the static factory method
    }

    public FireplaceRecipeBuilder addIngredient(SizedIngredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public FireplaceRecipeBuilder addOutput(ItemStackTemplate output) {
        this.outputs.add(output);
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        this.criteria.put(s, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String s) {
        this.group = s;
        return this;
    }

    /** 26.1 made a builder name its own recipe id: the result's registry id, as vanilla's helpers do. */
    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.outputs.isEmpty() ? new ItemStackTemplate(Items.AIR) : this.outputs.get(0));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> recipeKey) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeKey))
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        // Create the recipe instance
        FireplaceRecipe recipe = new FireplaceRecipe(
                this.ingredients,
                this.outputs
        );

        // Pass the recipe and advancement to the output
        recipeOutput.accept(recipeKey, recipe, advancement.build(recipeKey.identifier().withPrefix("recipes/")));
    }
}