package com.greatorator.tolkientweaks.datagen;

import com.greatorator.tolkientweaks.TolkienContent;
import com.greatorator.tolkientweaks.TolkienTweaks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class LangGenerator extends LanguageProvider {
    public LangGenerator(DataGenerator gen) {
        super(gen, TolkienTweaks.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        PrefixHelper helper = new PrefixHelper(this);
        blocks();
        items();
        quest();
        food();
        potion();
        coinToken();
        enchants();
        records();
        tools();
        entities();
        merchants(helper);
        trinket(helper);
        biomes(helper);
        chatMessages(helper);
        creativeTabGroups();
        gui(helper);
        keyCommand();
    }

    private void blocks() {
//        add(TTMContent.ORE_MITHRIL.get(), "Mithril Ore");
//        add(TTMContent.PLACARD.get(), "Country Signs");
//        addLore(Item.byBlock(TTMContent.PLACARD.get()), "Shift right-click on sign to cycle types");
    }

    private void items() {
//        add(TolkienContent.COIN_POUCH.get(), "Summon Legendary " + TextFormatting.AQUA + "Mithril " + TextFormatting.RESET + "Golem");
//        addLore(TTMContent.GOLEM_STONE_SUMMON.get(), "Ancient artifact to summon a Legendary Golem");
        add(TolkienContent.COIN_POUCH.get(), "Coin Pouch");
        addLore(TolkienContent.COIN_POUCH.get(), "Store your excess coins.");
        add(TolkienContent.ITEM_COIN_BRONZE.get(), "Bronze Coin");
        addLore(TolkienContent.ITEM_COIN_BRONZE.get(), "64 Can be traded for 1 Silver coin");
        add(TolkienContent.ITEM_COIN_SILVER.get(), "Silver Coin");
        addLore(TolkienContent.ITEM_COIN_SILVER.get(), "64 Can be traded for 1 Gold coin");
        add(TolkienContent.ITEM_COIN_GOLD.get(), "Gold Coin");
        addLore(TolkienContent.ITEM_COIN_GOLD.get(), "64 Can be traded for 1 Mithril coin");
        add(TolkienContent.ITEM_COIN_MITHRIL.get(), "Mithril Coin");
        addLore(TolkienContent.ITEM_COIN_MITHRIL.get(), "Very rare and valuable coin");


//        add("item_info.tolkienmobs.sneak_right_click_activate", "Shift right-click to activate/deactivate effect");
    }

    private void quest() {
    }

    private void coinToken() {
    }

    private void creativeTabGroups() {
//        add("itemGroup.tolkienmobs.tools", "TolkienTweaks Tools");
//        add("itemGroup.tolkienmobs.mats", "TolkienTweaks Materials & World Gen");
//        add("itemGroup.tolkienmobs.deco", "TolkienTweaks Decorative");
//        add("itemGroup.tolkienmobs.spawn", "TolkienTweaks Mobs");
//        add("itemGroup.tolkienmobs.food", "TolkienTweaks Food");
//        add("itemGroup.tolkienmobs.quest", "TolkienTweaks Quest Items");
//        add("itemGroup.tolkienmobs.signs", "TolkienTweaks Placards");
    }

    private void potion() {
//        add("effect.tolkienmobs.ent_draught", "Ent Draught");
//        add("item.minecraft.potion.effect.ent_draught", "Potion of Ent Draught");
//        add("item.minecraft.splash_potion.effect.ent_draught", "Splash Potion of Ent Draught");
//        add("item.minecraft.lingering_potion.effect.ent_draught", "Lingering Potion of Ent Draught");
//        add("item.minecraft.tipped_arrow.effect.ent_draught", "Arrow of Ent Draught");
    }

    private void enchants() {
//        add("enchantment.tolkienmobs.balrogs_mark", "Balrog's Mark");
    }

    private void food() {
    }

    private void records() {
//        add(TTMContent.RECORD_RIVENDELL.get(), "Travelling Music");
//        add("item.tolkienmobs." + TTMContent.RECORD_RIVENDELL.get() + ".desc", "Riders of Rivendell");
    }

    private void tools() {
    }

    private void trinket(PrefixHelper helper) {
//        add(TTMContent.TRINKET_AMULET.get(), "Magical Amulet of ");
    }

    private void biomes(PrefixHelper helper) {
//        helper.setPrefix("biome.tolkienmobs.");
//        helper.add("biome_lorinand", "Lothlorien");
    }

    private void merchants(PrefixHelper helper) {
    }

    private void entities() {
    }

    private void chatMessages(PrefixHelper helper) {
//        add("tolkienmobs.msg.helpcomming", "Goblin King is attempting to call for help...Reinforcements have arrived!");
    }

    private void gui(PrefixHelper helper) {
//        helper.setPrefix("gui.tolkienmobs.backpack.");
//        helper.add("fluid_storage",             "Fluid Storage");
    }

    private void keyCommand() {
//        add("key.category.tolkienmobs", "TolkienTweaks Mobs");
    }


        //region Helpers

    @Override
    public void add(Block key, String name) {
        if (key != null) super.add(key, name);
    }

    @Override
    public void add(Item key, String name) {
        if (key != null) super.add(key, name);
    }

    public void addLore(Item key, String lore) {
        add(key.getDescriptionId() + ".lore", lore);
    }

    public static class PrefixHelper {
        private LangGenerator generator;
        private String prefix;

        public PrefixHelper(LangGenerator generator) {
            this.generator = generator;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void add(String translationKey, String translation) {
            generator.add(prefix + translationKey, translation);
        }
    }

    //endregion

    @Override
    public String getName() {
        return "Tolkien Tweaks - Mobs Edition English Translation";
    }
}
