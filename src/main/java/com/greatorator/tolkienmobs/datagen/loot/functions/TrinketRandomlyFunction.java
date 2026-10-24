package com.greatorator.tolkienmobs.datagen.loot.functions;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienLootFunctions;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

/**
 * Gives a trinket a potion. Left without an {@code id} the potion is rolled when the loot is
 * dropped, so a single loot table yields a variety of trinkets instead of one fixed potion.
 * Only benevolent potions are drawn, since a trinket is worn. An {@code id} can still be
 * written by hand to pin a specific potion.
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
        Optional<Holder<Potion>> potion = this.potion.isPresent() ? this.potion : pickPotion(context.getRandom());
        potion.ifPresent(chosen -> stack.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(chosen)));
        return stack;
    }

    private static Optional<Holder<Potion>> pickPotion(RandomSource random) {
        List<Holder.Reference<Potion>> potions = BuiltInRegistries.POTION.listElements()
                .filter(potion -> isBenevolent(potion.value()))
                .toList();
        return potions.isEmpty() ? Optional.empty() : Optional.of(Util.getRandom(potions, random));
    }

    /**
     * True when the potion does something and everything it does helps the wearer. Reading the
     * effect categories rather than a list of names keeps vanilla's and this mod's potions on
     * one rule, and rules out water, mundane, thick and awkward, which have no effects at all.
     */
    private static boolean isBenevolent(Potion potion) {
        List<MobEffectInstance> effects = potion.getEffects();
        return !effects.isEmpty() && effects.stream().allMatch(effect -> effect.getEffect().value().isBeneficial());
    }

    public static Builder<?> randomPotion() {
        return simpleBuilder(conditions -> new TrinketRandomlyFunction(conditions, Optional.empty()));
    }
}
