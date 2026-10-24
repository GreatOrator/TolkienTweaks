package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.MimicChestEntity;
import com.greatorator.tolkienmobs.entity.monster.render.MimicChestRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The variant texture comes off the render state, which the renderer fills during extraction; the tongue and
 * legs are hidden by the renderer's {@code adjustModelBonesForRender}, where GeckoLib 5 wants bone work done.
 */
public class MimicChestModel extends GeoModel<MimicChestEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/mimic");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/mimic");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return MimicChestRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(MimicChestEntity animatable) {
        return ANIMATIONS;
    }
}
