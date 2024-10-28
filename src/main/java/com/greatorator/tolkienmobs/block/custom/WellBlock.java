package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WellBlock extends TolkienBlock {
    public static final MapCodec<WellBlock> CODEC = simpleCodec(WellBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SHAPE_S = Block.box(1.0D, 0.0D, -7.0D, 15.0D, 32.0D, 23.0D);
    protected static final VoxelShape SHAPE_E = Block.box(1.0D, 0.0D, -7.0D, 15.0D, 32.0D, 23.0D);
    protected static final VoxelShape SHAPE_W = Block.box(-7.0D, 0.0D, 1.0D, 23.0D, 32.0D, 15.0D);

    public WellBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighbor, LevelAccessor world, BlockPos pos, BlockPos offset) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = world.getFluidState(pos);
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack filledBottle = PotionContents.createItemStack(Items.POTION, Potions.WATER);
        ItemStack filledBucket = Items.WATER_BUCKET.getDefaultInstance();
        ItemStack resultStack = cleanItem(stack);

        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!resultStack.isEmpty()) {
            final var components = stack.getComponents();
            final var newItem = resultStack.copy();
            newItem.applyComponents(components);

            if (stack.getCount() <= 1) {
                    player.setItemInHand(hand, newItem);
            } else{
                if (player.getInventory().add(newItem)) {
                    stack.shrink(1);
                }
            }
            spawnParticlesAndPlaySound(level, pos, state);
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, level.random.nextFloat() + 0.5f);
            return ItemInteractionResult.SUCCESS;
        }else if (heldItem.getItem() == Items.GLASS_BOTTLE) {
            if (stack.getCount() == 1) {
                player.setItemInHand(hand, filledBottle);
            } else if (player.addItem(filledBottle)) {
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1f, 1f);
        }else if (heldItem.getItem() == Items.BUCKET) {
            if (stack.getCount() == 1) {
                player.setItemInHand(hand, filledBucket);
            } else if (player.addItem(filledBucket)) {
                stack.shrink(1);
            }
            spawnParticlesAndPlaySound(level, pos, state);
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, level.random.nextFloat() + 0.5f);
        } else {
            spawnParticlesAndPlaySound(level, pos, state);
        }
        return ItemInteractionResult.SUCCESS;
    }

    private void spawnParticlesAndPlaySound(Level level, BlockPos pos, BlockState state) {
        float dripWaterX = 0f;
        float dripWaterZ = 0f;
        switch (state.getValue(FACING)) {
            case NORTH -> {
                dripWaterZ = 0.25f;
                dripWaterX = -0.05f;
            }
            case SOUTH -> dripWaterX = 0.25f;
            case WEST -> {
                dripWaterX = 0.25f;
                dripWaterZ = 0.25f;
            }
            case EAST -> dripWaterZ = -0.05f;
        }
        float particleX = (float) pos.getX() + 0.5f;
        float particleY = (float) pos.getY() + 1.25f;
        float particleZ = (float) pos.getZ() + 0.5f;
        level.addParticle(ParticleTypes.SPLASH, (double) particleX + dripWaterX, (double) particleY - 0.45f, (double) particleZ + dripWaterZ, 0, 0, 0);
        for (int i = 0; i < 5; i++) {
            level.addParticle(ParticleTypes.SPLASH,
                    (double) particleX + Math.random() - 0.5f,
                    (double) particleY + Math.random() - 0.5f,
                    (double) particleZ + Math.random() - 0.5f,
                    0,
                    0,
                    0);
        }
        level.playSound(null, pos, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.1f, level.random.nextFloat() + 0.5f);
    }

    public ItemStack cleanItem(ItemStack itemStack) {
        if (itemStack.has(DataComponents.DYED_COLOR)) {
            itemStack.remove(DataComponents.DYED_COLOR);
        }
        return ItemStack.EMPTY;
    }
}
