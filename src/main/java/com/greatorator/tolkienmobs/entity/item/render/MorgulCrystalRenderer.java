package com.greatorator.tolkienmobs.entity.item.render;

import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.entity.item.MorgulCrystalEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@OnlyIn(Dist.CLIENT)
public class MorgulCrystalRenderer extends EntityRenderer<MorgulCrystalEntity, MorgulCrystalRenderState> {
   private static final ResourceLocation MORGUL_CRYSTAL_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/morgul_crystal/morgul_crystal.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(MORGUL_CRYSTAL_LOCATION);
   private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));
   private final ModelPart cube;
   private final ModelPart glass;
   private final ModelPart base;

   public MorgulCrystalRenderer(EntityRendererProvider.Context context) {
      super(context);
      this.shadowRadius = 0.5F;
      // The crystal keeps its own copy of the old end crystal layer: vanilla renamed the parts
      // to outer_glass/inner_glass/cube and moved the spin into its model, so the vanilla layer
      // no longer has the glass, cube and base parts this renderer draws.
      ModelPart modelpart = context.bakeLayer(TolkienModelLayers.MORGUL_CRYSTAL);
      this.glass = modelpart.getChild("glass");
      this.cube = modelpart.getChild("cube");
      this.base = modelpart.getChild("base");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   @Override
   public MorgulCrystalRenderState createRenderState() {
      return new MorgulCrystalRenderState();
   }

   @Override
   public void extractRenderState(MorgulCrystalEntity entity, MorgulCrystalRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.ticks = (float) entity.time + partialTick;
      state.showsBottom = entity.showsBottom();
   }

   @Override
   public void render(MorgulCrystalRenderState state, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
      stack.pushPose();
      float bob = getY(state.ticks);
      float spin = state.ticks * 3.0F;
      VertexConsumer vertexConsumer = bufferSource.getBuffer(RENDER_TYPE);
      stack.pushPose();
      stack.scale(2.0F, 2.0F, 2.0F);
      stack.translate(0.0D, -0.5D, 0.0D);
      int overlay = OverlayTexture.NO_OVERLAY;
      if (state.showsBottom) {
         this.base.render(stack, vertexConsumer, packedLight, overlay);
      }

      stack.mulPose(Axis.YP.rotationDegrees(spin));
      stack.translate(0.0D, (double)(1.5F + bob / 2.0F), 0.0D);
      stack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
      this.glass.render(stack, vertexConsumer, packedLight, overlay);
      stack.scale(0.875F, 0.875F, 0.875F);
      stack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
      stack.mulPose(Axis.YP.rotationDegrees(spin));
      this.glass.render(stack, vertexConsumer, packedLight, overlay);
      stack.scale(0.875F, 0.875F, 0.875F);
      stack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
      stack.mulPose(Axis.YP.rotationDegrees(spin));
      this.cube.render(stack, vertexConsumer, packedLight, overlay);
      stack.popPose();
      stack.popPose();

      super.render(state, stack, bufferSource, packedLight);
   }

   public static float getY(float ticks) {
      float f1 = Mth.sin(ticks * 0.2F) / 2.0F + 0.5F;
      f1 = (f1 * f1 + f1) * 0.4F;
      return f1 - 1.4F;
   }

   public ResourceLocation getTextureLocation(MorgulCrystalRenderState state) {
      return MORGUL_CRYSTAL_LOCATION;
   }

   @Override
   public boolean shouldRender(MorgulCrystalEntity entity, Frustum frustum, double x, double y, double z) {
      return super.shouldRender(entity, frustum, x, y, z) || entity.getBeamTarget() != null;
   }
}
