package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.enchantment.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEnchantmentEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, MODID);

    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<GondorResolveEnchantmentEffect>> GONDORIAN_RESOLVE =
            ENTITY_ENCHANTMENT_EFFECTS.register("gondorian_resolve", () -> GondorResolveEnchantmentEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<ElvenLongevityEnchantmentEffect>> ELVEN_LONGEVITY =
            ENTITY_ENCHANTMENT_EFFECTS.register("elven_longevity", () -> ElvenLongevityEnchantmentEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<ElvenFleetfootEnchantmentEffect>> ELVEN_FLEETFOOT =
            ENTITY_ENCHANTMENT_EFFECTS.register("elven_fleetfoot", () -> ElvenFleetfootEnchantmentEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<BalrogMarkEnchantmentEffect>> BALROG_MARK =
            ENTITY_ENCHANTMENT_EFFECTS.register("balrog_mark", () -> BalrogMarkEnchantmentEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<DwarvenEnduranceEnchantmentEffect>> DWARVEN_ENDURANCE =
            ENTITY_ENCHANTMENT_EFFECTS.register("dwarven_endurance", () -> DwarvenEnduranceEnchantmentEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<DwarvenMinerEnchantmentEffect>> DWARVEN_MINING =
            ENTITY_ENCHANTMENT_EFFECTS.register("dwarven_mining", () -> DwarvenMinerEnchantmentEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<HobbitPlowEnchantmentEffect>> HOBBIT_PLOW =
            ENTITY_ENCHANTMENT_EFFECTS.register("hobbit_plow", () -> HobbitPlowEnchantmentEffect.CODEC);
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<HobbitHarvestEnchantmentEffect>> HOBBIT_HARVEST =
            ENTITY_ENCHANTMENT_EFFECTS.register("hobbit_harvest", () -> HobbitHarvestEnchantmentEffect.CODEC);

    public static void register(IEventBus eventBus) {
        ENTITY_ENCHANTMENT_EFFECTS.register(eventBus);
    }
}
