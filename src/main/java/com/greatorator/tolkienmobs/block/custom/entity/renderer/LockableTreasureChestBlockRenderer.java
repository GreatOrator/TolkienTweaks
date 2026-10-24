package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.LockableTreasureChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableTreasureChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class LockableTreasureChestBlockRenderer extends GeoBlockRenderer<LockableTreasureChestBlockEntity, BlockEntityRenderState> {
    public LockableTreasureChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new LockableTreasureChestBlockModel());
    }
}
