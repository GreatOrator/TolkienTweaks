import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

/**
 * Reads placed blocks back out of a saved world and can cut a box of them into a structure template.
 * <p>
 * Answers "what actually stands in this world" when the only other record is a structure-block save
 * that may be older than the build. Handles the region format as 26.1 writes it: chunks packed three
 * bytes to an offset, each chunk zlib-compressed, or held uncompressed in a {@code .mcc} file when the
 * high bit of the compression byte is set.
 * <p>
 * usage:
 *   WorldScan &lt;regionDir&gt; find &lt;blockRegex&gt;
 *   WorldScan &lt;regionDir&gt; slice &lt;x0&gt; &lt;y0&gt; &lt;z0&gt; &lt;x1&gt; &lt;y1&gt; &lt;z1&gt; &lt;y&gt;
 *   WorldScan &lt;regionDir&gt; extract &lt;x0&gt; &lt;y0&gt; &lt;z0&gt; &lt;x1&gt; &lt;y1&gt; &lt;z1&gt; &lt;out.nbt&gt;
 */
public class WorldScan {

    /** Every block in the world, keyed by position, as name[properties]. */
    static final Map<Long, String> WORLD = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Path regionDir = Paths.get(args[0]);
        String mode = args[1];
        read(regionDir, (cx, cz, nbt) -> scanChunk(cx, cz, nbt));

        switch (mode) {
            case "find" -> find(Pattern.compile(args[2]));
            case "slice" -> slice(parseBox(args, 2), Integer.parseInt(args[8]));
            case "extract" -> extract(parseBox(args, 2), Paths.get(args[8]));
            default -> throw new IllegalArgumentException("mode is find, slice or extract");
        }
    }

    // ---- modes ----

    static void find(Pattern pattern) {
        Map<String, List<long[]>> hits = new TreeMap<>();
        for (Map.Entry<Long, String> entry : WORLD.entrySet()) {
            if (!pattern.matcher(entry.getValue()).find()) continue;
            String base = base(entry.getValue());
            hits.computeIfAbsent(base, k -> new ArrayList<>())
                    .add(new long[]{x(entry.getKey()), y(entry.getKey()), z(entry.getKey())});
        }
        if (hits.isEmpty()) {
            System.out.println("nothing matched");
            return;
        }
        for (Map.Entry<String, List<long[]>> entry : hits.entrySet()) {
            List<long[]> positions = entry.getValue();
            long minX = Long.MAX_VALUE, maxX = Long.MIN_VALUE, minY = Long.MAX_VALUE,
                    maxY = Long.MIN_VALUE, minZ = Long.MAX_VALUE, maxZ = Long.MIN_VALUE;
            for (long[] p : positions) {
                minX = Math.min(minX, p[0]); maxX = Math.max(maxX, p[0]);
                minY = Math.min(minY, p[1]); maxY = Math.max(maxY, p[1]);
                minZ = Math.min(minZ, p[2]); maxZ = Math.max(maxZ, p[2]);
            }
            System.out.printf("%-55s %5d  x %d..%d  y %d..%d  z %d..%d%n",
                    entry.getKey(), positions.size(), minX, maxX, minY, maxY, minZ, maxZ);
            positions.stream().sorted(Comparator.comparingLong(p -> p[0] * 1_000_000 + p[2] * 1_000 + p[1]))
                    .limit(400)
                    .forEach(p -> System.out.printf("      (%d, %d, %d)  %s%n", p[0], p[1], p[2], entry.getKey()));
            if (positions.size() > 400) System.out.println("      ... " + (positions.size() - 400) + " more");
        }
    }

    static void slice(int[] box, int y) {
        int width = box[3] - box[0] + 1;
        int depth = box[5] - box[2] + 1;
        Map<String, Character> legend = new LinkedHashMap<>();
        System.out.println("--- slice at y=" + y + ", x " + box[0] + ".." + box[3] + ", z " + box[2] + ".." + box[5] + " ---");
        for (int z = box[2]; z <= box[5]; z++) {
            StringBuilder row = new StringBuilder();
            for (int x = box[0]; x <= box[3]; x++) {
                String name = WORLD.getOrDefault(key(x, y, z), "minecraft:air[]");
                Character known = legend.get(name);
                if (known == null) {
                    known = ".#*+=~^%&@!?/abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(legend.size());
                    legend.put(name, known);
                }
                row.append(known);
            }
            System.out.printf("%4d %s%n", z, row);
        }
        System.out.println("legend (" + width + " x " + depth + "):");
        legend.forEach((name, ch) -> System.out.println("  " + ch + " = " + name));
    }

    static void extract(int[] box, Path out) throws IOException {
        int width = box[3] - box[0] + 1;
        int height = box[4] - box[1] + 1;
        int depth = box[5] - box[2] + 1;
        Map<String, Integer> palette = new LinkedHashMap<>();
        List<Object> blocks = new ArrayList<>();
        int air = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    String state = WORLD.getOrDefault(key(box[0] + x, box[1] + y, box[2] + z), "minecraft:air[]");
                    String base = base(state);
                    if (base.equals("minecraft:air") || base.equals("minecraft:cave_air")
                            || base.equals("minecraft:void_air")) {
                        air++;
                        continue;
                    }
                    Integer index = palette.get(state);
                    if (index == null) {
                        index = palette.size();
                        palette.put(state, index);
                    }
                    Map<String, Object> block = new LinkedHashMap<>();
                    block.put("pos", List.of(x, y, z));
                    block.put("state", index);
                    blocks.add(block);
                }
            }
        }
        List<Object> paletteList = new ArrayList<>();
        for (String state : palette.keySet()) {
            paletteList.add(stateTag(state));
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("size", List.of(width, height, depth));
        root.put("palette", paletteList);
        root.put("blocks", blocks);
        root.put("entities", new ArrayList<>());
        root.put("DataVersion", 4189);
        try (OutputStream os = new GZIPOutputStream(new BufferedOutputStream(Files.newOutputStream(out)))) {
            write(new DataOutputStream(os), root);
        }
        System.out.printf("wrote %s: %d x %d x %d, %d blocks, %d air skipped, %d palette entries%n",
                out, width, height, depth, blocks.size(), air, palette.size());
    }

    // ---- chunks ----

    interface ChunkConsumer {
        void accept(int chunkX, int chunkZ, Map<String, Object> nbt) throws IOException;
    }

    @SuppressWarnings("unchecked")
    static void scanChunk(int chunkX, int chunkZ, Map<String, Object> chunk) {
        Object sections = chunk.get("sections");
        if (!(sections instanceof List<?> list)) return;
        for (Object o : list) {
            Map<String, Object> section = (Map<String, Object>) o;
            int sectionY = ((Number) section.get("Y")).intValue();
            Map<String, Object> states = (Map<String, Object>) section.get("block_states");
            if (states == null) continue;
            List<?> paletteList = (List<?>) states.get("palette");
            if (paletteList == null || paletteList.isEmpty()) continue;
            List<String> names = new ArrayList<>();
            for (Object entry : paletteList) {
                Map<String, Object> state = (Map<String, Object>) entry;
                StringBuilder name = new StringBuilder((String) state.get("Name"));
                Object props = state.get("Properties");
                if (props instanceof Map<?, ?> map) {
                    TreeMap<String, String> sorted = new TreeMap<>();
                    for (Map.Entry<?, ?> pair : map.entrySet()) {
                        sorted.put(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()));
                    }
                    name.append('[');
                    boolean first = true;
                    for (Map.Entry<String, String> pair : sorted.entrySet()) {
                        if (!first) name.append(',');
                        first = false;
                        name.append(pair.getKey()).append('=').append(pair.getValue());
                    }
                    name.append(']');
                } else {
                    name.append("[]");
                }
                names.add(name.toString());
            }
            long[] data = (long[]) states.get("data");
            int bits = names.size() == 1 ? 0 : Math.max(4, 64 - Long.numberOfLeadingZeros(names.size() - 1));
            int perLong = bits == 0 ? 0 : 64 / bits;
            long mask = bits == 0 ? 0 : (1L << bits) - 1;
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        int index;
                        if (bits == 0) {
                            index = 0;
                        } else {
                            int i = (y * 16 + z) * 16 + x;
                            index = (int) ((data[i / perLong] >>> ((i % perLong) * bits)) & mask);
                        }
                        if (index == 0 && names.get(0).startsWith("minecraft:air")) continue;
                        WORLD.put(key(chunkX * 16 + x, sectionY * 16 + y, chunkZ * 16 + z), names.get(index));
                    }
                }
            }
        }
    }

    static void read(Path regionDir, ChunkConsumer consumer) throws IOException {
        List<Path> regions = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(regionDir, "r.*.mca")) {
            for (Path path : stream) regions.add(path);
        }
        Collections.sort(regions);
        for (Path path : regions) {
            String name = path.getFileName().toString();
            String[] parts = name.substring(2, name.length() - 4).split("\\.");
            int regionX = Integer.parseInt(parts[0]);
            int regionZ = Integer.parseInt(parts[1]);
            byte[] file = Files.readAllBytes(path);
            for (int i = 0; i < 1024; i++) {
                int offset = ((file[i * 4] & 0xFF) << 16) | ((file[i * 4 + 1] & 0xFF) << 8) | (file[i * 4 + 2] & 0xFF);
                int sectors = file[i * 4 + 3] & 0xFF;
                if (offset == 0 || sectors == 0) continue;
                int at = offset * 4096;
                if (at + 5 > file.length) continue;
                int length = ((file[at] & 0xFF) << 24) | ((file[at + 1] & 0xFF) << 16)
                        | ((file[at + 2] & 0xFF) << 8) | (file[at + 3] & 0xFF);
                int compression = file[at + 4] & 0xFF;
                int chunkX = regionX * 32 + (i % 32);
                int chunkZ = regionZ * 32 + (i / 32);
                byte[] payload;
                if ((compression & 0x80) != 0) {
                    Path external = regionDir.resolve("c." + chunkX + "." + chunkZ + ".mcc");
                    if (!Files.exists(external)) continue;
                    payload = Files.readAllBytes(external);
                    compression &= 0x7F;
                } else {
                    payload = Arrays.copyOfRange(file, at + 5, Math.min(file.length, at + 4 + length));
                }
                byte[] nbt = switch (compression) {
                    case 1 -> gunzip(payload);
                    case 2 -> inflate(payload);
                    case 3 -> payload;
                    default -> throw new IOException("chunk compression " + compression + " is not supported");
                };
                consumer.accept(chunkX, chunkZ, rootOf(nbt));
            }
            System.out.println("read " + name + " -> " + WORLD.size() + " blocks so far");
        }
    }

    static byte[] gunzip(byte[] data) throws IOException {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(data))) {
            return in.readAllBytes();
        }
    }

    static byte[] inflate(byte[] data) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length * 4);
        byte[] buffer = new byte[8192];
        try {
            while (!inflater.finished()) {
                int n = inflater.inflate(buffer);
                if (n == 0) {
                    if (inflater.needsInput() || inflater.needsDictionary()) break;
                }
                out.write(buffer, 0, n);
            }
        } catch (java.util.zip.DataFormatException e) {
            throw new IOException(e);
        } finally {
            inflater.end();
        }
        return out.toByteArray();
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> rootOf(byte[] nbt) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(nbt));
        int type = in.readUnsignedByte();
        in.readUTF();
        if (type != 10) throw new IOException("root is not a compound");
        return (Map<String, Object>) payload(in, type);
    }

    // ---- helpers ----

    static int[] parseBox(String[] args, int from) {
        int[] box = new int[6];
        for (int i = 0; i < 6; i++) box[i] = Integer.parseInt(args[from + i]);
        return box;
    }

    static String base(String state) {
        int bracket = state.indexOf('[');
        return bracket < 0 ? state : state.substring(0, bracket);
    }

    static Map<String, Object> stateTag(String state) {
        int bracket = state.indexOf('[');
        Map<String, Object> out = new LinkedHashMap<>();
        if (bracket < 0) {
            out.put("Name", state);
            return out;
        }
        out.put("Name", state.substring(0, bracket));
        Map<String, Object> props = new LinkedHashMap<>();
        String body = state.substring(bracket + 1, state.length() - 1);
        for (String pair : body.split(",")) {
            int eq = pair.indexOf('=');
            if (eq > 0) props.put(pair.substring(0, eq), pair.substring(eq + 1));
        }
        out.put("Properties", props);
        return out;
    }

    // x in the high bits, z in the middle, y in the low 12: a key per block, no collisions.
    static long key(int x, int y, int z) {
        return ((long) x << 32) | ((long) z << 12) | (y + 2048);
    }

    static int x(long k) { return (int) (k >> 32); }

    static int z(long k) { return (int) ((k >> 12) & 0xFFFFF); }

    static int y(long k) { return (int) (k & 0xFFF) - 2048; }

    // ---- NBT ----

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
