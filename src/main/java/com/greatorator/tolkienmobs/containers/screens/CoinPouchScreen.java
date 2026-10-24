package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.CoinPouchContainer;
import com.greatorator.tolkienmobs.containers.slots.CoinPouchSlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CoinPouchScreen extends AbstractContainerScreen<CoinPouchContainer> {
    private final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/inventory_small.png");
    protected final CoinPouchContainer container;

    public CoinPouchScreen(CoinPouchContainer container, Inventory inv, Component name) {
        // 26.1 makes the two sizes final, so the 181-high GUI goes through the constructor.
        super(container, inv, name, 176, 181);
        this.container = container;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 5, 5, 8552833);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    /** A right click on one of the pouch's own slots is swallowed, so nothing inspects that stack. */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1 && hoveredSlot instanceof CoinPouchSlot) { //Right click
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}
