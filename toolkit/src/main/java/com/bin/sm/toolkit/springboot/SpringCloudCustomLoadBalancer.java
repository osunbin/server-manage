//package com.bin.sm.toolkit.springboot;
//
//import com.bin.sm.core.Governance;
//import com.bin.sm.context.HttpNodeAdapter;
//import com.bin.sm.core.GovernanceContext;
//import com.bin.sm.loadbalance.RandomWeightLoadBalance;
//import com.googlecode.aviator.AviatorEvaluator;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.loadbalancer.DefaultResponse;
//import org.springframework.cloud.client.loadbalancer.EmptyResponse;
//import org.springframework.cloud.client.loadbalancer.Request;
//import org.springframework.cloud.client.loadbalancer.Response;
//import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
//import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
//import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
//import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
//import org.springframework.core.env.Environment;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//
//import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
//
//public class SpringCloudCustomLoadBalancer implements ReactorServiceInstanceLoadBalancer {
//
//
//    public ReactorLoadBalancer<ServiceInstance> springCloudLoadBalancer(Environment environment,
//                                                                        LoadBalancerClientFactory loadBalancerClientFactory) {
//
//        return new SpringCloudCustomLoadBalancer(loadBalancerClientFactory.
//                getLazyProvider("serviceInstanceListRouterSupplier", ServiceInstanceListSupplier.class));
//    }
//
//
//    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
//
//    public SpringCloudCustomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
//        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
//    }
//
//    @Override
//    public Mono<Response<ServiceInstance>> choose(Request request) {
//        ServiceInstanceListSupplier available = serviceInstanceListSupplierProvider.getIfAvailable();
//        if (available == null) {
//            throw new IllegalStateException("serviceInstanceListRouterSupplier is not available");
//        }
//        return available.get(request).next().map(instances -> {
//            return processInstanceResponse(instances,request);
//        });
//    }
//
//    //         requestAttributes.setAttribute("loadbalancer-execute","10412%instanceSize",SCOPE_REQUEST);
//    private Response<ServiceInstance> processInstanceResponse(List<ServiceInstance> instances, Request request) {
//        if(instances.isEmpty()){
//            return new EmptyResponse();
//        }
//
//        ServiceInstance serviceInstance = null;
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        boolean randomWeight = GovernanceContext.isRandomWeight();
//        Object attribute = null;
//        if (requestAttributes != null) {
//            attribute = requestAttributes.getAttribute("loadbalancer-execute", RequestAttributes.SCOPE_REQUEST);
//        }
//        if (attribute != null) {
//            int execute = (int) attribute;
//            serviceInstance = instances.get(execute % instances.size());
//        } else if (randomWeight) {
//            List<HttpNodeAdapter> nodes = instances.stream().map(HttpNodeAdapter::new).toList();
//            HttpNodeAdapter nodeAdapter = RandomWeightLoadBalance.doSelect(nodes);
//            serviceInstance = nodeAdapter.getServiceInstance();
//        } else {
//            List<String> addresses = instances.stream().map(this::genAddress).toList();
//            String address = Governance.INSTANCE.loadBalancer(addresses);
//            for (ServiceInstance instance : instances) {
//                if (genAddress(instance).equals(address)) {
//                    serviceInstance = instance;
//                    break;
//                }
//            }
//        }
//
//        if (serviceInstance == null) {
//            serviceInstance = instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
//        }
//
//        GovernanceContext.isP2cLoadBalance(genAddress(serviceInstance));
//
//        return new DefaultResponse(serviceInstance);
//    }
//
//    private String genAddress(ServiceInstance instance) {
//       return instance.getHost() + ":" + instance.getPort();
//    }
//}
