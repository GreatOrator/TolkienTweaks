package com.greatorator.tolkienmobs.util.block;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.handler.DelayedTask;
import com.greatorator.tolkienmobs.handler.vec.Vector3;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.LinkedList;

public class TeleportUtility {
    public static Entity teleportEntity(Entity entity, Entity destination) {
        return teleportEntity(entity, destination.level().dimension(), destination.getX(), destination.getY(), destination.zOld, destination.getYRot(), destination.getXRot());
    }

    public static Entity teleportEntity(Entity entity, ResourceKey<Level> dimension, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
        if (entity == null || entity.level().isClientSide) {
            return entity;
        }

        MinecraftServer server = entity.getServer();
        ResourceKey<Level> sourceDim = entity.level().dimension();

        if (!entity.isVehicle() && !entity.isPassenger()) {
            return handleEntityTeleport(entity, server, sourceDim, dimension, xCoord, yCoord, zCoord, yaw, pitch);
        }

        Entity rootEntity = entity.getRootVehicle();
        PassengerHelper passengerHelper = new PassengerHelper(rootEntity);
        PassengerHelper rider = passengerHelper.getPassenger(entity);
        if (rider == null) {
            TolkienMobsMain.LOGGER.error("RiddenEntity: This error should not be possible");
            return entity;
        }
        passengerHelper.teleport(server, sourceDim, dimension, xCoord, yCoord, zCoord, yaw, pitch);
        passengerHelper.remountRiders();
        passengerHelper.updateClients();

        return rider.entity;
    }

    public static Entity teleportEntity(Entity entity, ResourceKey<Level> dimension, Vector3 pos, float yaw, float pitch) {
        return teleportEntity(entity, dimension, pos.x, pos.y, pos.z, yaw, pitch);
    }

    public static Entity teleportEntity(Entity entity, ResourceKey<Level> dimension, double xCoord, double yCoord, double zCoord) {
        return teleportEntity(entity, dimension, xCoord, yCoord, zCoord, entity.getYRot(), entity.getXRot());
    }

    public static Entity teleportEntity(Entity entity, ResourceKey<Level> dimension, Vector3 pos) {
        return teleportEntity(entity, dimension, pos.x, pos.y, pos.z, entity.getYRot(), entity.getXRot());
    }

    private static Entity handleEntityTeleport(Entity entity, MinecraftServer server, ResourceKey<Level> sourceDim, ResourceKey<Level> targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
        if (entity == null || entity.level().isClientSide || targetDim == null) {
            return entity;
        }

        boolean interDimensional = sourceDim != targetDim;

        if (interDimensional && !CommonHooks.onTravelToDimension(entity, sourceDim)) {
            return entity;
        }

        if (interDimensional) {
            if (entity instanceof ServerPlayer) {
                return teleportPlayerInterdimentional((ServerPlayer) entity, server, targetDim, xCoord, yCoord, zCoord, yaw, pitch);
            } else {
                return teleportEntityInterdimentional(entity, server, targetDim, xCoord, yCoord, zCoord, yaw, pitch);
            }
        } else {
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                player.connection.teleport(xCoord, yCoord, zCoord, yaw, pitch);
                player.setYHeadRot(yaw);
            } else {
                entity.moveTo(xCoord, yCoord, zCoord, yaw, pitch);
                entity.setYHeadRot(yaw);
            }
        }
        return entity;
    }

    private static Entity teleportEntityInterdimentional(Entity entity, MinecraftServer server, ResourceKey<Level> targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
        ServerLevel targetWorld = server.getLevel(targetDim);
        if (!entity.isAlive() || targetWorld == null) {
            return null;
        }

        Entity movedEntity = entity.changeDimension(new DimensionTransition(targetWorld, new Vec3(xCoord, yCoord, zCoord), entity.getDeltaMovement(), yaw, pitch, DimensionTransition.DO_NOTHING));
        if (movedEntity != null) {
//            movedEntity.moveTo(xCoord, yCoord, zCoord, yaw, pitch);
            return movedEntity;
        }

        entity.unRide();
        movedEntity = entity.getType().create(targetWorld);
        if (movedEntity != null) {
            movedEntity.restoreFrom(entity);
            movedEntity.moveTo(xCoord, yCoord, zCoord, yaw, pitch);
            targetWorld.addDuringTeleport(movedEntity);
            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
            ((ServerLevel) entity.level()).resetEmptyTime();
            targetWorld.resetEmptyTime();
            return movedEntity;
        }
        return entity;
    }

    private static Player teleportPlayerInterdimentional(ServerPlayer player, MinecraftServer server, ResourceKey<Level> targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
        ServerLevel targetWorld = server.getLevel(targetDim);
        if (!player.isAlive() || targetWorld == null) {
            return player;
        }
        player.isChangingDimension = true;
        player.teleportTo(targetWorld, xCoord, yCoord, zCoord, yaw, pitch);

        player.lastSentExp = -1;
        player.lastSentHealth = -1.0F;
        player.lastSentFood = -1;

        player.onUpdateAbilities();
        return player;
    }
    public static Entity getHighestRidingEntity(Entity mount) {
        Entity entity;
        for(entity = mount; entity.getPassengers().size() > 0; entity = (Entity)entity.getPassengers().get(0)) {
        }

        return entity;
    }

    private static class PassengerHelper {
        public Entity entity;
        public LinkedList<PassengerHelper> passengers = new LinkedList<>();
        public double offsetX, offsetY, offsetZ;

        public PassengerHelper(Entity entity) {
            this.entity = entity;
            if (entity.isPassenger()) {
                offsetX = entity.getX() - entity.getVehicle().getX();
                offsetY = entity.getY() - entity.getVehicle().getY();
                offsetZ = entity.getZ() - entity.getVehicle().getZ();
            }
            for (Entity passenger : entity.getPassengers()) {
                passengers.add(new PassengerHelper(passenger));
            }
        }

        public void teleport(MinecraftServer server, ResourceKey<Level> sourceDim, ResourceKey<Level> targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
            entity.ejectPassengers();
            entity = handleEntityTeleport(entity, server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch);
            for (PassengerHelper passenger : passengers) {
                passenger.teleport(server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch);
            }
        }

        public void remountRiders() {
            if (entity == null) {
                return;
            }
            if (entity.isPassenger()) {
                entity.moveTo(entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, entity.getYRot(), entity.getXRot());
            }
            for (PassengerHelper passenger : passengers) {
                DelayedTask.run(1, () -> passenger.entity.startRiding(entity, true));
                passenger.remountRiders();
            }
        }

        public void updateClients() {
            if (entity instanceof ServerPlayer) {
                updateClient((ServerPlayer) entity);
            }
            for (PassengerHelper passenger : passengers) {
                passenger.updateClients();
            }
        }

        private void updateClient(ServerPlayer playerMP) {
            if (entity.isVehicle()) {
                playerMP.connection.send(new ClientboundSetPassengersPacket(entity));
            }
            for (PassengerHelper passenger : passengers) {
                passenger.updateClients();
            }
        }

        public PassengerHelper getPassenger(Entity passenger) {
            if (this.entity == passenger) {
                return this;
            }

            for (PassengerHelper rider : passengers) {
                PassengerHelper re = rider.getPassenger(passenger);
                if (re != null) {
                    return re;
                }
            }

            return null;
        }
    }
}