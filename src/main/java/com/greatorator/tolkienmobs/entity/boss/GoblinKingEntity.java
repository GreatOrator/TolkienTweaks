package com.greatorator.tolkienmobs.entity.boss;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.monster.GoblinEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GoblinKingEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(GoblinKingEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(GoblinKingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PANICKED = SynchedEntityData.defineId(GoblinKingEntity.class, EntityDataSerializers.BOOLEAN);
    private static final ResourceLocation REINFORCEMENT_CALLER_CHARGE_ID = ResourceLocation.fromNamespaceAndPath(MODID,"reinforcement_caller_charge");
    private static final AttributeModifier ZOMBIE_REINFORCEMENT_CALLEE_CHARGE = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(MODID,"reinforcement_callee_charge"), -0.05F, AttributeModifier.Operation.ADD_VALUE
    );

    public GoblinKingEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(GoblinEntity.class));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 100.0D);
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float distanceFactor) {
        Snowball snowballentity = new Snowball(this.level, this);
        double d0 = entity.getEyeY() - (double)1.1F;
        double d1 = entity.getX() - this.getX();
        double d2 = d0 - snowballentity.getY();
        double d3 = entity.getZ() - this.getZ();
        float f = MathUtility.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(snowballentity);
    }

    /**
     * Special Attack        snowballentity.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!super.hurt(source, amount)) {
            return false;
        } else if (!(this.level instanceof ServerLevel serverworld)) {
            return false;
        } else {
            ServerLevel serverlevel = (ServerLevel)this.level();
            LivingEntity livingentity = this.getTarget();
            if (livingentity == null && source.getEntity() instanceof LivingEntity) {
                livingentity = (LivingEntity) source.getEntity();
            }

            if (livingentity == null) return true;

            if (this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.nodrown").withStyle(ChatFormatting.DARK_BLUE));
                this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 2 * 20, 0));
            }

            if ((this.isOnFire() && !this.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.onfire").withStyle(ChatFormatting.DARK_RED));
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2 * 20, 0));
            }

            if (this.getHealth() < this.getMaxHealth()) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.healself").withStyle(ChatFormatting.LIGHT_PURPLE));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2 * 20, 0));
            }

            if (this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.speedup").withStyle(ChatFormatting.AQUA));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2 * 20, 0));
            }

            if (livingentity != null
                    && this.level().getDifficulty() == Difficulty.HARD
                    && (double)this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                    && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                int i = Mth.floor(this.getX());
                int j = Mth.floor(this.getY());
                int k = Mth.floor(this.getZ());
                GoblinEntity goblin = new GoblinEntity(this.level());

                for (int l = 0; l < 50; l++) {
                    int i1 = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int j1 = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int k1 = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    BlockPos blockpos = new BlockPos(i1, j1, k1);
                    EntityType<?> entitytype = goblin.getType();
                    if (SpawnPlacements.isSpawnPositionOk(entitytype, this.level(), blockpos)
                            && SpawnPlacements.checkSpawnRules(entitytype, serverlevel, MobSpawnType.REINFORCEMENT, blockpos, this.level().random)) {
                        goblin.setPos((double)i1, (double)j1, (double)k1);
                        if (!this.level().hasNearbyAlivePlayer((double)i1, (double)j1, (double)k1, 7.0)
                                && this.level().isUnobstructed(goblin)
                                && this.level().noCollision(goblin)
                                && !this.level().containsAnyLiquid(goblin.getBoundingBox())) {

                            livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.helpcomming").withStyle(ChatFormatting.DARK_GREEN));

                            goblin.setTarget(livingentity);
                            goblin.finalizeSpawn(serverlevel, this.level().getCurrentDifficultyAt(goblin.blockPosition()), MobSpawnType.REINFORCEMENT, null);
                            serverlevel.addFreshEntityWithPassengers(goblin);
                            AttributeInstance attributeinstance = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
                            AttributeModifier attributemodifier = attributeinstance.getModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                            double d0 = attributemodifier != null ? attributemodifier.amount() : 0.0;
                            attributeinstance.removeModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                            attributeinstance.addPermanentModifier(
                                    new AttributeModifier(REINFORCEMENT_CALLER_CHARGE_ID, d0 - 0.05, AttributeModifier.Operation.ADD_VALUE)
                            );
                            goblin.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(ZOMBIE_REINFORCEMENT_CALLEE_CHARGE);
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }
    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
        builder.define(VARIANT, 0);
        builder.define(PANICKED, false);
    }

    public boolean isPanicked() {
        return entityData.get(PANICKED);
    }

    public void setPanicked(boolean flag) {
        entityData.set(PANICKED, flag);
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
                event.getController().setAnimation(RawAnimation.begin().then("ranged", Animation.LoopType.PLAY_ONCE));
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
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_GOBLIN.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_GOBLIN.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_GOBLIN.get();
    }

    /**
     *BOSS BAR
     */
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.literal("Goblin King"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_10);

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

        if (level.isClientSide && isPanicked()) {
            for (int i = 0; i < 2; i++) {
                this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 0.5, this.getY() + this.getEyeHeight(), this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 0.5, 0, 0, 0);
            }
        }
    }
}