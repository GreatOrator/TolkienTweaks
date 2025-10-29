package com.greatorator.tolkienmobs.block;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.extensions.IOwnedSpawner;
import net.neoforged.neoforge.event.EventHooks;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.greatorator.tolkienmobs.block.custom.CamoSpawnerBlock.LIT;

public abstract class TolkienBaseSpawner extends BaseSpawner implements IOwnedSpawner {
    private final CamoSpawnerBlockEntity spawnerBlockEntity;
    public static final String SPAWN_DATA_TAG = "SpawnData";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int EVENT_SPAWN = 1;
//    private int spawnDelay = 20;
    private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
    @Nullable
    private SpawnData nextSpawnData;
    private double spin;
    private double oSpin;
    @Nullable
    private Entity displayEntity;

    public TolkienBaseSpawner(CamoSpawnerBlockEntity blockEntity) {
        this.spawnerBlockEntity = blockEntity;
    }

    public void setEntityId(EntityType<?> type, @Nullable Level level, RandomSource random, BlockPos pos) {
        this.getOrCreateNextSpawnData(level, random, pos).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
    }

    private boolean isNearPlayer(Level level, BlockPos pos) {
        return spawnerBlockEntity.getSpawnerSettings().requirePlayer && level.hasNearbyAlivePlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)spawnerBlockEntity.getSpawnerRanges().activationRange);
    }

    public void clientTick(Level level, BlockPos pos) {
        if (!spawnerBlockEntity.getSpawnerSettings().redstoneControl) {
            if (this.spawnerBlockEntity.getBlockEntity().getBlockState().getValue(LIT)) {
                if (!this.isNearPlayer(level, pos)) {
                    this.oSpin = this.spin;
                } else if (this.displayEntity != null && spawnerBlockEntity.getSpawnerSettings().spawnerParticles) {
                    RandomSource randomsource = level.getRandom();
                    double d0 = (double) pos.getX() + randomsource.nextDouble();
                    double d1 = (double) pos.getY() + randomsource.nextDouble();
                    double d2 = (double) pos.getZ() + randomsource.nextDouble();
                    level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
                    level.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
                    if (spawnerBlockEntity.getSpawnerDelays().spawnDelay > 0) {
                        --spawnerBlockEntity.getSpawnerDelays().spawnDelay;
                    }

                    this.oSpin = this.spin;
                    this.spin = (this.spin + (double) (1000.0F / ((float) spawnerBlockEntity.getSpawnerDelays().spawnDelay + 200.0F))) % 360.0;
                }
            }
        }else if(!this.isNearPlayer(level, pos)) {
            this.oSpin = this.spin;
        } else if (this.displayEntity != null && spawnerBlockEntity.getSpawnerSettings().spawnerParticles) {
            RandomSource randomsource = level.getRandom();
            double d0 = (double) pos.getX() + randomsource.nextDouble();
            double d1 = (double) pos.getY() + randomsource.nextDouble();
            double d2 = (double) pos.getZ() + randomsource.nextDouble();
            level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
            if (spawnerBlockEntity.getSpawnerDelays().spawnDelay > 0) {
                --spawnerBlockEntity.getSpawnerDelays().spawnDelay;
            }

            this.oSpin = this.spin;
            this.spin = (this.spin + (double) (1000.0F / ((float) spawnerBlockEntity.getSpawnerDelays().spawnDelay + 200.0F))) % 360.0;
        }
    }

    public void serverTick(ServerLevel serverLevel, BlockPos pos) {
        if (!spawnerBlockEntity.getSpawnerSettings().redstoneControl) {
            if (this.spawnerBlockEntity.getBlockEntity().getBlockState().getValue(LIT)) {
                if (this.isNearPlayer(serverLevel, pos) || !spawnerBlockEntity.getSpawnerSettings().requirePlayer) {
                    if (spawnerBlockEntity.getSpawnerDelays().spawnDelay == -1) {
                        this.delay(serverLevel, pos);
                    }

                    if (spawnerBlockEntity.getSpawnerDelays().spawnDelay > 0) {
                        --spawnerBlockEntity.getSpawnerDelays().spawnDelay;
                    } else {
                        boolean flag = false;
                        RandomSource randomsource = serverLevel.getRandom();
                        SpawnData spawndata = this.getOrCreateNextSpawnData(serverLevel, randomsource, pos);
                        int i = 0;

                        while (true) {
                            if (i >= spawnerBlockEntity.getSpawnerRanges().spawnCount) {
                                if (flag) {
                                    this.delay(serverLevel, pos);
                                }
                                break;
                            }

                            CompoundTag compoundtag = spawndata.getEntityToSpawn();
                            Optional<EntityType<?>> optional = EntityType.by(compoundtag);
                            if (optional.isEmpty()) {
                                this.delay(serverLevel, pos);
                                return;
                            }

                            ListTag listtag = compoundtag.getList("Pos", 6);
                            int j = listtag.size();
                            double d0 = j >= 1 ? listtag.getDouble(0) : (double) pos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) spawnerBlockEntity.getSpawnerRanges().clusterRange + 0.5;
                            double d1 = j >= 2 ? listtag.getDouble(1) : (double) (pos.getY() + randomsource.nextInt(3) - 1);
                            double d2 = j >= 3 ? listtag.getDouble(2) : (double) pos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) spawnerBlockEntity.getSpawnerRanges().clusterRange + 0.5;
                            if (serverLevel.noCollision(((EntityType) optional.get()).getSpawnAABB(d0, d1, d2))) {
                                label105:
                                {
                                    BlockPos blockpos = BlockPos.containing(d0, d1, d2);
                                    if (spawndata.getCustomSpawnRules().isPresent()) {
                                        if (!((EntityType) optional.get()).getCategory().isFriendly() && serverLevel.getDifficulty() == Difficulty.PEACEFUL) {
                                            break label105;
                                        }

                                        SpawnData.CustomSpawnRules spawndata$customspawnrules = (SpawnData.CustomSpawnRules) spawndata.getCustomSpawnRules().get();
                                        if (!spawndata$customspawnrules.isValidPosition(blockpos, serverLevel)) {
                                            break label105;
                                        }
                                    } else if (!SpawnPlacements.checkSpawnRules((EntityType) optional.get(), serverLevel, MobSpawnType.SPAWNER, blockpos, serverLevel.getRandom())) {
                                        break label105;
                                    }

                                    Entity entity = EntityType.loadEntityRecursive(compoundtag, serverLevel, (p_151310_) -> {
                                        p_151310_.moveTo(d0, d1, d2, p_151310_.getYRot(), p_151310_.getXRot());
                                        return p_151310_;
                                    });
                                    if (entity == null) {
                                        this.delay(serverLevel, pos);
                                        return;
                                    }

                                    int k = serverLevel.getEntities(EntityTypeTest.forExactClass(entity.getClass()), (new AABB((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double) (pos.getX() + 1), (double) (pos.getY() + 1), (double) (pos.getZ() + 1))).inflate((double) spawnerBlockEntity.getSpawnerRanges().clusterRange), EntitySelector.NO_SPECTATORS).size();
                                    if (k >= spawnerBlockEntity.getSpawnerRanges().maxCluster) {
                                        this.delay(serverLevel, pos);
                                        return;
                                    }

                                    entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), randomsource.nextFloat() * 360.0F, 0.0F);
                                    if (entity instanceof Mob) {
                                        Mob mob = (Mob) entity;
                                        if (!EventHooks.checkSpawnPositionSpawner(mob, serverLevel, MobSpawnType.SPAWNER, spawndata, this)) {
                                            break label105;
                                        }

                                        boolean flag1 = spawndata.getEntityToSpawn().size() == 1 && spawndata.getEntityToSpawn().contains("id", 8);
                                        EventHooks.finalizeMobSpawnSpawner(mob, serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, (SpawnGroupData) null, this, flag1);
                                        Optional var10000 = spawndata.getEquipment();
                                        Objects.requireNonNull(mob);
//                                var10000.ifPresent(mob::equip);
                                    }

                                    if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
                                        this.delay(serverLevel, pos);
                                        return;
                                    }

                                    serverLevel.levelEvent(2004, pos, 0);
                                    serverLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);
                                    if (entity instanceof Mob) {
                                        ((Mob) entity).spawnAnim();
                                    }

                                    flag = true;
                                }
                            }

                            ++i;
                        }
                    }
                }
            }
        }else if (this.isNearPlayer(serverLevel, pos) || !spawnerBlockEntity.getSpawnerSettings().requirePlayer) {
            if (spawnerBlockEntity.getSpawnerDelays().spawnDelay == -1) {
                this.delay(serverLevel, pos);
            }

            if (spawnerBlockEntity.getSpawnerDelays().spawnDelay > 0) {
                --spawnerBlockEntity.getSpawnerDelays().spawnDelay;
            } else {
                boolean flag = false;
                RandomSource randomsource = serverLevel.getRandom();
                SpawnData spawndata = this.getOrCreateNextSpawnData(serverLevel, randomsource, pos);
                int i = 0;

                while (true) {
                    if (i >= spawnerBlockEntity.getSpawnerRanges().spawnCount) {
                        if (flag) {
                            this.delay(serverLevel, pos);
                        }
                        break;
                    }

                    CompoundTag compoundtag = spawndata.getEntityToSpawn();
                    Optional<EntityType<?>> optional = EntityType.by(compoundtag);
                    if (optional.isEmpty()) {
                        this.delay(serverLevel, pos);
                        return;
                    }

                    ListTag listtag = compoundtag.getList("Pos", 6);
                    int j = listtag.size();
                    double d0 = j >= 1 ? listtag.getDouble(0) : (double) pos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) spawnerBlockEntity.getSpawnerRanges().clusterRange + 0.5;
                    double d1 = j >= 2 ? listtag.getDouble(1) : (double) (pos.getY() + randomsource.nextInt(3) - 1);
                    double d2 = j >= 3 ? listtag.getDouble(2) : (double) pos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) spawnerBlockEntity.getSpawnerRanges().clusterRange + 0.5;
                    if (serverLevel.noCollision(((EntityType) optional.get()).getSpawnAABB(d0, d1, d2))) {
                        label105:
                        {
                            BlockPos blockpos = BlockPos.containing(d0, d1, d2);
                            if (spawndata.getCustomSpawnRules().isPresent()) {
                                if (!((EntityType) optional.get()).getCategory().isFriendly() && serverLevel.getDifficulty() == Difficulty.PEACEFUL) {
                                    break label105;
                                }

                                SpawnData.CustomSpawnRules spawndata$customspawnrules = (SpawnData.CustomSpawnRules) spawndata.getCustomSpawnRules().get();
                                if (!spawndata$customspawnrules.isValidPosition(blockpos, serverLevel)) {
                                    break label105;
                                }
                            } else if (!SpawnPlacements.checkSpawnRules((EntityType) optional.get(), serverLevel, MobSpawnType.SPAWNER, blockpos, serverLevel.getRandom())) {
                                break label105;
                            }

                            Entity entity = EntityType.loadEntityRecursive(compoundtag, serverLevel, (p_151310_) -> {
                                p_151310_.moveTo(d0, d1, d2, p_151310_.getYRot(), p_151310_.getXRot());
                                return p_151310_;
                            });
                            if (entity == null) {
                                this.delay(serverLevel, pos);
                                return;
                            }

                            int k = serverLevel.getEntities(EntityTypeTest.forExactClass(entity.getClass()), (new AABB((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double) (pos.getX() + 1), (double) (pos.getY() + 1), (double) (pos.getZ() + 1))).inflate((double) spawnerBlockEntity.getSpawnerRanges().clusterRange), EntitySelector.NO_SPECTATORS).size();
                            if (k >= spawnerBlockEntity.getSpawnerRanges().maxCluster) {
                                this.delay(serverLevel, pos);
                                return;
                            }

                            entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), randomsource.nextFloat() * 360.0F, 0.0F);
                            if (entity instanceof Mob) {
                                Mob mob = (Mob) entity;
                                if (!EventHooks.checkSpawnPositionSpawner(mob, serverLevel, MobSpawnType.SPAWNER, spawndata, this)) {
                                    break label105;
                                }

                                boolean flag1 = spawndata.getEntityToSpawn().size() == 1 && spawndata.getEntityToSpawn().contains("id", 8);
                                EventHooks.finalizeMobSpawnSpawner(mob, serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, (SpawnGroupData) null, this, flag1);
                                Optional var10000 = spawndata.getEquipment();
                                Objects.requireNonNull(mob);
//                                var10000.ifPresent(mob::equip);
                            }

                            if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
                                this.delay(serverLevel, pos);
                                return;
                            }

                            serverLevel.levelEvent(2004, pos, 0);
                            serverLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);
                            if (entity instanceof Mob) {
                                ((Mob) entity).spawnAnim();
                            }

                            flag = true;
                        }
                    }

                    ++i;
                }
            }
        }
    }

    private void delay(Level level, BlockPos pos) {
        RandomSource randomsource = level.random;
        if (spawnerBlockEntity.getSpawnerDelays().maxSpawnDelay <= spawnerBlockEntity.getSpawnerDelays().minSpawnDelay) {
            spawnerBlockEntity.getSpawnerDelays().spawnDelay = spawnerBlockEntity.getSpawnerDelays().minSpawnDelay;
        } else {
            spawnerBlockEntity.getSpawnerDelays().spawnDelay = spawnerBlockEntity.getSpawnerDelays().minSpawnDelay + randomsource.nextInt(spawnerBlockEntity.getSpawnerDelays().maxSpawnDelay - spawnerBlockEntity.getSpawnerDelays().minSpawnDelay);
        }

        this.spawnPotentials.getRandom(randomsource).ifPresent((p_337965_) -> {
            this.setNextSpawnData(level, pos, (SpawnData)p_337965_.data());
        });
        this.broadcastEvent(level, pos, 1);
    }

    public void load(@Nullable Level level, BlockPos pos, CompoundTag tag) {
        spawnerBlockEntity.getSpawnerDelays().spawnDelay = tag.getShort("spawnDelay");
        boolean flag = tag.contains("SpawnData", 10);
        if (flag) {
            SpawnData spawndata = (SpawnData)SpawnData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("SpawnData")).resultOrPartial((p_186391_) -> {
                LOGGER.warn("Invalid SpawnData: {}", p_186391_);
            }).orElseGet(SpawnData::new);
            this.setNextSpawnData(level, pos, spawndata);
        }

        boolean flag1 = tag.contains("SpawnPotentials", 9);
        if (flag1) {
            ListTag listtag = tag.getList("SpawnPotentials", 10);
            this.spawnPotentials = (SimpleWeightedRandomList)SpawnData.LIST_CODEC.parse(NbtOps.INSTANCE, listtag).resultOrPartial((p_186388_) -> {
                LOGGER.warn("Invalid SpawnPotentials list: {}", p_186388_);
            }).orElseGet(SimpleWeightedRandomList::empty);
        } else {
            this.spawnPotentials = SimpleWeightedRandomList.single(this.nextSpawnData != null ? this.nextSpawnData : new SpawnData());
        }

        if (tag.contains("MinSpawnDelay", 99)) {
            spawnerBlockEntity.getSpawnerDelays().minSpawnDelay = tag.getShort("minSpawnDelay");
            spawnerBlockEntity.getSpawnerDelays().maxSpawnDelay = tag.getShort("maxSpawnDelay");
            spawnerBlockEntity.getSpawnerRanges().spawnCount = tag.getShort("spawnCount");
        }

        if (tag.contains("MaxNearbyEntities", 99)) {
            spawnerBlockEntity.getSpawnerRanges().maxCluster = tag.getShort("maxCluster");
            spawnerBlockEntity.getSpawnerRanges().activationRange = tag.getShort("activationRange");
        }

        if (tag.contains("clusterRange", 99)) {
            spawnerBlockEntity.getSpawnerRanges().clusterRange = tag.getShort("clusterRange");
        }

        this.displayEntity = null;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putShort("spawnDelay", (short)spawnerBlockEntity.getSpawnerDelays().spawnDelay);
        tag.putShort("minSpawnDelay", (short)spawnerBlockEntity.getSpawnerDelays().minSpawnDelay);
        tag.putShort("maxSpawnDelay", (short)spawnerBlockEntity.getSpawnerDelays().maxSpawnDelay);
        tag.putShort("spawnCount", (short)spawnerBlockEntity.getSpawnerRanges().spawnCount);
        tag.putShort("maxCluster", (short)spawnerBlockEntity.getSpawnerRanges().maxCluster);
        tag.putShort("activationRange", (short)spawnerBlockEntity.getSpawnerRanges().activationRange);
        tag.putShort("clusterRange", (short)spawnerBlockEntity.getSpawnerRanges().clusterRange);
        if (this.nextSpawnData != null) {
            tag.put("SpawnData", (Tag)SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, this.nextSpawnData).getOrThrow((p_337966_) -> {
                return new IllegalStateException("Invalid SpawnData: " + p_337966_);
            }));
        }

        tag.put("SpawnPotentials", (Tag)SpawnData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).getOrThrow());
        return tag;
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(Level level, BlockPos pos) {
        if (this.displayEntity == null) {
            CompoundTag compoundtag = this.getOrCreateNextSpawnData(level, level.getRandom(), pos).getEntityToSpawn();
            if (!compoundtag.contains("id", 8)) {
                return null;
            }

            this.displayEntity = EntityType.loadEntityRecursive(compoundtag, level, Function.identity());
            if (compoundtag.size() == 1 && this.displayEntity instanceof Mob) {
            }
        }

        return this.displayEntity;
    }

    public boolean onEventTriggered(Level level, int id) {
        if (id == 1) {
            if (level.isClientSide) {
                spawnerBlockEntity.getSpawnerDelays().spawnDelay = spawnerBlockEntity.getSpawnerDelays().minSpawnDelay;
            }

            return true;
        } else {
            return false;
        }
    }

    protected void setNextSpawnData(@Nullable Level level, BlockPos pos, SpawnData nextSpawnData) {
        this.nextSpawnData = nextSpawnData;
    }

    private SpawnData getOrCreateNextSpawnData(@Nullable Level level, RandomSource random, BlockPos pos) {
        if (this.nextSpawnData == null) {
            this.setNextSpawnData(level, pos, (SpawnData) this.spawnPotentials.getRandom(random).map(WeightedEntry.Wrapper::data).orElseGet(SpawnData::new));
        }
        return this.nextSpawnData;
    }

    public abstract void broadcastEvent(Level var1, BlockPos var2, int var3);

    public double getSpin() {
        return this.spin;
    }

    public double getoSpin() {
        return this.oSpin;
    }

    public @Nullable Either<BlockEntity, Entity> getOwner() {
        return null;
    }
}
