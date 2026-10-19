package com.greatorator.tolkienmobs.datagen;

import com.google.common.hash.Hashing;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Derives the village-ready copies - houses with their porch and residents, town centres, roads and
 * villagers - when datagen runs; users keep editing the original house NBTs.
 */
public final class TolkienVillageTemplateProvider implements DataProvider {
    private final PackOutput output;

    private final TemplateSource templates;

    public TolkienVillageTemplateProvider(PackOutput output, ExistingFileHelper existingFiles) {
        this(output, location -> existingFiles.getResource(location, PackType.SERVER_DATA).open());
    }

    public TolkienVillageTemplateProvider(PackOutput output, TemplateSource templates) {
        this.output = output;
        this.templates = templates;
    }

    @FunctionalInterface
    public interface TemplateSource {
        InputStream open(ResourceLocation location) throws IOException;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (var village : TolkienVillageProvider.VILLAGES_BY_STYLE) {
                    write(cache, village.houseTemplate(), prepareHouse(village, templates));
                    // A village that lays this mod's path block starts from a copy of the town centre
                    // with the paths in its plaza swapped too, the sockets included.
                    if (village.rewiresStreets()) {
                        write(cache, TolkienVillageProvider.townCentre(village), prepareTownCentre(village, templates));
                    }
                    for (String name : TolkienVillageProvider.VILLAGERS) {
                        write(cache, TolkienVillageProvider.villagerTemplate(village, name), prepareVillager(village, name, templates));
                    }
                }
                for (String style : TolkienVillageProvider.ROAD_STYLES.keySet()) {
                    for (var street : TolkienVillageProvider.streets(style)) write(cache, street.template(), prepareRoad(street, templates));
                }
            } catch (IOException error) {
                throw new IllegalStateException("Cannot prepare village templates", error);
            }
        }, Util.backgroundExecutor());
    }

    public static CompoundTag prepareHouse(TolkienVillageProvider.Village village, TemplateSource templates) throws IOException {
        CompoundTag house = read(village.houseSource(), templates);
        // Add a one-block porch outside the original footprint, keeping the entire house intact.
        BlockPos shift = village.templateShift();
        for (var tag : house.getList("blocks", Tag.TAG_COMPOUND)) shiftList(((CompoundTag) tag).getList("pos", Tag.TAG_INT), shift, false);
        for (var tag : house.getList("entities", Tag.TAG_COMPOUND)) {
            shiftList(((CompoundTag) tag).getList("blockPos", Tag.TAG_INT), shift, false);
            shiftList(((CompoundTag) tag).getList("pos", Tag.TAG_DOUBLE), shift, true);
        }
        var size = house.getList("size", Tag.TAG_INT);
        int dimension = village.front().getAxis() == Direction.Axis.X ? 0 : 2;
        size.set(dimension, IntTag.valueOf(size.getInt(dimension) + 1));
        // Clear occupied flags captured while building the templates, so generated beds can be used.
        for (var tag : house.getList("palette", Tag.TAG_COMPOUND)) {
            var state = (CompoundTag) tag;
            if (state.getString("Name").endsWith("_bed")) state.getCompound("Properties").putString("occupied", "false");
        }
        // The house's own path blocks, and the porch in front of its door, are this mod's path
        // block in a village that lays one; the source templates still carry vanilla's grass path.
        if (village.laysStonePath()) layStonePath(house);
        putState(house, village.preparedEntrance().below(),
                village.laysStonePath() ? TolkienVillageProvider.stonePathBlock() : "minecraft:dirt_path");
        addConnector(house, village.preparedEntrance(), village.orientation(), "minecraft:building_entrance",
                "minecraft:empty", "minecraft:air");
        addConnector(house, village.residentFloor().offset(shift), "up_north", "minecraft:bottom",
                "minecraft:village/" + village.vanillaStyle() + "/villagers", null);
        return house;
    }

    /** Vanilla's villager template with the entity swapped for this village's own NPC. */
    public static CompoundTag prepareVillager(TolkienVillageProvider.Village village, String name,
                                              TemplateSource templates) throws IOException {
        CompoundTag villager = read(ResourceLocation.withDefaultNamespace(
                "village/" + village.vanillaStyle() + "/villagers/" + name), templates);
        for (var tag : villager.getList("entities", Tag.TAG_COMPOUND)) {
            ((CompoundTag) tag).getCompound("nbt").putString("id", village.resident().toString());
        }
        return villager;
    }

    private void write(CachedOutput cache, net.minecraft.resources.ResourceLocation name, CompoundTag tag) throws IOException {
        var bytes = new ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, bytes);
        byte[] content = bytes.toByteArray();
        cache.writeIfNeeded(output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(name.getNamespace() + "/structure/" + name.getPath() + ".nbt"), content, Hashing.sha1().hashBytes(content));
    }

    private static CompoundTag read(ResourceLocation source, TemplateSource templates) throws IOException {
        CompoundTag tag;
        try (var input = templates.open(source.withPrefix("structure/").withSuffix(".nbt"))) {
            if (input == null) throw new IOException("Missing template " + source);
            tag = NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
        }
        tag = DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), tag, tag.getInt("DataVersion"));
        tag.putInt("DataVersion", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        return tag;
    }

    private static void shiftList(ListTag coordinates, BlockPos shift, boolean floating) {
        for (int i = 0; i < 3; i++) {
            int offset = i == 0 ? shift.getX() : i == 2 ? shift.getZ() : 0;
            if (floating) coordinates.set(i, DoubleTag.valueOf(coordinates.getDouble(i) + offset));
            else coordinates.set(i, IntTag.valueOf(coordinates.getInt(i) + offset));
        }
    }

    /** The town centre as a village that lays its own path block builds it: its paths swapped. */
    public static CompoundTag prepareTownCentre(TolkienVillageProvider.Village village,
                                                TemplateSource templates) throws IOException {
        CompoundTag centre = read(TolkienVillageProvider.vanillaTownCentre(village), templates);
        layStonePath(centre);
        return centre;
    }

    /**
     * Swaps every path block in a template for this mod's own: the palette entries that the blocks
     * list points at, and the jigsaw sockets that write a path block once their neighbour connects.
     * A socket's final state is set straight into the world after the processors have run, so
     * renaming the palette alone would leave a vanilla path behind at each of them.
     */
    private static void layStonePath(CompoundTag template) {
        String stone = TolkienVillageProvider.stonePathBlock();
        for (var tag : template.getList("palette", Tag.TAG_COMPOUND)) {
            var state = (CompoundTag) tag;
            if (state.getString("Name").equals("minecraft:dirt_path")) state.putString("Name", stone);
        }
        for (var tag : template.getList("blocks", Tag.TAG_COMPOUND)) {
            var nbt = ((CompoundTag) tag).getCompound("nbt");
            if (nbt.getString("final_state").equals("minecraft:dirt_path")) nbt.putString("final_state", stone);
        }
    }

    public static CompoundTag prepareRoad(TolkienVillageProvider.Street street, TemplateSource templates) throws IOException {
        // The street surface is left as the vanilla template wrote it, because the road pool's own
        // processor is what lays the village's path block there - keeping the grass patches a tenth
        // of a plains street grows over and the plank bridge where one crosses water.
        CompoundTag road = read(street.source(), templates);
        var palette = road.getList("palette", Tag.TAG_COMPOUND);
        var blocks = road.getList("blocks", Tag.TAG_COMPOUND);
        var size = road.getList("size", Tag.TAG_INT);
        var entrances = new java.util.ArrayList<CompoundTag>();
        for (var tag : blocks) {
            var block = (CompoundTag) tag;
            if (block.getCompound("nbt").getString("name").equals("minecraft:building_entrance")) entrances.add(block.copy());
        }
        for (var entrance : entrances) {
            var coordinates = entrance.getList("pos", Tag.TAG_INT);
            BlockPos old = new BlockPos(coordinates.getInt(0), coordinates.getInt(1), coordinates.getInt(2));
            Direction facing = Direction.byName(palette.getCompound(entrance.getInt("state"))
                    .getCompound("Properties").getString("orientation").split("_")[0]);
            BlockPos edge = switch (facing) {
                case NORTH -> new BlockPos(old.getX(), old.getY(), 0);
                case SOUTH -> new BlockPos(old.getX(), old.getY(), size.getInt(2) - 1);
                case WEST -> new BlockPos(0, old.getY(), old.getZ());
                case EAST -> new BlockPos(size.getInt(0) - 1, old.getY(), old.getZ());
                default -> throw new IllegalArgumentException("Street entrance must face horizontally");
            };
            removeBlock(road, old);
            for (BlockPos step = old; ; step = step.relative(facing)) {
                // A short driveway connects the reserved road edge to the original path. The
                // driveway is this mod's own block in a village that lays one, and the vanilla
                // material otherwise: sandstone down from the desert streets, the dirt path the
                // plains ones use. It is written in rather than put through the street rules so
                // the strip at each house's door is solid, not mottled with grass.
                putState(road, step.below(), street.laysStonePath() ? TolkienVillageProvider.stonePathBlock()
                        : street.style().equals("desert") ? "minecraft:sandstone" : "minecraft:dirt_path");
                if (step.equals(edge)) break;
            }
            coordinates.set(0, IntTag.valueOf(edge.getX())); coordinates.set(2, IntTag.valueOf(edge.getZ()));
            removeBlock(road, edge);
            blocks.add(entrance);
        }
        return road;
    }

    private static void removeBlock(CompoundTag template, BlockPos position) {
        var blocks = template.getList("blocks", Tag.TAG_COMPOUND);
        blocks.removeIf(tag -> {
            var pos = ((CompoundTag) tag).getList("pos", Tag.TAG_INT);
            return pos.getInt(0) == position.getX() && pos.getInt(1) == position.getY() && pos.getInt(2) == position.getZ();
        });
    }

    private static void putState(CompoundTag template, BlockPos position, String name) {
        var palette = template.getList("palette", Tag.TAG_COMPOUND);
        int index = -1;
        for (int i = 0; i < palette.size(); i++) if (palette.getCompound(i).getString("Name").equals(name)) { index = i; break; }
        if (index == -1) {
            index = palette.size();
            CompoundTag state = new CompoundTag(); state.putString("Name", name); palette.add(state);
        }
        removeBlock(template, position);
        CompoundTag block = new CompoundTag();
        ListTag pos = new ListTag(); pos.add(IntTag.valueOf(position.getX())); pos.add(IntTag.valueOf(position.getY())); pos.add(IntTag.valueOf(position.getZ()));
        block.put("pos", pos); block.putInt("state", index);
        template.getList("blocks", Tag.TAG_COMPOUND).add(block);
    }

    private static void addConnector(CompoundTag house, BlockPos position, String orientation,
                                     String name, String pool, String finalState) {
        var size = house.getList("size", Tag.TAG_INT);
        if (position.getX() < 0 || position.getX() >= size.getInt(0) || position.getY() < 0
                || position.getY() >= size.getInt(1) || position.getZ() < 0 || position.getZ() >= size.getInt(2)) {
            throw new IllegalArgumentException("Connector outside house: " + position);
        }
        var palette = house.getList("palette", Tag.TAG_COMPOUND);
        var blocks = house.getList("blocks", Tag.TAG_COMPOUND);
        CompoundTag replaced = null;
        for (var tag : blocks) {
            var block = (CompoundTag) tag;
            var pos = block.getList("pos", Tag.TAG_INT);
            if (pos.getInt(0) == position.getX() && pos.getInt(1) == position.getY() && pos.getInt(2) == position.getZ()) {
                replaced = block;
                break;
            }
        }
        if (finalState == null) {
            if (replaced == null) throw new IllegalArgumentException("Resident connector needs an existing floor");
            var state = palette.getCompound(replaced.getInt("state"));
            var properties = state.getCompound("Properties");
            finalState = state.getString("Name") + (properties.isEmpty() ? "" : properties.getAllKeys().stream().sorted()
                    .map(key -> key + "=" + properties.getString(key)).collect(Collectors.joining(",", "[", "]")));
        }
        CompoundTag state = new CompoundTag();
        state.putString("Name", "minecraft:jigsaw");
        CompoundTag properties = new CompoundTag();
        properties.putString("orientation", orientation);
        state.put("Properties", properties);
        int stateIndex = palette.size();
        palette.add(state);
        if (replaced != null) blocks.remove(replaced);
        CompoundTag block = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(position.getX())); pos.add(IntTag.valueOf(position.getY())); pos.add(IntTag.valueOf(position.getZ()));
        block.put("pos", pos);
        block.putInt("state", stateIndex);
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", "minecraft:jigsaw");
        nbt.putString("name", name);
        nbt.putString("target", name);
        nbt.putString("pool", pool);
        nbt.putString("joint", "aligned");
        nbt.putString("final_state", finalState);
        block.put("nbt", nbt);
        blocks.add(block);
    }

    @Override
    public String getName() { return "Tolkienmobs - Village templates and connectors"; }
}
