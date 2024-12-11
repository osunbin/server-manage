//package com.bin.sm.toolkit.springboot;
//
//
//import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
//import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
//import org.springframework.cloud.client.circuitbreaker.ConfigBuilder;
//
//
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//public class SmCircuitBreakerFactory extends CircuitBreakerFactory {
//
//    private static SmCircuitBreaker circuitBreaker = new SmCircuitBreaker();
//    @Override
//    public CircuitBreaker create(String id) {
//        return circuitBreaker;
//    }
//
//    @Override
//    protected ConfigBuilder configBuilder(String id) {
//        return new ConfigBuilder() {
//            @Override
//            public Object build() {
//                return null;
//            }
//        };
//    }
//
//    @Override
//    public void configureDefault(Function defaultConfiguration) {
//
//    }
//}
//
//
//class SmCircuitBreaker implements CircuitBreaker {
//
//
//    @Override
//    public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
//        try {
//            return toRun.get();
//        } catch (Exception ex) {
//            return fallback.apply(ex);
//        }
//    }
//
//
//}