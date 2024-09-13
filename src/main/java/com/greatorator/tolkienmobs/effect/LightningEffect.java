package com.greatorator.tolkienmobs.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            Level world = pLivingEntity.getCommandSenderWorld();
            BlockPos blockpos = pLivingEntity.blockPosition();

            LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(world);
            lightningboltentity.moveTo(Vec3.atBottomCenterOf(blockpos));
            lightningboltentity.setCause(null);
            world.addFreshEntity(lightningboltentity);
            world.playSound((Player) null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.WEATHER, 5.0f, 1.0f);
        }
        return false;
    }
}
