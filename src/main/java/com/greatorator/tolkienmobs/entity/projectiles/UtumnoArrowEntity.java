package com.greatorator.tolkienmobs.entity.projectiles;

import com.greatorator.tolkienmobs.entity.TolkienArrowEntity;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class UtumnoArrowEntity extends TolkienArrowEntity {
    public UtumnoArrowEntity(EntityType<? extends TolkienArrowEntity> entityType, Level level) {
        super(entityType, level);
    }
    public UtumnoArrowEntity(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(TolkienEntities.AMMO_ARROW_UTUMNO.get(), level, owner, pickupItemStack, firedFromWeapon);
    }

    public UtumnoArrowEntity(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(TolkienEntities.AMMO_ARROW_UTUMNO.get(), level, x, y, z, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(TolkienItems.UTUMNO_ARROW.get());
    }

    @Override
    public float getArrowPower() {
        return 20F;
    }
}
