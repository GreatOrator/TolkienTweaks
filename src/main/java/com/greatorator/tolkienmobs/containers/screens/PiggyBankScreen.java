package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.PiggyBankContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class PiggyBankScreen extends AbstractContainerScreen<PiggyBankContainer> {
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/inventory_base_upgrade_2.png");

    public PiggyBankScreen(PiggyBankContainer container, Inventory inv, Component name) {
        super(container, inv, Component.literal(""));
        this.imageHeight = 239;
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
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.block.tolkienmobs.block_piggybank"), -15, 3, 8552833, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, -15, 145, 8552833, false);
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
    }
}