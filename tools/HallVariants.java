import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Cuts one hall build into the three alliance halls the mod ships.
 * <p>
 * There is one source build - {@code faction_hall.nbt} - so a change to the hall is made once and the
 * three variants are cut from it again. Three things differ between them, and the game cannot do any
 * of those with a template processor, which is why this is a build step rather than a processor list:
 * <ul>
 *   <li>the acacia trim band is replaced by the alliance's own stair, keeping each stair's facing and
 *       half so the band still reads as the same carpentered detail in another wood;</li>
 *   <li>the warden behind the counter is that alliance's folk, standing where the hall's own template
 *       put him;</li>
 *   <li>the outside air becomes {@code minecraft:structure_void}, so a hall dropped into a hillside
 *       dresses its own ground and does not clear the country around it. Air the build encloses - its
 *       rooms, the space under its roof - stays air, which is what keeps the inside hollow.</li>
 * </ul>
 *
 * usage: HallVariants <source.nbt> <outDir>
 */
public class HallVariants {

    static final String VOID = "minecraft:structure_void";
    static final String AIR = "minecraft:air";
    static final String TRIM = "minecraft:acacia_stairs";

    /**
 * Where the warden stands: at the western table, the nearer of its pair to the front door, on the
 * open side of the table and turned to read it. The hall keeps four lecterns as its tables, set
 * against the pillar lines at (5,1,6) and (5,1,9) facing east and (10,1,6) and (10,1,9) facing west,
 * so the open side of the western pair is x=6 and of the eastern pair is x=9.
 */
    static final double WARDEN_X = 6.5D;
    static final double WARDEN_Y = 1.0D;
    static final double WARDEN_Z = 6.5D;
    /** Yaw 90 faces west, into the lectern it stands at. */
    static final float WARDEN_YAW = 90.0F;

    /** Where the envoy stands: the table across the aisle from the warden, likewise on its open side. */
    static final double VENDOR_X = 9.5D;
    static final double VENDOR_Y = 1.0D;
    static final double VENDOR_Z = 6.5D;
    /** Yaw 270 faces east, into its lectern. */
    static final float VENDOR_YAW = 270.0F;

    record Variant(String file, String stairs, String warden, String wardenName, String tag, int alliance, String vendorName) {}

    static final List<Variant> VARIANTS = List.of(
            new Variant("faction_hall_west.nbt", "tolkienmobs:stairs_lebethron",
                    "tolkienmobs:entityttmhuman", "Warden of the West", "tolkienmobs:faction_envoy/west", 0, "Envoy of the West"),
            new Variant("faction_hall_east.nbt", "tolkienmobs:stairs_morguliron",
                    "tolkienmobs:entityttmorc", "Warden of the East", "tolkienmobs:faction_envoy/east", 1, "Envoy of the East"),
            new Variant("faction_hall_harad.nbt", "minecraft:sandstone_stairs",
                    "tolkienmobs:entityttmsouthron_npc", "Warden of Haradwaith", "tolkienmobs:faction_envoy/harad", 2, "Envoy of Haradwaith"));

    public static void main(String[] args) throws Exception {
        Path source = Paths.get(args[0]);
        Path outDir = Paths.get(args[1]);
        Files.createDirectories(outDir);
        for (Variant variant : VARIANTS) {
            Map<String, Object> root = read(source);
            cut(root, variant);
            Path target = outDir.resolve(variant.file());
            try (OutputStream os = new GZIPOutputStream(new BufferedOutputStream(Files.newOutputStream(target)))) {
                write(new DataOutputStream(os), root);
            }
            System.out.println("wrote " + target.getFileName());
        }
    }

    @SuppressWarnings("unchecked")
    static void cut(Map<String, Object> root, Variant variant) {
        List<Object> palette = (List<Object>) root.get("palette");
        List<Object> blocks = (List<Object>) root.get("blocks");
        List<Object> size = (List<Object>) root.get("size");
        int width = ((Number) size.get(0)).intValue();
        int height = ((Number) size.get(1)).intValue();
        int depth = ((Number) size.get(2)).intValue();

        // What each state index is, and where every block sits, so a column's own top can be read.
        List<String> names = new ArrayList<>();
        List<String> suffixes = new ArrayList<>();
        for (Object entry : palette) {
            Map<String, Object> state = (Map<String, Object>) entry;
            names.add((String) state.get("Name"));
            StringBuilder suffix = new StringBuilder();
            Object props = state.get("Properties");
            if (props instanceof Map<?, ?> map) {
                TreeMap<String, String> sorted = new TreeMap<>();
                for (Map.Entry<?, ?> pair : map.entrySet()) {
                    sorted.put(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()));
                }
                for (Map.Entry<String, String> pair : sorted.entrySet()) {
                    if (suffix.length() > 0) suffix.append(',');
                    suffix.append(pair.getKey()).append('=').append(pair.getValue());
                }
            }
            suffixes.add(suffix.toString());
        }

        Map<Long, String> states = new HashMap<>();
        for (Object entry : blocks) {
            Map<String, Object> block = (Map<String, Object>) entry;
            List<Object> pos = (List<Object>) block.get("pos");
            int index = ((Number) block.get("state")).intValue();
            states.put(key(((Number) pos.get(0)).intValue(), ((Number) pos.get(1)).intValue(),
                    ((Number) pos.get(2)).intValue()), names.get(index) + "{" + suffixes.get(index) + "}");
        }

        // The highest solid block in each column, which is what decides whether a pocket of air is
        // inside the build or merely beside it.
        int[] top = new int[width * depth];
        Arrays.fill(top, Integer.MIN_VALUE);
        for (Map.Entry<Long, String> entry : states.entrySet()) {
            String name = entry.getValue().split("\\{")[0];
            if (isAir(name)) continue;
            int x = x(entry.getKey()), y = y(entry.getKey()), z = z(entry.getKey());
            int column = x + z * width;
            top[column] = Math.max(top[column], y);
        }

        Map<String, Integer> out = new LinkedHashMap<>();
        out.put(AIR, 0);
        out.put(VOID, 1);
        List<Object> newPalette = new ArrayList<>();
        newPalette.add(state(AIR, ""));
        newPalette.add(state(VOID, ""));
        List<int[]> positions = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        int voids = 0, airs = 0, kept = 0;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                int columnTop = top[x + z * width];
                for (int y = 0; y < height; y++) {
                    String raw = states.getOrDefault(key(x, y, z), AIR + "{}");
                    String name = raw.split("\\{")[0];
                    String suffix = raw.substring(name.length());
                    suffix = suffix.substring(1, suffix.length() - 1);
                    String emit;
                    if (!isAir(name)) {
                        // Only the block changes: the trim keeps every facing and half it had, so
                        // the band reads as the same carpentry in the alliance's own material.
                        emit = name.equals(TRIM) ? variant.stairs() : name;
                        kept++;
                    } else if (margin(x, z, width, depth)) {
                        // The build is drawn to a one-block margin and its eaves overhang that
                        // margin. Air there is outside the hall however much roof stands above it,
                        // so the margin is void and the country under the eaves is left standing.
                        emit = VOID;
                        suffix = "";
                        voids++;
                    } else if (columnTop != Integer.MIN_VALUE && y < columnTop) {
                        emit = AIR;
                        suffix = "";
                        airs++;
                    } else {
                        emit = VOID;
                        suffix = "";
                        voids++;
                    }
                    String paletteKey = suffix.isEmpty() ? emit : emit + "{" + suffix + "}";
                    if (!out.containsKey(paletteKey)) {
                        out.put(paletteKey, out.size());
                        newPalette.add(state(emit, suffix));
                    }
                    positions.add(new int[]{x, y, z});
                    indexes.add(out.get(paletteKey));
                }
            }
        }

        List<Object> newBlocks = new ArrayList<>(positions.size());
        for (int i = 0; i < positions.size(); i++) {
            Map<String, Object> block = new LinkedHashMap<>();
            int[] pos = positions.get(i);
            block.put("pos", List.of(pos[0], pos[1], pos[2]));
            block.put("state", indexes.get(i));
            newBlocks.add(block);
        }

        root.put("palette", newPalette);
        root.put("blocks", newBlocks);
        root.put("entities", List.of(warden(variant), vendor(variant)));
        System.out.printf("  %s: kept=%d air=%d void=%d palette=%d%n",
                variant.file(), kept, airs, voids, newPalette.size());
    }

    /** The warden: an NPC of the alliance's folk, standing behind the counter and not wandering off. */
    static Map<String, Object> warden(Variant variant) {
        Map<String, Object> nbt = new LinkedHashMap<>();
        nbt.put("id", variant.warden());
        nbt.put("Tags", List.of("tolkienmobs:faction_envoy", variant.tag()));
        // A plain string, which 26.1 reads as literal text. Handing it a JSON component here does not
        // parse: the string is taken whole and the warden shows the braces, which is what a {"text":...}
        // written into the tag used to do.
        nbt.put("CustomName", variant.wardenName());
        nbt.put("PersistenceRequired", (byte) 1);
        nbt.put("NoAI", (byte) 1);
        nbt.put("Invulnerable", (byte) 1);
        nbt.put("Silent", (byte) 1);
        nbt.put("Rotation", List.of(WARDEN_YAW, 0.0F));

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("pos", List.of(WARDEN_X, WARDEN_Y, WARDEN_Z));
        entry.put("blockPos", List.of((int) Math.floor(WARDEN_X), (int) Math.floor(WARDEN_Y),
                (int) Math.floor(WARDEN_Z)));
        entry.put("nbt", nbt);
        return entry;
    }
    /**
     * The envoy: the alliance's merchant, standing at the counter beside the warden. Its side is written
     * into its own data rather than left to the scoreboard tag, because the side is what decides which
     * shelves it opens - a tag is only how the hall is looked up.
     */
    static Map<String, Object> vendor(Variant variant) {
        Map<String, Object> nbt = new LinkedHashMap<>();
        nbt.put("id", "tolkienmobs:entityttmfactionvendor");
        nbt.put("Tags", List.of("tolkienmobs:faction_vendor",
                variant.tag().replace("faction_envoy", "faction_vendor")));
        nbt.put("CustomName", variant.vendorName());
        nbt.put("Alliance", variant.alliance());
        nbt.put("PersistenceRequired", (byte) 1);
        nbt.put("NoAI", (byte) 1);
        nbt.put("Invulnerable", (byte) 1);
        nbt.put("Silent", (byte) 1);
        nbt.put("Rotation", List.of(VENDOR_YAW, 0.0F));

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("pos", List.of(VENDOR_X, VENDOR_Y, VENDOR_Z));
        entry.put("blockPos", List.of((int) Math.floor(VENDOR_X), (int) Math.floor(VENDOR_Y),
                (int) Math.floor(VENDOR_Z)));
        entry.put("nbt", nbt);
        return entry;
    }

    static Map<String, Object> state(String name, String suffix) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("Name", name);
        if (!suffix.isEmpty()) {
            Map<String, Object> props = new LinkedHashMap<>();
            for (String pair : suffix.split(",")) {
                int eq = pair.indexOf('=');
                if (eq > 0) props.put(pair.substring(0, eq), pair.substring(eq + 1));
            }
            out.put("Properties", props);
        }
        return out;
    }

    /** The one-block ring a build drawn to the plot's edge leaves around itself. */
    static boolean margin(int x, int z, int width, int depth) {
        return x == 0 || z == 0 || x == width - 1 || z == depth - 1;
    }

    static boolean isAir(String name) {
        return name.equals("minecraft:air") || name.equals("minecraft:cave_air") || name.equals("minecraft:void_air");
    }

    // x in the high bits, z in the middle, y in the low 12: a key per block, no collisions.
    static long key(int x, int y, int z) {
        return ((long) x << 32) | ((long) z << 12) | (y + 2048);
    }

    static int x(long k) { return (int) (k >> 32); }

    static int z(long k) { return (int) ((k >> 12) & 0xFFFFF); }

    static int y(long k) { return (int) (k & 0xFFF) - 2048; }

    // ---- NBT ----

    @SuppressWarnings("unchecked")
    static Map<String, Object> read(Path file) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(Files.newInputStream(file))))) {
            int type = in.readUnsignedByte();
            in.readUTF();
            if (type != 10) throw new IOException("root is not a compound");
            return (Map<String, Object>) payload(in, type);
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

    static void write(DataOutputStream out, Object root) throws IOException {
        out.writeByte(10);
        out.writeUTF("");
        writePayload(out, root);
    }

    static void writePayload(DataOutputStream out, Object value) throws IOException {
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                int type = typeOf(entry.getValue());
                if (type == 0) continue;
                out.writeByte(type);
                out.writeUTF(String.valueOf(entry.getKey()));
                writePayload(out, entry.getValue());
            }
            out.writeByte(0);
        } else if (value instanceof List<?> list) {
            int elem = list.isEmpty() ? 0 : typeOf(list.get(0));
            out.writeByte(elem);
            out.writeInt(list.size());
            for (Object o : list) writePayload(out, o);
        } else if (value instanceof Byte b) {
            out.writeByte(b);
        } else if (value instanceof Short s) {
            out.writeShort(s);
        } else if (value instanceof Integer i) {
            out.writeInt(i);
        } else if (value instanceof Long l) {
            out.writeLong(l);
        } else if (value instanceof Float f) {
            out.writeFloat(f);
        } else if (value instanceof Double d) {
            out.writeDouble(d);
        } else if (value instanceof String s) {
            out.writeUTF(s);
        } else if (value instanceof byte[] bytes) {
            out.writeInt(bytes.length);
            out.write(bytes);
        } else if (value instanceof int[] ints) {
            out.writeInt(ints.length);
            for (int v : ints) out.writeInt(v);
        } else if (value instanceof long[] longs) {
            out.writeInt(longs.length);
            for (long v : longs) out.writeLong(v);
        } else {
            throw new IOException("can't write " + value.getClass());
        }
    }

    static int typeOf(Object value) {
        if (value instanceof Map<?, ?>) return 10;
        if (value instanceof List<?>) return 9;
        if (value instanceof Byte) return 1;
        if (value instanceof Short) return 2;
        if (value instanceof Integer) return 3;
        if (value instanceof Long) return 4;
        if (value instanceof Float) return 5;
        if (value instanceof Double) return 6;
        if (value instanceof byte[]) return 7;
        if (value instanceof String) return 8;
        if (value instanceof int[]) return 11;
        if (value instanceof long[]) return 12;
        return 0;
    }
}
