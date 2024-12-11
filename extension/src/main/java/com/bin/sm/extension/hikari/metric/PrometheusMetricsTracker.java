package com.bin.sm.extension.hikari.metric;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import io.prometheus.metrics.core.datapoints.CounterDataPoint;
import io.prometheus.metrics.core.datapoints.DistributionDataPoint;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Summary;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PrometheusMetricsTracker implements IMetricsTracker {
    private static final Counter CONNECTION_TIMEOUT_COUNTER = Counter.builder().name("hikaricp_connection_timeout_total").labelNames("pool").help("Connection timeout total count").build();
    private static final Summary ELAPSED_ACQUIRED_SUMMARY = createSummary("hikaricp_connection_acquired_nanos", "Connection acquired time (ns)");
    private static final Summary ELAPSED_USAGE_SUMMARY = createSummary("hikaricp_connection_usage_millis", "Connection usage (ms)");
    private static final Summary ELAPSED_CREATION_SUMMARY = createSummary("hikaricp_connection_creation_millis", "Connection creation (ms)");
    private final String poolName;
    private final Consumer<String>  clear;
    private final CounterDataPoint connectionTimeoutCounterChild;
    private final DistributionDataPoint elapsedAcquiredSummaryChild;
    private final DistributionDataPoint elapsedUsageSummaryChild;
    private final DistributionDataPoint elapsedCreationSummaryChild;

    PrometheusMetricsTracker(String poolName, PrometheusRegistry registry, Consumer<String> clear) {
        this.registerMetrics(registry);
        this.poolName = poolName;
        this.clear = clear;
        connectionTimeoutCounterChild = CONNECTION_TIMEOUT_COUNTER.labelValues(poolName);
        elapsedAcquiredSummaryChild = ELAPSED_ACQUIRED_SUMMARY.labelValues(poolName);
        elapsedUsageSummaryChild = ELAPSED_USAGE_SUMMARY.labelValues(poolName);
        this.elapsedCreationSummaryChild = ELAPSED_CREATION_SUMMARY.labelValues(poolName);
    }

    private void registerMetrics(PrometheusRegistry registry) {
            // PrometheusRegistry.defaultRegistry
            registry.register(CONNECTION_TIMEOUT_COUNTER);
            registry.register(ELAPSED_ACQUIRED_SUMMARY);
            registry.register(ELAPSED_USAGE_SUMMARY);
            registry.register(ELAPSED_CREATION_SUMMARY);
    }

    public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
        this.elapsedAcquiredSummaryChild.observe((double)elapsedAcquiredNanos);
    }

    public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
        this.elapsedUsageSummaryChild.observe((double)elapsedBorrowedMillis);
    }

    public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
        this.elapsedCreationSummaryChild.observe((double)connectionCreatedMillis);
    }

    public void recordConnectionTimeout() {
        this.connectionTimeoutCounterChild.inc();
    }

    private static Summary createSummary(String name, String help) {
        return Summary.builder().name(name).help(help).labelNames("pool")
                .quantile(0.5, 0.05)
                .quantile(0.95, 0.01)
                .quantile(0.99, 0.001)
                .maxAgeSeconds(TimeUnit.MINUTES.toSeconds(5L))
                .numberOfAgeBuckets(5).build();
    }

    public void close() {
        this.clear.accept(this.poolName);
        CONNECTION_TIMEOUT_COUNTER.remove(this.poolName);
        ELAPSED_ACQUIRED_SUMMARY.remove(this.poolName);
        ELAPSED_USAGE_SUMMARY.remove(this.poolName);
        ELAPSED_CREATION_SUMMARY.remove(this.poolName);
    }
}
