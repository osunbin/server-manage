package com.bin.sm.circuitbreak.simple;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;


public class RollingNumber {

    enum RollingNumberEvent {

        SUCCESS(1),
        FAILURE(1),
        TIMEOUT(1);

        private final int type;

        RollingNumberEvent(int type) {
            this.type = type;
        }

        public boolean isCounter() {
            return type == 1;
        }
    }

    @FunctionalInterface
    public interface RollingBucketEvent {

        void rollingBucket(long total,int errorPercentage);
    }

    final int timeInMilliseconds;
    final int numberOfBuckets;
    final int bucketSizeInMillseconds;

    final BucketCircularArray buckets;

    private final CumulativeSum cumulativeSum = new CumulativeSum();

    private RollingBucketEvent rollingBucket;

    public RollingNumber(RollingBucketEvent rollingBucket) {
        this(10 * 1000,10,rollingBucket);
    }
    public RollingNumber(int timeInMilliseconds, int numberOfBuckets,RollingBucketEvent rollingBucket) {
        this.rollingBucket = rollingBucket;
        this.timeInMilliseconds = timeInMilliseconds;
        this.numberOfBuckets = numberOfBuckets;

        if (timeInMilliseconds % numberOfBuckets != 0) {
            throw new IllegalArgumentException("The timeInMilliseconds must divide equally into numberOfBuckets. For example 1000/10 is ok, 1000/11 is not.");
        }
        this.bucketSizeInMillseconds = timeInMilliseconds / numberOfBuckets;

        buckets = new BucketCircularArray(numberOfBuckets);
    }

    public void incSuccess() {
        getCurrentBucket().getAdder(RollingNumberEvent.SUCCESS).increment();
    }

    public void incFailure() {
        getCurrentBucket().getAdder(RollingNumberEvent.FAILURE).increment();
    }

    public void incTimeout() {
        getCurrentBucket().getAdder(RollingNumberEvent.TIMEOUT).increment();
    }

    public void increment(RollingNumberEvent type) {
        getCurrentBucket().getAdder(type).increment();
    }


    public void add(RollingNumberEvent type, long value) {
        getCurrentBucket().getAdder(type).add(value);
    }


    public void reset() {
        // if we are resetting, that means the lastBucket won't have a chance to be captured in CumulativeSum, so let's do it here
        Bucket lastBucket = buckets.peekLast();
        if (lastBucket != null) {
            cumulativeSum.addBucket(lastBucket);
        }

        // clear buckets so we start over again
        buckets.clear();
    }

    public long getCumulativeSum(RollingNumberEvent type) {
        // this isn't 100% atomic since multiple threads can be affecting latestBucket & cumulativeSum independently
        // but that's okay since the count is always a moving target and we're accepting a "point in time" best attempt
        // we are however putting 'getValueOfLatestBucket' first since it can have side-affects on cumulativeSum whereas the inverse is not true
        return getValueOfLatestBucket(type) + cumulativeSum.get(type);
    }

    public long getRollingSum(RollingNumberEvent type) {
        Bucket lastBucket = getCurrentBucket();
        if (lastBucket == null)
            return 0;

        long sum = 0;
        for (Bucket b : buckets) {
            sum += b.getAdder(type).sum();
        }
        return sum;
    }

    public long getValueOfLatestBucket(RollingNumberEvent type) {
        Bucket lastBucket = getCurrentBucket();
        if (lastBucket == null)
            return 0;
        // we have bucket data so we'll return the lastBucket
        return lastBucket.get(type);
    }

    public long[] getValues(RollingNumberEvent type) {
        Bucket lastBucket = getCurrentBucket();
        if (lastBucket == null)
            return new long[0];

        // get buckets as an array (which is a copy of the current state at this point in time)
        Bucket[] bucketArray = buckets.getArray();

        // we have bucket data so we'll return an array of values for all buckets
        long values[] = new long[bucketArray.length];
        int i = 0;
        for (Bucket bucket : bucketArray) {
            values[i++] = bucket.getAdder(type).sum();
        }
        return values;
    }

    public long getRollingMaxValue(RollingNumberEvent type) {
        long values[] = getValues(type);
        if (values.length == 0) {
            return 0;
        } else {
            Arrays.sort(values);
            return values[values.length - 1];
        }
    }

    private ReentrantLock newBucketLock = new ReentrantLock();


    Bucket getCurrentBucket() {
        long currentTime = System.currentTimeMillis();

        Bucket currentBucket = buckets.peekLast();
        if (currentBucket != null && currentTime < currentBucket.windowStart + this.bucketSizeInMillseconds) {
            // if we're within the bucket 'window of time' return the current one
            // NOTE: We do not worry if we are BEFORE the window in a weird case of where thread scheduling causes that to occur,
            // we'll just use the latest as long as we're not AFTER the window
            return currentBucket;
        }

        /* if we didn't find the current bucket above, then we have to create one */

        /**
         * The following needs to be synchronized/locked even with a synchronized/thread-safe data structure such as LinkedBlockingDeque because
         * the logic involves multiple steps to check existence, create an object then insert the object. The 'check' or 'insertion' themselves
         * are thread-safe by themselves but not the aggregate algorithm, thus we put this entire block of logic inside synchronized.
         *
         * I am using a tryLock if/then (http://download.oracle.com/javase/6/docs/api/java/util/concurrent/locks/Lock.html#tryLock())
         * so that a single thread will get the lock and as soon as one thread gets the lock all others will go the 'else' block
         * and just return the currentBucket until the newBucket is created. This should allow the throughput to be far higher
         * and only slow down 1 thread instead of blocking all of them in each cycle of creating a new bucket based on some testing
         * (and it makes sense that it should as well).
         *
         * This means the timing won't be exact to the millisecond as to what data ends up in a bucket, but that's acceptable.
         * It's not critical to have exact precision to the millisecond, as long as it's rolling, if we can instead reduce the impact synchronization.
         *
         * More importantly though it means that the 'if' block within the lock needs to be careful about what it changes that can still
         * be accessed concurrently in the 'else' block since we're not completely synchronizing access.
         *
         * For example, we can't have a multi-step process to add a bucket, remove a bucket, then update the sum since the 'else' block of code
         * can retrieve the sum while this is all happening. The trade-off is that we don't maintain the rolling sum and let readers just iterate
         * bucket to calculate the sum themselves. This is an example of favoring write-performance instead of read-performance and how the tryLock
         * versus a synchronized block needs to be accommodated.
         */
        if (newBucketLock.tryLock()) {
            try {
                if (buckets.peekLast() == null) {
                    // the list is empty so create the first bucket
                    Bucket newBucket = new Bucket(currentTime);
                    buckets.addLast(newBucket);
                    return newBucket;
                } else {
                    // We go into a loop so that it will create as many buckets as needed to catch up to the current time
                    // as we want the buckets complete even if we don't have transactions during a period of time.
                    for (int i = 0; i < numberOfBuckets; i++) {
                        // we have at least 1 bucket so retrieve it
                        Bucket lastBucket = buckets.peekLast();
                        if (currentTime < lastBucket.windowStart + this.bucketSizeInMillseconds) {
                            // if we're within the bucket 'window of time' return the current one
                            // NOTE: We do not worry if we are BEFORE the window in a weird case of where thread scheduling causes that to occur,
                            // we'll just use the latest as long as we're not AFTER the window
                            return lastBucket;
                        } else if (currentTime - (lastBucket.windowStart + this.bucketSizeInMillseconds) > timeInMilliseconds) {
                            // the time passed is greater than the entire rolling counter so we want to clear it all and start from scratch
                            reset();
                            // recursively call getCurrentBucket which will create a new bucket and return it
                            return getCurrentBucket();
                        } else { // we're past the window so we need to create a new bucket
                            // create a new bucket and add it as the new 'last'
                            buckets.addLast(new Bucket(lastBucket.windowStart + this.bucketSizeInMillseconds));
                            // add the lastBucket values to the cumulativeSum
                            cumulativeSum.addBucket(lastBucket);
                            //
                            createNewBucketEvent();
                        }
                    }
                    // we have finished the for-loop and created all of the buckets, so return the lastBucket now
                    return buckets.peekLast();
                }
            } finally {
                newBucketLock.unlock();
            }
        } else {
            currentBucket = buckets.peekLast();
            if (currentBucket != null) {
                // we didn't get the lock so just return the latest bucket while another thread creates the next one
                return currentBucket;
            } else {
                // the rare scenario where multiple threads raced to create the very first bucket
                // wait slightly and then use recursion while the other thread finishes creating a bucket
                try {
                    Thread.sleep(5);
                } catch (Exception e) {
                    // ignore
                }
                return getCurrentBucket();
            }
        }
    }

    public void createNewBucketEvent() {
        // total  ErrorPercentage
        long successCounter = 0L;
        long failCounter = 0L;
        Iterator<Bucket> iterator = iterator();
        while (iterator.hasNext()) {
            Bucket tmp = iterator.next();
            successCounter += tmp.get(RollingNumberEvent.SUCCESS);
            failCounter += tmp.get(RollingNumberEvent.FAILURE);
            failCounter += tmp.get(RollingNumberEvent.TIMEOUT);
        }
        long total = successCounter + failCounter;
        int errorPercentage = (int) Math.round((double) failCounter / total * 100);
        rollingBucket.rollingBucket(total, errorPercentage);
    }

    public Iterator<Bucket> iterator() {
        return buckets.iterator();
    }

    static class Bucket {
        final long windowStart;
        final LongAdder[] adderForCounterType;

        Bucket(long startTime) {
            this.windowStart = startTime;
            adderForCounterType = new LongAdder[RollingNumberEvent.values().length];
            for (RollingNumberEvent type : RollingNumberEvent.values()) {
                if (type.isCounter()) {
                    adderForCounterType[type.ordinal()] = new LongAdder();
                }
            }

        }

        long get(RollingNumberEvent type) {
            if (type.isCounter()) {
                return adderForCounterType[type.ordinal()].sum();
            }
            throw new IllegalStateException("Unknown type of event: " + type.name());
        }

        LongAdder getAdder(RollingNumberEvent type) {
            if (!type.isCounter()) {
                throw new IllegalStateException("Type is not a Counter: " + type.name());
            }
            return adderForCounterType[type.ordinal()];
        }
    }

    static class CumulativeSum {
        final LongAdder[] adderForCounterType;

        CumulativeSum() {
            adderForCounterType = new LongAdder[RollingNumberEvent.values().length];
            for (RollingNumberEvent type : RollingNumberEvent.values()) {
                if (type.isCounter()) {
                    adderForCounterType[type.ordinal()] = new LongAdder();
                }
            }
        }

        public void addBucket(Bucket lastBucket) {
            for (RollingNumberEvent type : RollingNumberEvent.values()) {
                if (type.isCounter()) {
                    getAdder(type).add(lastBucket.getAdder(type).sum());
                }
            }
        }

        long get(RollingNumberEvent type) {
            if (type.isCounter()) {
                return adderForCounterType[type.ordinal()].sum();
            }
            throw new IllegalStateException("Unknown type of event: " + type.name());
        }

        LongAdder getAdder(RollingNumberEvent type) {
            if (!type.isCounter()) {
                throw new IllegalStateException("Type is not a Counter: " + type.name());
            }
            return adderForCounterType[type.ordinal()];
        }

    }

    static class BucketCircularArray implements Iterable<Bucket> {
        private final AtomicReference<ListState> state;
        private final int dataLength; // we don't resize, we always stay the same, so remember this
        private final int numBuckets;

        private class ListState {
            private final AtomicReferenceArray<Bucket> data;
            private final int size;
            private final int tail;
            private final int head;

            private ListState(AtomicReferenceArray<Bucket> data, int head, int tail) {
                this.head = head;
                this.tail = tail;
                if (head == 0 && tail == 0) {
                    size = 0;
                } else {
                    this.size = (tail + dataLength - head) % dataLength;
                }
                this.data = data;
            }

            public Bucket tail() {
                if (size == 0) {
                    return null;
                } else {
                    // we want to get the last item, so size()-1
                    return data.get(convert(size - 1));
                }
            }

            private Bucket[] getArray() {
                /*
                 * this isn't technically thread-safe since it requires multiple reads on something that can change
                 * but since we never clear the data directly, only increment/decrement head/tail we would never get a NULL
                 * just potentially return stale data which we are okay with doing
                 */
                ArrayList<Bucket> array = new ArrayList<Bucket>();
                for (int i = 0; i < size; i++) {
                    array.add(data.get(convert(i)));
                }
                return array.toArray(new Bucket[array.size()]);
            }

            private ListState incrementTail() {
                /* if incrementing results in growing larger than 'length' which is the max we should be at, then also increment head (equivalent of removeFirst but done atomically) */
                if (size == numBuckets) {
                    // increment tail and head
                    return new ListState(data, (head + 1) % dataLength, (tail + 1) % dataLength);
                } else {
                    // increment only tail
                    return new ListState(data, head, (tail + 1) % dataLength);
                }
            }

            public ListState clear() {
                return new ListState(new AtomicReferenceArray<Bucket>(dataLength), 0, 0);
            }

            public ListState addBucket(Bucket b) {
                /*
                 * We could in theory have 2 threads addBucket concurrently and this compound operation would interleave.
                 * <p>
                 * This should NOT happen since getCurrentBucket is supposed to be executed by a single thread.
                 * <p>
                 * If it does happen, it's not a huge deal as incrementTail() will be protected by compareAndSet and one of the two addBucket calls will succeed with one of the Buckets.
                 * <p>
                 * In either case, a single Bucket will be returned as "last" and data loss should not occur and everything keeps in sync for head/tail.
                 * <p>
                 * Also, it's fine to set it before incrementTail because nothing else should be referencing that index position until incrementTail occurs.
                 */
                data.set(tail, b);
                return incrementTail();
            }

            // The convert() method takes a logical index (as if head was
            // always 0) and calculates the index within elementData
            private int convert(int index) {
                return (index + head) % dataLength;
            }
        }

        BucketCircularArray(int size) {
            AtomicReferenceArray<Bucket> _buckets = new AtomicReferenceArray<Bucket>(size + 1); // + 1 as extra room for the add/remove;
            state = new AtomicReference<ListState>(new ListState(_buckets, 0, 0));
            dataLength = _buckets.length();
            numBuckets = size;
        }

        public void clear() {
            while (true) {
                ListState current = state.get();
                ListState newState = current.clear();
                if (state.compareAndSet(current, newState)) {
                    return;
                }
            }
        }


        public Iterator<Bucket> iterator() {
            return Collections.unmodifiableList(Arrays.asList(getArray())).iterator();
        }

        public void addLast(Bucket o) {
            ListState currentState = state.get();
            // create new version of state (what we want it to become)
            ListState newState = currentState.addBucket(o);

            /*
             * use compareAndSet to set in case multiple threads are attempting (which shouldn't be the case because since addLast will ONLY be called by a single thread at a time due to protection
             * provided in <code>getCurrentBucket</code>)
             */
            if (state.compareAndSet(currentState, newState)) {
                // we succeeded
                return;
            } else {
                // we failed, someone else was adding or removing
                // instead of trying again and risking multiple addLast concurrently (which shouldn't be the case)
                // we'll just return and let the other thread 'win' and if the timing is off the next call to getCurrentBucket will fix things
                return;
            }
        }

        public Bucket getLast() {
            return peekLast();
        }

        public int size() {
            // the size can also be worked out each time as:
            // return (tail + data.length() - head) % data.length();
            return state.get().size;
        }

        public Bucket peekLast() {
            return state.get().tail();
        }

        private Bucket[] getArray() {
            return state.get().getArray();
        }
    }
}
