package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.recipe.FireplaceRecipe;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienRecipeSerializers {
    private TolkienRecipeSerializers() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TrinketRecipe>> TRINKET_TABLE_SERIALIZER =
            RECIPE_SERIALIZER.register("trinket", () -> new SimpleCraftingRecipeSerializer<>(TrinketRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FireplaceRecipe>> FIREPLACE_SERIALIZER =
            RECIPE_SERIALIZER.register("fireplace", FireplaceRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZER.register(eventBus);
    }
}
