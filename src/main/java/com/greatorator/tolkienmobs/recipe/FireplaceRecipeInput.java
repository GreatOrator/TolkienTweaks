package com.greatorator.tolkienmobs.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record FireplaceRecipeInput(List<ItemStack> inputItems) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return inputItems.get(i);
    }

    @Override
    public int size() {
        return 2;
    }
}
