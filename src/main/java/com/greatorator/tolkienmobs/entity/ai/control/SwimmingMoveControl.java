package com.greatorator.tolkienmobs.entity.ai.control;

import com.greatorator.tolkienmobs.entity.DragonEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForgeMod;

public class SwimmingMoveControl extends MoveControl {
    private final float maxTurnPitch = 85;
    private final DragonEntity entity;

    public SwimmingMoveControl(DragonEntity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO && !this.entity.getNavigation().isDone()) {
            double entityToTargetX = this.wantedX - this.entity.getX();
            double entityToTargetY = this.wantedY - this.entity.getY();
            double entityToTargetZ = this.wantedZ - this.entity.getZ();
            double distanceToTargetSqr = entityToTargetX * entityToTargetX + entityToTargetY * entityToTargetY + entityToTargetZ * entityToTargetZ;
            if (distanceToTargetSqr < (double)2.5000003E-7F) {
                this.entity.setZza(0.0F);
            } else {
                float desiredAngleYaw = (float)(Mth.atan2(entityToTargetZ, entityToTargetX) * (double)(180F / (float)Math.PI)) - 90.0F;
                float currentAngleYaw = this.entity.getYRot();
                float maxTurnYaw;
                if (this.entity.getBreaching()){
                    maxTurnYaw = 15;
                }
                else if ((this.entity.getTarget() != null) || this.entity.canBeControlledByRider()) {
                    maxTurnYaw = 15;
                } else {
                    maxTurnYaw = 6;
                }
                this.entity.setYRot(this.rotlerp(currentAngleYaw, desiredAngleYaw, maxTurnYaw));
                this.entity.yBodyRot = this.entity.getYRot();
                this.entity.yHeadRot = this.entity.getYRot();

                float speed = ((float) (this.entity.getAttributeValue(NeoForgeMod.SWIM_SPEED) * this.speedModifier));
                if (this.entity.isInWater() || this.entity.level.getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()).getBlock().defaultBlockState().is(Blocks.WATER)) {
                    this.entity.setSpeed(speed);
                    double horizontalDistanceToTarget = Math.sqrt(entityToTargetX * entityToTargetX + entityToTargetZ * entityToTargetZ);

                    //If we are not directly above or below the target...
                    //Or if we are not at the target level...
                    if (Math.abs(entityToTargetY) > (double)1.0E-5F || Math.abs(horizontalDistanceToTarget) > (double)1.0E-5F) {
                        float desiredAnglePitch = -((float)(Mth.atan2(entityToTargetY, horizontalDistanceToTarget) * (double)(180F / (float)Math.PI)));
                        desiredAnglePitch = Mth.clamp(Mth.wrapDegrees(desiredAnglePitch), (float)(-this.maxTurnPitch), (float)this.maxTurnPitch);
                        float currentAnglePitch = this.entity.getXRot();
                        this.entity.setXRot(this.rotlerp(currentAnglePitch, desiredAnglePitch, 5.0F));
                    }
                    //Adjust acceleration components, this ensures the entity does not freeze entirely when reaching its target...
                    float f4 = Mth.cos(this.entity.getXRot() * ((float)Math.PI / 180F));
                    float f3 = Mth.sin(this.entity.getXRot() * ((float)Math.PI / 180F));
                    this.entity.zza = f4 * speed;
                    this.entity.yya = -f3 * speed;
                } else {
                    this.entity.setSpeed(speed);
                }

            }
        } else {
            this.entity.setSpeed(0.0F);
            this.entity.setXxa(0.0F);
            this.entity.setYya(0.0F);
            this.entity.setZza(0.0F);
        }
    }
}