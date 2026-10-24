package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableTreasureChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableTreasureChestBlockModel extends GeoModel<LockableTreasureChestBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "block/lockable_treasure_chest");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_treasure_chest_block.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "chest");

    @Override
    public ResourceLocation getModelResource(LockableTreasureChestBlockEntity animatable, GeoRenderer<LockableTreasureChestBlockEntity> renderer) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(LockableTreasureChestBlockEntity animatable, GeoRenderer<LockableTreasureChestBlockEntity> renderer) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LockableTreasureChestBlockEntity animatable) {
        return this.animations;
    }
}
