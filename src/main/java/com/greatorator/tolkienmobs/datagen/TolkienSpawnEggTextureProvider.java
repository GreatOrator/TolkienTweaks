package com.greatorator.tolkienmobs.datagen;

import com.google.common.hash.Hashing;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Paints one 16x16 sprite per spawn egg the mod registers, at
 * {@code textures/item/<egg>_spawn_egg.png}.
 *
 * <p>26.1 dropped the two-layer egg template and the pair of tints that went with it: vanilla now
 * ships a flat image per mob (87 of them) and points each egg's item definition straight at
 * {@code item/generated} with that image as {@code layer0}. A mod has to bring its own images, so
 * they are painted here from art the mod already has - the 16x16 entity icons - rather than drawn
 * by hand for every mob.</p>
 *
 * <p>Each egg is the one shape a spawn egg is known for, with the mob's icon filling it, the way
 * vanilla's eggs show a mob's face. The icon is mixed a little way towards the colour the egg was
 * registered with, so the eggs still fall into the groups the old tinted eggs did - the horde, the
 * ring-bearers, the beasts, the little things, the free peoples and the bosses each share a base
 * tone - while the face underneath stays its own. The rim of the egg is darkened and the body runs
 * from a lit crown to a shaded base, so a flat icon still reads as a rounded egg.</p>
 *
 * <p>Only the two horses have no icon to borrow (the mod ships none for a steed); their eggs take
 * a face painted from the glyphs in {@link Face} instead.</p>
 */
public class TolkienSpawnEggTextureProvider implements DataProvider {
    private static final int SIZE = 16;
    private static final String NAME_PREFIX = "entityttm";
    private static final String NAME_SUFFIX = "_spawn_egg";

    /** How far an icon's hue is pulled towards the egg's own; at 1 they all match it exactly. */
    private static final float TINT = 0.7F;

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

    private final ResourceManager resources;
    private final PackOutput.PathProvider pathProvider;

    public TolkienSpawnEggTextureProvider(PackOutput output, ResourceManager resources) {
        this.resources = resources;
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures/item");
    }

    /** What a mob's face is drawn as when the mod has no icon of it. */
    enum Face {
        /** Two calm eyes and a nose. */
        MAN,
        /** A broad muzzle with two nostrils. */
        BEAST,
        /** Four eyes above a pair of fangs. */
        SPIDER,
        /** A hooked nose and a heavy eye. */
        HAG,
        /** Two small eyes and a snout. */
        CRITTER
    }

    /** The mobs with no icon, and the face painted for them instead. */
    private static final Map<String, Face> FACELESS = faceless();

    /** The faces above, laid over the egg; a '.' leaves the egg's own colour showing. */
    private static final Map<Face, String[]> FACES = faces();

    /** Which icon each mob's egg is painted from, under {@code textures/entity_icon/}. */
    private static final Map<String, String> ICONS = icons();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        try {
            for (Map.Entry<DeferredItem<Item>, TolkienEntities.SpawnEggColors> entry : TolkienEntities.SPAWN_EGG_COLORS.entrySet()) {
                Identifier item = BuiltInRegistries.ITEM.getKey(entry.getKey().get());
                String mob = mobOf(item.getPath());

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ImageIO.write(paint(mob, entry.getValue()), "png", bytes);
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
     * Draws the mob's icon into the egg's shape, mixed towards the colour the egg registered with,
     * or - for a mob with no icon - paints its face from the glyphs instead.
     */
    private BufferedImage paint(String mob, TolkienEntities.SpawnEggColors colors) throws IOException {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        BufferedImage icon = readIcon(mob);
        String[] glyphs = icon == null ? FACES.get(FACELESS.getOrDefault(mob, Face.MAN)) : null;

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (EGG[y].charAt(x) != BODY) {
                    continue;
                }
                float light = lighting(x, y);
                int rgb;

                if (icon != null) {
                    int pixel = icon.getRGB(x, y) & 0xFFFFFF;
                    rgb = isEdge(x, y) ? rim(pixel, colors.background()) : tint(pixel, colors.background());
                } else {
                    char cell = glyphs[y].charAt(x);
                    rgb = switch (cell) {
                        case ACCENT -> colors.highlight();
                        case DARK -> shade(colors.background(), 0.22F);
                        default -> colors.background();
                    };
                    light = cell == DARK ? 1.0F : light;
                }
                image.setRGB(x, y, 0xFF000000 | shade(rgb, light));
            }
        }
        return image;
    }

    /** The mob's icon, if the mod ships one; icons are 16x16, so they drop straight onto the egg. */
    private BufferedImage readIcon(String mob) throws IOException {
        String path = ICONS.get(mob);
        if (path == null) {
            return null;
        }
        Identifier location = TolkienMobsMain.resLoc("textures/entity_icon/" + path);
        Optional<Resource> resource = this.resources.getResource(location);
        if (resource.isEmpty()) {
            throw new IOException("Missing " + location + ", which the spawn egg sprites are built from");
        }
        try (InputStream stream = resource.get().open()) {
            BufferedImage icon = ImageIO.read(stream);
            if (icon == null || icon.getWidth() != SIZE || icon.getHeight() != SIZE) {
                throw new IOException(location + " is not a " + SIZE + "x" + SIZE + " icon");
            }
            return icon;
        }
    }

    /**
     * Pulls an icon pixel's hue and saturation towards the egg's colour, keeping its brightness
     * and stretching that brightness a little. Blending the colours outright greys the face out;
     * keeping the light and dark of it is what lets a face survive while every egg in a group
     * still reads as one tone.
     */
    private static int tint(int rgb, int toward) {
        float[] base = Color.RGBtoHSB((toward >> 16) & 0xFF, (toward >> 8) & 0xFF, toward & 0xFF, null);
        float[] pixel = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);

        float hue = (base[0] + (pixel[0] - base[0]) * (1.0F - TINT) + 1.0F) % 1.0F;
        float saturation = pixel[1] + (base[1] - pixel[1]) * (TINT * 0.5F);
        float brightness = clamp((pixel[2] - 0.45F) * 1.45F + 0.47F);
        return Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
    }

    /**
     * The cell along the egg's edge: the egg's own colour, so the icon inside it ends where the
     * egg does rather than showing its own square border a pixel in from the rim.
     */
    private static int rim(int rgb, int toward) {
        float[] base = Color.RGBtoHSB((toward >> 16) & 0xFF, (toward >> 8) & 0xFF, toward & 0xFF, null);
        float[] pixel = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
        return Color.HSBtoRGB(base[0], base[1], (pixel[2] + base[2]) * 0.5F) & 0xFFFFFF;
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    /** Whether the egg has no body cell next to this one, which is what makes it its rim. */
    private static boolean isEdge(int x, int y) {
        return !isSolid(x - 1, y) || !isSolid(x + 1, y) || !isSolid(x, y - 1) || !isSolid(x, y + 1);
    }

    /**
     * How much light a cell gets: the rim of the egg is darkened into an outline, and the body runs
     * from a lit crown to a shaded base, a little brighter towards the top left.
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

        faces.put(Face.MAN, new FaceBuilder()
                .row(6, ".....D....D.....")    // eyes
                .row(8, ".......OO.......")    // nose
                .rows());

        faces.put(Face.BEAST, new FaceBuilder()
                .row(6, ".....D....D.....")    // eyes
                .row(8, ".......OO.......")    // muzzle
                .row(9, ".......DD.......")    // mouth under it
                .rows());

        faces.put(Face.SPIDER, new FaceBuilder()
                .row(6, ".....DD..DD.....")    // the big pair of eyes
                .row(7, "......D..D......")    // and the small pair beneath
                .row(9, ".......DD.......")    // fangs
                .rows());

        faces.put(Face.HAG, new FaceBuilder()
                .row(6, ".....D..........")    // one eye; the other sits in shadow
                .row(7, ".......O........")    // hooked nose
                .row(8, "......O.........")
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

    /** The mobs the mod has no icon for. */
    private static Map<String, Face> faceless() {
        Map<String, Face> faceless = new LinkedHashMap<>();

        faceless.put("nazgulsteed", Face.BEAST);
        faceless.put("shadowfax", Face.BEAST);

        return faceless;
    }

    private static Map<String, String> icons() {
        Map<String, String> icons = new LinkedHashMap<>();

        icons.put("auroch", "auroch/auroch1.png");
        icons.put("balrog", "balrog.png");
        icons.put("barrow", "barrowwight/barrowwight1.png");
        icons.put("brigand", "tmbrigand/brigand0.png");
        icons.put("crebain", "birds/crebain.png");
        icons.put("deepclaw", "tmdeepclaw/tmdeepclaw1.png");
        icons.put("duergar", "tmduergar/tmduergar1.png");
        icons.put("dwarf", "dwarf/dwarf1.png");
        icons.put("elementalgolem", "elementalgolem/elemental_golem_earth.png");
        icons.put("elves", "elves/elves1.png");
        icons.put("enragedfellspirit", "fellspirit/fellspirit1.png");
        icons.put("fellbeast", "fellbeast/fellbeast.png");
        icons.put("fellspirit", "fellspirit/fellspirit2.png");
        icons.put("frog", "toaddle/toaddle_green.png");
        icons.put("gecko", "toaddle/toaddle_green.png");
        icons.put("goat", "goat/goat1.png");
        icons.put("goblin", "goblin/goblin1.png");
        icons.put("goblinking", "goblin/goblin2.png");
        icons.put("gollum", "gollum.png");
        icons.put("greateagle", "birds/greateagle.png");
        icons.put("gwahir", "birds/greateagle.png");
        icons.put("haradrim", "tmharadrim/haradrim0.png");
        icons.put("hobbit", "hobbit/hobbit0.png");
        icons.put("human", "human/human1.png");
        icons.put("istari", "human/human4.png");
        icons.put("huron", "huron.png");
        icons.put("mimicchest", "mimicchest/mimicchest1.png");
        icons.put("minotaur", "minotaur.png");
        icons.put("mirkwoodspider", "mirkwoodspider.png");
        icons.put("mithrilgolem", "elementalgolem/elemental_golem_mithril.png");
        icons.put("mordororc", "orc/mordororc1.png");
        icons.put("morgulirongolem", "elementalgolem/elemental_golem_morgul.png");
        icons.put("mumakil", "mumakil/mumakil1.png");
        icons.put("nazgul", "nazgul.png");
        icons.put("oathbreaker", "oathbreaker/oathbreaker1.png");
        icons.put("orc", "orc/mordororc2.png");
        icons.put("orc_trader", "orc/mordororc3.png");
        icons.put("rat", "entityttmrat/entityttmrat0.png");
        icons.put("rockgolem", "elementalgolem/elemental_golem_none.png");
        icons.put("romiewalker", "romiewalker/romiewalker0.png");
        icons.put("shelob", "tmshelob.png");
        icons.put("southron", "tmharadrim/haradrim1.png");
        icons.put("southron_npc", "tmharadrim/haradrim2.png");
        icons.put("squirrel", "sosquirrel/sosquirrel.png");
        icons.put("swamphag", "human/human12.png");
        icons.put("swarm", "midgeflies.png");
        icons.put("thrush", "birds/thrush.png");
        icons.put("treeent", "treeent/treeent1.png");
        icons.put("troll", "troll/cave_troll1.png");
        icons.put("urukhai", "urukhai/urukhai1.png");
        icons.put("warg", "warg/warg1.png");
        icons.put("watcher", "tmshelob.png");
        icons.put("witchking", "witchking.png");

        return icons;
    }

    @Override
    public String getName() {
        return "Tolkienmobs - Spawn egg sprites";
    }
}
