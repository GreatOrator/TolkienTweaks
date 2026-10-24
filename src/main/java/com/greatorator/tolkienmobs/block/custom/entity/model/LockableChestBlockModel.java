package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The chest shows one texture, so the model needs nothing from the render state. */
public class LockableChestBlockModel extends GeoModel<LockableChestBlockEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "block/lockable_chest");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/block/chests/lockable_chest_block.png");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "chest");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(LockableChestBlockEntity animatable) {
        return ANIMATIONS;
    }
}
