package com.greatorator.tolkientweaks.datagen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(DataGenerator generatorIn, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
//        tag(TTMTags.blocks.FENCE_GATES_WOODEN).add(TTMContent.FENCE_GATE_MALLORN.get(), TTMContent.FENCE_GATE_MIRKWOOD.get(), TTMContent.FENCE_GATE_CULUMALDA.get(), TTMContent.FENCE_GATE_LEBETHRON.get());
//            tag(BlockTags.FENCE_GATES).addTag(TTMTags.blocks.FENCE_GATES_WOODEN);
//            tag(BlockTags.FENCES).addTag(TTMTags.blocks.FENCE_GATES_WOODEN);
    }
    @Nonnull
    @Override
    public String getName() {
        return "Tolkien Tweaks - Mobs Edition Block Tags";
    }
}