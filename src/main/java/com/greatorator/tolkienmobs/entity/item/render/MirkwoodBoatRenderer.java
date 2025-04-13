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

public class MirkwoodBoatRenderer extends BoatRenderer {
    private static final ResourceLocation MIRKWOOD_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/boat/mirkwood.png");
    private static final ResourceLocation MIRKWOOD_CHEST_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/mirkwood.png");
    private final Pair<ResourceLocation, ListModel<Boat>> mirkwoodBoatResource;

    public MirkwoodBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest);
        this.mirkwoodBoatResource = Pair.of(chest ? MIRKWOOD_CHEST_BOAT : MIRKWOOD_BOAT, chest ? new ChestBoatModel(context.bakeLayer(TolkienModelLayers.MIRKWOOD_CHEST_BOAT)) : new BoatModel(context.bakeLayer(TolkienModelLayers.MIRKWOOD_BOAT)));
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return this.mirkwoodBoatResource;
    }
}
