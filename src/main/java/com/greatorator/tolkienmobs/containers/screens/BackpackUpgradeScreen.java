package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.containers.BackpackUpgradeContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BackpackUpgradeScreen extends AbstractContainerScreen<BackpackUpgradeContainer> {

    public BackpackUpgradeScreen(BackpackUpgradeContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }
//    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/inventory_backpack_upgrade.png");
//    private static final Component NAME = Component.translatable("screen.tolkienmobs.backpack.backpack_upgrade");
//    protected final T entity;
//    protected final int xSize = 176;
//    protected final int ySize = 133;
//    @Nullable
//    private final Screen upgradeScreen;
//    protected int guiLeft;
//    protected int guiTop;
//
//    public BackpackUpgradeScreen(T entity, @Nullable Screen upgradeScreen) {
//        super(NAME);
//        this.entity = entity;
//        this.upgradeScreen = upgradeScreen;
//    }
}
