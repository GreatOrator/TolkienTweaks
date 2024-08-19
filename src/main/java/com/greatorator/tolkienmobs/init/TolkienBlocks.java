package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.block.custom.*;
import com.greatorator.tolkienmobs.block.TolkienFlowerBlock;
import com.greatorator.tolkienmobs.block.TolkienTorchBlock;
import com.greatorator.tolkienmobs.block.TolkienWallTorchBlock;
import com.greatorator.tolkienmobs.block.custom.LightningBugBlock;
import com.greatorator.tolkienmobs.block.custom.entity.LightningBugEntity;
import com.greatorator.tolkienmobs.block.custom.entity.LocustEntity;
import com.greatorator.tolkienmobs.init.types.TolkienParticleTypes;
import com.greatorator.tolkienmobs.world.TolkienConfiguredFeatures;
import com.greatorator.tolkienmobs.world.tree.TolkienTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.init.TolkienItems.ITEMS;

public class TolkienBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

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
    public static final DeferredBlock<Block> LEAFPILE_MALLORN = registerBlock("leafpile_mallorn",
            () -> new LeafPileBlock(BlockBehaviour.Properties.of()
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
    public static final DeferredBlock<Block> TORCH_MALLORN = registerBlock("torch_mallorn",
            () -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.MALLORN_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> WALL_TORCH_MALLORN = registerBlock("wall_torch_mallorn",
            () -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.MALLORN_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).lootFrom(TORCH_MALLORN)));
// sign
// Barrel
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
    public static final DeferredBlock<Block> LEAFPILE_MIRKWOOD = registerBlock("leafpile_mirkwood",
            () -> new LeafPileBlock(BlockBehaviour.Properties.of()
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
    public static final DeferredBlock<Block> TORCH_MIRKWOOD = registerBlock("torch_mirkwood",
            () -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.MIRKWOOD_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WALL_TORCH_MIRKWOOD = registerBlock("wall_torch_mirkwood",
            () -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.MIRKWOOD_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).lootFrom(TORCH_MIRKWOOD)));

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
    public static final DeferredBlock<Block> LEAFPILE_CULUMALDA = registerBlock("leafpile_culumalda",
            () -> new LeafPileBlock(BlockBehaviour.Properties.of()
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
    public static final DeferredBlock<Block> TORCH_CULUMALDA = registerBlock("torch_culumalda",
            () -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.CULUMALDA_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WALL_TORCH_CULUMALDA = registerBlock("wall_torch_culumalda",
            () -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.CULUMALDA_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).lootFrom(TORCH_CULUMALDA)));

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
    public static final DeferredBlock<Block> LEAFPILE_LEBETHRON = registerBlock("leafpile_lebethron",
            () -> new LeafPileBlock(BlockBehaviour.Properties.of()
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
    public static final DeferredBlock<Block> TORCH_LEBETHRON = registerBlock("torch_lebethron",
            () -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.LEBETHRON_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WALL_TORCH_LEBETHRON = registerBlock("wall_torch_lebethron",
            () -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.LEBETHRON_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).lootFrom(TORCH_LEBETHRON)));

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
    public static final DeferredBlock<Block> LEAFPILE_FANGORNOAK = registerBlock("leafpile_fangornoak",
            () -> new LeafPileBlock(BlockBehaviour.Properties.of()
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
    public static final DeferredBlock<Block> TORCH_FANGORNOAK = registerBlock("torch_fangornoak",
            () -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.FANGORNOAK_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WALL_TORCH_FANGORNOAK = registerBlock("wall_torch_fangornoak",
            () -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.FANGORNOAK_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).lootFrom(TORCH_FANGORNOAK)));

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
    public static final DeferredBlock<Block> TORCH_DEADWOOD = registerBlock("torch_deadwood",
            () -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.DEADWOOD_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WALL_TORCH_DEADWOOD = registerBlock("wall_torch_deadwood",
            () -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.DEADWOOD_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).lootFrom(TORCH_DEADWOOD)));

        // Flowers & Plants
    public static final DeferredBlock<Block> PIPEWEED = BLOCKS.register("pipeweed", () -> new PipeweedCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> SAPLING_MALLORN = registerBlock("sapling_mallorn",
            () -> new TolkienSaplingBlock(TolkienTreeGrowers.MALLORN, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING), Blocks.GRASS_BLOCK));
    public static final DeferredBlock<Block> SAPLING_MIRKWOOD = registerBlock("sapling_mirkwood",
            () -> new TolkienSaplingBlock(TolkienTreeGrowers.MIRKWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING), Blocks.GRASS_BLOCK));
    public static final DeferredBlock<Block> SAPLING_CULUMALDA = registerBlock("sapling_culumalda",
            () -> new TolkienSaplingBlock(TolkienTreeGrowers.CULUMALDA, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING), Blocks.GRASS_BLOCK));
    public static final DeferredBlock<Block> SAPLING_LEBETHRON = registerBlock("sapling_lebethron",
            () -> new TolkienSaplingBlock(TolkienTreeGrowers.LEBETHRON, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING), Blocks.GRASS_BLOCK));
    public static final DeferredBlock<Block> SAPLING_FANGORNOAK = registerBlock("sapling_fangornoak",
            () -> new TolkienSaplingBlock(TolkienTreeGrowers.FANGORNOAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING), Blocks.GRASS_BLOCK));
    public static final DeferredBlock<Block> SAPLING_DEADWOOD = registerBlock("sapling_deadwood",
            () -> new TolkienSaplingBlock(TolkienTreeGrowers.DEADWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING), Blocks.GRASS_BLOCK));
    public static final DeferredBlock<Block> MUSHROOM_DECAY_BLOOM = registerBlock("mushroom_decay_bloom",
            () -> new MushroomsBlock(TolkienConfiguredFeatures.MUSHROOM_DECAY_BLOOM_KEY, BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM)));
    public static final DeferredBlock<Block> MUSHROOM_BLOOM_DECAY = registerBlock("mushroom_bloom_decay",
            () -> new MushroomsBlock(TolkienConfiguredFeatures.MUSHROOM_BLOOM_DECAY_KEY, BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM)));
    public static final DeferredBlock<Block> BLOCK_DECAY_BLOOM = registerBlock("block_decay_bloom", () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.WOOD).strength(0.2F)));
    public static final DeferredBlock<Block> BLOCK_BLOOM_DECAY = registerBlock("block_bloom_decay", () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.WOOD).strength(0.2F)));
    public static final DeferredBlock<Block> FLOWER_SIMBELMYNE = registerBlock("flower_simbelmyne", () -> new TolkienFlowerBlock(MobEffects.HERO_OF_THE_VILLAGE, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_MIRKWOOD = registerBlock("flower_mirkwood", () -> new TolkienFlowerBlock(MobEffects.BAD_OMEN, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_ALFIRIN = registerBlock("flower_alfirin", () -> new TolkienFlowerBlock(MobEffects.SATURATION, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_ATHELAS = registerBlock("flower_athelas", () -> new TolkienFlowerBlock(MobEffects.REGENERATION, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_NIPHREDIL = registerBlock("flower_niphredil", () -> new TolkienFlowerBlock(MobEffects.SATURATION, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_SWAMPMILKWEED = registerBlock("flower_swamp_milkweed", () -> new TolkienFlowerBlock(MobEffects.MOVEMENT_SLOWDOWN, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_LILLYOFTHEVALLEY = registerBlock("flower_valley_lilly", () -> new TolkienFlowerBlock(MobEffects.HEAL, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_SIMBELMYNE = BLOCKS.register("potted_flower_simbelmyne", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_SIMBELMYNE, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_MIRKWOOD = BLOCKS.register("potted_flower_mirkwood", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_MIRKWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_ALFIRIN = BLOCKS.register("potted_flower_alfirin", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_ALFIRIN, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_ATHELAS = BLOCKS.register("potted_flower_athelas", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_ATHELAS, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_NIPHREDIL = BLOCKS.register("potted_flower_niphredil", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_NIPHREDIL, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_SWAMPMILKWEED = BLOCKS.register("potted_flower_swamp_milkweed", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_SWAMPMILKWEED, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_LILLYOFTHEVALLEY = BLOCKS.register("potted_flower_valley_lilly", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_LILLYOFTHEVALLEY, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_MALLORN = BLOCKS.register("potted_sapling_mallorn", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_MALLORN, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_MIRKWOOD = BLOCKS.register("potted_sapling_mirkwood", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_MIRKWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_CULUMALDA = BLOCKS.register("potted_sapling_culumalda", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_CULUMALDA, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_LEBETHRON = BLOCKS.register("potted_sapling_lebethron", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_LEBETHRON, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_FANGORNOAK = BLOCKS.register("potted_sapling_fangornoak", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_FANGORNOAK, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_DEADWOOD = BLOCKS.register("potted_sapling_deadwood", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_DEADWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_MUSHROOM_BLOOM_DECAY = BLOCKS.register("potted_mushroom_bloom_decay", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MUSHROOM_BLOOM_DECAY, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_MUSHROOM_DECAY_BLOOM = BLOCKS.register("potted_mushroom_decay_bloom", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MUSHROOM_DECAY_BLOOM, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));

        // Custom
    public static final DeferredBlock<Block> LIGHTNINGBUG_BLOCK = registerBlock("lightningbug", () -> new LightningBugBlock(BlockBehaviour.Properties.of().lightLevel((state) -> 15).instabreak().noCollission().noTerrainParticles().pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightningBugEntity>> LIGHTNINGBUG = BLOCK_ENTITIES.register("lightningbug", () ->
            BlockEntityType.Builder.of(LightningBugEntity::new, LIGHTNINGBUG_BLOCK.get()).build(null));
    public static final DeferredBlock<Block> LOCUST_BLOCK = registerBlock("locust", () -> new LocustBlock(BlockBehaviour.Properties.of().lightLevel((state) -> 15).instabreak().noCollission().noTerrainParticles().pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LocustEntity>> LOCUST = BLOCK_ENTITIES.register("locust", () ->
            BlockEntityType.Builder.of(LocustEntity::new, LOCUST_BLOCK.get()).build(null));

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }
}