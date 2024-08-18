package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.block.custom.PipeweedCropBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienBlockStateProvider extends BlockStateProvider {
    public TolkienBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        /* Metals & Gems */
            // Mithril
        blockWithItem(TolkienBlocks.ORE_MITHRIL);
        blockWithItem(TolkienBlocks.ORE_END_MITHRIL);
        blockWithItem(TolkienBlocks.ORE_NETHER_MITHRIL);
        blockWithItem(TolkienBlocks.ORE_DEEPSLATE_MITHRIL);
        blockWithItem(TolkienBlocks.RAW_MITHRIL_BLOCK);
        blockWithItem(TolkienBlocks.BLOCK_MITHRIL);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_MITHRIL.get()), blockTexture(TolkienBlocks.BLOCK_MITHRIL.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_MITHRIL.get()), blockTexture(TolkienBlocks.BLOCK_MITHRIL.get()), blockTexture(TolkienBlocks.BLOCK_MITHRIL.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_MITHRIL.get()), blockTexture(TolkienBlocks.BLOCK_MITHRIL.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.MITHRIL_BUTTON.get()), blockTexture(TolkienBlocks.BLOCK_MITHRIL.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_MITHRIL.get()), blockTexture(TolkienBlocks.BLOCK_MITHRIL.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_MITHRIL.get()), modLoc("block/door_mithril_bottom"), modLoc("block/door_mithril_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_MITHRIL.get()), modLoc("block/trapdoor_mithril"), true, "cutout");

        blockItem(TolkienBlocks.STAIRS_MITHRIL);
        blockItem(TolkienBlocks.SLAB_MITHRIL);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MITHRIL);
        blockItem(TolkienBlocks.TRAPDOOR_MITHRIL, "_bottom");
            // Morguliron
        blockWithItem(TolkienBlocks.ORE_MORGULIRON);
        blockWithItem(TolkienBlocks.ORE_END_MORGULIRON);
        blockWithItem(TolkienBlocks.ORE_NETHER_MORGULIRON);
        blockWithItem(TolkienBlocks.ORE_DEEPSLATE_MORGULIRON);
        blockWithItem(TolkienBlocks.RAW_MORGULIRON_BLOCK);
        blockWithItem(TolkienBlocks.BLOCK_MORGULIRON);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_MORGULIRON.get()), blockTexture(TolkienBlocks.BLOCK_MORGULIRON.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_MORGULIRON.get()), blockTexture(TolkienBlocks.BLOCK_MORGULIRON.get()), blockTexture(TolkienBlocks.BLOCK_MORGULIRON.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_MORGULIRON.get()), blockTexture(TolkienBlocks.BLOCK_MORGULIRON.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.MORGULIRON_BUTTON.get()), blockTexture(TolkienBlocks.BLOCK_MORGULIRON.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_MORGULIRON.get()), blockTexture(TolkienBlocks.BLOCK_MORGULIRON.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_MORGULIRON.get()), modLoc("block/door_morguliron_bottom"), modLoc("block/door_morguliron_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_MORGULIRON.get()), modLoc("block/trapdoor_morguliron"), true, "cutout");

        blockItem(TolkienBlocks.STAIRS_MORGULIRON);
        blockItem(TolkienBlocks.SLAB_MORGULIRON);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MORGULIRON);
        blockItem(TolkienBlocks.TRAPDOOR_MORGULIRON, "_bottom");
            //Ammolite
        blockWithItem(TolkienBlocks.ORE_AMMOLITE);
        blockWithItem(TolkienBlocks.ORE_END_AMMOLITE);
        blockWithItem(TolkienBlocks.ORE_NETHER_AMMOLITE);
        blockWithItem(TolkienBlocks.ORE_DEEPSLATE_AMMOLITE);
        simpleBlock(TolkienBlocks.BLOCK_AMMOLITE.value(), this.models().cubeAll("block_ammolite", modLoc("block/block_ammolite")).renderType("translucent"));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_DURIN.get()), modLoc("block/door_durin_bottom"), modLoc("block/door_durin_top"), "cutout");


        blockItem(TolkienBlocks.BLOCK_AMMOLITE);
        /* Wood & Foliage */
            // Mallorn
        logBlock((RotatedPillarBlock)TolkienBlocks.LOG_MALLORN.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.WOOD_MALLORN.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_MALLORN_LOG.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_MALLORN_WOOD.get());
        blockWithItem(TolkienBlocks.PLANKS_MALLORN);
        leavesBlock(TolkienBlocks.LEAVES_MALLORN);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_MALLORN.get()), blockTexture(TolkienBlocks.PLANKS_MALLORN.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_MALLORN.get()), blockTexture(TolkienBlocks.PLANKS_MALLORN.get()), blockTexture(TolkienBlocks.PLANKS_MALLORN.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_MALLORN.get()), blockTexture(TolkienBlocks.PLANKS_MALLORN.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.MALLORN_BUTTON.get()), blockTexture(TolkienBlocks.PLANKS_MALLORN.get()));
        fenceBlock(((FenceBlock) TolkienBlocks.FENCE_MALLORN.get()), blockTexture(TolkienBlocks.PLANKS_MALLORN.get()));
        fenceGateBlock(((FenceGateBlock) TolkienBlocks.FENCE_GATE_MALLORN.get()), blockTexture(TolkienBlocks.PLANKS_MALLORN.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_MALLORN.get()), modLoc("block/door_mallorn_bottom"), modLoc("block/door_mallorn_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_MALLORN.get()), modLoc("block/trapdoor_mallorn"), true, "cutout");
        torchBlock(TolkienBlocks.TORCH_MALLORN, TolkienBlocks.WALL_TORCH_MALLORN);

        blockItem(TolkienBlocks.TORCH_MALLORN);
        blockItem(TolkienBlocks.LOG_MALLORN);
        blockItem(TolkienBlocks.WOOD_MALLORN);
        blockItem(TolkienBlocks.STRIPPED_MALLORN_LOG);
        blockItem(TolkienBlocks.STRIPPED_MALLORN_WOOD);
        blockItem(TolkienBlocks.STAIRS_MALLORN);
        blockItem(TolkienBlocks.SLAB_MALLORN);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MALLORN);
        blockItem(TolkienBlocks.FENCE_GATE_MALLORN);
        blockItem(TolkienBlocks.TRAPDOOR_MALLORN, "_bottom");

        // Mirkwood
        logBlock((RotatedPillarBlock)TolkienBlocks.LOG_MIRKWOOD.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.WOOD_MIRKWOOD.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_MIRKWOOD_LOG.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_MIRKWOOD_WOOD.get());
        blockWithItem(TolkienBlocks.PLANKS_MIRKWOOD);
        leavesBlock(TolkienBlocks.LEAVES_MIRKWOOD);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_MIRKWOOD.get()), blockTexture(TolkienBlocks.PLANKS_MIRKWOOD.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_MIRKWOOD.get()), blockTexture(TolkienBlocks.PLANKS_MIRKWOOD.get()), blockTexture(TolkienBlocks.PLANKS_MIRKWOOD.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_MIRKWOOD.get()), blockTexture(TolkienBlocks.PLANKS_MIRKWOOD.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.MIRKWOOD_BUTTON.get()), blockTexture(TolkienBlocks.PLANKS_MIRKWOOD.get()));
        fenceBlock(((FenceBlock) TolkienBlocks.FENCE_MIRKWOOD.get()), blockTexture(TolkienBlocks.PLANKS_MIRKWOOD.get()));
        fenceGateBlock(((FenceGateBlock) TolkienBlocks.FENCE_GATE_MIRKWOOD.get()), blockTexture(TolkienBlocks.PLANKS_MIRKWOOD.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_MIRKWOOD.get()), modLoc("block/door_mirkwood_bottom"), modLoc("block/door_mirkwood_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_MIRKWOOD.get()), modLoc("block/trapdoor_mirkwood"), true, "cutout");
        torchBlock(TolkienBlocks.TORCH_MIRKWOOD, TolkienBlocks.WALL_TORCH_MIRKWOOD);

        blockItem(TolkienBlocks.TORCH_MIRKWOOD);
        blockItem(TolkienBlocks.LOG_MIRKWOOD);
        blockItem(TolkienBlocks.WOOD_MIRKWOOD);
        blockItem(TolkienBlocks.STRIPPED_MIRKWOOD_LOG);
        blockItem(TolkienBlocks.STRIPPED_MIRKWOOD_WOOD);
        blockItem(TolkienBlocks.STAIRS_MIRKWOOD);
        blockItem(TolkienBlocks.SLAB_MIRKWOOD);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MIRKWOOD);
        blockItem(TolkienBlocks.FENCE_GATE_MIRKWOOD);
        blockItem(TolkienBlocks.TRAPDOOR_MIRKWOOD, "_bottom");
            // Culumalda
        logBlock((RotatedPillarBlock)TolkienBlocks.LOG_CULUMALDA.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.WOOD_CULUMALDA.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_CULUMALDA_LOG.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_CULUMALDA_WOOD.get());
        blockWithItem(TolkienBlocks.PLANKS_CULUMALDA);
        leavesBlock(TolkienBlocks.LEAVES_CULUMALDA);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CULUMALDA.get()), blockTexture(TolkienBlocks.PLANKS_CULUMALDA.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CULUMALDA.get()), blockTexture(TolkienBlocks.PLANKS_CULUMALDA.get()), blockTexture(TolkienBlocks.PLANKS_CULUMALDA.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CULUMALDA.get()), blockTexture(TolkienBlocks.PLANKS_CULUMALDA.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CULUMALDA_BUTTON.get()), blockTexture(TolkienBlocks.PLANKS_CULUMALDA.get()));
        fenceBlock(((FenceBlock) TolkienBlocks.FENCE_CULUMALDA.get()), blockTexture(TolkienBlocks.PLANKS_CULUMALDA.get()));
        fenceGateBlock(((FenceGateBlock) TolkienBlocks.FENCE_GATE_CULUMALDA.get()), blockTexture(TolkienBlocks.PLANKS_CULUMALDA.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_CULUMALDA.get()), modLoc("block/door_culumalda_bottom"), modLoc("block/door_culumalda_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_CULUMALDA.get()), modLoc("block/trapdoor_culumalda"), true, "cutout");
        torchBlock(TolkienBlocks.TORCH_CULUMALDA, TolkienBlocks.WALL_TORCH_CULUMALDA);

        blockItem(TolkienBlocks.TORCH_CULUMALDA);
        blockItem(TolkienBlocks.LOG_CULUMALDA);
        blockItem(TolkienBlocks.WOOD_CULUMALDA);
        blockItem(TolkienBlocks.STRIPPED_CULUMALDA_LOG);
        blockItem(TolkienBlocks.STRIPPED_CULUMALDA_WOOD);
        blockItem(TolkienBlocks.STAIRS_CULUMALDA);
        blockItem(TolkienBlocks.SLAB_CULUMALDA);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CULUMALDA);
        blockItem(TolkienBlocks.FENCE_GATE_CULUMALDA);
        blockItem(TolkienBlocks.TRAPDOOR_CULUMALDA, "_bottom");
            // Lebethron
        logBlock((RotatedPillarBlock)TolkienBlocks.LOG_LEBETHRON.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.WOOD_LEBETHRON.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_LEBETHRON_LOG.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_LEBETHRON_WOOD.get());
        blockWithItem(TolkienBlocks.PLANKS_LEBETHRON);
        leavesBlock(TolkienBlocks.LEAVES_LEBETHRON);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_LEBETHRON.get()), blockTexture(TolkienBlocks.PLANKS_LEBETHRON.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_LEBETHRON.get()), blockTexture(TolkienBlocks.PLANKS_LEBETHRON.get()), blockTexture(TolkienBlocks.PLANKS_LEBETHRON.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_LEBETHRON.get()), blockTexture(TolkienBlocks.PLANKS_LEBETHRON.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.LEBETHRON_BUTTON.get()), blockTexture(TolkienBlocks.PLANKS_LEBETHRON.get()));
        fenceBlock(((FenceBlock) TolkienBlocks.FENCE_LEBETHRON.get()), blockTexture(TolkienBlocks.PLANKS_LEBETHRON.get()));
        fenceGateBlock(((FenceGateBlock) TolkienBlocks.FENCE_GATE_LEBETHRON.get()), blockTexture(TolkienBlocks.PLANKS_LEBETHRON.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_LEBETHRON.get()), modLoc("block/door_lebethron_bottom"), modLoc("block/door_lebethron_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_LEBETHRON.get()), modLoc("block/trapdoor_lebethron"), true, "cutout");
        torchBlock(TolkienBlocks.TORCH_LEBETHRON, TolkienBlocks.WALL_TORCH_LEBETHRON);

        blockItem(TolkienBlocks.TORCH_LEBETHRON);
        blockItem(TolkienBlocks.LOG_LEBETHRON);
        blockItem(TolkienBlocks.WOOD_LEBETHRON);
        blockItem(TolkienBlocks.STRIPPED_LEBETHRON_LOG);
        blockItem(TolkienBlocks.STRIPPED_LEBETHRON_WOOD);
        blockItem(TolkienBlocks.STAIRS_LEBETHRON);
        blockItem(TolkienBlocks.SLAB_LEBETHRON);
        blockItem(TolkienBlocks.PRESSURE_PLATE_LEBETHRON);
        blockItem(TolkienBlocks.FENCE_GATE_LEBETHRON);
        blockItem(TolkienBlocks.TRAPDOOR_LEBETHRON, "_bottom");
            // Fangorn Oak
        logBlock((RotatedPillarBlock)TolkienBlocks.LOG_FANGORNOAK.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.WOOD_FANGORNOAK.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_FANGORNOAK_LOG.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_FANGORNOAK_WOOD.get());
        blockWithItem(TolkienBlocks.PLANKS_FANGORNOAK);
        leavesBlock(TolkienBlocks.LEAVES_FANGORNOAK);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_FANGORNOAK.get()), blockTexture(TolkienBlocks.PLANKS_FANGORNOAK.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_FANGORNOAK.get()), blockTexture(TolkienBlocks.PLANKS_FANGORNOAK.get()), blockTexture(TolkienBlocks.PLANKS_FANGORNOAK.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_FANGORNOAK.get()), blockTexture(TolkienBlocks.PLANKS_FANGORNOAK.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.FANGORNOAK_BUTTON.get()), blockTexture(TolkienBlocks.PLANKS_FANGORNOAK.get()));
        fenceBlock(((FenceBlock) TolkienBlocks.FENCE_FANGORNOAK.get()), blockTexture(TolkienBlocks.PLANKS_FANGORNOAK.get()));
        fenceGateBlock(((FenceGateBlock) TolkienBlocks.FENCE_GATE_FANGORNOAK.get()), blockTexture(TolkienBlocks.PLANKS_FANGORNOAK.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_FANGORNOAK.get()), modLoc("block/door_fangornoak_bottom"), modLoc("block/door_fangornoak_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_FANGORNOAK.get()), modLoc("block/trapdoor_fangornoak"), true, "cutout");
        torchBlock(TolkienBlocks.TORCH_FANGORNOAK, TolkienBlocks.WALL_TORCH_FANGORNOAK);

        blockItem(TolkienBlocks.TORCH_FANGORNOAK);
        blockItem(TolkienBlocks.LOG_FANGORNOAK);
        blockItem(TolkienBlocks.WOOD_FANGORNOAK);
        blockItem(TolkienBlocks.STRIPPED_FANGORNOAK_LOG);
        blockItem(TolkienBlocks.STRIPPED_FANGORNOAK_WOOD);
        blockItem(TolkienBlocks.STAIRS_FANGORNOAK);
        blockItem(TolkienBlocks.SLAB_FANGORNOAK);
        blockItem(TolkienBlocks.PRESSURE_PLATE_FANGORNOAK);
        blockItem(TolkienBlocks.FENCE_GATE_FANGORNOAK);
        blockItem(TolkienBlocks.TRAPDOOR_FANGORNOAK, "_bottom");
            // Deadwood
        logBlock((RotatedPillarBlock)TolkienBlocks.LOG_DEADWOOD.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.WOOD_DEADWOOD.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_DEADWOOD_LOG.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_DEADWOOD_WOOD.get());
        stairsBlockWithRenderType(((StairBlock) TolkienBlocks.STAIRS_DEADWOOD.get()), blockTexture(TolkienBlocks.PLANKS_DEADWOOD.get()), "cutout");
        fenceBlockWithRenderType(((FenceBlock) TolkienBlocks.FENCE_DEADWOOD.get()), blockTexture(TolkienBlocks.PLANKS_DEADWOOD.get()), "cutout");
        fenceGateBlockWithRenderType(((FenceGateBlock) TolkienBlocks.FENCE_GATE_DEADWOOD.get()), blockTexture(TolkienBlocks.PLANKS_DEADWOOD.get()), "cutout");
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_DEADWOOD.get()), modLoc("block/door_deadwood_bottom"), modLoc("block/door_deadwood_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_DEADWOOD.get()), modLoc("block/trapdoor_deadwood"), true, "cutout");
        torchBlock(TolkienBlocks.TORCH_DEADWOOD, TolkienBlocks.WALL_TORCH_DEADWOOD);

        blockItem(TolkienBlocks.TORCH_DEADWOOD);
        blockItem(TolkienBlocks.LOG_DEADWOOD);
        blockItem(TolkienBlocks.WOOD_DEADWOOD);
        blockItem(TolkienBlocks.STRIPPED_DEADWOOD_LOG);
        blockItem(TolkienBlocks.STRIPPED_DEADWOOD_WOOD);
        blockItem(TolkienBlocks.STAIRS_DEADWOOD);
        blockItem(TolkienBlocks.SLAB_DEADWOOD);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DEADWOOD);
        blockItem(TolkienBlocks.FENCE_GATE_DEADWOOD);
        blockItem(TolkienBlocks.TRAPDOOR_DEADWOOD, "_bottom");
            //Flowers & Plants

        makeFlower(TolkienBlocks.FLOWER_SIMBELMYNE, TolkienBlocks.POTTED_FLOWER_SIMBELMYNE);
        makeFlower(TolkienBlocks.FLOWER_MIRKWOOD, TolkienBlocks.POTTED_FLOWER_MIRKWOOD);
        makeFlower(TolkienBlocks.FLOWER_ALFIRIN, TolkienBlocks.POTTED_FLOWER_ALFIRIN);
        makeFlower(TolkienBlocks.FLOWER_ATHELAS, TolkienBlocks.POTTED_FLOWER_ATHELAS);
        makeFlower(TolkienBlocks.FLOWER_NIPHREDIL, TolkienBlocks.POTTED_FLOWER_NIPHREDIL);
        makeFlower(TolkienBlocks.FLOWER_SWAMPMILKWEED, TolkienBlocks.POTTED_FLOWER_SWAMPMILKWEED);
        makeFlower(TolkienBlocks.FLOWER_LILLYOFTHEVALLEY, TolkienBlocks.POTTED_FLOWER_LILLYOFTHEVALLEY);
        makeFlower(TolkienBlocks.SAPLING_MALLORN, TolkienBlocks.POTTED_SAPLING_MALLORN);
        makeFlower(TolkienBlocks.SAPLING_MIRKWOOD, TolkienBlocks.POTTED_SAPLING_MIRKWOOD);
        makeFlower(TolkienBlocks.SAPLING_CULUMALDA, TolkienBlocks.POTTED_SAPLING_CULUMALDA);
        makeFlower(TolkienBlocks.SAPLING_LEBETHRON, TolkienBlocks.POTTED_SAPLING_LEBETHRON);
        makeFlower(TolkienBlocks.SAPLING_FANGORNOAK, TolkienBlocks.POTTED_SAPLING_FANGORNOAK);
        makeFlower(TolkienBlocks.SAPLING_DEADWOOD, TolkienBlocks.POTTED_SAPLING_DEADWOOD);
        makeFlower(TolkienBlocks.MUSHROOM_BLOOM_DECAY, TolkienBlocks.POTTED_MUSHROOM_BLOOM_DECAY);
        makeFlower(TolkienBlocks.MUSHROOM_DECAY_BLOOM, TolkienBlocks.POTTED_MUSHROOM_DECAY_BLOOM);
        makeCrop(((PipeweedCropBlock) TolkienBlocks.PIPEWEED.get()), "pipeweed_crop_stage", "pipeweed_stage");
            // Custom
//        directionalBlock(TolkienBlocks.LIGHTNINGBUG_BLOCK.get(), models().getExistingFile(modLoc("block/lightningbug")));
//        blockItem(TolkienBlocks.LIGHTNINGBUG_BLOCK);

    }

    protected ResourceLocation texture(String name) {
        return modLoc("block/" + name);
    }

    protected String name(Supplier<? extends Block> block) {
        return BuiltInRegistries.BLOCK.getKey(block.get()).getPath();
    }

    public void makeCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> states(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((PipeweedCropBlock) block).getAgeProperty()),
                ResourceLocation.fromNamespaceAndPath(MODID, "block/" + textureName +
                        state.getValue(((PipeweedCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    public void torchBlock(Supplier<? extends Block> block, Supplier<? extends Block> wall) {
        ModelFile torch = models().torch(name(block), texture(name(block))).renderType("cutout");
        ModelFile torchwall = models().torchWall(name(wall), texture(name(block))).renderType("cutout");

        simpleBlock(block.get(), torch);
        getVariantBuilder(wall.get()).forAllStates(state ->
                ConfiguredModel.builder()
                        .modelFile(torchwall)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360)
                        .build());
    }

    public void makeFlower(DeferredBlock<Block> flowerBlock, DeferredBlock<FlowerPotBlock> potBlock) {
        simpleBlockItem(flowerBlock.get(), new ModelFile.UncheckedModelFile("tolkienmobs:block/" + flowerBlock.getId().getPath()));
        simpleBlock(flowerBlock.get(),
                models().cross(blockTexture(flowerBlock.get()).getPath(),
                        blockTexture(flowerBlock.get())).renderType("cutout"));
        simpleBlock(potBlock.get(),
                models().withExistingParent(potBlock.getId().getPath(),
                                mcLoc("block/flower_pot_cross")).renderType("cutout")
                        .texture("plant", blockTexture(flowerBlock.get())));
    }

    public void leavesBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().cubeAll(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
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
