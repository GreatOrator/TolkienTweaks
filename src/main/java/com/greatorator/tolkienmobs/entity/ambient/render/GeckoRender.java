package com.greatorator.tolkienmobs.entity.ambient.render;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.ambient.GeckoEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.GeckoModel;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GeckoRender extends MobRenderer<GeckoEntity, GeckoRenderState, GeckoModel> {
    private static final Map<TolkienVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_blue.png"));
                map.put(TolkienVariant.RED,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_blue.png"));
                map.put(TolkienVariant.ORANGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_green.png"));
                map.put(TolkienVariant.YELLOW,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_pink.png"));
                map.put(TolkienVariant.GREEN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_brown.png"));
                map.put(TolkienVariant.BLUE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_blue.png"));
                map.put(TolkienVariant.INDIGO,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_green.png"));
                map.put(TolkienVariant.VIOLET,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_pink.png"));
                map.put(TolkienVariant.MAGENTA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_brown.png"));
                map.put(TolkienVariant.PINK,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_blue.png"));
                map.put(TolkienVariant.GRAY,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_green.png"));
                map.put(TolkienVariant.AQUA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_green.png"));
                map.put(TolkienVariant.BEIGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_pink.png"));
                map.put(TolkienVariant.BROWN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_blue.png"));
                map.put(TolkienVariant.CORAL,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_green.png"));
                map.put(TolkienVariant.CYAN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_brown.png"));
                map.put(TolkienVariant.LAVENDER,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/entityttmgecko/gecko_blue.png"));
            });

    public GeckoRender(EntityRendererProvider.Context context) {
        super(context, new GeckoModel(context.bakeLayer(GeckoModel.LAYER_LOCATION)), 0.25f);
    }

    @Override
    public GeckoRenderState createRenderState() {
        return new GeckoRenderState();
    }

    @Override
    public void extractRenderState(GeckoEntity entity, GeckoRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.variant = entity.getVariant();
    }

    @Override
    public Identifier getTextureLocation(GeckoRenderState state) {
        return LOCATION_BY_VARIANT.get(state.variant);
    }

    @Override
    protected void scale(GeckoRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
    }
}
