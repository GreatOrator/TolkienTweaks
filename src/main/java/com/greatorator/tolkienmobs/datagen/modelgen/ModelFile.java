package com.greatorator.tolkienmobs.datagen.modelgen;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

/**
 * A reference to a model file. Only the location matters when writing JSON.
 */
public class ModelFile {
    @Nullable
    protected final ResourceLocation location;

    protected ModelFile(@Nullable ResourceLocation location) {
        this.location = location;
    }

    public ModelFile(String location) {
        this(ResourceLocation.parse(location));
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "ModelFile[" + this.location + "]";
    }

    /** A model that is assumed to exist; nothing is validated or generated for it. */
    public static class ExistingModelFile extends ModelFile {
        public ExistingModelFile(ResourceLocation location) {
            super(location);
        }
    }

    /**
     * A model that is not written to disk, such as {@code minecraft:builtin/entity}
     * or a parent that lives outside this mod's generated output.
     */
    public static class UncheckedModelFile extends ModelFile {
        public UncheckedModelFile(String location) {
            super(ResourceLocation.parse(location));
        }

        public UncheckedModelFile(ResourceLocation location) {
            super(location);
        }
    }
}
