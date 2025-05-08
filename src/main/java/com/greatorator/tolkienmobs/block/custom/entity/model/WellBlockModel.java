package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.WellBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class WellBlockModel extends GeoModel<WellBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/block/well.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/custom/well.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/well.animation.json");

    @Override
    public ResourceLocation getModelResource(WellBlockEntity animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(WellBlockEntity animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(WellBlockEntity animatable) {
        return this.animations;
    }
}
