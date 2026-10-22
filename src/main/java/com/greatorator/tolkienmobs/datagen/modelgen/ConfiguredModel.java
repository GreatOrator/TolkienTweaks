package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** One model entry inside a blockstate variant or multipart part. */
public class ConfiguredModel {
    public static final int DEFAULT_WEIGHT = 1;

    public final ModelFile model;
    public final int rotationX;
    public final int rotationY;
    public final boolean uvLock;
    public final int weight;

    public ConfiguredModel(ModelFile model) {
        this(model, 0, 0, false, DEFAULT_WEIGHT);
    }

    public ConfiguredModel(ModelFile model, int rotationX, int rotationY, boolean uvLock) {
        this(model, rotationX, rotationY, uvLock, DEFAULT_WEIGHT);
    }

    public ConfiguredModel(ModelFile model, int rotationX, int rotationY, boolean uvLock, int weight) {
        this.model = model;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.uvLock = uvLock;
        this.weight = weight;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("model", this.model.getLocation().toString());
        if (this.rotationX != 0) {
            json.addProperty("x", this.rotationX);
        }
        if (this.rotationY != 0) {
            json.addProperty("y", this.rotationY);
        }
        if (this.uvLock) {
            json.addProperty("uvlock", true);
        }
        return json;
    }

    public static Builder<VariantBlockStateBuilder> builder() {
        return new Builder<>(models -> {
            throw new UnsupportedOperationException("build() is the terminal call here");
        });
    }

    public static class Builder<T> {
        private final Function<ConfiguredModel[], T> adder;
        private final List<ConfiguredModel> models = new ArrayList<>();

        private ModelFile currentModel;
        private int rotationX;
        private int rotationY;
        private boolean uvLock;
        private int weight = DEFAULT_WEIGHT;
        private boolean dirty;

        public Builder(Function<ConfiguredModel[], T> adder) {
            this.adder = adder;
        }

        public Builder<T> modelFile(ModelFile model) {
            this.currentModel = model;
            this.dirty = true;
            return this;
        }

        public Builder<T> rotationX(int value) {
            this.rotationX = value;
            this.dirty = true;
            return this;
        }

        public Builder<T> rotationY(int value) {
            this.rotationY = value;
            this.dirty = true;
            return this;
        }

        public Builder<T> uvLock(boolean value) {
            this.uvLock = value;
            this.dirty = true;
            return this;
        }

        public Builder<T> weight(int value) {
            this.weight = value;
            this.dirty = true;
            return this;
        }

        public Builder<T> nextModel() {
            flush();
            this.currentModel = null;
            this.rotationX = 0;
            this.rotationY = 0;
            this.uvLock = false;
            this.weight = DEFAULT_WEIGHT;
            this.dirty = false;
            return this;
        }

        private void flush() {
            if (this.dirty && this.currentModel != null) {
                this.models.add(new ConfiguredModel(this.currentModel, this.rotationX, this.rotationY, this.uvLock, this.weight));
            }
        }

        public ConfiguredModel[] build() {
            flush();
            return this.models.toArray(new ConfiguredModel[0]);
        }

        public ConfiguredModel buildLast() {
            flush();
            return this.models.get(this.models.size() - 1);
        }

        public T addModel() {
            return this.adder.apply(build());
        }
    }
}
