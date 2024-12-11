package com.bin.sm.extension.springboot;

public class TimeRunnable implements Runnable{
    private final Runnable runnable;
    private final long startTime;
    public TimeRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.startTime = System.currentTimeMillis();
    }


    public long getStartTime() {
        return startTime;
    }

    @Override
    public void run() {
        runnable.run();
    }
}
