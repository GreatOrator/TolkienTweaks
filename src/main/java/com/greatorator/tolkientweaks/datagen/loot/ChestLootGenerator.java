package com.greatorator.tolkientweaks.datagen.loot;

import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;

import static com.greatorator.tolkientweaks.TolkienTweaks.MODID;

public class ChestLootGenerator extends ChestLootTables {
    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> registrar) {
//        //Adding stuff to vanilla loot tables
//        createInjectPool(registrar, "shipwreck_supply", LootTable.lootTable()
//                .withPool(LootPool.lootPool()
//                        .setRolls(ConstantRange.exactly(1))
//                        .add(ItemLootEntry.lootTableItem(TTMContent.CRAM.get())
//                                .setWeight(2)
//                                .apply(SetCount.setCount(RandomValueRange.between(0, 4)))))
//                .withPool(LootPool.lootPool()
//                        .setRolls(ConstantRange.exactly(1))
//                        .add(ItemLootEntry.lootTableItem(TTMContent.INSECT.get())
//                                .setWeight(2)
//                                .apply(SetCount.setCount(RandomValueRange.between(0, 4)))))
//                .withPool(LootPool.lootPool()
//                        .setRolls(ConstantRange.exactly(1))
//                        .add(ItemLootEntry.lootTableItem(TTMContent.TREE_ACORN.get())
//                                .setWeight(2)
//                                .apply(SetCount.setCount(RandomValueRange.between(0, 4))))));

        //New loot tables
//        registrar.accept(new ResourceLocation(MODID, "chests/hobbit_grocer"),
//            LootTable.lootTable()
//                .withPool(LootPool.lootPool()
//                    .setRolls(RandomValueRange.between(3, 5))
//                    .bonusRolls(1, 2)
//                    .add(ItemLootEntry.lootTableItem(TTMContent.FOOD_HONEY.get())
//                        .setWeight(18))
//                    .add(ItemLootEntry.lootTableItem(TTMContent.HONEY_CAKE.get())
//                        .setWeight(12)
//                        .apply(SetCount.setCount(RandomValueRange.between(1, 3))))
//                    .add(ItemLootEntry.lootTableItem(Items.MUSIC_DISC_13)
//                        .setWeight(15)
//                        .apply(SetCount.setCount(RandomValueRange.between(1, 3))))
//                    .add(ItemLootEntry.lootTableItem(Items.MUSIC_DISC_CAT)
//                            .setWeight(15)
//                            .apply(SetCount.setCount(RandomValueRange.between(1, 2))))
//                    .add(ItemLootEntry.lootTableItem(Items.NAME_TAG)
//                            .setWeight(20)
//                            .apply(SetCount.setCount(RandomValueRange.between(1, 3))))
//                    .add(ItemLootEntry.lootTableItem(Items.GOLDEN_APPLE)
//                            .setWeight(2))
//                    .add(ItemLootEntry.lootTableItem(Items.BEETROOT_SEEDS)
//                            .setWeight(10)
//                            .apply(SetCount.setCount(RandomValueRange.between(1, 3))))
//                    .add(ItemLootEntry.lootTableItem(Items.MELON_SEEDS)
//                            .setWeight(15))
//                    .add(ItemLootEntry.lootTableItem(Items.PUMPKIN_SEEDS)
//                            .setWeight(10))));

    }

    public void createInjectPool(BiConsumer<ResourceLocation, LootTable.Builder> consumer, String name, LootTable.Builder builder) {
        consumer.accept(new ResourceLocation(MODID, "inject/chests/" + name), builder);
    }

    public String getName() {
        return "Tolkien Tweaks - Loot Tables - Chests";
    }
}
