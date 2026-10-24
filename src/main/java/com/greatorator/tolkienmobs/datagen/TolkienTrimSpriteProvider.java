package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.google.common.hash.Hashing;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Writes the trim overlay sprites for the mod's own trim materials - mithril, morguliron and
 * ammolite.
 *
 * <p>The trim materials vanilla ships get their sprites cut at load time by the paletted
 * permutation source in {@code minecraft:atlases/blocks.json}, which an armour trim model names as
 * {@code minecraft:trims/items/<slot>_trim_<material>}. That atlas file is the only one the game
 * reads for the block atlas, so a mod cannot add a source to it; instead these sprites are baked
 * here, by running vanilla's trim shape through the same palette swap, and written under
 * {@code textures/item/trim/}, a folder the block atlas already stitches. A trim model on one of
 * the mod's materials then points at {@code tolkienmobs:item/trim/<slot>_trim_<material>}.</p>
 */
public class TolkienTrimSpriteProvider implements DataProvider {
    private static final List<String> SLOTS = List.of("helmet", "chestplate", "leggings", "boots");
    private static final List<String> MATERIALS = List.of("mithril", "morguliron", "ammolite");
    private static final Identifier PALETTE_KEY = Identifier.withDefaultNamespace("textures/trims/color_palettes/trim_palette.png");

    private final ResourceManager resources;
    private final PackOutput.PathProvider pathProvider;

    public TolkienTrimSpriteProvider(PackOutput output, ResourceManager resources) {
        this.resources = resources;
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures/item/trim");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        try {
            BufferedImage key = read(PALETTE_KEY);
            List<CompletableFuture<?>> writes = new ArrayList<>();

            for (String material : MATERIALS) {
                BufferedImage palette = read(TolkienMobsMain.resLoc("textures/trims/color_palettes/" + material + ".png"));

                for (String slot : SLOTS) {
                    BufferedImage shape = read(Identifier.withDefaultNamespace("textures/trims/items/" + slot + "_trim.png"));
                    BufferedImage tinted = tint(shape, key, palette);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    ImageIO.write(tinted, "png", bytes);
                    byte[] data = bytes.toByteArray();

                    Identifier id = TolkienMobsMain.resLoc(slot + "_trim_" + material);
                    cache.writeIfNeeded(this.pathProvider.file(id, "png"), data, Hashing.sha1().hashBytes(data));
                }
            }

            return CompletableFuture.completedFuture(null);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write the trim sprites", exception);
        }
    }

    /**
     * Swaps every palette-key colour in {@code shape} for the colour in the same slot of
     * {@code palette}, which is what vanilla's paletted permutation source does at load time. The
     * shape's own alpha is kept, and a colour that is not in the key is left alone.
     */
    private static BufferedImage tint(BufferedImage shape, BufferedImage key, BufferedImage palette) {
        int entries = Math.min(key.getWidth(), palette.getWidth());
        if (key.getWidth() != palette.getWidth()) {
            throw new IllegalStateException("Trim palette is " + palette.getWidth() + " pixels wide but the key is "
                    + key.getWidth() + "; they must line up colour for colour");
        }

        Map<Integer, Integer> swaps = new HashMap<>();
        for (int x = 0; x < entries; x++) {
            int from = key.getRGB(x, 0);
            if ((from >>> 24) == 0) {
                continue;
            }
            swaps.put(from & 0xFFFFFF, palette.getRGB(x, 0) & 0xFFFFFF);
        }

        BufferedImage result = new BufferedImage(shape.getWidth(), shape.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < shape.getHeight(); y++) {
            for (int x = 0; x < shape.getWidth(); x++) {
                int pixel = shape.getRGB(x, y);
                int alpha = pixel >>> 24;
                if (alpha == 0) {
                    continue;
                }
                Integer swapped = swaps.get(pixel & 0xFFFFFF);
                result.setRGB(x, y, swapped == null ? pixel : (alpha << 24) | swapped);
            }
        }
        return result;
    }

    private BufferedImage read(Identifier location) throws IOException {
        Optional<Resource> resource = this.resources.getResource(location);
        if (resource.isEmpty()) {
            throw new IOException("Missing " + location + ", which the trim sprites are built from");
        }
        try (InputStream stream = resource.get().open()) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new IOException(location + " is not an image");
            }
            return image;
        }
    }

    @Override
    public String getName() {
        return "Tolkienmobs - Trim sprites";
    }
}
