package com.greatorator.tolkientweaks.proxy;

import com.greatorator.tolkientweaks.TolkienConfig;
import com.greatorator.tolkientweaks.TolkienContent;
import com.greatorator.tolkientweaks.integration.IntegrationHelper;
import com.greatorator.tolkientweaks.integration.curios.EquipmentManager;
import com.greatorator.tolkientweaks.item.CoinPouchItem;
import com.greatorator.tolkientweaks.util.TTTags;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxy {
    private Minecraft mc;
    private World lastWorld;

    public void construct() {
        registerEventListeners();
        TolkienConfig.load();
        TolkienContent.init();
        IntegrationHelper.init();
        EquipmentManager.initialize();
        TTTags.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();


    }

    public void commonSetup(FMLCommonSetupEvent event) {

    }

    public void clientSetup(FMLClientSetupEvent event) {
    }

    public void serverSetup(FMLDedicatedServerSetupEvent event) {

    }

    public void onAirPacket(int air) {

    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent tick) {
        if (mc.level == null || (mc.screen != null && mc.screen.isPauseScreen()))
            return;

        if (mc.level != lastWorld) {
            boolean newGame = lastWorld == null;
            lastWorld = mc.level;

            if (!newGame) {
            }
        }
    }

    public void registerEventListeners() {
        MinecraftForge.EVENT_BUS.addListener(CoinPouchItem::onItemPickup);
    }

    public PlayerEntity getPlayer() {
        return null;
    }
}
