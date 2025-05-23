package com.greatorator.tolkienmobs.entity.ai.goal.dragon;

import com.greatorator.tolkienmobs.entity.DragonEntity;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlyerWanderGoal extends WaterAvoidingRandomStrollGoal
{
    private final DragonEntity dragon;

    public FlyerWanderGoal(DragonEntity dragon, double speed, float probability)
    {
        super(dragon, speed, probability);
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));

        this.dragon = dragon;
    }

    public FlyerWanderGoal(DragonEntity dragon, double speed)
    {
        this(dragon, speed, 0.001f);
    }

    @Override
    public boolean canUse()
    {
        if (dragon.canBeControlledByRider()) return false;
        if (!dragon.isUsingFlyingNavigator() && dragon.isSleeping() && !dragon.level.isDay()) return false;
        Vec3 vec3d = getPosition();
        if (vec3d != null)
        {
            this.wantedX = vec3d.x;
            this.wantedY = vec3d.y;
            this.wantedZ = vec3d.z;
            this.forceTrigger = false;
            return true;
        }

        return false;
    }

    @Override
    public Vec3 getPosition()
    {
        Vec3 position = null;

        if (dragon.isUsingFlyingNavigator() || (!dragon.isLeashed() && dragon.getRandom().nextFloat() <= probability + 0.02))
        {
            if ((dragon.isSleeping() && !dragon.level.isDay()) || dragon.getRandom().nextFloat() <= probability)
                position = LandRandomPos.getPos(dragon, 20, 25);
            else
            {
                Vec3 vec3d = dragon.getLookAngle();
                if (!dragon.isWithinRestriction())
                    vec3d = Vec3.atLowerCornerOf(dragon.getRestrictCenter()).subtract(dragon.position()).normalize();

                int yOffset = dragon.getAltitude() > 40? 10 : 0;
                position = HoverRandomPos.getPos(dragon, 30, 50, vec3d.x, vec3d.z, MathUtility.PI / 2, 10, yOffset);
            }
            if (position != null && position.y > dragon.getY() + dragon.getBbHeight() && !dragon.isUsingFlyingNavigator()) dragon.setNavigator(DragonEntity.NavigationType.FLYING);
        }
        return position == null? super.getPosition() : position;
    }
}
