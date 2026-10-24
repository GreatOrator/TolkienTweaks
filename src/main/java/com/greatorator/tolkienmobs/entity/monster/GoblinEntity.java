package com.greatorator.tolkienmobs.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.GoblinFlockToSameKindGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.GoblinPanicOnFlockDeathGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.MeleeCombatGoal;
import com.greatorator.tolkienmobs.entity.boss.GoblinKingEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

public class GoblinEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PANICKED = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.BOOLEAN);

    public GoblinEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public GoblinEntity(Level level) {
        this(TolkienEntities.ENTITY_TTM_GOBLIN.get(), level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeCombatGoal(this));
        this.goalSelector.addGoal(2, new GoblinPanicOnFlockDeathGoal(this, 2.0F));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.3F));
        this.goalSelector.addGoal(5, new GoblinEntity.AttackTurtleEggGoal(this, 1.0D, 3));
        this.goalSelector.addGoal(5, new GoblinFlockToSameKindGoal(this, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(MathUtility.getRandomInteger(1, 10) > 10 ? GoblinEntity.class : GoblinKingEntity.class));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 6.0D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TolkienItems.AXE_MORGULIRON.get()));
    }

    /**
     * Special Attack
     */
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        if (super.doHurtTarget(serverLevel, entity)) {
            long time = System.currentTimeMillis();
            if (entity instanceof LivingEntity && time > nextAbilityUse) {
                nextAbilityUse = time + coolDown;
                int duration;
                switch (level.getDifficulty()) {
                    case EASY:
                        duration = 7;
                        break;
                    default:
                    case NORMAL:
                        duration = 15;
                        break;
                    case HARD:
                        duration = 30;
                        break;
                }

                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.HUNGER, duration * 20, 0));
            }

            return true;
        } else {
            return false;
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
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.reassessWeaponGoal();
        this.entityData.set(VARIANT, input.getIntOr("Variant", 0));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
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
     *
     * GeckoLib 5 builds a controller without the animatable and hands its handler an AnimationTest, so the
     * controller no longer holds `this`. The RawAnimations are built once, here, instead of once per frame.
     */
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().then("attack", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GoblinEntity>("Walk", 1, test ->
                test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP));
        controllers.add(new AnimationController<GoblinEntity>("Idle", 1, test ->
                test.isMoving() ? PlayState.STOP : test.setAndContinue(IDLE)));
        controllers.add(new AnimationController<GoblinEntity>("Attack", 1, test -> {
            // A swing has to play out: 5 resets the controller when the handler returns STOP, so dropping
            // to STOP mid-swing would end the attack on its first frame.
            if (test.controller().isAnimatingBones()) {
                return PlayState.CONTINUE;
            }

            if (this.swinging) {
                return test.setAndContinue(ATTACK);
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
     * Goals
     */
    @Override
    public void aiStep() {
        super.aiStep();

        if (level.isClientSide() && isPanicked()) {
            for (int i = 0; i < 2; i++) {
                this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 0.5, this.getY() + this.getEyeHeight(), this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 0.5, 0, 0, 0);
            }
        }
    }

    class AttackTurtleEggGoal extends RemoveBlockGoal {
        AttackTurtleEggGoal(PathfinderMob creatureIn, double speed, int yMax) {
            super(Blocks.TURTLE_EGG, creatureIn, speed, yMax);
        }

        @Override
        public void playDestroyProgressSound(LevelAccessor worldIn, BlockPos pos) {
            worldIn.playSound((Player)null, pos, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5F, 0.9F + GoblinEntity.this.random.nextFloat() * 0.2F);
        }

        @Override
        public void playBreakSound(Level worldIn, BlockPos pos) {
            worldIn.playSound((Player)null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + worldIn.getRandom().nextFloat() * 0.2F);
        }

        @Override
        public double acceptedDistance() {
            return 1.14D;
        }
    }
}
