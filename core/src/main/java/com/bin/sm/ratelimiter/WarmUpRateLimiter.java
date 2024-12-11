package com.bin.sm.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;

public class WarmUpRateLimiter {

    protected double count;
    private int coldFactor;
    protected int warningToken = 0;
    private int maxToken;
    protected double slope;

    private AtomicLong storedTokens = new AtomicLong(0);
    private AtomicLong lastFilledTime = new AtomicLong(0);

    private final int timeoutInMs;
    private final AtomicLong latestPassedTime = new AtomicLong(-1);

    public WarmUpRateLimiter(double count, int warmUpPeriodInSec,int timeOutMs) {
        construct(count, warmUpPeriodInSec, 3);
        this.timeoutInMs = timeOutMs;
    }

    private void construct(double count, int warmUpPeriodInSec, int coldFactor) {

        if (coldFactor <= 1) {
            throw new IllegalArgumentException("Cold factor should be larger than 1");
        }

        this.count = count;

        this.coldFactor = coldFactor;

        // thresholdPermits = 0.5 * warmupPeriod / stableInterval.
        // warningToken = 100;
        warningToken = (int)(warmUpPeriodInSec * count) / (coldFactor - 1);
        // / maxPermits = thresholdPermits + 2 * warmupPeriod /
        // (stableInterval + coldInterval)
        // maxToken = 200
        maxToken = warningToken + (int)(2 * warmUpPeriodInSec * count / (1.0 + coldFactor));

        // slope
        // slope = (coldIntervalMicros - stableIntervalMicros) / (maxPermits
        // - thresholdPermits);
        slope = (coldFactor - 1.0) / count / (maxToken - warningToken);

    }
    //
    public boolean canPassWarmUp() {
        long acquireCount = 1L;
        long passQps = 100L; // successQps

        long previousQps = 10L;
        syncToken(previousQps);

        // 开始计算它的斜率
        // 如果进入了警戒线，开始调整他的qps
        long restToken = storedTokens.get();
        if (restToken >= warningToken) {
            long aboveToken = restToken - warningToken;
            // 消耗的速度要比warning快，但是要比慢
            // current interval = restToken*slope+1/count
            double warningQps = Math.nextUp(1.0 / (aboveToken * slope + 1.0 / count));
            if (passQps + acquireCount <= warningQps) {
                return true;
            }
        } else {
            if (passQps + acquireCount <= count) {
                return true;
            }
        }

        return false;
    }


    public boolean canPass() {
        long previousQps = 10L; // 最后一个窗口的successQps
        syncToken(previousQps);
        long currentTime = System.currentTimeMillis();

        long restToken = storedTokens.get();
        long costTime = 0;
        long expectedTime = 0;
        if (restToken >= warningToken) {
            long aboveToken = restToken - warningToken;

            // current interval = restToken*slope+1/count
            double warmingQps = Math.nextUp(1.0 / (aboveToken * slope + 1.0 / count));
            costTime = Math.round(1.0 / warmingQps * 1000);
        } else {
            costTime = Math.round(1.0 / count * 1000);
        }
        expectedTime = costTime + latestPassedTime.get();

        if (expectedTime <= currentTime) {
            latestPassedTime.set(currentTime);
            return true;
        } else {
            long waitTime = costTime + latestPassedTime.get() - currentTime;
            if (waitTime > timeoutInMs) {
                return false;
            } else {
                long oldTime = latestPassedTime.addAndGet(costTime);
                try {
                    waitTime = oldTime - System.currentTimeMillis();
                    if (waitTime > timeoutInMs) {
                        latestPassedTime.addAndGet(-costTime);
                        return false;
                    }
                    if (waitTime > 0) {
                        Thread.sleep(waitTime);
                    }
                    return true;
                } catch (InterruptedException e) {
                }
            }
        }
        return false;
    }


    private void syncToken(long passQps) {
        long currentTime = System.currentTimeMillis();
        currentTime = currentTime - currentTime % 1000;
        long oldLastFillTime = lastFilledTime.get();
        if (currentTime <= oldLastFillTime) {
            return;
        }

        long oldValue = storedTokens.get();
        long newValue = coolDownTokens(currentTime, passQps);

        if (storedTokens.compareAndSet(oldValue, newValue)) {
            long currentValue = storedTokens.addAndGet(0 - passQps);
            if (currentValue < 0) {
                storedTokens.set(0L);
            }
            lastFilledTime.set(currentTime);
        }

    }

    private long coolDownTokens(long currentTime, long passQps) {
        long oldValue = storedTokens.get();
        long newValue = oldValue;

        // 添加令牌的判断前提条件:
        // 当令牌的消耗程度远远低于警戒线的时候
        if (oldValue < warningToken) {
            newValue = (long)(oldValue + (currentTime - lastFilledTime.get()) * count / 1000);
        } else if (oldValue > warningToken) {
            if (passQps < (int)count / coldFactor) {
                newValue = (long)(oldValue + (currentTime - lastFilledTime.get()) * count / 1000);
            }
        }
        return Math.min(newValue, maxToken);
    }
}
