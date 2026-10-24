package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class TrinketRecipe implements Recipe<TrinketRecipeInput> {
    private final String group;
    private final Item trinket;
    private final SizedIngredient fuel;
    private final Item potionItem;

    // 26.1 keeps a serializer as a record of its codecs, so they sit on the recipe and the registry holds the pair.
    public static final MapCodec<TrinketRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "trinket").forGetter(TrinketRecipe::group),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("trinket").forGetter(TrinketRecipe::getTrinket),
                    SizedIngredient.NESTED_CODEC.fieldOf("fuel").forGetter(TrinketRecipe::getFuel),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("potion").forGetter(TrinketRecipe::getPotionItem)
            ).apply(instance, TrinketRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TrinketRecipe> STREAM_CODEC = StreamCodec.of(
            TrinketRecipe::toNetwork, TrinketRecipe::fromNetwork
    );

    public static final RecipeSerializer<TrinketRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

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
    public ItemStack assemble(TrinketRecipeInput input) {
        if (!hasValidInputs(input)) {
            return ItemStack.EMPTY;
        }
        // Keep the trinket's identity, name, active state and other components.
        ItemStack result = input.trinket().copyWithCount(1);
        PotionContents contents = input.potion().get(DataComponents.POTION_CONTENTS);
        result.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(contents.potion().orElseThrow()));
        return result;
    }

    public ItemStack getResultItem() {
        return trinket.getDefaultInstance();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
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
        return SERIALIZER;
    }

    @Override
    public RecipeType<TrinketRecipe> getType() {
        return TolkienRecipesTypes.TRINKET_TABLE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(Ingredient.of(trinket), fuel.ingredient(), Ingredient.of(potionItem)));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
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
