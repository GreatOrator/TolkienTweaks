package com.greatorator.tolkienmobs.particle;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.entity.DragonEntity;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/**
 * The air over a diving beast, run as one sound for as long as the dive lasts. The mod's gusts are
 * about three seconds long, and the dive used to ask the server for one every twenty ticks: each
 * gust was cut off after its first second and replaced by a fresh one, and that cut is the seam
 * that was audible. Looping lets a gust run to its own end instead, and the loop follows the beast
 * and stops with the dive because it reads both off the beast each tick.
 */
public class DiveWindSound extends AbstractTickableSoundInstance
{
    private final DragonEntity entity;

    private DiveWindSound(DragonEntity entity)
    {
        super(TolkienSounds.WIND_FELLBEAST.get(), SoundSource.HOSTILE, RandomSource.create());
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.applyConfig();
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public void tick()
    {
        // A beast that has landed, been removed with its chunk, or levelled out is no longer diving,
        // and the wind has nothing left to say. The engine drops the looping sample on its own.
        if (!entity.isAlive() || entity.isRemoved() || !entity.isDiving())
        {
            stop();
            return;
        }

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.applyConfig();
    }

    /**
     * The dive's loudness and pitch, which belong to the air rather than to the beast's voice.
     * They are the mod config's, and the engine re-reads a looping sound's volume and pitch every
     * tick, so a change is heard without waiting for the next dive. This sound runs on each client,
     * so both are the listener's own settings rather than the server's.
     */
    private void applyConfig()
    {
        this.volume = (float) TolkienMobsConfig.fellbeastDiveWindVolume;
        this.pitch = (float) TolkienMobsConfig.fellbeastDiveWindPitch;
    }

    /** Starts the wind on this client. The caller only does so when the beast begins a dive. */
    public static void play(DragonEntity dragon)
    {
        Minecraft.getInstance().getSoundManager().play(new DiveWindSound(dragon));
    }
}
