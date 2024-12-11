package com.bin.sm.core;

import com.bin.sm.config.ConfigFactory;
import com.bin.sm.config.Configs;
import com.bin.sm.internal.http.client.HttpClient;
import com.bin.sm.internal.http.client.HttpGetRequest;

import java.io.IOException;

public class Bootstrap {

    private static String WEB_HOST = "";
    /**
     *  rpc 参数、分组、熔断、限流...
     *  线程池
     *  限流
     *  按钮
     *
     */
    public void start() {
        Configs configs = ConfigFactory.loadConfigs();

        // 加载配置 appName  env  zone


        String localAppName = configs.getLocalAppName();
        String localEnv = configs.getLocalEnv();
        // http 获取参数
        // Governance governance = Governance.INSTANCE;

        try {
            String s = HttpClient.get(WEB_HOST + "/service/group?callerName=" + localAppName);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
