package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.TrollEntity;
import com.greatorator.tolkienmobs.entity.monster.render.TrollRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The variant texture comes off the render state, which the renderer fills during extraction; the head turn
 * moved to the renderer's adjustModelBonesForRender, where GeckoLib 5 wants bone work done.
 */
public class TrollModel extends GeoModel<TrollEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/troll");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/base");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TrollRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(TrollEntity animatable) {
        return ANIMATIONS;
    }
}
