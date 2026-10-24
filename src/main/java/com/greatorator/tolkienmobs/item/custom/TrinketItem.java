package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class TrinketItem extends Item {
    private static int trinket_effect = 0;
    private static final Random random_effect = new Random();

    public TrinketItem(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            setActive(itemstack, !getActive(itemstack));
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (getActive(stack)) {
            TrinketComponent potionContents = stack.getOrDefault(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY);
            Optional<Holder<Potion>> potionType = potionContents.potion();
            Optional<ResourceKey<Potion>> key = potionType.flatMap(Holder::unwrapKey);
            Identifier effectType = key.get().identifier();
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
        MobEffectInstance mobEffectInstance = potionType.get().value().getEffects().getFirst();
            if (getActive(stack) && player != null) {
                MobEffectInstance active = player.getEffect(mobEffectInstance.getEffect());
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

    /**
     * The potion names the trinket, the way {@code PotionContents#getName} names a potion item: the item's own
     * description id plus the potion's name, or {@code empty} when it holds no potion.
     */
    @Override
    public Component getName(ItemStack stack) {
        TrinketComponent contents = stack.getOrDefault(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY);
        String potion = contents.potion().map(holder -> holder.value().name()).orElse("empty");
        return Component.translatable(this.getDescriptionId() + "." + potion);
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
