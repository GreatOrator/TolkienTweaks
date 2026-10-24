package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoSpawnerContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record SpawnerMobCountUpdateManager(String entityId, int spawnCount) implements CustomPacketPayload {
    public static final Type<SpawnerMobCountUpdateManager> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "spawner_mob_count_packet"));

    @Override
    public Type<SpawnerMobCountUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SpawnerMobCountUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, SpawnerMobCountUpdateManager::entityId,
                    ByteBufCodecs.VAR_INT, SpawnerMobCountUpdateManager::spawnCount,
                    SpawnerMobCountUpdateManager::new);

    public static void handle(final SpawnerMobCountUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoSpawnerContainer spawnerContainer && spawnerContainer.tileEntity instanceof CamoSpawnerBlockEntity spawnerBE) {
                for (com.greatorator.tolkienmobs.util.block.MobSpawnData mobData : spawnerBE.mobSpawnDataList) {
                    if (mobData.getEntityId().equals(payload.entityId())) {
                        mobData.setSpawnCount(payload.spawnCount());
                        spawnerBE.setChanged();
                        break;
                    }
                }
            }
        });
    }
}
