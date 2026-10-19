package com.greatorator.tolkienmobs.entity.ai.goal;

import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

public class MeleeCombatGoal extends Goal
{
    private final TolkienMonsterEntity hostMob;
    private LivingEntity target;
    private ItemStack storedMainHandItem;
    private ItemStack storedOffHandItem;

    public MeleeCombatGoal(TolkienMonsterEntity mobEntity) {
        this.hostMob = mobEntity;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean canUse()
    {
        this.target = this.hostMob.getTarget();

        if (target == null)
        {
            return false;
        }
        else if (!target.isAlive())
        {
            return false;
        }
        else
        {
            return this.hostMob.hasLineOfSight(this.target);
        }
    }

    /**
     * Resets the task - puts weapon away when no longer aggressive
     */
    @Override
    public void stop()
    {
        target = null;
        putWeaponAway();
    }

    /**
     * Updates the task - draws weapon when aggressive
     */
    @Override
    public void tick()
    {
        if (this.hostMob.isAggressive()) {
            drawWeapon();
        }
    }

    private void drawWeapon()
    {
        // Store current items if not already stored
        if (storedMainHandItem == null) {
            storedMainHandItem = this.hostMob.getMainHandItem().copy();
            storedOffHandItem = this.hostMob.getOffhandItem().copy();
        }

        // Check if weapon is already drawn (mainhand is not empty)
        ItemStack currentMainHand = this.hostMob.getMainHandItem();
        if (!currentMainHand.isEmpty()) {
            return; // Weapon already drawn
        }

        // Restore the weapon from stored items
        this.hostMob.setItemSlot(EquipmentSlot.MAINHAND, storedMainHandItem);
        this.hostMob.setItemSlot(EquipmentSlot.OFFHAND, storedOffHandItem);
    }

    private void putWeaponAway()
    {
        if (storedMainHandItem != null) {
            // Clear hands (put weapon away)
            this.hostMob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.hostMob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            
            // Clear stored items
            storedMainHandItem = null;
            storedOffHandItem = null;
        }
    }
}
