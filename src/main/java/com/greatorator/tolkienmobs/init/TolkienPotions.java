package com.greatorator.tolkienmobs.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, MODID);

    public static final Holder<Potion> ENT_DRAUGHT = POTIONS.register("ent_draught",
            () -> new Potion(new MobEffectInstance(TolkienEffects.ENT_STANCE, 2400, 0)));
    public static final Holder<Potion> PORTABLE_REPAIR = POTIONS.register("personal_blacksmith",
            () -> new Potion(new MobEffectInstance(TolkienEffects.PERSONAL_BLACKSMITH, 600, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}