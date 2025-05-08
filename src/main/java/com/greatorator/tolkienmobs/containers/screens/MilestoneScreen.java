package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.containers.MilestoneContainer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.NumberButton;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.network.manager.MilestoneSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.ColorUtility;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.block.custom.MilestoneBlock.LIT;

public class MilestoneScreen extends AbstractContainerScreen<MilestoneContainer> {
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/milestone/milestone_gui.png");
    protected final MilestoneContainer container;
    protected final MilestoneBlockEntity tileEntity;
    protected MilestoneSettings milestoneSettings;
    private EditBox milestoneName;
    protected List<AbstractWidget> widgetsToRemove = new ArrayList<>();
    protected List<AbstractWidget> widgetsToAdd = new ArrayList<>();
    protected boolean renderablesChanged = false;

    public MilestoneScreen(MilestoneContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.container = container;
        this.tileEntity = container.tileEntity;
        this.milestoneSettings = tileEntity.getMilestoneSettings();
        this.imageHeight = 196;

    }

    @Override
    public void init() {
        super.init();
        var saveCodeButton = this.addRenderableWidget(TolkienButton
                .builder(Component.translatable("buttons.tolkienmobs.milestone.saveinfo"), button -> {
                    sendMessageToServer();
                    this.minecraft.setScreen(null);
                }, ButtonTexture.LARGE_BUTTON)
                .size(30, 16)
                .build());
        var layout = new LinearLayout((leftPos - 20) + 151, topPos + 45,
                LinearLayout.Orientation.HORIZONTAL);
        layout.spacing(4);
        layout.addChild(saveCodeButton);
        layout.arrangeElements();

        tileEntity.updateClientState();
        addNameField();
        addMilestoneDistanceButton();
        addMilestoneMultiplierButton();
        addPaymentMethod();
    }

    public void addMilestoneDistanceButton() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.DISTANCE_BUTTON(offsetX + 16, offsetY + 48, milestoneSettings.distance, b -> {
            milestoneSettings.distance = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new MilestoneSettingsUpdateManager(milestoneSettings.distance, milestoneSettings.dimension, milestoneSettings.name, milestoneSettings.uuid));
        }));
    }

    private void addMilestoneMultiplierButton() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.DIMENSION_BUTTON(offsetX + 45, offsetY + 48, milestoneSettings.dimension, b -> {
            milestoneSettings.dimension = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new MilestoneSettingsUpdateManager(milestoneSettings.distance, milestoneSettings.dimension, milestoneSettings.name, milestoneSettings.uuid));
        }));
    }

    public void addPaymentMethod() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;

        if (TolkienMobsConfig.PAYMENT_TYPE.get()) {
            addRenderableWidget(ToggleButtonFactory.ITEM_PAYMENT_METHOD(offsetX + 74, offsetY + 48, 0, b -> {
            }));
        } else {
            addRenderableWidget(ToggleButtonFactory.EXPERIENCE_PAYMENT_METHOD(offsetX + 74, offsetY + 48, 0, b -> {
            }));
        }
    }

    public void addNameField() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(this.milestoneName = new EditBox(this.font, offsetX + 16, offsetY + 16, imageWidth - 31, this.font.lineHeight + 7, Component.literal(tileEntity.getMilestoneSettings().name)));

        this.milestoneName.setValue(tileEntity.getMilestoneSettings().name);
        this.milestoneName.setMaxLength(50);
        this.milestoneName.setVisible(true);
    }

    public void addLocations() {
        for (MilestoneHandler.MilestoneData data : MilestoneHandler.getKnownByPlayer(minecraft.player)) {
            if (data.getUuid().equals(tileEntity.getUUID())) continue;
//            GuiElement<?> container = new GuiElement<>()
//                    .setYSize(18);
//            GuiButton button = toolkit.createButton_old(data.getName(), container)
//                    .setYSize(container.ySize())
//                    .setXSize(listElement.getInsetRect().width - (data.getPaymentItem() == Items.AIR ? 0 : 18))
//                    .onPressed(() -> tile.sendPacketToServer(output -> output.writeUUID(data.getUuid()), 3));
//
//            if (data.getPaymentItem() == Items.AIR) {
//                listElement.addElement(container);
//                continue;
//            }
//
//            container.addChild(new GuiStackIcon())
//                    .setStack(tile.getTravelCost(data))
//                    .addSlotBackground()
//                    .setYSize(container.ySize())
//                    .setXSize(18)
//                    .setXPos(button.maxXPos());
//
//            listElement.addElement(container);
        }

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
        tooltip = Component.translatable("screen.tolkienmobs.milestone.instructions");
        this.milestoneName.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.milestoneName.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(font, title, 83, 5, 8552833);
        if (container.tileEntity.getBlockState().getValue(LIT)) {
            guiGraphics.drawCenteredString(font, " ", 83, 14, ColorUtility.DARKSLATEBLUE.getColor());
        }
        if (!milestoneName.isFocused() && milestoneName.getValue().isEmpty())
            guiGraphics.drawString(font, tileEntity.getMilestoneSettings().name, milestoneName.getX() - leftPos + 4, (milestoneName.getY() + 2) - topPos, -10197916);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
        if (TolkienMobsConfig.PAYMENT_TYPE.get()) {
            guiGraphics.renderFakeItem(BuiltInRegistries.ITEM.get(ResourceLocation.parse(TolkienMobsConfig.PAYMENT_ITEM.get())).getDefaultInstance(), relX + 74, relY + 48);
        } else {
            guiGraphics.renderFakeItem(BuiltInRegistries.ITEM.get(ResourceLocation.parse("tolkienmobs:experience_orb")).getDefaultInstance(), relX + 74, relY + 48);
        }
        this.milestoneName.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (renderablesChanged)
            updateRenderables();
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        boolean ret = super.mouseClicked(x, y, btn);
        if(this.milestoneName.mouseClicked(x, y, btn)) {
            this.milestoneName.setFocused(true);
            ret = true;
        }
        else if(this.milestoneName.isFocused()) {
            this.milestoneName.setFocused(false);
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
        if(this.milestoneName.isFocused()&&key!=GLFW.GLFW_KEY_ESCAPE)
            if(this.milestoneName.keyPressed(key, scancode, p_keyPressed_3_)||this.milestoneName.canConsumeInput())
                return true;
        return super.keyPressed(key, scancode, p_keyPressed_3_);
    }

    private void sendMessageToServer() {
        milestoneSettings.name = this.milestoneName.getValue();

        PacketDistributor.sendToServer(new MilestoneSettingsUpdateManager(milestoneSettings.distance, milestoneSettings.dimension, milestoneSettings.name, milestoneSettings.uuid));
    }
}