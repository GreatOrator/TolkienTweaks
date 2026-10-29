package com.greatorator.tolkienmobs.datagen.loot;

import com.greatorator.tolkienmobs.datagen.helpers.TolkienLootHelper;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

/**
 * What stands in the mod's chests.
 *
 * <p>Each table is a room's worth of things, not a hoard: the entries are the goods that room would
 * actually hold, and the weights run the right way round - the cheap, useful things are common and
 * the one good thing is a rare tail. A village chest is somebody's home; a ruin is somebody's
 * abandoned home, and holds the coin and the old metal that outlasted them.
 *
 * <p>Village houses are tiered by culture rather than by richness: a hobbit hole holds farm tools
 * and a pantry, a dwarven house holds a pick and ore, an elven house holds waybread and arrows.
 */
public record TolkienChestLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> register) {
        // ---- Hobbit ----------------------------------------------------------------------

        // A grocer's shop: what is on the shelves, plus the till behind the counter.
        register.accept(TolkienLootHelper.HOBBIT_GROCER,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.FOOD_HONEY.get(), 18, 1, 2),
                                Entry.of(TolkienItems.HONEY_CAKE.get(), 14, 2, 4),
                                Entry.of(TolkienItems.BRAMBLES_BERRY.get(), 14, 2, 5),
                                Entry.of(Items.BREAD, 12, 2, 4),
                                Entry.of(Items.APPLE, 10, 2, 5),
                                Entry.of(Items.PUMPKIN_PIE, 8, 1, 2),
                                Entry.of(Items.MELON_SEEDS, 6, 1, 3),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 6, 2, 6))));

        // A hobbit hole: a working pantry and the tools of a garden, not a weapon rack.
        register.accept(TolkienLootHelper.HOBBIT_HOUSE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.FOOD_HONEY.get(), 18, 1, 2),
                                Entry.of(TolkienItems.CRAM.get(), 14, 1, 3),
                                Entry.of(Items.BREAD, 12, 1, 3),
                                Entry.of(Items.COOKED_SALMON, 10, 1, 2),
                                Entry.of(TolkienItems.PIPEWEED_ITEM.get(), 8, 1, 3),
                                Entry.of(Items.STONE_HOE, 6, 1, 1),
                                Entry.of(Items.STONE_SHOVEL, 6, 1, 1),
                                Entry.of(TolkienItems.ITEM_LETTER.get(), 3, 1, 1))));

        // The baker's pantry: what a hobbit household kneads, bakes and keeps in the crocks.
        register.accept(TolkienLootHelper.HOBBIT_BAKER,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(Items.BREAD, 18, 1, 4),
                                Entry.of(Items.COOKIE, 15, 2, 6),
                                Entry.of(Items.WHEAT, 14, 1, 6),
                                Entry.of(TolkienItems.CRAM.get(), 12, 1, 3),
                                Entry.of(TolkienItems.HONEY_CAKE.get(), 9, 1, 2),
                                Entry.of(Items.PUMPKIN_PIE, 9, 1, 2),
                                Entry.of(TolkienItems.FOOD_HONEY.get(), 6, 1, 2))));

        // ---- Elven -----------------------------------------------------------------------

        // An elven house: waybread, a draught, and the arrows of the Galadhrim. The bow is the one
        // good thing in the room, so it sits at the very end of the weights.
        register.accept(TolkienLootHelper.ELVEN_HOUSE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.LEMBAS.get(), 18, 1, 3),
                                Entry.of(TolkienItems.DRINK_ELF_VITALITY.get(), 14, 1, 1),
                                Entry.of(TolkienItems.GALADHRIM_ARROW.get(), 14, 4, 8),
                                Entry.of(TolkienItems.MIRUVOR.get(), 10, 1, 1),
                                Entry.of(Items.BOOK, 6, 1, 1).enchanted(),
                                Entry.of(Items.GOLDEN_APPLE, 3, 1, 1),
                                Entry.of(TolkienItems.ELVEN_BOW.get(), 2, 1, 1))));

        // ---- Dwarven ---------------------------------------------------------------------

        // A dwarven house works stone and metal: the pick is the tool on the wall, the ore is what
        // came back from the seam, and the raw mithril is the lucky find.
        register.accept(TolkienLootHelper.DWARF_HOUSE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.DRINK_PERSONAL_BLACKSMITH.get(), 18, 1, 1),
                                Entry.of(TolkienItems.CRAM.get(), 14, 1, 3),
                                Entry.of(Items.IRON_INGOT, 12, 2, 4),
                                Entry.of(Items.COAL, 10, 4, 8),
                                Entry.of(Items.IRON_PICKAXE, 10, 1, 1),
                                Entry.of(TolkienItems.RAW_MITHRIL.get(), 6, 1, 3),
                                Entry.of(Items.BOOK, 5, 1, 1).enchanted())));

        // ---- Human -----------------------------------------------------------------------

        // A house of Men: the drink, the bread, and the correspondence of a household that trades.
        // The sword on the wall is the last resort, not the household armoury it used to be.
        register.accept(TolkienLootHelper.HUMAN_HOUSE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.GROG.get(), 18, 1, 1),
                                Entry.of(TolkienItems.HONEY_CAKE.get(), 12, 1, 3),
                                Entry.of(Items.BREAD, 12, 1, 3),
                                Entry.of(TolkienItems.ITEM_LETTER.get(), 10, 1, 1),
                                Entry.of(Items.COOKED_PORKCHOP, 10, 1, 2),
                                Entry.of(Items.EMERALD, 6, 1, 2),
                                Entry.of(Items.IRON_SWORD, 6, 1, 1),
                                Entry.of(TolkienItems.ITEM_SCROLL.get(), 4, 1, 1))));

        // A traveller's chest in a tent: what a night out in the wild asks for. Waybread and a
        // drink for the road, a torch and the means to light it, arrows for the pot, and pipe-weed
        // for the watch. Six things, so the chest reads as someone's kit rather than a hoard.
        register.accept(TolkienLootHelper.WILD_TENT,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(2, 4), UniformGenerator.between(1, 1),
                                Entry.of(TolkienItems.CRAM.get(), 25, 1, 3),
                                Entry.of(Items.TORCH, 20, 2, 4),
                                Entry.of(Items.ARROW, 20, 4, 8),
                                Entry.of(TolkienItems.GROG.get(), 13, 1, 1),
                                Entry.of(Items.FLINT_AND_STEEL, 12, 1, 1),
                                Entry.of(TolkienItems.PIPEWEED_ITEM.get(), 10, 1, 2))));

        // ---- The cold north --------------------------------------------------------------

        // A Lossoth house in Forochel: dried fish and hard bread, the hide it is wrapped in, and
        // the iron traded for at the coast.
        register.accept(TolkienLootHelper.FROZEN_HOUSE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.CRAM.get(), 18, 1, 3),
                                Entry.of(Items.COD, 14, 1, 3),
                                Entry.of(Items.LEATHER, 12, 1, 3),
                                Entry.of(TolkienItems.MONSTER_FUR.get(), 12, 1, 2),
                                Entry.of(Items.BONE, 8, 1, 3),
                                Entry.of(Items.IRON_INGOT, 6, 1, 2),
                                Entry.of(Items.SNOWBALL, 6, 4, 8))));

        // ---- The orc-holds ---------------------------------------------------------------

        // An orc's den: the drink, the meat it is eating, and the coin it took off the last one
        // to come through.
        register.accept(TolkienLootHelper.ORC_HOUSE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.GROG.get(), 18, 1, 2),
                                Entry.of(TolkienItems.MONSTER_FLESH.get(), 16, 1, 3),
                                Entry.of(Items.BONE, 12, 1, 3),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 10, 2, 5),
                                Entry.of(Items.GUNPOWDER, 6, 1, 3),
                                Entry.of(Items.IRON_INGOT, 8, 1, 2),
                                Entry.of(TolkienItems.SWORD_MORGULIRON.get(), 3, 1, 1))));

        // ---- The south -------------------------------------------------------------------

        // A house of Harad: waybread, what the caravan brought back, and the coin that paid for it.
        register.accept(TolkienLootHelper.DESERT_HOUSE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.CRAM.get(), 18, 1, 3),
                                Entry.of(Items.BREAD, 12, 1, 3),
                                Entry.of(Items.LEATHER, 12, 1, 2),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 10, 2, 5),
                                Entry.of(Items.GOLD_NUGGET, 10, 2, 6),
                                Entry.of(Items.IRON_SWORD, 5, 1, 1),
                                Entry.of(Items.EMERALD, 4, 1, 1))));

        // ---- Nurn ------------------------------------------------------------------------

        // A Nurn farmhouse: the crop it grows for Mordor's armies, the hoe it grows it with, and
        // very little else, because everything else is taken.
        register.accept(TolkienLootHelper.NURN_HOUSE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 1),
                                Entry.of(Items.WHEAT, 18, 2, 5),
                                Entry.of(Items.POTATO, 14, 2, 4),
                                Entry.of(Items.BEETROOT, 12, 2, 4),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 10, 2, 5),
                                Entry.of(Items.IRON_HOE, 8, 1, 1),
                                Entry.of(TolkienItems.MONSTER_FLESH.get(), 8, 1, 2),
                                Entry.of(Items.BREAD, 6, 1, 2))));

        // ---- Barrows ---------------------------------------------------------------------

        // The grave-goods of the Edain: old bone, an old blade, and the relics they were buried
        // with. A barrow holds a person's worth, not a treasure room's.
        register.accept(TolkienLootHelper.BARROW_CHEST,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(2, 4), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.ITEM_REMAINS.get(), 20, 1, 2),
                                Entry.of(Items.BONE, 18, 2, 5),
                                Entry.of(TolkienItems.ITEM_BROKENSWORD.get(), 10, 1, 1),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 8, 2, 6),
                                Entry.of(TolkienItems.GEM_AMMOLITE.get(), 8, 1, 2),
                                Entry.of(Items.GOLD_INGOT, 6, 1, 3),
                                Entry.of(Items.BOOK, 4, 1, 1).enchanted(),
                                Entry.of(TolkienItems.ITEM_KEYFRAGMENT.get(), 3, 1, 1))));

        register.accept(TolkienLootHelper.BARROW_GRAVE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(2, 4), UniformGenerator.between(1, 2),
                                Entry.of(Items.BONE, 20, 3, 6),
                                Entry.of(Items.ROTTEN_FLESH, 14, 2, 4),
                                Entry.of(TolkienItems.ITEM_REMAINS.get(), 10, 1, 1),
                                Entry.of(Items.IRON_INGOT, 10, 1, 2),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 8, 1, 4),
                                Entry.of(Items.EMERALD, 8, 1, 2),
                                Entry.of(Items.BOOK, 3, 1, 1).enchanted())));

        // ---- Ruins -----------------------------------------------------------------------

        // A settlement nobody came back to: the coin in the floor, the iron in the rubble, the one
        // thing worth carrying out.
        register.accept(TolkienLootHelper.DESOLATE_RUINS,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 18, 2, 6),
                                Entry.of(Items.IRON_INGOT, 14, 1, 3),
                                Entry.of(Items.BONE, 14, 2, 5),
                                Entry.of(TolkienItems.ITEM_REMAINS.get(), 10, 1, 1),
                                Entry.of(Items.SADDLE, 6, 1, 1),
                                Entry.of(TolkienItems.GEM_AMMOLITE.get(), 6, 1, 1),
                                Entry.of(Items.GOLD_INGOT, 5, 1, 2),
                                Entry.of(TolkienItems.ITEM_ARTIFACT.get(), 2, 1, 1))));

        // ---- Lairs -----------------------------------------------------------------------

        // The hag's stores: her herbs, the bottles they go into, and the things she has been
        // feeding. Her hoard is a working witch's, not a pirate's.
        register.accept(TolkienLootHelper.SWAMP_HAG_HUT,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.ITEM_PUNGENTHERB.get(), 18, 2, 4),
                                Entry.of(Items.GLASS_BOTTLE, 14, 1, 3),
                                Entry.of(Items.SPIDER_EYE, 14, 1, 3),
                                Entry.of(Items.GLOWSTONE_DUST, 10, 1, 3),
                                Entry.of(Items.REDSTONE, 10, 1, 3),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 8, 2, 6),
                                Entry.of(Items.ENDER_PEARL, 5, 1, 1))));

        // A spider's larder: silk, eyes, and what is left of what it wrapped up.
        register.accept(TolkienLootHelper.SPIDER_TREE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(Items.COBWEB, 18, 4, 10),
                                Entry.of(Items.SPIDER_EYE, 14, 2, 4),
                                Entry.of(Items.BONE, 14, 2, 5),
                                Entry.of(Items.STRING, 12, 4, 8),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 8, 1, 5),
                                Entry.of(TolkienItems.ITEM_REMAINS.get(), 3, 1, 1))));

        // A warg pit: bones, offal, and what the pack dragged in.
        register.accept(TolkienLootHelper.WARG_PIT,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(Items.BONE, 18, 3, 6),
                                Entry.of(TolkienItems.MONSTER_FLESH.get(), 16, 1, 3),
                                Entry.of(TolkienItems.MONSTER_FUR.get(), 12, 1, 3),
                                Entry.of(Items.LEATHER, 10, 1, 3),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 8, 2, 6),
                                Entry.of(Items.NAME_TAG, 3, 1, 1))));

        // The labyrinth's hoard: what the minotaur has taken off everyone who came before.
        register.accept(TolkienLootHelper.MINOTAUR_CHEST,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 18, 2, 6),
                                Entry.of(Items.GOLD_INGOT, 14, 1, 3),
                                Entry.of(TolkienItems.CRAM.get(), 10, 1, 3),
                                Entry.of(TolkienItems.ITEM_COIN_SILVER.get(), 8, 1, 2),
                                Entry.of(TolkienItems.INGOT_MORGULIRON.get(), 8, 1, 2),
                                Entry.of(TolkienItems.GEM_AMMOLITE.get(), 8, 1, 2),
                                Entry.of(Items.BOOK, 4, 1, 1).enchanted())));

        // Deeper in the maze: the same hoard, but the good things are nearer the top.
        register.accept(TolkienLootHelper.MAZE_CHEST,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 1),
                                Entry.of(TolkienItems.GEM_AMMOLITE.get(), 16, 1, 3),
                                Entry.of(TolkienItems.INGOT_MORGULIRON.get(), 12, 1, 3),
                                Entry.of(TolkienItems.DRINK_PERSONAL_BLACKSMITH.get(), 10, 1, 1),
                                Entry.of(TolkienItems.DRINK_ENT_DRAUGHT.get(), 8, 1, 1),
                                Entry.of(Items.BOOK, 6, 1, 1).enchanted(),
                                Entry.of(TolkienItems.ITEM_KEYFRAGMENT.get(), 5, 1, 1),
                                Entry.of(TolkienItems.INGOT_MITHRIL.get(), 4, 1, 1))));

        // The cave under the pool: fish, fish bones, and the gold of everything Gollum took.
        register.accept(TolkienLootHelper.GOLLUM_CAVE,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(Items.SALMON, 18, 1, 3),
                                Entry.of(Items.COD, 16, 1, 3),
                                Entry.of(Items.GOLD_NUGGET, 10, 1, 7),
                                Entry.of(TolkienItems.MONSTER_FLESH.get(), 8, 1, 3),
                                Entry.of(Items.PUFFERFISH, 8, 1, 2),
                                Entry.of(TolkienItems.ITEM_COIN_BRONZE.get(), 8, 1, 5),
                                Entry.of(TolkienItems.ITEM_REMAINS.get(), 3, 1, 1))));

        // A dark tower's strongroom: the gold of a garrison, the nuggets of Mordor's smiths, and
        // the one thing the builders were guarding.
        register.accept(TolkienLootHelper.TOWER_RUINS,
                LootTable.lootTable()
                        .withPool(chest(UniformGenerator.between(3, 5), UniformGenerator.between(1, 2),
                                Entry.of(TolkienItems.ITEM_COIN_GOLD.get(), 16, 1, 3),
                                Entry.of(TolkienItems.NUGGET_MORGULIRON.get(), 14, 2, 6),
                                Entry.of(Items.GOLD_INGOT, 12, 1, 3),
                                Entry.of(TolkienItems.MONSTER_FLESH.get(), 8, 1, 3),
                                Entry.of(Items.BOOK, 6, 1, 1).enchanted(),
                                Entry.of(TolkienItems.ITEM_DARKSIGIL.get(), 5, 1, 2),
                                Entry.of(Items.GOLDEN_APPLE, 4, 1, 1),
                                Entry.of(Items.ENCHANTED_GOLDEN_APPLE, 1, 1, 1))));
    }

    /**
     * One pool of a chest. {@code rolls} picks that many entries by weight, so a table of eight
     * candidates gives the four or five things the room actually holds, and {@code bonusRolls}
     * lets Luck widen a hoard.
     */
    private LootPool.Builder chest(NumberProvider rolls, NumberProvider bonusRolls, Entry... entries) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(rolls)
                .setBonusRolls(bonusRolls);
        for (Entry entry : entries) {
            LootItem.Builder<?> item = LootItem.lootTableItem(entry.item()).setWeight(entry.weight());
            if (entry.min() != 1 || entry.max() != 1) {
                item.apply(SetItemCountFunction.setCount(entry.min() == entry.max()
                        ? ConstantValue.exactly(entry.min())
                        : UniformGenerator.between(entry.min(), entry.max())));
            }
            if (entry.book()) {
                item.apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries));
            }
            pool.add(item);
        }
        return pool;
    }

    /**
     * One candidate in a chest: what it is, how likely it is among its pool-mates, and how many.
     *
     * @param book true for a book, which arrives with a random applicable enchantment
     */
    private record Entry(ItemLike item, int weight, int min, int max, boolean book) {
        static Entry of(ItemLike item, int weight, int min, int max) {
            return new Entry(item, weight, min, max, false);
        }

        /** The same entry, but as an enchanted book rather than a plain one. */
        Entry enchanted() {
            return new Entry(this.item, this.weight, this.min, this.max, true);
        }
    }
}
