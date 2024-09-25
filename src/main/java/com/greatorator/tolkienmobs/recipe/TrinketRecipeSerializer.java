package com.greatorator.tolkienmobs.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class TrinketRecipeSerializer implements RecipeSerializer<TrinketRecipe> {
    public static final TrinketRecipeSerializer INSTANCE = new TrinketRecipeSerializer();

    private TrinketRecipeSerializer() {
    }

    @Override
    public MapCodec<TrinketRecipe> codec() {
        return TrinketRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TrinketRecipe> streamCodec() {
        return TrinketRecipe.STREAM_CODEC;
    }
}