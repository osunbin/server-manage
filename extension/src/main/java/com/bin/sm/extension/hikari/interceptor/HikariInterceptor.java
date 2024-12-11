package com.bin.sm.extension.hikari.interceptor;

import com.bin.sm.extension.hikari.metric.PrometheusMetricsTrackerFactory;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import com.zaxxer.hikari.HikariConfig;

public class HikariInterceptor extends AbstractInterceptor {

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        HikariConfig config = (HikariConfig) context.getArguments()[0];
        config.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory());

        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return null;
    }
}
