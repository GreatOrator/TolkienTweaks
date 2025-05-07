package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.LockableDoubleChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableChestBlockModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableDoubleChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LockableDoubleChestBlockRenderer extends GeoBlockRenderer<LockableDoubleChestBlockEntity> {
    public LockableDoubleChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new LockableDoubleChestBlockModel());
    }
}
