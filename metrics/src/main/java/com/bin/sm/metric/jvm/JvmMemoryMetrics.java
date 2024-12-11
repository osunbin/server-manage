package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Unit;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class JvmMemoryMetrics {
    private static final String JVM_MEMORY_OBJECTS_PENDING_FINALIZATION = "jvm_memory_objects_pending_finalization";
    private static final String JVM_MEMORY_USED_BYTES = "jvm_memory_used_bytes";
    private static final String JVM_MEMORY_COMMITTED_BYTES = "jvm_memory_committed_bytes";
    private static final String JVM_MEMORY_MAX_BYTES = "jvm_memory_max_bytes";
    private static final String JVM_MEMORY_INIT_BYTES = "jvm_memory_init_bytes";
    private static final String JVM_MEMORY_POOL_USED_BYTES = "jvm_memory_pool_used_bytes";
    private static final String JVM_MEMORY_POOL_COMMITTED_BYTES = "jvm_memory_pool_committed_bytes";
    private static final String JVM_MEMORY_POOL_MAX_BYTES = "jvm_memory_pool_max_bytes";
    private static final String JVM_MEMORY_POOL_INIT_BYTES = "jvm_memory_pool_init_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_USED_BYTES = "jvm_memory_pool_collection_used_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_COMMITTED_BYTES = "jvm_memory_pool_collection_committed_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_MAX_BYTES = "jvm_memory_pool_collection_max_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_INIT_BYTES = "jvm_memory_pool_collection_init_bytes";
    private final PrometheusProperties config;
    private final MemoryMXBean memoryBean;
    private final List<MemoryPoolMXBean> poolBeans;

    private JvmMemoryMetrics(List<MemoryPoolMXBean> poolBeans, MemoryMXBean memoryBean, PrometheusProperties config) {
        this.config = config;
        this.poolBeans = poolBeans;
        this.memoryBean = memoryBean;
    }

    private void register(PrometheusRegistry registry) {
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_objects_pending_finalization")).help("The number of objects waiting in the finalizer queue.")).callback((callback) -> {
            callback.call((double)this.memoryBean.getObjectPendingFinalizationCount(), new String[0]);
        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_used_bytes")).help("Used bytes of a given JVM memory area.")).unit(Unit.BYTES)).labelNames(new String[]{"area"})).callback((callback) -> {
            callback.call((double)this.memoryBean.getHeapMemoryUsage().getUsed(), new String[]{"heap"});
            callback.call((double)this.memoryBean.getNonHeapMemoryUsage().getUsed(), new String[]{"nonheap"});
        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_committed_bytes")).help("Committed (bytes) of a given JVM memory area.")).unit(Unit.BYTES)).labelNames(new String[]{"area"})).callback((callback) -> {
            callback.call((double)this.memoryBean.getHeapMemoryUsage().getCommitted(), new String[]{"heap"});
            callback.call((double)this.memoryBean.getNonHeapMemoryUsage().getCommitted(), new String[]{"nonheap"});
        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_max_bytes")).help("Max (bytes) of a given JVM memory area.")).unit(Unit.BYTES)).labelNames(new String[]{"area"})).callback((callback) -> {
            callback.call((double)this.memoryBean.getHeapMemoryUsage().getMax(), new String[]{"heap"});
            callback.call((double)this.memoryBean.getNonHeapMemoryUsage().getMax(), new String[]{"nonheap"});
        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_init_bytes")).help("Initial bytes of a given JVM memory area.")).unit(Unit.BYTES)).labelNames(new String[]{"area"})).callback((callback) -> {
            callback.call((double)this.memoryBean.getHeapMemoryUsage().getInit(), new String[]{"heap"});
            callback.call((double)this.memoryBean.getNonHeapMemoryUsage().getInit(), new String[]{"nonheap"});
        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_pool_used_bytes")).help("Used bytes of a given JVM memory pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(this.poolBeans, MemoryPoolMXBean::getUsage, MemoryUsage::getUsed)).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_pool_committed_bytes")).help("Committed bytes of a given JVM memory pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(this.poolBeans, MemoryPoolMXBean::getUsage, MemoryUsage::getCommitted)).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_pool_max_bytes")).help("Max bytes of a given JVM memory pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(this.poolBeans, MemoryPoolMXBean::getUsage, MemoryUsage::getMax)).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_pool_init_bytes")).help("Initial bytes of a given JVM memory pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(this.poolBeans, MemoryPoolMXBean::getUsage, MemoryUsage::getInit)).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_pool_collection_used_bytes")).help("Used bytes after last collection of a given JVM memory pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(this.poolBeans, MemoryPoolMXBean::getCollectionUsage, MemoryUsage::getUsed)).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_pool_collection_committed_bytes")).help("Committed after last collection bytes of a given JVM memory pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(this.poolBeans, MemoryPoolMXBean::getCollectionUsage, MemoryUsage::getCommitted)).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_pool_collection_max_bytes")).help("Max bytes after last collection of a given JVM memory pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(this.poolBeans, MemoryPoolMXBean::getCollectionUsage, MemoryUsage::getMax)).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_memory_pool_collection_init_bytes")).help("Initial after last collection bytes of a given JVM memory pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(this.poolBeans, MemoryPoolMXBean::getCollectionUsage, MemoryUsage::getInit)).register(registry);
    }

    private Consumer<GaugeWithCallback.Callback> makeCallback(List<MemoryPoolMXBean> poolBeans, Function<MemoryPoolMXBean, MemoryUsage> memoryUsageFunc, Function<MemoryUsage, Long> valueFunc) {
        return (callback) -> {
            Iterator var4 = poolBeans.iterator();

            while(var4.hasNext()) {
                MemoryPoolMXBean pool = (MemoryPoolMXBean)var4.next();
                MemoryUsage poolUsage = (MemoryUsage)memoryUsageFunc.apply(pool);
                if (poolUsage != null) {
                    callback.call((double)(Long)valueFunc.apply(poolUsage), new String[]{pool.getName()});
                }
            }

        };
    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    public static class Builder {
        private final PrometheusProperties config;
        private MemoryMXBean memoryBean;
        private List<MemoryPoolMXBean> poolBeans;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder withMemoryBean(MemoryMXBean memoryBean) {
            this.memoryBean = memoryBean;
            return this;
        }

        Builder withMemoryPoolBeans(List<MemoryPoolMXBean> memoryPoolBeans) {
            this.poolBeans = memoryPoolBeans;
            return this;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            MemoryMXBean memoryMXBean = this.memoryBean != null ? this.memoryBean : ManagementFactory.getMemoryMXBean();
            List<MemoryPoolMXBean> poolBeans = this.poolBeans != null ? this.poolBeans : ManagementFactory.getMemoryPoolMXBeans();
            (new JvmMemoryMetrics(poolBeans, memoryMXBean, this.config)).register(registry);
        }
    }
}
