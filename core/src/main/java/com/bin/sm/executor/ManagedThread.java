package com.bin.sm.executor;

public class ManagedThread extends Thread{

    public ManagedThread(Runnable target) {
        super(target);
    }

    public ManagedThread(String name) {
        super(name);
    }

    public ManagedThread(Runnable target, String name) {
        super(target, name);
    }

    protected void beforeRun() {

    }

    protected void executeRun() {
        super.run();
    }

    protected void afterRun() {

    }


    @Override
    public final void run() {
        try {
            beforeRun();
            executeRun();
        }  finally {
            afterRun();
        }
    }


}
