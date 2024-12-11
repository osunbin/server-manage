package com.bin.sm.extension.hikari.metric;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PrometheusMetricsTrackerFactory implements MetricsTrackerFactory {


    private final PrometheusRegistry  collectorRegistry;
    private final Map<String,HikariCPMetric> hikariCPMetrics = new ConcurrentHashMap<>();
    private final Consumer<String> consumer = hikariCPMetrics::remove;

    public PrometheusMetricsTrackerFactory() {
        this(PrometheusRegistry.defaultRegistry);
    }

    public PrometheusMetricsTrackerFactory(PrometheusRegistry  collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }

    @Override
    public IMetricsTracker create(String poolName, PoolStats poolStats) {
        HikariCPMetric hikariCPMetric = new HikariCPMetric(poolName,poolStats);
        hikariCPMetrics.put(poolName, hikariCPMetric);

        hikariCPMetric.register();
        return new PrometheusMetricsTracker(poolName, this.collectorRegistry, consumer);
    }

}
