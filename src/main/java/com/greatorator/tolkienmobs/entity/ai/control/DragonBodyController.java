package com.greatorator.tolkienmobs.entity.ai.control;

import com.greatorator.tolkienmobs.entity.DragonEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

public class DragonBodyController extends BodyRotationControl
{
    public DragonEntity dragon;

    public DragonBodyController(DragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
    }

    @Override
    public void clientTick()
    {
        // animate limbs when rotating
        float deg = Math.min(Math.abs(dragon.getYRot() - dragon.yBodyRot) * 0.05f, 1f);
        dragon.pitchExtremities += deg * (1 - dragon.pitchExtremities * 2);

        // sync the body to the yRot; no reason to have any other random rotations.
        dragon.yBodyRot = dragon.getYRot();

        // clamp head rotations so necks don't fucking turn inside out
        dragon.yHeadRot = Mth.rotateIfNecessary(dragon.yHeadRot, dragon.yBodyRot, dragon.getMaxHeadYRot());
    }
}
