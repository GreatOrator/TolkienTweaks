package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienBlockStateProvider extends BlockStateProvider {
    public TolkienBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        fenceBlock(((FenceBlock) TolkienBlocks.FENCE_MALLORN.get()), blockTexture(TolkienBlocks.PLANKS_MALLORN.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_MALLORN.get()), modLoc("block/door_mallorn_bottom"), modLoc("block/door_mallorn_top"), "cutout");
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("tolkienmobs:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("tolkienmobs:block/" + deferredBlock.getId().getPath() + appendix));
    }
}
