package com.greatorator.tolkienmobs.entity.ambient;

import com.greatorator.tolkienmobs.entity.TolkienAmbientEntity;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ThrushEntity extends TolkienAmbientEntity implements GeoEntity, FlyingAnimal {
    protected static final EntityDataAccessor<Boolean> PECKING = SynchedEntityData.defineId(ThrushEntity.class, EntityDataSerializers.BOOLEAN);

   public ThrushEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);

        moveControl = new FlyingMoveControl(this, 20, true);

        setPathfindingMalus(PathType.DANGER_FIRE, -1.0f);
        setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0f);
        setPathfindingMalus(PathType.WATER, -1.0f);
        setPathfindingMalus(PathType.FENCE, -1.0f);
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
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.25, stack -> stack.is(TolkienTags.Items.INSECTS), false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new WanderGoal(this, 1.0));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));

        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public boolean isTame() {
        return false;
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
    public boolean isFood(ItemStack stack) {
        return stack.is(TolkienTags.Items.INSECTS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PECKING, false);
    }
    public void setPecking(boolean pecking) {
        this.entityData.set(PECKING, pecking);
    }

    public boolean isPecking() {
        return this.entityData.get(PECKING);
    }

    @Override
    public ThrushEntity getBreedOffspring(ServerLevel world, AgeableMob mate) {
        return TolkienEntities.ENTITY_TTM_THRUSH.get().create(world);
    }

    public static class WanderGoal extends WaterAvoidingRandomFlyingGoal {
        private static final int TREE_HORIZONTAL_RANGE = 3;
        private static final int TREE_VERTICAL_RANGE = 6;

        public WanderGoal(PathfinderMob mob, double speed) {
            super(mob, speed);
        }

        @Override
        @Nullable
        protected Vec3 getPosition() {
            Vec3 target = null;
            if (mob.isInWater()) {
                target = LandRandomPos.getPos(mob, 15, 15);
            }

            if (mob.getRandom().nextFloat() >= probability) {
                target = findTreePos();
            }

            return target == null ? super.getPosition() : target;
        }

        @Nullable
        private Vec3 findTreePos() {
            Level level = mob.level();
            BlockPos currentPos = mob.blockPosition();
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (BlockPos pos : BlockPos.betweenClosed(
                    Mth.floor(mob.getX() - TREE_HORIZONTAL_RANGE), Mth.floor(mob.getY() - TREE_VERTICAL_RANGE), Mth.floor(mob.getZ() - TREE_HORIZONTAL_RANGE),
                    Mth.floor(mob.getX() + TREE_HORIZONTAL_RANGE), Mth.floor(mob.getY() + TREE_VERTICAL_RANGE), Mth.floor(mob.getZ() + TREE_HORIZONTAL_RANGE))
            ) {
                if (currentPos.equals(pos)) {
                    continue;
                }
                BlockState belowState = level.getBlockState(mutablePos.setWithOffset(pos, Direction.DOWN));
                if (belowState.is(BlockTags.LEAVES) || belowState.is(BlockTags.LOGS)) {
                    BlockPos abovePos = mutablePos.setWithOffset(pos, Direction.UP);
                    if (level.isEmptyBlock(pos) && level.isEmptyBlock(abovePos)) {
                        return Vec3.atBottomCenterOf(pos);
                    }
                }
            }

            return null;
        }
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

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        int rand = MathUtility.getRandomInteger(100, 1);
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && !isFlying()) {
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
        controllers.add(new AnimationController<>(this, "Glide", 1, (event) -> {
            if (!event.isMoving() & isFlying()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("glide"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Fly", 1, (event) -> {
            if (event.isMoving() & isFlying()) {
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

    /** Goals */
}