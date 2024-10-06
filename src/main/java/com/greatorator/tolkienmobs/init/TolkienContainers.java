package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.containers.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienContainers {
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CoinPouchContainer>> COIN_POUCH_CONTAINER = CONTAINERS.register("coin_pouch_container",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CoinPouchContainer(windowId, inv, inv.player, data)));
    public static final DeferredHolder<MenuType<?>, MenuType<KeyRingContainer>> KEY_RING_CONTAINER = CONTAINERS.register("key_ring_container",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new KeyRingContainer(windowId, inv, inv.player, data)));
    public static final DeferredHolder<MenuType<?>, MenuType<KeyItemContainer>> KEY_ITEM_CONTAINER = CONTAINERS.register("key_item_container",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new KeyItemContainer(windowId, inv, inv.player, data)));
    public static final DeferredHolder<MenuType<?>, MenuType<KeyCodeContainer>> KEY_CODE_CONTAINER = CONTAINERS.register("key_code_container",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new KeyCodeContainer(windowId, inv, inv.player, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<TrinketTableContainer>> TRINKET_TABLE_CONTAINER = registerMenuType("trinket_table_container", TrinketTableContainer::new);
    public static final DeferredHolder<MenuType<?>, MenuType<PiggyBankContainer>> PIGGY_BANK_CONTAINER = registerMenuType("block_piggybank_container", PiggyBankContainer::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory) {
        return CONTAINERS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
