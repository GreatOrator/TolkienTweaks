package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableDoubleChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class LockableDoubleChestBlockRenderer extends GeoBlockRenderer<LockableDoubleChestBlockEntity, BlockEntityRenderState> {
    public LockableDoubleChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new LockableDoubleChestBlockModel());
    }
}
