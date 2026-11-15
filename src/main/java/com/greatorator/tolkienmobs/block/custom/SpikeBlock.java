package com.greatorator.tolkienmobs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A bed of spikes: eight plates stepping up from the full block at the floor to one block of four
 * pixels at the top, which is the shape the models draw. Standing on one is what hurts - the block
 * is in the same family as the cactus and the berry bush, so whoever is inside it takes a little
 * general damage and the game's own hurt timer keeps that to one hit every half second rather than
 * a point every tick.
 * <p>
 * One class for both materials: the stone and the wooden bed share this geometry and differ only in
 * the model that draws them, which reads a different half of the same texture sheet.
 */
public class SpikeBlock extends Block {
    /** Two points of damage a hit, so an unarmoured walker is not caught out by a bed of them. */
    public static final float DAMAGE = 2.0F;

    public static final VoxelShape SHAPE = shape();

    public SpikeBlock(Properties properties) {
        super(properties);
    }

    private static VoxelShape shape() {
        VoxelShape shape = Shapes.empty();
        for (int step = 0; step < 8; step++) {
            shape = Shapes.or(shape, Block.box(step, step, step, 16 - step, step + 1, 16 - step));
        }
        return shape;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        entity.hurt(level.damageSources().generic(), DAMAGE);
    }
}
