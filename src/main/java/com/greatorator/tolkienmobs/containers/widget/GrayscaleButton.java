package com.greatorator.tolkienmobs.containers.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;

public class GrayscaleButton extends TolkienButton {
    private Identifier texture;
    private boolean buttonActive;
    private int value;
    private Component localizationDisabled = Component.empty();

    public GrayscaleButton(int x, int y, int width, int height, Identifier texture, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = true; //AlwaysActive
        this.localization = localization;
        this.value = -1;
    }

    public GrayscaleButton(int x, int y, int width, int height, Identifier texture, Component localization, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = true; //AlwaysActive
        this.localization = localization;
        this.value = -1;
    }

    public GrayscaleButton(int x, int y, int width, int height, Identifier texture, Component localization, boolean active, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = active;
        this.localization = localization;
        this.value = -1;
    }

    public GrayscaleButton(int x, int y, int width, int height, Identifier texture, Component localizationOn, Component localizationOff, boolean active, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = active;
        this.localization = localizationOn;
        this.localizationDisabled = localizationOff;
        this.value = -1;
    }

    public GrayscaleButton(int x, int y, int width, int height, Identifier texture, Component localization, boolean active, int value, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.buttonActive = active;
        this.localization = localization;
        this.value = value;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int tint = buttonActive ? 0xFFFFFFFF : 0xFF545454;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), 0, 0, width, height, width, height, width, height, tint);
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
