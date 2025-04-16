package com.greatorator.tolkienmobs.util;

import com.greatorator.tolkienmobs.init.TolkienTags;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class GeneralUtility {
    public static final boolean DECK_THE_HALLS;
    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST,
            Direction.NORTH
    };
    public static final Direction[] VERTICAL_DIRECTIONS = new Direction[]{
            Direction.UP,
            Direction.DOWN
    };
    public static final Direction[][] CORNER_DIRECTIONS = new Direction[][]{
            {Direction.EAST, Direction.NORTH}, {Direction.EAST, Direction.SOUTH},
            {Direction.WEST, Direction.NORTH}, {Direction.WEST, Direction.SOUTH},
    };
    public enum RedstoneMode {
        TOGGLE,
        PULSE,
        DELAY;

        public RedstoneMode next() {
            RedstoneMode[] values = values();
            int nextOrdinal = (this.ordinal() + 1) % values.length;
            return values[nextOrdinal];
        }
    }

    public static boolean hasStoredEnchantment(ResourceKey<Enchantment> enchantment, ItemStack stack) {
        ItemEnchantments itemEnchantmentsComponent = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for(Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantmentsComponent.entrySet()) if(entry.getKey().is(enchantment)) return true;
        return false;
    }

    public static int getEnchantmentLevel(LevelAccessor level, ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        try {
            return itemStack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
        } catch (Exception e) {
            return 0;
        }
    }

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

    public static void doExtraTicks(ServerLevel serverLevel, BlockPos blockPos, double rate) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        if (!isValidTickAccelBlock(serverLevel, blockState, blockEntity))
            return;
        for (int i = 0; i < rate; i++) {
            if (blockEntity != null) {
                BlockEntityTicker<BlockEntity> ticker = blockEntity.getBlockState().getTicker(serverLevel, (BlockEntityType<BlockEntity>) blockEntity.getType());
                if (ticker != null) {
                    ticker.tick(serverLevel, blockPos, blockEntity.getBlockState(), blockEntity);
                }
            } else if (blockState.isRandomlyTicking()) {
                if (serverLevel.random.nextInt(1365) == 0) { //Average Random Tick Rate
                    blockState.randomTick(serverLevel, blockPos, serverLevel.random);
                }
            }
        }
    }

    public static boolean isValidTickAccelBlock(ServerLevel serverLevel, BlockState blockState, BlockEntity blockEntity) {
        if (blockEntity == null && !blockState.isRandomlyTicking())
            return false;
        if (blockEntity != null) {
            BlockEntityTicker<BlockEntity> ticker = blockEntity.getBlockState().getTicker(serverLevel, (BlockEntityType<BlockEntity>) blockEntity.getType());
            if (ticker == null)
                return false;
        }
        if (blockState.is(TolkienTags.Blocks.TICK_SPEED_DENY))
            return false;
        return true;
    }

    @NotNull
    public static BlockHitResult getHitResult(Player player) {
        var playerLook = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        var lookVec = player.getViewVector(1.0F);
        var reach = player.blockInteractionRange();
        var endLook = playerLook.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        BlockHitResult hitResult = player.level().clip(new ClipContext(playerLook, endLook, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return hitResult;
    }

    public static Entity getEntityLookedAt(Player player, double maxDistance) {
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getViewVector(1.0F).scale(maxDistance);
        Vec3 endPosition = eyePosition.add(lookVector);

        // Perform ray trace for entities
        HitResult hitResult = player.level().clip(new ClipContext(
                eyePosition, endPosition,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player));

        if (hitResult.getType() != HitResult.Type.MISS) {
            endPosition = hitResult.getLocation();
        }

        EntityHitResult entityHitResult = rayTraceEntities(player, eyePosition, endPosition, player.getBoundingBox().expandTowards(lookVector).inflate(1.0D, 1.0D, 1.0D), maxDistance);

        if (entityHitResult != null) {
            return entityHitResult.getEntity();
        }

        return null;
    }

    private static EntityHitResult rayTraceEntities(Player player, Vec3 start, Vec3 end, AABB boundingBox, double maxDistance) {
        double closestDistance = maxDistance;
        Entity closestEntity = null;
        Vec3 hitLocation = null;

        for (Entity entity : player.level().getEntities(player, boundingBox, e -> e != player && e.isPickable())) {
            AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> optHitVec = entityBoundingBox.clip(start, end);

            if (entityBoundingBox.contains(start)) {
                if (closestDistance >= 0.0D) {
                    closestEntity = entity;
                    hitLocation = optHitVec.orElse(start);
                    closestDistance = 0.0D;
                }
            } else if (optHitVec.isPresent()) {
                Vec3 hitVec = optHitVec.get();
                double distanceToHitVec = start.distanceTo(hitVec);

                if (distanceToHitVec < closestDistance || closestDistance == 0.0D) {
                    if (entity.getRootVehicle() == player.getRootVehicle() && !entity.canRiderInteract()) {
                        if (closestDistance == 0.0D) {
                            closestEntity = entity;
                            hitLocation = hitVec;
                        }
                    } else {
                        closestEntity = entity;
                        hitLocation = hitVec;
                        closestDistance = distanceToHitVec;
                    }
                }
            }
        }

        return closestEntity == null ? null : new EntityHitResult(closestEntity, hitLocation);
    }

    public static boolean inBounds(int x, int y, int w, int h, double ox, double oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }

    public static Vector3f findOffset(Direction direction, int slot, Vector3f[] offsets) {
        Vector3f offsetVector = new Vector3f(offsets[slot]);
        switch (direction) {
            case UP -> {
                Quaternionf quaternionf = Axis.XP.rotationDegrees(-270);
                offsetVector = quaternionf.transform(offsetVector);
                //offsetVector.transform(Vector3f.XP.rotationDegrees(-270));
                offsetVector.add(0, 1, 0);
            }
            case DOWN -> {
                Quaternionf quaternionf = Axis.XP.rotationDegrees(-90);
                offsetVector = quaternionf.transform(offsetVector);
                //offsetVector.transform(Vector3f.XP.rotationDegrees(-90));
                offsetVector.add(0, 0, 1);
                //reverse = false;
            }
            //case NORTH -> offsetVector;
            case EAST -> {
                Quaternionf quaternionf = Axis.YP.rotationDegrees(-90);
                offsetVector = quaternionf.transform(offsetVector);
                //offsetVector.transform(Vector3f.YP.rotationDegrees(-90));
                offsetVector.add(1, 0, 0);
            }
            case SOUTH -> {
                Quaternionf quaternionf = Axis.YP.rotationDegrees(-180);
                offsetVector = quaternionf.transform(offsetVector);
                //offsetVector.transform(Vector3f.YP.rotationDegrees(-180));
                offsetVector.add(1, 0, 1);
            }
            case WEST -> {
                Quaternionf quaternionf = Axis.YP.rotationDegrees(-270);
                offsetVector = quaternionf.transform(offsetVector);
                //offsetVector.transform(Vector3f.YP.rotationDegrees(-270));
                offsetVector.add(0, 0, 1);
            }
        }
        return offsetVector;
    }

    public static ListTag stringListToNBT(List<String> list) {
        ListTag nbtList = new ListTag();
        for (String string : list) {
            CompoundTag tag = new CompoundTag();
            tag.putString("list", string);
            nbtList.add(tag);
        }
        return nbtList;
    }

    public static List<String> NBTToStringList(ListTag nbtList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < nbtList.size(); i++) {
            CompoundTag tag = nbtList.getCompound(i);
            list.add(tag.getString("list"));
        }
        return list;
    }

    public static MutableComponent tooltipMaker(String string, int color) {
        Style style = Style.EMPTY;
        style = style.withColor(color);
        MutableComponent current = Component.translatable(string);
        current.setStyle(style);
        return current;
    }

    public static void acceptDirections(BlockPos blockPos, Consumer<BlockPos> blockPosConsumer) {
        for (Direction direction : Direction.values()) {
            blockPosConsumer.accept(blockPos.relative(direction));
        }
        for (Direction horizontal : HORIZONTAL_DIRECTIONS) {
            for (Direction vertical : VERTICAL_DIRECTIONS) {
                blockPosConsumer.accept(blockPos.relative(horizontal).relative(vertical));
            }
        }
        for (Direction[] corner : CORNER_DIRECTIONS) {
            BlockPos pos1 = blockPos;
            for (Direction direction : corner) {
                pos1 = pos1.relative(direction);
            }
            for (Direction verticalDirection : VERTICAL_DIRECTIONS) {
                pos1 = pos1.relative(verticalDirection);
                blockPosConsumer.accept(pos1);
            }
        }
    }
}