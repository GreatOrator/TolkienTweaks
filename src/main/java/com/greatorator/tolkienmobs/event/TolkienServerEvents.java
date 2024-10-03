package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.block.custom.SleepingBagBlock;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienPotions;
import com.greatorator.tolkienmobs.init.TolkienVillagers;
import com.greatorator.tolkienmobs.item.custom.DwarvenHammerItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public class TolkienServerEvents {
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

    @SubscribeEvent
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, TolkienItems.PIPEWEED_ITEM.get(), TolkienPotions.ENT_DRAUGHT);
        builder.addMix(Potions.AWKWARD, TolkienItems.INGOT_MITHRIL.get(), TolkienPotions.PORTABLE_REPAIR);
        builder.addMix(Potions.AWKWARD, TolkienItems.GEM_AMMOLITE.get(), TolkienPotions.ERU_BLESSING);
        builder.addMix(Potions.AWKWARD, TolkienItems.GOLDEN_TREE_ACORN.get(), TolkienPotions.ELF_NIMBLENESS);
        builder.addMix(Potions.AWKWARD, TolkienItems.LEMBAS.get(), TolkienPotions.ELF_VITALITY);
    }

    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        Level level = event.getEntity().level();

        if(event.getNewSpawn() != null)
        {
            Block block = level.getBlockState(event.getNewSpawn()).getBlock();

            if(!level.isClientSide && block instanceof SleepingBagBlock && !event.isForced())
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onHammerUsage(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();

        if(mainHandItem.getItem() instanceof DwarvenHammerItem hammer && player instanceof ServerPlayer serverPlayer) {
            BlockPos initialBlockPos = event.getPos();
            if(HARVESTED_BLOCKS.contains(initialBlockPos)) {
                return;
            }

            for(BlockPos pos : DwarvenHammerItem.getBlocksToBeDestroyed(2, initialBlockPos, serverPlayer)) {
                if(pos == initialBlockPos || !hammer.isCorrectToolForDrops(mainHandItem, event.getLevel().getBlockState(pos))) {
                    continue;
                }

                HARVESTED_BLOCKS.add(pos);
                serverPlayer.gameMode.destroyBlock(pos);
                HARVESTED_BLOCKS.remove(pos);
            }
        }
    }
    @SubscribeEvent
    public static void onVillagerTradesEvent(VillagerTradesEvent event) {
        if(event.getType() == TolkienVillagers.COIN_TRADER_PROFESSION.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_BRONZE.get(), 64),
                    new ItemStack(TolkienItems.ITEM_COIN_SILVER.get(), 1), 16, 25, 0.00f
            ));
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_SILVER.get(), 1),
                    new ItemStack(TolkienItems.ITEM_COIN_BRONZE.get(), 64), 16, 25, 0.00f
            ));
            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_SILVER.get(), 64),
                    new ItemStack(TolkienItems.ITEM_COIN_GOLD.get(), 1), 16, 25, 0.00f
            ));
            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_GOLD.get(), 1),
                    new ItemStack(TolkienItems.ITEM_COIN_SILVER.get(), 64), 16, 25, 0.00f
            ));
            trades.get(3).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_GOLD.get(), 64),
                    new ItemStack(TolkienItems.ITEM_COIN_MITHRIL.get(), 1), 16, 25, 0.00f
            ));
            trades.get(3).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_MITHRIL.get(), 1),
                    new ItemStack(TolkienItems.ITEM_COIN_GOLD.get(), 64), 16, 25, 0.00f
            ));
        }
        if(event.getType() == TolkienVillagers.GROCERY_STORE_PROFESSION.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_BRONZE.get(), 13),
                    new ItemStack(TolkienItems.HONEY_CAKE.get(), 3), 16, 25, 0.50F
            ));
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_BRONZE.get(), 8),
                    new ItemStack(TolkienItems.CRAM.get(), 3), 16, 25, 0.50F
            ));
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_BRONZE.get(), 6),
                    new ItemStack(TolkienItems.FOOD_HONEY.get(), 2), 16, 25, 0.50F
            ));
            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_SILVER.get(), 8),
                    new ItemStack(TolkienItems.LEMBAS.get(), 2), 16, 50, 0.50F
            ));
            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_SILVER.get(), 4),
                    new ItemStack(TolkienItems.MIRUVOR.get(), 2), 16, 50, 0.50F
            ));
            trades.get(3).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_GOLD.get(), 3),
                    new ItemStack(TolkienItems.DRINK_ERU_BLESSING.get(), 1), 16, 75, 0.50F
            ));
            trades.get(3).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_GOLD.get(), 4),
                    new ItemStack(TolkienItems.DRINK_ELF_VITALITY.get(), 1), 16, 75, 0.50F
            ));
            trades.get(4).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_MITHRIL.get(), 2),
                    new ItemStack(TolkienItems.DRINK_PERSONAL_BLACKSMITH.get(), 2), 16, 100, 0.50F
            ));
            trades.get(4).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_MITHRIL.get(), 2),
                    new ItemStack(TolkienItems.DRINK_ENT_DRAUGHT.get(), 2), 16, 100, 0.50F
            ));
        }
        if(event.getType() == TolkienVillagers.PET_MERCHANT_PROFESSION.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_BRONZE.get(), 36),
                    new ItemStack(TolkienItems.HONEY_CAKE.get(), 6), 16, 25, 0.50F
            ));
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_BRONZE.get(), 24),
                    new ItemStack(TolkienItems.CRAM.get(), 4), 16, 25, 0.50F
            ));
            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_SILVER.get(), 3),
                    new ItemStack(TolkienItems.FOOD_HONEY.get(), 6), 16, 50, 0.50F
            ));
            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(TolkienItems.ITEM_COIN_SILVER.get(), 5),
                    new ItemStack(TolkienItems.LEMBAS.get(), 4), 16, 50, 0.50F
            ));
        }
        if(event.getType() == TolkienVillagers.TRINKET_SMITH_PROFESSION.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        }
        if(event.getType() == TolkienVillagers.TRINKET_TAILOR_PROFESSION.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        }
        if(event.getType() == TolkienVillagers.JUNK_TRADER_PROFESSION.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        }
    }
}
