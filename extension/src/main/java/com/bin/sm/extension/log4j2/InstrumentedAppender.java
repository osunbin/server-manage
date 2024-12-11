package com.bin.sm.extension.log4j2;



import io.prometheus.metrics.core.datapoints.CounterDataPoint;
import io.prometheus.metrics.core.metrics.Counter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
    name = "Prometheus",
    category = "Core",
    elementType = "appender"
)
public final class InstrumentedAppender extends AbstractAppender {
    public static final String COUNTER_NAME = "log4j2_appender_total";
    private static final Counter COUNTER = Counter.builder().name(COUNTER_NAME).help("Log4j2 log statements at various log levels").labelNames("level").register();
    private static final CounterDataPoint INFO_LABEL;
    private static final CounterDataPoint WARN_LABEL;
    private static final CounterDataPoint ERROR_LABEL;

    static {
        INFO_LABEL = COUNTER.labelValues("info");
        WARN_LABEL = COUNTER.labelValues("warn");
        ERROR_LABEL = COUNTER.labelValues("error");
    }

    protected InstrumentedAppender(String name) {
        super(name, (Filter)null, (Layout)null);
    }

    public void append(LogEvent event) {
        Level level = event.getLevel();
        if (Level.INFO.equals(level)) {
            INFO_LABEL.inc();
        } else if (Level.WARN.equals(level)) {
            WARN_LABEL.inc();
        } else if (Level.ERROR.equals(level)) {
            ERROR_LABEL.inc();
        }
    }

    @PluginFactory
    public static InstrumentedAppender createAppender(@PluginAttribute("name") String name) {
        if (name == null) {
            AbstractLifeCycle.LOGGER.error("No name provided for InstrumentedAppender");
            return null;
        } else {
            return new InstrumentedAppender(name);
        }
    }


}
