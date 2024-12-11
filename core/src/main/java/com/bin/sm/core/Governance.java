package com.bin.sm.core;

import com.bin.sm.context.Instance;
import com.bin.sm.context.NodeAdapter;
import com.bin.sm.context.NodeInstance;
import com.bin.sm.context.RpcContext;
import com.bin.sm.loadbalance.P2c;
import com.bin.sm.loadbalance.P2cCallBack;
import com.bin.sm.router.RouterChain;
import com.bin.sm.router.RouterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求也要加重要性,超时 是否抛弃 限流、熔断 是否执行
 * VictoriaMetrics
 */
public class Governance {

    private static Logger logger = LoggerFactory.getLogger(Governance.class);

    public static final Governance INSTANCE = new Governance();


    private final Map<String, AppInstance> services = new HashMap<>();

    private String localAppName;

    private String localEnv = "local";

    private String localZone = "defaultZone";

    private String localIp = "";

    private int localPort = 8090;



    public void register(String serviceId, AppInstance appInstance) {
        services.put(serviceId, appInstance);
    }


    public AppInstance getApp(String serviceId) {
        return services.get(serviceId);
    }

    public Instance getInstance(String serviceId, String path) {
        AppInstance appInstance = getApp(serviceId);
        return appInstance.getInstance(path);
    }


    public Map<String, NodeInstance> getNodeInstanceByAddress(String serviceId, String path) {
        AppInstance appInstance = getApp(serviceId);
        Instance instance = appInstance.getInstance(path);
        return instance.getNodeInstanceByAddress();
    }



    public <N extends NodeAdapter> List<N> route(String serviceId, String func,
                                                 RouterContext routerContext, List<N> nodes) {
        routerContext.setLocalEnv(localEnv);
        routerContext.setLocalZone(localZone);
        routerContext.setFunc(func);
        Instance instance =  getInstance(serviceId,func);
        routerContext.setTagToAddresses(instance.getTagToAddresses());
        routerContext.setConditionRules(instance.getConditionRules());
        return RouterChain.DEFAULT.route(routerContext, nodes);
    }

    public String loadBalancer(List<String> addresses) {
        return P2c.p2c.loadBalancer(addresses);
    }


    public long getNodeExpireTime() {
        return 10 * 60 * 1000;
    }


    public void before() {

    }

    public void after() {

    }


    public String getLocalAppName() {
        return localAppName;
    }

    public String getLocalZone() {
        return localZone;
    }

    public String getLocalEnv() {
        return localEnv;
    }

    public String getLocalIp() {
        return localIp;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getAddress() {
        return localIp + ":" + localPort;
    }
}
