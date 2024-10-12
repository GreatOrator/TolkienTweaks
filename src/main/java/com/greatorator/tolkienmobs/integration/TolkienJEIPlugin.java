package com.greatorator.tolkienmobs.integration;

import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipe;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipeCategory;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import com.greatorator.tolkienmobs.recipe.TrinketRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienJEIPlugin implements IModPlugin {
    public static final RecipeType<TrinketRecipe> TRINKET_CRAFTING = RecipeType.create(MODID, "trinket", TrinketRecipe.class);
    public static final RecipeType<FireplaceRecipe> FIREPLACE_CRAFTING = RecipeType.create(MODID, "fireplace", FireplaceRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TrinketRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        RecipeManager manager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        var recipes = manager.getAllRecipesFor(TolkienRecipesTypes.TRINKET_TABLE).stream().map(RecipeHolder::value).toList();
        var recipes2 = manager.getAllRecipesFor(TolkienRecipesTypes.FIREPLACE).stream().map(RecipeHolder::value).toList();

        registration.addRecipes(new RecipeType<>(TrinketRecipeCategory.UID, TrinketRecipe.class), recipes);
        registration.addRecipes(new RecipeType<>(FireplaceRecipeCategory.UID, FireplaceRecipe.class), recipes2);
    }
}
