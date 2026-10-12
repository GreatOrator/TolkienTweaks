package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienAmbientEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Goal for eagles to dive attack enemies from the air
 */
public class EagleDiveAttackGoal extends Goal {
    private final TolkienAmbientEntity eagle;
    private LivingEntity target;
    private int diveCooldown;

    public EagleDiveAttackGoal(TolkienAmbientEntity eagle) {
        this.eagle = eagle;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!eagle.getFlying()) {
            return false;
        }

        if (diveCooldown > 0) {
            diveCooldown--;
            return false;
        }

        target = eagle.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        // Only dive if target is on ground and eagle is above
        if (!target.onGround()) {
            return false;
        }

        if (eagle.getY() <= target.getY() + 2.0) {
            return false;
        }

        // Check distance - only dive if within range
        double dist = eagle.distanceToSqr(target);
        return dist < 256.0 && dist > 9.0; // Between 3 and 16 blocks
    }

    @Override
    public void start() {
        eagle.setAggressive(true);
    }

    @Override
    public void tick() {
        if (target == null || !target.isAlive()) {
            return;
        }

        // Look at target
        eagle.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // Dive toward target
        Vec3 targetPos = target.position();
        Vec3 eaglePos = eagle.position();
        Vec3 direction = targetPos.subtract(eaglePos).normalize();
        
        // Increase speed during dive
        double speed = 1.5;
        eagle.getMoveControl().setWantedPosition(
            targetPos.x,
            targetPos.y,
            targetPos.z,
            speed
        );

        // Attack when close enough
        if (eagle.distanceToSqr(target) < 4.0) {
            eagle.doHurtTarget(target);
            diveCooldown = 100; // Cooldown after attack
        }
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && eagle.getFlying();
    }

    @Override
    public void stop() {
        eagle.setAggressive(false);
        target = null;
    }
}
