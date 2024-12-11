package com.bin.sm.circuitbreak.support;

public class CircuitBreakEventData {

    private String resource;
    private String resourceType;
    private String callerAddress;
    private String serviceAddress;
    private String callerName;
    private String serviceName;
    private String type;
    private String event;
    private String reason;


    public String getResource() {
        return resource;
    }

    public CircuitBreakEventData setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String getResourceType() {
        return resourceType;
    }

    public CircuitBreakEventData setResourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public String getCallerAddress() {
        return callerAddress;
    }

    public CircuitBreakEventData setCallerAddress(String callerAddress) {
        this.callerAddress = callerAddress;
        return this;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public CircuitBreakEventData setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
        return this;
    }

    public String getCallerName() {
        return callerName;
    }

    public CircuitBreakEventData setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public CircuitBreakEventData setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getType() {
        return type;
    }

    public CircuitBreakEventData setType(String type) {
        this.type = type;
        return this;
    }

    public String getEvent() {
        return event;
    }

    public CircuitBreakEventData setEvent(String event) {
        this.event = event;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public CircuitBreakEventData setReason(String reason) {
        this.reason = reason;
        return this;
    }
}
