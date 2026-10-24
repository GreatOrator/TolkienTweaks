package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.WellBlockEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class WellBlockModel extends GeoModel<WellBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "block/well");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/custom/well.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "well");

    @Override
    public ResourceLocation getModelResource(WellBlockEntity animatable, GeoRenderer<WellBlockEntity> renderer) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(WellBlockEntity animatable, GeoRenderer<WellBlockEntity> renderer) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(WellBlockEntity animatable) {
        return this.animations;
    }
}
