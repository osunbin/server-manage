package com.bin.sm.shutdown;


import java.util.Map;

import  com.bin.sm.common.ServerState;

/**
 *  如果 shutdown 对响应数据写入 state状态
 */
public class GracefulShutdownFilter {

    static final String serverState = "ServerState";

    // ip node
    public void gracefulShutdown(Map<String,String> attributes) {
        String state = attributes.get(serverState);

        if (ServerState.Reboot.stateName().equals(state) ||
                ServerState.Shutdown.stateName().equals(state)) {

        }
    }


}
