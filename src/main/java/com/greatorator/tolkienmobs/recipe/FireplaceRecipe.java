package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.init.TolkienRecipeSerializers;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record FireplaceRecipe(List<SizedIngredient> inputItems, List<ItemStack> output) implements Recipe<FireplaceRecipeInput> {

    @Override @ParametersAreNonnullByDefault
    public boolean matches(FireplaceRecipeInput container, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        List<ItemStack> inputItems = container.inputItems();
        List<SizedIngredient> remainingIngredients = new ArrayList<>(this.inputItems);

        for (ItemStack itemStack : inputItems) {
            if (itemStack.isEmpty()) {
                continue;
            }

            boolean ingredientFound = false;
            Iterator<SizedIngredient> iterator = remainingIngredients.iterator();

            while (iterator.hasNext()) {
                SizedIngredient ingredient = iterator.next();
                if (ingredient.ingredient().test(itemStack)) {
                    iterator.remove();
                    ingredientFound = true;
                    break;
                }
            }

            if (!ingredientFound) {
                return false;
            }
        }

        return remainingIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(FireplaceRecipeInput fireplaceRecipeInput, HolderLookup.Provider provider) {
        return output.isEmpty() ? ItemStack.EMPTY : output.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.isEmpty() ? ItemStack.EMPTY : output.get(0).copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<FireplaceRecipeInput>> getSerializer() {
        return (RecipeSerializer<? extends Recipe<FireplaceRecipeInput>>) TolkienRecipeSerializers.FIREPLACE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<FireplaceRecipeInput>> getType() {
        return TolkienRecipesTypes.FIREPLACE_TYPE.get();
    }

    public List<SizedIngredient> getInputItems() {
        return inputItems;
    }

    public List<ItemStack> getOutput() {
        return output;
    }

    public static class Serializer implements RecipeSerializer<FireplaceRecipe>{
        public static final FireplaceRecipe.Serializer INSTANCE = new FireplaceRecipe.Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "fireplace");

        private final MapCodec<FireplaceRecipe> CODEC = RecordCodecBuilder.mapCodec(alloySmeltingRecipeInstance -> alloySmeltingRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.listOf().fieldOf("ingredients").forGetter(FireplaceRecipe::getInputItems),
                ItemStack.CODEC.listOf().fieldOf("output").forGetter(FireplaceRecipe::getOutput)
        ).apply(alloySmeltingRecipeInstance, FireplaceRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FireplaceRecipe> STREAM_CODEC = StreamCodec.of(
                FireplaceRecipe.Serializer::toNetwork, FireplaceRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<FireplaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FireplaceRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static FireplaceRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int ingredientCount = buffer.readVarInt();
            List<SizedIngredient> inputItems = new ArrayList<>(ingredientCount);
            for (int i = 0; i < ingredientCount; i++) {
                inputItems.add(SizedIngredient.STREAM_CODEC.decode(buffer));
            }

            int outputCount = buffer.readVarInt();
            List<ItemStack> result = new ArrayList<>(outputCount);
            for (int i = 0; i < outputCount; i++) {
                result.add(ItemStack.STREAM_CODEC.decode(buffer));
            }

            return new FireplaceRecipe(inputItems, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FireplaceRecipe recipe) {
            buffer.writeVarInt(recipe.inputItems.size());
            for (SizedIngredient ingredient : recipe.inputItems) {
                SizedIngredient.STREAM_CODEC.encode(buffer, ingredient);
            }

            buffer.writeVarInt(recipe.output.size());
            for (ItemStack itemStack : recipe.output) {
                ItemStack.STREAM_CODEC.encode(buffer, itemStack);
            }

        }
    }
}