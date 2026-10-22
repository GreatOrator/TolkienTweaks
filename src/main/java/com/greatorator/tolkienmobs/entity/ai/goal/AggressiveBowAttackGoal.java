package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;

/**
 * A bow goal that publishes the mod's shooting flag while it runs, so the client-side animation
 * controllers know the mob is firing. Vanilla's goal already marks the mob aggressive (its start
 * and stop call setAggressive), and it already accepts a bow in either hand, so nothing else here
 * needs overriding - the uruk-hai and mordor orcs carry their uruk bow in the off hand.
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
        this.mob.setShooting(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setShooting(false);
    }
}
