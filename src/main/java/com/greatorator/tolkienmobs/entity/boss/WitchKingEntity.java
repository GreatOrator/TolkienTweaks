package com.greatorator.tolkienmobs.entity.boss;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WitchKingEntity extends TolkienMonsterEntity implements GeoEntity {
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;

    public WitchKingEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.ARMOR, 25.0D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TolkienItems.SWORD_WITCHKING.get()));
    }

    /** Special Attack */
    @Override
    public boolean doHurtTarget(Entity entityIn) {
        long time = System.currentTimeMillis();
        if (super.doHurtTarget(entityIn)) {
            if (entityIn instanceof Player) {
                if (time > nextAbilityUse) {
                    nextAbilityUse = time + coolDown;
                    Player player = (Player) entityIn;
                    BlockPos blockpos = player.blockPosition();
                    int strength = 2;
                    level.playSound(null, blockpos, TolkienSounds.ANGRY_WITCHKING.get(), SoundSource.HOSTILE, 1.0F + level.random.nextFloat(), level.random.nextFloat() * 0.7F + 0.3F);
                    player.addEffect(new MobEffectInstance(TolkienMobEffects.PARALYSING_FEAR, strength * 20, 0));
                }
            }
        }
        return true;
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
        })); controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (!event.isMoving() && !event.getAnimatable().isAggressive()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Attack", 1, (event) -> {
            if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
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
        return TolkienSounds.IDLE_WITCHKING.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_WITCHKING.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_WITCHKING.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(TolkienSounds.STEP_WITCHKING.get(), 0.15F, 1.0F);
    }

    /** BOSS BAR */
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.translatable("entity.tolkienmobs.entityttmwitchking"), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);

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
}
