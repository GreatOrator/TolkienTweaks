package com.greatorator.tolkienmobs.entity.boss.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.boss.GwahirEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Gwaihir shares the eagle's model and texture, so the model needs nothing from the entity; the crown he
 * shows where the plain eagle hides one is left alone, and the neck turn moved to the renderer.
 */
public class GwahirModel extends GeoModel<GwahirEntity> {
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
    public Identifier getAnimationResource(GwahirEntity animatable) {
        return ANIMATIONS;
    }
}
