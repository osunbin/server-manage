package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.Info;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

public class JvmRuntimeInfoMetric {
    private static final String JVM_RUNTIME_INFO = "jvm_runtime_info";
    private final PrometheusProperties config;
    private final String version;
    private final String vendor;
    private final String runtime;

    private JvmRuntimeInfoMetric(String version, String vendor, String runtime, PrometheusProperties config) {
        this.config = config;
        this.version = version;
        this.vendor = vendor;
        this.runtime = runtime;
    }

    private void register(PrometheusRegistry registry) {
        Info jvmInfo = (Info)((Info.Builder)((Info.Builder)Info.builder(this.config).name("jvm_runtime_info").help("JVM runtime info")).labelNames(new String[]{"version", "vendor", "runtime"})).register(registry);
        jvmInfo.setLabelValues(new String[]{this.version, this.vendor, this.runtime});
    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    public static class Builder {
        private final PrometheusProperties config;
        private String version;
        private String vendor;
        private String runtime;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder version(String version) {
            this.version = version;
            return this;
        }

        Builder vendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        Builder runtime(String runtime) {
            this.runtime = runtime;
            return this;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            String version = this.version != null ? this.version : System.getProperty("java.runtime.version", "unknown");
            String vendor = this.vendor != null ? this.vendor : System.getProperty("java.vm.vendor", "unknown");
            String runtime = this.runtime != null ? this.runtime : System.getProperty("java.runtime.name", "unknown");
            (new JvmRuntimeInfoMetric(version, vendor, runtime, this.config)).register(registry);
        }
    }
}
