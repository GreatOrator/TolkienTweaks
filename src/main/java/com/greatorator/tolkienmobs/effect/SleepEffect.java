package com.greatorator.tolkienmobs.effect;

import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/**
 * Sleepnesia: the sleeper drops where they stand, blind and slow, and wakes when the effect runs out.
 * <p>
 * An effect is one object shared by every entity that holds it, so the sleep could not be kept in a
 * field: a single flag there made the first sleeper in a world put every later one to bed for good,
 * and never let them up. How much longer this sleeper has is read back from their own instance of
 * the effect instead, which is the only per-entity state the effect has. The pose is put down a few
 * ticks before the effect lapses, because once it has gone there is no tick left to stand anyone up.
 * <p>
 * Only a player is laid down - a sleeping pose is written for a player's bed and says nothing to a
 * mob - so the pose is guarded rather than cast, which is what used to break when a boss handed the
 * effect to something that was not a player.
 */
public class SleepEffect extends TolkienEffect {
    /** Ticks of the effect left when the sleeper is stood up, so the pose never outlives the effect. */
    private static final int WAKE_TAIL = 5;

    public SleepEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        MobEffectInstance instance = entity.getEffect(TolkienMobEffects.SLEEPNESIA);
        if (instance == null) return false;

        boolean sleeping = instance.getDuration() > WAKE_TAIL;
        if (entity instanceof Player player) {
            player.setForcedPose(sleeping ? Pose.SLEEPING : null);
        }
        if (sleeping) {
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, amplifier, true, false, false, null));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, amplifier + 100, true, false, false, null));
        }
        return sleeping;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
