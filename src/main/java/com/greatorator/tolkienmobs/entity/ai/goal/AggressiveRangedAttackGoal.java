package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;

/**
 * The throwing equivalent of {@link AggressiveBowAttackGoal}: it marks its mob engaged for as long
 * as it runs, so a snowball, boulder or potion thrower reaches the attack animations that read the
 * aggressive flag.
 */
public class AggressiveRangedAttackGoal extends RangedAttackGoal {
    private final TolkienMonsterEntity mob;

    public AggressiveRangedAttackGoal(TolkienMonsterEntity mob, double speedModifier, int attackInterval, float attackRadius) {
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
