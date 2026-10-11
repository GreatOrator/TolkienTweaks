package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienRecipeSerializers;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public class TrinketRecipe implements Recipe<TrinketRecipeInput> {
    private final String group;
    private final Item trinket;
    private final SizedIngredient fuel;
    private final Item potionItem;

    public TrinketRecipe(String group, Item trinket, SizedIngredient fuel, Item potionItem) {
        this.group = group;
        this.trinket = trinket;
        this.fuel = fuel;
        this.potionItem = potionItem;
    }

    private boolean hasValidInputs(TrinketRecipeInput input) {
        return input.trinket().getItem() instanceof TrinketItem
                && input.trinket().is(trinket)
                && input.potion().getItem() instanceof PotionItem
                && input.potion().is(potionItem)
                && input.potion().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().isPresent()
                && input.fuel().is(TolkienItems.GEM_AMMOLITE.get())
                && fuel.test(input.fuel());
    }

    @Override
    public boolean matches(TrinketRecipeInput input, Level level) {
        return hasValidInputs(input);
    }

    @Override
    public ItemStack assemble(TrinketRecipeInput input, HolderLookup.Provider registries) {
        if (!hasValidInputs(input)) {
            return ItemStack.EMPTY;
        }
        // Keep the trinket's identity, name, active state and other components.
        ItemStack result = input.trinket().copyWithCount(1);
        PotionContents contents = input.potion().get(DataComponents.POTION_CONTENTS);
        result.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(contents.potion().orElseThrow()));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return trinket.getDefaultInstance();
    }

    @Override
    public String getGroup() {
        return group;
    }

    public Item getTrinket() {
        return trinket;
    }

    public SizedIngredient getFuel() {
        return fuel;
    }

    public Item getPotionItem() {
        return potionItem;
    }

    @Override
    public RecipeSerializer<TrinketRecipe> getSerializer() {
        return TolkienRecipeSerializers.TRINKET_TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<TrinketRecipe> getType() {
        return TolkienRecipesTypes.TRINKET_TABLE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TrinketRecipe> {
        private static final MapCodec<TrinketRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "trinket").forGetter(TrinketRecipe::getGroup),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("trinket").forGetter(TrinketRecipe::getTrinket),
                        SizedIngredient.FLAT_CODEC.fieldOf("fuel").forGetter(TrinketRecipe::getFuel),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("potion").forGetter(TrinketRecipe::getPotionItem)
                ).apply(instance, TrinketRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, TrinketRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        @Override
        public MapCodec<TrinketRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TrinketRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static TrinketRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new TrinketRecipe(buffer.readUtf(), buffer.readById(BuiltInRegistries.ITEM::byId),
                    SizedIngredient.STREAM_CODEC.decode(buffer), buffer.readById(BuiltInRegistries.ITEM::byId));
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TrinketRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeById(BuiltInRegistries.ITEM::getId, recipe.trinket);
            SizedIngredient.STREAM_CODEC.encode(buffer, recipe.fuel);
            buffer.writeById(BuiltInRegistries.ITEM::getId, recipe.potionItem);
        }
    }
}
