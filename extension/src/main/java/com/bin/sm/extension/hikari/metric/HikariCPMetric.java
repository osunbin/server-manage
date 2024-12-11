package com.bin.sm.extension.hikari.metric;

import com.zaxxer.hikari.metrics.PoolStats;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;

import java.util.function.Function;

public class HikariCPMetric {

    private final String poolName;
    private final PoolStats poolStats;


    HikariCPMetric(String poolName, PoolStats poolStats) {
        this.poolName = poolName;
        this.poolStats = poolStats;
    }

    void register() {
         this.register("hikaricp_active_connections", "Active connections", PoolStats::getActiveConnections);
         this.register("hikaricp_idle_connections", "Idle connections", PoolStats::getIdleConnections);
         this.register("hikaricp_pending_threads", "Pending threads", PoolStats::getPendingThreads);
         this.register("hikaricp_connections", "The number of current connections", PoolStats::getTotalConnections);
         this.register("hikaricp_max_connections", "Max connections", PoolStats::getMaxConnections);
         this.register("hikaricp_min_connections", "Min connections", PoolStats::getMinConnections);
    }



    private void register(String metric, String help, Function<PoolStats, Integer> metricValueFunction) {
        GaugeWithCallback.builder().name(metric).help(help).callback(callback -> {
            callback.call(metricValueFunction.apply(poolStats), poolName);
        }).register();
    }
}
