package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienBlockTagProvider extends BlockTagsProvider {
    public TolkienBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.LOGS_THAT_BURN)
                .add(TolkienBlocks.LOG_MALLORN.get())
                .add(TolkienBlocks.WOOD_MALLORN.get())
                .add(TolkienBlocks.STRIPPED_MALLORN_LOG.get())
                .add(TolkienBlocks.STRIPPED_MALLORN_WOOD.get())
                .add(TolkienBlocks.LOG_MIRKWOOD.get())
                .add(TolkienBlocks.WOOD_MIRKWOOD.get())
                .add(TolkienBlocks.STRIPPED_MIRKWOOD_LOG.get())
                .add(TolkienBlocks.STRIPPED_MIRKWOOD_WOOD.get())
                .add(TolkienBlocks.LOG_CULUMALDA.get())
                .add(TolkienBlocks.WOOD_CULUMALDA.get())
                .add(TolkienBlocks.STRIPPED_CULUMALDA_LOG.get())
                .add(TolkienBlocks.STRIPPED_CULUMALDA_WOOD.get())
                .add(TolkienBlocks.LOG_LEBETHRON.get())
                .add(TolkienBlocks.WOOD_LEBETHRON.get())
                .add(TolkienBlocks.STRIPPED_LEBETHRON_LOG.get())
                .add(TolkienBlocks.STRIPPED_LEBETHRON_WOOD.get())
                .add(TolkienBlocks.LOG_FANGORNOAK.get())
                .add(TolkienBlocks.WOOD_FANGORNOAK.get())
                .add(TolkienBlocks.STRIPPED_FANGORNOAK_LOG.get())
                .add(TolkienBlocks.STRIPPED_FANGORNOAK_WOOD.get())
                .add(TolkienBlocks.LOG_DEADWOOD.get())
                .add(TolkienBlocks.WOOD_DEADWOOD.get())
                .add(TolkienBlocks.STRIPPED_DEADWOOD_LOG.get())
                .add(TolkienBlocks.STRIPPED_DEADWOOD_WOOD.get())
                .add(TolkienBlocks.LOG_DWARVEN_MAPLE.get())
                .add(TolkienBlocks.WOOD_DWARVEN_MAPLE.get())
                .add(TolkienBlocks.STRIPPED_DWARVEN_MAPLE_LOG.get())
                .add(TolkienBlocks.STRIPPED_DWARVEN_MAPLE_WOOD.get());
        tag(BlockTags.LOGS)
                .add(TolkienBlocks.LOG_MALLORN.get())
                .add(TolkienBlocks.WOOD_MALLORN.get())
                .add(TolkienBlocks.STRIPPED_MALLORN_LOG.get())
                .add(TolkienBlocks.STRIPPED_MALLORN_WOOD.get())
                .add(TolkienBlocks.LOG_MIRKWOOD.get())
                .add(TolkienBlocks.WOOD_MIRKWOOD.get())
                .add(TolkienBlocks.STRIPPED_MIRKWOOD_LOG.get())
                .add(TolkienBlocks.STRIPPED_MIRKWOOD_WOOD.get())
                .add(TolkienBlocks.LOG_CULUMALDA.get())
                .add(TolkienBlocks.WOOD_CULUMALDA.get())
                .add(TolkienBlocks.STRIPPED_CULUMALDA_LOG.get())
                .add(TolkienBlocks.STRIPPED_CULUMALDA_WOOD.get())
                .add(TolkienBlocks.LOG_LEBETHRON.get())
                .add(TolkienBlocks.WOOD_LEBETHRON.get())
                .add(TolkienBlocks.STRIPPED_LEBETHRON_LOG.get())
                .add(TolkienBlocks.STRIPPED_LEBETHRON_WOOD.get())
                .add(TolkienBlocks.LOG_FANGORNOAK.get())
                .add(TolkienBlocks.WOOD_FANGORNOAK.get())
                .add(TolkienBlocks.STRIPPED_FANGORNOAK_LOG.get())
                .add(TolkienBlocks.STRIPPED_FANGORNOAK_WOOD.get())
                .add(TolkienBlocks.LOG_DEADWOOD.get())
                .add(TolkienBlocks.WOOD_DEADWOOD.get())
                .add(TolkienBlocks.STRIPPED_DEADWOOD_LOG.get())
                .add(TolkienBlocks.STRIPPED_DEADWOOD_WOOD.get())
                .add(TolkienBlocks.LOG_DWARVEN_MAPLE.get())
                .add(TolkienBlocks.WOOD_DWARVEN_MAPLE.get())
                .add(TolkienBlocks.STRIPPED_DWARVEN_MAPLE_LOG.get())
                .add(TolkienBlocks.STRIPPED_DWARVEN_MAPLE_WOOD.get());
        tag(BlockTags.STANDING_SIGNS)
                .add(TolkienBlocks.MALLORN_SIGN.get())
                .add(TolkienBlocks.MIRKWOOD_SIGN.get())
                .add(TolkienBlocks.CULUMALDA_SIGN.get())
                .add(TolkienBlocks.LEBETHRON_SIGN.get())
                .add(TolkienBlocks.FANGORNOAK_SIGN.get())
                .add(TolkienBlocks.DEADWOOD_SIGN.get())
                .add(TolkienBlocks.DWARVEN_MAPLE_SIGN.get());
        tag(BlockTags.WALL_SIGNS)
                .add(TolkienBlocks.MALLORN_WALL_SIGN.get())
                .add(TolkienBlocks.MIRKWOOD_WALL_SIGN.get())
                .add(TolkienBlocks.CULUMALDA_SIGN.get())
                .add(TolkienBlocks.LEBETHRON_WALL_SIGN.get())
                .add(TolkienBlocks.FANGORNOAK_WALL_SIGN.get())
                .add(TolkienBlocks.DEADWOOD_WALL_SIGN.get())
                .add(TolkienBlocks.DWARVEN_MAPLE_WALL_SIGN.get());
        tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(TolkienBlocks.MALLORN_HANGING_SIGN.get())
                .add(TolkienBlocks.MIRKWOOD_HANGING_SIGN.get())
                .add(TolkienBlocks.CULUMALDA_HANGING_SIGN.get())
                .add(TolkienBlocks.LEBETHRON_HANGING_SIGN.get())
                .add(TolkienBlocks.FANGORNOAK_HANGING_SIGN.get())
                .add(TolkienBlocks.DEADWOOD_HANGING_SIGN.get())
                .add(TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN.get());
        tag(BlockTags.WALL_HANGING_SIGNS)
                .add(TolkienBlocks.MALLORN_HANGING_WALL_SIGN.get())
                .add(TolkienBlocks.MIRKWOOD_HANGING_WALL_SIGN.get())
                .add(TolkienBlocks.CULUMALDA_HANGING_WALL_SIGN.get())
                .add(TolkienBlocks.LEBETHRON_HANGING_WALL_SIGN.get())
                .add(TolkienBlocks.FANGORNOAK_HANGING_WALL_SIGN.get())
                .add(TolkienBlocks.DEADWOOD_HANGING_WALL_SIGN.get())
                .add(TolkienBlocks.DWARVEN_MAPLE_HANGING_WALL_SIGN.get());
        tag(BlockTags.CLIMBABLE)
                .add(TolkienBlocks.LADDER_MALLORN.get())
                .add(TolkienBlocks.LADDER_MIRKWOOD.get())
                .add(TolkienBlocks.LADDER_CULUMALDA.get())
                .add(TolkienBlocks.LADDER_LEBETHRON.get())
                .add(TolkienBlocks.LADDER_FANGORNOAK.get())
                .add(TolkienBlocks.LADDER_DEADWOOD.get())
                .add(TolkienBlocks.LADDER_DWARVEN_MAPLE.get());
        tag(BlockTags.FENCES)
                .add(TolkienBlocks.FENCE_MALLORN.get())
                .add(TolkienBlocks.FENCE_MIRKWOOD.get())
                .add(TolkienBlocks.FENCE_CULUMALDA.get())
                .add(TolkienBlocks.FENCE_LEBETHRON.get())
                .add(TolkienBlocks.FENCE_FANGORNOAK.get())
                .add(TolkienBlocks.FENCE_DEADWOOD.get())
                .add(TolkienBlocks.FENCE_DWARVEN_MAPLE.get());
        tag(BlockTags.WOODEN_FENCES)
                .add(TolkienBlocks.FENCE_MALLORN.get())
                .add(TolkienBlocks.FENCE_MIRKWOOD.get())
                .add(TolkienBlocks.FENCE_CULUMALDA.get())
                .add(TolkienBlocks.FENCE_LEBETHRON.get())
                .add(TolkienBlocks.FENCE_FANGORNOAK.get())
                .add(TolkienBlocks.FENCE_DEADWOOD.get())
                .add(TolkienBlocks.FENCE_DWARVEN_MAPLE.get());
        tag(BlockTags.FENCE_GATES)
                .add(TolkienBlocks.FENCE_GATE_MALLORN.get())
                .add(TolkienBlocks.FENCE_GATE_MIRKWOOD.get())
                .add(TolkienBlocks.FENCE_GATE_CULUMALDA.get())
                .add(TolkienBlocks.FENCE_GATE_LEBETHRON.get())
                .add(TolkienBlocks.FENCE_GATE_FANGORNOAK.get())
                .add(TolkienBlocks.FENCE_GATE_DEADWOOD.get())
                .add(TolkienBlocks.FENCE_DWARVEN_MAPLE.get());
        tag(BlockTags.WALLS)
                .add(TolkienBlocks.WALL_MITHRIL.get())
                .add(TolkienBlocks.WALL_MORGULIRON.get())
                .add(TolkienBlocks.WALL_CHISELED_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_COBBLED_DARK_STONE.get())
                .add(TolkienBlocks.WALL_CRACKED_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_DARK_STONE.get())
                .add(TolkienBlocks.WALL_SMOOTH_DARK_STONE.get())
                .add(TolkienBlocks.WALL_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_CHISELED_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_COBBLED_DWARVEN_STONE.get())
                .add(TolkienBlocks.WALL_CRACKED_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_DWARVEN_STONE.get())
                .add(TolkienBlocks.WALL_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_SMOOTH_DWARVEN_STONE.get())
                .add(TolkienBlocks.WALL_ELVEN_MARBLE.get())
                .add(TolkienBlocks.WALL_SMOOTH_ELVEN_MARBLE.get())
                .add(TolkienBlocks.WALL_ELVEN_MARBLE_BRICKS.get())
                .add(TolkienBlocks.WALL_MOUNTAIN_STONE.get())
                .add(TolkienBlocks.WALL_SMOOTH_MOUNTAIN_STONE.get())
                .add(TolkienBlocks.WALL_MOUNTAIN_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_ELVEN_MARBLE.get())
                .add(TolkienBlocks.WALL_COBBLED_ELVEN_MARBLE.get())
                .add(TolkienBlocks.WALL_SMOOTH_ELVEN_MARBLE.get())
                .add(TolkienBlocks.WALL_ELVEN_MARBLE_BRICKS.get())
                .add(TolkienBlocks.WALL_CHISELED_ELVEN_MARBLE_BRICKS.get())
                .add(TolkienBlocks.WALL_CRACKED_ELVEN_MARBLE_BRICKS.get())
                .add(TolkienBlocks.WALL_MOUNTAIN_STONE.get())
                .add(TolkienBlocks.WALL_COBBLED_MOUNTAIN_STONE.get())
                .add(TolkienBlocks.WALL_SMOOTH_MOUNTAIN_STONE.get())
                .add(TolkienBlocks.WALL_MOUNTAIN_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_CHISELED_MOUNTAIN_STONE_BRICKS.get())
                .add(TolkienBlocks.WALL_CRACKED_MOUNTAIN_STONE_BRICKS.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(TolkienBlocks.PRESSURE_PLATE_MALLORN.get())
                .add(TolkienBlocks.PRESSURE_PLATE_MIRKWOOD.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CULUMALDA.get())
                .add(TolkienBlocks.PRESSURE_PLATE_LEBETHRON.get())
                .add(TolkienBlocks.PRESSURE_PLATE_FANGORNOAK.get())
                .add(TolkienBlocks.PRESSURE_PLATE_DEADWOOD.get())
                .add(TolkienBlocks.PRESSURE_PLATE_DWARVEN_MAPLE.get());
        tag(BlockTags.PRESSURE_PLATES)
                .add(TolkienBlocks.PRESSURE_PLATE_MITHRIL.get())
                .add(TolkienBlocks.PRESSURE_PLATE_MORGULIRON.get())
                .add(TolkienBlocks.PRESSURE_PLATE_DARK_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_COBBLED_DARK_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_SMOOTH_DARK_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CRACKED_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CHISELED_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CHISELED_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_COBBLED_DWARVEN_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CRACKED_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_SMOOTH_DWARVEN_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_ELVEN_MARBLE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_COBBLED_ELVEN_MARBLE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_SMOOTH_ELVEN_MARBLE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_ELVEN_MARBLE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CRACKED_ELVEN_MARBLE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CHISELED_ELVEN_MARBLE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_MOUNTAIN_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_COBBLED_MOUNTAIN_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_SMOOTH_MOUNTAIN_STONE.get())
                .add(TolkienBlocks.PRESSURE_PLATE_MOUNTAIN_STONE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CRACKED_MOUNTAIN_STONE_BRICKS.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CHISELED_MOUNTAIN_STONE_BRICKS.get());
        tag(BlockTags.DOORS)
                .add(TolkienBlocks.DOOR_MITHRIL.get())
                .add(TolkienBlocks.DOOR_MORGULIRON.get())
                .add(TolkienBlocks.DOOR_DURIN.get());
        tag(BlockTags.WOODEN_DOORS)
                .add(TolkienBlocks.DOOR_MALLORN.get())
                .add(TolkienBlocks.DOOR_MIRKWOOD.get())
                .add(TolkienBlocks.DOOR_CULUMALDA.get())
                .add(TolkienBlocks.DOOR_LEBETHRON.get())
                .add(TolkienBlocks.DOOR_FANGORNOAK.get())
                .add(TolkienBlocks.DOOR_DEADWOOD.get())
                .add(TolkienBlocks.DOOR_DWARVEN_MAPLE.get());
        tag(BlockTags.PLANKS)
                .add(TolkienBlocks.PLANKS_MALLORN.get())
                .add(TolkienBlocks.PLANKS_MIRKWOOD.get())
                .add(TolkienBlocks.PLANKS_CULUMALDA.get())
                .add(TolkienBlocks.PLANKS_LEBETHRON.get())
                .add(TolkienBlocks.PLANKS_FANGORNOAK.get())
                .add(TolkienBlocks.PLANKS_DEADWOOD.get())
                .add(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get());
        tag(BlockTags.WALL_POST_OVERRIDE)
                .add(TolkienBlocks.TORCH_MALLORN.get())
                .add(TolkienBlocks.TORCH_MIRKWOOD.get())
                .add(TolkienBlocks.TORCH_CULUMALDA.get())
                .add(TolkienBlocks.TORCH_LEBETHRON.get())
                .add(TolkienBlocks.TORCH_FANGORNOAK.get())
                .add(TolkienBlocks.TORCH_DEADWOOD.get())
                .add(TolkienBlocks.TORCH_DWARVEN_MAPLE.get())
                .add(TolkienBlocks.WALL_TORCH_MALLORN.get())
                .add(TolkienBlocks.WALL_TORCH_MIRKWOOD.get())
                .add(TolkienBlocks.WALL_TORCH_CULUMALDA.get())
                .add(TolkienBlocks.WALL_TORCH_LEBETHRON.get())
                .add(TolkienBlocks.WALL_TORCH_FANGORNOAK.get())
                .add(TolkienBlocks.WALL_TORCH_DEADWOOD.get())
                .add(TolkienBlocks.WALL_TORCH_DWARVEN_MAPLE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(TolkienBlocks.BLOCK_MITHRIL.get())
                .add(TolkienBlocks.ORE_MITHRIL.get())
                .add(TolkienBlocks.ORE_NETHER_MITHRIL.get())
                .add(TolkienBlocks.ORE_DEEPSLATE_MITHRIL.get())
                .add(TolkienBlocks.ORE_END_MITHRIL.get())
                .add(TolkienBlocks.BLOCK_MORGULIRON.get())
                .add(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.COBBLED_DARK_STONE.get())
                .add(TolkienBlocks.COBBLED_DWARVEN_STONE.get())
                .add(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.DARK_STONE.get())
                .add(TolkienBlocks.DWARVEN_STONE.get())
                .add(TolkienBlocks.DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.SMOOTH_DARK_STONE.get())
                .add(TolkienBlocks.SMOOTH_DWARVEN_STONE.get());
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(TolkienBlocks.ORE_MITHRIL.get())
                .add(TolkienBlocks.ORE_NETHER_MITHRIL.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(TolkienBlocks.ORE_DEEPSLATE_MITHRIL.get())
                .add(TolkienBlocks.ORE_END_MITHRIL.get())
                .add(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.COBBLED_DARK_STONE.get())
                .add(TolkienBlocks.COBBLED_DWARVEN_STONE.get())
                .add(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.DARK_STONE.get())
                .add(TolkienBlocks.DWARVEN_STONE.get())
                .add(TolkienBlocks.DARK_STONE_BRICKS.get())
                .add(TolkienBlocks.DWARVEN_STONE_BRICKS.get())
                .add(TolkienBlocks.SMOOTH_DARK_STONE.get())
                .add(TolkienBlocks.SMOOTH_DWARVEN_STONE.get());
        tag(BlockTags.BASE_STONE_OVERWORLD)
                .add(TolkienBlocks.DARK_STONE.get())
                .add(TolkienBlocks.DWARVEN_STONE.get())
                .add(TolkienBlocks.MOUNTAIN_STONE.get())
                .add(TolkienBlocks.ELVEN_MARBLE.get());
        tag(TolkienTags.Blocks.ROOT_TRACE_SKIP)
                .addTag(BlockTags.LOGS)
                .add(TolkienBlocks.LIVING_ROOTS.get())
                .addTag(BlockTags.FEATURES_CANNOT_REPLACE);
        tag(TolkienTags.Blocks.DECAY_GROW_BLOCK)
                .add(Blocks.MYCELIUM)
                .add(Blocks.PODZOL)
                .add(Blocks.CRIMSON_NYLIUM)
                .add(Blocks.WARPED_NYLIUM)
                .add(Blocks.COBBLESTONE)
                .add(Blocks.STONE);
        tag(TolkienTags.Blocks.BLACKLIST_HARVEST);
        tag(TolkienTags.Blocks.PORTAL_FRAME_BLOCKS)
                .add(TolkienBlocks.BLOCK_AMMOLITE.get());
        tag(TolkienTags.Blocks.PLANTS_HANG_ON)
                .addTag(BlockTags.DIRT)
                .add(Blocks.MOSS_BLOCK, TolkienBlocks.LIVING_ROOTS.get());
        tag(TolkienTags.Blocks.TICK_SPEED_DENY);
    }
}
