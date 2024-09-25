package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class CamoFluidBlock extends ChameleonBlock {
    public static final MapCodec<CamoFluidBlock> CODEC = simpleCodec(CamoFluidBlock::new);

    public CamoFluidBlock(Properties properties) {
        super(properties);
    }
}
