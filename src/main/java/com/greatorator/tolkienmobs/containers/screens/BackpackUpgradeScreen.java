package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.BackpackUpgradeContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BackpackUpgradeScreen extends AbstractContainerScreen<BackpackUpgradeContainer> {
    private static final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_upgrade.png");
    private static final Component NAME = Component.translatable("screen.tolkienmobs.backpack.backpack_upgrade");

    public BackpackUpgradeScreen(BackpackUpgradeContainer menu, Inventory playerInventory, Component name) {
        // 26.1 makes the two sizes final, so the 86x86 panel goes through the constructor.
        super(menu, playerInventory, NAME, 86, 86);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.backpack.backpack_upgrade"), -15, 3, 8552833, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX - 23, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
