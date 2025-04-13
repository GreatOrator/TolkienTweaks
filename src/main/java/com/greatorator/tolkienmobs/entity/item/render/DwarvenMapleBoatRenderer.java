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

public class DwarvenMapleBoatRenderer extends BoatRenderer {
    private static final ResourceLocation DWARVEN_MAPLE_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/boat/dwarven_maple.png");
    private static final ResourceLocation DWARVEN_MAPLE_CHEST_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/dwarven_maple.png");
    private final Pair<ResourceLocation, ListModel<Boat>> dwarven_mapleBoatResource;

    public DwarvenMapleBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest);
        this.dwarven_mapleBoatResource = Pair.of(chest ? DWARVEN_MAPLE_CHEST_BOAT : DWARVEN_MAPLE_BOAT, chest ? new ChestBoatModel(context.bakeLayer(TolkienModelLayers.DWARVEN_MAPLE_CHEST_BOAT)) : new BoatModel(context.bakeLayer(TolkienModelLayers.DWARVEN_MAPLE_BOAT)));
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return this.dwarven_mapleBoatResource;
    }
}
