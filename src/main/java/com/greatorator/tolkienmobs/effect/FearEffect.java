package com.greatorator.tolkienmobs.effect;

import com.greatorator.tolkienmobs.faction.Courage;
import com.greatorator.tolkienmobs.faction.CourageLedger;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * The dread aura of the Watcher stones and of Shelob: wither, blindness, weakness and a dragging
 * slowness, where the terror pins rather than slows.
 * <p>
 * It is the last thing courage answers. At {@link Courage#UNBOWED} the aura is refused, but only the
 * aura: the wither is not fear, it is what a gaze does to flesh, so it still lands. Everything below
 * that rung takes the whole of it, which keeps the Watcher dangerous long after the Nine have stopped
 * being frightening.
 */
public class FearEffect extends TolkienEffect{
    int fearDuration = 10;

    public FearEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        Courage courage = Courage.of(CourageLedger.courage(entity));

        if (courage.refusesDread()) {
            // Nothing left of the dread but what it does to the body.
            entity.addEffect((new MobEffectInstance(MobEffects.WITHER, 40, amplifier, true, false, false, null)));
            return true;
        }

        if (entity instanceof ServerPlayer player) {
            CourageLedger.fearWithstood(player);
        }

        entity.addEffect((new MobEffectInstance(MobEffects.WITHER, 40, amplifier, true, false, false, null)));
        entity.addEffect((new MobEffectInstance(MobEffects.BLINDNESS, 40, amplifier, true, false, false, null)));
        entity.addEffect((new MobEffectInstance(MobEffects.WEAKNESS, 40, amplifier, true, false, false, null)));
        entity.addEffect((new MobEffectInstance(MobEffects.SLOWNESS, 40, amplifier, true, false, false, null)));

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
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % fearDuration == 0;
    }
}
