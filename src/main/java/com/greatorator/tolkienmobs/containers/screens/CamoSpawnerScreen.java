package com.greatorator.tolkienmobs.containers.screens;

import com.greatorator.tolkienmobs.block.TolkienBaseSpawner;
import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoSpawnerContainer;
import com.greatorator.tolkienmobs.containers.handlers.ToggleButtonFactory;
import com.greatorator.tolkienmobs.containers.widget.ButtonTexture;
import com.greatorator.tolkienmobs.containers.widget.NumberButton;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import com.greatorator.tolkienmobs.containers.widget.TolkienButton;
import com.greatorator.tolkienmobs.network.manager.*;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.block.MobSpawnData;
import com.greatorator.tolkienmobs.util.block.SpawnerDelays;
import com.greatorator.tolkienmobs.util.block.SpawnerRanges;
import com.greatorator.tolkienmobs.util.block.SpawnerSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CamoSpawnerScreen extends AbstractContainerScreen<CamoSpawnerContainer> {
    private final Identifier GUI = Identifier.fromNamespaceAndPath(MODID, "textures/gui/spawner/spawner_gui.png");
    protected final CamoSpawnerContainer container;
    protected final CamoSpawnerBlockEntity tileEntity;
    protected SpawnerSettings spawnerSettings;
    protected SpawnerDelays spawnerDelays;
    protected SpawnerRanges spawnerRanges;
    protected List<AbstractWidget> widgetsToRemove = new ArrayList<>();
    protected List<AbstractWidget> widgetsToAdd = new ArrayList<>();
    private final List<AbstractWidget> entityButtons = new ArrayList<>();
    private final List<AbstractWidget> numberButtons = new ArrayList<>();
    private final List<AbstractWidget> spawnerButtons = new ArrayList<>();
    private String selectedModifier = "None";
    private String selectedEntityId = null;
    protected boolean renderablesChanged = false;

    public CamoSpawnerScreen(CamoSpawnerContainer menu, Inventory playerInventory, Component title) {
        // 26.1 makes the two sizes final, so the 256x206 GUI goes through the constructor.
        super(menu, playerInventory, title, 256, 206);
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
                    ClientPacketDistributor.sendToServer(new SpawnerDelaysUpdateManager(spawnerDelays.minSpawnDelay, spawnerDelays.maxSpawnDelay, spawnerDelays.spawnDelay));
                    ClientPacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
                    ClientPacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
                    this.minecraft.setScreen(null);
                }, ButtonTexture.LARGE_BUTTON)
                .size(64, 20)
                .build());
        var layout = new LinearLayout((leftPos - 20) + 113, topPos + this.font.lineHeight + 165,
                LinearLayout.Orientation.HORIZONTAL);
        layout.spacing(4);
        layout.addChild(saveCodeButton);
        layout.arrangeElements();

        // Initialize selectedEntityId to first mob if available
        if (this.tileEntity.mobSpawnDataList != null && !this.tileEntity.mobSpawnDataList.isEmpty()) {
            selectedEntityId = this.tileEntity.mobSpawnDataList.get(0).getEntityId();
        }

        addSpawnerButtons();
        addNumberButtons();
        addEntitiesToList();
        updateUIBasedOnMobCount();
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
    protected void containerTick() {
        super.containerTick();
    }

    private void updateDisplayEntity() {
        if (selectedEntityId != null && this.tileEntity.mobSpawnDataList != null) {
            for (MobSpawnData mobData : this.tileEntity.mobSpawnDataList) {
                if (mobData.getEntityId().equals(selectedEntityId)) {
                    // Create a CompoundTag for the entity
                    CompoundTag entityTag = new CompoundTag();
                    entityTag.putString("id", selectedEntityId);
                    net.minecraft.world.level.SpawnData spawnData = new net.minecraft.world.level.SpawnData(entityTag, Optional.empty(), Optional.empty());
                    this.tileEntity.getSpawnerBase().setDisplayEntity(spawnData);
                    break;
                }
            }
        }
    }

    private void updateUIBasedOnMobCount() {
        int mobCount = this.tileEntity.mobSpawnDataList != null ? this.tileEntity.mobSpawnDataList.size() : 0;

        // If only one mob, set spawn method to random (spawnAllTogether = false)
        if (mobCount == 1) {
            if (spawnerSettings.spawnAllTogether) {
                spawnerSettings.spawnAllTogether = false;
                ClientPacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
            }
        }

        // Hide/disable global spawn count if more than one mob
        if (mobCount > 1) {
            for (AbstractWidget button : numberButtons) {
                if (button instanceof NumberButton) {
                    button.active = false;
                    button.visible = false;
                }
            }
        } else {
            for (AbstractWidget button : numberButtons) {
                if (button instanceof NumberButton) {
                    button.active = true;
                    button.visible = true;
                }
            }
        }

        // Hide/disable individual mob spawn count if only one mob
        for (AbstractWidget button : entityButtons) {
            if (button instanceof NumberButton) {
                button.active = mobCount > 1;
                button.visible = mobCount > 1;
            }
        }

        // Hide/disable spawn all together button if only one mob
        for (AbstractWidget button : spawnerButtons) {
            if (button instanceof ToggleButton) {
                button.active = mobCount > 1;
                button.visible = mobCount > 1;
            }
        }
    }

    public void addEntitiesToList() {
        // Remove existing entity buttons from the screen
        for (AbstractWidget button : this.entityButtons) {
            this.removeWidget(button);
        }
        this.entityButtons.clear();

        // Reset selectedEntityId if list is empty
        if (this.tileEntity.mobSpawnDataList == null || this.tileEntity.mobSpawnDataList.isEmpty()) {
            selectedEntityId = null;
        } else if (selectedEntityId == null) {
            selectedEntityId = this.tileEntity.mobSpawnDataList.get(0).getEntityId();
        }

        if (this.tileEntity.mobSpawnDataList != null) {
            for (int i = 0; i < this.tileEntity.mobSpawnDataList.size(); i++) {
                MobSpawnData mobData = this.tileEntity.mobSpawnDataList.get(i);
                String entityKey = mobData.getEntityId();
                Optional<EntityType<?>> optional = EntityType.byString(entityKey);
                int relX = (this.width - this.imageWidth) / 2;
                int relY = (this.height - this.imageHeight) / 2;
                int y = 30 + (i * 18);
                int il = i;

                if (optional.isPresent()) {
                    // Entity name button
                    this.entityButtons.add(
                        this.addRenderableWidget(Button.builder(Component.literal(optional.get().getDescription().getString()),
                                button -> {
                                    selectedModifier = String.valueOf(Component.literal(optional.get().getDescription().getString()));
                                    selectedEntityId = entityKey;
                                    updateDisplayEntity();
                                })
                            .pos(relX + 117, relY + y)
                            .size(64, 16)
                            .build()
                    )
                    );

                    // Spawn count button
                    final int index = i;
                    NumberButton mobSpawnCountButton = ToggleButtonFactory.SPAWNER_MOB_SPAWN_COUNT(relX + 90, relY + y, mobData.getSpawnCount(), b -> {
                        int newCount = ((NumberButton) b).getValue();
                        mobData.setSpawnCount(newCount);
                        ClientPacketDistributor.sendToServer(new SpawnerMobCountUpdateManager(entityKey, newCount));
                    });
                    this.entityButtons.add(mobSpawnCountButton);
                    this.addRenderableWidget(mobSpawnCountButton);

                    // Remove button
                    this.entityButtons.add(
                        this.addRenderableWidget(ToggleButtonFactory.REMOVE_SINGLE_MOB_BUTTON(relX + 256, relY + y, true,
                    b -> {
                                    ClientPacketDistributor.sendToServer(new SpawnerClearUpdateManager(entityKey, false));
                                    this.tileEntity.mobSpawnDataList.remove(index);
                                    this.tileEntity.markDirtyClient();
                                    addEntitiesToList();
                                    updateUIBasedOnMobCount();
                                })
                        )
                    );
                }
            }
        }

        // Update UI based on mob count
        updateUIBasedOnMobCount();
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int pX, int pY) {
        super.extractTooltip(graphics, pX, pY);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof TolkienButton button && !button.getLocalization(pX, pY).equals(Component.empty()))
                graphics.setTooltipForNextFrame(font, button.getLocalization(pX, pY), pX, pY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 5, 5, 8552833);
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.minSpawnDelay"), 10, 20, 8552833, false);
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.activationRange"), 10, 60, 8552833, false);
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.spawnCount"), 10, 100, 8552833, false);
        graphics.text(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.clusterRange"), 10, 136, 8552833, false);
    }

    /**
     * The mob preview used to be a copy of vanilla's inventory-screen entity renderer. 26.1 rewrote
     * that path around render states, and vanilla still exposes it for exactly this use, so the copy
     * is gone and this calls the real one.
     */
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        TolkienBaseSpawner basespawner = this.tileEntity.getSpawnerBase();
        LivingEntity entityType = (LivingEntity) basespawner.getOrCreateDisplayEntity(this.tileEntity.getLevel(), this.tileEntity.getBlockPos());

        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        if (entityType instanceof LivingEntity) {
            InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, relX + 26, relY + 8, relX + 403, relY + 160, 30, 0.0625F, mouseX, mouseY, entityType);
        }

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
        addRenderableWidget(ToggleButtonFactory.SPAWNER_MIN_DELAY(offsetX + 15, offsetY + 32, spawnerDelays.minSpawnDelay, b -> {
            spawnerDelays.minSpawnDelay = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new SpawnerDelaysUpdateManager(spawnerDelays.minSpawnDelay, spawnerDelays.maxSpawnDelay, spawnerDelays.spawnDelay));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_MAX_DELAY(offsetX + 44, offsetY + 32, spawnerDelays.maxSpawnDelay, b -> {
            spawnerDelays.maxSpawnDelay = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new SpawnerDelaysUpdateManager(spawnerDelays.minSpawnDelay, spawnerDelays.maxSpawnDelay, spawnerDelays.spawnDelay));
        }));

            // Ranges
        addRenderableWidget(ToggleButtonFactory.SPAWNER_ACTIVATION_RANGE(offsetX + 15, offsetY + 72, spawnerRanges.activationRange, b -> {
            spawnerRanges.activationRange = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_SPAWN_RANGE(offsetX + 44, offsetY + 72, spawnerRanges.spawnRange, b -> {
            spawnerRanges.spawnRange = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        NumberButton spawnCountButton = ToggleButtonFactory.SPAWNER_SPAWN_COUNT(offsetX + 15, offsetY + 112, spawnerRanges.spawnCount, b -> {
            spawnerRanges.spawnCount = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        });
        this.numberButtons.add(spawnCountButton);
        addRenderableWidget(spawnCountButton);
        addRenderableWidget(ToggleButtonFactory.SPAWNER_MAX_CLUSTER(offsetX + 44, offsetY + 112, spawnerRanges.maxCluster, b -> {
            spawnerRanges.maxCluster = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_CLUSTER_RANGE(offsetX + 29, offsetY + 149, spawnerRanges.clusterRange, b -> {
            spawnerRanges.clusterRange = ((NumberButton) b).getValue();
            ClientPacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
    }

    private void addSpawnerButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.REMOVE_MOB_BUTTON(offsetX + 90, offsetY + 149, true, b -> {
            ClientPacketDistributor.sendToServer(new SpawnerClearUpdateManager("", true));
            this.tileEntity.mobSpawnDataList.clear();
            this.tileEntity.getSpawnerBase().clearSpawnData();
            this.tileEntity.markDirtyClient();
            addEntitiesToList();
        }));
        addRenderableWidget(ToggleButtonFactory.IGNORE_PLAYER_BUTTON(offsetX + 111, offsetY + 149, spawnerSettings.requirePlayer, b -> {
            spawnerSettings.requirePlayer = !spawnerSettings.requirePlayer;
            ClientPacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        }));
        addRenderableWidget(ToggleButtonFactory.IGNORE_REQUIREMENTS_BUTTON(offsetX + 132, offsetY + 149, spawnerSettings.ignoreSpawnReq, b -> {
            spawnerSettings.ignoreSpawnReq = !spawnerSettings.ignoreSpawnReq;
            ClientPacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        }));
        addRenderableWidget(ToggleButtonFactory.IGNORE_PARTICLES_BUTTON(offsetX + 153, offsetY + 149, spawnerSettings.spawnerParticles, b -> {
            spawnerSettings.spawnerParticles = !spawnerSettings.spawnerParticles;
            ClientPacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        }));
        addRenderableWidget(ToggleButtonFactory.REDSTONE_CONTROL_BUTTON(offsetX + 174, offsetY + 149, spawnerSettings.redstoneControl, b -> {
            spawnerSettings.redstoneControl = !spawnerSettings.redstoneControl;
            ClientPacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        }));
        ToggleButton spawnAllButton = ToggleButtonFactory.SPAWN_ALL_TOGETHER_BUTTON(offsetX + 195, offsetY + 149, spawnerSettings.spawnAllTogether, b -> {
            spawnerSettings.spawnAllTogether = !spawnerSettings.spawnAllTogether;
            ClientPacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        });
        this.spawnerButtons.add(spawnAllButton);
        addRenderableWidget(spawnAllButton);
    }
}
