package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LockedChestUpdateManager(BlockPos pos, String keyCode) implements CustomPacketPayload {
    public static final Type<LockedChestUpdateManager> TYPE =
            new Type<>(TolkienMobsMain.resLoc("chest_keycode"));

    public static final StreamCodec<FriendlyByteBuf, LockedChestUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, LockedChestUpdateManager::pos,
                    ByteBufCodecs.STRING_UTF8, LockedChestUpdateManager::keyCode,
                    LockedChestUpdateManager::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LockedChestUpdateManager message, IPayloadContext context) {
        var player = context.player();
        var level = player.level();
        var lockedChestToUpdate = level.getBlockEntity(message.pos);

        if (lockedChestToUpdate instanceof LockableChestBlockEntity entity) {
            LockableChestBlockEntity.setKeyCode(entity, message.keyCode);
        }
    }
}