package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.handler.TrinketComponent;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienVillagers;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public class TolkienEvents {
    @SubscribeEvent
    public static void onVillagerTradesEvent(VillagerTradesEvent event) {
        if(event.getType().equals(TolkienVillagers.COIN_TRADER_PROFESSION.value())) {
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
        if(event.getType().equals(TolkienVillagers.GROCERY_STORE_PROFESSION.value())) {
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
        if(event.getType().equals(TolkienVillagers.PET_MERCHANT_PROFESSION.value())) {
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
        if(event.getType().equals(TolkienVillagers.TRINKET_SMITH_PROFESSION.value())) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        }
        if(event.getType().equals(TolkienVillagers.TRINKET_TAILOR_PROFESSION.value())) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        }
        if(event.getType().equals(TolkienVillagers.JUNK_TRADER_PROFESSION.value())) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        }
    }
}
