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

    public FlyerMoveController(DragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
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
                if (y != 0) dragon.setYya(y > 0? speed : -speed);
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
