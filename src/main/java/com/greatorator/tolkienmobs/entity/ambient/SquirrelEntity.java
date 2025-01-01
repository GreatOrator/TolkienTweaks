package com.greatorator.tolkienmobs.entity.ambient;

import com.greatorator.tolkienmobs.entity.TolkienAmbientEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SquirrelEntity extends TolkienAmbientEntity implements GeoEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(SquirrelEntity.class, EntityDataSerializers.INT);

    private static final ResourceLocation EVIL_ATTACK_POWER_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "evil");
    private static final ResourceLocation KILLER_SQUIRREL = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/sosquirrel/killer_squirrel.png");

    public SquirrelEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, stack -> stack.is(TolkienTags.Items.ACORNS), false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(TolkienTags.Items.ACORNS);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        SquirrelEntity baby = TolkienEntities.ENTITY_TTM_SQUIRREL.get().create(serverLevel);
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
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public TolkienVariant getVariant() {
        return TolkienVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(TolkienVariant variant) {
        if (variant == TolkienVariant.LAVENDER) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(80.0);
            this.heal(100F);
            this.getAttribute(Attributes.ARMOR).setBaseValue(8.0);
            this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.4, true));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FrogEntity.class, true));
            this.getAttribute(Attributes.ATTACK_DAMAGE)
                    .addOrUpdateTransientModifier(new AttributeModifier(EVIL_ATTACK_POWER_MODIFIER, 5.0, AttributeModifier.Operation.ADD_VALUE));
            if (!this.hasCustomName()) {
                this.setCustomName(Component.translatable(Util.makeDescriptionId("entity", KILLER_SQUIRREL)));
            }
        } else {
            this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(EVIL_ATTACK_POWER_MODIFIER);
        }

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

    @Override
    public @NotNull Component getName() {
        Component name = Component.translatable("entity.tolkienmobs.entityttmsquirrel.killer_squirrel");

        return getVariant().equals(TolkienVariant.LAVENDER) ? name : super.getName();
    }

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_SQUIRREL.get();
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return TolkienSounds.HURT_SQUIRREL.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_SQUIRREL.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(TolkienSounds.STEP_SQUIRREL.get(), 0.15F, 1.0F);
    }

    @Override
    public void playAttackSound() {
        if (this.getVariant() == TolkienVariant.LAVENDER) {
            this.playSound(TolkienSounds.ANGRY_SQUIRREL.get(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    @Override
    public SoundSource getSoundSource() {
        return this.getVariant() == TolkienVariant.LAVENDER ? SoundSource.HOSTILE : SoundSource.NEUTRAL;
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && !event.getAnimatable().isAggressive()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
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
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (event.getAnimatable().isAggressive() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
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

    /**
     *BOSS BAR
     */
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.literal("Killer Squirrel"), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_10);

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        if (this.getVariant() == TolkienVariant.LAVENDER) {
            super.startSeenByPlayer(serverPlayer);
            this.bossEvent.addPlayer(serverPlayer);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        if (this.getVariant() == TolkienVariant.LAVENDER) {
            super.stopSeenByPlayer(serverPlayer);
            this.bossEvent.removePlayer(serverPlayer);
        }
    }

    @Override
    public void aiStep() {
        if (this.getVariant() == TolkienVariant.LAVENDER) {
            super.aiStep();
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }
}
