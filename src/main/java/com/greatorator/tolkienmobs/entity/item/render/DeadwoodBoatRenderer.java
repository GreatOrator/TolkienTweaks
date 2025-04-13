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

public class DeadwoodBoatRenderer extends BoatRenderer {
    private static final ResourceLocation DEADWOOD_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/boat/deadwood.png");
    private static final ResourceLocation DEADWOOD_CHEST_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/deadwood.png");
    private final Pair<ResourceLocation, ListModel<Boat>> deadwoodBoatResource;

    public DeadwoodBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest);
        this.deadwoodBoatResource = Pair.of(chest ? DEADWOOD_CHEST_BOAT : DEADWOOD_BOAT, chest ? new ChestBoatModel(context.bakeLayer(TolkienModelLayers.DEADWOOD_CHEST_BOAT)) : new BoatModel(context.bakeLayer(TolkienModelLayers.DEADWOOD_BOAT)));
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return this.deadwoodBoatResource;
    }
}
