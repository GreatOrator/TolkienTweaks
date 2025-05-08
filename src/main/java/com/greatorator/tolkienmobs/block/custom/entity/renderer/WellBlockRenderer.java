package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.entity.WellBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.WellBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WellBlockRenderer extends GeoBlockRenderer<WellBlockEntity> {
    public WellBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WellBlockModel());
    }
}
