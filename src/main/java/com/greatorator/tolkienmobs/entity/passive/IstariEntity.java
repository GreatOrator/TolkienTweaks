package com.greatorator.tolkienmobs.entity.passive;

import com.greatorator.tolkienmobs.entity.TolkienHeardEntity;
import com.greatorator.tolkienmobs.entity.boss.GoblinKingEntity;
import com.greatorator.tolkienmobs.entity.monster.GoblinEntity;
import com.greatorator.tolkienmobs.entity.monster.HaradrimEntity;
import com.greatorator.tolkienmobs.entity.monster.MordorOrcEntity;
import com.greatorator.tolkienmobs.entity.monster.UrukHaiEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class IstariEntity extends TolkienHeardEntity implements GeoEntity, NeutralMob, RangedAttackMob {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(IstariEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(IstariEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEALING = SynchedEntityData.defineId(IstariEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;
    protected boolean ranged;
    protected boolean hidden;

    public IstariEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ARMOR, 9.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 60, 10.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, HaradrimEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GoblinEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MordorOrcEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GoblinKingEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, UrukHaiEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    public boolean getRanged() {
        return ranged = true;
    }

    public boolean getHidden() {
        return hidden;
    }

    public boolean setHidden(boolean hide) {
        return hide;
    }

    public void setHealing(boolean healing) {
        this.getEntityData().set(DATA_HEALING, healing);
    }

    public boolean isHealing() {
        return this.getEntityData().get(DATA_HEALING);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!this.isHealing()) {
            Vec3 vec3 = target.getDeltaMovement();
            double d0 = target.getX() + vec3.x - this.getX();
            double d1 = target.getEyeY() - 1.1F - this.getY();
            double d2 = target.getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            Holder<Potion> holder = Potions.HARMING;
            if (target instanceof Player) {
                if (target.getHealth() <= 4.0F) {
                    holder = Potions.HEALING;
                } else {
                    holder = Potions.REGENERATION;
                }

                this.setTarget(null);
            }
        }
    }

    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
        builder.define(DATA_HEALING, false);
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(VARIANT, compound.getInt("Variant"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);

        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            int rand = MathUtility.getRandomInteger(100, 1);
            if (!event.isMoving() && !event.getAnimatable().isAggressive()) {
                if (rand <= 80) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                } else {
                    setHidden(false);
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle2"));
                    setHidden(true);
                }
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Combat", 1, (event) -> {
            if (event.getAnimatable().isAggressive() && this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().forceAnimationReset();
                event.getController().setAnimation(RawAnimation.begin().thenPlay("attack"));
                this.swinging =false;
            } else if (event.getAnimatable().isAggressive() && this.getRanged() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                setHidden(false);
                event.getController().forceAnimationReset();
                event.getController().setAnimation(RawAnimation.begin().thenPlay("heal"));
                this.ranged = false;
                setHidden(true);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
