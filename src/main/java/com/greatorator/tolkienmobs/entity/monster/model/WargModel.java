package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.WargEntity;
import com.greatorator.tolkienmobs.entity.monster.render.WargRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The variant texture comes off the render state, which the renderer fills during extraction; the head turn
 * moved to the renderer's {@code adjustModelBonesForRender}, where GeckoLib 5 wants bone work done.
 */
public class WargModel extends GeoModel<WargEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/warg");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/warg");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return WargRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(WargEntity animatable) {
        return ANIMATIONS;
    }
}
