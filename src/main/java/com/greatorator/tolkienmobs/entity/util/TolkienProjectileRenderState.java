package com.greatorator.tolkienmobs.entity.util;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.Map;

/**
 * The render state the mod's GeckoLib projectiles use.
 * <p>
 * {@link TolkienGeoRenderState} is a {@code LivingEntityRenderState}, and GeckoLib fills that half from
 * the entity by casting it to a living one, so a projectile - which is a plain {@link net.minecraft.world.entity.Entity} -
 * cannot use it. This holds the same GeckoLib data map on the plain entity state and adds the two
 * timers the web trap and the tornado scale themselves from, which used to be read straight off the
 * entity in {@code preRender}.
 */
public class TolkienProjectileRenderState extends EntityRenderState implements GeoRenderState {
    private final Map<DataTicket<?>, Object> geckolibData = new Reference2ObjectOpenHashMap<>();

    /** Ticks the projectile has existed, set by the renderer during extraction. */
    public int lifeTime;

    /** Whether the tornado is the blast that leaves the caster's hand rather than a swirl on the ground. */
    public boolean blast;

    /** The projectile's yaw in degrees, which the blast turns its funnel by. */
    public float yRot;

    @Override
    public <D> void addGeckolibData(DataTicket<D> dataTicket, D data) {
        this.geckolibData.put(dataTicket, data);
    }

    @Override
    public boolean hasGeckolibData(DataTicket<?> dataTicket) {
        return this.geckolibData.containsKey(dataTicket);
    }

    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return this.geckolibData;
    }
}
