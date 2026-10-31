package com.greatorator.tolkienmobs.effect;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * The blessing of Eru: a cure-all, taking every harmful effect off whoever it is given to.
 * <p>
 * It is an instant effect, and an instant effect is never ticked - the game calls
 * {@code applyInstantenousEffect} once and the instance is gone - so the cleansing lives there.
 * Written as a tick it did nothing at all, whatever it was drunk from.
 * <p>
 * The harmful test is the effect's own category. Comparing the instance itself against the category
 * was never true, so the cure also threw away every buff the player was carrying, which is the last
 * thing wanted from a draught taken to be rid of a curse.
 */
public class EruEffect extends TolkienEffect {
    public EruEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(ServerLevel level, @Nullable Entity source, @Nullable Entity indirectSource,
                                        LivingEntity entity, int amplifier, double health) {
        for (MobEffectInstance effect : ImmutableList.copyOf(entity.getActiveEffects())) {
            if (effect.getEffect().value().getCategory() != MobEffectCategory.BENEFICIAL) {
                entity.removeEffect(effect.getEffect());
            }
        }
    }
}
