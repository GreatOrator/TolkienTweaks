package com.greatorator.tolkienmobs.entity.passive.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.passive.IstariEntity;
import com.greatorator.tolkienmobs.entity.passive.render.IstariRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class IstariModel extends GeoModel<IstariEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "special/istari");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "special/istari");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return IstariRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(IstariEntity animatable) {
        return ANIMATIONS;
    }
}
