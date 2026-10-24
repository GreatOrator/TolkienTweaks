package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.TrinketTableContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

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
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.block.tolkienmobs.trinket_table"), this.titleLabelX, this.titleLabelY, 4210752, false);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX - 23, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        extractProgressArrow(graphics, x, y);
        extractProgressTrinket(graphics, x, y);
    }

    private void extractProgressArrow(GuiGraphicsExtractor graphics, int x, int y) {
        if(menu.isCrafting()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, ARROW_TEXTURE, x + 65, y + 34, 0, 0, menu.getScaledArrowProgress(), 18, 18, 18);
        }
    }

    private void extractProgressTrinket(GuiGraphicsExtractor graphics, int x, int y) {
        if(menu.isCrafting()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TRINKET_TEXTURE, x + 33, y + 37 + 16 - menu.getScaledTrinketProgress(), 0, 18 - menu.getScaledTrinketProgress(), 18, menu.getScaledTrinketProgress(), 18, 18);
        }
    }
}
