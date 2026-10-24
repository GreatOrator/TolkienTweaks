package com.greatorator.tolkienmobs.entity.ambient.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.ambient.CrebainEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class CrebainModel extends GeoModel<CrebainEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "ambient/crebain");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/birds/crebain.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "ambient/crebain");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(CrebainEntity animatable) {
        return ANIMATIONS;
    }
}
