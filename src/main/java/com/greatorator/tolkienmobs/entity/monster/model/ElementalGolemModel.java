package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.ElementalGolemEntity;
import com.greatorator.tolkienmobs.entity.monster.render.ElementalGolemRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The variant texture comes off the render state, which the renderer fills during extraction; the head turn
 * moved to the renderer's {@code adjustModelBonesForRender}, where GeckoLib 5 wants bone work done.
 */
public class ElementalGolemModel extends GeoModel<ElementalGolemEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/golem");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/golem");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ElementalGolemRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(ElementalGolemEntity animatable) {
        return ANIMATIONS;
    }
}
