package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.entity.ambient.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    public static final DeferredRegister.Items SPAWN_EGGS = DeferredRegister.createItems(MODID);

    public static final Supplier<EntityType<GeckoEntity>> ENTITY_TTM_GECKO =
            ENTITIES.register("entityttmgecko", () -> EntityType.Builder.of(GeckoEntity::new, MobCategory.CREATURE)
                    .sized(0.75f, 0.35f).build("entityttmgecko"));
    public static final Supplier<EntityType<RatEntity>> ENTITY_TTM_RAT =
            ENTITIES.register("entityttmrat", () -> EntityType.Builder.of(RatEntity::new, MobCategory.CREATURE)
                    .sized(0.75f, 0.35f).build("entityttmrat"));
    public static final Supplier<EntityType<SquirrelEntity>> ENTITY_TTM_SQUIRREL =
            ENTITIES.register("entityttmsquirrel", () -> EntityType.Builder.of(SquirrelEntity::new, MobCategory.CREATURE)
                    .sized(0.75f, 0.35f).build("entityttmsquirrel"));
    public static final Supplier<EntityType<FrogEntity>> ENTITY_TTM_FROG =
            ENTITIES.register("entityttmfrog", () -> EntityType.Builder.of(FrogEntity::new, MobCategory.CREATURE)
                    .sized(0.75f, 0.35f).build("entityttmfrog"));
    public static final Supplier<EntityType<SwarmEntity>> ENTITY_TTM_SWARM =
            ENTITIES.register("entityttmswarm", () -> EntityType.Builder.of(SwarmEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 0.35f).build("entityttmswarm"));

    public static final DeferredItem<Item> EGG_TTMGECKO = SPAWN_EGGS.register("entityttmgecko_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_GECKO, 9482106, 16121867,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMRAT = SPAWN_EGGS.register("entityttmrat_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_RAT, 9482106, 5600397,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMSQUIRREL = SPAWN_EGGS.register("entityttmsquirrel_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_SQUIRREL, 9482106, 7405383,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMFROG = SPAWN_EGGS.register("entityttmfrog_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_FROG, 9482106, 14289362,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMSWARM = SPAWN_EGGS.register("entityttmswarm_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_SWARM, 9482106, 12198412,
                    new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
        SPAWN_EGGS.register(eventBus);
    }
}