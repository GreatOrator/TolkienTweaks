package com.greatorator.tolkienmobs.entity.passive;

import com.greatorator.tolkienmobs.entity.TolkienHeardEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Random;

public class MumakilEntity extends TolkienHeardEntity implements GeoEntity {
    private static final int MAX_STRENGTH = 5;
    private static final EntityDataAccessor<Integer> DATA_STRENGTH_ID = SynchedEntityData.defineId(MumakilEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(MumakilEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DRINKING = SynchedEntityData.defineId(MumakilEntity.class, EntityDataSerializers.BOOLEAN);
    @Nullable
    protected BlockPos waterPos;
    boolean didAttack;

    public MumakilEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new MumakilDrinkWaterGoal(this));
        this.goalSelector.addGoal(6, new MumakilMoveToWaterGoal(this, 1.0D, 8, 4));
        this.targetSelector.addGoal(1, new MumakilEntity.MumakilHurtByTargetGoal(this));
    }

    private void setStrength(int strength) {
        this.entityData.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, strength)));
    }

    private void setRandomStrength(RandomSource random) {
        int i = random.nextFloat() < 0.04F ? 5 : 3;
        this.setStrength(1 + random.nextInt(i));
    }

    public int getStrength() {
        return this.entityData.get(DATA_STRENGTH_ID);
    }

    void setDidAttack(boolean didAttack) {
        this.didAttack = didAttack;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            this.setDidAttack(false);
        }

        return super.hurt(source, amount);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        MumakilEntity baby = TolkienEntities.ENTITY_TTM_MUMAKIL.get().create(serverLevel);
        baby.setVariant(variant);

        return baby;
    }

    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(DRINKING, false);
        builder.define(DATA_STRENGTH_ID, 0);
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public TolkienVariant getVariant() {
        return TolkienVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(TolkienVariant variant) {
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getTypeVariant());
        compound.putInt("Strength", this.getStrength());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setStrength(compound.getInt("Strength"));
        super.readAdditionalSaveData(compound);
        this.entityData.set(VARIANT, compound.getInt("Variant"));
        this.entityData.set(DRINKING, false);
    }

    public void setDrinking(boolean drinking) {
        this.entityData.set(DRINKING, drinking);
    }

    public boolean isDrinking() {
        return this.entityData.get(DRINKING);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        RandomSource randomsource = level.getRandom();

        this.setRandomStrength(randomsource);
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_MUMAKIL.get();
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return TolkienSounds.HURT_MUMAKIL.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.HURT_MUMAKIL.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && !event.getAnimatable().isAggressive()) {
                if (this.isInWater()) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle_water"));
                }else {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                }
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            if (event.isMoving()) {
                if (this.isInWater()) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("walking_water"));
                }else {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("walking"));
                }
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Drink", 1, (event) -> {
            if (this.isDrinking()) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("drink"));
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

    static class MumakilMoveToWaterGoal extends MoveToBlockGoal {
        private final MumakilEntity mumakil;

        public MumakilMoveToWaterGoal(MumakilEntity pathfinderMob, double speedModifier, int searchRange, int verticalSearchRange) {
            super(pathfinderMob, speedModifier, searchRange, verticalSearchRange);
            this.mumakil = pathfinderMob;
        }

        @Override
        public boolean canUse() {
            return !this.mumakil.isBaby() && !this.mumakil.isDrinking() && this.mumakil.waterPos == null && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.isReachedTarget() && super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            if (level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (level.getFluidState(pos.relative(direction)).is(Fluids.WATER)) {
                        this.mumakil.waterPos = pos.relative(direction);
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
            this.mumakil.setDrinking(true);
            super.stop();
        }
    }

    static class MumakilDrinkWaterGoal extends Goal {
        private final MumakilEntity mumakil;
        private int drinkTicks;

        public MumakilDrinkWaterGoal(MumakilEntity mumakil) {
            this.mumakil = mumakil;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.mumakil.waterPos == null || this.mumakil.distanceToSqr(Vec3.atCenterOf(this.mumakil.waterPos)) > 15) {
                this.mumakil.setDrinking(false);
                return false;
            }
            return this.mumakil.isDrinking();
        }

        @Override
        public boolean canContinueToUse() {
            return this.drinkTicks > 0 && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.drinkTicks = 150;
            if (this.mumakil.waterPos != null) {
                this.mumakil.getLookControl().setLookAt(Vec3.atCenterOf(this.mumakil.waterPos));
            }
        }

        @Override
        public void tick() {
            this.drinkTicks--;
            if (this.mumakil.waterPos != null) {
                this.mumakil.getLookControl().setLookAt(Vec3.atCenterOf(this.mumakil.waterPos));
            }
        }

        @Override
        public void stop() {
            this.mumakil.waterPos = null;
            this.mumakil.setDrinking(false);
        }
    }

    static class MumakilHurtByTargetGoal extends HurtByTargetGoal {
        public MumakilHurtByTargetGoal(MumakilEntity mumakil) {
            super(mumakil);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob instanceof MumakilEntity mumakil && mumakil.didAttack) {
                mumakil.setDidAttack(false);
                return false;
            }

            return super.canContinueToUse();
        }
    }
}