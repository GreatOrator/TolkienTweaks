package com.greatorator.tolkienmobs.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class TerrorEffect extends TolkienEffect{
    int fearDuration = 10;

    public TerrorEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.addEffect((new MobEffectInstance(MobEffects.BLINDNESS, 40, amplifier, true, false, false, null)));
        entity.addEffect((new MobEffectInstance(MobEffects.WEAKNESS, 40, amplifier, true, false, false, null)));
        entity.addEffect((new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, amplifier+100, true, false, false, null)));

        if (entity.level() instanceof ServerLevel serverLevel) {
            RandomSource random = entity.getRandom();

            serverLevel.sendParticles(
                    ParticleTypes.DRIPPING_WATER,
                    entity.position().x,
                    entity.position().y + (random.nextFloat() * entity.getEyeHeight()),
                    entity.position().z,
                    3,
                    entity.getBbWidth() * 0.5f,
                    entity.getBbHeight() * 0.5f,
                    entity.getBbWidth() * 0.5f,
                    0.8d);
        }
        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % fearDuration == 0;
    }
}
