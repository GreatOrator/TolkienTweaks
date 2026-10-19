package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoSpawnerContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record SpawnerClearUpdateManager(String entityId, boolean clearAll) implements CustomPacketPayload {
    public static final Type<SpawnerClearUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "spawner_clear_packet"));

    @Override
    public Type<SpawnerClearUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SpawnerClearUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, SpawnerClearUpdateManager::entityId,
                    ByteBufCodecs.BOOL, SpawnerClearUpdateManager::clearAll,
                    SpawnerClearUpdateManager::new);

    public static void handle(final SpawnerClearUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoSpawnerContainer spawnerContainer && spawnerContainer.tileEntity instanceof CamoSpawnerBlockEntity spawnerBE) {
                if (payload.clearAll()) {
                    spawnerBE.mobSpawnDataList.clear();
                    spawnerBE.getSpawnerBase().clearSpawnData();
                } else {
                    spawnerBE.mobSpawnDataList.removeIf(mob -> mob.getEntityId().equals(payload.entityId()));
                }
                spawnerBE.setChanged();
            }
        });
    }
}
