package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.block.BaseFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);

    /* Metals & Gems */
        // Mithril
    public static final DeferredBlock<Block> ORE_MITHRIL = registerBlock("ore_mithril",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_END_MITHRIL = registerBlock("ore_end_mithril",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_NETHER_MITHRIL = registerBlock("ore_nether_mithril",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_DEEPSLATE_MITHRIL = registerBlock("ore_deepslate_mithril",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));
    public static final DeferredBlock<Block> BLOCK_MITHRIL = registerBlock("block_mithril",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5f).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> RAW_MITHRIL_BLOCK = registerBlock("raw_mithril_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4f).requiresCorrectToolForDrops().sound(SoundType.ANCIENT_DEBRIS)));
    public static final DeferredBlock<Block> STAIRS_MITHRIL = registerBlock("stairs_mithril",
            () -> new StairBlock(TolkienBlocks.BLOCK_MITHRIL.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_MITHRIL = registerBlock("slab_mithril",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MITHRIL = registerBlock("pressure_plate_mithril",
            () -> new PressurePlateBlock(BlockSetType.GOLD, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MITHRIL_BUTTON = registerBlock("mithril_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
//    public static final DeferredBlock<Block> MITHRIL_BARS = registerBlock("mithril_bars",
//            () -> new IronBarsBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
//                    .strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<Block> WALL_MITHRIL = registerBlock("wall_mithril",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DOOR_MITHRIL = registerBlock("door_mithril",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> TRAPDOOR_MITHRIL = registerBlock("trapdoor_mithril",
            () -> new TrapDoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));

        // MorgulIron
    public static final DeferredBlock<Block> ORE_MORGULIRON = registerBlock("ore_morguliron",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_END_MORGULIRON = registerBlock("ore_end_morguliron",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_NETHER_MORGULIRON = registerBlock("ore_nether_morguliron",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_DEEPSLATE_MORGULIRON = registerBlock("ore_deepslate_morguliron",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));
    public static final DeferredBlock<Block> BLOCK_MORGULIRON = registerBlock("block_morguliron",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5f).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> RAW_MORGULIRON_BLOCK = registerBlock("raw_morguliron_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4f).requiresCorrectToolForDrops().sound(SoundType.ANCIENT_DEBRIS)));
    public static final DeferredBlock<Block> STAIRS_MORGULIRON = registerBlock("stairs_morguliron",
            () -> new StairBlock(TolkienBlocks.BLOCK_MORGULIRON.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_MORGULIRON = registerBlock("slab_morguliron",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MORGULIRON = registerBlock("pressure_plate_morguliron",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MORGULIRON_BUTTON = registerBlock("morguliron_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
//    public static final DeferredBlock<Block> MORGULIRON_BARS = registerBlock("morguliron_bars",
//            () -> new IronBarsBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
//                    .strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<Block> WALL_MORGULIRON = registerBlock("wall_morguliron",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DOOR_MORGULIRON = registerBlock("door_morguliron",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> TRAPDOOR_MORGULIRON = registerBlock("trapdoor_morguliron",
            () -> new TrapDoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));

        // Ammolite
    public static final DeferredBlock<Block> ORE_AMMOLITE = registerBlock("ore_ammolite",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_END_AMMOLITE = registerBlock("ore_end_ammolite",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_NETHER_AMMOLITE = registerBlock("ore_nether_ammolite",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORE_DEEPSLATE_AMMOLITE = registerBlock("ore_deepslate_ammolite",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));
    public static final DeferredBlock<Block> BLOCK_AMMOLITE = registerBlock("block_ammolite",
            () -> new TransparentBlock(BlockBehaviour.Properties.of()
                    .strength(0.3f).requiresCorrectToolForDrops().isValidSpawn(Blocks::never).isRedstoneConductor(TolkienBlocks::never).isSuffocating(TolkienBlocks::never).isViewBlocking(TolkienBlocks::never).noOcclusion().sound(SoundType.GLASS)));
//    public static final DeferredBlock<Block> PANE_AMMOLITE = registerBlock("pane_ammolite",
//            () -> new IronBarsBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
//                    .strength(5.0F, 6.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final DeferredBlock<Block> DOOR_DURIN = registerBlock("door_durin",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));

    /* Wood & Foliage */
        // Mallorn
    public static final DeferredBlock<Block> LOG_MALLORN = registerBlock("log_mallorn",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WOOD_MALLORN = registerBlock("wood_mallorn",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_MALLORN_LOG = registerBlock("stripped_log_mallorn",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_MALLORN_WOOD = registerBlock("stripped_wood_mallorn",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_MALLORN = registerBlock("leaves_mallorn",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> PLANKS_MALLORN = registerBlock("planks_mallorn",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STAIRS_MALLORN = registerBlock("stairs_mallorn",
            () -> new StairBlock(TolkienBlocks.PLANKS_MALLORN.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> SLAB_MALLORN = registerBlock("slab_mallorn",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MALLORN = registerBlock("pressure_plate_mallorn",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> MALLORN_BUTTON = registerBlock("mallorn_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> FENCE_MALLORN = registerBlock("fence_mallorn",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> FENCE_GATE_MALLORN = registerBlock("fence_gate_mallorn",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> DOOR_MALLORN = registerBlock("door_mallorn",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> TRAPDOOR_MALLORN = registerBlock("trapdoor_mallorn",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
// torch
// sign
// Barrel
// leafpile

        // Mirkwood
    public static final DeferredBlock<Block> LOG_MIRKWOOD = registerBlock("log_mirkwood",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WOOD_MIRKWOOD = registerBlock("wood_mirkwood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_MIRKWOOD_LOG = registerBlock("stripped_log_mirkwood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_MIRKWOOD_WOOD = registerBlock("stripped_wood_mirkwood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_MIRKWOOD = registerBlock("leaves_mirkwood",
                () -> new LeavesBlock(BlockBehaviour.Properties.of()
                        .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> PLANKS_MIRKWOOD = registerBlock("planks_mirkwood",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STAIRS_MIRKWOOD = registerBlock("stairs_mirkwood",
            () -> new StairBlock(TolkienBlocks.PLANKS_MIRKWOOD.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> SLAB_MIRKWOOD = registerBlock("slab_mirkwood",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MIRKWOOD = registerBlock("pressure_plate_mirkwood",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> MIRKWOOD_BUTTON = registerBlock("mirkwood_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> FENCE_MIRKWOOD = registerBlock("fence_mirkwood",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> FENCE_GATE_MIRKWOOD = registerBlock("fence_gate_mirkwood",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> DOOR_MIRKWOOD = registerBlock("door_mirkwood",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> TRAPDOOR_MIRKWOOD = registerBlock("trapdoor_mirkwood",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));

        // Culumalda
    public static final DeferredBlock<Block> LOG_CULUMALDA = registerBlock("log_culumalda",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WOOD_CULUMALDA = registerBlock("wood_culumalda",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_CULUMALDA_LOG = registerBlock("stripped_log_culumalda",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_CULUMALDA_WOOD = registerBlock("stripped_wood_culumalda",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_CULUMALDA = registerBlock("leaves_culumalda",
                () -> new LeavesBlock(BlockBehaviour.Properties.of()
                        .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> PLANKS_CULUMALDA = registerBlock("planks_culumalda",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STAIRS_CULUMALDA = registerBlock("stairs_culumalda",
            () -> new StairBlock(TolkienBlocks.PLANKS_CULUMALDA.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> SLAB_CULUMALDA = registerBlock("slab_culumalda",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CULUMALDA = registerBlock("pressure_plate_culumalda",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> CULUMALDA_BUTTON = registerBlock("culumalda_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> FENCE_CULUMALDA = registerBlock("fence_culumalda",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> FENCE_GATE_CULUMALDA = registerBlock("fence_gate_culumalda",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> DOOR_CULUMALDA = registerBlock("door_culumalda",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> TRAPDOOR_CULUMALDA = registerBlock("trapdoor_culumalda",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));

        // Lebethron
    public static final DeferredBlock<Block> LOG_LEBETHRON = registerBlock("log_lebethron",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WOOD_LEBETHRON = registerBlock("wood_lebethron",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_LEBETHRON_LOG = registerBlock("stripped_log_lebethron",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_LEBETHRON_WOOD = registerBlock("stripped_wood_lebethron",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_LEBETHRON = registerBlock("leaves_lebethron",
                () -> new LeavesBlock(BlockBehaviour.Properties.of()
                        .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> PLANKS_LEBETHRON = registerBlock("planks_lebethron",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STAIRS_LEBETHRON = registerBlock("stairs_lebethron",
            () -> new StairBlock(TolkienBlocks.PLANKS_LEBETHRON.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> SLAB_LEBETHRON = registerBlock("slab_lebethron",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_LEBETHRON = registerBlock("pressure_plate_lebethron",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> LEBETHRON_BUTTON = registerBlock("lebethron_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> FENCE_LEBETHRON = registerBlock("fence_lebethron",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> FENCE_GATE_LEBETHRON = registerBlock("fence_gate_lebethron",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> DOOR_LEBETHRON = registerBlock("door_lebethron",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> TRAPDOOR_LEBETHRON = registerBlock("trapdoor_lebethron",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));

        // Fangorn Oak
    public static final DeferredBlock<Block> LOG_FANGORNOAK = registerBlock("log_fangornoak",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WOOD_FANGORNOAK = registerBlock("wood_fangornoak",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_FANGORNOAK_LOG = registerBlock("stripped_log_fangornoak",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_FANGORNOAK_WOOD = registerBlock("stripped_wood_fangornoak",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_FANGORNOAK = registerBlock("leaves_fangornoak",
                () -> new LeavesBlock(BlockBehaviour.Properties.of()
                        .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> PLANKS_FANGORNOAK = registerBlock("planks_fangornoak",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STAIRS_FANGORNOAK = registerBlock("stairs_fangornoak",
            () -> new StairBlock(TolkienBlocks.PLANKS_FANGORNOAK.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> SLAB_FANGORNOAK = registerBlock("slab_fangornoak",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_FANGORNOAK = registerBlock("pressure_plate_fangornoak",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> FANGORNOAK_BUTTON = registerBlock("fangornoak_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> FENCE_FANGORNOAK = registerBlock("fence_fangornoak",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> FENCE_GATE_FANGORNOAK = registerBlock("fence_gate_fangornoak",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> DOOR_FANGORNOAK = registerBlock("door_fangornoak",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> TRAPDOOR_FANGORNOAK = registerBlock("trapdoor_fangornoak",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));

        // Deadwood
    public static final DeferredBlock<Block> LOG_DEADWOOD = registerBlock("log_deadwood",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WOOD_DEADWOOD = registerBlock("wood_deadwood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_DEADWOOD_LOG = registerBlock("stripped_log_deadwood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_DEADWOOD_WOOD = registerBlock("stripped_wood_deadwood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> PLANKS_DEADWOOD = registerBlock("planks_deadwood",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STAIRS_DEADWOOD = registerBlock("stairs_deadwood",
            () -> new StairBlock(TolkienBlocks.PLANKS_DEADWOOD.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava().noOcclusion()));
    public static final DeferredBlock<Block> SLAB_DEADWOOD = registerBlock("slab_deadwood",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DEADWOOD = registerBlock("pressure_plate_deadwood",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava().noOcclusion()));
    public static final DeferredBlock<Block> DEADWOOD_BUTTON = registerBlock("deadwood_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> FENCE_DEADWOOD = registerBlock("fence_deadwood",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> FENCE_GATE_DEADWOOD = registerBlock("fence_gate_deadwood",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> DOOR_DEADWOOD = registerBlock("door_deadwood",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> TRAPDOOR_DEADWOOD = registerBlock("trapdoor_deadwood",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));

        // Flowers & Plants
    public static final DeferredBlock<Block> FLOWER_SIMBELMYNE = registerBlock("flower_simbelmyne", () -> new BaseFlowerBlock(MobEffects.HERO_OF_THE_VILLAGE, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_SIMBELMYNE = BLOCKS.register("potted_flower_simbelmyne", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_SIMBELMYNE, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        TolkienItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}