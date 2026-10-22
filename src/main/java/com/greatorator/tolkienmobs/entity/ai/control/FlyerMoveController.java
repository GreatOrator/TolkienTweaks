package com.greatorator.tolkienmobs.entity.ai.control;

import com.greatorator.tolkienmobs.entity.DragonEntity;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlyerMoveController extends MoveControl
{
    private final DragonEntity dragon;
    /** Whether the navigator has given this controller a target at least once. */
    private boolean hasTarget;

    public FlyerMoveController(DragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed)
    {
        super.setWantedPosition(x, y, z, speed);
        this.hasTarget = true;
    }

    /**
     * The height the dragon should be flying at. The wing model reads it to decide whether to beat
     * or glide, and it outlives the move itself: wantedY keeps the last waypoint's height while the
     * path is between legs, so a beast that has reached a waypoint bobs there instead of falling.
     */
    public double getWantedY()
    {
        return wantedY;
    }

    public boolean hasTarget()
    {
        return hasTarget;
    }

    public void tick()
    {
        if (dragon.canBeControlledByRider())
        {
            operation = Operation.WAIT;
            return;
        }
        if (operation == Operation.MOVE_TO)
        {
            double x = wantedX - dragon.getX();
            double y = wantedY - dragon.getY();
            double z = wantedZ - dragon.getZ();
            double distSq = x * x + y * y + z * z;
            if (distSq < 2.5000003E-7)
            {
                dragon.setZza(0f);
                return;
            }
            if (y > 1) dragon.setNavigator(DragonEntity.NavigationType.FLYING);
            float speed;

            if (dragon.isUsingFlyingNavigator())
            {
                speed = (float) (dragon.getAttributeValue(Attributes.FLYING_SPEED) * speedModifier);

                if (!dragon.getLookControl().isLookingAtTarget())
                    dragon.getLookControl().setLookAt(wantedX, wantedY, wantedZ, dragon.getHeadRotSpeed(), 75);
                // No yya here: the wings own the vertical. DragonEntity#flyOnWings climbs by beating
                // toward this controller's wantedY and sinks between beats, so setting a climb here
                // would only add a second, instantaneous one on top of it.
            }
            else
            {
                speed = (float) (dragon.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedModifier);
                BlockPos blockpos = dragon.blockPosition();
                BlockState state = dragon.level.getBlockState(blockpos);
                Block block = state.getBlock();
                VoxelShape voxelshape = state.getCollisionShape(dragon.level, blockpos);
                if (y > (double) dragon.getAttributeValue(Attributes.STEP_HEIGHT) && x * x + z * z < (double) Math.max(1.0F, dragon.getBbWidth()) || !voxelshape.isEmpty() && dragon.getY() < voxelshape.max(Direction.Axis.Y) + (double) blockpos.getY() && !state.is(BlockTags.DOORS) && !state.is(BlockTags.FENCES))
                {
                    dragon.getJumpControl().jump();
                    operation = Operation.JUMPING;
                }
            }
            dragon.setYRot(rotlerp(dragon.getYRot(), (float) (Mth.atan2(z, x) * (180f / MathUtility.PI)) - 90f, dragon.getYawRotationSpeed()));
            dragon.setSpeed(speed);
            operation = Operation.WAIT;
        }
        else
        {
            dragon.setSpeed(0);
            dragon.setXxa(0);
            dragon.setYya(0);
            dragon.setZza(0);
        }
    }
}
