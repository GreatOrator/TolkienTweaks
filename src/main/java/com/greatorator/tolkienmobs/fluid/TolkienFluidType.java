package com.greatorator.tolkienmobs.fluid;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    public IClientFluidTypeExtensions getClientFluidTypeExtensions() {
        return new IClientFluidTypeExtensions() {
            @Override
            public Identifier getStillTexture() {
                return stillTexture;
            }

            @Override
            public Identifier getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public @Nullable Identifier getOverlayTexture() {
                return overlayTexture;
            }

            @Override
            public int getTintColor() {
                return tintColor;
            }

            @Override
            public @NotNull Vector4f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                                    int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                return new Vector4f(fogColor, 1f);
            }

            @Override
            public @NotNull FogParameters modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick,
                                                          FogParameters fogParameters) {
                return new FogParameters(1f, 6f, fogParameters.shape(), fogParameters.red(), fogParameters.green(),
                        fogParameters.blue(), fogParameters.alpha());
            }
        };
    }
}