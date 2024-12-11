package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Unit;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class JvmNativeMemoryMetrics {
    private static final String JVM_NATIVE_MEMORY_RESERVED_BYTES = "jvm_native_memory_reserved_bytes";
    private static final String JVM_NATIVE_MEMORY_COMMITTED_BYTES = "jvm_native_memory_committed_bytes";
    private static final Pattern pattern = Pattern.compile("\\s*([A-Z][A-Za-z\\s]*[A-Za-z]+).*reserved=(\\d+), committed=(\\d+)");
    static final AtomicBoolean isEnabled = new AtomicBoolean(true);
    private final PrometheusProperties config;
    private final PlatformMBeanServerAdapter adapter;

    private JvmNativeMemoryMetrics(PrometheusProperties config, PlatformMBeanServerAdapter adapter) {
        this.config = config;
        this.adapter = adapter;
    }

    private void register(PrometheusRegistry registry) {
        this.vmNativeMemorySummaryInBytesOrEmpty();
        if (isEnabled.get()) {
            ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)
                    GaugeWithCallback.builder(this.config)
                            .name("jvm_native_memory_reserved_bytes"))
                    .help("Reserved bytes of a given JVM. Reserved memory represents the total amount of memory the JVM can potentially use.")).unit(Unit.BYTES))
                    .labelNames(new String[]{"pool"})).callback(this.makeCallback(true)).register(registry);
            ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)
                    GaugeWithCallback.builder(this.config).name("jvm_native_memory_committed_bytes")).help("Committed bytes of a given JVM. Committed memory represents the amount of memory the JVM is using right now.")).unit(Unit.BYTES)).labelNames(new String[]{"pool"})).callback(this.makeCallback(false)).register(registry);
        }

    }

    private Consumer<GaugeWithCallback.Callback> makeCallback(Boolean reserved) {
        return (callback) -> {
            String summary = this.vmNativeMemorySummaryInBytesOrEmpty();
            String category;
            long value;
            if (!summary.isEmpty()) {
                for(Matcher matcher = pattern.matcher(summary); matcher.find(); callback.call((double)value, new String[]{category})) {
                    category = matcher.group(1);
                    if (reserved) {
                        value = Long.parseLong(matcher.group(2));
                    } else {
                        value = Long.parseLong(matcher.group(3));
                    }
                }
            }

        };
    }

    private String vmNativeMemorySummaryInBytesOrEmpty() {
        if (!isEnabled.get()) {
            return "";
        } else {
            try {
                String summary = this.adapter.vmNativeMemorySummaryInBytes();
                if (!summary.isEmpty() && !summary.trim().contains("Native memory tracking is not enabled")) {
                    return summary;
                } else {
                    isEnabled.set(false);
                    return "";
                }
            } catch (Exception var2) {
                isEnabled.set(false);
                return "";
            }
        }
    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    interface PlatformMBeanServerAdapter {
        String vmNativeMemorySummaryInBytes();
    }

    public static class Builder {
        private final PrometheusProperties config;
        private final PlatformMBeanServerAdapter adapter;

        private Builder(PrometheusProperties config) {
            this(config, (PlatformMBeanServerAdapter)(new DefaultPlatformMBeanServerAdapter()));
        }

        Builder(PrometheusProperties config, PlatformMBeanServerAdapter adapter) {
            this.config = config;
            this.adapter = adapter;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            (new JvmNativeMemoryMetrics(this.config, this.adapter)).register(registry);
        }
    }

    static class DefaultPlatformMBeanServerAdapter implements PlatformMBeanServerAdapter {
        DefaultPlatformMBeanServerAdapter() {
        }

        public String vmNativeMemorySummaryInBytes() {
            try {
                return (String)ManagementFactory.getPlatformMBeanServer().invoke(new ObjectName("com.sun.management:type=DiagnosticCommand"), "vmNativeMemory", new Object[]{new String[]{"summary", "scale=B"}}, new String[]{"[Ljava.lang.String;"});
            } catch (MalformedObjectNameException | InstanceNotFoundException | MBeanException | ReflectionException var2) {
                JMException e = var2;
                throw new IllegalStateException("Native memory tracking is not enabled", e);
            }
        }
    }
}
