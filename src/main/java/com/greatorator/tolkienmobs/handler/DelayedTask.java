package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.handler.interfaces.block.IProcess;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.neoforged.api.distmarker.Dist;

public class DelayedTask {
    @Deprecated
    public static void run(int delay, Runnable task) {
        TolkienMobsMain.proxy.runSidedProcess(new Task(delay, task));
    }

    public static void sided(int delay, Runnable task) {
        GeneralUtility.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ProcessHandlerClient.addProcess(new Task(delay, task)));
        GeneralUtility.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> ProcessHandler.addProcess(new Task(delay, task)));
    }

    public static void server(int delay, Runnable task) {
        ProcessHandler.addProcess(new Task(delay, task));
    }

    public static void client(int delay, Runnable task) {
        ProcessHandlerClient.addProcess(new Task(delay, task));
    }


    public static class Task implements IProcess {
        private int delay;
        private Runnable task;
        private boolean hasExecuted = false;

        public Task(int delay, Runnable task) {
            this.delay = delay;
            this.task = task;
        }

        @Override
        public final void updateProcess() {
            if (delay <= 0 && !hasExecuted) {
                task.run();
                hasExecuted = true;
            }
            delay--;
        }

        @Override
        public boolean isDead() {
            return hasExecuted;
        }
    }
}
