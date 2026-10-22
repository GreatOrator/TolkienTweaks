package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

/** Right-click opens the backpack from the hand; sneak + right-click on a block places it. */
public class BackpackItem extends BlockItem {
    public BackpackItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            open(serverPlayer, stack, hand);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.isSecondaryUseActive()) {
            return use(context.getLevel(), player, context.getHand());
        }
        return super.useOn(context);
    }

    /** Opens a backpack stack that isn't placed: from a hand, or from the Curios back slot when {@code hand} is null. */
    public static void open(ServerPlayer player, ItemStack stack, @Nullable InteractionHand hand) {
        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BackpackBlockContainer(id, inv, BackpackBlockEntity.createHeld(p, stack, hand)),
                stack.getHoverName()), buf -> {
            if (hand != null) {
                buf.writeByte(BackpackBlockContainer.SOURCE_HAND);
                buf.writeEnum(hand);
            } else {
                buf.writeByte(BackpackBlockContainer.SOURCE_CURIO);
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.tolkienmobs.backpack.controls").withStyle(ChatFormatting.GRAY));
    }
}
