package com.greatorator.tolkienmobs.util;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;

import static net.minecraft.ChatFormatting.*;

@Deprecated
public class TolkienProfiler {
    private static final CrashLock LOCK = new CrashLock("Already Initialized.");
    public static ProfilerInstance RENDER = new ProfilerInstance("Render");
    public static ProfilerInstance TICK = new ProfilerInstance("Tick");

    public static boolean enableProfiler = false;
    public static List<String> renderDebug = new ArrayList<>();
    public static List<String> tickDebug = new ArrayList<>();

    public static void init() {
        LOCK.lock();

        NeoForge.EVENT_BUS.register(new TolkienProfiler());
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent.Post event) {
        if (!enableProfiler) {
            return;
        }

        tickDebug.clear();
        TICK.dumpDebug(tickDebug);
        TICK.update();
    }

    @SubscribeEvent
    public void renderWorldLast(RenderLevelStageEvent event) {
        if (!enableProfiler || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        renderDebug.clear();
        RENDER.dumpDebug(renderDebug);
        RENDER.update();
    }

    public static class ProfilerInstance {
        private final Map<Thread, TolkienProfiler> threadProfilers = Collections.synchronizedMap(new HashMap<>());
        private String mode;

        ProfilerInstance(String mode) {
            this.mode = mode;
        }

        public synchronized void start(String name) {
            if (!enableProfiler) return;
            Thread t = Thread.currentThread();
            threadProfilers.computeIfAbsent(t, thread -> new TolkienProfiler(thread.getName(), mode)).startNode(name);
        }

        public synchronized void stop() {
            if (!enableProfiler) return;
            Thread t = Thread.currentThread();
            TolkienProfiler p = threadProfilers.get(t);
            if (p != null) {
                p.stopNode();
            }
        }

        private void update() {
            synchronized (threadProfilers) {
                threadProfilers.entrySet().removeIf(e -> !e.getKey().isAlive());
                threadProfilers.forEach((t, p) -> p.update());
            }
        }

        private void dumpDebug(List<String> list) {
            synchronized (threadProfilers) {
                for (TolkienProfiler p: threadProfilers.values()) {
                    p.dumpTimigs(list);
                }
            }
        }
    }

    //Profiler code

    private String thread;
    private Map<String, PNode> nodes = new HashMap<>();
    private Map<String, long[]> timings = new HashMap<>();
    private PNode rootNode;
    private String mode;
    private PNode activeNode;
    private int timingIndex = 0;
    private int timingTicks = 100;

    private TolkienProfiler() {}

    private TolkienProfiler(String thread, String mode) {
        this.thread = thread;
        this.rootNode = new PNode(thread, this, null);
        this.mode = mode;
        activeNode = rootNode;
    }

    private void dumpTimigs(List<String> list) {
        rootNode.dumpTimings(list, "");
    }

    private void startNode(String name) {
        String nodeName = (activeNode == null ? "" : activeNode.name + ".") + name;
        PNode node = nodes.computeIfAbsent(nodeName, s -> new PNode(s, this, activeNode));
        activeNode = node;
        node.start();
    }

    private void stopNode() {
        if (activeNode != null) {
            activeNode.stop();
            activeNode = activeNode.parent;
        }
    }

    private void update() {
        timingIndex++;
        if (timingIndex == timingTicks) {
            timingIndex = 0;
        }

        for (long[] times: timings.values()) {
            times[timingIndex] = 0;
        }

        rootNode.update();
    }

    private void addTime(String node, long time) {
        long[] times = timings.computeIfAbsent(node, s -> new long[timingTicks]);
        times[timingIndex] += time;
    }

    private static class PNode {
        private String name;
        private TolkienProfiler profiler;
        private PNode parent;
        private List<PNode> children = new ArrayList<>();
        private long startTime = -1;
        private boolean isDead = false;

        public PNode(String name, TolkienProfiler profiler, PNode parent) {
            this.name = name;
            this.profiler = profiler;
            this.parent = parent;
            if (parent != null) {
                parent.children.add(this);
            }
        }

        private void start() {
            startTime = System.nanoTime();
        }

        private void stop() {
            profiler.addTime(name, System.nanoTime() - startTime);
        }

        private void update() {
            children.forEach(PNode::update);
            children.removeIf(PNode::checkDead);
        }

        private boolean checkDead() {
            if (isDead) {
                profiler.nodes.remove(name);
            }
            return isDead;
        }

        private void dumpTimings(List<String> list, String inset) {
            long[] times = profiler.timings.get(name);

            if (times == null) {
                list.add(inset + name + ": " + profiler.mode);
            }
            else {
                long min = Long.MAX_VALUE;
                long max = 0;
                long average = 0;
                for (long time: times) {
                    if (time < min && time > 0) min = time;
                    if (time > max) max = time;
                    average += time;
                }
                if (min == Long.MAX_VALUE) {
                    min = 0;
                }

                isDead = average == 0;

                average /= times.length;
                list.add(inset + name.replace(parent.name + ".", "") + String.format(": (%s, %s, %s)", GREEN + ((min / 1000D) + "us") + RESET, RED + ((max / 1000D) + "us") + RESET, YELLOW + ((average / 1000D) + "us") + RESET));
            }
            children.forEach(pNode -> pNode.dumpTimings(list, inset + "  "));
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PNode && ((PNode) obj).name.equals(name);
        }
    }
}