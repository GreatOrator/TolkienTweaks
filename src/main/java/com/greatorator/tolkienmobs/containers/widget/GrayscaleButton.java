package com.greatorator.tolkienmobs.containers.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

public class GrayscaleButton extends TolkienButton {
    private ResourceLocation texture;
    private boolean buttonActive;
    private int value;
    private Component localizationDisabled = Component.empty();

    public GrayscaleButton(int x, int y, int width, int height, ResourceLocation texture, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = true; //AlwaysActive
        this.localization = localization;
        this.value = -1;
    }

    public GrayscaleButton(int x, int y, int width, int height, ResourceLocation texture, Component localization, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = true; //AlwaysActive
        this.localization = localization;
        this.value = -1;
    }

    public GrayscaleButton(int x, int y, int width, int height, ResourceLocation texture, Component localization, boolean active, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = active;
        this.localization = localization;
        this.value = -1;
    }

    public GrayscaleButton(int x, int y, int width, int height, ResourceLocation texture, Component localizationOn, Component localizationOff, boolean active, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = active;
        this.localization = localizationOn;
        this.localizationDisabled = localizationOff;
        this.value = -1;
    }

    public GrayscaleButton(int x, int y, int width, int height, ResourceLocation texture, Component localization, boolean active, int value, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = active;
        this.localization = localization;
        this.value = value;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (buttonActive)
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        else
            RenderSystem.setShaderColor(0.33f, 0.33f, 0.33f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, width, height, width, height);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        super.onClick(p_onClick_1_, p_onClick_3_);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return super.mouseClicked(x, y, button);
    }

    public boolean getButtonActive() {
        return buttonActive;
    }

    public void toggleActive() {
        buttonActive = !buttonActive;
    }

    @Override
    public Component getLocalization() {
        if (!localizationDisabled.equals(Component.empty()) && !getButtonActive())
            return localizationDisabled;
        if (getValue() == -1 || !getButtonActive())
            return localization;
        else
            return Component.translatable(((TranslatableContents) (localization).getContents()).getKey() + "value", getValue());
    }

    public int getValue() {
        return value;
    }
}
