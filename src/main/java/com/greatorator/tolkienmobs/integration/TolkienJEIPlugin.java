package com.greatorator.tolkienmobs.integration;

import com.greatorator.tolkienmobs.containers.screens.FireplaceScreen;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipe;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@JeiPlugin
public class TolkienJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
//        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, DimletModule.ATTRIBUTE_DIMLET.get(), DimletInterpreter.INSTANCE);
//        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, DimletModule.TERRAIN_DIMLET.get(), DimletInterpreter.INSTANCE);
//        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, DimletModule.FLUID_DIMLET.get(), DimletInterpreter.INSTANCE);
//        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, DimletModule.FEATURE_DIMLET.get(), DimletInterpreter.INSTANCE);
//        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, DimletModule.BIOME_DIMLET.get(), DimletInterpreter.INSTANCE);
//        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, DimletModule.BIOME_CONTROLLER_DIMLET.get(), DimletInterpreter.INSTANCE);
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

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FireplaceScreen.class, 100, 31, 18, 23, FireplaceRecipeCategory.FIREPLACE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TolkienBlocks.FIREPLACE.get().asItem()),
                FireplaceRecipeCategory.FIREPLACE_RECIPE_TYPE);
    }
}