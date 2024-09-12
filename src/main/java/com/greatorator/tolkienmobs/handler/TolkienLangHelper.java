package com.greatorator.tolkienmobs.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greatorator.tolkienmobs.util.LangConversionHelper;
import com.greatorator.tolkienmobs.util.LangFormatSplitter;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public abstract class TolkienLangHelper extends LanguageProvider {
    private final Map<String, String> TF_TIPS = new HashMap<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    public final Map<String, String> upsideDownEntries = new HashMap<>();

    public TolkienLangHelper(PackOutput output) {
        super(output, MODID, "en_us");
        this.output = output;
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        List<LangFormatSplitter.Component> splitEnglish = LangFormatSplitter.split(value);
        this.upsideDownEntries.put(key, LangConversionHelper.convertComponents(splitEnglish));
    }

    public void addMusicDisc(DeferredItem<Item> disc, String description) {
        this.addItem(disc, "Music Disc");
        this.add(Util.makeDescriptionId("jukebox_song", disc.get().components().get(DataComponents.JUKEBOX_PLAYABLE).song().key().location()), description);
    }

    public void addItemWithLore(Item key, String name, String lore) {
        this.add(key.getDescriptionId(), name);
        this.add(key.getDescriptionId() + ".lore", lore);
    }

    public void addBlockWithLore(Block key, String name, String lore) {
        this.add(key.getDescriptionId(), name);
        this.add(key.getDescriptionId() + ".lore", lore);
    }

    public void createMetalSet(String metalPrefix, String metalName) {
        this.add("block.tolkienmobs.ore_" + metalPrefix, metalName + " Ore");
        this.add("block.tolkienmobs.ore_deepslate_" + metalPrefix, metalName + " Deepslate Ore");
        this.add("block.tolkienmobs.ore_end_" + metalPrefix, metalName + " End Ore");
        this.add("block.tolkienmobs.ore_nether_" + metalPrefix, metalName + " Nether Ore");
        this.add("block.tolkienmobs.raw_" + metalPrefix + "_block", "Raw " + metalName + " Block");
        this.add("block.tolkienmobs.trapdoor_" + metalPrefix, metalName + " Trapdoor");
        this.add("block.tolkienmobs.stairs_" + metalPrefix, metalName + " Stairs");
        this.add("block.tolkienmobs." + metalPrefix + "_button", metalName + " Button");
        this.add("item.tolkienmobs.dust_" + metalPrefix, metalName + " Dust");
        this.add("block.tolkienmobs.wall_" + metalPrefix, metalName + " Wall");
        this.add("block.tolkienmobs.pressure_plate_" + metalPrefix, metalName + " Pressure Plate");
        this.add("block.tolkienmobs.door_" + metalPrefix, metalName + " Door");
        this.add("block.tolkienmobs.slab_" + metalPrefix, metalName + " Slab");
        this.add("block.tolkienmobs." + metalPrefix + "_fluid", "Molten" + metalName);
        this.add("block.tolkienmobs.barrel_" + metalPrefix, metalName + " Barrel");
        this.add("item.tolkienmobs.ingot_" + metalPrefix, metalName + " Ingot");
        this.add("item.tolkienmobs.nugget_" + metalPrefix, metalName + " Nugget");
        this.add("item.tolkienmobs.raw_" + metalPrefix, "Raw " + metalName);
        this.add("item.tolkienmobs." + metalPrefix + "_fluid_bucket", "Molten " + metalName + " Bucket");
        this.add("block.tolkienmobs.block_" + metalPrefix, metalName + " Block");
        this.add("block.tolkienmobs." + metalPrefix + "_bars", metalName + " Bars");
    }

    public void createWoodSet(String woodPrefix, String woodName) {
        this.add("block.tolkienmobs.planks_" + woodPrefix, woodName + " Planks");
        this.add("block.tolkienmobs.slab_" + woodPrefix, woodName + " Slab");
        this.add("block.tolkienmobs.stairs_" + woodPrefix, woodName + " Stairs");
        this.add("block.tolkienmobs." + woodPrefix + "_button", woodName + " Button");
        this.add("block.tolkienmobs.ladder_" + woodPrefix, woodName + " Ladder");
        this.add("block.tolkienmobs.fence_" + woodPrefix, woodName + " Fence");
        this.add("block.tolkienmobs.fence_gate_" + woodPrefix, woodName + " Fence Gate");
        this.add("block.tolkienmobs.pressure_plate_" + woodPrefix, woodName + " Pressure Plate");
        this.add("block.tolkienmobs.trapdoor_" + woodPrefix, woodName + " Trapdoor");
        this.add("block.tolkienmobs.door_" + woodPrefix, woodName + " Door");
        this.add("block.tolkienmobs.sign_" + woodPrefix, woodName + " Sign");
        this.add("block.tolkienmobs.wall_sign_" + woodPrefix + "", woodName + " Wall Sign");
        this.add("block.tolkienmobs.hanging_sign_" + woodPrefix + "", woodName + " Hanging Sign");
        this.add("block.tolkienmobs.wall_hanging_sign_" + woodPrefix, woodName + " Wall Hanging Sign");
        this.add("block.tolkienmobs.barrel_" + woodPrefix, woodName + " Barrel");
        this.add("block.tolkienmobs.log_" + woodPrefix, woodName + " Log");
        this.add("block.tolkienmobs.wood_" + woodPrefix, woodName + " Wood");
        this.add("block.tolkienmobs.stripped_log_" + woodPrefix, woodName + " Stripped Log");
        this.add("block.tolkienmobs.stripped_wood_" + woodPrefix, woodName + " Stripped Wood");
        this.add("block.tolkienmobs.sapling_" + woodPrefix, woodName + " Sapling");
        this.add("block.tolkienmobs.potted_sapling_" + woodPrefix, "Potted" + woodName + " Sapling");
        this.add("block.tolkienmobs.leaves_" + woodPrefix, woodName + " Leaves");
        this.add("block.tolkienmobs.leafpile_" + woodPrefix, woodName + " Leaf Pile");
        this.add("item.tolkienmobs." + woodPrefix + "_boat", woodName + " Boat");
        this.add("item.tolkienmobs." + woodPrefix + "_chest_boat", woodName + " Chest Boat");
    }

    public void addTools(String itemKey, String item) {
        this.add("item.tolkienmobs.sword_" + itemKey, item + " Sword");
        this.add("item.tolkienmobs.pickaxe_" + itemKey, item + " Pickaxe");
        this.add("item.tolkienmobs.axe_" + itemKey, item + " Axe");
        this.add("item.tolkienmobs.shovel_" + itemKey, item + " Shovel");
        this.add("item.tolkienmobs.hoe_" + itemKey, item + " Hoe");
        this.add("item.tolkienmobs.shears_" + itemKey, item + " Shears");
    }

    protected void addFlowerBlock(Block key, String name, String flower) {
        this.add(key.getDescriptionId(), name);
        this.add("block.tolkienmobs.potted_" + flower, "Potted" + name);
    }
}
