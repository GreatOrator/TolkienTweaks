package com.greatorator.tolkienmobs.entity.passive;

import com.greatorator.tolkienmobs.entity.monster.NazgulEntity;
import com.greatorator.tolkienmobs.handler.interfaces.CommonTraits;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NazgulSteedEntity extends AbstractChestedHorse implements GeoEntity, CommonTraits {
    private static final EntityDataAccessor<Boolean> DATA_ID_CHEST = SynchedEntityData.defineId(NazgulSteedEntity.class, EntityDataSerializers.BOOLEAN);

    public NazgulSteedEntity(EntityType<? extends AbstractChestedHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(Items.GOLDEN_APPLE), false));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)1.0F)
                .add(Attributes.JUMP_STRENGTH, 2.5D)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 8.0D);
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

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof LivingEntity entity) {
            callback.accept(entity, getX() + 0.3f, getY() + 0.65f, getZ() + 0.25f);
            this.xRotO = entity.xRotO;
        }
    }

    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_CHEST, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("ChestedGoat", this.hasChest());
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
        if (MathUtility.getRandomInteger(15, 1) == 2) {
            NazgulEntity entityNazgul = TolkienEntities.ENTITY_TTM_NAZGUL.get().create(this.level);
            entityNazgul.moveTo(this.getX(), this.getY(), this.getZ(), this.yRotO, 0.0F);
            entityNazgul.finalizeSpawn(level, difficulty, spawnType, null);
            this.setTamed(true);
            this.isSaddled();
            entityNazgul.startRiding(this);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /** Sounds */
    protected void playGallopSound(SoundType soundType) {
        this.playSound(SoundEvents.HORSE_GALLOP, soundType.getVolume() * 0.15F, soundType.getPitch());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.HORSE_ANGRY;
    }

    @javax.annotation.Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && this.isEating()) {
                event.getController().setAnimation(RawAnimation.begin().then("eat", Animation.LoopType.PLAY_ONCE).thenLoop("chewing"));
                return PlayState.CONTINUE;
            }else if (!event.isMoving()){
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            double speed = getMovementSpeed(this);
            if (event.isMoving() && speed > (double)1.0F) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
                return PlayState.CONTINUE;
            } else if (event.isMoving()){
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
}
