package lotr.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class LOTRModelPortal extends Model {
    private ModelRenderer[] ringParts = new ModelRenderer[60];

    public LOTRModelPortal(Function<ResourceLocation, RenderType> p_i225947_1_ ,int i) {
        super(p_i225947_1_);

        for (int j = 0; j < 60; j++) {
            if (i == 0) {
                this.ringParts[j] = new ModelRenderer(this, 0, 0).texOffs(64, 32);
                this.ringParts[j].addBox(-2.0F, -3.5F, -38.0F, 4, 7, 3);
            }
            else {
                this.ringParts[j] = new ModelRenderer(this, j % 30 * 8, 0).texOffs(240, 5);
                this.ringParts[j].mirror = true;
                this.ringParts[j].addBox(-2.0F, -2.5F, -38.0F, 4, 5, 0);
            }
            this.ringParts[j].setPos(0.0F, 0.0F, 0.0F);
            this.ringParts[j].yRot = (j * 6.0F / 180.0F * 3.141593F);
        }
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        for (int i = 0; i < 60; i++) {
            this.ringParts[i].render(matrixStack, iVertexBuilder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        }
    }
}