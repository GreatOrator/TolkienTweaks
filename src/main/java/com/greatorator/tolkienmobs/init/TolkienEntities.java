package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.block.custom.tiles.PlacardTile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.init.TolkienBlocks.PLACARD;

public class TolkienEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);


    public static void register(IEventBus eventBus) {
    }
}