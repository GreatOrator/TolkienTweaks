package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.containers.KeyCodeContainer;
import com.greatorator.tolkienmobs.containers.KeyItemContainer;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.TolkienItem;
import com.greatorator.tolkienmobs.network.KeyCodeComponent;
import net.minecraft.ChatFormatting;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class KeyItem extends TolkienItem {

    public KeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);

        if (Screen.hasShiftDown() && player.isCreative()) {
            player.openMenu(new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) -> new KeyCodeContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs.code" + itemstack.getDescriptionId())), (buf -> {
                        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
            }));
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        }
        if (player.isCreative()) {
            player.openMenu(new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) -> new KeyItemContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs." + itemstack.getDescriptionId())), (buf -> {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
            }));
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        }else if (!getKeyCode(itemstack).isEmpty()){
            player.openMenu(new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) -> new KeyItemContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs." + itemstack.getDescriptionId())), (buf -> {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
            }));
        }
        player.sendSystemMessage(Component.translatable(MODID + ".msg.wrong_key").withStyle(ChatFormatting.RED));

        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(getDescriptionId() + ".lore2"));
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable(getDescriptionId() + ".shift_down"));
            }
            if (getUses(stack) > 0) {
                tooltipComponents.add(Component.translatable(getDescriptionId() + "Number of uses left: " + getUses(stack)));
            }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static void setKeyCode(ItemStack keyItem, String keyCode, int keyUses) {
        if (keyItem.isEmpty() || !(keyItem.getItem() instanceof KeyItem))
            return;
        if (keyCode.length() > 50)
            return;
        if (keyUses < -1)
            return;
        keyItem.set(TolkienDataComponents.KEY_CODE, new KeyCodeComponent(keyCode, keyUses));
    }

    public static boolean setKeyData(ItemStack keyItem, String keyCode, int keyUses) {
        if (keyItem.isEmpty() || !(keyItem.getItem() instanceof KeyItem))
            return false;
        if (keyCode.length() > 50)
            return false;
        if (keyUses < -1)
            return false;
        keyItem.set(TolkienDataComponents.KEY_CODE, new KeyCodeComponent(keyCode, keyUses));
        return true;
    }

    public static String getKeyCode(ItemStack key) {
        if (key.isEmpty() || !(key.getItem() instanceof KeyItem))
            return "";
        if (!key.has(TolkienDataComponents.KEY_CODE))
            return "";
        var keyData = key.get(TolkienDataComponents.KEY_CODE);
        return keyData.keyCode();
    }

    public static int getUses(ItemStack key) {
        if (key.isEmpty() || !(key.getItem() instanceof KeyItem))
            return -1;
        if (!key.has(TolkienDataComponents.KEY_CODE))
            return -1;
        var keyData = key.get(TolkienDataComponents.KEY_CODE);
        return keyData.uses();
    }
}
