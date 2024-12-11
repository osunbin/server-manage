package com.bin.sm.shutdown.signal;

import com.bin.sm.shutdown.GracefulShutdownManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class OperateSignal implements SignalHandler {

    private final static Logger logger = LoggerFactory.getLogger(OperateSignal.class);


    @Override
    public void handle(Signal signalName) {
        logger.info("server:{} current state is:{} received signal: {}", "serviceName","ServerState",signalName.getName());
        if (!GracefulShutdownManager.isShutdown()) {
            GracefulShutdownManager.shutdown();
            System.out.println("服务下线");
        }
        // setServerState(ServerStateType.Reboot);

        // TODO  sendClose()  获取上游所有IP,然后http 通知下线
        logger.info("server : {} will reboot !", "ServiceName");

    }
}
