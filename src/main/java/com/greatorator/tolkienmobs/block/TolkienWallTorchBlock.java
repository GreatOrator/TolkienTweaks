package com.greatorator.tolkienmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class TolkienWallTorchBlock  extends WallTorchBlock {

    Supplier<SimpleParticleType> particleType;
    public TolkienWallTorchBlock(Supplier<SimpleParticleType> particleType, Properties pProperties) {
        super(null, pProperties);
        this.particleType = particleType;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if(this.flameParticle == null){
            this.flameParticle = this.particleType.get();
        }
        super.animateTick(pState, pLevel, pPos, pRandom);
    }
}