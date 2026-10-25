package com.greatorator.tolkienmobs.containers.widget;

import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory.TextureLocalization;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ToggleButton extends TolkienButton {
    private List<TextureLocalization> textureLocalizations;
    private int texturePosition;
    private int maxValue = -1;

    public ToggleButton(int x, int y, int width, int height, List<TextureLocalization> textureLocalizations, int texturePosition, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);

        this.textureLocalizations = textureLocalizations;
        setTexturePosition(texturePosition);
    }

    public ToggleButton(int x, int y, int width, int height, List<TextureLocalization> textureLocalizations, int texturePosition, int maxValue, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);

        this.textureLocalizations = textureLocalizations;
        setTexturePosition(texturePosition);
        this.maxValue = maxValue;
    }

    public ToggleButton(int x, int y, int width, int height, List<TextureLocalization> textureLocalizations, boolean texturePosition, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);

        this.textureLocalizations = textureLocalizations;
        setTexturePosition(texturePosition);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, textureLocalizations.get(getTexturePosition()).texture(), this.getX(), this.getY(), 0, 0, width, height, width, height);
    }

    public int getTexturePosition() {
        return texturePosition >= textureLocalizations.size() ? 0 : texturePosition;
    }

    public void setTexturePosition(boolean texturePosition) {
        setTexturePosition(texturePosition ? 1 : 0);
    }

    /**
     * A right click steps the toggle backwards and anything else forwards, which is why the screen
     * calls this by hand for button 1 - vanilla only dispatches left clicks to a widget.
     */
    public void onClick(MouseButtonEvent event) {
        if (event.button() == 1)
            previousTexturePosition();
        else
            nextTexturePosition();
        onPress(event);
    }

    /**
     * 26.1 hands a left click to {@link net.minecraft.client.gui.components.AbstractWidget#onClick},
     * which used to be a no-op, so the press callback ran without the texture moving: a toggle with
     * three faces only ever showed the first. Stepping it here keeps the face and the setting the
     * callback reports in step.
     */
    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        nextTexturePosition();
        super.onClick(event, doubleClick);
    }

    public void setTexturePosition(int texturePosition) {
        if (texturePosition >= textureLocalizations.size())
            this.texturePosition = textureLocalizations.size();
        else
            this.texturePosition = texturePosition;
    }

    public void nextTexturePosition() {
        if (maxValue == -1)
            texturePosition = (getTexturePosition() + 1) % textureLocalizations.size();
        else
            texturePosition = (getTexturePosition() + 1) % Math.min(textureLocalizations.size(), maxValue);
    }

    @Override
    public Component getLocalization() {
        return textureLocalizations.get(getTexturePosition()).localization();
    }

    public void previousTexturePosition() {
        if (maxValue == -1) {
            int size = textureLocalizations.size();
            texturePosition = (getTexturePosition() - 1 + size) % size;
        } else {
            int limit = Math.min(textureLocalizations.size(), maxValue); // Determine the effective limit
            texturePosition = (getTexturePosition() - 1 + limit) % limit;
        }
    }
}
