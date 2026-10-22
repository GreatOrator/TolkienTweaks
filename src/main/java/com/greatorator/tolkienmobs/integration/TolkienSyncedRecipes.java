package com.greatorator.tolkienmobs.integration;

import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;

import java.util.List;

/**
 * The fireplace recipes the server has sent to this client.
 *
 * <p>1.21.4 no longer syncs recipes to the client with the rest of the world: the server only sends
 * the recipe <em>displays</em> the player has unlocked, so nothing on the client can look a recipe up.
 * A server instead sends whole recipe types when a mod asks for them in
 * {@link net.neoforged.neoforge.event.OnDatapackSyncEvent#sendRecipes}, and the client receives them in
 * {@link net.neoforged.neoforge.client.event.RecipesReceivedEvent}. JEI registers its plugin recipes
 * after that, so the fireplace category reads them from here.
 */
public final class TolkienSyncedRecipes {
    private static List<FireplaceRecipe> fireplace = List.of();

    private TolkienSyncedRecipes() {
    }

    public static void accept(RecipeMap recipes) {
        fireplace = recipes.byType(TolkienRecipesTypes.FIREPLACE_TYPE.get()).stream()
                .map(RecipeHolder::value)
                .toList();
    }

    public static void clear() {
        fireplace = List.of();
    }

    public static List<FireplaceRecipe> fireplace() {
        return fireplace;
    }
}
