package com.greatorator.tolkienmobs.datagen.loot.functions;

import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienLootFunctions;
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
import java.util.stream.Collectors;

public class TrinketRandomlyFunction extends LootItemConditionalFunction {
    public static final MapCodec<TrinketRandomlyFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_348460_ -> commonFields(p_348460_).and(Potion.CODEC.fieldOf("id").forGetter(p_298158_ -> p_298158_.potion)).apply(p_348460_, TrinketRandomlyFunction::new)
    );
    private final Holder<Potion> potion;

    private TrinketRandomlyFunction(List<LootItemCondition> conditions, Holder<Potion> potion) {
        super(conditions);
        this.potion = potion;
    }

    @Override
    public LootItemFunctionType<TrinketRandomlyFunction> getType() {
        return TolkienLootFunctions.TRINKET_RANDOMLY.get();
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {

        stack.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(this.potion));
        return stack;
    }

    public static Builder<?> setPotion(ItemStack stack) {
        final RandomSource random = RandomSource.create();
        List<Holder<Potion>> list = (List) BuiltInRegistries.POTION.holders().filter((p_340770_) -> {
            return !((Potion) p_340770_.value()).getEffects().isEmpty();
        }).collect(Collectors.toList());
        Holder<Potion> holder = (Holder) Util.getRandom(list, random);

        return simpleBuilder(p_316108_ -> new TrinketRandomlyFunction(p_316108_, holder));
    }
}
