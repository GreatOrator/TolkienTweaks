package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.greatorator.tolkienmobs.entity.TolkienArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@OnlyIn(Dist.CLIENT)
public class TolkienArrowRenderer extends ArrowRenderer<TolkienArrowEntity> {
    protected final ResourceLocation TEXTURE;
    public TolkienArrowRenderer(EntityRendererProvider.Context context, String textureName) {
        super(context);
        TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/" + textureName + ".png");
    }
    @Override public ResourceLocation getTextureLocation(TolkienArrowEntity arrow) {return TEXTURE;}
}