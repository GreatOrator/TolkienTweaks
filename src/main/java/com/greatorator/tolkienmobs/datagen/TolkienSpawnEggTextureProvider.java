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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Paints one 16x16 sprite per spawn egg the mod registers, at {@code textures/item/<egg>_spawn_egg.png}.
 *
 * <p>26.1 dropped the two-layer egg template and the pair of tints that went with it: vanilla now ships a
 * flat image per mob and points each egg's item definition straight at {@code item/generated} with that
 * image as {@code layer0}. A mod has to bring its own images, so they are painted here.</p>
 *
 * <p>An egg is drawn the way vanilla draws one: the shell in the mob's own colour, shaded from a lit crown
 * to a shaded base and rimmed in a darker line, with the mob's face set into the middle of it. What a face
 * is made of is the mob's business - a man's two eyes and nose, an orc's slitted eyes over tusks, a
 * spider's cluster of eyes above its fangs, a mumakil's tusks and trunk - so the egg says what will hatch
 * before it is thrown. Each mob therefore names one face from {@link Face}, and the four colours that face
 * is painted in: its shell, its markings, its eyes, and its bone or teeth.</p>
 *
 * <p>The faces are written as glyphs rather than drawn: one string of cells per row, in the small alphabet
 * below, laid over the shell. A cell the glyph leaves empty keeps the shell's own colour, which is what
 * lets a face sit in a coloured egg without a box around it.</p>
 */
public class TolkienSpawnEggTextureProvider implements DataProvider {
    private static final int SIZE = 16;
    private static final String NAME_PREFIX = "entityttm";
    private static final String NAME_SUFFIX = "_spawn_egg";

    /** Cells a glyph may use. Everything else is shell. */
    private static final char NONE = '.';
    private static final char SHELL = 'X';
    private static final char MARK = 'a';
    private static final char DARK = 'd';
    private static final char BONE = 'w';
    /** A lit marking - an eye that glows, a flame - which keeps its brightness instead of its cell's. */
    private static final char GLOW = 'g';

    /**
     * The egg's silhouette. Its crown is two pixels narrower than its waist, which is what stops it reading
     * as a pebble, and it leaves a one-pixel margin so the sprite has room to breathe in a slot.
     */
    private static final String[] EGG = {
            "................",
            ".......XX.......",
            ".....XXXXXX.....",
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
            "....XXXXXXXX...."};

    /** The first and last row the egg is drawn on, which its shading runs between. */
    private static final int TOP = 1;
    private static final int BOTTOM = 15;

    /** How far apart in brightness a face colour and the shell behind it have to be on the 0-255 scale. */
    private static final int CONTRAST = 46;

    /** Every way the mod's mobs can be told apart on an egg. */
    enum Face {
        /** A small face with wide-set eyes: the beasts of the field and the little things. */
        SMALL,
        /** Two eyes, a nose and a mouth: a Man with nothing on his head. */
        HUMAN,
        /** The same face under a shaggy beard. */
        BEARDED,
        /** A cloth cowl over the crown, the face in shadow under it. */
        HOODED,
        /** A steel cap with a nasal bar down the face: the alliance's soldiers. */
        HELMED,
        /** A wrap across the brow with a fold falling either side: the desert folk. */
        CLOTH,
        /** A pointed hat with a brim, and a beard under it: a wizard. */
        HAT,
        /** A heavy brow, a broad nose and a beard. */
        DWARF,
        /** High cheeks under a fall of hair. */
        ELF,
        /** A round face with a small mouth. */
        HOBBIT,
        /** Slitted, lit eyes over a mouth of tusks. */
        ORC,
        /** Wide ears either side of a wide grin. */
        GOBLIN,
        /** A black brow over a jaw of teeth. */
        URUH,
        /** Small eyes set far apart over a heavy jaw. */
        TROLL,
        /** Hollow sockets, a nasal hole and a row of teeth. */
        UNDEAD,
        /** A hood with two points of light under it, and the mouth open. */
        WRAITH,
        /** A hood with loops of mail over it and a slot for the eyes: the ring-wraiths. */
        HELM_SLIT,
        /** A hood crowned in iron spikes: the wraith that wears a crown. */
        HELM_SPIKED,
        /** One lit eye, a hooked nose and a thin mouth. */
        HAG,
        /** Two eyes too large for the face, over a wide grin: the thin one. */
        GAUNT,
        /** Eyes on the crown of the head above a mouth ruled right across. */
        FROG,
        /** Eyes out at the rim of the face and a small closed smile. */
        LIZARD,
        /** A pointed snout with whiskers. */
        RODENT,
        /** A small nose over two front teeth. */
        NIBBLER,
        /** No face at all: a scatter of specks, the way a swarm reads. */
        SWARM,
        /** A bark face: two seams of grain and lit eyes between them. */
        ENT,
        /** Blocks of stone seamed together, with lit eyes. */
        GOLEM,
        /** A cluster of eyes over a pair of fangs. */
        SPIDER,
        /** A long muzzle with fangs below it. */
        WARG,
        /** A long muzzle between two pricked ears, with fangs. */
        WOLF,
        /** A long face with nostrils: the horses. */
        HORSE,
        /** A pair of horns over a broad snout. */
        AUROCH,
        /** Swept-back horns over a narrow muzzle. */
        GOAT,
        /** Tusks either side of a hanging trunk. */
        MUMAKIL,
        /** Horns, a broad nose and a ring through it. */
        MINOTAUR,
        /** A lid of teeth over a dark mouth and two lit eyes. */
        MIMIC,
        /** A hooked beak under two eyes. */
        EAGLE,
        /** A small pointed bill between two eyes: the songbirds. */
        BIRD,
        /** A scaled snout with teeth below it. */
        DRAGON,
        /** Flame above two burning eyes. */
        BALROG,
        /** One great eye over a fringe of tentacles. */
        WATCHER
    }

    /** One mob's egg: the face it wears and the four colours that face is painted in. */
    record Design(Face face, int shell, int mark, int dark, int bone) {
    }

    private final PackOutput.PathProvider pathProvider;

    public TolkienSpawnEggTextureProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures/item");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        try {
            for (Map.Entry<DeferredItem<Item>, TolkienEntities.SpawnEggColors> entry : TolkienEntities.SPAWN_EGG_COLORS.entrySet()) {
                Identifier item = BuiltInRegistries.ITEM.getKey(entry.getKey().get());
                Design design = DESIGNS.getOrDefault(mobOf(item.getPath()), FALLBACK);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ImageIO.write(paint(design, FACES.get(design.face())), "png", bytes);
                byte[] data = bytes.toByteArray();

                Identifier sprite = TolkienMobsMain.resLoc(item.getPath());
                cache.writeIfNeeded(this.pathProvider.file(sprite, "png"), data, Hashing.sha1().hashBytes(data));
            }
            return CompletableFuture.completedFuture(null);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write the spawn egg sprites", exception);
        }
    }

    @Override
    public String getName() {
        return "Spawn egg textures";
    }

    /** Strips {@code entityttm} and {@code _spawn_egg}, leaving the mob's name. */
    private static String mobOf(String itemPath) {
        String name = itemPath.startsWith(NAME_PREFIX) ? itemPath.substring(NAME_PREFIX.length()) : itemPath;
        return name.endsWith(NAME_SUFFIX) ? name.substring(0, name.length() - NAME_SUFFIX.length()) : name;
    }

    /** The shell, then the face laid over it in the mob's own colours. */
    private static BufferedImage paint(Design design, String[] face) {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (EGG[y].charAt(x) != SHELL) {
                    continue;
                }
                int rgb = edge(x, y)
                        ? shade(design.shell(), 0.62F)
                        : shade(design.shell(), lighting(x, y));
                image.setRGB(x, y, 0xFF000000 | rgb);
            }
        }

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                char cell = face[y].charAt(x);
                if (cell == NONE || EGG[y].charAt(x) != SHELL) {
                    continue;
                }
                int rgb = switch (cell) {
                    case MARK -> design.mark();
                    case DARK -> design.dark();
                    case BONE -> design.bone();
                    case GLOW -> design.mark();
                    default -> design.shell();
                };
                // A glow is its own light; everything else sits on the shell and takes its shading.
                int lit = cell == GLOW ? rgb : shade(rgb, lighting(x, y));
                // A marking that sits at the shell's own brightness disappears into it - a black horse's
                // muzzle on a black shell, a raven's beak on a raven egg - so a face is always pulled far
                // enough from the shell there to be seen.
                if (cell != GLOW) {
                    lit = separate(lit, shade(design.shell(), lighting(x, y)));
                }
                image.setRGB(x, y, 0xFF000000 | lit);
            }
        }

        return image;
    }

    /**
     * How much light a cell gets: the crown of the egg is lit and its base in shadow, with a little more
     * light towards the top left, the way a light from above and behind the shoulder falls.
     */
    private static float lighting(int x, int y) {
        float lit = 1.16F - 0.34F * ((y - TOP) / (float) Math.max(1, BOTTOM - TOP));
        lit += 0.06F * (1.0F - (x + y) / 30.0F);
        return lit;
    }

    /** Whether this cell sits on the egg's edge, where the shell darkens into its own outline. */
    private static boolean edge(int x, int y) {
        return !solid(x - 1, y) || !solid(x + 1, y) || !solid(x, y - 1) || !solid(x, y + 1);
    }

    private static boolean solid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && EGG[y].charAt(x) == SHELL;
    }

    /**
     * Pushes a face colour away from the shell colour behind it when the two are too close in brightness.
     * Hue alone is not enough at this size: two dark browns read as one flat egg.
     */
    private static int separate(int rgb, int behind) {
        int face = luma(rgb);
        int shell = luma(behind);
        int gap = Math.abs(face - shell);
        if (gap >= CONTRAST) {
            return rgb;
        }
        int target = face <= shell ? shell - CONTRAST : shell + CONTRAST;
        float factor = Math.max(0.05F, Math.min(2.6F, target / (float) Math.max(8, face)));
        return shade(rgb, factor);
    }

    private static int luma(int rgb) {
        return (int) (0.299F * ((rgb >> 16) & 0xFF) + 0.587F * ((rgb >> 8) & 0xFF) + 0.114F * (rgb & 0xFF));
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

    /** A glyph being built: each call paints cells at a row and column of the 16x16 grid. */
    private static final class Glyph {
        private final char[][] rows = new char[SIZE][SIZE];

        Glyph() {
            for (char[] row : rows) {
                java.util.Arrays.fill(row, NONE);
            }
        }

        Glyph put(int x, int y, String cells) {
            for (int i = 0; i < cells.length(); i++) {
                int column = x + i;
                if (y >= 0 && y < SIZE && column >= 0 && column < SIZE) {
                    rows[y][column] = cells.charAt(i);
                }
            }
            return this;
        }

        String[] rows() {
            String[] out = new String[SIZE];
            for (int y = 0; y < SIZE; y++) {
                out[y] = new String(rows[y]);
            }
            return out;
        }
    }

    private static Map<Face, String[]> faces() {
        Map<Face, String[]> faces = new LinkedHashMap<>();

        faces.put(Face.SMALL, new Glyph()
                .put(5, 7, "d").put(10, 7, "d")
                .put(7, 10, "dd")
                .rows());

        // A mouth is a short line, not a solid bar across the face: the lip shadow that went under it
        // read as a small beard on every Man in the set.
        faces.put(Face.HUMAN, new Glyph()
                .put(5, 7, "dd").put(9, 7, "dd")
                .put(7, 9, "a")
                .put(6, 11, "ddd")
                .rows());

        // A beard hugs the jaw rather than hanging under the mouth, or it reads as an open screaming mouth.
        faces.put(Face.BEARDED, new Glyph()
                .put(5, 7, "dd").put(9, 7, "dd")
                .put(7, 9, "aa")
                .put(6, 10, "aaaa")
                .put(4, 11, "a").put(11, 11, "a")
                .put(5, 12, "aaaaaa")
                .rows());

        faces.put(Face.HOODED, new Glyph()
                .put(5, 4, "aaaaaa")
                .put(4, 5, "aaaaaaaa")
                .put(3, 7, "aa").put(11, 7, "aa")
                .put(3, 9, "a").put(12, 9, "a")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(7, 10, "a")
                .put(6, 11, "ddd")
                .rows());

        faces.put(Face.HELMED, new Glyph()
                .put(5, 4, "wwwwww")
                .put(4, 5, "wwwwwwww")
                .put(7, 6, "ww").put(7, 7, "ww")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(7, 10, "a")
                .put(6, 11, "ddd")
                .rows());

        faces.put(Face.CLOTH, new Glyph()
                .put(4, 5, "aaaaaaaa")
                .put(3, 6, "aa").put(11, 6, "aa")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(7, 10, "a")
                .put(6, 11, "ddd")
                .rows());

        // The hat fills the crown so its silhouette is the cone, in the dark tone: drawn in the light one it
        // vanished into a pale egg and left a bald head with a band across it.
        faces.put(Face.HAT, new Glyph()
                .put(7, 1, "dd")
                .put(6, 2, "dddd")
                .put(5, 3, "dddddd")
                .put(4, 4, "dddddddd")
                .put(5, 7, "dd").put(9, 7, "dd")
                .put(7, 9, "a")
                .put(5, 11, "aaaaaa")
                .put(6, 12, "aaaa")
                .put(7, 13, "aa")
                .put(7, 14, "a")
                .rows());

        faces.put(Face.DWARF, new Glyph()
                .put(5, 6, "dddddd")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(7, 9, "aa")
                .put(4, 11, "aaaaaaaa")
                .put(5, 12, "aaaaaa")
                .rows());

        faces.put(Face.ELF, new Glyph()
                .put(4, 5, "aaaaaaaa")
                .put(3, 6, "aa").put(11, 6, "aa")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(6, 11, "dddd")
                .rows());

        faces.put(Face.HOBBIT, new Glyph()
                .put(4, 6, "aa").put(10, 6, "aa")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(7, 10, "dd")
                .rows());

        faces.put(Face.ORC, new Glyph()
                .put(5, 7, "gg").put(9, 7, "gg")
                .put(6, 10, "ddd")
                .put(6, 11, "w..w")
                .rows());

        faces.put(Face.GOBLIN, new Glyph()
                .put(2, 7, "aa").put(12, 7, "aa")
                .put(5, 7, "gg").put(9, 7, "gg")
                .put(6, 10, "dddd")
                .rows());

        // Brows angled down towards the nose, which is what keeps the face from wearing a hat.
        faces.put(Face.URUH, new Glyph()
                .put(4, 6, "dd").put(6, 7, "dd").put(9, 7, "dd").put(11, 6, "dd")
                .put(5, 8, "dd").put(10, 8, "dd")
                .put(5, 11, "ww..ww")
                .rows());

        faces.put(Face.TROLL, new Glyph()
                .put(6, 7, "dd").put(9, 7, "dd")
                .put(7, 9, "aa")
                .put(5, 11, "w.w.w")
                .rows());

        faces.put(Face.UNDEAD, new Glyph()
                .put(5, 7, "dd").put(9, 7, "dd")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(7, 9, "dd")
                .put(5, 11, "w.w.w.w")
                .rows());

        faces.put(Face.WRAITH, new Glyph()
                .put(4, 5, "dddddddd")
                .put(3, 6, "dd").put(12, 6, "dd")
                .put(5, 8, "gg").put(9, 8, "gg")
                .put(6, 11, "ddd")
                .rows());

        // The eyes shine out of a dark slot rather than sitting on the helm, which was the same colour as them.
        faces.put(Face.HELM_SLIT, new Glyph()
                .put(5, 4, "aaaaaa")
                .put(4, 5, "aaaaaaaa")
                .put(7, 6, "aa")
                .put(4, 7, "dddddddd")
                .put(5, 7, "gg").put(9, 7, "gg")
                .put(6, 11, "ddd")
                .rows());

        faces.put(Face.HELM_SPIKED, new Glyph()
                .put(5, 2, "g").put(7, 2, "g").put(9, 2, "g")
                .put(4, 4, "aaaaaaaa")
                .put(4, 5, "aaaaaaaa")
                .put(4, 7, "dddddddd")
                .put(5, 7, "gg").put(9, 7, "gg")
                .put(6, 11, "ddd")
                .rows());

        faces.put(Face.GAUNT, new Glyph()
                .put(5, 7, "gg").put(9, 7, "gg")
                .put(5, 8, "gg").put(9, 8, "gg")
                .put(6, 8, "d").put(9, 8, "d")
                .put(7, 10, "d")
                .put(4, 10, "d").put(11, 10, "d")
                .put(5, 11, "dddddd")
                .rows());

        faces.put(Face.FROG, new Glyph()
                .put(5, 5, "gg").put(9, 5, "gg")
                .put(5, 6, "gg").put(9, 6, "gg")
                .put(5, 9, "dddddd")
                .rows());

        faces.put(Face.LIZARD, new Glyph()
                .put(3, 8, "gg").put(11, 8, "gg")
                .put(6, 10, "d").put(7, 11, "dd").put(9, 10, "d")
                .rows());

        faces.put(Face.RODENT, new Glyph()
                .put(4, 8, "dd").put(11, 8, "dd")
                .put(6, 9, "aaaa")
                .put(7, 10, "aa")
                .put(7, 11, "dd")
                .rows());

        faces.put(Face.NIBBLER, new Glyph()
                .put(5, 7, "dd").put(10, 7, "dd")
                .put(7, 9, "d")
                .put(6, 10, "ddd")
                .put(7, 11, "ww")
                .rows());

        faces.put(Face.SWARM, new Glyph()
                .put(4, 6, "a").put(9, 5, "a").put(11, 8, "a")
                .put(5, 9, "d").put(8, 11, "a").put(10, 12, "a")
                .put(6, 13, "a").put(3, 11, "d").put(12, 10, "a")
                .rows());

        faces.put(Face.HAG, new Glyph()
                .put(9, 7, "g")
                .put(5, 7, "d")
                .put(8, 8, "a").put(7, 9, "aa").put(6, 10, "a")
                .put(5, 11, "dddd")
                .rows());

        faces.put(Face.ENT, new Glyph()
                .put(4, 6, "d..d..d..d")
                .put(5, 8, "gg").put(9, 8, "gg")
                .put(6, 11, "ddd")
                .rows());

        // Cracks rather than a seam across the brow, which read as a hat brim on the first pass.
        faces.put(Face.GOLEM, new Glyph()
                .put(4, 5, "dd").put(10, 5, "dd")
                .put(6, 6, "d").put(9, 7, "d")
                .put(5, 8, "gg").put(9, 8, "gg")
                .put(6, 9, "dd").put(9, 10, "dd")
                .put(7, 11, "dd")
                .rows());

        faces.put(Face.SPIDER, new Glyph()
                .put(5, 6, "gg").put(9, 6, "gg")
                .put(6, 7, "g").put(9, 7, "g")
                .put(6, 10, "w..w")
                .rows());

        faces.put(Face.WARG, new Glyph()
                .put(5, 7, "gg").put(9, 7, "gg")
                .put(7, 9, "aa")
                .put(6, 10, "aaaa")
                .put(6, 11, "w..w")
                .rows());

        faces.put(Face.HORSE, new Glyph()
                .put(4, 4, "dd").put(10, 4, "dd")
                .put(4, 7, "dd").put(11, 7, "dd")
                .put(6, 9, "aaaa")
                .put(6, 10, "aaaa")
                .put(6, 10, "d").put(9, 10, "d")
                .put(6, 11, "aaaa")
                .put(6, 12, "dddd")
                .rows());

        faces.put(Face.WOLF, new Glyph()
                .put(4, 3, "d").put(11, 3, "d")
                .put(4, 4, "dd").put(10, 4, "dd")
                .put(5, 7, "gg").put(9, 7, "gg")
                .put(6, 8, "aaaa")
                .put(6, 9, "aaaa")
                .put(7, 10, "aa")
                .put(7, 9, "d")
                .put(6, 11, "w..w")
                .rows());

        faces.put(Face.AUROCH, new Glyph()
                .put(3, 5, "ww").put(11, 5, "ww")
                .put(4, 6, "w").put(11, 6, "w")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(6, 10, "aaaa")
                .rows());

        faces.put(Face.GOAT, new Glyph()
                .put(4, 5, "ww").put(11, 5, "ww")
                .put(4, 6, "w").put(11, 6, "w")
                .put(5, 8, "d").put(10, 8, "d")
                .put(7, 10, "aa")
                .rows());

        faces.put(Face.MUMAKIL, new Glyph()
                .put(5, 6, "dd").put(9, 6, "dd")
                .put(3, 9, "ww").put(11, 9, "ww")
                .put(3, 10, "w").put(12, 10, "w")
                .put(7, 8, "aa")
                .put(7, 9, "aa")
                .put(7, 10, "aaa")
                .rows());

        faces.put(Face.MINOTAUR, new Glyph()
                .put(3, 5, "ww").put(11, 5, "ww")
                .put(4, 6, "w").put(11, 6, "w")
                .put(5, 8, "dd").put(9, 8, "dd")
                .put(7, 10, "a")
                .put(6, 11, "aa")
                .rows());

        faces.put(Face.MIMIC, new Glyph()
                .put(4, 6, "aaaaaaaa")
                .put(5, 7, "dddddd")
                .put(5, 7, "gg..gg")
                .put(5, 9, "w.w.w")
                .put(6, 10, "w.w.w")
                .rows());

        // A bill that tapers to a point, with the corners of the mouth dark either side of it: the shape
        // that says bird at this size, which a soft blob of marking did not.
        faces.put(Face.EAGLE, new Glyph()
                .put(4, 5, "dd").put(10, 5, "dd")
                .put(4, 6, "gg").put(10, 6, "gg")
                .put(6, 7, "wwww")
                .put(7, 8, "ww")
                .put(6, 9, "d").put(7, 9, "ww").put(9, 9, "d")
                .put(8, 10, "w")
                .rows());

        faces.put(Face.BIRD, new Glyph()
                .put(5, 7, "g").put(10, 7, "g")
                .put(7, 8, "aa")
                .put(8, 9, "a")
                .rows());

        faces.put(Face.DRAGON, new Glyph()
                .put(5, 7, "gg").put(10, 7, "gg")
                .put(6, 9, "aaaa")
                .put(6, 9, "d..d")
                .put(5, 10, "w.w.w")
                .rows());

        // Three tongues of flame licking up the crown, then the burning eyes below them.
        faces.put(Face.BALROG, new Glyph()
                .put(4, 4, "g").put(7, 3, "g").put(10, 4, "g")
                .put(3, 5, "a").put(5, 5, "a").put(6, 4, "a")
                .put(9, 4, "a").put(10, 5, "a").put(11, 5, "a")
                .put(5, 8, "gg").put(9, 8, "gg")
                .put(6, 10, "dddd")
                .put(6, 11, "w..w")
                .rows());

        faces.put(Face.WATCHER, new Glyph()
                .put(5, 7, "wwww")
                .put(5, 8, "wddw")
                .put(5, 9, "wwww")
                .put(4, 11, "a.a.a.a")
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

    private static final Map<Face, String[]> FACES = faces();

    /** What an egg no line below describes is painted as: a small face on a middling shell. */
    private static final Design FALLBACK = new Design(Face.SMALL, 0x8A8A8A, 0xD8D8CC, 0x2A2A2A, 0xF0F0E8);

    private static Map<String, Design> designs() {
        Map<String, Design> designs = new LinkedHashMap<>();

        // The little things: one pair of eyes and not much else, in each creature's own green or brown.
        designs.put("gecko", new Design(Face.LIZARD, 0x6FA34A, 0xC9E27A, 0x22321A, 0xF0F4E0));
        designs.put("rat", new Design(Face.RODENT, 0x7A5B43, 0xD8C4A8, 0x241A12, 0xF0E4D4));
        designs.put("squirrel", new Design(Face.NIBBLER, 0x9A5B32, 0xE8D9B0, 0x2A1808, 0xF4E8D0));
        designs.put("frog", new Design(Face.FROG, 0x4F8F3A, 0xD9E86B, 0x16300E, 0xF0F8D8));
        designs.put("thrush", new Design(Face.BIRD, 0x6B5138, 0xE8D9A8, 0x1E160E, 0xF4ECD8));
        designs.put("crebain", new Design(Face.EAGLE, 0x2E2E34, 0xACA698, 0x101012, 0xE0DCCC));
        designs.put("swarm", new Design(Face.SWARM, 0x5A5A4A, 0xD8CE8A, 0x1A1A10, 0xF4EEC8));

        // The beasts of the field and the road.
        designs.put("greateagle", new Design(Face.EAGLE, 0x8A6A34, 0xF0D27A, 0x2A1E0C, 0xF8EEC8));
        designs.put("gwahir", new Design(Face.EAGLE, 0xC8A050, 0xF8E6B0, 0x3A2A10, 0xFFF8E0));
        designs.put("auroch", new Design(Face.AUROCH, 0x5A4330, 0x8A6A4A, 0x1E1208, 0xF0E8D8));
        designs.put("mumakil", new Design(Face.MUMAKIL, 0x7A7470, 0x9A928C, 0x24211E, 0xF6F2E6));
        designs.put("goat", new Design(Face.GOAT, 0x8A8378, 0xB0A898, 0x24211C, 0xF0EBDC));
        designs.put("shadowfax", new Design(Face.HORSE, 0xD8D8D4, 0xC0C0BC, 0x3A3A38, 0xFFFFFF));
        designs.put("nazgulsteed", new Design(Face.HORSE, 0x24242A, 0x6E6E78, 0x08080A, 0xC8C8D0));
        designs.put("warg", new Design(Face.WOLF, 0x6A6A66, 0x4A4A46, 0x141412, 0xF0EEE4));
        designs.put("fellbeast", new Design(Face.DRAGON, 0x4A4A4E, 0x9A9A9E, 0x101012, 0xE8E0C4));

        // The free peoples.
        designs.put("human", new Design(Face.HUMAN, 0xC79A6B, 0x8A5E36, 0x2A1A0E, 0xF4E4CC));
        designs.put("hobbit", new Design(Face.HOBBIT, 0xD2A87C, 0x8A5E36, 0x331F0E, 0xF8EEDC));
        designs.put("dwarf", new Design(Face.DWARF, 0xB07A4E, 0xD9B25C, 0x26170C, 0xF0DCB8));
        designs.put("elves", new Design(Face.ELF, 0xE0C9A8, 0xF4E8CC, 0x3A2A16, 0xFFFBE8));
        designs.put("istari", new Design(Face.HAT, 0xB8BCC4, 0x7A808C, 0x2A2E34, 0xF8F4E4));
        designs.put("gollum", new Design(Face.GAUNT, 0x8E9A86, 0xE4F0D8, 0x1A2018, 0xE8F0DC));
        designs.put("factionguard", new Design(Face.HELMED, 0x5A6472, 0xC8D2DA, 0x1A1E24, 0x9FB0BC));

        // The envoys: a cowled head over a merchant's cloth, and one egg for all three sides.
        designs.put("factionvendor", new Design(Face.HOODED, 0x6E5A46, 0xD8C49C, 0x1A1410, 0xF2E6C6));

        // The folk of the towns and the traders on the road.
        designs.put("southron", new Design(Face.CLOTH, 0xB08A5A, 0xE0CBAA, 0x241708, 0xF0DCB8));
        designs.put("southron_npc", new Design(Face.CLOTH, 0xC0A070, 0xEDDBBB, 0x241708, 0xF4E4C4));
        designs.put("orc_trader", new Design(Face.ORC, 0x7A8A52, 0xD8C86A, 0x161C0C, 0xE8E8C8));
        designs.put("haradrim", new Design(Face.CLOTH, 0xC0A070, 0xA8502A, 0x241708, 0xF0DCB4));
        designs.put("brigand", new Design(Face.HOODED, 0x8A6A4A, 0x4A3620, 0x160E06, 0xE0D0B4));

        // The horde.
        designs.put("orc", new Design(Face.ORC, 0x6E8A4A, 0xC8B84A, 0x121808, 0xE8E8C0));
        designs.put("mordororc", new Design(Face.ORC, 0x4A5A3A, 0xA83A2A, 0x0E120A, 0xD8D8B0));
        designs.put("urukhai", new Design(Face.URUH, 0x5A6A3A, 0xC83A2A, 0x10140A, 0xF0EEDA));
        designs.put("duergar", new Design(Face.DWARF, 0x6A7A6A, 0xC8C0A8, 0x141A14, 0xE8E4D0));
        designs.put("goblin", new Design(Face.GOBLIN, 0x7A9A4A, 0xE8D25A, 0x121A08, 0xF0EEC8));
        designs.put("goblinking", new Design(Face.GOBLIN, 0xC8A83A, 0xF0DC6A, 0x201604, 0xFFF8D8));
        designs.put("troll", new Design(Face.TROLL, 0x7A8A6A, 0x9A9A88, 0x1C2218, 0xE8E4CC));
        designs.put("huron", new Design(Face.BEARDED, 0x8A5A3A, 0x5A2A14, 0x1A0E06, 0xE8D0B4));
        designs.put("minotaur", new Design(Face.MINOTAUR, 0x6A4A32, 0xB08A5A, 0x1A1008, 0xF4ECD4));
        designs.put("deepclaw", new Design(Face.WOLF, 0x3A4A5A, 0x94AABC, 0x0C1218, 0xD8E8F0));

        // The dead, and the wraiths.
        designs.put("barrow", new Design(Face.UNDEAD, 0xC8C4B0, 0x8A8A78, 0x22221A, 0xF6F2E0));
        designs.put("romiewalker", new Design(Face.UNDEAD, 0x7A8A6A, 0x9AA88A, 0x18200E, 0xE8EEDA));
        designs.put("oathbreaker", new Design(Face.WRAITH, 0x8AB8A8, 0xD8F2E8, 0x1A302A, 0xF0FFF8));
        designs.put("fellspirit", new Design(Face.WRAITH, 0x5A6470, 0x9AD8D0, 0x12181E, 0xE0F0EC));
        designs.put("enragedfellspirit", new Design(Face.WRAITH, 0x7A2A2A, 0xE8A05A, 0x1A0606, 0xF8D8B0));
        designs.put("nazgul", new Design(Face.HELM_SLIT, 0x241E28, 0x8A6A2A, 0x060508, 0xC8BCA0));
        designs.put("witchking", new Design(Face.HELM_SPIKED, 0x22202A, 0x9AC8A8, 0x08070C, 0xD8E8DC));

        // The beasts of the dark and the deep.
        designs.put("mirkwoodspider", new Design(Face.SPIDER, 0x2A2430, 0xC83A3A, 0x0A080E, 0xE8E0D0));
        designs.put("shelob", new Design(Face.SPIDER, 0x241E28, 0x9A2A2A, 0x060408, 0xC8B8A8));
        designs.put("watcher", new Design(Face.WATCHER, 0x2A4A4A, 0x8AC8B8, 0x080E0E, 0xF0F8F4));
        designs.put("swamphag", new Design(Face.HAG, 0x5A6A4A, 0xB8C86A, 0x141A10, 0xE8EED0));
        designs.put("treeent", new Design(Face.ENT, 0x6A5238, 0x8AB04A, 0x14100A, 0xE8F0C8));
        designs.put("mimicchest", new Design(Face.MIMIC, 0x8A5E30, 0xC8A02A, 0x1E1206, 0xF8F0D8));

        // The stone folk.
        designs.put("rockgolem", new Design(Face.GOLEM, 0x7A7A72, 0xE8A83A, 0x24241E, 0xD8D8D0));
        designs.put("elementalgolem", new Design(Face.GOLEM, 0x4A7AA8, 0x9AD8F0, 0x101C28, 0xE0F4FF));
        designs.put("mithrilgolem", new Design(Face.GOLEM, 0xC8D8E0, 0xFFFFFF, 0x2A3A44, 0xF8FEFF));
        designs.put("morgulirongolem", new Design(Face.GOLEM, 0x5A4A6A, 0xB08AD8, 0x120C1A, 0xE0D0F0));

        // The great ones.
        designs.put("balrog", new Design(Face.BALROG, 0x3A1E1A, 0xF07A2A, 0x0E0503, 0xF8E0B0));

        return designs;
    }

    private static final Map<String, Design> DESIGNS = designs();
}
