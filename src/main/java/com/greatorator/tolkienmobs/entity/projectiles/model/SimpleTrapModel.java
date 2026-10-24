package com.greatorator.tolkienmobs.entity.projectiles.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.projectiles.SimpleTrapEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Both trap types share one model and one texture, so the model needs nothing from the render state. */
public class SimpleTrapModel extends GeoModel<SimpleTrapEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "item/trap");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/web_trap.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "trap");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(SimpleTrapEntity animatable) {
        return ANIMATIONS;
    }
}
