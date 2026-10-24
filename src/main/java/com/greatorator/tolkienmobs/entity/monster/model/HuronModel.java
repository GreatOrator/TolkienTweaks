package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.HuronEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The huron wears one texture, so the model needs nothing from the entity; the head turn moved to the
 * renderer's {@code adjustModelBonesForRender}, which is where GeckoLib 5 wants bone work done.
 */
public class HuronModel extends GeoModel<HuronEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/huron");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/huron.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/huron");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(HuronEntity animatable) {
        return ANIMATIONS;
    }
}
