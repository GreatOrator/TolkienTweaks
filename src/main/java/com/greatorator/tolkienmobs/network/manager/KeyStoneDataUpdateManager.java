package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
import com.greatorator.tolkienmobs.util.block.KeyStoneCode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record KeyStoneDataUpdateManager(
        String keyCode
) implements CustomPacketPayload {
    public static final Type<KeyStoneDataUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "key_stone_data_packet"));

    @Override
    public Type<KeyStoneDataUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, KeyStoneDataUpdateManager> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, KeyStoneDataUpdateManager::keyCode,
            KeyStoneDataUpdateManager::new
    );

    public static void handle(final KeyStoneDataUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoKeyStoneContainer keyStoneContainer && keyStoneContainer.tileEntity instanceof CamoKeyStoneBlockEntity keyStoneBE) {
                keyStoneBE.setKeyStoneData(new KeyStoneCode(payload.keyCode()));
            }
        });
    }
}
