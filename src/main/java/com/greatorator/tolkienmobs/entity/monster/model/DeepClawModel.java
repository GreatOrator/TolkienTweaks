package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.DeepClawEntity;
import com.greatorator.tolkienmobs.entity.monster.render.DeepClawRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The variant texture comes off the render state, which the renderer fills during extraction; the head turn
 * moved to the renderer's {@code adjustModelBonesForRender}, where GeckoLib 5 wants bone work done.
 */
public class DeepClawModel extends GeoModel<DeepClawEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/deepclaw");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/deepclaw");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return DeepClawRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(DeepClawEntity animatable) {
        return ANIMATIONS;
    }
}
