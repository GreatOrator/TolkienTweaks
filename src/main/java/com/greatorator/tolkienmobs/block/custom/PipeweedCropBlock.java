package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.tags.BlockTags;

import java.util.function.Supplier;

public class PipeweedCropBlock extends CropBlock {
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);

    private final Supplier<ItemLike> seeds;

    public PipeweedCropBlock(Properties properties) {
        this(properties, () -> TolkienItems.PIPEWEED_SEEDS);
    }

    public PipeweedCropBlock(Properties properties, Supplier<ItemLike> seeds) {
        super(properties);
        this.seeds = seeds;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seeds.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }

    /**
     * Pipe-weed is a weed: it takes hold on turf and soil as readily as on a tilled garden bed. That is
     * what lets the wild patches in the Shire stand on the meadow rather than only on farmland. Turf
     * is named beside the soil tag because, in this version, {@code #minecraft:dirt} no longer holds
     * the grass block - which is the one ground a meadow is mostly made of.
     */
    private static boolean isSoil(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(BlockTags.DIRT);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return super.mayPlaceOn(state, level, pos) || isSoil(state);
    }

    /**
     * A crop in this version asks whether it may stand where it is put through {@code canSurvive}
     * rather than through {@link #mayPlaceOn}, so the ground it takes is named in both.
     */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos) || isSoil(level.getBlockState(pos.below()));
    }
}
