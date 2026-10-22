package com.greatorator.tolkienmobs.entity.projectiles.model;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.entity.projectiles.SimpleTrapEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;


public class SimpleTrapModel<T extends SimpleTrapEntity> extends GeoModel<T> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "geo/item/trap.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "textures/entity/web_trap.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "animations/trap.animation.json");

    @Override
    public ResourceLocation getModelResource(T object, GeoRenderer<T> renderer) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(T object, GeoRenderer<T> renderer) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T object) {
        return this.animations;
    }
}