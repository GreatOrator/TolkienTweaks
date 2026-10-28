package com.greatorator.tolkienmobs.datagen;

import com.google.common.hash.Hashing;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;

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

    /**
     * NeoForge 21.4 removed ExistingFileHelper; the templates are read straight out of the resource manager the
     * gather event exposes for the data pack.
     */
    public TolkienVillageTemplateProvider(PackOutput output, ResourceManager resources) {
        this(output, location -> resources.getResource(location)
                .orElseThrow(() -> new IOException("missing village template " + location))
                .open());
    }

    public TolkienVillageTemplateProvider(PackOutput output, TemplateSource templates) {
        this.output = output;
        this.templates = templates;
    }

    @FunctionalInterface
    public interface TemplateSource {
        InputStream open(Identifier location) throws IOException;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (var village : TolkienVillageProvider.VILLAGES_BY_STYLE) {
                    // A village draws on one plan or several; each is prepared against its own NBT.
                    for (var house : village.houses()) {
                        write(cache, village.houseTemplate(house), prepareHouse(village, house, templates));
                    }
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

    public static CompoundTag prepareHouse(TolkienVillageProvider.Village village, TolkienVillageProvider.House plan,
                                           TemplateSource templates) throws IOException {
        CompoundTag house = read(village.houseSource(plan), templates);
        // Add a one-block porch outside the original footprint, keeping the entire house intact.
        BlockPos shift = village.templateShift();
        for (Tag tag : list(house, "blocks")) shiftList(list((CompoundTag) tag, "pos"), shift, false);
        for (Tag tag : list(house, "entities")) {
            shiftList(list((CompoundTag) tag, "blockPos"), shift, false);
            shiftList(list((CompoundTag) tag, "pos"), shift, true);
        }
        ListTag size = list(house, "size");
        int dimension = village.front().getAxis() == Direction.Axis.X ? 0 : 2;
        size.set(dimension, IntTag.valueOf(size.getIntOr(dimension, 0) + 1));
        // Clear occupied flags captured while building the templates, so generated beds can be used.
        for (Tag tag : list(house, "palette")) {
            CompoundTag state = (CompoundTag) tag;
            if (string(state, "Name").endsWith("_bed")) {
                compound(state, "Properties").putString("occupied", "false");
            }
        }
        // The house's own path blocks, and the porch in front of its door, are this mod's path
        // block in a village that lays one; the source templates still carry vanilla's grass path.
        if (village.laysStonePath()) layStonePath(house);
        BlockPos entrance = village.preparedEntrance(plan);
        putState(house, entrance.below(),
                village.laysStonePath() ? TolkienVillageProvider.stonePathBlock() : "minecraft:dirt_path");
        addConnector(house, entrance, village.orientation(), "minecraft:building_entrance",
                "minecraft:empty", "minecraft:air");
        requireFrontEdge(village, plan, house, entrance);
        addConnector(house, plan.residentFloor().offset(shift), "up_north", "minecraft:bottom",
                "minecraft:village/" + village.vanillaStyle() + "/villagers", null);
        return house;
    }

    /**
     * A doorway marker only connects a house from the house's own front edge. One set inside the
     * footprint asks the street behind it to grow into the walls, which the game refuses, and it
     * drops the whole house rather than placing it: the village keeps its town centre and nothing
     * else. The size read here already carries the porch column, so an entrance coordinate copied
     * from a house of another depth or width fails the data run instead of a world.
     */
    private static void requireFrontEdge(TolkienVillageProvider.Village village, TolkienVillageProvider.House plan,
                                         CompoundTag house, BlockPos marker) {
        Direction front = village.front();
        int extent = list(house, "size").getIntOr(front.getAxis() == Direction.Axis.X ? 0 : 2, 0);
        int along = front.getAxis() == Direction.Axis.X ? marker.getX() : marker.getZ();
        int edge = front.getAxisDirection() == Direction.AxisDirection.POSITIVE ? extent - 1 : 0;
        if (along != edge) {
            throw new IllegalStateException("The " + village.style() + " village's " + plan.name() + " puts its doorway marker at "
                    + marker + ", column " + along + " of " + extent + " along " + front
                    + ", but the marker has to sit on the " + front + " edge, column " + edge
                    + ": an entrance coordinate is written against a house's own template, not against"
                    + " the template whose other numbers it borrows.");
        }
    }

    /** Vanilla's villager template with the entity swapped for this village's own NPC. */
    public static CompoundTag prepareVillager(TolkienVillageProvider.Village village, String name,
                                              TemplateSource templates) throws IOException {
        CompoundTag villager = read(Identifier.withDefaultNamespace(
                "village/" + village.vanillaStyle() + "/villagers/" + name), templates);
        for (Tag tag : list(villager, "entities")) {
            compound((CompoundTag) tag, "nbt").putString("id", village.resident().toString());
        }
        return villager;
    }

    /**
     * 26.1's {@code CompoundTag} reads hand back {@link java.util.Optional}s instead of a fallback value.
     * These keep the template editing below readable, and the one that must not be detached throws rather
     * than handing back a list whose edits would go nowhere: structure NBT always carries these.
     */
    private static ListTag list(CompoundTag tag, String name) {
        return tag.getList(name).orElseThrow(() -> new IllegalArgumentException("Template has no " + name + " list"));
    }

    private static String string(CompoundTag tag, String name) {
        return tag.getStringOr(name, "");
    }

    private static CompoundTag compound(CompoundTag tag, String name) {
        return tag.getCompoundOrEmpty(name);
    }

    private void write(CachedOutput cache, Identifier name, CompoundTag tag) throws IOException {
        var bytes = new ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, bytes);
        byte[] content = bytes.toByteArray();
        cache.writeIfNeeded(output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(name.getNamespace() + "/structure/" + name.getPath() + ".nbt"), content, Hashing.sha1().hashBytes(content));
    }

    private static CompoundTag read(Identifier source, TemplateSource templates) throws IOException {
        CompoundTag tag;
        try (var input = templates.open(source.withPrefix("structure/").withSuffix(".nbt"))) {
            if (input == null) throw new IOException("Missing template " + source);
            tag = NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
        }
        tag = DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), tag, tag.getIntOr("DataVersion", 0));
        tag.putInt("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());
        return tag;
    }

    private static void shiftList(ListTag coordinates, BlockPos shift, boolean floating) {
        for (int i = 0; i < 3; i++) {
            int offset = i == 0 ? shift.getX() : i == 2 ? shift.getZ() : 0;
            if (floating) coordinates.set(i, DoubleTag.valueOf(coordinates.getDoubleOr(i, 0) + offset));
            else coordinates.set(i, IntTag.valueOf(coordinates.getIntOr(i, 0) + offset));
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
        for (Tag tag : list(template, "palette")) {
            CompoundTag state = (CompoundTag) tag;
            if (string(state, "Name").equals("minecraft:dirt_path")) state.putString("Name", stone);
        }
        for (Tag tag : list(template, "blocks")) {
            CompoundTag nbt = compound((CompoundTag) tag, "nbt");
            if (string(nbt, "final_state").equals("minecraft:dirt_path")) nbt.putString("final_state", stone);
        }
    }

    public static CompoundTag prepareRoad(TolkienVillageProvider.Street street, TemplateSource templates) throws IOException {
        // The street surface is left as the vanilla template wrote it, because the road pool's own
        // processor is what lays the village's path block there - keeping the grass patches a tenth
        // of a plains street grows over and the plank bridge where one crosses water.
        CompoundTag road = read(street.source(), templates);
        ListTag palette = list(road, "palette");
        ListTag blocks = list(road, "blocks");
        ListTag size = list(road, "size");
        var entrances = new java.util.ArrayList<CompoundTag>();
        for (Tag tag : blocks) {
            CompoundTag block = (CompoundTag) tag;
            if (string(compound(block, "nbt"), "name").equals("minecraft:building_entrance")) entrances.add(block.copy());
        }
        for (CompoundTag entrance : entrances) {
            ListTag coordinates = list(entrance, "pos");
            BlockPos old = new BlockPos(coordinates.getIntOr(0, 0), coordinates.getIntOr(1, 0), coordinates.getIntOr(2, 0));
            Direction facing = Direction.byName(palette.getCompoundOrEmpty(entrance.getIntOr("state", 0))
                    .getCompoundOrEmpty("Properties").getStringOr("orientation", "").split("_")[0]);
            BlockPos edge = switch (facing) {
                case NORTH -> new BlockPos(old.getX(), old.getY(), 0);
                case SOUTH -> new BlockPos(old.getX(), old.getY(), size.getIntOr(2, 0) - 1);
                case WEST -> new BlockPos(0, old.getY(), old.getZ());
                case EAST -> new BlockPos(size.getIntOr(0, 0) - 1, old.getY(), old.getZ());
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
        ListTag blocks = list(template, "blocks");
        blocks.removeIf(tag -> {
            ListTag pos = list((CompoundTag) tag, "pos");
            return pos.getIntOr(0, 0) == position.getX() && pos.getIntOr(1, 0) == position.getY() && pos.getIntOr(2, 0) == position.getZ();
        });
    }

    private static void putState(CompoundTag template, BlockPos position, String name) {
        ListTag palette = list(template, "palette");
        int index = -1;
        for (int i = 0; i < palette.size(); i++) {
            if (string(palette.getCompoundOrEmpty(i), "Name").equals(name)) { index = i; break; }
        }
        if (index == -1) {
            index = palette.size();
            CompoundTag state = new CompoundTag(); state.putString("Name", name); palette.add(state);
        }
        removeBlock(template, position);
        CompoundTag block = new CompoundTag();
        ListTag pos = new ListTag(); pos.add(IntTag.valueOf(position.getX())); pos.add(IntTag.valueOf(position.getY())); pos.add(IntTag.valueOf(position.getZ()));
        block.put("pos", pos); block.putInt("state", index);
        list(template, "blocks").add(block);
    }

    private static void addConnector(CompoundTag house, BlockPos position, String orientation,
                                     String name, String pool, String finalState) {
        ListTag size = list(house, "size");
        if (position.getX() < 0 || position.getX() >= size.getIntOr(0, 0) || position.getY() < 0
                || position.getY() >= size.getIntOr(1, 0) || position.getZ() < 0 || position.getZ() >= size.getIntOr(2, 0)) {
            throw new IllegalArgumentException("Connector outside house: " + position);
        }
        ListTag palette = list(house, "palette");
        ListTag blocks = list(house, "blocks");
        CompoundTag replaced = null;
        for (Tag tag : blocks) {
            CompoundTag block = (CompoundTag) tag;
            ListTag pos = list(block, "pos");
            if (pos.getIntOr(0, 0) == position.getX() && pos.getIntOr(1, 0) == position.getY() && pos.getIntOr(2, 0) == position.getZ()) {
                replaced = block;
                break;
            }
        }
        if (finalState == null) {
            if (replaced == null) throw new IllegalArgumentException("Resident connector needs an existing floor");
            CompoundTag state = palette.getCompoundOrEmpty(replaced.getIntOr("state", 0));
            CompoundTag properties = compound(state, "Properties");
            finalState = string(state, "Name") + (properties.isEmpty() ? "" : properties.keySet().stream().sorted()
                    .map(key -> key + "=" + string(properties, key)).collect(Collectors.joining(",", "[", "]")));
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
