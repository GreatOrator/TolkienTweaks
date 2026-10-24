package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.containers.CoinPouchContainer;
import com.greatorator.tolkienmobs.containers.handlers.CoinPouchItemStackHandler;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.item.TolkienItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoinPouchItem extends TolkienItem {
    public CoinPouchItem(Properties properties) {
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
                (windowId, playerInventory, playerEntity) -> new CoinPouchContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs." + itemstack.getItem().getDescriptionId())), (buf -> {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
        }));

        return InteractionResult.PASS;
    }

    public CoinPouchItemStackHandler getItemHandler(ItemStack stack) {
        return new CoinPouchItemStackHandler(CoinPouchContainer.SLOTS, stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
        if (!(entity instanceof Player player) || !getActive(stack)) return;
        boolean changed = getItemHandler(stack).convertCoins();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack coinStack = player.getInventory().getItem(i);
            if (!isValidItem(coinStack)) continue;
            int before = coinStack.getCount();
            addCoinToInventory(stack, coinStack);
            changed |= before != coinStack.getCount();
        }
        if (changed) player.getInventory().setChanged();
    }

    public static ItemStack addCoinToInventory(ItemStack coinHolder, ItemStack coin) {
        if (!(coinHolder.getItem() instanceof CoinPouchItem pouch) || !isValidItem(coin)) return coin;
        CoinPouchItemStackHandler handler = pouch.getItemHandler(coinHolder);
        handler.convertCoins();
        while (!coin.isEmpty()) {
            int before = coin.getCount();
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, coin, false);
            coin.setCount(remainder.getCount());
            boolean converted = handler.convertCoins();
            // An exchange can free slots for the rest of a large pickup.
            if (!converted || coin.getCount() == before) break;
        }
        return coin;
    }

    private static boolean isValidItem(ItemStack stack) {
        return stack.is(TolkienTags.Items.COINS);
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

    public static float getCoinCount(ItemStack stack) {
        ComponentItemHandler handler = new ComponentItemHandler(stack, TolkienDataComponents.ITEMSTACK_HANDLER.get(), CoinPouchContainer.SLOTS);
        int count = 0;
        int j;
        for (j = 0; j < handler.getSlots(); j++) {
            count+=handler.getStackInSlot(j).getCount();
        }
        return count / (float)(handler.getSlots() * 64);
    }
}