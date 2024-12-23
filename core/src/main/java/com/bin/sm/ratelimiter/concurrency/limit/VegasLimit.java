package com.bin.sm.ratelimiter.concurrency.limit;

import com.bin.sm.ratelimiter.functions.Log10RootFunction;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Limiter based on TCP Vegas where the limit increases by alpha if the queue_use is small ({@literal <} alpha)
 * and decreases by alpha if the queue_use is large ({@literal >} beta).
 * 
 * Queue size is calculated using the formula, 
 *  queue_use = limit − BWE×RTTnoLoad = limit × (1 − RTTnoLoad/RTTactual)
 *
 * For traditional TCP Vegas alpha is typically 2-3 and beta is typically 4-6.  To allow for better growth and 
 * stability at higher limits we set alpha=Max(3, 10% of the current limit) and beta=Max(6, 20% of the current limit)
 *
 *  基于TCP Vegas的限制器，如果queue_use较小（{@literal<}alpha），则限制增加alpha；如果queue.use较大（{@literal>}beta），则减少alpha。队列大小使用以下公式计算：Queue_use=limit−BWE×RTTnoLoad=limit×（1−RTTnoLoad/RTTactual）对于传统的TCP Vegas，alpha通常为2-3，beta通常为4-6。为了实现更好的增长和
 * 在较高极限下的稳定性，我们设置alpha=Max（电流极限的3.10%）和beta=Max（当前极限的6.20%）
 */
public class VegasLimit extends AbstractLimit {
    private static final Logger LOG = LoggerFactory.getLogger(VegasLimit.class);
    
    private static final Function<Integer, Integer> LOG10 = Log10RootFunction.create(0);

    public static class Builder {
        private int initialLimit = 20;
        private int maxConcurrency = 1000;
        private double smoothing = 1.0;
        
        private Function<Integer, Integer> alphaFunc = (limit) -> 3 * LOG10.apply(limit.intValue());
        private Function<Integer, Integer> betaFunc = (limit) -> 6 * LOG10.apply(limit.intValue());
        private Function<Integer, Integer> thresholdFunc = (limit) -> LOG10.apply(limit.intValue());
        private Function<Double, Double> increaseFunc = (limit) -> limit + LOG10.apply(limit.intValue());
        private Function<Double, Double> decreaseFunc = (limit) -> limit - LOG10.apply(limit.intValue());
        private int probeMultiplier = 30;
        
        private Builder() {
        }
        
        /**
         * The limiter will probe for a new noload RTT every probeMultiplier * current limit
         * iterations.  Default value is 30.  
         * @param probeMultiplier 
         * @return Chainable builder
         */
        public Builder probeMultiplier(int probeMultiplier) {
            this.probeMultiplier = probeMultiplier;
            return this;
        }
        
        public Builder alpha(int alpha) {
            this.alphaFunc = (ignore) -> alpha;
            return this;
        }
        
        public Builder threshold(Function<Integer, Integer> threshold) {
            this.thresholdFunc = threshold;
            return this;
        }
        
        public Builder alpha(Function<Integer, Integer> alpha) {
            this.alphaFunc = alpha;
            return this;
        }
        
        public Builder beta(int beta) {
            this.betaFunc = (ignore) -> beta;
            return this;
        }
        
        public Builder beta(Function<Integer, Integer> beta) {
            this.betaFunc = beta;
            return this;
        }
        
        public Builder increase(Function<Double, Double> increase) {
            this.increaseFunc = increase;
            return this;
        }
        
        public Builder decrease(Function<Double, Double> decrease) {
            this.decreaseFunc = decrease;
            return this;
        }
        
        public Builder smoothing(double smoothing) {
            this.smoothing = smoothing;
            return this;
        }
        
        public Builder initialLimit(int initialLimit) {
            this.initialLimit = initialLimit;
            return this;
        }
        
        @Deprecated
        public Builder tolerance(double tolerance) {
            return this;
        }
        
        public Builder maxConcurrency(int maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
            return this;
        }
        
        @Deprecated
        public Builder backoffRatio(double ratio) {
            return this;
        }

        
        public VegasLimit build() {
            if (initialLimit > maxConcurrency) {
                LOG.warn("Initial limit {} exceeded maximum limit {}", initialLimit, maxConcurrency);
            }
            return new VegasLimit(this);
        }
    }
    
    public static Builder newBuilder() {
        return new Builder();
    }
    
    public static VegasLimit newDefault() {
        return newBuilder().build();
    }
    
    /**
     * Estimated concurrency limit based on our algorithm<br/>
     *  20
     */
    private volatile double estimatedLimit;
    
    private volatile long rtt_noload = 0;
    
    /**
     * Maximum allowed limit providing an upper bound failsafe
     */
    private final int maxLimit; 
    
    private final double smoothing;
    private final Function<Integer, Integer> alphaFunc;
    private final Function<Integer, Integer> betaFunc;
    private final Function<Integer, Integer> thresholdFunc;
    private final Function<Double, Double> increaseFunc;
    private final Function<Double, Double> decreaseFunc;
    /**
     *  30
     */
    private final int probeMultiplier;
    private int probeCount = 0;
    /**
     *  0.5-1.0
     */
    private double probeJitter;

    private VegasLimit(Builder builder) {
        super(builder.initialLimit);
        this.estimatedLimit = builder.initialLimit;
        this.maxLimit = builder.maxConcurrency;
        this.alphaFunc = builder.alphaFunc;
        this.betaFunc = builder.betaFunc;
        this.increaseFunc = builder.increaseFunc;
        this.decreaseFunc = builder.decreaseFunc;
        this.thresholdFunc = builder.thresholdFunc;
        this.smoothing = builder.smoothing;
        this.probeMultiplier = builder.probeMultiplier;

        resetProbeJitter();

    }

    private void resetProbeJitter() {
        probeJitter = ThreadLocalRandom.current().nextDouble(0.5, 1);
    }

    private boolean shouldProbe() {
        return probeJitter * probeMultiplier * estimatedLimit <= probeCount;
    }

    @Override
    protected int _update(long startTime, long rtt, int inflight, boolean didDrop) {
        Preconditions.checkArgument(rtt > 0, "rtt must be >0 but got " + rtt);

        probeCount++;
        if (shouldProbe()) {
            LOG.debug("Probe MinRTT {}", TimeUnit.NANOSECONDS.toMicros(rtt) / 1000.0);
            resetProbeJitter();
            probeCount = 0;
            rtt_noload = rtt;
            return (int)estimatedLimit;
        }
        
        if (rtt_noload == 0 || rtt < rtt_noload) { // 小于之前的 rt
            LOG.debug("New MinRTT {}", TimeUnit.NANOSECONDS.toMicros(rtt) / 1000.0);
            rtt_noload = rtt;
            return (int)estimatedLimit;
        }
        

        return updateEstimatedLimit(rtt, inflight, didDrop);
    }

    private int updateEstimatedLimit(long rtt, int inflight, boolean didDrop) {
        final int queueSize = (int) Math.ceil(estimatedLimit * (1 - (double)rtt_noload / rtt));

        double newLimit;
        // Treat any drop (i.e timeout) as needing to reduce the limit
        if (didDrop) {
            newLimit = decreaseFunc.apply(estimatedLimit);
        // Prevent upward drift if not close to the limit
        } else if (inflight * 2 < estimatedLimit) {
            return (int)estimatedLimit;
        } else {
            int alpha = alphaFunc.apply((int)estimatedLimit);
            int beta = betaFunc.apply((int)estimatedLimit);
            int threshold = this.thresholdFunc.apply((int)estimatedLimit);

            // Aggressive increase when no queuing
            // 无排队时大幅增加
            if (queueSize <= threshold) {
                newLimit = estimatedLimit + beta;
            // Increase the limit if queue is still manageable
            } else if (queueSize < alpha) {
                newLimit = increaseFunc.apply(estimatedLimit);
            // Detecting latency so decrease
            } else if (queueSize > beta) {
                newLimit = decreaseFunc.apply(estimatedLimit);
            // We're within he sweet spot so nothing to do
            } else {
                return (int)estimatedLimit;
            }
        }

        newLimit = Math.max(1, Math.min(maxLimit, newLimit));
        newLimit = (1 - smoothing) * estimatedLimit + smoothing * newLimit;
        if ((int)newLimit != (int)estimatedLimit && LOG.isDebugEnabled()) {
            LOG.debug("New limit={} minRtt={} ms winRtt={} ms queueSize={}",
                    (int)newLimit,
                    TimeUnit.NANOSECONDS.toMicros(rtt_noload) / 1000.0,
                    TimeUnit.NANOSECONDS.toMicros(rtt) / 1000.0,
                    queueSize);
        }
        estimatedLimit = newLimit;
        return (int)estimatedLimit;
    }

    @Override
    public String toString() {
        return "VegasLimit [limit=" + getLimit() + 
                ", rtt_noload=" + TimeUnit.NANOSECONDS.toMicros(rtt_noload) / 1000.0 +
                " ms]";
    }
}
