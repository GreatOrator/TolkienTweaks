package com.greatorator.tolkienmobs.entity.monster;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
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

public class FellSpiritEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(FellSpiritEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(FellSpiritEntity.class, EntityDataSerializers.INT);

    public FellSpiritEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TolkienItems.SWORD_MORGULIRON.get()));
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

        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(level, randomsource, difficulty);
        this.reassessWeaponGoal();

        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_BARROW.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_BARROW.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.HURT_BARROW.get();
    }

    /**
     * Animations
     *
     * GeckoLib 5 builds a controller without the animatable and hands its handler an AnimationTest, so the
     * controller no longer holds `this`. The RawAnimations are built once, here, instead of once per frame.
     */
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().then("attack", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<FellSpiritEntity>("Walk", 1, test ->
                test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP));
        controllers.add(new AnimationController<FellSpiritEntity>("Idle", 1, test ->
                test.isMoving() ? PlayState.STOP : test.setAndContinue(IDLE)));
        controllers.add(new AnimationController<FellSpiritEntity>("Attack", 1, test -> {
            // A swing has to play out: 5 resets the controller when the handler returns STOP, so dropping
            // to STOP mid-swing would end the attack on its first frame.
            if (test.controller().isAnimatingBones()) {
                return PlayState.CONTINUE;
            }

            if (this.swinging) {
                return test.setAndContinue(ATTACK);
            }

            return PlayState.STOP;
        }));    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}