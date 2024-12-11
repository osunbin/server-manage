package com.bin.sm.ratelimiter.concurrency.limiter;

import com.bin.sm.ratelimiter.concurrency.Limit;
import com.bin.sm.ratelimiter.concurrency.Limiter;
import com.bin.sm.ratelimiter.concurrency.limit.VegasLimit;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractLimiter<ContextT>  implements Limiter<ContextT> {

    public static final String ID_TAG = "id";
    public static final String STATUS_TAG = "status";

    public abstract static class Builder<BuilderT extends Builder<BuilderT>> {
        private static final AtomicInteger idCounter = new AtomicInteger();

        private Limit limit = VegasLimit.newDefault();
        private Supplier<Long> clock = System::nanoTime;

        protected String name = "unnamed-" + idCounter.incrementAndGet();

        private final Predicate<Object> ALWAYS_FALSE = (context) -> false;
        private Predicate<Object> bypassResolver = ALWAYS_FALSE;

        public BuilderT named(String name) {
            this.name = name;
            return self();
        }

        public BuilderT limit(Limit limit) {
            this.limit = limit;
            return self();
        }

        public BuilderT clock(Supplier<Long> clock) {
            this.clock = clock;
            return self();
        }



        protected abstract BuilderT self();

        /**
         * Add a chainable bypass resolver predicate from context. Multiple resolvers may be added and if any of the
         * predicate condition returns true the call is bypassed without increasing the limiter inflight count and
         * affecting the algorithm. Will not bypass any calls by default if no resolvers are added.
         *
         * Due to the builders not having access to the ContextT, it is the duty of subclasses to ensure that
         * implementations are type safe.
         *
         * @param shouldBypass Predicate condition to bypass limit
         * @return Chainable builder
         */
        protected final BuilderT bypassLimitResolverInternal(Predicate<?> shouldBypass) {
            if (this.bypassResolver == ALWAYS_FALSE) {
                this.bypassResolver = (Predicate<Object>) shouldBypass;
            } else {
                this.bypassResolver = bypassResolver.or((Predicate<Object>) shouldBypass);
            }
            return self();
        }
    }

    private final AtomicInteger inFlight = new AtomicInteger();
    private  Supplier<Long> clock = System::nanoTime;
    private final Limit limitAlgorithm;
    private volatile int limit;

    public AbstractLimiter() {
        this.limitAlgorithm = VegasLimit.newDefault();
        this.limit = limitAlgorithm.getLimit();
        this.limitAlgorithm.notifyOnChange(this::onNewLimit);
    }

    public AbstractLimiter(Builder<?> builder) {
        this.limitAlgorithm = builder.limit;
        this.limit = limitAlgorithm.getLimit();
        this.limitAlgorithm.notifyOnChange(this::onNewLimit);
    }

    public AbstractLimiter(Limit limit,Supplier<Long> clock) {
        this.clock = clock;
        this.limitAlgorithm = limit;
        this.limit = limitAlgorithm.getLimit();
        this.limitAlgorithm.notifyOnChange(this::onNewLimit);
    }


    @Override
    public Optional<Listener> acquire(ContextT context) {
        int inflight = getInflight();
        if (inflight > 1 && inflight >= getLimit()) {
            return Optional.empty();
        }
        return Optional.of(createListener());
    }


    protected Listener createListener() {
        final long startTime = clock.get();
        final int currentInflight = inFlight.incrementAndGet();
        return new Listener() {
            @Override
            public void onSuccess() {
                inFlight.decrementAndGet();
                limitAlgorithm.onSample(startTime, clock.get() - startTime, currentInflight, false);
            }

            @Override
            public void onIgnore() {
                inFlight.decrementAndGet();
            }

            @Override
            public void onDropped() {
                inFlight.decrementAndGet();
                limitAlgorithm.onSample(startTime, clock.get() - startTime, currentInflight, true);
            }
        };
    }

    public int getLimit() {
        return limit;
    }

    public int getInflight() { return inFlight.get(); }

    protected void onNewLimit(int newLimit) {
        limit = newLimit;
    }

}
