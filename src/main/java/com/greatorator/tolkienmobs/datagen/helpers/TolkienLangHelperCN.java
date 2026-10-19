package com.greatorator.tolkienmobs.datagen.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greatorator.tolkienmobs.util.LangConversionHelper;
import com.greatorator.tolkienmobs.util.LangFormatSplitter;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public abstract class TolkienLangHelperCN extends LanguageProvider {
    private final Map<String, String> TF_TIPS = new HashMap<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    public final Map<String, String> upsideDownEntries = new HashMap<>();

    public TolkienLangHelperCN(PackOutput output) {
        super(output, MODID, "zh_cn");
        this.output = output;
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        List<LangFormatSplitter.Component> splitEnglish = LangFormatSplitter.split(value);
        this.upsideDownEntries.put(key, LangConversionHelper.convertComponents(splitEnglish));
    }

    public void addVillager(EntityType<?> key, String name) {
        this.add(key.getDescriptionId(), name);
        this.add(key.getDescriptionId() + ".tolkienmobs.none", "人类");
        this.add(key.getDescriptionId() + ".tolkienmobs.coin_trader", "硬币兑换商");
        this.add(key.getDescriptionId() + ".tolkienmobs.grocery_store", "杂货店");
        this.add(key.getDescriptionId() + ".tolkienmobs.pet_merchant", "废品处理商");
        this.add(key.getDescriptionId() + ".tolkienmobs.junk_trader", "宠物商人");
        this.add(key.getDescriptionId() + ".tolkienmobs.trinket_smith", "魔法盔甲匠");
        this.add(key.getDescriptionId() + ".tolkienmobs.trinket_tailor", "魔法裁缝");
        this.add(key.getDescriptionId() + ".armorer", "盔甲匠");
        this.add(key.getDescriptionId() + ".butcher", "屠夫");
        this.add(key.getDescriptionId() + ".cartographer", "制图师");
        this.add(key.getDescriptionId() + ".cleric", "教士");
        this.add(key.getDescriptionId() + ".farmer", "农民");
        this.add(key.getDescriptionId() + ".fisherman", "渔民");
        this.add(key.getDescriptionId() + ".leatherworker", "皮匠");
        this.add(key.getDescriptionId() + ".librarian", "图书管理员");
        this.add(key.getDescriptionId() + ".mason", "石匠");
        this.add(key.getDescriptionId() + ".nitwit", "傻瓜");
        this.add(key.getDescriptionId() + ".shepherd", "牧羊人");
        this.add(key.getDescriptionId() + ".toolsmith", "工具铁匠");
        this.add(key.getDescriptionId() + ".weaponsmith", "武器铁匠");
        this.add(key.getDescriptionId() + ".fletcher", "制箭师");
    }

    public void addTrinket(Item key, String name) {
        this.add(key.getDescriptionId() + ".water", name);
    }

    public void addSound(String category, String subtitleName, String name) {
        this.add("subtitles.tolkienmobs." + category + "." + subtitleName, name);
    }

    public void addMusicDisc(DeferredItem<Item> disc, String description) {
        this.addItem(disc, "音乐唱片");
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

    public void addEnchantment(String key, String name) {
        this.add("enchantment.tolkienmobs." + key, name);
    }

    public void addEnchantmentDesc(String key, String desc) {
        this.add("enchantment.tolkienmobs." + key + ".desc", desc);
    }

    public void createTrinketSet(String trinketType, String descriptionType) {
        this.add("item.tolkienmobs.trinket_" + trinketType + ".empty", "基础" + descriptionType);
        this.add("item.tolkienmobs.trinket_" + trinketType + ".water", "魔法" + descriptionType + " - 无效果");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".mundane", "魔法" + descriptionType + " - 无效果");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".thick", "魔法" + descriptionType + " - 无效果");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".awkward", "魔法" + descriptionType + " - 无效果");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".night_vision", "魔法" + descriptionType + " - 夜视");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".invisibility", "魔法" + descriptionType + " - 隐身");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".leaping", "魔法" + descriptionType + " - 跳跃");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".fire_resistance", "魔法" + descriptionType + " - 抗火");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".swiftness", "魔法" + descriptionType + " - 速度");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".slowness", "魔法" + descriptionType + " - 缓慢");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".turtle_master", "魔法" + descriptionType + " - 神鬼");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".water_breathing", "魔法" + descriptionType + " - 水肺");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".healing", "魔法" + descriptionType + " - 治疗");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".harming", "魔法" + descriptionType + " - 伤痛");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".poison", "魔法" + descriptionType + " - 中毒");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".regeneration", "魔法" + descriptionType + " - 回血");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".strength", "魔法" + descriptionType + " - 力量");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".weakness", "魔法" + descriptionType + " - 虚弱");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".luck", "魔法" + descriptionType + " - 幸运");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".slow_falling", "魔法" + descriptionType + " - 羽落");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".wind_charged", "魔法" + descriptionType + " - 蓄风");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".weaving", "魔法" + descriptionType + " - 盘丝");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".oozing", "魔法" + descriptionType + " - 渗浆");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".infested", "魔法" + descriptionType + " - 寄生");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".ent_draught", "魔法" + descriptionType + " - 恩特饮料");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".personal_blacksmith", "魔法" + descriptionType + " - 修复");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".blessing_of_eru", "魔法" + descriptionType + " - 一如祝福");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elf_vitality", "魔法" + descriptionType + " - 精灵活力");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elven_nimbleness", "魔法" + descriptionType + " - 精灵敏捷");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".inventory_corrosion", "魔法" + descriptionType + " - 锈蚀");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elemental_flight", "魔法" + descriptionType + " - 漂浮");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".crippling_terror", "魔法" + descriptionType + " - 悚怖");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".dread_aura", "魔法" + descriptionType + " - 恐惧");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".sleepnesia", "魔法" + descriptionType + " - 沉睡");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elemental_burning", "魔法" + descriptionType + " - 焚身");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elemental_lightning", "魔法" + descriptionType + " - 震击");
        this.add("item.tolkienmobs.trinket_" + trinketType + ".elemental_drowning", "魔法" + descriptionType + " - 溺水");
    }

    public void createPotionSet(String potionPrefix, String potionName) {
        this.add("item.minecraft.potion.effect." + potionPrefix, potionName + "药水");
        this.add("item.minecraft.splash_potion.effect." + potionPrefix, "喷溅型" + potionName + "药水");
        this.add("item.minecraft.lingering_potion.effect." + potionPrefix, "滞留型" + potionName + "药水");
        this.add("effect.tolkienmobs." + potionPrefix, potionName);
    }

    public void createGemSet(String gemPrefix, String gemName) {
        this.add("block.tolkienmobs.ore_" + gemPrefix, gemName + "矿石");
        this.add("block.tolkienmobs.ore_deepslate_" + gemPrefix, "深层" + gemName + "矿石");
        this.add("block.tolkienmobs.ore_end_" + gemPrefix, "末影" + gemName + "矿石");
        this.add("block.tolkienmobs.ore_nether_" + gemPrefix, "炽热" + gemName + "矿石");
        this.add("item.tolkienmobs.gem_" + gemPrefix,  gemName + "宝石");
        this.add("block.tolkienmobs.block_" + gemPrefix, gemName + "块");
        this.add("block.tolkienmobs.pane_" + gemPrefix, gemName + "板");
    }

    public void createStoneSet(String stonePrefix, String stoneName) {
        this.add("block.tolkienmobs." + stonePrefix, stoneName);
        this.add("block.tolkienmobs.stairs_" + stonePrefix, stoneName + "楼梯");
        this.add("block.tolkienmobs.slab_" + stonePrefix, stoneName + "台阶");
        this.add("block.tolkienmobs.pressure_plate_" + stonePrefix, stoneName + "压力板");
        this.add("block.tolkienmobs." + stonePrefix + "_button",  stoneName + "按钮");
        this.add("block.tolkienmobs.wall_" + stonePrefix, stoneName + "砖墙");
    }

    public void createMetalSet(String metalPrefix, String metalName) {
        this.add("block.tolkienmobs.ore_" + metalPrefix, metalName + "矿石");
        this.add("block.tolkienmobs.ore_deepslate_" + metalPrefix, "深层" + metalName + "矿石");
        this.add("block.tolkienmobs.ore_end_" + metalPrefix, "末影" + metalName + "矿石");
        this.add("block.tolkienmobs.ore_nether_" + metalPrefix, "炽热" + metalName + "矿石");
        this.add("block.tolkienmobs.raw_" + metalPrefix + "_block", "粗" + metalName + "块");
        this.add("block.tolkienmobs.trapdoor_" + metalPrefix, metalName + "活板门");
        this.add("block.tolkienmobs.stairs_" + metalPrefix, metalName + "楼梯");
        this.add("block.tolkienmobs." + metalPrefix + "_button", metalName + "按钮");
        this.add("item.tolkienmobs.dust_" + metalPrefix, metalName + "粉");
        this.add("block.tolkienmobs.wall_" + metalPrefix, metalName + "墙");
        this.add("block.tolkienmobs.pressure_plate_" + metalPrefix, metalName + "压力板");
        this.add("block.tolkienmobs.door_" + metalPrefix, metalName + "门");
        this.add("block.tolkienmobs.slab_" + metalPrefix, metalName + "台阶");
        this.add("block.tolkienmobs." + metalPrefix + "_fluid", "熔融" + metalName);
        this.add("block.tolkienmobs.barrel_" + metalPrefix, metalName + "桶");
        this.add("item.tolkienmobs.ingot_" + metalPrefix, metalName + "锭");
        this.add("item.tolkienmobs.nugget_" + metalPrefix, metalName + "粒");
        this.add("item.tolkienmobs.raw_" + metalPrefix, "Raw " + metalName);
        this.add("item.tolkienmobs." + metalPrefix + "_fluid_bucket", "桶装熔融" + metalName);
        this.add("block.tolkienmobs.block_" + metalPrefix, metalName + "块");
        this.add("block.tolkienmobs." + metalPrefix + "_bars", metalName + "栏杆");
    }

    public void createWoodSet(String woodPrefix, String woodName) {
        this.add("block.tolkienmobs.planks_" + woodPrefix, woodName + "木板");
        this.add("block.tolkienmobs.slab_" + woodPrefix, woodName + "台阶");
        this.add("block.tolkienmobs.stairs_" + woodPrefix, woodName + "楼梯");
        this.add("block.tolkienmobs." + woodPrefix + "_button", woodName + "按钮");
        this.add("block.tolkienmobs.ladder_" + woodPrefix, woodName + "梯子");
        this.add("block.tolkienmobs.fence_" + woodPrefix, woodName + "栅栏");
        this.add("block.tolkienmobs.fence_gate_" + woodPrefix, woodName + "栅栏门");
        this.add("block.tolkienmobs.pressure_plate_" + woodPrefix, woodName + "压力板");
        this.add("block.tolkienmobs.trapdoor_" + woodPrefix, woodName + "活板门");
        this.add("block.tolkienmobs.door_" + woodPrefix, woodName + "门");
        this.add("block.tolkienmobs.sign_" + woodPrefix, woodName + "告示牌");
        this.add("block.tolkienmobs.wall_sign_" + woodPrefix, "墙上" + woodName + "告示牌");
        this.add("block.tolkienmobs.hanging_sign_" + woodPrefix, "悬挂式" + woodName + "告示牌");
        this.add("block.tolkienmobs.wall_hanging_sign_" + woodPrefix, "墙上的悬挂式" + woodName + "告示牌");
        this.add("block.tolkienmobs.barrel_" + woodPrefix, woodName + "木桶");
        this.add("block.tolkienmobs.log_" + woodPrefix, woodName + "原木");
        this.add("block.tolkienmobs.wood_" + woodPrefix, woodName + "木头");
        this.add("block.tolkienmobs.stripped_log_" + woodPrefix, "去皮" + woodName + "原木");
        this.add("block.tolkienmobs.stripped_wood_" + woodPrefix, "去皮" + woodName + "木头");
        this.add("block.tolkienmobs.sapling_" + woodPrefix, woodName + "树苗");
        this.add("block.tolkienmobs.potted_sapling_" + woodPrefix, "盆栽" + woodName + "树苗");
        this.add("block.tolkienmobs.leaves_" + woodPrefix, woodName + "树叶");
        this.add("block.tolkienmobs.leafpile_" + woodPrefix, woodName + "落叶堆");
        this.add("item.tolkienmobs." + woodPrefix + "_boat", woodName + "船");
        this.add("item.tolkienmobs." + woodPrefix + "_chest_boat", woodName + "运输船");
    }

    public void addTools(String itemKey, String item) {
        this.add("item.tolkienmobs.sword_" + itemKey, item + "剑");
        this.add("item.tolkienmobs.pickaxe_" + itemKey, item + "镐");
        this.add("item.tolkienmobs.axe_" + itemKey, item + "斧");
        this.add("item.tolkienmobs.shovel_" + itemKey, item + "锹");
        this.add("item.tolkienmobs.hoe_" + itemKey, item + "锄");
        this.add("item.tolkienmobs.shears_" + itemKey, item + "剪刀");
    }

    protected void addFlowerBlock(Block key, String name, String flower) {
        this.add(key.getDescriptionId(), name);
        this.add("block.tolkienmobs.potted_" + flower, "盆栽" + name);
    }
}
