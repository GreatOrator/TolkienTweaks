package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienRecipesTypes {
    private TolkienRecipesTypes() {
    }

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister
            .create(Registries.RECIPE_TYPE, MODID);

    public static final RecipeType<TrinketRecipe> TRINKET_TABLE = register("trinket");

    private static <T extends Recipe<?>> RecipeType<T> register(String id) {
        RecipeType<T> type = RecipeType.simple(TolkienMobsMain.resLoc(id));
        RECIPE_TYPE.register(id, () -> type);
        return type;
    }
}