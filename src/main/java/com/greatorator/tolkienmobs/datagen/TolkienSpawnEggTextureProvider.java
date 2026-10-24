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
import java.util.Arrays;
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
 * <p>Every egg keeps the shape a spawn egg is known for. What tells them apart is the face, the way
 * vanilla's differ: an orc's jaw and tusks, a goblin's grin, a spider's four eyes, a beast's muzzle,
 * a bird's beak, a wraith's glow under its hood, a troll's brow and teeth, a tiny creature's snout,
 * a balrog's flame. The two colours stay the ones the egg registered with, so the eggs still fall
 * into the groups the old tinted eggs did: the horde, the rings, the beasts, the little things,
 * the free peoples and the bosses each share a base tone and vary in their spots.</p>
 *
 * <p>Shading is worked out rather than drawn: the rim of the egg is darkened from its own edge, the
 * crown is lit and the base sinks, and a face's eyes and gaps are painted near-black.</p>
 */
public class TolkienSpawnEggTextureProvider implements DataProvider {
    private static final int SIZE = 16;
    private static final String NAME_PREFIX = "entityttm";
    private static final String NAME_SUFFIX = "_spawn_egg";

    private static final char EMPTY = '.';
    private static final char BODY = 'X';
    private static final char ACCENT = 'O';
    private static final char DARK = 'D';

    /** The egg itself: one shape, the one every spawn egg has. The dark rim is added when painting. */
    private static final String[] EGG = {
            "................",
            "......XXXX......",
            "....XXXXXXXX....",
            "...XXXXXXXXXX...",
            "...XXXXXXXXXX...",
            "..XXXXXXXXXXXX..",
            "..XXXXXXXXXXXX..",
            "..XXXXXXXXXXXX..",
            ".XXXXXXXXXXXXXX.",
            ".XXXXXXXXXXXXXX.",
            ".XXXXXXXXXXXXXX.",
            "..XXXXXXXXXXXX..",
            "..XXXXXXXXXXXX..",
            "...XXXXXXXXXX...",
            "....XXXXXXXX....",
            "................"};

    /** The first and last row the egg is drawn on, which its shading runs between. */
    private static final int TOP = firstRow();
    private static final int BOTTOM = lastRow();

    private final PackOutput.PathProvider pathProvider;

    public TolkienSpawnEggTextureProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures/item");
    }

    /** What a mob's face is drawn as. */
    enum Face {
        /** Angry eyes over a wide jaw: the orcs, the uruks, the men who march with them. */
        ORC,
        /** Two calm eyes and a nose: elves, dwarves, hobbits, men. */
        MAN,
        /** Squinting eyes over a row of teeth: goblins and Gollum. */
        GOBLIN,
        /** Two points of glow under a hood: the wraiths and the dead. */
        WRAITH,
        /** Four eyes above a pair of fangs: spiders. */
        SPIDER,
        /** A broad muzzle with two nostrils: wargs, goats, horses, the great beasts. */
        BEAST,
        /** A beak under one eye: eagles, crebain, the fell beast. */
        BIRD,
        /** A heavy brow over a row of teeth: trolls, ent, golems, the goblin king. */
        TROLL,
        /** A hooked nose and a heavy eye: the swamp hag. */
        HAG,
        /** A crown of flame: the balrog. */
        FLAME,
        /** Two small eyes and a snout: rats, squirrels, frogs, the swarm. */
        CRITTER
    }

    /** Which face each egg's mob takes; anything not named gets a man's. */
    private static final Map<String, Face> MOBS = mobs();

    /** The faces, laid over the egg; a '.' leaves the egg's own colour showing. */
    private static final Map<Face, String[]> FACES = faces();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        try {
            for (Map.Entry<DeferredItem<Item>, TolkienEntities.SpawnEggColors> entry : TolkienEntities.SPAWN_EGG_COLORS.entrySet()) {
                Identifier item = BuiltInRegistries.ITEM.getKey(entry.getKey().get());
                Face face = MOBS.getOrDefault(mobOf(item.getPath()), Face.MAN);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ImageIO.write(paint(face, entry.getValue()), "png", bytes);
                byte[] data = bytes.toByteArray();

                Identifier sprite = TolkienMobsMain.resLoc(item.getPath());
                cache.writeIfNeeded(this.pathProvider.file(sprite, "png"), data, Hashing.sha1().hashBytes(data));
            }
            return CompletableFuture.completedFuture(null);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write the spawn egg sprites", exception);
        }
    }

    /** Strips {@code entityttm} and {@code _spawn_egg}, leaving the mob's name. */
    private static String mobOf(String itemPath) {
        String name = itemPath.startsWith(NAME_PREFIX) ? itemPath.substring(NAME_PREFIX.length()) : itemPath;
        return name.endsWith(NAME_SUFFIX) ? name.substring(0, name.length() - NAME_SUFFIX.length()) : name;
    }

    /**
     * Draws the egg in the colours it registered, then the face over it: the body takes the base
     * colour, the face's pattern the highlight, and its eyes the dark that makes a face read.
     */
    private static BufferedImage paint(Face face, TolkienEntities.SpawnEggColors colors) {
        String[] features = FACES.get(face);
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (EGG[y].charAt(x) != BODY) {
                    continue;
                }
                char cell = features[y].charAt(x) == EMPTY ? BODY : features[y].charAt(x);
                int rgb = switch (cell) {
                    case ACCENT -> colors.highlight();
                    case DARK -> shade(colors.background(), 0.22F);
                    default -> colors.background();
                };
                float light = cell == DARK ? 1.0F : lighting(x, y);
                image.setRGB(x, y, 0xFF000000 | shade(rgb, light));
            }
        }
        return image;
    }

    /**
     * How much light a cell gets: the rim of the egg is darkened into an outline, and the body runs
     * from a lit crown to a shaded base, a little brighter towards the top left, so the egg reads
     * as round from two colours.
     */
    private static float lighting(int x, int y) {
        float lit = 1.10F - 0.28F * ((y - TOP) / (float) Math.max(1, BOTTOM - TOP));
        lit += 0.05F * (1.0F - (x + y) / 30.0F);
        return isSolid(x - 1, y) && isSolid(x + 1, y) && isSolid(x, y - 1) && isSolid(x, y + 1) ? lit : lit * 0.68F;
    }

    private static boolean isSolid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && EGG[y].charAt(x) == BODY;
    }

    private static int firstRow() {
        for (int y = 0; y < SIZE; y++) {
            if (EGG[y].indexOf(BODY) >= 0) {
                return y;
            }
        }
        return 0;
    }

    private static int lastRow() {
        for (int y = SIZE - 1; y >= 0; y--) {
            if (EGG[y].indexOf(BODY) >= 0) {
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

    private static Map<Face, String[]> faces() {
        Map<Face, String[]> faces = new LinkedHashMap<>();

        faces.put(Face.ORC, new FaceBuilder()
                .row(6, ".....DD..DD.....")    // narrow eyes
                .row(8, "......O..O......")    // tusks, rising past the jaw
                .row(9, "......ODDO......")    // mouth between them
                .rows());

        faces.put(Face.MAN, new FaceBuilder()
                .row(6, ".....D....D.....")    // eyes
                .row(8, ".......OO.......")    // nose
                .rows());

        faces.put(Face.GOBLIN, new FaceBuilder()
                .row(6, ".....DD..DD.....")    // eyes
                .row(9, ".....DDDDDD.....")    // wide grin
                .row(10, "......O..O......")    // two teeth hanging from it
                .rows());

        faces.put(Face.WRAITH, new FaceBuilder()
                .row(5, ".....DDDDDD.....")    // the shadow of a hood
                .row(7, "......O..O......")    // two points of glow beneath it
                .rows());

        faces.put(Face.SPIDER, new FaceBuilder()
                .row(6, ".....DD..DD.....")    // the big pair of eyes
                .row(7, "......D..D......")    // and the small pair beneath
                .row(9, ".......DD.......")    // fangs
                .rows());

        faces.put(Face.BEAST, new FaceBuilder()
                .row(6, ".....D....D.....")    // eyes
                .row(8, ".......OO.......")    // muzzle
                .row(9, ".......DD.......")    // mouth under it
                .rows());

        faces.put(Face.BIRD, new FaceBuilder()
                .row(6, ".....D..........")    // eye
                .row(7, "......OOOO......")    // beak
                .row(8, ".......OO.......")
                .rows());

        faces.put(Face.TROLL, new FaceBuilder()
                .row(5, "....DDDDDDDD....")    // one heavy brow
                .row(7, ".....D....D.....")    // eyes under it
                .row(9, ".......DD.......")    // mouth
                .row(10, "......OOOO......")    // and an underbite of teeth
                .rows());

        faces.put(Face.HAG, new FaceBuilder()
                .row(6, ".....D..........")    // one eye; the other sits in shadow
                .row(7, ".......O........")    // hooked nose
                .row(8, "......O.........")
                .rows());

        faces.put(Face.FLAME, new FaceBuilder()
                .row(2, "......O.O.......")    // a crown of fire
                .row(3, ".....OOOOOO.....")
                .row(4, "....OOOOOOOO....")
                .row(6, ".....D....D.....")    // eyes below it
                .rows());

        faces.put(Face.CRITTER, new FaceBuilder()
                .row(7, ".....D....D.....")    // eyes
                .row(8, "......DD........")    // snout
                .rows());

        for (Map.Entry<Face, String[]> entry : faces.entrySet()) {
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
        return faces;
    }

    /** Collects the rows of one face; every row it is not given stays transparent. */
    private static final class FaceBuilder {
        private final String[] rows = new String[SIZE];

        FaceBuilder() {
            Arrays.fill(this.rows, "................");
        }

        FaceBuilder row(int y, String row) {
            this.rows[y] = row;
            return this;
        }

        String[] rows() {
            return this.rows;
        }
    }

    private static Map<String, Face> mobs() {
        Map<String, Face> mobs = new LinkedHashMap<>();

        add(mobs, Face.ORC, "orc mordororc orc_trader urukhai brigand haradrim");
        add(mobs, Face.MAN, "human elves dwarf duergar hobbit southron southron_npc istari romiewalker huron");
        add(mobs, Face.GOBLIN, "goblin goblinking gollum");
        add(mobs, Face.WRAITH, "nazgul barrow fellspirit enragedfellspirit oathbreaker witchking");
        add(mobs, Face.SPIDER, "mirkwoodspider shelob watcher deepclaw");
        add(mobs, Face.BEAST, "warg auroch goat mumakil shadowfax nazgulsteed");
        add(mobs, Face.BIRD, "greateagle gwahir crebain thrush fellbeast");
        add(mobs, Face.TROLL, "troll treeent rockgolem mithrilgolem morgulirongolem elementalgolem mimicchest minotaur");
        add(mobs, Face.HAG, "swamphag");
        add(mobs, Face.FLAME, "balrog");
        add(mobs, Face.CRITTER, "rat squirrel gecko frog swarm");

        return mobs;
    }

    private static void add(Map<String, Face> mobs, Face face, String names) {
        for (String name : names.split(" ")) {
            mobs.put(name, face);
        }
    }

    @Override
    public String getName() {
        return "Tolkienmobs - Spawn egg sprites";
    }
}
