package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** Right-click opens the backpack from the hand; sneak + right-click on a block places it. */
public class BackpackItem extends BlockItem {
    public BackpackItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive()) {
            return InteractionResultHolder.pass(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            openHeld(serverPlayer, hand);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.isSecondaryUseActive()) {
            return use(context.getLevel(), player, context.getHand()).getResult();
        }
        return super.useOn(context);
    }

    public static void openHeld(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BackpackBlockContainer(id, inv, BackpackBlockEntity.createHeld(p, hand)),
                stack.getHoverName()), buf -> {
            buf.writeBoolean(true);
            buf.writeEnum(hand);
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.tolkienmobs.backpack.controls").withStyle(ChatFormatting.GRAY));
    }
}
