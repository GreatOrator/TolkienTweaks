package com.greatorator.tolkienmobs.entity.passive.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.passive.GollumEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class GollumModel extends GeoModel<GollumEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "special/gollum");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/gollum.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "special/gollum");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(GollumEntity animatable) {
        return ANIMATIONS;
    }
}
