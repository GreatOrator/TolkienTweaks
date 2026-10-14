package com.greatorator.tolkienmobs.entity.boss;

import com.greatorator.tolkienmobs.entity.DragonEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.dragon.*;
import com.greatorator.tolkienmobs.entity.projectiles.FellBeastFireballEntity;
import com.greatorator.tolkienmobs.handler.EntityHandler;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.LerpedFloatUtility;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.ArrayUtils;

import java.util.EnumSet;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class FellBeastEntity extends DragonEntity {
    static {
        IDLE_ANIMATION_VARIANTS = 1;
        ATTACK_ANIMATION_VARIANTS = 2;
        SITTING_ANIMATION_TIME = 60;
        SLEEPING_ANIMATION_TIME = 60;
    }
    public static final EntityDataAccessor<Boolean> BREATHING_FIRE = SynchedEntityData.defineId(FellBeastEntity.class, EntityDataSerializers.BOOLEAN);

    public static final String ROAR_ANIMATION = "roar";
    public static final int ROAR_ANIMATION_TYPE = 2;
    public static final int ROAR_ANIMATION_TIME = 80;
    public static final boolean ROAR_ANIMATION_MOVES = true;

    public static final String FIRE_ANIMATION = "fire";
    public static final int FIRE_ANIMATION_TYPE = 1;
    public static final int FIRE_ANIMATION_TIME = 80;
    public static final boolean FIRE_ANIMATION_MOVES = false;

    public static final String ATTACK_ANIMATION = "attack";
    public static final int ATTACK_ANIMATION_TYPE = 2;
    public static final int ATTACK_ANIMATION_TIME_1 = 20;
    public static final int ATTACK_ANIMATION_TIME_2 = 13;
    public static final int ATTACK_ANIMATION_TIME_3 = 35;
    public static final int ATTACK_QUEUE_TIME_1 = 9;
    public static final int ATTACK_QUEUE_TIME_2 = 9;
    public static final int ATTACK_QUEUE_TIME_3 = 25;

    public final LerpedFloatUtility flightTimer = LerpedFloatUtility.unit();
    public final LerpedFloatUtility breathTimer = LerpedFloatUtility.unit();
    private int fireballCooldown;

    public FellBeastEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setPathfindingMalus(PathType.DANGER_FIRE, 0);
        setPathfindingMalus(PathType.DAMAGE_FIRE, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 240)
                .add(MOVEMENT_SPEED, 0.22)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(FOLLOW_RANGE, 60)
                .add(ATTACK_KNOCKBACK, 4)
                .add(ATTACK_DAMAGE, 12)
                .add(FLYING_SPEED, 0.121);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        goalSelector.addGoal(5, new DragonAttackGoal(this));
        goalSelector.addGoal(9, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 10f));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        goalSelector.addGoal(3, new RunWhenLosingGoal(this, 0.2f, 0.001f, 20f, 1.15f, 1f));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false,
                e -> e.getType() == EntityType.PLAYER || e instanceof Animal || e instanceof AbstractVillager));
    }

    @Override
    protected void updateWeaponGoal() {
        // This entity owns its animated melee/breath goal; the humanoid melee goal would override it.
        scheduleWeaponGoalUpdate = false;
    }

    @Override
    public EntityHandler<DragonEntity> getSerializer() {
        return SERIALIZER;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BREATHING_FIRE, false);
    }

    public void setBreathingFire(boolean breathingFire) {
        if (!level.isClientSide) entityData.set(BREATHING_FIRE, breathingFire);
    }

    public boolean getBreathingFire() {
        return entityData.get(BREATHING_FIRE);
    }

    @Override
    public boolean shouldSleep() {
        return super.shouldSleep();
    }

    @Override
    public Vec3 getApproximateMouthPos() {
        Vec3 rotVector = calculateViewVector(getXRot() * 0.65f, yHeadRot);
        Vec3 position = getEyePosition(1).subtract(0, 0.9, 0);
        position = position.add(rotVector.scale(getBbWidth() + 1.3));
        return position;
    }

    @Override
    public Attribute[] getScaledAttributes() {
        return (Attribute[]) ArrayUtils.addAll(super.getScaledAttributes(), ATTACK_KNOCKBACK.value());
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        flightTimer.add(isUsingFlyingNavigator() ? 0.1f : -0.085f);
        if (!level.isClientSide && getBreathingFire()
                && (getTarget() == null || !getTarget().isAlive() || !isWithinRestriction(getTarget().blockPosition()))) {
            setBreathingFire(false);
        }
        breathTimer.add(getBreathingFire() ? 0.15f : -0.2f);
        if (!level.isClientSide) {
            if (fireballCooldown > 0) fireballCooldown--;
            if (getBreathingFire() && breathTimer.get() >= 1 && fireballCooldown == 0) {
                LivingEntity target = getTarget();
                if (target != null && hasLineOfSight(target)) {
                    Vec3 mouth = getApproximateMouthPos();
                    Vec3 aim = target.getEyePosition().subtract(mouth);
                    FellBeastFireballEntity fireball = new FellBeastFireballEntity(this, level);
                    fireball.setPos(mouth);
                    fireball.shoot(aim.x, aim.y, aim.z, 1.5F, 1.0F);
                    fireball.pickup = AbstractArrow.Pickup.DISALLOWED;
                    level.addFreshEntity(fireball);
                    // A charged breath must not create an unlaunched projectile every tick.
                    fireballCooldown = 10;
                }
            }
        }
    }

    private boolean shouldBreatheFire() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double distFromTarget = distanceToSqr(target);
            double degrees = Math.atan2(target.getZ() - getZ(), target.getX() - getX()) * (180 / Math.PI) - 90;
            double headAngle = Math.abs(Mth.wrapDegrees(degrees - yHeadRot));
            return (distFromTarget > 100 || target.getY() - getY() > 3 || isUsingFlyingNavigator())
                    && headAngle < 30 && hasLineOfSight(target) && canBreatheFire();
        }
        return false;
    }

    public boolean canBreatheFire() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.WITHER_IMMUNE_TO) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isImmuneToArrows() {
        return false;
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    @Override
    public boolean speciesCanFly() {
        return true;
    }


    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return super.causeFallDamage(distance, damageMultiplier, source);
    }

    @Override
    public boolean dragonCanFly() {
        return super.dragonCanFly();
    }

    @Override
    public int getYawRotationSpeed() {
        return isUsingFlyingNavigator() ? 5 : 7;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return true;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return super.canAddPassenger(passenger);
    }

    /**
     * The seats are the model's own rider bones (assets/tolkienmobs/geo/boss/fellbeast.geo.json): rider1
     * at model xyz (0, 52.5, -31) and rider2 at (0, 52.5, -10). The model is drawn at 16 units to the block
     * and FellBeastRender scales it up 1.5x, so both bones are 52.5 / 16 * 1.5 = 4.921875 blocks above the
     * beast's feet. The model's front is -Z while an entity's front is +Z at yaw 0, so rider1 sits 31 / 16
     * * 1.5 = 2.90625 blocks ahead of the beast and rider2 0.9375 blocks ahead.
     */
    @Override
    public Vec3 getRiderSeatOffset(int passengerIndex) {
        return passengerIndex == 0
                ? new Vec3(0.0D, 4.921875D, 2.90625D)
                : new Vec3(0.0D, 4.921875D, 0.9375D);
    }

    @Override
    public int getMaxPassengers() {
        return 1;
    }

    public boolean speciesCanSwim() {
        return false;
    }

    /** Sounds */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_FELLBEAST.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDER_DRAGON_DEATH;
    }

    @Override
    public float getSoundVolume() {
        return 1.5f * getScale();
    }

    /** Goals */
    class DragonAttackGoal extends AnimatedGoal {
        private final FellBeastEntity entity;

        boolean animationPlaying;
        int ticksUntilNextAttack;
        private boolean attackIsQueued;
        private int queuedAttackTimer;
        private int attackQueueTimer = 0;
        double inflateValue;
        int disableShieldTime;

        public DragonAttackGoal(FellBeastEntity entity) {
            super(entity);
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            this.entity = entity;
        }

        @Override
        public void start() {
            entity.setAggressive(true);
        }

        @Override
        public boolean requiresUpdateEveryTick() { return true; }

        @Override
        public boolean canUse() {
            LivingEntity target = getTarget();
            if (target != null && target.isAlive()) {
                if (!isWithinRestriction(target.blockPosition())) return false;
                return TargetingConditions.forCombat().test(entity, target);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = getTarget();
            if (target != null && target.isAlive()) {
                if (!isWithinRestriction(target.blockPosition())) {
                    return false;
                }
                return TargetingConditions.forCombat().test(entity, target);
            }
            return false;
        }

        @Override
        public void tick() {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive() || !isWithinRestriction(target.blockPosition())) {
                stop();
                return;
            }
            double distFromTarget = distanceToSqr(target);
            boolean isBreathingFire = getBreathingFire();
            boolean canSeeTarget = getSensing().hasLineOfSight(target);
            if (animationPlaying) {
                if (super.canContinueToUse()) super.tick();
                else {
                    super.stop();
                    animationPlaying = false;
                }
            }
            if (attackIsQueued) {
                if (this.attackQueueTimer == queuedAttackTimer) {
                    attackQueueTimer = 0;
                    attackIsQueued = false;
                    attackInBox(getOffsetBox(getBbWidth()).inflate(inflateValue), disableShieldTime);
                } else {
                    attackQueueTimer++;
                }
            } else {

                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);

                getLookControl().setLookAt(target, 90, 90);

                if (getRandom().nextDouble() < 0.001 || distFromTarget > 900) {
                    setNavigator(NavigationType.FLYING);
                }

                if (entity.shouldBreatheFire() != isBreathingFire) {
                    if (!animationPlaying) {
                        setBreathingFire(entity.shouldBreatheFire());
                        animationPlaying = true;
                        super.start(FIRE_ANIMATION, FIRE_ANIMATION_TYPE, FIRE_ANIMATION_TIME, FIRE_ANIMATION_MOVES);
                    }
                }
                else if (distFromTarget <= 24 && !isBreathingFire && canSeeTarget && !isUsingFlyingNavigator()) {
                    this.checkAndPerformAttack();
                }
            }

            if (getNavigation().isDone()) {
                boolean isFlyingTarget = target instanceof DragonEntity && ((DragonEntity) target).isUsingFlyingNavigator();
                double y = target.getY() + (isUsingFlyingNavigator() && !isFlyingTarget ? 8 : 0);
                getNavigation().moveTo(target.getX(), y, target.getZ(), !isUsingFlyingNavigator() && isBreathingFire ? 0.8d : 1.3d);
            }

        }

        @Override
        public void stop() {
            LivingEntity livingentity = this.entity.getTarget();
            if (livingentity == null || !livingentity.isAlive() || !isWithinRestriction(livingentity.blockPosition())
                    || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                this.entity.setTarget((LivingEntity) null);
            }
            this.entity.setAggressive(false);
            this.entity.getNavigation().stop();
            this.entity.setBreathingFire(false);
            attackIsQueued = false;
            attackQueueTimer = 0;
            animationPlaying = false;
            super.stop();
        }

        protected void checkAndPerformAttack() {
            LivingEntity target = getTarget();
            if (this.ticksUntilNextAttack <= 0) {
                int attackVariant = 1+entity.random.nextInt(ATTACK_ANIMATION_VARIANTS) /*+ 1*/;
                String attackAnimation = ATTACK_ANIMATION + attackVariant;
                int attackAnimationTime = 0;

                switch (attackVariant) {
                    case 1:
                        attackAnimationTime = ATTACK_ANIMATION_TIME_1;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_1;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_1;
                        break;
                    case 2:
                        attackAnimationTime = ATTACK_ANIMATION_TIME_2;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_2;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_2;

                        break;

                    case 3:
                        attackAnimationTime = ATTACK_ANIMATION_TIME_3;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_3;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_3;
                        break;
                }

                if (!animationPlaying) {
                    animationPlaying = true;
                    super.start(attackAnimation, ATTACK_ANIMATION_TYPE, attackAnimationTime, ATTACK_ANIMATION_MOVES);
                    this.attackIsQueued = true;
                }
            }
        }
    }
}
