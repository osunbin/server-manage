package com.bin.sm.circuitbreak.simple;

import com.bin.sm.circuitbreak.CircuitBreaker;
import com.bin.sm.circuitbreak.support.CircuitBreakerStatusEvent;
import com.bin.sm.circuitbreak.CircuitBreakerType;
import com.bin.sm.circuitbreak.support.DefaultCircuitBreakerStatusEvent;
import com.bin.sm.context.NodeInstance;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultCircuitBreaker implements CircuitBreaker, RollingNumber.RollingBucketEvent {


    private final AtomicReference<Status> status = new AtomicReference<>(Status.CLOSED);
    private final AtomicLong circuitOpened = new AtomicLong(-1);
    private final AtomicBoolean halfOpen = new AtomicBoolean(false);
    private final AtomicInteger halfOpenPass = new AtomicInteger(0);
    private final AtomicInteger halfOpenSuccess = new AtomicInteger(0);
    private final AtomicInteger halfOpenFail = new AtomicInteger(0);
    private RollingNumber metrics;
    private CircuitBreakerStatusEvent circuitBreakerStatus = DefaultCircuitBreakerStatusEvent.circuitBreakerStatusEvent;

    // ----  动态配置
    private int sleepWindowInMilliseconds;
    private long requestVolumeThreshold;
    private int errorThresholdPercentage = 20;
    private int maxHalfOpenPass = 20;


    public DefaultCircuitBreaker() {
        metrics = new RollingNumber(10 * 1000,10,this);
    }

    public NodeInstance nodeInstance() {
        return null;
    }

    public CircuitBreakerType circuitBreakerType(){
        return CircuitBreakerType.DEFAULT;
    }

    @Override
    public boolean allow() {
        if (circuitOpened.get() == -1) { // 关闭
            return true;
        } else {
            // 熔断后一段时间 不允许通过请求,然后 尝试通过一些请求
            if (isAfterSleepWindow()) {
                if (status.compareAndSet(Status.OPEN, Status.HALF_OPEN)) {
                    halfOpenPass.set(0);
                    halfOpenSuccess.set(0);
                    halfOpenFail.set(0);
                    halfOpen.set(true);
                    return true;
                } else {
                    // HALF_OPEN-半开状态
                    if (halfOpenPass.incrementAndGet() > maxHalfOpenPass)
                        return false;
                    else
                        return true;
                }
            } else {
                return false;
            }
        }
    }

    private boolean isAfterSleepWindow() {
        final long circuitOpenTime = circuitOpened.get();
        final long currentTime = System.currentTimeMillis();
        final long sleepWindowTime = sleepWindowInMilliseconds;
        return currentTime > circuitOpenTime + sleepWindowTime;
    }


    @Override
    public void markSuccess() {
        metrics.incSuccess();
        if (halfOpen.get() && halfOpenSuccess.incrementAndGet() > (maxHalfOpenPass / 2)) {
            if (status.compareAndSet(Status.HALF_OPEN, Status.CLOSED)) {
                halfOpen.set(false);
                circuitOpened.set(-1L);
                circuitBreakerStatus.openToClose(this);
            }
        }
    }

    @Override
    public void markFailed() {
        metrics.incFailure();
        failure();
    }

    @Override
    public void markTimeout() {
        metrics.incTimeout();
        failure();
    }

    private void failure() {
        if (halfOpen.get() && halfOpenFail.incrementAndGet() > (maxHalfOpenPass / 2)) {
            if (status.compareAndSet(Status.HALF_OPEN, Status.OPEN)) {
                halfOpen.set(false);
                circuitOpened.set(System.currentTimeMillis());
            }
        }
    }


    public void rollingBucket(long total, int errorPercentage) {
        if (total > requestVolumeThreshold &&
                errorPercentage > errorThresholdPercentage &&
                status.compareAndSet(Status.CLOSED, Status.OPEN)) {
            circuitOpened.set(System.currentTimeMillis());
            circuitBreakerStatus.closeToOpen(this,String.format("total %s errorPercentage %s",total,errorPercentage));
        }
    }


}
