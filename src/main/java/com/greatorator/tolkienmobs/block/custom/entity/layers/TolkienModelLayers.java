package com.greatorator.tolkienmobs.block.custom.entity.layers;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class TolkienModelLayers {
    public static final ModelLayerLocation LIGHTNINGBUG = register("cicada");
    public static final ModelLayerLocation LOCUST = register("locust");

    public static final ModelLayerLocation MALLORN_BOAT = register("mallorn_boat");
    public static final ModelLayerLocation MALLORN_CHEST_BOAT = register("mallorn_chest_boat");
    public static final ModelLayerLocation MIRKWOOD_BOAT = register("boat_mirkwood");
    public static final ModelLayerLocation MIRKWOOD_CHEST_BOAT = register("mirkwood_chest_boat");
    public static final ModelLayerLocation CULUMALDA_BOAT = register("boat_culumalda");
    public static final ModelLayerLocation CULUMALDA_CHEST_BOAT = register("chest_boat_culumalda");
    public static final ModelLayerLocation LEBETHRON_BOAT = register("boat_lebethron");
    public static final ModelLayerLocation LEBETHRON_CHEST_BOAT = register("chest_boat_lebethron");
    public static final ModelLayerLocation FANGORNOAK_BOAT = register("boat_fangornoak");
    public static final ModelLayerLocation FANGORNOAK_CHEST_BOAT = register("chest_boat_fangornoak");
    public static final ModelLayerLocation DEADWOOD_BOAT = register("boat_deadwood");
    public static final ModelLayerLocation DEADWOOD_CHEST_BOAT = register("chest_boat_deadwood");
    public static final ModelLayerLocation DWARVEN_MAPLE_BOAT = register("boat_dwarven_maple");
    public static final ModelLayerLocation DWARVEN_MAPLE_CHEST_BOAT = register("chest_boat_dwarven_maple");

    private static ModelLayerLocation register(String p_171294_) {
        return register(p_171294_, "main");
    }

    private static ModelLayerLocation register(String p_171301_, String p_171302_) {
        return new ModelLayerLocation(TolkienMobsMain.prefix(p_171301_), p_171302_);
    }
}
