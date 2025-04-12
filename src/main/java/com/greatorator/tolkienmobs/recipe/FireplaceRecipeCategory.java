package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.block.custom.FireplaceBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FireplaceRecipeCategory implements IRecipeCategory<FireplaceRecipe> {
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/fireplace/fireplace_gui.png");
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "fireplace");
    private final Component localizedName;
    private final IDrawable background;
    private final IDrawable icon;

    public static final RecipeType<FireplaceRecipe> FIREPLACE_RECIPE_TYPE =
            new RecipeType<>(UID, FireplaceRecipe.class);

    public FireplaceRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(GUI, 0, 0, 176, 83);
        this.localizedName = FireplaceBlock.TextComponent;
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TolkienBlocks.FIREPLACE));
    }

    @Override
    public @NotNull RecipeType<FireplaceRecipe> getRecipeType() {return FIREPLACE_RECIPE_TYPE;}

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IDrawable getBackground() {
        return background;
    }

//    @Override
//    public void draw(FireplaceRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
//        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
//    }

    @Override @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, FireplaceRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < recipe.getInputItems().size(); i++) {
            var input = recipe.getInputItems().get(i);
            var slot = builder.addSlot(RecipeIngredientRole.INPUT, 2 + (i + recipe.getInputItems().size()) * 22, 15);
            for (var stack : input.ingredient().getItems()) {
                slot.addIngredient(VanillaTypes.ITEM_STACK, Util.make(stack.copy(), s -> s.setCount(input.count())));
            }
        }

        for (int i = 0; i < recipe.getOutput().size(); i++) {
            var output = recipe.getOutput().get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 114, 35)
                    .addItemStack(output.getItem().getDefaultInstance());
        }

//        builder.addSlot(RecipeIngredientRole.INPUT, 23, 15).addIngredients(recipe.getIngredients().get(0));
//        builder.addSlot(RecipeIngredientRole.INPUT, 45, 15).addIngredients(recipe.getIngredients().get(1));
//
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 35).addItemStack(recipe.getResultItem(null));
    }
}