package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.enchantment.*;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class TolkienEnchantments {
    public static final ResourceKey<Enchantment> GONDORIAN_RESOLVE_KEY = create("gondorian_resolve_key");
    public static final ResourceKey<Enchantment> ELVEN_LONGEVITY_KEY = create("elven_longevity_key");
    public static final ResourceKey<Enchantment> ELVEN_FLEETFOOT_KEY = create("elven_fleetfoot_key");
    public static final ResourceKey<Enchantment> BALROG_MARK_KEY = create("balrog_mark_key");
    public static final ResourceKey<Enchantment> DWARVEN_ENDURANCE_KEY = create("dwarven_endurance_key");
    public static final ResourceKey<Enchantment> DWARVEN_MINING_KEY = create("dwarven_mining_key");
    public static final ResourceKey<Enchantment> HOBBIT_PLOW_KEY = create("hobbit_plow_key");
//    public static final ResourceKey<Enchantment> HOBBIT_HARVEST_KEY = create("hobbit_harvest_key");

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantmentHolderGetter = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemHolderGetter = context.lookup(Registries.ITEM);
        HolderGetter<DamageType> damageTypeHolderGetter = context.lookup(Registries.DAMAGE_TYPE);
        HolderGetter<Block> blockHolderGetter = context.lookup(Registries.BLOCK);

//        register(
//                context,
//                HOBBIT_HARVEST_KEY,
//                Enchantment.enchantment(
//                                Enchantment.definition(
//                                        itemHolderGetter.getOrThrow(ItemTags.HOES),
//                                        5,
//                                        1,
//                                        Enchantment.dynamicCost(5, 8),
//                                        Enchantment.dynamicCost(25, 8),
//                                        3,
//                                        EquipmentSlotGroup.MAINHAND)
//                        )
//                        .withEffect(
//                                EnchantmentEffectComponents.TICK,
//                                new HobbitHarvestEnchantmentEffect(1)
//                        )
//        );
        register(
                context,
                HOBBIT_PLOW_KEY,
                Enchantment.enchantment(Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.HOES),
                        1,
                        1,
                        Enchantment.dynamicCost(10, 10),
                        Enchantment.dynamicCost(25, 10),
                        4,
                        EquipmentSlotGroup.MAINHAND)
                ).exclusiveWith(
                        enchantmentHolderGetter.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE)
                ).withEffect(
                        TolkienEnchantmentEffectComponents.USE_ON_BLOCK,
                        new HobbitPlowEnchantmentEffect(1)
                )
        );
        register(
                context,
                DWARVEN_MINING_KEY,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemHolderGetter.getOrThrow(ItemTags.PICKAXES),
                                5,
                                5,
                                Enchantment.dynamicCost(1, 10),
                                Enchantment.dynamicCost(15, 10),
                                2,
                                EquipmentSlotGroup.HAND
                        )
                ).withEffect(
                        EnchantmentEffectComponents.HIT_BLOCK,
                        new DwarvenMinerEnchantmentEffect(3)
                        )
        );
        register(
                context,
                ELVEN_LONGEVITY_KEY,
                Enchantment.enchantment(Enchantment.definition(
                                itemHolderGetter.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                                5,
                                5,
                                Enchantment.dynamicCost(5, 8),
                                Enchantment.dynamicCost(25, 8),
                                2,
                                EquipmentSlotGroup.CHEST)
                        ).withEffect(
                                EnchantmentEffectComponents.TICK,
                                new ElvenLongevityEnchantmentEffect(1)
                )
        );
        register(
                context,
                ELVEN_FLEETFOOT_KEY,
                Enchantment.enchantment(Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(25, 8),
                        2,
                        EquipmentSlotGroup.FEET)
                ).withEffect(
                        EnchantmentEffectComponents.TICK,
                        new ElvenFleetfootEnchantmentEffect(1)
                )
        );
        register(
                context,
                GONDORIAN_RESOLVE_KEY,
                Enchantment.enchantment(Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(25, 8),
                        2,
                        EquipmentSlotGroup.LEGS)
                ).withEffect(
                        EnchantmentEffectComponents.TICK,
                        new GondorResolveEnchantmentEffect(1)
                )
        );
        register(
                context,
                BALROG_MARK_KEY,
                Enchantment.enchantment(Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                                1,
                                3,
                                Enchantment.dynamicCost(10, 10),
                                Enchantment.dynamicCost(25, 10),
                                4,
                                EquipmentSlotGroup.FEET)
                ).withEffect(
                        EnchantmentEffectComponents.TICK,
                        new BalrogMarkEnchantmentEffect(1)
                )
        );
        register(
                context,
                DWARVEN_ENDURANCE_KEY,
                Enchantment.enchantment(Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        1,
                        1,
                        Enchantment.dynamicCost(10, 10),
                        Enchantment.dynamicCost(25, 10),
                        4,
                        EquipmentSlotGroup.HEAD)
                ).withEffect(
                        EnchantmentEffectComponents.TICK,
                        new DwarvenEnduranceEnchantmentEffect(14)
                )
        );
    }

    private static ResourceKey<Enchantment> create(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, TolkienMobsMain.resLoc(name));
    }

    public static void register(
            BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
}