package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MODID);

        // Beneficial
    public static final DeferredHolder<MobEffect, MobEffect> ENT_STANCE = MOB_EFFECTS.register("ent_draught",
            () -> new TolkienEffect(MobEffectCategory.BENEFICIAL, 3135135).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(MODID, "ent_draught"), (double)1.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(MODID, "ent_draught"), (double)1.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> PERSONAL_BLACKSMITH = MOB_EFFECTS.register("personal_blacksmith",
            () -> new BlacksmithEffect(MobEffectCategory.BENEFICIAL, 14370245));
    public static final DeferredHolder<MobEffect, MobEffect> BLESSING_OF_ERU = MOB_EFFECTS.register("blessing_of_eru",
            () -> new EruEffect(MobEffectCategory.BENEFICIAL, 1873869));
    public static final DeferredHolder<MobEffect, MobEffect> ELF_VITALITY = MOB_EFFECTS.register("elf_vitality",
            () -> new ElvenLifeEffect(MobEffectCategory.BENEFICIAL, 14687673));
    public static final DeferredHolder<MobEffect, MobEffect> ELF_NIMBLENESS = MOB_EFFECTS.register("elven_nimbleness",
            () -> new NimbleEffect(MobEffectCategory.BENEFICIAL, 16777062));

        // Harmful
   public static final DeferredHolder<MobEffect, MobEffect> INVENTORY_CORROSION = MOB_EFFECTS.register("inventory_corrosion",
                () -> new CorrosionEffect(MobEffectCategory.HARMFUL, 7811840));
    public static final DeferredHolder<MobEffect, MobEffect> SLEEPNESIA = MOB_EFFECTS.register("sleepnesia",
            () -> new SleepEffect(MobEffectCategory.HARMFUL, 2890775));
    public static final DeferredHolder<MobEffect, MobEffect> ELEMENTAL_BURNING = MOB_EFFECTS.register("elemental_burning",
            () -> new BurningEffect(MobEffectCategory.HARMFUL, 15545365));
    public static final DeferredHolder<MobEffect, MobEffect> ELEMENTAL_LIGHTNING = MOB_EFFECTS.register("elemental_lightning",
            () -> new LightningEffect(MobEffectCategory.HARMFUL, 16640281));
    public static final DeferredHolder<MobEffect, MobEffect> ELEMENTAL_DROWNING = MOB_EFFECTS.register("elemental_drowning",
            () -> new DrownEffect(MobEffectCategory.HARMFUL, 7791097));
    public static final DeferredHolder<MobEffect, MobEffect> ELEMENTAL_FLYING = MOB_EFFECTS.register("elemental_flight",
            () -> new FlyingEffect(MobEffectCategory.HARMFUL, 6498056));
    public static final DeferredHolder<MobEffect, MobEffect> PARALYSING_FEAR = MOB_EFFECTS.register("crippling_terror",
            () -> new TerrorEffect(MobEffectCategory.HARMFUL, 5655556));
    public static final DeferredHolder<MobEffect, MobEffect> WATCHER_FEAR = MOB_EFFECTS.register("dread_aura",
            () -> new FearEffect(MobEffectCategory.HARMFUL, 4852999));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}