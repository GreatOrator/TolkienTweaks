package com.greatorator.tolkienmobs.item.projectile;

import com.greatorator.tolkienmobs.entity.projectiles.GaladhrimArrowEntity;
import com.greatorator.tolkienmobs.item.TolkienArrowItem;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class GaladhrimArrowItem extends TolkienArrowItem {
    public GaladhrimArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        return new GaladhrimArrowEntity(level, shooter, ammo.copyWithCount(1), weapon);
    }
    @Override public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        AbstractArrow arrow = new GaladhrimArrowEntity(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }
    @Override public int getBaseDamage() {return 15;}
}
