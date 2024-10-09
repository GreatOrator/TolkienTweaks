package com.greatorator.tolkienmobs.containers.widget;

public enum ButtonTexture implements TexturePosition {

  LARGE_BUTTON(0, 88, 20, 200),
  SMALL_BUTTON(0, 168, 15, 200),
  UP_BUTTON(176, 0, 8, 16),
  DOWN_BUTTON(172, 8, 8, 16),
  DEPLOY_BED_BUTTON(208, 0, 16, 16),
  PICKUP_BED_BUTTON(208, 16, 16, 16),
  DEPLOY_CAMPFIRE_BUTTON(224, 0, 16, 16),
  PICKUP_CAMPFIRE_BUTTON(224, 16, 16, 16),
  OPEN_UPGRADES_BUTTON(240, 0, 16, 16),
  CLOSE_UPGRADES_BUTTON(240, 16, 16, 16),
  KEY_KEEP_BUTTON(208, 64, 16, 16),
  KEY_CONSUME_BUTTON(208, 80, 16, 16),
  REDSTONE_BUTTON(224, 64, 16, 16),
  REDSTONE_ACTIVE_BUTTON(224, 80, 16, 16),
  REDSTONE_DELAY_BUTTON(240, 64, 16, 16),
  REDSTONE_DELAY_ACTIVE_BUTTON(240, 80, 16, 16),
  REDSTONE_PULSE_BUTTON(240, 96, 16, 16),
  REDSTONE_PULSE_ACTIVE_BUTTON(240, 112, 16, 16),
  SPAWN_REQUIREMENTS_BUTTON(208, 128, 16, 16),
  SPAWN_REQUIREMENTS_IGNORE_BUTTON(208, 144, 16, 16),
  PLAYER_BUTTON(224, 128, 16, 16),
  PLAYER_IGNORE_BUTTON(224, 144, 16, 16),
  SPAWN_PARTICLES_BUTTON(240, 128, 16, 16),
  SPAWN_PARTICLES_IGNORE_BUTTON(240, 144, 16, 16);

  private final int x, y, height, width;

  ButtonTexture(int x, int y, int height, int width) {
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;
  }

  @Override
  public int x() {
    return x;
  }

  @Override
  public int y() {
    return y;
  }

  @Override
  public int height() {
    return height;
  }

  @Override
  public int width() {
    return width;
  }
}
