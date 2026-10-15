package com.greatorator.tolkienmobs.datagen.loot.functions;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienLootFunctions;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gives a trinket a potion. Left without an {@code id} the potion is rolled when the loot is
 * dropped, so a single loot table yields a variety of trinkets instead of one fixed potion.
 * An {@code id} can still be written by hand to pin a specific potion.
 */
public class TrinketRandomlyFunction extends LootItemConditionalFunction {
    public static final MapCodec<TrinketRandomlyFunction> CODEC = RecordCodecBuilder.mapCodec(
        instance -> commonFields(instance)
                .and(Potion.CODEC.optionalFieldOf("id").forGetter(function -> function.potion))
                .apply(instance, TrinketRandomlyFunction::new)
    );
    private final Optional<Holder<Potion>> potion;

    private TrinketRandomlyFunction(List<LootItemCondition> conditions, Optional<Holder<Potion>> potion) {
        super(conditions);
        this.potion = potion;
    }

    @Override
    public LootItemFunctionType<TrinketRandomlyFunction> getType() {
        return TolkienLootFunctions.TRINKET_RANDOMLY.get();
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        Holder<Potion> potion = this.potion.orElseGet(() -> pickPotion(context.getRandom()));
        stack.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(potion));
        return stack;
    }

    /** Any potion that does something, which leaves out water, mundane, thick and awkward. */
    private static Holder<Potion> pickPotion(RandomSource random) {
        List<Holder<Potion>> potions = BuiltInRegistries.POTION.holders()
                .filter(potion -> !potion.value().getEffects().isEmpty())
                .collect(Collectors.toList());
        return Util.getRandom(potions, random);
    }

    public static Builder<?> randomPotion() {
        return simpleBuilder(conditions -> new TrinketRandomlyFunction(conditions, Optional.empty()));
    }
}
