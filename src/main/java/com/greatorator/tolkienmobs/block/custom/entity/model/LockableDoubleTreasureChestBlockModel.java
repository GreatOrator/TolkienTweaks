package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleTreasureChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableDoubleTreasureChestBlockModel extends GeoModel<LockableDoubleTreasureChestBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/block/lockable_double_treasure_chest.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_double_treasure_chest_block.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/chest.animation.json");

    @Override
    public ResourceLocation getModelResource(LockableDoubleTreasureChestBlockEntity animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(LockableDoubleTreasureChestBlockEntity animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LockableDoubleTreasureChestBlockEntity animatable) {
        return this.animations;
    }
}
