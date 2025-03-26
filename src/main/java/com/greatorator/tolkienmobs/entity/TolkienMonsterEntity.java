package com.greatorator.tolkienmobs.entity;

import com.greatorator.tolkienmobs.entity.ai.goal.SwitchCombatGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TolkienMonsterEntity extends Monster implements RangedAttackMob {
    public static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(TolkienMonsterEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(TolkienMonsterEntity.class, EntityDataSerializers.BOOLEAN);
    public boolean scheduleWeaponGoalUpdate = true;
    protected boolean ranged;

    private final RangedBowAttackGoal<TolkienMonsterEntity> bowGoal = new RangedBowAttackGoal<>(this, 1.0, 20, 15.0F);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2, false) {
        @Override
        public void stop() {
            super.stop();
            TolkienMonsterEntity.this.setAggressive(false);
        }

        @Override
        public void start() {
            super.start();
            TolkienMonsterEntity.this.setAggressive(true);
        }
    };

    protected TolkienMonsterEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.reassessWeaponGoal();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SwitchCombatGoal(this, 6.0D, 12.0D));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.ARMOR, 5.0D);
    }
    /**
     * Set up using weapons
     */
    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        setHeldItem(stack);
        reassessWeaponGoal();
    }

    public void setHeldItem(ItemStack heldItem) {
        this.entityData.set(HELD_ITEM, heldItem.copy());
    }

    public ItemStack getHeldItem() {
        return this.entityData.get(HELD_ITEM).copy();
    }

    public void reassessWeaponGoal() {
        if (!this.level.isClientSide) {
            scheduleWeaponGoalUpdate = true;
        }
    }

    @Override
    public void tick() {
        if (scheduleWeaponGoalUpdate) {
            updateWeaponGoal();
        }
        super.tick();
    }

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
            this.goalSelector.addGoal(4, this.bowGoal);
            this.ranged = true;
        } else {
            this.goalSelector.addGoal(4, this.meleeGoal);
            this.ranged = false;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
        builder.define(HELD_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    public boolean getRanged() {
        return ranged;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack weapon = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> {
            return item instanceof BowItem;
        }));
        ItemStack itemstack1 = this.getProjectile(weapon);
        AbstractArrow abstractarrow = this.getArrow(itemstack1, distanceFactor, weapon);
        Item var7 = weapon.getItem();
        if (var7 instanceof ProjectileWeaponItem weaponItem) {
            abstractarrow = weaponItem.customArrow(abstractarrow, itemstack1, weapon);
        }

        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333) - abstractarrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrow);
    }

    protected AbstractArrow getArrow(ItemStack arrow, float velocity, @Nullable ItemStack weapon) {
        return ProjectileUtil.getMobArrow(this, arrow, velocity, weapon);
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem projectileWeapon) {
        return projectileWeapon == Items.BOW;
    }
}