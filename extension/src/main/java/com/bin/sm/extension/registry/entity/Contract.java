package com.bin.sm.extension.registry.entity;

import java.util.List;

public class Contract extends BaseInfo {
    /**
     * The name of the interface
     */
    private String interfaceName;

    /**
     * The key of the service registration
     */
    private String serviceKey;

    /**
     * The path of the request
     */
    private String url;

    /**
     * Service ID
     */
    private String serviceId;

    /**
     * Method set
     */
    private List<MethodInfo> methodInfoList;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<MethodInfo> getMethodInfoList() {
        return methodInfoList;
    }

    public void setMethodInfoList(List<MethodInfo> methodInfoList) {
        this.methodInfoList = methodInfoList;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }
}
