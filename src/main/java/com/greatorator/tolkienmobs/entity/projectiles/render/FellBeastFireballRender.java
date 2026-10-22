package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.greatorator.tolkienmobs.entity.projectiles.FellBeastFireballEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.FellBeastFireballModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FellBeastFireballRender extends EntityRenderer<FellBeastFireballEntity, ThrownProjectileRenderState> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/item/ammo/ammo_fellbeast_fireball.png");

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

    @Override
    public void render(ThrownProjectileRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
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

        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    public ResourceLocation getTextureLocation(ThrownProjectileRenderState state) {
        return TEXTURE_LOCATION;
    }
}
