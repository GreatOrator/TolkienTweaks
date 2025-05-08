package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableTreasureChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableTreasureChestBlockModel extends GeoModel<LockableTreasureChestBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/block/lockable_treasure_chest.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_treasure_chest_block.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/chest.animation.json");

    @Override
    public ResourceLocation getModelResource(LockableTreasureChestBlockEntity animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(LockableTreasureChestBlockEntity animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LockableTreasureChestBlockEntity animatable) {
        return this.animations;
    }
}
