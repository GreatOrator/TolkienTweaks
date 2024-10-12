package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.FireplaceBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.integration.TolkienJEIPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FireplaceRecipeCategory implements IRecipeCategory<FireplaceRecipe> {
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/trinkettable/fireplace_gui.png");
    public static final ResourceLocation UID = TolkienMobsMain.resLoc("fireplace");
    private final IDrawable background;
    private final IDrawable icon;

    public FireplaceRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(GUI, 0, 0, 176, 166);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TolkienBlocks.TRINKET_TABLE.get()));
    }

    @Override
    public @NotNull RecipeType<FireplaceRecipe> getRecipeType() {return TolkienJEIPlugin.FIREPLACE_CRAFTING;}

    @Override
    public @NotNull Component getTitle() {return FireplaceBlock.TextComponent;}

    @Override
    public @NotNull IDrawable getBackground() {return background;}

    @Override
    public @NotNull IDrawable getIcon() {return icon;}

    @Override @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, FireplaceRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 15).addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 15).addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.INPUT, 34, 55).addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 35).addIngredients(Ingredient.of(recipe.output));
    }
}