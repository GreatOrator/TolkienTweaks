package com.greatorator.tolkienmobs.block;

import net.minecraft.world.level.block.Block;

/**
 * The base for the mod's plain blocks. Its lore flag and tooltip override are gone: 26.1 removed
 * {@code Block#appendHoverText}, so a tooltip is the item's business now - {@code TolkienItem} carries
 * the lore flag for the items, and the few block items that need one extend {@code BlockItem}.
 */
public abstract class TolkienBlock extends Block {
    public boolean hasEffectOverride = false;
    private boolean canSpawnEntity = false;
    private boolean itemHasUse = false;

    public TolkienBlock(Properties properties) {
        super(properties);
    }

    public TolkienBlock setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    public TolkienBlock setItemHasUse() {
        this.itemHasUse = true;
        return this;
    }

    public TolkienBlock setSpawnInfo() {
        this.canSpawnEntity = true;
        return this;
    }
}
