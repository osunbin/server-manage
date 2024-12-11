package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Unit;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.List;

public class JvmBufferPoolMetrics {
    private static final String JVM_BUFFER_POOL_USED_BYTES = "jvm_buffer_pool_used_bytes";
    private static final String JVM_BUFFER_POOL_CAPACITY_BYTES = "jvm_buffer_pool_capacity_bytes";
    private static final String JVM_BUFFER_POOL_USED_BUFFERS = "jvm_buffer_pool_used_buffers";
    private final PrometheusProperties config;
    private final List<BufferPoolMXBean> bufferPoolBeans;

    private JvmBufferPoolMetrics(List<BufferPoolMXBean> bufferPoolBeans, PrometheusProperties config) {
        this.config = config;
        this.bufferPoolBeans = bufferPoolBeans;
    }

    private void register(PrometheusRegistry registry) {
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_buffer_pool_used_bytes")).help("Used bytes of a given JVM buffer pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback((callback) -> {
            Iterator var2 = this.bufferPoolBeans.iterator();

            while(var2.hasNext()) {
                BufferPoolMXBean pool = (BufferPoolMXBean)var2.next();
                callback.call((double)pool.getMemoryUsed(), new String[]{pool.getName()});
            }

        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_buffer_pool_capacity_bytes")).help("Bytes capacity of a given JVM buffer pool.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback((callback) -> {
            Iterator var2 = this.bufferPoolBeans.iterator();

            while(var2.hasNext()) {
                BufferPoolMXBean pool = (BufferPoolMXBean)var2.next();
                callback.call((double)pool.getTotalCapacity(), new String[]{pool.getName()});
            }

        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_buffer_pool_used_buffers")).help("Used buffers of a given JVM buffer pool.")).labelNames(new String[]{"pool"})).callback((callback) -> {
            Iterator var2 = this.bufferPoolBeans.iterator();

            while(var2.hasNext()) {
                BufferPoolMXBean pool = (BufferPoolMXBean)var2.next();
                callback.call((double)pool.getCount(), new String[]{pool.getName()});
            }

        }).register(registry);
    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    public static class Builder {
        private final PrometheusProperties config;
        private List<BufferPoolMXBean> bufferPoolBeans;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder bufferPoolBeans(List<BufferPoolMXBean> bufferPoolBeans) {
            this.bufferPoolBeans = bufferPoolBeans;
            return this;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            List<BufferPoolMXBean> bufferPoolBeans = this.bufferPoolBeans;
            if (bufferPoolBeans == null) {
                bufferPoolBeans = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
            }

            (new JvmBufferPoolMetrics(bufferPoolBeans, this.config)).register(registry);
        }
    }
}