package com.bin.sm.extension.registry.entity;

public class BaseInfo {
    /**
     * Current service IP
     */
    private String ip;

    /**
     * The current service port
     */
    private String port;

    /**
     * Frame type
     */
    private String serviceType;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}
