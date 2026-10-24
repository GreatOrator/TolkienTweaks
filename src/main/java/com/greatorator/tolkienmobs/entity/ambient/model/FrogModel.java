package com.greatorator.tolkienmobs.entity.ambient.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.ambient.FrogEntity;
import com.greatorator.tolkienmobs.entity.ambient.render.FrogRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class FrogModel extends GeoModel<FrogEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "ambient/frog");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "ambient/frog");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return FrogRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(FrogEntity animatable) {
        return ANIMATIONS;
    }
}
