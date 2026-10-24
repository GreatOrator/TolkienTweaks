package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.FireplaceContainer;
import com.greatorator.tolkienmobs.containers.slots.FuelSlot;
import com.greatorator.tolkienmobs.util.LangTranslationUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FireplaceScreen extends AbstractContainerScreen<FireplaceContainer> {
    private static final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/fireplace/fireplace_gui.png");
    public static final Identifier ARROW_TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/fireplace/craft_full.png");
    public static final Identifier FIRE_TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/fireplace/fire_full.png");

    public FireplaceScreen(FireplaceContainer container, Inventory inv, Component name) {
        super(container, inv, Component.literal(""));

    }

    @Override
    public void init() {
        super.init();

        this.inventoryLabelY = 74;
        this.titleLabelX = 5;
        this.titleLabelY = 5;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.block.tolkienmobs.fireplace"), this.titleLabelX, this.titleLabelY, 4210752, false);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int pX, int pY) {
        super.extractTooltip(graphics, pX, pY);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        if (hoveredSlot instanceof FuelSlot) {
            graphics.setTooltipForNextFrame(this.font, LangTranslationUtils.remainingFuel(menu.containerData.get(2)), relX + 33, relY + 35);
        }
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
        extractProgressFireplace(graphics, x, y);
    }

    private void extractProgressArrow(GuiGraphicsExtractor graphics, int x, int y) {
        if(menu.isCrafting()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, ARROW_TEXTURE, x + 67, y + 34, 0, 0, menu.getScaledArrowProgress(), 16, 18, 18);
        }
    }

    private void extractProgressFireplace(GuiGraphicsExtractor graphics, int x, int y) {
        if(menu.isCrafting()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, FIRE_TEXTURE, x + 33, y + 35 + 16 - menu.getScaledFireplaceProgress(), 0, 16 - menu.getScaledFireplaceProgress(), 16, menu.getScaledFireplaceProgress(), 18, 18);
        }
    }
}
