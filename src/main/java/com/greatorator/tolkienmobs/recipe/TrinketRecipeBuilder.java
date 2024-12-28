package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.handler.data.TrinketComponent;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.LinkedHashMap;
import java.util.Map;

public class TrinketRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final Ingredient ingredient;
    private final Item potionItem;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
    private String group;

    public TrinketRecipeBuilder(RecipeCategory trinketCategory, String group, ItemLike itemLike, Ingredient ingredient, ItemLike potion) {
        this.category = trinketCategory;
        this.group = group;
        this.result = itemLike.asItem();
        this.ingredient = ingredient;
        this.potionItem = potion.asItem();
    }

    public static <T extends AbstractCookingRecipe> TrinketRecipeBuilder recipe(RecipeCategory trinketCategory, String group, Item ingredient, ItemLike itemLike, ItemLike potion) {
        return new TrinketRecipeBuilder(trinketCategory, group, itemLike, Ingredient.of(ingredient), potion);
    }

    @Override
    public TrinketRecipeBuilder unlockedBy(String string, Criterion<?> criterion) {
        this.criteria.put(string, criterion);
        return this;
    }

    @Override
    public TrinketRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        if (potionItem == Items.POTION) {
            Holder<Potion> holder = potionItem.getDefaultInstance().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().orElse(Potions.WATER);

            ItemStack itemstack = new ItemStack(result);
            itemstack.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(holder));

            return itemstack.getItem();
        }
        return null;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
        this.ensureValid(recipeId);
        Advancement.Builder builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        recipeOutput.accept(recipeId, new TrinketRecipe(CraftingBookCategory.EQUIPMENT), builder.build(recipeId.withPrefix("recipes/misc/")));
    }

    private void ensureValid(ResourceLocation recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
