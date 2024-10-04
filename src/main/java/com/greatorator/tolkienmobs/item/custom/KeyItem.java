package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.containers.CoinPouchContainer;
import com.greatorator.tolkienmobs.containers.KeyCodeContainer;
import com.greatorator.tolkienmobs.containers.KeyItemContainer;
import com.greatorator.tolkienmobs.containers.handlers.CoinPouchItemStackHandler;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.TolkienItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class KeyItem extends TolkienItem {
    private KeyItem keyStack;

    public KeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.get(TolkienDataComponents.KEY_CODE) == null) {
            itemstack.set(TolkienDataComponents.KEY_CODE, "No Code Set");
        }
        this.getDefaultInstance();

        if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);

        if (Screen.hasShiftDown() && player.isCreative()) {
            player.openMenu(new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) -> new KeyCodeContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs.code" + itemstack.getDescriptionId())), (buf -> {
                        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
            }));
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        }

        player.openMenu(new SimpleMenuProvider(
                (windowId, playerInventory, playerEntity) -> new KeyItemContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs." + itemstack.getDescriptionId())), (buf -> {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
        }));

        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

    public CoinPouchItemStackHandler getItemHandler(ItemStack stack) {
        return new CoinPouchItemStackHandler(CoinPouchContainer.SLOTS, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(getDescriptionId() + ".lore2"));
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable(getDescriptionId() + ".shift_down"));
            }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = super.getDefaultInstance();
        itemstack.set(TolkienDataComponents.KEY_CODE, "No Code Set");
        return itemstack;
    }
}
