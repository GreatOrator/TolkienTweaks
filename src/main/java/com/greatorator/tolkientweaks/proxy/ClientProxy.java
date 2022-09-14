package com.greatorator.tolkientweaks.proxy;

import com.greatorator.tolkientweaks.client.gui.CoinPouchScreen;
import com.greatorator.tolkientweaks.item.CoinPouchItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

import static com.greatorator.tolkientweaks.TolkienContent.COIN_POUCH;
import static com.greatorator.tolkientweaks.TolkienContent.COIN_POUCH_CONTAINER;

public class ClientProxy extends CommonProxy {

    @Override
    public void construct() {
        super.construct();
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
        RenderType cutoutMipped = RenderType.cutoutMipped();
        RenderType translucent = RenderType.translucent();

        // Blocks

        // GUIs
        ScreenManager.register(COIN_POUCH_CONTAINER, CoinPouchScreen::new);
    }

    //#################################################################
    // Render Registry
    //#################################################################
    public static void registerPropertyOverride() {
        ItemModelsProperties.register(COIN_POUCH.get(), new ResourceLocation("fullness"), CoinPouchItem::getFullnessPropertyOverride);
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