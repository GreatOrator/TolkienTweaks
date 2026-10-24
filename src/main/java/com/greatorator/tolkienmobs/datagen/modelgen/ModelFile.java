package com.greatorator.tolkienmobs.datagen.modelgen;

import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

/**
 * A reference to a model file. Only the location matters when writing JSON.
 */
public class ModelFile {
    @Nullable
    protected final Identifier location;

    protected ModelFile(@Nullable Identifier location) {
        this.location = location;
    }

    public ModelFile(String location) {
        this(Identifier.parse(location));
    }

    public Identifier getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "ModelFile[" + this.location + "]";
    }

    /** A model that is assumed to exist; nothing is validated or generated for it. */
    public static class ExistingModelFile extends ModelFile {
        public ExistingModelFile(Identifier location) {
            super(location);
        }
    }

    /**
     * A model that is not written to disk, such as {@code minecraft:builtin/entity}
     * or a parent that lives outside this mod's generated output.
     */
    public static class UncheckedModelFile extends ModelFile {
        public UncheckedModelFile(String location) {
            super(Identifier.parse(location));
        }

        public UncheckedModelFile(Identifier location) {
            super(location);
        }
    }
}
