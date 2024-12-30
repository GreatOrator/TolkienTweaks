package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.monster.RomieWalkerEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class RomieWalkerAttackGoal extends MeleeAttackGoal {
    private final RomieWalkerEntity romieWalker;
    private int raiseArmTicks;

    public RomieWalkerAttackGoal(RomieWalkerEntity romieWalker, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(romieWalker, speedModifier, followingTargetEvenIfNotSeen);
        this.romieWalker = romieWalker;
    }

    @Override
    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.romieWalker.setAggressive(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.raiseArmTicks++;
        if (this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
            this.romieWalker.setAggressive(true);
        } else {
            this.romieWalker.setAggressive(false);
        }
    }
}
