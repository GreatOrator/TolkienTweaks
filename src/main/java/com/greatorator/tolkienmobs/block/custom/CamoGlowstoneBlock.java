package com.greatorator.tolkienmobs.block.custom;

import com.mojang.serialization.MapCodec;

public class CamoGlowstoneBlock extends ChameleonBlock {
    public static final MapCodec<CamoGlowstoneBlock> CODEC = simpleCodec(CamoGlowstoneBlock::new);

    public CamoGlowstoneBlock(Properties properties) {
        super(properties);
    }
}
