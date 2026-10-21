package com.greatorator.tolkienmobs.block.custom;

import net.minecraft.world.InteractionResult;

import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

// ChameleonBlock's type parameter goes unused; naming it keeps createBlockStateDefinition overridable
// instead of clashing with the raw signature that extending ChameleonBlock bare would produce.
public class CamoSmokerBlock extends ChameleonBlock<Block> {
    public static final MapCodec<CamoSmokerBlock> CODEC = simpleCodec(CamoSmokerBlock::new);

    /** Highest dye stage: 1 is a third of the way to the dye, 3 is the dye itself, 0 is untinted smoke. */
    public static final int MAX_DYE_STAGE = 3;
    /** Which of the sixteen dyes the smoke was last tinted with. */
    public static final IntegerProperty DYE_COLOR = IntegerProperty.create("dye_color", 0, 15);
    /** How far the smoke has been pushed towards {@link #DYE_COLOR}; 0 leaves it the natural colour. */
    public static final IntegerProperty DYE_STAGE = IntegerProperty.create("dye_stage", 0, MAX_DYE_STAGE);

    public CamoSmokerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DYE_COLOR, DYE_STAGE);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(stack.getItem() instanceof DyeItem dyeItem)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        DyeColor color = dyeItem.getDyeColor();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Clicking with the dye already on the block walks it up a stage, and past the third stage back to
        // untinted smoke so there is a way to take the colour off again. A different dye starts over at one.
        int stage = state.getValue(DYE_STAGE);
        boolean sameDye = stage > 0 && state.getValue(DYE_COLOR) == color.getId();
        int newStage = sameDye ? (stage + 1) % (MAX_DYE_STAGE + 1) : 1;
        BlockState dyed = state.setValue(DYE_COLOR, color.getId()).setValue(DYE_STAGE, newStage);

        level.setBlock(pos, dyed, Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(smokeParticle(dyed), pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 6, 0.2D, 0.1D, 0.2D, 0.01D);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * The smoke this block should give off: vanilla's own cosy smoke while it is untinted, and the tinted
     * copy of it once a dye has been applied.
     */
    public static ParticleOptions smokeParticle(BlockState state) {
        int stage = state.getValue(DYE_STAGE);
        if (stage <= 0) {
            return ParticleTypes.CAMPFIRE_COSY_SMOKE;
        }
        return ColorParticleOption.create(TolkienParticleTypes.TINTED_SMOKE.get(), 0xFF000000 | tint(state));
    }

    /** The dye colour of the block, blended away from the smoke's natural white by one third per stage. */
    public static int tint(BlockState state) {
        int dye = DyeColor.byId(state.getValue(DYE_COLOR)).getTextureDiffuseColor();
        float amount = state.getValue(DYE_STAGE) / (float) MAX_DYE_STAGE;
        int red = mix(255, (dye >> 16) & 0xFF, amount);
        int green = mix(255, (dye >> 8) & 0xFF, amount);
        int blue = mix(255, dye & 0xFF, amount);
        return red << 16 | green << 8 | blue;
    }

    private static int mix(int from, int to, float amount) {
        return Math.round(from + (to - from) * amount);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState blockState, Level worldIn, BlockPos pos, RandomSource random) {
        double d0 = (double)pos.getX() + 0.5D;
        double d1 = pos.getY();
        double d2 = (double)pos.getZ() + 0.5D;
        worldIn.addParticle(smokeParticle(blockState), d0 + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), d1 + random.nextDouble() + random.nextDouble(), d2 + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
    }
}
