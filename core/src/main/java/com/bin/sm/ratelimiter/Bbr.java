package com.bin.sm.ratelimiter;

import com.bin.sm.common.SystemCpuTask;
import com.bin.sm.internal.window.Bucket;
import com.bin.sm.internal.window.RollingCounter;
import com.bin.sm.util.OsUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Bbr {
    private double decay = 0.95;
    private AtomicLong gCpu = new AtomicLong();


    private int bucket;

    private long cpuThreshold = 800;

    private RollingCounter passStat;
    private RollingCounter rtStat;
    private AtomicLong inFlight = new AtomicLong();
    private long bucketPerSecond;
    private long bucketDuration;
    private AtomicLong prevDropTime = new AtomicLong();
    private AtomicReference<CounterCache> maxPASSCache = new AtomicReference();
    private AtomicReference<CounterCache> minRtCache = new AtomicReference();
    private Supplier<Long> getCpu = () -> gCpu.get();
    // LongAdder

    public void init() {
        ScheduledExecutorService scheduled =
                Executors.newScheduledThreadPool(1);


        scheduled.scheduleWithFixedDelay(() -> cpuproc(),
                500, 500, TimeUnit.MILLISECONDS);
    }

    private void cpuproc() {
        // 500 毫秒 TODO
        double usage = OsUtil.getCpuUsage();
        usage = Math.min(usage, 1000);
        long curCPU = (long) (gCpu.get() * decay + usage * (1.0 - decay));
        gCpu.set(curCPU);
    }


    public Bbr() {

        long window = TimeUnit.SECONDS.toSeconds(10);
        int bucket = 100;
        bucketDuration = window / bucket;
        passStat = new RollingCounter(bucket, bucketDuration);
        rtStat = new RollingCounter(bucket, bucketDuration);

        this.bucket = bucket;
        // 1s
        bucketPerSecond =  TimeUnit.SECONDS.toSeconds(1) / bucketDuration;

    }


    private long maxPASS() {
        CounterCache passCache = maxPASSCache.get();
        if (passCache != null) {
            if (timespan(passCache.time) < 1)
                return passCache.val;
        }
        double rawMaxPass = passStat.reduce(lists -> {
            double result = 1.0;
            Iterator<Bucket> iterator = lists.iterator();

            for (int i = 0; iterator.hasNext() && i < bucket; i++) {
                Bucket bucket = iterator.next();
                double count = Arrays.stream(bucket.points()).sum();
                result = Math.max(result, count);
            }
            return result;
        });
        long result = Math.round(rawMaxPass);
        maxPASSCache.setPlain(new CounterCache(result, System.currentTimeMillis()));
        return result;
    }


    /**
     * 自lastTime以来通过的bucket计数，
     * 如果它比上次记录的时间早一个bucket持续时间，
     * 它将返回BucketNum
     */
    private int timespan(long lastTime) {
        int v = (int) (lastTime / bucketDuration);
        if (v > -1) return v;
        return bucket;
    }


    private long minRT() {
        CounterCache rtCache = minRtCache.get();
        if (rtCache != null) {
            if (timespan(rtCache.time) < 1)
                return rtCache.val;
        }
        double reduce = rtStat.reduce(lists -> {
            double result = Double.MAX_VALUE;
            Iterator<Bucket> iterator = lists.iterator();
            for (int i = 1; iterator.hasNext() && i < bucket; i++) {
                Bucket bucket = iterator.next();
                if (bucket.points().length == 0)
                    continue;

                double total = Arrays.stream(bucket.points()).sum();

                double avg = total / bucket.count();
                result = Math.min(result, avg);
            }
            return result;
        });
        long rawMinRT = Math.round(Math.ceil(reduce));
        if (rawMinRT <= 0) {
            rawMinRT = 1;
        }
        minRtCache.setPlain(new CounterCache(rawMinRT, System.currentTimeMillis()));

        return rawMinRT;
    }


    private long maxInFlight() {

        return Math.round(
                // 向下取整  qps   minRt
                Math.floor(maxPASS() * minRT() * bucketPerSecond / 1000.0)
                        + 0.5);
    }


    private boolean shouldDrop() {
        long now = System.currentTimeMillis();
        if (getCpu.get() < cpuThreshold) {
            long preDropTime = prevDropTime.get(); // 上一次丢弃的时间
            if (preDropTime == 0) return false;
            if (now - preDropTime <= 1000) { // 1s
                // 一秒钟前开始降落，检查当前飞行计数
                // 1s之内丢弃过,检测还有多少请求未完成
                long inFlight = this.inFlight.get();
                return inFlight > 1 && inFlight > maxInFlight();
            }
            prevDropTime.setPlain(0);
            return false;
        }
        // 当前cpu负载超过阈值
        long inFlight = this.inFlight.get();
        boolean drop = inFlight > 1 && inFlight > maxInFlight();
        if (drop) {
            long prevDrop = prevDropTime.get();
            // 已经开始下降，直接返回
            if (prevDrop != 0)
                return drop;

            prevDropTime.set(now);
        }
        return drop;
    }


    public Runnable allow() {
        if (shouldDrop())
            return () -> {
            };//  rate limit exceeded

        inFlight.incrementAndGet();
        long start = System.currentTimeMillis();

        return () -> {
            int rt = (int) (System.currentTimeMillis() - start);
            if (rt > 0) rtStat.add(rt);
            inFlight.decrementAndGet();
            passStat.add(1);
        };

    }




    public Stat stat() {
        return new Stat()
                .cpu(gCpu.get())
                .minRt(minRT())
                .maxPass(maxPASS())
                .maxInFlight(maxInFlight())
                .inFlight(inFlight.get());
    }


    private class Stat {
        private long cpu;
        private long inFlight;
        private long maxInFlight;
        private long minRt;
        private long maxPass;

        public Stat() {
        }

        public Stat cpu(long cpu) {
            this.cpu = cpu;
            return this;
        }

        public Stat inFlight(long inFlight) {
            this.inFlight = inFlight;
            return this;
        }

        public Stat maxInFlight(long maxInFlight) {
            this.maxInFlight = maxInFlight;
            return this;
        }

        public Stat minRt(long minRt) {
            this.minRt = minRt;
            return this;
        }

        public Stat maxPass(long maxPass) {
            this.maxPass = maxPass;
            return this;
        }
    }

    /**
     * counterCache用于缓存maxPASS和minRt结果。
     * 当前bucket的值不是实时计算的。
     * 缓存时间等于bucket持续时间
     */
    class CounterCache {
        long val;
        long time;

        public CounterCache() {
        }

        public CounterCache(long val, long time) {
            this.val = val;
            this.time = time;
        }

    }


}
