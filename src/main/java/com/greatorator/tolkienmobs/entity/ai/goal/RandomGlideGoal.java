package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienFlyingAmbientEntity;
import net.minecraft.world.entity.ai.goal.Goal;


/**
 * Goal that randomly switches between gliding and flapping while flying
 */
public class RandomGlideGoal extends Goal {
    private final TolkienFlyingAmbientEntity bird;
    private int switchTimer;

    public RandomGlideGoal(TolkienFlyingAmbientEntity bird) {
        this.bird = bird;
        // Animation state must not prevent navigation goals from running.
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
    public void stop() {
        bird.setGliding(false);
    }

    @Override
    public boolean canContinueToUse() {
        return bird.getFlying();
    }
}
