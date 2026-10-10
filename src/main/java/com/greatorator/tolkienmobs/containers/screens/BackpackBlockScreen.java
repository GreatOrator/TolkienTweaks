package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.containers.handlers.FluidTankRenderer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.network.manager.BackpackPlacementUpdateManager;
import com.greatorator.tolkienmobs.network.manager.BackpackSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.MouseUtil;
import com.greatorator.tolkienmobs.util.block.BackpackFluidUpgrades;
import com.greatorator.tolkienmobs.util.block.BackpackPlacement;
import com.greatorator.tolkienmobs.util.block.BackpackSettings;
import com.greatorator.tolkienmobs.util.block.BackpackUpgrades;
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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BackpackBlockScreen extends AbstractContainerScreen<BackpackBlockContainer> {
    private static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack.png");
    private static final ResourceLocation GUI_STORAGE_UPGRADE_1 = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_1.png");
    private static final ResourceLocation GUI_STORAGE_UPGRADE_2 = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_2.png");
    private static final ResourceLocation GUI_CRAFTING_UPGRADE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_crafting.png");
    private static final ResourceLocation GUI_CRAFTING_STORAGE_UPGRADE_1 = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_crafting_upgrade_1.png");
    private static final ResourceLocation GUI_CRAFTING_STORAGE_UPGRADE_2 = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_crafting_upgrade_2.png");
    private static final ResourceLocation GUI_TANK_UPGRADE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_fluid.png");
    private static final ResourceLocation GUI_TANK_STORAGE_UPGRADE_1 = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_fluid_upgrade_1.png");
    private static final ResourceLocation GUI_TANK_STORAGE_UPGRADE_2 = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_fluid_upgrade_2.png");
    private static final ResourceLocation GUI_FULL_UPGRADE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_full_basic.png");
    private static final ResourceLocation GUI_FULL_UPGRADE_1 = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_full_upgrade_1.png");
    private static final ResourceLocation GUI_FULL_UPGRADE_2 = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_full_upgrade_2.png");
    protected final ResourceLocation FLUIDBAR = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/tank_overlay.png");
    protected BackpackBlockContainer container;
    protected BackpackSettings backpackSettings;
    protected BackpackPlacement backpackPlacement;
    protected BackpackUpgrades backpackUpgrades;
    protected BackpackFluidUpgrades backpackFluidUpgrades;
    private BackpackUpgrades shownUpgrades;
    private BackpackFluidUpgrades shownFluidUpgrades;
    private final BackpackBlockEntity tileEntity;
    private Level level;
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
        this.backpackPlacement = tileEntity.getBackpackPlacement();
        this.backpackUpgrades = tileEntity.getBackpackUpgrades();
        this.backpackFluidUpgrades = tileEntity.getBackpackFluidUpgrades();
    }

    @Override
    public void init() {
        super.init();

        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;

        rememberShownUpgrades();
        assignFluidRenderer();
        addUpgradeButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (!upgradesMatchShown()) {
            rebuildWidgets();
        }
    }

    private void rememberShownUpgrades() {
        shownUpgrades = new BackpackUpgrades(backpackUpgrades.crafting, backpackUpgrades.sleepingBag, backpackUpgrades.campfire, backpackUpgrades.size_upgrade, backpackUpgrades.size_upgrade_2);
        shownFluidUpgrades = new BackpackFluidUpgrades(backpackFluidUpgrades.fluid_tank, backpackFluidUpgrades.fluid_tank_2, backpackFluidUpgrades.fluid_tank_3, backpackFluidUpgrades.fluid_tank_4, backpackFluidUpgrades.fluid_tank_5);
    }

    private boolean upgradesMatchShown() {
        return backpackUpgrades.equals(shownUpgrades) && backpackFluidUpgrades.equals(shownFluidUpgrades);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        if (backpackSettings.upgrade)
            return mouseX < (double) guiLeftIn - 100 || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.imageWidth) || mouseY >= (double) (guiTopIn + this.imageHeight);
        return super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn, mouseButton);
    }

    private void assignFluidRenderer() {
        fluidRenderer = new FluidTankRenderer(tileEntity.getMaxMB(), true, 11, 75);
    }

    private void renderFluidTooltipArea(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y,
                                        FluidStack stack, int offsetX, int offsetY, FluidTankRenderer renderer) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, offsetX, offsetY, renderer)) {
            if (!backpackFluidUpgrades.fluid_tank_5) {
                guiGraphics.renderTooltip(this.font, renderer.getTooltip(stack, TooltipFlag.Default.NORMAL),
                        Optional.empty(), pMouseX - x, pMouseY - y);
            } else {
                guiGraphics.renderTooltip(this.font, renderer.getTooltip(stack, TooltipFlag.Default.ADVANCED),
                        Optional.empty(), pMouseX - x, pMouseY - y);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
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
        if (backpackSettings.upgrade) {
            guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.backpack.backpack_upgrade"), 236, 41, 8552833, false);
        }
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 64, 163, 8552833, false);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(this.tileEntity.hasFluidTank()) {
            renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.tileEntity.getFluid(), 0, 8, fluidRenderer);
        }
    }

    @Override
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        super.renderSlotContents(guiGraphics, itemstack, slot, countString);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(getGUIState(), relX - 23, relY, 0, 0, this.imageWidth, this.imageHeight);

        if (renderablesChanged)
            updateRenderables();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(this.tileEntity.hasFluidTank()) {
            guiGraphics.blit(FLUIDBAR, relX, relY + 5, 0, 0, 11, 75, 12, 75);
            fluidRenderer.render(guiGraphics, x, y + 4, menu.tileEntity.getFluid());
        }

        if (backpackSettings.upgrade) {
            ResourceLocation upgradeGUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_upgrade.png");
            RenderSystem.setShaderTexture(0, upgradeGUI);
            guiGraphics.blit(upgradeGUI, getGuiLeft() + 220, getGuiTop() + 36, 0, 0, this.imageWidth, this.imageHeight);
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

    public void addUpgradeButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        if (this.tileEntity.backpackUpgrades.sleepingBag) {
            addRenderableWidget(ToggleButtonFactory.SLEEPING_BAG_TOGGLE_BUTTON(offsetX + 173, offsetY + 156, backpackSettings.sleepingBag, b -> {
                backpackSettings.sleepingBag = !backpackSettings.sleepingBag;
                backpackPlacement.sleepingBag = backpackSettings.sleepingBag;
                PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));
                PacketDistributor.sendToServer(new BackpackPlacementUpdateManager(backpackPlacement.sleepingBag, !backpackPlacement.campfire));
            }));
        }

        if (this.tileEntity.backpackUpgrades.campfire) {
            addRenderableWidget(ToggleButtonFactory.CAMPFIRE_TOGGLE_BUTTON(offsetX + 191, offsetY + 156, backpackSettings.campfire, b -> {
                backpackSettings.campfire = !backpackSettings.campfire;
                backpackPlacement.campfire = backpackSettings.campfire;
                PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));
                PacketDistributor.sendToServer(new BackpackPlacementUpdateManager(!backpackPlacement.sleepingBag, backpackPlacement.campfire));
            }));
        }

        addRenderableWidget(ToggleButtonFactory.UPGRADE_TOGGLE_BUTTON(offsetX + 209, offsetY + 156, backpackSettings.upgrade, b -> {
            backpackSettings.upgrade = !backpackSettings.upgrade;
            PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));
        }));

    }

    private ResourceLocation getGUIState() {
        boolean crafting = tileEntity.getBackpackUpgrades().crafting;
        boolean fluid = tileEntity.hasFluidTank();
        return switch (tileEntity.getStorageSize()) {
            case 72 -> crafting && fluid ? GUI_FULL_UPGRADE_2 : crafting ? GUI_CRAFTING_STORAGE_UPGRADE_2 : fluid ? GUI_TANK_STORAGE_UPGRADE_2 : GUI_STORAGE_UPGRADE_2;
            case 54 -> crafting && fluid ? GUI_FULL_UPGRADE_1 : crafting ? GUI_CRAFTING_STORAGE_UPGRADE_1 : fluid ? GUI_TANK_STORAGE_UPGRADE_1 : GUI_STORAGE_UPGRADE_1;
            default -> crafting && fluid ? GUI_FULL_UPGRADE : crafting ? GUI_CRAFTING_UPGRADE : fluid ? GUI_TANK_UPGRADE : GUI_BASE;
        };
    }

    public void updateRenderables() {
        if (!widgetsToRemove.isEmpty()) {
            for (AbstractWidget abstractWidget : widgetsToRemove) {
                removeWidget(abstractWidget);
            }
            widgetsToRemove.clear();
        }
        PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));

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
