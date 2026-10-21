package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/** Adds a {@code loader} block to a model, used by the layered item models. */
public abstract class CustomLoaderBuilder<T extends ModelBuilder<T>> {
    protected final ResourceLocation loaderId;
    protected final T parent;
    protected final JsonObject json = new JsonObject();

    protected CustomLoaderBuilder(ResourceLocation loaderId, T parent) {
        this.loaderId = loaderId;
        this.parent = parent;
    }

    public CustomLoaderBuilder<T> renderType(String renderType) {
        this.json.addProperty("render_type", renderType);
        return this;
    }

    public CustomLoaderBuilder<T> renderType(ResourceLocation renderType) {
        this.json.addProperty("render_type", renderType.toString());
        return this;
    }

    public T end() {
        this.parent.setCustomLoader(this.loaderId, this.json);
        return this.parent;
    }
}
