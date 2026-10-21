package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
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
 * {@code assets/<ns>/items/<name>.json}; vanilla ships 1,385 of them. They are emitted here
 * alongside the model they point at.</p>
 */
public class ItemModelProvider extends ModelProvider<ItemModelBuilder> {
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
        for (ResourceLocation location : this.generatedModels.keySet()) {
            if (!location.getPath().startsWith("item/")) {
                continue;
            }
            ResourceLocation definition = ResourceLocation.fromNamespaceAndPath(
                    location.getNamespace(), location.getPath().substring("item/".length()));
            futures.add(DataProvider.saveStable(cache, definition(location), items.json(definition)));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private static JsonObject definition(ResourceLocation model) {
        JsonObject selector = new JsonObject();
        selector.addProperty("type", "minecraft:model");
        selector.addProperty("model", model.toString());
        JsonObject root = new JsonObject();
        root.add("model", selector);
        return root;
    }
}
