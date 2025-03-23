package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.KeyCodeContainer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.NumberButton;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import com.greatorator.tolkienmobs.network.KeyCodeUpdateManager;
import com.greatorator.tolkienmobs.network.KeyStoneRedstoneUpdateManager;
import com.greatorator.tolkienmobs.network.KeyStoneSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class KeyCodeScreen extends AbstractContainerScreen<KeyCodeContainer> {
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/inventory_text.png");
    protected final KeyCodeContainer container;
    public static ItemStack keyStack;
    private final InteractionHand hand;
    private EditBox nameField;
    private String keyCode;
    private int keyUses;
    private boolean keyMode;

    public KeyCodeScreen(KeyCodeContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        keyStack = inv.player.getItemInHand(InteractionHand.MAIN_HAND);
        this.hand = inv.player.swingingArm;
        this.container = container;
        this.imageHeight = 62;
        this.keyCode = KeyItem.getKeyCode(this.keyStack);
        this.keyUses = KeyItem.getUses(this.keyStack);
        this.keyMode = KeyItem.getMode(this.keyStack);

    }

    @Override
    public void init() {
        super.init();
        var saveCodeButton = this.addRenderableWidget(TolkienButton
                .builder(Component.translatable("buttons.tolkienmobs.saveinfo"), button -> {
                    sendMessageToServer();
                    this.minecraft.setScreen(null);
                }, ButtonTexture.LARGE_BUTTON)
                .size(64, 20)
                .build());
        var layout = new LinearLayout((leftPos - 20) + 122, topPos + 36,
                LinearLayout.Orientation.HORIZONTAL);
        layout.spacing(4);
        layout.addChild(saveCodeButton);
        layout.arrangeElements();

        addNameField();
        addKeyUsesButton();
        addKeyStoneButtons();
    }

    public void addKeyUsesButton() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.KEYUSESBUTTON(offsetX + 36, offsetY + 36, KeyItem.getUses(this.keyStack), b -> {
            keyUses = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new KeyCodeUpdateManager(InteractionHand.MAIN_HAND, this.nameField.getValue(), this.keyUses, this.keyMode));
        }));
    }

    public void addNameField() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(this.nameField = new EditBox(this.font, offsetX + 16, offsetY + 16, imageWidth - 31, this.font.lineHeight + 7, Component.literal(KeyItem.getKeyCode(this.keyStack))));

        this.nameField.setValue(this.keyCode);
        this.nameField.setMaxLength(50);
        this.nameField.setVisible(true);
    }

    public void addKeyStoneButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.KEEPKEYBUTTON(offsetX + 16, offsetY + 36, KeyItem.getMode(this.keyStack), b -> {
            this.keyMode = !this.keyMode;
            PacketDistributor.sendToServer(new KeyCodeUpdateManager(InteractionHand.MAIN_HAND, this.nameField.getValue(), this.keyUses, this.keyMode));
        }));
    }

    public int adjustNumberButton(int value, int change, int min, int max) {
        if (Screen.hasShiftDown()) change *= 10;
        if (Screen.hasControlDown()) change *= 64;
        if (change < 0) {
            value = (Math.max(value + change, min));
        } else {
            value = (Math.min(value + change, max));
        }
        return value;
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof TolkienButton button && !button.getLocalization(pX, pY).equals(Component.empty()))
                pGuiGraphics.renderTooltip(font, button.getLocalization(pX, pY), pX, pY);
        }
        Component tooltip = Component.literal("");
        tooltip = Component.translatable("screen.tolkienmobs.namefieldtext");
        this.nameField.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.nameField.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 5, 5, 8552833);

        if (!nameField.isFocused() && nameField.getValue().isEmpty())
            guiGraphics.drawString(font, KeyItem.getKeyCode(this.keyStack), nameField.getX() - leftPos + 4, (nameField.getY() + 2) - topPos, -10197916);
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
        boolean ret = super.mouseClicked(x, y, btn);
        if(this.nameField.mouseClicked(x, y, btn)) {
            this.nameField.setFocused(true);
            ret = true;
        }
        else if(this.nameField.isFocused()) {
            this.nameField.setFocused(false);
            ret = true;
        }
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof NumberButton numberButton && GeneralUtility.inBounds(numberButton.getX(), numberButton.getY(), numberButton.getWidth(), numberButton.getHeight(), x, y)) {
                if (btn == 0)
                    numberButton.setValue(adjustNumberButton(numberButton.getValue(), 1, numberButton.min, numberButton.max));
                else if (btn == 1)
                    numberButton.setValue(adjustNumberButton(numberButton.getValue(), -1, numberButton.min, numberButton.max));
                numberButton.onPress();
                numberButton.playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
            if (renderable instanceof ToggleButton toggleButton && GeneralUtility.inBounds(toggleButton.getX(), toggleButton.getY(), toggleButton.getWidth(), toggleButton.getHeight(), x, y)) {
                if (btn == 1) {
                    toggleButton.onClick(x, y, btn);
                    toggleButton.playDownSound(Minecraft.getInstance().getSoundManager());
                }
            }
        }
        return ret;
    }

    @Override
    public boolean keyPressed(int key, int scancode, int p_keyPressed_3_) {
        if(this.nameField.isFocused()&&key!=GLFW.GLFW_KEY_ESCAPE)
            if(this.nameField.keyPressed(key, scancode, p_keyPressed_3_)||this.nameField.canConsumeInput())
                return true;
        return super.keyPressed(key, scancode, p_keyPressed_3_);
    }

    private void sendMessageToServer() {
        this.keyCode = this.nameField.getValue();
        this.keyCode = this.keyCode.trim();
        var success = KeyItem.setKeyData(this.keyStack, this.keyCode, this.keyUses, this.keyMode);

        if (success) {
            PacketDistributor.sendToServer(new KeyCodeUpdateManager(InteractionHand.MAIN_HAND, this.keyCode, this.keyUses, this.keyMode));
        }
    }
}