package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienBlock;
import com.greatorator.tolkienmobs.init.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import javax.annotation.Nullable;

public class ArdaPortalBlock extends TolkienBlock implements Portal {
    public static final MapCodec<ArdaPortalBlock> CODEC = simpleCodec(ArdaPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final int AABB_OFFSET = 2;
    protected static final VoxelShape X_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    @Override
    public MapCodec<ArdaPortalBlock> codec() {
        return CODEC;
    }

    public ArdaPortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        switch(state.getValue(AXIS)) {
            case Z:
                return Z_AABB;
            case X:
            default:
                return X_AABB;
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.dimensionType().natural()
                && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                && random.nextInt(2000) < level.getDifficulty().getId()) {
            while (level.getBlockState(pos).is(this)) {
                pos = pos.below();
            }

            if (level.getBlockState(pos).isValidSpawn(level, pos, TolkienEntities.ENTITY_TTM_MORDORORC.get())) {
                Entity entity = TolkienEntities.ENTITY_TTM_MORDORORC.get().spawn(level, pos.above(), MobSpawnType.STRUCTURE);
                if (entity != null) {
                    entity.setPortalCooldown();
                }
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        Direction.Axis direction$axis = facing.getAxis();
        Direction.Axis direction$axis1 = stateIn.getValue(AXIS);
        boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
        return !flag && facingState.getBlock() != this && !(new Size(worldIn, currentPos, direction$axis1)).validatePortal()
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        return entity instanceof Player player
                ? Math.max(
                1,
                level.getGameRules()
                        .getInt(
                                player.getAbilities().invulnerable
                                        ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY
                                        : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY
                        )
        )
                : 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level worldIn, BlockPos pos, RandomSource rand) {
        if (rand.nextInt(100) == 0) {
            worldIn.playLocalSound(
                    (double)pos.getX() + 0.5D,
                    (double)pos.getY() + 0.5D,
                    (double)pos.getZ() + 0.5D,
                    TolkienSounds.ARDA_PORTAL.get(),
                    SoundSource.BLOCKS,
                    0.5F,
                    rand.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        for(int i = 0; i < 4; ++i) {
            double x = (double)pos.getX() + rand.nextDouble();
            double y = (double)pos.getY() + rand.nextDouble();
            double z = (double)pos.getZ() + rand.nextDouble();
            double xSpeed = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double ySpeed = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double zSpeed = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;
            if (!worldIn.getBlockState(pos.west()).is(this) && !worldIn.getBlockState(pos.east()).is(this)) {
                x = (double)pos.getX() + 0.5D + 0.25D * (double)j;
                xSpeed = rand.nextFloat() * 2.0F * (float)j;
            } else {
                z = (double)pos.getZ() + 0.5D + 0.25D * (double)j;
                zSpeed = rand.nextFloat() * 2.0F * (float)j;
            }

            worldIn.addParticle(TolkienParticleTypes.MALLORN_LEAVES.get(), x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        switch(rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch(state.getValue(AXIS)) {
                    case Z:
                        return state.setValue(AXIS, Direction.Axis.X);
                    case X:
                        return state.setValue(AXIS, Direction.Axis.Z);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    public boolean trySpawnPortal(LevelAccessor worldIn, BlockPos pos) {
        ArdaPortalBlock.Size ArdaPortalBlock$size = this.isPortal(worldIn, pos);
        if (ArdaPortalBlock$size != null) {
            ArdaPortalBlock$size.placePortalBlocks();
            return true;
        } else {
            return false;
        }
    }

    public static BlockEvent.PortalSpawnEvent onTrySpawnPortal(LevelAccessor world, BlockPos pos, Size size) {
        return NeoForge.EVENT_BUS.post(new BlockEvent.PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
    }

    @Nullable
    public ArdaPortalBlock.Size isPortal(LevelAccessor worldIn, BlockPos pos) {
        ArdaPortalBlock.Size ArdaPortalBlock$size = new Size(worldIn, pos, Direction.Axis.X);
        if (ArdaPortalBlock$size.isValid() && ArdaPortalBlock$size.portalBlockCount == 0) {
            return ArdaPortalBlock$size;
        } else {
            ArdaPortalBlock.Size ArdaPortalBlock$size1 = new Size(worldIn, pos, Direction.Axis.Z);
            return ArdaPortalBlock$size1.isValid() && ArdaPortalBlock$size1.portalBlockCount == 0 ? ArdaPortalBlock$size1 : null;
        }
    }

    public static class Size extends PortalShape {
        private final LevelAccessor level;
        private final Direction.Axis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private int portalBlockCount;
        @Nullable
        private BlockPos bottomLeft;
        private int height;
        private int width;

        public Size(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
            super(level, pos, axis);
            this.level = level;
            this.axis = axis;
            if (axis == Direction.Axis.X) {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            } else {
                this.leftDir = Direction.NORTH;
                this.rightDir = Direction.SOUTH;
            }

            for(BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 && this.canConnect(level.getBlockState(pos.below())); pos = pos.below()) {
            }

            int i = this.getDistanceUntilEdge(pos, this.leftDir) - 1;
            if (i >= 0) {
                this.bottomLeft = pos.relative(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
                if (this.width < 2 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }

            if (this.bottomLeft != null) {
                this.height = this.calculatePortalHeight();
            }

        }

        protected int getDistanceUntilEdge(BlockPos pos, Direction directionIn) {
            int i;
            for(i = 0; i < 22; ++i) {
                BlockPos blockpos = pos.relative(directionIn, i);
                if(!this.canConnect(this.level.getBlockState(blockpos)) ||
                        !(this.level.getBlockState(blockpos.below()).is(TolkienTags.Blocks.PORTAL_FRAME_BLOCKS))) {
                    break;
                }
            }

            BlockPos framePos = pos.relative(directionIn, i);
            return this.level.getBlockState(framePos).is(TolkienTags.Blocks.PORTAL_FRAME_BLOCKS) ? i : 0;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int calculatePortalHeight() {
            label56:
            for(this.height = 0; this.height < 21; ++this.height) {
                for(int i = 0; i < this.width; ++i) {
                    BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i).above(this.height);
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    if (!this.canConnect(blockstate)) {
                        break label56;
                    }

                    Block block = blockstate.getBlock();
                    if (block == TolkienBlocks.ARDA_PORTAL.get()) {
                        ++this.portalBlockCount;
                    }

                    if (i == 0) {
                        BlockPos framePos = blockpos.relative(this.leftDir);
                        if (!(this.level.getBlockState(framePos).is(TolkienTags.Blocks.PORTAL_FRAME_BLOCKS))) {
                            break label56;
                        }
                    } else if (i == this.width - 1) {
                        BlockPos framePos = blockpos.relative(this.rightDir);
                        if (!(this.level.getBlockState(framePos).is(TolkienTags.Blocks.PORTAL_FRAME_BLOCKS))) {
                            break label56;
                        }
                    }
                }
            }

            for(int j = 0; j < this.width; ++j) {
                BlockPos framePos = this.bottomLeft.relative(this.rightDir, j).above(this.height);
                if (!(this.level.getBlockState(framePos).is(TolkienTags.Blocks.PORTAL_FRAME_BLOCKS))) {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            } else {
                this.bottomLeft = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }

        protected boolean canConnect(BlockState pos) {
            Block block = pos.getBlock();
            return pos.isAir() || block == TolkienBlocks.ARDA_PORTAL.get();
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void placePortalBlocks() {
            for(int i = 0; i < this.width; ++i) {
                BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i);

                for(int j = 0; j < this.height; ++j) {
                    this.level.setBlock(blockpos.above(j), TolkienBlocks.ARDA_PORTAL.get().defaultBlockState().setValue(ArdaPortalBlock.AXIS, this.axis), 18);
                }
            }

        }

        private boolean isPortalCountValidForSize() {
            return this.portalBlockCount >= this.width * this.height;
        }

        public boolean validatePortal() {
            return this.isValid() && this.isPortalCountValidForSize();
        }
    }

    /** Dimension being travelled to */

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
        return null;
    }

//    @Nullable
//    @Override
//    public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
//        ResourceKey<Level> resourcekey = level.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
//        ServerLevel serverlevel = level.getServer().getLevel(resourcekey);
//        if (serverlevel == null) {
//            return null;
//        } else {
//            boolean flag = serverlevel.dimension() == Level.NETHER;
//            WorldBorder worldborder = serverlevel.getWorldBorder();
//            double d0 = DimensionType.getTeleportationScale(level.dimensionType(), serverlevel.dimensionType());
//            BlockPos blockpos = worldborder.clampToBounds(entity.getX() * d0, entity.getY(), entity.getZ() * d0);
//            return this.getExitPortal(serverlevel, entity, pos, blockpos, flag, worldborder);
//        }
//    }
//
//    @Nullable
//    private DimensionTransition getExitPortal(
//            ServerLevel level, Entity entity, BlockPos pos, BlockPos exitPos, boolean isNether, WorldBorder worldBorder
//    ) {
//        Optional<BlockPos> optional = level.getPortalForcer().findClosestPortalPosition(exitPos, isNether, worldBorder);
//        BlockUtil.FoundRectangle blockutil$foundrectangle;
//        DimensionTransition.PostDimensionTransition dimensiontransition$postdimensiontransition;
//        if (optional.isPresent()) {
//            BlockPos blockpos = optional.get();
//            BlockState blockstate = level.getBlockState(blockpos);
//            blockutil$foundrectangle = BlockUtil.getLargestRectangleAround(
//                    blockpos,
//                    blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS),
//                    21,
//                    Direction.Axis.Y,
//                    21,
//                    p_351970_ -> level.getBlockState(p_351970_) == blockstate
//            );
//            dimensiontransition$postdimensiontransition = DimensionTransition.PLAY_PORTAL_SOUND.then(p_351967_ -> p_351967_.placePortalTicket(blockpos));
//        } else {
//            Direction.Axis direction$axis = entity.level().getBlockState(pos).getOptionalValue(AXIS).orElse(Direction.Axis.X);
//            Optional<BlockUtil.FoundRectangle> optional1 = level.getPortalForcer().createPortal(exitPos, direction$axis);
//            if (optional1.isEmpty()) {
//                LOGGER.error("Unable to create a portal, likely target out of worldborder");
//                return null;
//            }
//
//            blockutil$foundrectangle = optional1.get();
//            dimensiontransition$postdimensiontransition = DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET);
//        }
//
//        return getDimensionTransitionFromExit(entity, pos, blockutil$foundrectangle, level, dimensiontransition$postdimensiontransition);
//    }
//
//    private static DimensionTransition getDimensionTransitionFromExit(
//            Entity entity, BlockPos pos, BlockUtil.FoundRectangle rectangle, ServerLevel level, DimensionTransition.PostDimensionTransition postDimensionTransition
//    ) {
//        BlockState blockstate = entity.level().getBlockState(pos);
//        Direction.Axis direction$axis;
//        Vec3 vec3;
//        if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
//            direction$axis = blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS);
//            BlockUtil.FoundRectangle blockutil$foundrectangle = BlockUtil.getLargestRectangleAround(
//                    pos, direction$axis, 21, Direction.Axis.Y, 21, p_351016_ -> entity.level().getBlockState(p_351016_) == blockstate
//            );
//            vec3 = entity.getRelativePortalPosition(direction$axis, blockutil$foundrectangle);
//        } else {
//            direction$axis = Direction.Axis.X;
//            vec3 = new Vec3(0.5, 0.0, 0.0);
//        }
//
//        return createDimensionTransition(
//                level, rectangle, direction$axis, vec3, entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), postDimensionTransition
//        );
//    }
//
//    private static DimensionTransition createDimensionTransition(
//            ServerLevel level,
//            BlockUtil.FoundRectangle rectangle,
//            Direction.Axis axis,
//            Vec3 offset,
//            Entity entity,
//            Vec3 speed,
//            float yRot,
//            float xRot,
//            DimensionTransition.PostDimensionTransition postDimensionTransition
//    ) {
//        BlockPos blockpos = rectangle.minCorner;
//        BlockState blockstate = level.getBlockState(blockpos);
//        Direction.Axis direction$axis = blockstate.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
//        double d0 = (double)rectangle.axis1Size;
//        double d1 = (double)rectangle.axis2Size;
//        EntityDimensions entitydimensions = entity.getDimensions(entity.getPose());
//        int i = axis == direction$axis ? 0 : 90;
//        Vec3 vec3 = axis == direction$axis ? speed : new Vec3(speed.z, speed.y, -speed.x);
//        double d2 = (double)entitydimensions.width() / 2.0 + (d0 - (double)entitydimensions.width()) * offset.x();
//        double d3 = (d1 - (double)entitydimensions.height()) * offset.y();
//        double d4 = 0.5 + offset.z();
//        boolean flag = direction$axis == Direction.Axis.X;
//        Vec3 vec31 = new Vec3((double)blockpos.getX() + (flag ? d2 : d4), (double)blockpos.getY() + d3, (double)blockpos.getZ() + (flag ? d4 : d2));
//        Vec3 vec32 = PortalShape.findCollisionFreePosition(vec31, level, entity, entitydimensions);
//        return new DimensionTransition(level, vec32, vec3, yRot + (float)i, xRot, postDimensionTransition);
//    }
//
//    @Override
//    public Portal.Transition getLocalTransition() {
//        return Portal.Transition.CONFUSION;
//    }
}
