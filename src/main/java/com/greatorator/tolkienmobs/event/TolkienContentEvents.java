package com.greatorator.tolkienmobs.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greatorator.tolkienmobs.TolkienMobsConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Iterator;
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The three places the config's content switches are enforced.
 *
 * <p>The switches cannot take a block or an item out of the registries - those are filled once, at
 * startup, and never emptied - so instead every path that hands one out is closed. A mob is refused
 * as it enters the world, which is the one gate every route into a level passes through: natural
 * spawning, a spawner, a spawn egg and a command all end at the same {@link EntityJoinLevelEvent}.
 * Blocks and items are taken out of the creative tabs and out of the recipes that make them, which
 * leaves anything already placed or carried alone.
 */
@EventBusSubscriber(modid = MODID)
public final class TolkienContentEvents {
    /** Where a recipe JSON states what it hands out: the vanilla field, and the fireplace's list. */
    private static final String[] OUTPUT_KEYS = {"result", "output"};

    /** The trinket table names its product in a field of its own. */
    private static final String TRINKET_KEY = "trinket";

    private TolkienContentEvents() {
    }

    /**
     * Refuses a switched-off mob as it joins a level. An entity read back off disk is left alone, so
     * turning a mob off does not empty the world of the ones already living in it: they stay until
     * they wander out of view for good, and no new one follows them.
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()) {
            return;
        }
        EntityType<?> type = event.getEntity().getType();
        if (TolkienMobsConfig.isMobDisabled(type)) {
            event.setCanceled(true);
        }
    }

    /**
     * Empties the switched-off items out of every creative tab, and out of the search tab with them.
     *
     * <p>Registered by hand from the mod's constructor rather than by this class's annotation: the
     * tab event is a mod bus event, and the subscriptions here are on the game bus.
     */
    public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (!TolkienMobsConfig.disableAllItems && !TolkienMobsConfig.disableAllBlocks) {
            return;
        }
        event.removeIf(stack -> TolkienMobsConfig.isItemHidden(stack.getItem()),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    /**
     * Drops the recipes that hand out a switched-off item before they are ever deserialized, which is
     * what keeps such an item out of the recipe book as well as out of a crafting grid. This runs
     * again on every {@code /reload}, so switching an item back on restores its recipes.
     */
    @SubscribeEvent
    public static void onModifyRecipeJsons(ModifyRecipeJsonsEvent event) {
        if (!TolkienMobsConfig.disableAllItems && !TolkienMobsConfig.disableAllBlocks) {
            return;
        }
        Iterator<Map.Entry<Identifier, JsonElement>> recipes = event.getRecipeJsons().entrySet().iterator();
        while (recipes.hasNext()) {
            if (handsOutHiddenItem(recipes.next().getValue())) {
                recipes.remove();
            }
        }
    }

    /** Whether any item this recipe hands out is one the config keeps out of the game. */
    private static boolean handsOutHiddenItem(JsonElement json) {
        if (!json.isJsonObject()) {
            return false;
        }
        JsonObject recipe = json.getAsJsonObject();
        for (String key : OUTPUT_KEYS) {
            if (recipe.has(key) && outputsHiddenItem(recipe.get(key))) {
                return true;
            }
        }
        return recipe.has(TRINKET_KEY) && outputsHiddenItem(recipe.get(TRINKET_KEY));
    }

    /**
     * Walks an output as a recipe writes it, which is a stack, a list of stacks, or - in an older
     * shape - a bare item id.
     */
    private static boolean outputsHiddenItem(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return false;
        }
        if (element.isJsonArray()) {
            for (JsonElement entry : element.getAsJsonArray()) {
                if (outputsHiddenItem(entry)) {
                    return true;
                }
            }
            return false;
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            return object.has("id") && isHiddenItem(object.get("id"));
        }
        return isHiddenItem(element);
    }

    private static boolean isHiddenItem(JsonElement id) {
        if (id == null || !id.isJsonPrimitive() || !id.getAsJsonPrimitive().isString()) {
            return false;
        }
        Identifier name = Identifier.tryParse(id.getAsString());
        if (name == null) {
            return false;
        }
        // The item registry is defaulted, so an unknown name has to be ruled out before the lookup:
        // 26.1 hands back air for it, which is not something the switches should ever be asked about.
        Item item = BuiltInRegistries.ITEM.containsKey(name) ? BuiltInRegistries.ITEM.getValue(name) : null;
        return TolkienMobsConfig.isItemHidden(item);
    }
}
