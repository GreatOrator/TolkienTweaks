package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.block.*;
import com.greatorator.tolkienmobs.block.custom.*;
import com.greatorator.tolkienmobs.block.custom.entity.*;
import com.greatorator.tolkienmobs.datagen.world.TolkienWorldConfigurationProvider;
import com.greatorator.tolkienmobs.handler.interfaces.TolkienRegistry;
import com.greatorator.tolkienmobs.world.components.feature.tree.TolkienTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
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
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.block.custom.FancyLanternBlock.LIT;
import static com.greatorator.tolkienmobs.init.TolkienItems.ITEMS;

public class TolkienBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<TolkienWoodTypes> WOOD_TYPES = DeferredRegister.create(TolkienRegistry.WOOD_TYPE_REGISTRY, MODID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);

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
    public static final DeferredBlock<IronBarsBlock> MITHRIL_BARS = registerBlock("mithril_bars", () ->
            new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)));
    public static final DeferredBlock<Block> WALL_MITHRIL = registerBlock("wall_mithril",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DOOR_MITHRIL = registerBlock("door_mithril",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> TRAPDOOR_MITHRIL = registerBlock("trapdoor_mithril",
            () -> new TrapDoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> BARREL_MITHRIL = registerBlock("barrel_mithril",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_WHITE)));

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
    public static final DeferredBlock<IronBarsBlock> MORGULIRON_BARS = registerBlock("morguliron_bars", () ->
            new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)));
    public static final DeferredBlock<Block> WALL_MORGULIRON = registerBlock("wall_morguliron",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DOOR_MORGULIRON = registerBlock("door_morguliron",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> TRAPDOOR_MORGULIRON = registerBlock("trapdoor_morguliron",
            () -> new TrapDoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> BARREL_MORGULIRON = registerBlock("barrel_morguliron",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_BLACK)));

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
    public static final DeferredBlock<IronBarsBlock> PANE_AMMOLITE = registerBlock("pane_ammolite", () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)));
    public static final DeferredBlock<Block> DOOR_DURIN = registerBlock("door_durin",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));

        // Darkstone
    public static final DeferredBlock<Block> DARK_STONE = registerBlock("dark_stone",
                () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_DARK_STONE = registerBlock("stairs_dark_stone",
            () -> new StairBlock(TolkienBlocks.DARK_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_DARK_STONE = registerBlock("slab_dark_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DARK_STONE = registerBlock("pressure_plate_dark_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DARK_STONE_BUTTON = registerBlock("dark_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_DARK_STONE = registerBlock("wall_dark_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHISELED_DARK_STONE_BRICKS = registerBlock("chiseled_dark_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_CHISELED_DARK_STONE_BRICKS = registerBlock("stairs_chiseled_dark_stone_bricks",
            () -> new StairBlock(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_CHISELED_DARK_STONE_BRICKS = registerBlock("slab_chiseled_dark_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_DARK_STONE_BRICKS = registerBlock("pressure_plate_chiseled_dark_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHISELED_DARK_STONE_BRICKS_BUTTON = registerBlock("chiseled_dark_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_CHISELED_DARK_STONE_BRICKS = registerBlock("wall_chiseled_dark_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COBBLED_DARK_STONE = registerBlock("cobbled_dark_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_COBBLED_DARK_STONE = registerBlock("stairs_cobbled_dark_stone",
            () -> new StairBlock(TolkienBlocks.COBBLED_DARK_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_COBBLED_DARK_STONE = registerBlock("slab_cobbled_dark_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_DARK_STONE = registerBlock("pressure_plate_cobbled_dark_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COBBLED_DARK_STONE_BUTTON = registerBlock("cobbled_dark_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_COBBLED_DARK_STONE = registerBlock("wall_cobbled_dark_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRACKED_DARK_STONE_BRICKS = registerBlock("cracked_dark_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_CRACKED_DARK_STONE_BRICKS = registerBlock("stairs_cracked_dark_stone_bricks",
            () -> new StairBlock(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_CRACKED_DARK_STONE_BRICKS = registerBlock("slab_cracked_dark_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_DARK_STONE_BRICKS = registerBlock("pressure_plate_cracked_dark_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRACKED_DARK_STONE_BRICKS_BUTTON = registerBlock("cracked_dark_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_CRACKED_DARK_STONE_BRICKS = registerBlock("wall_cracked_dark_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DARK_STONE_BRICKS = registerBlock("dark_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_DARK_STONE_BRICKS = registerBlock("stairs_dark_stone_bricks",
            () -> new StairBlock(TolkienBlocks.DARK_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_DARK_STONE_BRICKS = registerBlock("slab_dark_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DARK_STONE_BRICKS = registerBlock("pressure_plate_dark_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DARK_STONE_BRICKS_BUTTON = registerBlock("dark_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_DARK_STONE_BRICKS = registerBlock("wall_dark_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SMOOTH_DARK_STONE = registerBlock("smooth_dark_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_DARK_STONE = registerBlock("stairs_smooth_dark_stone",
            () -> new StairBlock(TolkienBlocks.SMOOTH_DARK_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_SMOOTH_DARK_STONE = registerBlock("slab_smooth_dark_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_DARK_STONE = registerBlock("pressure_plate_smooth_dark_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SMOOTH_DARK_STONE_BUTTON = registerBlock("smooth_dark_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_SMOOTH_DARK_STONE = registerBlock("wall_smooth_dark_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));

        // Dwarven Stone
    public static final DeferredBlock<Block> DWARVEN_STONE = registerBlock("dwarven_stone",
                () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_DWARVEN_STONE = registerBlock("stairs_dwarven_stone",
            () -> new StairBlock(TolkienBlocks.DWARVEN_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_DWARVEN_STONE = registerBlock("slab_dwarven_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DWARVEN_STONE = registerBlock("pressure_plate_dwarven_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DWARVEN_STONE_BUTTON = registerBlock("dwarven_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_DWARVEN_STONE = registerBlock("wall_dwarven_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHISELED_DWARVEN_STONE_BRICKS = registerBlock("chiseled_dwarven_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_CHISELED_DWARVEN_STONE_BRICKS = registerBlock("stairs_chiseled_dwarven_stone_bricks",
            () -> new StairBlock(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_CHISELED_DWARVEN_STONE_BRICKS = registerBlock("slab_chiseled_dwarven_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_DWARVEN_STONE_BRICKS = registerBlock("pressure_plate_chiseled_dwarven_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHISELED_DWARVEN_STONE_BRICKS_BUTTON = registerBlock("chiseled_dwarven_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_CHISELED_DWARVEN_STONE_BRICKS = registerBlock("wall_chiseled_dwarven_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COBBLED_DWARVEN_STONE = registerBlock("cobbled_dwarven_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_COBBLED_DWARVEN_STONE = registerBlock("stairs_cobbled_dwarven_stone",
            () -> new StairBlock(TolkienBlocks.COBBLED_DWARVEN_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_COBBLED_DWARVEN_STONE = registerBlock("slab_cobbled_dwarven_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_DWARVEN_STONE = registerBlock("pressure_plate_cobbled_dwarven_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COBBLED_DWARVEN_STONE_BUTTON = registerBlock("cobbled_dwarven_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_COBBLED_DWARVEN_STONE = registerBlock("wall_cobbled_dwarven_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRACKED_DWARVEN_STONE_BRICKS = registerBlock("cracked_dwarven_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_CRACKED_DWARVEN_STONE_BRICKS = registerBlock("stairs_cracked_dwarven_stone_bricks",
            () -> new StairBlock(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_CRACKED_DWARVEN_STONE_BRICKS = registerBlock("slab_cracked_dwarven_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_DWARVEN_STONE_BRICKS = registerBlock("pressure_plate_cracked_dwarven_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRACKED_DWARVEN_STONE_BRICKS_BUTTON = registerBlock("cracked_dwarven_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_CRACKED_DWARVEN_STONE_BRICKS = registerBlock("wall_cracked_dwarven_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DWARVEN_STONE_BRICKS = registerBlock("dwarven_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_DWARVEN_STONE_BRICKS = registerBlock("stairs_dwarven_stone_bricks",
            () -> new StairBlock(TolkienBlocks.DWARVEN_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_DWARVEN_STONE_BRICKS = registerBlock("slab_dwarven_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DWARVEN_STONE_BRICKS = registerBlock("pressure_plate_dwarven_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DWARVEN_STONE_BRICKS_BUTTON = registerBlock("dwarven_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_DWARVEN_STONE_BRICKS = registerBlock("wall_dwarven_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SMOOTH_DWARVEN_STONE = registerBlock("smooth_dwarven_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_DWARVEN_STONE = registerBlock("stairs_smooth_dwarven_stone",
            () -> new StairBlock(TolkienBlocks.SMOOTH_DWARVEN_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_SMOOTH_DWARVEN_STONE = registerBlock("slab_smooth_dwarven_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_DWARVEN_STONE = registerBlock("pressure_plate_smooth_dwarven_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SMOOTH_DWARVEN_STONE_BUTTON = registerBlock("smooth_dwarven_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_SMOOTH_DWARVEN_STONE = registerBlock("wall_smooth_dwarven_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));

        // Elven Marble
    public static final DeferredBlock<Block> ELVEN_MARBLE = registerBlock("elven_marble",
                () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_ELVEN_MARBLE = registerBlock("stairs_elven_marble",
            () -> new StairBlock(TolkienBlocks.ELVEN_MARBLE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_ELVEN_MARBLE = registerBlock("slab_elven_marble",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_ELVEN_MARBLE = registerBlock("pressure_plate_elven_marble",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ELVEN_MARBLE_BUTTON = registerBlock("elven_marble_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_ELVEN_MARBLE = registerBlock("wall_elven_marble",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("chiseled_elven_marble_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("stairs_chiseled_elven_marble_bricks",
            () -> new StairBlock(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("slab_chiseled_elven_marble_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("pressure_plate_chiseled_elven_marble_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHISELED_ELVEN_MARBLE_BRICKS_BUTTON = registerBlock("chiseled_elven_marble_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("wall_chiseled_elven_marble_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COBBLED_ELVEN_MARBLE = registerBlock("cobbled_elven_marble",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_COBBLED_ELVEN_MARBLE = registerBlock("stairs_cobbled_elven_marble",
            () -> new StairBlock(TolkienBlocks.COBBLED_ELVEN_MARBLE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_COBBLED_ELVEN_MARBLE = registerBlock("slab_cobbled_elven_marble",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_ELVEN_MARBLE = registerBlock("pressure_plate_cobbled_elven_marble",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COBBLED_ELVEN_MARBLE_BUTTON = registerBlock("cobbled_elven_marble_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_COBBLED_ELVEN_MARBLE = registerBlock("wall_cobbled_elven_marble",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("cracked_elven_marble_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("stairs_cracked_elven_marble_bricks",
            () -> new StairBlock(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("slab_cracked_elven_marble_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("pressure_plate_cracked_elven_marble_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRACKED_ELVEN_MARBLE_BRICKS_BUTTON = registerBlock("cracked_elven_marble_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("wall_cracked_elven_marble_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ELVEN_MARBLE_BRICKS = registerBlock("elven_marble_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_ELVEN_MARBLE_BRICKS = registerBlock("stairs_elven_marble_bricks",
            () -> new StairBlock(TolkienBlocks.ELVEN_MARBLE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_ELVEN_MARBLE_BRICKS = registerBlock("slab_elven_marble_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_ELVEN_MARBLE_BRICKS = registerBlock("pressure_plate_elven_marble_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ELVEN_MARBLE_BRICKS_BUTTON = registerBlock("elven_marble_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_ELVEN_MARBLE_BRICKS = registerBlock("wall_elven_marble_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SMOOTH_ELVEN_MARBLE = registerBlock("smooth_elven_marble",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_ELVEN_MARBLE = registerBlock("stairs_smooth_elven_marble",
            () -> new StairBlock(TolkienBlocks.SMOOTH_ELVEN_MARBLE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_SMOOTH_ELVEN_MARBLE = registerBlock("slab_smooth_elven_marble",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_ELVEN_MARBLE = registerBlock("pressure_plate_smooth_elven_marble",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SMOOTH_ELVEN_MARBLE_BUTTON = registerBlock("smooth_elven_marble_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_SMOOTH_ELVEN_MARBLE = registerBlock("wall_smooth_elven_marble",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));

        // Mountain Stone
    public static final DeferredBlock<Block> MOUNTAIN_STONE = registerBlock("mountain_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_MOUNTAIN_STONE = registerBlock("stairs_mountain_stone",
            () -> new StairBlock(TolkienBlocks.MOUNTAIN_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_MOUNTAIN_STONE = registerBlock("slab_mountain_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOUNTAIN_STONE = registerBlock("pressure_plate_mountain_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MOUNTAIN_STONE_BUTTON = registerBlock("mountain_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_MOUNTAIN_STONE = registerBlock("wall_mountain_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("chiseled_mountain_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("stairs_chiseled_mountain_stone_bricks",
            () -> new StairBlock(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("slab_chiseled_mountain_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("pressure_plate_chiseled_mountain_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CHISELED_MOUNTAIN_STONE_BRICKS_BUTTON = registerBlock("chiseled_mountain_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("wall_chiseled_mountain_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COBBLED_MOUNTAIN_STONE = registerBlock("cobbled_mountain_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_COBBLED_MOUNTAIN_STONE = registerBlock("stairs_cobbled_mountain_stone",
            () -> new StairBlock(TolkienBlocks.COBBLED_MOUNTAIN_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_COBBLED_MOUNTAIN_STONE = registerBlock("slab_cobbled_mountain_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_MOUNTAIN_STONE = registerBlock("pressure_plate_cobbled_mountain_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> COBBLED_MOUNTAIN_STONE_BUTTON = registerBlock("cobbled_mountain_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_COBBLED_MOUNTAIN_STONE = registerBlock("wall_cobbled_mountain_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("cracked_mountain_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("stairs_cracked_mountain_stone_bricks",
            () -> new StairBlock(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("slab_cracked_mountain_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("pressure_plate_cracked_mountain_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CRACKED_MOUNTAIN_STONE_BRICKS_BUTTON = registerBlock("cracked_mountain_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("wall_cracked_mountain_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MOUNTAIN_STONE_BRICKS = registerBlock("mountain_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_MOUNTAIN_STONE_BRICKS = registerBlock("stairs_mountain_stone_bricks",
            () -> new StairBlock(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_MOUNTAIN_STONE_BRICKS = registerBlock("slab_mountain_stone_bricks",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOUNTAIN_STONE_BRICKS = registerBlock("pressure_plate_mountain_stone_bricks",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MOUNTAIN_STONE_BRICKS_BUTTON = registerBlock("mountain_stone_bricks_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_MOUNTAIN_STONE_BRICKS = registerBlock("wall_mountain_stone_bricks",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SMOOTH_MOUNTAIN_STONE = registerBlock("smooth_mountain_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_MOUNTAIN_STONE = registerBlock("stairs_smooth_mountain_stone",
            () -> new StairBlock(TolkienBlocks.SMOOTH_MOUNTAIN_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SLAB_SMOOTH_MOUNTAIN_STONE = registerBlock("slab_smooth_mountain_stone",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_MOUNTAIN_STONE = registerBlock("pressure_plate_smooth_mountain_stone",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of()
                    .noCollission().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SMOOTH_MOUNTAIN_STONE_BUTTON = registerBlock("smooth_mountain_stone_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> WALL_SMOOTH_MOUNTAIN_STONE = registerBlock("wall_smooth_mountain_stone",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops()));

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
    public static final DeferredBlock<Block> BARREL_MALLORN = registerBlock("barrel_mallorn",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MALLORN_SIGN = registerBlock("sign_mallorn", () -> new TolkienSignBlock(TolkienWoodTypes.MALLORN, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava()));
    public static final DeferredBlock<Block> MALLORN_WALL_SIGN = registerNoItem("sign_wall_mallorn", () -> new TolkienWallSignBlock(TolkienWoodTypes.MALLORN, BlockBehaviour.Properties.ofFullCopy(MALLORN_SIGN.get())));
    public static final DeferredBlock<Block> MALLORN_HANGING_SIGN = registerBlock("hanging_sign_mallorn", () -> new TolkienHangingSignBlock(TolkienWoodTypes.MALLORN, BlockBehaviour.Properties.ofFullCopy(MALLORN_SIGN.get())));
    public static final DeferredBlock<Block> MALLORN_HANGING_WALL_SIGN = registerNoItem("hanging_sign_wall_mallorn", () -> new TolkienHangingWallSignBlock(TolkienWoodTypes.MALLORN, BlockBehaviour.Properties.ofFullCopy(MALLORN_HANGING_SIGN.get())));
    public static final DeferredBlock<Block> LADDER_MALLORN = registerBlock("ladder_mallorn", () -> new LadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));

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
    public static final DeferredBlock<Block> BARREL_MIRKWOOD = registerBlock("barrel_mirkwood",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_BLACK)));
    public static final DeferredBlock<Block> MIRKWOOD_SIGN = registerBlock("sign_mirkwood", () -> new TolkienSignBlock(TolkienWoodTypes.MIRKWOOD, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava()));
    public static final DeferredBlock<Block> MIRKWOOD_WALL_SIGN = registerNoItem("sign_wall_mirkwood", () -> new TolkienWallSignBlock(TolkienWoodTypes.MIRKWOOD, BlockBehaviour.Properties.ofFullCopy(MIRKWOOD_SIGN.get())));
    public static final DeferredBlock<Block> MIRKWOOD_HANGING_SIGN = registerBlock("hanging_sign_mirkwood", () -> new TolkienHangingSignBlock(TolkienWoodTypes.MIRKWOOD, BlockBehaviour.Properties.ofFullCopy(MIRKWOOD_SIGN.get())));
    public static final DeferredBlock<Block> MIRKWOOD_HANGING_WALL_SIGN = registerNoItem("hanging_sign_wall_mirkwood", () -> new TolkienHangingWallSignBlock(TolkienWoodTypes.MIRKWOOD, BlockBehaviour.Properties.ofFullCopy(MIRKWOOD_HANGING_SIGN.get())));
    public static final DeferredBlock<Block> LADDER_MIRKWOOD = registerBlock("ladder_mirkwood", () -> new LadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));

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
    public static final DeferredBlock<Block> BARREL_CULUMALDA = registerBlock("barrel_culumalda",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> CULUMALDA_SIGN = registerBlock("sign_culumalda", () -> new TolkienSignBlock(TolkienWoodTypes.CULUMALDA, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava()));
    public static final DeferredBlock<Block> CULUMALDA_WALL_SIGN = registerNoItem("sign_wall_culumalda", () -> new TolkienWallSignBlock(TolkienWoodTypes.CULUMALDA, BlockBehaviour.Properties.ofFullCopy(CULUMALDA_SIGN.get())));
    public static final DeferredBlock<Block> CULUMALDA_HANGING_SIGN = registerBlock("hanging_sign_culumalda", () -> new TolkienHangingSignBlock(TolkienWoodTypes.CULUMALDA, BlockBehaviour.Properties.ofFullCopy(CULUMALDA_SIGN.get())));
    public static final DeferredBlock<Block> CULUMALDA_HANGING_WALL_SIGN = registerNoItem("hanging_sign_wall_culumalda", () -> new TolkienHangingWallSignBlock(TolkienWoodTypes.CULUMALDA, BlockBehaviour.Properties.ofFullCopy(CULUMALDA_HANGING_SIGN.get())));
    public static final DeferredBlock<Block> LADDER_CULUMALDA = registerBlock("ladder_culumalda", () -> new LadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));

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
    public static final DeferredBlock<Block> BARREL_LEBETHRON = registerBlock("barrel_lebethron",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));
    public static final DeferredBlock<Block> LEBETHRON_SIGN = registerBlock("sign_lebethron", () -> new TolkienSignBlock(TolkienWoodTypes.LEBETHRON, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava()));
    public static final DeferredBlock<Block> LEBETHRON_WALL_SIGN = registerNoItem("sign_wall_lebethron", () -> new TolkienWallSignBlock(TolkienWoodTypes.LEBETHRON, BlockBehaviour.Properties.ofFullCopy(LEBETHRON_SIGN.get())));
    public static final DeferredBlock<Block> LEBETHRON_HANGING_SIGN = registerBlock("hanging_sign_lebethron", () -> new TolkienHangingSignBlock(TolkienWoodTypes.LEBETHRON, BlockBehaviour.Properties.ofFullCopy(LEBETHRON_SIGN.get())));
    public static final DeferredBlock<Block> LEBETHRON_HANGING_WALL_SIGN = registerNoItem("hanging_sign_wall_lebethron", () -> new TolkienHangingWallSignBlock(TolkienWoodTypes.LEBETHRON, BlockBehaviour.Properties.ofFullCopy(LEBETHRON_HANGING_SIGN.get())));
    public static final DeferredBlock<Block> LADDER_LEBETHRON = registerBlock("ladder_lebethron", () -> new LadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));

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
    public static final DeferredBlock<Block> BARREL_FANGORNOAK = registerBlock("barrel_fangornoak",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)));
    public static final DeferredBlock<Block> FANGORNOAK_SIGN = registerBlock("sign_fangornoak", () -> new TolkienSignBlock(TolkienWoodTypes.FANGORNOAK, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava()));
    public static final DeferredBlock<Block> FANGORNOAK_WALL_SIGN = registerNoItem("sign_wall_fangornoak", () -> new TolkienWallSignBlock(TolkienWoodTypes.FANGORNOAK, BlockBehaviour.Properties.ofFullCopy(FANGORNOAK_SIGN.get())));
    public static final DeferredBlock<Block> FANGORNOAK_HANGING_SIGN = registerBlock("hanging_sign_fangornoak", () -> new TolkienHangingSignBlock(TolkienWoodTypes.FANGORNOAK, BlockBehaviour.Properties.ofFullCopy(FANGORNOAK_SIGN.get())));
    public static final DeferredBlock<Block> FANGORNOAK_HANGING_WALL_SIGN = registerNoItem("hanging_sign_wall_fangornoak", () -> new TolkienHangingWallSignBlock(TolkienWoodTypes.FANGORNOAK, BlockBehaviour.Properties.ofFullCopy(FANGORNOAK_HANGING_SIGN.get())));
    public static final DeferredBlock<Block> LADDER_FANGORNOAK = registerBlock("ladder_fangornoak", () -> new LadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));

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
    public static final DeferredBlock<Block> BARREL_DEADWOOD = registerBlock("barrel_deadwood",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> DEADWOOD_SIGN = registerBlock("sign_deadwood", () -> new TolkienSignBlock(TolkienWoodTypes.DEADWOOD, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava()));
    public static final DeferredBlock<Block> DEADWOOD_WALL_SIGN = registerNoItem("sign_wall_deadwood", () -> new TolkienWallSignBlock(TolkienWoodTypes.DEADWOOD, BlockBehaviour.Properties.ofFullCopy(DEADWOOD_SIGN.get())));
    public static final DeferredBlock<Block> DEADWOOD_HANGING_SIGN = registerBlock("hanging_sign_deadwood", () -> new TolkienHangingSignBlock(TolkienWoodTypes.DEADWOOD, BlockBehaviour.Properties.ofFullCopy(DEADWOOD_SIGN.get())));
    public static final DeferredBlock<Block> DEADWOOD_HANGING_WALL_SIGN = registerNoItem("hanging_sign_wall_deadwood", () -> new TolkienHangingWallSignBlock(TolkienWoodTypes.DEADWOOD, BlockBehaviour.Properties.ofFullCopy(DEADWOOD_HANGING_SIGN.get())));
    public static final DeferredBlock<Block> LADDER_DEADWOOD = registerBlock("ladder_deadwood", () -> new LadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));

        // Dwarven Maple
    public static final DeferredBlock<Block> LOG_DWARVEN_MAPLE = registerBlock("log_dwarven_maple",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> WOOD_DWARVEN_MAPLE = registerBlock("wood_dwarven_maple",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_DWARVEN_MAPLE_LOG = registerBlock("stripped_log_dwarven_maple",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STRIPPED_DWARVEN_MAPLE_WOOD = registerBlock("stripped_wood_dwarven_maple",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_DWARVEN_MAPLE = registerBlock("leaves_dwarven_maple",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> LEAFPILE_DWARVEN_MAPLE = registerBlock("leafpile_dwarven_maple",
            () -> new LeafPileBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> PLANKS_DWARVEN_MAPLE = registerBlock("planks_dwarven_maple",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> STAIRS_DWARVEN_MAPLE = registerBlock("stairs_dwarven_maple",
            () -> new StairBlock(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> SLAB_DWARVEN_MAPLE = registerBlock("slab_dwarven_maple",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DWARVEN_MAPLE = registerBlock("pressure_plate_dwarven_maple",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava()));
    public static final DeferredBlock<Block> DWARVEN_MAPLE_BUTTON = registerBlock("dwarven_maple_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<Block> FENCE_DWARVEN_MAPLE = registerBlock("fence_dwarven_maple",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> FENCE_GATE_DWARVEN_MAPLE = registerBlock("fence_gate_dwarven_maple",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> DOOR_DWARVEN_MAPLE = registerBlock("door_dwarven_maple",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> TRAPDOOR_DWARVEN_MAPLE = registerBlock("trapdoor_dwarven_maple",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> TORCH_DWARVEN_MAPLE = registerBlock("torch_dwarven_maple",
            () -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.DWARVEN_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> WALL_TORCH_DWARVEN_MAPLE = registerBlock("wall_torch_dwarven_maple",
            () -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.DWARVEN_FLAME.get(),
                    BlockBehaviour.Properties.of()
                            .noCollission().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).lootFrom(TORCH_DWARVEN_MAPLE)));
    public static final DeferredBlock<Block> BARREL_DWARVEN_MAPLE = registerBlock("barrel_dwarven_maple",
            () -> new BarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> DWARVEN_MAPLE_SIGN = registerBlock("sign_dwarven_maple", () -> new TolkienSignBlock(TolkienWoodTypes.DWARVEN_MAPLE, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava()));
    public static final DeferredBlock<Block> DWARVEN_MAPLE_WALL_SIGN = registerNoItem("sign_wall_dwarven_maple", () -> new TolkienWallSignBlock(TolkienWoodTypes.DWARVEN_MAPLE, BlockBehaviour.Properties.ofFullCopy(DWARVEN_MAPLE_SIGN.get())));
    public static final DeferredBlock<Block> DWARVEN_MAPLE_HANGING_SIGN = registerBlock("hanging_sign_dwarven_maple", () -> new TolkienHangingSignBlock(TolkienWoodTypes.DWARVEN_MAPLE, BlockBehaviour.Properties.ofFullCopy(DWARVEN_MAPLE_SIGN.get())));
    public static final DeferredBlock<Block> DWARVEN_MAPLE_HANGING_WALL_SIGN = registerNoItem("hanging_sign_wall_dwarven_maple", () -> new TolkienHangingWallSignBlock(TolkienWoodTypes.DWARVEN_MAPLE, BlockBehaviour.Properties.ofFullCopy(DWARVEN_MAPLE_HANGING_SIGN.get())));
    public static final DeferredBlock<Block> LADDER_DWARVEN_MAPLE = registerBlock("ladder_dwarven_maple", () -> new LadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));

    // Flowers & Plants
    public static final DeferredBlock<Block> PIPEWEED = BLOCKS.register("pipeweed", () -> new PipeweedCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> MOSS_PATCH = registerBlock("moss_patch", () -> new MossPatchBlock(BlockBehaviour.Properties.of().ignitedByLava().instabreak().mapColor(MapColor.PLANT).noCollission().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SoundType.MOSS)));
    public static final DeferredBlock<Block> CLOVER_PATCH = registerBlock("clover_patch", () -> new TolkienPatchBlock(BlockBehaviour.Properties.of().ignitedByLava().noCollission().noOcclusion().instabreak().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
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
    public static final DeferredBlock<Block> SAPLING_DWARVEN_MAPLE = registerBlock("sapling_dwarven_maple",
            () -> new TolkienSaplingBlock(TolkienTreeGrowers.DWARVEN_MAPLE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING), Blocks.GRASS_BLOCK));
    public static final DeferredBlock<Block> MUSHROOM_DECAY_BLOOM = registerBlock("mushroom_decay_bloom",
            () -> new MushroomsBlock(TolkienWorldConfigurationProvider.MUSHROOM_DECAY_BLOOM_KEY, BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM)));
    public static final DeferredBlock<Block> MUSHROOM_BLOOM_DECAY = registerBlock("mushroom_bloom_decay",
            () -> new MushroomsBlock(TolkienWorldConfigurationProvider.MUSHROOM_BLOOM_DECAY_KEY, BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM)));
    public static final DeferredBlock<Block> BLOCK_DECAY_BLOOM = registerBlock("block_decay_bloom", () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.WOOD).strength(0.2F)));
    public static final DeferredBlock<Block> BLOCK_BLOOM_DECAY = registerBlock("block_bloom_decay", () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.WOOD).strength(0.2F)));
    public static final DeferredBlock<Block> LIVING_ROOTS = registerBlock("living_roots", () -> new Block(BlockBehaviour.Properties.of().noCollission().ignitedByLava().instabreak().sound(SoundType.GRASS).strength(2.0F, 3.0F)));
    public static final DeferredBlock<Block> FLOWER_SIMBELMYNE = registerBlock("flower_simbelmyne", () -> new TolkienFlowerBlock(MobEffects.HERO_OF_THE_VILLAGE, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_MIRKWOOD = registerBlock("flower_mirkwood", () -> new TolkienFlowerBlock(MobEffects.BAD_OMEN, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_ALFIRIN = registerBlock("flower_alfirin", () -> new TolkienFlowerBlock(MobEffects.SATURATION, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_ATHELAS = registerBlock("flower_athelas", () -> new TolkienFlowerBlock(MobEffects.REGENERATION, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_NIPHREDIL = registerBlock("flower_niphredil", () -> new TolkienFlowerBlock(MobEffects.SATURATION, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_SWAMPMILKWEED = registerBlock("flower_swamp_milkweed", () -> new TolkienFlowerBlock(MobEffects.MOVEMENT_SLOWDOWN, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_LILLYOFTHEVALLEY = registerBlock("flower_valley_lilly", () -> new TolkienFlowerBlock(MobEffects.HEAL, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_ELANOR = registerBlock("flower_elanor", () -> new TolkienFlowerBlock(MobEffects.MOVEMENT_SPEED, 7, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_AEGLOS = registerBlock("flower_aeglos", () -> new TolkienFlowerBlock(MobEffects.BAD_OMEN, 7,BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_LISSUIN = registerBlock("flower_lissuin", () -> new TolkienFlowerBlock(MobEffects.DAMAGE_RESISTANCE, 7,BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_MALLOS = registerBlock("flower_mallos", () -> new TolkienFlowerBlock(MobEffects.DIG_SPEED, 7,BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWER_BRAMBLES = registerBlock("flower_brambles", () -> new BramblesBushBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_SIMBELMYNE = BLOCKS.register("potted_flower_simbelmyne", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_SIMBELMYNE, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_MIRKWOOD = BLOCKS.register("potted_flower_mirkwood", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_MIRKWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_ALFIRIN = BLOCKS.register("potted_flower_alfirin", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_ALFIRIN, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_ATHELAS = BLOCKS.register("potted_flower_athelas", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_ATHELAS, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_NIPHREDIL = BLOCKS.register("potted_flower_niphredil", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_NIPHREDIL, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_SWAMPMILKWEED = BLOCKS.register("potted_flower_swamp_milkweed", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_SWAMPMILKWEED, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_LILLYOFTHEVALLEY = BLOCKS.register("potted_flower_valley_lilly", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_LILLYOFTHEVALLEY, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_ELANOR = BLOCKS.register("potted_flower_elanor", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_ELANOR, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_AEGLOS = BLOCKS.register("potted_flower_aeglos", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_AEGLOS, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_LISSUIN = BLOCKS.register("potted_flower_lissuin", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_LISSUIN, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_MALLOS = BLOCKS.register("potted_flower_mallos", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_MALLOS, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_BRAMBLES = BLOCKS.register("potted_flower_brambles", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_BRAMBLES, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_MALLORN = BLOCKS.register("potted_sapling_mallorn", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_MALLORN, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_MIRKWOOD = BLOCKS.register("potted_sapling_mirkwood", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_MIRKWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_CULUMALDA = BLOCKS.register("potted_sapling_culumalda", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_CULUMALDA, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_LEBETHRON = BLOCKS.register("potted_sapling_lebethron", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_LEBETHRON, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_FANGORNOAK = BLOCKS.register("potted_sapling_fangornoak", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_FANGORNOAK, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_DEADWOOD = BLOCKS.register("potted_sapling_deadwood", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_DEADWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_DWARVEN_MAPLE = BLOCKS.register("potted_sapling_dwarven_maple", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_DWARVEN_MAPLE, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_MUSHROOM_BLOOM_DECAY = BLOCKS.register("potted_mushroom_bloom_decay", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MUSHROOM_BLOOM_DECAY, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_MUSHROOM_DECAY_BLOOM = BLOCKS.register("potted_mushroom_decay_bloom", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MUSHROOM_DECAY_BLOOM, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT)));

        // Custom
    public static final DeferredBlock<Block> LIGHTNINGBUG_BLOCK = registerBlock("lightningbug", () -> new LightningBugBlock(BlockBehaviour.Properties.of().lightLevel((state) -> 15).instabreak().noCollission().noTerrainParticles().pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<Block> LOCUST_BLOCK = registerBlock("locust", () -> new LocustBlock(BlockBehaviour.Properties.of().lightLevel((state) -> 15).instabreak().noCollission().noTerrainParticles().pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<Block> SILMARIL_LANTERN = registerBlock("silmaril_lantern",
            () -> new FancyLanternBlock(BlockBehaviour.Properties.of().strength(4.5F, 5.5F).requiresCorrectToolForDrops().noOcclusion().lightLevel((state) -> state.getValue(LIT) ? 15:0).sound(SoundType.LANTERN)));
    public static final DeferredBlock<Block> ELVEN_LANTERN = registerBlock("elven_lantern",
            () -> new FancyLanternBlock(BlockBehaviour.Properties.of().strength(3.5F, 3.5F).requiresCorrectToolForDrops().noOcclusion().lightLevel((state) -> state.getValue(LIT) ? 13:0).sound(SoundType.LANTERN)));
    public static final DeferredBlock<Block> MORGUL_LANTERN = registerBlock("morgul_lantern",
            () -> new FancyLanternBlock(BlockBehaviour.Properties.of().strength(3.5F, 3.5F).requiresCorrectToolForDrops().noOcclusion().lightLevel((state) -> state.getValue(LIT) ? 11:0).sound(SoundType.LANTERN)));
    public static final DeferredBlock<Block> BLOCK_HALLOWED = registerBlock("block_hallowed", () -> new HallowedBlock(BlockBehaviour.Properties.of().randomTicks().sound(SoundType.GRAVEL)));
    public static final DeferredBlock<Block> STONE_PATH = registerBlock("block_stone_path", () -> new StonePathBlock(BlockBehaviour.Properties.of().isViewBlocking(TolkienBlocks::never).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ROCKPILE = registerBlock("rockpile", () -> new RockPileBlock(BlockBehaviour.Properties.of().offsetType(BlockBehaviour.OffsetType.XZ).noCollission().instabreak().sound(SoundType.STONE).strength(0.2F).dynamicShape()));
    public static final DeferredBlock<Block> WALL_DECAY_BLOOM = registerBlock("wall_decay_bloom", () -> new WallDecayBloomBlock(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.GRASS).strength(0.2F).dynamicShape()));
    public static final DeferredBlock<Block> WALL_MUSHROOM_RED = registerBlock("wall_mushroom_red", () -> new WallMushroomRedBlock(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.GRASS).strength(0.2F).dynamicShape()));
    public static final DeferredBlock<Block> WALL_MUSHROOM_BROWN = registerBlock("wall_mushroom_brown", () -> new WallMushroomBrownBlock(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.GRASS).strength(0.2F).dynamicShape()));
    public static final DeferredBlock<Block> TRINKET_TABLE = registerBlock("trinket_table", () -> new TrinketTableBlock(BlockBehaviour.Properties.of().strength(3.5F, 3.5F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> FIREPLACE = registerBlock("fireplace", () -> new FireplaceBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE).strength(5f, 6f).lightLevel((state) -> state.getValue(LIT) ? 15:0)));
    public static final DeferredBlock<Block> PIGGYBANK = registerBlock("block_piggybank", () -> new PiggyBankBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(5f, 6f)));
    public static final DeferredBlock<Block> BACKPACK = registerBlock("backpack", () -> new BackpackBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f)));
    public static final DeferredBlock<Block> CHAMELEON_BLOCK = registerBlock("chameleon_block", () -> new ChameleonBlock(BlockBehaviour.Properties.of().noCollission().noOcclusion()));
    public static final DeferredBlock<Block> KEY_STONE_BLOCK = registerBlock("block_key_stone", () -> new CamoKeyStoneBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> CAMO_GLOWSTONE_BLOCK = registerBlock("block_camo_glowstone", () -> new CamoGlowstoneBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().lightLevel((state) -> 15)));
    public static final DeferredBlock<Block> CAMO_SMOKER_BLOCK = registerBlock("block_camo_smoker", () -> new CamoSmokerBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> CAMO_FLUID_BLOCK = registerBlock("block_camo_fluid", () -> new CamoFluidBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> CAMO_CHEST_BLOCK = registerBlock("block_camo_chest", () -> new CamoChestBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> CAMO_SPAWNER_BLOCK = registerBlock("block_camo_spawner", () -> new CamoSpawnerBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> MILESTONE_BLOCK = registerBlock("milestone_block", () -> new MilestoneBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion().lightLevel((state) -> 4)));
    public static final DeferredBlock<Block> LOCKABLE_CHEST_BLOCK = registerBlock("lockable_chest_block", () -> new LockableChestBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> LOCKABLE_TREASURE_CHEST_BLOCK = registerBlock("lockable_treasure_chest_block", () -> new LockableTreasureChestBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> LOCKABLE_DOUBLE_CHEST_BLOCK = registerBlock("lockable_double_chest_block", () -> new LockableDoubleChestBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> LOCKABLE_DOUBLE_TREASURE_CHEST_BLOCK = registerBlock("lockable_double_treasure_chest_block", () -> new LockableDoubleTreasureChestBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> WELL = registerBlock("well_block", () -> new WellBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().strength(3.5F, 3.5F).noOcclusion()));
    public static final DeferredBlock<Block> ARDA_PORTAL = registerBlockWithoutBlockItem("arda_portal", () -> new ArdaPortalBlock(BlockBehaviour.Properties.of().strength(-1F).noCollission().lightLevel((state) -> 10).noLootTable()));

        // Sleeping Bags
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_WHITE = BLOCKS.register("sleeping_bag_white", () -> new SleepingBagBlock(DyeColor.WHITE, Block.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_ORANGE = BLOCKS.register("sleeping_bag_orange", () -> new SleepingBagBlock(DyeColor.ORANGE, Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_MAGENTA = BLOCKS.register("sleeping_bag_magenta", () -> new SleepingBagBlock(DyeColor.MAGENTA, Block.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_LIGHT_BLUE = BLOCKS.register("sleeping_bag_light_blue", () -> new SleepingBagBlock(DyeColor.LIGHT_BLUE, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_YELLOW = BLOCKS.register("sleeping_bag_yellow", () -> new SleepingBagBlock(DyeColor.YELLOW, Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_LIME = BLOCKS.register("sleeping_bag_lime", () -> new SleepingBagBlock(DyeColor.LIME, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_PINK = BLOCKS.register("sleeping_bag_pink", () -> new SleepingBagBlock(DyeColor.PINK, Block.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_GRAY = BLOCKS.register("sleeping_bag_gray", () -> new SleepingBagBlock(DyeColor.GRAY, Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_LIGHT_GRAY = BLOCKS.register("sleeping_bag_light_gray", () -> new SleepingBagBlock(DyeColor.LIGHT_GRAY, Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_CYAN = BLOCKS.register("sleeping_bag_cyan", () -> new SleepingBagBlock(DyeColor.CYAN, Block.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_PURPLE = BLOCKS.register("sleeping_bag_purple", () -> new SleepingBagBlock(DyeColor.PURPLE, Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_BLUE = BLOCKS.register("sleeping_bag_blue", () -> new SleepingBagBlock(DyeColor.BLUE, Block.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_BROWN = BLOCKS.register("sleeping_bag_brown", () -> new SleepingBagBlock(DyeColor.BROWN, Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_GREEN = BLOCKS.register("sleeping_bag_green", () -> new SleepingBagBlock(DyeColor.GREEN, Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_RED = BLOCKS.register("sleeping_bag_red", () -> new SleepingBagBlock(DyeColor.RED, Block.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_BLACK = BLOCKS.register("sleeping_bag_black", () -> new SleepingBagBlock(DyeColor.BLACK, Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY)).setHasLore());

        //Placards
    public static final DeferredBlock<Block> PLACARD = registerBlock("placard", () -> new PlacardBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f)).setHasLore());

    //Boats

        // Block Entities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightningBugBlockEntity>> LIGHTNINGBUG_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("lightningbug", () ->
                    BlockEntityType.Builder.of(LightningBugBlockEntity::new, LIGHTNINGBUG_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LocustBlockEntity>> LOCUST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("locust", () ->
                    BlockEntityType.Builder.of(LocustBlockEntity::new, LOCUST_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlacardBlockEntity>> PLACARD_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("placard_tile", () ->
                    BlockEntityType.Builder.of(PlacardBlockEntity::new, PLACARD.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TolkienSignBlockEntity>> TOLKIEN_SIGN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("tolkien_sign_entity", () ->
                    BlockEntityType.Builder.of(TolkienSignBlockEntity::new, MALLORN_SIGN.get(), MALLORN_WALL_SIGN.get(), MIRKWOOD_SIGN.get(), MIRKWOOD_WALL_SIGN.get(), CULUMALDA_SIGN.get(), CULUMALDA_WALL_SIGN.get(), LEBETHRON_SIGN.get(), LEBETHRON_WALL_SIGN.get(), FANGORNOAK_SIGN.get(), FANGORNOAK_WALL_SIGN.get(), DEADWOOD_SIGN.get(), DEADWOOD_WALL_SIGN.get(), DWARVEN_MAPLE_SIGN.get(), DWARVEN_MAPLE_WALL_SIGN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TolkienHangingSignBlockEntity>> TOLKIEN_HANGING_SIGN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("tolkien_hanging_sign_entity", () ->
                    BlockEntityType.Builder.of(TolkienHangingSignBlockEntity::new, MALLORN_HANGING_SIGN.get(), MALLORN_HANGING_WALL_SIGN.get(), MIRKWOOD_HANGING_SIGN.get(), MIRKWOOD_HANGING_WALL_SIGN.get(), CULUMALDA_HANGING_SIGN.get(), CULUMALDA_HANGING_WALL_SIGN.get(), LEBETHRON_HANGING_SIGN.get(), LEBETHRON_HANGING_WALL_SIGN.get(), FANGORNOAK_HANGING_SIGN.get(), FANGORNOAK_HANGING_WALL_SIGN.get(), DEADWOOD_HANGING_SIGN.get(), DEADWOOD_HANGING_WALL_SIGN.get(), DWARVEN_MAPLE_HANGING_SIGN.get(), DWARVEN_MAPLE_HANGING_WALL_SIGN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrinketTableBlockEntity>> TRINKET_TABLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("trinket_table", () ->
                    BlockEntityType.Builder.of(TrinketTableBlockEntity::new, TRINKET_TABLE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PiggyBankBlockEntity>> PIGGY_BANK_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("block_piggybank", () ->
                    BlockEntityType.Builder.of(PiggyBankBlockEntity::new, PIGGYBANK.get()).build(null));

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

    private static <T extends Block> DeferredBlock<T> registerBlockWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static DeferredBlock<Block> registerNoItem(String name, Supplier<? extends Block> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        WOOD_TYPES.register(eventBus);
    }
}