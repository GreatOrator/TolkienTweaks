package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.containers.CoinPouchContainer;
import com.greatorator.tolkienmobs.containers.handlers.CoinPouchItemStackHandler;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.item.custom.CoinPouchItem;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder("tolkienmobs_coins")
@PrefixGameTestTemplate(false)
public class CoinPouchGameTests {
    @GameTest(template = "coin_pouch_arena")
    public static void thresholdsCascadesAndSplitStacks(GameTestHelper helper) {
        Item[] tiers = tiers();
        for (int tier = 0; tier < 3; tier++) {
            ItemStack pouch = pouch();
            var handler = handler(pouch);
            ItemStack below = new ItemStack(tiers[tier], 63);
            CoinPouchItem.addCoinToInventory(pouch, below);
            helper.assertTrue(below.isEmpty() && count(pouch, tiers[tier]) == 63
                    && count(pouch, tiers[tier + 1]) == 0, "63 coins must not upgrade at tier " + tier);
            ItemStack pickup = new ItemStack(tiers[tier], 2);
            long before = value(pouch) + value(pickup);
            CoinPouchItem.addCoinToInventory(pouch, pickup);
            helper.assertTrue(pickup.isEmpty() && count(pouch, tiers[tier]) == 1
                    && count(pouch, tiers[tier + 1]) == 1, "65 coins must become one higher coin plus one remainder");
            helper.assertTrue(value(pouch) == before, "Conversion must conserve value");
        }
        ItemStack pouch = pouch();
        var handler = handler(pouch);
        handler.setStackInSlot(0, new ItemStack(tiers[0], 32));
        handler.setStackInSlot(1, new ItemStack(tiers[0], 31));
        handler.setStackInSlot(2, new ItemStack(tiers[1], 63));
        handler.setStackInSlot(3, new ItemStack(tiers[2], 63));
        CoinPouchItem.addCoinToInventory(pouch, new ItemStack(tiers[0]));
        helper.assertTrue(count(pouch, tiers[0]) == 0 && count(pouch, tiers[1]) == 0
                && count(pouch, tiers[2]) == 0 && count(pouch, tiers[3]) == 1, "Split stacks must carry all the way to mithril");
        CoinPouchItem.addCoinToInventory(pouch, new ItemStack(tiers[3], 63));
        helper.assertTrue(count(pouch, tiers[3]) == 64, "Mithril must not convert further");
        ItemStack bulkPouch = pouch();
        for (int i = 0; i < 64; i++) CoinPouchItem.addCoinToInventory(bulkPouch, new ItemStack(tiers[0], 64));
        helper.assertTrue(count(bulkPouch, tiers[2]) == 1 && count(bulkPouch, tiers[0]) == 0
                && count(bulkPouch, tiers[1]) == 0, "4096 bronze across pickups must become one gold");
        helper.succeed();
    }

    @GameTest(template = "coin_pouch_arena")
    public static void fullPouchesTagsAndPersistence(GameTestHelper helper) {
        Item[] tiers = tiers();
        ItemStack pouch = pouch();
        var handler = handler(pouch);
        for (int slot = 0; slot < handler.getSlots(); slot++) handler.setStackInSlot(slot, new ItemStack(tiers[3], 64));
        ItemStack pickup = new ItemStack(tiers[0], 64);
        long before = value(pouch) + value(pickup);
        CoinPouchItem.addCoinToInventory(pouch, pickup);
        helper.assertTrue(pickup.getCount() == 64 && value(pouch) + value(pickup) == before, "Full pouch must leave pickup intact");

        handler.setStackInSlot(0, new ItemStack(tiers[0], 63));
        pickup = new ItemStack(tiers[0], 2);
        before = value(pouch) + value(pickup);
        CoinPouchItem.addCoinToInventory(pouch, pickup);
        helper.assertTrue(pickup.getCount() == 1 && count(pouch, tiers[0]) == 0 && count(pouch, tiers[1]) == 1,
                "Full pouch must convert the accepted coin and keep the unaccepted remainder");
        helper.assertTrue(value(pouch) + value(pickup) == before, "Partial pickup must conserve value");

        // Existing full lower-tier stacks must free space before collection, even with no empty slot.
        for (int slot = 0; slot < handler.getSlots(); slot++) handler.setStackInSlot(slot, new ItemStack(tiers[0], 64));
        pickup = new ItemStack(tiers[0], 64);
        CoinPouchItem.addCoinToInventory(pouch, pickup);
        helper.assertTrue(pickup.isEmpty() && count(pouch, tiers[1]) == 16 && count(pouch, tiers[0]) == 0,
                "Stored full stacks must upgrade and free room for the pickup");

        ItemStack taggedPouch = pouch();
        ItemStack sigil = new ItemStack(TolkienItems.ITEM_DARKSIGIL.get(), 64);
        sigil.set(DataComponents.CUSTOM_NAME, Component.literal("Saved sigils"));
        ItemStack expectedSigil = sigil.copy();
        CoinPouchItem.addCoinToInventory(taggedPouch, sigil);
        helper.assertTrue(sigil.isEmpty() && ItemStack.matches(handler(taggedPouch).getStackInSlot(0), expectedSigil),
                "Other tagged coins must retain item, count and components");
        ItemStack dirt = new ItemStack(Items.DIRT, 7);
        CoinPouchItem.addCoinToInventory(taggedPouch, dirt);
        helper.assertTrue(dirt.getCount() == 7, "Noncoins must stay outside the pouch");

        // Tagged tokens with max stack size one may fill only the remaining slot.
        for (int slot = 1; slot < 14; slot++) handler(taggedPouch).setStackInSlot(slot, new ItemStack(tiers[3], 64));
        ItemStack tokens = new ItemStack(TolkienItems.ITEM_TOKEN_EASTERN_ALLIANCE.get(), 2);
        CoinPouchItem.addCoinToInventory(taggedPouch, tokens);
        helper.assertTrue(tokens.getCount() == 1 && count(taggedPouch, TolkienItems.ITEM_TOKEN_EASTERN_ALLIANCE.get()) == 1,
                "Insertion must respect token limits and retain overflow");

        pouch.set(DataComponents.CUSTOM_NAME, Component.literal("My savings"));
        CoinPouchItem.setActive(pouch, true);
        var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var restored = ItemStack.CODEC.parse(ops, ItemStack.CODEC.encodeStart(ops, pouch).getOrThrow()).getOrThrow();
        helper.assertTrue(value(restored) == value(pouch) && count(restored, tiers[1]) == 16
                && CoinPouchItem.getActive(restored) && restored.getHoverName().equals(pouch.getHoverName()),
                "Converted coins and pouch settings must survive saving and reopening");
        helper.succeed();
    }

    @GameTest(template = "coin_pouch_arena")
    public static void inventoryCollectionAndRealPickup(GameTestHelper helper) {
        var player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "CoinTest"));
        player.getInventory().clearContent();
        var pouch = pouch();
        var pouchItem = (CoinPouchItem) pouch.getItem();
        Item[] tiers = tiers();
        for (int tier = 0; tier < 3; tier++) handler(pouch).setStackInSlot(tier, new ItemStack(tiers[tier], 63));
        player.getInventory().setItem(0, pouch);
        player.getInventory().setItem(1, new ItemStack(tiers[0]));
        player.getInventory().setItem(4, new ItemStack(TolkienItems.ITEM_DARKSIGIL.get(), 2));
        player.getInventory().setItem(5, new ItemStack(Items.DIRT, 7));
        pouchItem.inventoryTick(pouch, helper.getLevel(), player, 0, true);
        helper.assertTrue(player.getInventory().getItem(1).getCount() == 1 && count(pouch, tiers[0]) == 63,
                "Inactive pouch must not collect or convert");
        CoinPouchItem.setActive(pouch, true);
        pouchItem.inventoryTick(pouch, helper.getLevel(), player, 0, true);
        helper.assertTrue(player.getInventory().getItem(1).isEmpty() && count(pouch, tiers[3]) == 1
                && count(pouch, tiers[0]) == 0 && count(pouch, tiers[1]) == 0 && count(pouch, tiers[2]) == 0,
                "Inventory tick must consume the pickup and carry through all tiers");
        helper.assertTrue(player.getInventory().getItem(4).isEmpty() && count(pouch, TolkienItems.ITEM_DARKSIGIL.get()) == 2
                && player.getInventory().getItem(5).getCount() == 7, "Collection must use the coin tag");
        var secondPouch = pouch();
        CoinPouchItem.setActive(secondPouch, true);
        player.getInventory().setItem(2, secondPouch);
        var drop = new ItemEntity(helper.getLevel(), player.getX(), player.getY(), player.getZ(), new ItemStack(tiers[0]));
        helper.getLevel().addFreshEntity(drop);
        drop.playerTouch(player);
        helper.assertTrue(drop.isRemoved(), "Player must actually pick up the dropped coin");
        pouchItem.inventoryTick(pouch, helper.getLevel(), player, 0, true);
        pouchItem.inventoryTick(secondPouch, helper.getLevel(), player, 2, false);
        pouchItem.inventoryTick(pouch, helper.getLevel(), player, 0, true);
        helper.assertTrue(count(pouch, tiers[0]) == 1 && count(pouch, tiers[3]) == 1 && value(secondPouch) == 0,
                "Repeated ticks and multiple pouches must not duplicate collected value");
        player.getInventory().clearContent();
        helper.succeed();
    }

    private static Item[] tiers() {
        return new Item[]{TolkienItems.ITEM_COIN_BRONZE.get(), TolkienItems.ITEM_COIN_SILVER.get(),
                TolkienItems.ITEM_COIN_GOLD.get(), TolkienItems.ITEM_COIN_MITHRIL.get()};
    }

    private static ItemStack pouch() { return new ItemStack(TolkienItems.COIN_POUCH.get()); }

    private static CoinPouchItemStackHandler handler(ItemStack pouch) {
        return new CoinPouchItemStackHandler(CoinPouchContainer.SLOTS, pouch);
    }

    private static int count(ItemStack pouch, Item item) {
        int count = 0;
        var handler = handler(pouch);
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    private static long value(ItemStack stack) {
        Item[] tiers = tiers();
        long total = 0, weight = 1;
        for (Item tier : tiers) {
            total += weight * (stack.getItem() instanceof CoinPouchItem ? count(stack, tier) : stack.is(tier) ? stack.getCount() : 0);
            weight *= 64;
        }
        return total;
    }
}
