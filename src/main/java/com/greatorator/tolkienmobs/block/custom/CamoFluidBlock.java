package com.greatorator.tolkienmobs.block.custom;

import com.mojang.serialization.MapCodec;

public class CamoFluidBlock extends ChameleonBlock {
    public static final MapCodec<CamoFluidBlock> CODEC = simpleCodec(CamoFluidBlock::new);

    public CamoFluidBlock(Properties properties) {
        super(properties);
    }
}
