package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableChestBlockModel extends GeoModel<LockableChestBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "block/lockable_chest");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_chest_block.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "chest");

    @Override
    public ResourceLocation getModelResource(LockableChestBlockEntity animatable, GeoRenderer<LockableChestBlockEntity> renderer) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(LockableChestBlockEntity animatable, GeoRenderer<LockableChestBlockEntity> renderer) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LockableChestBlockEntity animatable) {
        return this.animations;
    }
}
