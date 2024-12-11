//package com.bin.sm.springboot;
//
//import com.bin.sm.util.MethodSignatures;
//import org.apache.logging.log4j.core.config.Order;
//import org.springframework.beans.BeansException;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.StringJoiner;
//
//@Component
//@Order(1)
//public class RequestMappingInterceptor implements CommandLineRunner, ApplicationContextAware {
//
//
//
//    private ApplicationContext applicationContext;
//
//    /**
//     * 上报 path
//     */
//    @Override
//    public void run(String... args) throws Exception {
//
//
//    }
//
//    private List<HttpMappingInfo> getResources() {
//        List<HttpMappingInfo> resList = new ArrayList<>();
//        RequestMappingHandlerMapping mapping  = applicationContext.getBean(RequestMappingHandlerMapping.class);
//
//        //1:获取controller中所有带有@RequestMapper标签的方法
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
//        for (RequestMappingInfo requestMappingInfo : handlerMethods.keySet()) {
//            //1、获取控制器请求路径
//            String controllMappingUrl = "";
//            String methodType = "";
//
//            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
//            Set<String> patterns = patternsCondition.getPatterns();
//            if (patterns != null && !patterns.isEmpty()) {
//                List<String> urls = new ArrayList<>(patterns);
//                controllMappingUrl = urls.getFirst();
//            }
//
//            //2、获取方法请求路径
//            HandlerMethod handlerMethod = handlerMethods.get(requestMappingInfo);
//            Class<?> beanType = handlerMethod.getBeanType();
//            String className = beanType.getName();
//            String methodMappingUrl = "";
//
//            //获取方法所有注解
//            Method method = handlerMethod.getMethod();
//            String methodSignature = MethodSignatures.methodSignature(beanType, method);
//            String methodName = method.getName();
//            Annotation[] annotations = method.getAnnotations();
//            for (Annotation annotation : annotations) {
//
//                String methodUrl = "";
//                if (annotation instanceof GetMapping) {
//                    methodUrl = ((GetMapping) annotation).value()[0];
//                    methodType = "GET";
//                }  else if (annotation instanceof PostMapping) {
//                    methodUrl = ((PostMapping) annotation).value()[0];
//                    methodType = "POST";
//                }else if (annotation instanceof PutMapping) {
//                    methodUrl = ((PutMapping) annotation).value()[0];
//                    methodType = "PUT";
//                }else if (annotation instanceof DeleteMapping) {
//                    methodUrl = ((DeleteMapping) annotation).value()[0];
//                    methodType = "DELETE";
//                } else if (annotation instanceof RequestMapping) {
//                    methodUrl = ((RequestMapping) annotation).value()[0];
//                    RequestMethod[] requestMethods = ((RequestMapping) annotation).method();
//                    StringJoiner sj = new StringJoiner("|");
//                    for (RequestMethod requestMethod : requestMethods) {
//                        sj.add(requestMethod.name());
//
//                    }
//                    methodType = sj.toString();
//                }
//                methodMappingUrl = methodUrl.startsWith("/")  ? methodUrl : "/" + methodUrl;
//            }
//
//            HttpMappingInfo httpMappingInfo = new HttpMappingInfo();
//            httpMappingInfo.className = className;
//            httpMappingInfo.methodName = methodName;
//            httpMappingInfo.methodSignature = methodSignature;
//            httpMappingInfo.httpPath = controllMappingUrl + methodMappingUrl;
//            httpMappingInfo.httpMethod = methodType;
//
//
//            resList.add(httpMappingInfo);
//        }
//        return resList;
//    }
//
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//
//    class HttpMappingInfo{
//        String className;
//        String methodName;
//        String methodSignature;
//        String httpPath;
//        String httpMethod;
//
//    }
//}
