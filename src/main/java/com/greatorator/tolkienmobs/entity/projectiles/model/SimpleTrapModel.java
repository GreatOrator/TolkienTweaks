package com.greatorator.tolkienmobs.entity.projectiles.model;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.entity.projectiles.SimpleTrapEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;


public class SimpleTrapModel<T extends SimpleTrapEntity> extends GeoModel<T> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "item/trap");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "textures/entity/web_trap.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "trap");

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