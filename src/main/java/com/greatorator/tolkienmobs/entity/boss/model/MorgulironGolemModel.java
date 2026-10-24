package com.greatorator.tolkienmobs.entity.boss.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.boss.MorgulironGolemEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The morgul-iron golem wears one texture, so the model needs nothing from the entity; the head turn moved
 * to the renderer's {@code adjustModelBonesForRender}, which is where GeckoLib 5 wants bone work done.
 */
public class MorgulironGolemModel extends GeoModel<MorgulironGolemEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/golem");
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/elementalgolem/elemental_golem_morgul.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/golem");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(MorgulironGolemEntity animatable) {
        return ANIMATIONS;
    }
}
