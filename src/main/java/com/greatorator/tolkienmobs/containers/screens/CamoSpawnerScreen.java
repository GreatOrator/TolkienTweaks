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
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
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
        super(menu, playerInventory, title);
        this.imageHeight = 206;
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
                    PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
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
                PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
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
                        PacketDistributor.sendToServer(new SpawnerMobCountUpdateManager(entityKey, newCount));
                    });
                    this.entityButtons.add(mobSpawnCountButton);
                    this.addRenderableWidget(mobSpawnCountButton);

                    // Remove button
                    this.entityButtons.add(
                        this.addRenderableWidget(ToggleButtonFactory.REMOVE_SINGLE_MOB_BUTTON(relX + 256, relY + y, true,
                    b -> {
                                    PacketDistributor.sendToServer(new SpawnerClearUpdateManager(entityKey, false));
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
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.minSpawnDelay"), 10, 20, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.activationRange"), 10, 60, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.spawnCount"), 10, 100, 8552833, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.tolkienmobs.camo_spawner.clusterRange"), 10, 136, 8552833, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        TolkienBaseSpawner basespawner = this.tileEntity.getSpawnerBase();
        LivingEntity entityType = (LivingEntity) basespawner.getOrCreateDisplayEntity(this.tileEntity.getLevel(), this.tileEntity.getBlockPos());

        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(RenderType::guiTextured, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        if (entityType instanceof LivingEntity) {
            renderEntityInInventoryFollowsMouse(guiGraphics, relX + 26, relY + 8, relX + 403, relY + 160, 30, 0.0625F, mouseX, mouseY, entityType);
        }

        if (renderablesChanged)
            updateRenderables();
    }

    public static void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)(x1 + x2) / 2.0F;
        float f1 = (float)(y1 + y2) / 2.0F;
        float f2 = (float)Math.atan((double)((f - mouseX) / 40.0F));
        float f3 = (float)Math.atan((double)((f1 - mouseY) / 40.0F));
        renderEntityInInventoryFollowsAngle(guiGraphics, x1, y1, x2, y2, scale, yOffset, f2, f3, entity);
    }

    public static void renderEntityInInventoryFollowsAngle(GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_, int p_294406_, int p_294663_, float p_275604_, float angleXComponent, float angleYComponent, LivingEntity p_275689_) {
        float f = (float)(p_275688_ + p_275535_) / 2.0F;
        float f1 = (float)(p_275245_ + p_294406_) / 2.0F;
        p_282802_.enableScissor(p_275688_, p_275245_, p_275535_, p_294406_);
        float f2 = angleXComponent;
        float f3 = angleYComponent;
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f3 * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf1);
        float f4 = p_275689_.yBodyRot;
        float f5 = p_275689_.getYRot();
        float f6 = p_275689_.getXRot();
        float f7 = p_275689_.yHeadRotO;
        float f8 = p_275689_.yHeadRot;
        p_275689_.yBodyRot = 180.0F + f2 * 20.0F;
        p_275689_.setYRot(180.0F + f2 * 40.0F);
        p_275689_.setXRot(-f3 * 20.0F);
        p_275689_.yHeadRot = p_275689_.getYRot();
        p_275689_.yHeadRotO = p_275689_.getYRot();
        float f9 = p_275689_.getScale();
        Vector3f vector3f = new Vector3f(0.0F, p_275689_.getBbHeight() / 2.0F + p_275604_ * f9, 0.0F);
        float f10 = (float)p_294663_ / f9;
        renderEntityInInventory(p_282802_, f, f1, f10, vector3f, quaternionf, quaternionf1, p_275689_);
        p_275689_.yBodyRot = f4;
        p_275689_.setYRot(f5);
        p_275689_.setXRot(f6);
        p_275689_.yHeadRotO = f7;
        p_275689_.yHeadRot = f8;
        p_282802_.disableScissor();
    }

    public static void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, float scale, Vector3f translate, Quaternionf pose, @Nullable Quaternionf cameraOrientation, LivingEntity entity) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((double)x, (double)y, 50.0);
        guiGraphics.pose().scale(scale, scale, -scale);
        guiGraphics.pose().translate(translate.x, translate.y, translate.z);
        guiGraphics.pose().mulPose(pose);
        guiGraphics.flush();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (cameraOrientation != null) {
            entityrenderdispatcher.overrideCameraOrientation(cameraOrientation.conjugate(new Quaternionf()).rotateY(3.1415927F));
        }

        entityrenderdispatcher.setRenderShadow(false);
        guiGraphics.drawSpecial(bufferSource -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 1.0F, guiGraphics.pose(), bufferSource, 15728880));
        guiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
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
            PacketDistributor.sendToServer(new SpawnerDelaysUpdateManager(spawnerDelays.minSpawnDelay, spawnerDelays.maxSpawnDelay, spawnerDelays.spawnDelay));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_MAX_DELAY(offsetX + 44, offsetY + 32, spawnerDelays.maxSpawnDelay, b -> {
            spawnerDelays.maxSpawnDelay = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerDelaysUpdateManager(spawnerDelays.minSpawnDelay, spawnerDelays.maxSpawnDelay, spawnerDelays.spawnDelay));
        }));

            // Ranges
        addRenderableWidget(ToggleButtonFactory.SPAWNER_ACTIVATION_RANGE(offsetX + 15, offsetY + 72, spawnerRanges.activationRange, b -> {
            spawnerRanges.activationRange = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_SPAWN_RANGE(offsetX + 44, offsetY + 72, spawnerRanges.spawnRange, b -> {
            spawnerRanges.spawnRange = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        NumberButton spawnCountButton = ToggleButtonFactory.SPAWNER_SPAWN_COUNT(offsetX + 15, offsetY + 112, spawnerRanges.spawnCount, b -> {
            spawnerRanges.spawnCount = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        });
        this.numberButtons.add(spawnCountButton);
        addRenderableWidget(spawnCountButton);
        addRenderableWidget(ToggleButtonFactory.SPAWNER_MAX_CLUSTER(offsetX + 44, offsetY + 112, spawnerRanges.maxCluster, b -> {
            spawnerRanges.maxCluster = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
        addRenderableWidget(ToggleButtonFactory.SPAWNER_CLUSTER_RANGE(offsetX + 29, offsetY + 149, spawnerRanges.clusterRange, b -> {
            spawnerRanges.clusterRange = ((NumberButton) b).getValue();
            PacketDistributor.sendToServer(new SpawnerRangesUpdateManager(spawnerRanges.activationRange, spawnerRanges.spawnRange, spawnerRanges.spawnCount, spawnerRanges.maxCluster, spawnerRanges.clusterRange));
        }));
    }

    private void addSpawnerButtons() {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        addRenderableWidget(ToggleButtonFactory.REMOVE_MOB_BUTTON(offsetX + 90, offsetY + 149, true, b -> {
            PacketDistributor.sendToServer(new SpawnerClearUpdateManager("", true));
            this.tileEntity.mobSpawnDataList.clear();
            this.tileEntity.getSpawnerBase().clearSpawnData();
            this.tileEntity.markDirtyClient();
            addEntitiesToList();
        }));
        addRenderableWidget(ToggleButtonFactory.IGNORE_PLAYER_BUTTON(offsetX + 111, offsetY + 149, spawnerSettings.requirePlayer, b -> {
            spawnerSettings.requirePlayer = !spawnerSettings.requirePlayer;
            PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        }));
        addRenderableWidget(ToggleButtonFactory.IGNORE_REQUIREMENTS_BUTTON(offsetX + 132, offsetY + 149, spawnerSettings.ignoreSpawnReq, b -> {
            spawnerSettings.ignoreSpawnReq = !spawnerSettings.ignoreSpawnReq;
            PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        }));
        addRenderableWidget(ToggleButtonFactory.IGNORE_PARTICLES_BUTTON(offsetX + 153, offsetY + 149, spawnerSettings.spawnerParticles, b -> {
            spawnerSettings.spawnerParticles = !spawnerSettings.spawnerParticles;
            PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        }));
        addRenderableWidget(ToggleButtonFactory.REDSTONE_CONTROL_BUTTON(offsetX + 174, offsetY + 149, spawnerSettings.redstoneControl, b -> {
            spawnerSettings.redstoneControl = !spawnerSettings.redstoneControl;
            PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        }));
        ToggleButton spawnAllButton = ToggleButtonFactory.SPAWN_ALL_TOGETHER_BUTTON(offsetX + 195, offsetY + 149, spawnerSettings.spawnAllTogether, b -> {
            spawnerSettings.spawnAllTogether = !spawnerSettings.spawnAllTogether;
            PacketDistributor.sendToServer(new SpawnerSettingsUpdateManager(spawnerSettings.requirePlayer, spawnerSettings.ignoreSpawnReq, spawnerSettings.redstoneControl, spawnerSettings.spawnerParticles, spawnerSettings.spawnAllTogether));
        });
        this.spawnerButtons.add(spawnAllButton);
        addRenderableWidget(spawnAllButton);
    }
}