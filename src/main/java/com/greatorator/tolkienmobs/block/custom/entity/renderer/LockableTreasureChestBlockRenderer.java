package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.LockableTreasureChestBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableChestBlockModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.LockableTreasureChestBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LockableTreasureChestBlockRenderer extends GeoBlockRenderer<LockableTreasureChestBlockEntity> {
    public LockableTreasureChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new LockableTreasureChestBlockModel());
    }
}
