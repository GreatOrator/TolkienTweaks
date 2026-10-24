package com.greatorator.tolkienmobs.entity.ambient.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.ambient.SquirrelEntity;
import com.greatorator.tolkienmobs.entity.ambient.render.SquirrelRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class SquirrelModel extends GeoModel<SquirrelEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "ambient/squirrel");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "ambient/squirrel");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return SquirrelRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(SquirrelEntity animatable) {
        return ANIMATIONS;
    }
}
