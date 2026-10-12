package com.greatorator.tolkienmobs.entity.ai.control;

import com.greatorator.tolkienmobs.entity.TolkienFlyingAmbientEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

/** Uses vanilla flight steering, with the same limits for every navigation/attack goal. */
public class FreeFlightMoveControl extends FlyingMoveControl {
    private final TolkienFlyingAmbientEntity bird;
    private final MoveControl groundControl;

    public FreeFlightMoveControl(TolkienFlyingAmbientEntity bird) {
        super(bird, 20, true);
        this.bird = bird;
        this.groundControl = new MoveControl(bird);
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        Vec3 target = new Vec3(x, y, z);
        if (bird.getFlying()) target = bird.restrictFlightTarget(target);
        super.setWantedPosition(target.x, target.y, target.z, speed);
    }

    @Override
    public void tick() {
        if (!bird.getFlying()) {
            if (operation == Operation.MOVE_TO) {
                groundControl.setWantedPosition(wantedX, wantedY, wantedZ, speedModifier);
                operation = Operation.WAIT;
            }
            groundControl.tick();
            bird.setNoGravity(false);
        } else {
            super.tick();
            bird.setNoGravity(true);
        }
    }
}
