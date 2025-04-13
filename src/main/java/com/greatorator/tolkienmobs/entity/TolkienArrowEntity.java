package com.greatorator.tolkienmobs.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

public class TolkienArrowEntity extends AbstractArrow {
    @Override
    protected ItemStack getDefaultPickupItem() {
        return null;
    }
    public float powerMultiplier = 1F;
    public TolkienArrowEntity(EntityType<? extends TolkienArrowEntity> entityType, Level level) {
        super(entityType, level);
    }
    public TolkienArrowEntity(EntityType<? extends AbstractArrow> type, Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(type, owner, level, pickupItemStack, firedFromWeapon);
    }
    public TolkienArrowEntity(EntityType<? extends AbstractArrow> type, Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(type, x, y, z, level, pickupItemStack, firedFromWeapon);
    }
    /**
     * The arrow power represents the ratio of the arrow's mass to surface area and is used to multiply the damage yield on impact.
     * @return the arrow's base power calculated from {@code mass / tip surface area}
     */
    public float getArrowPower() {return 2F;}
    @Override
    protected void onHitEntity(EntityHitResult result) {
        setBaseDamage(getArrowPower() * powerMultiplier);
        super.onHitEntity(result);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("power_m", powerMultiplier);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        powerMultiplier = compound.getFloat("power_m");
    }
    public void addEffect(MobEffectInstance effectInstance) {
        Iterable<MobEffectInstance> contents = getPotionContents().getAllEffects();
        for(MobEffectInstance c : contents) if(c.is(effectInstance.getEffect())) return;
        setPotionContents(getPotionContents().withEffectAdded(effectInstance));
    }
    protected PotionContents getPotionContents() {
        return getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }
    private void setPotionContents(PotionContents potionContents) {
        getPickupItemStackOrigin().set(DataComponents.POTION_CONTENTS, potionContents);
    }
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        Entity entity = getEffectSource();
        PotionContents potioncontents = getPotionContents();
        if(potioncontents.potion().isPresent()) for(MobEffectInstance mobeffectinstance : potioncontents.potion().get().value().getEffects()){
            if(mobeffectinstance.getEffect().value().isInstantenous()) mobeffectinstance.getEffect().value().applyInstantenousEffect(getOwner(), this, living, mobeffectinstance.getAmplifier(), 1D);
            else living.addEffect(new MobEffectInstance(mobeffectinstance.getEffect(), Math.max(mobeffectinstance.mapDuration((p_268168_) -> p_268168_ / 8), 1), mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()), entity);
        } for(MobEffectInstance mobeffectinstance1 : potioncontents.customEffects()) {
            if(mobeffectinstance1.getEffect().value().isInstantenous()) mobeffectinstance1.getEffect().value().applyInstantenousEffect(getOwner(), this, living, mobeffectinstance1.getAmplifier(), 1D);
            else living.addEffect(mobeffectinstance1, entity);
        }
    }
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
    }
}
