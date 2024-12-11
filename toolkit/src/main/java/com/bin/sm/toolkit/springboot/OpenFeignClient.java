//package com.bin.sm.springboot;
//
//import com.bin.sm.circuitbreak.CircuitBreaker;
//import com.bin.sm.core.Governance;
//import com.bin.sm.context.NodeInstance;
//import com.bin.sm.context.RpcContext;
//import com.bin.sm.core.GovernanceContext;
//import com.bin.sm.loadbalance.P2cCallBack;
//import com.bin.sm.loadbalance.P2cNode;
//import com.bin.sm.ratelimiter.concurrency.Limiter;
//import com.bin.sm.util.StringUtil;
//import feign.Client;
//import feign.Request;
//import feign.Response;
//import feign.RetryableException;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//public class OpenFeignClient implements Client {
//
//    private final Client delegate;
//
//    public OpenFeignClient(Client delegate) {
//        this.delegate = delegate;
//    }
//    @Override
//    public Response execute(Request request, Request.Options options) throws IOException {
//        /**
//         *   路由
//         *   超时
//         *  主动降级->函数降级
//         *
//         *   被限流了
//         *      响应携带回来,是否限流
//         *      直接抛弃-重要性/func
//         *   熔断
//         */
//        // 限流后要重试
//
//        Request.Options curroOptions = new Request.Options(Duration.ofMillis(10000),Duration.ofMillis(20000),true);
//
//
//        RpcContext rpcContext = GovernanceContext.getRpcContext();
//        P2cNode p2cNode = rpcContext.getP2cNode();
//        NodeInstance nodeInstance = rpcContext.getNodeInstance();
//
//        Limiter limiter = nodeInstance.getLimiter();
//
//        CircuitBreaker circuitBreaker = nodeInstance.getCircuitBreaker();
//
//        if (!circuitBreaker.allow()) {
//            // 降级
//        }
//
//
//        double cpuUsage = 0.0;
//
//        P2cCallBack p2cCallBack = p2cNode.pick();
//        Response response = null;
//        try {
//
//            response = delegate.execute(request, options);
//
//            Map<String, Collection<String>> headers = response.headers();
//            Collection<String> cpuUsages = headers.get("cpuUsage");
//
//            if (cpuUsages != null && !cpuUsages.isEmpty()) {
//                String cpu = cpuUsages.stream().findFirst().get();
//                if (StringUtil.isNotEmpty(cpu)) {
//                    cpuUsage = Double.parseDouble(cpu);
//                }
//            }
//
//            p2cCallBack.call(cpuUsage,false);
//            circuitBreaker.markSuccess();
//
//        }catch (Throwable e) {
//            p2cCallBack.call(cpuUsage,true);
//            circuitBreaker.markFailed();
//            throw e;
//          //  throw new RetryableException(500, "retry", request.httpMethod(), new Date(), request);
//
//        }finally {
//            rpcContext.clear();
//        }
//
//        return response;
//    }
//
//
//
//}
