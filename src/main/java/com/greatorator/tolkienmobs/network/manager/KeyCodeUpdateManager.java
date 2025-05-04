package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KeyCodeUpdateManager(InteractionHand hand, String keyCode, int keyUses, boolean mode) implements CustomPacketPayload {
    public static final Type<KeyCodeUpdateManager> TYPE =
            new Type<>(TolkienMobsMain.resLoc("edit_keycode"));

    public static final StreamCodec<FriendlyByteBuf, KeyCodeUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    NeoForgeStreamCodecs.enumCodec(InteractionHand.class), KeyCodeUpdateManager::hand,
                    ByteBufCodecs.STRING_UTF8, KeyCodeUpdateManager::keyCode,
                    ByteBufCodecs.INT, KeyCodeUpdateManager::keyUses,
                    ByteBufCodecs.BOOL, KeyCodeUpdateManager::mode,
                    KeyCodeUpdateManager::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KeyCodeUpdateManager message, IPayloadContext context) {
        var player = context.player();
        var itemStackToUpdate = player.getItemInHand(message.hand);

        if (itemStackToUpdate.getItem() instanceof KeyItem) {
            KeyItem.setKeyCode(itemStackToUpdate, message.keyCode, message.keyUses, message.mode);
        }
    }
}