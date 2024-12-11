package com.bin.sm.extension.registry.interceptor;

import com.bin.sm.extension.registry.entity.BaseInfo;
import com.bin.sm.extension.registry.entity.ServerInfo;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

import org.springframework.cloud.client.serviceregistry.Registration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SpringCloudRegistryInterceptor extends AbstractInterceptor {

    public static final Map<String, BaseInfo> REGISTRY_MAP = new ConcurrentHashMap<>();


    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {

        if (context.getArguments() != null && context.getArguments().length == 1
                && context.getArguments()[0] instanceof Registration) {

            Registration registration = (Registration) context.getArguments()[0];
            Map<String, String> metadata = registration.getMetadata();
            String zone = metadata.get("zone");
            String serviceId = registration.getServiceId();



            BaseInfo baseInfo = new BaseInfo();
            baseInfo.setServiceType("springcloud");
            baseInfo.setIp(registration.getHost());

            baseInfo.setPort(String.valueOf(registration.getPort()));
            if (REGISTRY_MAP.get("springcloud") == null) {
                REGISTRY_MAP.put("springcloud", baseInfo);
                ServerInfo serverInfo = new ServerInfo();
                serverInfo.setRegistryInfo(REGISTRY_MAP);
//        serverInfo.setApplicationName(BootArgsIndexer.getAppName());
//        serverInfo.setGroupName(serviceMeta.getApplication());
//        serverInfo.setVersion(serviceMeta.getVersion());
//        serverInfo.setEnvironment(serviceMeta.getEnvironment());
//        serverInfo.setZone(serviceMeta.getZone());
//        serverInfo.setProject(serviceMeta.getProject());
//        serverInfo.setInstanceId(BootArgsIndexer.getInstanceId());
// TODO
            }
        }
        return context;
    }
}
