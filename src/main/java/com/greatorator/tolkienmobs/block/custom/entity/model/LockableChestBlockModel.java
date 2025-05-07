package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableChestBlockModel extends GeoModel<LockableChestBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/block/lockable_chest.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_chest_block.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/chest.animation.json");

    @Override
    public ResourceLocation getModelResource(LockableChestBlockEntity animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(LockableChestBlockEntity animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LockableChestBlockEntity animatable) {
        return this.animations;
    }
}
