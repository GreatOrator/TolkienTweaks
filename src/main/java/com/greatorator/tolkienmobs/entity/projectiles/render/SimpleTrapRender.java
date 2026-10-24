package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.projectiles.SimpleTrapEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.SimpleTrapModel;
import com.greatorator.tolkienmobs.entity.util.TolkienProjectileRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * The trap used to scale itself in {@code preRender}, which 5 replaced with a hook over the render
 * state: the life time it needed is captured during extraction, because the entity is gone by the
 * time the model is drawn.
 */
public class SimpleTrapRender extends GeoEntityRenderer<SimpleTrapEntity, TolkienProjectileRenderState> {
    public SimpleTrapRender(EntityRendererProvider.Context context) {
        super(context, new SimpleTrapModel());
    }

    @Override
    public TolkienProjectileRenderState createRenderState(SimpleTrapEntity animatable, Void relatedObject) {
        return new TolkienProjectileRenderState();
    }

    @Override
    public void addRenderData(SimpleTrapEntity animatable, Void relatedObject, TolkienProjectileRenderState renderState, float partialTick) {
        renderState.lifeTime = animatable.lifeTime;
    }

    /** The web pops into being: on its first two ticks the old renderer scaled it away entirely. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienProjectileRenderState> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.renderState().lifeTime <= 1 ? 0.0F : 2.0F;

        super.scaleModelForRender(renderPassInfo, widthScale * scale, heightScale * scale);
    }

    /** The web is translucent; 26.1 folded the old item translucent cull type into this one. */
    @Override
    public RenderType getRenderType(TolkienProjectileRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
