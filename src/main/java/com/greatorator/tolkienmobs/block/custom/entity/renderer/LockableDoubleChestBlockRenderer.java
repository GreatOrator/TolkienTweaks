package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableDoubleChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.geckolib.renderer.GeoBlockRenderer;

public class LockableDoubleChestBlockRenderer extends GeoBlockRenderer<LockableDoubleChestBlockEntity> {
    public LockableDoubleChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new LockableDoubleChestBlockModel());
    }
}
