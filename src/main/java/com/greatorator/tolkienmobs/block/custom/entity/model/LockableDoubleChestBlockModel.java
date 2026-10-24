package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableDoubleChestBlockModel extends GeoModel<LockableDoubleChestBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "block/lockable_double_chest");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_double_chest_block.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "chest");

    @Override
    public ResourceLocation getModelResource(LockableDoubleChestBlockEntity animatable, GeoRenderer<LockableDoubleChestBlockEntity> renderer) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(LockableDoubleChestBlockEntity animatable, GeoRenderer<LockableDoubleChestBlockEntity> renderer) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LockableDoubleChestBlockEntity animatable) {
        return this.animations;
    }
}
