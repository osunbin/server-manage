package com.bin.sm.util;



public class TimeRunnable implements Runnable{

    private static final ThreadLocal<Long> START_TIME_THREAD_LOCAL = new ThreadLocal<Long>();


    private Runnable runnable;
    private long startTime = System.currentTimeMillis();

    public TimeRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public long getStartTime() {
        return startTime;
    }


    public static void beforeExecute(Thread t, Runnable r) {
        if (r instanceof TimeRunnable timed) {
            START_TIME_THREAD_LOCAL.set(timed.getStartTime());
        }

    }

    public static long getStartTimeFromThreadLocal() {
        Long time = START_TIME_THREAD_LOCAL.get();
        if (time != null && time > 0) {
            START_TIME_THREAD_LOCAL.remove();
            return time;
        }
        return -1;
    }

    @Override
    public void run() {
        runnable.run();
    }
}
