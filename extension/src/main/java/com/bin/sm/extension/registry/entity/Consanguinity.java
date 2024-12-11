package com.bin.sm.extension.registry.entity;

import java.util.List;

public class Consanguinity extends BaseInfo {
    /**
     * The name of the interface
     */
    private String interfaceName;

    /**
     * URL path
     */
    private String url;

    /**
     * The key at the time of service registration
     */
    private String serviceKey;

    /**
     * Service Provider Information
     */
    private List<Contract> providers;

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

    public List<Contract> getProviders() {
        return providers;
    }

    public void setProviders(List<Contract> providers) {
        this.providers = providers;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }
}