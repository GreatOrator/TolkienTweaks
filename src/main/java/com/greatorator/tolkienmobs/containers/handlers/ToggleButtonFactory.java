package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.containers.widget.GrayscaleButton;
import com.greatorator.tolkienmobs.containers.widget.NumberButton;
import com.greatorator.tolkienmobs.containers.widget.ToggleButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ToggleButtonFactory {
    public record TextureLocalization(ResourceLocation texture, MutableComponent localization) {
    }

    private static final int STANDARD_WIDTH = 16; // Example width
    private static final int STANDARD_HEIGHT = 16; // Example height

    /** Key Stone Button **/
    private static final List<TextureLocalization> KEEP_KEY_TEXTURES = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/keystone/key_keep.png"), Component.translatable("screen.tolkienmobs.keystone.key.keep")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/keystone/key_consume.png"), Component.translatable("screen.tolkienmobs.keystone.key.consume"))
    );

    public static ToggleButton KEEPKEYBUTTON(int x, int y, boolean startingValue, Button.OnPress onPress) {
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, KEEP_KEY_TEXTURES, startingValue, onPress);
    }

    /** Spawner Button **/
    private static final List<TextureLocalization> IGNORE_PARTICLES = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/spawner/particles.png"), Component.translatable("screen.tolkienmobs.camo_spawner.spawnerParticles.enabled")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/spawner/hide_particles.png"), Component.translatable("screen.tolkienmobs.camo_spawner.spawnerParticles.disabled"))
    );
    private static final List<TextureLocalization> IGNORE_PLAYER = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/spawner/player.png"), Component.translatable("screen.tolkienmobs.camo_spawner.requirePlayer.enabled")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/spawner/ignore_player.png"), Component.translatable("screen.tolkienmobs.camo_spawner.requirePlayer.disabled"))
    );
    private static final List<TextureLocalization> IGNORE_REQUIREMENTS = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/spawner/spawn_requirements.png"), Component.translatable("screen.tolkienmobs.camo_spawner.ignoreSpawnReq.enabled")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/spawner/ignore_spawn_requirements.png"), Component.translatable("screen.tolkienmobs.camo_spawner.ignoreSpawnReq.ignored"))
    );

    public static ToggleButton IGNORE_PARTICLES_BUTTON(int x, int y, boolean startingValue, Button.OnPress onPress) {
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, IGNORE_PARTICLES, startingValue, onPress);
    }
    public static ToggleButton IGNORE_PLAYER_BUTTON(int x, int y, boolean startingValue, Button.OnPress onPress) {
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, IGNORE_PLAYER, startingValue, onPress);
    }
    public static ToggleButton IGNORE_REQUIREMENTS_BUTTON(int x, int y, boolean startingValue, Button.OnPress onPress) {
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, IGNORE_REQUIREMENTS, startingValue, onPress);
    }

    /** Spawner Numbers **/
    private static final Component spawnerDelayMinLocalization = Component.translatable("screen.tolkienmobs.camo_spawner.minDelayValue");
    private static final Component spawnerDelayMaxLocalization = Component.translatable("screen.tolkienmobs.camo_spawner.maxDelayValue");


    private static final Component activationRangeLocalization = Component.translatable("screen.tolkienmobs.camo_spawner.activationRange");
    private static final Component spawnRangeLocalization = Component.translatable("screen.tolkienmobs.camo_spawner.spawnRange");
    private static final Component spawnCountLocalization = Component.translatable("screen.tolkienmobs.camo_spawner.spawnCount");
    private static final Component maxClusterLocalization = Component.translatable("screen.tolkienmobs.camo_spawner.maxCluster");
    private static final Component clusterRangeLocalization = Component.translatable("screen.tolkienmobs.camo_spawner.clusterRange");

    public static NumberButton SPAWNER_ACTIVATION_RANGE(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 16, value, 1, 200, activationRangeLocalization, onPress);
    }

    public static NumberButton SPAWNER_SPAWN_RANGE(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 16, value, 1, 200, spawnRangeLocalization, onPress);
    }

    public static NumberButton SPAWNER_SPAWN_COUNT(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 16, value, 1, 200, spawnCountLocalization, onPress);
    }

    public static NumberButton SPAWNER_MAX_CLUSTER(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 16, value, 1, 200, maxClusterLocalization, onPress);
    }

    public static NumberButton SPAWNER_CLUSTER_RANGE(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 16, value, 1, 200, clusterRangeLocalization, onPress);
    }

    public static NumberButton SPAWNER_MIN_DELAY(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 16, value, 1, 200, spawnerDelayMinLocalization, onPress);
    }

    public static NumberButton SPAWNER_MAX_DELAY(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 16, value, 1, 200, spawnerDelayMaxLocalization, onPress);
    }

    /** Redstone Mode **/
    private static final List<TextureLocalization> REDSTONE_TOGGLE_TEXTURES = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/keystone/redstone_always.png"), Component.translatable("screen.tolkienmobs.keystone.always.active")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/keystone/redstone_delay.png"), Component.translatable("screen.tolkienmobs.keystone.delay.active")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/keystone/redstone_pulse.png"), Component.translatable("screen.tolkienmobs.keystone.pulse.active"))
    );
    public static ToggleButton REDSTONE_TOGGLE_BUTTON(int x, int y, int startingValue, Button.OnPress onPress) {
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, REDSTONE_TOGGLE_TEXTURES, startingValue, onPress);
    }

    /** Sleeping Bag **/
    private static final List<TextureLocalization> SLEEPING_BAG_TOGGLE_TEXTURES = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/bed_deployed.png"), Component.translatable("screen.tolkienmobs.backpack.bed.remove")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/bed.png"), Component.translatable("screen.tolkienmobs.backpack.bed.deployed"))
    );
    public static ToggleButton SLEEPING_BAG_TOGGLE_BUTTON(int x, int y, boolean startingValue, Button.OnPress onPress) {
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, SLEEPING_BAG_TOGGLE_TEXTURES, startingValue, onPress);
    }

    /** Campfire **/
    private static final List<TextureLocalization> CAMPFIRE_TOGGLE_TEXTURES = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/campfire_deployed.png"), Component.translatable("screen.tolkienmobs.backpack.campfire.remove")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/campfire.png"), Component.translatable("screen.tolkienmobs.backpack.campfire.deployed"))
    );
    public static ToggleButton CAMPFIRE_TOGGLE_BUTTON(int x, int y, boolean startingValue, Button.OnPress onPress) {
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, CAMPFIRE_TOGGLE_TEXTURES, startingValue, onPress);
    }

    /** Upgrade Menu **/
    private static final List<TextureLocalization> UPGRADE_TOGGLE_TEXTURES = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/upgrade.png"), Component.translatable("screen.tolkienmobs.backpack.open.upgrade")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/backpack/close_upgrade.png"), Component.translatable("screen.tolkienmobs.backpack.close.upgrade"))
    );
    public static ToggleButton UPGRADE_TOGGLE_BUTTON(int x, int y, boolean startingValue, Button.OnPress onPress) {
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, UPGRADE_TOGGLE_TEXTURES, startingValue, onPress);
    }

    /** Set Key Uses **/
    private static final Component ticksButtonLocalization = Component.translatable("screen.tolkienmobs.keystone.tickdelay");

    public static NumberButton KEYUSESBUTTON(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 16, value, TolkienMobsConfig.MINIMUM_TICK_SPEED.get(), 50, usesButtonLocalization, onPress);
    }

    /** Set Tick Delay Amount **/
    private static final Component usesButtonLocalization = Component.translatable("screen.tolkienmobs.keyuses");

    public static NumberButton TICKDELAYBUTTON(int x, int y, int value, Button.OnPress onPress) {
        return new NumberButton(x, y, 24, 12, value, TolkienMobsConfig.MINIMUM_TICK_SPEED.get(), 1200, ticksButtonLocalization, onPress);
    }

    /** Other Button Examples **/
    private static final ResourceLocation EXTRACT_EXP_BUTTON = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/buttons/remove.png");
    private static final Component EXTRACT_EXP_BUTTON_LOCALIZATION = Component.translatable("justdirethings.screen.retrieveexp");

    public static GrayscaleButton EXTRACTEXPBUTTON(int x, int y, boolean startingValue, Button.OnPress onPress) {
        return new GrayscaleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, EXTRACT_EXP_BUTTON, EXTRACT_EXP_BUTTON_LOCALIZATION, startingValue, onPress);
    }

    private static final List<TextureLocalization> INVENTORY_CONNECTION_TEXTURES = List.of(
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/buttons/inv-normal.png"), Component.translatable("justdirethings.screen.inv-normal")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/buttons/inv-armor.png"), Component.translatable("justdirethings.screen.inv-armor")),
            new TextureLocalization(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/buttons/inv-offhand.png"), Component.translatable("justdirethings.screen.inv-offhand"))
    );

    public static ToggleButton INVENTORYCONNECTIONBUTTON(int x, int y, MutableComponent component, int startingValue, Button.OnPress onPress) {
        List<TextureLocalization> textureLocalizations = new ArrayList<>();
        for (TextureLocalization textureLocalization : INVENTORY_CONNECTION_TEXTURES) {
            textureLocalizations.add(new TextureLocalization(textureLocalization.texture, Component.literal("").append(component).append(Component.literal(": ")).append(textureLocalization.localization)));
        }
        return new ToggleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, textureLocalizations, startingValue, onPress);
    }
}
