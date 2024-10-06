package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienBlock;
import com.greatorator.tolkienmobs.block.custom.entity.ChameleonBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class CamoGlowstoneBlock extends ChameleonBlock {
    public static final MapCodec<CamoGlowstoneBlock> CODEC = simpleCodec(CamoGlowstoneBlock::new);

    public CamoGlowstoneBlock(Properties properties) {
        super(properties);
    }
}
