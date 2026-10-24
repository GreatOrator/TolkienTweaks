package com.greatorator.tolkienmobs.entity.npc.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.npc.OrcEntity;
import com.greatorator.tolkienmobs.entity.npc.render.OrcRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class OrcModel extends GeoModel<OrcEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "merchant/orc");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "merchant/orc");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return OrcRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(OrcEntity animatable) {
        return ANIMATIONS;
    }
}
