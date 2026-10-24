package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.greatorator.tolkienmobs.entity.projectiles.FellBeastFireballEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.FellBeastFireballModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FellBeastFireballRender extends EntityRenderer<FellBeastFireballEntity, ThrownProjectileRenderState> {
    private static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(MODID, "textures/item/ammo/ammo_fellbeast_fireball.png");

    private final FellBeastFireballModel model;

    public FellBeastFireballRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new FellBeastFireballModel(context.bakeLayer(FellBeastFireballModel.LAYER_LOCATION));
    }

    @Override
    public ThrownProjectileRenderState createRenderState() {
        return new ThrownProjectileRenderState();
    }

    @Override
    public void extractRenderState(FellBeastFireballEntity entity, ThrownProjectileRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.grounded = entity.isGrounded();
        state.renderingRotation = entity.getRenderingRotation();
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.groundedPitch = entity.groundedOffset.x;
        state.groundedYaw = entity.groundedOffset.y;
    }

    /**
     * 26.1 collects draws instead of writing them: the pose and the model go to the collector, which
     * submits them in its own pass with the texture, light and overlay it is handed here.
     */
    @Override
    public void submit(ThrownProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();

        if (!state.grounded) {
            poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
            poseStack.mulPose(Axis.XP.rotationDegrees(state.renderingRotation * 5f));
            poseStack.translate(0, -1.0f, 0);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(state.groundedYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(state.groundedPitch));
            poseStack.translate(0, -1.0f, 0);
        }

        submitNodeCollector.submitModel(this.model, state, poseStack, TEXTURE_LOCATION, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}
