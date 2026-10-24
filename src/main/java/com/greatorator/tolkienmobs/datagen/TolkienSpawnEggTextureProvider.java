package com.greatorator.tolkienmobs.datagen;

import com.google.common.hash.Hashing;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Paints one 16x16 sprite per spawn egg the mod registers, at
 * {@code textures/item/<egg>_spawn_egg.png}.
 *
 * <p>26.1 dropped the two-layer egg template and the pair of tints that went with it: vanilla now
 * ships a flat image per mob (87 of them) and points each egg's item definition straight at
 * {@code item/generated} with that image as {@code layer0}. A mod has to bring its own images, so
 * they are painted here from the two colours the egg was already registered with in
 * {@link TolkienEntities#SPAWN_EGG_COLORS} - adding an egg still means one line in
 * {@code TolkienEntities}, not a new asset.</p>
 *
 * <p>Each egg takes its silhouette from its family ({@link Silhouette}), so an orc, a spider and
 * an eagle do not all get the same oval, and the egg's second colour is spent on the pattern that
 * belongs to that family: a tunic, a saddle, a belt, wings, eyes. The shading is not drawn by
 * hand either - the rim is darkened from the silhouette's own edge and the body runs from a lit
 * top to a shaded bottom, which is what stops a flat two-colour sprite from looking like a bug.</p>
 */
public class TolkienSpawnEggTextureProvider implements DataProvider {
    private static final int SIZE = 16;
    private static final String NAME_PREFIX = "entityttm";
    private static final String NAME_SUFFIX = "_spawn_egg";

    private static final char EMPTY = '.';
    private static final char ACCENT = 'O';

    private final PackOutput.PathProvider pathProvider;

    public TolkienSpawnEggTextureProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures/item");
    }

    /** The shapes an egg can take. A family that looks like nothing else gets its own. */
    enum Silhouette {
        /** A plain standing egg, with a tunic across it: orcs, men, elves, dwarves. */
        BIPED,
        /** Broad-shouldered and flat-topped, belted: trolls, ent, golems, the goblin king. */
        HULK,
        /** A hood tapering to a point, hem torn away, and two eyes under it: the dead. */
        WRAITH,
        /** Round body on legs, marked on the abdomen: spiders. */
        SPIDER,
        /** Ears, four feet and a saddle across the back: wargs, goats, the great beasts. */
        BEAST,
        /** Wings held out from the body: eagles, crebain, the fell beast. */
        WINGED,
        /** A small egg sitting low: rats, squirrels, frogs, the swarm. */
        SMALL
    }

    /** The silhouettes, one row per line of the sprite. */
    private static final Map<Silhouette, String[]> MASKS = masks();

    /** Which silhouette each egg's family takes; anything not named here gets a plain egg. */
    private static final Map<String, Silhouette> FAMILIES = families();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        try {
            for (Map.Entry<DeferredItem<Item>, TolkienEntities.SpawnEggColors> entry : TolkienEntities.SPAWN_EGG_COLORS.entrySet()) {
                Identifier item = BuiltInRegistries.ITEM.getKey(entry.getKey().get());
                Silhouette silhouette = FAMILIES.getOrDefault(familyOf(item.getPath()), Silhouette.BIPED);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ImageIO.write(paint(silhouette, entry.getValue()), "png", bytes);
                byte[] data = bytes.toByteArray();

                Identifier sprite = TolkienMobsMain.resLoc(item.getPath());
                cache.writeIfNeeded(this.pathProvider.file(sprite, "png"), data, Hashing.sha1().hashBytes(data));
            }
            return CompletableFuture.completedFuture(null);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write the spawn egg sprites", exception);
        }
    }

    /** Strips {@code entityttm} and {@code _spawn_egg}, leaving the name a shape is chosen by. */
    private static String familyOf(String itemPath) {
        String name = itemPath.startsWith(NAME_PREFIX) ? itemPath.substring(NAME_PREFIX.length()) : itemPath;
        return name.endsWith(NAME_SUFFIX) ? name.substring(0, name.length() - NAME_SUFFIX.length()) : name;
    }

    /**
     * Draws the silhouette in the egg's two colours: the body takes the base colour and the
     * pattern the highlight, which is the pairing the old two-layer egg used.
     */
    private static BufferedImage paint(Silhouette silhouette, TolkienEntities.SpawnEggColors colors) {
        String[] mask = MASKS.get(silhouette);
        int top = top(mask);
        int bottom = bottom(mask);
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                char cell = mask[y].charAt(x);
                if (cell == EMPTY) {
                    continue;
                }
                int rgb = cell == ACCENT ? colors.highlight() : colors.background();
                image.setRGB(x, y, 0xFF000000 | shade(rgb, lighting(mask, x, y, top, bottom)));
            }
        }
        return image;
    }

    /**
     * How much light a cell gets. The rim of the silhouette is darkened into an outline, and the
     * body runs from a lit crown to a shaded base, a little brighter towards the top left, so the
     * egg reads as round however simple its two colours are.
     */
    private static float lighting(String[] mask, int x, int y, int top, int bottom) {
        float lit = 1.10F - 0.28F * ((y - top) / (float) Math.max(1, bottom - top));
        lit += 0.05F * (1.0F - (x + y) / 30.0F);

        boolean rim = !isSolid(mask, x - 1, y) || !isSolid(mask, x + 1, y)
                || !isSolid(mask, x, y - 1) || !isSolid(mask, x, y + 1);
        return rim ? lit * 0.68F : lit;
    }

    private static boolean isSolid(String[] mask, int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && mask[y].charAt(x) != EMPTY;
    }

    /** The first and last row of the sprite that are drawn, which the shading runs between. */
    private static int top(String[] mask) {
        for (int y = 0; y < SIZE; y++) {
            if (!mask[y].equals("................")) {
                return y;
            }
        }
        return 0;
    }

    private static int bottom(String[] mask) {
        for (int y = SIZE - 1; y >= 0; y--) {
            if (!mask[y].equals("................")) {
                return y;
            }
        }
        return SIZE - 1;
    }

    /** Scales a packed RGB colour, keeping it inside 0-255 per channel. */
    private static int shade(int rgb, float factor) {
        int red = clamp(Math.round(((rgb >> 16) & 0xFF) * factor));
        int green = clamp(Math.round(((rgb >> 8) & 0xFF) * factor));
        int blue = clamp(Math.round((rgb & 0xFF) * factor));
        return (red << 16) | (green << 8) | blue;
    }

    private static int clamp(int channel) {
        return Math.max(0, Math.min(255, channel));
    }

    private static Map<Silhouette, String[]> masks() {
        Map<Silhouette, String[]> masks = new LinkedHashMap<>();

        masks.put(Silhouette.BIPED, new String[]{
                "................",
                "......XXXX......",
                ".....XXXXXX.....",
                ".....XXXXXX.....",
                "....XXXXXXXX....",
                "....XXXXXXXX....",
                "...XXOOXXXXXX...",   // markings
                "...XXOOXXXXXX...",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXOOXX..",
                "..XXXXXXXXOOXX..",
                "..XXXXXXXXXXXX..",
                "...XXXXXXXXXX...",
                "....XXXXXXXX....",
                "................"});

        masks.put(Silhouette.HULK, new String[]{
                "................",
                "....XXXXXXXX....",
                "...XXXXXXXXXX...",
                "..XXXXXXXXXXXX..",
                ".XXXXXXXXXXXXXX.",
                ".OOXXXXXXXXXXOO.",   // pauldrons
                ".OOXXXXXXXXXXOO.",
                ".OOXXXXXXXXXXOO.",
                ".XXXXXXXXXXXXXX.",
                ".XXXXXXXXXXXXXX.",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXXXXX..",
                "...XXXXXXXXXX...",
                "...XXX....XXX...",
                "...XXX....XXX...",
                "................"});

        masks.put(Silhouette.WRAITH, new String[]{
                "................",
                ".......XX.......",
                "......XXXX......",
                "......XXXX......",
                ".....XXXXXX.....",
                ".....OXXXXO.....",   // eyes under the hood
                "....XXXXXXXX....",
                "...XXXXXXXXXX...",
                "...XXXXXXXXXX...",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXXXXX..",
                ".XXXXXXXXXXXXXX.",
                ".XXX..XXXX..XXX.",   // torn hem
                "................"});

        masks.put(Silhouette.SPIDER, new String[]{
                "................",
                "................",
                "................",
                ".....XXXXXX.....",
                "...XXXXXXXXXX...",
                "..XXXXXXXXXXXX..",
                "XX.XXXXXXXXXX.XX",
                "X.XXXXXXXXXXXX.X",
                "..XXXXXXXXXXXX..",
                "X.XXXXXOOXXXXX.X",   // abdomen marking
                "X.XXXXOOOOXXXX.X",
                "XX.XXXXOOXXXX.XX",
                "..XXXXXXXXXXXX..",
                "...XXXXXXXXXX...",
                "....XXXXXXXX....",
                "................"});

        masks.put(Silhouette.BEAST, new String[]{
                "................",
                "...XX......XX...",
                "...XXXX..XXXX...",
                "....XXXXXXXX....",
                "....XXXXXXXX....",
                "...XXXXXXXXXX...",
                "..XXXXXXXXXXXX..",
                "..XXOOXXXXOOXX..",   // saddle
                "..XXOOXXXXOOXX..",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXXXXX..",
                "...XXXXXXXXXX...",
                "...XXXXXXXXXX...",
                "...XX......XX...",
                "................"});

        masks.put(Silhouette.WINGED, new String[]{
                "................",
                "......XXXX......",
                ".....XXXXXX.....",
                "....XXXXXXXX....",
                "...XXXXXXXXXX...",
                "XX..XXXXXXXX..XX",
                "OOO.XXXXXXXX.OOO",   // wing tips
                "OOO.XXXXXXXX.OOO",
                "OOOXXXXXXXXXXOOO",
                "..XXXXXXXXXXXX..",
                "..XXXXXXXXXXXX..",
                "...XXXXXXXXXX...",
                "...XXXXXXXXXX...",
                "....XXXXXXXX....",
                "................",
                "................"});

        masks.put(Silhouette.SMALL, new String[]{
                "................",
                "................",
                "................",
                "................",
                "................",
                "......XXXX......",
                ".....XXXXXX.....",
                "....XXXXXXXX....",
                "....XXOOOOXX....",   // spot
                "...XXXOOOOXXX...",
                "...XXXOOOOXXX...",
                "...XXXXXXXXXX...",
                "....XXXXXXXX....",
                ".....XXXXXX.....",
                "................",
                "................"});

        for (Map.Entry<Silhouette, String[]> entry : masks.entrySet()) {
            String[] rows = entry.getValue();
            if (rows.length != SIZE) {
                throw new IllegalStateException(entry.getKey() + " has " + rows.length + " rows; an egg is " + SIZE);
            }
            for (int y = 0; y < SIZE; y++) {
                if (rows[y].length() != SIZE) {
                    throw new IllegalStateException(entry.getKey() + " row " + y + " is " + rows[y].length()
                            + " wide; an egg is " + SIZE);
                }
            }
        }
        return masks;
    }

    private static Map<String, Silhouette> families() {
        Map<String, Silhouette> families = new LinkedHashMap<>();

        for (String name : "rat squirrel gecko frog swarm hobbit".split(" ")) {
            families.put(name, Silhouette.SMALL);
        }
        for (String name : "auroch goat warg mumakil shadowfax nazgulsteed".split(" ")) {
            families.put(name, Silhouette.BEAST);
        }
        for (String name : "mirkwoodspider shelob watcher deepclaw".split(" ")) {
            families.put(name, Silhouette.SPIDER);
        }
        for (String name : "nazgul barrow fellspirit enragedfellspirit oathbreaker witchking".split(" ")) {
            families.put(name, Silhouette.WRAITH);
        }
        for (String name : "crebain greateagle gwahir thrush fellbeast balrog".split(" ")) {
            families.put(name, Silhouette.WINGED);
        }
        for (String name : "troll treeent elementalgolem mithrilgolem morgulirongolem rockgolem minotaur goblinking mimicchest".split(" ")) {
            families.put(name, Silhouette.HULK);
        }
        for (String name : "brigand duergar dwarf elves goblin gollum haradrim human huron istari mordororc orc orc_trader romiewalker southron southron_npc swamphag urukhai".split(" ")) {
            families.put(name, Silhouette.BIPED);
        }
        return families;
    }

    @Override
    public String getName() {
        return "Tolkienmobs - Spawn egg sprites";
    }
}
