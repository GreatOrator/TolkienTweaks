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
    public static final Holder<Potion> ENT_DRAUGHT = potion("ent_draught",
            new MobEffectInstance(TolkienMobEffects.ENT_STANCE, 2400, 0));
    public static final Holder<Potion> PORTABLE_REPAIR = potion("personal_blacksmith",
            new MobEffectInstance(TolkienMobEffects.PERSONAL_BLACKSMITH, 600, 0));
    public static final Holder<Potion> ERU_BLESSING = potion("blessing_of_eru",
            new MobEffectInstance(TolkienMobEffects.BLESSING_OF_ERU, 600, 0));
    public static final Holder<Potion> ELF_VITALITY = potion("elf_vitality",
            new MobEffectInstance(TolkienMobEffects.ELF_VITALITY, 600, 0));
    public static final Holder<Potion> ELF_NIMBLENESS = potion("elven_nimbleness",
            new MobEffectInstance(TolkienMobEffects.ELF_NIMBLENESS, 600, 0));

        // Harmful
    public static final Holder<Potion> INVENTORY_CORROSION = potion("inventory_corrosion",
            new MobEffectInstance(TolkienMobEffects.INVENTORY_CORROSION, 600, 0));
    public static final Holder<Potion> GOLEM_FLYING = potion("elemental_flight",
            new MobEffectInstance(TolkienMobEffects.ELEMENTAL_FLYING, 600, 0));
    public static final Holder<Potion> PARALYSING_FEAR = potion("crippling_terror",
            new MobEffectInstance(TolkienMobEffects.PARALYSING_FEAR, 600, 0));
    public static final Holder<Potion> WATCHER_FEAR = potion("dread_aura",
            new MobEffectInstance(TolkienMobEffects.WATCHER_FEAR, 600, 0));
    public static final Holder<Potion> SLEEPNESIA = potion("sleepnesia",
            new MobEffectInstance(TolkienMobEffects.SLEEPNESIA, 600, 0));
    public static final Holder<Potion> ELEMENTAL_BURNING = potion("elemental_burning",
            new MobEffectInstance(TolkienMobEffects.ELEMENTAL_BURNING, 600, 0));
    public static final Holder<Potion> ELEMENTAL_LIGHTNING = potion("elemental_lightning",
            new MobEffectInstance(TolkienMobEffects.ELEMENTAL_LIGHTNING, 600, 0));
    public static final Holder<Potion> ELEMENTAL_DROWNING = potion("elemental_drowning",
            new MobEffectInstance(TolkienMobEffects.ELEMENTAL_DROWNING, 600, 0));

    /**
     * 1.21.4 gave {@link Potion} a name: it is not the registry id but the suffix the potion lang keys are built
     * from ("item.minecraft.potion.effect.&lt;name&gt;"), so both are taken from the same argument to keep them in step.
     */
    private static Holder<Potion> potion(String name, MobEffectInstance... effects) {
        return POTIONS.register(name, () -> new Potion(name, effects));
    }

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
