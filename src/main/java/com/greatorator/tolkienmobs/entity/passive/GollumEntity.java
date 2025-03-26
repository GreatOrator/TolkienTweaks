package com.greatorator.tolkienmobs.entity.passive;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class GollumEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData.defineId(GollumEntity.class, EntityDataSerializers.BOOLEAN);
    @Nullable
    protected BlockPos waterPos;

    public GollumEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new GollumEntity.GollumEatGoal(this));
        this.goalSelector.addGoal(6, new GollumEntity.GollumMoveToWaterGoal(this, 1.0D, 8, 4));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ARMOR, 6.0D);
    }

    @Override
    public boolean getRanged() {
        return ranged = false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EATING, false);
    }

    @Override
    public ItemStack getHeldItem() {
        return super.getHeldItem();
    }

    @Override
    public void setHeldItem(ItemStack heldItem) {
    }

    public void setEating(boolean eating) {
        this.entityData.set(EATING, eating);
    }

    public boolean isEating() {
        return this.entityData.get(EATING);
    }

    /** Sounds */
    @Override
    protected SoundEvent getAmbientSound()
    {
        return TolkienSounds.IDLE_GOLLUM.get();
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        return TolkienSounds.HURT_GOLLUM.get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TolkienSounds.DEATH_GOLLUM.get();
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && this.isEating()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("eat"));
            } else if(!event.isMoving() && !event.getAnimatable().isAggressive()) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
            }
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (this.swinging) {
                event.getController().forceAnimationReset();
                event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    /** Goals */
    static class GollumMoveToWaterGoal extends MoveToBlockGoal {
        private final GollumEntity gollum;

        public GollumMoveToWaterGoal(GollumEntity pathfinderMob, double speedModifier, int searchRange, int verticalSearchRange) {
            super(pathfinderMob, speedModifier, searchRange, verticalSearchRange);
            this.gollum = pathfinderMob;
        }

        @Override
        public boolean canUse() {
            return !this.gollum.isBaby() && !this.gollum.isEating() && this.gollum.waterPos == null && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.isReachedTarget() && super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, @Nonnull BlockPos pos) {
            if (level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (level.getFluidState(pos.relative(direction)).is(Fluids.WATER)) {
                        this.gollum.waterPos = pos.relative(direction);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public double acceptedDistance() {
            return 2.5D;
        }

        @Override
        public void stop() {
            this.gollum.setEating(true);
            super.stop();
        }
    }

    static class GollumEatGoal extends Goal {
        private final GollumEntity gollum;
        private int eatTicks;

        public GollumEatGoal(GollumEntity gollum) {
            this.gollum = gollum;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.gollum.waterPos == null || this.gollum.distanceToSqr(Vec3.atCenterOf(this.gollum.waterPos)) > 15) {
                this.gollum.setEating(false);
                return false;
            }
            return this.gollum.isEating();
        }

        @Override
        public boolean canContinueToUse() {
            return this.eatTicks > 0 && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.eatTicks = 150;
            if (this.gollum.waterPos != null) {
                this.gollum.getLookControl().setLookAt(Vec3.atCenterOf(this.gollum.waterPos));
            }
        }

        @Override
        public void tick() {
            this.eatTicks--;
            if (this.gollum.waterPos != null) {
                this.gollum.getLookControl().setLookAt(Vec3.atCenterOf(this.gollum.waterPos));
            }
        }

        @Override
        public void stop() {
            this.gollum.waterPos = null;
            this.gollum.setEating(false);
        }
    }
}