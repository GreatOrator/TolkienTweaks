package com.greatorator.tolkienmobs.entity.boss;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.BalrogAttackGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.SwitchCombatGoal;
import com.greatorator.tolkienmobs.entity.projectiles.FellBeastFireballEntity;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.util.MathUtility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BalrogEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID =
            SynchedEntityData.defineId(BalrogEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(BalrogEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Map<ServerLevel, Map<BlockPos, Long>> trackedBlocks = new HashMap<>();
    private static final Map<Entity, Boolean> balrogOnGroundMap = new HashMap<>();
    private static final int DURATION_TICKS = 100;
    private static final int GROUND_DEPTH = 1;
    private final RangedBowAttackGoal<TolkienMonsterEntity> bowGoal = new RangedBowAttackGoal<>(this, 1.0, 20, 15.0F);
    private final MeleeAttackGoal meleeGoal = new BalrogAttackGoal(this, 1.0D, 20, 15.0F, false);

    public BalrogEntity(EntityType<? extends Monster> entityType, Level level) {
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
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwitchCombatGoal(this, 6.0D, 12.0D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TolkienItems.WHIP_FIRE.get()));
    }

    @Override
    protected void updateWeaponGoal() {
        scheduleWeaponGoalUpdate = false;
        this.goalSelector.removeGoal(this.meleeGoal);
        this.goalSelector.removeGoal(this.bowGoal);
        ItemStack itemstack = this.getMainHandItem();
        if (itemstack.getItem() == Items.BOW) {
            int i = 20;
            if (this.level.getDifficulty() != Difficulty.HARD) {
                i = 40;
            }

            this.bowGoal.setMinAttackInterval(i);
            this.goalSelector.addGoal(2, this.bowGoal);
            this.ranged = true;
        } else {
            this.goalSelector.addGoal(1, this.meleeGoal);
            this.ranged = false;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float distanceFactor) {
        double d0 = entity.getEyeY() - (double)1.1F;
        double d1 = entity.getX() - this.getX();
        double d3 = entity.getZ() - this.getZ();
        FellBeastFireballEntity fireballentity = new FellBeastFireballEntity(this, this.level);
        double d2 = d0 - fireballentity.getY();
        float f = MathUtility.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        fireballentity.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(fireballentity);

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

            setCharged(true);

            if (livingentity == null) return true;

            if (this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.nodrown.balrog").withStyle(ChatFormatting.DARK_BLUE));
                this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 2 * 20, 0));
            }

            if ((this.isOnFire() && !this.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.onfire.balrog").withStyle(ChatFormatting.DARK_RED));
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2 * 20, 0));
            }

            if (this.getHealth() < this.getMaxHealth()) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.healself.balrog").withStyle(ChatFormatting.LIGHT_PURPLE));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2 * 20, 0));
            }

            if (this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
                livingentity.sendSystemMessage(Component.translatable(MODID + ".msg.speedup.balrog").withStyle(ChatFormatting.AQUA));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2 * 20, 0));
            }
            return true;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
        builder.define(DATA_FLAGS_ID, (byte)0);
    }

    /** Animations */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            if (event.isMoving() && !event.getAnimatable().isAggressive()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("walk"));
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
                event.getController().setAnimation(RawAnimation.begin().then("ranged", Animation.LoopType.PLAY_ONCE));
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
        return TolkienSounds.IDLE_BALROG.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_BALROG.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_BALROG.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(TolkienSounds.STEP_BALROG.get(), 0.15F, 1.0F);
    }

    /** BOSS BAR */
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.translatable("entity.tolkienmobs.entityttmbalrog"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);

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

    /** Special Balrog Effect */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            Vec3 playerPosition = this.position();
            Vec3 movementVector = this.getDeltaMovement().scale(2);

            if (this.level() instanceof ServerLevel serverLevel) {
                BlockPos groundPos = new BlockPos(
                        (int) Math.floor(playerPosition.x + movementVector.x),
                        (int) Math.floor(playerPosition.y - GROUND_DEPTH),
                        (int) Math.floor(playerPosition.z + movementVector.z)
                );

                Vec3 lookDirection = this.getLookAngle().normalize();

                for (int xOffset = -2; xOffset <= 2; xOffset++) {
                    for (int zOffset = -2; zOffset <= 2; zOffset++) {
                        for (int yOffset = 0; yOffset <= 1; yOffset++) { // Ground level and one block up
                            BlockPos checkPos = this.blockPosition().offset(xOffset, yOffset, zOffset);

                            Vec3 blockVec = new Vec3(checkPos.getX() - this.getX(), 0, checkPos.getZ() - this.getZ());
                            double dotProduct = blockVec.normalize().dot(lookDirection);

                            if (dotProduct > 0.5 && this.onGround()) {
                                playMagmaEffects(serverLevel, checkPos);
                                replaceSurfaceWithMagmaBlock(serverLevel, checkPos, DURATION_TICKS);
                            }
                        }
                    }
                }
                boolean isOnGround = serverLevel.getBlockState(groundPos).is(BlockTags.MINEABLE_WITH_AXE) || serverLevel.getBlockState(groundPos).is(BlockTags.MINEABLE_WITH_PICKAXE) || serverLevel.getBlockState(groundPos).is(BlockTags.MINEABLE_WITH_SHOVEL);

                boolean wasOnGround = balrogOnGroundMap.getOrDefault(this, false);

                if (isOnGround && !wasOnGround) {
                    playMagmaEffects(serverLevel, groundPos);
                    replaceSurfaceWithMagmaBlock(serverLevel, groundPos, DURATION_TICKS);
                }

                balrogOnGroundMap.put(this, isOnGround);

                revertExpiredBlocks(serverLevel);
            }
        }

        if (this.level().isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level()
                        .playLocalSound(
                                this.getX() + 0.5,
                                this.getY() + 0.5,
                                this.getZ() + 0.5,
                                SoundEvents.BLAZE_BURN,
                                this.getSoundSource(),
                                1.0F + this.random.nextFloat(),
                                this.random.nextFloat() * 0.7F + 0.3F,
                                false
                        );
            }

            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public boolean isOnFire() {
        return this.isCharged();
    }

    private boolean isCharged() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setCharged(boolean charged) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (charged) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    private void replaceSurfaceWithMagmaBlock(ServerLevel level, BlockPos center, int durationTicks) {
        int radius = 3; // Radius for transformation
        long expirationTime = level.getGameTime() + durationTicks;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;

                BlockPos pos = center.offset(x, 0, z);
                if (level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_AXE) || level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_PICKAXE) || level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_SHOVEL)) { // Includes flowing lava
                    level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
                    trackBlock(level, pos, expirationTime);
                }
            }
        }
    }

    private void trackBlock(ServerLevel level, BlockPos pos, long expirationTime) {
        trackedBlocks.computeIfAbsent(level, k -> new HashMap<>())
                .put(pos.immutable(), expirationTime);
    }

    private void revertExpiredBlocks(ServerLevel level) {
        Map<BlockPos, Long> levelBlocks = trackedBlocks.getOrDefault(level, new HashMap<>());
        long currentTime = level.getGameTime();

        levelBlocks.entrySet().removeIf(entry -> {
            if (currentTime >= entry.getValue()) {
                BlockPos pos = entry.getKey();
                if (level.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
                    level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
                    playRevertEffects(level, pos);
                }
                return true;
            }
            return false;
        });
    }

    private void playMagmaEffects(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_AXE) || level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_PICKAXE) || level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.7f, 0.8f);

            level.sendParticles(ParticleTypes.LAVA,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    20, 0.5, 0.5, 0.5, 0.2);
        }
    }

    private void playRevertEffects(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
            level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.5f, 0.9f);
            level.sendParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    5, 0.2, 0.2, 0.2, 0.01);
        }
    }

    public static boolean isTemporaryMagmaBlock(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            Map<BlockPos, Long> levelBlocks = trackedBlocks.getOrDefault(serverLevel, new HashMap<>());
            return levelBlocks.containsKey(pos.immutable());
        }
        return false;
    }

    public static void removeAllTemporaryBlocks(ServerLevel level) {
        if (!trackedBlocks.containsKey(level)) return;

        Map<BlockPos, Long> levelBlocks = trackedBlocks.get(level);
        for (BlockPos pos : levelBlocks.keySet()) {
            if (level.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
                level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState()); // Restore lava
            }
        }
        trackedBlocks.remove(level);
    }
}
