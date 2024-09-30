package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.containers.KeyCodeContainer;
import com.greatorator.tolkienmobs.handler.TolkienDataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketUpdateManager {
    public static final PacketUpdateManager INSTANCE = new PacketUpdateManager();

    public static PacketUpdateManager get() {
        return INSTANCE;
    }

    public void handle(final KeyCodePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            AbstractContainerMenu container = player.containerMenu;

            if (container == null || !(container instanceof KeyCodeContainer))
                return;

            KeyCodeContainer.setCode(TolkienDataComponents.KEY_CODE, payload.templateName());
        });
    }
}
