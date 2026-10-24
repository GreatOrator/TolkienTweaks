package com.greatorator.tolkienmobs.event.client;

import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.integration.TolkienSyncedRecipes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.common.NeoForge;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class TolkienClientEvents {

    @SubscribeEvent
    public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
        if(event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == TolkienItems.ELVEN_BOW.get()) {
            float fovModifier = 1f;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float deltaTicks = (float)ticksUsingItem / 20f;
            if(deltaTicks > 1f) {
                deltaTicks = 1f;
            } else {
                deltaTicks *= deltaTicks;
            }
            fovModifier *= 1f - deltaTicks * 0.15f;
            event.setNewFovModifier(fovModifier);
        }
        if(event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == TolkienItems.URUK_BOW.get()) {
            float fovModifier = 1f;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float deltaTicks = (float)ticksUsingItem / 20f;
            if(deltaTicks > 1f) {
                deltaTicks = 1f;
            } else {
                deltaTicks *= deltaTicks;
            }
            fovModifier *= 1f - deltaTicks * 0.15f;
            event.setNewFovModifier(fovModifier);
        }
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        TolkienSyncedRecipes.accept(event.getRecipeMap());
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        TolkienSyncedRecipes.clear();
    }

    public static void initGameEvents() {
        NeoForge.EVENT_BUS.addListener(FogHandler::renderFog);
        NeoForge.EVENT_BUS.addListener(FogHandler::computeFogColor);
        NeoForge.EVENT_BUS.addListener(FogHandler::unloadFog);
    }
}
