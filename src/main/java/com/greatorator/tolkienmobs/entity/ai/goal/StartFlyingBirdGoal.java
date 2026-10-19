package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienFlyingAmbientEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class StartFlyingBirdGoal extends Goal {
    private final TolkienFlyingAmbientEntity bird;
    private final int chance;

    private Vec3 target;
    private int flightTicks;


    public StartFlyingBirdGoal(TolkienFlyingAmbientEntity bird, int chance) {
        this.bird = bird;
        this.chance = chance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.bird.getFlying() || this.bird.isOrderedToSit()) {
            return false;
        }

        if (this.bird.getRandom().nextInt(reducedTickDelay(this.chance)) != 0) {
            return false;
        }

        Vec3 start = this.bird.getEyePosition();
        // Try to find a horizontal flight path instead of shooting straight up
        for (int i = 0; i <= 360; i += 45) {
            double yaw = i * 0.0174533;
            // More horizontal flight path - only go up 2-3 blocks, not 8
            Vec3 end = new Vec3(start.x + Math.cos(yaw) * 15f, start.y + 2.0 + this.bird.getRandom().nextFloat() * 2.0, start.z + Math.sin(yaw) * 15f);
            end = bird.restrictFlightTarget(end);
            if (end.y <= bird.getY() + 1.0) continue;
            BlockHitResult result = this.bird.level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, this.bird));
            if (result.getType() != HitResult.Type.MISS) continue;

            target = end;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.bird.getNavigation().stop();
        flightTicks = 0;
        this.bird.setFlying(true);
        this.bird.getMoveControl().setWantedPosition(target.x, target.y, target.z, 1.0);
    }

    @Override
    public boolean canContinueToUse() {
        return !bird.isOrderedToSit() && flightTicks < 100 && bird.distanceToSqr(target) > 1.0;
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void tick() {
        flightTicks++;
        bird.getMoveControl().setWantedPosition(target.x, target.y, target.z, 1.0);
    }

    @Override
    public void stop() {
        if (bird.onGround() || bird.isOrderedToSit()) bird.setFlying(false);
    }
}
