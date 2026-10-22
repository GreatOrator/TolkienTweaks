package com.greatorator.tolkienmobs.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class LightningEffect extends TolkienEffect {
    public LightningEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity pLivingEntity, int pAmplifier) {
        BlockPos blockpos = pLivingEntity.blockPosition();

        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (lightningBolt != null) {
            lightningBolt.moveTo(Vec3.atBottomCenterOf(blockpos));
            lightningBolt.setCause(null);
            level.addFreshEntity(lightningBolt);
        }
        level.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.WEATHER, 5.0f, 1.0f);
        return false;
    }
}
