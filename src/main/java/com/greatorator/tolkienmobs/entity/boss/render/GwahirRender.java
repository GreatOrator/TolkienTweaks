package com.greatorator.tolkienmobs.entity.boss.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.boss.GwahirEntity;
import com.greatorator.tolkienmobs.entity.boss.model.GwahirModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Gwaihir shares the eagle's model and texture, and the one thing his renderer adds is the crown the plain
 * great eagle hides. He is larger than an ordinary eagle and turns his neck rather than his head.
 */
public class GwahirRender extends GeoEntityRenderer<GwahirEntity, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/birds/entitygreateagle.png");

    public GwahirRender(EntityRendererProvider.Context context) {
        super(context, new GwahirModel());
        this.shadowRadius = 1.0F;
    }

    @Override
    public TolkienGeoRenderState createRenderState(GwahirEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return TEXTURE;
    }

    /** The eagle's model is drawn larger for Gwaihir; 5 applies that here rather than in preRender. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        super.scaleModelForRender(renderPassInfo, 1.4F, 1.4F);
    }

    /** The neck turn the model used to make in setCustomAnimations, which only turned the head on yaw. */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        snapshots.ifPresent("neck", bone -> bone.setRotY(yaw * 0.5f * Mth.DEG_TO_RAD));
    }
}
