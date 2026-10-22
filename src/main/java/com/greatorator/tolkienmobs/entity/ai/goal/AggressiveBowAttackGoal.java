package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;

/**
 * A bow goal that also marks its mob engaged for as long as it runs. The vanilla goal never touches
 * the mod's aggressive flag, so a mob that only ever shoots stayed "calm" and every animation gated
 * on that flag - including its own ranged attack - was never played.
 */
public class AggressiveBowAttackGoal extends RangedBowAttackGoal<TolkienMonsterEntity> {
    private final TolkienMonsterEntity mob;

    public AggressiveBowAttackGoal(TolkienMonsterEntity mob, double speedModifier, int attackInterval, float attackRadius) {
        super(mob, speedModifier, attackInterval, attackRadius);
        this.mob = mob;
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
        this.mob.setShooting(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.setShooting(false);
    }
}
