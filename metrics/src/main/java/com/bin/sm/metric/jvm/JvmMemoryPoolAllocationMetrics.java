package com.bin.sm.metric.jvm;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.datapoints.CounterDataPoint;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

public class JvmMemoryPoolAllocationMetrics {
    private static final String JVM_MEMORY_POOL_ALLOCATED_BYTES_TOTAL = "jvm_memory_pool_allocated_bytes_total";
    private final PrometheusProperties config;
    private final List<GarbageCollectorMXBean> garbageCollectorBeans;

    private JvmMemoryPoolAllocationMetrics(List<GarbageCollectorMXBean> garbageCollectorBeans, PrometheusProperties config) {
        this.garbageCollectorBeans = garbageCollectorBeans;
        this.config = config;
    }

    private void register(PrometheusRegistry registry) {
        Counter allocatedCounter = (Counter)((Counter.Builder)((Counter.Builder)Counter.builder().name("jvm_memory_pool_allocated_bytes_total").help("Total bytes allocated in a given JVM memory pool. Only updated after GC, not continuously.")).labelNames(new String[]{"pool"})).register(registry);
        AllocationCountingNotificationListener listener = new AllocationCountingNotificationListener(allocatedCounter);
        Iterator var4 = this.garbageCollectorBeans.iterator();

        while(var4.hasNext()) {
            GarbageCollectorMXBean garbageCollectorMXBean = (GarbageCollectorMXBean)var4.next();
            if (garbageCollectorMXBean instanceof NotificationEmitter) {
                ((NotificationEmitter)garbageCollectorMXBean).addNotificationListener(listener, (NotificationFilter)null, (Object)null);
            }
        }

    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    static class AllocationCountingNotificationListener implements NotificationListener {
        private final Map<String, Long> lastMemoryUsage = new HashMap();
        private final Counter counter;

        AllocationCountingNotificationListener(Counter counter) {
            this.counter = counter;
        }

        public synchronized void handleNotification(Notification notification, Object handback) {
            GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData)notification.getUserData());
            GcInfo gcInfo = info.getGcInfo();
            Map<String, MemoryUsage> memoryUsageBeforeGc = gcInfo.getMemoryUsageBeforeGc();
            Map<String, MemoryUsage> memoryUsageAfterGc = gcInfo.getMemoryUsageAfterGc();
            Iterator var7 = memoryUsageBeforeGc.entrySet().iterator();

            while(var7.hasNext()) {
                Map.Entry<String, MemoryUsage> entry = (Map.Entry)var7.next();
                String memoryPool = (String)entry.getKey();
                long before = ((MemoryUsage)entry.getValue()).getUsed();
                long after = ((MemoryUsage)memoryUsageAfterGc.get(memoryPool)).getUsed();
                this.handleMemoryPool(memoryPool, before, after);
            }

        }

        void handleMemoryPool(String memoryPool, long before, long after) {
            long last = getAndSet(this.lastMemoryUsage, memoryPool, after);
            long diff1 = before - last;
            long diff2 = after - before;
            if (diff1 < 0L) {
                diff1 = 0L;
            }

            if (diff2 < 0L) {
                diff2 = 0L;
            }

            long increase = diff1 + diff2;
            if (increase > 0L) {
                ((CounterDataPoint)this.counter.labelValues(new String[]{memoryPool})).inc(increase);
            }

        }

        private static long getAndSet(Map<String, Long> map, String key, long value) {
            Long last = (Long)map.put(key, value);
            return last == null ? 0L : last;
        }
    }

    public static class Builder {
        private final PrometheusProperties config;
        private List<GarbageCollectorMXBean> garbageCollectorBeans;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder withGarbageCollectorBeans(List<GarbageCollectorMXBean> garbageCollectorBeans) {
            this.garbageCollectorBeans = garbageCollectorBeans;
            return this;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            List<GarbageCollectorMXBean> garbageCollectorBeans = this.garbageCollectorBeans;
            if (garbageCollectorBeans == null) {
                garbageCollectorBeans = ManagementFactory.getGarbageCollectorMXBeans();
            }

            (new JvmMemoryPoolAllocationMetrics(garbageCollectorBeans, this.config)).register(registry);
        }
    }
}