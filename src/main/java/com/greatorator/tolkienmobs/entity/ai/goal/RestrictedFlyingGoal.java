package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienFlyingAmbientEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/** Chooses destinations relative to the persistent spawn position, including perches. */
public class RestrictedFlyingGoal extends WaterAvoidingRandomFlyingGoal {
    protected final PathfinderMob mob;

    public RestrictedFlyingGoal(PathfinderMob mob, double speed, double maxDistance) {
        super(mob, speed);
        this.mob = mob;
        if (maxDistance != TolkienFlyingAmbientEntity.FLIGHT_RADIUS) {
            throw new IllegalArgumentException("Bird flight radius must match the movement controller");
        }
    }

    @Override
    public boolean canUse() {
        return ((TolkienFlyingAmbientEntity) mob).getFlying() && !((TolkienFlyingAmbientEntity) mob).isOrderedToSit() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return ((TolkienFlyingAmbientEntity) mob).getFlying() && !((TolkienFlyingAmbientEntity) mob).isOrderedToSit() && super.canContinueToUse();
    }

    @Nullable
    protected Vec3 restrictPosition(@Nullable Vec3 target) {
        return target == null ? null : ((TolkienFlyingAmbientEntity) mob).restrictFlightTarget(target);
    }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        return restrictPosition(super.getPosition());
    }
}
