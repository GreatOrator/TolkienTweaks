package com.greatorator.tolkienmobs.util;

import com.greatorator.tolkienmobs.handler.vec.Vector3;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class VectorUtils {

    public static Vector3 calculateNormal(Vector3... vertices) {
        Vector3 diff1 = vertices[1].copy().subtract(vertices[0]);
        Vector3 diff2 = vertices[2].copy().subtract(vertices[0]);
        return diff1.crossProduct(diff2).normalize().copy();
    }

    public static int findSide(Vector3 normal) {
        Direction dir = findSideE(normal);
        if (dir == null) return -1;

        return dir.ordinal();
    }

    public static @Nullable Direction findSideE(Vector3 normal) {
        if (normal.y <= -0.99) return Direction.DOWN;
        if (normal.y >= 0.99) return Direction.UP;
        if (normal.z <= -0.99) return Direction.NORTH;
        if (normal.z >= 0.99) return Direction.SOUTH;
        if (normal.x <= -0.99) return Direction.WEST;
        if (normal.x >= 0.99) return Direction.EAST;

        return null;
    }
}
