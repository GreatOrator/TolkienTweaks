package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
import com.greatorator.tolkienmobs.handler.interfaces.TolkienRegistry;
import com.greatorator.tolkienmobs.util.KeyStoneSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record KeyStoneSettingsUpdateManager(
        boolean keepKey
) implements CustomPacketPayload {
    public static final Type<KeyStoneSettingsUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "key_stone_setting_packet"));

    @Override
    public Type<KeyStoneSettingsUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, KeyStoneSettingsUpdateManager> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, KeyStoneSettingsUpdateManager::keepKey,
            KeyStoneSettingsUpdateManager::new
    );

    public static void handle(final KeyStoneSettingsUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoKeyStoneContainer keyStoneContainer && keyStoneContainer.tileEntity instanceof TolkienRegistry keyStoneBE) {
                keyStoneBE.setKeyStoneSettings(new KeyStoneSettings(payload.keepKey()));
            }
        });
    }
}
