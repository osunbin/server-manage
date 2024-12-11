//package com.bin.sm.springboot;
//
//import com.bin.sm.context.DefaultNodeInstance;
//import com.bin.sm.core.Governance;
//import com.bin.sm.core.GovernanceFactory;
//import com.bin.sm.context.HttpNodeAdapter;
//import com.bin.sm.context.Instance;
//import com.bin.sm.context.NodeInstance;
//import com.bin.sm.router.HttpRouterContext;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.loadbalancer.Request;
//import org.springframework.cloud.client.loadbalancer.RequestData;
//import org.springframework.cloud.client.loadbalancer.RequestDataContext;
//import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
//import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
//import org.springframework.context.ConfigurableApplicationContext;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//import java.util.Map;
//
//public class ServiceInstanceListRouterSupplier extends DelegatingServiceInstanceListSupplier {
//
//    public ServiceInstanceListSupplier serviceInstanceListRouterSupplier(ConfigurableApplicationContext context) {
//        return ServiceInstanceListSupplier.builder().withBlockingDiscoveryClient().withCaching()
//                .with((ctx, delegate) ->
//                        new ServiceInstanceListRouterSupplier(delegate))
//                .build(context);
//    }
//
//    public ServiceInstanceListRouterSupplier(ServiceInstanceListSupplier delegate) {
//        super(delegate);
//    }
//
//    @Override
//    public Flux<List<ServiceInstance>> get() {
//        return getDelegate().get().map(instances -> instances);
//    }
//
//    public Flux<List<ServiceInstance>> get(Request request) {
//        return getDelegate().get().map(instances -> filtered(request, instances));
//    }
//
//
//    private List<ServiceInstance> filtered(Request request, List<ServiceInstance> serviceInstances) {
//
//        RequestData clientRequest = ((RequestDataContext) request.getContext()).getClientRequest();
//        String path = clientRequest.getUrl().getPath();
//        String serviceId = serviceInstances.getFirst().getServiceId();
//
//        Governance governance = GovernanceFactory.getGovernance();
//        Instance instance = governance.getInstance(serviceId, path);
//
//
//
//
//        long currentMillis = System.currentTimeMillis();
//        Map<String, NodeInstance> nodeInstances = instance.getNodeInstanceByAddress();
//        for (ServiceInstance serviceInstance : serviceInstances) {
//            NodeInstance nodeInstance = nodeInstances.get(genAddress(serviceInstance));
//            if (nodeInstance == null) {
//                nodeInstance = new DefaultNodeInstance(serviceInstance.getHost(), serviceInstance.getPort());
//                instance.createNodeInstance(nodeInstance);
//            } else {
//                nodeInstance.setLastUsed(currentMillis);
//            }
//        }
//        instance.expireNodes(currentMillis);
//
//
//        List<HttpNodeAdapter> nodeAdapters = serviceInstances.stream().map(HttpNodeAdapter::new).toList();
//
//        List<HttpNodeAdapter> route =
//                governance.route(serviceId, path, new HttpRouterContext(clientRequest), nodeAdapters);
//
//        List<ServiceInstance> list = route.stream().map(HttpNodeAdapter::getServiceInstance).toList();
//
//        return list;
//    }
//
//
//    private String genAddress(ServiceInstance serviceInstance) {
//        return serviceInstance.getHost() + ":" + serviceInstance.getPort();
//    }
//}
//
