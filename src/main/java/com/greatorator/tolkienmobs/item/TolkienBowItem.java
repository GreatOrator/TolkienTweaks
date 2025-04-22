package com.greatorator.tolkienmobs.item;

import com.greatorator.tolkienmobs.util.ColorUtility;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.LangTranslationUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.sounds.SoundEvents.ARROW_SHOOT;
import static net.minecraft.sounds.SoundSource.PLAYERS;
import static net.minecraft.stats.Stats.ITEM_USED;

public class TolkienBowItem extends BowItem {
    public final int useDuration;
    public final ColorUtility nameColor;
    public final Supplier<Item> infinityArrow;
    public final float speedScale;
    public TolkienBowItem(Properties properties, int uses, int useDuration, float speedScale, Supplier<Item> infinityArrow, ColorUtility nameColor) {
        super((uses == 0 ? properties.stacksTo(1).component(DataComponents.UNBREAKABLE, new Unbreakable(true)) : properties.durability(uses)));
        this.useDuration = useDuration;
        this.nameColor = nameColor;
        this.infinityArrow = infinityArrow;
        this.speedScale = speedScale;
    }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) {return useDuration;}
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(itemstack).isEmpty();
        InteractionResultHolder<ItemStack> ret = EventHooks.onArrowNock(itemstack, level, player, hand, flag);
        if(ret != null) return ret;
        else if(infinityArrow == null && !player.hasInfiniteMaterials() && !flag) return InteractionResultHolder.fail(itemstack);
        else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }
    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if(entityLiving instanceof Player player) {
            ItemStack itemstack = player.getProjectile(stack);
            if(infinityArrow != null && (itemstack.isEmpty() || itemstack.is(infinityArrow.get()))) itemstack = new ItemStack(infinityArrow.get());
            if(itemstack.isEmpty()) return;
            int i = EventHooks.onArrowLoose(stack, level, player, (getUseDuration(stack, entityLiving) - timeLeft) * 72000 / useDuration, true);
            if(i < 0) return;
            float f = getPowerForTime(i);
            if(f >= .1F) {
                List<ItemStack> list = draw(stack, itemstack, player);
                if(level instanceof ServerLevel serverlevel && !list.isEmpty()) shoot(serverlevel, player, player.getUsedItemHand(), stack, list, f * 3F * speedScale, 1, f == 1, null);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ARROW_SHOOT, PLAYERS, 1, 1 / (level.getRandom().nextFloat() * .4F + 1.2F) + f * .5F);
                player.awardStat(ITEM_USED.get(this));
            }
        }
    }
    @Override public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
        return new ItemStack(infinityArrow == null ? Items.ARROW : infinityArrow.get());
    }
    @Override public boolean isEnchantable(ItemStack stack) {return true;}
    @Override public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) && (stack.has(DataComponents.MAX_DAMAGE) || !(enchantment.is(Enchantments.MENDING) || enchantment.is(Enchantments.UNBREAKING)));
    }
    @Override public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return stack.has(DataComponents.MAX_DAMAGE) || !(GeneralUtility.hasStoredEnchantment(Enchantments.MENDING, book) || GeneralUtility.hasStoredEnchantment(Enchantments.UNBREAKING, book));
    }
    public static void addEffect(Arrow arrow, MobEffectInstance instance) {
        ItemStack stack = arrow.getPickupItemStackOrigin();
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        Iterable<MobEffectInstance> contentsit = contents.getAllEffects();
        for(MobEffectInstance c : contentsit) if(c.is(instance.getEffect())) return;
        stack.set(DataComponents.POTION_CONTENTS, contents.withEffectAdded(instance));
    }
    @OnlyIn(Dist.CLIENT)
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        float speed = 72000F / useDuration;
        tooltip.add(LangTranslationUtils.shootingPower(speedScale));
        if(speed > 1) tooltip.add(LangTranslationUtils.bowFasterPull(speed));
        if(speed < 1) tooltip.add(LangTranslationUtils.bowSlowerPull(1 / speed));
        if(infinityArrow != null) tooltip.add(LangTranslationUtils.infiniteAmmo());
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
    @Override public Component getName(ItemStack pStack) {
        return nameColor != null ? ((MutableComponent) super.getName(pStack)).withColor(nameColor.getColor()) : super.getName(pStack);
    }
}