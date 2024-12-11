package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.SummaryWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Quantiles;
import io.prometheus.metrics.model.snapshots.Unit;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.List;

public class JvmGarbageCollectorMetrics {
    private static final String JVM_GC_COLLECTION_SECONDS = "jvm_gc_collection_seconds";
    private final PrometheusProperties config;
    private final List<GarbageCollectorMXBean> garbageCollectorBeans;

    private JvmGarbageCollectorMetrics(List<GarbageCollectorMXBean> garbageCollectorBeans, PrometheusProperties config) {
        this.config = config;
        this.garbageCollectorBeans = garbageCollectorBeans;
    }

    private void register(PrometheusRegistry registry) {
        ((SummaryWithCallback.Builder)((SummaryWithCallback.Builder)((SummaryWithCallback.Builder)((SummaryWithCallback.Builder)SummaryWithCallback.builder(this.config).name("jvm_gc_collection_seconds")).help("Time spent in a given JVM garbage collector in seconds.")).unit(Unit.SECONDS)).labelNames(new String[]{"gc"})).callback((callback) -> {
            Iterator var2 = this.garbageCollectorBeans.iterator();

            while(var2.hasNext()) {
                GarbageCollectorMXBean gc = (GarbageCollectorMXBean)var2.next();
                callback.call(gc.getCollectionCount(), Unit.millisToSeconds(gc.getCollectionTime()), Quantiles.EMPTY, new String[]{gc.getName()});
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
        private List<GarbageCollectorMXBean> garbageCollectorBeans;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder garbageCollectorBeans(List<GarbageCollectorMXBean> garbageCollectorBeans) {
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

            (new JvmGarbageCollectorMetrics(garbageCollectorBeans, this.config)).register(registry);
        }
    }
}
