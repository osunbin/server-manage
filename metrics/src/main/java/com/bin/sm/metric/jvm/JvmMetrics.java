package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import java.util.concurrent.atomic.AtomicBoolean;

public class JvmMetrics {
    private static AtomicBoolean registeredWithTheDefaultRegistry = new AtomicBoolean(false);

    public JvmMetrics() {
    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    public static class Builder {
        private final PrometheusProperties config;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        public void register() {
            if (!JvmMetrics.registeredWithTheDefaultRegistry.getAndSet(true)) {
                this.register(PrometheusRegistry.defaultRegistry);
            }

        }

        public void register(PrometheusRegistry registry) {
            JvmThreadsMetrics.builder(this.config).register(registry);
            JvmBufferPoolMetrics.builder(this.config).register(registry);
            JvmClassLoadingMetrics.builder(this.config).register(registry);
            JvmCompilationMetrics.builder(this.config).register(registry);
            JvmGarbageCollectorMetrics.builder(this.config).register(registry);
            JvmMemoryPoolAllocationMetrics.builder(this.config).register(registry);
            JvmMemoryMetrics.builder(this.config).register(registry);
            JvmNativeMemoryMetrics.builder(this.config).register(registry);
            JvmRuntimeInfoMetric.builder(this.config).register(registry);
            ProcessMetrics.builder(this.config).register(registry);
        }
    }
}