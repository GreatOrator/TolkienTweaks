package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.containers.FireplaceContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienRecipeSerializers;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class FireplaceRecipe implements Recipe<FireplaceContainer> {
    public final ItemStack output;
    private final Ingredient trinket;
    private final Ingredient gem;
    private final Ingredient potion;

    public FireplaceRecipe(Ingredient trinket, Ingredient potion, Ingredient gem, ItemStack output) {
        this.trinket = trinket;
        this.potion = potion;
        this.gem = gem;
        this.output = output;
    }

    @Override @ParametersAreNonnullByDefault
    public boolean matches(FireplaceContainer container, Level level) {
        boolean testTrinket = trinket.test(container.getItem(FireplaceContainer.INGREDIENT_1_SLOT_ID));
        boolean testPotion = potion.test(container.getItem(FireplaceContainer.INGREDIENT_2_SLOT_ID));
        boolean testGem = gem.test(container.getItem(FireplaceContainer.FUEL_SLOT_ID));
        return (testTrinket && testPotion && testGem);
    }

    @Override @ParametersAreNonnullByDefault
    public @NotNull ItemStack assemble(FireplaceContainer container, HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return TolkienBlocks.TRINKET_TABLE.get().asItem().getDefaultInstance();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return TolkienRecipeSerializers.TRINKET_TABLE_SERIALIZER.get();
    }

    public Ingredient getIngredient() {
        return trinket;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TolkienRecipesTypes.TRINKET_TABLE; }


    public static class Type implements RecipeType<FireplaceRecipe> {
        public Type() { }
    }

    public static class Serializer implements RecipeSerializer<FireplaceRecipe> {
        public static final MapCodec<FireplaceRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("trinket").forGetter((recipe) -> recipe.trinket),
                Ingredient.CODEC_NONEMPTY.fieldOf("potion").forGetter((recipe) -> recipe.trinket),
                Ingredient.CODEC_NONEMPTY.fieldOf("gem").forGetter((recipe) -> recipe.trinket),
                ItemStack.STRICT_CODEC.fieldOf("output").forGetter((recipe) -> recipe.output)
        ).apply(builder, FireplaceRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FireplaceRecipe> STREAM_CODEC = StreamCodec.of(FireplaceRecipe.Serializer::toNetwork, FireplaceRecipe.Serializer::fromNetwork);

        private static FireplaceRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            var trinket = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            var potion = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            var gem = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            var output = ItemStack.STREAM_CODEC.decode(buffer);
            return new FireplaceRecipe(trinket, potion, gem, output);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FireplaceRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
        }

        @Override
        public @NotNull MapCodec<FireplaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FireplaceRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}