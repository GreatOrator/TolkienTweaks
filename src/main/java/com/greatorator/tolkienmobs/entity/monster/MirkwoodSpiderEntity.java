package com.greatorator.tolkienmobs.entity.monster;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.RangedWebAttackGoal;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.handler.interfaces.TrapsTarget;
import com.greatorator.tolkienmobs.handler.interfaces.WebShooter;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class MirkwoodSpiderEntity extends TolkienMonsterEntity implements GeoEntity, WebShooter {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(MirkwoodSpiderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WEB_SHOOTING = SynchedEntityData.defineId(MirkwoodSpiderEntity.class, EntityDataSerializers.BOOLEAN);
    private RangedWebAttackGoal<?> rangedWebAttackGoal;
    private LeapAtTargetGoal leapAtTargetGoal;
    private MirkwoodSpiderEntity.MirkwoodSpiderEntityAttackGoal meleeAttackGoal;
    public int targetTrappedCounter = 0;

    public MirkwoodSpiderEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 5.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.rangedWebAttackGoal = new RangedWebAttackGoal<>(this, 1.0D, 60, 20.0F);
        this.meleeAttackGoal = new MirkwoodSpiderEntity.MirkwoodSpiderEntityAttackGoal(this);
        this.targetSelector.addGoal(2, new MirkwoodSpiderEntity.MirkwoodSpiderEntityTargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new MirkwoodSpiderEntity.MirkwoodSpiderEntityTargetGoal<>(this, LivingEntity.class));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TolkienItems.SWORD_MORGULIRON.get()));
    }

    /**
     * Special Attack
     */
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        long time = System.currentTimeMillis();
        if (super.doHurtTarget(entityIn)) {
            if (entityIn instanceof Player) {
                if (time > nextAbilityUse) {
                    nextAbilityUse = time + coolDown;
                    Player player = (Player) entityIn;
                    BlockPos blockpos = player.blockPosition();
                    ItemEntity dropItem = player.drop(player.getInventory().removeItem(player.getInventory().selected, 1), false);
                    if (dropItem != null) {
                        dropItem.setPickUpDelay(50);
                        level.playSound(null, blockpos, SoundEvents.SLIME_ATTACK, SoundSource.HOSTILE, 1.0F + level.random.nextFloat(), level.random.nextFloat() * 0.7F + 0.3F);
                    }
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
        builder.define(WEB_SHOOTING, false);
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

        RandomSource randomsource = level.getRandom();
        if (randomsource.nextInt(100) == 0) {
            Skeleton skeleton = EntityType.SKELETON.create(this.level());
            if (skeleton != null) {
                skeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                skeleton.finalizeSpawn(level, difficulty, spawnType, null);
                skeleton.startRiding(this);
            }
        }

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
            if (!event.isMoving() && !event.getAnimatable().isAggressive()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (this.swinging && !this.isWebShooting() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            } else if (this.isWebShooting() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)){
                event.getController().setAnimation(RawAnimation.begin().then("attack_web", Animation.LoopType.PLAY_ONCE));
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
    static class MirkwoodSpiderEntityAttackGoal extends MeleeAttackGoal {

        public MirkwoodSpiderEntityAttackGoal(MirkwoodSpiderEntity p_33822_) {
            super(p_33822_, 1.0D, true);
        }

        public boolean canUse() {
            return super.canUse() && !this.mob.isVehicle();
        }

        public boolean canContinueToUse() {
            float f = this.mob.getLightLevelDependentMagicValue();
            if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setTarget((LivingEntity)null);
                return false;
            } else {
                return super.canContinueToUse();
            }
        }

        protected double getAttackReachSqr(LivingEntity p_33825_) {
            return (double)(4.0F + p_33825_.getBbWidth());
        }
    }

    static class MirkwoodSpiderEntityTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public MirkwoodSpiderEntityTargetGoal(MirkwoodSpiderEntity p_33832_, Class<T> p_33833_) {
            super(p_33832_, p_33833_, true);
        }

        public boolean canUse() {
            float f = this.mob.getLightLevelDependentMagicValue();
            return f >= 0.5F ? false : super.canUse();
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.reassessAttackGoals();
    }

    private void reassessAttackGoals() {
        LivingEntity target = this.getTarget();
        if(this.meleeAttackGoal != null
                && this.rangedWebAttackGoal != null
                && target != null){
            if(!this.isTargetTrapped()){
                this.goalSelector.removeGoal(this.meleeAttackGoal);
                if(this.leapAtTargetGoal != null){
                    this.goalSelector.removeGoal(this.leapAtTargetGoal);
                }
                this.goalSelector.addGoal(4, (Goal) this.rangedWebAttackGoal);
            } else{
                this.goalSelector.removeGoal((Goal) this.rangedWebAttackGoal);
                if(this.leapAtTargetGoal != null){
                    this.goalSelector.addGoal(3, this.leapAtTargetGoal);
                }
                this.goalSelector.addGoal(4, this.meleeAttackGoal);
            }
        }
    }

    @Override
    public boolean isTargetTrapped() {
        return this.targetTrappedCounter > 0;
    }

    @Override
    public void setWebShooting(boolean webShooting) {
        this.playSound(TolkienSounds.SHOOT_SPIDER.get(), this.getSoundVolume(), this.getVoicePitch());
        this.entityData.set(WEB_SHOOTING, webShooting);
    }

    @Override
    public boolean isWebShooting() {
        return this.entityData.get(WEB_SHOOTING);
    }

    @Override
    public void setTargetTrapped(boolean trapped, boolean notifyOthers) {
        TargetingConditions spiderTargeting = TargetingConditions.forCombat().range(10.0D).ignoreInvisibilityTesting();

        if (notifyOthers) {
            List<Spider> spiders = this.level.getNearbyEntities(Spider.class, spiderTargeting, this, this.getBoundingBox().inflate(10.0D));

            for(Spider spider : spiders) {
                if (spider instanceof TrapsTarget && this.getTarget() != null && spider.getTarget() != null && spider.getTarget() == this.getTarget()) {
                    ((TrapsTarget)spider).setTargetTrapped(trapped, false);
                }
            }
        }
        if (trapped) {
            this.targetTrappedCounter = 20;
        } else {
            this.targetTrappedCounter = 0;
        }
    }

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }
}