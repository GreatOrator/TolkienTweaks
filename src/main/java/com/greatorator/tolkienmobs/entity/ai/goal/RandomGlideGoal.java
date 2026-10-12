package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienAmbientEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Goal that randomly switches between gliding and flapping while flying
 */
public class RandomGlideGoal extends Goal {
    private final TolkienAmbientEntity bird;
    private int switchTimer;

    public RandomGlideGoal(TolkienAmbientEntity bird) {
        this.bird = bird;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return bird.getFlying();
    }

    @Override
    public void start() {
        switchTimer = 60 + bird.getRandom().nextInt(100);
        // Start with random glide state
        bird.setGliding(bird.getRandom().nextBoolean());
    }

    @Override
    public void tick() {
        switchTimer--;
        if (switchTimer <= 0) {
            // Switch between glide and flap
            bird.setGliding(!bird.isGliding());
            switchTimer = 60 + bird.getRandom().nextInt(100);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return bird.getFlying();
    }
}
