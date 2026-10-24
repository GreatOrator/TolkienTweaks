package com.greatorator.tolkienmobs.entity.boss;

import com.greatorator.tolkienmobs.entity.ai.goal.WatcherAttackGoal;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class WatcherEntity extends WaterAnimal implements GeoEntity {
    public static final EntityDataAccessor<Integer> DATA_ID_STATE =
            SynchedEntityData.defineId(WatcherEntity.class, EntityDataSerializers.INT);
    private float tx;
    private float ty;
    private float tz;
    private boolean drowning;
    private boolean sleeping;
    public float xBodyRotO;
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;
    private long nextDefensiveAbilityUse = 0L;
    private final static long defensiveCoolDown = 10000L;

    public WatcherEntity(EntityType<? extends WaterAnimal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 45, 1, 0.35F, 0.01F, true);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 17.0D)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.MOVEMENT_SPEED, 0.40D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ARMOR, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WatcherAttackGoal(this, 1, 0));
        this.goalSelector.addGoal(1, new WatcherRandomMovementGoal(this));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public void tryAttack(LivingEntity target) {
        Player attacker = this.getLastHurtByPlayer();
        if (attacker == null) return;
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        boolean bl = target.doHurtTarget(serverLevel, attacker);
        if (bl) {
            this.canAttack(target);
        }
    }

    @Override
    public void travel(@Nonnull Vec3 vec3) {
        if (!this.isImmobile() && this.isInWater()) {
            this.updateSwimming();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8D, 0.8D, 0.8D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(vec3);
        }
    }

    public void setAttackingState(int time) {
        this.entityData.set(DATA_ID_STATE, time);
    }

    /** Special Attack */
    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (!super.hurtServer(serverLevel, source, amount)) {
            return false;
        } else {
            LivingEntity livingentity = this.getTarget();
            if (livingentity == null && source.getEntity() instanceof LivingEntity) {
                livingentity = (LivingEntity) source.getEntity();
            }

            if (livingentity == null) return true;

            // The reactions below are rate limited, otherwise they fired - and messaged the attacker - on every single hit.
            long reactionTime = System.currentTimeMillis();
            if (reactionTime <= this.nextDefensiveAbilityUse) {
                return true;
            }
            this.nextDefensiveAbilityUse = reactionTime + defensiveCoolDown;

            if (this.getHealth() < this.getMaxHealth()) {
                GeneralUtility.sendMessage(livingentity, Component.translatable(MODID + ".msg.healself.watcher").withStyle(ChatFormatting.LIGHT_PURPLE));
                // Regeneration only heals on ticks divisible by (50 >> amplifier): 40 ticks at amplifier 0 never healed at all.
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 1));
            }

            if (this.getTarget() != null && !this.hasEffect(MobEffects.SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
                GeneralUtility.sendMessage(livingentity, Component.translatable(MODID + ".msg.speedup.watcher").withStyle(ChatFormatting.AQUA));
                this.addEffect(new MobEffectInstance(MobEffects.SPEED, 2 * 20, 0));
            }
            return true;
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entityIn) {
        serverLevel.broadcastEntityEvent(this, (byte) 4);
        float f = 17.0F;
        float f1 = f / 2.0F + (float) this.random.nextInt((int) f);
            entityIn.setDeltaMovement(entityIn.getDeltaMovement().add(0.0D, 0.4F, 0.0D));
            long time = System.currentTimeMillis();
            if (super.doHurtTarget(serverLevel, entityIn)) {
                if (entityIn instanceof Player) {
                    if (time > nextAbilityUse) {
                        nextAbilityUse = time + coolDown;
                        Player player = (Player) entityIn;
                        int strength = 3;
                        if (MathUtility.getRandom(10) <= 3) {
                            this.drowning = true;
                            player.addEffect(new MobEffectInstance(TolkienMobEffects.ELEMENTAL_DROWNING, strength * 20, 0));
                        } else {
                            this.sleeping = true;
                            this.spawnInk();
                            player.addEffect(new MobEffectInstance(TolkienMobEffects.SLEEPNESIA, strength * 20, 0));
                        }
                    }
                }
            }
        this.playSound(TolkienSounds.IDLE_WATCHER.get(), 1.0F, 1.0F);
        return true;
    }

    private boolean getSleeping() {
        return sleeping;
    }

    private boolean getDrowning() {
        return drowning;
    }

    private Vec3 rotateVector(Vec3 vector) {
        Vec3 vec3 = vector.xRot(this.xBodyRotO * (float) (Math.PI / 180.0));
        return vec3.yRot(-this.yBodyRotO * (float) (Math.PI / 180.0));
    }

    private void spawnInk() {
        this.makeSound(this.getSquirtSound());
        Vec3 vec3 = this.rotateVector(new Vec3(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());

        for (int i = 0; i < 30; i++) {
            Vec3 vec31 = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
            Vec3 vec32 = vec31.scale(0.3 + (double)(this.random.nextFloat() * 2.0F));
            ((ServerLevel)this.level()).sendParticles(this.getInkParticle(), vec3.x, vec3.y + 0.5, vec3.z, 0, vec32.x, vec32.y, vec32.z, 0.1F);
        }
    }

    protected ParticleOptions getInkParticle() {
        return ParticleTypes.SQUID_INK;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_STATE, 0);
    }

    /** Animations */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<WatcherEntity>("Walk", 1, (event) -> {
            if (event.isMoving()) {
                event.controller().setAnimation(RawAnimation.begin().thenLoop("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<WatcherEntity>("Idle", 1, (event) -> {
            if (!event.isMoving()) {
                event.controller().setAnimation(RawAnimation.begin().thenLoop("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<WatcherEntity>("Whirlpool", 1, (event) -> {
            if (this.isAggressive() && this.getDrowning()) {
                event.controller().setAnimation(RawAnimation.begin().thenPlay("whirlpool"));
                this.drowning = false;
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<WatcherEntity>("Bite", 1, (event) -> {
            if (this.isAggressive() && this.getSleeping()) {
                event.controller().setAnimation(RawAnimation.begin().thenPlay("bite"));
                this.sleeping = false;
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<WatcherEntity>("Swing", 1, (event) -> {
            if (this.isAggressive()) {
                event.controller().setAnimation(RawAnimation.begin().thenPlay("swing"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<WatcherEntity>("Death", 1, (event) -> {
            if (this.isDeadOrDying()) {
                event.controller().setAnimation(RawAnimation.begin().thenPlay("death"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    /** Sounds */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_WATCHER.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.DROWNED_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_WATCHER.get();
    }

    protected SoundEvent getSquirtSound() {
        return SoundEvents.SQUID_SQUIRT;
    }

    /** BOSS BAR */
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Mth.createInsecureUUID(this.random), Component.translatable("entity.tolkienmobs.entityttmwatcher"), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10);

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    /** Goals */
    public void setMovementVector(float tx, float ty, float tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    public boolean hasMovementVector() {
        return this.tx != 0.0F || this.ty != 0.0F || this.tz != 0.0F;
    }

    static class WatcherRandomMovementGoal extends Goal {
        private final WatcherEntity squid;

        public WatcherRandomMovementGoal(WatcherEntity p_30004_) {
            this.squid = p_30004_;
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            int i = this.squid.getNoActionTime();
            if (i > 100) {
                this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
            } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
                float f = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2F);
                float f1 = Mth.cos(f) * 0.2F;
                float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
                float f3 = Mth.sin(f) * 0.2F;
                this.squid.setMovementVector(f1, f2, f3);
            }

        }
    }
}