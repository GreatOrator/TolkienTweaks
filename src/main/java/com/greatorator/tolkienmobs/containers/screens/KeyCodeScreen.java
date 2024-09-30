package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.containers.KeyCodeContainer;
import com.greatorator.tolkienmobs.containers.slots.KeyCodeSlot;
import com.greatorator.tolkienmobs.handler.TolkienDataComponents;
import com.greatorator.tolkienmobs.network.KeyCodePayload;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.UUID;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class KeyCodeScreen extends AbstractContainerScreen<KeyCodeContainer> {
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/key_code.png");
    protected final KeyCodeContainer container;
    public ItemStack keyStack;
    private EditBox nameField;
    private Button buttonSave;

    public KeyCodeScreen(KeyCodeContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        this.keyStack = inv.player.getItemInHand(InteractionHand.MAIN_HAND);
        this.container = container;
        this.imageHeight = 62;
    }

    @Override
    public void init() {
        super.init();
        this.nameField = new EditBox(this.font, (this.leftPos) + 16, topPos + 17, imageWidth - 31, this.font.lineHeight + 7, Component.translatable(""));
        updateNameField();

        buttonSave = new ExtendedButton((leftPos - 20) + 122, topPos + 36, 60, 13, Component.translatable("buttons.tolkienmobs.save"), (button) -> {
            KeyCodeContainer.setCode(TolkienDataComponents.KEY_CODE, this.nameField.getValue());
        });

        addRenderableWidget(buttonSave);
        this.nameField.setMaxLength(50);
        this.nameField.setVisible(true);
        addRenderableWidget(nameField);

    }

        @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void updateNameField() {
        this.nameField.setValue(TolkienDataComponents.KEY_CODE.get().toString());
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 5, 5, 8552833);

        if (!nameField.isFocused() && nameField.getValue().isEmpty())
            guiGraphics.drawString(font, container.getCode(), nameField.getX() - leftPos + 4, (nameField.getY() + 2) - topPos, -10197916);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        this.nameField.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (btn == 1 && hoveredSlot instanceof KeyCodeSlot) { //Right click
            int slot = hoveredSlot.getSlotIndex();
            return true;
        }
        return super.mouseClicked(x, y, btn);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256) {
            this.onClose();
            return true;
        }

        return this.nameField.isFocused() ? this.nameField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
}
