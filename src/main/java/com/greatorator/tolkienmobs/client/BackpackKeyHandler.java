package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.network.manager.BackpackMagnetToggleManager;
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
    /** Flips the magnet of the worn or carried backpack without opening it. */
    public static final KeyMapping TOGGLE_MAGNET = new KeyMapping("key.tolkienmobs.toggle_magnet", InputConstants.KEY_M, CATEGORY);
    /** Opens the ledger of alliances: where the player stands with the West, the East and Harad. */
    public static final KeyMapping FACTION_LEDGER = new KeyMapping("key.tolkienmobs.faction_ledger", InputConstants.KEY_J, CATEGORY);

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class Registration {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.registerCategory(CATEGORY);
            event.register(OPEN_BACKPACK);
            event.register(TOGGLE_MAGNET);
            event.register(FACTION_LEDGER);
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
            // The magnet key works with nothing open, and also while the backpack's own screen is up,
            // which is where the button that follows it lives.
            while (TOGGLE_MAGNET.consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    ClientPacketDistributor.sendToServer(new BackpackMagnetToggleManager());
                }
            }
            // The ledger opens from the world only: with a screen already up the key would be typing,
            // and a player reading their inventory does not want a ledger thrown over it.
            while (FACTION_LEDGER.consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null && minecraft.screen == null) {
                    minecraft.setScreen(new FactionLedgerScreen());
                }
            }
        }
    }
}
