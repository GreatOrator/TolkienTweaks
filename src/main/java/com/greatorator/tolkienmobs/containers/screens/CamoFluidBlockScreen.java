package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoFluidBlockContainer;
import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
import com.greatorator.tolkienmobs.containers.handlers.BucketItemHandler;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.NumberButton;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.network.KeyStoneDataUpdateManager;
import com.greatorator.tolkienmobs.network.KeyStoneDelayUpdateManager;
import com.greatorator.tolkienmobs.network.KeyStoneRedstoneUpdateManager;
import com.greatorator.tolkienmobs.network.KeyStoneSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.KeyStoneCode;
import com.greatorator.tolkienmobs.util.KeyStoneSettings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CamoFluidBlockScreen extends AbstractContainerScreen<CamoFluidBlockContainer> {
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/inventory_tiny.png");

    public CamoFluidBlockScreen(CamoFluidBlockContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 130;
        this.imageWidth = 176;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        if (hoveredSlot instanceof BucketItemHandler){
            pGuiGraphics.renderTooltip(this.font, Component.translatable("tolkienmobs.camo_fluid.fluid.instructions").withStyle(ChatFormatting.AQUA), pX, pY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.translatable("block.tolkienmobs.block_camo_fluid"), 7, 4, 8552833, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 7, 37, 8552833, false);
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
        return super.mouseClicked(x, y, btn);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }
}
