package com.greatorator.tolkienmobs.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class GeneralUtility {
    public static final boolean DECK_THE_HALLS;

    static {
        Calendar calendar = Calendar.getInstance();
        DECK_THE_HALLS = calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) >= 19;
    }

    public static Component formatEffectNumber(float number, int decimalPlaces, String suffix) {
        String numberString = "";
        if (number >= 0) {
            numberString += "+";
        } else {
            numberString += "-";
        }
        numberString += String.format("%." + decimalPlaces + "f", number);
        numberString += suffix;
        return Component.literal(numberString).withStyle(ChatFormatting.GREEN);
    }

    public static Component formatEffectNumber(int number, String suffix) {
        return formatEffectNumber(number, 0, suffix);
    }

    @Nullable
    public static ItemStack getHeldStack(Player player, Item item) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return item == main.getItem()? main : item == off.getItem()? off : null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> getEntityTypeByKey(@Nonnull String key) {
        return (EntityType<T>) EntityType.byString(key).orElse(null);
    }

    public static void playLocalSound(Level level, BlockPos pos, SoundEvent sound, float volume, float pitch) {
        playLocalSound(level, pos, sound, SoundSource.NEUTRAL, volume, pitch);
    }

    public static void playLocalSound(Level level, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch) {
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), sound, category, volume, pitch, false);
    }

    public static Iterable<BlockPos> eachPositionIn(AABB aabb) {
        return BlockPos.betweenClosed(
                Mth.floor(aabb.minX),
                Mth.floor(aabb.minY),
                Mth.floor(aabb.minZ),
                Mth.ceil(aabb.maxX),
                Mth.ceil(aabb.maxY),
                Mth.ceil(aabb.maxZ));
    }

    public static <T> void runAndClear(Collection<T> c, Consumer<T> run) {
        Iterator<T> itr = c.iterator();
        while (itr.hasNext()) {
            run.accept(itr.next());
            itr.remove();
        }
    }

    public static Vec3 getOffsetPos(Entity entity, double offsetX, double offsetY, double offsetZ, float rotation) {
        Vec3 vector3d = (new Vec3(offsetZ, offsetY, offsetX).yRot(-rotation * ((float)Math.PI / 180F) - ((float)Math.PI / 2F)));
        return entity.position().add(vector3d.x, vector3d.y, vector3d.z);
    }

    public static Vec3 getOffsetMotion(Entity entity, double offsetX, double offsetY, double offsetZ, float rotation) {
        Vec3 vector3d = (new Vec3(offsetZ, offsetY, offsetX).yRot(-rotation * ((float)Math.PI / 180F) - ((float)Math.PI / 2F)));
        return vector3d;
    }

    public static void moveToCorrectHeight(Entity entity) {
        BlockPos blockpos = entity.blockPosition();
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = entity.level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(entity.level, blockpos1, Direction.UP)) {
                if (!entity.level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = entity.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(entity.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(blockpos.getY()) - 1);

        if (flag) {
            entity.setPos(blockpos.getX(), blockpos.getY() + d0, blockpos.getZ());
        }
    }
}