package com.greatorator.tolkientweaks.datagen.loot;

import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.TableLootEntry;

import java.util.HashSet;
import java.util.Set;

public class EntityLootGenerator extends EntityLootTables {
    private final Set<EntityType<?>> knownEntities = new HashSet<>();

    @Override
    protected void add(EntityType<?> entity, LootTable.Builder builder) {
        super.add(entity, builder);
        knownEntities.add(entity);
    }

    @Override
    protected void addTables() {
        // Ambient
//        add(EntityGenerator.ENTITY_TTM_RAT.get(), noLoot());
//        add(EntityGenerator.ENTITY_TTM_THRUSH.get(), fromEntityLootTable(EntityType.PARROT));
//        add(EntityGenerator.ENTITY_TTM_SQUIRREL.get(),
//                LootTable.lootTable()
//                        .withPool(LootPool.lootPool()
//                                .setRolls(ConstantRange.exactly(1))
//                                .add(ItemLootEntry.lootTableItem(TTMContent.TREE_ACORN.get())
//                                        .apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F)))
//                                        .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F))))
//                                .when(KilledByPlayer.killedByPlayer()))
//                        .withPool(LootPool.lootPool()
//                                .setRolls(ConstantRange.exactly(1))
//                                .add(ItemLootEntry.lootTableItem(TTMContent.GOLDEN_TREE_ACORN.get())
//                                        .apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F)))
//                                        .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F))))
//                                .when(KilledByPlayer.killedByPlayer())));
    }

    public LootTable.Builder noLoot() {
        return LootTable.lootTable();
    }

    public LootTable.Builder fromEntityLootTable(EntityType<?> parent) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantRange.exactly(1))
                        .add(TableLootEntry.lootTableReference(parent.getDefaultLootTable())));
    }

    @Override
    public Set<EntityType<?>> getKnownEntities() {
        return knownEntities;
    }

    public String getName() {
        return "Tolkien Tweaks - Loot Tables - Entity";
    }
}
