package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AncientWatcherDeadStatueBlockEntity extends TolkienBlockEntity {
    // Kept per block entity: state on the Block itself would be shared by every statue in every world.
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;

    public AncientWatcherDeadStatueBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TolkienBlocks.ANCIENT_WATCHER_DEAD_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public void tickClient() {
    }

    public void tickServer() {
    }

    /**
     * Consumes this statue's ability cooldown.
     *
     * @return true when the ability may be used, false while this statue is still cooling down.
     */
    public boolean tryUseAbility() {
        long time = System.currentTimeMillis();
        if (time <= this.nextAbilityUse) {
            return false;
        }
        this.nextAbilityUse = time + coolDown;
        return true;
    }
}
