package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.handler.data.MCDataInput;
import com.greatorator.tolkienmobs.handler.data.MCDataOutput;
import com.greatorator.tolkienmobs.network.PacketHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import javax.annotation.Nullable;
import java.util.*;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MilestoneHandler extends SavedData {
    private static final String SAVE_DATA_NAME = "milestone_data";
    private static final MilestoneHandler CLIENT_INSTANCE = new MilestoneHandler();

    private static final Map<UUID, MilestoneData> dataMap = new HashMap<>();

    /**
     * The entries are the whole of this saved data, so the codec is a list of them and nothing else.
     * 26.1 writes a data type's codec output under the file's {@code data} key, which is the key and
     * the list shape a world's milestone file already used.
     */
    public static final Codec<MilestoneHandler> CODEC = MilestoneData.CODEC.listOf()
            .xmap(MilestoneHandler::fromMilestones, MilestoneHandler::toMilestones);

    public static final SavedDataType<MilestoneHandler> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(MODID, SAVE_DATA_NAME), MilestoneHandler::new, CODEC);

    public MilestoneHandler() {
        super();
    }

    public static void init() {
        NeoForge.EVENT_BUS.addListener(MilestoneHandler::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(MilestoneHandler::onServerStarted);
    }

    /**
     * Reads the world's file as the server finishes starting. A saved data is normally decoded lazily, on
     * the first lookup, and decoding drops whatever is in the map - so a milestone placed, renamed or
     * broken before that first lookup would be thrown away by the read that came after it. Reading it
     * here means every change afterwards goes into the map the world actually holds.
     */
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel overworld = event.getServer().getLevel(Level.OVERWORLD);
        if (overworld != null) {
            getInstance(overworld);
        }
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        MilestoneHandler saveData = getInstance(player.level);
        if (saveData != null) {
            PacketHandler.sendMilestonesToClients(saveData, player);
        }
    }

    // These are the methods you will use

    public static void addPlayerToMilestone(MilestoneBlockEntity tile, Player player) {
        if (player.level.isClientSide()) return;
        MilestoneHandler saveData = getInstance(tile.getLevel());
        if (saveData != null) {
            updateMilestone(tile, player);
            UUID uuid = tile.getUUID();
            MilestoneData data = dataMap.get(uuid);
            if (!data.name.equals(tile.getMilestoneSettings().name)) {
                data.name = tile.getMilestoneSettings().name;
                saveData.setDirty();
                PacketHandler.sendMilestonesToClients(saveData, (ServerPlayer) player);
            }
            if (!data.players.contains(player.getUUID())) {
                data.players.add(player.getUUID());
                saveData.setDirty();
                PacketHandler.sendMilestonesToClients(saveData, (ServerPlayer) player);
            }
        }
    }

    public static void updateMilestone(MilestoneBlockEntity tile, Player player) {
        if (tile.getLevel().isClientSide()) return;
        MilestoneHandler saveData = getInstance(tile.getLevel());
        if (saveData != null) {
            UUID uuid = tile.getUUID();
            MilestoneData data = dataMap.computeIfAbsent(uuid, e -> new MilestoneData(tile.getLevel().dimension(), tile.getBlockPos(), uuid));
            data.name = tile.getMilestoneSettings().name;
            data.distanceCost = tile.getMilestoneSettings().distance;
            data.dimensionCost = tile.getMilestoneSettings().dimension;


            PacketHandler.sendMilestonesToClients(saveData, (ServerPlayer) player);
            saveData.setDirty();
        }
    }

    public static List<MilestoneData> getKnownByPlayer(Player player) {
        MilestoneHandler saveData = player.level.isClientSide() ? CLIENT_INSTANCE : getInstance(player.level);
        List<MilestoneData> known = new ArrayList<>();
        if (saveData != null) {
            for (MilestoneData data : dataMap.values()) {
                if (data.players.contains(player.getUUID())) {
                    known.add(data);
                }
            }
        }
        return known;
    }

    @Nullable
    public static MilestoneData getMilestoneData(Level world, UUID uuid) {
        MilestoneHandler saveData = getInstance(world);
        if (saveData != null && dataMap.containsKey(uuid)) {
            return dataMap.get(uuid);
        }
        return null;
    }

    public static void removeMilestone(MilestoneBlockEntity tile) {
        MilestoneHandler saveData = getInstance(tile.getLevel());
        UUID id = tile.getUUID();
        if (saveData != null && dataMap.containsKey(id)) {
            dataMap.remove(id);
            saveData.setDirty();
        }
    }

    public static boolean isKnownByClient(UUID tileUUID, UUID playerUUID) {
        return dataMap.containsKey(tileUUID) && dataMap.get(tileUUID).players.contains(playerUUID);
    }

    // Internal Methods

    @Nullable
    public static MilestoneHandler getInstance(Level world) {
        if (world instanceof ServerLevel && world.getServer() != null) {
            ServerLevel level = world.getServer().getLevel(Level.OVERWORLD);
            if (level != null) {
                return level.getDataStorage().computeIfAbsent(TYPE);
            }
        }
        return null;
    }

    /**
     * The map is static, so decoding fills it and encoding reads it; the handler instance itself
     * carries no state. Anything already in the map is dropped first, which is what loading a world
     * should do - and it is read once, as the server starts, before anything can write.
     */
    private static MilestoneHandler fromMilestones(List<MilestoneData> milestones) {
        dataMap.clear();
        for (MilestoneData data : milestones) {
            dataMap.put(data.uuid, data);
        }
        return new MilestoneHandler();
    }

    private static List<MilestoneData> toMilestones(MilestoneHandler handler) {
        return new ArrayList<>(dataMap.values());
    }

    public void serialize(MCDataOutput output) {
        output.writeVarInt(dataMap.size());
        for (MilestoneData data : dataMap.values()) {
            data.serialize(output);
        }
    }

    public static void deserialize(MCDataInput input) {
        CLIENT_INSTANCE.dataMap.clear();
        int count = input.readVarInt();
        for (int i = 0; i < count; i++) {
            MilestoneData data = MilestoneData.deserialize(input);
            CLIENT_INSTANCE.dataMap.put(data.uuid, data);
        }
    }

    public static class MilestoneData {
        private final BlockPos pos;
        private final UUID uuid;
        private final ResourceKey<Level> worldKey;
        private final List<UUID> players = new ArrayList<>();

        private String name;
        private int distanceCost = 0;
        private int dimensionCost = 0;

        public MilestoneData(ResourceKey<Level> worldKey, BlockPos pos, UUID uuid) {
            this.pos = pos;
            this.uuid = uuid;
            this.worldKey = worldKey;
        }

        private MilestoneData(BlockPos pos, UUID uuid, ResourceKey<Level> worldKey, String name, int distanceCost, int dimensionCost, List<UUID> players) {
            this(worldKey, pos, uuid);
            this.name = name;
            this.distanceCost = distanceCost;
            this.dimensionCost = dimensionCost;
            this.players.addAll(players);
        }

        public BlockPos getPos() {
            return pos;
        }

        public UUID getUuid() {
            return uuid;
        }

        public ResourceKey<Level> getWorldKey() {
            return worldKey;
        }

        public List<UUID> getPlayers() {
            return players;
        }

        public String getName() {
            return name;
        }

        public int getDistanceCost() {
            return distanceCost;
        }

        public int getDimensionCost() {
            return dimensionCost;
        }

        /**
         * The keys and their shapes are the ones the hand-written NBT used: the position as a packed
         * long, the milestone's own id as the int-array UUID, the dimension and the player list as
         * strings. The name and the two costs are optional so a partial file still loads.
         */
        public static final Codec<MilestoneData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.xmap(BlockPos::of, BlockPos::asLong).fieldOf("milestonePos").forGetter(data -> data.pos),
                UUIDUtil.CODEC.fieldOf("milestoneUUID").forGetter(data -> data.uuid),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("milestoneWorld").forGetter(data -> data.worldKey),
                Codec.STRING.optionalFieldOf("milestoneTitle", "").forGetter(data -> data.name == null ? "" : data.name),
                Codec.INT.optionalFieldOf("milestoneCost", 0).forGetter(data -> data.distanceCost),
                Codec.INT.optionalFieldOf("milestoneDimension", 0).forGetter(data -> data.dimensionCost),
                UUIDUtil.STRING_CODEC.listOf().optionalFieldOf("players", List.of()).forGetter(data -> data.players)
        ).apply(instance, MilestoneData::new));

        private void serialize(MCDataOutput output) {
            output.writePos(pos);
            output.writeUUID(uuid);
            output.writeResourceLocation(worldKey.identifier());
            output.writeString(name);
            output.writeVarInt(distanceCost);
            output.writeVarInt(dimensionCost);

            output.writeVarInt(players.size());
            for (UUID player : players) {
                output.writeUUID(player);
            }
        }

        public static MilestoneData deserialize(MCDataInput input) {
            BlockPos pos = input.readPos();
            UUID uuid = input.readUUID();
            ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, input.readResourceLocation());
            MilestoneData data = new MilestoneData(key, pos, uuid);
            data.name = input.readString();
            data.distanceCost = input.readVarInt();
            data.dimensionCost = input.readVarInt();

            int count = input.readVarInt();
            for (int i = 0; i < count; i++) {
                data.players.add(input.readUUID());
            }
            return data;
        }
    }
}
