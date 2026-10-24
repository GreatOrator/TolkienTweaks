package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.CamoChestContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CamoChestScreen extends AbstractContainerScreen<CamoChestContainer> {
    private static final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/inventory_medium.png");

    public CamoChestScreen(CamoChestContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        // The texture's slot grid starts higher than vanilla's chest, so both labels sit closer to the top.
        this.titleLabelY = 3;
        this.inventoryLabelY = 74;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
