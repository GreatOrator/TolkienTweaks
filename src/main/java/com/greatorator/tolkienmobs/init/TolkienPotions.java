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

        // Beneficial
    public static final Holder<Potion> ENT_DRAUGHT = POTIONS.register("ent_draught",
            () -> new Potion(new MobEffectInstance(TolkienEffects.ENT_STANCE, 2400, 0)));
    public static final Holder<Potion> PORTABLE_REPAIR = POTIONS.register("personal_blacksmith",
            () -> new Potion(new MobEffectInstance(TolkienEffects.PERSONAL_BLACKSMITH, 600, 0)));
    public static final Holder<Potion> ERU_BLESSING = POTIONS.register("blessing_of_eru",
            () -> new Potion(new MobEffectInstance(TolkienEffects.BLESSING_OF_ERU, 600, 0)));
    public static final Holder<Potion> ELF_VITALITY = POTIONS.register("elf_vitality",
            () -> new Potion(new MobEffectInstance(TolkienEffects.ELF_VITALITY, 600, 0)));
    public static final Holder<Potion> ELF_NIMBLENESS = POTIONS.register("elven_nimbleness",
            () -> new Potion(new MobEffectInstance(TolkienEffects.ELF_VITALITY, 600, 0)));

        // Harmful
    public static final Holder<Potion> INVENTORY_CORROSION = POTIONS.register("inventory_corrosion",
                () -> new Potion(new MobEffectInstance(TolkienEffects.INVENTORY_CORROSION, 600, 0)));
    public static final Holder<Potion> GOLEM_FLYING = POTIONS.register("elemental_flight",
            () -> new Potion(new MobEffectInstance(TolkienEffects.ELEMENTAL_FLYING, 600, 0)));
    public static final Holder<Potion> PARALYSING_FEAR = POTIONS.register("crippling_terror",
            () -> new Potion(new MobEffectInstance(TolkienEffects.PARALYSING_FEAR, 600, 0)));
    public static final Holder<Potion> WATCHER_FEAR = POTIONS.register("dread_aura",
            () -> new Potion(new MobEffectInstance(TolkienEffects.WATCHER_FEAR, 600, 0)));
    public static final Holder<Potion> SLEEPNESIA = POTIONS.register("sleepnesia",
            () -> new Potion(new MobEffectInstance(TolkienEffects.WATCHER_FEAR, 600, 0)));
    public static final Holder<Potion> ELEMENTAL_BURNING = POTIONS.register("elemental_burning",
            () -> new Potion(new MobEffectInstance(TolkienEffects.WATCHER_FEAR, 600, 0)));
    public static final Holder<Potion> ELEMENTAL_LIGHTNING = POTIONS.register("elemental_lightning",
            () -> new Potion(new MobEffectInstance(TolkienEffects.WATCHER_FEAR, 600, 0)));
    public static final Holder<Potion> ELEMENTAL_DROWNING = POTIONS.register("elemental_drowning",
            () -> new Potion(new MobEffectInstance(TolkienEffects.WATCHER_FEAR, 600, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}