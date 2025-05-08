package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.handler.data.MCDataInput;
import com.greatorator.tolkienmobs.handler.data.MCDataOutput;
import com.greatorator.tolkienmobs.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;
import java.util.*;

public class MilestoneHandler extends SavedData {
    private static final String SAVE_DATA_NAME = "milestone_data";
    private static final MilestoneHandler CLIENT_INSTANCE = new MilestoneHandler();

    private static final Map<UUID, MilestoneData> dataMap = new HashMap<>();

    public MilestoneHandler() {
        super();
    }

    public static SavedData.Factory<MilestoneHandler> factory() {
        return new SavedData.Factory<>(MilestoneHandler::new, (nbt, provider) -> MilestoneHandler.load(nbt));
    }

    public static void init() {
        NeoForge.EVENT_BUS.addListener(MilestoneHandler::onPlayerLogin);
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
        if (player.level.isClientSide) return;
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
        if (tile.getLevel().isClientSide) return;
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
        MilestoneHandler saveData = player.level.isClientSide ? CLIENT_INSTANCE : getInstance(player.level);
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
                return level.getDataStorage().computeIfAbsent(MilestoneHandler.factory(), SAVE_DATA_NAME);
            }
        }
        return null;
    }

    public static MilestoneHandler load(CompoundTag nbt) {
        dataMap.clear();
        MilestoneHandler saveData = new MilestoneHandler();
        ListTag list = nbt.getList("data", 10);
        for (Tag inbt : list) {
            MilestoneData data = MilestoneData.read((CompoundTag) inbt);
            dataMap.put(data.uuid, data);
        }
        return saveData;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        for (MilestoneData value : dataMap.values()) {
            list.add(value.write());
        }
        compoundTag.put("data", list);
        return compoundTag;
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

        private CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            nbt.putLong("milestonePos", pos.asLong());
            nbt.putUUID("milestoneUUID", uuid);
            nbt.putString("milestoneWorld", worldKey.location().toString());
            nbt.putString("milestoneTitle", name);
            nbt.putInt("milestoneCost", distanceCost);
            nbt.putInt("milestoneDimension", dimensionCost);
            ListTag list = new ListTag();
            players.forEach(e -> list.add(StringTag.valueOf(e.toString())));
            nbt.put("players", list);
            return nbt;
        }

        private static MilestoneData read(CompoundTag nbt) {
            BlockPos pos = BlockPos.of(nbt.getLong("milestonePos"));
            UUID uuid = nbt.getUUID("milestoneUUID");
            ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("milestoneWorld")));
            MilestoneData data = new MilestoneData(key, pos, uuid);
            data.name = nbt.getString("milestoneTitle");
            data.distanceCost = nbt.getInt("milestoneCost");
            data.dimensionCost = nbt.getInt("milestoneDimension");

            ListTag list = nbt.getList("players", 8);
            list.forEach(e -> data.players.add(UUID.fromString(e.getAsString())));
            return data;
        }

        private void serialize(MCDataOutput output) {
            output.writePos(pos);
            output.writeUUID(uuid);
            output.writeResourceLocation(worldKey.location());
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
