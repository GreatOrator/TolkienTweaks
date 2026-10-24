package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleTreasureChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableDoubleTreasureChestBlockModel extends GeoModel<LockableDoubleTreasureChestBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "block/lockable_double_treasure_chest");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_double_treasure_chest_block.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "chest");

    @Override
    public ResourceLocation getModelResource(LockableDoubleTreasureChestBlockEntity animatable, GeoRenderer<LockableDoubleTreasureChestBlockEntity> renderer) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(LockableDoubleTreasureChestBlockEntity animatable, GeoRenderer<LockableDoubleTreasureChestBlockEntity> renderer) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LockableDoubleTreasureChestBlockEntity animatable) {
        return this.animations;
    }
}
