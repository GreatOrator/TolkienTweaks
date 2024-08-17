package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.effect.BlacksmithEffect;
import com.greatorator.tolkienmobs.effect.TolkienEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MODID);

    public static final Holder<MobEffect> ENT_STANCE = MOB_EFFECTS.register("ent_draught",
            () -> new TolkienEffect(MobEffectCategory.BENEFICIAL, 3135135).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,
                    ResourceLocation.fromNamespaceAndPath(MODID, "ent_draught"), (double)1.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> PERSONAL_BLACKSMITH = MOB_EFFECTS.register("personal_blacksmith",
            () -> new BlacksmithEffect(MobEffectCategory.BENEFICIAL, 14370245));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}