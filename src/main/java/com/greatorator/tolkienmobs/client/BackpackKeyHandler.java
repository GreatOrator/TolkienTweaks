package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.network.manager.OpenBackpackCurioManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BackpackKeyHandler {
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.tolkienmobs.open_backpack", InputConstants.KEY_B, "key.categories.tolkienmobs");

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class Registration {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OPEN_BACKPACK);
        }
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class Input {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            while (OPEN_BACKPACK.consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null && minecraft.screen == null && ModList.get().isLoaded("curios")) {
                    PacketDistributor.sendToServer(new OpenBackpackCurioManager());
                }
            }
        }
    }
}
