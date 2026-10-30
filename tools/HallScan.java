import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;

/**
 * Audits a saved structure: what it is made of, where the things worth knowing about sit, and what a
 * horizontal slice of it looks like.
 * <p>
 * Written for reading the hall builds back out of a test world, where the only record of what was
 * placed is the NBT a structure block wrote. Nothing here changes the file it reads.
 * <p>
 * usage: HallScan &lt;file.nbt&gt; [blockRegex] [sliceY]
 */
public class HallScan {

    public static void main(String[] args) throws Exception {
        Path file = Paths.get(args[0]);
        String filter = args.length > 1 ? args[1] : "";
        Integer sliceY = args.length > 2 ? Integer.valueOf(args[2]) : null;

        Map<String, Object> root = read(file);
        List<?> sizeList = (List<?>) root.get("size");
        int width = ((Number) sizeList.get(0)).intValue();
        int height = ((Number) sizeList.get(1)).intValue();
        int depth = ((Number) sizeList.get(2)).intValue();

        System.out.println("file: " + file);
        System.out.println("size: " + width + " x " + height + " x " + depth + " (x, y, z)");
        System.out.println("DataVersion: " + root.get("DataVersion"));

        List<String> names = paletteNames(root);
        Map<Long, String> at = new HashMap<>();
        List<?> blocks = (List<?>) root.get("blocks");
        for (Object o : blocks) {
            Map<?, ?> block = (Map<?, ?>) o;
            List<?> pos = (List<?>) block.get("pos");
            String name = names.get(((Number) block.get("state")).intValue());
            at.put(key(((Number) pos.get(0)).intValue(), ((Number) pos.get(1)).intValue(),
                    ((Number) pos.get(2)).intValue()), name);
        }

        Map<String, Integer> byName = new TreeMap<>();
        for (String name : at.values()) {
            byName.merge(base(name), 1, Integer::sum);
        }
        System.out.println("blocks: " + blocks.size() + ", distinct states: " + names.size()
                + ", distinct blocks: " + byName.size());
        System.out.println("--- what it is made of ---");
        byName.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.printf("%6d  %s%n", e.getValue(), e.getKey()));

        Object entities = root.get("entities");
        if (entities instanceof List<?> list && !list.isEmpty()) {
            System.out.println("--- entities ---");
            for (Object o : list) {
                Map<?, ?> entity = (Map<?, ?>) o;
                System.out.println("  " + entity.get("nbt") + " at " + entity.get("pos"));
            }
        }

        if (!filter.isEmpty()) {
            Pattern pattern = Pattern.compile(filter);
            System.out.println("--- blocks matching " + filter + " ---");
            at.entrySet().stream()
                    .filter(e -> pattern.matcher(e.getValue()).find())
                    .sorted(Comparator.comparingLong(Map.Entry::getKey))
                    .forEach(e -> System.out.printf("  (%3d, %2d, %3d)  %s%n",
                            x(e.getKey()), y(e.getKey()), z(e.getKey()), e.getValue()));
        }

        if (sliceY != null) {
            System.out.println("--- slice at y=" + sliceY + " (north at top, x left to right) ---");
            Map<String, Character> legend = new LinkedHashMap<>();
            for (int z = 0; z < depth; z++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < width; x++) {
                    String name = at.getOrDefault(key(x, sliceY, z), "minecraft:air[]");
                    row.append(letter(legend, name));
                }
                System.out.printf("%3d %s%n", z, row);
            }
            System.out.println("legend:");
            legend.forEach((name, ch) -> System.out.println("  " + ch + " = " + name));
        }
    }

    static char letter(Map<String, Character> legend, String name) {
        Character known = legend.get(name);
        if (known != null) return known;
        char ch = ".#*+=~^%&@!?/abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(legend.size());
        legend.put(name, ch);
        return ch;
    }

    static String base(String state) {
        int bracket = state.indexOf('[');
        return bracket < 0 ? state : state.substring(0, bracket);
    }

    static List<String> paletteNames(Map<String, Object> root) {
        List<String> names = new ArrayList<>();
        for (Object entry : (List<?>) root.get("palette")) {
            Map<?, ?> state = (Map<?, ?>) entry;
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
            }
            names.add(name.toString());
        }
        return names;
    }

    // x in the high bits, z in the middle, y in the low 12: a key per block, no collisions.
    static long key(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }

    static int x(long k) { return (int) (k >> 38); }

    static int z(long k) { return (int) (k << 26 >> 38); }

    static int y(long k) { return (int) (k << 52 >> 52); }

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
}
