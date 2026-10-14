package com.greatorator.tolkienmobs.entity.ai.navigation;

import com.greatorator.tolkienmobs.entity.DragonEntity;
import com.greatorator.tolkienmobs.entity.ai.DragonFlyNodeEvaluator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.pathfinder.PathFinder;

public class FlyerPathNavigator extends FlyingPathNavigation
{
    public FlyerPathNavigator(DragonEntity entity)
    {
        super(entity, entity.level);
    }

    @Override
    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        this.nodeEvaluator = new DragonFlyNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
    }

    @Override
    public boolean isStableDestination(BlockPos pos)
    {
        return true;
    }
}
