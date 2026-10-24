package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.geckolib.renderer.GeoEntityRenderer;
import com.greatorator.tolkienmobs.entity.projectiles.CobwebProjectileEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.CobwebModel;
import com.greatorator.tolkienmobs.entity.util.TolkienProjectileRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** The web needs no per-frame data, but 5 still asks for the state it fills on the way in. */
public class CobwebRender extends GeoEntityRenderer<CobwebProjectileEntity, TolkienProjectileRenderState> {
    public CobwebRender(EntityRendererProvider.Context context) {
        super(context, new CobwebModel());
    }

    @Override
    public TolkienProjectileRenderState createRenderState(CobwebProjectileEntity animatable, Void relatedObject) {
        return new TolkienProjectileRenderState();
    }
}
