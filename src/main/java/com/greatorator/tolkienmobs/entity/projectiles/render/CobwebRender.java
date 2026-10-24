package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.greatorator.tolkienmobs.entity.projectiles.CobwebProjectileEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.CobwebModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;

public class CobwebRender extends GeoEntityRenderer<CobwebProjectileEntity> {
    public CobwebRender(EntityRendererProvider.Context context) {
        super(context, new CobwebModel());
    }
}
