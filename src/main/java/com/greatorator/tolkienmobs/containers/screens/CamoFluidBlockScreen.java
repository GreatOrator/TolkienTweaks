package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.CamoFluidBlockContainer;
import com.greatorator.tolkienmobs.containers.handlers.BucketItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CamoFluidBlockScreen extends AbstractContainerScreen<CamoFluidBlockContainer> {
    private final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/inventory_tiny.png");

    public CamoFluidBlockScreen(CamoFluidBlockContainer menu, Inventory playerInventory, Component title) {
        // 26.1 makes the two sizes final, so the 176x130 GUI goes through the constructor.
        super(menu, playerInventory, title, 176, 130);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int pX, int pY) {
        super.extractTooltip(graphics, pX, pY);
        if (hoveredSlot instanceof BucketItemHandler){
            graphics.setTooltipForNextFrame(this.font, Component.translatable("tolkienmobs.camo_fluid.fluid.instructions").withStyle(ChatFormatting.AQUA), pX, pY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, Component.translatable("block.tolkienmobs.block_camo_fluid"), 7, 4, 8552833, false);
        graphics.text(this.font, this.playerInventoryTitle, 7, 37, 8552833, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
