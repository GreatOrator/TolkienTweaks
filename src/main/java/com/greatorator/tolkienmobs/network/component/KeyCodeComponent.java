package com.greatorator.tolkienmobs.network.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record KeyCodeComponent(String keyCode, int uses, boolean mode) {
    public static final Codec<KeyCodeComponent> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("key_code").forGetter(KeyCodeComponent::keyCode),
                    Codec.INT.fieldOf("key_uses").forGetter(KeyCodeComponent::uses),
                    Codec.BOOL.fieldOf("key_mode").forGetter(KeyCodeComponent::mode)
            ).apply(instance, KeyCodeComponent::new));

    public static final StreamCodec<FriendlyByteBuf, KeyCodeComponent> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, KeyCodeComponent::keyCode,
                    ByteBufCodecs.INT, KeyCodeComponent::uses,
                    ByteBufCodecs.BOOL, KeyCodeComponent::mode,
                    KeyCodeComponent::new);
}
