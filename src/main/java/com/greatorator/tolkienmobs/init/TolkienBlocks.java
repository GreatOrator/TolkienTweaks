package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.block.*;
import com.greatorator.tolkienmobs.block.custom.*;
import com.greatorator.tolkienmobs.block.custom.entity.*;
import com.greatorator.tolkienmobs.handler.capability.TolkienFluidTank;
import com.greatorator.tolkienmobs.handler.capability.TolkienItemStackHandler;
import com.greatorator.tolkienmobs.handler.interfaces.TolkienRegistry;
import com.greatorator.tolkienmobs.item.custom.BackpackItem;
import com.greatorator.tolkienmobs.item.custom.PlacardItem;
import com.greatorator.tolkienmobs.world.components.config.TolkienConfiguredFeatures;
import com.greatorator.tolkienmobs.world.components.feature.tree.TolkienTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.TolkienMobsMain.resLoc;
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
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_END_MITHRIL = registerBlock("ore_end_mithril",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_NETHER_MITHRIL = registerBlock("ore_nether_mithril",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_DEEPSLATE_MITHRIL = registerBlock("ore_deepslate_mithril",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE));
    public static final DeferredBlock<Block> BLOCK_MITHRIL = registerBlock("block_mithril",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                    .strength(5f).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> RAW_MITHRIL_BLOCK = registerBlock("raw_mithril_block",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                    .strength(4f).requiresCorrectToolForDrops().sound(SoundType.ANCIENT_DEBRIS));
    public static final DeferredBlock<Block> STAIRS_MITHRIL = registerBlock("stairs_mithril",
            properties -> new StairBlock(TolkienBlocks.BLOCK_MITHRIL.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MITHRIL = registerBlock("slab_mithril",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MITHRIL = registerBlock("pressure_plate_mithril",
            properties -> new PressurePlateBlock(BlockSetType.GOLD, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MITHRIL_BUTTON = registerBlock("mithril_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<IronBarsBlock> MITHRIL_BARS = registerBlock("mithril_bars", properties ->
            new IronBarsBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS));
    public static final DeferredBlock<Block> WALL_MITHRIL = registerBlock("wall_mithril",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> DOOR_MITHRIL = registerBlock("door_mithril",
            properties -> new DoorBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL));
    public static final DeferredBlock<Block> TRAPDOOR_MITHRIL = registerBlock("trapdoor_mithril",
            properties -> new TrapDoorBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL));
    public static final DeferredBlock<Block> BARREL_MITHRIL = registerBlock("barrel_mithril",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_WHITE));

        // MorgulIron
    public static final DeferredBlock<Block> ORE_MORGULIRON = registerBlock("ore_morguliron",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_END_MORGULIRON = registerBlock("ore_end_morguliron",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_NETHER_MORGULIRON = registerBlock("ore_nether_morguliron",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_DEEPSLATE_MORGULIRON = registerBlock("ore_deepslate_morguliron",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE));
    public static final DeferredBlock<Block> BLOCK_MORGULIRON = registerBlock("block_morguliron",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                    .strength(5f).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> RAW_MORGULIRON_BLOCK = registerBlock("raw_morguliron_block",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                    .strength(4f).requiresCorrectToolForDrops().sound(SoundType.ANCIENT_DEBRIS));
    public static final DeferredBlock<Block> STAIRS_MORGULIRON = registerBlock("stairs_morguliron",
            properties -> new StairBlock(TolkienBlocks.BLOCK_MORGULIRON.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MORGULIRON = registerBlock("slab_morguliron",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MORGULIRON = registerBlock("pressure_plate_morguliron",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MORGULIRON_BUTTON = registerBlock("morguliron_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<IronBarsBlock> MORGULIRON_BARS = registerBlock("morguliron_bars", properties ->
            new IronBarsBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS));
    public static final DeferredBlock<Block> WALL_MORGULIRON = registerBlock("wall_morguliron",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DOOR_MORGULIRON = registerBlock("door_morguliron",
            properties -> new DoorBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL));
    public static final DeferredBlock<Block> TRAPDOOR_MORGULIRON = registerBlock("trapdoor_morguliron",
            properties -> new TrapDoorBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL));
    public static final DeferredBlock<Block> BARREL_MORGULIRON = registerBlock("barrel_morguliron",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_BLACK));

        // Ammolite
    public static final DeferredBlock<Block> ORE_AMMOLITE = registerBlock("ore_ammolite",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_END_AMMOLITE = registerBlock("ore_end_ammolite",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_NETHER_AMMOLITE = registerBlock("ore_nether_ammolite",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ORE_DEEPSLATE_AMMOLITE = registerBlock("ore_deepslate_ammolite",
            properties -> new DropExperienceBlock(UniformInt.of(2, 4), properties),
            BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE));
    public static final DeferredBlock<Block> BLOCK_AMMOLITE = registerBlock("block_ammolite",
            properties -> new TransparentBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(0.3f).requiresCorrectToolForDrops().isValidSpawn(Blocks::never).isRedstoneConductor(TolkienBlocks::never).isSuffocating(TolkienBlocks::never).isViewBlocking(TolkienBlocks::never).noOcclusion().sound(SoundType.GLASS));
    public static final DeferredBlock<IronBarsBlock> PANE_AMMOLITE = registerBlock("pane_ammolite", properties -> new IronBarsBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
    public static final DeferredBlock<Block> DOOR_DURIN = registerBlock("door_durin",
            properties -> new DoorBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL));

        // Darkstone
    public static final DeferredBlock<Block> DARK_STONE = registerBlock("dark_stone",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_DARK_STONE = registerBlock("stairs_dark_stone",
            properties -> new StairBlock(TolkienBlocks.DARK_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_DARK_STONE = registerBlock("slab_dark_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DARK_STONE = registerBlock("pressure_plate_dark_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DARK_STONE_BUTTON = registerBlock("dark_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_DARK_STONE = registerBlock("wall_dark_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_DARK_STONE_BRICKS = registerBlock("chiseled_dark_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CHISELED_DARK_STONE_BRICKS = registerBlock("stairs_chiseled_dark_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CHISELED_DARK_STONE_BRICKS = registerBlock("slab_chiseled_dark_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_DARK_STONE_BRICKS = registerBlock("pressure_plate_chiseled_dark_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_DARK_STONE_BRICKS_BUTTON = registerBlock("chiseled_dark_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CHISELED_DARK_STONE_BRICKS = registerBlock("wall_chiseled_dark_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_DARK_STONE = registerBlock("cobbled_dark_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_COBBLED_DARK_STONE = registerBlock("stairs_cobbled_dark_stone",
            properties -> new StairBlock(TolkienBlocks.COBBLED_DARK_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_COBBLED_DARK_STONE = registerBlock("slab_cobbled_dark_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_DARK_STONE = registerBlock("pressure_plate_cobbled_dark_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_DARK_STONE_BUTTON = registerBlock("cobbled_dark_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_COBBLED_DARK_STONE = registerBlock("wall_cobbled_dark_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_DARK_STONE = registerBlock("mossy_cobbled_dark_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_COBBLED_DARK_STONE = registerBlock("stairs_mossy_cobbled_dark_stone",
            properties -> new StairBlock(TolkienBlocks.MOSSY_COBBLED_DARK_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_COBBLED_DARK_STONE = registerBlock("slab_mossy_cobbled_dark_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_COBBLED_DARK_STONE = registerBlock("pressure_plate_mossy_cobbled_dark_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_DARK_STONE_BUTTON = registerBlock("mossy_cobbled_dark_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_COBBLED_DARK_STONE = registerBlock("wall_mossy_cobbled_dark_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_DARK_STONE_BRICKS = registerBlock("mossy_dark_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_DARK_STONE_BRICKS = registerBlock("stairs_mossy_dark_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.MOSSY_DARK_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_DARK_STONE_BRICKS = registerBlock("slab_mossy_dark_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_DARK_STONE_BRICKS = registerBlock("pressure_plate_mossy_dark_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_DARK_STONE_BRICKS_BUTTON = registerBlock("mossy_dark_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_DARK_STONE_BRICKS = registerBlock("wall_mossy_dark_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_DARK_STONE_BRICKS = registerBlock("cracked_dark_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CRACKED_DARK_STONE_BRICKS = registerBlock("stairs_cracked_dark_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CRACKED_DARK_STONE_BRICKS = registerBlock("slab_cracked_dark_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_DARK_STONE_BRICKS = registerBlock("pressure_plate_cracked_dark_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_DARK_STONE_BRICKS_BUTTON = registerBlock("cracked_dark_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CRACKED_DARK_STONE_BRICKS = registerBlock("wall_cracked_dark_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DARK_STONE_BRICKS = registerBlock("dark_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_DARK_STONE_BRICKS = registerBlock("stairs_dark_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.DARK_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_DARK_STONE_BRICKS = registerBlock("slab_dark_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DARK_STONE_BRICKS = registerBlock("pressure_plate_dark_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DARK_STONE_BRICKS_BUTTON = registerBlock("dark_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_DARK_STONE_BRICKS = registerBlock("wall_dark_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_DARK_STONE = registerBlock("smooth_dark_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_DARK_STONE = registerBlock("stairs_smooth_dark_stone",
            properties -> new StairBlock(TolkienBlocks.SMOOTH_DARK_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_SMOOTH_DARK_STONE = registerBlock("slab_smooth_dark_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_DARK_STONE = registerBlock("pressure_plate_smooth_dark_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_DARK_STONE_BUTTON = registerBlock("smooth_dark_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_SMOOTH_DARK_STONE = registerBlock("wall_smooth_dark_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());

        // Dwarven Stone
    public static final DeferredBlock<Block> DWARVEN_STONE = registerBlock("dwarven_stone",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_DWARVEN_STONE = registerBlock("stairs_dwarven_stone",
            properties -> new StairBlock(TolkienBlocks.DWARVEN_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_DWARVEN_STONE = registerBlock("slab_dwarven_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DWARVEN_STONE = registerBlock("pressure_plate_dwarven_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DWARVEN_STONE_BUTTON = registerBlock("dwarven_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_DWARVEN_STONE = registerBlock("wall_dwarven_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_DWARVEN_STONE_BRICKS = registerBlock("mossy_dwarven_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_DWARVEN_STONE_BRICKS = registerBlock("stairs_mossy_dwarven_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.MOSSY_DWARVEN_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_DWARVEN_STONE_BRICKS = registerBlock("slab_mossy_dwarven_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_DWARVEN_STONE_BRICKS = registerBlock("pressure_plate_mossy_dwarven_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_DWARVEN_STONE_BRICKS_BUTTON = registerBlock("mossy_dwarven_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_DWARVEN_STONE_BRICKS = registerBlock("wall_mossy_dwarven_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_DWARVEN_STONE = registerBlock("mossy_cobbled_dwarven_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_COBBLED_DWARVEN_STONE = registerBlock("stairs_mossy_cobbled_dwarven_stone",
            properties -> new StairBlock(TolkienBlocks.MOSSY_COBBLED_DWARVEN_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_COBBLED_DWARVEN_STONE = registerBlock("slab_mossy_cobbled_dwarven_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_COBBLED_DWARVEN_STONE = registerBlock("pressure_plate_mossy_cobbled_dwarven_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_DWARVEN_STONE_BUTTON = registerBlock("mossy_cobbled_dwarven_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_COBBLED_DWARVEN_STONE = registerBlock("wall_mossy_cobbled_dwarven_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_DWARVEN_STONE_BRICKS = registerBlock("chiseled_dwarven_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CHISELED_DWARVEN_STONE_BRICKS = registerBlock("stairs_chiseled_dwarven_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CHISELED_DWARVEN_STONE_BRICKS = registerBlock("slab_chiseled_dwarven_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_DWARVEN_STONE_BRICKS = registerBlock("pressure_plate_chiseled_dwarven_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_DWARVEN_STONE_BRICKS_BUTTON = registerBlock("chiseled_dwarven_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CHISELED_DWARVEN_STONE_BRICKS = registerBlock("wall_chiseled_dwarven_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_DWARVEN_STONE = registerBlock("cobbled_dwarven_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_COBBLED_DWARVEN_STONE = registerBlock("stairs_cobbled_dwarven_stone",
            properties -> new StairBlock(TolkienBlocks.COBBLED_DWARVEN_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_COBBLED_DWARVEN_STONE = registerBlock("slab_cobbled_dwarven_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_DWARVEN_STONE = registerBlock("pressure_plate_cobbled_dwarven_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_DWARVEN_STONE_BUTTON = registerBlock("cobbled_dwarven_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_COBBLED_DWARVEN_STONE = registerBlock("wall_cobbled_dwarven_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_DWARVEN_STONE_BRICKS = registerBlock("cracked_dwarven_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CRACKED_DWARVEN_STONE_BRICKS = registerBlock("stairs_cracked_dwarven_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CRACKED_DWARVEN_STONE_BRICKS = registerBlock("slab_cracked_dwarven_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_DWARVEN_STONE_BRICKS = registerBlock("pressure_plate_cracked_dwarven_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_DWARVEN_STONE_BRICKS_BUTTON = registerBlock("cracked_dwarven_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CRACKED_DWARVEN_STONE_BRICKS = registerBlock("wall_cracked_dwarven_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DWARVEN_STONE_BRICKS = registerBlock("dwarven_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_DWARVEN_STONE_BRICKS = registerBlock("stairs_dwarven_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.DWARVEN_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_DWARVEN_STONE_BRICKS = registerBlock("slab_dwarven_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DWARVEN_STONE_BRICKS = registerBlock("pressure_plate_dwarven_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> DWARVEN_STONE_BRICKS_BUTTON = registerBlock("dwarven_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_DWARVEN_STONE_BRICKS = registerBlock("wall_dwarven_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_DWARVEN_STONE = registerBlock("smooth_dwarven_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_DWARVEN_STONE = registerBlock("stairs_smooth_dwarven_stone",
            properties -> new StairBlock(TolkienBlocks.SMOOTH_DWARVEN_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_SMOOTH_DWARVEN_STONE = registerBlock("slab_smooth_dwarven_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_DWARVEN_STONE = registerBlock("pressure_plate_smooth_dwarven_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_DWARVEN_STONE_BUTTON = registerBlock("smooth_dwarven_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_SMOOTH_DWARVEN_STONE = registerBlock("wall_smooth_dwarven_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());

        // Elven Marble
    public static final DeferredBlock<Block> ELVEN_MARBLE = registerBlock("elven_marble",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_ELVEN_MARBLE = registerBlock("stairs_elven_marble",
            properties -> new StairBlock(TolkienBlocks.ELVEN_MARBLE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_ELVEN_MARBLE = registerBlock("slab_elven_marble",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_ELVEN_MARBLE = registerBlock("pressure_plate_elven_marble",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> ELVEN_MARBLE_BUTTON = registerBlock("elven_marble_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_ELVEN_MARBLE = registerBlock("wall_elven_marble",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("chiseled_elven_marble_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("stairs_chiseled_elven_marble_bricks",
            properties -> new StairBlock(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("slab_chiseled_elven_marble_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("pressure_plate_chiseled_elven_marble_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_ELVEN_MARBLE_BRICKS_BUTTON = registerBlock("chiseled_elven_marble_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CHISELED_ELVEN_MARBLE_BRICKS = registerBlock("wall_chiseled_elven_marble_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_ELVEN_MARBLE = registerBlock("cobbled_elven_marble",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_COBBLED_ELVEN_MARBLE = registerBlock("stairs_cobbled_elven_marble",
            properties -> new StairBlock(TolkienBlocks.COBBLED_ELVEN_MARBLE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_COBBLED_ELVEN_MARBLE = registerBlock("slab_cobbled_elven_marble",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_ELVEN_MARBLE = registerBlock("pressure_plate_cobbled_elven_marble",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_ELVEN_MARBLE_BUTTON = registerBlock("cobbled_elven_marble_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_COBBLED_ELVEN_MARBLE = registerBlock("wall_cobbled_elven_marble",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("cracked_elven_marble_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("stairs_cracked_elven_marble_bricks",
            properties -> new StairBlock(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("slab_cracked_elven_marble_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("pressure_plate_cracked_elven_marble_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_ELVEN_MARBLE_BRICKS_BUTTON = registerBlock("cracked_elven_marble_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CRACKED_ELVEN_MARBLE_BRICKS = registerBlock("wall_cracked_elven_marble_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> ELVEN_MARBLE_BRICKS = registerBlock("elven_marble_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_ELVEN_MARBLE_BRICKS = registerBlock("stairs_elven_marble_bricks",
            properties -> new StairBlock(TolkienBlocks.ELVEN_MARBLE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_ELVEN_MARBLE_BRICKS = registerBlock("slab_elven_marble_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_ELVEN_MARBLE_BRICKS = registerBlock("pressure_plate_elven_marble_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> ELVEN_MARBLE_BRICKS_BUTTON = registerBlock("elven_marble_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_ELVEN_MARBLE_BRICKS = registerBlock("wall_elven_marble_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_ELVEN_MARBLE = registerBlock("smooth_elven_marble",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_ELVEN_MARBLE = registerBlock("stairs_smooth_elven_marble",
            properties -> new StairBlock(TolkienBlocks.SMOOTH_ELVEN_MARBLE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_SMOOTH_ELVEN_MARBLE = registerBlock("slab_smooth_elven_marble",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_ELVEN_MARBLE = registerBlock("pressure_plate_smooth_elven_marble",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_ELVEN_MARBLE_BUTTON = registerBlock("smooth_elven_marble_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_SMOOTH_ELVEN_MARBLE = registerBlock("wall_smooth_elven_marble",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_ELVEN_MARBLE_BRICKS = registerBlock("mossy_elven_marble_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_ELVEN_MARBLE_BRICKS = registerBlock("stairs_mossy_elven_marble_bricks",
            properties -> new StairBlock(TolkienBlocks.MOSSY_ELVEN_MARBLE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_ELVEN_MARBLE_BRICKS = registerBlock("slab_mossy_elven_marble_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_ELVEN_MARBLE_BRICKS = registerBlock("pressure_plate_mossy_elven_marble_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_ELVEN_MARBLE_BRICKS_BUTTON = registerBlock("mossy_elven_marble_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_ELVEN_MARBLE_BRICKS = registerBlock("wall_mossy_elven_marble_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_ELVEN_MARBLE = registerBlock("mossy_cobbled_elven_marble",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_COBBLED_ELVEN_MARBLE = registerBlock("stairs_mossy_cobbled_elven_marble",
            properties -> new StairBlock(TolkienBlocks.MOSSY_COBBLED_ELVEN_MARBLE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_COBBLED_ELVEN_MARBLE = registerBlock("slab_mossy_cobbled_elven_marble",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_COBBLED_ELVEN_MARBLE = registerBlock("pressure_plate_mossy_cobbled_elven_marble",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_ELVEN_MARBLE_BUTTON = registerBlock("mossy_cobbled_elven_marble_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_COBBLED_ELVEN_MARBLE = registerBlock("wall_mossy_cobbled_elven_marble",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());

        // Mountain Stone
    public static final DeferredBlock<Block> MOUNTAIN_STONE = registerBlock("mountain_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOUNTAIN_STONE = registerBlock("stairs_mountain_stone",
            properties -> new StairBlock(TolkienBlocks.MOUNTAIN_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOUNTAIN_STONE = registerBlock("slab_mountain_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOUNTAIN_STONE = registerBlock("pressure_plate_mountain_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOUNTAIN_STONE_BUTTON = registerBlock("mountain_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOUNTAIN_STONE = registerBlock("wall_mountain_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_MOUNTAIN_STONE = registerBlock("mossy_cobbled_mountain_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_COBBLED_MOUNTAIN_STONE = registerBlock("stairs_mossy_cobbled_mountain_stone",
            properties -> new StairBlock(TolkienBlocks.MOSSY_COBBLED_MOUNTAIN_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_COBBLED_MOUNTAIN_STONE = registerBlock("slab_mossy_cobbled_mountain_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_COBBLED_MOUNTAIN_STONE = registerBlock("pressure_plate_mossy_cobbled_mountain_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_MOUNTAIN_STONE_BUTTON = registerBlock("mossy_cobbled_mountain_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_COBBLED_MOUNTAIN_STONE = registerBlock("wall_mossy_cobbled_mountain_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_MOUNTAIN_STONE_BRICKS = registerBlock("mossy_mountain_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_MOUNTAIN_STONE_BRICKS = registerBlock("stairs_mossy_mountain_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.MOSSY_MOUNTAIN_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_MOUNTAIN_STONE_BRICKS = registerBlock("slab_mossy_mountain_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_MOUNTAIN_STONE_BRICKS = registerBlock("pressure_plate_mossy_mountain_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_MOUNTAIN_STONE_BRICKS_BUTTON = registerBlock("mossy_mountain_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_MOUNTAIN_STONE_BRICKS = registerBlock("wall_mossy_mountain_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("chiseled_mountain_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("stairs_chiseled_mountain_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("slab_chiseled_mountain_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("pressure_plate_chiseled_mountain_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_MOUNTAIN_STONE_BRICKS_BUTTON = registerBlock("chiseled_mountain_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CHISELED_MOUNTAIN_STONE_BRICKS = registerBlock("wall_chiseled_mountain_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_MOUNTAIN_STONE = registerBlock("cobbled_mountain_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_COBBLED_MOUNTAIN_STONE = registerBlock("stairs_cobbled_mountain_stone",
            properties -> new StairBlock(TolkienBlocks.COBBLED_MOUNTAIN_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_COBBLED_MOUNTAIN_STONE = registerBlock("slab_cobbled_mountain_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_MOUNTAIN_STONE = registerBlock("pressure_plate_cobbled_mountain_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_MOUNTAIN_STONE_BUTTON = registerBlock("cobbled_mountain_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_COBBLED_MOUNTAIN_STONE = registerBlock("wall_cobbled_mountain_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("cracked_mountain_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("stairs_cracked_mountain_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("slab_cracked_mountain_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("pressure_plate_cracked_mountain_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_MOUNTAIN_STONE_BRICKS_BUTTON = registerBlock("cracked_mountain_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CRACKED_MOUNTAIN_STONE_BRICKS = registerBlock("wall_cracked_mountain_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOUNTAIN_STONE_BRICKS = registerBlock("mountain_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOUNTAIN_STONE_BRICKS = registerBlock("stairs_mountain_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOUNTAIN_STONE_BRICKS = registerBlock("slab_mountain_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOUNTAIN_STONE_BRICKS = registerBlock("pressure_plate_mountain_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOUNTAIN_STONE_BRICKS_BUTTON = registerBlock("mountain_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOUNTAIN_STONE_BRICKS = registerBlock("wall_mountain_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_MOUNTAIN_STONE = registerBlock("smooth_mountain_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_MOUNTAIN_STONE = registerBlock("stairs_smooth_mountain_stone",
            properties -> new StairBlock(TolkienBlocks.SMOOTH_MOUNTAIN_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_SMOOTH_MOUNTAIN_STONE = registerBlock("slab_smooth_mountain_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_MOUNTAIN_STONE = registerBlock("pressure_plate_smooth_mountain_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_MOUNTAIN_STONE_BUTTON = registerBlock("smooth_mountain_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_SMOOTH_MOUNTAIN_STONE = registerBlock("wall_smooth_mountain_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());

        //Iron Stone
    public static final DeferredBlock<Block> IRON_STONE = registerBlock("iron_stone",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_IRON_STONE = registerBlock("stairs_iron_stone",
            properties -> new StairBlock(TolkienBlocks.IRON_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_IRON_STONE = registerBlock("slab_iron_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_IRON_STONE = registerBlock("pressure_plate_iron_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> IRON_STONE_BUTTON = registerBlock("iron_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_IRON_STONE = registerBlock("wall_iron_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_IRON_STONE = registerBlock("mossy_cobbled_iron_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_COBBLED_IRON_STONE = registerBlock("stairs_mossy_cobbled_iron_stone",
            properties -> new StairBlock(TolkienBlocks.MOSSY_COBBLED_IRON_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_COBBLED_IRON_STONE = registerBlock("slab_mossy_cobbled_iron_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_COBBLED_IRON_STONE = registerBlock("pressure_plate_mossy_cobbled_iron_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_IRON_STONE_BUTTON = registerBlock("mossy_cobbled_iron_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_COBBLED_IRON_STONE = registerBlock("wall_mossy_cobbled_iron_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_IRON_STONE_BRICKS = registerBlock("mossy_iron_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_IRON_STONE_BRICKS = registerBlock("stairs_mossy_iron_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.MOSSY_IRON_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_IRON_STONE_BRICKS = registerBlock("slab_mossy_iron_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_IRON_STONE_BRICKS = registerBlock("pressure_plate_mossy_iron_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_IRON_STONE_BRICKS_BUTTON = registerBlock("mossy_iron_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_IRON_STONE_BRICKS = registerBlock("wall_mossy_iron_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_IRON_STONE_BRICKS = registerBlock("chiseled_iron_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CHISELED_IRON_STONE_BRICKS = registerBlock("stairs_chiseled_iron_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.CHISELED_IRON_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CHISELED_IRON_STONE_BRICKS = registerBlock("slab_chiseled_iron_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_IRON_STONE_BRICKS = registerBlock("pressure_plate_chiseled_iron_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_IRON_STONE_BRICKS_BUTTON = registerBlock("chiseled_iron_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CHISELED_IRON_STONE_BRICKS = registerBlock("wall_chiseled_iron_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_IRON_STONE = registerBlock("cobbled_iron_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_COBBLED_IRON_STONE = registerBlock("stairs_cobbled_iron_stone",
            properties -> new StairBlock(TolkienBlocks.COBBLED_IRON_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_COBBLED_IRON_STONE = registerBlock("slab_cobbled_iron_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_IRON_STONE = registerBlock("pressure_plate_cobbled_iron_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_IRON_STONE_BUTTON = registerBlock("cobbled_iron_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_COBBLED_IRON_STONE = registerBlock("wall_cobbled_iron_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_IRON_STONE_BRICKS = registerBlock("cracked_iron_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CRACKED_IRON_STONE_BRICKS = registerBlock("stairs_cracked_iron_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.CRACKED_IRON_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CRACKED_IRON_STONE_BRICKS = registerBlock("slab_cracked_iron_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_IRON_STONE_BRICKS = registerBlock("pressure_plate_cracked_iron_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_IRON_STONE_BRICKS_BUTTON = registerBlock("cracked_iron_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CRACKED_IRON_STONE_BRICKS = registerBlock("wall_cracked_iron_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> IRON_STONE_BRICKS = registerBlock("iron_stone_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_IRON_STONE_BRICKS = registerBlock("stairs_iron_stone_bricks",
            properties -> new StairBlock(TolkienBlocks.IRON_STONE_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_IRON_STONE_BRICKS = registerBlock("slab_iron_stone_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_IRON_STONE_BRICKS = registerBlock("pressure_plate_iron_stone_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> IRON_STONE_BRICKS_BUTTON = registerBlock("iron_stone_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_IRON_STONE_BRICKS = registerBlock("wall_iron_stone_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_IRON_STONE = registerBlock("smooth_iron_stone",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_IRON_STONE = registerBlock("stairs_smooth_iron_stone",
            properties -> new StairBlock(TolkienBlocks.SMOOTH_IRON_STONE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_SMOOTH_IRON_STONE = registerBlock("slab_smooth_iron_stone",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_IRON_STONE = registerBlock("pressure_plate_smooth_iron_stone",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_IRON_STONE_BUTTON = registerBlock("smooth_iron_stone_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_SMOOTH_IRON_STONE = registerBlock("wall_smooth_iron_stone",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());

        // Angainor
    public static final DeferredBlock<Block> ANGAINOR = registerBlock("angainor",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_ANGAINOR = registerBlock("stairs_angainor",
            properties -> new StairBlock(TolkienBlocks.ANGAINOR.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_ANGAINOR = registerBlock("slab_angainor",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_ANGAINOR = registerBlock("pressure_plate_angainor",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> ANGAINOR_BUTTON = registerBlock("angainor_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_ANGAINOR = registerBlock("wall_angainor",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_ANGAINOR = registerBlock("mossy_cobbled_angainor",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_COBBLED_ANGAINOR = registerBlock("stairs_mossy_cobbled_angainor",
            properties -> new StairBlock(TolkienBlocks.MOSSY_COBBLED_ANGAINOR.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_COBBLED_ANGAINOR = registerBlock("slab_mossy_cobbled_angainor",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_COBBLED_ANGAINOR = registerBlock("pressure_plate_mossy_cobbled_angainor",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_COBBLED_ANGAINOR_BUTTON = registerBlock("mossy_cobbled_angainor_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_COBBLED_ANGAINOR = registerBlock("wall_mossy_cobbled_angainor",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_ANGAINOR_BRICKS = registerBlock("mossy_angainor_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_MOSSY_ANGAINOR_BRICKS = registerBlock("stairs_mossy_angainor_bricks",
            properties -> new StairBlock(TolkienBlocks.MOSSY_ANGAINOR_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_MOSSY_ANGAINOR_BRICKS = registerBlock("slab_mossy_angainor_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MOSSY_ANGAINOR_BRICKS = registerBlock("pressure_plate_mossy_angainor_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> MOSSY_ANGAINOR_BRICKS_BUTTON = registerBlock("mossy_angainor_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_MOSSY_ANGAINOR_BRICKS = registerBlock("wall_mossy_angainor_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_ANGAINOR_BRICKS = registerBlock("chiseled_angainor_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CHISELED_ANGAINOR_BRICKS = registerBlock("stairs_chiseled_angainor_bricks",
            properties -> new StairBlock(TolkienBlocks.CHISELED_ANGAINOR_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CHISELED_ANGAINOR_BRICKS = registerBlock("slab_chiseled_angainor_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CHISELED_ANGAINOR_BRICKS = registerBlock("pressure_plate_chiseled_angainor_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHISELED_ANGAINOR_BRICKS_BUTTON = registerBlock("chiseled_angainor_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CHISELED_ANGAINOR_BRICKS = registerBlock("wall_chiseled_angainor_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_ANGAINOR = registerBlock("cobbled_angainor",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_COBBLED_ANGAINOR = registerBlock("stairs_cobbled_angainor",
            properties -> new StairBlock(TolkienBlocks.COBBLED_ANGAINOR.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_COBBLED_ANGAINOR = registerBlock("slab_cobbled_angainor",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_COBBLED_ANGAINOR = registerBlock("pressure_plate_cobbled_angainor",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> COBBLED_ANGAINOR_BUTTON = registerBlock("cobbled_angainor_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_COBBLED_ANGAINOR = registerBlock("wall_cobbled_angainor",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_ANGAINOR_BRICKS = registerBlock("cracked_angainor_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_CRACKED_ANGAINOR_BRICKS = registerBlock("stairs_cracked_angainor_bricks",
            properties -> new StairBlock(TolkienBlocks.CRACKED_ANGAINOR_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_CRACKED_ANGAINOR_BRICKS = registerBlock("slab_cracked_angainor_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CRACKED_ANGAINOR_BRICKS = registerBlock("pressure_plate_cracked_angainor_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_ANGAINOR_BRICKS_BUTTON = registerBlock("cracked_angainor_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_CRACKED_ANGAINOR_BRICKS = registerBlock("wall_cracked_angainor_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> ANGAINOR_BRICKS = registerBlock("angainor_bricks",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_ANGAINOR_BRICKS = registerBlock("stairs_angainor_bricks",
            properties -> new StairBlock(TolkienBlocks.ANGAINOR_BRICKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_ANGAINOR_BRICKS = registerBlock("slab_angainor_bricks",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_ANGAINOR_BRICKS = registerBlock("pressure_plate_angainor_bricks",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> ANGAINOR_BRICKS_BUTTON = registerBlock("angainor_bricks_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_ANGAINOR_BRICKS = registerBlock("wall_angainor_bricks",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_ANGAINOR = registerBlock("smooth_angainor",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.BASALT));
    public static final DeferredBlock<Block> STAIRS_SMOOTH_ANGAINOR = registerBlock("stairs_smooth_angainor",
            properties -> new StairBlock(TolkienBlocks.SMOOTH_ANGAINOR.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SLAB_SMOOTH_ANGAINOR = registerBlock("slab_smooth_angainor",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<Block> PRESSURE_PLATE_SMOOTH_ANGAINOR = registerBlock("pressure_plate_smooth_angainor",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties),
            BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).forceSolidOn().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SMOOTH_ANGAINOR_BUTTON = registerBlock("smooth_angainor_button",
            properties -> new ButtonBlock(BlockSetType.IRON, 10, properties),
            BlockBehaviour.Properties.of()
                    .strength(4F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> WALL_SMOOTH_ANGAINOR = registerBlock("wall_smooth_angainor",
            properties -> new WallBlock(properties),
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops());

    /* Wood & Foliage */
        // Mallorn
    public static final DeferredBlock<Block> LOG_MALLORN = registerBlock("log_mallorn",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WOOD_MALLORN = registerBlock("wood_mallorn",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_MALLORN_LOG = registerBlock("stripped_log_mallorn",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_MALLORN_WOOD = registerBlock("stripped_wood_mallorn",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> LEAVES_MALLORN = registerBlock("leaves_mallorn",
            properties -> new UntintedParticleLeavesBlock(0.01F, TolkienParticleTypes.MALLORN_LEAVES.get(), properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> HARDENED_LEAVES_MALLORN = registerBlock("hardened_leaves_mallorn",
            properties -> new TolkienHardenedLeavesBlock(properties, LEAVES_MALLORN.get()),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> LEAFPILE_MALLORN = registerBlock("leafpile_mallorn",
            properties -> new LeafPileBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> PLANKS_MALLORN = registerBlock("planks_mallorn",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STAIRS_MALLORN = registerBlock("stairs_mallorn",
            properties -> new StairBlock(TolkienBlocks.PLANKS_MALLORN.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> SLAB_MALLORN = registerBlock("slab_mallorn",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MALLORN = registerBlock("pressure_plate_mallorn",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> MALLORN_BUTTON = registerBlock("mallorn_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> FENCE_MALLORN = registerBlock("fence_mallorn",
            properties -> new FenceBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> FENCE_GATE_MALLORN = registerBlock("fence_gate_mallorn",
            properties -> new FenceGateBlock(WoodType.ACACIA, properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> DOOR_MALLORN = registerBlock("door_mallorn",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TRAPDOOR_MALLORN = registerBlock("trapdoor_mallorn",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TORCH_MALLORN = registerNoItem("torch_mallorn",
            properties -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.MALLORN_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD));

    public static final DeferredBlock<Block> WALL_TORCH_MALLORN = registerNoItem("wall_torch_mallorn",
            properties -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.MALLORN_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).overrideLootTable(blockLoot("torch_mallorn")));
    public static final DeferredBlock<Block> BARREL_MALLORN = registerBlock("barrel_mallorn",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_WHITE));
    public static final DeferredBlock<StandingSignBlock> MALLORN_SIGN = registerBlockWithoutBlockItem("sign_mallorn", properties -> new TolkienSignBlock(TolkienWoodTypes.MALLORN, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallSignBlock> MALLORN_WALL_SIGN = registerBlockWithoutBlockItem("sign_wall_mallorn", properties -> new TolkienWallSignBlock(TolkienWoodTypes.MALLORN, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<CeilingHangingSignBlock> MALLORN_HANGING_SIGN = registerBlockWithoutBlockItem("hanging_sign_mallorn", properties -> new TolkienHangingSignBlock(TolkienWoodTypes.MALLORN, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallHangingSignBlock> MALLORN_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("hanging_sign_wall_mallorn", properties -> new TolkienHangingWallSignBlock(TolkienWoodTypes.MALLORN, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<Block> LADDER_MALLORN = registerBlock("ladder_mallorn", properties -> new LadderBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER));

    // Mirkwood
    public static final DeferredBlock<Block> LOG_MIRKWOOD = registerBlock("log_mirkwood",
                properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WOOD_MIRKWOOD = registerBlock("wood_mirkwood",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_MIRKWOOD_LOG = registerBlock("stripped_log_mirkwood",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_MIRKWOOD_WOOD = registerBlock("stripped_wood_mirkwood",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> LEAVES_MIRKWOOD = registerBlock("leaves_mirkwood",
            properties -> new UntintedParticleLeavesBlock(0.01F, TolkienParticleTypes.MIRKWOOD_LEAVES.get(), properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> HARDENED_LEAVES_MIRKWOOD = registerBlock("hardened_leaves_mirkwood",
            properties -> new TolkienHardenedLeavesBlock(properties, LEAVES_MIRKWOOD.get()),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> LEAFPILE_MIRKWOOD = registerBlock("leafpile_mirkwood",
            properties -> new LeafPileBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> PLANKS_MIRKWOOD = registerBlock("planks_mirkwood",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STAIRS_MIRKWOOD = registerBlock("stairs_mirkwood",
            properties -> new StairBlock(TolkienBlocks.PLANKS_MIRKWOOD.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> SLAB_MIRKWOOD = registerBlock("slab_mirkwood",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> PRESSURE_PLATE_MIRKWOOD = registerBlock("pressure_plate_mirkwood",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> MIRKWOOD_BUTTON = registerBlock("mirkwood_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> FENCE_MIRKWOOD = registerBlock("fence_mirkwood",
            properties -> new FenceBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> FENCE_GATE_MIRKWOOD = registerBlock("fence_gate_mirkwood",
            properties -> new FenceGateBlock(WoodType.ACACIA, properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> DOOR_MIRKWOOD = registerBlock("door_mirkwood",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TRAPDOOR_MIRKWOOD = registerBlock("trapdoor_mirkwood",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TORCH_MIRKWOOD = registerNoItem("torch_mirkwood",
            properties -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.MIRKWOOD_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WALL_TORCH_MIRKWOOD = registerNoItem("wall_torch_mirkwood",
            properties -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.MIRKWOOD_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).overrideLootTable(blockLoot("torch_mirkwood")));
    public static final DeferredBlock<Block> BARREL_MIRKWOOD = registerBlock("barrel_mirkwood",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_BLACK));
    public static final DeferredBlock<StandingSignBlock> MIRKWOOD_SIGN = registerBlockWithoutBlockItem("sign_mirkwood", properties -> new TolkienSignBlock(TolkienWoodTypes.MIRKWOOD, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallSignBlock> MIRKWOOD_WALL_SIGN = registerBlockWithoutBlockItem("sign_wall_mirkwood", properties -> new TolkienWallSignBlock(TolkienWoodTypes.MIRKWOOD, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<CeilingHangingSignBlock> MIRKWOOD_HANGING_SIGN = registerBlockWithoutBlockItem("hanging_sign_mirkwood", properties -> new TolkienHangingSignBlock(TolkienWoodTypes.MIRKWOOD, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallHangingSignBlock> MIRKWOOD_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("hanging_sign_wall_mirkwood", properties -> new TolkienHangingWallSignBlock(TolkienWoodTypes.MIRKWOOD, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<Block> LADDER_MIRKWOOD = registerBlock("ladder_mirkwood", properties -> new LadderBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER));

        // Culumalda
    public static final DeferredBlock<Block> LOG_CULUMALDA = registerBlock("log_culumalda",
                properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WOOD_CULUMALDA = registerBlock("wood_culumalda",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_CULUMALDA_LOG = registerBlock("stripped_log_culumalda",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_CULUMALDA_WOOD = registerBlock("stripped_wood_culumalda",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> LEAVES_CULUMALDA = registerBlock("leaves_culumalda",
                properties -> new UntintedParticleLeavesBlock(0.01F, TolkienParticleTypes.CULUMALDA_LEAVES.get(), properties),
            BlockBehaviour.Properties.of()
                        .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> HARDENED_LEAVES_CULUMALDA = registerBlock("hardened_leaves_culumalda",
            properties -> new TolkienHardenedLeavesBlock(properties, LEAVES_CULUMALDA.get()),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> LEAFPILE_CULUMALDA = registerBlock("leafpile_culumalda",
            properties -> new LeafPileBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> PLANKS_CULUMALDA = registerBlock("planks_culumalda",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STAIRS_CULUMALDA = registerBlock("stairs_culumalda",
            properties -> new StairBlock(TolkienBlocks.PLANKS_CULUMALDA.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> SLAB_CULUMALDA = registerBlock("slab_culumalda",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> PRESSURE_PLATE_CULUMALDA = registerBlock("pressure_plate_culumalda",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> CULUMALDA_BUTTON = registerBlock("culumalda_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> FENCE_CULUMALDA = registerBlock("fence_culumalda",
            properties -> new FenceBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> FENCE_GATE_CULUMALDA = registerBlock("fence_gate_culumalda",
            properties -> new FenceGateBlock(WoodType.ACACIA, properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> DOOR_CULUMALDA = registerBlock("door_culumalda",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TRAPDOOR_CULUMALDA = registerBlock("trapdoor_culumalda",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TORCH_CULUMALDA = registerNoItem("torch_culumalda",
            properties -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.CULUMALDA_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WALL_TORCH_CULUMALDA = registerNoItem("wall_torch_culumalda",
            properties -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.CULUMALDA_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).overrideLootTable(blockLoot("torch_culumalda")));
    public static final DeferredBlock<Block> BARREL_CULUMALDA = registerBlock("barrel_culumalda",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_GRAY));
    public static final DeferredBlock<StandingSignBlock> CULUMALDA_SIGN = registerBlockWithoutBlockItem("sign_culumalda", properties -> new TolkienSignBlock(TolkienWoodTypes.CULUMALDA, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallSignBlock> CULUMALDA_WALL_SIGN = registerBlockWithoutBlockItem("sign_wall_culumalda", properties -> new TolkienWallSignBlock(TolkienWoodTypes.CULUMALDA, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<CeilingHangingSignBlock> CULUMALDA_HANGING_SIGN = registerBlockWithoutBlockItem("hanging_sign_culumalda", properties -> new TolkienHangingSignBlock(TolkienWoodTypes.CULUMALDA, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallHangingSignBlock> CULUMALDA_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("hanging_sign_wall_culumalda", properties -> new TolkienHangingWallSignBlock(TolkienWoodTypes.CULUMALDA, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<Block> LADDER_CULUMALDA = registerBlock("ladder_culumalda", properties -> new LadderBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER));

        // Lebethron
    public static final DeferredBlock<Block> LOG_LEBETHRON = registerBlock("log_lebethron",
                properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WOOD_LEBETHRON = registerBlock("wood_lebethron",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_LEBETHRON_LOG = registerBlock("stripped_log_lebethron",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_LEBETHRON_WOOD = registerBlock("stripped_wood_lebethron",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> LEAVES_LEBETHRON = registerBlock("leaves_lebethron",
                properties -> new UntintedParticleLeavesBlock(0.01F, TolkienParticleTypes.LEBETHRON_LEAVES.get(), properties),
            BlockBehaviour.Properties.of()
                        .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> HARDENED_LEAVES_LEBETHRON = registerBlock("hardened_leaves_lebethron",
            properties -> new TolkienHardenedLeavesBlock(properties, LEAVES_LEBETHRON.get()),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> LEAFPILE_LEBETHRON = registerBlock("leafpile_lebethron",
            properties -> new LeafPileBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> PLANKS_LEBETHRON = registerBlock("planks_lebethron",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STAIRS_LEBETHRON = registerBlock("stairs_lebethron",
            properties -> new StairBlock(TolkienBlocks.PLANKS_LEBETHRON.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> SLAB_LEBETHRON = registerBlock("slab_lebethron",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> PRESSURE_PLATE_LEBETHRON = registerBlock("pressure_plate_lebethron",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> LEBETHRON_BUTTON = registerBlock("lebethron_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> FENCE_LEBETHRON = registerBlock("fence_lebethron",
            properties -> new FenceBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> FENCE_GATE_LEBETHRON = registerBlock("fence_gate_lebethron",
            properties -> new FenceGateBlock(WoodType.ACACIA, properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> DOOR_LEBETHRON = registerBlock("door_lebethron",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TRAPDOOR_LEBETHRON = registerBlock("trapdoor_lebethron",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TORCH_LEBETHRON = registerNoItem("torch_lebethron",
            properties -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.LEBETHRON_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WALL_TORCH_LEBETHRON = registerNoItem("wall_torch_lebethron",
            properties -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.LEBETHRON_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).overrideLootTable(blockLoot("torch_lebethron")));
    public static final DeferredBlock<Block> BARREL_LEBETHRON = registerBlock("barrel_lebethron",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY));
    public static final DeferredBlock<StandingSignBlock> LEBETHRON_SIGN = registerBlockWithoutBlockItem("sign_lebethron", properties -> new TolkienSignBlock(TolkienWoodTypes.LEBETHRON, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallSignBlock> LEBETHRON_WALL_SIGN = registerBlockWithoutBlockItem("sign_wall_lebethron", properties -> new TolkienWallSignBlock(TolkienWoodTypes.LEBETHRON, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<CeilingHangingSignBlock> LEBETHRON_HANGING_SIGN = registerBlockWithoutBlockItem("hanging_sign_lebethron", properties -> new TolkienHangingSignBlock(TolkienWoodTypes.LEBETHRON, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallHangingSignBlock> LEBETHRON_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("hanging_sign_wall_lebethron", properties -> new TolkienHangingWallSignBlock(TolkienWoodTypes.LEBETHRON, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<Block> LADDER_LEBETHRON = registerBlock("ladder_lebethron", properties -> new LadderBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER));

        // Fangorn Oak
    public static final DeferredBlock<Block> LOG_FANGORNOAK = registerBlock("log_fangornoak",
                properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WOOD_FANGORNOAK = registerBlock("wood_fangornoak",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_FANGORNOAK_LOG = registerBlock("stripped_log_fangornoak",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_FANGORNOAK_WOOD = registerBlock("stripped_wood_fangornoak",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> LEAVES_FANGORNOAK = registerBlock("leaves_fangornoak",
                properties -> new UntintedParticleLeavesBlock(0.01F, TolkienParticleTypes.FANGORNOAK_LEAVES.get(), properties),
            BlockBehaviour.Properties.of()
                        .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> HARDENED_LEAVES_FANGORNOAK = registerBlock("hardened_leaves_fangornoak",
            properties -> new TolkienHardenedLeavesBlock(properties, LEAVES_FANGORNOAK.get()),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> LEAFPILE_FANGORNOAK = registerBlock("leafpile_fangornoak",
            properties -> new LeafPileBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> PLANKS_FANGORNOAK = registerBlock("planks_fangornoak",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STAIRS_FANGORNOAK = registerBlock("stairs_fangornoak",
            properties -> new StairBlock(TolkienBlocks.PLANKS_FANGORNOAK.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> SLAB_FANGORNOAK = registerBlock("slab_fangornoak",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> PRESSURE_PLATE_FANGORNOAK = registerBlock("pressure_plate_fangornoak",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> FANGORNOAK_BUTTON = registerBlock("fangornoak_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> FENCE_FANGORNOAK = registerBlock("fence_fangornoak",
            properties -> new FenceBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> FENCE_GATE_FANGORNOAK = registerBlock("fence_gate_fangornoak",
            properties -> new FenceGateBlock(WoodType.ACACIA, properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> DOOR_FANGORNOAK = registerBlock("door_fangornoak",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TRAPDOOR_FANGORNOAK = registerBlock("trapdoor_fangornoak",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TORCH_FANGORNOAK = registerNoItem("torch_fangornoak",
            properties -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.FANGORNOAK_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WALL_TORCH_FANGORNOAK = registerNoItem("wall_torch_fangornoak",
            properties -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.FANGORNOAK_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).overrideLootTable(blockLoot("torch_fangornoak")));
    public static final DeferredBlock<Block> BARREL_FANGORNOAK = registerBlock("barrel_fangornoak",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GREEN));
    public static final DeferredBlock<StandingSignBlock> FANGORNOAK_SIGN = registerBlockWithoutBlockItem("sign_fangornoak", properties -> new TolkienSignBlock(TolkienWoodTypes.FANGORNOAK, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallSignBlock> FANGORNOAK_WALL_SIGN = registerBlockWithoutBlockItem("sign_wall_fangornoak", properties -> new TolkienWallSignBlock(TolkienWoodTypes.FANGORNOAK, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<CeilingHangingSignBlock> FANGORNOAK_HANGING_SIGN = registerBlockWithoutBlockItem("hanging_sign_fangornoak", properties -> new TolkienHangingSignBlock(TolkienWoodTypes.FANGORNOAK, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallHangingSignBlock> FANGORNOAK_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("hanging_sign_wall_fangornoak", properties -> new TolkienHangingWallSignBlock(TolkienWoodTypes.FANGORNOAK, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<Block> LADDER_FANGORNOAK = registerBlock("ladder_fangornoak", properties -> new LadderBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER));

        // Deadwood
    public static final DeferredBlock<Block> LOG_DEADWOOD = registerBlock("log_deadwood",
                properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WOOD_DEADWOOD = registerBlock("wood_deadwood",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_DEADWOOD_LOG = registerBlock("stripped_log_deadwood",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_DEADWOOD_WOOD = registerBlock("stripped_wood_deadwood",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> PLANKS_DEADWOOD = registerBlock("planks_deadwood",
                properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STAIRS_DEADWOOD = registerBlock("stairs_deadwood",
            properties -> new StairBlock(TolkienBlocks.PLANKS_DEADWOOD.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava().noOcclusion());
    public static final DeferredBlock<Block> SLAB_DEADWOOD = registerBlock("slab_deadwood",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DEADWOOD = registerBlock("pressure_plate_deadwood",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava().noOcclusion());
    public static final DeferredBlock<Block> DEADWOOD_BUTTON = registerBlock("deadwood_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> FENCE_DEADWOOD = registerBlock("fence_deadwood",
            properties -> new FenceBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> FENCE_GATE_DEADWOOD = registerBlock("fence_gate_deadwood",
            properties -> new FenceGateBlock(WoodType.ACACIA, properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> DOOR_DEADWOOD = registerBlock("door_deadwood",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TRAPDOOR_DEADWOOD = registerBlock("trapdoor_deadwood",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TORCH_DEADWOOD = registerNoItem("torch_deadwood",
            properties -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.DEADWOOD_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WALL_TORCH_DEADWOOD = registerNoItem("wall_torch_deadwood",
            properties -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.DEADWOOD_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).overrideLootTable(blockLoot("torch_deadwood")));
    public static final DeferredBlock<Block> BARREL_DEADWOOD = registerBlock("barrel_deadwood",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_ORANGE));
    public static final DeferredBlock<StandingSignBlock> DEADWOOD_SIGN = registerBlockWithoutBlockItem("sign_deadwood", properties -> new TolkienSignBlock(TolkienWoodTypes.DEADWOOD, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallSignBlock> DEADWOOD_WALL_SIGN = registerBlockWithoutBlockItem("sign_wall_deadwood", properties -> new TolkienWallSignBlock(TolkienWoodTypes.DEADWOOD, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<CeilingHangingSignBlock> DEADWOOD_HANGING_SIGN = registerBlockWithoutBlockItem("hanging_sign_deadwood", properties -> new TolkienHangingSignBlock(TolkienWoodTypes.DEADWOOD, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallHangingSignBlock> DEADWOOD_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("hanging_sign_wall_deadwood", properties -> new TolkienHangingWallSignBlock(TolkienWoodTypes.DEADWOOD, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<Block> LADDER_DEADWOOD = registerBlock("ladder_deadwood", properties -> new LadderBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER));

        // Dwarven Maple
    public static final DeferredBlock<Block> LOG_DWARVEN_MAPLE = registerBlock("log_dwarven_maple",
                properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> WOOD_DWARVEN_MAPLE = registerBlock("wood_dwarven_maple",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_DWARVEN_MAPLE_LOG = registerBlock("stripped_log_dwarven_maple",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STRIPPED_DWARVEN_MAPLE_WOOD = registerBlock("stripped_wood_dwarven_maple",
            properties -> new RotatedPillarBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> LEAVES_DWARVEN_MAPLE = registerBlock("leaves_dwarven_maple",
            properties -> new UntintedParticleLeavesBlock(0.01F, TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> HARDENED_LEAVES_DWARVEN_MAPLE = registerBlock("hardened_leaves_dwarven_maple",
            properties -> new TolkienHardenedLeavesBlock(properties, LEAVES_DWARVEN_MAPLE.get()),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> LEAFPILE_DWARVEN_MAPLE = registerBlock("leafpile_dwarven_maple",
            properties -> new LeafPileBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> PLANKS_DWARVEN_MAPLE = registerBlock("planks_dwarven_maple",
            properties -> new Block(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> STAIRS_DWARVEN_MAPLE = registerBlock("stairs_dwarven_maple",
            properties -> new StairBlock(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.of()
                    .strength(2f).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> SLAB_DWARVEN_MAPLE = registerBlock("slab_dwarven_maple",
            properties -> new SlabBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> PRESSURE_PLATE_DWARVEN_MAPLE = registerBlock("pressure_plate_dwarven_maple",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().ignitedByLava());
    public static final DeferredBlock<Block> DWARVEN_MAPLE_BUTTON = registerBlock("dwarven_maple_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.of()
                    .strength(0.5F).requiresCorrectToolForDrops().noCollision());
    public static final DeferredBlock<Block> FENCE_DWARVEN_MAPLE = registerBlock("fence_dwarven_maple",
            properties -> new FenceBlock(properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> FENCE_GATE_DWARVEN_MAPLE = registerBlock("fence_gate_dwarven_maple",
            properties -> new FenceGateBlock(WoodType.ACACIA, properties),
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).ignitedByLava().requiresCorrectToolForDrops().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> DOOR_DWARVEN_MAPLE = registerBlock("door_dwarven_maple",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TRAPDOOR_DWARVEN_MAPLE = registerBlock("trapdoor_dwarven_maple",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .strength(3.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.WOOD));
    public static final DeferredBlock<Block> TORCH_DWARVEN_MAPLE = registerNoItem("torch_dwarven_maple",
            properties -> new TolkienTorchBlock(
                    () -> TolkienParticleTypes.DWARVEN_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD));

    public static final DeferredBlock<Block> WALL_TORCH_DWARVEN_MAPLE = registerNoItem("wall_torch_dwarven_maple",
            properties -> new TolkienWallTorchBlock(
                    () -> TolkienParticleTypes.DWARVEN_FLAME.get(), properties),
            BlockBehaviour.Properties.of()
                            .noCollision().instabreak().lightLevel((state) -> 10).sound(SoundType.WOOD).overrideLootTable(blockLoot("torch_dwarven_maple")));
    public static final DeferredBlock<Block> BARREL_DWARVEN_MAPLE = registerBlock("barrel_dwarven_maple",
            properties -> new TolkienBarrelBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .mapColor(MapColor.TERRACOTTA_WHITE));
    public static final DeferredBlock<StandingSignBlock> DWARVEN_MAPLE_SIGN = registerBlockWithoutBlockItem("sign_dwarven_maple", properties -> new TolkienSignBlock(TolkienWoodTypes.DWARVEN_MAPLE, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallSignBlock> DWARVEN_MAPLE_WALL_SIGN = registerBlockWithoutBlockItem("sign_wall_dwarven_maple", properties -> new TolkienWallSignBlock(TolkienWoodTypes.DWARVEN_MAPLE, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<CeilingHangingSignBlock> DWARVEN_MAPLE_HANGING_SIGN = registerBlockWithoutBlockItem("hanging_sign_dwarven_maple", properties -> new TolkienHangingSignBlock(TolkienWoodTypes.DWARVEN_MAPLE, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<WallHangingSignBlock> DWARVEN_MAPLE_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("hanging_sign_wall_dwarven_maple", properties -> new TolkienHangingWallSignBlock(TolkienWoodTypes.DWARVEN_MAPLE, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f).ignitedByLava());
    public static final DeferredBlock<Block> LADDER_DWARVEN_MAPLE = registerBlock("ladder_dwarven_maple", properties -> new LadderBlock(properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER));

    // Flowers & Plants
    public static final DeferredBlock<Block> PIPEWEED = registerBlockWithoutBlockItem("pipeweed", properties -> new PipeweedCropBlock(properties),
            BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> MOSS_PATCH = registerBlock("moss_patch", properties -> new MossPatchBlock(properties),
            BlockBehaviour.Properties.of().ignitedByLava().instabreak().mapColor(MapColor.PLANT).noCollision().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SoundType.MOSS));
    public static final DeferredBlock<Block> CLOVER_PATCH = registerBlock("clover_patch", properties -> new TolkienPatchBlock(properties),
            BlockBehaviour.Properties.of().ignitedByLava().noCollision().noOcclusion().instabreak().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> SAPLING_MALLORN = registerBlock("sapling_mallorn",
            properties -> new TolkienSaplingBlock(TolkienTreeGrowers.MALLORN, properties, Blocks.GRASS_BLOCK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
    public static final DeferredBlock<Block> SAPLING_MIRKWOOD = registerBlock("sapling_mirkwood",
            properties -> new TolkienSaplingBlock(TolkienTreeGrowers.MIRKWOOD, properties, Blocks.GRASS_BLOCK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
    public static final DeferredBlock<Block> SAPLING_CULUMALDA = registerBlock("sapling_culumalda",
            properties -> new TolkienSaplingBlock(TolkienTreeGrowers.CULUMALDA, properties, Blocks.GRASS_BLOCK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
    public static final DeferredBlock<Block> SAPLING_LEBETHRON = registerBlock("sapling_lebethron",
            properties -> new TolkienSaplingBlock(TolkienTreeGrowers.LEBETHRON, properties, Blocks.GRASS_BLOCK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
    public static final DeferredBlock<Block> SAPLING_FANGORNOAK = registerBlock("sapling_fangornoak",
            properties -> new TolkienSaplingBlock(TolkienTreeGrowers.FANGORNOAK, properties, Blocks.GRASS_BLOCK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
    public static final DeferredBlock<Block> SAPLING_DEADWOOD = registerBlock("sapling_deadwood",
            properties -> new TolkienSaplingBlock(TolkienTreeGrowers.DEADWOOD, properties, Blocks.GRASS_BLOCK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
    public static final DeferredBlock<Block> SAPLING_DWARVEN_MAPLE = registerBlock("sapling_dwarven_maple",
            properties -> new TolkienSaplingBlock(TolkienTreeGrowers.DWARVEN_MAPLE, properties, Blocks.GRASS_BLOCK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
    public static final DeferredBlock<Block> MUSHROOM_DECAY_BLOOM = registerBlock("mushroom_decay_bloom",
            properties -> new MushroomsBlock(TolkienConfiguredFeatures.MUSHROOM_DECAY_BLOOM_KEY, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM));
    public static final DeferredBlock<Block> MUSHROOM_BLOOM_DECAY = registerBlock("mushroom_bloom_decay",
            properties -> new MushroomsBlock(TolkienConfiguredFeatures.MUSHROOM_BLOOM_DECAY_KEY, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM));
    public static final DeferredBlock<Block> BLOCK_DECAY_BLOOM = registerBlock("block_decay_bloom", properties -> new HugeMushroomBlock(properties),
            BlockBehaviour.Properties.of().noCollision().instabreak().sound(SoundType.WOOD).strength(0.2F));
    public static final DeferredBlock<Block> BLOCK_BLOOM_DECAY = registerBlock("block_bloom_decay", properties -> new HugeMushroomBlock(properties),
            BlockBehaviour.Properties.of().noCollision().instabreak().sound(SoundType.WOOD).strength(0.2F));
    public static final DeferredBlock<Block> LIVING_ROOTS = registerBlock("living_roots", properties -> new Block(properties),
            BlockBehaviour.Properties.of().noCollision().ignitedByLava().instabreak().sound(SoundType.GRASS).strength(2.0F, 3.0F));
    public static final DeferredBlock<Block> FLOWER_SIMBELMYNE = registerBlock("flower_simbelmyne", properties -> new TolkienFlowerBlock(MobEffects.HERO_OF_THE_VILLAGE, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_MIRKWOOD = registerBlock("flower_mirkwood", properties -> new TolkienFlowerBlock(MobEffects.BAD_OMEN, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_ALFIRIN = registerBlock("flower_alfirin", properties -> new TolkienFlowerBlock(MobEffects.SATURATION, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_ATHELAS = registerBlock("flower_athelas", properties -> new TolkienFlowerBlock(MobEffects.REGENERATION, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_NIPHREDIL = registerBlock("flower_niphredil", properties -> new TolkienFlowerBlock(MobEffects.SATURATION, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_SWAMPMILKWEED = registerBlock("flower_swamp_milkweed", properties -> new TolkienFlowerBlock(MobEffects.SLOWNESS, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_LILLYOFTHEVALLEY = registerBlock("flower_valley_lilly", properties -> new TolkienFlowerBlock(MobEffects.INSTANT_HEALTH, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_ELANOR = registerBlock("flower_elanor", properties -> new TolkienFlowerBlock(MobEffects.SPEED, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_AEGLOS = registerBlock("flower_aeglos", properties -> new TolkienFlowerBlock(MobEffects.BAD_OMEN, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_LISSUIN = registerBlock("flower_lissuin", properties -> new TolkienFlowerBlock(MobEffects.RESISTANCE, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_MALLOS = registerBlock("flower_mallos", properties -> new TolkienFlowerBlock(MobEffects.HASTE, 7, properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<Block> FLOWER_BRAMBLES = registerBlock("flower_brambles", properties -> new BramblesBushBlock(properties),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_SIMBELMYNE = registerBlockWithoutBlockItem("potted_flower_simbelmyne", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_SIMBELMYNE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_MIRKWOOD = registerBlockWithoutBlockItem("potted_flower_mirkwood", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_MIRKWOOD, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_ALFIRIN = registerBlockWithoutBlockItem("potted_flower_alfirin", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_ALFIRIN, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_ATHELAS = registerBlockWithoutBlockItem("potted_flower_athelas", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_ATHELAS, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_NIPHREDIL = registerBlockWithoutBlockItem("potted_flower_niphredil", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_NIPHREDIL, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_SWAMPMILKWEED = registerBlockWithoutBlockItem("potted_flower_swamp_milkweed", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_SWAMPMILKWEED, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_LILLYOFTHEVALLEY = registerBlockWithoutBlockItem("potted_flower_valley_lilly", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_LILLYOFTHEVALLEY, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_ELANOR = registerBlockWithoutBlockItem("potted_flower_elanor", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_ELANOR, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_AEGLOS = registerBlockWithoutBlockItem("potted_flower_aeglos", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_AEGLOS, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_LISSUIN = registerBlockWithoutBlockItem("potted_flower_lissuin", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_LISSUIN, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_MALLOS = registerBlockWithoutBlockItem("potted_flower_mallos", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_MALLOS, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_FLOWER_BRAMBLES = registerBlockWithoutBlockItem("potted_flower_brambles", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLOWER_BRAMBLES, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_MALLORN = registerBlockWithoutBlockItem("potted_sapling_mallorn", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_MALLORN, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_MIRKWOOD = registerBlockWithoutBlockItem("potted_sapling_mirkwood", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_MIRKWOOD, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_CULUMALDA = registerBlockWithoutBlockItem("potted_sapling_culumalda", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_CULUMALDA, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_LEBETHRON = registerBlockWithoutBlockItem("potted_sapling_lebethron", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_LEBETHRON, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_FANGORNOAK = registerBlockWithoutBlockItem("potted_sapling_fangornoak", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_FANGORNOAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_DEADWOOD = registerBlockWithoutBlockItem("potted_sapling_deadwood", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_DEADWOOD, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAPLING_DWARVEN_MAPLE = registerBlockWithoutBlockItem("potted_sapling_dwarven_maple", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAPLING_DWARVEN_MAPLE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_MUSHROOM_BLOOM_DECAY = registerBlockWithoutBlockItem("potted_mushroom_bloom_decay", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MUSHROOM_BLOOM_DECAY, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
    public static final DeferredBlock<FlowerPotBlock> POTTED_MUSHROOM_DECAY_BLOOM = registerBlockWithoutBlockItem("potted_mushroom_decay_bloom", properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MUSHROOM_DECAY_BLOOM, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));

        // Custom
    public static final DeferredBlock<Block> LIGHTNINGBUG_BLOCK = registerBlock("lightningbug", properties -> new LightningBugBlock(properties),
            BlockBehaviour.Properties.of().lightLevel((state) -> 15).instabreak().noCollision().noTerrainParticles().pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK));
    public static final DeferredBlock<Block> LOCUST_BLOCK = registerBlock("locust", properties -> new LocustBlock(properties),
            BlockBehaviour.Properties.of().lightLevel((state) -> 15).instabreak().noCollision().noTerrainParticles().pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK));
    public static final DeferredBlock<Block> SILMARIL_LANTERN = registerBlock("silmaril_lantern",
            properties -> new FancyLanternBlock(properties),
            BlockBehaviour.Properties.of().strength(4.5F, 5.5F).requiresCorrectToolForDrops().noOcclusion().lightLevel((state) -> state.getValue(LIT) ? 15:0).sound(SoundType.LANTERN));
    public static final DeferredBlock<Block> ELVEN_LANTERN = registerBlock("elven_lantern",
            properties -> new FancyLanternBlock(properties),
            BlockBehaviour.Properties.of().strength(3.5F, 3.5F).requiresCorrectToolForDrops().noOcclusion().lightLevel((state) -> state.getValue(LIT) ? 13:0).sound(SoundType.LANTERN));
    public static final DeferredBlock<Block> MORGUL_LANTERN = registerBlock("morgul_lantern",
            properties -> new FancyLanternBlock(properties),
            BlockBehaviour.Properties.of().strength(3.5F, 3.5F).requiresCorrectToolForDrops().noOcclusion().lightLevel((state) -> state.getValue(LIT) ? 11:0).sound(SoundType.LANTERN));
    public static final DeferredBlock<Block> BLOCK_HALLOWED = registerBlock("block_hallowed", properties -> new HallowedBlock(properties),
            BlockBehaviour.Properties.of().randomTicks().sound(SoundType.GRAVEL));
    public static final DeferredBlock<Block> STONE_PATH = registerBlock("block_stone_path", properties -> new StonePathBlock(properties),
            BlockBehaviour.Properties.of().isViewBlocking(TolkienBlocks::never).sound(SoundType.STONE));
    public static final DeferredBlock<Block> ROCKPILE = registerBlock("rockpile", properties -> new RockPileBlock(properties),
            BlockBehaviour.Properties.of().offsetType(BlockBehaviour.OffsetType.XZ).noCollision().instabreak().sound(SoundType.STONE).strength(0.2F).dynamicShape());
    public static final DeferredBlock<Block> WALL_DECAY_BLOOM = registerBlock("wall_decay_bloom", properties -> new WallDecayBloomBlock(properties),
            BlockBehaviour.Properties.of().noCollision().instabreak().sound(SoundType.GRASS).strength(0.2F).dynamicShape());
    public static final DeferredBlock<Block> WALL_MUSHROOM_RED = registerBlock("wall_mushroom_red", properties -> new WallMushroomRedBlock(properties),
            BlockBehaviour.Properties.of().noCollision().instabreak().sound(SoundType.GRASS).strength(0.2F).dynamicShape());
    public static final DeferredBlock<Block> WALL_MUSHROOM_BROWN = registerBlock("wall_mushroom_brown", properties -> new WallMushroomBrownBlock(properties),
            BlockBehaviour.Properties.of().noCollision().instabreak().sound(SoundType.GRASS).strength(0.2F).dynamicShape());
    public static final DeferredBlock<Block> TRINKET_TABLE = registerBlock("trinket_table", properties -> new TrinketTableBlock(properties),
            BlockBehaviour.Properties.of().strength(3.5F, 3.5F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE));
    public static final DeferredBlock<Block> FIREPLACE = registerBlock("fireplace", properties -> new FireplaceBlock(true, properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().sound(SoundType.STONE).strength(5f, 6f).lightLevel((state) -> state.getValue(LIT) ? 15:0));
    public static final DeferredBlock<Block> PIGGYBANK = registerBlock("block_piggybank", properties -> new PiggyBankBlock(properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(5f, 6f));
    public static final DeferredBlock<Block> BACKPACK = registerBlockWithoutBlockItem("backpack", properties -> new BackpackBlock(properties),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f));
    public static final DeferredItem<BackpackItem> BACKPACK_ITEM = ITEMS.registerItem("backpack", properties -> new BackpackItem(BACKPACK.get(), properties), () -> new Item.Properties().stacksTo(1));
    public static final DeferredBlock<Block> CHAMELEON_BLOCK = registerBlock("chameleon_block", properties -> new ChameleonBlock(properties),
            BlockBehaviour.Properties.of().noCollision().noOcclusion());
    public static final DeferredBlock<Block> KEY_STONE_BLOCK = registerBlock("block_key_stone", properties -> new CamoKeyStoneBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> CAMO_GLOWSTONE_BLOCK = registerBlock("block_camo_glowstone", properties -> new CamoGlowstoneBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().lightLevel((state) -> 15));
    public static final DeferredBlock<Block> CAMO_SMOKER_BLOCK = registerBlock("block_camo_smoker", properties -> new CamoSmokerBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> CAMO_FLUID_BLOCK = registerBlock("block_camo_fluid", properties -> new CamoFluidBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> CAMO_CHEST_BLOCK = registerBlock("block_camo_chest", properties -> new CamoChestBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> CAMO_SPAWNER_BLOCK = registerBlock("block_camo_spawner", properties -> new CamoSpawnerBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> MILESTONE_BLOCK = registerBlock("milestone_block", properties -> new MilestoneBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion().lightLevel((state) -> 4));
    public static final DeferredBlock<Block> LOCKABLE_CHEST_BLOCK = registerBlock("lockable_chest_block", properties -> new LockableChestBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> LOCKABLE_TREASURE_CHEST_BLOCK = registerBlock("lockable_treasure_chest_block", properties -> new LockableTreasureChestBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> LOCKABLE_DOUBLE_CHEST_BLOCK = registerBlock("lockable_double_chest_block", properties -> new LockableDoubleChestBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> LOCKABLE_DOUBLE_TREASURE_CHEST_BLOCK = registerBlock("lockable_double_treasure_chest_block", properties -> new LockableDoubleTreasureChestBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion());
    public static final DeferredBlock<Block> WELL = registerBlock("well_block", properties -> new WellBlock(properties),
            BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().strength(3.5F, 3.5F).noOcclusion());
    public static final DeferredBlock<Block> ARDA_PORTAL = registerBlockWithoutBlockItem("arda_portal", properties -> new ArdaPortalBlock(properties),
            BlockBehaviour.Properties.of().strength(-1F).noCollision().lightLevel((state) -> 10).noLootTable());
    public static final DeferredBlock<Block> ANCIENT_WATCHER = registerBlock("ancient_watcher",
            properties -> new AncientWatcherStatueBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).randomTicks().noLootTable().noOcclusion().sound(SoundType.STONE));
    public static final DeferredBlock<Block> ANCIENT_DEAD_WATCHER = registerBlock("ancient_dead_watcher",
            properties -> new AncientWatcherDeadStatueBlock(properties),
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).randomTicks().noLootTable().noOcclusion().sound(SoundType.STONE));

        // Sleeping Bags
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_WHITE = registerBlockWithoutBlockItem("sleeping_bag_white", properties -> new SleepingBagBlock(DyeColor.WHITE, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_ORANGE = registerBlockWithoutBlockItem("sleeping_bag_orange", properties -> new SleepingBagBlock(DyeColor.ORANGE, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_MAGENTA = registerBlockWithoutBlockItem("sleeping_bag_magenta", properties -> new SleepingBagBlock(DyeColor.MAGENTA, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_LIGHT_BLUE = registerBlockWithoutBlockItem("sleeping_bag_light_blue", properties -> new SleepingBagBlock(DyeColor.LIGHT_BLUE, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_YELLOW = registerBlockWithoutBlockItem("sleeping_bag_yellow", properties -> new SleepingBagBlock(DyeColor.YELLOW, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_LIME = registerBlockWithoutBlockItem("sleeping_bag_lime", properties -> new SleepingBagBlock(DyeColor.LIME, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_PINK = registerBlockWithoutBlockItem("sleeping_bag_pink", properties -> new SleepingBagBlock(DyeColor.PINK, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_GRAY = registerBlockWithoutBlockItem("sleeping_bag_gray", properties -> new SleepingBagBlock(DyeColor.GRAY, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_LIGHT_GRAY = registerBlockWithoutBlockItem("sleeping_bag_light_gray", properties -> new SleepingBagBlock(DyeColor.LIGHT_GRAY, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_CYAN = registerBlockWithoutBlockItem("sleeping_bag_cyan", properties -> new SleepingBagBlock(DyeColor.CYAN, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_PURPLE = registerBlockWithoutBlockItem("sleeping_bag_purple", properties -> new SleepingBagBlock(DyeColor.PURPLE, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_BLUE = registerBlockWithoutBlockItem("sleeping_bag_blue", properties -> new SleepingBagBlock(DyeColor.BLUE, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_BROWN = registerBlockWithoutBlockItem("sleeping_bag_brown", properties -> new SleepingBagBlock(DyeColor.BROWN, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_GREEN = registerBlockWithoutBlockItem("sleeping_bag_green", properties -> new SleepingBagBlock(DyeColor.GREEN, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_RED = registerBlockWithoutBlockItem("sleeping_bag_red", properties -> new SleepingBagBlock(DyeColor.RED, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SleepingBagBlock> SLEEPING_BAG_BLACK = registerBlockWithoutBlockItem("sleeping_bag_black", properties -> new SleepingBagBlock(DyeColor.BLACK, properties).setHasLore(),
            Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.WOOL).strength(0.2F).noOcclusion().pushReaction(PushReaction.DESTROY));

        //Placards
    // 26.1 dropped Block#appendHoverText, so the lore line rides on the item and the block is registered without one.
    public static final DeferredBlock<Block> PLACARD = registerBlockWithoutBlockItem("placard", properties -> new PlacardBlock(properties).setHasLore(),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(1f, 1f));
    public static final DeferredItem<PlacardItem> PLACARD_ITEM = ITEMS.registerItem("placard", properties -> new PlacardItem(PLACARD.get(), properties), () -> new Item.Properties());

    //Boats

        // Block Entities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightningBugBlockEntity>> LIGHTNINGBUG_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("lightningbug", () ->
                    new BlockEntityType<>(LightningBugBlockEntity::new, LIGHTNINGBUG_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LocustBlockEntity>> LOCUST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("locust", () ->
                    new BlockEntityType<>(LocustBlockEntity::new, LOCUST_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlacardBlockEntity>> PLACARD_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("placard_tile", () ->
                    new BlockEntityType<>(PlacardBlockEntity::new, PLACARD.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TolkienSignBlockEntity>> TOLKIEN_SIGN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("tolkien_sign_entity", () ->
                    new BlockEntityType<>(TolkienSignBlockEntity::new, MALLORN_SIGN.get(), MALLORN_WALL_SIGN.get(), MIRKWOOD_SIGN.get(), MIRKWOOD_WALL_SIGN.get(), CULUMALDA_SIGN.get(), CULUMALDA_WALL_SIGN.get(), LEBETHRON_SIGN.get(), LEBETHRON_WALL_SIGN.get(), FANGORNOAK_SIGN.get(), FANGORNOAK_WALL_SIGN.get(), DEADWOOD_SIGN.get(), DEADWOOD_WALL_SIGN.get(), DWARVEN_MAPLE_SIGN.get(), DWARVEN_MAPLE_WALL_SIGN.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TolkienHangingSignBlockEntity>> TOLKIEN_HANGING_SIGN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("tolkien_hanging_sign_entity", () ->
                    new BlockEntityType<>(TolkienHangingSignBlockEntity::new, MALLORN_HANGING_SIGN.get(), MALLORN_HANGING_WALL_SIGN.get(), MIRKWOOD_HANGING_SIGN.get(), MIRKWOOD_HANGING_WALL_SIGN.get(), CULUMALDA_HANGING_SIGN.get(), CULUMALDA_HANGING_WALL_SIGN.get(), LEBETHRON_HANGING_SIGN.get(), LEBETHRON_HANGING_WALL_SIGN.get(), FANGORNOAK_HANGING_SIGN.get(), FANGORNOAK_HANGING_WALL_SIGN.get(), DEADWOOD_HANGING_SIGN.get(), DEADWOOD_HANGING_WALL_SIGN.get(), DWARVEN_MAPLE_HANGING_SIGN.get(), DWARVEN_MAPLE_HANGING_WALL_SIGN.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrinketTableBlockEntity>> TRINKET_TABLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("trinket_table", () ->
                    new BlockEntityType<>(TrinketTableBlockEntity::new, TRINKET_TABLE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FireplaceBlockEntity>> FIREPLACE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("fireplace", () ->
                    new BlockEntityType<>(FireplaceBlockEntity::new, FIREPLACE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PiggyBankBlockEntity>> PIGGY_BANK_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("block_piggybank", () ->
                    new BlockEntityType<>(PiggyBankBlockEntity::new, PIGGYBANK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LockableChestBlockEntity>> LOCKABLE_CHEST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("lockable_chest_block", () ->
                    new BlockEntityType<>(LockableChestBlockEntity::new, LOCKABLE_CHEST_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LockableTreasureChestBlockEntity>> LOCKABLE_TREASURE_CHEST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("lockable_treasure_chest_block", () ->
                    new BlockEntityType<>(LockableTreasureChestBlockEntity::new, LOCKABLE_TREASURE_CHEST_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LockableDoubleChestBlockEntity>> LOCKABLE_DOUBLE_CHEST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("lockable_double_chest_block", () ->
                    new BlockEntityType<>(LockableDoubleChestBlockEntity::new, LOCKABLE_DOUBLE_CHEST_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LockableDoubleTreasureChestBlockEntity>> LOCKABLE_DOUBLE_TREASURE_CHEST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("lockable_double_treasure_chest_block", () ->
                    new BlockEntityType<>(LockableDoubleTreasureChestBlockEntity::new, LOCKABLE_DOUBLE_TREASURE_CHEST_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BackpackBlockEntity>> BACKPACK_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("backpack", () ->
                    new BlockEntityType<>(BackpackBlockEntity::new, BACKPACK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CamoSpawnerBlockEntity>> CAMO_SPAWNER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("camo_spawner", () ->
                    new BlockEntityType<>(CamoSpawnerBlockEntity::new, CAMO_SPAWNER_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CamoKeyStoneBlockEntity>> KEY_STONE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("block_key_stone", () ->
                    new BlockEntityType<>(CamoKeyStoneBlockEntity::new, KEY_STONE_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CamoChestBlockEntity>> CAMO_CHEST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("block_camo_chest", () ->
                    new BlockEntityType<>(CamoChestBlockEntity::new, CAMO_CHEST_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CamoFluidBlockEntity>> CAMO_FLUID_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("block_camo_fluid", () ->
                    new BlockEntityType<>(CamoFluidBlockEntity::new, CAMO_FLUID_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TolkienBarrelBlockEntity>> TOLKIEN_BARREL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("tolkien_barrel_block_entity", () ->
                    new BlockEntityType<>(TolkienBarrelBlockEntity::new, BARREL_MALLORN.get(), BARREL_MIRKWOOD.get(), BARREL_CULUMALDA.get(), BARREL_DEADWOOD.get(), BARREL_MITHRIL.get(), BARREL_FANGORNOAK.get(), BARREL_MORGULIRON.get(), BARREL_DWARVEN_MAPLE.get(), BARREL_LEBETHRON.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MilestoneBlockEntity>> MILESTONE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("milestone_block", () ->
                    new BlockEntityType<>(MilestoneBlockEntity::new, MILESTONE_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WellBlockEntity>> WELL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("well_block", () ->
                    new BlockEntityType<>(WellBlockEntity::new, WELL.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AncientWatcherStatueBlockEntity>> ANCIENT_WATCHER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("ancient_watcher", () ->
                    new BlockEntityType<>(AncientWatcherStatueBlockEntity::new, ANCIENT_WATCHER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AncientWatcherDeadStatueBlockEntity>> ANCIENT_WATCHER_DEAD_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("ancient_dead_watcher", () ->
                    new BlockEntityType<>(AncientWatcherDeadStatueBlockEntity::new, ANCIENT_DEAD_WATCHER.get()));

        // Attachment Types
    public static final Supplier<AttachmentType<TolkienItemStackHandler>> BACKPACK_HANDLER = ATTACHMENT_TYPES.register(
            "backpack_handler", () -> AttachmentType.serializable(holder -> {
                if (holder instanceof BackpackBlockEntity backpackBlockEntity)
                    return new TolkienItemStackHandler(backpackBlockEntity.BUCKET_SLOTS);
                return new TolkienItemStackHandler(2);
            }).build());
    public static final Supplier<AttachmentType<TolkienFluidTank>> BACKPACK_FLUID_HANDLER = ATTACHMENT_TYPES.register(
            "backpack_fluid_handler", () -> AttachmentType.serializable(holder -> {
                if (holder instanceof BackpackBlockEntity backpackBlockEntity)
                    return new TolkienFluidTank(backpackBlockEntity.getMaxMB());
                return new TolkienFluidTank(0);
            }).build());


    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    /** 1.21.4 dropped Properties#lootFrom, so a block that drops like another one names that block's loot table. */
    private static Optional<ResourceKey<LootTable>> blockLoot(String blockName) {
        return Optional.of(ResourceKey.create(Registries.LOOT_TABLE, resLoc("blocks/" + blockName)));
    }

    /**
     * 1.21.4 reads the registry id out of the properties while the block is constructed, so the properties are made
     * here rather than inside the factory: {@code DeferredRegister.Blocks#registerBlock} stamps the id before the
     * factory runs, and its item is registered the same way with the id {@code registerSimpleBlockItem} derives.
     */
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, block, () -> properties);
        ITEMS.registerSimpleBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithoutBlockItem(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
        return BLOCKS.registerBlock(name, block, () -> properties);
    }

    private static DeferredBlock<Block> registerNoItem(String name, Function<BlockBehaviour.Properties, Block> block, BlockBehaviour.Properties properties) {
        return BLOCKS.registerBlock(name, block, () -> properties);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        WOOD_TYPES.register(eventBus);
        ATTACHMENT_TYPES.register(eventBus);
    }
}