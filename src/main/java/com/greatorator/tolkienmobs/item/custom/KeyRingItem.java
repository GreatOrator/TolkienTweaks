package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.containers.KeyRingContainer;
import com.greatorator.tolkienmobs.containers.handlers.KeyRingItemStackHandler;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KeyRingItem extends Item {
    public KeyRingItem(Properties properties) {
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

        player.openMenu(new SimpleMenuProvider(
                (windowId, playerInventory, playerEntity) -> new KeyRingContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs." + itemstack.getItem().getDescriptionId())), (buf -> {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
        }));

        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player && getActive(stack)) {
            // The key in either hand stays there: it is what a keystone or a lockable chest reads
            // when it is used, and those only ever look at the stack in hand.
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack keyStack = player.getInventory().getItem(i);
                if (keyStack == mainHand || keyStack == offHand) continue;
                if (isValidItem(keyStack))
                    addKeyToInventory(stack, keyStack);
            }
        }
    }

    public static ItemStack addKeyToInventory(ItemStack keyHolder, ItemStack key) {
        // Every key is stowed. This used to skip any key carrying components, which is every key
        // that has been given a code, so an active ring never picked one up.
        KeyRingItemStackHandler handler = new KeyRingItemStackHandler(KeyRingContainer.SLOTS, keyHolder);
        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stackInSlot = ItemUtil.getStack(handler, i);
            if (stackInSlot.isEmpty()) emptySlots.add(i);
            if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameComponents(stackInSlot, key)) {
                int j = stackInSlot.getCount() + key.getCount();
                // A key stacks to one, and its slot holds one: 64 here would write two keys into a
                // slot whose capacity is one, which is more than the slot can hold.
                int maxSize = Math.min(64, key.getMaxStackSize());
                if (j <= maxSize) {
                    key.setCount(0);
                    handler.set(i, ItemResource.of(stackInSlot), j);
                } else if (stackInSlot.getCount() < maxSize) {
                    key.shrink(maxSize - stackInSlot.getCount());
                    handler.set(i, ItemResource.of(stackInSlot), maxSize);
                }
                if (key.isEmpty()) {
                    return key;
                }
            }
        }
        if (emptySlots.isEmpty()) return key;
        handler.set(emptySlots.get(0), ItemResource.of(key), key.getCount());
        key.setCount(0);
        return key;
    }

    private static boolean isValidItem(ItemStack stack) {
        return stack.is(TolkienTags.Items.KEYS);
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

    public static float getKeyCount(ItemStack stack) {
        KeyRingItemStackHandler handler = new KeyRingItemStackHandler(KeyRingContainer.SLOTS, stack);
        int count = 0;
        for (int slot = 0; slot < handler.size(); slot++) {
            count += ItemUtil.getStack(handler, slot).getCount();
        }
        return count / (float)(handler.size());
    }
}
