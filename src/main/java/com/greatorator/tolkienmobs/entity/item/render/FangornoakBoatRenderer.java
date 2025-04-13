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

public class FangornoakBoatRenderer extends BoatRenderer {
    private static final ResourceLocation FANGORNOAK_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/boat/fangornoak.png");
    private static final ResourceLocation FANGORNOAK_CHEST_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/fangornoak.png");
    private final Pair<ResourceLocation, ListModel<Boat>> fangornoakBoatResource;

    public FangornoakBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest);
        this.fangornoakBoatResource = Pair.of(chest ? FANGORNOAK_CHEST_BOAT : FANGORNOAK_BOAT, chest ? new ChestBoatModel(context.bakeLayer(TolkienModelLayers.FANGORNOAK_CHEST_BOAT)) : new BoatModel(context.bakeLayer(TolkienModelLayers.FANGORNOAK_BOAT)));
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return this.fangornoakBoatResource;
    }
}
