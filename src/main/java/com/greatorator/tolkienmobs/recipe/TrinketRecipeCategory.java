package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.TrinketTableBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TrinketRecipeCategory implements IRecipeCategory<TrinketRecipe> {
    private static final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/trinkettable/trinket_gui.png");
    public static final Identifier UID = TolkienMobsMain.resLoc("trinket");
    private final IDrawable icon;

    public static final RecipeType<TrinketRecipe> TRINKETTABLE_RECIPE_TYPE =
            new RecipeType<>(UID, TrinketRecipe.class);

    public TrinketRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TolkienBlocks.TRINKET_TABLE.get()));
    }

    @Override
    public @NotNull RecipeType<TrinketRecipe> getRecipeType() {return TRINKETTABLE_RECIPE_TYPE;}

    @Override
    public @NotNull Component getTitle() {return TrinketTableBlock.TextComponent;}

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 83;
    }

    @Override @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, TrinketRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 15).addIngredient(VanillaTypes.ITEM_STACK, recipe.getTrinket().getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 15).addIngredient(VanillaTypes.ITEM_STACK, PotionContents.createItemStack(recipe.getPotionItem(), Potions.WATER));
        builder.addSlot(RecipeIngredientRole.INPUT, 34, 55).addItemStacks(recipe.getFuel().ingredient().items().map(ItemStack::new).toList());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 35).addItemStack(recipe.getResultItem(null));
    }
}