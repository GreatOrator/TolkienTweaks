package com.greatorator.tolkienmobs.entity.ambient;

import com.greatorator.tolkienmobs.entity.TolkienFlyingAmbientEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.LandBirdGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.RandomGlideGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.RestrictedFlyingGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.StartFlyingBirdGoal;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class ThrushEntity extends TolkienFlyingAmbientEntity implements GeoEntity, FlyingAnimal {
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;    protected static final EntityDataAccessor<Boolean> PECKING = SynchedEntityData.defineId(ThrushEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(ThrushEntity.class, EntityDataSerializers.LONG);

   public ThrushEntity(EntityType<? extends ThrushEntity> entityType, Level level) {
        super(entityType, level);

       setTame(false, false);

        setPathfindingMalus(PathType.DANGER_FIRE, -1.0f);
        setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.TEMPT_RANGE, 10.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, world);
        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(true);
        return navigator;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new StartFlyingBirdGoal(this, 120));
        this.goalSelector.addGoal(2, new LandBirdGoal(this, 240));
        this.goalSelector.addGoal(3, new RandomGlideGoal(this));
        this.goalSelector.addGoal(4, new RestrictedFlyingGoal(this, 1.0D, 64.0));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.25, stack -> stack.is(TolkienTags.Items.INSECTS), false));
        this.goalSelector.addGoal(5, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F));
        this.goalSelector.addGoal(6, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
    }

    public void resetLastPoseChangeTick(long pLastPoseChangeTick) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, pLastPoseChangeTick);
    }

    public boolean isSitting() {
        return this.entityData.get(LAST_POSE_CHANGE_TICK) < 0L;
    }
    public void toggleSitting() {
        if (this.isSitting()) {
            standUp();
        } else {
            sitDown();
        }
    }

    public void sitDown() {
        if (!this.isSitting()) {
            this.makeSound(SoundEvents.CAMEL_SIT);
            this.setPose(Pose.SITTING);
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.resetLastPoseChangeTick(-this.level().getGameTime());
        }
        setOrderedToSit(true);
        setInSittingPose(true);
    }
    public void standUp() {
        if (this.isSitting()) {
            this.makeSound(SoundEvents.CAMEL_STAND);
            this.setPose(Pose.STANDING);
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.resetLastPoseChangeTick(this.level().getGameTime());
        }
        setOrderedToSit(false);
        setInSittingPose(false);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        calculateFlapping();
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (!this.onGround() && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if(!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if(!this.onGround() && !this.getFlying() && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flap += this.flapping * 2.0F;
    }

    @SuppressWarnings("resource")
    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if(isTame() && pHand == InteractionHand.MAIN_HAND && !isFood(itemstack)) {
            toggleSitting();
            return InteractionResult.SUCCESS;
        }

        if(!this.isTame() && itemstack.is(TolkienTags.Items.INSECTS)) {
            if(!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if(!this.isSilent()) {
                this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F,
                        1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if(!this.level().isClientSide()) {
                if(this.random.nextInt(6) == 0 && !net.neoforged.neoforge.event.EventHooks.onAnimalTame(this, pPlayer)) {
                    this.tame(pPlayer);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                }
                else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }

            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        else if(this.isTame() && this.isOwnedBy(pPlayer)) {
            if(isFood(itemstack)) {
                return super.mobInteract(pPlayer, pHand);
            }
            else if(!this.level().isClientSide()) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }

            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public boolean isFlying() {
        return !onGround();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if(otherAnimal == this)
            return false;
        else if(!isTame())
            return false;
        else if(!(otherAnimal instanceof ThrushEntity))
            return false;
        else {
            ThrushEntity otherEagle = (ThrushEntity) otherAnimal;
            if(!otherEagle.isTame())
                return false;
            else if(otherEagle.isInSittingPose())
                return false;
            else
                return isInLove() && otherEagle.isInLove();
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        var child = TolkienEntities.ENTITY_TTM_THRUSH.get().create(level, EntitySpawnReason.BREEDING);
        if(child != null /*&& otherParent instanceof BaldEagle eagle*/) {
            if(this.isTame()) {
                child.setOwnerUUID(this.getOwnerUUID());
                child.setTame(true, true);
            }
        }
        return child;
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity pEntity) {
        return pEntity.hurtServer(serverLevel, pEntity.damageSources().mobAttack(this), 3.0F);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(TolkienTags.Items.INSECTS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PECKING, false);
        builder.define(LAST_POSE_CHANGE_TICK, 0L);
   }
    public void setPecking(boolean pecking) {
        this.entityData.set(PECKING, pecking);
    }

    public boolean isPecking() {
        return this.entityData.get(PECKING);
    }

    /**
     * Sounds
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_THRUSH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_THRUSH.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_THRUSH.get();
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.playSound(TolkienSounds.FLAP_THRUSH.get(), 0.15F, 1.0F);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void doPush(Entity pEntity) {
        if(!(pEntity instanceof Player)) {
            super.doPush(pEntity);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource pSource, float pAmount) {
        if(this.isInvulnerableTo(serverLevel, pSource)) {
            return false;
        }
        else {
            this.setOnGround(true);
            this.setOrderedToSit(false);

            return super.hurtServer(serverLevel, pSource, pAmount);
        }
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        int rand = MathUtility.getRandomInteger(100, 1);
        controllers.add(new AnimationController<ThrushEntity>("Idle", 1, (event) -> {
            if (!event.isMoving() && !isFlying() ) {
                if (rand < 50) {
                    event.controller().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
                }else if (rand > 50 && rand < 80) {
                    setPecking(true);
                    event.controller().setAnimation(RawAnimation.begin().thenPlay("idle3"));
                    setPecking(false);
                    return PlayState.CONTINUE;
                }else {
                    event.controller().setAnimation(RawAnimation.begin().thenPlay("idle2"));
                    return PlayState.CONTINUE;
                }
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<ThrushEntity>("Walk", 1, (event) -> {
            if (event.isMoving() && !isFlying()) {
                event.controller().setAnimation(RawAnimation.begin().thenPlay("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<ThrushEntity>("Sit", 1, (event) -> {
            if (isOrderedToSit()) {
                setOnGround(true);
                event.controller().setAnimation(RawAnimation.begin().thenPlay("sit"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<ThrushEntity>("Glide", 1, (event) -> {
            if (isFlying() && isGliding()) {
                event.controller().setAnimation(RawAnimation.begin().thenPlay("glide"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<ThrushEntity>("Fly", 1, (event) -> {
            if (isFlying() && !isGliding()) {
                onFlap();
                event.controller().setAnimation(RawAnimation.begin().thenPlay("fly"));
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