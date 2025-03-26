package com.greatorator.tolkienmobs.entity.ambient;

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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ThrushEntity extends TamableAnimal implements GeoEntity, FlyingAnimal {
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
       moveControl = new FlyingMoveControl(this, 20, true);

        setPathfindingMalus(PathType.DANGER_FIRE, -1.0f);
        setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, world);
        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(true);
        navigator.setCanPassDoors(true);
        return navigator;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.25, stack -> stack.is(TolkienTags.Items.INSECTS), false));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
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
        if(!this.onGround() && vec3.y < 0.0D) {
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

            if(!this.level().isClientSide) {
                if(this.random.nextInt(6) == 0 && !net.neoforged.neoforge.event.EventHooks.onAnimalTame(this, pPlayer)) {
                    this.tame(pPlayer);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                }
                else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        else if(this.isTame() && this.isOwnedBy(pPlayer)) {
            if(isFood(itemstack)) {
                return super.mobInteract(pPlayer, pHand);
            }
            else if(!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
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
        var child = TolkienEntities.ENTITY_TTM_THRUSH.get().create(level);
        if(child != null /*&& otherParent instanceof BaldEagle eagle*/) {
            if(this.isTame()) {
                child.setOwnerUUID(this.getOwnerUUID());
                child.setTame(true, true);
            }
        }
        return child;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        return pEntity.hurt(pEntity.damageSources().mobAttack(this), 3.0F);
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
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.isInvulnerableTo(pSource)) {
            return false;
        }
        else {
            if(!this.level().isClientSide) {
                this.setOnGround(true);
                this.setOrderedToSit(false);
            }

            return super.hurt(pSource, pAmount);
        }
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        int rand = MathUtility.getRandomInteger(100, 1);
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && !isFlying() ) {
                if (rand < 50) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
                }else if (rand > 50 && rand < 80) {
                    setPecking(true);
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle3"));
                    setPecking(false);
                    return PlayState.CONTINUE;
                }else {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("idle2"));
                    return PlayState.CONTINUE;
                }
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            if (event.isMoving() && !isFlying()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Sit", 1, (event) -> {
            if (isOrderedToSit()) {
                setOnGround(true);
                event.getController().setAnimation(RawAnimation.begin().thenPlay("sit"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Glide", 1, (event) -> {
            if (event.isMoving() & isFlying()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("glide"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Fly", 1, (event) -> {
            if (!event.isMoving() & isFlying()) {
                onFlap();
                event.getController().setAnimation(RawAnimation.begin().thenPlay("fly"));
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