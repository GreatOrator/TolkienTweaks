package com.greatorator.tolkienmobs.entity.item.render;

import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MallornBoatRenderer extends BoatRenderer {
    private static final ResourceLocation MALLORN_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/boat/mallorn.png");
    private static final ResourceLocation MALLORN_CHEST_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/mallorn.png");
    private final Pair<ResourceLocation, ListModel<Boat>> mallornBoatResource;

    public MallornBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest);
        this.mallornBoatResource = Pair.of(chest ? MALLORN_CHEST_BOAT : MALLORN_BOAT, chest ? new ChestBoatModel(context.bakeLayer(TolkienModelLayers.MALLORN_CHEST_BOAT)) : new BoatModel(context.bakeLayer(TolkienModelLayers.MALLORN_BOAT)));
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return this.mallornBoatResource;
    }
}
