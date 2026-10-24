package com.greatorator.tolkienmobs.entity.boss.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.boss.FellBeastEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The beast wears one texture, so the model needs nothing from the entity; the head turn moved to the
 * renderer's {@code adjustModelBonesForRender}, which is where GeckoLib 5 wants bone work done.
 */
public class FellBeastModel extends GeoModel<FellBeastEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "boss/fellbeast");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/fellbeast/fellbeast.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "boss/fellbeast");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(FellBeastEntity animatable) {
        return ANIMATIONS;
    }
}
