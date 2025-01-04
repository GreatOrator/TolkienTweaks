package com.greatorator.tolkienmobs.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.UnaryOperator;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEnchantmentEffectComponents {

    private static final DeferredRegister<DataComponentType<?>> REGISTER =
        DeferredRegister.create(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, MODID);

    public static final DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> USE_ON_BLOCK = register(
        "use_on_block",
        (it) -> it.persistent(
            ConditionalEffect.codec(
                EnchantmentEntityEffect.CODEC,
                    TolkienLootContextParamSets.USE_ON_ITEM
            ).listOf()
        )
    );

    public static final DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> POST_BREAK_BLOCK = register(
        "post_break_block",
        (it) -> it.persistent(
            ConditionalEffect.codec(
                EnchantmentEntityEffect.CODEC,
                    TolkienLootContextParamSets.POST_BREAK_BLOCK
            ).listOf()
        )
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        DataComponentType<T> dct = operator.apply(DataComponentType.builder()).build();
        REGISTER.register(name, () -> dct);
        return dct;
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
