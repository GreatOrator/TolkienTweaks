/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package com.greatorator.tolkienmobs.recipe.misc;

import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.recipe.TrinketProcessType;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public final class TrinketRecipeHelper {

    private TrinketRecipeHelper() {
    }

    public static Iterable<RecipeHolder<TrinketRecipe>> getRecipes(Level level) {
        return level.getRecipeManager().byType(TolkienRecipesTypes.TRINKET_TABLE);
    }

    @Nullable
    public static TrinketRecipe findRecipe(Level level, ItemStack input, ItemStack plateA, ItemStack plateB, boolean supportNamePress) {
        for (var holder : getRecipes(level)) {
            var recipe = holder.value();
            // The recipe can be flipped at will
            final boolean matchA = recipe.getTopOptional().test(plateA) && recipe.getBottomOptional().test(plateB);
            final boolean matchB = recipe.getTopOptional().test(plateB) && recipe.getBottomOptional().test(plateA);

            if ((matchA || matchB) && recipe.getMiddleInput().test(input)) {
                return recipe;
            }
        }
        return null;
    }

    public static boolean isValidOptionalIngredientCombination(Level level, ItemStack pressA, ItemStack pressB) {
        for (var holder : getRecipes(level)) {
            var recipe = holder.value();
            if (recipe.getTopOptional().test(pressA) && recipe.getBottomOptional().test(pressB)
                    || recipe.getTopOptional().test(pressB) && recipe.getBottomOptional().test(pressA)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidOptionalIngredient(Level level, ItemStack is) {
        for (var holder : getRecipes(level)) {
            var recipe = holder.value();
            if (recipe.getTopOptional().test(is) || recipe.getBottomOptional().test(is)) {
                return true;
            }
        }

        return false;
    }

}
