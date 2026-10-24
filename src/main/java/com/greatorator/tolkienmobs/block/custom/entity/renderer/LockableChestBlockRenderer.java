package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.geckolib.renderer.GeoBlockRenderer;

public class LockableChestBlockRenderer extends GeoBlockRenderer<LockableChestBlockEntity> {
    public LockableChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new LockableChestBlockModel());
    }
}
