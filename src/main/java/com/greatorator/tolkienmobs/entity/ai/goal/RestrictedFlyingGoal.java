package com.greatorator.tolkienmobs.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Restricted flying goal that keeps birds within 64 blocks of their spawn point
 */
public class RestrictedFlyingGoal extends WaterAvoidingRandomFlyingGoal {
    protected final PathfinderMob mob;
    private final double maxDistance;

    public RestrictedFlyingGoal(PathfinderMob mob, double speed, double maxDistance) {
        super(mob, speed);
        this.mob = mob;
        this.maxDistance = maxDistance;
    }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        Vec3 target = super.getPosition();
        
        if (target == null) {
            return null;
        }

        // Calculate distance from spawn point
        double distFromSpawn = mob.distanceToSqr(mob.blockPosition().getCenter());
        
        // If too far from spawn, bias target back toward spawn
        if (distFromSpawn > maxDistance * maxDistance) {
            Vec3 spawnPos = Vec3.atCenterOf(mob.blockPosition());
            Vec3 directionToSpawn = spawnPos.subtract(mob.position()).normalize();
            target = mob.position().add(directionToSpawn.scale(10));
        }

        return target;
    }
}
