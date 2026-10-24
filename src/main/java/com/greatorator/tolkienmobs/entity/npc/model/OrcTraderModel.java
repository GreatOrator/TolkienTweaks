package com.greatorator.tolkienmobs.entity.npc.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.npc.OrcTraderEntity;
import com.greatorator.tolkienmobs.entity.npc.render.OrcTraderRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class OrcTraderModel extends GeoModel<OrcTraderEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "merchant/southron");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "merchant/southron");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return OrcTraderRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(OrcTraderEntity animatable) {
        return ANIMATIONS;
    }
}
