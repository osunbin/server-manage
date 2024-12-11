package com.bin.sm.shutdown.signal;

import com.bin.sm.shutdown.GracefulShutdownManager;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ShutdownSignal implements SignalHandler {
    @Override
    public void handle(Signal signalName) {
        if (!GracefulShutdownManager.isShutdown()) {
            GracefulShutdownManager.shutdown();
            System.out.println("服务下线");
        }
    }
}
