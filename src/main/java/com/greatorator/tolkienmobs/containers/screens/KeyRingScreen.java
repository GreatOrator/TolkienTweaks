package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.KeyRingContainer;
import com.greatorator.tolkienmobs.containers.slots.KeyRingSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class KeyRingScreen extends AbstractContainerScreen<KeyRingContainer> {
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/inventory_small.png");
    protected final KeyRingContainer container;

    public KeyRingScreen(KeyRingContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        this.container = container;
        this.imageHeight = 181;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 5, 5, 8552833);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (btn == 1 && hoveredSlot instanceof KeyRingSlot) { //Right click
            int slot = hoveredSlot.getSlotIndex();
            return true;
        }
        return super.mouseClicked(x, y, btn);
    }
}