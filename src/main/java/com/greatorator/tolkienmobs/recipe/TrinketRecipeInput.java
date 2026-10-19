package com.greatorator.tolkienmobs.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record TrinketRecipeInput(ItemStack trinket, ItemStack potion, ItemStack fuel) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> trinket;
            case 1 -> potion;
            case 2 -> fuel;
            default -> throw new IllegalArgumentException("Invalid trinket recipe slot: " + index);
        };
    }

    @Override
    public int size() {
        return 3;
    }
}
