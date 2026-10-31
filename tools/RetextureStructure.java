import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Rewrites a saved structure's palette: every vanilla stone in it becomes one of the mod's dark
 * stone variants, every vanilla wood the matching mirkwood piece. Only the palette is touched, so
 * the blocks stay where the builder put them and only what they are made of changes; block states
 * are kept as they are because each mod block is the same vanilla block class as the one it
 * replaces (a stair is still a stair, a wall still a wall).
 * <p>
 * A sign is the one case that carries more than a block name: its text lives in a block entity, so
 * the entity's id is renamed with it.
 * <p>
 * usage: RetextureStructure &lt;file.nbt&gt; [file.nbt ...]
 */
public class RetextureStructure {

    /** Vanilla block -> the mod block that stands in for it. */
    static final Map<String, String> RENAME = new LinkedHashMap<>();

    static {
        // Stone: the whole family the two builds use, plus the dressed sandstone that reads as
        // dressed stone once it is in Mordor rather than in Nurn.
        RENAME.put("minecraft:stone", "tolkienmobs:dark_stone");
        RENAME.put("minecraft:cobblestone", "tolkienmobs:cobbled_dark_stone");
        RENAME.put("minecraft:mossy_cobblestone", "tolkienmobs:mossy_cobbled_dark_stone");
        RENAME.put("minecraft:stone_bricks", "tolkienmobs:dark_stone_bricks");
        RENAME.put("minecraft:mossy_stone_bricks", "tolkienmobs:mossy_dark_stone_bricks");
        RENAME.put("minecraft:cracked_stone_bricks", "tolkienmobs:cracked_dark_stone_bricks");
        RENAME.put("minecraft:chiseled_stone_bricks", "tolkienmobs:chiseled_dark_stone_bricks");
        RENAME.put("minecraft:stone_slab", "tolkienmobs:slab_dark_stone");
        RENAME.put("minecraft:stone_brick_slab", "tolkienmobs:slab_dark_stone_bricks");
        RENAME.put("minecraft:cobblestone_slab", "tolkienmobs:slab_cobbled_dark_stone");
        RENAME.put("minecraft:smooth_stone_slab", "tolkienmobs:slab_smooth_dark_stone");
        RENAME.put("minecraft:stone_brick_stairs", "tolkienmobs:stairs_dark_stone_bricks");
        RENAME.put("minecraft:cobblestone_stairs", "tolkienmobs:stairs_cobbled_dark_stone");
        RENAME.put("minecraft:stone_brick_wall", "tolkienmobs:wall_dark_stone_bricks");
        RENAME.put("minecraft:cobblestone_wall", "tolkienmobs:wall_cobbled_dark_stone");
        RENAME.put("minecraft:sandstone", "tolkienmobs:smooth_dark_stone");
        RENAME.put("minecraft:cut_sandstone", "tolkienmobs:smooth_dark_stone");
        RENAME.put("minecraft:sandstone_slab", "tolkienmobs:slab_smooth_dark_stone");
        RENAME.put("minecraft:iron_bars", "tolkienmobs:morguliron_bars");

        // Wood: every vanilla species in either build becomes mirkwood, piece for piece.
        RENAME.put("minecraft:oak_planks", "tolkienmobs:planks_mirkwood");
        RENAME.put("minecraft:birch_planks", "tolkienmobs:planks_mirkwood");
        RENAME.put("minecraft:spruce_planks", "tolkienmobs:planks_mirkwood");
        RENAME.put("minecraft:jungle_planks", "tolkienmobs:planks_mirkwood");
        RENAME.put("minecraft:oak_log", "tolkienmobs:log_mirkwood");
        RENAME.put("minecraft:birch_log", "tolkienmobs:log_mirkwood");
        RENAME.put("minecraft:spruce_log", "tolkienmobs:log_mirkwood");
        RENAME.put("minecraft:oak_wood", "tolkienmobs:wood_mirkwood");
        RENAME.put("minecraft:oak_stairs", "tolkienmobs:stairs_mirkwood");
        RENAME.put("minecraft:oak_slab", "tolkienmobs:slab_mirkwood");
        RENAME.put("minecraft:spruce_slab", "tolkienmobs:slab_mirkwood");
        RENAME.put("minecraft:oak_fence", "tolkienmobs:fence_mirkwood");
        RENAME.put("minecraft:spruce_fence", "tolkienmobs:fence_mirkwood");
        RENAME.put("minecraft:oak_fence_gate", "tolkienmobs:fence_gate_mirkwood");
        RENAME.put("minecraft:oak_door", "tolkienmobs:door_mirkwood");
        RENAME.put("minecraft:spruce_door", "tolkienmobs:door_mirkwood");
        RENAME.put("minecraft:oak_trapdoor", "tolkienmobs:trapdoor_mirkwood");
        RENAME.put("minecraft:spruce_trapdoor", "tolkienmobs:trapdoor_mirkwood");
        RENAME.put("minecraft:oak_pressure_plate", "tolkienmobs:pressure_plate_mirkwood");
        RENAME.put("minecraft:oak_sign", "tolkienmobs:sign_mirkwood");
        RENAME.put("minecraft:oak_wall_sign", "tolkienmobs:sign_wall_mirkwood");
        RENAME.put("minecraft:oak_leaves", "tolkienmobs:leaves_mirkwood");
        RENAME.put("minecraft:jungle_leaves", "tolkienmobs:leaves_mirkwood");
        RENAME.put("minecraft:ladder", "tolkienmobs:ladder_mirkwood");
        RENAME.put("minecraft:torch", "tolkienmobs:torch_mirkwood");
        RENAME.put("minecraft:wall_torch", "tolkienmobs:wall_torch_mirkwood");
    }

    /** A block entity's id where it has to follow the block it belongs to. */
    static final Map<String, String> ENTITY_RENAME = Map.of(
            "minecraft:sign", "tolkienmobs:tolkien_sign_entity");

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            retouch(Paths.get(arg));
        }
    }

    static void retouch(Path file) throws IOException {
        Map<String, Object> root = read(file);
        List<?> palette = (List<?>) root.get("palette");
        int changed = 0;
        List<String> report = new ArrayList<>();
        for (Object entry : palette) {
            Map<String, Object> state = (Map<String, Object>) entry;
            String name = (String) state.get("Name");
            String replacement = RENAME.get(name);
            if (replacement != null) {
                state.put("Name", replacement);
                changed++;
                report.add("  " + name + " -> " + replacement);
            }
        }

        int entities = 0;
        Object blockEntities = root.get("block_entities");
        if (blockEntities instanceof List<?> list) {
            for (Object o : list) {
                Map<String, Object> holder = (Map<String, Object>) o;
                Object nbt = holder.get("nbt");
                if (nbt instanceof Map<?, ?> map) {
                    Map<String, Object> compound = (Map<String, Object>) map;
                    String id = (String) compound.get("id");
                    String replacement = ENTITY_RENAME.get(id);
                    if (replacement != null) {
                        compound.put("id", replacement);
                        entities++;
                        report.add("  block entity " + id + " -> " + replacement);
                    }
                }
            }
        }

        write(file, root);
        System.out.println(file + ": renamed " + changed + " palette entries, " + entities + " block entities");
        report.forEach(System.out::println);
    }

    // ---- NBT ----

    static Map<String, Object> read(Path file) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(Files.newInputStream(file))))) {
            int type = in.readUnsignedByte();
            in.readUTF();
            if (type != 10) throw new IOException("root is not a compound");
            return (Map<String, Object>) payload(in, type);
        }
    }

    static void write(Path file, Map<String, Object> root) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(file)))) {
            out.writeByte(10);
            out.writeUTF("");
            payloadValue(out, root);
        }
    }

    static Object payload(DataInputStream in, int type) throws IOException {
        switch (type) {
            case 0: return null;
            case 1: return in.readByte();
            case 2: return in.readShort();
            case 3: return in.readInt();
            case 4: return in.readLong();
            case 5: return in.readFloat();
            case 6: return in.readDouble();
            case 7: { byte[] b = new byte[in.readInt()]; in.readFully(b); return b; }
            case 8: return in.readUTF();
            case 9: {
                int elem = in.readUnsignedByte();
                int n = in.readInt();
                List<Object> list = new ArrayList<>(n);
                for (int i = 0; i < n; i++) list.add(payload(in, elem));
                return list;
            }
            case 10: {
                Map<String, Object> map = new LinkedHashMap<>();
                while (true) {
                    int t = in.readUnsignedByte();
                    if (t == 0) return map;
                    map.put(in.readUTF(), payload(in, t));
                }
            }
            case 11: { int[] r = new int[in.readInt()]; for (int i = 0; i < r.length; i++) r[i] = in.readInt(); return r; }
            case 12: { long[] r = new long[in.readInt()]; for (int i = 0; i < r.length; i++) r[i] = in.readLong(); return r; }
            default: throw new IOException("tag " + type);
        }
    }

    static void payload(DataOutputStream out, Object value) throws IOException {
        if (value instanceof Byte b) { out.writeByte(1); out.writeByte(b); }
        else if (value instanceof Short s) { out.writeByte(2); out.writeShort(s); }
        else if (value instanceof Integer i) { out.writeByte(3); out.writeInt(i); }
        else if (value instanceof Long l) { out.writeByte(4); out.writeLong(l); }
        else if (value instanceof Float f) { out.writeByte(5); out.writeFloat(f); }
        else if (value instanceof Double d) { out.writeByte(6); out.writeDouble(d); }
        else if (value instanceof byte[] a) { out.writeByte(7); out.writeInt(a.length); out.write(a); }
        else if (value instanceof String s) { out.writeByte(8); out.writeUTF(s); }
        else if (value instanceof List<?> list) {
            out.writeByte(9);
            out.writeByte(list.isEmpty() ? 0 : tagOf(list.get(0)));
            out.writeInt(list.size());
            for (Object element : list) payloadValue(out, element);
        } else if (value instanceof Map<?, ?> map) {
            out.writeByte(10);
            for (Map.Entry<?, ?> pair : map.entrySet()) {
                out.writeByte(tagOf(pair.getValue()));
                out.writeUTF(String.valueOf(pair.getKey()));
                payloadValue(out, pair.getValue());
            }
            out.writeByte(0);
        } else if (value instanceof int[] a) { out.writeByte(11); out.writeInt(a.length); for (int i : a) out.writeInt(i); }
        else if (value instanceof long[] a) { out.writeByte(12); out.writeInt(a.length); for (long l : a) out.writeLong(l); }
        else throw new IOException("cannot write " + value);
    }

    /** A list's elements are written without their own tag byte: the list already declared it. */
    static void payloadValue(DataOutputStream out, Object value) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        payload(new DataOutputStream(buffer), value);
        byte[] bytes = buffer.toByteArray();
        out.write(bytes, 1, bytes.length - 1);
    }

    static int tagOf(Object value) throws IOException {
        if (value instanceof Byte) return 1;
        if (value instanceof Short) return 2;
        if (value instanceof Integer) return 3;
        if (value instanceof Long) return 4;
        if (value instanceof Float) return 5;
        if (value instanceof Double) return 6;
        if (value instanceof byte[]) return 7;
        if (value instanceof String) return 8;
        if (value instanceof List<?>) return 9;
        if (value instanceof Map<?, ?>) return 10;
        if (value instanceof int[]) return 11;
        if (value instanceof long[]) return 12;
        throw new IOException("cannot tag " + value);
    }
}
