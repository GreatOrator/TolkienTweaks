package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BackpackBlockScreen extends AbstractContainerScreen<BackpackBlockContainer> {
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_full_upgrade_2.png");
    protected final ResourceLocation FLUIDBAR = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/tank_overlay.png");
    protected BackpackBlockContainer container;
    private final BackpackBlockEntity tileEntity;

    public BackpackBlockScreen(BackpackBlockContainer container, Inventory inv, Component name) {
        super(container, inv, Component.literal(""));
        this.imageHeight = 256;
        this.imageWidth = 256;
        this.container = container;
        tileEntity = container.tileEntity;
    }

    @Override
    public void init() {
        super.init();

        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.block.tolkienmobs.backpack"), 64, 3, 8552833, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 64, 163, 8552833, false);
    }

    @Override
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        super.renderSlotContents(guiGraphics, itemstack, slot, countString);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX - 23, relY, 0, 0, this.imageWidth, this.imageHeight);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(FLUIDBAR, relX, relY + 5, 0, 0, 11, 75, 12, 75);
        int maxMB = tileEntity.getMaxMB(), height = 70;
        if (maxMB > 0) {
            int remaining = (this.container.getFluidAmount() * height) / maxMB;
            renderFluid(guiGraphics, relX, relY + 5 + 75 - 1, 11, remaining);
        }
        guiGraphics.blit(FLUIDBAR, relX, relY + 5, 0, 0, 11, 75, 12, 75);
    }

    public void renderFluid(GuiGraphics guiGraphics, int startX, int startY, int width, int height) {
        FluidStack fluidStack = container.getFluidStack();
        if (fluidStack.isEmpty() || height <= 0) return;

        Fluid fluid = fluidStack.getFluid();
        ResourceLocation fluidStill = IClientFluidTypeExtensions.of(fluid).getStillTexture();
        TextureAtlasSprite fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
        int fluidColor = IClientFluidTypeExtensions.of(fluid).getTintColor(fluidStack);

        float red = (float) (fluidColor >> 16 & 255) / 255.0F;
        float green = (float) (fluidColor >> 8 & 255) / 255.0F;
        float blue = (float) (fluidColor & 255) / 255.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        RenderSystem.setShaderColor(red, green, blue, 1.0f);

        int zLevel = 0;
        float uMin = fluidStillSprite.getU0();
        float uMax = fluidStillSprite.getU1();
        float vMin = fluidStillSprite.getV0();
        float vMax = fluidStillSprite.getV1();
        int textureWidth = fluidStillSprite.contents().width();
        int textureHeight = fluidStillSprite.contents().height();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder vertexBuffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        int yOffset = 0;
        while (yOffset < height) {
            int drawHeight = Math.min(textureHeight, height - yOffset);
            int drawY = startY - yOffset - drawHeight; // Adjust for bottom-to-top drawing

            float vMaxAdjusted = vMin + (vMax - vMin) * ((float) drawHeight / textureHeight);

            int xOffset = 0;
            while (xOffset < width) {
                int drawWidth = Math.min(textureWidth, width - xOffset);

                float uMaxAdjusted = uMin + (uMax - uMin) * ((float) drawWidth / textureWidth);

                vertexBuffer.addVertex(poseStack.last().pose(), startX + xOffset, drawY + drawHeight, zLevel).setUv(uMin, vMaxAdjusted);
                vertexBuffer.addVertex(poseStack.last().pose(), startX + xOffset + drawWidth, drawY + drawHeight, zLevel).setUv(uMaxAdjusted, vMaxAdjusted);
                vertexBuffer.addVertex(poseStack.last().pose(), startX + xOffset + drawWidth, drawY, zLevel).setUv(uMaxAdjusted, vMin);
                vertexBuffer.addVertex(poseStack.last().pose(), startX + xOffset, drawY, zLevel).setUv(uMin, vMin);

                xOffset += drawWidth;
            }
            yOffset += drawHeight;
        }

        BufferUploader.drawWithShader(vertexBuffer.buildOrThrow());
        poseStack.popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.applyModelViewMatrix();
    }
}
