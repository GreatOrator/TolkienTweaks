package com.greatorator.tolkienmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class TolkienEffect extends MobEffect {
    public TolkienEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

//    @Override
//    public boolean applyEffectTick(LivingEntity l, int amplifier) {
//
//        if (this == FootworkEffects.FEAR.get() && l.level() instanceof ServerLevel s) {
//            s.sendParticles(ParticleTypes.DRIPPING_WATER, l.getX(), l.getY() + l.getBbHeight() / 2, l.getZ(), 5, l.getBbWidth() / 4, l.getBbHeight() / 4, l.getBbWidth() / 4, 0.5f);
//        }
//        return false;
//    }
}
