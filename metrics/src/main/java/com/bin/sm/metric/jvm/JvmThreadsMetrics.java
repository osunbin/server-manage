package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.CounterWithCallback;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JvmThreadsMetrics {
    private static final String UNKNOWN = "UNKNOWN";
    private static final String JVM_THREADS_STATE = "jvm_threads_state";
    private static final String JVM_THREADS_CURRENT = "jvm_threads_current";
    private static final String JVM_THREADS_DAEMON = "jvm_threads_daemon";
    private static final String JVM_THREADS_PEAK = "jvm_threads_peak";
    private static final String JVM_THREADS_STARTED_TOTAL = "jvm_threads_started_total";
    private static final String JVM_THREADS_DEADLOCKED = "jvm_threads_deadlocked";
    private static final String JVM_THREADS_DEADLOCKED_MONITOR = "jvm_threads_deadlocked_monitor";
    private final PrometheusProperties config;
    private final ThreadMXBean threadBean;
    private final boolean isNativeImage;

    private JvmThreadsMetrics(boolean isNativeImage, ThreadMXBean threadBean, PrometheusProperties config) {
        this.config = config;
        this.threadBean = threadBean;
        this.isNativeImage = isNativeImage;
    }

    private void register(PrometheusRegistry registry) {
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_threads_current")).help("Current thread count of a JVM")).callback((callback) -> {
            callback.call((double)this.threadBean.getThreadCount(), new String[0]);
        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_threads_daemon")).help("Daemon thread count of a JVM")).callback((callback) -> {
            callback.call((double)this.threadBean.getDaemonThreadCount(), new String[0]);
        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_threads_peak")).help("Peak thread count of a JVM")).callback((callback) -> {
            callback.call((double)this.threadBean.getPeakThreadCount(), new String[0]);
        }).register(registry);
        ((CounterWithCallback.Builder)CounterWithCallback.builder(this.config).name("jvm_threads_started_total").help("Started thread count of a JVM")).callback((callback) -> {
            callback.call((double)this.threadBean.getTotalStartedThreadCount(), new String[0]);
        }).register(registry);
        if (!this.isNativeImage) {
            ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_threads_deadlocked")).help("Cycles of JVM-threads that are in deadlock waiting to acquire object monitors or ownable synchronizers")).callback((callback) -> {
                callback.call(this.nullSafeArrayLength(this.threadBean.findDeadlockedThreads()), new String[0]);
            }).register(registry);
            ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_threads_deadlocked_monitor")).help("Cycles of JVM-threads that are in deadlock waiting to acquire object monitors")).callback((callback) -> {
                callback.call(this.nullSafeArrayLength(this.threadBean.findMonitorDeadlockedThreads()), new String[0]);
            }).register(registry);
            ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_threads_state")).help("Current count of threads by state")).labelNames(new String[]{"state"})).callback((callback) -> {
                Map<String, Integer> threadStateCounts = this.getThreadStateCountMap(this.threadBean);
                Iterator var3 = threadStateCounts.entrySet().iterator();

                while(var3.hasNext()) {
                    Map.Entry<String, Integer> entry = (Map.Entry)var3.next();
                    callback.call((double)(Integer)entry.getValue(), new String[]{(String)entry.getKey()});
                }

            }).register(registry);
        }

    }

    private Map<String, Integer> getThreadStateCountMap(ThreadMXBean threadBean) {
        long[] threadIds = threadBean.getAllThreadIds();
        int writePos = 0;

        int numberOfInvalidThreadIds;
        for(numberOfInvalidThreadIds = 0; numberOfInvalidThreadIds < threadIds.length; ++numberOfInvalidThreadIds) {
            if (threadIds[numberOfInvalidThreadIds] > 0L) {
                threadIds[writePos++] = threadIds[numberOfInvalidThreadIds];
            }
        }

        numberOfInvalidThreadIds = threadIds.length - writePos;
        threadIds = Arrays.copyOf(threadIds, writePos);
        ThreadInfo[] allThreads = threadBean.getThreadInfo(threadIds, 0);
        HashMap<String, Integer> threadCounts = new HashMap();
        Thread.State[] var7 = State.values();
        int var8 = var7.length;

        int var9;
        for(var9 = 0; var9 < var8; ++var9) {
            Thread.State state = var7[var9];
            threadCounts.put(state.name(), 0);
        }

        ThreadInfo[] var12 = allThreads;
        var8 = allThreads.length;

        for(var9 = 0; var9 < var8; ++var9) {
            ThreadInfo curThread = var12[var9];
            if (curThread != null) {
                Thread.State threadState = curThread.getThreadState();
                threadCounts.put(threadState.name(), (Integer)threadCounts.get(threadState.name()) + 1);
            }
        }

        threadCounts.put("UNKNOWN", numberOfInvalidThreadIds);
        return threadCounts;
    }

    private double nullSafeArrayLength(long[] array) {
        return null == array ? 0.0 : (double)array.length;
    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    public static class Builder {
        private final PrometheusProperties config;
        private Boolean isNativeImage;
        private ThreadMXBean threadBean;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder threadBean(ThreadMXBean threadBean) {
            this.threadBean = threadBean;
            return this;
        }

        Builder isNativeImage(boolean isNativeImage) {
            this.isNativeImage = isNativeImage;
            return this;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            ThreadMXBean threadBean = this.threadBean != null ? this.threadBean : ManagementFactory.getThreadMXBean();
            boolean isNativeImage = this.isNativeImage != null ? this.isNativeImage : NativeImageChecker.isGraalVmNativeImage;
            (new JvmThreadsMetrics(isNativeImage, threadBean, this.config)).register(registry);
        }
    }
}
