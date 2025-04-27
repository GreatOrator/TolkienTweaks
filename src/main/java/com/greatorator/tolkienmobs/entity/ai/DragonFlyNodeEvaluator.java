package com.greatorator.tolkienmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;

public class DragonFlyNodeEvaluator extends FlyNodeEvaluator {


    public PathType getPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        PathType blockpathtypes = getPathTypeFromState(pLevel, blockpos$mutableblockpos.set(pX, pY, pZ));
        if (blockpathtypes == PathType.OPEN && pY >= pLevel.getMinBuildHeight() + 1) {
            PathType blockpathtypes1 = getPathTypeFromState(pLevel, blockpos$mutableblockpos.set(pX, pY - 1, pZ));
            if (blockpathtypes1 != PathType.DAMAGE_FIRE && blockpathtypes1 != PathType.LAVA) {
                if (blockpathtypes1 == PathType.DAMAGE_OTHER) {
                    blockpathtypes = PathType.DAMAGE_OTHER;
                } else if (blockpathtypes1 == PathType.COCOA) {
                    blockpathtypes = PathType.COCOA;
                } else if (blockpathtypes1 == PathType.FENCE) {
                    blockpathtypes = PathType.FENCE;
                } else {
                    blockpathtypes = blockpathtypes1 != PathType.WALKABLE && blockpathtypes1 != PathType.OPEN && blockpathtypes1 != PathType.WATER ? PathType.WALKABLE : PathType.OPEN;
                }
            } else {
                blockpathtypes = PathType.DAMAGE_FIRE;
            }
        }
        return blockpathtypes;
    }
}