package com.greatorator.tolkienmobs.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class ElvenLifeEffect extends TolkienEffect{
    int damageTime = 10;

    public ElvenLifeEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.addEffect((new MobEffectInstance(MobEffects.ABSORPTION, 160, 6 + amplifier, false, false, false)));
        entity.addEffect((new MobEffectInstance(MobEffects.REGENERATION, 900, 6 + amplifier, false, false, false)));

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % damageTime == 0;
    }
}
