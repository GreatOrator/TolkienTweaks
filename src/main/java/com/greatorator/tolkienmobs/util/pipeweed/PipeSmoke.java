package com.greatorator.tolkienmobs.util.pipeweed;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * The shapes a pipe draws in the air. Every shape is a list of points in the plane in front of the
 * smoker (x to the right, y up), which is then laid out along the way they are looking. The points
 * are handed out in order with a little more forward reach each time, so the puff reads as one shape
 * drawn out over a heartbeat rather than a wall of smoke that appears at once.
 */
public final class PipeSmoke {
    /** How far each later point is pushed forward, which is what draws the shape out. */
    private static final double TRAIL = 0.016;
    private static final double DRIFT = 0.02;

    private PipeSmoke() {
    }

    public enum Shape {
        /** A ring, the old hobbit trick. */
        RING,
        /** A little ship under sail, for the Brandywine in the smoker's mind. */
        SHIP,
        /** A long serpent with wings, which no hobbit admits to blowing. */
        DRAGON
    }

    public static void blow(ServerLevel level, Player player, Shape shape) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 right = look.cross(new Vec3(0.0, 1.0, 0.0));
        if (right.lengthSqr() < 1.0E-4) {
            right = new Vec3(1.0, 0.0, 0.0);
        }
        right = right.normalize();
        Vec3 up = right.cross(look).normalize();

        Vec3 mouth = player.getEyePosition().add(look.scale(0.55)).add(up.scale(-0.12));

        for (int i = 0; i < 3; i++) {
            Vec3 at = mouth.add(look.scale(i * 0.05));
            puff(level, at, look);
        }

        List<Vec3> outline = outline(shape);
        for (int i = 0; i < outline.size(); i++) {
            Vec3 point = outline.get(i);
            Vec3 at = mouth.add(right.scale(point.x)).add(up.scale(point.y)).add(look.scale(TRAIL * i));
            puff(level, at, look);
        }
    }

    private static void puff(ServerLevel level, Vec3 at, Vec3 look) {
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, at.x, at.y, at.z, 0,
                look.x * DRIFT, look.y * DRIFT + 0.004, look.z * DRIFT, 0.01);
    }

    private static List<Vec3> outline(Shape shape) {
        List<Vec3> points = new ArrayList<>();
        switch (shape) {
            case RING -> {
                int steps = 16;
                for (int i = 0; i < steps; i++) {
                    double angle = (Math.PI * 2.0 * i) / steps;
                    points.add(new Vec3(Math.cos(angle) * 0.5, Math.sin(angle) * 0.5 + 0.35, 0.0));
                }
            }
            case SHIP -> {
                // The hull dips in the middle and rises at bow and stern.
                for (int i = 0; i <= 10; i++) {
                    double t = -1.0 + (2.0 * i) / 10.0;
                    points.add(new Vec3(t * 0.6, 0.45 - 0.3 * (1.0 - t * t), 0.0));
                }
                // The mast.
                line(points, new Vec3(0.0, 0.4, 0.0), new Vec3(0.0, 1.15, 0.0), 5);
                // The sail, hanging off the mast.
                line(points, new Vec3(0.0, 1.15, 0.0), new Vec3(0.45, 0.6, 0.0), 4);
                line(points, new Vec3(0.45, 0.6, 0.0), new Vec3(0.0, 0.45, 0.0), 3);
            }
            case DRAGON -> {
                // A wavy body, then wings off its back and a head at the front.
                for (int i = 0; i <= 16; i++) {
                    double t = -1.0 + (2.0 * i) / 16.0;
                    points.add(new Vec3(t * 0.65, 0.6 + 0.18 * Math.sin(t * Math.PI * 3.0), 0.0));
                }
                line(points, new Vec3(0.0, 0.65, 0.0), new Vec3(0.5, 1.1, 0.0), 3);
                line(points, new Vec3(0.5, 1.1, 0.0), new Vec3(0.1, 0.75, 0.0), 2);
                line(points, new Vec3(0.0, 0.65, 0.0), new Vec3(-0.5, 1.1, 0.0), 3);
                line(points, new Vec3(-0.5, 1.1, 0.0), new Vec3(-0.1, 0.75, 0.0), 2);
                line(points, new Vec3(0.65, 0.55, 0.0), new Vec3(0.85, 0.7, 0.0), 2);
            }
        }
        return points;
    }

    private static void line(List<Vec3> points, Vec3 from, Vec3 to, int steps) {
        for (int i = 1; i <= steps; i++) {
            double t = (double) i / steps;
            points.add(new Vec3(from.x + (to.x - from.x) * t, from.y + (to.y - from.y) * t, 0.0));
        }
    }
}
