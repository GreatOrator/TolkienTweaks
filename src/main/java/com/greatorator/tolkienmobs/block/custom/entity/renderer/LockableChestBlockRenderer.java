package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class LockableChestBlockRenderer extends GeoBlockRenderer<LockableChestBlockEntity, BlockEntityRenderState> {
    public LockableChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new LockableChestBlockModel());
    }
}
