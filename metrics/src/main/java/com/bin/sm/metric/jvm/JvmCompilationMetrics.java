package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.CounterWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Unit;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

public class JvmCompilationMetrics {
    private static final String JVM_COMPILATION_TIME_SECONDS_TOTAL = "jvm_compilation_time_seconds_total";
    private final PrometheusProperties config;
    private final CompilationMXBean compilationBean;

    private JvmCompilationMetrics(CompilationMXBean compilationBean, PrometheusProperties config) {
        this.compilationBean = compilationBean;
        this.config = config;
    }

    private void register(PrometheusRegistry registry) {
        if (this.compilationBean != null && this.compilationBean.isCompilationTimeMonitoringSupported()) {
            ((CounterWithCallback.Builder)((CounterWithCallback.Builder)CounterWithCallback.builder(this.config).name("jvm_compilation_time_seconds_total").help("The total time in seconds taken for HotSpot class compilation")).unit(Unit.SECONDS)).callback((callback) -> {
                callback.call(Unit.millisToSeconds(this.compilationBean.getTotalCompilationTime()), new String[0]);
            }).register(registry);
        }
    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    public static class Builder {
        private final PrometheusProperties config;
        private CompilationMXBean compilationBean;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder compilationBean(CompilationMXBean compilationBean) {
            this.compilationBean = compilationBean;
            return this;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            CompilationMXBean compilationBean = this.compilationBean != null ? this.compilationBean : ManagementFactory.getCompilationMXBean();
            (new JvmCompilationMetrics(compilationBean, this.config)).register(registry);
        }
    }
}
