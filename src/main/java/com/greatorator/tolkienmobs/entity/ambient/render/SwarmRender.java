package com.greatorator.tolkienmobs.entity.ambient.render;

import com.greatorator.tolkienmobs.entity.ambient.SwarmEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.SwarmModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SwarmRender extends MobRenderer<SwarmEntity, LivingEntityRenderState, SwarmModel> {
	public SwarmRender(EntityRendererProvider.Context context) {
		super(context, new SwarmModel(context.bakeLayer(SwarmModel.LAYER_LOCATION)), 0.0F);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return Identifier.fromNamespaceAndPath(MODID, "textures/entity/midgeflies.png");
	}

	@Override
	protected float getFlipDegrees() {
		return 0.0F;
	}
}
