package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

/** Adds a {@code loader} block to a model, used by the layered item models. */
public abstract class CustomLoaderBuilder<T extends ModelBuilder<T>> {
    protected final Identifier loaderId;
    protected final T parent;
    protected final JsonObject json = new JsonObject();

    protected CustomLoaderBuilder(Identifier loaderId, T parent) {
        this.loaderId = loaderId;
        this.parent = parent;
    }

    public CustomLoaderBuilder<T> renderType(String renderType) {
        this.json.addProperty("render_type", renderType);
        return this;
    }

    public CustomLoaderBuilder<T> renderType(Identifier renderType) {
        this.json.addProperty("render_type", renderType.toString());
        return this;
    }

    public T end() {
        this.parent.setCustomLoader(this.loaderId, this.json);
        return this.parent;
    }
}
