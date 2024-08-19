package com.greatorator.tolkienmobs.block.custom.entity.layers;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class TolkienModelLayers {
    public static final ModelLayerLocation LIGHTNINGBUG = register("cicada");
    public static final ModelLayerLocation LOCUST = register("locust");

    private static ModelLayerLocation register(String p_171294_) {
        return register(p_171294_, "main");
    }

    private static ModelLayerLocation register(String p_171301_, String p_171302_) {
        return new ModelLayerLocation(TolkienMobsMain.prefix(p_171301_), p_171302_);
    }
}
