package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoSpawnerContainer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.NumberButton;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.network.manager.SpawnerDelaysUpdateManager;
import com.greatorator.tolkienmobs.network.manager.SpawnerRangesUpdateManager;
import com.greatorator.tolkienmobs.network.manager.SpawnerSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.block.SpawnerDelays;
import com.greatorator.tolkienmobs.util.block.SpawnerRanges;
import com.greatorator.tolkienmobs.util.block.SpawnerSettings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CamoSpawnerScreen extends AbstractContainerScreen<CamoSpawnerContainer> {
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/spawner/spawner_gui.png");
    protected final CamoSpawnerContainer container;
    protected final CamoSpawnerBlockEntity tileEntity;
    protected SpawnerSettings spawnerSettings;
    protected SpawnerDelays spawnerDelays;
    protected SpawnerRanges spawnerRanges;
    protected List<AbstractWidget> widgetsToRemove = new ArrayList<>();
    protected List<AbstractWidget> widgetsToAdd = new ArrayList<>();
    private final List<AbstractWidget> entityButtons = new ArrayList<>();
    private String selectedModifier = "None";
    protected boolean renderablesChanged = false;

    public CamoSpawnerScreen(CamoSpawnerContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 256;
        this.imageWidth = 256;
        this.container = menu;
        tileEntity = container.tileEntity;
        this.spawnerSettings = tileEntity.getSpawnerSettings();
        this.spawnerDelays = tileEntity.getSpawnerDelays();
        this.spawnerRanges = tileEntity.getSpawnerRanges();
    }

    @Override
    public void init() {
        super.init();
        var saveCodeButton = this.addRenderableWidget(TolkienButton
                .builder(Component.translatable("screen.tolkienmobs.camo_spawner.saveValue"), button -> {
                    PacketDistributor.sendToServer(new SpawnerDelaysUpdateManager(spawnerDelays.minSpawnDelay, spawnerDelays.maxSpawnDelay, spawnerDelays.spawnDelay));
                    PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
                    PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.spawnerParticles));
                    this.minecraft.setScreen(null);
                }, ButtonTexture.LARGE_BUTTON)
                .size(64, 20)
                .build());
        var layout = new LinearLayout((leftPos - 20) + 113, topPos + this.font.lineHeight + 219,
                LinearLayout.Orientation.HORIZONTAL);
        layout.spacing(4);
        layout.addChild(saveCodeButton);
        layout.arrangeElements();

        addSpawnerButtons();
        addNumberButtons();
        addEntitiesToList();
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
    protected void containerTick() {
        super.containerTick();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void addEntitiesToList() {
        if (this.tileEntity.entityTags != null) {
            for (int i = 0; i < this.tileEntity.entityTags.size(); i++) {
                CompoundTag compoundTag = this.tileEntity.entityTags.get(i);
                int relX = (this.width - this.imageWidth) / 2;
                int relY = (this.height - this.imageHeight) / 2;
                int y = 30 + (i * 14);
                int il = i;
                String entityKey = compoundTag.getString("entityID");
                Optional<EntityType<?>> optional = EntityType.byString(entityKey);
                this.entityButtons.add(
                        this.addRenderableWidget(Button.builder(Component.literal(optional.get().getDescription().getString()),
                                        button -> {
                                            selectedModifier = String.valueOf(Component.literal(optional.get().getDescription().getString()));
                                        })
                                .pos(relX + 93, relY + y)
                                .size(152, 16)
                                .build()
                        )
                );
            }
        }
        this.tileEntity.markDirtyClient();
        this.tileEntity.setChanged();
        TolkienMobsMain.LOGGER.warn(String.valueOf(this.tileEntity.entityTags.size()));
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
        guiGraphics.drawString(font, title, 5, 5, 8552833);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.minSpawnDelay"), 5, 20, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.maxSpawnDelay"), 5, 50, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.activationRange"), 5, 80, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.spawnRange"), 5, 110, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.spawnCount"), 5, 140, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.maxCluster"), 5, 170, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.clusterRange"), 5, 200, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.entityTags"), 93, 20, 8552833);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        if (renderablesChanged)
            updateRenderables();
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

    /** Buttons/fields */
    public void addNumberButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;

            // Delays
        addRenderableWidget(ToggleButtonFactory.SPAWNER_MIN_DELAY(offsetX + 5, offsetY + 32, spawnerDelays.minSpawnDelay, b -> {
            TolkienMobsMain.LOGGER.warn(String.valueOf(((NumberButton) b).getValue()));
            spawnerDelays.minSpawnDelay = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerDelaysUpdateManager(spawnerDelays.minSpawnDelay, spawnerDelays.maxSpawnDelay, spawnerDelays.spawnDelay));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_MAX_DELAY(offsetX + 5, offsetY + 62, spawnerDelays.maxSpawnDelay, b -> {
            TolkienMobsMain.LOGGER.warn(String.valueOf(((NumberButton) b).getValue()));
            spawnerDelays.maxSpawnDelay = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerDelaysUpdateManager(spawnerDelays.minSpawnDelay, spawnerDelays.maxSpawnDelay, spawnerDelays.spawnDelay));
        }));

            // Ranges
        addRenderableWidget(ToggleButtonFactory.SPAWNER_ACTIVATION_RANGE(offsetX + 5, offsetY + 92, spawnerRanges.activationRange, b -> {
            TolkienMobsMain.LOGGER.warn(String.valueOf(((NumberButton) b).getValue()));
            spawnerRanges.activationRange = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_SPAWN_RANGE(offsetX + 5, offsetY + 122, spawnerRanges.spawnRange, b -> {
            TolkienMobsMain.LOGGER.warn(String.valueOf(((NumberButton) b).getValue()));
            spawnerRanges.spawnRange = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_SPAWN_COUNT(offsetX + 5, offsetY + 152, spawnerRanges.spawnCount, b -> {
            TolkienMobsMain.LOGGER.warn(String.valueOf(((NumberButton) b).getValue()));
            spawnerRanges.spawnCount = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_MAX_CLUSTER(offsetX + 5, offsetY + 182, spawnerRanges.maxCluster, b -> {
            TolkienMobsMain.LOGGER.warn(String.valueOf(((NumberButton) b).getValue()));
            spawnerRanges.maxCluster = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_CLUSTER_RANGE(offsetX + 5, offsetY + 212, spawnerRanges.clusterRange, b -> {
            TolkienMobsMain.LOGGER.warn(String.valueOf(((NumberButton) b).getValue()));
            spawnerRanges.clusterRange = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
    }

    private void addSpawnerButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.IGNORE_PLAYER_BUTTON(offsetX + 185, offsetY + 230, spawnerSettings.requirePlayer, b -> {
            spawnerSettings.requirePlayer = !spawnerSettings.requirePlayer;
            TolkienMobsMain.LOGGER.warn(String.valueOf(spawnerSettings.requirePlayer));
            PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.spawnerParticles));
        }));
        addRenderableWidget(ToggleButtonFactory.IGNORE_REQUIREMENTS_BUTTON(offsetX + 206, offsetY + 230, spawnerSettings.ignoreSpawnReq, b -> {
            spawnerSettings.ignoreSpawnReq = !spawnerSettings.ignoreSpawnReq;
            TolkienMobsMain.LOGGER.warn(String.valueOf(spawnerSettings.ignoreSpawnReq));
            PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.spawnerParticles));
        }));
        addRenderableWidget(ToggleButtonFactory.IGNORE_PARTICLES_BUTTON(offsetX + 227, offsetY + 230, spawnerSettings.spawnerParticles, b -> {
            spawnerSettings.spawnerParticles = !spawnerSettings.spawnerParticles;
            TolkienMobsMain.LOGGER.warn(String.valueOf(spawnerSettings.spawnerParticles));
            PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.spawnerParticles));
        }));
        addRenderableWidget(ToggleButtonFactory.REMOVE_MOB_BUTTON(offsetX + 164, offsetY + 230, true, b -> {
            this.tileEntity.entityTags.clear();
            this.tileEntity.markDirtyClient();
            this.tileEntity.setChanged();
        }));
    }
}