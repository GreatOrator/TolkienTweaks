package com.greatorator.tolkienmobs.containers.widget;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TolkienButton extends Button {
  protected Component localization = Component.empty();
  private static final Identifier WIDGETS_LOCATION =
      TolkienMobsMain.resLoc("textures/gui/widgets.png");

  private TexturePosition texturePosition;

  protected TolkienButton(AbstractBuilder<?, ?> builder) {
    super(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress,
        builder.createNarration);
    this.texturePosition = builder.texturePosition;
  }

  protected TolkienButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration) {
    super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
  }

  public Component getLocalization() {
    return localization;
  }

  public Component getLocalization(int mouseX, int mouseY) {
    if (GeneralUtility.inBounds(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY))
      return getLocalization();
    return Component.empty();
  }

  public void setTexturePosition(TexturePosition texturePosition) {
    this.texturePosition = texturePosition;
  }

  protected int getYImage(boolean hovered) {
    if (!this.active) {
      return 0;
    }
    return hovered ? 2 : 1;
  }

  /**
   * 26.1 draws a widget in an extract pass: `renderWidget` is `extractContents`, which takes the
   * {@link GuiGraphicsExtractor} everything is drawn through, and the tooltip and cursor are handled
   * by the base class around it.
   */
  @Override
  protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    var font = Minecraft.getInstance().font;
    int i = this.getYImage(this.isHoveredOrFocused());

    int xOffset = this.texturePosition.x();
    int yOffset = this.texturePosition.y();
    int h = this.texturePosition.height();
    int w = this.texturePosition.width();

    graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS_LOCATION, this.getX(), this.getY(), xOffset, yOffset + i * h, this.width / 2, h, 256, 256);
    graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS_LOCATION, this.getX() + this.width / 2, this.getY(),
        xOffset + w - this.width / 2,
        yOffset + i * h, this.width / 2, h, 256, 256);
    int j = getFGColor();
    graphics.centeredText(font, this.getMessage(), this.getX() + this.width / 2,
        this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
  }

  public static Builder builder(Component message, OnPress onPress,
      TexturePosition texturePosition) {
    return new Builder(message, onPress, texturePosition);
  }

  public static Builder builder(String translationKey, OnPress onPress,
      TexturePosition texturePosition) {
    return new Builder(Component.translatable(translationKey), onPress, texturePosition);
  }

  public static class Builder extends AbstractBuilder<Builder, TolkienButton> {

    public Builder(Component message, OnPress onPress, TexturePosition texturePosition) {
      super(TolkienButton::new, message, onPress, texturePosition);
    }
  }

  protected static abstract class AbstractBuilder<SELF extends AbstractBuilder<SELF, T>,
      T extends TolkienButton> {

    private final Function<SELF, T> factory;

    private final Component message;
    private final OnPress onPress;
    private final TexturePosition texturePosition;

    @Nullable
    private Tooltip tooltip;
    private int x;
    private int y;
    private int width = 150;
    private int height = 20;
    private CreateNarration createNarration = DEFAULT_NARRATION;

    public AbstractBuilder(Function<SELF, T> factory, Component message, OnPress onPress,
        TexturePosition texturePosition) {
      this.factory = factory;
      this.message = message;
      this.onPress = onPress;
      this.texturePosition = texturePosition;
    }

    public SELF pos(int x, int y) {
      this.x = x;
      this.y = y;
      return this.self();
    }

    public SELF width(int width) {
      this.width = width;
      return this.self();
    }

    public SELF size(int width, int height) {
      this.width = width;
      this.height = height;
      return this.self();
    }

    public SELF bounds(int x, int y, int width, int height) {
      return this.pos(x, y).size(width, height);
    }

    public SELF tooltip(@Nullable Tooltip tooltip) {
      this.tooltip = tooltip;
      return this.self();
    }

    public SELF createNarration(CreateNarration createNarration) {
      this.createNarration = createNarration;
      return this.self();
    }

    public T build() {
      var button = this.factory.apply(this.self());
      button.setTooltip(this.tooltip);
      return button;
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
      return (SELF) this;
    }
  }
}
