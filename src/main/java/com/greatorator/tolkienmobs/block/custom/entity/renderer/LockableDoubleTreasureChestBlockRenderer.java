package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleTreasureChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableDoubleTreasureChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class LockableDoubleTreasureChestBlockRenderer extends GeoBlockRenderer<LockableDoubleTreasureChestBlockEntity, BlockEntityRenderState> {
    public LockableDoubleTreasureChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new LockableDoubleTreasureChestBlockModel());
    }
}
