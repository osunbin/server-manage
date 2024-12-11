package com.bin.sm.metric.export;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;

public class Tracer {

    public static String traceId() {
       return TraceContext.traceId();
    }
}
