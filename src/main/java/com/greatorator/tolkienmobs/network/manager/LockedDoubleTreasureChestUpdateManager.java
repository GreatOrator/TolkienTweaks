package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleTreasureChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LockedDoubleTreasureChestUpdateManager(BlockPos pos, String keyCode) implements CustomPacketPayload {
    public static final Type<LockedDoubleTreasureChestUpdateManager> TYPE =
            new Type<>(TolkienMobsMain.resLoc("double_treasure_chest_keycode"));

    public static final StreamCodec<FriendlyByteBuf, LockedDoubleTreasureChestUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, LockedDoubleTreasureChestUpdateManager::pos,
                    ByteBufCodecs.STRING_UTF8, LockedDoubleTreasureChestUpdateManager::keyCode,
                    LockedDoubleTreasureChestUpdateManager::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LockedDoubleTreasureChestUpdateManager message, IPayloadContext context) {
        var player = context.player();
        var level = player.level();
        var lockedChestToUpdate = level.getBlockEntity(message.pos);

        if (lockedChestToUpdate instanceof LockableDoubleTreasureChestBlockEntity entity) {
            LockableDoubleTreasureChestBlockEntity.setKeyCode(entity, message.keyCode);
        }
    }
}