package com.greatorator.tolkientweaks.proxy;

import com.greatorator.tolkientweaks.TolkienContent;
import com.greatorator.tolkientweaks.client.gui.KeyRingScreen;
import com.greatorator.tolkientweaks.event.client.ClientEvents;
import com.greatorator.tolkientweaks.item.KeyRingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

import static com.greatorator.tolkientweaks.TolkienContent.KEY_RING;
import static com.greatorator.tolkientweaks.TolkienContent.KEY_RING_CONTAINER;

public class ClientProxy extends CommonProxy {
    @Override
    public void construct() {
        super.construct();
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onModelBakeEvent);
    }

    @Override
    public void commonSetup(FMLCommonSetupEvent event) {
        super.commonSetup(event);
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        super.clientSetup(event);
        setupRenderLayers();
        registerPropertyOverride();
        registerEntityRenderer();
        registerTileRenderers();
    }

    public static void setupRenderLayers() {
        RenderType cutout = RenderType.cutout();
        RenderType solid = RenderType.solid();
        RenderType cutoutMipped = RenderType.cutoutMipped();
        RenderType translucent = RenderType.translucent();

        // Blocks
        RenderTypeLookup.setRenderLayer(TolkienContent.CHAMELEON_BLOCK.get(), solid);

        // GUIs
        ScreenManager.register(KEY_RING_CONTAINER, KeyRingScreen::new);
//        ScreenManager.register(BRONZE_KEY_CONTAINER, BronzeKeyAccessScreen::new);
//        ScreenManager.register(SILVER_KEY_CONTAINER, KeyRingScreen::new);
//        ScreenManager.register(GOLD_KEY_CONTAINER, KeyRingScreen::new);
//        ScreenManager.register(MITHRIL_KEY_CONTAINER, KeyRingScreen::new);
//        ScreenManager.register(MASTER_KEY_CONTAINER, KeyRingScreen::new);
    }

    //#################################################################
    // Render Registry
    //#################################################################
    public static void registerPropertyOverride() {
        ItemModelsProperties.register(KEY_RING.get(), new ResourceLocation("fullness"), KeyRingItem::getFullnessPropertyOverride);
    }

    public static void registerEntityRenderer() {
//        RenderingRegistry.registerEntityRenderingHandler(EntityGenerator.AMMO_BOULDER.get(), new RenderBoulder.RenderFactory());
    }

    private void registerTileRenderers() {
//        ClientRegistry.bindTileEntityRenderer(TTMContent.TMFIREPLACE_TILE.get(), RenderFireplaceTile::new);
    }

    @Override
    public void serverSetup(FMLDedicatedServerSetupEvent event) {
        super.serverSetup(event);
    }

    @Override
    public PlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }
}