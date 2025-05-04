package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.containers.KeyCodeContainer;
import com.greatorator.tolkienmobs.containers.KeyItemContainer;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.TolkienItem;
import com.greatorator.tolkienmobs.network.component.KeyCodeComponent;
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

        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    }

//    @Override
//    public InteractionResult useOn(UseOnContext context) {
//        Level level = context.getLevel();
//        BlockEntity entity = level.getBlockEntity(context.getClickedPos());
//        Player player = context.getPlayer();
//        ItemStack itemstack = context.getItemInHand();
//        int uses = getUses(itemstack);
//        boolean mode = getMode(itemstack);
//
//        if (entity instanceof LockableChestBlockEntity || entity instanceof LockableTreasureChestBlockEntity || entity instanceof LockableDoubleChestBlockEntity || entity instanceof LockableDoubleTreasureChestBlockEntity) {
//            if (getKeyCode(context.getItemInHand()).equals(entity.getData(TolkienDataComponents.CHEST_CODE))) {
//                if (mode) {
//                    if (uses >= 0) {
//                        level.sendBlockUpdated(context.getClickedPos(), entity.getBlockState(), entity.getBlockState(), 3);
//
//                        if (uses == 0) {
//                            level.playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
//                            itemstack.shrink(1);
//                            player.sendSystemMessage(Component.translatable("tolkienmobs.msg.key_used").withStyle(ChatFormatting.RED));
//                            return InteractionResult.CONSUME;
//                        }
//
//                        setKeyData(itemstack, getKeyCode(itemstack), uses - 1, getMode(itemstack));
//                    }
//                }
//                level.playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
//                player.openMenu(new SimpleMenuProvider(
//                        (windowId, playerInventory, playerEntity) -> new KeyItemContainer(windowId, playerInventory, player, itemstack), Component.translatable("screen.tolkienmobs." + itemstack.getDescriptionId())), (buf -> {
//                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemstack);
//                }));
//            }
//        } else if (entity instanceof CamoKeyStoneBlockEntity) {
//            player.sendSystemMessage(Component.translatable("tolkienmobs.msg.key_used").withStyle(ChatFormatting.RED));
//            return InteractionResult.SUCCESS;
//    }
//        level.playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
//          player.sendSystemMessage(Component.translatable("tolkienmobs.msg.wrong_key").withStyle(ChatFormatting.RED));
//        return InteractionResult.SUCCESS;
//    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(getDescriptionId() + ".lore2"));
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable(getDescriptionId() + ".shift_down"));
            }
            if (getUses(stack) > 0) {
                tooltipComponents.add(Component.translatable("Number of uses left: " + getUses(stack)));
            }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static void setKeyCode(ItemStack keyItem, String keyCode, int keyUses, boolean mode) {
        if (keyItem.isEmpty() || !(keyItem.getItem() instanceof KeyItem))
            return;
        if (keyCode.length() > 50)
            return;
        if (keyUses <= -1)
            return;
        keyItem.set(TolkienDataComponents.KEY_CODE, new KeyCodeComponent(keyCode, keyUses, mode));
    }

    public static boolean setKeyData(ItemStack keyItem, String keyCode, int keyUses, boolean keyMode) {
        if (keyItem.isEmpty() || !(keyItem.getItem() instanceof KeyItem))
            return false;
        if (keyCode.length() > 50)
            return false;
        if (keyUses < -1)
            return false;

        keyItem.set(TolkienDataComponents.KEY_CODE, new KeyCodeComponent(keyCode, keyUses, keyMode));
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
            return 20;
        if (!key.has(TolkienDataComponents.KEY_CODE))
            return 20;
        var keyData = key.get(TolkienDataComponents.KEY_CODE);
        return keyData.uses();
    }

    public static boolean getMode(ItemStack key) {
        var keyData = key.get(TolkienDataComponents.KEY_CODE);
        if (!key.has(TolkienDataComponents.KEY_CODE))
            return false;
        assert keyData != null;
        return keyData.mode();
    }
}
