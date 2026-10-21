package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/** Writes the {@code variants} form of a blockstate JSON. */
public class VariantBlockStateBuilder implements IGeneratedBlockState {
    private final Block owner;
    private final Map<String, List<ConfiguredModel>> variants =
            new TreeMap<>(Comparator.naturalOrder());

    public VariantBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    public Block getOwner() {
        return this.owner;
    }

    public VariantBlockStateBuilder addModels(PartialBlockstate state, ConfiguredModel... models) {
        this.variants.computeIfAbsent(state.toString(), ignored -> new ArrayList<>()).addAll(List.of(models));
        return this;
    }

    public VariantBlockStateBuilder setModels(PartialBlockstate state, ConfiguredModel... models) {
        this.variants.put(state.toString(), new ArrayList<>(List.of(models)));
        return this;
    }

    public PartialBlockstate partialState() {
        return new PartialBlockstate(this);
    }

    public VariantBlockStateBuilder forAllStates(Function<BlockState, ConfiguredModel[]> mapper) {
        return this.forAllStatesExcept(mapper);
    }

    public VariantBlockStateBuilder forAllStatesExcept(Function<BlockState, ConfiguredModel[]> mapper, Property<?>... ignored) {
        // An ignored property is left out of the variant key, so every value of it collapses onto
        // one key: the first state of each group supplies the model and the rest are skipped.
        List<Property<?>> skip = List.of(ignored);
        for (BlockState state : this.owner.getStateDefinition().getPossibleStates()) {
            String key = PartialBlockstate.toKey(state, skip);
            if (!this.variants.containsKey(key)) {
                this.variants.put(key, new ArrayList<>(List.of(mapper.apply(state))));
            }
        }
        return this;
    }

    public JsonObject toJson() {
        JsonObject variantsJson = new JsonObject();
        this.variants.forEach((key, models) -> {
            if (models.size() == 1) {
                variantsJson.add(key, models.get(0).toJson());
            } else {
                var array = new com.google.gson.JsonArray();
                models.forEach(model -> array.add(model.toJson()));
                variantsJson.add(key, array);
            }
        });
        JsonObject root = new JsonObject();
        root.add("variants", variantsJson);
        return root;
    }

    public static class PartialBlockstate {
        private final VariantBlockStateBuilder owner;
        private final Map<Property<?>, Comparable<?>> states = new LinkedHashMap<>();

        private PartialBlockstate(VariantBlockStateBuilder owner) {
            this.owner = owner;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public <T extends Comparable<T>> PartialBlockstate with(Property<T> property, T value) {
            this.states.put(property, value);
            return this;
        }

        public ConfiguredModel.Builder<VariantBlockStateBuilder> modelForState() {
            return new ConfiguredModel.Builder<>(models -> this.owner.setModels(this, models));
        }

        public VariantBlockStateBuilder addModels(ConfiguredModel... models) {
            return this.owner.addModels(this, models);
        }

        public VariantBlockStateBuilder setModels(ConfiguredModel... models) {
            return this.owner.setModels(this, models);
        }

        public Block getOwner() {
            return this.owner.owner;
        }

        @Override
        public String toString() {
            List<String> parts = new ArrayList<>();
            this.states.entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
                    .forEach(entry -> parts.add(entry.getKey().getName() + "=" + name(entry.getKey(), entry.getValue())));
            return String.join(",", parts);
        }

        static String toKey(BlockState state) {
            return toKey(state, List.of());
        }

        static String toKey(BlockState state, List<Property<?>> ignored) {
            List<String> parts = new ArrayList<>();
            state.getProperties().stream()
                    .filter(property -> !ignored.contains(property))
                    .sorted(Comparator.comparing(Property::getName))
                    .forEach(property -> parts.add(property.getName() + "=" + name(property, state.getValue(property))));
            return String.join(",", parts);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static String name(Property property, Comparable value) {
            return property.getName(value);
        }
    }
}
