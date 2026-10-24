package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.block.custom.entity.WellBlockEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The well shows one texture, so the model needs nothing from the render state. */
public class WellBlockModel extends GeoModel<WellBlockEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "block/well");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/block/custom/well.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "well");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(WellBlockEntity animatable) {
        return ANIMATIONS;
    }
}
