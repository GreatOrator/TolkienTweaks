package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LockedDoubleChestUpdateManager(BlockPos pos, String keyCode) implements CustomPacketPayload {
    public static final Type<LockedDoubleChestUpdateManager> TYPE =
            new Type<>(TolkienMobsMain.resLoc("double_chest_keycode"));

    public static final StreamCodec<FriendlyByteBuf, LockedDoubleChestUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, LockedDoubleChestUpdateManager::pos,
                    ByteBufCodecs.STRING_UTF8, LockedDoubleChestUpdateManager::keyCode,
                    LockedDoubleChestUpdateManager::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LockedDoubleChestUpdateManager message, IPayloadContext context) {
        var player = context.player();
        var level = player.level();
        var lockedChestToUpdate = level.getBlockEntity(message.pos);

        if (lockedChestToUpdate instanceof LockableDoubleChestBlockEntity entity) {
            LockableDoubleChestBlockEntity.setKeyCode(entity, message.keyCode);
        }
    }
}