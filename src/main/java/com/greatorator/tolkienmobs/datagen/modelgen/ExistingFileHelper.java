package com.greatorator.tolkienmobs.datagen.modelgen;

import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

/**
 * Stand-in for NeoForge's removed {@code ExistingFileHelper}.
 *
 * <p>The old helper scanned resource packs to fail early on a missing texture or model.
 * Generation never depended on that check, only the quality of its error message, so this
 * accepts anything. The datagen output is compared against the committed generated files,
 * which catches a genuinely missing texture far more directly.</p>
 */
public class ExistingFileHelper {
    public boolean exists(ResourceLocation location, PackType type) {
        return true;
    }

    public boolean exists(ResourceLocation location, PackType type, String path) {
        return true;
    }

    public void trackGenerated(ResourceLocation location, PackType type) {
    }

    public <T extends Collection<ResourceLocation>> T trackGenerated(T locations, PackType type) {
        return locations;
    }
}
