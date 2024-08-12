package com.greatorator.tolkienmobs.init;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
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

    /* Wood & Foliage */
        // Mallorn
    public static final DeferredBlock<Block> PLANKS_MALLORN = registerBlock("planks_mallorn",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_MALLORN = registerBlock("leaves_mallorn",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
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

        // Mirkwood
    public static final DeferredBlock<Block> PLANKS_MIRKWOOD = registerBlock("planks_mirkwood",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_MIRKWOOD = registerBlock("leaves_mirkwood",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));

        // Culumalda
    public static final DeferredBlock<Block> PLANKS_CULUMALDA = registerBlock("planks_culumalda",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_CULUMALDA = registerBlock("leaves_culumalda",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));

        // Lebethron
    public static final DeferredBlock<Block> PLANKS_LEBETHRON = registerBlock("planks_lebethron",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_LEBETHRON = registerBlock("leaves_lebethron",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));

        // Fangorn Oak
    public static final DeferredBlock<Block> PLANKS_FANGORNOAK = registerBlock("planks_fangornoak",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LEAVES_FANGORNOAK = registerBlock("leaves_fangornoak",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).randomTicks().noOcclusion().ignitedByLava().isSuffocating(TolkienBlocks::never).pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));

        // Deadwood
    public static final DeferredBlock<Block> PLANKS_DEADWOOD = registerBlock("planks_deadwood",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F).requiresCorrectToolForDrops().ignitedByLava().noOcclusion().sound(SoundType.WOOD)));


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