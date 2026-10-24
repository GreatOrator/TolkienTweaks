package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.PiggyBankContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class PiggyBankScreen extends AbstractContainerScreen<PiggyBankContainer> {
    private static final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/inventory_large.png");

    public PiggyBankScreen(PiggyBankContainer container, Inventory inv, Component name) {
        // 26.1 makes the two sizes final, so the 221-high GUI goes through the constructor.
        super(container, inv, Component.literal(""), 176, 221);
    }

    @Override
    public void init() {
        super.init();

        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.block.tolkienmobs.block_piggybank"), -15, 3, 8552833, false);
        graphics.text(this.font, this.playerInventoryTitle, -15, 128, 8552833, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX - 23, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
