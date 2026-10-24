package com.greatorator.tolkienmobs.entity.monster.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.monster.HuronEntity;
import com.greatorator.tolkienmobs.entity.monster.model.HuronModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * The huron has one texture, so the only thing its renderer has to do beyond the base class is turn the
 * head towards what it is looking at and keep its translucent render type.
 */
public class HuronRender extends GeoEntityRenderer<HuronEntity, TolkienGeoRenderState> {
    public HuronRender(EntityRendererProvider.Context context) {
        super(context, new HuronModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(HuronEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public RenderType getRenderType(TolkienGeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();
        float pitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        snapshots.ifPresent("head", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
