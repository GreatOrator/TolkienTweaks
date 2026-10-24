package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.WellBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.model.WellBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

/**
 * The well draws from constants, so it uses 5's own block render state; a renderer only needs a state
 * class of its own when a hook of its own has to read something 5 did not put on the default one.
 * <p>
 * The constructor also takes the provider's context now: the renderer keeps the block model resolver
 * for the crumbling overlay, so GeckoLib has to be handed it at registration.
 */
public class WellBlockRenderer extends GeoBlockRenderer<WellBlockEntity, BlockEntityRenderState> {
    public WellBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new WellBlockModel());
    }
}
