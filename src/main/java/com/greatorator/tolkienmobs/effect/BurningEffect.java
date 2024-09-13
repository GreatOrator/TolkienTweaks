package com.greatorator.tolkienmobs.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class BurningEffect extends TolkienEffect {
    public static float fireDuration = 10;

    public BurningEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide()) {
            if (livingEntity instanceof WitherSkeleton)
                livingEntity.hurt(livingEntity.damageSources().magic(), 1.5f);
            else
                livingEntity.hurt(livingEntity.damageSources().onFire(), 1.5f);
        }

        if (livingEntity.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.FLAME,
                    livingEntity.getX() + (level.getRandom().nextGaussian() / 5.0),
                    livingEntity.getY() + (level.getRandom().nextGaussian() / 3.0),
                    livingEntity.getZ() + (level.getRandom().nextGaussian() / 5.0),
                    2, 0.0, 0.0, 0.0, 0.0F);
            level.sendParticles(ParticleTypes.SMOKE,
                    livingEntity.getX() + (level.getRandom().nextGaussian() / 5.0),
                    livingEntity.getY() + (level.getRandom().nextGaussian() / 3.0),
                    livingEntity.getZ() + (level.getRandom().nextGaussian() / 5.0),
                    2, 0.0, 0.0, 0.0, 0.0F);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % fireDuration == 0;
    }
}
