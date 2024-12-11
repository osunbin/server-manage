package com.bin.sm;


import io.prometheus.metrics.core.datapoints.Timer;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.model.snapshots.Labels;
import prometheus.Remote;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Counter counter = Counter.builder().name("jvm_thread").labelNames("a","v").register();
        counter.labelValues("a","v").incWithExemplar(Labels.of("a","v"));

        Histogram histogram = Histogram.builder().name("a").register();
        Timer timer = histogram.startTimer();
// -javaagent:D:\code\self\sm-core\agent\target\agent-1.0-SNAPSHOT.jar
        timer.observeDuration();


        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}