package com.greatorator.tolkienmobs.entity.ambient;

import com.greatorator.tolkienmobs.entity.TolkienAmbientEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.LandBirdGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.StartFlyingBirdGoal;
import com.greatorator.tolkienmobs.entity.passive.AurochEntity;
import com.greatorator.tolkienmobs.entity.passive.GoatEntity;
import com.greatorator.tolkienmobs.handler.interfaces.CommonTraits;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class GreatEagleEntity extends TolkienAmbientEntity implements GeoEntity, FlyingAnimal, CommonTraits {
    protected static final EntityDataAccessor<Boolean> PECKING = SynchedEntityData.defineId(GreatEagleEntity.class, EntityDataSerializers.BOOLEAN);
    protected BlockPos stonePos;
    public boolean scheduleWeaponGoalUpdate = true;
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2, false) {
        @Override
        public void stop() {
            super.stop();
            GreatEagleEntity.this.setAggressive(false);
        }

        @Override
        public void start() {
            super.start();
            GreatEagleEntity.this.setAggressive(true);
        }
    };

    public GreatEagleEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);

        moveControl = new FlyingMoveControl(this, 20, true);

        setPathfindingMalus(PathType.DANGER_FIRE, -1.0f);
        setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0f);
        setPathfindingMalus(PathType.WATER, -1.0f);
        setPathfindingMalus(PathType.FENCE, -1.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.ARMOR, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 21.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.75F)
                .add(Attributes.FLYING_SPEED, (double) 1.4F);
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
        this.goalSelector.addGoal(2, new StartFlyingBirdGoal(this, 120));
        this.goalSelector.addGoal(3, new LandBirdGoal(this, 240));
        this.goalSelector.addGoal(3, new GreatEagleEntity.WanderGoal(this, 1.0));
        this.goalSelector.addGoal(5, new GreatEagleEntity.BirdPeckGoal(this));
        this.goalSelector.addGoal(6, new GreatEagleEntity.BirdMoveToStoneGoal(this, 1.0D, 8, 4));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Chicken.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Cow.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AurochEntity.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, GoatEntity.class, true));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    @Override
    public void tick() {
        if (scheduleWeaponGoalUpdate) {
            updateWeaponGoal();
        }
        super.tick();
    }

    protected void updateWeaponGoal() {
        scheduleWeaponGoalUpdate = false;
        this.goalSelector.removeGoal(this.meleeGoal);
        this.goalSelector.addGoal(4, this.meleeGoal);
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
        return false;
    }

    @Override
    public GreatEagleEntity getBreedOffspring(ServerLevel world, AgeableMob mate) {
        return null;
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
        return TolkienSounds.IDLE_GREAT_EAGLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_GREAT_EAGLE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_GREAT_EAGLE.get();
    }

    @Override
    public void playAttackSound() {
        this.playSound(TolkienSounds.ATTACK_GREAT_EAGLE.get(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        int rand = MathUtility.getRandomInteger(100, 1);
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && !event.getAnimatable().isAggressive() && this.onGround()) {
                if (rand <= 10 && this.isPecking()) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle3"));
                    return PlayState.CONTINUE;
                }else if (rand <=30) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle2"));
                    return PlayState.CONTINUE;
                }else {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                    return PlayState.CONTINUE;
                }
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (event.getAnimatable().isAggressive() && this.onGround() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().setAnimation(RawAnimation.begin().then("attack_ground", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }else if (event.getAnimatable().isAggressive() && !this.onGround() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("attack_flight"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            double speed = getMovementSpeed(this);
            if (event.isMoving() && this.onGround() && speed > (double)0.2F) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
                return PlayState.CONTINUE;
            }else if (event.isMoving() && this.onGround()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Glide", 1, (event) -> {
            if (!this.onGround() & isFlying()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("glide"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Fly", 1, (event) -> {
            if (event.isMoving() && isFlying()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("fly"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    /**
     * Goals
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PECKING, false);
    }

    public void setPecking(boolean pecking) {
        this.entityData.set(PECKING, pecking);
    }

    public boolean isPecking() {
        return this.entityData.get(PECKING);
    }
    static class BirdMoveToStoneGoal extends MoveToBlockGoal {
        private final GreatEagleEntity bird;

        public BirdMoveToStoneGoal(GreatEagleEntity pathfinderMob, double speedModifier, int searchRange, int verticalSearchRange) {
            super(pathfinderMob, speedModifier, searchRange, verticalSearchRange);
            this.bird = pathfinderMob;
        }

        @Override
        public boolean canUse() {
            return !this.bird.isBaby() && !this.bird.isPecking() && this.bird.stonePos == null && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.isReachedTarget() && super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, @Nonnull BlockPos pos) {
            if (level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (level.getBlockState(pos.relative(direction)).is(Blocks.STONE)) {
                        this.bird.stonePos = pos.relative(direction);
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
            this.bird.setPecking(true);
            super.stop();
        }
    }

    static class BirdPeckGoal extends Goal {
        private final GreatEagleEntity bird;
        private int eatTicks;

        public BirdPeckGoal(GreatEagleEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.bird.stonePos == null || this.bird.distanceToSqr(Vec3.atCenterOf(this.bird.stonePos)) > 15) {
                this.bird.setPecking(false);
                return false;
            }
            return this.bird.isPecking();
        }

        @Override
        public boolean canContinueToUse() {
            return this.eatTicks > 0 && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.eatTicks = 150;
            if (this.bird.stonePos != null) {
                this.bird.getLookControl().setLookAt(Vec3.atCenterOf(this.bird.stonePos));
            }
        }

        @Override
        public void tick() {
            this.eatTicks--;
            if (this.bird.stonePos != null) {
                this.bird.getLookControl().setLookAt(Vec3.atCenterOf(this.bird.stonePos));
            }
        }

        @Override
        public void stop() {
            this.bird.stonePos = null;
            this.bird.setPecking(false);
        }
    }
}