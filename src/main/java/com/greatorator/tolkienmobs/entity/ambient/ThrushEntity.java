package com.greatorator.tolkienmobs.entity.ambient;

import com.greatorator.tolkienmobs.entity.TolkienAmbientEntity;
import com.greatorator.tolkienmobs.entity.util.BinaryAnimation;
import com.greatorator.tolkienmobs.entity.util.Easings;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ThrushEntity extends TolkienAmbientEntity implements FlyingAnimal {
    public final BinaryAnimation flyAnimationState = new BinaryAnimation(SharedConstants.TICKS_PER_SECOND / 4, Easings::inOutSine);

    public ThrushEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);

        moveControl = new FlyingMoveControl(this, 20, true);

        setPathfindingMalus(PathType.DANGER_FIRE, -1.0f);
        setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0f);
        setPathfindingMalus(PathType.WATER, -1.0f);
        setPathfindingMalus(PathType.FENCE, -1.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, world);
        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(true);
        navigator.setCanPassDoors(true);
        return navigator;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new TemptGoal(this, 1.25, stack -> stack.is(TolkienTags.Items.INSECTS), false));
        goalSelector.addGoal(3, new WanderGoal(this, 1.0));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            flyAnimationState.tick(isFlying());
        }
    }

    public float getFlightAnimation(float partialTicks) {
        return flyAnimationState.get(partialTicks);
    }

    @Override
    public boolean isFlying() {
        return !onGround();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(TolkienTags.Items.INSECTS);
    }

    @Override
    public ThrushEntity getBreedOffspring(ServerLevel world, AgeableMob mate) {
        return TolkienEntities.ENTITY_TTM_THRUSH.get().create(world);
    }

    public static class WanderGoal extends WaterAvoidingRandomFlyingGoal {
        private static final int TREE_HORIZONTAL_RANGE = 3;
        private static final int TREE_VERTICAL_RANGE = 6;

        public WanderGoal(PathfinderMob mob, double speed) {
            super(mob, speed);
        }

        @Override
        @Nullable
        protected Vec3 getPosition() {
            Vec3 target = null;
            if (mob.isInWater()) {
                target = LandRandomPos.getPos(mob, 15, 15);
            }

            if (mob.getRandom().nextFloat() >= probability) {
                target = findTreePos();
            }

            return target == null ? super.getPosition() : target;
        }

        @Nullable
        private Vec3 findTreePos() {
            Level level = mob.level();
            BlockPos currentPos = mob.blockPosition();
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (BlockPos pos : BlockPos.betweenClosed(
                    Mth.floor(mob.getX() - TREE_HORIZONTAL_RANGE), Mth.floor(mob.getY() - TREE_VERTICAL_RANGE), Mth.floor(mob.getZ() - TREE_HORIZONTAL_RANGE),
                    Mth.floor(mob.getX() + TREE_HORIZONTAL_RANGE), Mth.floor(mob.getY() + TREE_VERTICAL_RANGE), Mth.floor(mob.getZ() + TREE_HORIZONTAL_RANGE))
            ) {
                if (currentPos.equals(pos)) {
                    continue;
                }
                BlockState belowState = level.getBlockState(mutablePos.setWithOffset(pos, Direction.DOWN));
                if (belowState.is(BlockTags.LEAVES) || belowState.is(BlockTags.LOGS)) {
                    BlockPos abovePos = mutablePos.setWithOffset(pos, Direction.UP);
                    if (level.isEmptyBlock(pos) && level.isEmptyBlock(abovePos)) {
                        return Vec3.atBottomCenterOf(pos);
                    }
                }
            }

            return null;
        }
    }

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_THRUSH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_THRUSH.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_THRUSH.get();
    }
}