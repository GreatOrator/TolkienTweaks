package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Random;

public class CamoSmokerBlock extends ChameleonBlock {
    public static final MapCodec<CamoSmokerBlock> CODEC = simpleCodec(CamoSmokerBlock::new);

    public CamoSmokerBlock(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState blockState, Level worldIn, BlockPos pos, RandomSource random) {
        double d0 = (double)pos.getX() + 0.5D;
        double d1 = (double)pos.getY();
        double d2 = (double)pos.getZ() + 0.5D;
        worldIn.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, d0 + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), d1 + random.nextDouble() + random.nextDouble(), d2 + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
    }
}
