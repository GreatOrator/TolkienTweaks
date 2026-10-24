package com.greatorator.tolkienmobs.fluid;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.fluid.FluidTintSources;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A fluid whose still, flowing and overlay sprites come from the fluid model rather than from the
 * client extensions: 26.1 moved the sprites to {@code FluidModel}, registered per fluid on
 * {@code RegisterFluidModelsEvent}, and the extensions now only carry the overlay and the fog.
 */
public class TolkienFluidType extends FluidType {
    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final Identifier overlayTexture;
    private final int tintColor;
    private final Vector3f fogColor;

    public TolkienFluidType(final Identifier stillTexture, final Identifier flowingTexture, final Identifier overlayTexture,
                            final int tintColor, final Vector3f fogColor, final Properties properties) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
    }

    /**
     * Registers this type's sprites and tint for a fluid's still and flowing forms. Both forms show
     * the same model's source or flowing sprites, picked by the renderer from the fluid's height.
     */
    public void registerFluidModel(RegisterFluidModelsEvent event, Fluid still, Fluid flowing) {
        event.register(new FluidModel.Unbaked(
                new Material(stillTexture),
                new Material(flowingTexture),
                overlayTexture != null ? new Material(overlayTexture) : null,
                FluidTintSources.constant(tintColor)), still, flowing);
    }

    public IClientFluidTypeExtensions getClientFluidTypeExtensions() {
        return new IClientFluidTypeExtensions() {
            @Override
            public void modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                       int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                fluidFogColor.set(fogColor, 1f);
            }

            @Override
            public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance,
                                        float partialTick, FogData fogData) {
                fogData.environmentalStart = 1f;
                fogData.environmentalEnd = 6f;
                fogData.renderDistanceStart = 1f;
                fogData.renderDistanceEnd = 6f;
            }
        };
    }
}
