package com.greatorator.tolkienmobs.integration;

import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipe;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipeCategory;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
//        registration.addRecipeCategories(new TrinketRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FireplaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        RecipeManager manager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

//        var recipes = manager.getAllRecipesFor(TolkienRecipesTypes.TRINKET_TABLE_TYPE.get()).stream().map(RecipeHolder::value).toList();

        List<FireplaceRecipe> fireplaceRecipes = manager
                .getAllRecipesFor(TolkienRecipesTypes.FIREPLACE_TYPE.get()).stream().map(RecipeHolder::value).toList();

//        registration.addRecipes(new RecipeType<>(TrinketRecipeCategory.UID, TrinketRecipe.class), recipes);

        registration.addRecipes(FireplaceRecipeCategory.FIREPLACE_RECIPE_TYPE, fireplaceRecipes);
    }

//    @Override
//    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
//        registration.addRecipeClickArea(FireplaceScreen.class, 100, 31, 18, 23, FireplaceRecipeCategory.UID);
//    }
}