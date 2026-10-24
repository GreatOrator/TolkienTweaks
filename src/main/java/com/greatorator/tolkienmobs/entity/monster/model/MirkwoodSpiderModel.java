package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.MirkwoodSpiderEntity;
import com.greatorator.tolkienmobs.entity.monster.render.MirkwoodSpiderRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class MirkwoodSpiderModel extends GeoModel<MirkwoodSpiderEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/mirkwoodspider");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/mirkwoodspider");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return MirkwoodSpiderRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(MirkwoodSpiderEntity animatable) {
        return ANIMATIONS;
    }
}
