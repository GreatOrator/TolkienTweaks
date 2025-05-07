package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableDoubleChestBlockModel extends GeoModel<LockableDoubleChestBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/block/lockable_double_chest.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_double_chest_block.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/chest.animation.json");

    @Override
    public ResourceLocation getModelResource(LockableDoubleChestBlockEntity animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(LockableDoubleChestBlockEntity animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LockableDoubleChestBlockEntity animatable) {
        return this.animations;
    }
}
