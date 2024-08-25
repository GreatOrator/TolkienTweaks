package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class HallowedBlock extends Block {
    public static final MapCodec<HallowedBlock> CODEC = simpleCodec(HallowedBlock::new);

    @Override
    public MapCodec<HallowedBlock> codec() {
        return CODEC;
    }

    public HallowedBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity && ((LivingEntity) entity).isInvertedHealAndHarm()) {
            if(!TolkienMobsConfig.disableFakePlayer) {
                entity.hurt(level.damageSources().dragonBreath(), 1.0F);
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}