package com.greatorator.tolkientweaks.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;

import static com.greatorator.tolkientweaks.TolkienTweaks.MODID;

public class jei implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = new ResourceLocation(MODID, "jei_plugin");
    public static IJeiHelpers jeiHelpers = null;

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
//        registration.addRecipeCategories(fireplaceRecipeCategory = new FireplaceRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
//        ErrorUtil.checkNotNull(fireplaceRecipeCategory, "fireplaceRecipeCategory");
        jeiHelpers = registration.getJeiHelpers();

        ClientWorld world = Minecraft.getInstance().level;
//        registration.addRecipes(world.getRecipeManager().getAllRecipesFor(TolkienMobs.FIREPLACE_RECIPE_TYPE), FireplaceRecipeCategory.UID);
    }
}
