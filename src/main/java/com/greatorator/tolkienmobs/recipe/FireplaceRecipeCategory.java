package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.FireplaceBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.integration.TolkienJEIPlugin;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FireplaceRecipeCategory implements IRecipeCategory<FireplaceRecipe> {
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/fireplace/fireplace_gui.png");
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "fireplace");
    private final IDrawable background;
    private final Component localizedName;
    private final IDrawable overlay;
    private final IDrawable icon;

    public static final RecipeType<FireplaceRecipe> FIREPLACE_RECIPE_TYPE =
            new RecipeType<>(UID, FireplaceRecipe.class);

    public FireplaceRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(GUI, 0, 0, 176, 166);
        this.localizedName = FireplaceBlock.TextComponent;
        this.overlay = guiHelper.createDrawable(GUI, 42, 29, 64, 64);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TolkienBlocks.FIREPLACE));
    }

    @Override
    public @NotNull RecipeType<FireplaceRecipe> getRecipeType() {return FIREPLACE_RECIPE_TYPE;}

    @Override
    public @NotNull Component getTitle() {return localizedName;}

    @Override
    public @NotNull IDrawable getIcon() {return icon;}

    @Override
    public @NotNull IDrawable getBackground() {return background;}

    @Override @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, FireplaceRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 15).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 15).addIngredients(recipe.getIngredients().get(1));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 35).addItemStack(recipe.getResultItem(null));
    }
}