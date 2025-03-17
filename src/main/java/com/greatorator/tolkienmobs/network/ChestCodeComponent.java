package com.greatorator.tolkienmobs.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ChestCodeComponent(String keyCode) {
    public static final Codec<ChestCodeComponent> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("key_code").forGetter(ChestCodeComponent::keyCode)
            ).apply(instance, ChestCodeComponent::new));

    public static final StreamCodec<FriendlyByteBuf, ChestCodeComponent> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ChestCodeComponent::keyCode,
                    ChestCodeComponent::new);
}
