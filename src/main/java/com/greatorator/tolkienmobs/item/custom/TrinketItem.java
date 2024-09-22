package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.handler.TolkienDataComponents;
import com.greatorator.tolkienmobs.handler.TrinketComponent;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;
import java.util.List;

public class TrinketItem extends Item {
    private static int trinket_effect = 0;
    private static final Random random_effect = new Random();
    private final java.util.Map<Item, ItemColor> itemColors = new java.util.IdentityHashMap<>();

    public TrinketItem(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);

        if (player.isShiftKeyDown()) {
            setActive(itemstack, !getActive(itemstack));
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (getActive(stack)) {
            TrinketComponent potionContents = stack.getOrDefault(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY);
            Optional<Holder<Potion>> potionType = potionContents.potion();
            Optional<ResourceKey<Potion>> key = potionType.flatMap(Holder::unwrapKey);
            ResourceLocation effectType = key.get().location();
            if (potionContents.hasEffects()) {
                if (key.isPresent()) {
                    updateTrinket(stack, entity);

                }else {
                    updateTrinket(stack, entity);
                }
            }
        }
    }

    private void updateTrinket(ItemStack stack, Entity entity) {
        Player player = (Player) entity;
        TrinketComponent potionContents = stack.getOrDefault(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY);
        Optional<Holder<Potion>> potionType = potionContents.potion();
        Optional<ResourceKey<Potion>> key = potionType.flatMap(Holder::unwrapKey);
        ResourceLocation effectType = key.get().location();
        MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(effectType);
        MobEffectInstance mobEffectInstance = potionType.get().value().getEffects().getFirst();
            if (getActive(stack)) {
                MobEffectInstance active = player.getEffect((Holder<MobEffect>) mobEffect);
                if (active == null || active.getDuration() < 30 * 20) {
                    player.addEffect(mobEffectInstance);

                }
            } else {
                player.removeEffect(mobEffectInstance.getEffect());
            }
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = super.getDefaultInstance();
        itemstack.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(Potions.WATER));
        return itemstack;
    }

    public static TrinketComponent getPotionContents(ItemStack itemStack) {
        return itemStack.getOrDefault(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY);
    }

    public static int getPotionColor(ItemStack itemStack) {
        return getPotionContents(itemStack).getColor();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        TrinketComponent potioncontents = stack.get(TolkienDataComponents.POTION_CONTENTS);
        if (potioncontents != null) {
            potioncontents.addPotionTooltip(tooltipComponents::add, 0.125F, context.tickRate());
        }
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return Potion.getName(stack.getOrDefault(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY).potion(), this.getDescriptionId() + ".");
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return getActive(itemStack);
    }

    public static boolean getActive(ItemStack stack) {
        return stack.getOrDefault(TolkienDataComponents.TRINKET_ACTIVE, false);
    }

    public static boolean setActive(ItemStack stack, boolean active) {
        if (!active)
            stack.remove(TolkienDataComponents.TRINKET_ACTIVE);
        else
            stack.set(TolkienDataComponents.TRINKET_ACTIVE, active);
        return active;
    }
}
