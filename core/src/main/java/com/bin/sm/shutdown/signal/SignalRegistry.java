package com.bin.sm.shutdown.signal;

import com.bin.sm.shutdown.GracefulShutdownManager;
import com.bin.sm.util.OsUtil;

import sun.misc.Signal;

public class SignalRegistry {


    public void register() {

        if (!OsUtil.isMac() && !OsUtil.isWindows()) {
            Signal sig = new Signal("USR2");
            // 重启
            Signal.handle(sig, new OperateSignal());
            // 下线
            Signal sig2 = new Signal("TERM");
            Signal.handle(sig2, new ShutdownSignal());
        }
        // TODO cas
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!GracefulShutdownManager.isShutdown()) {
                GracefulShutdownManager.shutdown();
                System.out.println("服务下线");
            }
        }
        ));

    }
}
