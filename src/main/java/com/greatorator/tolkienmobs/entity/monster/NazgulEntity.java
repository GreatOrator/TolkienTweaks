package com.greatorator.tolkienmobs.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.MeleeCombatGoal;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

public class NazgulEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(NazgulEntity.class, EntityDataSerializers.BOOLEAN);

    public NazgulEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeCombatGoal(this));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
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
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entityIn) {
        long time = System.currentTimeMillis();
        if (super.doHurtTarget(serverLevel, entityIn)) {
            if (entityIn instanceof Player) {
                if (time > nextAbilityUse) {
                    nextAbilityUse = time + coolDown;
                    Player player = (Player) entityIn;
                    BlockPos blockpos = player.blockPosition();
                    int strength = 2;
                    level.playSound(null, blockpos, TolkienSounds.IDLE_NAZGUL.get(), SoundSource.HOSTILE, 1.0F + level.getRandom().nextFloat(), level.getRandom().nextFloat() * 0.7F + 0.3F);
                    player.addEffect(new MobEffectInstance(TolkienMobEffects.PARALYSING_FEAR, strength * 20, 0));
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
        builder.define(ATTACKING, false);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.reassessWeaponGoal();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        RandomSource randomsource = level.getRandom();

        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(level, randomsource, difficulty);
        this.reassessWeaponGoal();

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
    private static final RawAnimation SITTING = RawAnimation.begin().thenPlay("sitting");
    private static final RawAnimation ATTACK = RawAnimation.begin().then("attack", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<NazgulEntity>("Walk", 1, test -> {
            // NazgulSteedEntity spawns a nazgul riding it; a carried rider must never walk-animate
            if (this.isPassenger()) {
                return PlayState.STOP;
            }

            return test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP;
        }));
        controllers.add(new AnimationController<NazgulEntity>("Idle", 1, test -> {
            // Sitting in the saddle instead of standing on it (same as WitchKingEntity)
            if (this.isPassenger()) {
                return test.setAndContinue(SITTING);
            }

            return test.isMoving() ? PlayState.STOP : test.setAndContinue(IDLE);
        }));
        controllers.add(new AnimationController<NazgulEntity>("Attack", 1, test -> {
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
        return TolkienSounds.IDLE_NAZGUL.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_NAZGUL.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_NAZGUL.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(TolkienSounds.STEP_NAZGUL.get(), 0.15F, 1.0F);
    }
}
