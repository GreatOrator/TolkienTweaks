package com.greatorator.tolkienmobs.datagen.helpers;

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

    public void addTrinket(Item key, String name) {
        this.add(key.getDescriptionId() + ".water", name);
    }

    public void addSound(String category, String subtitleName, String name) {
        this.add("subtitles.tolkienmobs." + category + "." + subtitleName, name);
    }

    public void addMusicDisc(DeferredItem<Item> disc, String description) {
        this.addItem(disc, "Music Disc");
        this.add(Util.makeDescriptionId("jukebox_song", disc.get().components().get(DataComponents.JUKEBOX_PLAYABLE).song().key().location()), description);
    }

    public void addItemScreen(Item key, String name) {
        this.add("screen.tolkienmobs." + key.getDescriptionId(), name);
    }

    public void addBlockScreen(Block key, String name) {
        this.add("screen.tolkienmobs." + key.getDescriptionId(), name);
    }

    public void addCodeScreen(Item key, String name) {
        this.add("screen.tolkienmobs.code" + key.getDescriptionId(), name);
    }

    public void addItemWithLore(Item key, String name, String lore) {
        this.add(key.getDescriptionId(), name);
        this.add(key.getDescriptionId() + ".lore", lore);
    }

    public void addItemWithInstructions(Item key, String name, String lore, String lore2, String inst) {
        this.add(key.getDescriptionId(), name);
        this.add(key.getDescriptionId() + ".shift_down", inst);
        this.add(key.getDescriptionId() + ".lore", lore);
        this.add(key.getDescriptionId() + ".lore2", lore2);
    }

    public void addBlockWithLore(Block key, String name, String lore) {
        this.add(key.getDescriptionId(), name);
        this.add(key.getDescriptionId() + ".lore", lore);
    }

    public void createTrinketSet(String trinketType, String descriptionType) {
        this.add("item.tolkienmobs.trinket_" + trinketType + ".empty", "Base " + descriptionType);
        this.add("item.tolkienmobs.trinket_" + trinketType + ".water", "Magical " + descriptionType + " of Nothingness");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".mundane", "Magical " + descriptionType + " of Nothingness");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".thick", "Magical " + descriptionType + " of Nothingness");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".awkward", "Magical " + descriptionType + " of Nothingness");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".night_vision", "Magical " + descriptionType + " of Night Vision");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".invisibility", "Magical " + descriptionType + " of Invisibility");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".leaping", "Magical " + descriptionType + " of Leaping");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".fire_resistance", "Magical " + descriptionType + " of Fire Resistance");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".swiftness", "Magical " + descriptionType + " of Speed");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".slowness", "Magical " + descriptionType + " of Slowing");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".turtle_master", "Magical " + descriptionType + " of Turtles");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".water_breathing", "Magical " + descriptionType + " of Water Breathing");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".healing", "Magical " + descriptionType + " of Healing");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".harming", "Magical " + descriptionType + " of Pain");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".poison", "Magical " + descriptionType + " of Poison");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".regeneration", "Magical " + descriptionType + " of Regeneration");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".strength", "Magical " + descriptionType + " of Strength");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".weakness", "Magical " + descriptionType + " of Weakness");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".luck", "Magical " + descriptionType + " of Luck");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".slow_falling", "Magical " + descriptionType + " of Feathers");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".wind_charged", "Magical " + descriptionType + " of Wind Charged");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".weaving", "Magical " + descriptionType + " of Weaving");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".oozing", "Magical " + descriptionType + " of Oozing");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".infested", "Magical " + descriptionType + " of Infestation");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".ent_draught", "Magical " + descriptionType + " of Ent Draught");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".personal_blacksmith", "Magical " + descriptionType + " of Repair");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".blessing_of_eru", "Magical " + descriptionType + " of Eru's Blessing");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elf_vitality", "Magical " + descriptionType + " of Elven Vitality");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elven_nimbleness", "Magical " + descriptionType + " of Elven Nimbleness");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".inventory_corrosion", "Magical " + descriptionType + " of Rusting");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elemental_flight", "Magical " + descriptionType + " of Levitation");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".crippling_terror", "Magical " + descriptionType + " of Terror");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".dread_aura", "Magical " + descriptionType + " of Fear");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".sleepnesia", "Magical " + descriptionType + " of Sleeping");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elemental_burning", "Magical " + descriptionType + " of Fire");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elemental_lightning", "Magical " + descriptionType + " of Shock");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elemental_drowning", "Magical " + descriptionType + " of Suffocation");
    }

    public void createPotionSet(String potionPrefix, String potionName) {
        this.add("item.minecraft.potion.effect." + potionPrefix, "Potion of " + potionName);
        this.add("item.minecraft.splash_potion.effect." + potionPrefix, "Splash Potion of " + potionName);
        this.add("item.minecraft.lingering_potion.effect." + potionPrefix, "Lingering Potion of " + potionName);
        this.add("effect.tolkienmobs." + potionPrefix, potionName);
    }

    public void createGemSet(String gemPrefix, String gemName) {
        this.add("block.tolkienmobs.ore_" + gemPrefix, gemName + " Ore");
        this.add("block.tolkienmobs.ore_deepslate_" + gemPrefix, gemName + " Deepslate Ore");
        this.add("block.tolkienmobs.ore_end_" + gemPrefix, gemName + " Ender Ore");
        this.add("block.tolkienmobs.ore_nether_" + gemPrefix, "Firey " + gemName + " Ore");
        this.add("item.tolkienmobs.gem_" + gemPrefix,  gemName + " Gem");
        this.add("block.tolkienmobs.block_" + gemPrefix, gemName + " Block");
        this.add("block.tolkienmobs.pane_" + gemPrefix, gemName + " Pane");
    }

    public void createStoneSet(String stonePrefix, String stoneName) {
        this.add("block.tolkienmobs." + stonePrefix, stoneName);
        this.add("block.tolkienmobs.stairs_" + stonePrefix, stoneName + " Stairs");
        this.add("block.tolkienmobs.slab_" + stonePrefix, stoneName + " Slab");
        this.add("block.tolkienmobs.pressure_plate_" + stonePrefix, stoneName + " Pressure Plate");
        this.add("block.tolkienmobs." + stonePrefix + "_button",  stoneName + " Button");
        this.add("block.tolkienmobs.wall_" + stonePrefix, stoneName + " Wall");
    }

    public void createMetalSet(String metalPrefix, String metalName) {
        this.add("block.tolkienmobs.ore_" + metalPrefix, metalName + " Ore");
        this.add("block.tolkienmobs.ore_deepslate_" + metalPrefix, metalName + " Deepslate Ore");
        this.add("block.tolkienmobs.ore_end_" + metalPrefix, metalName + " Ender Ore");
        this.add("block.tolkienmobs.ore_nether_" + metalPrefix, "Firey " + metalName + " Ore");
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
        this.add("block.tolkienmobs.wall_sign_" + woodPrefix, woodName + " Wall Sign");
        this.add("block.tolkienmobs.hanging_sign_" + woodPrefix, woodName + " Hanging Sign");
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
