package com.greatorator.tolkienmobs.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record KeyCodeComponent(String keyCode, int uses) {
    public static final Codec<KeyCodeComponent> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("key_code").forGetter(KeyCodeComponent::keyCode),
                    Codec.INT.fieldOf("key_uses").forGetter(KeyCodeComponent::uses)
            ).apply(instance, KeyCodeComponent::new));

    public static final StreamCodec<FriendlyByteBuf, KeyCodeComponent> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, KeyCodeComponent::keyCode,
                    ByteBufCodecs.INT, KeyCodeComponent::uses,
                    KeyCodeComponent::new);
}
