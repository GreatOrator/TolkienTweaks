package verify;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.greatorator.tolkienmobs.datagen.modelgen.BlockStateProvider;
import com.greatorator.tolkienmobs.datagen.modelgen.ExistingFileHelper;
import com.greatorator.tolkienmobs.datagen.modelgen.ItemModelBuilder;
import com.greatorator.tolkienmobs.datagen.modelgen.ItemModelProvider;
import com.greatorator.tolkienmobs.datagen.modelgen.ModelFile;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;

/**
 * Generates blockstates and models for a spread of vanilla shapes using the replacement generator,
 * then compares every produced file with the matching entry in the vanilla client jar.
 *
 * <p>The mod's committed generated output was produced by the old NeoForge generator; this checks the
 * same code paths against the files that generator was modelled on, without needing the mod to compile.</p>
 */
public final class VerifyModelGen {
    private static final int MAX_SHOWN = 6;

    public static void main(String[] args) throws Exception {
        Path out = Paths.get(args[0]);
        Path clientJar = Paths.get(args[1]);

        deleteRecursively(out);
        Files.createDirectories(out);

        // NeoForge patches FeatureFlags/Blocks to read the mod list during static init, so give it an
        // empty one; otherwise Blocks cannot load outside a real FML launch.
        net.neoforged.fml.loading.LoadingModList.of(java.util.List.of(), java.util.List.of(),
                java.util.List.of(), java.util.List.of(), java.util.Map.of());

        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        PackOutput output = new PackOutput(out);
        ExistingFileHelper helper = new ExistingFileHelper();
        new Shapes(output, "minecraft", helper).run(CachedOutput.NO_CACHE).join();
        new ItemShapes(output, "minecraft", helper).run(CachedOutput.NO_CACHE).join();

        compare(out, clientJar);
    }

    private static final class Shapes extends BlockStateProvider {
        Shapes(PackOutput output, String modid, ExistingFileHelper helper) {
            super(output, modid, helper);
        }

        @Override
        protected void registerStatesAndModels() {
            simpleBlockWithItem(Blocks.OAK_PLANKS, cubeAll(Blocks.OAK_PLANKS));
            axisBlock((RotatedPillarBlock) Blocks.OAK_LOG);
            simpleBlock(Blocks.AZALEA_LEAVES, models().cubeAll("azalea_leaves", blockTexture(Blocks.AZALEA_LEAVES)));
            stairsBlock((StairBlock) Blocks.OAK_STAIRS, blockTexture(Blocks.OAK_PLANKS));
            slabBlock((SlabBlock) Blocks.OAK_SLAB, blockTexture(Blocks.OAK_PLANKS), blockTexture(Blocks.OAK_PLANKS));
            fenceBlock((FenceBlock) Blocks.OAK_FENCE, blockTexture(Blocks.OAK_PLANKS));
            fenceGateBlock((FenceGateBlock) Blocks.OAK_FENCE_GATE, blockTexture(Blocks.OAK_PLANKS));
            wallBlock((WallBlock) Blocks.COBBLESTONE_WALL, blockTexture(Blocks.COBBLESTONE));
            buttonBlock((ButtonBlock) Blocks.OAK_BUTTON, blockTexture(Blocks.OAK_PLANKS));
            pressurePlateBlock((PressurePlateBlock) Blocks.OAK_PRESSURE_PLATE, blockTexture(Blocks.OAK_PLANKS));
            doorBlock((DoorBlock) Blocks.OAK_DOOR, blockTexture(Blocks.OAK_DOOR).withSuffix("_bottom"), blockTexture(Blocks.OAK_DOOR).withSuffix("_top"));
            trapdoorBlock((TrapDoorBlock) Blocks.OAK_TRAPDOOR, blockTexture(Blocks.OAK_TRAPDOOR), false);
            trapdoorBlockWithRenderType((TrapDoorBlock) Blocks.IRON_TRAPDOOR, blockTexture(Blocks.IRON_TRAPDOOR), false, "cutout");
            paneBlock((IronBarsBlock) Blocks.GLASS_PANE, blockTexture(Blocks.GLASS), blockTexture(Blocks.GLASS_PANE).withSuffix("_top"));
            horizontalBlock(Blocks.FURNACE, models()
                    .withExistingParent("furnace", "block/orientable")
                    .texture("top", blockTexture(Blocks.FURNACE).withSuffix("_top"))
                    .texture("front", blockTexture(Blocks.FURNACE).withSuffix("_front"))
                    .texture("side", blockTexture(Blocks.FURNACE).withSuffix("_side")));
        }
    }

    private static final class ItemShapes extends ItemModelProvider {
        ItemShapes(PackOutput output, String modid, ExistingFileHelper helper) {
            super(output, modid, helper);
        }

        ItemModelBuilder handheldItem(net.minecraft.world.item.Item item) {
            net.minecraft.resources.ResourceLocation name = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
            return getBuilder(name.getPath())
                    .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                    .texture("layer0", name.getNamespace() + ":item/" + name.getPath());
        }

        @Override
        protected void registerModels() {
            basicItem(Items.APPLE);
            handheldItem(Items.DIAMOND_SWORD);
            withExistingParent("item/oak_planks_flat", "item/generated")
                    .texture("layer0", modLoc("block/oak_planks"));
        }
    }

    private static void compare(Path out, Path clientJar) throws IOException {
        List<String> matched = new ArrayList<>();
        List<String> reordered = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> different = new ArrayList<>();
        List<String> diffs = new ArrayList<>();

        try (JarFile jar = new JarFile(clientJar.toFile());
                Stream<Path> files = Files.walk(out)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                String entryName = out.relativize(file).toString().replace('\\', '/');
                String json = Files.readString(file, StandardCharsets.UTF_8);
                JarEntry entry = jar.getJarEntry(entryName);
                if (entry == null) {
                    missing.add(entryName);
                    continue;
                }
                String vanilla;
                try (Reader reader = new InputStreamReader(jar.getInputStream(entry), StandardCharsets.UTF_8)) {
                    vanilla = readAll(reader);
                }
                JsonElement generated = JsonParser.parseString(json);
                JsonElement expected = JsonParser.parseString(vanilla);
                if (generated.equals(expected)) {
                    matched.add(entryName);
                } else if (canonical(generated).equals(canonical(expected))) {
                    // Same content, different ordering: multipart parts are a list, and NeoForge's
                    // generator (which produced the mod's committed JSON) walks directions in a
                    // different order than vanilla's hand-written files. Check with the committed
                    // assets/tolkienmobs/blockstates/fence_deadwood.json, which is north/south/west/east.
                    reordered.add(entryName);
                } else {
                    different.add(entryName);
                    if (diffs.size() < MAX_SHOWN) {
                        diffs.add("---- " + entryName + "\n  generated: " + json.strip()
                                + "\n  vanilla:   " + vanilla.strip());
                    }
                }
            }
        }

        System.out.printf("compared=%d identical=%d same-content-different-order=%d different=%d not-in-vanilla=%d%n",
                matched.size() + reordered.size() + different.size(), matched.size(), reordered.size(),
                different.size(), missing.size());
        if (!different.isEmpty()) {
            System.out.println();
            System.out.println("MISMATCHES:");
            different.forEach(name -> System.out.println("  " + name));
            System.out.println();
            diffs.forEach(text -> System.out.println(text + "\n"));
        }
        if (!missing.isEmpty()) {
            System.out.println("NOT IN VANILLA JAR:");
            missing.forEach(name -> System.out.println("  " + name));
        }
    }

    /** Sorts object keys and array entries, so only a real content difference survives. */
    private static String canonical(JsonElement element) {
        if (element.isJsonObject()) {
            java.util.TreeMap<String, String> sorted = new java.util.TreeMap<>();
            element.getAsJsonObject().entrySet()
                    .forEach(entry -> sorted.put(entry.getKey(), canonical(entry.getValue())));
            return sorted.toString();
        }
        if (element.isJsonArray()) {
            List<String> entries = new ArrayList<>();
            element.getAsJsonArray().forEach(entry -> entries.add(canonical(entry)));
            java.util.Collections.sort(entries);
            return entries.toString();
        }
        return element.toString();
    }

    private static String readAll(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[8192];
        int read;
        while ((read = reader.read(buffer)) > 0) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            for (Path file : walk.sorted((a, b) -> b.getNameCount() - a.getNameCount()).toList()) {
                Files.deleteIfExists(file);
            }
        }
    }

    private VerifyModelGen() {
    }
}
