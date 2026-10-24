package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.RockGolemEntity;
import com.greatorator.tolkienmobs.entity.monster.render.RockGolemRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * GeckoLib 5 hands the model a render state rather than the entity, so neither resource getter can ask the
 * golem anything. What it needs for its texture - the variant - is put on the state by the renderer's
 * {@code addRenderData}; the head bone is turned by the renderer's {@code adjustModelBonesForRender}, which
 * is where 5 wants that done.
 */
public class RockGolemModel extends GeoModel<RockGolemEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/rockgolem");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/rockgolem");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return RockGolemRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(RockGolemEntity animatable) {
        return ANIMATIONS;
    }
}
