package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleTreasureChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableDoubleTreasureChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LockableDoubleTreasureChestBlockRenderer extends GeoBlockRenderer<LockableDoubleTreasureChestBlockEntity> {
    public LockableDoubleTreasureChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new LockableDoubleTreasureChestBlockModel());
    }
}
