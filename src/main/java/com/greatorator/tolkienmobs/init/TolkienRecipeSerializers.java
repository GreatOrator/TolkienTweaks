package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.recipe.TrinketRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienRecipeSerializers {
    private TolkienRecipeSerializers() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, MODID);

    static {
        register("trinket", TrinketRecipeSerializer.INSTANCE);
    }

    private static void register(String id, RecipeSerializer<?> serializer) {
        RECIPE_SERIALIZER.register(id, () -> serializer);
    }
}
