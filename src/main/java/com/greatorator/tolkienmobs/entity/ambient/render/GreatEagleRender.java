package com.greatorator.tolkienmobs.entity.ambient.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.ambient.GreatEagleEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.GreatEagleModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.Set;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GreatEagleRender extends GeoEntityRenderer<GreatEagleEntity, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/birds/entitygreateagle.png");

    public GreatEagleRender(EntityRendererProvider.Context context) {
        super(context, new GreatEagleModel());
        this.shadowRadius = 1.0F;
    }

    @Override
    public TolkienGeoRenderState createRenderState(GreatEagleEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public void addRenderData(GreatEagleEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        renderState.hiddenBones = Set.of("crown");
    }

    /** The model's own scaling, which 5 applies here rather than in preRender. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        super.scaleModelForRender(renderPassInfo, 1.4F, 1.4F);
    }

    /** The bone work the model used to do in setCustomAnimations, done per render pass. */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();

        for (String bone : renderState.hiddenBones) {
            snapshots.ifPresent(bone, snapshot -> snapshot.skipRender(true).skipChildrenRender(true));
        }

        float pitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        snapshots.ifPresent("neck", bone -> bone.setRotY(yaw * 0.5f * Mth.DEG_TO_RAD));
    }
}
