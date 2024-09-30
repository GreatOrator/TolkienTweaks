package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienRecipeSerializers {
    private TolkienRecipeSerializers() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TrinketRecipe>> TRINKET_TABLE_SERIALIZER =
            RECIPE_SERIALIZER.register("trinket_tinker", TrinketRecipe.Serializer::new);

    private static void register(String id, RecipeSerializer<?> serializer) {
        RECIPE_SERIALIZER.register(id, () -> serializer);
    }
}
