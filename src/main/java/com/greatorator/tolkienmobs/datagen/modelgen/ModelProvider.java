package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * Writes model JSON for one folder ("block" or "item").
 *
 * <p>Paths follow the old NeoForge rule: a name containing a slash is used as-is,
 * a bare name is placed inside this provider's folder. Parents and texture keys
 * match what the committed generated models already contain.</p>
 */
public abstract class ModelProvider<T extends ModelBuilder<T>> implements DataProvider {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final PackOutput output;
    protected final String modid;
    protected final String folder;
    protected final ExistingFileHelper existingFileHelper;
    public final Map<Identifier, T> generatedModels = new LinkedHashMap<>();

    protected ModelProvider(PackOutput output, String modid, String folder, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.modid = modid;
        this.folder = folder;
        this.existingFileHelper = existingFileHelper;
    }

    protected abstract T createBuilder(Identifier location);

    protected abstract void registerModels();

    public T getBuilder(String path) {
        Identifier location = extendWithFolder(path.contains(":")
                ? Identifier.parse(path)
                : Identifier.fromNamespaceAndPath(this.modid, path));
        return this.generatedModels.computeIfAbsent(location, this::createBuilder);
    }

    private Identifier extendWithFolder(Identifier location) {
        if (location.getPath().contains("/")) {
            return location;
        }
        return Identifier.fromNamespaceAndPath(location.getNamespace(), this.folder + "/" + location.getPath());
    }

    public Identifier modLoc(String name) {
        return Identifier.fromNamespaceAndPath(this.modid, name);
    }

    public Identifier mcLoc(String name) {
        return Identifier.parse(name);
    }

    public ModelFile getExistingFile(Identifier location) {
        return new ModelFile.ExistingModelFile(location);
    }

    public ModelFile getExistingFile(String location) {
        return this.getExistingFile(Identifier.parse(location));
    }

    public T withExistingParent(String name, String parent) {
        return this.withExistingParent(name, this.mcLoc(parent));
    }

    public T withExistingParent(String name, Identifier parent) {
        return this.getBuilder(name).parent(this.getExistingFile(parent));
    }

    public T singleTexture(String name, String parent, String textureKey, Identifier texture) {
        return this.withExistingParent(name, parent).texture(textureKey, texture);
    }

    public T singleTexture(String name, Identifier parent, String textureKey, Identifier texture) {
        return this.withExistingParent(name, parent).texture(textureKey, texture);
    }

    public T singleTexture(String name, String parent, Identifier texture) {
        return this.singleTexture(name, parent, "texture", texture);
    }

    private T sideBottomTop(String name, String parent, Identifier side, Identifier bottom, Identifier top) {
        return this.withExistingParent(name, parent)
                .texture("side", side)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    public T cubeAll(String name, Identifier texture) {
        return this.singleTexture(name, "block/cube_all", "all", texture);
    }

    public T cubeBottomTop(String name, Identifier side, Identifier bottom, Identifier top) {
        return this.withExistingParent(name, "block/cube_bottom_top")
                .texture("side", side)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    public T cubeColumn(String name, Identifier side, Identifier end) {
        return this.withExistingParent(name, "block/cube_column").texture("side", side).texture("end", end);
    }

    public T cubeColumnHorizontal(String name, Identifier side, Identifier end) {
        return this.withExistingParent(name, "block/cube_column_horizontal").texture("side", side).texture("end", end);
    }

    public T cross(String name, Identifier cross) {
        return this.singleTexture(name, "block/cross", "cross", cross);
    }

    public T crop(String name, Identifier crop) {
        return this.singleTexture(name, "block/crop", "crop", crop);
    }

    public T slab(String name, Identifier side, Identifier bottom, Identifier top) {
        return this.sideBottomTop(name, "block/slab", side, bottom, top);
    }

    public T slabTop(String name, Identifier side, Identifier bottom, Identifier top) {
        return this.sideBottomTop(name, "block/slab_top", side, bottom, top);
    }

    public T stairs(String name, Identifier side, Identifier bottom, Identifier top) {
        return this.sideBottomTop(name, "block/stairs", side, bottom, top);
    }

    public T stairsInner(String name, Identifier side, Identifier bottom, Identifier top) {
        return this.sideBottomTop(name, "block/inner_stairs", side, bottom, top);
    }

    public T stairsOuter(String name, Identifier side, Identifier bottom, Identifier top) {
        return this.sideBottomTop(name, "block/outer_stairs", side, bottom, top);
    }

    public T fencePost(String name, Identifier texture) {
        return this.singleTexture(name, "block/fence_post", texture);
    }

    public T fenceSide(String name, Identifier texture) {
        return this.singleTexture(name, "block/fence_side", texture);
    }

    public T fenceGate(String name, Identifier texture) {
        return this.singleTexture(name, "block/template_fence_gate", texture);
    }

    public T fenceGateOpen(String name, Identifier texture) {
        return this.singleTexture(name, "block/template_fence_gate_open", texture);
    }

    public T fenceGateWall(String name, Identifier texture) {
        return this.singleTexture(name, "block/template_fence_gate_wall", texture);
    }

    public T fenceGateWallOpen(String name, Identifier texture) {
        return this.singleTexture(name, "block/template_fence_gate_wall_open", texture);
    }

    public T torch(String name, Identifier torch) {
        return this.singleTexture(name, "block/template_torch", "torch", torch);
    }

    public T torchWall(String name, Identifier torch) {
        return this.singleTexture(name, "block/template_torch_wall", "torch", torch);
    }

    public void clear() {
        this.generatedModels.clear();
    }

    /** Writes every model collected so far, without registering anything new. */
    public CompletableFuture<?> generateAll(CachedOutput cache) {
        PackOutput.PathProvider pathProvider = this.output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        return CompletableFuture.allOf(this.generatedModels.entrySet().stream()
                .map(entry -> DataProvider.saveStable(cache, entry.getValue().toJson(), pathProvider.json(entry.getKey())))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.clear();
        this.registerModels();
        return this.generateAll(cache);
    }

    @Override
    public String getName() {
        return "Models (" + this.modid + "/" + this.folder + ")";
    }
}
