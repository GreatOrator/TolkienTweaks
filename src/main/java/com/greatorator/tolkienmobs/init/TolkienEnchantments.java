package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.enchantment.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.DamageImmunity;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

import java.util.Optional;

public class TolkienEnchantments {
    public static final ResourceKey<Enchantment> GONDORIAN_RESOLVE_KEY = create("gondorian_resolve_key");
    public static final ResourceKey<Enchantment> ELVEN_LONGEVITY_KEY = create("elven_longevity_key");
    public static final ResourceKey<Enchantment> ELVEN_FLEETFOOT_KEY = create("elven_fleetfoot_key");
    public static final ResourceKey<Enchantment> BALROG_MARK_KEY = create("balrog_mark_key");
    public static final ResourceKey<Enchantment> DWARVEN_ENDURANCE_KEY = create("dwarven_endurance_key");
    public static final ResourceKey<Enchantment> DWARVEN_MINING_KEY = create("dwarven_mining_key");
    public static final ResourceKey<Enchantment> HOBBIT_PLOW_KEY = create("hobbit_plow_key");
    public static final ResourceKey<Enchantment> HOBBIT_HARVEST_KEY = create("hobbit_harvest_key");

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        var enchantment = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        context.register(
            GONDORIAN_RESOLVE_KEY,
                Enchantment.enchantment(
                    Enchantment.definition(
                        items.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE), items.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(5,  8),
                        Enchantment.dynamicCost(25, 8),
                        3,
                        EquipmentSlotGroup.LEGS
                    )
                )
                .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(
                        EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new GondorResolveEnchantmentEffect(1)
                ).build(GONDORIAN_RESOLVE_KEY.location())
        );
        context.register(ELVEN_LONGEVITY_KEY, Enchantment.enchantment(Enchantment.definition(items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE), 5, 4,
                        Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(25, 8), 3, EquipmentSlotGroup.CHEST))
                .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM, new ElvenLongevityEnchantmentEffect(1)).build(ELVEN_LONGEVITY_KEY.location()));
        context.register(ELVEN_FLEETFOOT_KEY, Enchantment.enchantment(Enchantment.definition(items.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE), 5, 4,
                        Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(25, 8), 3, EquipmentSlotGroup.CHEST))
                .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM, new ElvenFleetfootEnchantmentEffect(1)).build(ELVEN_FLEETFOOT_KEY.location()));
        context.register(
            BALROG_MARK_KEY,
                Enchantment.enchantment(
                        Enchantment.definition(
                                items.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE), items.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                                5,
                                2,
                                Enchantment.dynamicCost(5, 8),
                                Enchantment.dynamicCost(25, 8),
                                3,
                                EquipmentSlotGroup.FEET
                        )
                )
                .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE))
                .withEffect(
                        EnchantmentEffectComponents.DAMAGE_IMMUNITY,
                        DamageImmunity.INSTANCE,
                        DamageSourceCondition.hasDamageSource(
                                DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate.is(DamageTypeTags.BURN_FROM_STEPPING))
                                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.LOCATION_CHANGED,
                        new ReplaceDisk(
                                new LevelBasedValue.Clamped(LevelBasedValue.perLevel(3.0F, 1.0F), 0.0F, 16.0F),
                                LevelBasedValue.constant(1.0F),
                                new Vec3i(0, -1, 0),
                                Optional.of(
                                        BlockPredicate.allOf(
                                                BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
                                                BlockPredicate.matchesBlocks(Blocks.DIRT),
                                                BlockPredicate.matchesBlocks(Blocks.STONE),
                                                BlockPredicate.unobstructed()
                                        )
                                ),
                                BlockStateProvider.simple(Blocks.MAGMA_BLOCK),
                                Optional.of(GameEvent.BLOCK_PLACE)
                        ),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true))
                        )
                ).build(BALROG_MARK_KEY.location())
        );
        context.register(DWARVEN_ENDURANCE_KEY, Enchantment.enchantment(Enchantment.definition(items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE), 5, 4,
                        Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(25, 8), 3, EquipmentSlotGroup.HEAD))
                .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM, new DwarvenEnduranceEnchantmentEffect(1)).build(DWARVEN_ENDURANCE_KEY.location()));
        context.register(DWARVEN_MINING_KEY, Enchantment.enchantment(Enchantment.definition(items.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                        items.getOrThrow(ItemTags.MINING_ENCHANTABLE), 5, 5,
                        Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(25, 8), 3, EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM, new DwarvenMinerEnchantmentEffect(1)).build(DWARVEN_MINING_KEY.location()));
        context.register(HOBBIT_PLOW_KEY, Enchantment.enchantment(Enchantment.definition(items.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                        items.getOrThrow(ItemTags.MINING_ENCHANTABLE), 5, 4,
                        Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(25, 8), 3, EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM, new HobbitPlowEnchantmentEffect(1)).build(HOBBIT_PLOW_KEY.location()));
        register(
            context,
            HOBBIT_HARVEST_KEY,
            Enchantment.enchantment(
                Enchantment.definition(
                    items.getOrThrow(ItemTags.HOES),
                    5,
                   4,
                    Enchantment.dynamicCost(5, 8),
                    Enchantment.dynamicCost(25, 8),
                    3,
                    EquipmentSlotGroup.HAND)
                )
            .withEffect(
                TolkienEnchantmentEffectComponents.USE_ON_BLOCK,
                new HobbitHarvestEnchantmentEffect(1),
                MatchTool.toolMatches(
                        ItemPredicate.Builder.item()
                                .of(ItemTags.HOES)
                )
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