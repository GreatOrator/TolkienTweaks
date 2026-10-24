package com.greatorator.tolkienmobs.entity.npc.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.npc.SouthronEntity;
import com.greatorator.tolkienmobs.entity.npc.render.SouthronRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class SouthronModel extends GeoModel<SouthronEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "merchant/southron");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "merchant/southron");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return SouthronRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(SouthronEntity animatable) {
        return ANIMATIONS;
    }
}
