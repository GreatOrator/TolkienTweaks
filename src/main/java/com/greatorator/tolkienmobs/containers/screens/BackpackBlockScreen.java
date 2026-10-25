package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.containers.handlers.FluidTankRenderer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.network.manager.BackpackMagnetToggleManager;
import com.greatorator.tolkienmobs.network.manager.BackpackPlacementUpdateManager;
import com.greatorator.tolkienmobs.network.manager.BackpackSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.MouseUtil;
import com.greatorator.tolkienmobs.util.block.BackpackFluidUpgrades;
import com.greatorator.tolkienmobs.util.block.BackpackPlacement;
import com.greatorator.tolkienmobs.util.block.BackpackSettings;
import com.greatorator.tolkienmobs.util.block.BackpackUpgrades;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BackpackBlockScreen extends AbstractContainerScreen<BackpackBlockContainer> {
    private static final Identifier GUI_BASE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack.png");
    private static final Identifier GUI_STORAGE_UPGRADE_1 = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_1.png");
    private static final Identifier GUI_STORAGE_UPGRADE_2 = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_2.png");
    private static final Identifier GUI_CRAFTING_UPGRADE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_crafting.png");
    private static final Identifier GUI_CRAFTING_STORAGE_UPGRADE_1 = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_crafting_upgrade_1.png");
    private static final Identifier GUI_CRAFTING_STORAGE_UPGRADE_2 = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_crafting_upgrade_2.png");
    private static final Identifier GUI_TANK_UPGRADE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_fluid.png");
    private static final Identifier GUI_TANK_STORAGE_UPGRADE_1 = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_fluid_upgrade_1.png");
    private static final Identifier GUI_TANK_STORAGE_UPGRADE_2 = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_fluid_upgrade_2.png");
    private static final Identifier GUI_FULL_UPGRADE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_full_basic.png");
    private static final Identifier GUI_FULL_UPGRADE_1 = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_full_upgrade_1.png");
    private static final Identifier GUI_FULL_UPGRADE_2 = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_full_upgrade_2.png");
    private static final Identifier GUI_UPGRADE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_upgrade.png");
    protected final Identifier FLUIDBAR = Identifier.fromNamespaceAndPath(MODID, "textures/gui/backpack/tank_overlay.png");
    protected BackpackBlockContainer container;
    protected BackpackSettings backpackSettings;
    protected BackpackPlacement backpackPlacement;
    protected BackpackUpgrades backpackUpgrades;
    protected BackpackFluidUpgrades backpackFluidUpgrades;
    private BackpackUpgrades shownUpgrades;
    private BackpackFluidUpgrades shownFluidUpgrades;
    private boolean shownSleepingBag;
    private boolean shownCampfire;
    private boolean shownMagnet;
    private final BackpackBlockEntity tileEntity;
    private Level level;
    private FluidTankRenderer fluidRenderer;
    protected boolean renderablesChanged = false;
    protected List<AbstractWidget> widgetsToRemove = new ArrayList<>();
    protected List<AbstractWidget> widgetsToAdd = new ArrayList<>();

    public BackpackBlockScreen(BackpackBlockContainer container, Inventory inv, Component name) {
        // 26.1 makes the two sizes final, so the 256x256 GUI goes through the constructor.
        super(container, inv, Component.literal(""), 256, 256);
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
        if (!upgradesMatchShown() || shownSleepingBag != backpackSettings.sleepingBag
                || shownCampfire != backpackSettings.campfire || shownMagnet != backpackSettings.magnet) {
            rebuildWidgets();
        }
    }

    private void rememberShownUpgrades() {
        shownSleepingBag = backpackSettings.sleepingBag;
        shownCampfire = backpackSettings.campfire;
        // The magnet key flips the setting without the screen, so the button has to follow it here.
        shownMagnet = backpackSettings.magnet;
        shownUpgrades = new BackpackUpgrades(backpackUpgrades.crafting, backpackUpgrades.sleepingBag, backpackUpgrades.campfire, backpackUpgrades.size_upgrade, backpackUpgrades.size_upgrade_2,
                backpackUpgrades.magnet_1, backpackUpgrades.magnet_2, backpackUpgrades.magnet_3);
        shownFluidUpgrades = new BackpackFluidUpgrades(backpackFluidUpgrades.fluid_tank, backpackFluidUpgrades.fluid_tank_2, backpackFluidUpgrades.fluid_tank_3, backpackFluidUpgrades.fluid_tank_4, backpackFluidUpgrades.fluid_tank_5);
    }

    private boolean upgradesMatchShown() {
        return backpackUpgrades.equals(shownUpgrades) && backpackFluidUpgrades.equals(shownFluidUpgrades);
    }

    /**
     * 26.1 dropped the mouse button from this test, because a container screen no longer asks it
     * which button was pressed - the click event carries that itself.
     */
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn) {
        if (backpackSettings.upgrade)
            return mouseX < (double) guiLeftIn - 100 || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.imageWidth) || mouseY >= (double) (guiTopIn + this.imageHeight);
        return super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn);
    }

    private void assignFluidRenderer() {
        fluidRenderer = new FluidTankRenderer(tileEntity.getMaxMB(), true, 11, 75);
    }

    /**
     * The tank's own tooltip. 26.1 defers tooltips to the frame's deferred pass, so the position is
     * the cursor itself and no longer the cursor minus the GUI origin - the old subtraction put the
     * line in the wrong place, up and left of the pointer by the GUI's own offset.
     */
    private void extractFluidTooltipArea(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, int x, int y,
                                         FluidStack stack, int offsetX, int offsetY, FluidTankRenderer renderer) {
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, offsetX, offsetY, renderer)) {
            if (!backpackFluidUpgrades.fluid_tank_5) {
                graphics.setTooltipForNextFrame(this.font, renderer.getTooltip(stack, TooltipFlag.Default.NORMAL),
                        Optional.empty(), pMouseX, pMouseY);
            } else {
                graphics.setTooltipForNextFrame(this.font, renderer.getTooltip(stack, TooltipFlag.Default.ADVANCED),
                        Optional.empty(), pMouseX, pMouseY);
            }
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int pX, int pY) {
        super.extractTooltip(graphics, pX, pY);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof TolkienButton button && !button.getLocalization(pX, pY).equals(Component.empty()))
                graphics.setTooltipForNextFrame(font, button.getLocalization(pX, pY), pX, pY);
        }

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (this.tileEntity.hasFluidTank()) {
            extractFluidTooltipArea(graphics, pX, pY, x, y, menu.tileEntity.getFluid(), 0, 8, fluidRenderer);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.block.tolkienmobs.backpack"), 64, 3, 8552833, false);
        if (backpackSettings.upgrade) {
            graphics.text(this.font, Component.translatable("screen.tolkienmobs.backpack.backpack_upgrade"), 236, 41, 8552833, false);
        }
        graphics.text(this.font, this.playerInventoryTitle, 64, 163, 8552833, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, getGUIState(), relX - 23, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        if (renderablesChanged)
            updateRenderables();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (this.tileEntity.hasFluidTank()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, FLUIDBAR, relX, relY + 5, 0, 0, 11, 75, 12, 75);
            fluidRenderer.render(graphics, x, y + 4, menu.tileEntity.getFluid());
        }

        if (backpackSettings.upgrade) {
            // 26.1 marks the GUI origin accessors for removal; the frame's own relX/relY are the same corner.
            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_UPGRADE, relX + 220, relY + 36, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean ret = super.mouseClicked(event, doubleClick);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof ToggleButton toggleButton && GeneralUtility.inBounds(toggleButton.getX(), toggleButton.getY(), toggleButton.getWidth(), toggleButton.getHeight(), event.x(), event.y())) {
                if (event.button() == 1) {
                    toggleButton.onClick(event);
                    toggleButton.playDownSound(Minecraft.getInstance().getSoundManager());
                }
            }
        }
        return ret;
    }

    public void addUpgradeButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        // The row reads sleeping bag, campfire, magnet, upgrade, and it has to end inside the panel:
        // the art stops at 233, so the four buttons sit at 155/173/191/209 and the upgrade toggle keeps
        // the corner it has always had.
        if (this.tileEntity.backpackUpgrades.sleepingBag) {
            addRenderableWidget(ToggleButtonFactory.SLEEPING_BAG_TOGGLE_BUTTON(offsetX + 155, offsetY + 156, backpackSettings.sleepingBag, b -> {
                ((ToggleButton) b).setTexturePosition(backpackSettings.sleepingBag);
                ClientPacketDistributor.sendToServer(new BackpackPlacementUpdateManager(true, false));
            }));
        }

        if (this.tileEntity.backpackUpgrades.campfire) {
            addRenderableWidget(ToggleButtonFactory.CAMPFIRE_TOGGLE_BUTTON(offsetX + 173, offsetY + 156, backpackSettings.campfire, b -> {
                ((ToggleButton) b).setTexturePosition(backpackSettings.campfire);
                ClientPacketDistributor.sendToServer(new BackpackPlacementUpdateManager(false, true));
            }));
        }

        if (this.tileEntity.backpackUpgrades.magnetRange() > 0) {
            addRenderableWidget(ToggleButtonFactory.MAGNET_TOGGLE_BUTTON(offsetX + 191, offsetY + 156, backpackSettings.magnet, b -> {
                // The face has already flipped; the server flips its own copy and sends the result back.
                backpackSettings.magnet = !backpackSettings.magnet;
                ClientPacketDistributor.sendToServer(new BackpackMagnetToggleManager());
            }));
        }

        addRenderableWidget(ToggleButtonFactory.UPGRADE_TOGGLE_BUTTON(offsetX + 209, offsetY + 156, backpackSettings.upgrade, b -> {
            backpackSettings.upgrade = !backpackSettings.upgrade;
            ClientPacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));
        }));

    }

    private Identifier getGUIState() {
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
        ClientPacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));

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
