package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/**
 * Builds the {@code model} node of an item model definition.
 *
 * <p>Since 1.21.4 an item points at a definition under {@code assets/<ns>/items/}, and the
 * definition holds the tree that used to live in a model's {@code overrides} array. Every
 * method here returns the node itself, ready to be wrapped by
 * {@link ItemModelProvider#definition}.</p>
 */
public final class ItemModelDefinition {
    private static final String MODEL = "minecraft:model";
    private static final String SPECIAL = "minecraft:special";
    private static final String RANGE_DISPATCH = "minecraft:range_dispatch";
    private static final String CONDITION = "minecraft:condition";
    private static final String SELECT = "minecraft:select";
    private static final String CONSTANT_TINT = "minecraft:constant";

    private ItemModelDefinition() {
    }

    /** A plain model, drawn with no tints. */
    public static JsonObject model(ResourceLocation model) {
        JsonObject node = typed(MODEL);
        node.addProperty("model", model.toString());
        return node;
    }

    /** A model drawn through one tint source per texture layer, in layer order. */
    public static JsonObject model(ResourceLocation model, JsonObject... tints) {
        JsonObject node = model(model);
        JsonArray array = new JsonArray();
        for (JsonObject tint : tints) {
            array.add(tint);
        }
        node.add("tints", array);
        return node;
    }

    /**
     * A model drawn by a registered special renderer instead of by quads, which is how 1.21.4
     * renders a GeckoLib item now that {@code builtin/entity} is gone. {@code base} is still an
     * ordinary model: it supplies the display transforms and the particle sprite, so it needs no
     * elements of its own.
     */
    public static JsonObject special(ResourceLocation base, ResourceLocation renderer) {
        JsonObject node = typed(SPECIAL);
        node.addProperty("base", base.toString());
        node.add("model", typed(renderer.toString()));
        return node;
    }

    /** A tint source that paints every pixel the same colour. */
    public static JsonObject constantTint(int argb) {
        JsonObject tint = typed(CONSTANT_TINT);
        tint.addProperty("value", argb);
        return tint;
    }

    /** A tint source registered on the client, such as {@code tolkienmobs:trinket}. */
    public static JsonObject tintSource(ResourceLocation id) {
        return typed(id.toString());
    }

    /**
     * Picks a model by comparing a float property against the entries in ascending threshold
     * order, falling back to {@code fallback} below the first threshold.
     */
    public static JsonObject rangeDispatch(ResourceLocation property, JsonObject fallback, Entry... entries) {
        JsonObject node = typed(RANGE_DISPATCH);
        node.addProperty("property", property.toString());
        JsonArray array = new JsonArray();
        for (Entry entry : entries) {
            JsonObject json = new JsonObject();
            json.addProperty("threshold", entry.threshold());
            json.add("model", entry.model());
            array.add(json);
        }
        node.add("entries", array);
        node.add("fallback", fallback);
        return node;
    }

    public static Entry entry(float threshold, JsonObject model) {
        return new Entry(threshold, model);
    }

    /**
     * Picks a model by matching a registry-backed select property, such as the trim material on
     * a piece of armour, falling back to {@code fallback} when nothing matches.
     */
    public static JsonObject select(ResourceLocation property, JsonObject fallback, Case... cases) {
        JsonObject node = typed(SELECT);
        node.addProperty("property", property.toString());
        JsonArray array = new JsonArray();
        for (Case entry : cases) {
            JsonObject json = new JsonObject();
            json.addProperty("when", entry.when());
            json.add("model", entry.model());
            array.add(json);
        }
        node.add("cases", array);
        node.add("fallback", fallback);
        return node;
    }

    public static Case when(String when, JsonObject model) {
        return new Case(when, model);
    }

    /** Picks {@code onTrue} or {@code onFalse} from a boolean property. */
    public static JsonObject condition(ResourceLocation property, JsonObject onTrue, JsonObject onFalse) {
        JsonObject node = typed(CONDITION);
        node.addProperty("property", property.toString());
        node.add("on_true", onTrue);
        node.add("on_false", onFalse);
        return node;
    }

    private static JsonObject typed(String type) {
        JsonObject node = new JsonObject();
        node.addProperty("type", type);
        return node;
    }

    public record Entry(float threshold, JsonObject model) {
    }

    public record Case(String when, JsonObject model) {
    }
}
