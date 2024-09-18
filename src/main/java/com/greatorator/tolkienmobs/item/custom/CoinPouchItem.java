package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.containers.CoinPouchContainer;
import com.greatorator.tolkienmobs.containers.handlers.CoinPouchItemStackHandler;
import com.greatorator.tolkienmobs.handler.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.item.TolkienCoinItem;
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

public class CoinPouchItem extends Item {
    public CoinPouchItem(Properties properties) {
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
                (windowId, playerInventory, playerEntity) -> new CoinPouchContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs." + itemstack.getDescriptionId())), (buf -> {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
        }));

        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

    public CoinPouchItemStackHandler getItemHandler(ItemStack stack) {
        return new CoinPouchItemStackHandler(CoinPouchContainer.SLOTS, stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        //if (world.getDayTime() % 20 == 0) return;
        if (entity instanceof Player player && getActive(stack)) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack coinStack = player.getInventory().getItem(i);
                if (isValidItem(coinStack))
                    addCoinToInventory(stack, coinStack);
            }
        }
    }

    public static ItemStack addCoinToInventory(ItemStack coinHolder, ItemStack coin) {
        ComponentItemHandler handler = new ComponentItemHandler(coinHolder, TolkienDataComponents.ITEMSTACK_HANDLER.get(), CoinPouchContainer.SLOTS);
        if (handler == null) return coin;
        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if (stackInSlot.isEmpty()) emptySlots.add(i);
            if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameComponents(stackInSlot, coin)) {
                int j = stackInSlot.getCount() + coin.getCount();
                int maxSize = 64;
                if (j <= maxSize) {
                    coin.setCount(0);
                    stackInSlot.setCount(j);
                    handler.setStackInSlot(i, stackInSlot);
                } else if (stackInSlot.getCount() < maxSize) {
                    coin.shrink(maxSize - stackInSlot.getCount());
                    stackInSlot.setCount(maxSize);
                    handler.setStackInSlot(i, stackInSlot);
                }
                if (coin.isEmpty()) {
                    return coin;
                }
            }
        }
        if (emptySlots.isEmpty()) return coin;
        handler.insertItem(emptySlots.get(0), coin.split(coin.getCount()), false);
        return coin;
    }

    private static boolean isValidItem(ItemStack stack) {
        return stack.is(TolkienTags.Items.COINS);
    }

    public static UUID getUUID(ItemStack stack) {
        if (!stack.has(TolkienDataComponents.COIN_POUCH_UUID)) {
            UUID newId = UUID.randomUUID();
            stack.set(TolkienDataComponents.COIN_POUCH_UUID, newId);
            return newId;
        }
        return stack.get(TolkienDataComponents.COIN_POUCH_UUID);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return getActive(itemStack);
    }

    public static boolean getActive(ItemStack stack) {
        return stack.getOrDefault(TolkienDataComponents.COIN_POUCH_ACTIVE, false);
    }

    public static boolean setActive(ItemStack stack, boolean active) {
        if (!active)
            stack.remove(TolkienDataComponents.COIN_POUCH_ACTIVE);
        else
            stack.set(TolkienDataComponents.COIN_POUCH_ACTIVE, active);
        return active;
    }

    @Nullable
    private static IItemHandler getItemStackHandlerCoinPouch(ItemStack itemStack) {
        IItemHandler optional = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
        if (optional != null) {
            return optional;
        }
        return null;
    }

    public static float getFullnessPropertyOverride(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int i) {
        int maxSlots = 15;
        int j=i;
        int count = itemStack.getOrDefault(TolkienDataComponents.COIN_POUCH_COUNTER, 0);
        IItemHandler coinPouch = getItemStackHandlerCoinPouch(itemStack);

        if (coinPouch == null) return 0;

        for (j = 0; j < coinPouch.getSlots(); j++) {
            itemStack.getOrDefault(TolkienDataComponents.COIN_POUCH_COUNTER, count+=coinPouch.getStackInSlot(j).getCount());
        }

        return Math.min(count - maxSlots, 15);
    }
}
