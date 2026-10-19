package com.greatorator.tolkienmobs.datagen.helpers;

import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.LinkedHashMap;
import java.util.Map;

public class TrinketRecipeBuilder implements RecipeBuilder {
    private final Item trinket;
    private final SizedIngredient fuel;
    private final Item potionItem;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private String group = "trinket";

    private TrinketRecipeBuilder(ItemLike trinket, SizedIngredient fuel, ItemLike potion) {
        this.trinket = trinket.asItem();
        this.fuel = fuel;
        this.potionItem = potion.asItem();
    }

    public static TrinketRecipeBuilder recipe(ItemLike trinket, SizedIngredient fuel, ItemLike potion) {
        return new TrinketRecipeBuilder(trinket, fuel, potion);
    }

    @Override
    public TrinketRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public TrinketRecipeBuilder group(String group) {
        this.group = group == null ? "" : group;
        return this;
    }

    @Override
    public Item getResult() {
        return trinket;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        recipeOutput.accept(recipeId, new TrinketRecipe(group, trinket, fuel, potionItem),
                builder.build(recipeId.withPrefix("recipes/misc/")));
    }
}
