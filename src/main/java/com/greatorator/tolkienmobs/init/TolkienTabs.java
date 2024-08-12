package com.greatorator.tolkienmobs.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final Supplier<CreativeModeTab> TOLKIEN_MATS = CREATIVE_MODE_TAB.register("tolkienmobs_tab_mats",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienItems.RAW_MITHRIL.get()))
                    .title(Component.translatable("itemGroup.tolkienmobs.mats"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienBlocks.ORE_MITHRIL);
                        output.accept(TolkienBlocks.ORE_END_MITHRIL);
                        output.accept(TolkienBlocks.ORE_NETHER_MITHRIL);
                        output.accept(TolkienBlocks.ORE_DEEPSLATE_MITHRIL);
                        output.accept(TolkienItems.DUST_MITHRIL);
                        output.accept(TolkienItems.RAW_MITHRIL);
                        output.accept(TolkienItems.INGOT_MITHRIL);
                        output.accept(TolkienItems.NUGGET_MITHRIL);
                        output.accept(TolkienBlocks.ORE_MORGULIRON);
                        output.accept(TolkienBlocks.ORE_END_MORGULIRON);
                        output.accept(TolkienBlocks.ORE_NETHER_MORGULIRON);
                        output.accept(TolkienBlocks.ORE_DEEPSLATE_MORGULIRON);
                        output.accept(TolkienItems.DUST_MORGULIRON);
                        output.accept(TolkienItems.RAW_MORGULIRON);
                        output.accept(TolkienItems.INGOT_MORGULIRON);
                        output.accept(TolkienItems.NUGGET_MORGULIRON);
                        output.accept(TolkienBlocks.ORE_AMMOLITE);
                        output.accept(TolkienBlocks.ORE_END_AMMOLITE);
                        output.accept(TolkienBlocks.ORE_NETHER_AMMOLITE);
                        output.accept(TolkienBlocks.ORE_DEEPSLATE_AMMOLITE);
                        output.accept(TolkienItems.GEM_AMMOLITE);
                        output.accept(TolkienBlocks.PLANKS_MALLORN);
                        output.accept(TolkienBlocks.LEAVES_MALLORN);
                        output.accept(TolkienBlocks.PLANKS_MIRKWOOD);
                        output.accept(TolkienBlocks.LEAVES_MIRKWOOD);
                        output.accept(TolkienBlocks.PLANKS_CULUMALDA);
                        output.accept(TolkienBlocks.LEAVES_CULUMALDA);
                        output.accept(TolkienBlocks.PLANKS_LEBETHRON);
                        output.accept(TolkienBlocks.LEAVES_LEBETHRON);
                        output.accept(TolkienBlocks.PLANKS_FANGORNOAK);
                        output.accept(TolkienBlocks.LEAVES_FANGORNOAK);
                        output.accept(TolkienBlocks.PLANKS_DEADWOOD);
                        output.accept(TolkienItems.CREBAIN_FEATHER);
                        output.accept(TolkienItems.BIRD_FEATHER);
                        output.accept(TolkienItems.MUMAKIL_LEATHER);
                        output.accept(TolkienItems.MONSTER_FUR);
                        output.accept(TolkienItems.BOTTLE_FANCY);
                        output.accept(TolkienItems.GOLEM_STONE);
                        output.accept(TolkienItems.GOLEM_STONE_EARTH);
                        output.accept(TolkienItems.GOLEM_STONE_AIR);
                        output.accept(TolkienItems.GOLEM_STONE_FIRE);
                        output.accept(TolkienItems.GOLEM_STONE_WATER);
                        output.accept(TolkienItems.GOLEM_STONE_SUMMON);
                    }).build());
    public static final Supplier<CreativeModeTab> TOLKIEN_QUEST = CREATIVE_MODE_TAB.register("tolkienmobs_tab_quest",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienItems.ITEM_FORTRESSMAP.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MODID, "tolkienmobs_tab_mats"))
                    .title(Component.translatable("itemGroup.tolkienmobs.quest"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienItems.ITEM_BERYL);
                        output.accept(TolkienItems.ITEM_FORTRESSMAP);
                        output.accept(TolkienItems.ITEM_WATCHERHEART);
                        output.accept(TolkienItems.ITEM_WATCHERHEART_CRACKED);
                        output.accept(TolkienItems.ITEM_KEYSTONE);
                        output.accept(TolkienItems.ITEM_DARKSADDLE);
                        output.accept(TolkienItems.ITEM_ARTIFACT);
                        output.accept(TolkienItems.ITEM_BLANKPAPER);
                        output.accept(TolkienItems.ITEM_FANCYARMOR);
                        output.accept(TolkienItems.ITEM_FANCYCLOTH);
                        output.accept(TolkienItems.ITEM_FANCYHAMMER);
                        output.accept(TolkienItems.ITEM_FANCYHELM);
                        output.accept(TolkienItems.ITEM_FANCYKEY);
                        output.accept(TolkienItems.ITEM_FANCYPICK);
                        output.accept(TolkienItems.ITEM_FANCYSHIELD);
                        output.accept(TolkienItems.ITEM_FANCYSHIELD2);
                        output.accept(TolkienItems.ITEM_FANCYSWORD);
                        output.accept(TolkienItems.ITEM_FANCYSWORD2);
                        output.accept(TolkienItems.ITEM_LETTER);
                        output.accept(TolkienItems.ITEM_SCROLL);
                        output.accept(TolkienItems.ITEM_SCROLL2);
                        output.accept(TolkienItems.ITEM_SPECIALFLOWER);
                        output.accept(TolkienItems.ITEM_STORYBOOK);
                        output.accept(TolkienItems.ITEM_STORYBOOK2);
                        output.accept(TolkienItems.ITEM_STORYBOOK3);
                        output.accept(TolkienItems.ITEM_STORYBOOK4);
                        output.accept(TolkienItems.ITEM_WORNARMOR);
                        output.accept(TolkienItems.ITEM_WORNHELM);
                        output.accept(TolkienItems.ITEM_WORNKEY);
                        output.accept(TolkienItems.ITEM_WORNPICK);
                        output.accept(TolkienItems.ITEM_WORNSHIELD);
                        output.accept(TolkienItems.ITEM_WORNSHIELD2);
                        output.accept(TolkienItems.ITEM_WORNSWORD);
                        output.accept(TolkienItems.ITEM_WOVENBASKET);
                        output.accept(TolkienItems.ITEM_WRITTENPAPER);
                        output.accept(TolkienItems.ITEM_PUNGENTHERB);
                        output.accept(TolkienItems.ITEM_LOCKPICK);
                        output.accept(TolkienItems.ITEM_BROKENSWORD);
                        output.accept(TolkienItems.ITEM_REFORGEDSWORD);
                        output.accept(TolkienItems.ITEM_MAGIC_CLOTH);
                        output.accept(TolkienItems.ITEM_KEYFRAGMENT);
                        output.accept(TolkienItems.ITEM_OILYKEY);
                        output.accept(TolkienItems.ITEM_MITHRILNUGGET);
                        output.accept(TolkienItems.ITEM_REMAINS);
                        output.accept(TolkienItems.ITEM_RUNE_STONE);
                        output.accept(TolkienItems.ITEM_COIN_BRONZE);
                        output.accept(TolkienItems.ITEM_COIN_SILVER);
                        output.accept(TolkienItems.ITEM_COIN_GOLD);
                        output.accept(TolkienItems.ITEM_COIN_MITHRIL);
                        output.accept(TolkienItems.ITEM_DARKSIGIL);
                        output.accept(TolkienItems.ITEM_FACTIONCOIN);
                        output.accept(TolkienItems.ITEM_FACTIONTOKEN);
                        output.accept(TolkienItems.ITEM_CAVECOMPLETE);
                        output.accept(TolkienItems.ITEM_WATCHERCOMPLETE);
                        output.accept(TolkienItems.ITEM_TOKEN_EASTERN_ALLIANCE);
                        output.accept(TolkienItems.ITEM_TOKEN_WESTERN_ALLIANCE);
                    }).build());

    public static final Supplier<CreativeModeTab> TOLKIEN_BLOCKS = CREATIVE_MODE_TAB.register("tolkienmobs_tab_deco",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienBlocks.RAW_MITHRIL_BLOCK))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MODID, "tolkienmobs_tab_quest"))
                    .title(Component.translatable("itemGroup.tolkienmobs.deco"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienBlocks.BLOCK_MITHRIL);
                        output.accept(TolkienBlocks.RAW_MITHRIL_BLOCK);
                        output.accept(TolkienBlocks.BLOCK_MORGULIRON);
                        output.accept(TolkienBlocks.RAW_MORGULIRON_BLOCK);
                        output.accept(TolkienBlocks.BLOCK_AMMOLITE);
                        output.accept(TolkienBlocks.STAIRS_MALLORN);
                        output.accept(TolkienBlocks.SLAB_MALLORN);
                        output.accept(TolkienBlocks.MALLORN_BUTTON);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_MALLORN);
                        output.accept(TolkienBlocks.FENCE_MALLORN);
                        output.accept(TolkienBlocks.FENCE_GATE_MALLORN);
                        output.accept(TolkienBlocks.DOOR_MALLORN);
                        output.accept(TolkienBlocks.TRAPDOOR_MALLORN);
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
