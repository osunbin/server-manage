package com.bin.sm.common;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

public class SystemCpuTask implements Runnable {

    private static final Logger logger =
            LoggerFactory.getLogger(SystemCpuTask.class);


    /**
     *  推荐：cpu核心数 * 2.5
     *  例子：假如系统最多能承载10个进程，那么
     *    有5个进程运行时，load值为0.5，即50%，所有进程能够通畅运行，还有50%空闲空间；
     *    有10个进程运行时，load值为1，即100%，所有进程能够通畅运行，没有空闲
     *    有20个进程运行时，load值为2，即200%，有10个进程能够通畅运行，还有10个在等待中
     *
     *    系统负载高了,说明服务处理能力不够了,应该减少处理
     *
     */
    volatile double currentLoad = -1;
    /**
     * cpu usage, between [0, 1]
     */
    volatile double currentCpuUsage = -1;

    volatile String reason = "";

    volatile long processCpuTime = 0;
    volatile long processUpTime = 0;

    public double getSystemAverageLoad() {
        return currentLoad;
    }

    public double getCpuUsage() {
        return currentCpuUsage;
    }

    @Override
    public void run() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            currentLoad = osBean.getSystemLoadAverage();
            double cpuLoad = osBean.getCpuLoad(); // 整个系统 cpu usage
            double processCpuLoad = osBean.getProcessCpuLoad(); // jvm cpu usage
            System.out.println("cpuLoad=" + cpuLoad);
            System.out.println("processCpuLoad=" + processCpuLoad);
            double systemCpuUsage = osBean.getCpuLoad();

            double processCpuUsage = oldCpuUsage(osBean);
            currentCpuUsage = Math.max(processCpuUsage, systemCpuUsage);
            System.out.println("extProcessCpuLoad=" + processCpuUsage);
        } catch (Throwable e) {
            logger.error("[SystemStatusListener] Failed to get system metrics from JMX",e);
        }
    }


    private double oldCpuUsage(OperatingSystemMXBean osBean) {
        /*
         * Java Doc copied from {@link OperatingSystemMXBean#getSystemCpuLoad()}:</br>
         * Returns the "recent cpu usage" for the whole system. This value is a double in the [0.0,1.0] interval.
         * A value of 0.0 means that all CPUs were idle during the recent period of time observed, while a value
         * of 1.0 means that all CPUs were actively running 100% of the time during the recent period being
         * observed. All values between 0.0 and 1.0 are possible depending of the activities going on in the
         * system. If the system recent cpu usage is not available, the method returns a negative value.
         */

        // calculate process cpu usage to support application running in container environment
        RuntimeMXBean runtimeBean = ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
        long newProcessCpuTime = osBean.getProcessCpuTime();
        long newProcessUpTime = runtimeBean.getUptime();
        int cpuCores = osBean.getAvailableProcessors();

        long processCpuTimeDiffInMs = TimeUnit.NANOSECONDS
                .toMillis(newProcessCpuTime - processCpuTime);

        long processUpTimeDiffInMs = newProcessUpTime - processUpTime;
        double processCpuUsage = (double) processCpuTimeDiffInMs / processUpTimeDiffInMs / cpuCores;

        processCpuTime = newProcessCpuTime;
        processUpTime = newProcessUpTime;

        return processCpuUsage;
    }

}