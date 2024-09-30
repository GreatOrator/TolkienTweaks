package com.greatorator.tolkienmobs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record KeyCodePayload(String templateName) implements CustomPacketPayload {
    public static final Type<KeyCodePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "update_key_code"));

    @Override
    public Type<KeyCodePayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, KeyCodePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            KeyCodePayload::templateName,
            KeyCodePayload::new
    );
}
