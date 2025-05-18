package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.handler.interfaces.block.IProcess;
import com.greatorator.tolkienmobs.util.CrashLock;
import com.greatorator.tolkienmobs.util.TolkienProfiler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessHandlerClient {
    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    private static List<IProcess> processes = new ArrayList<IProcess>();
    private static List<IProcess> newProcesses = new ArrayList<IProcess>();

    private static List<IProcess> persistentProcesses = new ArrayList<IProcess>();
    private static List<IProcess> newPersistentProcesses = new ArrayList<IProcess>();

    public static void init() {
        LOCK.lock();
        NeoForge.EVENT_BUS.register(new ProcessHandlerClient());
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {
        TolkienProfiler.TICK.start("process_handler");
        while (!syncTasks.isEmpty()) {
            syncTasks.poll().run();
        }

        Iterator<IProcess> i = processes.iterator();
        while (i.hasNext()) {
            IProcess process = i.next();
            if (process.isDead()) {
                i.remove();
            } else {
                process.updateProcess();
            }
        }

        if (!newProcesses.isEmpty()) {
            processes.addAll(newProcesses);
            newProcesses.clear();
        }


        i = persistentProcesses.iterator();
        while (i.hasNext()) {
            IProcess process = i.next();
            if (process.isDead()) {
                i.remove();
            } else {
                process.updateProcess();
            }
        }

        if (!newPersistentProcesses.isEmpty()) {
            persistentProcesses.addAll(newPersistentProcesses);
            newPersistentProcesses.clear();
        }
        TolkienProfiler.TICK.stop();
    }

    @SubscribeEvent
    public void onWorldClose(LevelEvent.Unload event) {
        processes.clear();
        newProcesses.clear();
    }

    public static void addProcess(IProcess process) {
        newProcesses.add(process);
    }

    /**
     * Adds a new process that will not be removed when the world is closed.
     */
    public static void addPersistentProcess(IProcess process) {
        newPersistentProcesses.add(process);
    }

    private static final Queue<Runnable> syncTasks = new ConcurrentLinkedQueue<>();

    public static void syncTask(Runnable task) {
        syncTasks.add(task);
    }
}