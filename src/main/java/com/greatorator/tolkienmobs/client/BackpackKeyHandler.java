package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.network.manager.OpenBackpackCurioManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BackpackKeyHandler {
    /** 26.1 keys belong to a category object rather than a free-form category string. */
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MODID, "general"));
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.tolkienmobs.open_backpack", InputConstants.KEY_B, CATEGORY);

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class Registration {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.registerCategory(CATEGORY);
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
                    ClientPacketDistributor.sendToServer(new OpenBackpackCurioManager());
                }
            }
        }
    }
}
