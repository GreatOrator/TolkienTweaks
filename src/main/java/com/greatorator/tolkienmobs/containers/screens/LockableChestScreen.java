package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import com.greatorator.tolkienmobs.containers.LockableChestContainer;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import com.greatorator.tolkienmobs.network.KeyCodeUpdateManager;
import com.greatorator.tolkienmobs.network.LockedChestUpdateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableChestScreen extends AbstractContainerScreen<LockableChestContainer> {
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chests/chest_text.png");
    protected final LockableChestContainer container;
    public static ItemStack keyStack;
    private final InteractionHand hand;
    private EditBox nameField;
    private String keyCode;
    private int keyUses;

    public LockableChestScreen(LockableChestContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        keyStack = inv.player.getItemInHand(InteractionHand.MAIN_HAND);
        this.hand = inv.player.swingingArm;
        this.container = container;
        this.imageHeight = 62;
        this.keyCode = container.keyCode;
        this.keyUses = KeyItem.getUses(keyStack);

    }

    @Override
    public void init() {
        super.init();
        this.nameField = new EditBox(this.font, (this.leftPos) + 16, topPos + 17, imageWidth - 31, this.font.lineHeight + 7, Component.translatable("screen.tolkienmobs.namefieldtext"));
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

        this.nameField.setValue(this.keyCode);
        this.nameField.setMaxLength(50);
        this.nameField.setVisible(true);
        addRenderableWidget(nameField);
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
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        Component tooltip = Component.literal("");
        tooltip = Component.translatable("screen.tolkienmobs.namefieldtext");
        this.nameField.setTooltip(Tooltip.create(tooltip));
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
        BlockPos pos = container.tileEntity.getBlockPos();

        PacketDistributor.sendToServer(new LockedChestUpdateManager(pos, this.keyCode));
    }
}