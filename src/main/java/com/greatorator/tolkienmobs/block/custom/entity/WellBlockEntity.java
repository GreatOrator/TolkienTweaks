package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.keyframe.event.builtin.AutoPlayingSoundKeyframeHandler;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WellBlockEntity extends TolkienBlockEntity implements GeoBlockEntity {
    protected static final RawAnimation WELL_BUCKET = RawAnimation.begin().thenPlay("fill");
    protected static final RawAnimation WELL_IDLE = RawAnimation.begin().thenLoop("idle");
    private AnimationController<WellBlockEntity> animationController;

    public WellBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.WELL_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void onRightClick(BlockState state, Player player, InteractionHand hand) {
        triggerAnim("popup_controller", "fill");
    }

    /** Animations */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        animationController = getController();
        controllers.add(animationController.setSoundKeyframeHandler(new AutoPlayingSoundKeyframeHandler<>()));
        controllers.add(new AnimationController<>(this, "controller", 0, event -> event.setAndContinue(WELL_IDLE)));
    }

    private AnimationController<WellBlockEntity> getController() {
        return new AnimationController<>(this, "popup_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("fill", WELL_BUCKET);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
