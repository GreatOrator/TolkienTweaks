package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Writes models under {@code assets/<ns>/models/item/}.
 *
 * <p>1.21.4 also needs an item model <em>definition</em> per item at
 * {@code assets/<ns>/items/<name>.json}; vanilla ships 1,385 of them. One is emitted here for
 * every item the mod registers. Unless the item asks for something else through
 * {@link #definition}, that definition is a plain pointer at the item's own model, which is
 * what the old, unconditional {@code models/item} lookup did.</p>
 */
public class ItemModelProvider extends ModelProvider<ItemModelBuilder> {
    private final Map<ResourceLocation, JsonObject> definitions = new LinkedHashMap<>();

    public ItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, "item", existingFileHelper);
    }

    @Override
    protected ItemModelBuilder createBuilder(ResourceLocation location) {
        return new ItemModelBuilder(location, location.getPath());
    }

    @Override
    protected void registerModels() {
    }

    /**
     * Points an item at a definition of its own instead of its plain model: a dispatch tree, or a
     * model with tints, built with {@link ItemModelDefinition}.
     */
    public void definition(Item item, JsonObject model) {
        ResourceLocation name = BuiltInRegistries.ITEM.getKey(item);
        JsonObject root = new JsonObject();
        root.add("model", model);
        this.definitions.put(name, root);
    }

    public ItemModelBuilder basicItem(Item item) {
        return this.basicItem(item, "item/generated");
    }

    public ItemModelBuilder basicItem(ResourceLocation item) {
        return this.getBuilder(item.getPath())
                .parent(this.getExistingFile(this.mcLoc("item/generated")))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public ItemModelBuilder basicItem(Item item, String parent) {
        ResourceLocation name = BuiltInRegistries.ITEM.getKey(item);
        return this.getBuilder(name.getPath())
                .parent(this.getExistingFile(this.mcLoc(parent)))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "item/" + name.getPath()));
    }

    @Override
    public CompletableFuture<?> generateAll(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(super.generateAll(cache));

        PackOutput.PathProvider items = this.output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        for (Map.Entry<ResourceLocation, JsonObject> entry : this.itemDefinitions().entrySet()) {
            futures.add(DataProvider.saveStable(cache, entry.getValue(), items.json(entry.getKey())));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.definitions.clear();
        return super.run(cache);
    }

    /**
     * Every item in this mod's namespace, mapped to its definition. Items that never asked for
     * one get the plain pointer at {@code <ns>:item/<name>} that the model generator writes for
     * them by default.
     */
    protected Map<ResourceLocation, JsonObject> itemDefinitions() {
        Map<ResourceLocation, JsonObject> result = new LinkedHashMap<>();
        for (ResourceLocation item : BuiltInRegistries.ITEM.keySet()) {
            if (!item.getNamespace().equals(this.modid)) {
                continue;
            }
            JsonObject given = this.definitions.get(item);
            result.put(item, given != null ? given : plainDefinition(this.modelOf(item)));
        }
        return result;
    }

    /** The model an item draws with when nothing else is specified. */
    public ResourceLocation modelOf(ResourceLocation item) {
        return ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath());
    }

    private static JsonObject plainDefinition(ResourceLocation model) {
        JsonObject root = new JsonObject();
        root.add("model", ItemModelDefinition.model(model));
        return root;
    }
}
