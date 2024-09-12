package com.greatorator.tolkienmobs.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class FlyingEffect extends TolkienEffect{
    public static int flyDuration = 10;

    public FlyingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.addEffect((new MobEffectInstance(MobEffects.LEVITATION, 40, amplifier, true, false, false, null)));
        entity.addEffect((new MobEffectInstance(MobEffects.WIND_CHARGED, 40, amplifier, true, false, false, null)));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % flyDuration == 0;
    }
}