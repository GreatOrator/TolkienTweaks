package com.greatorator.tolkienmobs.network.config;

import com.google.common.base.Joiner;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import com.greatorator.tolkienmobs.util.CrashLock;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.network.PacketHandler.C_CONFIG_SYNC;
import static com.greatorator.tolkienmobs.network.PacketHandler.NET_CHANNEL;

public class ConfigSyncManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    private static final Map<ResourceLocation, ConfigTag> SYNC_MAP = new HashMap<>();

    public static void registerSync(ResourceLocation key, ConfigTag tag) {
        ConfigTag prev = SYNC_MAP.put(key, tag);
        if (prev != null) {
            throw new IllegalArgumentException("Key '" + key + "' already registered.");
        }
    }

    @ApiStatus.Internal
    public static void init(IEventBus modBus) {
        LOCK.lock();

        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(ConfigSyncManager::onClientDisconnected);
        }

        modBus.addListener(ConfigSyncManager::onGameConfigurationEvent);
    }

    @ApiStatus.Internal
    public static void readSyncPacket(PacketCustom packet) {
        int numPackets = packet.readVarInt();
        for (int i = 0; i < numPackets; i++) {
            ResourceLocation ident = packet.readResourceLocation();
            LOGGER.info("Applying config sync for {}.", ident);
            ConfigTag config = SYNC_MAP.get(ident);
            if (config == null) {
                LOGGER.fatal("Client is missing sync tag: {}. Potentially skipped other configs!", ident);
                return;
            }
            config.read(packet);
            config.runSync(ConfigCallback.Reason.SYNC);
        }
    }

    private static void onClientDisconnected(ClientPlayerNetworkEvent.LoggingOut event) {
        for (Map.Entry<ResourceLocation, ConfigTag> entry : SYNC_MAP.entrySet()) {
            LOGGER.info("Client disconnected, rolling back config for {}.", entry.getKey());
            ConfigTag config = entry.getValue();
            config.resetFromNetwork();
            config.runSync(ConfigCallback.Reason.ROLLBACK);
        }
    }

    private static void onGameConfigurationEvent(RegisterConfigurationTasksEvent event) {
        event.register(new ConfigSyncConfigurationTask(event.getListener()));
    }

    private record ConfigSyncConfigurationTask(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {

        private static final Type TYPE = new Type(ResourceLocation.fromNamespaceAndPath(MODID, "config_sync"));

        @Override
        public void run(Consumer<CustomPacketPayload> sender) {
            if (!SYNC_MAP.isEmpty()) {
                PacketCustom packet = new PacketCustom(NET_CHANNEL, C_CONFIG_SYNC, null);
                packet.writeVarInt(SYNC_MAP.size());
                for (Map.Entry<ResourceLocation, ConfigTag> entry : SYNC_MAP.entrySet()) {
                    packet.writeResourceLocation(entry.getKey());
                    entry.getValue().write(packet);
                }

                String mods = Joiner.on(", ").join(SYNC_MAP.keySet());
                LOGGER.info("Sending config sync packet for {} to connecting player.", mods);
                sender.accept(packet.toCustomPayload());
            }
            listener.finishCurrentTask(TYPE);
        }

        @Override
        public Type type() {
            return TYPE;
        }
    }
}
