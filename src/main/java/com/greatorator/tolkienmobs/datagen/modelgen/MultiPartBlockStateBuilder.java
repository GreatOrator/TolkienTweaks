package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

/** Writes the {@code multipart} form of a blockstate JSON, as walls, fences and panes use. */
public class MultiPartBlockStateBuilder {
    private final Block owner;
    private final List<PartBuilder> parts = new ArrayList<>();

    public MultiPartBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    public Block getOwner() {
        return this.owner;
    }

    public ConfiguredModel.Builder<PartBuilder> part() {
        PartBuilder part = new PartBuilder();
        this.parts.add(part);
        return new ConfiguredModel.Builder<>(part::withModels);
    }

    public JsonObject toJson() {
        JsonArray multipart = new JsonArray();
        this.parts.forEach(part -> multipart.add(part.toJson()));
        JsonObject root = new JsonObject();
        root.add("multipart", multipart);
        return root;
    }

    public class PartBuilder {
        private final List<ConfiguredModel> models = new ArrayList<>();
        private final Map<Property<?>, List<Comparable<?>>> conditions = new LinkedHashMap<>();
        private boolean useOr;

        private PartBuilder withModels(ConfiguredModel[] models) {
            this.models.addAll(List.of(models));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T extends Comparable<T>> PartBuilder condition(Property<T> property, T value) {
            this.conditions.computeIfAbsent(property, ignored -> new ArrayList<>()).add(value);
            return this;
        }

        public PartBuilder useOr() {
            this.useOr = true;
            return this;
        }

        public MultiPartBlockStateBuilder end() {
            return MultiPartBlockStateBuilder.this;
        }

        private JsonObject toJson() {
            JsonObject part = new JsonObject();
            if (this.models.size() == 1) {
                part.add("apply", this.models.get(0).toJson());
            } else {
                JsonArray apply = new JsonArray();
                this.models.forEach(model -> apply.add(model.toJson()));
                part.add("apply", apply);
            }
            if (!this.conditions.isEmpty()) {
                JsonObject when = new JsonObject();
                this.conditions.forEach((property, values) -> {
                    if (values.size() == 1) {
                        when.addProperty(property.getName(), name(property, values.get(0)));
                    } else {
                        JsonArray any = new JsonArray();
                        values.forEach(value -> any.add(name(property, value)));
                        when.add(this.useOr ? "OR" : property.getName(), any);
                    }
                });
                part.add("when", when);
            }
            return part;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private String name(Property property, Comparable value) {
            return property.getName(value);
        }
    }
}
