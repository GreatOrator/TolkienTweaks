package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.containers.KeyRingContainer;
import com.greatorator.tolkienmobs.containers.handlers.KeyRingItemStackHandler;
import com.greatorator.tolkienmobs.handler.TolkienDataComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KeyRingItem extends Item {
    public KeyRingItem(Properties properties) {
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

        player.openMenu(new SimpleMenuProvider(
                (windowId, playerInventory, playerEntity) -> new KeyRingContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs." + itemstack.getDescriptionId())), (buf -> {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
        }));

        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

    public KeyRingItemStackHandler getItemHandler(ItemStack stack) {
        return new KeyRingItemStackHandler(KeyRingContainer.SLOTS, stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        //if (world.getDayTime() % 20 == 0) return;
        if (entity instanceof Player player && getActive(stack)) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack keyStack = player.getInventory().getItem(i);
                if (isValidItem(keyStack))
                    addKeyToInventory(stack, keyStack);
            }
        }
    }

    public static ItemStack addKeyToInventory(ItemStack keyHolder, ItemStack key) {
        if (key.getItem() instanceof KeyItem && !key.isComponentsPatchEmpty())
            return key;
        ComponentItemHandler handler = new ComponentItemHandler(keyHolder, TolkienDataComponents.ITEMSTACK_HANDLER.get(), KeyRingContainer.SLOTS);
        if (handler == null) return key;
        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if (stackInSlot.isEmpty()) emptySlots.add(i);
            if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameComponents(stackInSlot, key)) {
                int j = stackInSlot.getCount() + key.getCount();
                int maxSize = 64;
                if (j <= maxSize) {
                    key.setCount(0);
                    stackInSlot.setCount(j);
                    handler.setStackInSlot(i, stackInSlot);
                } else if (stackInSlot.getCount() < maxSize) {
                    key.shrink(maxSize - stackInSlot.getCount());
                    stackInSlot.setCount(maxSize);
                    handler.setStackInSlot(i, stackInSlot);
                }
                if (key.isEmpty()) {
                    return key;
                }
            }
        }
        if (emptySlots.isEmpty()) return key;
        handler.insertItem(emptySlots.get(0), key.split(key.getCount()), false);
        return key;
    }

    private static boolean isValidItem(ItemStack stack) {
        return true;
    }

    public static UUID getUUID(ItemStack stack) {
        if (!stack.has(TolkienDataComponents.KEY_RING_UUID)) {
            UUID newId = UUID.randomUUID();
            stack.set(TolkienDataComponents.KEY_RING_UUID, newId);
            return newId;
        }
        return stack.get(TolkienDataComponents.KEY_RING_UUID);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return getActive(itemStack);
    }

    public static boolean getActive(ItemStack stack) {
        return stack.getOrDefault(TolkienDataComponents.KEY_RING_ACTIVE, false);
    }

    public static boolean setActive(ItemStack stack, boolean active) {
        if (!active)
            stack.remove(TolkienDataComponents.KEY_RING_ACTIVE);
        else
            stack.set(TolkienDataComponents.KEY_RING_ACTIVE, active);
        return active;
    }

    @Nullable
    private static IItemHandler getItemStackHandlerCoinPouch(ItemStack itemStack) {
        IItemHandler optional = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
        return optional;
    }

    public static float getFullnessPropertyOverride(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int i) {
        IItemHandler keyRing = getItemStackHandlerCoinPouch(itemStack);
        if (keyRing == null) return 0;
        int count = 0;
        int j = i;
        for (j = 0; j < keyRing.getSlots(); j++) {
            count+=keyRing.getStackInSlot(j).getCount();
        }
        float fractionEmpty = count / (float)(keyRing.getSlots());
        return 0.0F + fractionEmpty;
    }
}
