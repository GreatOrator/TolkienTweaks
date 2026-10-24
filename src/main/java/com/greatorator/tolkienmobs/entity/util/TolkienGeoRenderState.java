package com.greatorator.tolkienmobs.entity.util;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.Map;
import java.util.Set;

/**
 * The render state every GeckoLib-rendered mob in this mod uses.
 * <p>
 * GeckoLib 5 stopped handing the entity to models and render layers: a renderer fills a render state
 * during extraction and everything after that reads the state instead. Vanilla's own
 * {@link LivingEntityRenderState} already carries the head angles, walk animation and baby flag, so the
 * mod only needs to add what it tells apart by hand - the colour variant, which used to be read straight
 * off the entity by the model's texture getter, and the bones a mob hides depending on its state.
 * <p>
 * The data map is GeckoLib's too. GeckoLib applies {@code GeoRenderState} to vanilla's render state by
 * mixin, which the compiler cannot see, so a state class of our own has to implement the three methods
 * that route data in and out; keeping all of them on one map here is what stops the class's map and the
 * mixin's from drifting apart.
 */
public class TolkienGeoRenderState extends LivingEntityRenderState implements GeoRenderState {
    private final Map<DataTicket<?>, Object> geckolibData = new Reference2ObjectOpenHashMap<>();

    /** The mob's colour variant, set by the renderer during extraction. */
    public TolkienVariant variant = TolkienVariant.DEFAULT;

    /** Whether a human is in its winter clothing, set by the renderer during extraction. */
    public boolean winter;

    /**
     * The bones this pass must not draw, set by the renderer during extraction. 5 has no per-model
     * {@code setCustomAnimations} left, so a model that used to hide a prop bone has to say so here and let
     * the renderer hide it, and the set is replaced whole on every pass rather than added to, so a part
     * that comes back is not still hidden.
     */
    public Set<String> hiddenBones = Set.of();

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
