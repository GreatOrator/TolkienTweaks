package com.greatorator.tolkienmobs.entity.monster;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;

public class MimicChestEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(MimicChestEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(MimicChestEntity.class, EntityDataSerializers.INT);

    public MimicChestEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 5.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.3F));
    }

    public void setActive(boolean active) {
        this.entityData.set(ATTACKING, active);
    }

    public boolean isActive() {
        return this.entityData.get(ATTACKING);
    }

    /**
     * Special Attack
     */
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        long time = System.currentTimeMillis();
        if (super.doHurtTarget(entityIn)) {
            if (entityIn instanceof Player) {
                if (time > nextAbilityUse) {
                    nextAbilityUse = time + coolDown;
                    Player player = (Player) entityIn;
                    int strength = 2;
                    player.addEffect(new MobEffectInstance(TolkienMobEffects.INVENTORY_CORROSION, strength * 20, 0));
                }
            }
        }
        return true;
    }

    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
        builder.define(VARIANT, 0);
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
        this.reassessWeaponGoal();
        this.entityData.set(VARIANT, compound.getInt("Variant"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setActive(false);
        this.setNoAi(true);

        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        this.setVariant(variant);

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!this.isActive() && !this.level.isClientSide) {
            this.setActive(true);
            this.setNoAi(false);
            this.isEffectiveAi();
            this.setCustomName(Component.translatable("entity.tolkienmobs.entityttmmimicchest"));
            return InteractionResult.PASS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void playStepSound(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        this.playSound(TolkienSounds.STEP_MIMIC.get(), 0.15F, 1.0F);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && this.isActive()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }else {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle_mimic"));
                return PlayState.CONTINUE;
            }
        }));
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (this.isActive() && this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().forceAnimationReset();
                this.playSound(TolkienSounds.ANGRY_MIMIC.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}