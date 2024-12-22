package com.greatorator.tolkienmobs.entity.passive;

import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GoatEntity extends AbstractChestedHorse implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(GoatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ID_CHEST = SynchedEntityData.defineId(GoatEntity.class, EntityDataSerializers.BOOLEAN);

    private int warningSoundTicks;

    public GoatEntity(EntityType<? extends AbstractChestedHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new GoatEntity.GoatEntityMeleeAttackGoal());
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(TolkienItems.PIPEWEED_ITEM.get()), false));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.JUMP_STRENGTH)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 8.0D);
    }

    @Override
    protected void randomizeAttributes(RandomSource random) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.generateRandomMaxHealth());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)this.generateRandomSpeed());
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue((double)this.generateRandomJumpStrength());
    }

    protected float generateRandomMaxHealth() {
        return 20.0F + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
    }

    protected double generateRandomJumpStrength() {
        return (double)0.6F + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D;
    }

    protected double generateRandomSpeed() {
        return ((double)0.55F + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    public boolean hasChest() {
        return (Boolean)this.entityData.get(DATA_ID_CHEST);
    }

    public void setChest(boolean chested) {
        this.entityData.set(DATA_ID_CHEST, chested);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(DATA_ID_CHEST, false);
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
        compound.putBoolean("ChestedGoat", this.hasChest());
        compound.putInt("Variant", this.getTypeVariant());
        if (this.hasChest()) {
            ListTag listtag = new ListTag();

            for(int i = 1; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag compoundtag = new CompoundTag();
                    compoundtag.putByte("Slot", (byte)(i - 1));
                    listtag.add(itemstack.save(this.registryAccess(), compoundtag));
                }
            }
            compound.put("Items", listtag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setChest(compound.getBoolean("ChestedGoat"));
        this.entityData.set(VARIANT, compound.getInt("Variant"));
        this.createInventory();
        if (this.hasChest()) {
            ListTag listtag = compound.getList("Items", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                int j = compoundtag.getByte("Slot") & 255;
                if (j < this.inventory.getContainerSize() - 1) {
                    this.inventory.setItem(j + 1, (ItemStack)ItemStack.parse(this.registryAccess(), compoundtag).orElse(ItemStack.EMPTY));
                }
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);

        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /** Sounds */
    protected void playGallopSound(SoundType soundType) {
        this.playSound(SoundEvents.HORSE_GALLOP, soundType.getVolume() * 0.15F, soundType.getPitch());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return TolkienSounds.IDLE_GOAT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return TolkienSounds.DEATH_GOAT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return TolkienSounds.HURT_GOAT.get();
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return TolkienSounds.ANGRY_GOAT.get();
    }

    @javax.annotation.Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(TolkienSounds.SCREAM_GOAT.get(), 1.0F, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }

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
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    /** Goals */
    class GoatEntityMeleeAttackGoal extends MeleeAttackGoal {
        public GoatEntityMeleeAttackGoal() {
            super(GoatEntity.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(LivingEntity p_29589_, double p_29590_) {
            double d0 = this.getAttackReachSqr(p_29589_);
            if (p_29590_ <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(p_29589_);
                GoatEntity.this.setStanding(false);
            } else if (p_29590_ <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    GoatEntity.this.setStanding(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    GoatEntity.this.setStanding(true);
                    GoatEntity.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                GoatEntity.this.setStanding(false);
            }

        }

        public void stop() {
            GoatEntity.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity p_29587_) {
            return (double)(4.0F + p_29587_.getBbWidth());
        }
    }
}
