package com.greatorator.tolkienmobs.datagen.modelgen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

/** Writes models under {@code assets/<ns>/models/block/}. */
public class BlockModelProvider extends ModelProvider<BlockModelBuilder> {
    public BlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, "block", existingFileHelper);
    }

    @Override
    protected BlockModelBuilder createBuilder(ResourceLocation location) {
        return new BlockModelBuilder(location, location.getPath());
    }

    @Override
    protected void registerModels() {
    }
}
