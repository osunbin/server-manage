package com.bin.sm.metric.jvm;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.CounterWithCallback;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Unit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProcessMetrics {
    private static final String PROCESS_CPU_SECONDS_TOTAL = "process_cpu_seconds_total";
    private static final String PROCESS_START_TIME_SECONDS = "process_start_time_seconds";
    private static final String PROCESS_OPEN_FDS = "process_open_fds";
    private static final String PROCESS_MAX_FDS = "process_max_fds";
    private static final String PROCESS_VIRTUAL_MEMORY_BYTES = "process_virtual_memory_bytes";
    private static final String PROCESS_RESIDENT_MEMORY_BYTES = "process_resident_memory_bytes";
    private static final File PROC_SELF_STATUS = new File("/proc/self/status");
    private final PrometheusProperties config;
    private final OperatingSystemMXBean osBean;
    private final RuntimeMXBean runtimeBean;
    private final Grepper grepper;
    private final boolean linux;

    private ProcessMetrics(OperatingSystemMXBean osBean, RuntimeMXBean runtimeBean, Grepper grepper, PrometheusProperties config) {
        this.osBean = osBean;
        this.runtimeBean = runtimeBean;
        this.grepper = grepper;
        this.config = config;
        this.linux = PROC_SELF_STATUS.canRead();
    }

    private void register(PrometheusRegistry registry) {
        ((CounterWithCallback.Builder)((CounterWithCallback.Builder)CounterWithCallback.builder(this.config).name("process_cpu_seconds_total").help("Total user and system CPU time spent in seconds.")).unit(Unit.SECONDS)).callback((callback) -> {
            try {
                Long processCpuTime = this.callLongGetter((String)"getProcessCpuTime", this.osBean);
                if (processCpuTime != null) {
                    callback.call(Unit.nanosToSeconds(processCpuTime), new String[0]);
                }
            } catch (Exception var3) {
            }

        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("process_start_time_seconds")).help("Start time of the process since unix epoch in seconds.")).unit(Unit.SECONDS)).callback((callback) -> {
            callback.call(Unit.millisToSeconds(this.runtimeBean.getStartTime()), new String[0]);
        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("process_open_fds")).help("Number of open file descriptors.")).callback((callback) -> {
            try {
                Long openFds = this.callLongGetter((String)"getOpenFileDescriptorCount", this.osBean);
                if (openFds != null) {
                    callback.call((double)openFds, new String[0]);
                }
            } catch (Exception var3) {
            }

        }).register(registry);
        ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("process_max_fds")).help("Maximum number of open file descriptors.")).callback((callback) -> {
            try {
                Long maxFds = this.callLongGetter((String)"getMaxFileDescriptorCount", this.osBean);
                if (maxFds != null) {
                    callback.call((double)maxFds, new String[0]);
                }
            } catch (Exception var3) {
            }

        }).register(registry);
        if (this.linux) {
            ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("process_virtual_memory_bytes")).help("Virtual memory size in bytes.")).unit(Unit.BYTES)).callback((callback) -> {
                try {
                    String line = this.grepper.lineStartingWith(PROC_SELF_STATUS, "VmSize:");
                    callback.call(Unit.kiloBytesToBytes(Double.parseDouble(line.split("\\s+")[1])), new String[0]);
                } catch (Exception var3) {
                }

            }).register(registry);
            ((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)((GaugeWithCallback.Builder)GaugeWithCallback.builder(this.config).name("process_resident_memory_bytes")).help("Resident memory size in bytes.")).unit(Unit.BYTES)).callback((callback) -> {
                try {
                    String line = this.grepper.lineStartingWith(PROC_SELF_STATUS, "VmRSS:");
                    callback.call(Unit.kiloBytesToBytes(Double.parseDouble(line.split("\\s+")[1])), new String[0]);
                } catch (Exception var3) {
                }

            }).register(registry);
        }

    }

    private Long callLongGetter(String getterName, Object obj) throws NoSuchMethodException, InvocationTargetException {
        return this.callLongGetter(obj.getClass().getMethod(getterName), obj);
    }

    private Long callLongGetter(Method method, Object obj) throws InvocationTargetException {
        try {
            return (Long)method.invoke(obj);
        } catch (IllegalAccessException var10) {
            Class[] var3 = method.getDeclaringClass().getInterfaces();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Class<?> clazz = var3[var5];

                try {
                    Method interfaceMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
                    Long result = this.callLongGetter(interfaceMethod, obj);
                    if (result != null) {
                        return result;
                    }
                } catch (NoSuchMethodException var9) {
                }
            }

            return null;
        }
    }

    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    interface Grepper {
        String lineStartingWith(File var1, String var2) throws IOException;
    }

    public static class Builder {
        private final PrometheusProperties config;
        private OperatingSystemMXBean osBean;
        private RuntimeMXBean runtimeBean;
        private Grepper grepper;

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        Builder osBean(OperatingSystemMXBean osBean) {
            this.osBean = osBean;
            return this;
        }

        Builder runtimeBean(RuntimeMXBean runtimeBean) {
            this.runtimeBean = runtimeBean;
            return this;
        }

        Builder grepper(Grepper grepper) {
            this.grepper = grepper;
            return this;
        }

        public void register() {
            this.register(PrometheusRegistry.defaultRegistry);
        }

        public void register(PrometheusRegistry registry) {
            OperatingSystemMXBean osBean = this.osBean != null ? this.osBean : ManagementFactory.getOperatingSystemMXBean();
            RuntimeMXBean runtimeMXBean = this.runtimeBean != null ? this.runtimeBean : ManagementFactory.getRuntimeMXBean();
            Grepper grepper = this.grepper != null ? this.grepper : new FileGrepper();
            (new ProcessMetrics(osBean, runtimeMXBean, (Grepper)grepper, this.config)).register(registry);
        }
    }

    private static class FileGrepper implements Grepper {
        private FileGrepper() {
        }

        public String lineStartingWith(File file, String prefix) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String var5;
            label35: {
                try {
                    for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                        if (line.startsWith(prefix)) {
                            var5 = line;
                            break label35;
                        }
                    }
                } catch (Throwable var7) {
                    try {
                        reader.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }

                    throw var7;
                }

                reader.close();
                return null;
            }

            reader.close();
            return var5;
        }
    }
}
