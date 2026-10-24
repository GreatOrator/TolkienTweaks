package com.greatorator.tolkienmobs.datagen.modelgen;

import net.minecraft.resources.Identifier;

/**
 * The NeoForge {@code neoforge:item_layers} loader, used for items that draw more than one
 * texture layer with optional emissive lighting.
 */
public class ItemLayerModelBuilder extends CustomLoaderBuilder<ItemModelBuilder> {
    public static ItemLayerModelBuilder begin(ItemModelBuilder builder) {
        return new ItemLayerModelBuilder(builder);
    }

    protected ItemLayerModelBuilder(ItemModelBuilder parent) {
        super(Identifier.fromNamespaceAndPath("neoforge", "item_layers"), parent);
    }

    public ItemLayerModelBuilder emissive(int block, int sky, int index) {
        this.json.addProperty("emissive", true);
        return this;
    }

    public ItemLayerModelBuilder renderType(String renderType, int index) {
        super.renderType(renderType);
        return this;
    }
}
