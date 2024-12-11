package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.CounterWithCallback;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

public class JvmClassLoadingMetrics {
    private static final String JVM_CLASSES_CURRENTLY_LOADED = "jvm_classes_currently_loaded";
    private static final String JVM_CLASSES_LOADED_TOTAL = "jvm_classes_loaded_total";
    private static final String JVM_CLASSES_UNLOADED_TOTAL = "jvm_classes_unloaded_total";
    private final PrometheusProperties config;
    private final ClassLoadingMXBean classLoadingBean;

    private JvmClassLoadingMetrics(ClassLoadingMXBean classLoadingBean, PrometheusProperties config) {
        this.classLoadingBean = classLoadingBean;
        this.config = config;
    }

    private void register(PrometheusRegistry registry) {
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("jvm_classes_currently_loaded")).help("The number of classes that are currently loaded in the JVM")).callback((callback) -> {
            callback.call((double)this.classLoadingBean.getLoadedClassCount(), new String[0]);
        }).register(registry);
        ((CounterWithCallback.Builder)CounterWithCallback.builder(this.config).name("jvm_classes_loaded_total").help("The total number of classes that have been loaded since the JVM has started execution")).callback((callback) -> {
            callback.call((double)this.classLoadingBean.getTotalLoadedClassCount(), new String[0]);
        }).register(registry);
        ((CounterWithCallback.Builder)CounterWithCallback.builder(this.config).name("jvm_classes_unloaded_total").help("The total number of classes that have been unloaded since the JVM has started execution")).callback((callback) -> {
            callback.call((double)this.classLoadingBean.getUnloadedClassCount(), new String[0]);
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
        private ClassLoadingMXBean classLoadingBean;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder classLoadingBean(ClassLoadingMXBean classLoadingBean) {
            this.classLoadingBean = classLoadingBean;
            return this;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            ClassLoadingMXBean classLoadingBean = this.classLoadingBean != null ? this.classLoadingBean : ManagementFactory.getClassLoadingMXBean();
            (new JvmClassLoadingMetrics(classLoadingBean, this.config)).register(registry);
        }
    }
}
