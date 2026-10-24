package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.NumberButton;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.network.manager.KeyStoneDataUpdateManager;
import com.greatorator.tolkienmobs.network.manager.KeyStoneDelayUpdateManager;
import com.greatorator.tolkienmobs.network.manager.KeyStoneRedstoneUpdateManager;
import com.greatorator.tolkienmobs.network.manager.KeyStoneSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.block.KeyStoneCode;
import com.greatorator.tolkienmobs.util.block.KeyStoneSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CamoKeyStoneScreen extends AbstractContainerScreen<CamoKeyStoneContainer> {
    private final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/keystone/keystone_gui.png");
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
        // 26.1 makes the two sizes final, so the fixed height goes through the constructor rather
        // than being assigned after the fact.
        super(menu, playerInventory, title, 176, 115);
        this.container = menu;
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
                    ClientPacketDistributor.sendToServer(new KeyStoneDataUpdateManager(this.nameField.getValue()));
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
    protected void extractTooltip(GuiGraphicsExtractor graphics, int pX, int pY) {
        super.extractTooltip(graphics, pX, pY);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof TolkienButton button && !button.getLocalization(pX, pY).equals(Component.empty()))
                graphics.setTooltipForNextFrame(font, button.getLocalization(pX, pY), pX, pY);
        }
        Component tooltip = Component.literal("");
        tooltip = Component.translatable("screen.tolkienmobs.namefieldtext");
        this.nameField.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 5, 5, 8552833);

        if (!nameField.isFocused() && nameField.getValue().isEmpty())
            graphics.text(font, keyCode, nameField.getX() - leftPos + 4, (nameField.getY() + 2) - topPos, -10197916);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // The name field is a widget, so it is drawn with the rest of them; it used to be rendered
        // here as well, which drew it over itself.
        if (renderablesChanged)
            updateRenderables();
    }

    /**
     * Shift and control step further through the number buttons. 26.1 keeps the modifiers on the
     * input event that caused the click, so the event is what this needs.
     */
    public int adjustNumberButton(int value, int change, int min, int max, MouseButtonEvent event) {
        if (event.hasShiftDown()) change *= 10;
        if (event.hasControlDown()) change *= 64;
        if (change < 0) {
            value = (Math.max(value + change, min));
        } else {
            value = (Math.min(value + change, max));
        }
        return value;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean ret = super.mouseClicked(event, doubleClick);
        if (this.nameField.mouseClicked(event, doubleClick)) {
            this.nameField.setFocused(true);
            ret = true;
        } else if (this.nameField.isFocused()) {
            this.nameField.setFocused(false);
            ret = true;
        }
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof NumberButton numberButton && GeneralUtility.inBounds(numberButton.getX(), numberButton.getY(), numberButton.getWidth(), numberButton.getHeight(), event.x(), event.y())) {
                if (event.button() == 0)
                    numberButton.setValue(adjustNumberButton(numberButton.getValue(), 1, numberButton.min, numberButton.max, event));
                else if (event.button() == 1)
                    numberButton.setValue(adjustNumberButton(numberButton.getValue(), -1, numberButton.min, numberButton.max, event));
                numberButton.onPress(event);
                numberButton.playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
            if (renderable instanceof ToggleButton toggleButton && GeneralUtility.inBounds(toggleButton.getX(), toggleButton.getY(), toggleButton.getWidth(), toggleButton.getHeight(), event.x(), event.y())) {
                if (event.button() == 1) {
                    toggleButton.onClick(event);
                    toggleButton.playDownSound(Minecraft.getInstance().getSoundManager());
                }
            }
        }
        return ret;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.nameField.isFocused() && !event.isEscape())
            if (this.nameField.keyPressed(event) || this.nameField.canConsumeInput())
                return true;
        return super.keyPressed(event);
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
            ClientPacketDistributor.sendToServer(new KeyStoneSettingsUpdateManager(keyStoneSettings.keepKey));
        }));

        addRenderableWidget(ToggleButtonFactory.REDSTONE_TOGGLE_BUTTON(offsetX + 85, offsetY + 36, tileEntity.getRedstoneControlData().redstoneMode.ordinal(), b -> {
            redstoneMode = GeneralUtility.RedstoneMode.values()[((ToggleButton) b).getTexturePosition()];

            ClientPacketDistributor.sendToServer(new KeyStoneRedstoneUpdateManager(redstoneMode.ordinal()));
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
            ClientPacketDistributor.sendToServer(new KeyStoneDelayUpdateManager(tickDelay));
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
