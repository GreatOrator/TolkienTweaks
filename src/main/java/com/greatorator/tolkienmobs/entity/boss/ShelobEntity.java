package com.greatorator.tolkienmobs.entity.boss;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.RangedWebAttackGoal;
import com.greatorator.tolkienmobs.entity.monster.MirkwoodSpiderEntity;
import com.greatorator.tolkienmobs.handler.interfaces.TrapsTarget;
import com.greatorator.tolkienmobs.handler.interfaces.WebShooter;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ShelobEntity extends TolkienMonsterEntity implements GeoEntity, WebShooter {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID =
            SynchedEntityData.defineId(ShelobEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> WEB_SHOOTING =
            SynchedEntityData.defineId(ShelobEntity.class, EntityDataSerializers.BOOLEAN);
    private RangedWebAttackGoal<?> rangedWebAttackGoal;
    private ShelobEntity.ShelobEntityAttackGoal meleeAttackGoal;
    private LeapAtTargetGoal leapAtTargetGoal;
    private static final ResourceLocation REINFORCEMENT_CALLER_CHARGE_ID = ResourceLocation.fromNamespaceAndPath(MODID,"reinforcement_caller_charge");
    private static final AttributeModifier ZOMBIE_REINFORCEMENT_CALLEE_CHARGE = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(MODID,"reinforcement_callee_charge"), -0.05F, AttributeModifier.Operation.ADD_VALUE
    );
    public int targetTrappedCounter = 0;
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;

    public ShelobEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
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
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 100.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.rangedWebAttackGoal = new RangedWebAttackGoal<>(this, 1.0D, 60, 20.0F);
        this.meleeAttackGoal = new ShelobEntity.ShelobEntityAttackGoal(this);
        this.targetSelector.addGoal(2, new ShelobEntity.ShelobEntityTargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new ShelobEntity.ShelobEntityTargetGoal<>(this, LivingEntity.class));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean p_33820_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_33820_) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }
        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WEB_SHOOTING, false);
        builder.define(DATA_FLAGS_ID, (byte)0);
    }

    /** Special Attack */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!super.hurt(source, amount)) {
            return false;
        } else if (!(this.level() instanceof ServerLevel)) {
            return false;
        } else {
            LivingEntity livingentity = this.getTarget();
            if (livingentity == null && source.getEntity() instanceof LivingEntity) {
                livingentity = (LivingEntity) source.getEntity();
            }

            if (livingentity == null) return true;

            if (this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.nodrown.shelob").withStyle(ChatFormatting.DARK_BLUE));
                this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 2 * 20, 0));
            }

            if ((this.isOnFire() && !this.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.onfire.shelob").withStyle(ChatFormatting.DARK_RED));
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2 * 20, 0));
            }

            if (this.getHealth() < this.getMaxHealth()) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.healself.shelob").withStyle(ChatFormatting.LIGHT_PURPLE));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2 * 20, 0));
            }

            if (this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.speedup.shelob").withStyle(ChatFormatting.AQUA));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2 * 20, 0));
            }

            if (livingentity != null
                    && this.level().getDifficulty() == Difficulty.HARD
                    && (double)this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                    && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                    && this.level() instanceof ServerLevel serverLevel) {

                int i = Mth.floor(this.getX());
                int j = Mth.floor(this.getY());
                int k = Mth.floor(this.getZ());
                MirkwoodSpiderEntity spider = new MirkwoodSpiderEntity(this.level());

                for (int l = 0; l < 50; l++) {
                    int i1 = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int j1 = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int k1 = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    BlockPos blockpos = new BlockPos(i1, j1, k1);
                    EntityType<?> entitytype = spider.getType();
                    if (SpawnPlacements.isSpawnPositionOk(entitytype, this.level(), blockpos)
                            && SpawnPlacements.checkSpawnRules(entitytype, serverLevel, MobSpawnType.REINFORCEMENT, blockpos, this.level().random)) {
                        spider.setPos((double)i1, (double)j1, (double)k1);
                        if (!this.level().hasNearbyAlivePlayer((double)i1, (double)j1, (double)k1, 7.0)
                                && this.level().isUnobstructed(spider)
                                && this.level().noCollision(spider)
                                && !this.level().containsAnyLiquid(spider.getBoundingBox())) {

                            livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.helpcomming.shelob").withStyle(ChatFormatting.DARK_GREEN));

                            spider.setTarget(livingentity);
                            spider.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(spider.blockPosition()), MobSpawnType.REINFORCEMENT, null);
                            serverLevel.addFreshEntityWithPassengers(spider);
                            AttributeInstance attributeinstance = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
                            AttributeModifier attributemodifier = attributeinstance.getModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                            double d0 = attributemodifier != null ? attributemodifier.amount() : 0.0;
                            attributeinstance.removeModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                            attributeinstance.addPermanentModifier(
                                    new AttributeModifier(REINFORCEMENT_CALLER_CHARGE_ID, d0 - 0.05, AttributeModifier.Operation.ADD_VALUE)
                            );
                            spider.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(ZOMBIE_REINFORCEMENT_CALLEE_CHARGE);
                            break;
                        }
                    }
                }
            }
            return true;
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        entityIn.setDeltaMovement(entityIn.getDeltaMovement().add(0.0D, 0.4F, 0.0D));
        long time = System.currentTimeMillis();
        if (super.doHurtTarget(entityIn)) {
            if (entityIn instanceof Player) {
                if (time > nextAbilityUse) {
                    nextAbilityUse = time + coolDown;
                    Player player = (Player) entityIn;
                    int strength = 2;
                    if (MathUtility.getRandom(10) <= 3) {
                        player.addEffect(new MobEffectInstance(TolkienMobEffects.WATCHER_FEAR, strength * 20, 0));
                    } else {
                        player.addEffect(new MobEffectInstance(TolkienMobEffects.SLEEPNESIA, strength * 20, 0));
                    }
                }
            }
        }
        this.playSound(SoundEvents.SPIDER_HURT, 1.0F, 1.0F);
        return true;
    }

    /** Sounds */
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

    /** Animations */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            if (event.isMoving() && !event.getAnimatable().isAggressive()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("move"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && !event.getAnimatable().isAggressive()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (this.swinging && !this.getRanged() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }else if (this.isAggressive() && this.getRanged() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)){
                event.getController().setAnimation(RawAnimation.begin().then("shooting", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    /** BOSS BAR */
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.translatable("entity.tolkienmobs.entityttmshelob"), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10);

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
    static class ShelobEntityAttackGoal extends MeleeAttackGoal {

        public ShelobEntityAttackGoal(ShelobEntity entity) {
            super(entity, 1.0D, true);
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

    static class ShelobEntityTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public ShelobEntityTargetGoal(ShelobEntity entity, Class<T> pClass) {
            super(entity, pClass, true);
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
}