package com.bin.sm.circuitbreak.support;

import com.bin.sm.circuitbreak.CircuitBreaker;
import com.bin.sm.common.LogHelper;
import com.bin.sm.common.Urls;
import com.bin.sm.context.Instance;
import com.bin.sm.context.NodeInstance;
import com.bin.sm.core.Governance;
import com.bin.sm.core.GovernanceContext;
import com.bin.sm.internal.http.client.HttpClient;

import java.io.IOException;

public class DefaultCircuitBreakerStatusEvent implements CircuitBreakerStatusEvent {

    public static final DefaultCircuitBreakerStatusEvent circuitBreakerStatusEvent = new DefaultCircuitBreakerStatusEvent();

    private static final String OPEN_TO_CLOSE = "close";
    private static final String CLOSE_TO_OPEN = "open";

    @Override
    public void openToClose(CircuitBreaker circuitBreaker) {
        event(circuitBreaker, OPEN_TO_CLOSE, "");

    }

    @Override
    public void closeToOpen(CircuitBreaker circuitBreaker, String reason) {
        event(circuitBreaker, CLOSE_TO_OPEN, reason);
    }


    private void event(CircuitBreaker circuitBreaker, String event, String reason) {
        NodeInstance nodeInstance = circuitBreaker.nodeInstance();
        Instance instance = nodeInstance.instance();

        Governance governance = Governance.INSTANCE;
        String resource = instance.instanceName();
        String resourceType = instance.instanceType().getName();
        String callerAddress = governance.getAddress();
        String serviceAddress = nodeInstance.getAddress();
        String callerName = governance.getLocalAppName();
        String serviceName = GovernanceContext.getRpcContext().getAppName();
        String type = circuitBreaker.circuitBreakerType().getName();

        CircuitBreakEventData eventData = new CircuitBreakEventData()
                .setResource(resource)
                .setResourceType(resourceType)
                .setCallerAddress(callerAddress)
                .setServiceAddress(serviceAddress)
                .setCallerName(callerName)
                .setServiceName(serviceName)
                .setType(type)
                .setEvent(event)
                .setReason(reason);

        try {
            String response = HttpClient.post(Urls.REPORT_CIRCUIT_BREAKER_EVENT, eventData);
        } catch (IOException ignore) {
            LogHelper.warn("service:{} resource:{} report circuit breaker event:{} failed :{}", serviceName, resource, event,ignore);
        }
    }

}
