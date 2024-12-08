package com.greatorator.tolkienmobs.entity.ambient.render;

import com.greatorator.tolkienmobs.entity.ambient.SwarmEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.SwarmModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SwarmRender extends MobRenderer<SwarmEntity, SwarmModel> {
	public SwarmRender(EntityRendererProvider.Context context) {
		super(context, new SwarmModel(context.bakeLayer(SwarmModel.LAYER_LOCATION)), 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(SwarmEntity swarmEntity) {
		return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/midgeflies.png");
	}

	@Override
	protected float getFlipDegrees(SwarmEntity entity) {
		return 0.0F;
	}
}
