package com.greatorator.tolkienmobs.effect;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class EruEffect extends TolkienEffect{
    public static int damageTime = 10;

    public EruEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        for (MobEffectInstance effect : ImmutableList.copyOf(entity.getActiveEffects())) {
            if (!effect.equals(MobEffectCategory.BENEFICIAL)) {
                entity.removeEffect(effect.getEffect());
            }
        }
        return false;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % damageTime == 0;
    }
}
