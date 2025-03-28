package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.containers.BackpackUpgradeContainer;
import com.greatorator.tolkienmobs.containers.handlers.FluidTankRenderer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.handlers.UpgradeItemHandler;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.network.BackpackSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.BackpackSettings;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.MouseUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BackpackBlockScreen extends AbstractContainerScreen<BackpackBlockContainer> {
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_full_upgrade_2.png");
    protected final ResourceLocation FLUIDBAR = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/tank_overlay.png");
    protected BackpackBlockContainer container;
    protected BackpackSettings backpackSettings;
    private boolean upgradeContainer;
    private final BackpackBlockEntity tileEntity;
    private FluidTankRenderer fluidRenderer;
    protected boolean renderablesChanged = false;
    protected List<AbstractWidget> widgetsToRemove = new ArrayList<>();
    protected List<AbstractWidget> widgetsToAdd = new ArrayList<>();

    public BackpackBlockScreen(BackpackBlockContainer container, Inventory inv, Component name) {
        super(container, inv, Component.literal(""));
        this.imageHeight = 256;
        this.imageWidth = 256;
        this.container = container;
        tileEntity = container.tileEntity;
        this.backpackSettings = tileEntity.getBackpackSettings();
        upgradeContainer = backpackSettings.upgrade;
    }

    @Override
    public void init() {
        super.init();

        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        assignFluidRenderer();
        addUpgradeButtons();
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        if (upgradeContainer)
            return mouseX < (double) guiLeftIn - 100 || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.imageWidth) || mouseY >= (double) (guiTopIn + this.imageHeight);
        return super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn, mouseButton);
    }

    private void assignFluidRenderer() {
        fluidRenderer = new FluidTankRenderer(8000, true, 11, 75);
    }

    private void renderFluidTooltipArea(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y,
                                        FluidStack stack, int offsetX, int offsetY, FluidTankRenderer renderer) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, offsetX, offsetY, renderer)) {
            guiGraphics.renderTooltip(this.font, renderer.getTooltip(stack, TooltipFlag.Default.NORMAL),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        validateUpgrade();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof TolkienButton button && !button.getLocalization(pX, pY).equals(Component.empty()))
                pGuiGraphics.renderTooltip(font, button.getLocalization(pX, pY), pX, pY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.block.tolkienmobs.backpack"), 64, 3, 8552833, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 64, 163, 8552833, false);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.tileEntity.getFluid(), 0, 8, fluidRenderer);
    }

    @Override
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        super.renderSlotContents(guiGraphics, itemstack, slot, countString);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX - 23, relY, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(FLUIDBAR, relX, relY + 5, 0, 0, 11, 75, 12, 75);

        if (renderablesChanged)
            updateRenderables();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        fluidRenderer.render(guiGraphics, x, y + 4, menu.tileEntity.getFluid());
        if (upgradeContainer) {
            ResourceLocation upgradeGUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_upgrade.png");
            RenderSystem.setShaderTexture(0, upgradeGUI);
            guiGraphics.blit(upgradeGUI, getGuiLeft() - 100, getGuiTop() + 24, 0, 0, this.imageWidth, this.imageHeight);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        boolean ret = super.mouseClicked(x, y, btn);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof ToggleButton toggleButton && GeneralUtility.inBounds(toggleButton.getX(), toggleButton.getY(), toggleButton.getWidth(), toggleButton.getHeight(), x, y)) {
                if (btn == 1) {
                    toggleButton.onClick(x, y, btn);
                    toggleButton.playDownSound(Minecraft.getInstance().getSoundManager());
                }
            }
        }
        return ret;
    }

    public boolean validateUpgrade() {
        if (backpackSettings.upgrade){
            upgradeContainer = true;
            toggleUpgradeSlots();
            return true;
        }
        upgradeContainer = false;
        toggleUpgradeSlots();
        return false;
    }

    private void toggleUpgradeSlots() {
        for (int i = 83; i < 83 + BackpackUpgradeContainer.SLOTS; i++) {
            if (i >= container.slots.size()) continue;
            Slot slot = container.getSlot(i);
            if (!(slot instanceof UpgradeItemHandler)) continue;
            TolkienMobsMain.LOGGER.warn(String.valueOf(upgradeContainer));
            ((UpgradeItemHandler) slot).setEnabled(upgradeContainer);
        }
    }

    public void addUpgradeButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.SLEEPING_BAG_TOGGLE_BUTTON(offsetX + 173, offsetY + 156, backpackSettings.sleepingBag, b -> {
            backpackSettings.sleepingBag = !backpackSettings.sleepingBag;
            PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));
            if (!backpackSettings.sleepingBag) {
                tileEntity.deploySleepingbag();
            }else {
                tileEntity.removeSleepingbag();
            }
        }));

        addRenderableWidget(ToggleButtonFactory.CAMPFIRE_TOGGLE_BUTTON(offsetX + 191, offsetY + 156, backpackSettings.campfire, b -> {
            backpackSettings.campfire = !backpackSettings.campfire;
            PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));
            if (!backpackSettings.campfire) {
                tileEntity.deployCampfire();
            }else {
                tileEntity.removeCampfire();
            }
        }));

        addRenderableWidget(ToggleButtonFactory.UPGRADE_TOGGLE_BUTTON(offsetX + 209, offsetY + 156, backpackSettings.upgrade, b -> {
            backpackSettings.upgrade = !backpackSettings.upgrade;
            PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));
            Minecraft.getInstance().setScreen(null);
        }));

    }

    public void updateRenderables() {
        if (!widgetsToRemove.isEmpty()) {
            for (AbstractWidget abstractWidget : widgetsToRemove) {
                removeWidget(abstractWidget);
            }
            widgetsToRemove.clear();
        }            PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));

        if (!widgetsToAdd.isEmpty()) {
            for (AbstractWidget abstractWidget : widgetsToAdd) {
                addRenderableWidget(abstractWidget);
            }
            widgetsToAdd.clear();
        }
        renderablesChanged = false;
    }

    public static boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, FluidTankRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }
}
