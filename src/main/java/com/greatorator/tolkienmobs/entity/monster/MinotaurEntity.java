package com.greatorator.tolkienmobs.entity.monster;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.entity.boss.GoblinKingEntity;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

public class MinotaurEntity extends TolkienMonsterEntity implements GeoEntity {
    public MinotaurEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GoblinEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MordorOrcEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GoblinKingEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, UrukHaiEntity.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ARMOR, 6.0D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TolkienItems.AXE_MORGULIRON.get()));
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
    private static final RawAnimation RANGED = RawAnimation.begin().then("ranged", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<MinotaurEntity>("Walk", 1, test ->
                test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP));
        controllers.add(new AnimationController<MinotaurEntity>("Idle", 1, test ->
                test.isMoving() ? PlayState.STOP : test.setAndContinue(IDLE)));
        controllers.add(new AnimationController<MinotaurEntity>("Attack", 1, test -> {
            // A swing or a charge has to play out: 5 resets the controller when the handler returns STOP, so
            // dropping to STOP mid-animation would end the attack on its first frame.
            if (test.controller().isAnimatingBones()) {
                return PlayState.CONTINUE;
            }

            if (this.swinging) {
                this.swinging = false;
                return test.setAndContinue(ATTACK);
            }

            // The ranged flag is a one-shot latch here, cleared as the charge animation starts.
            if (this.getRanged()) {
                this.ranged = false;
                return test.setAndContinue(RANGED);
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
        return TolkienSounds.IDLE_MINOTAUR.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return TolkienSounds.HURT_MINOTAUR.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_MINOTAUR.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(TolkienSounds.STEP_MINOTAUR.get(), 0.15F, 1.0F);
    }
}