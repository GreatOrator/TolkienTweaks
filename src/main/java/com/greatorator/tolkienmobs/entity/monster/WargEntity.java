package com.greatorator.tolkienmobs.entity.monster;

import net.minecraft.world.entity.EntitySpawnReason;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

public class WargEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(WargEntity.class, EntityDataSerializers.INT);
    private boolean isWet;

    public WargEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D);
    }

    @Override
    public ItemStack getHeldItem() {
        return super.getHeldItem();
    }

    @Override
    public void setHeldItem(ItemStack heldItem) {
    }

    @Override
    public void tick(){
        super.tick();
        if (this.isInWaterRainOrBubble()) {
            this.isWet = true;
        }
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof LivingEntity entity) {
            callback.accept(entity, getX(), getY() + 1.90f, getZ());
            this.xRotO = entity.xRotO;
        }
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
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.reassessWeaponGoal();
        this.entityData.set(VARIANT, input.getIntOr("Variant", 0));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        RandomSource randomsource = level.getRandom();
        if (MathUtility.getRandomInteger(15, 1) == 2) {
            MordorOrcEntity entitymordororc = TolkienEntities.ENTITY_TTM_MORDORORC.get().create(this.level, EntitySpawnReason.JOCKEY);
            entitymordororc.moveTo(this.getX(), this.getY(), this.getZ(), this.yRotO, 0.0F);
            entitymordororc.finalizeSpawn(level, difficulty, spawnType, null);
            entitymordororc.startRiding(this);
        }

        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(level, randomsource, difficulty);
        this.reassessWeaponGoal();

        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /**
     * Animations
     *
     * GeckoLib 5 builds a controller without the animatable and hands its handler an AnimationTest, so the
     * controller no longer holds `this`. The RawAnimations are built once, here, instead of once per frame.
     */
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation SHAKE = RawAnimation.begin().then("shake", LoopType.PLAY_ONCE);
    private static final RawAnimation ATTACK = RawAnimation.begin().then("attack", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<WargEntity>("Walk", 1, test ->
                test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP));
        controllers.add(new AnimationController<WargEntity>("Idle", 1, test -> {
            // The wet flag is the warg's own one-shot latch, set in tick() while it is in water or rain and
            // cleared here so a single shake plays per soaking.
            if (this.isWet) {
                this.isWet = false;
                return test.setAndContinue(SHAKE);
            }

            return test.isMoving() ? PlayState.STOP : test.setAndContinue(IDLE);
        }));
        controllers.add(new AnimationController<WargEntity>("Attack", 1, test -> {
            // A swing has to play out: 5 resets the controller when the handler returns STOP, so dropping
            // to STOP mid-swing would end the attack on its first frame.
            if (test.controller().isAnimatingBones()) {
                return PlayState.CONTINUE;
            }

            if (this.swinging) {
                this.swinging = false;
                return test.setAndContinue(ATTACK);
            }

            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_WARG.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_WARG.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_WARG.get();
    }
}
