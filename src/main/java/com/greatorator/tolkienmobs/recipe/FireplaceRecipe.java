package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 26.1 made a recipe serializer a record holding its two codecs rather than an interface a recipe
 * implements, so the codecs live on the recipe as constants and the registry holds a
 * {@code RecipeSerializer} built from them.
 */
public record FireplaceRecipe(List<SizedIngredient> inputItems, List<ItemStack> output) implements Recipe<FireplaceRecipeInput> {
    public static final MapCodec<FireplaceRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(alloySmeltingRecipeInstance -> alloySmeltingRecipeInstance.group(
            SizedIngredient.NESTED_CODEC.listOf().fieldOf("ingredients").forGetter(FireplaceRecipe::getInputItems),
            ItemStack.CODEC.listOf().fieldOf("output").forGetter(FireplaceRecipe::getOutput)
    ).apply(alloySmeltingRecipeInstance, FireplaceRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FireplaceRecipe> STREAM_CODEC = StreamCodec.of(
            FireplaceRecipe::toNetwork, FireplaceRecipe::fromNetwork
    );

    public static final RecipeSerializer<FireplaceRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

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
    public ItemStack assemble(FireplaceRecipeInput fireplaceRecipeInput) {
        return output.isEmpty() ? ItemStack.EMPTY : output.get(0).copy();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    /** The fireplace has one recipe shape and no book group, so the group is empty. */
    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<FireplaceRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<FireplaceRecipeInput>> getType() {
        return TolkienRecipesTypes.FIREPLACE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(inputItems.stream().map(SizedIngredient::ingredient).toList());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public List<SizedIngredient> getInputItems() {
        return inputItems;
    }

    public List<ItemStack> getOutput() {
        return output;
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
