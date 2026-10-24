package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.projectiles.TornadoEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.TornadoModel;
import com.greatorator.tolkienmobs.entity.util.TolkienProjectileRenderState;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * The funnel used to scale and turn itself in {@code preRender}. 5 splits that in two: the scale is a
 * hook over the render state, and the blast's turn is a pose adjustment, since the state is the only
 * thing left to read the funnel's angle from.
 */
public class TornadoRender extends GeoEntityRenderer<TornadoEntity, TolkienProjectileRenderState> {
    public TornadoRender(EntityRendererProvider.Context context) {
        super(context, new TornadoModel());
    }

    @Override
    public TolkienProjectileRenderState createRenderState(TornadoEntity animatable, Void relatedObject) {
        return new TolkienProjectileRenderState();
    }

    @Override
    public void addRenderData(TornadoEntity animatable, Void relatedObject, TolkienProjectileRenderState renderState, float partialTick) {
        renderState.lifeTime = animatable.lifeTime;
        renderState.blast = animatable.isBlast();
        renderState.yRot = animatable.getYRot(partialTick);
    }

    /** A blast leaves the hand at full size, the ground swirl is a quarter larger again; neither shows on its first tick. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienProjectileRenderState> renderPassInfo, float widthScale, float heightScale) {
        TolkienProjectileRenderState renderState = renderPassInfo.renderState();
        float scale = renderState.lifeTime <= 1 ? 0.0F : renderState.blast ? 1.0F : 1.25F;

        super.scaleModelForRender(renderPassInfo, widthScale * scale, heightScale * scale);
    }

    /** The blast's funnel faces the way it was cast. */
    @Override
    public void adjustRenderPose(RenderPassInfo<TolkienProjectileRenderState> renderPassInfo) {
        if (renderPassInfo.renderState().blast) {
            renderPassInfo.poseStack().mulPose(Axis.YP.rotationDegrees(renderPassInfo.renderState().yRot * Mth.DEG_TO_RAD));
        }
    }

    /** The funnel is translucent; 26.1 folded the old item translucent cull type into this one. */
    @Override
    public RenderType getRenderType(TolkienProjectileRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
