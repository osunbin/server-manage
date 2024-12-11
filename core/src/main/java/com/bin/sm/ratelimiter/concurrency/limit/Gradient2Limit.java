package com.bin.sm.ratelimiter.concurrency.limit;

import com.bin.sm.ratelimiter.concurrency.limit.measurement.ExpAvgMeasurement;
import com.bin.sm.ratelimiter.concurrency.limit.measurement.Measurement;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Concurrency limit algorithm that adjusts the limit based on the gradient of change of the current average RTT and
 * a long term exponentially smoothed average RTT.  Unlike traditional congestion control algorithms we use average
 * instead of minimum since RPC methods can be very bursty due to various factors such as non-homogenous request
 * processing complexity as well as a wide distribution of data size.  We have also found that using minimum can result
 * in an bias towards an impractically low base RTT resulting in excessive load shedding.  An exponential decay is
 * applied to the base RTT so that the value is kept stable yet is allowed to adapt to long term changes in latency
 * characteristics.
 *
 * The core algorithm re-calculates the limit every sampling window (ex. 1 second) using the formula
 *
 *      // Calculate the gradient limiting to the range [0.5, 1.0] to filter outliers
 *      gradient = max(0.5, min(1.0, longtermRtt / currentRtt));
 *
 *      // Calculate the new limit by applying the gradient and allowing for some queuing
 *      newLimit = gradient * currentLimit + queueSize;
 *
 *      // Update the limit using a smoothing factor (default 0.2)
 *      newLimit = currentLimit * (1-smoothing) + newLimit * smoothing
 *
 * The limit can be in one of three main states
 *
 * 1.  Steady state
 *
 * In this state the average RTT is very stable and the current measurement whipsaws around this value, sometimes reducing
 * the limit, sometimes increasing it.
 *
 * 2.  Transition from steady state to load
 *
 * In this state either the RPS to latency has spiked. The gradient is {@literal <} 1.0 due to a growing request queue that
 * cannot be handled by the system. Excessive requests and rejected due to the low limit. The baseline RTT grows using
 * exponential decay but lags the current measurement, which keeps the gradient {@literal <} 1.0 and limit low.
 *
 * 3.  Transition from load to steady state
 *
 * In this state the system goes back to steady state after a prolonged period of excessive load.  Requests aren't rejected
 * and the sample RTT remains low. During this state the long term RTT may take some time to go back to normal and could
 * potentially be several multiples higher than the current RTT.
 *
 *  并发限制算法，根据当前平均RTT和长期指数平滑平均RTT的变化梯度调整限制。
 *  与传统的拥塞控制算法不同，我们使用平均值而不是最小值，
 *  因为由于各种因素，如非同质请求处理复杂性以及数据大小的广泛分布，RPC方法可能非常突发。
 *  我们还发现，使用最小值可能会导致偏向不切实际的低基RTT，从而导致过度甩负荷。
 *  对基础RTT应用指数衰减，使其值保持稳定，同时允许其适应延迟特性的长期变化。
 * 核心算法在每个采样窗口（例如1秒）使用以下公式重新计算极限：计算[0.5，1.0]范围内的
 * 梯度极限，
 * 以过滤异常值梯度=max（0.5，min（1.0，longtermRtt/currentRtt））；
 * 通过应用梯度并考虑一些排队来计算新的限制
 * newLimit=梯度*currentLimit+queueSize；
 * 使用平滑因子（默认值0.2）更新限制
 * newLimit=currentLimit*（1-平滑）+newLimit*平滑
 * 限制可以处于三种主要状态之一
 * 1.稳态
 * 在这种状态下，平均RTT非常稳定，当前的测量值在这个值附近波动，有时会降低
 * 限制，有时会增加。
 * 2.从稳态到负载的过渡
 * 在这种状态下，RPS到延迟已经飙升。由于请求队列不断增长，梯度为｛@literal＜｝1.0
 * 系统无法处理。请求过多，因限制过低而被拒绝。基线RTT增长使用
 * 指数衰减，但滞后于电流测量，这使梯度{@literal<}1.0保持较低水平。
 * *
 * 3.从负载到稳态的过渡
 * *
 * *在这种状态下，系统在长时间过载后恢复到稳定状态。请求未被拒绝
 * *并且采样RTT保持较低。在此状态下，长期RTT可能需要一些时间才能恢复正常，并且可能
 * *可能比当前RTT高几倍。
 *
 *
 *
 */
public final class Gradient2Limit extends AbstractLimit {
    private static final Logger LOG = LoggerFactory.getLogger(Gradient2Limit.class);

    public static class Builder {
        private int initialLimit = 20;
        private int minLimit = 20;
        private int maxConcurrency = 200;

        private double smoothing = 0.2;
        private Function<Integer, Integer> queueSize = concurrency -> 4;
        private int longWindow = 600;
        private double rttTolerance = 1.5;

        /**
         * Initial limit used by the limiter
         * @param initialLimit
         * @return Chainable builder
         */
        public Builder initialLimit(int initialLimit) {
            this.initialLimit = initialLimit;
            return this;
        }

        /**
         * Minimum concurrency limit allowed.  The minimum helps prevent the algorithm from adjust the limit
         * too far down.  Note that this limit is not desirable when use as backpressure for batch apps.
         *
         * @param minLimit
         * @return Chainable builder
         */
        public Builder minLimit(int minLimit) {
            this.minLimit = minLimit;
            return this;
        }

        /**
         * Maximum allowable concurrency.  Any estimated concurrency will be capped
         * at this value
         * @param maxConcurrency
         * @return Chainable builder
         */
        public Builder maxConcurrency(int maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
            return this;
        }

        /**
         * Fixed amount the estimated limit can grow while latencies remain low
         * @param queueSize
         * @return Chainable builder
         */
        public Builder queueSize(int queueSize) {
            this.queueSize = (ignore) -> queueSize;
            return this;
        }

        /**
         * Function to dynamically determine the amount the estimated limit can grow while
         * latencies remain low as a function of the current limit.
         * @param queueSize
         * @return Chainable builder
         */
        public Builder queueSize(Function<Integer, Integer> queueSize) {
            this.queueSize = queueSize;
            return this;
        }

        /**
         * Tolerance for changes in minimum latency.
         * @param rttTolerance Value {@literal >}= 1.0 indicating how much change in minimum latency is acceptable
         *  before reducing the limit.  For example, a value of 2.0 means that a 2x increase in latency is acceptable.
         * @return Chainable builder
         */
        public Builder rttTolerance(double rttTolerance) {
            Preconditions.checkArgument(rttTolerance >= 1.0, "Tolerance must be >= 1.0");
            this.rttTolerance = rttTolerance;
            return this;
        }

        /**
         * Maximum multiple of the fast window after which we need to reset the limiter
         * @param multiplier
         * @return
         */
        @Deprecated
        public Builder driftMultiplier(int multiplier) {
            return this;
        }

        /**
         * Smoothing factor to limit how aggressively the estimated limit can shrink
         * when queuing has been detected.
         * @param smoothing Value of 0.0 to 1.0 where 1.0 means the limit is completely
         *  replicated by the new estimate.
         * @return Chainable builder
         */
        public Builder smoothing(double smoothing) {
            this.smoothing = smoothing;
            return this;
        }


        @Deprecated
        public Builder shortWindow(int n) {
            return this;
        }

        public Builder longWindow(int n) {
            this.longWindow = n;
            return this;
        }

        public Gradient2Limit build() {
            if (initialLimit > maxConcurrency) {
                LOG.warn("Initial limit {} exceeded maximum limit {}", initialLimit, maxConcurrency);
            }
            if (initialLimit < minLimit) {
                LOG.warn("Initial limit {} is less than minimum limit {}", initialLimit, minLimit);
            }
            return new Gradient2Limit(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Gradient2Limit newDefault() {
        return newBuilder().build();
    }

    /**
     * Estimated concurrency limit based on our algorithm
     */
    private volatile double estimatedLimit;

    /**
     * Tracks a measurement of the short time, and more volatile, RTT meant to represent the current system latency
     */
    private long lastRtt;

    /**
     * Tracks a measurement of the long term, less volatile, RTT meant to represent the baseline latency.  When the system
     * is under load this number is expect to trend higher.
     */
    private final Measurement longRtt;

    /**
     * Maximum allowed limit providing an upper bound failsafe
     */
    private final int maxLimit;

    private final int minLimit;

    private final Function<Integer, Integer> queueSize;

    private final double smoothing;


    private final double tolerance;

    private Gradient2Limit(Builder builder) {
        super(builder.initialLimit);

        this.estimatedLimit = builder.initialLimit;
        this.maxLimit = builder.maxConcurrency;
        this.minLimit = builder.minLimit;
        this.queueSize = builder.queueSize;
        this.smoothing = builder.smoothing;
        this.tolerance = builder.rttTolerance;
        this.lastRtt = 0;
        this.longRtt = new ExpAvgMeasurement(builder.longWindow, 10);

       }

    @Override
    public int _update(final long startTime, final long rtt, final int inflight, final boolean didDrop) {
        // queueSize = 4
        final double queueSize = this.queueSize.apply((int)this.estimatedLimit);

        this.lastRtt = rtt;
        final double shortRtt = (double)rtt;
        final double longRtt = this.longRtt.add(rtt).doubleValue();



        // If the long RTT is substantially larger than the short RTT then reduce the long RTT measurement.
        // This can happen when latency returns to normal after a prolonged prior of excessive load.  Reducing the
        // long RTT without waiting for the exponential smoothing helps bring the system back to steady state.
        // 如果长RTT明显大于短RTT，则减少长RTT测量。
        // 当延迟在长时间过度负载后恢复正常时，就会发生这种情况。减少
        //无需等待指数平滑的长RTT有助于使系统恢复到稳定状态。
        if (longRtt / shortRtt > 2) {
            this.longRtt.update(current -> current.doubleValue() * 0.95);
        }

        // Don't grow the limit if we are app limited
        // 如果我们受到应用程序限制，不要增加限制
        if (inflight < estimatedLimit / 2) {
            return (int) estimatedLimit;
        }

        // Rtt could be higher than rtt_noload because of smoothing rtt noload updates
        // so set to 1.0 to indicate no queuing.  Otherwise calculate the slope and don't
        // allow it to be reduced by more than half to avoid aggressive load-shedding due to 
        // outliers.
        final double gradient = Math.max(0.5, Math.min(1.0, tolerance * longRtt / shortRtt));
        double newLimit = estimatedLimit * gradient + queueSize;
        newLimit = estimatedLimit * (1 - smoothing) + newLimit * smoothing;
        newLimit = Math.max(minLimit, Math.min(maxLimit, newLimit));

        if ((int)estimatedLimit != newLimit) {
            LOG.debug("New limit={} shortRtt={} ms longRtt={} ms queueSize={} gradient={}",
                    (int)newLimit,
                    getLastRtt(TimeUnit.MICROSECONDS) / 1000.0,
                    getRttNoLoad(TimeUnit.MICROSECONDS) / 1000.0,
                    queueSize,
                    gradient);
        }

        estimatedLimit = newLimit;

        return (int)estimatedLimit;
    }

    public long getLastRtt(TimeUnit units) {
        return units.convert(lastRtt, TimeUnit.NANOSECONDS);
    }

    public long getRttNoLoad(TimeUnit units) {
        return units.convert(longRtt.get().longValue(), TimeUnit.NANOSECONDS);
    }

    @Override
    public String toString() {
        return "GradientLimit [limit=" + (int)estimatedLimit + "]";
    }
}
