package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
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

public class CamoKeyStoneScreen extends AbstractContainerScreen<CamoKeyStoneContainer> {
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/keystone/keystone_gui.png");
    protected final CamoKeyStoneContainer container;
    protected final CamoKeyStoneBlockEntity tileEntity;
    private EditBox nameField;
    private String keyCode;
    protected KeyStoneSettings keyStoneSettings;
    protected KeyStoneCode keyStoneCode;
    protected GeneralUtility.RedstoneMode redstoneMode;
    public int tickDelay;
    protected boolean renderablesChanged = false;
    protected List<AbstractWidget> widgetsToRemove = new ArrayList<>();
    protected List<AbstractWidget> widgetsToAdd = new ArrayList<>();

    public CamoKeyStoneScreen(CamoKeyStoneContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.container = menu;
        this.imageHeight = 115;
        this.keyCode = menu.keyCode;
        tileEntity = container.tileEntity;
        this.keyStoneSettings = tileEntity.getKeyStoneSettings();
        this.redstoneMode = GeneralUtility.RedstoneMode.TOGGLE;
        this.tickDelay = tileEntity.getTickDelay();
    }

    @Override
    public void init() {
        super.init();
        var saveCodeButton = this.addRenderableWidget(TolkienButton
                .builder(Component.translatable("buttons.tolkienmobs.saveinfo"), button -> {
                    PacketDistributor.sendToServer(new KeyStoneDataUpdateManager(this.nameField.getValue()));
                    this.minecraft.setScreen(null);
                }, ButtonTexture.LARGE_BUTTON)
                .size(64, 20)
                .build());
        var layout = new LinearLayout((leftPos - 20) + 72, topPos + this.font.lineHeight + 67,
                LinearLayout.Orientation.HORIZONTAL);
        layout.spacing(4);
        layout.addChild(saveCodeButton);
        layout.arrangeElements();

        addNameField();
        addKeyStoneButtons();
        addTickDelayButton();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.nameField.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
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
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 5, 5, 8552833);

        if (!nameField.isFocused() && nameField.getValue().isEmpty())
            guiGraphics.drawString(font, keyCode, nameField.getX() - leftPos + 4, (nameField.getY() + 2) - topPos, -10197916);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        this.nameField.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (renderablesChanged)
            updateRenderables();
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
        if(this.nameField.isFocused()&&key!= GLFW.GLFW_KEY_ESCAPE)
            if(this.nameField.keyPressed(key, scancode, p_keyPressed_3_)||this.nameField.canConsumeInput())
                return true;
        return super.keyPressed(key, scancode, p_keyPressed_3_);
    }

    public void addNameField() {
        addRenderableWidget(this.nameField = new EditBox(this.font, (this.leftPos) + 16, topPos + 17, imageWidth - 31, this.font.lineHeight + 7, Component.literal(tileEntity.getKeyCode())));

        this.nameField.setValue(this.keyCode);
        this.nameField.setMaxLength(50);
        this.nameField.setVisible(true);
    }

    public void addKeyStoneButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.KEEPKEYBUTTON(offsetX + 67, offsetY + 36, keyStoneSettings.keepKey, b -> {
            keyStoneSettings.keepKey = !keyStoneSettings.keepKey;
            PacketDistributor.sendToServer(new KeyStoneSettingsUpdateManager(keyStoneSettings.keepKey));
        }));

        addRenderableWidget(ToggleButtonFactory.REDSTONE_TOGGLE_BUTTON(offsetX + 85, offsetY + 36, tileEntity.getRedstoneControlData().redstoneMode.ordinal(), b -> {
            redstoneMode = GeneralUtility.RedstoneMode.values()[((ToggleButton) b).getTexturePosition()];

            PacketDistributor.sendToServer(new KeyStoneRedstoneUpdateManager(redstoneMode.ordinal()));
        }));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }

    public void addTickDelayButton() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.TICKDELAYBUTTON(offsetX + 71, offsetY + 58, tickDelay, b -> {
            tickDelay = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new KeyStoneDelayUpdateManager(tickDelay));
        }));
    }

    public void updateRenderables() {
        if (!widgetsToRemove.isEmpty()) {
            for (AbstractWidget abstractWidget : widgetsToRemove) {
                removeWidget(abstractWidget);
            }
            widgetsToRemove.clear();
        }
        if (!widgetsToAdd.isEmpty()) {
            for (AbstractWidget abstractWidget : widgetsToAdd) {
                addRenderableWidget(abstractWidget);
            }
            widgetsToAdd.clear();
        }
        renderablesChanged = false;
    }
}
