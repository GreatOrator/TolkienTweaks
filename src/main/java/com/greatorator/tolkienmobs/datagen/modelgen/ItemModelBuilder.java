package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/** A model written under {@code assets/<ns>/models/item/}. */
public class ItemModelBuilder extends ModelBuilder<ItemModelBuilder> {
    public ItemModelBuilder(ResourceLocation location, String name) {
        super(location, name);
    }

    public OverrideBuilder override() {
        JsonObject override = new JsonObject();
        this.overrides.add(override);
        return new OverrideBuilder(override);
    }

    public class OverrideBuilder {
        private final JsonObject override;
        private final JsonObject predicates = new JsonObject();

        private OverrideBuilder(JsonObject override) {
            this.override = override;
        }

        public OverrideBuilder model(ModelFile model) {
            this.override.addProperty("model", model.getLocation().toString());
            return this;
        }

        public OverrideBuilder predicate(String key, float value) {
            this.predicates.addProperty(key, value);
            return this;
        }

        public ItemModelBuilder end() {
            if (!this.predicates.isEmpty()) {
                this.override.add("predicate", this.predicates);
            }
            return ItemModelBuilder.this;
        }
    }
}
