package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.block.custom.*;
import com.greatorator.tolkienmobs.datagen.helpers.BlockModelBuilders;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TolkienBlockStateProvider extends BlockModelBuilders {
    public TolkienBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, exFileHelper);
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
        ModelFile barrelMithril = models().cubeBottomTop("barrel_mithril", modLoc("block/barrel/barrel_mithril_side"), modLoc("block/barrel/barrel_mithril_bottom"), modLoc("block/barrel/barrel_mithril_top"));
        ModelFile barrelMithrilOpen = models().cubeBottomTop("barrel_mithril_open", modLoc("block/barrel/barrel_mithril_side"), modLoc("block/barrel/barrel_mithril_bottom"), modLoc("block/barrel/barrel_mithril_top_open"));
        directionalBlock(TolkienBlocks.BARREL_MITHRIL.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelMithrilOpen : barrelMithril);
        paneBlockWithRenderType(TolkienBlocks.MITHRIL_BARS.get(), modLoc("block/mithril_bars"), modLoc("block/mithril_bars"), "cutout");

        blockItem(TolkienBlocks.STAIRS_MITHRIL);
        blockItem(TolkienBlocks.SLAB_MITHRIL);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MITHRIL);
        blockItem(TolkienBlocks.TRAPDOOR_MITHRIL, "_bottom");
        blockItem(TolkienBlocks.BARREL_MITHRIL);
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
        ModelFile barrelMorguliron = models().cubeBottomTop("barrel_morguliron", modLoc("block/barrel/barrel_morguliron_side"), modLoc("block/barrel/barrel_morguliron_bottom"), modLoc("block/barrel/barrel_morguliron_top"));
        ModelFile barrelMorgulironOpen = models().cubeBottomTop("barrel_morguliron_open", modLoc("block/barrel/barrel_morguliron_side"), modLoc("block/barrel/barrel_morguliron_bottom"), modLoc("block/barrel/barrel_morguliron_top_open"));
        directionalBlock(TolkienBlocks.BARREL_MORGULIRON.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelMorgulironOpen : barrelMorguliron);
        paneBlockWithRenderType(TolkienBlocks.MORGULIRON_BARS.get(), modLoc("block/morguliron_bars"), modLoc("block/morguliron_bars"), "cutout");

        blockItem(TolkienBlocks.STAIRS_MORGULIRON);
        blockItem(TolkienBlocks.SLAB_MORGULIRON);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MORGULIRON);
        blockItem(TolkienBlocks.TRAPDOOR_MORGULIRON, "_bottom");
        blockItem(TolkienBlocks.BARREL_MORGULIRON);
        //Ammolite
        blockWithItem(TolkienBlocks.ORE_AMMOLITE);
        blockWithItem(TolkienBlocks.ORE_END_AMMOLITE);
        blockWithItem(TolkienBlocks.ORE_NETHER_AMMOLITE);
        blockWithItem(TolkienBlocks.ORE_DEEPSLATE_AMMOLITE);
        simpleBlock(TolkienBlocks.BLOCK_AMMOLITE.value(), this.models().cubeAll("block_ammolite", modLoc("block/block_ammolite")).renderType("translucent"));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_DURIN.get()), modLoc("block/door_durin_bottom"), modLoc("block/door_durin_top"), "cutout");
        paneBlockWithRenderType(TolkienBlocks.PANE_AMMOLITE.get(), modLoc("block/block_ammolite"), modLoc("block/ammolite_pane_top"), "translucent");

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
        ModelFile barrelMallorn = models().cubeBottomTop("barrel_mallorn", modLoc("block/barrel/barrel_mallorn_side"), modLoc("block/barrel/barrel_mallorn_bottom"), modLoc("block/barrel/barrel_mallorn_top"));
        ModelFile barrelMallornOpen = models().cubeBottomTop("barrel_mallorn_open", modLoc("block/barrel/barrel_mallorn_side"), modLoc("block/barrel/barrel_mallorn_bottom"), modLoc("block/barrel/barrel_mallorn_top_open"));
        directionalBlock(TolkienBlocks.BARREL_MALLORN.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelMallornOpen : barrelMallorn);
        signBlock(TolkienBlocks.MALLORN_SIGN, TolkienBlocks.MALLORN_WALL_SIGN, TolkienBlocks.PLANKS_MALLORN);
        hangingSignBlock(TolkienBlocks.MALLORN_HANGING_SIGN, TolkienBlocks.MALLORN_HANGING_WALL_SIGN, TolkienBlocks.PLANKS_MALLORN);
        ladder(TolkienBlocks.LADDER_MALLORN, TolkienBlocks.PLANKS_MALLORN);

        blockItem(TolkienBlocks.TORCH_MALLORN);
        blockItem(TolkienBlocks.WALL_TORCH_MALLORN);
        blockItem(TolkienBlocks.LOG_MALLORN);
        blockItem(TolkienBlocks.WOOD_MALLORN);
        blockItem(TolkienBlocks.STRIPPED_MALLORN_LOG);
        blockItem(TolkienBlocks.STRIPPED_MALLORN_WOOD);
        blockItem(TolkienBlocks.STAIRS_MALLORN);
        blockItem(TolkienBlocks.SLAB_MALLORN);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MALLORN);
        blockItem(TolkienBlocks.FENCE_GATE_MALLORN);
        blockItem(TolkienBlocks.TRAPDOOR_MALLORN, "_bottom");
        blockItem(TolkienBlocks.BARREL_MALLORN);

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
        ModelFile barrelMirkwood = models().cubeBottomTop("barrel_mirkwood", modLoc("block/barrel/barrel_mirkwood_side"), modLoc("block/barrel/barrel_mirkwood_bottom"), modLoc("block/barrel/barrel_mirkwood_top"));
        ModelFile barrelMirkwoodOpen = models().cubeBottomTop("barrel_mirkwood_open", modLoc("block/barrel/barrel_mirkwood_side"), modLoc("block/barrel/barrel_mirkwood_bottom"), modLoc("block/barrel/barrel_mirkwood_top_open"));
        directionalBlock(TolkienBlocks.BARREL_MIRKWOOD.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelMirkwoodOpen : barrelMirkwood);
        signBlock(TolkienBlocks.MIRKWOOD_SIGN, TolkienBlocks.MIRKWOOD_WALL_SIGN, TolkienBlocks.PLANKS_MIRKWOOD);
        hangingSignBlock(TolkienBlocks.MIRKWOOD_HANGING_SIGN, TolkienBlocks.MIRKWOOD_HANGING_WALL_SIGN, TolkienBlocks.PLANKS_MIRKWOOD);
        ladder(TolkienBlocks.LADDER_MIRKWOOD, TolkienBlocks.PLANKS_MIRKWOOD);

        blockItem(TolkienBlocks.TORCH_MIRKWOOD);
        blockItem(TolkienBlocks.WALL_TORCH_MIRKWOOD);
        blockItem(TolkienBlocks.LOG_MIRKWOOD);
        blockItem(TolkienBlocks.WOOD_MIRKWOOD);
        blockItem(TolkienBlocks.STRIPPED_MIRKWOOD_LOG);
        blockItem(TolkienBlocks.STRIPPED_MIRKWOOD_WOOD);
        blockItem(TolkienBlocks.STAIRS_MIRKWOOD);
        blockItem(TolkienBlocks.SLAB_MIRKWOOD);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MIRKWOOD);
        blockItem(TolkienBlocks.FENCE_GATE_MIRKWOOD);
        blockItem(TolkienBlocks.TRAPDOOR_MIRKWOOD, "_bottom");
        blockItem(TolkienBlocks.BARREL_MIRKWOOD);

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
        ModelFile barrelCulumalda = models().cubeBottomTop("barrel_culumalda", modLoc("block/barrel/barrel_culumalda_side"), modLoc("block/barrel/barrel_culumalda_bottom"), modLoc("block/barrel/barrel_culumalda_top"));
        ModelFile barrelCulumaldaOpen = models().cubeBottomTop("barrel_culumalda_open", modLoc("block/barrel/barrel_culumalda_side"), modLoc("block/barrel/barrel_culumalda_bottom"), modLoc("block/barrel/barrel_culumalda_top_open"));
        directionalBlock(TolkienBlocks.BARREL_CULUMALDA.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelCulumaldaOpen : barrelCulumalda);
        signBlock(TolkienBlocks.CULUMALDA_SIGN, TolkienBlocks.CULUMALDA_WALL_SIGN, TolkienBlocks.PLANKS_CULUMALDA);
        hangingSignBlock(TolkienBlocks.CULUMALDA_HANGING_SIGN, TolkienBlocks.CULUMALDA_HANGING_WALL_SIGN, TolkienBlocks.PLANKS_CULUMALDA);
        ladder(TolkienBlocks.LADDER_CULUMALDA, TolkienBlocks.PLANKS_CULUMALDA);

        blockItem(TolkienBlocks.TORCH_CULUMALDA);
        blockItem(TolkienBlocks.WALL_TORCH_CULUMALDA);
        blockItem(TolkienBlocks.LOG_CULUMALDA);
        blockItem(TolkienBlocks.WOOD_CULUMALDA);
        blockItem(TolkienBlocks.STRIPPED_CULUMALDA_LOG);
        blockItem(TolkienBlocks.STRIPPED_CULUMALDA_WOOD);
        blockItem(TolkienBlocks.STAIRS_CULUMALDA);
        blockItem(TolkienBlocks.SLAB_CULUMALDA);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CULUMALDA);
        blockItem(TolkienBlocks.FENCE_GATE_CULUMALDA);
        blockItem(TolkienBlocks.TRAPDOOR_CULUMALDA, "_bottom");
        blockItem(TolkienBlocks.BARREL_CULUMALDA);

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
        ModelFile barrelLebethron = models().cubeBottomTop("barrel_lebethron", modLoc("block/barrel/barrel_lebethron_side"), modLoc("block/barrel/barrel_lebethron_bottom"), modLoc("block/barrel/barrel_lebethron_top"));
        ModelFile barrelLebethronOpen = models().cubeBottomTop("barrel_lebethron_open", modLoc("block/barrel/barrel_lebethron_side"), modLoc("block/barrel/barrel_lebethron_bottom"), modLoc("block/barrel/barrel_lebethron_top_open"));
        directionalBlock(TolkienBlocks.BARREL_LEBETHRON.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelLebethronOpen : barrelLebethron);
        signBlock(TolkienBlocks.LEBETHRON_SIGN, TolkienBlocks.LEBETHRON_WALL_SIGN, TolkienBlocks.PLANKS_LEBETHRON);
        hangingSignBlock(TolkienBlocks.LEBETHRON_HANGING_SIGN, TolkienBlocks.LEBETHRON_HANGING_WALL_SIGN, TolkienBlocks.PLANKS_LEBETHRON);
        ladder(TolkienBlocks.LADDER_LEBETHRON, TolkienBlocks.PLANKS_LEBETHRON);

        blockItem(TolkienBlocks.TORCH_LEBETHRON);
        blockItem(TolkienBlocks.WALL_TORCH_LEBETHRON);
        blockItem(TolkienBlocks.LOG_LEBETHRON);
        blockItem(TolkienBlocks.WOOD_LEBETHRON);
        blockItem(TolkienBlocks.STRIPPED_LEBETHRON_LOG);
        blockItem(TolkienBlocks.STRIPPED_LEBETHRON_WOOD);
        blockItem(TolkienBlocks.STAIRS_LEBETHRON);
        blockItem(TolkienBlocks.SLAB_LEBETHRON);
        blockItem(TolkienBlocks.PRESSURE_PLATE_LEBETHRON);
        blockItem(TolkienBlocks.FENCE_GATE_LEBETHRON);
        blockItem(TolkienBlocks.TRAPDOOR_LEBETHRON, "_bottom");
        blockItem(TolkienBlocks.BARREL_LEBETHRON);

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
        ModelFile barrelFangornOak = models().cubeBottomTop("barrel_fangornoak", modLoc("block/barrel/barrel_fangornoak_side"), modLoc("block/barrel/barrel_fangornoak_bottom"), modLoc("block/barrel/barrel_fangornoak_top"));
        ModelFile barrelFangornOakOpen = models().cubeBottomTop("barrel_fangornoak_open", modLoc("block/barrel/barrel_fangornoak_side"), modLoc("block/barrel/barrel_fangornoak_bottom"), modLoc("block/barrel/barrel_fangornoak_top_open"));
        directionalBlock(TolkienBlocks.BARREL_FANGORNOAK.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelFangornOakOpen : barrelFangornOak);
        signBlock(TolkienBlocks.FANGORNOAK_SIGN, TolkienBlocks.FANGORNOAK_WALL_SIGN, TolkienBlocks.PLANKS_FANGORNOAK);
        hangingSignBlock(TolkienBlocks.FANGORNOAK_HANGING_SIGN, TolkienBlocks.FANGORNOAK_HANGING_WALL_SIGN, TolkienBlocks.PLANKS_FANGORNOAK);
        ladder(TolkienBlocks.LADDER_FANGORNOAK, TolkienBlocks.PLANKS_FANGORNOAK);

        blockItem(TolkienBlocks.TORCH_FANGORNOAK);
        blockItem(TolkienBlocks.WALL_TORCH_FANGORNOAK);
        blockItem(TolkienBlocks.LOG_FANGORNOAK);
        blockItem(TolkienBlocks.WOOD_FANGORNOAK);
        blockItem(TolkienBlocks.STRIPPED_FANGORNOAK_LOG);
        blockItem(TolkienBlocks.STRIPPED_FANGORNOAK_WOOD);
        blockItem(TolkienBlocks.STAIRS_FANGORNOAK);
        blockItem(TolkienBlocks.SLAB_FANGORNOAK);
        blockItem(TolkienBlocks.PRESSURE_PLATE_FANGORNOAK);
        blockItem(TolkienBlocks.FENCE_GATE_FANGORNOAK);
        blockItem(TolkienBlocks.TRAPDOOR_FANGORNOAK, "_bottom");
        blockItem(TolkienBlocks.BARREL_FANGORNOAK);

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
        ModelFile barrelDeadwood = models().cubeBottomTop("barrel_deadwood", modLoc("block/barrel/barrel_deadwood_side"), modLoc("block/barrel/barrel_deadwood_bottom"), modLoc("block/barrel/barrel_deadwood_top"));
        ModelFile barrelDeadwoodOpen = models().cubeBottomTop("barrel_deadwood_open", modLoc("block/barrel/barrel_deadwood_side"), modLoc("block/barrel/barrel_deadwood_bottom"), modLoc("block/barrel/barrel_deadwood_top_open"));
        directionalBlock(TolkienBlocks.BARREL_DEADWOOD.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelDeadwoodOpen : barrelDeadwood);
        signBlock(TolkienBlocks.DEADWOOD_SIGN, TolkienBlocks.DEADWOOD_WALL_SIGN, TolkienBlocks.PLANKS_DEADWOOD);
        hangingSignBlock(TolkienBlocks.DEADWOOD_HANGING_SIGN, TolkienBlocks.DEADWOOD_HANGING_WALL_SIGN, TolkienBlocks.PLANKS_DEADWOOD);
        ladder(TolkienBlocks.LADDER_DEADWOOD, TolkienBlocks.PLANKS_DEADWOOD);

        blockItem(TolkienBlocks.TORCH_DEADWOOD);
        blockItem(TolkienBlocks.WALL_TORCH_DEADWOOD);
        blockItem(TolkienBlocks.LOG_DEADWOOD);
        blockItem(TolkienBlocks.WOOD_DEADWOOD);
        blockItem(TolkienBlocks.STRIPPED_DEADWOOD_LOG);
        blockItem(TolkienBlocks.STRIPPED_DEADWOOD_WOOD);
        blockItem(TolkienBlocks.STAIRS_DEADWOOD);
        blockItem(TolkienBlocks.SLAB_DEADWOOD);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DEADWOOD);
        blockItem(TolkienBlocks.FENCE_GATE_DEADWOOD);
        blockItem(TolkienBlocks.TRAPDOOR_DEADWOOD, "_bottom");
        blockItem(TolkienBlocks.BARREL_DEADWOOD);

            // Dwarven Maple
        logBlock((RotatedPillarBlock)TolkienBlocks.LOG_DWARVEN_MAPLE.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.WOOD_DWARVEN_MAPLE.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_DWARVEN_MAPLE_LOG.get());
        logBlock((RotatedPillarBlock)TolkienBlocks.STRIPPED_DWARVEN_MAPLE_WOOD.get());
        blockWithItem(TolkienBlocks.PLANKS_DWARVEN_MAPLE);
        leavesBlock(TolkienBlocks.LEAVES_DWARVEN_MAPLE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_DWARVEN_MAPLE.get()), blockTexture(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_DWARVEN_MAPLE.get()), blockTexture(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get()), blockTexture(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_DWARVEN_MAPLE.get()), blockTexture(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.DWARVEN_MAPLE_BUTTON.get()), blockTexture(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get()));
        fenceBlock(((FenceBlock) TolkienBlocks.FENCE_DWARVEN_MAPLE.get()), blockTexture(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get()));
        fenceGateBlock(((FenceGateBlock) TolkienBlocks.FENCE_GATE_DWARVEN_MAPLE.get()), blockTexture(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get()));
        doorBlockWithRenderType(((DoorBlock) TolkienBlocks.DOOR_DWARVEN_MAPLE.get()), modLoc("block/door_dwarven_maple_bottom"), modLoc("block/door_dwarven_maple_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) TolkienBlocks.TRAPDOOR_DWARVEN_MAPLE.get()), modLoc("block/trapdoor_dwarven_maple"), true, "cutout");
        torchBlock(TolkienBlocks.TORCH_DWARVEN_MAPLE, TolkienBlocks.WALL_TORCH_DWARVEN_MAPLE);
        ModelFile barrelDwarvenMaple = models().cubeBottomTop("barrel_dwarven_maple", modLoc("block/barrel/barrel_dwarven_maple_side"), modLoc("block/barrel/barrel_dwarven_maple_bottom"), modLoc("block/barrel/barrel_dwarven_maple_top"));
        ModelFile barrelDwarvenMapleOpen = models().cubeBottomTop("barrel_dwarven_maple_open", modLoc("block/barrel/barrel_dwarven_maple_side"), modLoc("block/barrel/barrel_dwarven_maple_bottom"), modLoc("block/barrel/barrel_dwarven_maple_top_open"));
        directionalBlock(TolkienBlocks.BARREL_DWARVEN_MAPLE.get(), state -> state.getValue(BarrelBlock.OPEN) ? barrelDwarvenMapleOpen : barrelDwarvenMaple);
        signBlock(TolkienBlocks.DWARVEN_MAPLE_SIGN, TolkienBlocks.DWARVEN_MAPLE_WALL_SIGN, TolkienBlocks.PLANKS_DWARVEN_MAPLE);
        hangingSignBlock(TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN, TolkienBlocks.DWARVEN_MAPLE_HANGING_WALL_SIGN, TolkienBlocks.PLANKS_DWARVEN_MAPLE);
        ladder(TolkienBlocks.LADDER_DWARVEN_MAPLE, TolkienBlocks.PLANKS_DWARVEN_MAPLE);

        blockItem(TolkienBlocks.TORCH_DWARVEN_MAPLE);
        blockItem(TolkienBlocks.WALL_TORCH_DWARVEN_MAPLE);
        blockItem(TolkienBlocks.LOG_DWARVEN_MAPLE);
        blockItem(TolkienBlocks.WOOD_DWARVEN_MAPLE);
        blockItem(TolkienBlocks.STRIPPED_DWARVEN_MAPLE_LOG);
        blockItem(TolkienBlocks.STRIPPED_DWARVEN_MAPLE_WOOD);
        blockItem(TolkienBlocks.STAIRS_DWARVEN_MAPLE);
        blockItem(TolkienBlocks.SLAB_DWARVEN_MAPLE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DWARVEN_MAPLE);
        blockItem(TolkienBlocks.FENCE_GATE_DWARVEN_MAPLE);
        blockItem(TolkienBlocks.TRAPDOOR_DWARVEN_MAPLE, "_bottom");
        blockItem(TolkienBlocks.BARREL_DWARVEN_MAPLE);

            // Darkstone
        blockWithItem(TolkienBlocks.DARK_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_DARK_STONE.get()), blockTexture(TolkienBlocks.DARK_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_DARK_STONE.get()), blockTexture(TolkienBlocks.DARK_STONE.get()), blockTexture(TolkienBlocks.DARK_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_DARK_STONE.get()), blockTexture(TolkienBlocks.DARK_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.DARK_STONE_BUTTON.get()), blockTexture(TolkienBlocks.DARK_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_DARK_STONE.get()), blockTexture(TolkienBlocks.DARK_STONE.get()));
        blockWithItem(TolkienBlocks.CHISELED_DARK_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CHISELED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CHISELED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CHISELED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CHISELED_DARK_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_CHISELED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.COBBLED_DARK_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_COBBLED_DARK_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DARK_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_COBBLED_DARK_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DARK_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DARK_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_COBBLED_DARK_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DARK_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.COBBLED_DARK_STONE_BUTTON.get()), blockTexture(TolkienBlocks.COBBLED_DARK_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_COBBLED_DARK_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DARK_STONE.get()));
        blockWithItem(TolkienBlocks.CRACKED_DARK_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CRACKED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CRACKED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CRACKED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CRACKED_DARK_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_CRACKED_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.DARK_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DARK_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DARK_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DARK_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.DARK_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.DARK_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_DARK_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DARK_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.SMOOTH_DARK_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_SMOOTH_DARK_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DARK_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_SMOOTH_DARK_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DARK_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DARK_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_SMOOTH_DARK_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DARK_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.SMOOTH_DARK_STONE_BUTTON.get()), blockTexture(TolkienBlocks.SMOOTH_DARK_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_SMOOTH_DARK_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DARK_STONE.get()));

        blockItem(TolkienBlocks.STAIRS_SMOOTH_DARK_STONE);
        blockItem(TolkienBlocks.SLAB_SMOOTH_DARK_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_SMOOTH_DARK_STONE);
        blockItem(TolkienBlocks.STAIRS_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_CRACKED_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_CRACKED_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CRACKED_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_COBBLED_DARK_STONE);
        blockItem(TolkienBlocks.SLAB_COBBLED_DARK_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_COBBLED_DARK_STONE);
        blockItem(TolkienBlocks.STAIRS_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_CHISELED_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_CHISELED_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CHISELED_DARK_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_DARK_STONE);
        blockItem(TolkienBlocks.SLAB_DARK_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DARK_STONE);

            // Dwarven Stone
        blockWithItem(TolkienBlocks.DWARVEN_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.DWARVEN_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.DWARVEN_STONE.get()), blockTexture(TolkienBlocks.DWARVEN_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.DWARVEN_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.DWARVEN_STONE_BUTTON.get()), blockTexture(TolkienBlocks.DWARVEN_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.DWARVEN_STONE.get()));
        blockWithItem(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CHISELED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CHISELED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CHISELED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_CHISELED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.COBBLED_DWARVEN_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_COBBLED_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DWARVEN_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_COBBLED_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DWARVEN_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_COBBLED_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DWARVEN_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.COBBLED_DWARVEN_STONE_BUTTON.get()), blockTexture(TolkienBlocks.COBBLED_DWARVEN_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_COBBLED_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_DWARVEN_STONE.get()));
        blockWithItem(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CRACKED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CRACKED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CRACKED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_CRACKED_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.DWARVEN_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DWARVEN_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DWARVEN_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DWARVEN_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.DWARVEN_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.DWARVEN_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_DWARVEN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.DWARVEN_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.SMOOTH_DWARVEN_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_SMOOTH_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DWARVEN_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_SMOOTH_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DWARVEN_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_SMOOTH_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DWARVEN_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.SMOOTH_DWARVEN_STONE_BUTTON.get()), blockTexture(TolkienBlocks.SMOOTH_DWARVEN_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_SMOOTH_DWARVEN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_DWARVEN_STONE.get()));

        blockItem(TolkienBlocks.STAIRS_SMOOTH_DWARVEN_STONE);
        blockItem(TolkienBlocks.SLAB_SMOOTH_DWARVEN_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_SMOOTH_DWARVEN_STONE);
        blockItem(TolkienBlocks.STAIRS_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_CRACKED_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_CRACKED_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CRACKED_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_COBBLED_DWARVEN_STONE);
        blockItem(TolkienBlocks.SLAB_COBBLED_DWARVEN_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_COBBLED_DWARVEN_STONE);
        blockItem(TolkienBlocks.STAIRS_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_CHISELED_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_CHISELED_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CHISELED_DWARVEN_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_DWARVEN_STONE);
        blockItem(TolkienBlocks.SLAB_DWARVEN_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE);

            // Elven Marble
        blockWithItem(TolkienBlocks.ELVEN_MARBLE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.ELVEN_MARBLE_BUTTON.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE.get()));
        blockWithItem(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CHISELED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CHISELED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CHISELED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_CHISELED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get()));
        blockWithItem(TolkienBlocks.COBBLED_ELVEN_MARBLE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_COBBLED_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.COBBLED_ELVEN_MARBLE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_COBBLED_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.COBBLED_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.COBBLED_ELVEN_MARBLE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_COBBLED_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.COBBLED_ELVEN_MARBLE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.COBBLED_ELVEN_MARBLE_BUTTON.get()), blockTexture(TolkienBlocks.COBBLED_ELVEN_MARBLE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_COBBLED_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.COBBLED_ELVEN_MARBLE.get()));
        blockWithItem(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CRACKED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CRACKED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CRACKED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_CRACKED_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get()));
        blockWithItem(TolkienBlocks.ELVEN_MARBLE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.ELVEN_MARBLE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_ELVEN_MARBLE_BRICKS.get()), blockTexture(TolkienBlocks.ELVEN_MARBLE_BRICKS.get()));
        blockWithItem(TolkienBlocks.SMOOTH_ELVEN_MARBLE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_SMOOTH_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.SMOOTH_ELVEN_MARBLE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_SMOOTH_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.SMOOTH_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.SMOOTH_ELVEN_MARBLE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_SMOOTH_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.SMOOTH_ELVEN_MARBLE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.SMOOTH_ELVEN_MARBLE_BUTTON.get()), blockTexture(TolkienBlocks.SMOOTH_ELVEN_MARBLE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_SMOOTH_ELVEN_MARBLE.get()), blockTexture(TolkienBlocks.SMOOTH_ELVEN_MARBLE.get()));

        blockItem(TolkienBlocks.STAIRS_SMOOTH_ELVEN_MARBLE);
        blockItem(TolkienBlocks.SLAB_SMOOTH_ELVEN_MARBLE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_SMOOTH_ELVEN_MARBLE);
        blockItem(TolkienBlocks.STAIRS_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.SLAB_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_CRACKED_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.SLAB_CRACKED_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CRACKED_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_COBBLED_ELVEN_MARBLE);
        blockItem(TolkienBlocks.SLAB_COBBLED_ELVEN_MARBLE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_COBBLED_ELVEN_MARBLE);
        blockItem(TolkienBlocks.STAIRS_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.SLAB_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_CHISELED_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.SLAB_CHISELED_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CHISELED_ELVEN_MARBLE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_ELVEN_MARBLE);
        blockItem(TolkienBlocks.SLAB_ELVEN_MARBLE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_ELVEN_MARBLE);

            // Mountain Stone
        blockWithItem(TolkienBlocks.MOUNTAIN_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.MOUNTAIN_STONE_BUTTON.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE.get()));
        blockWithItem(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CHISELED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CHISELED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CHISELED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_CHISELED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.COBBLED_MOUNTAIN_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_COBBLED_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_MOUNTAIN_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_COBBLED_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_MOUNTAIN_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_COBBLED_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_MOUNTAIN_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.COBBLED_MOUNTAIN_STONE_BUTTON.get()), blockTexture(TolkienBlocks.COBBLED_MOUNTAIN_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_COBBLED_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.COBBLED_MOUNTAIN_STONE.get()));
        blockWithItem(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_CRACKED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_CRACKED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_CRACKED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_CRACKED_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.MOUNTAIN_STONE_BRICKS);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.MOUNTAIN_STONE_BRICKS_BUTTON.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_MOUNTAIN_STONE_BRICKS.get()), blockTexture(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get()));
        blockWithItem(TolkienBlocks.SMOOTH_MOUNTAIN_STONE);
        stairsBlock(((StairBlock) TolkienBlocks.STAIRS_SMOOTH_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_MOUNTAIN_STONE.get()));
        slabBlock(((SlabBlock) TolkienBlocks.SLAB_SMOOTH_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_MOUNTAIN_STONE.get()));
        pressurePlateBlock(((PressurePlateBlock) TolkienBlocks.PRESSURE_PLATE_SMOOTH_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_MOUNTAIN_STONE.get()));
        buttonBlock(((ButtonBlock) TolkienBlocks.SMOOTH_MOUNTAIN_STONE_BUTTON.get()), blockTexture(TolkienBlocks.SMOOTH_MOUNTAIN_STONE.get()));
        wallBlock(((WallBlock) TolkienBlocks.WALL_SMOOTH_MOUNTAIN_STONE.get()), blockTexture(TolkienBlocks.SMOOTH_MOUNTAIN_STONE.get()));

        blockItem(TolkienBlocks.STAIRS_SMOOTH_MOUNTAIN_STONE);
        blockItem(TolkienBlocks.SLAB_SMOOTH_MOUNTAIN_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_SMOOTH_MOUNTAIN_STONE);
        blockItem(TolkienBlocks.STAIRS_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_CRACKED_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_CRACKED_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CRACKED_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_COBBLED_MOUNTAIN_STONE);
        blockItem(TolkienBlocks.SLAB_COBBLED_MOUNTAIN_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_COBBLED_MOUNTAIN_STONE);
        blockItem(TolkienBlocks.STAIRS_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_CHISELED_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.SLAB_CHISELED_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.PRESSURE_PLATE_CHISELED_MOUNTAIN_STONE_BRICKS);
        blockItem(TolkienBlocks.STAIRS_MOUNTAIN_STONE);
        blockItem(TolkienBlocks.SLAB_MOUNTAIN_STONE);
        blockItem(TolkienBlocks.PRESSURE_PLATE_MOUNTAIN_STONE);

        //Flowers & Plants
        makeFlower(TolkienBlocks.FLOWER_SIMBELMYNE, TolkienBlocks.POTTED_FLOWER_SIMBELMYNE);
        makeFlower(TolkienBlocks.FLOWER_MIRKWOOD, TolkienBlocks.POTTED_FLOWER_MIRKWOOD);
        makeFlower(TolkienBlocks.FLOWER_ALFIRIN, TolkienBlocks.POTTED_FLOWER_ALFIRIN);
        makeFlower(TolkienBlocks.FLOWER_ATHELAS, TolkienBlocks.POTTED_FLOWER_ATHELAS);
        makeFlower(TolkienBlocks.FLOWER_NIPHREDIL, TolkienBlocks.POTTED_FLOWER_NIPHREDIL);
        makeFlower(TolkienBlocks.FLOWER_SWAMPMILKWEED, TolkienBlocks.POTTED_FLOWER_SWAMPMILKWEED);
        makeFlower(TolkienBlocks.FLOWER_LILLYOFTHEVALLEY, TolkienBlocks.POTTED_FLOWER_LILLYOFTHEVALLEY);
        makeFlower(TolkienBlocks.FLOWER_ELANOR, TolkienBlocks.POTTED_FLOWER_ELANOR);
        makeFlower(TolkienBlocks.FLOWER_AEGLOS, TolkienBlocks.POTTED_FLOWER_AEGLOS);
        makeFlower(TolkienBlocks.FLOWER_LISSUIN, TolkienBlocks.POTTED_FLOWER_LISSUIN);
        makeFlower(TolkienBlocks.FLOWER_MALLOS, TolkienBlocks.POTTED_FLOWER_MALLOS);
        makeFlower(TolkienBlocks.SAPLING_MALLORN, TolkienBlocks.POTTED_SAPLING_MALLORN);
        makeFlower(TolkienBlocks.SAPLING_MIRKWOOD, TolkienBlocks.POTTED_SAPLING_MIRKWOOD);
        makeFlower(TolkienBlocks.SAPLING_CULUMALDA, TolkienBlocks.POTTED_SAPLING_CULUMALDA);
        makeFlower(TolkienBlocks.SAPLING_LEBETHRON, TolkienBlocks.POTTED_SAPLING_LEBETHRON);
        makeFlower(TolkienBlocks.SAPLING_FANGORNOAK, TolkienBlocks.POTTED_SAPLING_FANGORNOAK);
        makeFlower(TolkienBlocks.SAPLING_DEADWOOD, TolkienBlocks.POTTED_SAPLING_DEADWOOD);
        makeFlower(TolkienBlocks.SAPLING_DWARVEN_MAPLE, TolkienBlocks.POTTED_SAPLING_DWARVEN_MAPLE);
        makeFlower(TolkienBlocks.MUSHROOM_BLOOM_DECAY, TolkienBlocks.POTTED_MUSHROOM_BLOOM_DECAY);
        makeFlower(TolkienBlocks.MUSHROOM_DECAY_BLOOM, TolkienBlocks.POTTED_MUSHROOM_DECAY_BLOOM);
        blockWithItem(TolkienBlocks.BLOCK_DECAY_BLOOM);
        blockWithItem(TolkienBlocks.BLOCK_BLOOM_DECAY);
        blockWithItem(TolkienBlocks.LIVING_ROOTS);

        makeCrop(((PipeweedCropBlock) TolkienBlocks.PIPEWEED.get()), "pipeweed_crop_stage", "pipeweed_stage");

        makeBush(((BramblesBushBlock) TolkienBlocks.FLOWER_BRAMBLES.get()), TolkienBlocks.POTTED_FLOWER_BRAMBLES, "flower_brambles", "flower_brambles_stage");

        leafpileBuilder(TolkienBlocks.LEAFPILE_MALLORN.get(), "mallorn");
        leafpileBuilder(TolkienBlocks.LEAFPILE_MIRKWOOD.get(), "mirkwood");
        leafpileBuilder(TolkienBlocks.LEAFPILE_CULUMALDA.get(), "culumalda");
        leafpileBuilder(TolkienBlocks.LEAFPILE_LEBETHRON.get(), "lebethron");
        leafpileBuilder(TolkienBlocks.LEAFPILE_FANGORNOAK.get(), "fangornoak");
        leafpileBuilder(TolkienBlocks.LEAFPILE_DWARVEN_MAPLE.get(), "dwarven_maple");

        blockItem(TolkienBlocks.FLOWER_BRAMBLES, "3");

            // Custom
        builtinEntity(TolkienBlocks.LIGHTNINGBUG_BLOCK.get(), "block/blank");
        builtinEntity(TolkienBlocks.LOCUST_BLOCK.get(), "block/blank");

        simpleBlock(TolkienBlocks.BLOCK_HALLOWED.value(), this.models().cubeBottomTop("block_hallowed", modLoc("block/block_hallowed_side"), modLoc("block/block_hallowed"), modLoc("block/block_hallowed_top")));
        simpleBlock(TolkienBlocks.STONE_PATH.value(), this.models().getExistingFile(modLoc("block/block_stone_path")));

        blockItem(TolkienBlocks.BLOCK_HALLOWED);
        blockItem(TolkienBlocks.STONE_PATH);

            //Placards
        ModelFile placardWallModel = models().getExistingFile(modLoc("block/placard_wall"));
        ModelFile placardStandingModel = models().getExistingFile(modLoc("block/placard_standing"));
        ModelFile placardHangingModel = models().getExistingFile(modLoc("block/placard_hanging"));

        VariantBlockStateBuilder placardBuilder = getVariantBuilder(TolkienBlocks.PLACARD.get());
        for (AttachFace face : PlacardBlock.ATTACH_FACE.getPossibleValues()) {
            for (PlacardBlock.PlacardType type : PlacardBlock.PlacardType.values()) {
                ModelFile baseModel = face == AttachFace.FLOOR ? placardStandingModel : face == AttachFace.CEILING ? placardHangingModel : placardWallModel;
                ModelFile model = models().getBuilder("placard_" + face.getSerializedName() + "_" + type.getName()).parent(baseModel).texture("tex", "tolkienmobs:block/signs/placard_" + type.getName());
                for (Direction dir : PlacardBlock.FACING.getPossibleValues()) {

                    int angle = (int) dir.toYRot();
                    placardBuilder.partialState()
                            .with(PlacardBlock.FACING, dir)
                            .with(PlacardBlock.ATTACH_FACE, face)
                            .with(PlacardBlock.PLACARD_TYPE, type)
                            .modelForState()
                            .modelFile(model)
                            .rotationY(angle)
                            .addModel();
                }
            }
        }
        blockItem(TolkienBlocks.PLACARD, "_wall_empty");

        simpleBlock(TolkienBlocks.CAMO_GLOWSTONE_BLOCK.get());
        simpleBlock(TolkienBlocks.CAMO_SMOKER_BLOCK.get());
        simpleBlock(TolkienBlocks.CAMO_FLUID_BLOCK.get());
        simpleBlock(TolkienBlocks.CHAMELEON_BLOCK.get());
        simpleBlock(TolkienBlocks.CAMO_CHEST_BLOCK.get(), models().getExistingFile(modLoc("block/block_camo_chest")));
        simpleBlock(TolkienBlocks.CAMO_SPAWNER_BLOCK.value(), this.models().cubeAll("block_camo_spawner", modLoc("block/block_camo_spawner")).renderType("translucent"));
        horizontalBlock(TolkienBlocks.LOCKABLE_CHEST_BLOCK.get(), models().getExistingFile(modLoc("block/lockable_chest_block")), 180);
        horizontalBlock(TolkienBlocks.LOCKABLE_TREASURE_CHEST_BLOCK.get(), models().getExistingFile(modLoc("block/lockable_treasure_chest_block")), 180);
        horizontalBlock(TolkienBlocks.LOCKABLE_DOUBLE_CHEST_BLOCK.get(), models().getExistingFile(modLoc("block/lockable_double_chest_block")), 180);
        horizontalBlock(TolkienBlocks.LOCKABLE_DOUBLE_TREASURE_CHEST_BLOCK.get(), models().getExistingFile(modLoc("block/lockable_double_treasure_chest_block")), 180);
        horizontalBlock(TolkienBlocks.BACKPACK.get(), models().getExistingFile(modLoc("block/container_backpack")), 0);
        horizontalBlock(TolkienBlocks.WELL.get(), models().getExistingFile(modLoc("block/well_block")), 0);

        ModelFile trinketActive = models().getExistingFile(modLoc("block/trinket_table_active"));
        ModelFile trinketInactive = models().getExistingFile(modLoc("block/trinket_table"));
        horizontalBlock(TolkienBlocks.TRINKET_TABLE.get(), state -> state.getValue(TrinketTableBlock.LIT) ? trinketActive : trinketInactive, 0);

        ModelFile bankFull = models().getExistingFile(modLoc("block/block_piggybank_full"));
        ModelFile bankEmpty = models().getExistingFile(modLoc("block/block_piggybank"));
        horizontalBlock(TolkienBlocks.PIGGYBANK.get(), e -> e.getValue(PiggyBankBlock.FULL) ? bankFull : bankEmpty, 180);

        ModelFile fireActive = models().getExistingFile(modLoc("block/fireplace_active"));
        ModelFile fireInactive = models().getExistingFile(modLoc("block/fireplace_inactive"));
        horizontalBlock(TolkienBlocks.FIREPLACE.get(), e -> e.getValue(FireplaceBlock.LIT) ? fireActive : fireInactive, 180);

        ModelFile keyActive = models().getExistingFile(modLoc("block/block_key_stone_active"));
        ModelFile keyInactive = models().getExistingFile(modLoc("block/block_key_stone"));
        horizontalBlock(TolkienBlocks.KEY_STONE_BLOCK.get(), e -> e.getValue(CamoKeyStoneBlock.ACTIVE) ? keyActive : keyInactive, 180);

        ModelFile mileActive = models().getExistingFile(modLoc("block/milestone_block_active"));
        ModelFile mileInactive = models().getExistingFile(modLoc("block/milestone_block"));
        horizontalBlock(TolkienBlocks.MILESTONE_BLOCK.get(), e -> e.getValue(MilestoneBlock.LIT) ? mileActive : mileInactive, 180);

        blockItem(TolkienBlocks.TRINKET_TABLE);
        simpleBlockItem(TolkienBlocks.FIREPLACE.get(), models().getExistingFile(modLoc("block/fireplace_active")));
        blockItem(TolkienBlocks.PIGGYBANK);
        simpleBlockItem(TolkienBlocks.BACKPACK.get(), models().getExistingFile(modLoc("block/container_backpack")));
        blockItem(TolkienBlocks.CAMO_FLUID_BLOCK);
        blockItem(TolkienBlocks.CAMO_CHEST_BLOCK);
        blockItem(TolkienBlocks.CAMO_SPAWNER_BLOCK);
        blockItem(TolkienBlocks.CAMO_GLOWSTONE_BLOCK);
        blockItem(TolkienBlocks.CHAMELEON_BLOCK);
        blockItem(TolkienBlocks.CAMO_SMOKER_BLOCK);
        blockItem(TolkienBlocks.KEY_STONE_BLOCK);
        blockItem(TolkienBlocks.MILESTONE_BLOCK);
        blockItem(TolkienBlocks.WELL);

        fluid();
    }
}
