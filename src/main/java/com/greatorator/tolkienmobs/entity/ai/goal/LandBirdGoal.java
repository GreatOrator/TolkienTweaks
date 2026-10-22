package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienFlyingAmbientEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LandBirdGoal extends Goal {
    private final TolkienFlyingAmbientEntity bird;
    private final int chance;

    private BlockPos pos;
    private int landingTicks;


    public LandBirdGoal(TolkienFlyingAmbientEntity bird, int chance) {
        this.bird = bird;
        this.chance = chance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.bird.getFlying()) {
            return false;
        }

        if (this.bird.getRandom().nextInt(reducedTickDelay(this.chance)) != 0) {
            return false;
        }

        Vec3 end = this.bird.getEyePosition();
        end = new Vec3(end.x, bird.level().getMinY(), end.z);
        BlockHitResult result = this.bird.level.clip(new ClipContext(this.bird.getEyePosition(), end, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, this.bird));
        if (result.getType() != HitResult.Type.BLOCK) {
            return false;
        }
        BlockState block = this.bird.level.getBlockState(result.getBlockPos());
        if (!block.isSolid()) {
            return false;
        }

        pos = result.getBlockPos();
        return true;
    }

    @Override
    public void start() {
        bird.getNavigation().stop();
        landingTicks = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void tick() {
        landingTicks++;
        // Aim slightly into the solid surface so collision actually puts the bird on ground.
        bird.getMoveControl().setWantedPosition(pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 1.0);
    }

    @Override
    public boolean canContinueToUse() {
        return !bird.onGround() && !bird.isOrderedToSit() && landingTicks < 200;
    }

    @Override
    public void stop() {
        if (bird.onGround() || bird.isOrderedToSit()) bird.setFlying(false);
    }
}
