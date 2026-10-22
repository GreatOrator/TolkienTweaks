package com.greatorator.tolkienmobs.block.custom.entity.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.client.model.ExtendedUnbakedModel;

/**
 * The unbaked half of the patch model.
 *
 * <p>1.21.4 replaced NeoForge's geometry API ({@code IUnbakedGeometry}, {@code IGeometryBakingContext},
 * {@code IGeometryLoader}) with vanilla's model pipeline, so a custom loader now hands the bakery a
 * {@link net.minecraft.client.resources.model.UnbakedModel} that bakes itself. The patch keeps its own
 * {@code texture} entry rather than the standard {@code textures} block, because it draws that one sprite on
 * every face it generates.
 */
public record UnbakedPatchModel(Material material, boolean shaggify) implements ExtendedUnbakedModel {
    public UnbakedPatchModel(ResourceLocation texture, boolean shaggify) {
        this(new Material(TextureAtlas.LOCATION_BLOCKS, texture), shaggify);
    }

    /** The patch is drawn entirely from the texture the model names, so it has no parent to resolve. */
    @Override
    public void resolveDependencies(Resolver resolver) {
    }

    @Override
    public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion,
                           boolean usesBlockLight, ItemTransforms itemTransforms, ContextMap additionalProperties) {
        return new PatchModel(baker.sprites().get(this.material), this.shaggify);
    }
}
