package com.greatorator.tolkienmobs.entity.monster;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.ai.target.SwampHagNearestAttackableTargetGoal;
import com.greatorator.tolkienmobs.entity.ai.target.SwampHagNearestHealableRaiderTargetGoal;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

public class SwampHagEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(SwampHagEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEALING = SynchedEntityData.defineId(SwampHagEntity.class, EntityDataSerializers.BOOLEAN);
    private static final ResourceLocation SPEED_MODIFIER_DRINKING_ID = ResourceLocation.withDefaultNamespace("drinking");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(
            SPEED_MODIFIER_DRINKING_ID, -0.25, AttributeModifier.Operation.ADD_VALUE
    );
    private SwampHagNearestHealableRaiderTargetGoal<TolkienMonsterEntity> healRaidersGoal;
    private SwampHagNearestAttackableTargetGoal<Player> attackPlayersGoal;
    private int healingTime;

    public SwampHagEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ARMOR, 5.0D);
    }

    @Override
    protected void registerGoals() {
        this.attackPlayersGoal = new SwampHagNearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)null);
        this.healRaidersGoal = new SwampHagNearestHealableRaiderTargetGoal<>(this, TolkienMonsterEntity.class, true, (entity) -> {
            return entity != null  && entity.getType() != TolkienEntities.ENTITY_TTM_SWAMPHAG.get();
        });
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 60, 10.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class));
        this.targetSelector.addGoal(2, this.healRaidersGoal);
        this.targetSelector.addGoal(3, this.attackPlayersGoal);
    }

    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive()) {
            this.healRaidersGoal.decrementCooldown();
            if (this.healRaidersGoal.getCooldown() <= 0) {
                this.attackPlayersGoal.setCanAttack(true);
            } else {
                this.attackPlayersGoal.setCanAttack(false);
            }

            if (this.isHealing()) {
                if (this.healingTime-- <= 0) {
                    this.setHealing(false);
                    ItemStack itemstack = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    PotionContents potioncontents = itemstack.get(DataComponents.POTION_CONTENTS);
                    if (itemstack.is(Items.POTION) && potioncontents != null) {
                        potioncontents.forEachEffect(this::addEffect);
                    }
                    this.gameEvent(GameEvent.DRINK);
                    this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING.id());
                }
            } else {
                Holder<Potion> holder = null;
                if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    holder = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15F
                        && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE))
                        && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    holder = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    holder = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5F
                        && this.getTarget() != null
                        && !this.hasEffect(MobEffects.MOVEMENT_SPEED)
                        && this.getTarget().distanceToSqr(this) > 121.0) {
                    holder = Potions.SWIFTNESS;
                }

                if (holder != null) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, PotionContents.createItemStack(Items.POTION, holder));
                    this.healingTime = this.getMainHandItem().getUseDuration(this);
                    this.setHealing(true);
                    if (!this.isSilent()) {
                        this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }
                }
            }
            if (this.random.nextFloat() < 7.5E-4F) {
                this.level.broadcastEntityEvent(this, (byte)15);
            }
        }
        super.aiStep();
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
            if (target instanceof Raider) {
                if (target.getHealth() <= 4.0F) {
                    holder = Potions.HEALING;
                } else {
                    holder = Potions.REGENERATION;
                }

                this.setTarget(null);
            } else if (d3 >= 8.0 && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                holder = Potions.SLOWNESS;
            } else if (target.getHealth() >= 8.0F && !target.hasEffect(MobEffects.POISON)) {
                holder = Potions.POISON;
            } else if (d3 <= 3.0 && !target.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                holder = Potions.WEAKNESS;
            }

            ThrownPotion thrownpotion = new ThrownPotion(this.level(), this);
            thrownpotion.setItem(PotionContents.createItemStack(Items.SPLASH_POTION, holder));
            thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
            thrownpotion.shoot(d0, d1 + d3 * 0.2, d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.level()
                        .playSound(
                                null,
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                SoundEvents.WITCH_THROW,
                                this.getSoundSource(),
                                1.0F,
                                0.8F + this.random.nextFloat() * 0.4F
                        );
            }

            this.level().addFreshEntity(thrownpotion);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 15) {
            for (int i = 0; i < this.random.nextInt(35) + 10; i++) {
                this.level()
                        .addParticle(
                                ParticleTypes.WITCH,
                                this.getX() + this.random.nextGaussian() * 0.13F,
                                this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.13F,
                                this.getZ() + this.random.nextGaussian() * 0.13F,
                                0.0,
                                0.0,
                                0.0
                        );
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float damage) {
        damage = super.getDamageAfterMagicAbsorb(source, damage);
        if (source.getEntity() == this) {
            damage = 0.0F;
        }

        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            damage *= 0.15F;
        }

        return damage;
    }

    @Override
    public ItemStack getHeldItem() {
        return super.getHeldItem();
    }

    @Override
    public void setHeldItem(ItemStack heldItem) {
    }

    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(DATA_HEALING, false);
    }

    public void setHealing(boolean healing) {
        this.getEntityData().set(DATA_HEALING, healing);
    }

    public boolean isHealing() {
        return this.getEntityData().get(DATA_HEALING);
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
        RandomSource randomsource = level.getRandom();

        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(level, randomsource, difficulty);
        this.reassessWeaponGoal();

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
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (this.getRanged() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().forceAnimationReset();
                event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
                this.ranged =false;
            }else if (this.isHealing() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().forceAnimationReset();
                event.getController().setAnimation(RawAnimation.begin().then("attack_heal", Animation.LoopType.PLAY_ONCE));
                this.setHealing(false);
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

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.WITCH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }
}