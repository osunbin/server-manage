//package com.bin.sm.toolkit.springboot;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.env.EnvironmentPostProcessor;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.MapPropertySource;
//import org.springframework.core.env.PropertySource;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//@Configuration
//public class SmConfiguration implements EnvironmentPostProcessor {
//
//    @Bean
//    public MyCircuitBreakerFactory circuitBreakerFactory() {
//        return new MyCircuitBreakerFactory();
//    }
//
//    @Override
//    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//       // String name = "applicationConfig: [classpath:/application-dev.properties]";
//        Iterator<PropertySource<?>> iterator = environment.getPropertySources().iterator();
//        if (iterator.hasNext()) {
//            MapPropertySource propertySource = (MapPropertySource) iterator.next();
//            String name = propertySource.getName();
//            Map<String, Object> source = propertySource.getSource();
//            Map<String, Object> map = new HashMap<>(source);
//            map.replace("spring.cloud.openfeign.circuitbreaker.enabled", true);
//            environment.getPropertySources().replace(name, new MapPropertySource(name, map));
//
//        }
//    }
//
//    @Bean
//    public FilterRegistrationBean<EntryFilter> filter() {
//        FilterRegistrationBean<EntryFilter> filterRegistrationBean =
//                new FilterRegistrationBean<>();
//        filterRegistrationBean.setFilter(new EntryFilter());
//        filterRegistrationBean.setUrlPatterns(List.of("/*"));
//        filterRegistrationBean.setName("entryFilter");
//        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
//        return filterRegistrationBean;
//    }
//}
//
