package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.enchantment.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEnchantmentEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, MODID);

    static {
        ENTITY_ENCHANTMENT_EFFECTS.register("gondorian_resolve", () -> GondorResolveEnchantmentEffect.CODEC);
        ENTITY_ENCHANTMENT_EFFECTS.register("elven_longevity", () -> ElvenLongevityEnchantmentEffect.CODEC);
        ENTITY_ENCHANTMENT_EFFECTS.register("elven_fleetfoot", () -> ElvenFleetfootEnchantmentEffect.CODEC);
        ENTITY_ENCHANTMENT_EFFECTS.register("balrog_mark", () -> BalrogMarkEnchantmentEffect.CODEC);
        ENTITY_ENCHANTMENT_EFFECTS.register("dwarven_endurance", () -> DwarvenEnduranceEnchantmentEffect.CODEC);
        ENTITY_ENCHANTMENT_EFFECTS.register("dwarven_mining", () -> DwarvenMinerEnchantmentEffect.CODEC);
        ENTITY_ENCHANTMENT_EFFECTS.register("hobbit_plow", () -> HobbitPlowEnchantmentEffect.CODEC);
    }

    public static void register(IEventBus eventBus) {
        ENTITY_ENCHANTMENT_EFFECTS.register(eventBus);
    }
}
