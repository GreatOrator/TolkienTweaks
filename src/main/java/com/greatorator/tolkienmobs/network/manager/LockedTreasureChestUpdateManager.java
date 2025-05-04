package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LockableTreasureChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LockedTreasureChestUpdateManager(BlockPos pos, String keyCode) implements CustomPacketPayload {
    public static final Type<LockedTreasureChestUpdateManager> TYPE =
            new Type<>(TolkienMobsMain.resLoc("treasure_chest_keycode"));

    public static final StreamCodec<FriendlyByteBuf, LockedTreasureChestUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, LockedTreasureChestUpdateManager::pos,
                    ByteBufCodecs.STRING_UTF8, LockedTreasureChestUpdateManager::keyCode,
                    LockedTreasureChestUpdateManager::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LockedTreasureChestUpdateManager message, IPayloadContext context) {
        var player = context.player();
        var level = player.level();
        var lockedChestToUpdate = level.getBlockEntity(message.pos);

        if (lockedChestToUpdate instanceof LockableTreasureChestBlockEntity entity) {
            LockableTreasureChestBlockEntity.setKeyCode(entity, message.keyCode);
        }
    }
}