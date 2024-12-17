package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienEntityBlock;
import com.greatorator.tolkienmobs.block.custom.entity.FireplaceBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FireplaceBlock extends TolkienEntityBlock {
    public static final Component TextComponent = Component.translatable("screen.tolkienmobs.block.tolkienmobs.fireplace");
    public static final MapCodec<FireplaceBlock> CODEC = RecordCodecBuilder.mapCodec(
            codec -> codec.group(
                            Codec.BOOL.fieldOf("spawn_particles").forGetter(getter -> getter.spawnParticles),
                            propertiesCodec()
                    )
                    .apply(codec, FireplaceBlock::new)
    );
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private final boolean spawnParticles;

    protected static final VoxelShape SHAPE_NORTH = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_EAST = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_WEST = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_COMMON = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);

    public FireplaceBlock(boolean spawnParticles, Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
        this.spawnParticles = spawnParticles;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        switch(state.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_COMMON;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    /* Entity Stuff */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FireplaceBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (worldIn.getBlockEntity(pPos) instanceof FireplaceBlockEntity blockEntity) {
                blockEntity.drops();
            }
        }

        super.onRemove(state, worldIn, pPos, newState, isMoving);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if (!level.isClientSide())
        {
            if (level.getBlockEntity(blockPos) instanceof FireplaceBlockEntity fireplace)
            {
                player.openMenu(fireplace, blockPos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return pState.getValue(LIT) ? createTickerHelper(pBlockEntityType, TolkienBlocks.FIREPLACE_BLOCK_ENTITY.get(), FireplaceBlockEntity::particleTick) : null;
        } else {
            return createTickerHelper(pBlockEntityType, TolkienBlocks.FIREPLACE_BLOCK_ENTITY.get(),
                    (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
//        if (state.getValue(LIT)) {
            if (random.nextInt(10) == 0) {
                level.playLocalSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        0.5F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.6F,
                        false
                );
            }

            if (this.spawnParticles && random.nextInt(5) == 0) {
                for (int i = 0; i < random.nextInt(1) + 1; i++) {
                    level.addParticle(
                            ParticleTypes.LAVA,
                            (double)pos.getX() + 0.5,
                            (double)pos.getY() + 0.5,
                            (double)pos.getZ() + 0.5,
                            (double)(random.nextFloat() / 2.0F),
                            5.0E-5,
                            (double)(random.nextFloat() / 2.0F)
                    );
                }
            }
            SimpleParticleType simpleparticletype = ParticleTypes.CAMPFIRE_COSY_SMOKE;
            level.addAlwaysVisibleParticle(
                    simpleparticletype,
                    true,
                    (double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
                    (double)pos.getY() + random.nextDouble() + random.nextDouble(),
                    (double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
                    0.0,
                    0.07,
                    0.0
            );
//        }
    }

    public static void makeParticles(Level level, BlockPos pos, boolean isSignalFire, boolean spawnExtraSmoke) {
        RandomSource randomsource = level.getRandom();
        SimpleParticleType simpleparticletype = isSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        level.addAlwaysVisibleParticle(
                simpleparticletype,
                true,
                (double)pos.getX() + 0.5 + randomsource.nextDouble() / 3.0 * (double)(randomsource.nextBoolean() ? 1 : -1),
                (double)pos.getY() + randomsource.nextDouble() + randomsource.nextDouble(),
                (double)pos.getZ() + 0.5 + randomsource.nextDouble() / 3.0 * (double)(randomsource.nextBoolean() ? 1 : -1),
                0.0,
                0.07,
                0.0
        );
        if (spawnExtraSmoke) {
            level.addParticle(
                    ParticleTypes.SMOKE,
                    (double)pos.getX() + 0.5 + randomsource.nextDouble() / 4.0 * (double)(randomsource.nextBoolean() ? 1 : -1),
                    (double)pos.getY() + 0.4,
                    (double)pos.getZ() + 0.5 + randomsource.nextDouble() / 4.0 * (double)(randomsource.nextBoolean() ? 1 : -1),
                    0.0,
                    0.005,
                    0.0
            );
        }
    }
}