package com.greatorator.tolkienmobs.datagen.loot;

import com.greatorator.tolkienmobs.datagen.loot.functions.TrinketRandomlyFunction;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.advancements.criterion.EntityFlagsPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

/**
 * What each of the mod's creatures leaves behind.
 *
 * <p>Every table is built from the same three parts, so a mob reads as a purse rather than a
 * shopping list:
 *
 * <ul>
 *   <li><b>the body</b> - what the creature physically is: hide, meat, feathers, a golem's stones.
 *       Always dropped when a player makes the kill.</li>
 *   <li><b>the purse</b> - the coin ladder (bronze for the common rabble up to mithril for a
 *       boss), plus the faction coin and token that only the mobs of a faction carry, and the
 *       dark sigil that only the undead and Mordor's servants carry.</li>
 *   <li><b>the one odd thing</b> - the single item worth hunting the creature for: a trinket, a
 *       rare ingot, the Witch-king's own blade. The trinket is left behind by chance, and Looting
 *       improves the odds.</li>
 * </ul>
 *
 * <p>Weights inside a pool are relative to that pool only, and a pool that is not the body pool is
 * behind a dice roll that Looting improves, so a table should be read as "on a player kill, this
 * much is certain, that much is a chance". Regular mobs are held to about six distinct items; the
 * bosses are allowed more, because a boss table is the reward for the fight.
 */
public class TolkienEntityLootTables extends EntityLootSubProvider {
    /** A coin for the common rabble. */
    private static final float COMMON_PURSE = 0.40F;
    /** A coin for the creatures worth a little more care. */
    private static final float UNCOMMON_PURSE = 0.30F;
    /** The second coin up, for the undead and the elite. */
    private static final float RARE_PURSE = 0.08F;
    /** The badge of a faction - only the mobs that serve one carry it. */
    private static final float FACTION_COIN = 0.12F;
    /** The token that raises standing - rarer still, and only for the orc-kin. */
    private static final float FACTION_TOKEN = 0.04F;
    /** The mark of the Enemy - only the undead and Mordor's servants carry it. */
    private static final float DARK_SIGIL = 0.15F;
    /** A trinket, off any of the twelve creatures that carry one. */
    private static final float TRINKET = 0.25F;

    public TolkienEntityLootTables(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return TolkienEntities.ENTITY_TYPES.getEntries().stream().map(DeferredHolder::value);
    }

    @Override
    public void generate() {
        // ---- Ambient ---------------------------------------------------------------------

        add(TolkienEntities.ENTITY_TTM_RAT.get(), emptyLootTable());

        add(TolkienEntities.ENTITY_TTM_THRUSH.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.BIRD_FEATHER.get(), 1, 1, 2))));

        add(TolkienEntities.ENTITY_TTM_SQUIRREL.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.TREE_ACORN.get(), 1, 1, 3)))
                        .withPool(chance(0.05F, Drop.of(TolkienItems.GOLDEN_TREE_ACORN.get(), 1, 1, 1))));

        add(TolkienEntities.ENTITY_TTM_CREBAIN.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.CREBAIN_FEATHER.get(), 1, 1, 3)))
                        .withPool(chance(0.08F, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 1))));

        add(TolkienEntities.ENTITY_TTM_FROG.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.INSECT.get(), 1, 1, 2))));

        add(TolkienEntities.ENTITY_TTM_GECKO.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.INSECT.get(), 1, 1, 2))));

        add(TolkienEntities.ENTITY_TTM_SWARM.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.INSECT.get(), 1, 2, 5)))
                        .withPool(chance(0.05F, Drop.of(TolkienItems.GOLDEN_INSECT.get(), 1, 1, 1))));

        add(TolkienEntities.ENTITY_TTM_GREAT_EAGLE.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.BIRD_FEATHER.get(), 3, 2, 4),
                                Drop.of(Items.BONE, 1, 1, 3))));

        // ---- Passive ---------------------------------------------------------------------

        // The beasts of the field: hide, a little leather, and the meat. Two of the three roll, so
        // a herd is worth more than a single animal without any one kill being a windfall.
        add(TolkienEntities.ENTITY_TTM_AUROCH.get(),
                LootTable.lootTable()
                        .withPool(herd(
                                Drop.of(TolkienItems.MONSTER_FUR.get(), 4, 1, 3),
                                Drop.of(Items.LEATHER, 2, 1, 2),
                                Drop.meat(Items.BEEF, 3, 1, 3))));

        add(TolkienEntities.ENTITY_TTM_GOAT.get(),
                LootTable.lootTable()
                        .withPool(herd(
                                Drop.of(TolkienItems.MONSTER_FUR.get(), 2, 1, 2),
                                Drop.of(Items.LEATHER, 2, 1, 2),
                                Drop.meat(Items.MUTTON, 3, 1, 2))));

        add(TolkienEntities.ENTITY_TTM_MUMAKIL.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MUMAKIL_LEATHER.get(), 4, 2, 5),
                                Drop.meat(Items.BEEF, 2, 2, 5),
                                Drop.of(Items.BONE, 1, 1, 4))));

        // Shadowfax is a mount, not a larder: the hide of a horse out of legend, and nothing else.
        add(TolkienEntities.ENTITY_TTM_SHADOWFAX.get(),
                LootTable.lootTable()
                        .withPool(body(1,
                                Drop.of(TolkienItems.MONSTER_FUR.get(), 3, 2, 4),
                                Drop.of(Items.LEATHER, 1, 2, 4))));

        add(TolkienEntities.ENTITY_TTM_NAZGULSTEED.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FUR.get(), 3, 2, 3),
                                Drop.of(Items.LEATHER, 2, 1, 2),
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 2, 1, 3)))
                        .withPool(chance(0.10F, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 1))));

        // Gollum: a wretch in a cave. The ring he carries is the reason to find him.
        add(TolkienEntities.ENTITY_TTM_GOLLUM.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 1, 2),
                                Drop.of(Items.SALMON, 2, 0, 1),
                                Drop.of(TolkienItems.SHOVEL_MORGULIRON.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 4))
                        .withPool(trinket(TolkienItems.TRINKET_RING.get())));

        // An Istar is a person, not a monster: a staff, a robe's worth of leather, and a rune-stone
        // if you are lucky. He does not leave a cut of meat behind.
        add(TolkienEntities.ENTITY_TTM_ISTARI.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.ARDA_STAFF.get(), 1, 1, 1)))
                        .withPool(chance(0.40F, Drop.of(Items.LEATHER, 1, 1, 2)))
                        .withPool(chance(0.20F, Drop.of(TolkienItems.ITEM_RUNE_STONE.get(), 1, 1, 1))));

        // ---- Monster ---------------------------------------------------------------------

        // The Dead of the barrows: old bone, an old blade, and the sigil that binds them.
        add(TolkienEntities.ENTITY_TTM_BARROW.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(Items.BONE, 3, 2, 5),
                                Drop.of(TolkienItems.ITEM_REMAINS.get(), 2, 0, 1),
                                Drop.of(TolkienItems.SWORD_MORGULIRON.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 4))
                        .withPool(chance(DARK_SIGIL, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 1))));

        add(TolkienEntities.ENTITY_TTM_OATHBREAKER.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(Items.BONE, 3, 1, 4),
                                Drop.of(TolkienItems.ITEM_REMAINS.get(), 2, 0, 1),
                                Drop.of(TolkienItems.SWORD_MORGULIRON.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 4))
                        .withPool(chance(DARK_SIGIL, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 1))));

        // A fell spirit has no body left, only the shadow it clings to and the crystal it was bound in.
        add(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(),
                LootTable.lootTable()
                        .withPool(body(1,
                                Drop.of(TolkienItems.ITEM_REMAINS.get(), 3, 0, 1),
                                Drop.of(TolkienItems.MORGUL_CRYSTAL.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 3))
                        .withPool(chance(DARK_SIGIL, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 1))));

        // The enraged spirit is a boss, the marsh-wraith swollen past its old shape: the crystal it
        // was bound in twice over, and the remains.
        add(TolkienEntities.ENTITY_TTM_ENRAGED_FELLSPIRIT.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.ITEM_REMAINS.get(), 3, 0, 1),
                                Drop.of(TolkienItems.MORGUL_CRYSTAL.get(), 2, 1, 2)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 4))
                        .withPool(chance(DARK_SIGIL, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 1)))
                        .withPool(chance(0.05F, Drop.of(TolkienItems.RING_OF_HOPE.get(), 1, 1, 1))));

        // The brigands are men and carry a man's purse: coin of the realm, and a blade they took
        // off the last traveller.
        add(TolkienEntities.ENTITY_TTM_BRIGAND.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(Items.LEATHER, 2, 1, 2),
                                Drop.of(Items.BREAD, 2, 1, 2),
                                Drop.of(Items.IRON_SWORD, 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5))
                        .withPool(coins(TolkienItems.ITEM_FACTIONCOIN.get(), FACTION_COIN, 1, 2)));

        add(TolkienEntities.ENTITY_TTM_HARADRIM.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(Items.LEATHER, 2, 1, 2),
                                Drop.meat(TolkienItems.CRAM.get(), 2, 1, 2),
                                Drop.of(Items.IRON_SWORD, 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5))
                        .withPool(coins(TolkienItems.ITEM_FACTIONCOIN.get(), FACTION_COIN, 1, 2)));

        add(TolkienEntities.ENTITY_TTM_ROMIEWALKER.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(Items.ROTTEN_FLESH, 3, 1, 2),
                                Drop.of(Items.POISONOUS_POTATO, 2, 0, 1),
                                Drop.of(TolkienItems.ITEM_PUNGENTHERB.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), UNCOMMON_PURSE, 1, 3)));

        // A mimic is a chest that bites back: the chest, what was in it, the drink it guards, and
        // a charm, if it has not swallowed it whole.
        add(TolkienEntities.ENTITY_TTM_MIMICCHEST.get(),
                LootTable.lootTable()
                        .withPool(body(3,
                                Drop.of(Items.CHEST, 4, 1, 1),
                                Drop.of(TolkienItems.DRINK_PERSONAL_BLACKSMITH.get(), 3, 1, 1),
                                Drop.of(TolkienItems.GEM_AMMOLITE.get(), 1, 1, 3)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5))
                        .withPool(trinket(TolkienItems.TRINKET_CHARM.get())));

        // The orc-kin carry the three marks of their service: a purse, the faction coin, and the
        // token that raises standing with them.
        add(TolkienEntities.ENTITY_TTM_MORDORORC.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.GROG.get(), 3, 1, 2),
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 1, 2),
                                Drop.of(TolkienItems.SWORD_MORGULIRON.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5))
                        .withPool(coins(TolkienItems.ITEM_FACTIONCOIN.get(), FACTION_COIN, 1, 2))
                        .withPool(coins(TolkienItems.ITEM_FACTIONTOKEN.get(), FACTION_TOKEN, 1, 1)));

        add(TolkienEntities.ENTITY_TTM_URUKHAI.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.GROG.get(), 3, 1, 2),
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 1, 2),
                                Drop.of(TolkienItems.SWORD_URUK.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5))
                        .withPool(coins(TolkienItems.ITEM_FACTIONCOIN.get(), FACTION_COIN, 1, 2))
                        .withPool(coins(TolkienItems.ITEM_FACTIONTOKEN.get(), FACTION_TOKEN, 1, 1)));

        add(TolkienEntities.ENTITY_TTM_DUERGAR.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 1, 2),
                                Drop.of(TolkienItems.AXE_MORGULIRON.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5))
                        .withPool(coins(TolkienItems.ITEM_FACTIONCOIN.get(), FACTION_COIN, 1, 2))
                        .withPool(coins(TolkienItems.ITEM_FACTIONTOKEN.get(), FACTION_TOKEN, 1, 1)));

        add(TolkienEntities.ENTITY_TTM_GOBLIN.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 1, 2),
                                Drop.of(Items.GUNPOWDER, 2, 1, 3),
                                Drop.of(TolkienItems.SWORD_MORGULIRON.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5))
                        .withPool(coins(TolkienItems.ITEM_FACTIONCOIN.get(), FACTION_COIN, 1, 2))
                        .withPool(coins(TolkienItems.ITEM_FACTIONTOKEN.get(), FACTION_TOKEN, 1, 1)));

        // A spider keeps no purse; what it has is what it wrapped up and left hanging.
        add(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(Items.SPIDER_EYE, 3, 1, 3),
                                Drop.of(Items.COBWEB, 2, 0, 3),
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 1, 1, 2)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), UNCOMMON_PURSE, 1, 3)));

        // The golems are the one creature whose body is its loot: their stones, two rolls of them,
        // and a cut of worked metal if the construct was well made. The blocks they used to drop
        // were nine ingots apiece, which made a golem a better mine than an ore vein.
        add(TolkienEntities.ENTITY_ROCKGOLEM.get(),
                LootTable.lootTable()
                        .withPool(golemStones())
                        .withPool(chance(0.20F,
                                Drop.of(TolkienItems.INGOT_MORGULIRON.get(), 3, 1, 2),
                                Drop.of(TolkienItems.INGOT_MITHRIL.get(), 1, 1, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), UNCOMMON_PURSE, 1, 4)));

        add(TolkienEntities.ENTITY_TTM_ELEMENTALGOLEM.get(),
                LootTable.lootTable()
                        .withPool(golemStones())
                        .withPool(chance(0.25F,
                                Drop.of(TolkienItems.INGOT_MORGULIRON.get(), 3, 1, 2),
                                Drop.of(TolkienItems.INGOT_MITHRIL.get(), 1, 1, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), UNCOMMON_PURSE, 1, 4)));

        // A Huron is a walking tree, and comes apart as one.
        add(TolkienEntities.ENTITY_TTM_HURON.get(),
                LootTable.lootTable()
                        .withPool(body(3,
                                Drop.of(Items.STICK, 4, 1, 6),
                                Drop.of(TolkienBlocks.LOG_DEADWOOD.get(), 3, 2, 6),
                                Drop.of(TolkienBlocks.LEAFPILE_CULUMALDA.get(), 2, 1, 2),
                                Drop.of(TolkienItems.GROG.get(), 1, 1, 1))));

        add(TolkienEntities.ENTITY_TTM_MINOTAUR.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 2, 4),
                                Drop.of(TolkienItems.MONSTER_FUR.get(), 2, 1, 3),
                                Drop.of(Items.BONE, 2, 1, 3),
                                Drop.of(TolkienItems.AXE_MORGULIRON.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5)));

        add(TolkienEntities.ENTITY_TTM_DEEPCLAW.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 1, 3),
                                Drop.of(TolkienItems.MONSTER_FUR.get(), 2, 1, 3),
                                Drop.of(Items.BONE, 2, 1, 3)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), UNCOMMON_PURSE, 1, 4)));

        add(TolkienEntities.ENTITY_TTM_TROLL.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 2, 4),
                                Drop.of(Items.BONE, 2, 1, 4),
                                Drop.of(TolkienItems.CLUB_WOODEN.get(), 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 6))
                        .withPool(coins(TolkienItems.ITEM_FACTIONCOIN.get(), FACTION_COIN, 1, 2))
                        .withPool(coins(TolkienItems.ITEM_FACTIONTOKEN.get(), FACTION_TOKEN, 1, 1)));

        add(TolkienEntities.ENTITY_TTM_TREEENT.get(),
                LootTable.lootTable()
                        .withPool(body(3,
                                Drop.of(Items.STICK, 4, 2, 6),
                                Drop.of(TolkienBlocks.LOG_DEADWOOD.get(), 3, 2, 6),
                                Drop.of(TolkienBlocks.LEAFPILE_CULUMALDA.get(), 2, 1, 2),
                                Drop.of(TolkienItems.DRINK_ENT_DRAUGHT.get(), 1, 1, 1))));

        // The swamp hag keeps a witch's stores: her herbs, the bottles they go in, and the things
        // she has been feeding.
        add(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(),
                LootTable.lootTable()
                        .withPool(body(3,
                                Drop.of(TolkienItems.ITEM_PUNGENTHERB.get(), 4, 1, 3),
                                Drop.of(Items.GLASS_BOTTLE, 3, 1, 3),
                                Drop.of(Items.SPIDER_EYE, 3, 1, 3),
                                Drop.of(Items.ENDER_PEARL, 1, 0, 1)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), COMMON_PURSE, 1, 5))
                        .withPool(chance(DARK_SIGIL, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 1))));

        add(TolkienEntities.ENTITY_TTM_WARG.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 1, 3),
                                Drop.of(TolkienItems.MONSTER_FUR.get(), 2, 2, 4)))
                        .withPool(coins(TolkienItems.ITEM_COIN_BRONZE.get(), UNCOMMON_PURSE, 1, 4)));

        // A Nazgul is a wraith: no body, only the blade, the cloak and the gold it was buried with.
        add(TolkienEntities.ENTITY_TTM_NAZGUL.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.SWORD_MORGULIRON.get(), 1, 1, 1)))
                        .withPool(chance(0.25F, Drop.of(TolkienItems.MORGUL_CRYSTAL.get(), 1, 1, 2)))
                        .withPool(coins(TolkienItems.ITEM_COIN_GOLD.get(), COMMON_PURSE, 1, 3))
                        .withPool(chance(DARK_SIGIL, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 1)))
                        .withPool(trinket(TolkienItems.TRINKET_CLOAK.get())));

        // ---- Boss ------------------------------------------------------------------------

        // The Goblin King: his blade, his hoard, the mark of his alliance, and the amulet.
        add(TolkienEntities.ENTITY_TTM_GOBLINKING.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.SWORD_MORGULIRON.get(), 1, 1, 1)))
                        .withPool(body(2,
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 2, 4),
                                Drop.of(Items.BONE, 2, 2, 4),
                                Drop.of(Items.GOLD_INGOT, 1, 1, 3)))
                        .withPool(coins(TolkienItems.ITEM_COIN_SILVER.get(), 0.75F, 1, 4))
                        .withPool(coins(TolkienItems.ITEM_FACTIONCOIN.get(), 0.50F, 1, 3))
                        .withPool(coins(TolkienItems.ITEM_FACTIONTOKEN.get(), 0.20F, 1, 2))
                        .withPool(trinket(TolkienItems.TRINKET_AMULET.get())));

        // The Balrog: the whip first, then the fire, then the mithril it was hoarding.
        add(TolkienEntities.ENTITY_TTM_BALROG.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.WHIP_FIRE.get(), 1, 1, 1)))
                        .withPool(body(2,
                                Drop.of(TolkienItems.INGOT_MITHRIL.get(), 3, 1, 3),
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 3, 6),
                                Drop.of(Items.FIRE_CHARGE, 2, 1, 3),
                                Drop.of(TolkienItems.DUST_MORGULIRON.get(), 1, 1, 4)))
                        .withPool(coins(TolkienItems.ITEM_COIN_MITHRIL.get(), 1.0F, 1, 1))
                        .withPool(trinket(TolkienItems.TRINKET_BELT.get())));

        // Shelob: silk, eyes, and the mithril of the things she has been eating.
        add(TolkienEntities.ENTITY_TTM_SHELOB.get(),
                LootTable.lootTable()
                        .withPool(body(3,
                                Drop.of(Items.COBWEB, 4, 4, 8),
                                Drop.of(Items.SPIDER_EYE, 3, 3, 5),
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 3, 5),
                                Drop.of(TolkienItems.INGOT_MITHRIL.get(), 1, 1, 3)))
                        .withPool(coins(TolkienItems.ITEM_COIN_MITHRIL.get(), 1.0F, 1, 1))
                        .withPool(trinket(TolkienItems.TRINKET_GLOVE.get())));

        // The Witch-king: the sword he carries is his own, and nothing else in the mod drops it.
        add(TolkienEntities.ENTITY_TTM_WITCHKING.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.SWORD_MORGULIRON.get(), 1, 1, 1)))
                        .withPool(chance(0.15F, Drop.of(TolkienItems.SWORD_WITCHKING.get(), 1, 1, 1)))
                        .withPool(chance(0.50F, Drop.of(TolkienItems.MORGUL_CRYSTAL.get(), 1, 1, 2)))
                        .withPool(chance(1.0F, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 2)))
                        .withPool(coins(TolkienItems.ITEM_COIN_MITHRIL.get(), 1.0F, 1, 1))
                        .withPool(trinket(TolkienItems.TRINKET_GLOVE.get())));

        // The Watcher in the Water: what it has swallowed, and the sea's own treasures.
        add(TolkienEntities.ENTITY_TTM_WATCHER.get(),
                LootTable.lootTable()
                        .withPool(body(3,
                                Drop.of(Items.PRISMARINE_SHARD, 4, 4, 8),
                                Drop.of(Items.INK_SAC, 3, 3, 5),
                                Drop.of(TolkienItems.MONSTER_FLESH.get(), 3, 3, 5),
                                Drop.of(Items.NAUTILUS_SHELL, 2, 1, 1),
                                Drop.of(TolkienItems.INGOT_MITHRIL.get(), 1, 1, 3)))
                        .withPool(chance(0.40F, Drop.of(Items.HEART_OF_THE_SEA, 1, 1, 1)))
                        .withPool(chance(1.0F, Drop.of(TolkienItems.ITEM_DARKSIGIL.get(), 1, 1, 2)))
                        .withPool(coins(TolkienItems.ITEM_COIN_MITHRIL.get(), 1.0F, 1, 1))
                        .withPool(trinket(TolkienItems.TRINKET_HAT.get())));

        add(TolkienEntities.ENTITY_TTM_MITHRILGOLEM.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.INGOT_MITHRIL.get(), 1, 4, 8)))
                        .withPool(body(1, Drop.of(TolkienItems.GOLEM_STONE_SUMMON.get(), 1, 3, 5)))
                        .withPool(coins(TolkienItems.ITEM_COIN_MITHRIL.get(), 1.0F, 1, 1))
                        .withPool(trinket(TolkienItems.TRINKET_CHARM.get())));

        add(TolkienEntities.ENTITY_TTM_MORGULIRONGOLEM.get(),
                LootTable.lootTable()
                        .withPool(body(1, Drop.of(TolkienItems.INGOT_MORGULIRON.get(), 1, 4, 8)))
                        .withPool(chance(0.50F, Drop.of(TolkienItems.DUST_MORGULIRON.get(), 1, 2, 5)))
                        .withPool(coins(TolkienItems.ITEM_COIN_MITHRIL.get(), 1.0F, 1, 1))
                        .withPool(trinket(TolkienItems.TRINKET_CHARM.get())));

        // Gwaihir is a lord of eagles, not a beast to be butchered: feathers and old bone.
        add(TolkienEntities.ENTITY_TTM_GWAHIR.get(),
                LootTable.lootTable()
                        .withPool(body(2,
                                Drop.of(TolkienItems.BIRD_FEATHER.get(), 3, 4, 8),
                                Drop.of(Items.BONE, 2, 3, 6)))
                        .withPool(trinket(TolkienItems.TRINKET_HAT.get())));

        // A fell beast is a mount and an enemy in one; it is ridden, not harvested.
        add(TolkienEntities.ENTITY_FELL_BEAST.get(), emptyLootTable());

        // NPCs drop nothing, but every entity type that is not a projectile or a vehicle names a
        // loot table, so they get the same empty one vanilla gives villagers and wandering traders.
        add(TolkienEntities.ENTITY_TTM_HUMAN.get(), emptyLootTable());
        add(TolkienEntities.ENTITY_TTM_DWARF.get(), emptyLootTable());
        add(TolkienEntities.ENTITY_TTM_ELVES.get(), emptyLootTable());
        add(TolkienEntities.ENTITY_TTM_HOBBIT.get(), emptyLootTable());
        add(TolkienEntities.ENTITY_TTM_SOUTHRON.get(), emptyLootTable());
        add(TolkienEntities.ENTITY_TTM_SOUTHRON_NPC.get(), emptyLootTable());
        add(TolkienEntities.ENTITY_TTM_ORC.get(), emptyLootTable());
        add(TolkienEntities.ENTITY_TTM_ORC_TRADER.get(), emptyLootTable());
    }

    public LootTable.Builder emptyLootTable() {
        return LootTable.lootTable();
    }

    // ---- The three parts a table is made of -------------------------------------------------

    /**
     * What the creature physically is. Rolls the pool {@code rolls} times, so a beast with a hide,
     * a leather and a cut of meat gives two of the three rather than the whole animal.
     */
    private LootPool.Builder body(int rolls, Drop... drops) {
        return pool(rolls, drops);
    }

    /** A herd animal: hide, leather, meat - two rolls, so a kill is worth a little of each. */
    private LootPool.Builder herd(Drop... drops) {
        return pool(2, drops);
    }

    /**
     * Something behind a dice roll. Looting improves the odds, which is what makes an otherwise
     * ordinary mob worth farming once the player has a looting blade.
     */
    private LootPool.Builder chance(float chance, Drop... drops) {
        return pool(1, drops)
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(
                        this.registries, chance, 0.03F));
    }

    /** One coin type, a handful of it, on a dice roll. */
    private LootPool.Builder coins(ItemLike coin, float chance, int min, int max) {
        return chance(chance, Drop.of(coin, 1, min, max));
    }

    /**
     * A trinket: one pool, one roll, on a dice roll that Looting improves.
     */
    private LootPool.Builder trinket(ItemLike trinket) {
        return trinketPool(trinket)
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(
                        this.registries, TRINKET, 0.03F));
    }

    private LootPool.Builder trinketPool(ItemLike trinket) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(trinket)
                        .apply(TrinketRandomlyFunction.randomPotion())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                .when(LootItemKilledByPlayerCondition.killedByPlayer());
    }

    /**
     * A golem's body: two rolls from the six stones it was built around, with the summoning stone
     * the rarest of them.
     */
    private LootPool.Builder golemStones() {
        return pool(2,
                Drop.of(TolkienItems.GOLEM_STONE.get(), 20, 1, 2),
                Drop.of(TolkienItems.GOLEM_STONE_EARTH.get(), 18, 1, 1),
                Drop.of(TolkienItems.GOLEM_STONE_FIRE.get(), 18, 1, 1),
                Drop.of(TolkienItems.GOLEM_STONE_WATER.get(), 18, 1, 1),
                Drop.of(TolkienItems.GOLEM_STONE_AIR.get(), 14, 1, 1),
                Drop.of(TolkienItems.GOLEM_STONE_SUMMON.get(), 6, 1, 1));
    }

    /**
     * The one place pools are actually assembled. Every pool a mob rolls is gated on a player
     * make the kill, so a mob that falls to another mob or to a cliff leaves nothing behind -
     * which is how these tables have always behaved.
     */
    private LootPool.Builder pool(int rolls, Drop... drops) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(rolls));
        for (Drop drop : drops) {
            LootItem.Builder<?> entry = LootItem.lootTableItem(drop.item()).setWeight(drop.weight());
            if (drop.min() != 1 || drop.max() != 1) {
                entry.apply(SetItemCountFunction.setCount(drop.min() == drop.max()
                        ? ConstantValue.exactly(drop.min())
                        : UniformGenerator.between(drop.min(), drop.max())));
            }
            if (drop.cooks()) {
                entry.apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS,
                        EntityPredicate.Builder.entity().flags(
                                EntityFlagsPredicate.Builder.flags().setOnFire(true)))));
            }
            pool.add(entry);
        }
        // Looting still adds a little to the amounts, the way it always has.
        pool.apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                this.registries, UniformGenerator.between(0.0F, 1.0F)));
        return pool.when(LootItemKilledByPlayerCondition.killedByPlayer());
    }

    /**
     * One entry of a pool: what drops, how likely it is among its pool-mates, and how many.
     *
     * @param cooks true for a cut of meat, which comes off the animal cooked if it was on fire when
     *              it died
     */
    private record Drop(ItemLike item, int weight, int min, int max, boolean cooks) {
        static Drop of(ItemLike item, int weight, int min, int max) {
            return new Drop(item, weight, min, max, false);
        }

        static Drop meat(ItemLike item, int weight, int min, int max) {
            return new Drop(item, weight, min, max, true);
        }
    }
}
