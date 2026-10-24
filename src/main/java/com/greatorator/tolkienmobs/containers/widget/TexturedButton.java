package com.greatorator.tolkienmobs.containers.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TexturedButton extends Button {
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;
    private final Identifier texture;

    public TexturedButton(
            int pX,
            int pY,
            int pWidth,
            int pHeight,
            Identifier texture,
            int yDiffTex,
            int textureWidth,
            int textureHeight,
            OnPress pOnPress
    ) {
        super(pX, pY, pWidth, pHeight, Component.empty(), pOnPress, DEFAULT_NARRATION);

        this.yDiffTex = yDiffTex;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.texture = texture;
    }

    /**
     * The hovered variant sits {@code yDiffTex} pixels below the idle one in the sheet. The base
     * class sets {@link #isHovered} before calling this, so the offset follows the mouse again: the
     * old override of {@code render} skipped that bookkeeping, so this button never showed it.
     */
    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int pVOffset = 0;
        if (this.isHovered) {
            pVOffset = yDiffTex;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), 0, pVOffset, width, height, textureWidth, textureHeight);
    }
}
