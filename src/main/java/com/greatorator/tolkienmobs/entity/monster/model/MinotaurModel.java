package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.MinotaurEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The minotaur wears one texture, so the model needs nothing from the entity; the head turn moved to the
 * renderer's {@code adjustModelBonesForRender}, which is where GeckoLib 5 wants bone work done.
 */
public class MinotaurModel extends GeoModel<MinotaurEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/minotaur");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/minotaur.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/minotaur");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(MinotaurEntity animatable) {
        return ANIMATIONS;
    }
}
