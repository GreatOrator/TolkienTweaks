package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.recipe.FireplaceRecipe;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienRecipesTypes {
    private TolkienRecipesTypes() {
    }

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister
            .create(Registries.RECIPE_TYPE, MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TrinketRecipe>> TRINKET_TABLE_TYPE =
            RECIPE_TYPE.register("trinket", () -> new RecipeType<TrinketRecipe>() {
                @Override
                public String toString() {
                    return "trinket";
                }
            });
    public static final DeferredHolder<RecipeType<?>, RecipeType<FireplaceRecipe>> FIREPLACE_TYPE =
            RECIPE_TYPE.register("fireplace", () -> new RecipeType<FireplaceRecipe>() {
                @Override
                public String toString() {
                    return "fireplace";
                }
            });

    public static void register(IEventBus eventBus) {
        RECIPE_TYPE.register(eventBus);
    }
}