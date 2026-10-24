package com.greatorator.tolkienmobs.item;

import com.greatorator.tolkienmobs.util.LangTranslationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;

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
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(LangTranslationUtils.baseDamage(getBaseDamage()));
        super.appendHoverText(stack, context, display, tooltip, flagIn);
    }

}
