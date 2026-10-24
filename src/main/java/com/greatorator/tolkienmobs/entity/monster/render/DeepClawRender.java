package com.greatorator.tolkienmobs.entity.monster.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.monster.DeepClawEntity;
import com.greatorator.tolkienmobs.entity.monster.model.DeepClawModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;



import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The variant rides on the render state, because the model only ever sees that. */
public class DeepClawRender extends GeoEntityRenderer<DeepClawEntity, TolkienGeoRenderState> {
    public static final Map<TolkienVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw1.png"));
                map.put(TolkienVariant.RED,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw1.png"));
                map.put(TolkienVariant.ORANGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw2.png"));
                map.put(TolkienVariant.YELLOW,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw3.png"));
                map.put(TolkienVariant.GREEN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw4.png"));
                map.put(TolkienVariant.BLUE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw1.png"));
                map.put(TolkienVariant.INDIGO,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw2.png"));
                map.put(TolkienVariant.VIOLET,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw3.png"));
                map.put(TolkienVariant.MAGENTA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw4.png"));
                map.put(TolkienVariant.PINK,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw1.png"));
                map.put(TolkienVariant.GRAY,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw2.png"));
                map.put(TolkienVariant.AQUA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw3.png"));
                map.put(TolkienVariant.BEIGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw4.png"));
                map.put(TolkienVariant.BROWN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw1.png"));
                map.put(TolkienVariant.CORAL,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw2.png"));
                map.put(TolkienVariant.CYAN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw3.png"));
                map.put(TolkienVariant.LAVENDER,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmdeepclaw/tmdeepclaw4.png"));
            });

    public DeepClawRender(EntityRendererProvider.Context context) {
        super(context, new DeepClawModel());
        this.shadowRadius = 0.30f;
    }

    /** The variant's texture, for the model and for the base class's texture lookup. */
    public static Identifier textureFor(TolkienGeoRenderState renderState) {
        return LOCATION_BY_VARIANT.get(renderState.variant);
    }

    @Override
    public TolkienGeoRenderState createRenderState(DeepClawEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public void addRenderData(DeepClawEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        renderState.variant = animatable.getVariant();
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();
        float pitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        snapshots.ifPresent("head", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
