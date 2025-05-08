package com.greatorator.tolkienmobs.recipe;

import com.google.common.base.Suppliers;
import com.greatorator.tolkienmobs.datagen.helpers.TolkienRecipeHelper;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienRecipeSerializers;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TrinketRecipe extends ShapelessRecipe {
    private static final List<Predicate<ItemStack>> ITEM_PREDICATES = List.of(
            stack -> stack.getItem() instanceof PotionItem,
            stack -> stack.getItem() instanceof TrinketItem,
            stack -> stack.getItem() == TolkienItems.GEM_AMMOLITE.get()
    );
    private static final Supplier<Ingredient> POTIONS = Suppliers.memoize(() ->
            Ingredient.of(BuiltInRegistries.POTION.holders().flatMap(TrinketRecipe::potionStacks))
    );

    public TrinketRecipe(CraftingBookCategory category) {
        super("", category, TolkienItems.TRINKET_RING.get().getDefaultInstance(), NonNullList.of(Ingredient.EMPTY, Ingredient.of(TolkienItems.TRINKET_RING.get()), POTIONS.get(), Ingredient.of(TolkienItems.GEM_AMMOLITE.get())));
    }

    private static Stream<ItemStack> potionStacks(Holder<Potion> potion) {
        return Stream.of(
                PotionContents.createItemStack(Items.POTION, potion),
                PotionContents.createItemStack(Items.SPLASH_POTION, potion),
                PotionContents.createItemStack(Items.LINGERING_POTION, potion)
        );
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        return TolkienRecipeHelper.allPresent(container, ITEM_PREDICATES);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        List<ItemStack> stacks = TolkienRecipeHelper.findItems(inv, ITEM_PREDICATES);
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
    public RecipeSerializer<?> getSerializer() {
        return TolkienRecipeSerializers.TRINKET_TABLE_SERIALIZER.get();
    }}