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
import com.greatorator.tolkienmobs.faction.Carrock;
import com.greatorator.tolkienmobs.faction.GundabadGate;
import com.greatorator.tolkienmobs.faction.GundabadCave;
import com.greatorator.tolkienmobs.faction.GundabadKeep;
import com.greatorator.tolkienmobs.faction.OrcPieces;
import com.greatorator.tolkienmobs.faction.Stones;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.faction.Places;
import com.greatorator.tolkienmobs.faction.Hobbiton;
import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MilestoneHandler extends SavedData {
    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    private static final String SAVE_DATA_NAME = "milestone_data";
    private static final MilestoneHandler CLIENT_INSTANCE = new MilestoneHandler();

    private static final Map<UUID, MilestoneData> dataMap = new HashMap<>();

    /** How often, in ticks, a town's stone that could not be stood yet is asked for again. Ten seconds. */
    private static final int STONE_RETRY = 200;
    /** Whether both towns' stones are standing, so the clock stops asking for them. */
    private static boolean stonesDone;

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
        NeoForge.EVENT_BUS.addListener(MilestoneHandler::onServerTick);
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
        // The Gundabad gate stands at its own site in Arda rather than waiting for a page or a command: every world is
        // drawn with the pass, and the gate is part of the pass. One that is already standing is left exactly as it is.
        GundabadGate.autospawn(event.getServer());
        // The dwarven keep the pass was cut for, stood up at the end of the gate's chamber. The hall is carried north
        // to the keep's wall and cleared through where that wall will stand, so the gate goes first and the keep's own
        // doorway is then written whole over the clearing - laid the other way round, the hall opens the wall across
        // the chamber's width and the hold has no door. A keep already standing at its plan is left exactly as it is.
        GundabadKeep.autospawn(event.getServer());
        // The cave east of the gate, cut out of the shelf on the second mark the owner laid. It is rock rather than a
        // template, so it is cut rather than laid - and like the hold it is left alone once it stands.
        GundabadCave.autospawn(event.getServer());
        // The Carrock, the rock the owner shaped in the Anduin: a cut rather than a template, shipped in the jar and
        // stood at its own pin - 8307, 187, -4165, its low north-west corner - on the first start of a world. Nothing
        // there depends on anything else in this pass, so it goes last; a world that already notes the rock is left
        // exactly as it is.
        Carrock.autospawn(event.getServer());
        // The orc pieces standing at marks somebody walked out - the two ranks of towers and the huts on the plaza - go up
        // in the same pass, after the gate, because the towers stand on its own footprint. A piece that is whole is left
        // exactly as it is, so a world that has them is untouched.
        ServerLevel arda = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (arda != null) {
            // The hold's travel stone, on the mark the owner set into the gate's facade. Laid on every start rather
            // than with the gate, because a world whose gate has stood for weeks still wants its stone; a stone of the
            // same name already standing a short walk away is left alone.
            stonesDone = plantTownStones(arda, true);
            OrcPieces.raise(arda);
            // The towns this world is given rather than surveys: a board points at a town, so the ones that stand have
            // a name from the first start, whether or not a player has walked into any of them.
            Places.standTowns(arda);
        }
    }

    /**
     * Stands the stones the two towns are given, and says whether both are up.
     * <p>
     * Both are laid by the world rather than by a page, so both are asked for again on a clock until they stand: a stone
     * that is owed the moment the world starts is owed before the town it names has been raised - the Shire is raised by
     * its own pass minutes into a new world, and the hold's gate with it - and a stone planted over ground the town has
     * not laid yet is a stone buried under the town's own paving, or one that could not be stood at all. What the ground
     * will take is {@link Stones#stone}'s question; the Shire's stone also waits for the village's own note, so it is
     * only asked for once the village is complete.
     * <p>
     * {@code open} loads the two chunks the stones are in, which the start does and the clock does not: posting a stone
     * is not a reason to generate country every ten seconds. A stone already standing is answered and not stacked.
     */
    private static boolean plantTownStones(ServerLevel arda, boolean open) {
        BlockPos stone = ArdaMap.gundabadStone();
        BlockPos shireStone = ArdaMap.hobbitonMilestone();
        if (open) {
            arda.getChunk(stone.getX() >> 4, stone.getZ() >> 4);
            arda.getChunk(shireStone.getX() >> 4, shireStone.getZ() >> 4);
        } else if (!arda.hasChunkAt(stone) || !arda.hasChunkAt(shireStone)) {
            return false;
        }
        // Both standing: there is nothing left to ask for, and the village's note is not read again on the clock.
        if (stood(arda, stone) && stood(arda, shireStone)) return true;
        // The hold's stone, on the mark the owner set into the gate's facade.
        Stones.stone(arda, stone, "Gundabad", null);
        // The Shire's stone, at the paving the owner marked in the village. The mark here is gravel rather than one of
        // our ammolite blocks, so the stone is tried in the spot and then on top of it - which is the same stone either
        // way, and the paving is not dug up for it. The village's note comes first: the paving the stone is stood on is
        // part of what the village's pass lays, and a stone stood before that pass is a stone it buried.
        boolean shire = stood(arda, shireStone);
        if (!shire && Hobbiton.complete(arda)) {
            Stones.stone(arda, shireStone, "Hobbiton", null);
            if (!stood(arda, shireStone)) {
                Stones.stone(arda, shireStone.above(), "Hobbiton", null);
            }
            shire = stood(arda, shireStone);
            if (shire) {
                LOGGER.info("The Shire: the Hobbiton milestone stands at {}", shireStone.toShortString());
            } else if (open) {
                LOGGER.warn("The Shire: no milestone could be stood at {}; the ground there is {}",
                        shireStone.toShortString(), arda.getBlockState(shireStone));
            }
        }
        return shire && stood(arda, stone);
    }

    /** Whether a town's stone is standing in a spot or on top of it. */
    private static boolean stood(ServerLevel level, BlockPos spot) {
        return level.getBlockState(spot).is(TolkienBlocks.MILESTONE_BLOCK.get())
                || level.getBlockState(spot.above()).is(TolkienBlocks.MILESTONE_BLOCK.get());
    }

    /**
     * The clock that stands what a world is owed and could not stand at its start.
     * <p>
     * A town's stone is owed whether or not the town is up yet, and a stone refused by ground the town has not laid is
     * not refused for good. This asks again every ten seconds until both towns' stones are standing, and then stops.
     */
    public static void onServerTick(ServerTickEvent.Post event) {
        if (stonesDone || event.getServer().getTickCount() % STONE_RETRY != 0) return;
        ServerLevel arda = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (arda == null) return;
        stonesDone = plantTownStones(arda, false);
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

    public static void updateMilestone(MilestoneBlockEntity tile, @Nullable Player player) {
        if (tile.getLevel().isClientSide()) return;
        MilestoneHandler saveData = getInstance(tile.getLevel());
        if (saveData != null) {
            UUID uuid = tile.getUUID();
            MilestoneData data = dataMap.computeIfAbsent(uuid, e -> new MilestoneData(tile.getLevel().dimension(), tile.getBlockPos(), uuid));
            data.name = tile.getMilestoneSettings().name;
            data.distanceCost = tile.getMilestoneSettings().distance;
            data.dimensionCost = tile.getMilestoneSettings().dimension;


            // A stone laid by the world itself is registered before anybody is online to be sent the list, so the
            // packet is for a player rather than required: without it the hold's stone existed in the world and not in
            // the travel list until someone right-clicked it.
            if (player instanceof ServerPlayer online) {
                PacketHandler.sendMilestonesToClients(saveData, online);
            }
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

    /**
     * Whether a stone of this name already stands within a short walk of a point, whether or not this
     * player has touched it. The campaign plants its stones from a page rather than from a hand, and a
     * page is taken more than once - by a second player, or by the same one after a death - so the
     * question is about the world's list rather than about one player's.
     */
    public static boolean standsNear(Level world, String name, BlockPos pos, int radius) {
        if (getInstance(world) == null) return false;
        for (MilestoneData data : dataMap.values()) {
            if (name.equals(data.getName()) && data.getPos().closerThan(pos, radius)) return true;
        }
        return false;
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
