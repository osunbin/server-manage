package com.bin.sm.executor.util;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public final class ContextMutexFactory {

    final Map<Object, Mutex> mutexMap = new HashMap<Object, Mutex>();

    // synchronizes access to mutexMap and Mutex.referenceCount
    private final Object mainMutex = new Object();

    public Mutex mutexFor(Object mutexKey) {
        Mutex mutex;
        synchronized (mainMutex) {
            mutex = mutexMap.get(mutexKey);
            if (mutex == null) {
                mutex = new Mutex(mutexKey);
                mutexMap.put(mutexKey, mutex);
            }
            mutex.referenceCount++;
        }
        return mutex;
    }

    /**
     * Reference counted mutex, which will remove itself from the mutexMap when it is no longer referenced.
     */
    public final class Mutex implements Closeable {

        private final Object key;

        private int referenceCount;

        private Mutex(Object key) {
            this.key = key;
        }

        @Override
        public void close() {
            synchronized (mainMutex) {
                referenceCount--;
                if (referenceCount == 0) {
                    mutexMap.remove(key);
                }
            }
        }
    }
}