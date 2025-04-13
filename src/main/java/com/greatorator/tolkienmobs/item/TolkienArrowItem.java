package com.greatorator.tolkienmobs.item;

import com.greatorator.tolkienmobs.util.LangTranslationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class TolkienArrowItem extends ArrowItem {
    public TolkienArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isInfinite(ItemStack ammo, ItemStack bow, LivingEntity livingEntity) {
        return bow.getEnchantmentLevel(livingEntity.registryAccess().holderOrThrow(Enchantments.INFINITY)) > 0 || (bow.getItem() instanceof TolkienBowItem b && b.infinityArrow != null && b.infinityArrow.get() == this);
    }

    public int getBaseDamage() {return 2;}
    @OnlyIn(Dist.CLIENT)
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LangTranslationUtils.baseDamage(getBaseDamage()));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

}
