package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableEntity;
import com.greatorator.tolkienmobs.containers.TrinketTableContainer;
import com.greatorator.tolkienmobs.containers.slots.GemSlot;
import com.greatorator.tolkienmobs.containers.slots.PotionSlot;
import com.greatorator.tolkienmobs.containers.slots.TrinketSlot;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TrinketTableScreen extends AbstractContainerScreen<TrinketTableContainer> {
    public static final int SLOT_COLOR = -2130706433;
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/trinkettable/trinket_gui.png");

    private int renderSlot = 0;

    private final TrinketTableEntity tileEntity;
    private final TrinketTableContainer container;

    public TrinketTableScreen(TrinketTableContainer container, Inventory inv, Component name) {
        super(container, inv, Component.literal(""));

        this.container = container;
        this.tileEntity = container.getTe();
    }

    @Override
    public void init() {
        super.init();
        this.renderSlot = 1;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderProgress(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.block.tolkienmobs.trinket_table"), this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
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
    }

    protected void renderProgress(GuiGraphics graphics)
    {
        int maxProgress = this.menu.getMaxWork();
        int width = 23; // Overlay Width
        if (maxProgress > 0)
        {
            int remaining = this.menu.getWork();
            float progressRatio = 1.0f - ((float) remaining / (float) maxProgress);
            int uWidth = (int)(progressRatio * width);
            graphics.blit(GUI, leftPos + 56, topPos + 34, 177, 0, uWidth, 18);
        }
    }
}