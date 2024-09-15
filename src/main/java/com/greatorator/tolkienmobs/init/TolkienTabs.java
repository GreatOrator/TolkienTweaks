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
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienItems.INGOT_MITHRIL.get()))
                    .title(Component.translatable("itemGroup.tolkienmobs.mats"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienItems.RAW_MITHRIL);
                        output.accept(TolkienItems.RAW_MORGULIRON);
                        output.accept(TolkienItems.GEM_AMMOLITE);
                        output.accept(TolkienItems.DUST_MITHRIL);
                        output.accept(TolkienItems.DUST_MORGULIRON);
                        output.accept(TolkienItems.NUGGET_MITHRIL);
                        output.accept(TolkienItems.NUGGET_MORGULIRON);
                        output.accept(TolkienItems.INGOT_MITHRIL);
                        output.accept(TolkienItems.INGOT_MORGULIRON);
                        output.accept(TolkienFluids.MITHRIL_FLUID_BUCKET);
                        output.accept(TolkienFluids.MORGULIRON_FLUID_BUCKET);
                        output.accept(TolkienItems.CREBAIN_FEATHER);
                        output.accept(TolkienItems.BIRD_FEATHER);
                        output.accept(TolkienItems.MUMAKIL_LEATHER);
                        output.accept(TolkienItems.MONSTER_FUR);
                        output.accept(TolkienItems.BOTTLE_FANCY);
                        output.accept(TolkienItems.FOOD_HONEY);
                        output.accept(TolkienItems.MONSTER_FLESH);
                        output.accept(TolkienItems.INSECT);
                        output.accept(TolkienItems.GOLDEN_INSECT);
                        output.accept(TolkienItems.TREE_ACORN);
                        output.accept(TolkienItems.GOLDEN_TREE_ACORN);
                        output.accept(TolkienItems.GOLEM_STONE);
                        output.accept(TolkienItems.GOLEM_STONE_EARTH);
                        output.accept(TolkienItems.GOLEM_STONE_AIR);
                        output.accept(TolkienItems.GOLEM_STONE_FIRE);
                        output.accept(TolkienItems.GOLEM_STONE_WATER);
                        output.accept(TolkienItems.GOLEM_STONE_SUMMON);
                        output.accept(TolkienItems.ITEM_BACKPACK_UPGRADE_BASE);
                        output.accept(TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE);
                        output.accept(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID);
                        output.accept(TolkienItems.ITEM_BACKPACK_UPGRADE_CRAFTING);
                        output.accept(TolkienItems.ITEM_BACKPACK_UPGRADE_SLEEPING);
                        output.accept(TolkienItems.ITEM_BACKPACK_UPGRADE_CAMPFIRE);

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
                        output.accept(TolkienItems.BRONZE_KEY);
                        output.accept(TolkienItems.SILVER_KEY);
                        output.accept(TolkienItems.GOLD_KEY);
                        output.accept(TolkienItems.MITHRIL_KEY);
                        output.accept(TolkienItems.MASTER_KEY);
                    }).build());

    public static final Supplier<CreativeModeTab> TOLKIEN_FOODS = CREATIVE_MODE_TAB.register("tolkienmobs_tab_food",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienItems.LEMBAS.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MODID, "tolkienmobs_tab_quest"))
                    .title(Component.translatable("itemGroup.tolkienmobs.food"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienItems.LEMBAS);
                        output.accept(TolkienItems.CRAM);
                        output.accept(TolkienItems.HONEY_CAKE);
                        output.accept(TolkienItems.FOOD_HONEY);
                        output.accept(TolkienItems.MONSTER_FLESH);
                        output.accept(TolkienItems.INSECT);
                        output.accept(TolkienItems.GOLDEN_INSECT);
                        output.accept(TolkienItems.TREE_ACORN);
                        output.accept(TolkienItems.GOLDEN_TREE_ACORN);
                        output.accept(TolkienItems.MIRUVOR);
                        output.accept(TolkienItems.GROG);
                        output.accept(TolkienItems.DRINK_ELF_FLEETFOOT);
                        output.accept(TolkienItems.DRINK_ENT_DRAUGHT);
                        output.accept(TolkienItems.DRINK_ELF_VITALITY);
                        output.accept(TolkienItems.DRINK_ERU_BLESSING);
                        output.accept(TolkienItems.DRINK_PERSONAL_BLACKSMITH);

                    }).build());

    public static final Supplier<CreativeModeTab> TOLKIEN_FUNCTION = CREATIVE_MODE_TAB.register("tolkienmobs_tab_function",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienBlocks.MALLORN_SIGN.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MODID, "tolkienmobs_tab_food"))
                    .title(Component.translatable("itemGroup.tolkienmobs.function"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienBlocks.SILMARIL_LANTERN);
                        output.accept(TolkienBlocks.ELVEN_LANTERN);
                        output.accept(TolkienBlocks.MORGUL_LANTERN);
                        output.accept(TolkienBlocks.TORCH_MALLORN);
                        output.accept(TolkienBlocks.TORCH_MIRKWOOD);
                        output.accept(TolkienBlocks.TORCH_CULUMALDA);
                        output.accept(TolkienBlocks.TORCH_LEBETHRON);
                        output.accept(TolkienBlocks.TORCH_FANGORNOAK);
                        output.accept(TolkienBlocks.TORCH_DEADWOOD);
                        output.accept(TolkienBlocks.MALLORN_SIGN);
                        output.accept(TolkienBlocks.MIRKWOOD_SIGN);
                        output.accept(TolkienBlocks.CULUMALDA_SIGN);
                        output.accept(TolkienBlocks.LEBETHRON_SIGN);
                        output.accept(TolkienBlocks.FANGORNOAK_SIGN);
                        output.accept(TolkienBlocks.DEADWOOD_SIGN);
                        output.accept(TolkienBlocks.MALLORN_HANGING_SIGN);
                        output.accept(TolkienBlocks.MIRKWOOD_HANGING_SIGN);
                        output.accept(TolkienBlocks.CULUMALDA_HANGING_SIGN);
                        output.accept(TolkienBlocks.LEBETHRON_HANGING_SIGN);
                        output.accept(TolkienBlocks.FANGORNOAK_HANGING_SIGN);
                        output.accept(TolkienBlocks.DEADWOOD_HANGING_SIGN);
                        output.accept(TolkienBlocks.LADDER_MALLORN.asItem());
                        output.accept(TolkienBlocks.LADDER_MIRKWOOD.asItem());
                        output.accept(TolkienBlocks.LADDER_CULUMALDA.asItem());
                        output.accept(TolkienBlocks.LADDER_LEBETHRON.asItem());
                        output.accept(TolkienBlocks.LADDER_FANGORNOAK.asItem());
                        output.accept(TolkienBlocks.LADDER_DEADWOOD.asItem());
                        output.accept(TolkienBlocks.SLEEPING_BAG_BLUE);
                        output.accept(TolkienBlocks.SLEEPING_BAG_RED);
                        output.accept(TolkienBlocks.SLEEPING_BAG_BLACK);
                        output.accept(TolkienBlocks.SLEEPING_BAG_BROWN);
                        output.accept(TolkienBlocks.SLEEPING_BAG_CYAN);
                        output.accept(TolkienBlocks.SLEEPING_BAG_GRAY);
                        output.accept(TolkienBlocks.SLEEPING_BAG_GREEN);
                        output.accept(TolkienBlocks.SLEEPING_BAG_LIGHT_BLUE);
                        output.accept(TolkienBlocks.SLEEPING_BAG_LIGHT_GRAY);
                        output.accept(TolkienBlocks.SLEEPING_BAG_LIME);
                        output.accept(TolkienBlocks.SLEEPING_BAG_MAGENTA);
                        output.accept(TolkienBlocks.SLEEPING_BAG_ORANGE);
                        output.accept(TolkienBlocks.SLEEPING_BAG_PINK);
                        output.accept(TolkienBlocks.SLEEPING_BAG_PURPLE);
                        output.accept(TolkienBlocks.SLEEPING_BAG_WHITE);
                        output.accept(TolkienBlocks.SLEEPING_BAG_YELLOW);
                    }).build());

    public static final Supplier<CreativeModeTab> TOLKIEN_BLOCKS = CREATIVE_MODE_TAB.register("tolkienmobs_tab_deco",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienBlocks.BLOCK_AMMOLITE))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MODID, "tolkienmobs_tab_function"))
                    .title(Component.translatable("itemGroup.tolkienmobs.deco"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienBlocks.LOG_MALLORN);
                        output.accept(TolkienBlocks.WOOD_MALLORN);
                        output.accept(TolkienBlocks.STRIPPED_MALLORN_LOG);
                        output.accept(TolkienBlocks.STRIPPED_MALLORN_WOOD);
                        output.accept(TolkienBlocks.PLANKS_MALLORN);
                        output.accept(TolkienBlocks.STAIRS_MALLORN);
                        output.accept(TolkienBlocks.SLAB_MALLORN);
                        output.accept(TolkienBlocks.FENCE_MALLORN);
                        output.accept(TolkienBlocks.FENCE_GATE_MALLORN);
                        output.accept(TolkienBlocks.DOOR_MALLORN);
                        output.accept(TolkienBlocks.TRAPDOOR_MALLORN);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_MALLORN);
                        output.accept(TolkienBlocks.MALLORN_BUTTON);
                        output.accept(TolkienBlocks.BARREL_MALLORN);
                        output.accept(TolkienBlocks.LOG_MIRKWOOD);
                        output.accept(TolkienBlocks.WOOD_MIRKWOOD);
                        output.accept(TolkienBlocks.STRIPPED_MIRKWOOD_LOG);
                        output.accept(TolkienBlocks.STRIPPED_MIRKWOOD_WOOD);
                        output.accept(TolkienBlocks.PLANKS_MIRKWOOD);
                        output.accept(TolkienBlocks.STAIRS_MIRKWOOD);
                        output.accept(TolkienBlocks.SLAB_MIRKWOOD);
                        output.accept(TolkienBlocks.FENCE_MIRKWOOD);
                        output.accept(TolkienBlocks.FENCE_GATE_MIRKWOOD);
                        output.accept(TolkienBlocks.DOOR_MIRKWOOD);
                        output.accept(TolkienBlocks.TRAPDOOR_MIRKWOOD);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_MIRKWOOD);
                        output.accept(TolkienBlocks.MIRKWOOD_BUTTON);
                        output.accept(TolkienBlocks.BARREL_MIRKWOOD);
                        output.accept(TolkienBlocks.LOG_CULUMALDA);
                        output.accept(TolkienBlocks.WOOD_CULUMALDA);
                        output.accept(TolkienBlocks.STRIPPED_CULUMALDA_LOG);
                        output.accept(TolkienBlocks.STRIPPED_CULUMALDA_WOOD);
                        output.accept(TolkienBlocks.PLANKS_CULUMALDA);
                        output.accept(TolkienBlocks.STAIRS_CULUMALDA);
                        output.accept(TolkienBlocks.SLAB_CULUMALDA);
                        output.accept(TolkienBlocks.FENCE_CULUMALDA);
                        output.accept(TolkienBlocks.FENCE_GATE_CULUMALDA);
                        output.accept(TolkienBlocks.DOOR_CULUMALDA);
                        output.accept(TolkienBlocks.TRAPDOOR_CULUMALDA);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_CULUMALDA);
                        output.accept(TolkienBlocks.CULUMALDA_BUTTON);
                        output.accept(TolkienBlocks.BARREL_CULUMALDA);
                        output.accept(TolkienBlocks.LOG_LEBETHRON);
                        output.accept(TolkienBlocks.WOOD_LEBETHRON);
                        output.accept(TolkienBlocks.STRIPPED_LEBETHRON_LOG);
                        output.accept(TolkienBlocks.STRIPPED_LEBETHRON_WOOD);
                        output.accept(TolkienBlocks.PLANKS_LEBETHRON);
                        output.accept(TolkienBlocks.STAIRS_LEBETHRON);
                        output.accept(TolkienBlocks.SLAB_LEBETHRON);
                        output.accept(TolkienBlocks.FENCE_LEBETHRON);
                        output.accept(TolkienBlocks.FENCE_GATE_LEBETHRON);
                        output.accept(TolkienBlocks.DOOR_LEBETHRON);
                        output.accept(TolkienBlocks.TRAPDOOR_LEBETHRON);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_LEBETHRON);
                        output.accept(TolkienBlocks.LEBETHRON_BUTTON);
                        output.accept(TolkienBlocks.BARREL_LEBETHRON);
                        output.accept(TolkienBlocks.LOG_FANGORNOAK);
                        output.accept(TolkienBlocks.WOOD_FANGORNOAK);
                        output.accept(TolkienBlocks.STRIPPED_FANGORNOAK_LOG);
                        output.accept(TolkienBlocks.STRIPPED_FANGORNOAK_WOOD);
                        output.accept(TolkienBlocks.PLANKS_FANGORNOAK);
                        output.accept(TolkienBlocks.STAIRS_FANGORNOAK);
                        output.accept(TolkienBlocks.SLAB_FANGORNOAK);
                        output.accept(TolkienBlocks.FENCE_FANGORNOAK);
                        output.accept(TolkienBlocks.FENCE_GATE_FANGORNOAK);
                        output.accept(TolkienBlocks.DOOR_FANGORNOAK);
                        output.accept(TolkienBlocks.TRAPDOOR_FANGORNOAK);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_FANGORNOAK);
                        output.accept(TolkienBlocks.FANGORNOAK_BUTTON);
                        output.accept(TolkienBlocks.BARREL_FANGORNOAK);
                        output.accept(TolkienBlocks.LOG_DEADWOOD);
                        output.accept(TolkienBlocks.WOOD_DEADWOOD);
                        output.accept(TolkienBlocks.STRIPPED_DEADWOOD_LOG);
                        output.accept(TolkienBlocks.STRIPPED_DEADWOOD_WOOD);
                        output.accept(TolkienBlocks.PLANKS_DEADWOOD);
                        output.accept(TolkienBlocks.STAIRS_DEADWOOD);
                        output.accept(TolkienBlocks.SLAB_DEADWOOD);
                        output.accept(TolkienBlocks.FENCE_DEADWOOD);
                        output.accept(TolkienBlocks.FENCE_GATE_DEADWOOD);
                        output.accept(TolkienBlocks.DOOR_DEADWOOD);
                        output.accept(TolkienBlocks.TRAPDOOR_DEADWOOD);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_DEADWOOD);
                        output.accept(TolkienBlocks.DEADWOOD_BUTTON);
                        output.accept(TolkienBlocks.BARREL_DEADWOOD);
                        output.accept(TolkienBlocks.BLOCK_MITHRIL);
                        output.accept(TolkienBlocks.STAIRS_MITHRIL);
                        output.accept(TolkienBlocks.SLAB_MITHRIL);
                        output.accept(TolkienBlocks.WALL_MITHRIL);
                        output.accept(TolkienBlocks.MITHRIL_BARS);
                        output.accept(TolkienBlocks.DOOR_MITHRIL);
                        output.accept(TolkienBlocks.TRAPDOOR_MITHRIL);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_MITHRIL);
                        output.accept(TolkienBlocks.MITHRIL_BUTTON);
                        output.accept(TolkienBlocks.BARREL_MITHRIL);
                        output.accept(TolkienBlocks.BLOCK_MORGULIRON);
                        output.accept(TolkienBlocks.STAIRS_MORGULIRON);
                        output.accept(TolkienBlocks.SLAB_MORGULIRON);
                        output.accept(TolkienBlocks.WALL_MORGULIRON);
                        output.accept(TolkienBlocks.MORGULIRON_BARS);
                        output.accept(TolkienBlocks.DOOR_MORGULIRON);
                        output.accept(TolkienBlocks.TRAPDOOR_MORGULIRON);
                        output.accept(TolkienBlocks.PRESSURE_PLATE_MORGULIRON);
                        output.accept(TolkienBlocks.MORGULIRON_BUTTON);
                        output.accept(TolkienBlocks.BARREL_MORGULIRON);
                        output.accept(TolkienBlocks.BLOCK_AMMOLITE);
                        output.accept(TolkienBlocks.PANE_AMMOLITE);
                        output.accept(TolkienBlocks.DOOR_DURIN);
                        output.accept(TolkienBlocks.BLOCK_HALLOWED);
                        output.accept(TolkienBlocks.STONE_PATH);
                        output.accept(TolkienBlocks.PLACARD);
                        output.accept(TolkienBlocks.ROCKPILE);
                    }).build());

    public static final Supplier<CreativeModeTab> TOLKIEN_NATURE = CREATIVE_MODE_TAB.register("tolkienmobs_tab_natural",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienBlocks.BLOCK_HALLOWED))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MODID, "tolkienmobs_tab_deco"))
                    .title(Component.translatable("itemGroup.tolkienmobs.natural"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienBlocks.ORE_MITHRIL);
                        output.accept(TolkienBlocks.ORE_END_MITHRIL);
                        output.accept(TolkienBlocks.ORE_NETHER_MITHRIL);
                        output.accept(TolkienBlocks.ORE_DEEPSLATE_MITHRIL);
                        output.accept(TolkienBlocks.ORE_MORGULIRON);
                        output.accept(TolkienBlocks.ORE_END_MORGULIRON);
                        output.accept(TolkienBlocks.ORE_NETHER_MORGULIRON);
                        output.accept(TolkienBlocks.ORE_DEEPSLATE_MORGULIRON);
                        output.accept(TolkienBlocks.ORE_AMMOLITE);
                        output.accept(TolkienBlocks.ORE_END_AMMOLITE);
                        output.accept(TolkienBlocks.ORE_NETHER_AMMOLITE);
                        output.accept(TolkienBlocks.ORE_DEEPSLATE_AMMOLITE);
                        output.accept(TolkienBlocks.RAW_MITHRIL_BLOCK);
                        output.accept(TolkienBlocks.RAW_MORGULIRON_BLOCK);
                        output.accept(TolkienBlocks.LOG_MALLORN);
                        output.accept(TolkienBlocks.LOG_MIRKWOOD);
                        output.accept(TolkienBlocks.LOG_CULUMALDA);
                        output.accept(TolkienBlocks.LOG_LEBETHRON);
                        output.accept(TolkienBlocks.LOG_FANGORNOAK);
                        output.accept(TolkienBlocks.LOG_DEADWOOD);
                        output.accept(TolkienBlocks.WOOD_MALLORN);
                        output.accept(TolkienBlocks.WOOD_MIRKWOOD);
                        output.accept(TolkienBlocks.WOOD_CULUMALDA);
                        output.accept(TolkienBlocks.WOOD_LEBETHRON);
                        output.accept(TolkienBlocks.WOOD_FANGORNOAK);
                        output.accept(TolkienBlocks.WOOD_DEADWOOD);
                        output.accept(TolkienBlocks.STRIPPED_MALLORN_LOG);
                        output.accept(TolkienBlocks.STRIPPED_MIRKWOOD_LOG);
                        output.accept(TolkienBlocks.STRIPPED_CULUMALDA_LOG);
                        output.accept(TolkienBlocks.STRIPPED_LEBETHRON_LOG);
                        output.accept(TolkienBlocks.STRIPPED_FANGORNOAK_LOG);
                        output.accept(TolkienBlocks.STRIPPED_DEADWOOD_LOG);
                        output.accept(TolkienBlocks.STRIPPED_MALLORN_WOOD);
                        output.accept(TolkienBlocks.STRIPPED_MIRKWOOD_WOOD);
                        output.accept(TolkienBlocks.STRIPPED_CULUMALDA_WOOD);
                        output.accept(TolkienBlocks.STRIPPED_LEBETHRON_WOOD);
                        output.accept(TolkienBlocks.STRIPPED_FANGORNOAK_WOOD);
                        output.accept(TolkienBlocks.STRIPPED_DEADWOOD_WOOD);
                        output.accept(TolkienBlocks.SAPLING_MALLORN);
                        output.accept(TolkienBlocks.SAPLING_MIRKWOOD);
                        output.accept(TolkienBlocks.SAPLING_CULUMALDA);
                        output.accept(TolkienBlocks.SAPLING_LEBETHRON);
                        output.accept(TolkienBlocks.SAPLING_FANGORNOAK);
                        output.accept(TolkienBlocks.SAPLING_DEADWOOD);
                        output.accept(TolkienBlocks.LEAVES_MALLORN);
                        output.accept(TolkienBlocks.LEAVES_MIRKWOOD);
                        output.accept(TolkienBlocks.LEAVES_CULUMALDA);
                        output.accept(TolkienBlocks.LEAVES_LEBETHRON);
                        output.accept(TolkienBlocks.LEAVES_FANGORNOAK);
                        output.accept(TolkienBlocks.LEAFPILE_MALLORN);
                        output.accept(TolkienBlocks.LEAFPILE_MIRKWOOD);
                        output.accept(TolkienBlocks.LEAFPILE_CULUMALDA);
                        output.accept(TolkienBlocks.LEAFPILE_LEBETHRON);
                        output.accept(TolkienBlocks.LEAFPILE_FANGORNOAK);
                        output.accept(TolkienBlocks.FLOWER_SIMBELMYNE);
                        output.accept(TolkienBlocks.FLOWER_MIRKWOOD);
                        output.accept(TolkienBlocks.FLOWER_ALFIRIN);
                        output.accept(TolkienBlocks.FLOWER_ATHELAS);
                        output.accept(TolkienBlocks.FLOWER_NIPHREDIL);
                        output.accept(TolkienBlocks.FLOWER_SWAMPMILKWEED);
                        output.accept(TolkienBlocks.FLOWER_LILLYOFTHEVALLEY);
                        output.accept(TolkienBlocks.MUSHROOM_BLOOM_DECAY);
                        output.accept(TolkienBlocks.MUSHROOM_DECAY_BLOOM);
                        output.accept(TolkienItems.PIPEWEED_ITEM);
                        output.accept(TolkienItems.PIPEWEED_SEEDS);
                        output.accept(TolkienBlocks.LIGHTNINGBUG_BLOCK);
                        output.accept(TolkienBlocks.LOCUST_BLOCK);
                        output.accept(TolkienBlocks.BLOCK_HALLOWED);
                        output.accept(TolkienBlocks.STONE_PATH);
                        output.accept(TolkienBlocks.LIVING_ROOTS);
                        output.accept(TolkienBlocks.PLACARD);
                        output.accept(TolkienBlocks.ROCKPILE);
                        output.accept(TolkienBlocks.WALL_DECAY_BLOOM);
                        output.accept(TolkienBlocks.WALL_MUSHROOM_RED);
                        output.accept(TolkienBlocks.WALL_MUSHROOM_BROWN);
                    }).build());

    public static final Supplier<CreativeModeTab> TOLKIEN_TOOLS = CREATIVE_MODE_TAB.register("tolkienmobs_tab_tool",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(TolkienItems.SWORD_MITHRIL.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MODID, "tolkienmobs_tab_natural"))
                    .title(Component.translatable("itemGroup.tolkienmobs.tools"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(TolkienItems.COIN_POUCH);
                        output.accept(TolkienItems.KEY_RING);
                        output.accept(TolkienItems.SWORD_WITCHKING);
                        output.accept(TolkienItems.SWORD_URUK);
                        output.accept(TolkienItems.WHIP_FIRE);
                        output.accept(TolkienItems.CLUB_WOODEN);
                        output.accept(TolkienItems.SWORD_MITHRIL);
                        output.accept(TolkienItems.PICKAXE_MITHRIL);
                        output.accept(TolkienItems.AXE_MITHRIL);
                        output.accept(TolkienItems.SHOVEL_MITHRIL);
                        output.accept(TolkienItems.HOE_MITHRIL);
                        output.accept(TolkienItems.SHEARS_MITHRIL);
                        output.accept(TolkienItems.SWORD_MORGULIRON);
                        output.accept(TolkienItems.PICKAXE_MORGULIRON);
                        output.accept(TolkienItems.AXE_MORGULIRON);
                        output.accept(TolkienItems.SHOVEL_MORGULIRON);
                        output.accept(TolkienItems.HOE_MORGULIRON);
                        output.accept(TolkienItems.SHEARS_MORGULIRON);
                        output.accept(TolkienItems.SWORD_AMMOLITE);
                        output.accept(TolkienItems.PICKAXE_AMMOLITE);
                        output.accept(TolkienItems.AXE_AMMOLITE);
                        output.accept(TolkienItems.SHOVEL_AMMOLITE);
                        output.accept(TolkienItems.HOE_AMMOLITE);
                        output.accept(TolkienItems.SHEARS_AMMOLITE);
                        output.accept(TolkienItems.HYPE_HORN);
                        output.accept(TolkienItems.RECORD_EREBOR);
                        output.accept(TolkienItems.RECORD_HOBBITS);
                        output.accept(TolkienItems.RECORD_MINASTIRITH);
                        output.accept(TolkienItems.RECORD_MURDERFROG);
                        output.accept(TolkienItems.RECORD_BOMBADIL);
                        output.accept(TolkienItems.RECORD_REDER);
                        output.accept(TolkienItems.RECORD_RIVENDELL);
                        output.accept(TolkienItems.RECORD_LOTHLORIEN);
                        output.accept(TolkienItems.RECORD_WILLOW);
                        output.accept(TolkienItems.RECORD_FUMBLE);
                        output.accept(TolkienItems.RECORD_EDORAS);
                        output.accept(TolkienItems.RECORD_WBATTLE);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
