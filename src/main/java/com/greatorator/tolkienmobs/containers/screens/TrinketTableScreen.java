package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.TrinketTableContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.RenderType;

import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TrinketTableScreen extends AbstractContainerScreen<TrinketTableContainer> {
    private static final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/trinkettable/trinket_gui.png");
    private static final Identifier ARROW_TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/trinkettable/craft_full.png");
    private static final Identifier TRINKET_TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/trinkettable/trinket_full.png");

    public TrinketTableScreen(TrinketTableContainer container, Inventory inv, Component name) {
        super(container, inv, Component.literal(""));

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
        guiGraphics.blit(RenderType::guiTextured, GUI, relX - 23, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(guiGraphics, x, y);
        renderProgressTrinket(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(RenderType::guiTextured, ARROW_TEXTURE, x + 65, y + 34, 0, 0, menu.getScaledArrowProgress(), 18, 18, 18);
        }
    }

    private void renderProgressTrinket(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(RenderType::guiTextured, TRINKET_TEXTURE, x + 33, y + 37 + 16 - menu.getScaledTrinketProgress(), 0, 18 - menu.getScaledTrinketProgress(), 18, menu.getScaledTrinketProgress(), 18, 18);
        }
    }
}