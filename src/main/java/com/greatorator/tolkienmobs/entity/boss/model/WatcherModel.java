package com.greatorator.tolkienmobs.entity.boss.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.boss.WatcherEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The Watcher never moved a bone by hand, so its model is only the three resource paths. */
public class WatcherModel extends GeoModel<WatcherEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "boss/tmwatcher");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmwatcher.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "boss/tmwatcher");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(WatcherEntity animatable) {
        return ANIMATIONS;
    }
}
