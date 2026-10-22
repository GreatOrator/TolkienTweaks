package com.greatorator.tolkienmobs.item;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TolkienFood extends Item {
    public boolean hasEffectOverride = false;

    public TolkienFood(Properties properties) {
        super(properties);
    }

    public TolkienFood setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEffectOverride || super.isFoil(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        ItemStack itemstack = super.finishUsingItem(stack, worldIn, entityLiving);

        return entityLiving instanceof Player && ((Player) entityLiving).getAbilities().instabuild ? itemstack : new ItemStack(TolkienItems.BOTTLE_FANCY.get());
    }
}
