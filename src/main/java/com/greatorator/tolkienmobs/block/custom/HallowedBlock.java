package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;

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

    /**
     * Hallowed ground hurts the undead. The config option is meant to spare fake players -
     * deployers and the like - so that a farm built on it is not eaten by its own machine; it was
     * read as a plain off switch instead and skipped the damage for every entity, which meant the
     * block did nothing at all while the option was on, as it is by default.
     */
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity living && living.isInvertedHealAndHarm()) {
            boolean sparedFakePlayer = entity instanceof FakePlayer && TolkienMobsConfig.disableFakePlayer;
            if (!sparedFakePlayer) {
                entity.hurt(level.damageSources().dragonBreath(), 1.0F);
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}