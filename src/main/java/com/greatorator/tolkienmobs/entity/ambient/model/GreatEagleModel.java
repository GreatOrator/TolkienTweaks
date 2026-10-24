package com.greatorator.tolkienmobs.entity.ambient.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.ambient.GreatEagleEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class GreatEagleModel extends GeoModel<GreatEagleEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "special/eagle");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/birds/entitygreateagle.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "special/eagle");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(GreatEagleEntity animatable) {
        return ANIMATIONS;
    }
}
