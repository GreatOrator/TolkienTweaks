package com.greatorator.tolkienmobs.entity.item;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienWoodTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CulumaldaBoatEntity extends Boat {
    private static final EntityDataAccessor<String> WOOD_TYPE = SynchedEntityData.defineId(CulumaldaBoatEntity.class, EntityDataSerializers.STRING);

    public CulumaldaBoatEntity(EntityType<? extends CulumaldaBoatEntity> type, Level level) {
        super(type, level, TolkienItems.CULUMALDA_BOAT);
    }

    public CulumaldaBoatEntity(Level level, double x, double y, double z, String woodType) {
        this(TolkienEntities.CULUMALDA_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.entityData.set(WOOD_TYPE, woodType);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WOOD_TYPE, TolkienWoodTypes.CULUMALDA.name());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Type", this.getWoodType());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setWoodType(input.getStringOr("Type", ""));
    }

    public String getWoodType() {
        return this.entityData.get(WOOD_TYPE);
    }

    public void setWoodType(String woodType) {
        this.entityData.set(WOOD_TYPE, woodType);
    }
}
