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

public class CulumaldaBoatRenderer extends BoatRenderer {
    private static final ResourceLocation CULUMALDA_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/boat/culumalda.png");
    private static final ResourceLocation CULUMALDA_CHEST_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/culumalda.png");
    private final Pair<ResourceLocation, ListModel<Boat>> culumaldaBoatResource;

    public CulumaldaBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest);
        this.culumaldaBoatResource = Pair.of(chest ? CULUMALDA_CHEST_BOAT : CULUMALDA_BOAT, chest ? new ChestBoatModel(context.bakeLayer(TolkienModelLayers.CULUMALDA_CHEST_BOAT)) : new BoatModel(context.bakeLayer(TolkienModelLayers.CULUMALDA_BOAT)));
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return this.culumaldaBoatResource;
    }
}
