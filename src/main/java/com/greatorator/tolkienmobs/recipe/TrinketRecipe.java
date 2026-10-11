package com.greatorator.tolkienmobs.recipe;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienRecipeSerializers;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TrinketRecipe implements Recipe<TrinketRecipeInput> {
    private static final List<Predicate<ItemStack>> ITEM_PREDICATES = List.of(
            stack -> stack.getItem() instanceof PotionItem,
            stack -> stack.getItem() instanceof TrinketItem,
            stack -> stack.getItem() == TolkienItems.GEM_AMMOLITE.get()
    );

    private final RecipeCategory category;
    private final String group;
    private final Item result;
    private final SizedIngredient ingredient;
    private final Item potionItem;

    public TrinketRecipe(RecipeCategory category, String group, Item result, SizedIngredient ingredient, Item potionItem) {
        this.category = category;
        this.group = group;
        this.result = result;
        this.ingredient = ingredient;
        this.potionItem = potionItem;
    }

    @Override @ParametersAreNonnullByDefault
    public boolean matches(TrinketRecipeInput container, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return allPresent(container, ITEM_PREDICATES) && ingredient.ingredient().test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(TrinketRecipeInput inv, HolderLookup.Provider registryAccess) {
        List<ItemStack> stacks = findItems(inv, ITEM_PREDICATES);
        if (stacks.size() == 3) {
            ItemStack potion = stacks.get(0);
            Holder<Potion> potioncontents = (potion.get(DataComponents.POTION_CONTENTS)).potion().get();
            ItemStack trinket = stacks.get(1).copy();
            ItemStack gem = stacks.get(2).copy();
            if (!trinket.isEmpty() && !potion.isEmpty() && !gem.isEmpty()) {
                trinket.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(potioncontents));
                return trinket;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return TolkienItems.TRINKET_RING.get().getDefaultInstance();
    }

    @Override
    public RecipeSerializer<? extends Recipe<TrinketRecipeInput>> getSerializer() {
        return (RecipeSerializer<? extends Recipe<TrinketRecipeInput>>) TolkienRecipeSerializers.TRINKET_TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<TrinketRecipeInput>> getType() {
        return TolkienRecipesTypes.TRINKET_TABLE_TYPE.get();
    }

    public List<Predicate<ItemStack>> getItemPredicates() {
        return ITEM_PREDICATES;
    }

    private boolean allPresent(TrinketRecipeInput container, List<Predicate<ItemStack>> predicates) {
        List<ItemStack> items = container.inputItems();
        for (Predicate<ItemStack> predicate : predicates) {
            boolean found = false;
            for (ItemStack item : items) {
                if (!item.isEmpty() && predicate.test(item)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private List<ItemStack> findItems(TrinketRecipeInput container, List<Predicate<ItemStack>> predicates) {
        List<ItemStack> result = new java.util.ArrayList<>();
        List<ItemStack> items = container.inputItems();
        for (Predicate<ItemStack> predicate : predicates) {
            for (ItemStack item : items) {
                if (!item.isEmpty() && predicate.test(item)) {
                    result.add(item);
                    break;
                }
            }
        }
        return result;
    }

    public static class Serializer implements RecipeSerializer<TrinketRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "trinket");

        private final MapCodec<TrinketRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        MapCodec.unit("trinket").forGetter(recipe -> recipe.group),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(recipe -> recipe.result),
                        SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("potion").forGetter(recipe -> recipe.potionItem)
                ).apply(instance, (group, result, ingredient, potionItem) -> new TrinketRecipe(RecipeCategory.MISC, group, result, ingredient, potionItem))
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
            return new TrinketRecipe(
                    RecipeCategory.MISC,
                    "trinket",
                    buffer.readById(BuiltInRegistries.ITEM::byId),
                    SizedIngredient.STREAM_CODEC.decode(buffer),
                    buffer.readById(BuiltInRegistries.ITEM::byId)
            );
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TrinketRecipe recipe) {
            buffer.writeById(BuiltInRegistries.ITEM::getId, recipe.result);
            SizedIngredient.STREAM_CODEC.encode(buffer, recipe.ingredient);
            buffer.writeById(BuiltInRegistries.ITEM::getId, recipe.potionItem);
        }
    }
}