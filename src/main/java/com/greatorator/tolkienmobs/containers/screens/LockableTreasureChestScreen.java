package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.LockableTreasureChestContainer;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import com.greatorator.tolkienmobs.network.manager.LockedTreasureChestUpdateManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class LockableTreasureChestScreen extends AbstractContainerScreen<LockableTreasureChestContainer> {
    private final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/chests/chest_text.png");
    protected final LockableTreasureChestContainer container;
    public static ItemStack keyStack;
    private final InteractionHand hand;
    private EditBox nameField;
    private String keyCode;
    private int keyUses;

    public LockableTreasureChestScreen(LockableTreasureChestContainer container, Inventory inv, Component title) {
        // 26.1 makes the two sizes final, so the fixed 62-high GUI goes through the constructor.
        super(container, inv, title, 176, 62);
        keyStack = inv.player.getItemInHand(InteractionHand.MAIN_HAND);
        this.hand = inv.player.swingingArm;
        this.container = container;
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
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 5, 5, 8552833);

        if (!nameField.isFocused() && nameField.getValue().isEmpty())
            graphics.text(font, KeyItem.getKeyCode(this.keyStack), nameField.getX() - leftPos + 4, (nameField.getY() + 2) - topPos, -10197916);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // The name field is a renderable widget, so it is drawn with the rest of them; it used to be
        // rendered here as well, which drew it over itself.
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int pX, int pY) {
        super.extractTooltip(graphics, pX, pY);
        Component tooltip = Component.literal("");
        tooltip = Component.translatable("screen.tolkienmobs.namefieldtext");
        this.nameField.setTooltip(Tooltip.create(tooltip));
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
        return ret;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.nameField.isFocused() && !event.isEscape())
            if (this.nameField.keyPressed(event) || this.nameField.canConsumeInput())
                return true;
        return super.keyPressed(event);
    }

    private void sendMessageToServer() {
        this.keyCode = this.nameField.getValue();
        BlockPos pos = container.tileEntity.getBlockPos();

        ClientPacketDistributor.sendToServer(new LockedTreasureChestUpdateManager(pos, this.keyCode));
    }
}
