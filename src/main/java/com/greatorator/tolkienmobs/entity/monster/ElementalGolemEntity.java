package com.greatorator.tolkienmobs.entity.monster;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.projectiles.TornadoEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;

public class ElementalGolemEntity extends TolkienMonsterEntity implements GeoEntity, NeutralMob {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(ElementalGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ELEMENT_TYPE = SynchedEntityData.defineId(ElementalGolemEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int attackAnimationTick;
    private int remainingPersistentAngerTime;
    @javax.annotation.Nullable
    private UUID persistentAngerTarget;
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;
    public int liftAttackAnimationTick;
    public int liftAttackAnimationLength = 25;
    public int liftAttackAnimationActionPoint = 15;
    public int soundLoopTick;

    public ElementalGolemEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6D, false));
        this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TolkienMonsterEntity.class, 5, false, false, (entity) -> entity instanceof Enemy && !(entity instanceof Creeper)));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public ItemStack getHeldItem() {
        return super.getHeldItem();
    }

    @Override
    public void setHeldItem(ItemStack heldItem) {
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }

        if (this.getDeltaMovement().horizontalDistanceSqr() > (double)2.5000003E-7F && this.random.nextInt(5) == 0) {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY() - (double)0.2F);
            int k = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.level.getBlockState(pos);
            if (!blockstate.isAir()) {
                this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getX() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), 4.0D * ((double)this.random.nextFloat() - 0.5D), 0.5D, ((double)this.random.nextFloat() - 0.5D) * 4.0D);
            }
        }

        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level, true);
        }
    }

    public void tickDownAnimTimers() {
        if (this.liftAttackAnimationTick > 0) {
            this.liftAttackAnimationTick--;
        }
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angryTime) {
        this.remainingPersistentAngerTime = angryTime;
    }

    @javax.annotation.Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@javax.annotation.Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public void setGolemType(int golemType) {
        this.entityData.set(ELEMENT_TYPE, golemType);
    }

    public int getGolemType() {
        return this.entityData.get(ELEMENT_TYPE);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.attackAnimationTick = 10;
        this.level.broadcastEntityEvent(this, (byte)4);
        float f = this.getAttackDamage();
        float f1 = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
        DamageSource source = damageSources().mobAttack(this);
        boolean flag = entity.hurt(source, f1);
        if (flag) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.4F, 0.0D));
            if(entity.level() instanceof ServerLevel level) EnchantmentHelper.doPostAttackEffects(level, entity, source);
            if (this.getGolemType() == 1 ||this.getGolemType() == 6 || this.getGolemType() == 11 || this.getGolemType() == 16) { /* Stone */
                long time = System.currentTimeMillis();
                if (super.doHurtTarget(entity)) {
                    if (entity instanceof Player) {
                        if (time > nextAbilityUse) {
                            nextAbilityUse = time + coolDown;
                            Player player = (Player) entity;
                            int strength = 2;
                            player.addEffect(new MobEffectInstance(TolkienMobEffects.INVENTORY_CORROSION, strength * 20, 0));
                        }
                    }
                }
            } else if (this.getGolemType() == 2 ||this.getGolemType() == 7 || this.getGolemType() == 12) { /* Earth */
                long time = System.currentTimeMillis();
                if (super.doHurtTarget(entity)) {
                    if (entity instanceof Player) {
                        if (time > nextAbilityUse) {
                            nextAbilityUse = time + coolDown;
                            Player player = (Player) entity;
                            int strength = 2;
                            if (MathUtility.getRandom(10) <= 3) {
                                player.addEffect(new MobEffectInstance(TolkienMobEffects.ELEMENTAL_FLYING, strength * 20, 0));
                            } else {
                                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, strength * 20, 0));
                            }
                        }
                    }
                }
            } else if (this.getGolemType() == 4 || this.getGolemType() == 9 || this.getGolemType() == 14) { /* Air */
                long time = System.currentTimeMillis();
                if (super.doHurtTarget(entity)) {
                    if (entity instanceof Player) {
                        if (time > nextAbilityUse) {
                            nextAbilityUse = time + coolDown;
                            Player player = (Player) entity;
                            int strength = 2;
                            if (MathUtility.getRandom(10) <= 3 && this.liftAttackAnimationTick > 0) {
                                this.level.addParticle(TolkienParticleTypes.WIND_PARTICLE.get(), this.getRandomX(0.1D), this.getY() + 0.05D, this.getRandomZ(0.1D), (this.random.nextDouble() - 0.5D), 0.0, (this.random.nextDouble() - 0.5D));
                                this.goalSelector.addGoal(1, new LiftAttackGoal(this));
                            } else {
                                player.addEffect(new MobEffectInstance(TolkienMobEffects.ELEMENTAL_LIGHTNING, strength * 20, 0));
                            }
                        }
                    }
                }
            } else if (this.getGolemType() == 3 || this.getGolemType() == 8 || this.getGolemType() == 13) { /* Fire */
                long time = System.currentTimeMillis();
                if (super.doHurtTarget(entity)) {
                    if (entity instanceof Player) {
                        if (time > nextAbilityUse) {
                            nextAbilityUse = time + coolDown;
                            Player player = (Player) entity;
                            int strength = 2;
                            if (MathUtility.getRandom(10) <= 3) {
                                player.addEffect(new MobEffectInstance(TolkienMobEffects.ELEMENTAL_BURNING, strength * 20, 0));
                            } else {
                                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, strength * 20, 0));
                            }
                        }
                    }
                }
            } else if (this.getGolemType() == 5 || this.getGolemType() == 10 || this.getGolemType() == 15) { /* Water */
                long time = System.currentTimeMillis();
                if (super.doHurtTarget(entity)) {
                    if (entity instanceof Player) {
                        if (time > nextAbilityUse) {
                            nextAbilityUse = time + coolDown;
                            Player player = (Player) entity;
                            int strength = 2;
                            if (MathUtility.getRandom(10) <= 3) {
                                player.addEffect(new MobEffectInstance(TolkienMobEffects.ELEMENTAL_DROWNING, strength * 20, 0));
                            } else {
                                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, strength * 20, 0));
                            }
                        }
                    }
                }
            }
        }
        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    @Override
    public void handleEntityEvent(byte stage) {
        if (stage == 4) {
            this.attackAnimationTick = 10;
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else if (stage == 11) {
            this.liftAttackAnimationTick = liftAttackAnimationLength;
        }else {
            super.handleEntityEvent(stage);
        }
    }

    boolean steepDropBelow() {
        boolean blockBeneath = false;

        for (int i = 0; i < 4; i++) {
            if (!this.level.getBlockState(new BlockPos(this.blockPosition().getX(), this.blockPosition().getY() - i, this.blockPosition().getZ())).isAir()) {
                blockBeneath = true;
            }
        }
        return !this.level.isClientSide && !blockBeneath;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, @Nonnull DamageSource damageSource) {
        return false;
    }

    public void baseTick() {
        super.baseTick();
        this.tickDownAnimTimers();

        if (this.getTarget() != null && this.distanceTo(this.getTarget()) > 4 && ((!this.onGround() && steepDropBelow()) || ((this.getTarget().getY() > this.getY() + 4 || this.getY() < this.getTarget().getY() - 2) && this.distanceTo(this.getTarget()) > 4) || this.distanceTo(this.getTarget()) > 10)) {
            if (this.getY() < this.getTarget().getY() + 4) {
                this.setDeltaMovement(0.0D, 0.05D, 0.0D);
            } else {
                this.setDeltaMovement(0.0D, -0.01D, 0.0D);
            }

            double x = this.getTarget().getX() - this.getX();
            double y = this.getTarget().getY() - this.getY();
            double z = this.getTarget().getZ() - this.getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            this.setDeltaMovement(this.getDeltaMovement()
                    .add(x / d * 0.20000000298023224D, y / d * 0.20000000298023224D, z / d * 0.20000000298023224D)
                    .scale(1.5D));
            this.move(MoverType.SELF, this.getDeltaMovement());

            this.getNavigation().stop();
            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.getTarget().getX(), this.getTarget().getEyeY(), this.getTarget().getZ()));
        }

//        this.soundLoopTick ++;
//
//        if (this.soundLoopTick % 40 == 0) {
//            this.playSound(TolkienSounds.SHOOT_TORNADO.get(), 0.75F, 1.0F);
//        }
    }

    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(ELEMENT_TYPE, 0);
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
        this.setGolemType(variant.getId());
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
            if (!event.isMoving() && !event.getAnimatable().isAggressive()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
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

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.15F, 1.0F);
    }

    /**
     * Goals
     */
    static class LiftAttackGoal extends Goal {
        public ElementalGolemEntity mob;
        @javax.annotation.Nullable
        public LivingEntity target;

        private final Predicate<Entity> TORNADO = (entity) -> entity instanceof TornadoEntity;

        public LiftAttackGoal(ElementalGolemEntity mob) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
            this.mob = mob;
            this.target = mob.getTarget();
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            target = mob.getTarget();

            int nearbyTornadoes = 1;

            if (target != null) {
                nearbyTornadoes = mob.level.getEntities(mob, target.getBoundingBox().inflate(5.0D), TORNADO)
                        .size();
            }

            return target != null && target.onGround() && mob.random.nextInt(30) == 0 && mob.distanceTo(target) <= 16 && mob.distanceTo(target) > 3 && nearbyTornadoes == 0 && mob.hasLineOfSight(target) && animationsUseable();
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && !animationsUseable();
        }

        @Override
        public void start() {
            mob.liftAttackAnimationTick = mob.liftAttackAnimationLength;
            mob.level.broadcastEntityEvent(mob, (byte) 4);
        }

        @Override
        public void tick() {
            target = mob.getTarget();

            if (target != null) {
                mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            }

            if (target != null && mob.liftAttackAnimationTick == mob.liftAttackAnimationActionPoint) {
                TornadoEntity tornado = TolkienEntities.AMMO_TORNADO.get().create(mob.level);
                tornado.moveTo(target.blockPosition(), 0, 0);
                tornado.playSound(TolkienSounds.SHOOT_TORNADO.get(), 1.5F, 1.0F);
                ((ServerLevel)mob.level).addFreshEntityWithPassengers(tornado);
            }
        }

        public boolean animationsUseable() {
            return mob.liftAttackAnimationTick <= 0;
        }
    }
}