package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.greatorator.tolkienmobs.entity.TolkienArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienArrowRenderer extends ArrowRenderer<TolkienArrowEntity, ArrowRenderState> {
    protected final Identifier TEXTURE;

    public TolkienArrowRenderer(EntityRendererProvider.Context context, String textureName) {
        super(context);
        TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/" + textureName + ".png");
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    protected Identifier getTextureLocation(ArrowRenderState state) {
        return TEXTURE;
    }
}
