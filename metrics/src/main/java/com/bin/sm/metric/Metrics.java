package com.bin.sm.metric;

import com.bin.sm.metric.export.Tracer;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.core.metrics.Info;
import io.prometheus.metrics.core.metrics.Metric;
import io.prometheus.metrics.core.metrics.Summary;
import io.prometheus.metrics.model.snapshots.Labels;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class Metrics {

    public static void main(String[] args) {
        Counter counter = Counter.builder().name("jvm_thread").labelNames("a","v").register();
        counter.incWithExemplar(1L, Labels.of("trace_id", Tracer.traceId()));

        Histogram.Builder servletLatencyBuilder = Histogram.builder()
                .name("servlet_request_seconds")
                .help("The time taken fulfilling servlet requests")
                .labelNames("context", "method");

        servletLatencyBuilder.classicUpperBounds(.01, .05, .1, .25, .5, 1, 2.5, 5, 10, 30);



        // foo_info{entity="controller",name="pretty name",version="8.2.7"} 1.0
        Info info =  Info.builder().build();
        info.addLabelValues("1");


        Summary s = Summary.builder().build();
        s.observe(2D);

    }


    public void tomcat() throws Exception {
        String jmxDomain = "Catalina/Tomcat";
        String[] genericAttributes = new String[]{"currentThreadCount", "currentThreadsBusy", "maxThreads", "connectionCount", "maxConnections"};

        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName filterName = new ObjectName(jmxDomain + ":type=ThreadPool,name=*");
        Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);
        for (final ObjectInstance mBean : mBeans) {
            List<String> labelValueList = Collections.singletonList(mBean.getObjectName().getKeyProperty("name").replaceAll("[\"\\\\]", ""));
            AttributeList attributeList = server.getAttributes(mBean.getObjectName(), genericAttributes);
            for (Attribute attribute : attributeList.asList()) {
                switch (attribute.getName()) {
                    case "currentThreadCount":
                        System.out.println("currentThreadCount:"+labelValueList + "-" + attribute.getValue());
                        break;
                    case "currentThreadsBusy":
                        System.out.println("currentThreadsBusy:"+labelValueList + "-" + attribute.getValue());
                        break;
                    case "maxThreads":
                        System.out.println("maxThreads:"+labelValueList + "-" + attribute.getValue());
                        break;
                    case "connectionCount":
                        System.out.println("connectionCount:"+labelValueList + "-" + attribute.getValue());
                        break;
                    case "maxConnections":
                        System.out.println("maxConnections:"+labelValueList + "-" + attribute.getValue());
                }
            }
        }

    }

    private static final ConcurrentMap<String, Metric> metrics = new ConcurrentHashMap<>();



    public static void registerCounter(String name,String help,String... labelNames) {
        Counter counter = Counter.builder().name(name).labelNames(labelNames).help(help).register();
        metrics.put(name, counter);
    }


    public static void counterInc(String name,long value) {
        Metric metric = metrics.get(name);
        if (metric instanceof Counter counter) {
            counter.inc(value);
        }
    }






}
