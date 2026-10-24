package com.greatorator.tolkienmobs.containers.handlers;

import com.google.common.base.Preconditions;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FluidTankRenderer {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    private static final int TEXTURE_SIZE = 16;
    private static final int ATLAS_TEXTURE_SIZE = 256;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    private final long capacity;
    private final TooltipMode tooltipMode;
    private final int width;
    private final int height;

    enum TooltipMode {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST
    }

    public FluidTankRenderer(long capacity, boolean showCapacity, int width, int height) {
        this(capacity, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height);
    }

    private FluidTankRenderer(long capacity, TooltipMode tooltipMode, int width, int height) {
        Preconditions.checkArgument(capacity > 0, "capacity must be > 0");
        Preconditions.checkArgument(width > 0, "width must be > 0");
        Preconditions.checkArgument(height > 0, "height must be > 0");

        this.capacity = capacity;
        this.tooltipMode = tooltipMode;
        this.width = width;
        this.height = height;
    }

    /**
     * 26.1 draws a GUI through the extractor, whose pose is an {@code org.joml.Matrix3x2fStack} and
     * has no blend or shader-colour state to switch: the GUI pipeline blends already, and the tint
     * rides on each blit's colour argument.
     */
    public void render(GuiGraphicsExtractor graphics, int x, int y, FluidStack fluidStack) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        drawFluid(graphics, width, height, fluidStack);
        graphics.pose().popMatrix();
    }

    private void drawFluid(GuiGraphicsExtractor graphics, final int width, final int height, FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid.isSame(Fluids.EMPTY)) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);
        int fluidColor = getColorTint(fluidStack);

        long amount = fluidStack.getAmount();
        long scaledAmount = (amount * height) / capacity;

        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }

        drawTiledSprite(graphics, width, height, fluidColor, scaledAmount, fluidStillSprite);
    }

    /**
     * 26.1 bakes a fluid's still and flowing sprites and its tint into a {@link FluidModel}, keyed by
     * the fluid state, instead of asking {@code IClientFluidTypeExtensions} for them per draw. The
     * sprite comes pre-baked against the block atlas, so there is no atlas lookup left to do here.
     */
    private static FluidModel getFluidModel(FluidStack fluidStack) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet()
                .get(fluidStack.getFluid().defaultFluidState());
    }

    private TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        return getFluidModel(fluidStack).stillMaterial().sprite();
    }

    private int getColorTint(FluidStack fluidStack) {
        FluidTintSource tintSource = getFluidModel(fluidStack).fluidTintSource();
        return tintSource == null ? 0xFFFFFFFF : tintSource.colorAsStack(fluidStack);
    }

    private static void drawTiledSprite(GuiGraphicsExtractor graphics, final int tiledWidth, final int tiledHeight, int color, long scaledAmount, TextureAtlasSprite sprite) {
        final int xTileCount = tiledWidth / TEXTURE_SIZE;
        final int xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
        final long yTileCount = scaledAmount / TEXTURE_SIZE;
        final long yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

        final int yStart = tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : TEXTURE_SIZE;
                long height = (yTile == yTileCount) ? yRemainder : TEXTURE_SIZE;
                int x = (xTile * TEXTURE_SIZE);
                int y = yStart - ((yTile + 1) * TEXTURE_SIZE);
                if (width > 0 && height > 0) {
                    long maskTop = TEXTURE_SIZE - height;
                    int maskRight = TEXTURE_SIZE - width;

                    drawTextureWithMasking(graphics, x, y, sprite, maskTop, maskRight, color);
                }
            }
        }
    }

    private static void drawTextureWithMasking(GuiGraphicsExtractor graphics, int xCoord, int yCoord, TextureAtlasSprite textureSprite, long maskTop, long maskRight, int color) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();
        uMax = uMax - (maskRight / 16F * (uMax - uMin));
        vMax = vMax - (maskTop / 16F * (vMax - vMin));

        int u = Math.round(uMin * ATLAS_TEXTURE_SIZE);
        int v = Math.round(vMin * ATLAS_TEXTURE_SIZE);
        int uWidth = Math.max(1, Math.round((uMax - uMin) * ATLAS_TEXTURE_SIZE));
        int vHeight = Math.max(1, Math.round((vMax - vMin) * ATLAS_TEXTURE_SIZE));

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                textureSprite.atlasLocation(),
                xCoord,
                yCoord,
                u,
                v,
                TEXTURE_SIZE - (int) maskRight,
                TEXTURE_SIZE - (int) maskTop,
                uWidth,
                vHeight,
                ATLAS_TEXTURE_SIZE,
                ATLAS_TEXTURE_SIZE,
                color
        );
    }

    public List<Component> getTooltip(FluidStack fluidStack, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();

        Fluid fluidType = fluidStack.getFluid();
        try {
            if (fluidType.isSame(Fluids.EMPTY)) {
                MutableComponent amountString;
                if (capacity > 128000) {
                    tooltip.add(Component.translatable("tolkienmobs.tooltip.liquid.amount.empty"));
                    amountString = Component.translatable("tolkienmobs.tooltip.liquid.amount.with.capacity", 0, Component.literal("∞"));
                } else {
                    amountString = Component.translatable("tolkienmobs.tooltip.liquid.amount.with.capacity", 0, nf.format(capacity));
                }
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
                return tooltip;
            }

            Component displayName = fluidStack.getHoverName();
            tooltip.add(displayName);

            long amount = fluidStack.getAmount();
            long milliBuckets = (amount * 1000) / FluidType.BUCKET_VOLUME;

            if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
                MutableComponent amountString;
                if (capacity > 128000 && milliBuckets < 128000) {
                    amountString = Component.translatable("tolkienmobs.tooltip.liquid.amount.with.capacity", nf.format(milliBuckets), Component.literal("∞"));
                } else if (capacity > 128000 && milliBuckets > 128000){
                    amountString = Component.translatable("tolkienmobs.tooltip.liquid.amount.with.capacity", nf.format("∞"), Component.literal("∞"));
                } else {
                    amountString = Component.translatable("tolkienmobs.tooltip.liquid.amount.with.capacity", nf.format(milliBuckets), nf.format(capacity));
                }
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            } else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
                MutableComponent amountString = Component.translatable("tolkienmobs.tooltip.liquid.amount", nf.format(milliBuckets));
                tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
            }
        } catch (RuntimeException e) {
            TolkienMobsMain.LOGGER.error("Failed to get tooltip for fluid: " + e);
        }

        return tooltip;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}