package com.greatorator.tolkientweaks;

import com.greatorator.tolkientweaks.integration.IntegrationHelper;
import com.greatorator.tolkientweaks.network.common.NetworkHelper;
import com.greatorator.tolkientweaks.proxy.ClientProxy;
import com.greatorator.tolkientweaks.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Locale;

@Mod(TolkienTweaks.MODID)
public class TolkienTweaks {
    public static final Logger LOGGER = LogManager.getLogger("TolkienTweaks");

    public static final String MODID = "tolkientweaks";
    public static final String NAME = "Tolkien Tweaks";
    public static final String VERSION = "${mod_version}";
    public static TolkienTweaks instance;
    public NetworkHelper networkHelper;
    public static SimpleChannel NETWORK;
    private HashMap<String, Long> modifiedPlayerTimes;

    public static CommonProxy proxy;

    public TolkienTweaks() {
        instance = this;
        modifiedPlayerTimes = new HashMap<>();

        synchronized (MinecraftForge.EVENT_BUS) {
            Logger ttLog = LogManager.getLogger("tolkientweaks");
            Logger bcLog = LogManager.getLogger("brandonscore");
            LOGGER.info("Meeting of the Fellowship started! Waiting for the rest of the party to arrive...");
            if (IntegrationHelper.isTTInstalled) {
                ttLog.log(Level.INFO, "You shall have my axe!");
                bcLog.log(Level.INFO, "...and you shall have my bow!");
                LOGGER.info("Together we shall be the Fellowship of the Mods!");
            } else {
                ttLog.log(Level.INFO, "...");
                LOGGER.info("No party, no play!");
            }
        }

        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.construct();

        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        if (IntegrationHelper.isJEIInstalled) {
        }
    }

    public static TolkienTweaks instance() {
        return instance;
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.commonSetup(event);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        proxy.clientSetup(event);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }

    public HashMap<String, Long> getModifiedPlayerTimes() {
        return modifiedPlayerTimes;
    }
}
