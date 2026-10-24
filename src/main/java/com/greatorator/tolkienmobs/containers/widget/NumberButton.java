package com.greatorator.tolkienmobs.containers.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class NumberButton extends TolkienButton {
    private int value;
    public int min;
    public int max;

    public NumberButton(int x, int y, int width, int height, int value, int min, int max, Component localization, OnPress onPress) {
        super(x, y, width, height, net.minecraft.network.chat.Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.value = value;
        this.localization = localization;
        this.min = min;
        this.max = max;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF353535);
        graphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, 0xFFD8D8D8);
        Font font = Minecraft.getInstance().font;
        // The pose is a 2D matrix now, so scaling is pushMatrix/scale/popMatrix rather than a
        // PoseStack, and text is placed on integer pixels.
        Matrix3x2fStack stack = graphics.pose();
        stack.pushMatrix();
        float scale = 0.75f;//value > 99 || value < -99 ? 0.75f : 0.75f;
        stack.scale(scale, scale);
        String msg = String.format("%,d", value);
        float x = (this.getX() + this.width / 2f) / scale - font.width(msg) / 2f;
        float y = (this.getY() + (this.height - font.lineHeight) / 2f / scale) / scale + 1;
        graphics.text(font, msg, Math.round(x), Math.round(y), Color.DARK_GRAY.getRGB(), false);
        stack.popMatrix();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
